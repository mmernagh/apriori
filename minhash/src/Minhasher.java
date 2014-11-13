package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Minhasher {

	private List<Integer> hashSeed;
	private List<List<Integer>> sketch;
	
	public Minhasher(List<List<Integer>> words, int numFV, int numberMinhashes) {
		hashSeed = getSeeds(numberMinhashes);
		sketch = initSketch(numberMinhashes, numFV);
		fillSketch(words);
	}
	
	public List<List<Integer>> getSketch() {
		return sketch;
	}
	
	private List<Integer> getSeeds(int num) {
		List<Integer> seeds = new ArrayList<Integer>(num);
		Random rand = new Random();
		for (int i = 0; i < num; ++i) {
			seeds.add(rand.nextInt());
		}
		return seeds;
	}
	
	private List<List<Integer>> initSketch(int numHashes, int numFV) {
		List<List<Integer>> sketch = new ArrayList<List<Integer>>(numHashes);
		Integer[] vals = new Integer[numFV];
		Arrays.fill(vals, Integer.MAX_VALUE);
		for (int i = 0; i < numHashes; ++i) {
			sketch.add(Arrays.asList(vals));
		}
		return sketch;
	}
	
	private void fillSketch(List<List<Integer>> words) {
		List<Integer> hashes;
		for (int i = 0; i < words.size(); ++i) {
			for (int a : words.get(i)) {
				hashes = hash(i);
				
				if (sketch.get(arg0))
			}
		}
	}
	
	private List<Integer> hash(int i) {
		List<Integer> hashes = new ArrayList<Integer>(hashSeed.size());
		for (int a = 0; a < hashSeed.size(); ++a) {
			hashes.add(hashSeed.get(a) ^ i);
		}
		return hashes;
	}
}
