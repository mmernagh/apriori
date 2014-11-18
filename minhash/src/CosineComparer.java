package src;


import java.util.List;

/**
 * Created by kfritschie on 11/16/2014.
 */
public class CosineComparer {
    private double sumSquaredDifference;

    public CosineComparer(List<List<Integer>> fullSet, int start, int stop, short[] jaccardResult){
    	compareResults(fullSet,start,stop, jaccardResult);
    }

    public double getSumSquaredDiff() {
    	return sumSquaredDifference;
    }

    private void compareResults(List<List<Integer>> words, int start, int stop, short[] jaccardResult) {
        double cosine;
        int index=0;
        for (int i = start; i < stop; ++i) {
            for (int j = i + 1; j < stop; ++j) {
                cosine = distance(words.get(i), words.get(j));
                sumSquaredDifference += Math.pow(difference(cosine, jaccardResult[index]), 2.0);
                //this is assuming that the correct jaccard array is passed to the CosineComparer
                index++;
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
        double distance =0;
        double normA, normB;
        normA = 0;
        normB = 0;

        for (int i = 0; i <arg1.size(); i++)
        {
            double diff= Math.abs(arg1.get(i)-arg2.get(i));
            normA += Math.pow(arg1.get(i),2);
            normB += Math.pow(arg2.get(i), 2);
            distance += diff*diff;
        }
        distance = distance/Math.sqrt(normA)/Math.sqrt(normB);
        distance = 1-distance;

        return distance;
    }
}
