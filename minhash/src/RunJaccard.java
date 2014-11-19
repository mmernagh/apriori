package src;

import java.util.List;

/**this class should extend the runnable class for multi threading.
 * look at the example in the previous lab
 * Created by kfritschie on 11/12/2014.
 */
public class RunJaccard implements Runnable{

    private JaccardComparer jaccardComparer;
    private int start;
    private int end;
    private List<List<Integer>> fvList;
    private List<short[]> results;

    public RunJaccard(List<List<Integer>> fullSet, int start, int stop){
    	this.start = start;
    	this.end = stop;
    	this.fvList = fullSet;
    }

    public void run() {
        jaccardComparer = new JaccardComparer(fvList, start, end);
        results = jaccardComparer.getResults();
    }
    
    public List<short[]> getJaccardResults(){
    	return results;
    }
}
