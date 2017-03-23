/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package correlationmatrix;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author srv_veralab
 */
public class RegressionSetsTest {
    
public static void main(String[] args) throws IOException {
        //Get the data from file
        try(FileInputStream inputStream = new FileInputStream("breast_cancer_dataset.txt")) {     
            String data = IOUtils.toString(inputStream, Charset.defaultCharset());
            // Split each line of data and store into array
            String[] tokens = data.split("\\R");
            // Store each element into a 2d array
            String[][] elementsAsString = new String[tokens.length][];
            for (int i = 0; i < elementsAsString.length; i++) {
                elementsAsString[i] = tokens[i].split(",");
            }   
            // Make sure the array contains only numbers
            for (int i = 0; i < elementsAsString.length; i++) {
                if (elementsAsString[i][1].contentEquals("M")) {
                    elementsAsString[i][1] = "0.0";
                }
                else if (elementsAsString[i][1].contentEquals("B")) {
                    elementsAsString[i][1] = "1.0";
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
            MultipleRegression ds = new MultipleRegression(elements, 0.7);
            System.out.println("Correlation Matrix:\n");
            System.out.print("       ");
            double[][] correlationData = ds.getCorrelationTable();
            
            for (int i = 0; i < correlationData.length; i++) {
                if (i < 9)
                    System.out.print("x" + i + "    ");
                else
                    System.out.print("x" + i + "   ");
            }
            
            for (int i = 0; i < correlationData.length; i++) {
                if (i < 10)
                    System.out.print("\nx" + i + ":  ");
                else
                    System.out.print("\nx" + i + ": ");
                for (int j = 0; j < correlationData[0].length; j++) {
                    if (Math.round(correlationData[i][j] * 100.0) / 100.0 >= 0) {
                        System.out.print(" " + String.format( "%.2f", Math.round(correlationData[i][j] * 100.0) / 100.0) + " ");
                    }
                    else {
                        System.out.print(String.format( "%.2f", Math.round(correlationData[i][j] * 100.0) / 100.0) + " ");
                    }
                }
            }
            // Compute and display subsets
            System.out.println("\n");
            int i = 1;
            String subsetDisplay = "Correlation subsets:" + System.lineSeparator() + System.lineSeparator();
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
