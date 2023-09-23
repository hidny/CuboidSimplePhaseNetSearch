package SimplePhaseSearch.firstIteration;

//TODO: this is very incomplete!

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.NeighbourGraphCreator;
import Model.Utils;

public class CuboidToFoldOnExtendedSimplePhase1  implements CuboidToFoldOnInterface {

	
	private CoordWithRotationAndIndex[][] neighbours;
	
	public static final int NUM_DIMENSIONS_CUBOID = 3;
	private int dimensions[] = new int[NUM_DIMENSIONS_CUBOID];

	public CuboidToFoldOnExtendedSimplePhase1(int a, int b, int c) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c);
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;

		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		
		sanityTestGetCellsToAddGoingUpAndDown();
		
		setupAnswerSheetInBetweenLayers();
		setupAnswerSheetForTopCell();
	}
	
	public int getNumCellsToFill() {
		return Utils.getTotalArea(this.dimensions);
	}
	
	public CoordWithRotationAndIndex[] getNeighbours(int cellIndex) {
		return neighbours[cellIndex];
	}
	

	public int[] getDimensions() {
		return dimensions;
	}
	
	public void initializeNewBottomIndexAndRotation(int bottomIndex, int bottomRotationRelativeFlatMap) {
		
		this.topLeftGroundedIndex = bottomIndex;
		this.topLeftGroundRotationRelativeFlatMap = bottomRotationRelativeFlatMap;

		prevSideBumps = new int[DIM_N_OF_Nx1x1];
		prevGroundedIndexes = new int[DIM_N_OF_Nx1x1];
		prevGroundedRotations = new int[DIM_N_OF_Nx1x1];
		prevLayerIndex = new int[DIM_N_OF_Nx1x1];
		currentLayerIndex = 0;
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		tmpArray[bottomIndex] = true;
		this.curState = convertBoolArrayToLongs(tmpArray);
	}


	//Constants:
	public final static int LEVEL_OPTIONS[][] = {
			{1, 1, 1, 1, 0, 0, 0},
			{1, 1, 0, 1, 0, 0, 1},
			{1, 0, 1, 1, 0, 1, 0},
			{1, 0, 0, 1, 0, 1, 1}
	};
	
	//public static final int NUM_POSSIBLE_SIDE_BUMPS = 13;
	public static final int NUM_POSSIBLE_SIDE_BUMPS = 2 * LEVEL_OPTIONS[0].length - 1;
	
	//TODO: compute these vars instead of declaring them:
	// (I appended 'SANITY' to these variables because I feel like this would be a good sanity check.)

	//TODO: use this in the pre-compute functions:
	public final static int CELLS_TO_ADD_BY_STATE_GOING_UP[][] = {
			{1, 1, 1, 1, 0, 0, 0},
			//Hug left:
			{1, 1, 0, 1, 0, 0, 0},
			{1, 0, 1, 1, 0, 0, 0},
			{1, 0, 0, 1, 0, 0, 0},
			//Hug right:
			{0, 0, 0, 1, 0, 0, 1},
			{0, 0, 1, 1, 0, 1, 0},
			{0, 0, 0, 1, 0, 1, 1}
	};

	//TODO: use this in the pre-compute functions:
	public final static int CELLS_TO_ADD_BY_STATE_GOING_DOWN[][] = {
			{0, 0, 0, 0, 0, 0, 0},
			
			//Ground right:
			{0, 0, 0, 0, 0, 0, 1},
			{0, 0, 0, 0, 0, 1, 0},
			{0, 0, 0, 0, 0, 1, 1},
			
			//Ground left:
			{1, 1, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0},
	};
	
	public static final int NUM_LAYER_STATES = CELLS_TO_ADD_BY_STATE_GOING_DOWN.length;
	public static final int LENGTH_LAYER_STATES = CELLS_TO_ADD_BY_STATE_GOING_DOWN[0].length;
	
	public static final int NUM_ROTATIONS = 4;
	

	public static final int BAD_ROTATION = -10;
	public static final int BAD_INDEX = -20;
	
	private static final int NUM_BYTES_IN_LONG = 64;
	private static final int NUM_LONGS_TO_USE = 3;
	
	
	//Variables to compute at construction time:
	
	private int DIM_N_OF_Nx1x1;
	
	private long answerSheetGoingUp[][][][][][];
	private int newGroundedIndexAbove[][][][][];
	private int newGroundedRotationAbove[][][][][];

	//TODO: also take care of the cells that are grounded by layers above themselves:
	private long answerSheetGoingDown[][][][][][];
	private int newGroundedIndexBelow[][][][][];
	private int newGroundedRotationBelow[][][][][];
	
	private long answerSheetForTopCell[][][][];
	private long answerSheetForTopCellAnySideBump[][][];


	//State variables:
	private long curState[] = new long[NUM_LONGS_TO_USE];

	private int topLeftGroundedIndex = 0;
	private int topLeftGroundRotationRelativeFlatMap = 0;
	
	private int prevSideBumps[];
	private int prevGroundedIndexes[];
	private int prevGroundedRotations[];
	private int prevLayerIndex[];
	private int currentLayerIndex;
	
	public boolean isNewLayerValidSimpleFast(int layerIndex, int sideBump) {
	
		long tmp[] = answerSheetGoingUp[prevLayerIndex[layerIndex - 1]][layerIndex][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		return ((curState[0] & tmp[0]) | (curState[1] & tmp[1]) | (curState[2] & tmp[2])) == 0L;
		
	}
	
	public void addNewLayerFast(int layerIndex, int sideBump) {
		long tmp[] = answerSheetGoingUp[prevLayerIndex[layerIndex - 1]][layerIndex][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		curState[0] = curState[0] | tmp[0];
		curState[1] = curState[1] | tmp[1];
		curState[2] = curState[2] | tmp[2];
		
		int tmp1 = newGroundedIndexAbove[prevLayerIndex[layerIndex - 1]][layerIndex][this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		int tmp2 = newGroundedRotationAbove[prevLayerIndex[layerIndex - 1]][layerIndex][this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		prevGroundedIndexes[currentLayerIndex] = this.topLeftGroundedIndex;
		prevGroundedRotations[currentLayerIndex] = this.topLeftGroundRotationRelativeFlatMap;
		prevSideBumps[currentLayerIndex] = sideBump;
		prevLayerIndex[currentLayerIndex] = layerIndex;
		currentLayerIndex++;
		
		this.topLeftGroundedIndex = tmp1;
		this.topLeftGroundRotationRelativeFlatMap = tmp2;
		
		//TODO: connect newly grounded lower layers
		
	}
	
	public void removePrevLayerFast() {
		
		currentLayerIndex--;
		this.topLeftGroundedIndex = prevGroundedIndexes[currentLayerIndex]; 
		this.topLeftGroundRotationRelativeFlatMap = prevGroundedRotations[currentLayerIndex];
		int sideBumpToCancel  = prevSideBumps[currentLayerIndex];
		
		int layerBelow = prevLayerIndex[currentLayerIndex - 1];
		int layerIndexUsed = prevLayerIndex[currentLayerIndex];
		
		
		long tmp[] = answerSheetGoingUp[layerBelow][layerIndexUsed][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBumpToCancel];
		curState[0] = curState[0] ^ tmp[0];
		curState[1] = curState[1] ^ tmp[1];
		curState[2] = curState[2] ^ tmp[2];
		
		//TODO: disconnect newly grounded lower layers
	}
	
	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedFast() {
		
		long tmp[] = answerSheetForTopCellAnySideBump[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap];
		
		boolean ret = this.prevLayerIndex[currentLayerIndex] == 0 && ((~curState[0] & tmp[0]) | (~curState[1] & tmp[1]) | (~curState[2] & tmp[2])) != 0;

		
		return ret;
	}

	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedForSideBumpFast(int sideBump) {
		long tmp[] = answerSheetForTopCell[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		return this.prevLayerIndex[currentLayerIndex] == 0 &&  ((~curState[0] & tmp[0]) | (~curState[1] & tmp[1]) | (~curState[2] & tmp[2])) != 0;
	}
	
	// ***********************************************************
	// ***********************************************************
	//Pre-compute functions that make the program faster:
	
	private void setupAnswerSheetInBetweenLayers() {
		
		answerSheetGoingUp = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		newGroundedRotationAbove = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		newGroundedIndexAbove = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		

		answerSheetGoingDown = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		newGroundedIndexBelow = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		newGroundedRotationBelow = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		
		for(int layerStateBelow=0; layerStateBelow<NUM_LAYER_STATES; layerStateBelow++) {
			for(int layerStateAbove=0; layerStateAbove<NUM_LAYER_STATES; layerStateAbove++) {
				for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
					for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
						
						for(int sideBump=0; sideBump<NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
						
							
							if(layerStateBelow == 0 && layerStateAbove == 0) {
								handleSimpleLayerOverSimpleLayer(index, rotation, sideBump);
							} else {
								
								//TODO: also bottom to top
								
								//TODO: also isolated line bottom to top.
								
								//Top to bottom:
								handleLayerStateOverLayerStatePreComputeTopToBottom(layerStateBelow, layerStateAbove, index, rotation, sideBump);
							}
						}
					}
				}
			}
		}
		
	}
	
	private void getSideBumpAndStateIdArrayToIterateOver() {
		
		//TODO:
		//this should just find cases when:
		// newGroundedIndexAbove[layerBelow][layerAbove][index][rotation][sideBump] >= 0 (not N/A) exist for at least one of (layerBelow, layerAbove, and sideBump)
		
	}
	
	
	private boolean connectToTopToBottomFromLeft(int layerState) {
		
		if(layerState <= 4) {
			return true;
		} else {
			return false;
		}
	}

	
	//TODO: test this!
	private void handleLayerStateOverLayerStatePreComputeTopToBottom(int layerStateBelow, int layerStateAbove, int indexGroundedAbove, int rotationGroundedAbove, int sideBump) {
		
		if(layerStateBelow == 0) {	
			// No need to connect from top to bottom because
			// below layer is fully connected
			// I set it to impossible, but it's more like "Not applicable"...
			
			answerSheetGoingUp[0][0][indexGroundedAbove][rotationGroundedAbove][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAbove[0][0][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_INDEX;
			newGroundedRotationAbove[0][0][indexGroundedAbove][rotationGroundedAbove][rotationGroundedAbove] = BAD_ROTATION;						
			return;

		} else if(layerStateAbove != 0 && connectToTopToBottomFromLeft(layerStateBelow) != connectToTopToBottomFromLeft(layerStateAbove)) {

			// No need to connect from top to bottom because
			// below state connects the left part while above state connects the right part.

			answerSheetGoingUp[0][0][indexGroundedAbove][rotationGroundedAbove][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAbove[0][0][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_INDEX;
			newGroundedRotationAbove[0][0][indexGroundedAbove][rotationGroundedAbove][rotationGroundedAbove] = BAD_ROTATION;						
			return;

		}
		
		int groundingFromAbove[] = CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateAbove];
		
		if(layerStateAbove == 0) {
			groundingFromAbove = LEVEL_OPTIONS[0];
		}
		
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		

		int leftMostRelativeBottomLayer = sideBump - 6;
		
		boolean connected = false;
		boolean wentThroughLoopAlready = false;
		

		Coord2D curGroundAbove = null;

		for(int i=0; i<CELLS_TO_ADD_BY_STATE_GOING_DOWN.length; i++) {
			
			int topLayerIndex = i - leftMostRelativeBottomLayer;
			
			if( topLayerIndex < 0 || topLayerIndex >= CELLS_TO_ADD_BY_STATE_GOING_DOWN.length) {
				continue;
			}

			if(groundingFromAbove[topLayerIndex] == 1) {
				
				if(curGroundAbove == null) {
					curGroundAbove = new Coord2D(indexGroundedAbove, rotationGroundedAbove);
				} else {
					if(groundingFromAbove[leftMostRelativeBottomLayer - 1] != 1) {
						System.out.println("ERROR: unexpected result in handleLayerStateOverLayerStatePreComputeTopToBottom");
						System.exit(1);
					}
					curGroundAbove = tryAttachCellInDir(curGroundAbove.i, curGroundAbove.j, RIGHT);
				}
				
				if(CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow][i] == 1) {

					connected = true;

					Coord2D cellBelowCurGround = tryAttachCellInDir(curGroundAbove.i, curGroundAbove.j, BELOW);

					Coord2D curCell = cellBelowCurGround;
					//Go to left:
					for(int k=i; k - 1 >=0 && CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow][k-1] == 1; k--) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, LEFT);
						tmpArray[curCell.i] = true;
					}
					
					Coord2D nextGounded = curCell;
					
					curCell = cellBelowCurGround;
					
					//Go to right:
					for(int k=i; k + 1 < CELLS_TO_ADD_BY_STATE_GOING_DOWN.length 
							&& CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow][k+1] == 1; k++) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, RIGHT);
						tmpArray[curCell.i] = true;
					}

					if(wentThroughLoopAlready == false) {

						answerSheetGoingDown[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = convertBoolArrayToLongs(tmpArray);
						
						newGroundedIndexBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = nextGounded.i;
						newGroundedRotationBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = nextGounded.j;
					
						tmpArray = new boolean[Utils.getTotalArea(this.dimensions)];
						wentThroughLoopAlready = true;
						
					} else {
						
						//If the answer isn't consistent, then it doesn't work:
						
						if(convertBoolArrayToLongs(tmpArray) !=
								answerSheetGoingDown[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump]
							|| nextGounded.i !=
								newGroundedIndexBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump]
							|| nextGounded.j !=
									newGroundedRotationBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump]
							) {

							answerSheetGoingUp[0][0][indexGroundedAbove][rotationGroundedAbove][sideBump] = setImpossibleForAnswerSheet();
							newGroundedIndexAbove[0][0][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_INDEX;
							newGroundedRotationAbove[0][0][indexGroundedAbove][rotationGroundedAbove][rotationGroundedAbove] = BAD_ROTATION;						
							return;

						}
						
					}
				}
			}
		
		}
		
		if(connected == false) {
			System.out.println("ERROR: something went wrong in handleLayerStateOverLayerStatePreComputeTopToBottom");
			System.out.println("DEBUG");
			System.exit(1);
		}
		
	}
	

	private void handleLayerStateOverLayerStatePreComputeBottomToTop(int layerStateBelow, int layerStateAbove, int index, int rotation, int sideBump) {
		//TODO!
	}

	//TODO: I might decide to throw this away later.
	private void handleSimpleLayerOverSimpleLayer(int index, int rotation, int sideBump) {
		

		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		
		int leftMostRelativeTopLeftGrounded = sideBump - 6;
		
		if( leftMostRelativeTopLeftGrounded < -3 || leftMostRelativeTopLeftGrounded > 3) {
			
			answerSheetGoingUp[0][0][index][rotation][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAbove[0][0][index][rotation][sideBump] = BAD_INDEX;
			newGroundedRotationAbove[0][0][index][rotation][sideBump] = BAD_ROTATION;						
			return;
		}
		

		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = false;
		}

		Coord2D nextGounded = null;
		
		if(leftMostRelativeTopLeftGrounded<=0) {
			
			Coord2D aboveGroundedTopLeft = tryAttachCellInDir(index, rotation, ABOVE);

			tmpArray[aboveGroundedTopLeft.i] = true;
			
			Coord2D cur = aboveGroundedTopLeft;
			//Go to left:
			for(int i=0; i>leftMostRelativeTopLeftGrounded; i--) {
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				tmpArray[cur.i] = true;
			}
			
			nextGounded = cur;
			
			cur = aboveGroundedTopLeft;
			//Go to right:
			for(int i=0; i<leftMostRelativeTopLeftGrounded + 3; i++) {
				
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				tmpArray[cur.i] = true;
			}
			
		} else {
			
			Coord2D cur = new Coord2D(index, rotation);
			//Go to right until there's a cell above:
			
			for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			}
			
			
			Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);
			
			nextGounded = cellAbove;
			
			tmpArray[cellAbove.i] = true;
			
			cur = cellAbove;
			//Go to right:
			for(int i=0; i<3; i++) {
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				tmpArray[cur.i] = true;
			}
			
		}
		
		answerSheetGoingUp[0][0][index][rotation][sideBump] = convertBoolArrayToLongs(tmpArray);
		
		newGroundedIndexAbove[0][0][index][rotation][sideBump] = nextGounded.i;
		newGroundedRotationAbove[0][0][index][rotation][sideBump] = nextGounded.j;
	}
	
	public void setupAnswerSheetForTopCell() {
		
		answerSheetForTopCell = new long[Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		answerSheetForTopCellAnySideBump = new long[Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_LONGS_TO_USE];
		
		for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
				
				boolean tmpArrayForAnySideBump[] = new boolean[Utils.getTotalArea(this.dimensions)];
				
				for(int sideBump=0; sideBump<NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
					
					
					Coord2D cur = new Coord2D(index, rotation);
					//Go to right until there's a cell above:
			
					int leftMostRelativeTopLeftGrounded = sideBump - 6;
					
					if(leftMostRelativeTopLeftGrounded >= 0 && leftMostRelativeTopLeftGrounded < 4) {
					

						boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
						
						for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {
				
							cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
						}
						
						Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
						
						tmpArray[cellAbove.i] = true;
						tmpArrayForAnySideBump[cellAbove.i] = true;
						
						answerSheetForTopCell[index][rotation][sideBump] = convertBoolArrayToLongs(tmpArray);
						//return ! this.cellsUsed[cellAbove.i];
			
					} else {
						answerSheetForTopCell[index][rotation][sideBump] = setImpossibleForTopAnswerSheet();
					}
				}
				
				answerSheetForTopCellAnySideBump[index][rotation] = convertBoolArrayToLongs(tmpArrayForAnySideBump);
			}
		}
		
	}

	private long[] convertBoolArrayToLongs(boolean tmpArray[]) {
		
		//1st entry:
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = 0;
		}
		
		for(int i=0; i<tmpArray.length; i++) {
			
			if(tmpArray[i]) {
				int indexArray = i / NUM_BYTES_IN_LONG;
				int bitShift = (NUM_BYTES_IN_LONG - 1) - i - indexArray * NUM_BYTES_IN_LONG;
				
				ret[indexArray] += 1L << bitShift;
			}
		}
		
		
		return ret;
	}

	private static long[] setImpossibleForAnswerSheet() {
		
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = -1L;
		}
		
		return ret;
	}
	
	private static long[] setImpossibleForTopAnswerSheet() {
		
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = 0L;
		}
		
		return ret;
	}

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.neighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_ROTATIONS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_ROTATIONS) % NUM_ROTATIONS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}

	
	// Only for testing:
	public static void main(String args[]) {

		sanityTestGetCellsToAddGoingUpAndDown();
		
		CuboidToFoldOnExtendedSimplePhase1 test1 = new CuboidToFoldOnExtendedSimplePhase1(5, 1, 1);
		

		//CuboidToFoldOnExtendedSimplePhase1 test2 = new CuboidToFoldOnExtendedSimplePhase1(3, 2, 1);
		
	}

	public static void sanityTestGetCellsToAddGoingUpAndDown() {
		
		//Up:
		int goingUp[][] = new int[NUM_LAYER_STATES][LEVEL_OPTIONS.length];
		int goingDown[][] = new int[NUM_LAYER_STATES][LEVEL_OPTIONS.length];
		
		//1st
		for(int j=0; j<LEVEL_OPTIONS.length; j++) {
			goingUp[0][j] = LEVEL_OPTIONS[0][j];
			goingDown[0][j] = 0;
		}
		
		//left and middle ones going up and right going down:
		for(int i=1; i<4; i++) {
			
			int numZeroFoundAfter1 = 0;
			boolean currentlyReadOnes = false;

			for(int j=0; j<LEVEL_OPTIONS.length; j++) {
				
				if(LEVEL_OPTIONS[i][j] == 1) {
					currentlyReadOnes = true;
				} else if(LEVEL_OPTIONS[i][j] == 0) {

					if(currentlyReadOnes) {
						numZeroFoundAfter1++;
					}
					currentlyReadOnes = false;
				}
				
				goingUp[i][j] = 0;
				goingDown[i][j] = 0;
				
				if(numZeroFoundAfter1 < 2) {
					goingUp[i][j] = LEVEL_OPTIONS[i][j];
				} else {
					goingDown[i][j] = LEVEL_OPTIONS[i][j];
				}
			}
		}
		
		int INDEX_ADJUST = 3;
		
		//middle and right ones going up and right going down:
		for(int i=1; i<4; i++) {
			
			int numZeroFoundAfter1 = 0;
			boolean currentlyReadOnes = false;

			for(int j=0; j<LEVEL_OPTIONS.length; j++) {
				
				if(LEVEL_OPTIONS[i][j] == 1) {
					currentlyReadOnes = true;
				} else if(LEVEL_OPTIONS[i][j] == 0) {

					if(currentlyReadOnes) {
						numZeroFoundAfter1++;
					}
					currentlyReadOnes = false;
				}
				
				goingUp[i + INDEX_ADJUST][j] = 0;
				goingDown[i + INDEX_ADJUST][j] = 0;
				
				if(numZeroFoundAfter1 < 1) {
					goingDown[i + INDEX_ADJUST][j] = LEVEL_OPTIONS[i][j];
				} else {
					goingUp[i + INDEX_ADJUST][j] = LEVEL_OPTIONS[i][j];
				}
			}
		}
		
		//Sanity test:
		
		for(int i=0; i<NUM_LAYER_STATES; i++) {
			for(int j=0; j<LEVEL_OPTIONS.length; j++) {
				
				if(goingUp[i][j] != CELLS_TO_ADD_BY_STATE_GOING_UP[i][j]) {
					System.out.println("ERROR: unexpected value for CELLS_TO_ADD_BY_STATE_GOING_UP. (" + i + "," + j + ")");
					System.exit(1);
				}

				if(goingDown[i][j] != CELLS_TO_ADD_BY_STATE_GOING_DOWN[i][j]) {
					System.out.println("ERROR: unexpected value for CELLS_TO_ADD_BY_STATE_GOING_DOWN. (" + i + "," + j + ")");
					System.exit(1);
				}
			}
		}
		
	}

}
