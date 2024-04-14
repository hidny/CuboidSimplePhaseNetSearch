package NewModelWithIntersection.filterOutTwoTops;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class FilterOutTwoTopsFaster {

	//private long multiplesForIndexUpDown[][];

	//private long multiplesForIndexRightLeft[][];
	
	private int listGoingUpDownByIndex[][];
	private int listGoingRightLeftByIndex[][];
	
	public FilterOutTwoTopsFaster(CoordWithRotationAndIndex[][] allNeighbours) {
		
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
	
	public boolean shouldFilterOutTwoTops(CoordWithRotationAndIndex[][] allNeighbours, long curState[]) {
	
		//System.out.println("---------------");
		boolean array[] = new boolean[allNeighbours.length];
		
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
