package semiGrained.iteration1;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.DataModelViews;
import Model.Utils;

public class SetupAllowed1stAndLastRing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private CoordWithRotationAndIndex neighbours[][];
	private int indexToRing[];
	
	int topLeftMostShiftIndex[];
	int topRightMostShiftIndex[];
	
	int bottomLeftMostShiftIndex[];
	int bottomRightMostShiftIndex[];
	
	int topBottomShiftIndexLeftMost[][];
	int dimensions[];
	
	public SetupAllowed1stAndLastRing(
			CoordWithRotationAndIndex neighbours[][],
			int indexToRing[],
			int dimensions[],
			int topBottomShiftIndexLeftMost[][],
			//Passing CuboidToFoldOnSemiGrained object is not ideal, but whatever. 
			CuboidToFoldOnSemiGrained sg
		) {
		
		this.neighbours = neighbours;
		this.indexToRing = indexToRing;
		this.dimensions = dimensions;

		this.topBottomShiftIndexLeftMost = topBottomShiftIndexLeftMost;


		this.topLeftMostShiftIndex = new int[3];
		this.topRightMostShiftIndex = new int[3];
		int curTopi = 0;
		
		this.bottomLeftMostShiftIndex = new int[3];
		this.bottomRightMostShiftIndex = new int[3];
		int curTopj = 0;
		
		for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
			for(int rotation=0; rotation<4; rotation++) {
				
				int indexType = sg.getIndexRotToTopBottomShiftLocation(index, rotation);
				
				if(indexType == CuboidToFoldOnSemiGrained.LEFT_TOP_LOCATION) {
					topLeftMostShiftIndex[curTopi] = index;
					curTopi++;
					
				} else if(indexType == CuboidToFoldOnSemiGrained.LEFT_BOTTOM_LOCATION) {
					bottomLeftMostShiftIndex[curTopj] = index;
					curTopj++;
					
				}
			}
		}
		
		for(int i=0; i<3; i++) {
			System.out.println(topLeftMostShiftIndex[i]);
			System.out.println(bottomLeftMostShiftIndex[i]);
			System.out.println();
		}
		
	}
	
	
	public int getNumCellsToFill() {
		return neighbours.length;
	}
	
	public static int NUM_ROTATIONS = 4;
	public static int NUM_NEIGHBOURS = NUM_ROTATIONS;
	

	
	// Top/Bottom shift type, index, rotation
	public boolean allowedFirstRingIndexRotations1x1Counter[][][];
	public boolean allowedLastRingIndexRotations1x1Counter[][][];
	
	public boolean allowedFirstRingIndexRotations1x1Clock[][][];
	public boolean allowedLastRingIndexRotations1x1Clock[][][];
	
	
	
	public void setupAllowedFirstRingIndexRotations1x1() {
		
		allowedFirstRingIndexRotations1x1Counter = new boolean[(int)Math.pow(2, 3)][this.getNumCellsToFill()][NUM_ROTATIONS];
		allowedFirstRingIndexRotations1x1Clock = new boolean[(int)Math.pow(2, 3)][this.getNumCellsToFill()][NUM_ROTATIONS];
		
		for(int i=0; i<allowedFirstRingIndexRotations1x1Counter.length; i++) {
			for(int j=0; j<allowedFirstRingIndexRotations1x1Counter[0].length; j++) {
				for(int k=0; k<allowedFirstRingIndexRotations1x1Counter[0][0].length; k++) {
					allowedFirstRingIndexRotations1x1Counter[i][j][k] = true;
					allowedFirstRingIndexRotations1x1Clock[i][j][k] = true;
				}
			}
		}
		
		for(int i=0; i<allowedFirstRingIndexRotations1x1Counter.length; i++) {
			for(int j=0; j<allowedFirstRingIndexRotations1x1Counter[0].length; j++) {
				for(int k=0; k<allowedFirstRingIndexRotations1x1Counter[0][0].length; k++) {
					
					if(indexToRing[j] == 0 && k%2 == 0) {
						allowedFirstRingIndexRotations1x1Counter[i][j][k] = false;
						allowedFirstRingIndexRotations1x1Clock[i][j][k] = false;
					}
				}
			}
		}
		//TODO 1: all 0
		
		Coord2D beforeCounter = new Coord2D(topLeftMostShiftIndex[0], 0);
		
		int index_type = 0;
		boolean isTop = true;

		//TODO: generalize for all types.
		//TODO: if # above is 0 mod 4, there's no 1x1 above

		//TODO: if # below is 0 mod 4, there's no 1x1 above
		
		
		Coord2D cur = tryAttachCellInDir(beforeCounter.i, beforeCounter.j, LEFT);
		while( ! hitBarrier(index_type, cur, isTop)) {
			
			//TODO: function to check if 1x4 doesn't hit barrier!
			allowedFirstRingIndexRotations1x1Clock[index_type][cur.i][2] = true;
			Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, 2);
			
			allowedFirstRingIndexRotations1x1Clock[index_type][flippedCoord.i][flippedCoord.j] = true;
			

			boolean hitBarrier = false;
			for(int j=0; j<4; j++) {
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				if(hitBarrier(index_type, cur, isTop)) {
					hitBarrier = true;
				}
			}
			if(hitBarrier) {
				break;
			}
			System.out.println(cur.i);
		}
		

		cur = tryAttachCellInDir(beforeCounter.i, beforeCounter.j, LEFT);
		cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
		
		while( ! hitBarrier(index_type, cur, isTop)) {
			allowedFirstRingIndexRotations1x1Counter[index_type][cur.i][2] = true;
			Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, 2);
			
			allowedFirstRingIndexRotations1x1Counter[index_type][flippedCoord.i][flippedCoord.j] = true;
			
			boolean hitBarrier = false;
			for(int j=0; j<4; j++) {
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				if(hitBarrier(index_type, cur, isTop)) {
					hitBarrier = true;
				}
			}
			if(hitBarrier) {
				break;
			}
			System.out.println("2: " +  cur.i);
		}
		
		
		System.out.println("TODO");
		
		System.out.println("Clock 0:");
		labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Clock[0], 0);

		System.out.println("Clock 0: rotation 2");
		labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Clock[0], 2);
		System.out.println("Counter 0:");
		labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Counter[0], 0);
		
		System.out.println("Counter 0: rotation 2:");
		labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Counter[0], 2);
		System.exit(1);
		//
	}

	public boolean hitBarrier(int index_type, Coord2D coord, boolean top) {
		return hitBarrier(index_type, coord.i, top);
	}
	
	public boolean hitBarrier(int index_type, int index, boolean top) {
		
		//TODO
		if(top) {
			for(int i=0; i<topLeftMostShiftIndex.length; i++) {
				if(index == topLeftMostShiftIndex[i] && ((~index_type) & (1 << i)) != 0) {
					return true;
				}
			}
			
			//TODO: define topRightMostShiftIndex
			for(int i=0; i<topRightMostShiftIndex.length; i++) {
				if(index == topRightMostShiftIndex[i] && (index_type & (1 << i)) != 0) {
					return true;
				}
			}
		} else {
			
			for(int i=0; i<bottomLeftMostShiftIndex.length; i++) {
				if(index == bottomLeftMostShiftIndex[i] && ((~index_type) & (1 << i)) != 0) {
					return true;
				}
			}

			//TODO: define bottomRightMostShiftIndex
			for(int i=0; i<bottomRightMostShiftIndex.length; i++) {
				if(index == bottomRightMostShiftIndex[i] && (index_type & (1 << i)) != 0) {
					return true;
				}
			}
		}
		return false;
	}
	

	public void setupAllowedFirstLastIndexRotations1x1() {
		//TODO
		allowedLastRingIndexRotations1x1Counter = new boolean[(int)Math.pow(2, 3)][this.getNumCellsToFill()][NUM_ROTATIONS];
		allowedLastRingIndexRotations1x1Clock = new boolean[(int)Math.pow(2, 3)][this.getNumCellsToFill()][NUM_ROTATIONS];
		
		for(int i=0; i<allowedLastRingIndexRotations1x1Counter.length; i++) {
			for(int j=0; j<allowedLastRingIndexRotations1x1Counter[0].length; j++) {
				for(int k=0; k<allowedLastRingIndexRotations1x1Counter[0][0].length; k++) {
					allowedLastRingIndexRotations1x1Counter[i][j][k] = true;
					allowedLastRingIndexRotations1x1Clock[i][j][k] = true;
				}
			}
		}
	}
	
	
	private Coord2D topLeftIndexRotAfter180Flip1x4layer(int index, int rotation) {
		Coord2D flippedIndexAndRotation = new Coord2D(index, rotation);
		
		for(int j=0; j<4 - 1; j++) {
			flippedIndexAndRotation = tryAttachCellInDir(flippedIndexAndRotation.i, flippedIndexAndRotation.j, RIGHT);
		}
		
		int flipRotation = (flippedIndexAndRotation.j + NUM_ROTATIONS/2) % NUM_ROTATIONS;
		
		return new Coord2D(flippedIndexAndRotation.i, flipRotation);
	}
	
	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.neighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	
	
	
	
	private void labelDebugIfTrueAllowedRingIndex(boolean indexRot[][], int rotation) {
		
		
		String labels[] = new String[getNumCellsToFill()];
		for(int i=0; i<labels.length; i++) {
			String labelSoFar = "11";
			
			
			if(indexRot[i][rotation] == false) {
				labelSoFar = "00";
			}
			
			labels[i] = labelSoFar;
			
			
		}
		
		System.out.println(DataModelViews.getFlatNumberingView(this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				labels));
		
	}
	
	

	public boolean areTopShiftIndexesAllSet(CuboidToFoldOnSemiGrained sg) {
		for(int i=0; i<topLeftMostShiftIndex.length; i++) {
			if(sg.topBottomShiftSetDepth[topLeftMostShiftIndex[i]] > sg.currentLayerIndex || sg.topBottomShiftSetDepth[topLeftMostShiftIndex[i]] < 0) {
				return false;
			}
		}
		
		return true;
	}

	public boolean areBottomShiftIndexesAllSet(CuboidToFoldOnSemiGrained sg) {
		for(int i=0; i<topLeftMostShiftIndex.length; i++) {
			if(sg.topBottomShiftSetDepth[bottomLeftMostShiftIndex[i]] > sg.currentLayerIndex || sg.topBottomShiftSetDepth[bottomLeftMostShiftIndex[i]] < 0) {
				return false;
			}
		}
		
		return true;
	}
	

	public int getTopShiftType(int topBottomShiftMod4FromPrevRound[]) {
		
		int ret = 0;
		for(int i=0; i<topLeftMostShiftIndex.length; i++) {
			if(topBottomShiftMod4FromPrevRound[topLeftMostShiftIndex[i]] == 0) {
				ret = 2 * ret;
			} else {
				ret = 2*ret + 1;
			}
		}
		return ret;
	}

	public int getBottomShiftType(int topBottomShiftMod4FromPrevRound[]) {
		
		int ret = 0;
		for(int i=0; i<bottomLeftMostShiftIndex.length; i++) {
			if(topBottomShiftMod4FromPrevRound[bottomLeftMostShiftIndex[i]] == 0) {
				ret = 2 * ret;
			} else {
				ret = 2*ret + 1;
			}
		}
		return ret;
	}
	
}
