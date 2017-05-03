package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import edu.stanford.cs276.util.Pair;

/**
 * Implement {@link EditCostModel} interface. Use the query corpus to learn a model
 * of errors that occur in our dataset of queries, and use this to compute P(R|Q).
 */
public class EmpiricalCostModel implements EditCostModel {
	private static final long serialVersionUID = 1L;
	private Map<String, String> commonFixes = new TreeMap<String, String>();
	List<String> commonlyEliminated = new ArrayList<String>();
	List<String> commonlyInserted = new ArrayList<String>();
	
	// Pairs are in the format: noisy string, clean string
	List<Pair<String, String>> commonlyTransposed = new ArrayList<Pair<String, String>>();
	List<Pair<String, String>> commonlySubbed = new ArrayList<Pair<String, String>>();
	
	/*
	 * This next method is directly from the Apache Commons implementation of StringUtils
	 * Returns -1 if no difference could be found
	 */
	public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
	    if (cs1 == cs2) {
	        return -1;
	    }
	    if (cs1 == null || cs2 == null) {
	        return 0;
	    }
	    int i;
	    for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
	        if (cs1.charAt(i) != cs2.charAt(i)) {
	            break;
	        }
	    }
	    if (i < cs2.length() || i < cs1.length()) {
	        return i;
	    }
	    return -1;
	}
	
	public void populateDataStructures(String noisy, String clean) {
		String noisySubstr = noisy;
	      String cleanSubstr = clean;
	      int index = indexOfDifference(noisySubstr, cleanSubstr);
	    	  if (index > 0) {
	        	  noisySubstr = noisy.substring(index);
	        	  cleanSubstr = clean.substring(index);

	    	  } 
	    	  
	    	  if (index == 0) {
	    		  // First letters of strings differ
	        	  if (noisySubstr.length() == 0) {
	        		  commonlyEliminated.add(cleanSubstr);
	        	  } else if (cleanSubstr.length() == 0) {
	        		  commonlyInserted.add(noisySubstr);
	        	  } else {
	        		  if (cleanSubstr.length() >= 2 && noisySubstr.length() >= 2) {
	    				  // Checking for commonly transposed characters
	    				  if ((cleanSubstr.substring(0,1) == noisySubstr.substring(1,2)) && (noisySubstr.substring(0,1) == cleanSubstr.substring(1,2))) {
	    					  Pair<String, String> thisPair = new Pair<String, String>(noisySubstr.substring(0,2), cleanSubstr.substring(0,2));
	    					  commonlyTransposed.add(thisPair);
	    				  }
	    			  }
	        		  
	        		  if (cleanSubstr.length() >= 1 && noisySubstr.length() >= 1) {
	        			  // Checking for commonly substituted characters
	        			  if (cleanSubstr.substring(1).equalsIgnoreCase(noisySubstr.substring(1))) {
	        				  Pair<String, String> thisPair = new Pair<String, String>(noisySubstr.substring(0, 1), cleanSubstr.substring(0,1));
	        				  commonlySubbed.add(thisPair);
	        			  }
	        		  }
	        	  }
	    	  } // else if (index == -1) Do Nothing, since there was no difference
	}
	
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
      
      // Singling out the character level common edits
      populateDataStructures(noisy, clean);
      lineSc.close();
       
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
	  
	  if (distance == 0) {
		  return 0.9;
	  }
	  // Give more weight to queries that are common misspellings
	  if (commonFixes.containsKey(original)) {
		  if (commonFixes.get(original).equals(R)) {
			  return 0.5;
		  } else {
			  // And take weight away when the suggested fix isn't the common fix
			  return Math.pow(0.05, distance);
		  }
	  }
	  
	  
	  return Math.pow(0.1, distance);
  }
}
