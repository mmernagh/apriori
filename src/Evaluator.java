import java.util.List;
import java.util.concurrent.Callable;


public class Evaluator<V> implements Callable<Double> {
	
	private List<Transaction> transactions;
	private List<Transaction> rules;
	private List<Short> indices;
	private int start;
	private int end; 
	private int defaultClass;
	
	/**
	 * Creates an evaluator that will evaluate the class labels stored in a list of
	 * {@link Transaction}s against the label predicted by a list of rules (also stored
	 * as a list of Transactions). If no label is found using the rules, a default label
	 * is compared to the true label. The Transactions to be evaluated are determined by either:
	 *  - a list of indices
	 *  - a start and end index, e.g., [start, end). 
	 * If a list of indices is supplied, it will be used.
	 * 
	 * @param fv The list of Transactions to be evaluated.
	 * @param rules The rules.
	 * @param validFV Nullable. The list of indices of the transactions to be evaluated.
	 * @param start The start index of the transactions to be evaluated.
	 * @param end The end index of the transactions to be evaluated.
	 * @param defaultClass The default class label.
	 */
	public Evaluator(List<Transaction> fv, List<Transaction> rules, 
			List<Short> validFV, int start, int end, int defaultClass) {
		this.transactions = fv;
		this.rules = rules;
		this.indices = validFV;
		this.start = start;
		this.end = end;
		this.defaultClass = defaultClass;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 * Returns the ratio of correctly predicted classes to attempted class predictions.
	 */
	@Override
	public Double call() throws Exception {
		int count = 0;
		int success = 0;
		if (indices != null) {
			for (int i = start; i < end; ++i) {
				Transaction t = transactions.get(i);
				if (correctlyLabeled(t)) {
					++success;
				}
				++count;
			}
		} else {
			for (short s : indices) {
				Transaction t = transactions.get(s);
				if (correctlyLabeled(t)) {
					++success;
				}
				++count;
			}
		}
		return (double) success / count;
	}
	
	/**
	 * Returns true iff the predicted label is contained in the list of the Transaction's
	 * class labels.
	 * 
	 * @param t The transaction
	 * @return
	 */
	private boolean correctlyLabeled(Transaction t) {
		int i = 0;
		short label = getLabel(t);
		while (i < t.classLabels().size()) {
			if (label == t.classLabels().get(i)) {
				return true;
			} else if (label < t.classLabels().get(i)) {
				return false;
			} else {
				++i;
			}
		}
		return false;
	}
	
	/**
	 * Returns the predicted label for a {@link Transaction}. To do so, it traverses
	 * the (already-sorted) list of rules, searching for a rule all of whose antecedents
	 * are members of the attributes of the given Transaction. The first match
	 * @param t
	 * @return
	 */
	private short getLabel(Transaction t) {
		int ruleIndex = 0;
		while (ruleIndex < rules.size()) {
			Transaction rule = rules.get(ruleIndex);
			short rAttrIndex = 0;
			short tAttrIndex = 0;
			while (rAttrIndex < rule.attributeSize() && tAttrIndex < t.attributeSize()) {
				if (rule.attributes()[rAttrIndex] < t.attributes()[tAttrIndex]) {
					break;
				} else if (rule.attributes()[rAttrIndex] == t.attributes()[tAttrIndex]) {
					++rAttrIndex;
				}
				++tAttrIndex;
			}
			if (rAttrIndex == rule.attributeSize()) {
				return rule.classLabels().get(0);
			}
		}
		return (short) defaultClass;
	}
}
