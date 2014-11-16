package src;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Generates the Jaccard similarity of all feature vectors pairwise, and compares this to
 * a min-hash comparison of all feature vectors.
 */
public class Main {

	public static final String FILENAME = "feature_vectors.txt";
	public static final List<Integer> SKETCH_SIZES = Arrays.asList(16, 32, 64, 128);
    private static final int numthreads = 4;

	public static void main(String[] args) {

        List<Thread> threads = new ArrayList<Thread>(numthreads);

		Mapper mapper = null;
		try {
			mapper = new Mapper(FILENAME);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		List<List<Integer>> fVLists = mapper.getFV();
		List<List<Integer>> wordLists = mapper.getWordLists();
		
		mapper = null;

		long startTime = System.currentTimeMillis();
		
		// TODO: build comparisons Jaccard
		
		System.out.format("Time to generate Jaccard comparisons: %d\n", 
				(System.currentTimeMillis() - startTime) / 1000);
		
		Minhasher minhasher;
		List<List<Integer>> sketch;
		
		for (int a : SKETCH_SIZES) {
			minhasher = new Minhasher(wordLists, fVLists.size(), a);
			sketch = minhasher.getSketch();
						
			// TODO: compare minhasher to jaccard results
			
		}
		
		System.out.format("Time to generate Minhash comparisons: %d\n",
				(System.currentTimeMillis() - startTime) / 1000);
	}

}
