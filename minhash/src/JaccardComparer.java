package src;

import java.util.ArrayList;
import java.util.List;

public class JaccardComparer {
    private List<short[]> jaccardResults = new ArrayList<short[]>();

    public JaccardComparer(List<List<Integer>> fullSet, int start, int stop){
        fillResults(fullSet, start, stop);
    }

    public List<short[]> getResults() {
    	return jaccardResults;
    }

     private void fillResults(List<List<Integer>> words, int start, int stop) {
        double result;
        for (int i = start; i < stop; ++i) {
            for (int j = i + 1; j < words.size(); ++j) {
                result= similarity(words.get(i), words.get(j));
                if (Double.compare(0, result) < 0) {
                  storeResult(result, i, j);
                }
            }
        }
    }

    public void storeResult (double result, int smallerIndex, int biggerIndex)
    {
        short shortRes = (short) (result*65535-32768);
        short[] list = {(short) smallerIndex, (short) biggerIndex, shortRes};
        jaccardResults.add(list);
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
            return intersection/union;
    }
}
