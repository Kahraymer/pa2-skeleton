package edu.stanford.cs276;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CandidateGenerator implements Serializable {

	private static final long serialVersionUID = 1L;
	private static CandidateGenerator cg_;

  /** 
  * Constructor
  * IMPORTANT NOTE: As in the NoisyChannelModel and LanguageModel classes, 
  * we want this class to use the Singleton design pattern.  Therefore, 
  * under normal circumstances, you should not change this constructor to 
  * 'public', and you should not call it from anywhere outside this class.  
  * You can get a handle to a CandidateGenerator object using the static 
  * 'get' method below.  
  */
  private CandidateGenerator() {}

  public static CandidateGenerator get() throws Exception {
    if (cg_ == null) {
      cg_ = new CandidateGenerator();
    }
    return cg_;
  }

  public static final Character[] alphabet = { 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', ' ', ',' };

  // Generate all candidates for the target query
  public Set<String> getCandidates(String query) throws Exception {
    Set<String> candidates = new HashSet<String>();
    /*
     * Your code here
     */
    
    for (int i = 0; i < query.length(); i++) {
    	String temp;
    	for (Character letter : alphabet) {
    		// Each possible letter replacement
    		String thisChar = letter.toString();
    		temp = query.substring(0, i).concat(thisChar);
    		if (i != (query.length() - 1)) {
    			temp = temp.concat(query.substring(i+1));
    		}
    		candidates.add(temp);
    		
    		// Each possible letter addition
        	temp = query.substring(0,i).concat(thisChar);
    		if (i != (query.length() - 1)) {
    			temp = temp.concat(query.substring(i));
    		}
    		candidates.add(temp);

    	}
    	if (i != (query.length() - 1)) {
    		// Each possible transposition
    		temp = query.substring(0, i).concat(query.substring(i+1, i+2)).concat(query.substring(i, i+1));
    		if (i != (query.length() - 2)) {
    			temp = temp.concat(query.substring(i+2));
    		}
    	candidates.add(temp);
    	}
    	
    	// Each possible letter elimination
    	temp = query.substring(0, i);
    	if (i != (query.length() - 1)) {
        	temp = temp.concat(query.substring(i+1));

    	}
    	candidates.add(temp);    	
    
    }
    return candidates;
  }

}
