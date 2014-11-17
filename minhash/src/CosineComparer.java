package src;

import java.util.List;

/**
 * Created by kfritschie on 11/16/2014.
 */
public class CosineComparer {
    private short cosineResults[];

    public CosineComparer(List<List<Integer>> fullSet, int start, int stop){fillResults(fullSet, start, stop);}

    public short[] getResults()
    {return cosineResults;}

    private void fillResults(List<List<Integer>> words, int start, int stop) {
        double result;
        int index=0;
        for (int i = start; i < stop; ++i) {
            for (int j : words.get(i)) {
                result= distance(words.get(i), words.get(j));
                storeResult(result,index);
                index++;
            }
        }
    }

    public void storeResult (double result, int index)
    {
        short shortRes = (short) result;
        cosineResults[index]= shortRes;
    }

    public double distance( List<Integer> arg1,  List<Integer> arg2)
    {

        double distance =0;
        int firstI, secondI;
        int firstNumValues = arg1.size(); //set a
        int secondNumValues = arg2.size(); //set b
        int union = firstNumValues+secondNumValues;// everything in both sets (union a,b)
        int classIndex = union-1;
        double normA, normB;
        normA = 0;
        normB = 0;

        for (int i = 0, j= 0; i < firstNumValues || j < secondNumValues;)
        {
            if (i >= firstNumValues)
                firstI = union;
            else firstI = arg1.get(i);

            if (j >= secondNumValues)
                secondI = union;
            else secondI = arg2.get(j);

            if (firstI == classIndex) { //end of row
                i++;
                continue;
            }
            if (secondI == classIndex) {// end of column
                j++;
                continue;
            }
            double diff;
            if (firstI == secondI) {
                diff = Math.abs(arg1.get(i)-arg2.get(j));
                normA += Math.pow(arg1.get(i), 2);
                normB += Math.pow(arg2.get(j), 2);
                i++;
                j++;
            }
            else if (firstI > secondI) {
                diff = Math.abs(arg2.get(j));
                normB+= Math.pow(arg2.get(j), 2);
                j++;
            }
            else {
                diff = Math.abs(arg1.get(i));
                normA += Math.pow(arg1.get(i), 2);
                i++;
            }
            distance += diff*diff;
        }
        distance = distance/Math.sqrt(normA)/Math.sqrt(normB);
        distance = 1-distance;

        return distance;

    }
}
