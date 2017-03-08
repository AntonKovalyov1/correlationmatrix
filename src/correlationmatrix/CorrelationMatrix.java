package correlationmatrix;

import correlationmatrix.DependenceSubsets.DataVariable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class CorrelationMatrix {
    
    public static void main(String[] args) throws IOException {
        //Get the data from file
        try(FileInputStream inputStream = new FileInputStream("abalone.txt")) {     
            String data = IOUtils.toString(inputStream);
            // Split each line of data and store into array
            String[] tokens = data.split("\\R");
            // Store each element into a 2d array
            String[][] elementsAsString = new String[tokens.length][];
            for (int i = 0; i < elementsAsString.length; i++) {
                elementsAsString[i] = tokens[i].split(",");
                for (int j = 0; j < 9; j++) {
                }
            }   
            // Make sure the array contains only numbers
            for (int i = 0; i < elementsAsString.length; i++) {
                if (elementsAsString[i][0].contentEquals("M")) {
                    elementsAsString[i][0] = "1.0";
                }
                else if (elementsAsString[i][0].contentEquals("F")) {
                    elementsAsString[i][0] = "2.0";
                }
                else if (elementsAsString[i][0].contentEquals("I")) {
                    elementsAsString[i][0] = "3.0";
                }            
            }
            // convert the strings into double values and store in a 2d array of double numbers
            double[][] elements = new double[elementsAsString.length][elementsAsString[0].length];        
            for (int i = 0; i < elementsAsString.length; i++) {
                for (int j = 0; j < elementsAsString[0].length; j++) {
                    elements[i][j] = Double.parseDouble(elementsAsString[i][j]);
                }
            }
            // Compute and display the correlations matrix
            DependenceSubsets ds = new DependenceSubsets(elements, 0.7);
            System.out.println("Correlation Matrix:\n");
            System.out.print("      x0    x1    x2    x3    x4    x5    x6    x7    x8");
            double[][] correlationData = ds.getCorrelationMatrix().getData();
            for (int i = 0; i < correlationData.length; i++) {
                System.out.print("\nx" + i + ": ");
                for (int j = 0; j < correlationData[0].length; j++) {
                    if (correlationData[i][j] > 0) {
                        System.out.print(" " + String.format( "%.2f", Math.round(correlationData[i][j] * 100.0) / 100.0) + " ");
                    }
                    else {
                        System.out.print(String.format( "%.2f", Math.round(correlationData[i][j] * 100.0) / 100.0) + " ");
                    }
                }
            }
            // Compute and display subsets
            System.out.println("\n\nCorrelation subsets:");
            int i = 1;
            String subsetDisplay = "";
            for (Set subset : ds.getSubsets()) {
                subsetDisplay = subsetDisplay.concat("subset" + i + ": {");
                for (Object o : subset) {
                    DataVariable x = (DataVariable)o;
                    subsetDisplay = subsetDisplay.concat("x" + x.getId() + ", ");
                }
                subsetDisplay = subsetDisplay.substring(0, subsetDisplay.length() - 2);
                subsetDisplay += "}\n";
                i++;
            }
            System.out.println(subsetDisplay);
        }        
        catch (FileNotFoundException ex) {
                Logger.getLogger(CorrelationMatrix.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}
