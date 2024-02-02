package GetTransitionMatrices2;

public class LayerState {

	boolean cellTable[];
	
	boolean connections[][];
	
	boolean hasHoleTooBig;
	
	public LayerState() {
		
	}
	
	//Leave connections blank until you put it on another one.
	public LayerState(int perimeter, long horiNumber) {
		
		cellTable = new boolean[perimeter * perimeter];
		
		cellTable[0] = true;
		
		boolean valid = true;
		
		int quotientP = 0;
		for(int indexModP = 1; indexModP<perimeter; indexModP++) {
			
			long remainder3 = horiNumber % 3;
			horiNumber /= 3;
			
			if(remainder3 == 0) {
				//pass
				
			} else if(remainder3 == 1) {
				quotientP++;
				
			} else if(remainder3 == 2){
				quotientP--;
				
			}
			
			if(quotientP < 0) {
				valid = false;
				break;
			}
			
			cellTable[quotientP * perimeter + indexModP]  = true;
		}
		
		//TODO: LATER:
		//TODO: try ruling out states where there's a hole bigger than perimeter
		this.hasHoleTooBig = hasHoleTooBig(cellTable, perimeter);
				
		// Layer: Try ruling out LayerStates where connections is connecting too much from bottom or
		// too much from top.
		
		if(valid == false) {
			this.cellTable = null;
			this.connections = null;
		} else {
		
			int numElements = getNumberOfIslands(cellTable);
		
			connections = new boolean[numElements][numElements];
		}
		
	}
	
	public LayerState hardCopy() {
		LayerState copy = new LayerState();
		
		copy.cellTable = new boolean[this.cellTable.length];
		copy.connections = new boolean[this.connections.length][this.connections[0].length];
		
		for(int i=0; i<copy.cellTable.length; i++) {
			copy.cellTable[i] = this.cellTable[i];
		}

		for(int i=0; i<copy.connections.length; i++) {
			for(int j=0; j<copy.connections[0].length; j++) {
				copy.connections[i][j] = this.connections[i][j];
			}
		}
		copy.hasHoleTooBig = this.hasHoleTooBig;
		
		return copy;
	}

	public int getNumberOfIslands(boolean array[]) {
		return getNumberOfIslandsFromTable(this.cellTable);
	}

	public static int getNumberOfIslandsFromTable(boolean array[]) {
		
		int numIslands = 0;
		
		boolean curOnIsland = false;
		
		for(int i=0; i<array.length; i++) {

			if(! curOnIsland && array[i]) {
				numIslands++;
				curOnIsland = true;
				
			} else if(array[i] == false) {
				curOnIsland = false;
			}
		}
		
		return numIslands;
	}

	public static boolean hasHoleTooBig(boolean array[], int perimeter) {
		
		int curHoleSize = 0;
		int numFound = 0;

		for(int i=0; i<array.length; i++) {

			if(! array[i]) {
				curHoleSize++;

				if(curHoleSize >= perimeter) {
					return true;
				}
			
			} else {
				numFound++;
				curHoleSize = 0;
				
				if(numFound == perimeter) {
					break;
				}
			}
		}
		
		return false;
	}
	
	public String toString() {
		String ret = "";

		for(int i=0; i<cellTable.length; i++) {
			if(cellTable[i]) {
				ret += "#";
			} else {
				ret += "-";
			}
		}
		ret += "\n";
		if(this.hasHoleTooBig) {
			ret += "(Has hole that's too big)\n";
			
		}
		ret += "Connections:\n";
		boolean noConnectionsFound = true;
		for(int i=0; i<connections.length; i++) {
			for(int j=i+1; j<connections[0].length; j++) {
				if(connections[i][j]) {
					
					ret += i + " <--> " + j + "\n";
					noConnectionsFound = false;
					
				}
			}
		}
		
		
		if(noConnectionsFound) {
			ret += "(N/A)\n";
		}
		ret += "\n";
		
		return ret;
	}
	
	public boolean equals(LayerState other) {
		return this.toString().equals(other);
	}
	
	public static long getUpperBoundPossibleLayers(int perimeter) {
		return (long)Math.pow(3, perimeter - 1);
		
	}
	
	public boolean isValid() {
		return this.cellTable != null;
	}
	
	
	
	public static void main(String args[]) {
		
		int perimeter = 9;
		
		int numValid = 0;
		
		long numToCheck = getUpperBoundPossibleLayers(perimeter);
		
		for(int i=0; i<numToCheck; i++) {
			
			LayerState cur = new LayerState(perimeter, i);
			
			if(cur.isValid()) {
				numValid++;
				System.out.println(cur);
			}
		}
		
		System.out.println("Number of valid layers: " + numValid);
	}
	
	//Patterns found on OEIS when you consider the 3^(p-1) solutions that go to the right of the initial cell:
	//https://oeis.org/A005773
	//https://oeis.org/A307789
	
	//(...)
	//2123
	//6046
	//17303
	//49721
	//143365
	//414584
	
	//I'm a bit shocked by this:
	//From Eric Rowland, Sep 25 2021: (Start)
	//There are a(4) = 13 directed animals of size 4:
	//  O
	//  O    O    O    OO              O         O
	//  O    O    OO   O    OO   O    OO   OOO   O    O    OO    O
	//  O    OO   O    O    OO   OOO  O    O    OO   OOO  OO   OOO  OOOO
	//(End)
	//In retrospect, it makes sense.
	
	//TODO: make function to see if you could put one layer on another
}
