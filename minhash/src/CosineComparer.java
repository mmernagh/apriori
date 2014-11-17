package src;

import java.util.List;

/**
 * Created by kfritschie on 11/16/2014.
 */
public class CosineComparer {
   // private short cosineResults[];
    private double sse;

    public CosineComparer(List<List<Integer>> fullSet, int start, int stop, short[] jaccardResult){
        compareResults(fullSet,start,stop, jaccardResult);
        }

    public double getSSE()
    {return sse;}

    private void compareResults(List<List<Integer>> words, int start, int stop, short[] jaccardResult) {
        double cosine;
        int index=0;
        for (int i = start; i < stop; ++i) {
            for (int j : words.get(i)) {
                cosine = distance(words.get(i), words.get(j));
                sse += Math.abs(jaccardResult[index] - cosine);
                //this is assuming that the correct jaccard array is passed to the CosineComparer
                index++;
            }
        }
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
