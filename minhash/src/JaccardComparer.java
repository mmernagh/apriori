package src;

import java.util.List;

public class JaccardComparer {
    private short jaccardResults[];

    public JaccardComparer(List<List<Integer>> fullSet, int start, int stop){fillResults(fullSet, start, stop);}

    public short[] getResults()
    {return jaccardResults;}

     private void fillResults(List<List<Integer>> words, int start, int stop) {
        double result;
        int index=0;
        for (int i = start; i < stop; ++i) {
            for (int j : words.get(i)) {
                result= similarity(words.get(i), words.get(j));
                storeResult(result,index);
                index++;
            }
        }
    }

    public void storeResult (double result, int index)
    {
        short shortRes = (short) result;
        jaccardResults[index]= shortRes;
    }

    public double similarity( List<Integer> arg1,  List<Integer> arg2)
        {
            double similarity = 0;
            int firstI, secondI;
            int firstNumValues = arg1.size(); //set a
            int secondNumValues = arg2.size(); //set b
            int union = firstNumValues+secondNumValues;// everything in both sets (union a,b)
            int classIndex = union-1;
            double intersection = 0;

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
                if (firstI == secondI) {
                    intersection ++;
                    i++;
                    j++;
                }
                else if (firstI > secondI) {
                    j++;
                }
                else {
                    i++;
                }
            }
            similarity = intersection/union;

            return similarity;

    }

}
