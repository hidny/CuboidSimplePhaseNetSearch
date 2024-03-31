package NewModelWithIntersection.topAndBottomTransitionList;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class TopAndBottomTransitionList2 {
	
	public static int[] addBottomTransitionsBottom(
			int dimensions[],
			CoordWithRotationAndIndex neighbours[][],
			Coord2D firstIndexFromTopOrBottomInput,
			Coord2D firstIndexGoingToFirstOrLastRingInput,
			int indexToRing[],
			boolean put1x1OnOtherSide
	) {
		
		if(indexToRing[firstIndexFromTopOrBottomInput.i] != -1) {
			System.out.println("ERROR: indexToRing should be -1");
			System.exit(1);
		}

		if(indexToRing[firstIndexGoingToFirstOrLastRingInput.i] == -1) {
			System.out.println("ERROR: firstIndexGoingToFirstOrLastRingInput should not be -1");
			System.exit(1);
		}
		
		Coord2D firstIndexFromTopOrBottomToUse = firstIndexFromTopOrBottomInput;
		Coord2D firstIndexGoingToFirstOrLastRingToUse = firstIndexGoingToFirstOrLastRingInput;
		
		if(firstIndexGoingToFirstOrLastRingToUse.j == 0) {
			firstIndexFromTopOrBottomToUse = topLeftIndexRotAfter180Flip1x4layer(
					neighbours, 
					firstIndexFromTopOrBottomInput.i, 
					firstIndexFromTopOrBottomInput.j);
			
			firstIndexGoingToFirstOrLastRingToUse = topLeftIndexRotAfter180Flip1x4layer(
					neighbours, 
					firstIndexGoingToFirstOrLastRingInput.i, 
					firstIndexGoingToFirstOrLastRingInput.j);
		}

		Coord2D adjustedTopBottomCoord = null;
		
		if(put1x1OnOtherSide) {
			if(is1x1LeftOfCell2(neighbours, dimensions, firstIndexFromTopOrBottomToUse)) {
				
				//Adjust Coord to put 1x1 cell on the right
				adjustedTopBottomCoord = new Coord2D(firstIndexFromTopOrBottomToUse.i - 1, firstIndexFromTopOrBottomToUse.j);
	
			} else {
				
				//Adjust Coord to put 1x1 cell on the left:
				adjustedTopBottomCoord = new Coord2D(firstIndexFromTopOrBottomToUse.i + 1, firstIndexFromTopOrBottomToUse.j);
				
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
				index1x1Cell
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
			int index1x1Cell
	) {
		
		int ret[] = new int[getNumCells(dimensions)];
		
		Coord2D curIndexFromTopOrBottomInput =  firstIndexFromTopOrBottomInput;
		Coord2D curIndexFirstOrLastRing =  firstIndexGoingToFirstOrLastRingInput;
		
		do {
			ret[curIndexFromTopOrBottomInput.i] = curIndexFirstOrLastRing.i;
			
			ret[topLeftIndexRotAfter180Flip1x4layer(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j).i] =
					topLeftIndexRotAfter180Flip1x4layer(neighbours, curIndexFromTopOrBottomInput.i, curIndexFromTopOrBottomInput.j).i;
			
			for(int j=0; j<4; j++) {
				curIndexFirstOrLastRing = tryAttachCellInDir(neighbours, curIndexFirstOrLastRing.i, curIndexFirstOrLastRing.j, RIGHT);
			}
			
			curIndexFromTopOrBottomInput =  getNextTopBottomCoord(
					neighbours,
					curIndexFromTopOrBottomInput,
					indexToRing,
					index1x1Cell);
			
		} while(curIndexFromTopOrBottomInput.i != firstIndexFromTopOrBottomInput.i
				&& curIndexFirstOrLastRing.i != curIndexFirstOrLastRing.i
				&& curIndexFirstOrLastRing.j != curIndexFirstOrLastRing.j);
		
		if(index1x1Cell != curIndexFromTopOrBottomInput.i
				&& curIndexFromTopOrBottomInput.j != firstIndexFromTopOrBottomInput.j) {
			System.out.println("ERROR: unexpected rotation in: addBottomTransitionsBottomByGoingAround");
			System.exit(1);
		}
		
		return ret;
	}
	
	private static Coord2D getNextTopBottomCoord(
			CoordWithRotationAndIndex neighbours[][],
			Coord2D curIndexFromTopOrBottomInput,
			int indexToRing[],
			int index1x1Cell
		) {
		
		if(curIndexFromTopOrBottomInput.i == index1x1Cell) {

			Coord2D cellToRight = tryAttachCellInDir(neighbours, curIndexFromTopOrBottomInput.i, curIndexFromTopOrBottomInput.j, RIGHT);

			Coord2D ret = curIndexFromTopOrBottomInput;
			
			if(indexToRing[cellToRight.i] != -1) {
				
				for(int j=0; j<4; j++) {
					ret = tryAttachCellInDir(neighbours, ret.i, ret.j, RIGHT);
				}
			} else {
				int rotation = (curIndexFromTopOrBottomInput.j + 1) % NUM_ROTATIONS;
				if(rotation % 2 == 1) {
					rotation = (rotation + 1) % NUM_ROTATIONS;
				}
				ret = new Coord2D(curIndexFromTopOrBottomInput.i, rotation);
				
				ret = tryAttachCellInDir(neighbours, ret.i, ret.j, RIGHT);
				
			}
			
			if(indexToRing[ret.i] != -1) {
				System.out.println("ERROR: I messed up getNextTopBottomIndex!");
				System.exit(1);
			}
			
			return ret;
			
		} else {
			
			Coord2D cellToRight = tryAttachCellInDir(neighbours, curIndexFromTopOrBottomInput.i, curIndexFromTopOrBottomInput.j, RIGHT);

			Coord2D ret = curIndexFromTopOrBottomInput;
			
			if(indexToRing[cellToRight.i] != -1) {
				
				ret = topLeftIndexRotAfter180Flip1x4layer(neighbours, ret.i, ret.j);
				return ret;
			}
			
			for(int j=0; j<4; j++) {
				ret = tryAttachCellInDir(neighbours, ret.i, ret.j, RIGHT);
			}
			
			if(ret.i != index1x1Cell) {
				return ret;
			} else {
				
				//Note the the rotation of the 1x1 cell just needs to be convinient for the algo,
				// it doesn't need to be 'right'.
				ret = new Coord2D(ret.i, (ret.j + 2) % NUM_ROTATIONS);
			
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
