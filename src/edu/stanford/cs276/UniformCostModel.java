package edu.stanford.cs276;

/**
 * Implement {@link EditCostModel} interface by assuming assuming
 * that any single edit in the Damerau-Levenshtein distance is equally likely,
 * i.e., having the same probability
 */
public class UniformCostModel implements EditCostModel {
	
	private static final long serialVersionUID = 1L;
	
  @Override
  public double editProbability(String original, String R, int distance) {
	  
	/*
     * START OF ADDED CODE
	 */
	  
	  // Let's assume that the weight we give to a DL distance is 0.5 (arbitrary, but equivalent)
	  if (distance == 0) {
		  return 0.90;
	  }
	  return Math.pow(0.1, distance);
			  
	/*
	 * END OF ADDED CODE
	 */
  }
}
