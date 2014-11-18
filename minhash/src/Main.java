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
  private static final int NUM_THREADS = 4;

	public static void main(String[] args) {

    List<Thread> threads = new ArrayList<Thread>(NUM_THREADS);
    List<short[]> jaccardResults = new ArrayList<short[]>();
    double SSE=0;
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

    RunJaccard runJaccard1 = new RunJaccard(fVLists, 0, fVLists.size()*5/32);
    RunJaccard runJaccard2 = new RunJaccard(fVLists, fVLists.size()*5/32, fVLists.size()*5/16);
    RunJaccard runJaccard3 = new RunJaccard(fVLists, fVLists.size()*5/16, fVLists.size()/2);
    RunJaccard runJaccard4 = new RunJaccard(fVLists, fVLists.size()/2, fVLists.size());

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
		
		runJaccard1 = null;
	  runJaccard2 = null;
	  runJaccard3 = null;
	  runJaccard4 = null;
	    
		Minhasher minhasher;
		List<List<Integer>> sketch;
		
		for (int a : SKETCH_SIZES) {
			minhasher = new Minhasher(wordLists, fVLists.size(), a);
			sketch = minhasher.getSketch();
			
			minhasher = null;

			startTime = System.currentTimeMillis();
			
      RunCosine runCos1 = new RunCosine(sketch, 0, sketch.size()*5/32, jaccardResults.get(0));
      RunCosine runCos2 = new RunCosine(sketch, sketch.size()*5/32, sketch.size()*5/16, jaccardResults.get(1));
      RunCosine runCos3 = new RunCosine(sketch, sketch.size()*5/16, sketch.size()/2, jaccardResults.get(2));
      RunCosine runCos4 = new RunCosine(sketch, sketch.size()/2, sketch.size(), jaccardResults.get(3));

      // Cosine Sketch comparisons
      threads.add(new Thread(runCos1));
      threads.get(0).start();
      threads.add(new Thread(runCos2));
      threads.get(1).start();
      threads.add(new Thread(runCos3));
      threads.get(2).start();
      threads.add(new Thread(runCos4));
      threads.get(3).start();

      try {
          for (Thread thread : threads) {
              thread.join();
          }
      } catch (InterruptedException e) {
          e.printStackTrace();
      }

      SSE = runCos1.getSumError() + runCos2.getSumError() + runCos3.getSumError() + runCos4.getSumError();
      threads.clear();
      
      System.out.format("Time to generate Minhash comparisons: %d\n",
  				(System.currentTimeMillis() - startTime) / 1000);

      // Now do a master comparison of the jaccard results and the cosine sketch results
  		System.out.println("The sum squared error is : " + SSE);
		}
	}
}
