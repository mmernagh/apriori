package src;

import java.util.List;

/**
 * Created by kfritschie on 11/16/2014.
 */
public class RunCosine implements Runnable{


   private CosineComparer cosineComparer;
   private String name;
   private double sse;

   public RunCosine(List<List<Integer>> fullSet, int start, int stop, short[] jaccardResult){
    this.cosineComparer = new CosineComparer(fullSet, start, stop, jaccardResult);
    }

    public void run() {
        long startTime = System.nanoTime();
        try {
             sse = cosineComparer.getSSE();
        } catch (Exception e) {
                e.printStackTrace();
        }
        System.out.format("Total time for %s: %d s\n", name, (System.nanoTime() - startTime) / 1000000000);
    }
    public double getSSE(){return sse;}

}
