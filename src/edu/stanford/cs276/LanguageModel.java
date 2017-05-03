package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.cs276.util.Dictionary;
import edu.stanford.cs276.util.Pair;

/**
 * LanguageModel class constructs a language model from the training corpus.
 * This model will be used to score generated query candidates.
 * 
 * This class uses the Singleton design pattern
 * (https://en.wikipedia.org/wiki/Singleton_pattern).
 */
public class LanguageModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
  private static LanguageModel lm_;

//  Dictionary unigram = new Dictionary();
  private static Map<String, Integer> unigramCount = new TreeMap<String, Integer>();
  private static Map<String, Double> unigramOdds = new TreeMap<String, Double>();
  private static Map<Pair<String, String>, Integer> bigramCount = new TreeMap<Pair<String, String>, Integer>();
  private static Map<Pair<String, String>, Double> bigramOdds = new TreeMap<Pair<String, String>, Double>();
//  Map<Pair<String, String>, Double> interpolatedOdds = new TreeMap<Pair<String, String>, Double>();
  int numUnigrams = 0;
  int numBigrams = 0;
  static double gamma = 0.1;

  /*
   * Feel free to add more members here (e.g., a data structure that stores bigrams)
   */

  /**
   * Constructor
   * IMPORTANT NOTE: you should NOT change the access level for this constructor to 'public', 
   * and you should NOT call this constructor outside of this class.  This class is intended
   * to follow the "Singleton" design pattern, which ensures that there is only ONE object of
   * this type in existence at any time.  In most circumstances, you should get a handle to a 
   * NoisyChannelModel object by using the static 'create' and 'load' methods below, which you
   * should not need to modify unless you are making substantial changes to the architecture
   * of the starter code.  
   *
   * For more info about the Singleton pattern, see https://en.wikipedia.org/wiki/Singleton_pattern.  
   */
  private LanguageModel(String corpusFilePath) throws Exception {
    constructDictionaries(corpusFilePath);
  }
  
  public static double probInterpolated(String w1, String w2) {
	  
	  double probUni = unigramOdds.get(w1);
	  Pair<String, String> thisBigram = new Pair<String, String>(w1, w2);
	  double probBi = 0.0;
	  if (bigramOdds.get(thisBigram) != null) {
		  probBi = bigramOdds.get(thisBigram);
	  }
	  return ((gamma*probUni) + ((1 - gamma)*(probBi)));
  }

  /**
   * This method is called by the constructor, and computes language model parameters 
   * (i.e. counts of unigrams, bigrams, etc.), which are then stored in the class members
   * declared above.  
   */
  public void constructDictionaries(String corpusFilePath) throws Exception {

    System.out.println("Constructing dictionaries...");
    File dir = new File(corpusFilePath);
    for (File file : dir.listFiles()) {
      if (".".equals(file.getName()) || "..".equals(file.getName())) {
        continue; // Ignore the self and parent aliases.
      }
      System.out.printf("Reading data file %s ...\n", file.getName());
      BufferedReader input = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = input.readLine()) != null) {
    	  
        /*
         * Remember: each line is a document (refer to PA2 handout)
         * TODO: Your code here
         */
    	  
    	  String[] tokens = line.trim().split("\\s+");
    	  int numWords = tokens.length;
    	  for (int i = 0; i < numWords; i++) {
    		  String token = tokens[i];
    		  numUnigrams++;
    		  if (unigramCount.containsKey(token)) {
        		  unigramCount.put(token, unigramCount.get(token) + 1);
    		  } else {
    			  unigramCount.put(token, 1);
    		  }
    		  
    		  // If this is not the first token, count the bigram occurrences, too.
    		  if (i != 0) {
    			  numBigrams++;
    			  Pair<String, String> bigram = new Pair<String, String>(tokens[i-1], tokens[i]);
        		  if (bigramCount.containsKey(bigram)) {
            		  bigramCount.put(bigram, bigramCount.get(bigram) + 1);
        		  } else {
        			  bigramCount.put(bigram, 1);
        		  }
    		  }
    	  }
      }
      
      
      // Three for-loops over a lot of data... Is there a more efficient way to do this though?
      // Populating the unigram and bigram probabilities
      for (Pair<String, String> bigram : bigramCount.keySet()) {
    	  bigramOdds.put(bigram, bigramCount.get(bigram)*1.0/unigramCount.get(bigram.getFirst()));
      }
      for (String unigram : unigramCount.keySet()) {
    	  unigramOdds.put(unigram, unigramCount.get(unigram)*1.0/numUnigrams);
      }
      
      // Interpolated Odds should be calculated on the fly, since they depend on the bigram before it which may not 
      // be in the bigram dictionary.
//      for (Pair<String, String> bigram : bigramOdds.keySet()) {
//    	  interpolatedOdds.put(bigram, gamma*unigramOdds.get(bigram.getSecond()) + (1 - gamma) * bigramOdds.get(bigram));
//      }
      
      input.close();
    }
    
    System.out.println("Done.");
  }

  /**
   * Creates a new LanguageModel object from a corpus. This method should be used to create a
   * new object rather than calling the constructor directly from outside this class
   */
  public static LanguageModel create(String corpusFilePath) throws Exception {
    if (lm_ == null) {
      lm_ = new LanguageModel(corpusFilePath);
    }
    return lm_;
  }

  /**
   * Loads the language model object (and all associated data) from disk
   */
  public static LanguageModel load() throws Exception {
    try {
      if (lm_ == null) {
        FileInputStream fiA = new FileInputStream(Config.languageModelFile);
        ObjectInputStream oisA = new ObjectInputStream(fiA);
        lm_ = (LanguageModel) oisA.readObject();
        oisA.close(); // THIS LINE WAS ADDED BY TOM
      }
    } catch (Exception e) {
      throw new Exception("Unable to load language model.  You may not have run buildmodels.sh!");
    }
    return lm_;
  }

  /**
   * Saves the object (and all associated data) to disk
   */
  public void save() throws Exception {
    FileOutputStream saveFile = new FileOutputStream(Config.languageModelFile);
    ObjectOutputStream save = new ObjectOutputStream(saveFile);
    save.writeObject(this);
    save.close();
  }
}
