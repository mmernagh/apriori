package src;

import java.util.List;
import java.util.Map;

public class JaccardComparer {
        public JaccardComparer(Map<Integer, List<Integer>> map){
            super();
        }
        public double distance ( List<Integer> arg1,  List<Integer> arg2)
        {
            double similarity = 0;
            int firstI, secondI;
            int firstNumValues = arg1.size(); //a
            int secondNumValues = arg2.size(); //b
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
