package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the feature vector file and generates the internal representation of the map.
 */
public class Mapper {
	
	private static final int FVLENGTH = 19024;

	private List<List<Integer>> fVLists = new ArrayList<List<Integer>>(FVLENGTH);
	private List<List<Integer>> wordOccurrences = new ArrayList<List<Integer>>(FVLENGTH);

	private int fVCount = 0;
	private Map<String, Integer> wordIndices = new HashMap<String, Integer>(30000);
	private int nextWordIndex = 0;
	
	Mapper(String file) throws Exception {	
		BufferedReader reader = null;
  	reader = new BufferedReader(new FileReader(file));
  	
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
	
	public List<List<Integer>> getFV() {
		return fVLists;
	}
	
	public List<List<Integer>> getWordLists() {
		return wordOccurrences;
	}
	
	private void parseLine(String line) {
		String[] split = line.split(" ");
		if (split.length > 0) {
			List<Integer> fvIndices = new ArrayList<Integer>(5);
			for (String s : split) {
				if (!wordIndices.containsKey(s)) {
					wordIndices.put(s, nextWordIndex);
					wordOccurrences.add(new ArrayList<Integer>(FVLENGTH));
					++nextWordIndex;
				}
				fvIndices.add(wordIndices.get(s));
				wordOccurrences.get(wordIndices.get(s)).add(fVCount);
			}
			fVLists.add(fvIndices);
			fVCount++;
		}
	}
}
