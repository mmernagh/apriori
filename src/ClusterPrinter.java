import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClusterPrinter {

	private ClusterPrinter() {
		// Intentionally left blank
	}

	public static List<List<Short>> print(int[] assignments, List<Transaction> fvTransactions, 
			int size, List<Integer> defaultIndices) {
		
		List<List<Short>> clusterIndices = new ArrayList<List<Short>>(size);
		Map<Integer, Integer> centroidListIndices = new HashMap<Integer, Integer>(size);
		List<Map<Short, Integer>> clusterClassCount = new ArrayList<Map<Short, Integer>>(size);
		
		for (int i = 0; i < assignments.length; ++i) {
			// New centroid
			if (!centroidListIndices.containsKey(assignments[i])) {
				centroidListIndices.put(assignments[i], clusterIndices.size());
				clusterClassCount.add(new HashMap<Short, Integer>());
				clusterIndices.add(new ArrayList<Short>());
			}
			
			// Add transaction to correct list
			clusterIndices.get(centroidListIndices.get(assignments[i])).add((short) i);
			
			// Update count of class labels in this cluster
			Map<Short, Integer> classCount = clusterClassCount.get(centroidListIndices.get(assignments[i]));
			for (short s : fvTransactions.get(i).classLabels()) {
				if (!classCount.containsKey(s)) {
					classCount.put(s, 1);
				} else {
					classCount.put(s, classCount.get(s) + 1);
				}
			}
		}
		
		// Find default class label for each cluster
		for (int j = 0; j < clusterClassCount.size(); ++j) {
			int smallestCount = Integer.MAX_VALUE;
			short smallestIndex = 0;
			for (Map.Entry<Short, Integer> e : clusterClassCount.get(j).entrySet()) {
				if (e.getValue() < smallestCount) {
					smallestCount = e.getValue();
					smallestIndex = e.getKey();
				}
			}
			defaultIndices.set(j, (int) smallestIndex);
		}
		return clusterIndices;
	}
}
