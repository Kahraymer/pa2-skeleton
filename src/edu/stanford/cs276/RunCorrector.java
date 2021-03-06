package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.stanford.cs276.util.Pair;


public class RunCorrector {

  public static LanguageModel languageModel;
  public static NoisyChannelModel nsm;

  public static void main(String[] args) throws Exception {
    
    // Parse input arguments
    String uniformOrEmpirical = null;
    String queryFilePath = null;
    String goldFilePath = null;
    String extra = null;
    BufferedReader goldFileReader = null;
    
    if (args.length == 2) {
      // Default: run without extra credit code or gold data comparison
      uniformOrEmpirical = args[0];
      queryFilePath = args[1];
    } 
    else if (args.length == 3) {
      uniformOrEmpirical = args[0];
      queryFilePath = args[1];
      if (args[2].equals("extra")) {
        extra = args[2];
      } else {
        goldFilePath = args[2];
      }
    } 
    else if (args.length == 4) {
      uniformOrEmpirical = args[0];
      queryFilePath = args[1];
      extra = args[2];
      goldFilePath = args[3];
    } 
    else {
      System.err.println(
          "Invalid arguments.  Argument count must be 2, 3 or 4 \n"
          + "./runcorrector <uniform | empirical> <query file> \n"
          + "./runcorrector <uniform | empirical> <query file> <gold file> \n"
          + "./runcorrector <uniform | empirical> <query file> <extra> \n"
          + "./runcorrector <uniform | empirical> <query file> <extra> <gold file> \n"
          + "SAMPLE: ./runcorrector empirical data/queries.txt \n"
          + "SAMPLE: ./runcorrector empirical data/queries.txt data/gold.txt \n"
          + "SAMPLE: ./runcorrector empirical data/queries.txt extra \n"
          + "SAMPLE: ./runcorrector empirical data/queries.txt extra data/gold.txt \n");
      return;
    }

    if (goldFilePath != null) {
      goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
    }

    // Load models from disk
    languageModel = LanguageModel.load();
    nsm = NoisyChannelModel.load();
    BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(queryFilePath)));
    nsm.setProbabilityType(uniformOrEmpirical);
    CandidateGenerator cg = CandidateGenerator.get();
    
    String query = null;

    /*
     * Each line in the file represents one query. We loop over each query and find
     * the most likely correction
     */
    while ((query = queriesFileReader.readLine()) != null) {

      String correctedQuery = query;
      
      /*
       * START OF ADDED CODE
       */
      
      
      /*
       * Your code here: currently the correctQuery and original query are the same
       * Complete this implementation so that the spell corrector corrects the 
       * (possibly) misspelled query
       * 
       */
      
	  String[] tokens = query.trim().split("\\s+");
//	  
//	  // Populate a map linking likelihoods to original/candidate String pairs
	  TreeMap<Double, String> candidateScores = new TreeMap<Double, String>();
	  List<Pair<String, String>> topCandidates = new ArrayList<Pair<String, String>>();
	  for (int i = 0; i < tokens.length; i++) {
		  // For every word, calculate a product of interpolated uni/bigram probability and the likelihood of that mistake
		  String token = tokens[i];
		  candidateScores.clear();
		  Set<String> candidates = cg.getCandidates(token);
		  for (String candidate : candidates) {
			  if (languageModel.unigramCount.keySet().contains(candidate)) {
				  
				  double editScore = nsm.editProbability(token, candidate, 1);
				  double probInterpolated = 0.0;
				  if (i == 0) {
					  probInterpolated = LanguageModel.probInterpolated("", candidate);
				  } else {
					  probInterpolated = LanguageModel.probInterpolated(tokens[i-1], candidate);
				  }
				  double finalScore = editScore*probInterpolated;
				  candidateScores.put(finalScore, candidate);
			  }
			  Pair<String, String> bestCandidate = new Pair<String, String>(token, candidateScores.get(candidateScores.lastKey()));
			  topCandidates.add(bestCandidate);
		  }
	  }
	  
	  int fixesLeft = 2;
	  for (int i = 0; i < tokens.length; i++) {
		  String token = tokens[i];
		  Pair<String, String> candidate = topCandidates.get(i);
		  if (token == candidate.getFirst()) { // Double checking that this is replacing the right word
			  if (token != candidate.getSecond()) {
				  tokens[i] = candidate.getSecond();
				  fixesLeft--;
				  if (fixesLeft == 0) {
					  break;
				  }
			  }
		  }
	  }
	  correctedQuery = String.join(" ", tokens);
      /*
       * END OF ADDED CODE
       */

      
      if ("extra".equals(extra)) {
        /*
         * If you are going to implement something regarding to running the corrector,
         * you can add code here. Feel free to move this code block to wherever
         * you think is appropriate. But make sure if you add "extra" parameter,
         * it will run code for your extra credit and it will run you basic
         * implementations without the "extra" parameter.
         */
      }

      // If a gold file was provided, compare our correction to the gold correction
      // and output the running accuracy
      if (goldFileReader != null) {
        String goldQuery = goldFileReader.readLine();
        /*
         * You can do any bookkeeping you wish here - track accuracy, track where your solution
         * diverges from the gold file, what type of errors are more common etc. This might
         * help you improve your candidate generation/scoring steps 
         */
      }
      
      /*
       * Output the corrected query.
       * IMPORTANT: In your final submission DO NOT add any additional print statements as 
       * this will interfere with the autograder
       */
      System.out.println(correctedQuery);
    }
    queriesFileReader.close();
  }
}
