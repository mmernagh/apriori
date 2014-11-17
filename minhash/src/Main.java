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
        List<short[]> jaccardResults = new ArrayList<short[]>();
        List<short[]> cosineResults = new ArrayList<short[]>();
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

        RunJaccard runJaccard1 = new RunJaccard(wordLists, 0, wordLists.size()*5/32);
        RunJaccard runJaccard2 = new RunJaccard(wordLists, wordLists.size()*5/32, wordLists.size()*5/16);
        RunJaccard runJaccard3 = new RunJaccard(wordLists, wordLists.size()*5/16, wordLists.size()/2);
        RunJaccard runJaccard4 = new RunJaccard(wordLists, wordLists.size()/2, wordLists.size());

		// Jaccard comparisons
            threads.add(new Thread(runJaccard1));
            threads.get(0).start();
            threads.add(new Thread(runJaccard2));
            threads.get(1).start();
            threads.add(new Thread(runJaccard3));
            threads.get(2).start();
            threads.add(new Thread(runJaccard4));
            threads.get(3).start();

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jaccardResults.add(runJaccard1.getJaccardResults());
        jaccardResults.add(runJaccard2.getJaccardResults());
        jaccardResults.add(runJaccard3.getJaccardResults());
        jaccardResults.add(runJaccard4.getJaccardResults());

        threads.clear();
		
		System.out.format("Time to generate Jaccard comparisons: %d\n", 
				(System.currentTimeMillis() - startTime) / 1000);
		
		Minhasher minhasher;
		List<List<Integer>> sketch;
		
		for (int a : SKETCH_SIZES) {
			minhasher = new Minhasher(wordLists, fVLists.size(), a);
			sketch = minhasher.getSketch();

            RunCosine runCos1 = new RunCosine(sketch, 0, sketch.size()*5/32);
            RunCosine runCos2 = new RunCosine(sketch, sketch.size()*5/32, sketch.size()*5/16);
            RunCosine runCos3 = new RunCosine(sketch, sketch.size()*5/16, sketch.size()/2);
            RunCosine runCos4 = new RunCosine(sketch, sketch.size()/2, sketch.size());

            // Cosine Sketch comparisons
            threads.add(new Thread(runJaccard1));
            threads.get(0).start();
            threads.add(new Thread(runJaccard2));
            threads.get(1).start();
            threads.add(new Thread(runJaccard3));
            threads.get(2).start();
            threads.add(new Thread(runJaccard4));
            threads.get(3).start();

            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cosineResults.add(runCos1.getCosineResults());
            cosineResults.add(runCos2.getCosineResults());
            cosineResults.add(runCos3.getCosineResults());
            cosineResults.add(runCos4.getCosineResults());

            threads.clear();

		}

		System.out.format("Time to generate Minhash comparisons: %d\n",
				(System.currentTimeMillis() - startTime) / 1000);

        // Now do a master comparison of the jaccard results and the cosine sketch results
        MasterComparer masterComparer = new MasterComparer(jaccardResults,cosineResults);
       double sse= masterComparer.sse();
       System.out.println("The sum squared error is : " + sse);
	}

}
