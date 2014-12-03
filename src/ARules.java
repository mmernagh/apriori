import java.util.List;
import java.util.concurrent.Callable;

/**
 * ARules creates associative rules for a given list of {@link Transaction}s.
 */
public class ARules<V> implements Callable<List<Transaction>> {
	
	public ARules(String transFile, String classFile, double support, double confidence, List<Short> indices) {
		// TODO: indices is nullable
	}

	@Override
	public List<Transaction> call() throws Exception {
		return null;
	}
}
