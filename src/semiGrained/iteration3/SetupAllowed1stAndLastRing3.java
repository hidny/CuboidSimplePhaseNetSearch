package semiGrained.iteration3;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.DataModelViews;
import Model.Utils;

public class SetupAllowed1stAndLastRing3 {

	private CoordWithRotationAndIndex neighbours[][];
	private int indexToRing[];
	
	int topLeftMostShiftIndex[];
	int topRightMostShiftIndex[];
	
	int bottomLeftMostShiftIndex[];
	int bottomRightMostShiftIndex[];
	
	int topBottomShiftIndexLeftMost[][];
	int dimensions[];
	
	public SetupAllowed1stAndLastRing3(
			CoordWithRotationAndIndex neighbours[][],
			int indexToRing[],
			int dimensions[],
			int topBottomShiftIndexLeftMost[][],
			//Passing CuboidToFoldOnSemiGrained object is not ideal, but whatever. 
			CuboidToFoldOnSemiGrained3 sg
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
				
				if(indexType == CuboidToFoldOnSemiGrained3.LEFT_TOP_LOCATION) {
					topLeftMostShiftIndex[curTopLefti] = index;
					curTopLefti++;
					
				} else if(indexType == CuboidToFoldOnSemiGrained3.LEFT_BOTTOM_LOCATION) {
					bottomLeftMostShiftIndex[SIZE - 1 - curBottomLeftj] = index;
					curBottomLeftj++;
					
				} else if(indexType == CuboidToFoldOnSemiGrained3.RIGHT_TOP_LOCATION) {
					topRightMostShiftIndex[SIZE - 1 - curTopRighti] = index;
					curTopRighti++;
					
				} else if(indexType == CuboidToFoldOnSemiGrained3.RIGHT_BOTTOM_LOCATION) {
					bottomRightMostShiftIndex[curBottomRightj] = index;
					curBottomRightj++;
					
				}
			}
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
	

	public boolean allowedFirstRingIndexRotations1x1Locations[][];
	
