import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 * The FVParser reads the feature vector file into a list of {@link Transaction}s,
 * and generates the {@link Instances} from the list of Records.
 */
public class FVParser {

	private Instances instances;
	
	private Map<String, Integer> attributeIndices = new HashMap<String, Integer>();
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private List<Transaction> records = new ArrayList<Transaction>();
	private List<Short> classIndices = new ArrayList<Short>();
	
	private Transaction activeRecord;
	
	public FVParser(String fileName) throws Exception {
		getAttributes(fileName);		
		initInstances();
	}

	public Instances instances() {
		return instances;
	}
	
	public List<Transaction> transactions() {
		return records;
	}
	
	public List<Short> classIndices() {
		return classIndices;
	}
	
	/**
   * Parses all of the feature vectors into the map.
   *
   * @param file The given file.
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
  	activeRecord = new Transaction();
  	
  	// Grab body words
  	int end = line.indexOf('<', 11);
  	parseBodyWords(line.substring(11, end));
  	
  	// Grab places
  	int start = skipTags(end, line, 2);
  	end = line.indexOf('<', start + 1);
  	parseNominalValue(line.substring(start + 2, end));
  	
  	// Grab topics
  	start = skipTags(end, line, 2);
  	end = line.indexOf('<', start + 1);
  	parseNominalValue(line.substring(start + 2, end));
  	
  	records.add(activeRecord);
  }
  	
  private int skipTags(int start, String line, int tagsToSkip) {
  	int end = start;
  	for (int i = 0; i < tagsToSkip; ++i) {
  		end = line.indexOf('>', end + 1);
  	}
  	return end;
  }
  
  /**
	 * This method splits words and their frequencies and adds them to the record.
	 * 
	 * @param str The String of words and frequencies.
	 */
	private void parseBodyWords(String str) {
		String[] split = str.split("[= ]");
		for (int i = 0; i < split.length - 1; i+=2) {
			addAttribute(split[i], Double.parseDouble(split[i+1]));
		}
	}
	
	/**
	 * This method adds each place in the given String (separated by spaces) to the
	 * current record.
	 * 
	 * @param str The string.
	 */
	private void parseNominalValue(String str) {
		String[] split = str.trim().split(" ");
		for (String place : split) {
			addAttribute(place, 15.0);
		}
	}
	
	private void addAttribute(String str, double value) {
		if (str.length() > 0) {
			if (attributeIndices.containsKey(str)) {
				activeRecord.addValue(attributeIndices.get(str), value);
			} else {
				int index = attributeIndices.size();
				attributes.add(new Attribute(str));
				attributeIndices.put(str, index);
				activeRecord.addValue(index, value);
			}
		}
	}
	
	private void initInstances() {
		instances = new Instances("Data", attributes, records.size());
		
		for (Transaction record : records) {
			addRecord(record);
		}
	}
	
	private void addRecord(Transaction record) {
		if (record.values().size() > 0) {
			int[] indices = new int[record.values().size()];
			double[] values = new double[record.values().size()];
			
			int i = 0;
			for (Entry<Integer, Double> entry : record.values().entrySet()) {
				indices[i] = entry.getKey();
				values[i] = entry.getValue();
				++i;
			}
			
			SparseInstance instance = new SparseInstance(1.0, values, indices, i);
			instance.setDataset(instances);
			instances.add(instance);
		}
	}
}
