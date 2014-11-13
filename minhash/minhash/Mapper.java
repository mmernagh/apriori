package minhash;

import java.util.List;
import java.util.Map;

/**
 * Reads the feature vector file and generates the internal representation of the map.
 */
public class Mapper {

	Map<Integer, List<Integer>> fVLists;
	Map<Integer, List<Integer>> wordOccurrences;

	Mapper(String file) {
		
	}
	
	public Map<Integer, List<Integer>> getFV() {
		return fVLists;
	}
	
	public Map<Integer, List<Integer>> getWordLists() {
		return wordOccurrences;
	}
}
