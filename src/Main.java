import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;


public class Main {

	public static final String FEATURE_VECTORS = "feature_vectors.txt";
	public static final String ALL_TRANS = "all_trans.txt";
	public static final String CLASS_EXCL = "class_excl.txt";
	public static final String CLUST_8 = "Clusterer 8";
	public static final String CLUST_16 = "Clusterer 16";
	public static final Double SUPPORT = .75;
	public static final Double CONFIDENCE = .9;
	public static final int NUM_THREADS = 4;
	
	public static void main(String[] args) {
		
		FVParser fvParser = null;
		try {
			fvParser = new FVParser(FEATURE_VECTORS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Transaction> fvTransactions = fvParser.transactions();
		List<Short> classIndices = fvParser.classIndices();
		Instances instances = fvParser.instances();
		
		long allTime = System.currentTimeMillis();
		List<Transaction> arules = ARules.rules(ALL_TRANS, CLASS_EXCL, SUPPORT, CONFIDENCE);
		
		ExecutorService evalAllExecutor = Executors.newFixedThreadPool(NUM_THREADS);
		Set<Future<Double>> evalAllFutures = new HashSet<Future<Double>>(NUM_THREADS);
		for (int i = 0; i < NUM_THREADS; ++i) {
			Callable<Double> callable = new Evaluator<Double>(fvTransactions, arules, i * fvTransactions.size() / NUM_THREADS, (i + 1) * fvTransactions.size() / NUM_THREADS);
			Future<Double> future = evalAllExecutor.submit(callable);
			evalAllFutures.add(future);
		}
		
		Double allRulesAccuracy = 0.0;
		for (Future<Double> f : evalAllFutures) {
			try {
				allRulesAccuracy += f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		allTime = System.currentTimeMillis() - allTime;
		
		List<SimpleKMeans> kMeans = new ArrayList<SimpleKMeans>(2);
		kMeans.add(new SimpleKMeans());
		kMeans.add(new SimpleKMeans());

		for (int i = 0; i < 2; ++i) {
			SimpleKMeans k = kMeans.get(i);
			int size = (i + 1) * 8;
			// TODO: customize clusterer
			
			long time = System.currentTimeMillis();
			try {
				k.buildClusterer(instances);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
			Set<Future<Double>> futures = new HashSet<Future<Double>>(size);
			for (int j = 0; j < size; ++j) {
				Callable<Double> callable = new Evaluator<Double>(fvTransactions, arules, j * fvTransactions.size() / size, (j + 1) * fvTransactions.size() / size);
				Future<Double> future = exec.submit(callable);
				futures.add(future);
			}
			
			Double acc = 0.0;
			for (Future<Double> f : evalAllFutures) {
				try {
					acc += f.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("Cluster size: " + size);
			System.out.println("Time (ms): " + (System.currentTimeMillis() - time));
			System.out.format("Accuracy: %.2f\n", acc);
		}
	}

}
