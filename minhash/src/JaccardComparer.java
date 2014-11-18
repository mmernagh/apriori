package src;

import java.util.List;

public class JaccardComparer {
    private short jaccardResults[];

    public JaccardComparer(List<List<Integer>> fullSet, int start, int stop){
        jaccardResults = new short[nChooseTwo(stop - start) * fullSet.size()];
        fillResults(fullSet, start, stop);
    }
    
    private int nChooseTwo(int n) {
    	return (n * n - 1) / 2;
    }

    public short[] getResults() {
    	return jaccardResults;
    }

     private void fillResults(List<List<Integer>> words, int start, int stop) {
        double result;
        int index=0;
        for (int i = start; i < stop; ++i) {
            for (int j = i + 1; j < words.size(); ++j) {
                result= similarity(words.get(i), words.get(j));
                storeResult(result,index);
                index++;
            }
        }
    }

    public void storeResult (double result, int index)
    {
        short shortRes = (short) (result*65535-32768);
        jaccardResults[index]= shortRes;
    }

    public double similarity( List<Integer> arg1,  List<Integer> arg2)
        {
            int firstI, secondI;
            int firstNumValues = arg1.size(); //set a
            int secondNumValues = arg2.size(); //set b
            int union = firstNumValues+secondNumValues;// everything in both sets (union a,b)

            double intersection = 0;

            for (int i = 0, j= 0; i < firstNumValues && j < secondNumValues;)
                {
                firstI = arg1.get(i);
                secondI = arg2.get(j);
                if (firstI == secondI) {
                    intersection ++;
                    i++;
                    j++;
                    --union;
                }
                else if (firstI > secondI) {
                    j++;
                }
                else {
                    i++;
                }
            }
            return 1 - intersection/union;
    }
}
