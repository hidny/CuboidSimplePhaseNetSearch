package SimplePhaseSearch.fourthIteration;

public class CustomDangerousQueue {

	int queue[];
	
	int firstInIndex = 0;
	int firstOutIndex = 0;
	
	
	public CustomDangerousQueue(int size) {
		queue = new int[size];
	}
	
	public void add(int item) {
		queue[firstInIndex] = item;
		firstInIndex++;
	}
	public int poll() {
		int output = queue[firstOutIndex];
		firstOutIndex++;
		return output;
	}
	
	public boolean isEmpty() {
		return firstInIndex == firstOutIndex;
	}
	
	public void resetQueue() {
		firstInIndex = 0;
		firstOutIndex = 0;
	}
}
