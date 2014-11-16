package src;

import java.util.List;

/**this class should extend the runnable class for multi threading.
 * look at the example in the previous lab
 * Created by kfritschie on 11/12/2014.
 */
public class RunJaccard implements Runnable{

    private JaccardComparer jaccardComparer;
    private String name;
    private short[] results;

        public RunJaccard(List<List<Integer>> fullSet, int start, int stop){
            this.jaccardComparer = new JaccardComparer(fullSet, start, stop);
        }

        public void run() {
            long startTime = System.nanoTime();
            try {
               results = jaccardComparer.getResults();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.format("Total time for %s: %d s\n", name, (System.nanoTime() - startTime) / 1000000000);
        }
    public short[] getJaccardResults(){return results;}
}
