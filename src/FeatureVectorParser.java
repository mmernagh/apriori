import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weka.core.Attribute;
import weka.core.BinarySparseInstance;
import weka.core.Instances;

/**
 * The FeatureVectorParser reads the feature vector file into a list of {@link Transaction}s.
 * It then generates {@link Instances} for the data (ignoring the class attribute).
 */
public class FeatureVectorParser {

	private Instances instances;
	
	// Keep track of attributes, transactions, and class labels
	private Map<String, Short> attributeIndices = new HashMap<String, Short>();
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private List<Transaction> transactions = new ArrayList<Transaction>();
	private Map<String, Short> topicIndices = new HashMap<String, Short>();
	
	// To generate the most frequent class label
	private Map<String, Integer> topicCount = new HashMap<String, Integer>();
	
	// Temporary items pertaining to the current transaction being parsed
	private Map<String, Short> transAttributeIndices = new HashMap<String, Short>();
	private List<Attribute> transactionAttributes = new ArrayList<Attribute>();
	private Transaction transaction;
	
	public FeatureVectorParser(String fileName) throws Exception {
		getAttributes(fileName);		
		initInstances();
	}

	public Instances instances() {
		return instances;
	}
	
	
	public List<Transaction> transactions() {
		return transactions;
	}
	
	public Set<Short> classIndices() {
		return new HashSet<Short>(topicIndices.values());
	}
	
	public int mostFrequentClassIndex() {
		// This method is expected to only be called once.
		int smallestCount = Integer.MAX_VALUE;
		String smallestClass = "";
		for (Map.Entry<String, Integer> entry : topicCount.entrySet()) {
			if (entry.getValue() < smallestCount) {
				smallestCount = entry.getValue();
				smallestClass = entry.getKey();
			}
		}
		return topicIndices.get(smallestClass);
	}
	
	/**
   * Parses all of the feature vectors into the map.
   *
   * @param file The given file.
	 * @throws Exception 
   */
  private void getAttributes(String fileName) throws Exception {
  	BufferedReader reader = null;
  	reader = new BufferedReader(new FileReader(fileName));
  	
  	try {
			String line  = reader.readLine();
			while (line != null) {
				parseLine(line);
				line  = reader.readLine();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			reader.close();
		}
  }
  
  private void parseLine(String line) {
  	transaction = new Transaction();
  	transAttributeIndices = new HashMap<String, Short>();
  	transactionAttributes = new ArrayList<Attribute>();

  	// Grab body words
  	int end = line.indexOf('<', 11);
  	parseBodyWords(line.substring(11, end));
  	
  	// Grab topics
  	int start = skipTags(end, line, 2);
  	end = line.indexOf('<', start + 1);
  	parseTopics(line.substring(start + 2, end));
  }
  	
  private int skipTags(int start, String line, int tagsToSkip) {
  	int end = start;
  	for (int i = 0; i < tagsToSkip; ++i) {
  		end = line.indexOf('>', end + 1);
  	}
  	return end;
  }
  
  /**
	 * This method splits words and their frequencies and adds them to the transaction.
	 * 
	 * @param str The String of words and frequencies.
	 */
	private void parseBodyWords(String str) {
		String[] split = str.trim().split(" ");
		for (String word : split) {
			addAttribute(word);
		}
	}
	
	private void addAttribute(String str) {
		if (str.length() > 0) {
			if (attributeIndices.containsKey(str)) {
				transaction.addIndex(attributeIndices.get(str));
			} else if (transAttributeIndices.containsKey(str)){
				transaction.addIndex(transAttributeIndices.get(str));
			} else {
				short index = (short) (attributeIndices.size() + transactionAttributes.size());
				transAttributeIndices.put(str, index);
				List<String> values = new ArrayList<String>();
				values.add("");
				values.add(str);
				transactionAttributes.add(new Attribute(str, values));
				transaction.addIndex(index);
			}
		}
	}
	
	/**
	 * This method adds the current transaction to the set mapped to each topic in
	 * the given String (separated by spaces). If no topics occur in the given
	 * string, then the transaction is added to the set mapped to the key "".
	 * 
	 * @param str The given string.
	 */
	private void parseTopics(String str) {
		String[] split = str.split(" ");
		if (!isEmpty(split)) {
			for (String topic : split) {
				addTopic(topic);
			}
	  	attributeIndices.putAll(transAttributeIndices);
	  	attributes.addAll(transactionAttributes);
		}
	}
	
	private boolean isEmpty(String[] arr) {
		return arr.length == 1 && arr[0].length() == 0;
	}
	
	/**
	 * Adds a transaction to the set mapped to a given string, creating a new map entry
	 * if necessary.
	 * 
	 * @param topic The map key.
	 */
	private void addTopic(String topic) {
		Transaction t = new Transaction(transaction);
		if (topicIndices.containsKey(topic)) {
			t.setClassIndex(topicIndices.get(topic));
			topicCount.put(topic, topicCount.get(topic) + 1);
		} else {
			short index = (short) (-1 * (topicIndices.size() + 1));
			topicIndices.put(topic, index);
			topicCount.put(topic, 0);
			t.setClassIndex(index);
		}
		transactions.add(t);
	}
	
	private void initInstances() {
		instances = new Instances("Data", attributes, transactions.size());
		for (Transaction t : transactions) {
			addTransaction(t);
		}
	}
	
	private void addTransaction(Transaction trans) {
		if (trans.attributes().length > 0 && trans.classIndex() < 0) {
			trans.sortAttributes();
			int[] indices = new int[trans.attributes().length];
			for (int i = 0; i < trans.attributes().length; ++i) {
				indices[i] = trans.attributes()[i];
			}
			BinarySparseInstance instance = new BinarySparseInstance(1.0, indices, attributes.size());
			instance.setDataset(instances);
			instances.add(instance);
		}
	}
}
