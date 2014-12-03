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
		
		// Parse feature vector file
		FeatureVectorParser fvParser = null;
		try {
			fvParser = new FeatureVectorParser(FEATURE_VECTORS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Transaction> fvTransactions = fvParser.transactions();
		Instances instances = fvParser.instances();
		int mostFrequentClass = fvParser.mostFrequentClassIndex();
		
		long allTime = System.currentTimeMillis();
		
		// Generate apriori rules for entire feature vector set
		ExecutorService aruleExec = Executors.newSingleThreadExecutor();
		Callable<List<Transaction>> aruleCallable = new ARules<List<Transaction>>(
				ALL_TRANS, CLASS_EXCL, SUPPORT, CONFIDENCE, null);
		Future<List<Transaction>> aruleFuture = aruleExec.submit(aruleCallable);
		List<Transaction> arules = null;
		try {
			arules = aruleFuture.get();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
		
		// Evaluate apriori rules
		ExecutorService evalAllExecutor = Executors.newFixedThreadPool(NUM_THREADS);
		Set<Future<Double>> evalAllFutures = new HashSet<Future<Double>>(NUM_THREADS);
		for (int i = 0; i < NUM_THREADS; ++i) {
			Callable<Double> callable = new Evaluator<Double>(fvTransactions, arules, 
					i * fvTransactions.size() / NUM_THREADS, (i + 1) * fvTransactions.size() / NUM_THREADS, mostFrequentClass);
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
		System.out.println("Time for entire doc (ms): " + (System.currentTimeMillis() - allTime));
		System.out.format("Accuracy for entire doc: %.4f\n", allRulesAccuracy);
		
		// Cluster and then create rules
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
			
			// Create rules
			int[] assignments = {};
			try {
				assignments = k.getAssignments();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			List<String> transactionFileNames = new ArrayList<String>(size);
			List<String> classFileNames = new ArrayList<String>(size);
			List<Integer> defaultIndices = new ArrayList<Integer>(size);
			
			// Print clusters to respective results and get lists of clusters
			List<List<Short>> clusterIndices = ClusterPrinter.print(assignments, fvTransactions, 
						transactionFileNames, classFileNames, size, defaultIndices);
						
			// Generate apriori rules for each cluster
			ExecutorService clAruleExec = Executors.newFixedThreadPool(NUM_THREADS);
			Set<Future<List<Transaction>>> clFutures = new HashSet<Future<List<Transaction>>>(size);
			for (int p = 0; p < size; ++p) {
				Callable<List<Transaction>> clAruleCallable = new ARules<List<Transaction>>(
						transactionFileNames.get(p), classFileNames.get(p), SUPPORT, CONFIDENCE, clusterIndices.get(p));
				Future<List<Transaction>> clAruleFuture = clAruleExec.submit(clAruleCallable);
				clFutures.add(clAruleFuture);
			}
			
			List<List<Transaction>> clArules = new ArrayList<List<Transaction>>(size);
			for (Future<List<Transaction>> f : clFutures) {
				try {
					clArules.add(f.get());
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				} catch (ExecutionException e2) {
					e2.printStackTrace();
				}
			}
			
			// Evaluate apriori rules for each cluster
			ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
			Set<Future<Double>> futures = new HashSet<Future<Double>>(size);
			for (int j = 0; j < size; ++j) {
				Callable<Double> callable = new Evaluator<Double>(fvTransactions, clArules.get(j), 
						j * fvTransactions.size() / size, (j + 1) * fvTransactions.size() / size, defaultIndices.get(j));
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
			System.out.format("Accuracy: %.4f\n", acc);
		}
	}

}
