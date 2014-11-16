package src;

import java.util.List;

/**
 * Created by kfritschie on 11/16/2014.
 */
public class RunCosine implements Runnable{


   private CosineComparer cosineComparer;
   private String name;
   private short[] results;

   public RunCosine(List<List<Integer>> fullSet, int start, int stop){
    this.cosineComparer = new CosineComparer(fullSet, start, stop);
    }

    public void run() {
        long startTime = System.nanoTime();
        try {
             results = cosineComparer.getResults();
        } catch (Exception e) {
                e.printStackTrace();
        }
        System.out.format("Total time for %s: %d s\n", name, (System.nanoTime() - startTime) / 1000000000);
    }
    public short[] getCosineResults(){return results;}

}
