package SimplePhaseSearch.firstIteration;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.NeighbourGraphCreator;
import Model.Utils;

public class CuboidToFoldOnExtendedSimplePhase1  implements CuboidToFoldOnInterface {

	
	private CoordWithRotationAndIndex[][] neighbours;
	
	public static final int NUM_DIMENSIONS_CUBOID = 3;
	private int dimensions[] = new int[3];

	public CuboidToFoldOnExtendedSimplePhase1(int a, int b, int c) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c);
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;

		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		
		setupAnswerSheetInBetweenLayersGoingBottomToTop();
		setupAnswerSheetInBetweenLayersGoingTopToBottom();
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

	//7 *2 -1:
	public static final int NUM_POSSIBLE_SIDE_BUMPS = 13;
	
	public static final int NUM_LAYER_STATES = 7;
	
	public static final int NUM_NEIGHBOURS = 4;
	public static final int NUM_ROTATIONS = 4;
	

	public static final int BAD_ROTATION = -10;
	public static final int BAD_INDEX = -20;
	
	private static final int NUM_BYTES_IN_LONG = 64;
	private static final int NUM_LONGS_TO_USE = 3;
	
	public static int NUM_SIDE_BUMP_OPTIONS = 15;
	
	//Variables to compute at construction time:
	
	private int DIM_N_OF_Nx1x1;
	
	private long answerSheet[][][][][][];
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
	
		//TODO: Should prevLayerIndex index be layerIndex - 1?
		long tmp[] = answerSheet[prevLayerIndex[layerIndex - 1]][layerIndex][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		return ((curState[0] & tmp[0]) | (curState[1] & tmp[1]) | (curState[2] & tmp[2])) == 0L;
		
	}
	
	public void addNewLayerFast(int layerIndex, int sideBump) {
		long tmp[] = answerSheet[prevLayerIndex[layerIndex - 1]][layerIndex][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
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
		
		
	}
	
	public void removePrevLayerFast() {
		
		currentLayerIndex--;
		this.topLeftGroundedIndex = prevGroundedIndexes[currentLayerIndex]; 
		this.topLeftGroundRotationRelativeFlatMap = prevGroundedRotations[currentLayerIndex];
		int sideBumpToCancel  = prevSideBumps[currentLayerIndex];
		
		int layerbefore = prevLayerIndex[currentLayerIndex - 1];
		int layerIndexUsed = prevLayerIndex[currentLayerIndex];
		
		
		long tmp[] = answerSheet[layerbefore][layerIndexUsed][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBumpToCancel];
		curState[0] = curState[0] ^ tmp[0];
		curState[1] = curState[1] ^ tmp[1];
		curState[2] = curState[2] ^ tmp[2];
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
	
	private void setupAnswerSheetInBetweenLayersGoingBottomToTop() {
		
		answerSheet = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
		newGroundedRotationAbove = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		newGroundedIndexAbove = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		
		for(int layerStateBefore=0; layerStateBefore<NUM_LAYER_STATES; layerStateBefore++) {
			for(int layerStateAfter=0; layerStateAfter<NUM_LAYER_STATES; layerStateAfter++) {
				for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
					for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
						
						for(int sideBump=0; sideBump<NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
						
							boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
							
							int leftMostRelativeTopLeftGrounded = sideBump - 6;
							
							if( leftMostRelativeTopLeftGrounded < -3 || leftMostRelativeTopLeftGrounded > 3) {
								
								answerSheet[0][0][index][rotation][sideBump] = setImpossibleForAnswerSheet();
								newGroundedIndexAbove[0][0][index][rotation][sideBump] = BAD_INDEX;
								newGroundedRotationAbove[0][0][index][rotation][sideBump] = BAD_ROTATION;						
								continue;
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
							
							answerSheet[0][0][index][rotation][sideBump] = convertBoolArrayToLongs(tmpArray);
							
							newGroundedIndexAbove[0][0][index][rotation][sideBump] = nextGounded.i;
							newGroundedRotationAbove[0][0][index][rotation][sideBump] = nextGounded.j;
						}
					}
				}
			}
		}
		
	}
	private void setupAnswerSheetInBetweenLayersGoingTopToBottom() {
	
		//TODO!
	}

	public void setupAnswerSheetForTopCell() {
		
		answerSheetForTopCell = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
		answerSheetForTopCellAnySideBump = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_LONGS_TO_USE];
		
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
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}


}
