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
public class DependenceSubsets {
    
    private final double[][] arrayOfAllData;
    private final List<DataVariable> dataVariables;
    private final Set<Set<DataVariable>> subsets = new HashSet<>();
    private final double[][] correlationTable;
    private final double threshold;
    
    public DependenceSubsets(double[][] arrayOfAllData, double threshold) {
        this.arrayOfAllData = arrayOfAllData;
        this.dataVariables = computeDataVariables(arrayOfAllData);
        this.correlationTable = new PearsonsCorrelation(arrayOfAllData).getCorrelationMatrix().getData();
        this.threshold = threshold;
        computeSubsets(dataVariables ,correlationBooleanTable(), new HashSet<DataVariable>(), this.subsets);
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
    
    private boolean[][] correlationBooleanTable() {
        boolean[][] booleanTable = new boolean[correlationTable.length][correlationTable[0].length];
        for (int i = 0; i < booleanTable.length; i++) {
            for (int j = 0; j < booleanTable[0].length; j++) {
                booleanTable[i][j] = Math.abs(correlationTable[i][j]) <= threshold;
            }
        }
        return booleanTable;
    }
    
    private GenericTree<DataVariable> subsetsTree(boolean[][] correlationBooleanTable) {
        GenericTree<DataVariable> subsetsTree = new GenericTree<>();
        GenericTreeNode<DataVariable> root = new GenericTreeNode<>(new DataVariable(-1));
        subsetsTree.setRoot(root);
        for (DataVariable currenVar : dataVariables) {
            GenericTreeNode<DataVariable> childNode = new GenericTreeNode<>(currenVar);
            computeSubsetsTrees(childNode, dataVariables, correlationBooleanTable);
            root.addChild(childNode);
        }
        return subsetsTree;
    }
    
    private void computeSubsetsTrees(GenericTreeNode<DataVariable> treeNode, 
                                List<DataVariable> correlationCandidateVars, 
                                boolean[][] correlationTable) {
        List<DataVariable> correlationVars = computeCorrelatedVars(treeNode.getData(), 
                correlationCandidateVars, correlationTable);
        if (!correlationVars.isEmpty()) {
            for (DataVariable currentVar : correlationVars) {
                GenericTreeNode<DataVariable> childNode = new GenericTreeNode<>(currentVar);
                treeNode.addChild(childNode);
                computeSubsetsTrees(childNode, correlationVars, correlationTable);
            }
        }
    }
    
    private void computeSubsets(List<DataVariable> list, boolean[][] correlationTable, Set<DataVariable> subset, Set<Set<DataVariable>> set) {
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
    
    private void computeSubsetsInefficient(List<DataVariable> list, boolean[][] correlationTable, Set<DataVariable> subset, Set<Set<DataVariable>> set) {
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
    
    private List<DataVariable> computeCorrelatedVars(DataVariable x, 
                                                     List<DataVariable> correlationCandidateVars, 
                                                     boolean[][] correlationTable) {
        List<DataVariable> correlationVars = new ArrayList<>();
        for (DataVariable currentVar : correlationCandidateVars) {
            if (x != currentVar && correlationTable[x.getId()][currentVar.getId()]) {
                correlationVars.add(currentVar);
            }
        }
        return correlationVars;
    }

    private void computeSubsets(GenericTreeNode<DataVariable> currentNode, 
                                Set<DataVariable> subset) {
        List<GenericTreeNode<DataVariable>> children = currentNode.getChildren();
        if (children.isEmpty()) {
            subsets.add(subset);
        }
        else {
            for (GenericTreeNode<DataVariable> child : children) {
                Set<DataVariable> updatedSubset = new HashSet<>(subset);
                updatedSubset.add(child.getData());
                computeSubsets(child, updatedSubset);
            }
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
}
