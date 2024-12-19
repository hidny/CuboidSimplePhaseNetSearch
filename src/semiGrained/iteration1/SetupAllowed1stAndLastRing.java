package semiGrained.iteration1;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.DataModelViews;
import Model.Utils;

public class SetupAllowed1stAndLastRing {

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
		int curTopLefti = 0;
		int curBottomLeftj = 0;
		
		this.bottomLeftMostShiftIndex = new int[3];
		this.bottomRightMostShiftIndex = new int[3];
		int curTopRighti = 0;
		int curBottomRightj = 0;
		//int cur
		
		int SIZE = 3;
		
		for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
			for(int rotation=0; rotation<4; rotation++) {
				
				int indexType = sg.getIndexRotToTopBottomShiftLocation(index, rotation);
				
				if(indexType == CuboidToFoldOnSemiGrained.LEFT_TOP_LOCATION) {
					topLeftMostShiftIndex[curTopLefti] = index;
					curTopLefti++;
					
				} else if(indexType == CuboidToFoldOnSemiGrained.LEFT_BOTTOM_LOCATION) {
					bottomLeftMostShiftIndex[SIZE - 1 - curBottomLeftj] = index;
					curBottomLeftj++;
					
				} else if(indexType == CuboidToFoldOnSemiGrained.RIGHT_TOP_LOCATION) {
					topRightMostShiftIndex[SIZE - 1 - curTopRighti] = index;
					curTopRighti++;
					
				} else if(indexType == CuboidToFoldOnSemiGrained.RIGHT_BOTTOM_LOCATION) {
					bottomRightMostShiftIndex[curBottomRightj] = index;
					curBottomRightj++;
					
				}
			}
		}
		
		for(int i=0; i<SIZE; i++) {
			System.out.println(topLeftMostShiftIndex[i]);
			System.out.println(bottomLeftMostShiftIndex[i]);
			System.out.println();
		}

		for(int i=0; i<SIZE; i++) {
			System.out.println(topRightMostShiftIndex[i]);
			System.out.println(bottomRightMostShiftIndex[i]);
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
		//TODO: do bottom later...
		boolean isTop = true;

		
		
		for(int index_type=0; index_type<Math.pow(2, 3); index_type++) {


			boolean aboveRing = true;
			

			Coord2D barrier1 = getBarrier1(index_type, isTop, aboveRing);
			
			Coord2D cur = tryAttachCellInDir(barrier1.i, barrier1.j, LEFT);
			while( ! hitBarrier(index_type, cur, isTop)) {
				
				//TODO: function to check if 1x4 doesn't hit barrier!
				allowedFirstRingIndexRotations1x1Clock[index_type][cur.i][2] = isLayer1x4Option(index_type, cur.i, 2, isTop);
				Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, 2);
				
				allowedFirstRingIndexRotations1x1Clock[index_type][flippedCoord.i][flippedCoord.j] = isLayer1x4Option(index_type, flippedCoord.i, flippedCoord.j, isTop);
				
	
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
			}
			
	
			if(getNumCellsBetweenBarrier(index_type, isTop, aboveRing) % 4 == 1) {
				//TODO: only activate if there's 1mod4 spaces available:
				cur = tryAttachCellInDir(barrier1.i, barrier1.j, LEFT);
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				
				while( ! hitBarrier(index_type, cur, isTop)) {
					
					
					allowedFirstRingIndexRotations1x1Counter[index_type][cur.i][2] = isLayer1x4Option(index_type, cur.i, 2, isTop);
					Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, 2);
					
					allowedFirstRingIndexRotations1x1Counter[index_type][flippedCoord.i][flippedCoord.j] = isLayer1x4Option(index_type, flippedCoord.i, flippedCoord.j, isTop);
					
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
			}
		

			//TODO: Try to remove copy/paste code.

			
			aboveRing = false;
			barrier1 = getBarrier1(index_type, isTop, aboveRing);
			
			cur = tryAttachCellInDir(barrier1.i, barrier1.j, LEFT);
			
			while( ! hitBarrier(index_type, cur, isTop)) {
				
				//TODO: function to check if 1x4 doesn't hit barrier!
				allowedFirstRingIndexRotations1x1Clock[index_type][cur.i][2] = isLayer1x4Option(index_type, cur.i, 2, isTop);
				Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, 2);
				
				allowedFirstRingIndexRotations1x1Clock[index_type][flippedCoord.i][flippedCoord.j] = isLayer1x4Option(index_type, flippedCoord.i, flippedCoord.j, isTop);
				
	
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
			
	
			if(getNumCellsBetweenBarrier(index_type, isTop, aboveRing) % 4 == 1) {
				//TODO: only activate if there's 1mod4 spaces available:
				cur = tryAttachCellInDir(barrier1.i, barrier1.j, LEFT);
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				
				while( ! hitBarrier(index_type, cur, isTop)) {
					
					
					allowedFirstRingIndexRotations1x1Counter[index_type][cur.i][2] = isLayer1x4Option(index_type, cur.i, 2, isTop);
					Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, 2);
					
					allowedFirstRingIndexRotations1x1Counter[index_type][flippedCoord.i][flippedCoord.j] = isLayer1x4Option(index_type, flippedCoord.i, flippedCoord.j, isTop);
					
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
				

				//END TODO: Try to remove copy/paste code.
			}
			
		
			//TODO: index_type 1 is wrong once you scroll right...
			//TODO: debug
			//TODO: clean code
		
			System.out.println("index_type: " + index_type);
			
			System.out.println("Clockwise 1x1 with rotation 0");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Clock[0], 0, index_type);
	
			System.out.println("Clockwise 1x1 with rotation 2");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Clock[0], 2, index_type);
			System.out.println("CounterClockwise 1x1 with rotation 0");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Counter[0], 0, index_type);
			
			System.out.println("Counter Clockwise 1x1 with rotation 2:");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Counter[0], 2, index_type);
		}
		
		System.exit(1);
		
	}

	public Coord2D getBarrier1(int indexType, boolean top, boolean aboveRing) {
		Coord2D barrier1 = new Coord2D(-1, -1);
		
		if(top) {
			if(aboveRing) {
	
				for(int i=0; i<topLeftMostShiftIndex.length; i++) {
					if(hitBarrier(indexType, topLeftMostShiftIndex[i],  top)) {
						barrier1 = new Coord2D(topLeftMostShiftIndex[i], 0);
						break;
					}
				}
				
				if(barrier1.i == -1) {
					
					for(int i=0; i<topRightMostShiftIndex.length; i++) {
						int newI = topRightMostShiftIndex.length - 1 - i;
						if(hitBarrier(indexType, topRightMostShiftIndex[newI],  top)) {
							barrier1 = new Coord2D(topRightMostShiftIndex[newI], 0);
							break;
						}
					}
				}
			} else {
				
				for(int i=0; i<topRightMostShiftIndex.length; i++) {
					int newI = topRightMostShiftIndex.length - 1 - i;
					if(hitBarrier(indexType, topLeftMostShiftIndex[i],  top)) {
						barrier1 = new Coord2D(topLeftMostShiftIndex[i], 0);
						break;
					}
				}
				
				
				if(barrier1.i == -1) {
					for(int i=0; i<3; i++) {
						if(hitBarrier(indexType, topRightMostShiftIndex[i],  top)) {
							barrier1 = new Coord2D(topRightMostShiftIndex[i], 0);
							break;
						}
					}
				}
			}
		} else {
			//TODO: handle bottom case.
			return new Coord2D(-2, -2);
		}
		
		return barrier1;
	}
	
	//TODO: test it!
	public int getNumCellsBetweenBarrier(int indexType, boolean top, boolean Ring0orLastabove) {
		if(indexType == 0 || indexType == 7) {
			int tmp = 2*this.dimensions[1] + 3;
			
			if(tmp % 4 != 1) {
				System.out.println("DOH in getNumCellsBetweenBarrier! " + this.dimensions[1]);
				System.exit(1);
			}
			return tmp;
		}

		Coord2D barrier1 = new Coord2D(-1, -1);
		Coord2D barrier2 = new Coord2D(-1, -1);
		
		if(Ring0orLastabove) {

			for(int i=0; i<topLeftMostShiftIndex.length; i++) {
				if(hitBarrier(indexType, topLeftMostShiftIndex[i],  top)) {
					barrier1 = new Coord2D(topLeftMostShiftIndex[i], 0);
					break;
				}
			}
			
			for(int i=0; i<topRightMostShiftIndex.length; i++) {
				if(hitBarrier(indexType, topRightMostShiftIndex[i],  top)) {
					barrier2 = new Coord2D(topRightMostShiftIndex[i], 0);
					break;
				}
			}
			
			if(barrier1.i == -1 || barrier2.i == -1) {
				System.out.println("Doh in getNumCellsBetweenBarrier");
			}
		} else {
			
			for(int i=topLeftMostShiftIndex.length - 1; i>=0; i--) {
				if(hitBarrier(indexType, topLeftMostShiftIndex[i],  top)) {
					barrier1 = new Coord2D(topLeftMostShiftIndex[i], 0);
					break;
				}
			}
			
			
			for(int i=topRightMostShiftIndex.length - 1; i>=0; i--) {
				if(hitBarrier(indexType, topRightMostShiftIndex[i],  top)) {
					barrier2 = new Coord2D(topRightMostShiftIndex[i], 0);
					break;
				}
			}
			
			if(barrier1.i == -1 || barrier2.i == -1) {
				System.out.println("Doh in getNumCellsBetweenBarrier 2");
			}
		}
		
		int ret = -1;
		Coord2D cur = barrier1;
		do {
			cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			ret++;
		} while( ! hitBarrier(indexType, cur, top));
		
		
		return ret;
		
	}

	public boolean isLayer1x4Option(int indexType, int index, int rotation, boolean top) {
		
		return isLayer1x4Option(indexType, new Coord2D(index, rotation), top);
	}
	public boolean isLayer1x4Option(int indexType, Coord2D leftMostCoord, boolean top) {
		
		Coord2D curCoord = leftMostCoord;
		for(int i=0; i<4; i++) {
			if(hitBarrier(indexType, curCoord, top)) {
				return false;
			}
			
			curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, RIGHT);
		}
		
		return true;
	}
	
	public boolean hitBarrier(int index_type, Coord2D coord, boolean top) {
		return hitBarrier(index_type, coord.i, top);
	}
	
	public boolean hitBarrier(int index_type, int index, boolean top) {
		
		if(top) {
			for(int i=0; i<topLeftMostShiftIndex.length; i++) {
				if(index == topLeftMostShiftIndex[i] && ((~index_type) & (1 << i)) != 0) {
					return true;
				}
			}
			
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

			for(int i=0; i<bottomRightMostShiftIndex.length; i++) {
				if(index == bottomRightMostShiftIndex[i] && (index_type & (1 << i)) != 0) {
					return true;
				}
			}
		}
		return false;
	}
	

	public void setupAllowedFirstLastIndexRotations1x1() {
		//TODO: do first Ring first though....
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
	
	
	
	
	private void labelDebugIfTrueAllowedRingIndex(boolean indexRot[][], int rotation, int index_type) {
		
		
		String labels[] = new String[getNumCellsToFill()];
		for(int i=0; i<labels.length; i++) {
			String labelSoFar = "11";
			
			
			if(indexRot[i][rotation] == false) {
				labelSoFar = "00";
			}
			if(hitBarrier(index_type, i, true)) {
				labelSoFar = "XX";
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
