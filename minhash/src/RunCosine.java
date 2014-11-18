package src;

import java.util.List;

/**
 * Created by kfritschie on 11/16/2014.
 */
public class RunCosine implements Runnable{


   private CosineComparer cosineComparer;
   private int start;
   private int end;
   private List<List<Integer>> fvList;
   private List<short[]> jaccardResults;
   private double se;

   public RunCosine(List<List<Integer>> fullSet, int start, int stop, List<short[]> jaccardResult){
  	 this.start = start;
  	 this.end = stop;
  	 this.jaccardResults = jaccardResult;
  	 this.fvList = fullSet;
   }

    public void run() {
        long startTime = System.nanoTime();
        cosineComparer = new CosineComparer(fvList, start, end, jaccardResults);
        System.out.format("Total time for cosine(s): %d s\n", (System.nanoTime() - startTime) / 1000000000);
        se = cosineComparer.getSumSquaredDiff();
    }
    
    public double getSumError(){return se;}

}
