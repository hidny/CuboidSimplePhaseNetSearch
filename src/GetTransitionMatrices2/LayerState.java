package GetTransitionMatrices2;

public class LayerState {

	boolean cellTable[];
	
	boolean connections[][];
	
	boolean hasHoleTooBig;
	
	public LayerState() {
		
	}
	
	public static final int BUFFER = 5;
	//Leave connections blank until you put it on another one.
	public LayerState(int perimeter, long horiNumber) {
		
		cellTable = new boolean[perimeter * perimeter + BUFFER];
		
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
		
			int numElements = this.getNumberOfIslands();
		
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

	public int getNumberOfIslands() {
		return getNumberOfIslandsFromTable(this.cellTable);
	}
	
	public int getIslandsIndex(int index) {
		
		int islandIndex = -1;
		
		boolean curOnIsland = false;
		
		for(int i=0; i<= index; i++) {

			if(! curOnIsland && this.cellTable[i]) {
				
				islandIndex++;
				curOnIsland = true;
				
			} else if(this.cellTable[i] == false) {
				curOnIsland = false;
			}
		}
		
		return islandIndex;
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
		return this.toString().equals(other.toString());
	}
	
	public static long getUpperBoundPossibleLayers(int perimeter) {
		return (long)Math.pow(3, perimeter - 1);
		
	}
	
	public boolean isValid() {
		return this.cellTable != null;
	}
	
	
	
	public static void main(String args[]) {
		
		int perimeter = 4;
		
		int numValid = 0;
		
		long numToCheck = getUpperBoundPossibleLayers(perimeter);
		
		//addLayerStateOnTopOfLayerState(LayerState bottom, LayerState top, int displacementX)
		
		for(int i=0; i<numToCheck; i++) {
			
			LayerState cur = new LayerState(perimeter, i);
			
			if(cur.isValid()) {
				numValid++;
				System.out.println(cur);
			}
		}
		
		System.out.println("Number of valid layers: " + numValid);
		
		LayerState layer0 = new LayerState(perimeter, 0);
		System.out.println(layer0);
		

		for(int i=0; i<numToCheck; i++) {
			
			LayerState cur = new LayerState(perimeter, i);
			
			if(cur.isValid()) {
				
				LayerState layerAbove = addLayerStateOnTopOfLayerState(layer0, cur, 0);
				
				if(layerAbove != null) {
					System.out.println(layerAbove);
				}
			}
		}
		
	}
	
	
	//TODO
	//pre: bottom is connected
	//post: if we could add layer on top of layer, return top layer with connection info
	// otherwise: return null.
	
	//TODO: make function to see if you could put one layer on another
	public static LayerState addLayerStateOnTopOfLayerState(LayerState bottom, LayerState top, int displacementX) {

		int numIslandsBottom = bottom.getNumberOfIslands();
		int numIslandsTop = top.getNumberOfIslands();
		
		boolean touchingBottomToTop[][] = new boolean[numIslandsBottom][numIslandsTop];
		
		for(int i=0; i<touchingBottomToTop.length; i++) {
			for(int j=0; j<touchingBottomToTop[0].length; j++) {
				touchingBottomToTop[i][j] = false;
			}
		}
		
		
		for(int i=0; i<bottom.cellTable.length; i++) {
			
			int jCoord = i + displacementX;

			
			if(jCoord >=0 && jCoord < bottom.cellTable.length) {

				int bottomIslandIndex = bottom.getIslandsIndex(i);
				int topIslandIndex = top.getIslandsIndex(jCoord);
				
				if(bottom.cellTable[i] && top.cellTable[jCoord]) {
					
					touchingBottomToTop[bottomIslandIndex][topIslandIndex] = true;
				}
			}
		}
		
		//Step 1:
		//Figure out if nothing in bottom is abandoned.
		
		//Get islands that touch bottom to top:
		boolean touchingDirectly[] = new boolean[touchingBottomToTop.length];
		for(int i=0; i<touchingBottomToTop.length; i++) {
			
			//Check for a direct touch
			boolean bottomIslandTouchingDirectly = false;
			for(int j=0; j<touchingBottomToTop[i].length; j++) {
				if(touchingBottomToTop[i][j]) {
					bottomIslandTouchingDirectly = true;
					break;
				}
			}
			
			touchingDirectly[i] = bottomIslandTouchingDirectly;
			
		}
		
		//Make sure bottom is touching top indirectly:
		
		boolean stillValid = true;
		for(int i=0; i<touchingBottomToTop.length; i++) {
			
			if(touchingDirectly[i] == false) {
				
				boolean currentIslandNoGood = true;
				
				for(int j=0; j<bottom.connections.length; j++) {
					
					if(bottom.connections[i][j] && touchingDirectly[j]) {
						currentIslandNoGood = false;
						break;
					}
				}
				
				if(currentIslandNoGood) {
					stillValid = false;
					break;
				}
			}
		}
		
		if(stillValid == false) {
			return null;
		}
		
		
		//Step 2:
		//Figure out the connections for top.
		LayerState topRet = top.hardCopy();
		
		topRet.connections = new boolean[numIslandsTop][numIslandsTop];
		for(int i=0; i<topRet.connections.length; i++) {
			for(int j=0; j<topRet.connections[0].length; j++) {
				topRet.connections[i][j] = false;
			}
		}
		
		for(int i=0; i<numIslandsTop; i++) {
			for(int i2=0; i2<numIslandsTop; i2++) {
				for(int j=0; j<numIslandsBottom; j++) {
					for(int j2=0; j2<numIslandsBottom; j2++) {
					
						
						if(i == i2
							||
								(j == j2
								&& touchingBottomToTop[j][i] && touchingBottomToTop[j][i2])
							||
							(touchingBottomToTop[j][i] && touchingBottomToTop[j2][i2]
									&& bottom.connections[j][j2])) {
							
							topRet.connections[i][i2] = true;
							topRet.connections[i2][i] = true;
							
						}
					}
				}
			}
		}
		
		
		return topRet;
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
	//After thinking about it, I still don't understand it. 
}
