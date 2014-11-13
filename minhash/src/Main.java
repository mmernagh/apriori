package src;

/**
 * Generates the Jaccard similarity of all feature vectors pairwise, and compares this to
 * a min-hash comparison of all feature vectors.
 */
public class Main {

	public static final String FILENAME = "feature_vectors.txt";
	
	public static void main(String[] args) {
		
		try {
			Mapper mapper = new Mapper(FILENAME);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
