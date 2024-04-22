package NewModelWithIntersection.filterOutTwoTops;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class FilterOutTwoTopsFaster4 {

	//private long multiplesForIndexUpDown[][];

	//private long multiplesForIndexRightLeft[][];
	
	private int listGoingUpDownByIndex[][];
	private int listGoingRightLeftByIndex[][];
	private int numCells;
	
	public FilterOutTwoTopsFaster4(CoordWithRotationAndIndex[][] allNeighbours) {
		
		this.numCells = allNeighbours.length;
		listGoingUpDownByIndex = new int[allNeighbours.length][];
		listGoingRightLeftByIndex = new int[allNeighbours.length][];
		
		
		for(int rotation = 0; rotation<2; rotation++) {
			for(int index = 0; index < allNeighbours.length; index++) {
				
				int indexes[] = new int[7];
				
				int middleIndexInList = 3;
				indexes[middleIndexInList] = index;
				
				Coord2D start = new Coord2D(index, rotation);
				
				Coord2D nextIndex = start;
				
				for(int j=middleIndexInList-1; j>=0; j--) {
					nextIndex = tryAttachCellInDir(
							allNeighbours,
							nextIndex.i,
							nextIndex.j,
							ABOVE
						);
					
					indexes[j] = nextIndex.i;
				}
				
				nextIndex = start;
				for(int j=middleIndexInList+1; j<indexes.length; j++) {
					nextIndex = tryAttachCellInDir(
							allNeighbours,
							nextIndex.i,
							nextIndex.j,
							BELOW
						);
					
					indexes[j] = nextIndex.i;
				}
				
				if(rotation == 0) {
					listGoingUpDownByIndex[index] = indexes;
				} else if(rotation == 1) {
					listGoingRightLeftByIndex[index] = indexes;
					
				}
				
				//TODO: later: maybe it a hash like in fastRegionCheck
				
			}
		}
		
	}
	
	public static final int NUM_NEIGHBOURS_PER_CELL =4;
	
	public static final int ABOVE = 0;
	public static final int BELOW = 2;
	
	public boolean shouldFilterOutTwoTops(long curState[]) {
	
		//System.out.println("---------------");
		boolean array[] = new boolean[numCells];
		
		for(int i=0; i<array.length; i++) {
			array[i] = isCellIoccupied(i, curState);
		}
		
		int numTops = 0;
		for(int index=0; index<array.length; index++) {
			
			if(! array[index]) {
				
				int listToUse[];
				boolean foundLine = false;
				
				for(int rotation = 0; rotation<2; rotation++) {
					
					if(rotation == 0) {
						listToUse = listGoingUpDownByIndex[index];
					} else {
						listToUse = listGoingRightLeftByIndex[index];
						
					}
					
					int numInARow = 0;
					
					for(int i=0; i<listToUse.length; i++) {
						
						if( ! array[listToUse[i]]) {
							numInARow++;

							if( numInARow == 4 ) {
								foundLine = true;
							}
							
							
						} else {
							numInARow = 0;
						}
					}
				}
				
				
				if(foundLine == false) {
					numTops++;
					//System.out.println("Index top: " + index);
					
					if(numTops > 1) {
						return true;
					}
				}
			}
		}
		
		
		return numTops > 1;
	}
	
	public boolean isPossibleAfterBasicDeduction(long curState[]) {
		
		if(shouldFilterOutTwoTops(curState)) {
			return false;
		}
		
		
		
		int topIndex = getForcedTopIndex(curState);
		
		if(topIndex == TOP_INDEX_UNKNOWN) {
		
			
			boolean array[] = new boolean[numCells];
			
			for(int i=0; i<array.length; i++) {
				array[i] = isCellIoccupied(i, curState);
			}
			
			for(int i=0; i<this.numCells; i++) {
				if(array[i] == false && isTopPossibleAfterBasicDeduction(curState, i)) {
					return true;
				}
			}
			return false;
			
		} else {
			return isTopPossibleAfterBasicDeduction(curState, topIndex);
		}
		
	}

	public static final int NO_WAY = 0;
	public static final int ONE_WAY = 1;
	public static final int MULT_WAYS = 2;
	
	public boolean isTopPossibleAfterBasicDeduction(long curState[], int topIndex) {
		//TODO
		//System.out.println("---------------");
		boolean array[] = new boolean[numCells];

		int possibilityLayerGoingUpDown[] = new int[this.numCells];
		int possibilityLayerGoingLeftRight[] = new int[this.numCells];
		
		for(int i=0; i<array.length; i++) {
			array[i] = isCellIoccupied(i, curState);
			
			if(array[i] || i == topIndex) {
				possibilityLayerGoingUpDown[i] = NO_WAY;
				possibilityLayerGoingLeftRight[i] = NO_WAY;
			} else {
				possibilityLayerGoingUpDown[i] = MULT_WAYS;
				possibilityLayerGoingLeftRight[i] = MULT_WAYS;
				
			}
		}
		array[topIndex] = true;
		
		boolean progress = true;
		
		while(progress == true) {
			
			progress = false;
			
			for(int index=0; index<array.length; index++) {


				
				for(int rotation = 0; rotation<2; rotation++) {

					if(array[index]) {
						continue;
					}

					int listToUse[];
					int possibilityListToUse[];
					int otherPossibilityList[];
					boolean foundLine = false;
					boolean isOnlyLine = false;
					
					if(rotation == 0) {
						listToUse = listGoingUpDownByIndex[index];
						possibilityListToUse = possibilityLayerGoingUpDown;
						otherPossibilityList = possibilityLayerGoingLeftRight;
					} else {
						listToUse = listGoingRightLeftByIndex[index];
						possibilityListToUse = possibilityLayerGoingLeftRight;
						otherPossibilityList = possibilityLayerGoingUpDown;
					}
					
					int numInARow = 0;
					
					for(int i=0; i<listToUse.length; i++) {
						
						if( possibilityListToUse[listToUse[i]] != NO_WAY) {
							numInARow++;

							if( numInARow == 4 ) {
								foundLine = true;
								isOnlyLine = true;
							} else if(numInARow > 4) {
								isOnlyLine = false;
							}
							
							
						} else {
							numInARow = 0;
						}
					}
					
					if(foundLine == false) {
						if(possibilityListToUse[index] != NO_WAY) {
							progress = true;
							
							possibilityListToUse[index] = NO_WAY;
							
							if(otherPossibilityList[index] == NO_WAY) {
								return false;
							}
						}
						
					} else if(foundLine && isOnlyLine) {
						if(possibilityListToUse[index] != ONE_WAY) {

							progress = true;
							if(possibilityListToUse[index] == NO_WAY) {
								System.out.println("ERROR: the number of ways got expanded through deduction (2). This should not be possible! (isTopPossibleAfterBasicDeduction)");
								System.exit(1);
							}
							
							possibilityListToUse[index] = ONE_WAY;
							
							if(otherPossibilityList[index] == NO_WAY) {
								
								boolean sanityCheckOnlyOneUpdate = false;
								numInARow = 0;
								
								for(int i=0; i<listToUse.length; i++) {
									
									if( possibilityListToUse[listToUse[i]] != NO_WAY) {
										numInARow++;
			
										if( numInARow == 4 ) {
											
											if(sanityCheckOnlyOneUpdate == true) {
												System.out.println("ERROR: sanityCheckOnlyRanOnce will run twice! (isTopPossibleAfterBasicDeduction)");
												System.exit(1);
											}
											sanityCheckOnlyOneUpdate = true;
											
											for(int j=0; j<4; j++) {
												
												
												int indexToFill = listToUse[i + (j - 3)];
												array[indexToFill] = true;
												possibilityListToUse[indexToFill] = NO_WAY;
												otherPossibilityList[indexToFill] = NO_WAY;
												
											}
										}
										
										
									} else {
										numInARow = 0;
									}
								}
								
								if(sanityCheckOnlyOneUpdate == false) {
									System.out.println("ERROR: sanityCheckOnlyRanOnce didn't run! (isTopPossibleAfterBasicDeduction)");
									System.exit(1);
								}
							}
							
						}
					} else if(foundLine && ! isOnlyLine) {
						if(possibilityListToUse[index] != MULT_WAYS) {
							System.out.println("ERROR: the number of ways got expanded through deduction. This should not be possible! (isTopPossibleAfterBasicDeduction)");
							System.out.println("index: " + index);
							System.exit(1);
						}
						
					} else {
						System.out.println("ERROR: unexpected state in isTopPossibleAfterBasicDeduction");
						System.exit(1);
					}
				}//END ROTATION LOOP
			}//END INDEX LOOP
			
			
			//TODO: check for split, and if split, make sure all regions are of size = 0 mod 4 
			
		}//END PROGRESS LOOP
		
		
		return true;
	}
	
	public static final int TOP_INDEX_UNKNOWN = -1;
	
	public int getForcedTopIndex(long curState[]) {
		
		//System.out.println("---------------");
		boolean array[] = new boolean[this.numCells];
		
		for(int i=0; i<array.length; i++) {
			array[i] = isCellIoccupied(i, curState);
		}
		
		for(int index=0; index<array.length; index++) {
			
			if(! array[index]) {
				
				int listToUse[];
				boolean foundLine = false;
				
				for(int rotation = 0; rotation<2; rotation++) {
					
					if(rotation == 0) {
						listToUse = listGoingUpDownByIndex[index];
					} else {
						listToUse = listGoingRightLeftByIndex[index];
						
					}
					
					int numInARow = 0;
					
					for(int i=0; i<listToUse.length; i++) {
						
						if( ! array[listToUse[i]]) {
							numInARow++;

							if( numInARow == 4 ) {
								foundLine = true;
							}
							
							
						} else {
							numInARow = 0;
						}
					}
				}
				
				
				if(foundLine == false) {
					return index;
				}
			}
		}
		
		
		return TOP_INDEX_UNKNOWN;
	}

	
	

	
	public static final int NUM_BYTES_IN_LONG = 64;
	
	private static boolean isCellIoccupied(int i, long curState[]) {
		int indexArray = i / NUM_BYTES_IN_LONG;
		int bitShift = (NUM_BYTES_IN_LONG - 1) - i - indexArray * NUM_BYTES_IN_LONG;
		
		return ((1L << bitShift) & curState[indexArray]) != 0L;
	}
	
	//Step only done during pre-processing:
	private static Coord2D tryAttachCellInDir(
			CoordWithRotationAndIndex[][] allNeighbours,
			int curIndex,
			int rotationRelativeFlatMap,
			int dir
		) {

		CoordWithRotationAndIndex neighbours[] = allNeighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS_PER_CELL;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS_PER_CELL) % NUM_NEIGHBOURS_PER_CELL;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	
}