	public void setupAllowedFirstAndLastRingIndexRotations1x4(int bottom1x1Index) {
		
		allowedFirstRingIndexRotations1x1Counter = new boolean[(int)Math.pow(2, 3)][this.getNumCellsToFill()][NUM_ROTATIONS];
		allowedFirstRingIndexRotations1x1Clock = new boolean[(int)Math.pow(2, 3)][this.getNumCellsToFill()][NUM_ROTATIONS];
		
		allowedFirstRingIndexRotations1x1Locations = new boolean[(int)Math.pow(2, 3)][this.getNumCellsToFill()];
		
		for(int i=0; i<allowedFirstRingIndexRotations1x1Counter.length; i++) {
			for(int j=0; j<allowedFirstRingIndexRotations1x1Counter[0].length; j++) {
				for(int k=0; k<allowedFirstRingIndexRotations1x1Counter[0][0].length; k++) {
					if(indexToRing[j] == 0) {
						allowedFirstRingIndexRotations1x1Counter[i][j][k] = true;
						allowedFirstRingIndexRotations1x1Clock[i][j][k] = true;
					}
				}
			}
		}
		
		for(int i=0; i<allowedFirstRingIndexRotations1x1Counter.length; i++) {
			for(int j=0; j<allowedFirstRingIndexRotations1x1Counter[0].length; j++) {

				allowedFirstRingIndexRotations1x1Locations[i][j] = false;
				
				for(int k=0; k<allowedFirstRingIndexRotations1x1Counter[0][0].length; k++) {
					
					if(indexToRing[j] == 0 && k%2 == 0) {
						allowedFirstRingIndexRotations1x1Counter[i][j][k] = false;
						allowedFirstRingIndexRotations1x1Clock[i][j][k] = false;
					}
				}
			}
		}

		//TODO: do bottom later...
		//TODO: bottom should be a flag in this function
		
		boolean isTop = true;
		
		//TODO: 1x1 is wrong when upside down maybe?

		for(int index_type=0; index_type<Math.pow(2, 3); index_type++) {
			

			Coord2D Cell1x1BetweenBarriers = get1x1BetweenBarriersIfExists(index_type, isTop);
			
			if(Cell1x1BetweenBarriers.i >= 0) {
				allowedFirstRingIndexRotations1x1Locations[index_type][Cell1x1BetweenBarriers.i] = true;
			}
			
			for(int aboveRingFlag=0; aboveRingFlag<=1; aboveRingFlag++) {
				
				boolean aboveRing = (aboveRingFlag == 1);
				
				Coord2D barrier1 = getBarrier1(index_type, isTop, aboveRing);
				
				Coord2D cur = tryAttachCellInDir(barrier1.i, barrier1.j, RIGHT);
				
				while( ! hitLastorRing0Barrier(index_type, cur, isTop)) {

					
					boolean is1x4SpaceAvailable = isLayer1x4Option(index_type, cur, isTop);
					
					if(is1x4SpaceAvailable == false) {
						break;
					}
					
					allowedFirstRingIndexRotations1x1Counter[index_type][cur.i][cur.j] = true;
					Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, cur.j);
					
					allowedFirstRingIndexRotations1x1Counter[index_type][flippedCoord.i][flippedCoord.j] = true;
					
					for(int j=0; j<4; j++) {
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
					}
				}
				
				if(getNumCellsBetweenBarrier(index_type, isTop, aboveRing) % 4 == 1) {
					
					cur = tryAttachCellInDir(barrier1.i, barrier1.j, RIGHT);
					
					Coord2D possible1x1IndexRotation = new Coord2D(cur.i, (cur.j + 2) % NUM_ROTATIONS);
					cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
					
					while( ! hitLastorRing0Barrier(index_type, cur, isTop)) {
						
						boolean is1x4SpaceAvailable = isLayer1x4Option(index_type, cur.i, cur.j, isTop);

						addPossible1x1(index_type, possible1x1IndexRotation);
						
						//allowedFirstRingIndexRotations1x1Locations[index_type][possible1x1index.i] = true;
						
						if(is1x4SpaceAvailable == false) {
							break;
						}
						allowedFirstRingIndexRotations1x1Clock[index_type][cur.i][cur.j] = true;
						Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur.i, cur.j);
						
						allowedFirstRingIndexRotations1x1Clock[index_type][flippedCoord.i][flippedCoord.j] = true;
						
						
						for(int j=0; j<4; j++) {
							possible1x1IndexRotation = tryAttachCellInDir(possible1x1IndexRotation.i, possible1x1IndexRotation.j, LEFT);
							cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
						}
					}
					
					if( ! hitLastorRing0Barrier(index_type, possible1x1IndexRotation.i, isTop)) {
						addPossible1x1(index_type, possible1x1IndexRotation);
					} else {
						System.out.println("ERROR in setupAllowedFirstAndLastRingIndexRotations1x4: unexpected branching.");
						System.exit(1);
					}
				}
			
			}
			
			
			if(isTop) {
				adjustAllowed1stRingBasedOnBottom1x1Location(index_type, isTop, bottom1x1Index);
			}
			//TODO: debug
		
