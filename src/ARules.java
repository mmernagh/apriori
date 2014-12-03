import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * ARules creates associative rules for a given list of {@link Transaction}s.
 */
public class ARules<V> implements Callable<List<Transaction>> {
	
	private String setsFile;
	private String labelFile;
	private String outFile;
	private double support;
	private double confidence;
	
	public ARules(String transFile, String classFile, String outFile, double support, double confidence) {
		this.setsFile = transFile;
		this.labelFile = classFile;
		this.outFile = outFile;
		this.support = support;
		this.confidence = confidence;
	}

	@Override
	public List<Transaction> call() throws Exception {
		/* Command options:
		 * -q-1: sort items in descending order w.r.t. item frequency
		 * -y: a posteriori pruning of infrequent itemsets
		 * -f" " fields are only separated by commas
		 * -i":" output format: <consequent>: <antecedent>
		 * 	-tr: generate apriori rules
		*/
		String cmd = "apriori -q-1 -y -f\" \" -i\":\" -tr -s" + (int)(support * 100) + 
				" -c" + (int)(confidence * 100) + " -R" + labelFile + " " + setsFile + " " + outFile;
		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		
		// Read output file into a list of transactions
		List<Transaction> results = new ArrayList<Transaction>();
		BufferedReader reader = null;
  	reader = new BufferedReader(new FileReader(outFile));
  	
  	try {
			String line  = reader.readLine();
			while (line != null) {
				results.add(parseLine(line));
				line  = reader.readLine();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			reader.close();
		}
  	return results;
	}
	
	/**
	 * Read a line of the output file into a {@link Transaction}.
	 * 
	 * @param line The line
	 * @return The transaction
	 */
	private Transaction parseLine(String line) {
		Transaction t = new Transaction();
		String[] words = line.split("[: ]");
		if (words.length > 0) {
			if (!words[0].startsWith("(")) {
				t.setClassIndex(Short.parseShort(words[0]));
			}
			int i = 1;
			while (i < words.length && !words[i].startsWith("(")) {
				t.addIndex(Short.parseShort(words[i++]));
			}
		}
		t.sortAttributes();
		return t;
	}
}
