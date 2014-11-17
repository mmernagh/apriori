package src;

import java.util.List;

/**
 * Pair-wise compares the results from minhash results (using cosine) to the Jaccard results
 */
public class MasterComparer {
	
	private double sse;
	public MasterComparer(List<short[]> jaccardResults, List<short[]> minCosResults) {
		compare(jaccardResults, minCosResults);
	}
	
	public double sse() {
		return sse;
	}

    public void compare(List<short[]> jaccardResults, List<short[]> minCosResults){
        double diff=0;

        for (int i= 0; i < 4; i++) {
            for(int j =0; j < jaccardResults.get(i).length; j++)
            {
                diff += Math.abs(jaccardResults.get(i)[j] - minCosResults.get(i)[j]);
            }
        }

        sse = diff*diff;
    }
}
