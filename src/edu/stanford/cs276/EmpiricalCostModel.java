package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Implement {@link EditCostModel} interface. Use the query corpus to learn a model
 * of errors that occur in our dataset of queries, and use this to compute P(R|Q).
 */
public class EmpiricalCostModel implements EditCostModel {
	private static final long serialVersionUID = 1L;
	private Map<String, String> commonFixes = new TreeMap<String, String>();
	
  public EmpiricalCostModel(String editsFile) throws IOException {
    BufferedReader input = new BufferedReader(new FileReader(editsFile));
    System.out.println("Constructing edit distance map...");
    String line = null;
    while ((line = input.readLine()) != null) {
      Scanner lineSc = new Scanner(line);
      lineSc.useDelimiter("\t");
      String noisy = lineSc.next();
      String clean = lineSc.next();
      
      
      /*
       * START OF ADDED CODE
       */
      
      commonFixes.put(noisy, clean);
      
      
      
      /*
       * END OF ADDED CODE
       */
    }

    input.close();
    System.out.println("Done.");
  }

  // You need to add code for this interface method to calculate the proper empirical cost.
  @Override
  public double editProbability(String original, String R, int distance) {
	  /*
	   * START OF ADDED CODE
	   */
	  
	  // Give more weight to queries that are common misspellings
	  if (commonFixes.containsKey(original)) {
		  if (commonFixes.get(original).equals(R)) {
			  return 0.9;
		  } else {
			  // And take weight away when the suggested fix isn't the common fix
			  return Math.pow(0.4, distance);
		  }
	  }
	  
	  
	  return Math.pow(0.5, distance);
  }
}
