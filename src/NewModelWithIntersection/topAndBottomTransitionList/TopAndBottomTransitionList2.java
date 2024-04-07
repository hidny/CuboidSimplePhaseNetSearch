package NewModelWithIntersection.topAndBottomTransitionList;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class TopAndBottomTransitionList2 {
	
	public static int[] addTransitionsTopBottom(
			int dimensions[],
			CoordWithRotationAndIndex neighbours[][],
			Coord2D currentIndexRotation,
			Coord2D nextIndexRotation,
			int indexToRing[],
			boolean put1x1OnOtherSide,
			int offsetRingTransitionOtherSide
	) {
		
		if(! (indexToRing[currentIndexRotation.i] == -1 ^ indexToRing[nextIndexRotation.i] == -1)) {
			System.out.println("ERROR: addBottomTransitionsTopBottom should have one index on top/bottom and one index not on top/bottom");
			System.exit(1);
		}
		
		Coord2D firstIndexFromTopOrBottomToUse = null;
		Coord2D firstIndexGoingToFirstOrLastRingToUse = null;
		if(indexToRing[currentIndexRotation.i] == -1) {
			firstIndexFromTopOrBottomToUse = currentIndexRotation;
			firstIndexGoingToFirstOrLastRingToUse = nextIndexRotation;
			
		} else if(indexToRing[nextIndexRotation.i] == -1){
			
			
			firstIndexFromTopOrBottomToUse = topLeftIndexRotAfter180Flip1x4layer(
					neighbours, 
					nextIndexRotation.i, 
					nextIndexRotation.j);
			
			firstIndexGoingToFirstOrLastRingToUse = topLeftIndexRotAfter180Flip1x4layer(
					neighbours, 
					currentIndexRotation.i, 
					currentIndexRotation.j);
			
			System.out.println("New: " + firstIndexFromTopOrBottomToUse.i + " to " + firstIndexGoingToFirstOrLastRingToUse.i);
			System.out.println("Rots should be 0: " + firstIndexFromTopOrBottomToUse.j + " to " + firstIndexGoingToFirstOrLastRingToUse.j);
			
		} else {
			System.out.println("ERROR: addBottomTransitionsTopBottom should have one index on top/bottom and one index not on top/bottom. (2)");
			System.exit(1);
		}
		
		Coord2D adjustedTopBottomCoord = null;
		
		if(put1x1OnOtherSide) {
			if(is1x1LeftOfCell2(neighbours, dimensions, firstIndexFromTopOrBottomToUse)) {
				
				//Adjust Coord to put 1x1 cell on the right
				adjustedTopBottomCoord = new Coord2D(firstIndexFromTopOrBottomToUse.i - 1, firstIndexFromTopOrBottomToUse.j);
				System.out.println("new first index: " + adjustedTopBottomCoord.i);
	
			} else {
				
				//Adjust Coord to put 1x1 cell on the left:
				adjustedTopBottomCoord = new Coord2D(firstIndexFromTopOrBottomToUse.i + 1, firstIndexFromTopOrBottomToUse.j);
				System.out.println("new first index: " + adjustedTopBottomCoord.i);
				
			}
			
		} else {
			
			adjustedTopBottomCoord = firstIndexFromTopOrBottomToUse;
		}
		
		int index1x1Cell = getIndex1x1Cell(
				neighbours,
				dimensions,
				adjustedTopBottomCoord
			);
		
		return addBottomTransitionsBottomByGoingAround(
				dimensions,
				neighbours,
				adjustedTopBottomCoord,
				firstIndexGoingToFirstOrLastRingToUse,
				indexToRing,
				index1x1Cell,
				offsetRingTransitionOtherSide
		);
		
	}
	
	private static int getIndex1x1Cell(
			CoordWithRotationAndIndex neighbours[][],
			int dimensions[],
			Coord2D firstIndexFromTopOrBottomInput
		) {
		
		if(is1x1LeftOfCell2(neighbours, dimensions, firstIndexFromTopOrBottomInput)) {
			return getIndex1x1CellIfLeft(dimensions, firstIndexFromTopOrBottomInput);
		} else {
			return getIndex1x1CellIfRight(dimensions, firstIndexFromTopOrBottomInput);
		}
	}
	

	private static boolean is1x1LeftOfCell2(
			CoordWithRotationAndIndex neighbours[][],
			int dimensions[],
			Coord2D firstIndexFromTopOrBottom) {
		
		int getIndex1x1CellIfLeft = getIndex1x1CellIfLeft(dimensions, firstIndexFromTopOrBottom);

		Coord2D tmp1 = firstIndexFromTopOrBottom;
		if(firstIndexFromTopOrBottom.j == 2) {
			//Flip it:
			tmp1 = new Coord2D(tmp1.i + 1, 0);
		}
		
		return (tmp1.i - getIndex1x1CellIfLeft) % 4 == 1;
		
	}
	
	private static int getIndex1x1CellIfLeft(int dimensions[], Coord2D firstIndexFromTopOrBottom) {
		if(isOnBottom(dimensions, firstIndexFromTopOrBottom)) {
			return getNumCells(dimensions) - dimensions[1];
		} else {
			return 0;
		}
	}
	
	private static int getIndex1x1CellIfRight(int dimensions[], Coord2D firstIndexFromTopOrBottom) {
		if(isOnBottom(dimensions, firstIndexFromTopOrBottom)) {
			return getNumCells(dimensions) - 1;
		} else {
			return dimensions[1] - 1;
		}
	}
	
	private static boolean isOnBottom(int dimensions[], Coord2D firstIndexFromTopOrBottom) {
		return dimensions[1] < firstIndexFromTopOrBottom.i;
	}
	
	private static int[] addBottomTransitionsBottomByGoingAround(
			int dimensions[],
			CoordWithRotationAndIndex neighbours[][],
			Coord2D firstIndexFromTopOrBottomInput,
			Coord2D firstIndexGoingToFirstOrLastRingInput,
			int indexToRing[],
			int index1x1Cell,
			int offsetRingTransitionOtherSide
	) {
		
		int ret[] = new int[getNumCells(dimensions)];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = -1;
		}
		
		Coord2D curIndexFromTopOrBottomInput =  firstIndexFromTopOrBottomInput;
		Coord2D curIndexFirstOrLastRing =  firstIndexGoingToFirstOrLastRingInput;
		
		if(curIndexFromTopOrBottomInput.j % 2 != 0) {
			curIndexFromTopOrBottomInput = new Coord2D(curIndexFromTopOrBottomInput.i, (curIndexFromTopOrBottomInput.j + 1) %4);
		}
		
		//Adjust the initial ring transition according to an offset:
		for(int i=0; i<Math.abs(offsetRingTransitionOtherSide); i++) {
			for(int j=0; j<4; j++) {
				if(offsetRingTransitionOtherSide > 0) {
					curIndexFirstOrLastRing = tryAttachCellInDir(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j, RIGHT);
				} else {
					curIndexFirstOrLastRing = tryAttachCellInDir(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j, LEFT);
				}
			}
		}

		
		do {
			ret[curIndexFromTopOrBottomInput.i] = curIndexFirstOrLastRing.i;
			System.out.println(curIndexFromTopOrBottomInput.i + " to " + curIndexFirstOrLastRing.i);
			
			if(curIndexFromTopOrBottomInput.i != index1x1Cell) {
				//1x4 on ring attaches to 1x4 on top/bottom side:
				
				ret[topLeftIndexRotAfter180Flip1x4layer(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j).i] =
						topLeftIndexRotAfter180Flip1x4layer(neighbours, curIndexFromTopOrBottomInput.i, curIndexFromTopOrBottomInput.j).i;
				
				System.out.println("other way:");
				System.out.println(topLeftIndexRotAfter180Flip1x4layer(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j).i
						+ " to " + topLeftIndexRotAfter180Flip1x4layer(neighbours, curIndexFromTopOrBottomInput.i, curIndexFromTopOrBottomInput.j).i);
				
				System.out.println("curIndexFirstOrLastRing.j = " + curIndexFirstOrLastRing.j);
				System.out.println("curIndexFromTopOrBottomInput.j = " + curIndexFromTopOrBottomInput.j);
				
				System.out.println("----");
				
			} else {
				//1x4 on ring attaches to 1x1:
				ret[topLeftIndexRotAfter180Flip1x4layer(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j).i] =
						curIndexFromTopOrBottomInput.i;
			}
			
			for(int j=0; j<4; j++) {
				curIndexFirstOrLastRing = tryAttachCellInDir(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j, RIGHT);
			}
			
			curIndexFromTopOrBottomInput =  getNextTopBottomCoord(
					neighbours,
					curIndexFromTopOrBottomInput,
					indexToRing,
					index1x1Cell);
			
		} while(! (curIndexFromTopOrBottomInput.i == firstIndexFromTopOrBottomInput.i
				&& curIndexFirstOrLastRing.i == curIndexFirstOrLastRing.i
				&& curIndexFirstOrLastRing.j == curIndexFirstOrLastRing.j)
				);
		
		
		return ret;
	}
	
	private static Coord2D getNextTopBottomCoord(
			CoordWithRotationAndIndex neighbours[][],
			Coord2D curIndexFromTopOrBottomInput,
			int indexToRing[],
			int index1x1Cell
		) {
		

		Coord2D ret = curIndexFromTopOrBottomInput;
		
		
		if(curIndexFromTopOrBottomInput.i == index1x1Cell) {
			
			Coord2D cellToRight = tryAttachCellInDir(neighbours, curIndexFromTopOrBottomInput.i, curIndexFromTopOrBottomInput.j, RIGHT);
			
			if(indexToRing[cellToRight.i] != -1) {
				int rotation = (curIndexFromTopOrBottomInput.j + 1) % NUM_ROTATIONS;
				if(rotation % 2 == 1) {
					rotation = (rotation + 1) % NUM_ROTATIONS;
				}
				ret = new Coord2D(curIndexFromTopOrBottomInput.i, rotation);
				
			}
			
			ret = tryAttachCellInDir(neighbours, ret.i, ret.j, RIGHT);
		
			
			if(indexToRing[ret.i] != -1) {
				System.out.println("ERROR: I messed up getNextTopBottomIndex! (" + ret.i + ")");
				System.exit(1);
			}
			
			return ret;
			
		} else {
			
			Coord2D cellToRight = curIndexFromTopOrBottomInput;
			boolean outOfBounds = false;
			for(int j=0; j<4; j++) {
				cellToRight = tryAttachCellInDir(neighbours, cellToRight.i, cellToRight.j, RIGHT);
				if(indexToRing[cellToRight.i] != -1) {
					outOfBounds = true;
				}
			}
			
			
			
			if(outOfBounds) {
				
				ret = topLeftIndexRotAfter180Flip1x4layer(neighbours, ret.i, ret.j);
				return ret;
			} else {
			
				for(int j=0; j<4; j++) {
					ret = tryAttachCellInDir(neighbours, ret.i, ret.j, RIGHT);
				}
				
				return ret;
			}
			
		}
		
	}
	
	//Should be in helper classes:
	private static int getNumCells(int dimensions[]) {
		return 2*(dimensions[0] * dimensions[1] + dimensions[0] * dimensions[2] + dimensions[1] * dimensions[2]);
	}
	
	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	private static final int NUM_NEIGHBOURS = 4;
	private static final int NUM_ROTATIONS = 4;
	
	private static Coord2D tryAttachCellInDir(CoordWithRotationAndIndex allNeighbours[][], int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = allNeighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	

	private static Coord2D topLeftIndexRotAfter180Flip1x4layer(CoordWithRotationAndIndex neighbours[][], int index, int rotation) {
		Coord2D flippedIndexAndRotation = new Coord2D(index, rotation);
		
		for(int j=0; j<4 - 1; j++) {
			flippedIndexAndRotation = tryAttachCellInDir(neighbours, flippedIndexAndRotation.i, flippedIndexAndRotation.j, RIGHT);
		}
		
		int flipRotation = (flippedIndexAndRotation.j + NUM_ROTATIONS/2) % NUM_ROTATIONS;
		
		return new Coord2D(flippedIndexAndRotation.i, flipRotation);
	}
	
}