			/*System.out.println("index_type: " + index_type);
			
			System.out.println("1x1 at clockwise extreme with rotation 0");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Clock[index_type], 0, index_type);
	
			System.out.println("1x1 at clockwise extreme with rotation 2");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Clock[index_type], 2, index_type);
			System.out.println("1x1 at counterclockwise extreme with rotation 0");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Counter[index_type], 0, index_type);
			
			System.out.println("1x1 at counterclockwise extreme with rotation 2:");
			labelDebugIfTrueAllowedRingIndex(allowedFirstRingIndexRotations1x1Counter[index_type], 2, index_type);
			
			System.out.println("Debug possible 1x1 bottom locations:");
			labelDebugIfTrueAllowedBottom1x1Index(allowedFirstRingIndexRotations1x1Locations[index_type], index_type);
			*/
		}
		
		
	}
	
	private void adjustAllowed1stRingBasedOnBottom1x1Location(int index_type, boolean isTop, int bottom1x1Index) {
		
		
		if(allowedFirstRingIndexRotations1x1Locations[index_type][bottom1x1Index]
			&& isTop) {
			
			//clockwise:
			Coord2D start1x1 = new Coord2D(bottom1x1Index, 0);
			
			int rotations[] = new int[] {0, 2};
			
			for(int i=0; i<rotations.length; i++) {
				
				int numMovements = 0;
				Coord2D cur1x1 = new Coord2D(start1x1.i, rotations[i]);
				
				while( ! hitLastorRing0Barrier(index_type, cur1x1, isTop)) {
					
					cur1x1 = tryAttachCellInDir(cur1x1.i, cur1x1.j, LEFT);
					numMovements++;
					
					if(numMovements % 4 != 0) {
	
						Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(cur1x1.i, cur1x1.j);
						
						allowedFirstRingIndexRotations1x1Clock[index_type][cur1x1.i][cur1x1.j] = false;
						allowedFirstRingIndexRotations1x1Clock[index_type][flippedCoord.i][flippedCoord.j] = false;
						
						allowedFirstRingIndexRotations1x1Counter[index_type][cur1x1.i][cur1x1.j] = false;
						allowedFirstRingIndexRotations1x1Counter[index_type][flippedCoord.i][flippedCoord.j] = false;
						
					}
				}
			}
			
		}
		
	}
	
	private void addPossible1x1(int index_type, Coord2D indexRotatation) {
		
		if(indexToRing[indexRotatation.i] == 0) {
			if(indexRotatation.j == 2) {
				allowedFirstRingIndexRotations1x1Locations[index_type][indexRotatation.i] = true;
			} else {
				Coord2D flippedCoord = topLeftIndexRotAfter180Flip1x4layer(indexRotatation.i, indexRotatation.j);
				allowedFirstRingIndexRotations1x1Locations[index_type][flippedCoord.i] = true;
			}
		}
		
	}
	
	public Coord2D get1x1BetweenBarriersIfExists(int indexType, boolean top) {
		
		if(top) {
			
			boolean rightMostWorks = true;
			for(int i=0; i<topRightMostShiftIndex.length; i++) {
				if(    (i == 1 && hitLastorRing0Barrier(indexType, topRightMostShiftIndex[i],  top))
				    || (i != 1 && ! hitLastorRing0Barrier(indexType, topRightMostShiftIndex[i],  top))
				){
					rightMostWorks = false;
					break;
				}
			}
			
			if(rightMostWorks) {
				return new Coord2D(topRightMostShiftIndex[1], 2);
			}
			
			boolean leftMostWorks = true;
			for(int i=0; i<topLeftMostShiftIndex.length; i++) {
				if(    (i == 1 && hitLastorRing0Barrier(indexType, topLeftMostShiftIndex[i],  top))
				    || (i != 1 && ! hitLastorRing0Barrier(indexType, topLeftMostShiftIndex[i],  top))
				){
					leftMostWorks = false;
					break;
				}
			}
			
			if(leftMostWorks) {
				return new Coord2D(topLeftMostShiftIndex[1], 2);
			}
		} else {
			
			boolean rightMostWorks = true;
			for(int i=0; i<bottomRightMostShiftIndex.length; i++) {
				if(    (i == 1 && hitLastorRing0Barrier(indexType, bottomRightMostShiftIndex[i],  top))
				    || (i != 1 && ! hitLastorRing0Barrier(indexType, bottomRightMostShiftIndex[i],  top))
				){
					rightMostWorks = false;
					break;
				}
			}
			
			if(rightMostWorks) {
				return new Coord2D(bottomRightMostShiftIndex[1], 2);
			}
			
			boolean leftMostWorks = true;
			for(int i=0; i<bottomLeftMostShiftIndex.length; i++) {
				if(    (i == 1 && hitLastorRing0Barrier(indexType, bottomLeftMostShiftIndex[i],  top))
				    || (i != 1 && ! hitLastorRing0Barrier(indexType, bottomLeftMostShiftIndex[i],  top))
				){
					leftMostWorks = false;
					break;
				}
			}
			
			if(leftMostWorks) {
				return new Coord2D(bottomLeftMostShiftIndex[1], 2);
			}
		}
		
		return new Coord2D(-1, -1);
	}

	public Coord2D getBarrier1(int indexType, boolean top, boolean aboveRing) {
		Coord2D barrier1 = new Coord2D(-1, -1);
		
		if(top) {
			if(aboveRing) {
	
				for(int i=0; i<topRightMostShiftIndex.length; i++) {
					if(hitLastorRing0Barrier(indexType, topRightMostShiftIndex[i],  top)) {
						barrier1 = new Coord2D(topRightMostShiftIndex[i], 0);
						break;
					}
				}
				
				if(barrier1.i == -1) {
					
					for(int i=0; i<topLeftMostShiftIndex.length; i++) {
						int newI = topLeftMostShiftIndex.length - 1 - i;
						if(hitLastorRing0Barrier(indexType, topLeftMostShiftIndex[newI],  top)) {
							barrier1 = new Coord2D(topLeftMostShiftIndex[newI], 0);
							break;
						}
					}
				}
				
			} else {
				
				for(int i=0; i<topLeftMostShiftIndex.length; i++) {
					int newI = topLeftMostShiftIndex.length - 1 - i;
					if(hitLastorRing0Barrier(indexType, topLeftMostShiftIndex[newI],  top)) {
						barrier1 = new Coord2D(topLeftMostShiftIndex[newI], 0);
						break;
					}
				}
				
				
				if(barrier1.i == -1) {
					for(int i=0; i<topRightMostShiftIndex.length; i++) {
						if(hitLastorRing0Barrier(indexType, topRightMostShiftIndex[i],  top)) {
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
		
		Coord2D barrier1 = getBarrier1(indexType, top, Ring0orLastabove);

		int ret = -1;
		Coord2D cur = new Coord2D(barrier1.i, 0);
		do {
			cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			ret++;
		} while( ! hitLastorRing0Barrier(indexType, cur, top));
		
		
		return ret;
		
	}

	public boolean isLayer1x4Option(int indexType, int index, int rotation, boolean top) {
		
		return isLayer1x4Option(indexType, new Coord2D(index, rotation), top);
	}
	public boolean isLayer1x4Option(int indexType, Coord2D leftMostCoord, boolean top) {
		
		Coord2D curCoord = leftMostCoord;
		for(int i=0; i<4; i++) {
			if(hitLastorRing0Barrier(indexType, curCoord, top)) {
				return false;
			}
			
			curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, RIGHT);
		}
		
		return true;
	}
	
	public int ringLastToBottomTransitions[][];
	
	//pre: areTopShiftIndexesAllSet is true
	public void setupLastRingAndBottomTransitions(
			Coord2D top1x1Index,
			Coord2D lastRingCoord,
			Coord2D bottomTopCoord,
			CuboidToFoldOnSemiGrained3 sg,
			int topBottomShiftIndexLeftMost[][]) {
		
		ringLastToBottomTransitions = new int[((int)Math.pow(2, 3))][getNumCellsToFill()];
		for(int i=0; i<ringLastToBottomTransitions.length; i++) {
			for(int j=0; j<ringLastToBottomTransitions[0].length; j++) {
				ringLastToBottomTransitions[i][j] = -1;
			}
		}
		

		boolean IS_TOP = false;
		
		NEXT_INDEX_TYPE:
		for(int index_type=0; index_type<(int)Math.pow(2, 3); index_type++) {
			
			//if(DEBUG) {
			//	System.out.println("index_type bottom: " + index_type);
			//}
			Coord2D curIndexLastRing = lastRingCoord;
			Coord2D curIndexBottom = bottomTopCoord;
			
			
			if(hitLastorRing0Barrier(index_type, top1x1Index, IS_TOP)) {
				continue;
			}
			
			do {
				
				Coord2D trialCoord = new Coord2D(curIndexBottom.i, curIndexBottom.j);
				
				//if(DEBUG) {
				//	System.out.println(curIndexBottom.i + "," + curIndexBottom.j);
				//}
				boolean goAround = false;
				boolean cancelItForIndexType = false;
				for(int i=0; i<4; i++) {
					
					trialCoord = tryAttachCellInDir(trialCoord.i, trialCoord.j, RIGHT);
					
					//TODO: this.indexToRing[trialCoord.i] <= sg.getDimensions()[0] - 2 will be hard to refactor
					if(hitTopBottomBarrier(index_type, trialCoord, IS_TOP) || (this.indexToRing[trialCoord.i] >=0 && this.indexToRing[trialCoord.i] <= sg.getDimensions()[0] - 2)) {
						goAround = true;
						
						if(i != 4 - 1) {
							cancelItForIndexType= true;
						}
						break;
					}
				}
				if(cancelItForIndexType) {
					//if(DEBUG) {
					//	System.out.println();
					//}
					for(int j=0; j<ringLastToBottomTransitions[0].length; j++) {
						ringLastToBottomTransitions[index_type][j] = -1;
					}
					continue NEXT_INDEX_TYPE;
				}
				
				if(goAround) {
					int leftMostIndex = topBottomShiftIndexLeftMost[curIndexBottom.i][curIndexBottom.j];
					
					if(leftMostIndex == bottomLeftMostShiftIndex[0]) {
						if(curIndexBottom.j == 2) {
							curIndexBottom = new Coord2D(bottomLeftMostShiftIndex[2], 1);
						} else {
							//TODO: 1 - > 3...
							curIndexBottom = new Coord2D(bottomRightMostShiftIndex[2], 1);
							
						}
					} else {
						if(curIndexBottom.j == 2) {
							curIndexBottom = new Coord2D(bottomLeftMostShiftIndex[0], 1);
						} else {
							
							curIndexBottom = new Coord2D(bottomRightMostShiftIndex[0], 1);
							
						}
					}
					
					if(hitTopBottomBarrier(index_type, curIndexBottom, IS_TOP)) {
						
						curIndexBottom = tryAttachCellInDir(curIndexBottom.i, curIndexBottom.j, RIGHT);
						
					}
					
					if(indexToRing[tryAttachCellInDir(curIndexBottom.i, curIndexBottom.j, RIGHT).i] == sg.getDimensions()[0] - 2) {
						System.out.println("Doh! Messed up the algo...");
						System.exit(1);
						
					}
					
				} else {
					curIndexBottom = trialCoord;
				}
			
				for(int i=0; i<4; i++) {
					curIndexLastRing = tryAttachCellInDir(curIndexLastRing.i, curIndexLastRing.j, RIGHT);
					
					
					while(hitLastorRing0Barrier(index_type, curIndexLastRing, IS_TOP) || curIndexLastRing.i == top1x1Index.i) {
						curIndexLastRing = tryAttachCellInDir(curIndexLastRing.i, curIndexLastRing.j, RIGHT);
					}
					
				}
				
				//System.exit(1);
				setForcedTransition(ringLastToBottomTransitions, index_type, curIndexLastRing, curIndexBottom);
				
			} while(curIndexBottom.i != bottomTopCoord.i);
			
		}
	}
	
	//start:

	public int ring0ToTopTransitions[][];
	
	public static boolean DEBUG = false;
	
	//pre: areTopShiftIndexesAllSet is true
	public void setupRing0AndTopTransitions(
			Coord2D bottom1x1Index,
			Coord2D firstRing0Coord,
			Coord2D firstTopCoord,
			CuboidToFoldOnSemiGrained3 sg,
			int topBottomShiftIndexLeftMost[][]) {
		
		ring0ToTopTransitions = new int[((int)Math.pow(2, 3))][getNumCellsToFill()];
		for(int i=0; i<ring0ToTopTransitions.length; i++) {
			for(int j=0; j<ring0ToTopTransitions[0].length; j++) {
				ring0ToTopTransitions[i][j] = -1;
			}
		}
		

		boolean IS_TOP = true;
		
		NEXT_INDEX_TYPE:
		for(int index_type=0; index_type<(int)Math.pow(2, 3); index_type++) {
			
			//if(DEBUG) {
			//	System.out.println("index_type: " + index_type);
			//}
			Coord2D curIndexRing0 = firstRing0Coord;
			Coord2D curIndexTop = firstTopCoord;
			
			
			if(hitLastorRing0Barrier(index_type, bottom1x1Index, IS_TOP)) {
				continue;
			}
			
			do {
				
				Coord2D trialCoord = new Coord2D(curIndexTop.i, curIndexTop.j);
				
				//if(DEBUG) {
				//	System.out.println(curIndexTop.i + "," + curIndexTop.j);
				//}
				boolean goAround = false;
				boolean cancelItForIndexType = false;
				for(int i=0; i<4; i++) {
					
					trialCoord = tryAttachCellInDir(trialCoord.i, trialCoord.j, RIGHT);
					
					if(hitTopBottomBarrier(index_type, trialCoord, IS_TOP) || this.indexToRing[trialCoord.i] >= 1) {
						goAround = true;
						
						if(i != 4 - 1) {
							cancelItForIndexType= true;
						}
						break;
					}
				}
				if(cancelItForIndexType) {
					//if(DEBUG) {
					//	System.out.println();
					//}
					for(int j=0; j<ring0ToTopTransitions[0].length; j++) {
						ring0ToTopTransitions[index_type][j] = -1;
					}
					continue NEXT_INDEX_TYPE;
				}
				
				if(goAround) {
					int leftMostIndex = topBottomShiftIndexLeftMost[curIndexTop.i][curIndexTop.j];
					
					if(leftMostIndex == topLeftMostShiftIndex[0]) {
						if(curIndexTop.j == 2) {
							curIndexTop = new Coord2D(topLeftMostShiftIndex[2], 3);
						} else {
							//1 - > 3...
							curIndexTop = new Coord2D(topRightMostShiftIndex[2], 3);
							
						}
					} else {
						if(curIndexTop.j == 2) {
							curIndexTop = new Coord2D(topLeftMostShiftIndex[0], 3);
						} else {
							
							curIndexTop = new Coord2D(topRightMostShiftIndex[0], 3);
							
						}
					}
					
					if(hitTopBottomBarrier(index_type, curIndexTop, IS_TOP)) {
						
						curIndexTop = tryAttachCellInDir(curIndexTop.i, curIndexTop.j, RIGHT);
						
					}
					
					if(indexToRing[tryAttachCellInDir(curIndexTop.i, curIndexTop.j, RIGHT).i] == 1) {
						System.out.println("Doh! Messed up the algo...");
						System.exit(1);
						
					}
					
				} else {
					curIndexTop = trialCoord;
				}
			
				for(int i=0; i<4; i++) {
					curIndexRing0 = tryAttachCellInDir(curIndexRing0.i, curIndexRing0.j, RIGHT);
					
					
					while(hitLastorRing0Barrier(index_type, curIndexRing0, IS_TOP) || curIndexRing0.i == bottom1x1Index.i) {
						curIndexRing0 = tryAttachCellInDir(curIndexRing0.i, curIndexRing0.j, RIGHT);
					}
					
				}
				
				setForcedTransition(ring0ToTopTransitions, index_type, curIndexRing0, curIndexTop);
				
			} while(curIndexTop.i != firstTopCoord.i);
			
		}
	}
	//end
	public int ring0ToRing1Transitions[][];
	
	public void setupRing1AndRing0Transitions(Coord2D bottom1x1Index, Coord2D firstRing1Coord) {
		
		ring0ToRing1Transitions = new int[((int)Math.pow(2, 3))][getNumCellsToFill()];
		for(int i=0; i<ring0ToRing1Transitions.length; i++) {
			for(int j=0; j<ring0ToRing1Transitions[0].length; j++) {
				ring0ToRing1Transitions[i][j] = -1;
			}
		}
		
		
		boolean IS_TOP = true;
		
		for(int index_type=0; index_type<(int)Math.pow(2, 3); index_type++) {
			

			
			Coord2D curIndexRing1 = firstRing1Coord;

			Coord2D curIndexRing0 = bottom1x1Index;
			
			
			if(hitLastorRing0Barrier(index_type, bottom1x1Index, IS_TOP)) {
				continue;
			}
			
			boolean firstMoveToRight = true;
					
			do {
				
				for(int i=0; i<4; i++) {
					curIndexRing1 = tryAttachCellInDir(curIndexRing1.i, curIndexRing1.j, RIGHT);
				}
				
				if(firstMoveToRight) {

					//Move from 1x1 square to 1x4 rectangle in the first iteration:
					firstMoveToRight = false;
					
					do {
						
						curIndexRing0 = tryAttachCellInDir(curIndexRing0.i, curIndexRing0.j, RIGHT);
						
					} while(hitLastorRing0Barrier(index_type, curIndexRing0, IS_TOP));
					
				} else {
					
					for(int i=0; i<4; i++) {
						curIndexRing0 = tryAttachCellInDir(curIndexRing0.i, curIndexRing0.j, RIGHT);
						
						while(hitLastorRing0Barrier(index_type, curIndexRing0, IS_TOP)) {
							curIndexRing0 = tryAttachCellInDir(curIndexRing0.i, curIndexRing0.j, RIGHT);
						}
					}
				}
				
				setForcedTransition(ring0ToRing1Transitions, index_type, curIndexRing0, curIndexRing1);
				
			} while(curIndexRing1.i != firstRing1Coord.i);
		}
	}
	

	public int ringSecondLastToLastRingTransitions[][];
	
	//pre: secondLastRingIndex goes to lastRingIndex
	public void setupRingSecondLastAndRingLastTransitions(Coord2D secondLastRingIndex, Coord2D lastRingIndex, int topIndex) {
		
		ringSecondLastToLastRingTransitions = new int[((int)Math.pow(2, 3))][getNumCellsToFill()];
		for(int i=0; i<ringSecondLastToLastRingTransitions.length; i++) {
			for(int j=0; j<ringSecondLastToLastRingTransitions[0].length; j++) {
				ringSecondLastToLastRingTransitions[i][j] = -1;
			}
		}
		
		
		boolean IS_TOP = false;
		
		NEXT_INDEX_TYPE:
		for(int index_type=0; index_type<(int)Math.pow(2, 3); index_type++) {
			

			Coord2D curIndexRingSecondLast = secondLastRingIndex;
			Coord2D curIndexRingLast = lastRingIndex;

			setForcedTransition(ringSecondLastToLastRingTransitions, index_type, curIndexRingSecondLast, curIndexRingLast);
			
			int debugCount = 0;
			boolean topIndexTouched = false;
			do {
				
				for(int i=0; i<4; i++) {
					curIndexRingSecondLast = tryAttachCellInDir(curIndexRingSecondLast.i, curIndexRingSecondLast.j, RIGHT);
				}
				
				if(curIndexRingLast.i == topIndex) {

					topIndexTouched = true;
					//Move from 1x1 square to 1x4 rectangle in the first iteration:
					//System.exit(1);
					
					do {
						
						curIndexRingLast = tryAttachCellInDir(curIndexRingLast.i, curIndexRingLast.j, RIGHT);
						
					} while(hitLastorRing0Barrier(index_type, curIndexRingLast, IS_TOP));
					
				} else {
					
					while(hitLastorRing0Barrier(index_type, curIndexRingLast, IS_TOP)) {
						curIndexRingLast = tryAttachCellInDir(curIndexRingLast.i, curIndexRingLast.j, RIGHT);
					}
					
					for(int i=0; i<4; i++) {
						
						if(hitLastorRing0Barrier(index_type, curIndexRingLast, IS_TOP)) {
							
							for(int j=0; j<ringSecondLastToLastRingTransitions[0].length; j++) {
								ringSecondLastToLastRingTransitions[index_type][j] = -1;
							}
							continue NEXT_INDEX_TYPE;
							
						}
						curIndexRingLast = tryAttachCellInDir(curIndexRingLast.i, curIndexRingLast.j, RIGHT);
						
					}
					while(hitLastorRing0Barrier(index_type, curIndexRingLast, IS_TOP)) {
						curIndexRingLast = tryAttachCellInDir(curIndexRingLast.i, curIndexRingLast.j, RIGHT);
					}
				}
				
				if(curIndexRingLast.i != topIndex) {
					//System.out.println("lastRingIndex: " + lastRingIndex.i);
					setForcedTransition(ringSecondLastToLastRingTransitions, index_type, curIndexRingSecondLast, curIndexRingLast);
					
				} else {
					//TODO: my guess is that it's not needed and delays everything:
					//setForcedTransitionSingleSide(ringSecondLastToLastRingTransitions, index_type, curIndexRingSecondLast, curIndexRingLast);
					
				}
				
				
				debugCount++;
			} while(curIndexRingSecondLast.i != secondLastRingIndex.i);
			
			if(topIndexTouched == false) {
				//System.out.println("Top index missed for topIndex = " + topIndex + " and index_type = " + index_type);
				for(int j=0; j<ringSecondLastToLastRingTransitions[0].length; j++) {
					ringSecondLastToLastRingTransitions[index_type][j] = -1;
				}
			}
			
			//if(debugCount != 9) {
			//	System.out.println("DOH " + debugCount);
			//	System.exit(1);
			//}
		}
	}
	
	
	private void setForcedTransitionSingleSide(int transitionArray[][], int index_type, Coord2D from, Coord2D to) {
		
		transitionArray[index_type][from.i] = to.i;
		
	}
	
	private void setForcedTransition(int transitionArray[][], int index_type, Coord2D from, Coord2D to) {
		
		transitionArray[index_type][from.i] = to.i;
		
		Coord2D toReversed = topLeftIndexRotAfter180Flip1x4layer(to.i, to.j);
		Coord2D fromReversed = topLeftIndexRotAfter180Flip1x4layer(from.i, from.j);
		
		transitionArray[index_type][toReversed.i] = fromReversed.i;
		
	}
	
	public boolean hitTopBottomBarrier(int index_type, Coord2D coord, boolean top) {
		return hitTopBottomBarrier(index_type, coord.i, top);
	}
	public boolean hitTopBottomBarrier(int index_type, int index, boolean top) {

		if(top) {
			for(int i=0; i<topLeftMostShiftIndex.length; i++) {
				
				int newi = topLeftMostShiftIndex.length - 1 - i;
				
				if(index == topLeftMostShiftIndex[i] && ((~index_type) & (1 << newi)) == 0) {
					return true;
				}
			}
			
			for(int i=0; i<topRightMostShiftIndex.length; i++) {
				
				int newi = topLeftMostShiftIndex.length - 1 - i;
				if(index == topRightMostShiftIndex[i] && (index_type & (1 << newi)) == 0) {
					return true;
				}
			}
		} else {
			
			//TODO: I don't think I tested this at all:
			for(int i=0; i<bottomLeftMostShiftIndex.length; i++) {
				
				int newi = bottomLeftMostShiftIndex.length - 1 - i;
				if(index == bottomLeftMostShiftIndex[i] && ((~index_type) & (1 << newi)) == 0) {
					return true;
				}
			}

			for(int i=0; i<bottomRightMostShiftIndex.length; i++) {
				
				int newi = bottomRightMostShiftIndex.length - 1 - i;
				if(index == bottomRightMostShiftIndex[i] && (index_type & (1 << newi)) == 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hitLastorRing0Barrier(int index_type, Coord2D coord, boolean top) {
		return hitLastorRing0Barrier(index_type, coord.i, top);
	}
	
	public boolean hitLastorRing0Barrier(int index_type, int index, boolean top) {
		
		if(top) {
			for(int i=0; i<topLeftMostShiftIndex.length; i++) {
				
				int newi = topLeftMostShiftIndex.length - 1 - i;
				
				if(index == topLeftMostShiftIndex[i] && ((~index_type) & (1 << newi)) != 0) {
					return true;
				}
			}
			
			for(int i=0; i<topRightMostShiftIndex.length; i++) {
				
				int newi = topLeftMostShiftIndex.length - 1 - i;
				if(index == topRightMostShiftIndex[i] && (index_type & (1 << newi)) != 0) {
					return true;
				}
			}
		} else {
			
			//TODO: I don't think I tested this at all:
			for(int i=0; i<bottomLeftMostShiftIndex.length; i++) {
				
				int newi = topLeftMostShiftIndex.length - 1 - i;
				if(index == bottomLeftMostShiftIndex[i] && ((~index_type) & (1 << newi)) != 0) {
					return true;
				}
			}

			for(int i=0; i<bottomRightMostShiftIndex.length; i++) {
				
				int newi = topLeftMostShiftIndex.length - 1 - i;
				if(index == bottomRightMostShiftIndex[i] && (index_type & (1 << newi)) != 0) {
					return true;
				}
			}
		}
		return false;
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
			if(hitLastorRing0Barrier(index_type, i, true)) {
				labelSoFar = "XX";
			}
			
			labels[i] = labelSoFar;
			
			
		}
		
		System.out.println(DataModelViews.getFlatNumberingView(this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				labels));
		
	}
	
	private void labelDebugIfTrueAllowedBottom1x1Index(boolean index[], int index_type) {
		
		
		String labels[] = new String[getNumCellsToFill()];
		for(int i=0; i<labels.length; i++) {
			String labelSoFar = "11";
			
			
			if(index[i] == false) {
				labelSoFar = "00";
			}
			if(hitLastorRing0Barrier(index_type, i, true)) {
				labelSoFar = "XX";
			}
			
			labels[i] = labelSoFar;
			
			
		}
		
		System.out.println(DataModelViews.getFlatNumberingView(this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				labels));
		
	}
	
	

	public boolean areTopShiftIndexesAllSet(CuboidToFoldOnSemiGrained3 sg) {
		for(int i=0; i<topLeftMostShiftIndex.length; i++) {
			if(sg.topBottomShiftSetDepth[topLeftMostShiftIndex[i]] > sg.currentLayerIndex || sg.topBottomShiftSetDepth[topLeftMostShiftIndex[i]] < 0) {
				return false;
			}
		}
		
		return true;
	}

	public boolean areBottomShiftIndexesAllSet(CuboidToFoldOnSemiGrained3 sg) {
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
