package semiGrained.iteration3x3;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.DataModelViews;
import Model.NeighbourGraphCreator;
import Model.Utils;
import NewModelWithIntersection.topAndBottomTransitionList.TopAndBottomTransitionHandler;

public class CuboidToFoldOnSemiGrained3x3  implements CuboidToFoldOnInterface {

	
	private CoordWithRotationAndIndex[][] neighbours;
	
	public int dimensions[] = new int[3];
	private boolean setup;

	public CuboidToFoldOnSemiGrained3x3(int a, int b, int c) {
		this(a, b, c, true, true);
	}


	public CuboidToFoldOnSemiGrained3x3(int a, int b, int c, boolean verbose, boolean setup) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c, verbose);
		
		this.setup = setup;
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;

		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		
		numLongsToUse = (int) Math.floor(Utils.getTotalArea(this.dimensions) / 64) + 1;
		System.out.println("Total area of CuboidToFoldOnSemiGrained: " + Utils.getTotalArea(this.dimensions));
		System.out.println("Num longs to use: " + numLongsToUse);
		
		curState = new long[numLongsToUse];
		
				
		if(setup) {
			setupAnswerSheetInBetweenLayers();
			setupAnswerSheetForTopCell();
		}
		
		if(dimensions[1] % 4 != 3 || dimensions[2] != 3) {
			System.out.println("ERROR: For now, the semi-grained cuboids must be the form: hx(3+4m)x3");
			System.exit(1);
		}
		

		if(setup) {
			this.topAndBottomHandler = new TopAndBottomTransitionHandler();
		
			forcedRepetition = new int[DIM_N_OF_Nx1x1 + 2];
			initializeForcedRepetition();
		}
		
		debugTopShiftIndex = new int[DIM_N_OF_Nx1x1 + 1];
	}
	
	private TopAndBottomTransitionHandler topAndBottomHandler = new TopAndBottomTransitionHandler();
	
	
	public int getNumCellsToFill() {
		return Utils.getTotalArea(this.dimensions);
	}
	
	public CoordWithRotationAndIndex[] getNeighbours(int cellIndex) {
		return neighbours[cellIndex];
	}
	

	@Override
	public int[] getDimensions() {
		return dimensions;
	}

	public void initializeNewBottomIndexAndRotation(int bottomIndex, int bottomRotationRelativeFlatMap) {
		
		
		this.bottomIndex = bottomIndex;
		this.topLeftGroundedIndex = bottomIndex;
		this.topLeftGroundRotationRelativeFlatMap = bottomRotationRelativeFlatMap;

		prevSideBumps = new int[DIM_N_OF_Nx1x1];
		prevGroundedIndexes = new int[DIM_N_OF_Nx1x1];
		prevGroundedRotations = new int[DIM_N_OF_Nx1x1];
		
		currentLayerIndex = 0;
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		tmpArray[bottomIndex] = true;
		this.curState = convertBoolArrayToLongs(tmpArray);
		
		//Specify 1st ring:
		if(getIndexToRingIndex(this.getBottomIndex()) != 0) {
			this.curState = setImpossibleForAnswerSheet();
		}
		
		
	}
	
	//Constants:

	//7 *2 -1
	public static final int NUM_POSSIBLE_SIDE_BUMPS = 13;
	
	public static final int NUM_NEIGHBOURS = 4;
	public static final int NUM_ROTATIONS = 4;
	

	public static final int BAD_ROTATION = -10;
	public static final int BAD_INDEX = -20;
	
	private static final int NUM_BYTES_IN_LONG = 64;
	
	public static int NUM_SIDE_BUMP_OPTIONS = 15;
	
	//Variables to compute at construction time:
	
	private int DIM_N_OF_Nx1x1;
	
	private long answerSheet[][][][];
	private int newGroundedIndexAbove[][][];
	private int newGroundedRotationAbove[][][];
	
	private long answerSheetForTopCell[][][][];
	private long answerSheetForTopCellAnySideBump[][][];


	//State variables:
	private static int numLongsToUse;
	private long curState[];

	private int topLeftGroundedIndex = 0;
	private int topLeftGroundRotationRelativeFlatMap = 0;
	
	public int prevSideBumps[];
	public int prevGroundedIndexes[];
	private int prevGroundedRotations[];
	
	//TODO: I made an exception, but I better not mess it up (Ideally, it should be a getter function0
	public int currentLayerIndex;
	private int forcedRepetition[];
	
	private long debugThru = 0L;
	private long debugStop = 0L;
	private long debugBugFix = 0L;
	
	//input: index
	private int indexToRing[];
	
	public int[] getIndexToRing() {
		
		//Hardcode just in case someone want to change it outside of the class:
		int ret[] = new int[indexToRing.length];
		for(int i=0; i<ret.length; i++) {
			ret[i] = indexToRing[i];
		}
		return ret;
	}

	//check if ring is decided: (depth)
	private int LayerIndexForRingDecided[];
	private int transitionBetweenRings[];
	//The ring mod 4 to use
	private int ringMod4AlreadySet[];
	
	//input: index and then rotation
	private int ringMod4Lookup[][];
	
	private int bottomIndex;
	

	public boolean isCellIoccupied(int i) {
		int indexArray = i / NUM_BYTES_IN_LONG;
		int bitShift = (NUM_BYTES_IN_LONG - 1) - i - indexArray * NUM_BYTES_IN_LONG;
		
		return ((1L << bitShift) & this.curState[indexArray]) != 0L;
	}
	
	
	public static int getAltNextRingIndexForHeight(int currentLayerIndex, int height) {
		
		return getAltCurRingIndexForHeight(currentLayerIndex + 1, height);
	}

	public static int getAltCurRingIndexForHeight(int currentLayerIndex, int height) {
		
		int layeringPerim = 2*(height + 3);
		
		int prevRingIndexAlt = ((currentLayerIndex-1 + layeringPerim) % (layeringPerim));
		
		if(prevRingIndexAlt > height + 2) {
			prevRingIndexAlt = layeringPerim - 6 - prevRingIndexAlt;
			
			if(prevRingIndexAlt < 0) {
				prevRingIndexAlt = -1;
			}
		
		} else if(prevRingIndexAlt >= height && prevRingIndexAlt <= height + 2) {
			prevRingIndexAlt = -1;
		}
		return prevRingIndexAlt;
	}
	
	//TODO Nov 28: try to avoid using this function while dealing with multiple semi-grained cuboids
	//
	public boolean isNewLayerValidForOtherMinNxMx1(int m, int sideBump) {
		
		//TODO: also add the 17 transition:
		
		//int ratio = (dimensions[1] + 1) / (m + 1);
		//int altHeight = ratio * (dimensions[0] + 1) - 1; 
		
		//General formula based on last 2 lines:
		int altHeight = ((dimensions[1] + 3) * (dimensions[0] + 3)) / (m + 3) - 3;
		
		int nextRingIndexAlt = getAltNextRingIndexForHeight(this.currentLayerIndex, altHeight);
		int prevRingIndexAlt = getAltCurRingIndexForHeight(this.currentLayerIndex, altHeight);
		
		int transitionIndex = Math.min(nextRingIndexAlt, prevRingIndexAlt);
		
		
		
		if(this.currentLayerIndex > altHeight
				&& transitionIndex != -1) {
			//I'm confused...
			
			//TODO: Nov 28:
			// There should be a condition involving the other constraint (even if it's not really used)
			
			//System.out.println(this.currentLayerIndex + " vs " + (1 + transitionIndex));
			if(this.prevSideBumps[ 1 + transitionIndex] != sideBump) {
				return false;
			}
		}
		
		return true;
	}
	
	public void printTopAndBottomHandlerDebug() {
		topAndBottomHandler.debugPrintTransitionLists();
	}
	
	public boolean isNewLayerValidSimpleFast(int sideBump) {
	
		long tmp[] = answerSheet[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		if(newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump] < 0) {
			return false;
		}

		int nextIndex = newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		int nextRot = newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		
		long collisionNumber = 0;
		for(int i=0; i<curState.length; i++) {
			collisionNumber |= curState[i] & tmp[i];
		}
		
		if(collisionNumber == 0L) {
			//pass
		} else {
			return false;
		}

		int prevRingIndex = indexToRing[this.topLeftGroundedIndex];
		int nextRingIndex = indexToRing[nextIndex];
		
		// Make sure the bottom index is on the right location of the grained ring (mod 4)
		if(nextRingIndex >=1 && nextRingIndex < dimensions[0] - 1
				&& LayerIndexForRingDecided[nextRingIndex] >= 0
				&& LayerIndexForRingDecided[nextRingIndex] < currentLayerIndex
				&& ringMod4AlreadySet[nextRingIndex] >=0
				&& ringMod4Lookup[nextIndex][nextRot] != ringMod4AlreadySet[nextRingIndex]) {
			
			return false;
		}

		/*
		// TODO: what does this even do?
		//if(getRingMod4(nextIndex, nextRot) == -1 
		//		&& ! isAcceptableTopOrBottomIndexForInbetweenLayer(nextIndex, nextRot)) {
		//	return false;
		//}
		*/
		
		if(forcedRepetition[this.currentLayerIndex] < this.currentLayerIndex
				&& sideBump != prevSideBumps[forcedRepetition[this.currentLayerIndex]]) {
			return false;
		}

		//TODO: Nov 28: change this to isGrainedRingToNonGrainedRingPossiblyFine
		/*
		if( ! topAndBottomHandler.isTopBottomTranstionsPossiblyFine(
				currentLayerIndex,
				dimensions,
				neighbours,
				new Coord2D(this.topLeftGroundedIndex, this.topLeftGroundRotationRelativeFlatMap),
				new Coord2D(newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump],
						    newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump]
				),
				indexToRing
			)) {
			
			return false;
		}
		*/
		
		//TODO: Nov 28: Add: is layer onTopOrBottom Possibly fine (Only works for (4M+3) where M> 0 for the bottom part...
		//Condition: rotation 0 or 2, and correct placement mod 4 depending on where bottom 1x1 and top 1x1 cell is.
		// For Top: leave 2 possibilities until it's been 'decided'
		
		//TODO: elsewhere...
		//TODO:  Nov 28:Add region check on the between layers on top and top, top + 1st ring.
		//TODO:  Nov 28:Make any layer touching the 2nd ring or 2nd last ring illegal.
		
		

		/*int topBottomShiftIndexLeftMost[][];
		int topBottomShiftMod4[][];
		
		int topBottomShiftSetDepth[];
		int topBottomShiftMod4FromPrevRound[];*/
		//nextIndex
		//nextRot
		
		int prev2RingIndex = -1;
		if(currentLayerIndex > 1) {
			prev2RingIndex = indexToRing[prevGroundedIndexes[currentLayerIndex - 1]];
		}
		//System.out.println(prev2RingIndex + " -> " + prevRingIndex);
		
	
		return true;
		
	}
	
	public static int debugFalseIndex = -1;
	public static int debugFalseCuboidIndex = -1; //TODO
	public static int debugFalseCuboidRot = -1; //TODO
	public static int debugShiftType = -1; //TODO
    public static Coord2D debugRing0ToMinus1_1 = null;
    public static Coord2D debugRing0ToMinus1_2 = null;
	
	public static int debugTopShiftIndex[];
	
	
	public void addNewLayerFast(int sideBump) {
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		for(int i=0; i<curState.length; i++) {
			curState[i] = curState[i] | tmp[i];
		}
		
		int tmp1 = newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		int tmp2 = newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		prevGroundedIndexes[currentLayerIndex] = this.topLeftGroundedIndex;
		prevGroundedRotations[currentLayerIndex] = this.topLeftGroundRotationRelativeFlatMap;
		prevSideBumps[currentLayerIndex] = sideBump;
		currentLayerIndex++;
		
		int transitionIndex = Math.min(indexToRing[tmp1], indexToRing[this.topLeftGroundedIndex]);
		
		this.topLeftGroundedIndex = tmp1;
		this.topLeftGroundRotationRelativeFlatMap = tmp2;
		
		if(indexToRing[this.topLeftGroundedIndex] >= 0
				&& LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] == -1) {
			
			LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] = currentLayerIndex;
			ringMod4AlreadySet[indexToRing[this.topLeftGroundedIndex]] = ringMod4Lookup[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap];
			
			if(transitionIndex != -1) {
				if(transitionBetweenRings.length <= transitionIndex) {
					System.out.println("WARNING: something weird with the transition index.");
				} else {
					transitionBetweenRings[transitionIndex] = sideBump;
				}
			}
		}
		
		
		
		

	}
	
	public void removePrevLayerFast() {

		
		if(debugFalseIndex == currentLayerIndex) {
			debugFalseIndex = -1;
		}
		
		if(indexToRing[this.topLeftGroundedIndex] >= 0
				&& LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] == this.currentLayerIndex) {
			LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] = -1;
		}
		
		
		
		currentLayerIndex--;
		this.topLeftGroundedIndex = prevGroundedIndexes[currentLayerIndex]; 
		this.topLeftGroundRotationRelativeFlatMap = prevGroundedRotations[currentLayerIndex];
		int sideBumpToCancel  = prevSideBumps[currentLayerIndex];
		
		
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBumpToCancel];

		for(int i=0; i<curState.length; i++) {
			curState[i] = curState[i] ^ tmp[i];
		}
		
		
		
		
	}
	
	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedFast() {
		
		long tmp[] = answerSheetForTopCellAnySideBump[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap];
		
		long collisionNumber = 0;

		for(int i=0; i<curState.length; i++) {
			collisionNumber |= ~curState[i] & tmp[i];
		}
		
		return collisionNumber != 0;
	}

	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedForSideBumpFast(int sideBump) {
		long tmp[] = answerSheetForTopCell[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		long collisionNumber = 0;

		for(int i=0; i<curState.length; i++) {
			collisionNumber |= ~curState[i] & tmp[i];
		}
		
		return collisionNumber != 0;
	}
	
	int ROTATION_AGAINST_GRAIN = 1;
	
	private void setupAnswerSheetInBetweenLayers() {
		
		
		indexToRing = new int[getNumCellsToFill()];
		
		for(int i=0; i<indexToRing.length; i++) {
			indexToRing[i] = getIndexToRingIndex(i);
			
			System.out.println("Cell " + i + ": " + indexToRing[i]);
		}

		answerSheet = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][numLongsToUse];
		newGroundedRotationAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		newGroundedIndexAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		
		
		for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
				
				for(int sideBump=0; sideBump<NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
				
					boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
					
					int leftMostRelativeTopLeftGrounded = sideBump - 6;
					
					if( leftMostRelativeTopLeftGrounded < -3 || leftMostRelativeTopLeftGrounded > 3) {
						
						answerSheet[index][rotation][sideBump] = setImpossibleForAnswerSheet();
						newGroundedIndexAbove[index][rotation][sideBump] = BAD_INDEX;
						newGroundedRotationAbove[index][rotation][sideBump] = BAD_ROTATION;						
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
					
					
					if( nextGounded.j % 2 == ROTATION_AGAINST_GRAIN && isWithinGrainedRing(nextGounded.i)) {
						
						answerSheet[index][rotation][sideBump] = setImpossibleForAnswerSheet();
						newGroundedIndexAbove[index][rotation][sideBump] = BAD_INDEX;
						newGroundedRotationAbove[index][rotation][sideBump] = BAD_ROTATION;		
						//System.out.println("REJECT " + index + ", " + rotation + "," +sideBump);				
						continue;
					
					} else if( ! isWithinGrainedRing(nextGounded.i)
							&& ! isAcceptableLayerOnTopOrBottomGrainedCuboid(nextGounded.i, nextGounded.j)) {
						answerSheet[index][rotation][sideBump] = setImpossibleForAnswerSheet();
						newGroundedIndexAbove[index][rotation][sideBump] = BAD_INDEX;
						newGroundedRotationAbove[index][rotation][sideBump] = BAD_ROTATION;
						//System.out.println("TEST " + index + ", " + rotation + "," +sideBump);
						continue;
					} else if ( ! isIndexRotationLegalSemiGrained(nextGounded.i, nextGounded.j)) {
						answerSheet[index][rotation][sideBump] = setImpossibleForAnswerSheet();
						newGroundedIndexAbove[index][rotation][sideBump] = BAD_INDEX;
						newGroundedRotationAbove[index][rotation][sideBump] = BAD_ROTATION;
						//System.out.println("TEST " + index + ", " + rotation + "," +sideBump);
						continue;
						
					} else if(isSideBumpTooBigBothTopBottom(index, rotation, sideBump, nextGounded)) {
						answerSheet[index][rotation][sideBump] = setImpossibleForAnswerSheet();
						newGroundedIndexAbove[index][rotation][sideBump] = BAD_INDEX;
						newGroundedRotationAbove[index][rotation][sideBump] = BAD_ROTATION;
						//System.out.println("TEST " + index + ", " + rotation + "," +sideBump);
						//System.out.println("Side bump too big");
						continue;
					}
					
					
					answerSheet[index][rotation][sideBump] = convertBoolArrayToLongs(tmpArray);
					
					newGroundedIndexAbove[index][rotation][sideBump] = nextGounded.i;
					newGroundedRotationAbove[index][rotation][sideBump] = nextGounded.j;
				}
			}
		}
		//System.exit(1);
		
		
		LayerIndexForRingDecided = new int[dimensions[0]];

		transitionBetweenRings = new int[dimensions[0] - 1];
		ringMod4AlreadySet = new int[dimensions[0]];
		
		for(int i=0; i<LayerIndexForRingDecided.length; i++) {
			LayerIndexForRingDecided[i] = -1;
			ringMod4AlreadySet[i] = 0;
		}

		ringMod4Lookup = new int[getNumCellsToFill()][NUM_ROTATIONS];
		for(int indexCell=0; indexCell<ringMod4Lookup.length; indexCell++) {
			for(int rotation=0; rotation<ringMod4Lookup[0].length; rotation++) {
				ringMod4Lookup[indexCell][rotation] = getRingMod4(indexCell, rotation);
				
				if(ringMod4Lookup[indexCell][rotation] != -1) {
					//System.out.println("Cell " + indexCell + " and rotation " + rotation + ": " + ringMod4Lookup[indexCell][rotation]);
				}
			}
			//System.out.println();
		}
		
		System.out.println("???");
		
		System.out.println("locations:");
		
		
		
		topBottomShiftSetDepth = new int[this.getNumCellsToFill()];
		topBottomShiftMod4FromPrevRound = new int[this.getNumCellsToFill()];
		
		for(int i=0; i<topBottomShiftSetDepth.length; i++) {
			topBottomShiftSetDepth[i] = -1;
			topBottomShiftMod4FromPrevRound[i] = -1;
		}
		
		
		
	}
	int topBottomShiftIndexLeftMost[][];
	int topBottomShiftMod4[][];
	
	int topBottomShiftSetDepth[];
	int topBottomShiftMod4FromPrevRound[];
	
	
	
	
	//Pre: it's not the first 1x1 cell or the last 1x1 cell
	//pre: getRingMod4(indexCell, rotation) returns -1:
	public boolean isAcceptableLayerOnTopOrBottomGrainedCuboid(int indexCell, int rotation) {
		
		Coord2D neighbour = this.tryAttachCellInDir(indexCell, rotation, RIGHT);
		
		for(int i=0; i<4 - 1; i++) {
			if(isWithinGrainedRing(neighbour.i)) {
				return false;
			}
			neighbour = this.tryAttachCellInDir(neighbour.i, neighbour.j, RIGHT);
		}
		
		return true;
	}
	
	private boolean isWithinGrainedRing(int indexCell) {
		return indexToRing[indexCell] >0 && indexToRing[indexCell] < dimensions[0] - 1;
	}
	
	
	private int getRingMod4(int indexCell, int rotation) {
		
		int numCellsTop = dimensions[1]*dimensions[2];
		
		if(indexCell < numCellsTop || indexCell >= this.getNumCellsToFill() - numCellsTop) {
			return -1;
		}
		
		if(rotation % 2 == 1) {
			return -1;
		}
		//Rotation 0
		
		int ret = 0;
		while(tryAttachCellInDir(indexCell, 0, LEFT).i < indexCell) {
			if(tryAttachCellInDir(indexCell, 0, LEFT).j != 0) {
				System.out.println("ERROR in getRingMod4: " + indexCell);
				System.exit(1);
			}
			indexCell = tryAttachCellInDir(indexCell, 0, LEFT).i;
			ret++;
		}

		//Rotation 2 means top left will be 1 to the left than if it's rotation 0:
		if(rotation == 2) {
			ret = ret + 1;
		}
		ret = ret % 4;
		
		if(ret < 0 || ret >=4) {
			System.out.println("Doh! getRingMod4 is wrong!");
			System.exit(1);
		}
		
		return ret;
	}
	
	public int getIndexToRingIndex(int indexCell) {
		int ret = -1;
		
		while(indexCell >= dimensions[1]*dimensions[2]) {
			ret++;
			
			indexCell = tryAttachCellInDir(indexCell, 0, ABOVE).i;
		}
		
		if(ret >= dimensions[0]) {
			return -1;
		} else {
			return ret;
		}
	}
	
	
	private long[] getPossiblyEmptyCellsAroundNewLayer(boolean newLayerArray[], int prevGroundIndex, int prevGroundRotation) {
		
		boolean tmpArray[] = new boolean[newLayerArray.length];
		
		//Get the bool array with the new layer indexes true:
		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = newLayerArray[i];
		}
		
		
		//Set the prev layer's indexes to true:
		Coord2D cur = new Coord2D(prevGroundIndex, prevGroundRotation);
		
		for(int i=0; i<NUM_ROTATIONS; i++) {
			tmpArray[cur.i] = true;
			cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
		}
		
		

		//Set output to the cells around the new layer that aren't the new layer and aren't the old layer:
		// This assumes both layers are type 0. (4 cells in a row)
		boolean output[] = new boolean[newLayerArray.length];

		for(int i=0; i<tmpArray.length; i++) {
			output[i] = false;
		}
		
		for(int i=0; i<newLayerArray.length; i++) {
			if(newLayerArray[i]) {
				
				for(int dir=0; dir<NUM_ROTATIONS; dir++) {
					
					cur = tryAttachCellInDir(i, 0, dir);
					
					if(tmpArray[cur.i] == false) {
						output[cur.i] = true;
					}
					
					//cells touching corner to corner are also around new layer:
					for(int dir2=0; dir2<NUM_ROTATIONS; dir2++) {
						
						if(dir2 % 2 == dir % 2) {
							continue;
						}
						Coord2D cur2 = tryAttachCellInDir(cur.i, cur.j, dir2);
						
						if(tmpArray[cur2.i] == false) {
							output[cur2.i] = true;
						}
						
						
					}
					
				}
			}
		}
		
		return convertBoolArrayToLongs(output);
	}
	

	public void setupAnswerSheetForTopCell() {
		
		answerSheetForTopCell = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][numLongsToUse];
		answerSheetForTopCellAnySideBump = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][numLongsToUse];
		
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

	//TODO: Why did you hard-code this?
	public static int[] getOtherWidthsToConsider() {
		//TODO: make this malleable:
		return new int[] {};
		
		//if((dimensions[1] + 3))
		//return new int[] {3};
	}
	
	private void initializeForcedRepetition() {

		for (int i = 0; i < forcedRepetition.length; i++) {
			forcedRepetition[i] = i;
		}

		boolean progress = true;

		int otherWidthsToConsider[] = getOtherWidthsToConsider();
		
		System.out.println("Other widths to consider:");
		for(int i=0; i<otherWidthsToConsider.length; i++) {
			System.out.println(otherWidthsToConsider[i]);
		}

		System.out.println("Starting initializeForcedRepetition()");
		while (progress == true) {

			progress = false;

			for (int i = 0; i < otherWidthsToConsider.length; i++) {

				if (((dimensions[1] + 3) * (dimensions[0] + 3)) % (otherWidthsToConsider[i] + 3) != 0) {
					System.out.println("ERROR in initializeForcedRepetition: unexpected forced width of "
							+ otherWidthsToConsider[i]);
					System.exit(1);
				}
				int altHeight = ((dimensions[1] + 3) * (dimensions[0] + 3)) / (otherWidthsToConsider[i] + 3) - 3;
				System.out.println("Alt height: " + altHeight);

				for (int j = 0; j < forcedRepetition.length; j++) {

					int nextRingIndexAlt = getAltNextRingIndexForHeight(j, altHeight);
					int prevRingIndexAlt = getAltCurRingIndexForHeight(j, altHeight);

					int transitionIndex = Math.min(nextRingIndexAlt, prevRingIndexAlt);
					
					System.out.println("j, transitionIndex: (" + j + ", "+ transitionIndex + ")");
					System.out.println("nextRingIndexAlt, prevRingIndexAlt: (" + nextRingIndexAlt + ", "+ prevRingIndexAlt + ")");

					if (transitionIndex > 0
							&& transitionIndex < altHeight - 2
							&& forcedRepetition[j] != forcedRepetition[transitionIndex + 1]
					) {

						if (Math.abs(prevRingIndexAlt - nextRingIndexAlt) != 1) {
							System.out.println("ERROR in initializeForcedRepetition! 22");
							System.exit(1);
						}

						int loweredIndex = Math.min(forcedRepetition[transitionIndex + 1], forcedRepetition[j]);
						forcedRepetition[j] = loweredIndex;
						forcedRepetition[transitionIndex + 1] = loweredIndex;

						progress = true;
					}
				}

			}

			// Get alt heights...
			// getAltCurRingIndexForHeight(int currentLayerIndex, int height)
		}

		for (int i = 0; i < forcedRepetition.length; i++) {
			System.out.println("forcedRepetion[" + i + "] = " + forcedRepetition[i]);
		}

	}
	
	
	private long[] convertBoolArrayToLongs(boolean tmpArray[]) {
		
		//1st entry:
		long ret[] = new long[numLongsToUse];
		
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
		
		long ret[] = new long[numLongsToUse];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = -1L;
		}
		
		return ret;
	}
	
	private static long[] setImpossibleForTopAnswerSheet() {
		
		long ret[] = new long[numLongsToUse];
		
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
	
	private Coord2D topLeftIndexRotAfter180Flip1x4layer(int index, int rotation) {
		Coord2D flippedIndexAndRotation = new Coord2D(index, rotation);
		
		for(int j=0; j<4 - 1; j++) {
			flippedIndexAndRotation = tryAttachCellInDir(flippedIndexAndRotation.i, flippedIndexAndRotation.j, RIGHT);
		}
		
		int flipRotation = (flippedIndexAndRotation.j + NUM_ROTATIONS/2) % NUM_ROTATIONS;
		
		return new Coord2D(flippedIndexAndRotation.i, flipRotation);
	}
	
	private boolean isSideBumpTooBigBothTopBottom(int index, int rotation, int sideBump, Coord2D landingSpot) {
		
		Coord2D c = new Coord2D(index, rotation);
		
		boolean isBottomOrTop1 = false;
		
		for(int i=0; i<4; i++) {
			if(indexToRing[c.i] < 0) {
				isBottomOrTop1 = true;
			}
			c = tryAttachCellInDir(c.i, c.j, RIGHT);
		}

		boolean isBottomOrTop2 = false;
		c = new Coord2D(landingSpot.i, landingSpot.j);

		for(int i=0; i<4; i++) {
			if(indexToRing[c.i] < 0) {
				isBottomOrTop2 = true;
			}
			c = tryAttachCellInDir(c.i, c.j, RIGHT);
		}
		
		if(isBottomOrTop1 && isBottomOrTop2 && Math.abs(sideBump - 6) >= 2) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private boolean isIndexRotationLegalSemiGrained(int index, int rotation) {
		
		boolean hasGrainedRing = false;
		boolean hasNonGrainedRingCell = false;
		
		Coord2D c = new Coord2D(index, rotation);
		
		for(int i=0; i<4; i++) {
			if(isWithinGrainedRing(c.i)) {
				hasGrainedRing = true;
			} else {
				hasNonGrainedRingCell = true;
				if(indexToRing[c.i] == -1 && c.j % 2 == 1 && dimensions[1] > dimensions[2]) {
					return false;
				}
			}
			c = tryAttachCellInDir(c.i, c.j, RIGHT);
		}
		
		if(hasGrainedRing && hasNonGrainedRingCell) {
			return false;
		}
		
		if(hasNonGrainedRingCell && indexToRing[index] == -1) {
			//By inspection, the layers on top and bottom have to be 0 or 1 mod 4 to the right of the side of the cuboid:
			int numAwayFromSide = 1;
			
			c = new Coord2D(index, rotation);
			//System.out.println("Trying top/bottom filter: " + c.i + ", " + c.j);
			while(indexToRing[tryAttachCellInDir(c.i, c.j, LEFT).i] == -1) {
				c = tryAttachCellInDir(c.i, c.j, LEFT);
				numAwayFromSide++;
			}
			
			if(numAwayFromSide % 4 >= 2) {
				//System.out.println("Dec 8th: False for " + index + ", " + rotation);
				return false;
			}
		}
		
		
		return true;
		
	}

	//DEBUG PRINT STATE ON OTHER CUBOID:
	public void printCurrentStateOnOtherCuboidsFlatMap() {
		
		CuboidToFoldOnSemiGrained3x3 toPrint = new CuboidToFoldOnSemiGrained3x3(
				this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				false,
				false
				);
		
		toPrint.initializeNewBottomIndexAndRotation(
				this.prevGroundedIndexes[0],
				this.prevGroundedRotations[0]
				);
		
		String labels[] = new String[Utils.getTotalArea(toPrint.dimensions)];
		
		for(int i=0; i<labels.length; i++) {
			labels[i] = null;
		}
		
		//Set the bottom index:
		labels[this.prevGroundedIndexes[0]] = "Bo";
		
		
		//Set the grounded Mid indexes (do more later)
		for(int i=0; i<this.currentLayerIndex; i++) {
			

			String labelToUse = getLabel(i);
			
			
			if(i < this.currentLayerIndex - 1) {
				labels[this.prevGroundedIndexes[i + 1]] = labelToUse;
				

				Coord2D cur = new Coord2D(this.prevGroundedIndexes[i + 1], this.prevGroundedRotations[i + 1]);
				
				for(int j=0; j<4 - 1; j++) {
					cur = this.tryAttachCellInDir(cur.i, cur.j, RIGHT);
					labels[cur.i] = labelToUse;
					
				}
				
			} else {
				
				labels[this.topLeftGroundedIndex] = labelToUse;
				

				Coord2D cur = new Coord2D(this.topLeftGroundedIndex, this.topLeftGroundRotationRelativeFlatMap);
				
				for(int j=0; j<4 - 1; j++) {
					cur = this.tryAttachCellInDir(cur.i, cur.j, RIGHT);
					labels[cur.i] = labelToUse;
				}
			}
			
		}
		
		int numNullLabels = 0;
		int curTopIndex = -1;
		//Add the top:
		for(int i=0; i<labels.length; i++) {
			if(labels[i] == null) {
				numNullLabels++;
				curTopIndex = i;
			}
		}
		
		if(numNullLabels == 1) {
			labels[curTopIndex] = "To";
		}

		System.out.println(DataModelViews.getFlatNumberingView(this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				labels));
		

		/*System.out.println("Location in ring mod 4:");
		for(int i=0; i<this.currentLayerIndex; i++) {

			
			String labelToUse = getLabel(i);
			
			if(i < this.currentLayerIndex - 1) {
				System.out.println(labelToUse + ": " + (this.ringMod4Lookup[this.prevGroundedIndexes[i + 1]][this.prevGroundedRotations[i + 1]]) + " (" + this.prevGroundedIndexes[i + 1] + ", " + this.prevGroundedRotations[i + 1] + ")");

			} else {
				System.out.println(labelToUse + ": " + (this.ringMod4Lookup[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap]) + " (" + this.topLeftGroundedIndex + ", " + this.topLeftGroundRotationRelativeFlatMap + ")");

			}
		}*/
	}

	private String getLabel(int layerIndex) {

		char label = (char)( (layerIndex % 26) + 'A');
		
		String labelToUse = label + "" + label;
		if(layerIndex >= 26 ) {
			labelToUse = label + "" + (layerIndex/26);
		}
		
		if(layerIndex >= 26* 10) {
			labelToUse = label + "" + (char)( ((layerIndex-10) / 26) + 'a');
		}
		return labelToUse;
	}



	public int getBottomIndex() {
		return bottomIndex;
	}
	
	

	//END DEBUG PRINT STATE ON OTHER CUBOID:
}
