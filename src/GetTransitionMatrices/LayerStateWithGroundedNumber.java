package GetTransitionMatrices;

public class LayerStateWithGroundedNumber {

	private boolean array[];
	private int groundNumber;
	
	public LayerStateWithGroundedNumber(boolean array[], int groundNumber) {
		
		this.array = array;
		this.groundNumber = groundNumber;
		
	}

	public int getGroundNumber() {
		return groundNumber;
	}

	public boolean isIndexOccupied(int index) {
		return this.array[index];
	}
	
	public int getLengthArray() {
		return this.array.length;
	}

	public static boolean isCellGroundedDirectly(LayerStateWithGroundedNumber layerState, int index) {
		
		if(! layerState.array[index]) {
			return false;
		}
		
		int nthIsland = getNthIslandOnIndexI(layerState, index);
		
		int power2ToUse = nthIsland + 1;
		
		
		return (layerState.groundNumber % (int)(Math.pow(2, power2ToUse))) / ((int)(Math.pow(2, power2ToUse - 1))) == 1;
	}
	
	public static int getNumberOfIslands(LayerStateWithGroundedNumber layerState) {
		return getNumberOfIslands(layerState.array);
	}

	public static int getNumberOfIslands(boolean array[]) {
		for(int i = array.length - 1; i>=0; i--) {
			if(array[i]) {
				return getNthIslandOnIndexI(array, i) + 1;
			}
		}
		
		//At this point, the array is all false.
		System.out.println("ERROR: Calling getNumberOfIslands with a boolean array that's completely empty.");
		return 0;
	}
	
	public static int getNthIslandOnIndexI(LayerStateWithGroundedNumber layerState, int index) {
		return getNthIslandOnIndexI(layerState.array, index);
	}
	
	public static int getNthIslandOnIndexI(boolean array[], int index) {
		
		if(! array[index]) {
			System.out.println("ERROR: called getNthIsland on a false index");
			System.exit(1);
		}
		
		int numIslandsFound = 0;
		boolean currentlyInIsland = false;
		
		for(int i=0; i<=index; i++) {
			
			if(array[i] && ! currentlyInIsland) {
				numIslandsFound++;
				currentlyInIsland = true;
				
			} else if( ! array[i] && currentlyInIsland) {
				currentlyInIsland = false;
			}
		}
		
		if(currentlyInIsland) {
			return numIslandsFound - 1;
		}
		
		if(! currentlyInIsland) {
			System.out.println("Oops!");
			System.exit(1);
		}
		
		return -1;
	}
	
	public String toString() {
		String ret = "";
		
		for(int i=0; i<array.length; i++) {
			if(array[i] == false) {
				ret+=".";
			} else {
				if(isCellGroundedDirectly(this, i)) {
					ret += "G";
				} else  {
					ret += "#";
				}
				
			}
		}
		
		
		return ret;
	}
	
	public static void main(String args[]) {
		
		boolean tmp[] = new boolean[] {true, false, true, false, true, true, false, false, true};
		
		LayerStateWithGroundedNumber layerState = new LayerStateWithGroundedNumber(tmp, 4);
		
		System.out.println("Start:");

		for(int i=0; i<tmp.length; i++) {
			
			if(tmp[i]) {
				
				if(isCellGroundedDirectly(layerState, i)) {
					System.out.println("True And grounded");
				} else {
					System.out.println("True and not grounded");
				}
				
			} else {
				System.out.println("false");
			}
		}
		
		System.out.println("Done");
		
		System.out.println();
		System.out.println("Number of islands: " + getNumberOfIslands(layerState));
		
		
	}
}
