import java.util.Arrays;

public class Transaction {

	private short[] attributeIndices = new short[5];
	private short index = 0;
	private short classIndex = 0;
	
	public Transaction(Transaction t) {
		short i;
		for (i = 0; i < t.attributes().length; ++i)  {
			attributeIndices[i] = t.attributes()[i];
		}
		index = i;
	}
	
	public Transaction() {
		// Intentionally left blank
	}
	
	public void addIndex(short i) {
		attributeIndices[index++] = i;
	}
	
	public void setClassIndex(short i) {
		classIndex = i;
	}
	
	public short[] attributes() {
		return attributeIndices;
	}
	
	public short classIndex() {
		return classIndex;
	}
	
	public void sortAttributes() {
		Arrays.sort(attributeIndices);
	}
}
