package src;


import java.util.List;

/**
 * Created by kfritschie on 11/16/2014.
 */
public class CosineComparer {
    private double sumSquaredDifference;

    public CosineComparer(List<List<Integer>> fullSet, int start, int stop, List<short[]> jaccardResult){
    	compareResults(fullSet,start,stop, jaccardResult);
    }

    public double getSumSquaredDiff() {
    	return sumSquaredDifference;
    }

    private void compareResults(List<List<Integer>> words, int start, int stop, List<short[]> jaccardResult) {
        double cosine;
        int jaccardIndex = 0;
        for (int i = start; i < stop; ++i) {
            for (int j = i + 1; j < words.size(); ++j) {
                cosine = distance(words.get(i), words.get(j));
                if (jaccardIndex < jaccardResult.size() && jaccardResult.get(jaccardIndex)[0] == i && jaccardResult.get(jaccardIndex)[1] == j) {
                  sumSquaredDifference += Math.pow(difference(cosine, jaccardResult.get(jaccardIndex)[2]), 2.0);
                  ++jaccardIndex;
                } else {
                  sumSquaredDifference += Math.pow(difference(cosine, Short.MIN_VALUE), 2.0);
                }
            }
        }
    }
    
    private double difference(double cosineValue, short jaccardValue) {
    	short convertedCosine = (short) (cosineValue * 65535 - 32768);
    	int shortDiff = Math.abs(jaccardValue - convertedCosine);
    	return shortDiff / 65535.0;
    }

    public double distance( List<Integer> arg1,  List<Integer> arg2)
    {
    	int denom = arg1.size() * 2;
    	int numerator = 0;
    	for (int i = 0; i < arg1.size(); ++i) {
    		if (arg1.get(i) == arg2.get(i)) {
    			++numerator;
    			--denom;
    		}
    	}
    	
    	return (double) numerator / denom;
    }
}
