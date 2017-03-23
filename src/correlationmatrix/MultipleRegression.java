/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package correlationmatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 *
 * @author srv_veralab
 */
public class MultipleRegression {
    
    private final double[][] arrayOfAllData;
    private final List<DataVariable> dataVariables;
    private final Set<Set<DataVariable>> subsets = new HashSet<>();
    private final double[][] correlationTable;
    private final boolean[][] correlatedBooleanTable;
    private final boolean[][] noncorrelatedBooleanTable;
    private final double threshold;
    
    public MultipleRegression(double[][] arrayOfAllData, double threshold) {
        this.arrayOfAllData = arrayOfAllData;
        this.threshold = threshold;
        this.dataVariables = computeDataVariables(arrayOfAllData);
        this.correlationTable = new PearsonsCorrelation(arrayOfAllData).getCorrelationMatrix().getData();
        this.correlatedBooleanTable = correlatedBooleanTable(this.correlationTable, this.threshold);
        this.noncorrelatedBooleanTable = noncorrelatedBooleanTable(this.correlationTable, this.threshold);
    }
    
    private List<DataVariable> computeDataVariables(double[][] arrayOfAllData) {
        List<DataVariable> dataVars = new ArrayList<>();
        
        for (int i = 0; i < arrayOfAllData[0].length; i++) {
            dataVars.add(new DataVariable(i));
            for (int j = 0; j < arrayOfAllData.length; j++) {
                dataVars.get(i).getData().add(arrayOfAllData[j][i]);    
            }
        }        
        return Collections.unmodifiableList(dataVars);
    }
    
    private boolean[][] correlatedBooleanTable(double[][] correlationTable, double threshold) {
        boolean[][] booleanTable = new boolean[correlationTable.length][correlationTable[0].length];
        for (int i = 0; i < booleanTable.length; i++) {
            for (int j = 0; j < booleanTable[0].length; j++) {
                booleanTable[i][j] = Math.abs(correlationTable[i][j]) > threshold;
            }
        }
        return booleanTable;
    }
    
    private boolean[][] noncorrelatedBooleanTable(double[][] correlationTable, double threshold) {
        boolean[][] booleanTable = new boolean[correlationTable.length][correlationTable[0].length];
        for (int i = 0; i < booleanTable.length; i++) {
            for (int j = 0; j < booleanTable[0].length; j++) {
                booleanTable[i][j] = Math.abs(correlationTable[i][j]) <= threshold;
            }
        }
        return booleanTable;
    }
    
    public void computeSubsets(List<DataVariable> list, boolean[][] correlationTable, Set<DataVariable> subset, Set<Set<DataVariable>> set) {
        List<DataVariable> rows = new ArrayList<>(list);
        List<DataVariable> columns = new ArrayList<>(list);
        for (int i = 0; i < rows.size(); i++) {
            List<DataVariable> vars = new ArrayList<>();
            DataVariable current = rows.get(i);
            if (current.getId() == -1)
                continue;
            for (int j = 0; j < columns.size(); j++) {
                if (current != columns.get(j) && correlationTable[current.getId()][columns.get(j).getId()]) {
                    vars.add(columns.get(j));
                    if (i == 0)
                        rows.set(j, new DataVariable(-1));
                }
            }
            rows.set(i, new DataVariable(-1));
            Set<DataVariable> updatedSubset = new HashSet<>(subset);
            updatedSubset.add(current);
            if (vars.isEmpty()) {
                set.add(updatedSubset);
            }
            else {
                computeSubsets(vars, correlationTable, updatedSubset, set);
            }
        }
    }
    
    public void computeSubsetsInefficient(List<DataVariable> list, boolean[][] correlationTable, Set<DataVariable> subset, Set<Set<DataVariable>> set) {
        for (int i = 0; i < list.size(); i++) {
            List<DataVariable> vars = new ArrayList<>();
            DataVariable current = list.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (current != list.get(j) && correlationTable[current.getId()][list.get(j).getId()]) {
                    vars.add(list.get(j));
                }
            }
            Set<DataVariable> updatedSubset = new HashSet<>(subset);
            updatedSubset.add(current);
            if (vars.isEmpty()) {
                set.add(updatedSubset);
            }
            else {
                computeSubsetsInefficient(vars, correlationTable, updatedSubset, set);
            }
        }
    }
    
    public Set<Set<DataVariable>> computeCorrelatedSubsets() {
        Set<Set<DataVariable>> set = new HashSet<>();
        Set<DataVariable> subset = new HashSet<>();
        computeSubsets(this.dataVariables, this.correlatedBooleanTable, subset, set);
        return set;
    }
    
    public Set<Set<DataVariable>> computeNoncorrelatedSubsets() {
        Set<Set<DataVariable>> set = new HashSet<>();
        Set<DataVariable> subset = new HashSet<>();
        computeSubsets(this.dataVariables, this.noncorrelatedBooleanTable, subset, set);
        return set;
    }
    
    public Set<Set<DataVariable>> computeFunctions() {
        Set<Set<DataVariable>> set = new HashSet<>();
        computeFunctions(this.dataVariables, this.correlatedBooleanTable, this.noncorrelatedBooleanTable, set);
        return set;
    }
    
    public void computeFunctions(List<DataVariable> list, boolean[][] correlatedTable, boolean[][] noncorrelatedTable, Set<Set<DataVariable>> set) {
        for (int i = 0; i < list.size(); i++) {
            List<DataVariable> correlatedVars = new ArrayList<>();
            DataVariable current = list.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (current != list.get(j) && correlatedTable[current.getId()][list.get(j).getId()]) {
                    correlatedVars.add(list.get(j));
                }
            }
            Set<DataVariable> subset = new HashSet<>();
            subset.add(current);
            computeSubsets(correlatedVars, noncorrelatedTable, subset, set);
        }
    }

    /**
     * @return the arrayOfAllData
     */
    public double[][] getArrayOfAllData() {
        return arrayOfAllData;
    }

    /**
     * @return the dataVariables
     */
    public List<DataVariable> getDataVariables() {
        return dataVariables;
    }

    /**
     * @return the subsets
     */
    public Set<Set<DataVariable>> getSubsets() {
        return subsets;
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * @return the correlationTable
     */
    public double[][] getCorrelationTable() {
        return correlationTable;
    }

    /**
     * @return the correlatedBooleanTable
     */
    public boolean[][] getCorrelatedBooleanTable() {
        return correlatedBooleanTable;
    }

    /**
     * @return the noncorrelatedBooleanTable
     */
    public boolean[][] getNoncorrelatedBooleanTable() {
        return noncorrelatedBooleanTable;
    }
}
