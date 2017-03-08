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
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 *
 * @author srv_veralab
 */
public class DependenceSubsets {
    
    private final double[][] arrayOfAllData;
    private final List<DataVariable> dataVariables;
    private final Set<Set> subsets;
    private final RealMatrix correlationMatrix;
    private final double threshold;
    
    public DependenceSubsets(double[][] arrayOfAllData, double threshold) {
        this.arrayOfAllData = arrayOfAllData;
        this.dataVariables = computeDataVariables(arrayOfAllData);
        this.correlationMatrix = new PearsonsCorrelation(arrayOfAllData).getCorrelationMatrix();
        this.threshold = threshold;
        this.subsets = computeSubsets(this.correlationMatrix.getData(), this.dataVariables, this.threshold);
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

    private Set<Set> computeSubsets(double[][] correlationDataTable, 
                                    List<DataVariable> dataVariables, 
                                    double threshold) { 
        
        Set<Set> allSubsets = new HashSet<>();
        l1: for (int i = 0; i < correlationDataTable.length; i++) {
                Set<DataVariable> subset = new HashSet<>();
                subset.add(dataVariables.get(i));
            l2: for (int j = 0; j < correlationDataTable[0].length; j++) {
                    if (Math.abs(correlationDataTable[i][j]) > threshold) {
                        for (DataVariable current : subset) {
                            if (Math.abs(correlationDataTable[current.getId()][j]) <= threshold) {
                                continue l2;
                            }
                        }
                        subset.add(dataVariables.get(j));
                    }
                }
                allSubsets.add(subset);
        }        
        return Collections.unmodifiableSet(allSubsets);
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
    public Set<Set> getSubsets() {
        return subsets;
    }

    /**
     * @return the correlationMatrix
     */
    public RealMatrix getCorrelationMatrix() {
        return correlationMatrix;
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }
    
    public final class DataVariable {
    
        private final int id;
        private List<Double> data;

        public DataVariable(final int id) {
            this.id = id;
            this.data = new ArrayList<>();
        }
        
        public DataVariable(final int id, List<Double> data) {
            this.id = id;
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof DataVariable) {
                DataVariable other = (DataVariable)o;
                if (this.id == other.getId()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 59 * hash + this.id;
            return hash;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the data
         */
        public List<Double> getData() {
            return data;
        }

        /**
         * @param data the data to set
         */
        public void setData(List<Double> data) {
            this.data = data;
        }
    }
}
