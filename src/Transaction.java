import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Transaction {

	private short[] attributeIndices = new short[5];
	private short index = 0;
	private List<Short> labels = new ArrayList<Short>();
	
	public void addIndex(short i) {
		attributeIndices[index++] = i;
	}
	
	public void addClassIndex(short i) {
		labels.add(i);
	}
	
	public short attributeSize() {
		return index;
	}
	
	public short[] attributes() {
		return attributeIndices;
	}
	
	public List<Short> classLabels() {
		return labels;
	}
	
	public void sortLists() {
		Arrays.sort(attributeIndices);
		Collections.sort(labels);
	}
}
