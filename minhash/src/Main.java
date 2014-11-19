package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates the Jaccard similarity of all feature vectors pairwise, and compares this to
 * a min-hash comparison of all feature vectors.
 */
public class Main {

	public static final String FILENAME = "feature_vectors.txt";
	public static final List<Integer> SKETCH_SIZES = Arrays.asList(16, 32, 64, 128);
  private static final int NUM_THREADS = 4;
  private static final List<Double> splits = Arrays.asList(0.0, 1 - Math.sqrt(3) / 2, 1 - Math.sqrt(2) / 2, .5, 1.0);

	public static void main(String[] args) {

    List<Thread> threads = new ArrayList<Thread>(NUM_THREADS);
    List<List<short[]>> jaccardResults = new ArrayList<List<short[]>>(NUM_THREADS);
    double SSE = 0;
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
		
		List<RunJaccard> runJaccards = new ArrayList<RunJaccard>(NUM_THREADS);
		
		for (int i = 0; i < NUM_THREADS; ++i) {
			runJaccards.add(new RunJaccard(fVLists, (int) (fVLists.size() * splits.get(i)), (int) (fVLists.size() * splits.get(i + 1))));
		}

		// Jaccard comparisons
		for (int k = 0; k < NUM_THREADS; ++k) {
			threads.add(new Thread(runJaccards.get(k)));
	    threads.get(k).start();
		}

    try {
        for (Thread thread : threads) {
            thread.join();
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    
    for (int j = 0; j < NUM_THREADS; ++j) {
    	jaccardResults.add(runJaccards.get(j).getJaccardResults());
    }

    threads.clear();
		
		System.out.format("Time to generate Jaccard comparisons: %d\n", 
				(System.currentTimeMillis() - startTime) / 1000);
		
		runJaccards.clear();
	    
		Minhasher minhasher;
		List<List<Integer>> sketch;
		List<RunCosine> runCosines = new ArrayList<RunCosine>(NUM_THREADS);
		
		for (int a : SKETCH_SIZES) {
			SSE = 0;
			minhasher = new Minhasher(wordLists, fVLists.size(), a);
			sketch = minhasher.getSketch();
			
			minhasher = null;

			startTime = System.currentTimeMillis();
			
			for (int m = 0; m < NUM_THREADS; ++m) {
				runCosines.add(new RunCosine(sketch, (int) (sketch.size() * splits.get(m)), (int) (sketch.size() * splits.get(m + 1)), jaccardResults.get(m)));
			}

			for (int n = 0; n < NUM_THREADS; ++n) {
				threads.add(new Thread(runCosines.get(n)));
	      threads.get(n).start();
			}

      try {
          for (Thread thread : threads) {
              thread.join();
          }
      } catch (InterruptedException e) {
          e.printStackTrace();
      }

      for (RunCosine runC : runCosines) {
      	SSE += runC.getSumError();
      }
      threads.clear();
      runCosines.clear();
      
      System.out.format("Time to generate Minhash comparison %d: %d\n", a,
  				(System.currentTimeMillis() - startTime) / 1000);

      // Now do a master comparison of the jaccard results and the cosine sketch results
  		System.out.println("The sum squared error is : " + SSE);
		}
	}
}
