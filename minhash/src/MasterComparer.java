package src;

import java.util.List;

/**
 * Pair-wise compares the min-hash sketch, and compares the results to a list of distances.
 */
public class MasterComparer {
	
	private double sse;
	public MasterComparer(short[] jaccardResults, short[] minCosResults) {
		compare(jaccardResults, minCosResults);
	}
	
	public double sse() {
		return sse;
	}

    public void compare(short[] jaccardResults, short[] minCosResults){
        double diff=0;

        for (int i= 0; i < jaccardResults.length; i++) {
            diff += Math.abs(jaccardResults[i] - minCosResults[i]);
        }
        
        sse = diff*diff;
    }
}
