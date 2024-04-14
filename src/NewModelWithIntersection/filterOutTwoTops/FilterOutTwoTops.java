package NewModelWithIntersection.filterOutTwoTops;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class FilterOutTwoTops {

	
	
	public static final int NUM_NEIGHBOURS_PER_CELL =4;
	
	public static final int ABOVE = 0;
	public static final int BELOW = 2;
	
	public static boolean shouldFilterOutTwoTops(CoordWithRotationAndIndex[][] allNeighbours, long curState[]) {
	
		//System.out.println("---------------");
		boolean array[] = new boolean[allNeighbours.length];
		
		for(int i=0; i<array.length; i++) {
			array[i] = isCellIoccupied(i, curState);
		}
		
		int numTops = 0;
		for(int index=0; index<array.length; index++) {
			
			if(! array[index]) {
				
				boolean foundLine = false;
				for(int rotation = 0; rotation<2; rotation++) {
					
					int numAligned = 1;
					
					Coord2D start = new Coord2D(index, rotation);
					
					Coord2D nextIndex = start;

					//Go dir 1:
					do {
						nextIndex = tryAttachCellInDir(
								allNeighbours,
								nextIndex.i,
								nextIndex.j,
								ABOVE
							);
						
						
						if(! array[nextIndex.i]) {
							numAligned++;
						}
						
					} while(numAligned < 4 && ! array[nextIndex.i]);
					
					if(numAligned == 4) {
						//Good
						foundLine = true;
						break;
					}
					
					
					nextIndex = start;
					
					//Go dir 1:
					do {
						nextIndex = tryAttachCellInDir(
								allNeighbours,
								nextIndex.i,
								nextIndex.j,
								BELOW
							);
						
						if(! array[nextIndex.i]) {
							numAligned++;
						}
						
					} while(numAligned < 4 && ! array[nextIndex.i]);
					
					if(numAligned >= 4) {
						//Good
						foundLine = true;
						break;
					} else {
						// no luck
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
	
	
	public static final int NUM_BYTES_IN_LONG = 64;
	
	private static boolean isCellIoccupied(int i, long curState[]) {
		int indexArray = i / NUM_BYTES_IN_LONG;
		int bitShift = (NUM_BYTES_IN_LONG - 1) - i - indexArray * NUM_BYTES_IN_LONG;
		
		return ((1L << bitShift) & curState[indexArray]) != 0L;
	}
}
