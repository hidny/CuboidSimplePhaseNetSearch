package semiGrained.iteration2;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.DataModelViews;
import Model.NeighbourGraphCreator;
import Model.Utils;
import NewModelWithIntersection.topAndBottomTransitionList.TopAndBottomTransitionHandler;
import semiGrained.iteration2.SetupAllowed1stAndLastRing2;

public class CuboidToFoldOnSemiGrained2  implements CuboidToFoldOnInterface {

	
	private CoordWithRotationAndIndex[][] neighbours;
	
	public int dimensions[] = new int[3];
	private boolean setup;

	public CuboidToFoldOnSemiGrained2(int a, int b, int c) {
		this(a, b, c, true, true);
	}


	public CuboidToFoldOnSemiGrained2(int a, int b, int c, boolean verbose, boolean setup) {

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
		debugBottomShiftIndex = new int[DIM_N_OF_Nx1x1 + 1];
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

	public void initializeNewBottomAndTopIndexAndRotation(int bottomIndex, int bottomRotationRelativeFlatMap, int topIndex) {
		
		
		this.topIndex = topIndex;
		
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
		
		if(this.setup) {
			setup1stAndLastRing.setupAllowedFirstAndLastRingIndexRotations1x4(bottomIndex);
			/*
			labelDebugTopBottomShiftLocation();
			labelDebugTopBottomShift(0);
			System.out.println();
			System.out.println();
			labelDebugTopBottomShift(2);
			
			
			labelDebugTopBottomShiftLeftMostIndex(0);
			System.out.println("Next");
			labelDebugTopBottomShiftLeftMostIndex(2);
			System.out.println("Done");
			System.exit(1);
			*/
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
	private int topIndex;
	
	public int getTopIndexAssumed() {
		return topIndex;
	}

	public SetupAllowed1stAndLastRing2 setup1stAndLastRing;

	
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
		
		//Check topBottomShiftMod4:
		if(topBottomShiftMod4[nextIndex][nextRot] >= 0) {
			
			int indexTopBottomShiftToUse = topBottomShiftIndexLeftMost[nextIndex][nextRot];
			
			if(//It's worth matching:
					topBottomShiftSetDepth[indexTopBottomShiftToUse] != -1
					&& topBottomShiftSetDepth[indexTopBottomShiftToUse] < currentLayerIndex
				
				//It doesn't matches:
					&& topBottomShiftMod4FromPrevRound[indexTopBottomShiftToUse] != topBottomShiftMod4[nextIndex][nextRot]
				) {
				
				return false;
			}
		}
		//End check topBottomShiftMod4:
		
		int prev2RingIndex = -1;
		if(currentLayerIndex > 1) {
			prev2RingIndex = indexToRing[prevGroundedIndexes[currentLayerIndex - 1]];
		}
		//System.out.println(prev2RingIndex + " -> " + prevRingIndex);
		
		if(prev2RingIndex == 1 && prevRingIndex == 0 && ! setup1stAndLastRing.areTopShiftIndexesAllSet(this)) {

			if(this.currentLayerIndex != 2*dimensions[0] + dimensions[2] - 1) {
				System.out.println("OOPS in areTopShiftIndexesAllSet.");
				System.exit(1);
			}
			setup1stAndLastRing.setupRing0AndTopTransitions(
					 	new Coord2D(getBottomIndex(), 2),
						new Coord2D(this.topLeftGroundedIndex, this.topLeftGroundRotationRelativeFlatMap),
						new Coord2D(nextIndex, nextRot),
						this,
						topBottomShiftIndexLeftMost);
			debugRing0ToMinus1_1 = new Coord2D(this.topLeftGroundedIndex, this.topLeftGroundRotationRelativeFlatMap);
			debugRing0ToMinus1_2 = new Coord2D(nextIndex, nextRot);
		
		}
		
		 if(prev2RingIndex == dimensions[0] - 2 && prevRingIndex == dimensions[0] - 1 && ! setup1stAndLastRing.areBottomShiftIndexesAllSet(this)) {
			
			setup1stAndLastRing.setupRingSecondLastAndRingLastTransitions(
					new Coord2D(this.topLeftGroundedIndex, this.topLeftGroundRotationRelativeFlatMap),
					new Coord2D(nextIndex, nextRot),
					this.topIndex);
		}
		
		//TODO: Make a last Ring index version of this...
		if(nextRingIndex == 0 && setup1stAndLastRing.areTopShiftIndexesAllSet(this)) {
			//printCurrentStateOnOtherCuboidsFlatMap();
			
			
			//System.out.println("Debug Top");
			
			//System.out.println("getTopShiftType: " + setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound));
			//System.exit(1);
			
			if(setup1stAndLastRing.allowedFirstRingIndexRotations1x1Clock
					[setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound)]
					[nextIndex]
					[nextRot] == false
				&&
				setup1stAndLastRing.allowedFirstRingIndexRotations1x1Counter
				[setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound)]
				[nextIndex]
				[nextRot] == false
					) {
				
				//System.out.println("false");
				return false;
				
				/*if(debugFalseIndex == -1) {
					debugFalseIndex = this.currentLayerIndex + 1;
					debugFalseCuboidIndex = nextIndex;
					debugFalseCuboidRot = nextRot;
					
				}*/
			}
			
			if(setup1stAndLastRing.allowedFirstRingIndexRotations1x1Locations
					[setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound)]
					[getBottomIndex()]
					== false) {

				return false;
				/*
				//DEBUG option: 
				if(debugFalseIndex == -1) {
					debugFalseIndex = this.currentLayerIndex + 1;
					debugFalseCuboidIndex = nextIndex;
					debugFalseCuboidRot = nextRot;
					
				}*/
			}
			
			//returns false for testing purposes:
			//return false;
		}
		

		if(setup1stAndLastRing.areTopShiftIndexesAllSet(this)
				&& ((nextRingIndex == 0 && prevRingIndex ==1) || (nextRingIndex == 1 && prevRingIndex == 0))
			) {
			if(setup1stAndLastRing.ring0ToRing1Transitions[setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound)][this.topLeftGroundedIndex] != nextIndex) {
				//System.out.println("Quick rejection!");
				return false;
				
			}
		} else if(setup1stAndLastRing.areBottomShiftIndexesAllSet(this)
				&& ((nextRingIndex == this.dimensions[0]-1 && prevRingIndex == this.dimensions[0]-2) || (nextRingIndex == this.dimensions[0]-2 && prevRingIndex == this.dimensions[0]-1))
			) {
			if(setup1stAndLastRing.ringSecondLastToLastRingTransitions[setup1stAndLastRing.getBottomShiftType(topBottomShiftMod4FromPrevRound)][this.topLeftGroundedIndex] != nextIndex) {
				//System.out.println("Quick rejection!");
				//return false;
				
				//System.out.println("New False");
				if(debugFalseIndex == -1) {
					debugFalseIndex = this.currentLayerIndex + 1;
					debugFalseCuboidIndex = nextIndex;
					debugFalseCuboidRot = nextRot;
					debugShiftType = setup1stAndLastRing.getBottomShiftType(topBottomShiftMod4FromPrevRound);
					
				}
				
			}
		}
		
		
		if(setup1stAndLastRing.areTopShiftIndexesAllSet(this)
				&&((isLayerCompletetelyOnRing0(this.topLeftGroundedIndex) && isLayerMostlyOnTop(nextIndex)) 
						||  (isLayerMostlyOnTop(this.topLeftGroundedIndex) && isLayerCompletetelyOnRing0(nextIndex))
					)
				) {
			
			if(setup1stAndLastRing.ring0ToTopTransitions[setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound)][this.topLeftGroundedIndex] != nextIndex) {

				return false;
				/*
					//Debug tool:
				//System.out.println("New False");
				if(debugFalseIndex == -1) {
					debugFalseIndex = this.currentLayerIndex + 1;
					debugFalseCuboidIndex = nextIndex;
					debugFalseCuboidRot = nextRot;
					
				}*/
			}
		}
		
		if(this.currentLayerIndex == 2*dimensions[0] + 2*dimensions[2] - 1) {
			
			//TODO
			//Check if the first time we go from ring 0 to top is good:
			int beforeTop = prevGroundedIndexes[currentLayerIndex - 3];
			int onTop = prevGroundedIndexes[currentLayerIndex - 2];
			
			if(setup1stAndLastRing.ring0ToTopTransitions[setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound)][beforeTop] != onTop) {
				System.out.println("Quick rejection! " + beforeTop + " to " + onTop);
				
				//Because of the way it's implemented, this doesn't actually do anything. Oh well.
				System.exit(1);
				
				return false;
				
				/*if(debugFalseIndex == -1) {
					debugFalseIndex = this.currentLayerIndex + 1;
					debugFalseCuboidIndex = nextIndex;
					debugFalseCuboidRot = nextRot;
					
				}*/
			}
		}
		
		debugTopShiftIndex[this.currentLayerIndex] = setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound);
		debugBottomShiftIndex[this.currentLayerIndex] = setup1stAndLastRing.getBottomShiftType(topBottomShiftMod4FromPrevRound);
		
		//TODO: (again) Make a last Ring index version of this...
		/*if(setup1stAndLastRing.areBottomShiftIndexesAllSet(this)) {
			printCurrentStateOnOtherCuboidsFlatMap();
			System.out.println("Debug bottom");
			System.out.println("getBottomShiftType: " + setup1stAndLastRing.getBottomShiftType(topBottomShiftMod4FromPrevRound));
		}*/
		
		return true;
		
	}
	
	public boolean isLayerCompletetelyOnRing0(int index) {
		return indexToRing[index] == 0 && !partOf1x4onTop(index);
	}

	public boolean isLayerMostlyOnTop(int index) {
		return indexToRing[index] == -1 || partOf1x4onTop(index);
	}
	
	public boolean partOf1x4onTop(int index) {
		if(setup1stAndLastRing.hitLastorRing0Barrier(
						setup1stAndLastRing.getTopShiftType(topBottomShiftMod4FromPrevRound),
						index,
						true)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int debugFalseIndex = -1;
	public static int debugFalseCuboidIndex = -1; //TODO
	public static int debugFalseCuboidRot = -1; //TODO
	public static int debugShiftType = -1; //TODO
    public static Coord2D debugRing0ToMinus1_1 = null;
    public static Coord2D debugRing0ToMinus1_2 = null;
	
	public static int debugTopShiftIndex[];
	public static int debugBottomShiftIndex[];
	
	
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
		
		if(currentLayerIndex == 1) {
			setup1stAndLastRing.setupRing1AndRing0Transitions(new Coord2D(getBottomIndex(), 2), new Coord2D(tmp1, tmp2));
		}
		

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
		
		//Set topBottomShiftMod4:
		if(topBottomShiftMod4[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap] >= 0) {
			
			int indexTopBottomShiftToUse = topBottomShiftIndexLeftMost[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap];
			
			if(//It's worth matching:
					topBottomShiftSetDepth[indexTopBottomShiftToUse] == -1
					|| topBottomShiftSetDepth[indexTopBottomShiftToUse] == currentLayerIndex
				) {
				
				topBottomShiftSetDepth[indexTopBottomShiftToUse] = currentLayerIndex;
				topBottomShiftMod4FromPrevRound[indexTopBottomShiftToUse] = topBottomShiftMod4[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap];
			}
		}
		//End set topBottomShiftMod4
		
		
		

	}
	
	public void removePrevLayerFast() {

		
		if(debugFalseIndex == currentLayerIndex) {
			debugFalseIndex = -1;
		}
		
		if(indexToRing[this.topLeftGroundedIndex] >= 0
				&& LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] == this.currentLayerIndex) {
			LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] = -1;
		}
		
		
		//Erase topBottomShiftMod4:
		if(topBottomShiftMod4[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap] >= 0) {
			
			int indexTopBottomShiftToUse = topBottomShiftIndexLeftMost[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap];
			
			if(topBottomShiftSetDepth[indexTopBottomShiftToUse] == currentLayerIndex
				) {
				topBottomShiftSetDepth[indexTopBottomShiftToUse] = -1;
				topBottomShiftMod4FromPrevRound[indexTopBottomShiftToUse] = -1;
			}
		}
		//End erase topBottomShiftMod4
		
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
		
		
		topBottomShiftIndexLeftMost = new int[this.getNumCellsToFill()][4];
		for(int i=0; i<topBottomShiftIndexLeftMost.length; i++) {
			for(int j=0; j<topBottomShiftIndexLeftMost[0].length; j++) {
				topBottomShiftIndexLeftMost[i][j] = getTopBottomShiftLeftMostIndex(i, j);
			}
		}//getTopBottomShiftMod4
		
		topBottomShiftMod4 = new int[this.getNumCellsToFill()][4];
		for(int i=0; i<topBottomShiftMod4.length; i++) {
			for(int j=0; j<topBottomShiftMod4[0].length; j++) {
				topBottomShiftMod4[i][j] = getTopBottomShiftMod4(i, j);
			}
		}
		
		topBottomShiftSetDepth = new int[this.getNumCellsToFill()];
		topBottomShiftMod4FromPrevRound = new int[this.getNumCellsToFill()];
		
		for(int i=0; i<topBottomShiftSetDepth.length; i++) {
			topBottomShiftSetDepth[i] = -1;
			topBottomShiftMod4FromPrevRound[i] = -1;
		}
		
		//Dec 18th:

		
		setup1stAndLastRing = new SetupAllowed1stAndLastRing2(
				neighbours,
				indexToRing,
				dimensions,
				topBottomShiftIndexLeftMost,
				this
		);
		
		
	}
	int topBottomShiftIndexLeftMost[][];
	int topBottomShiftMod4[][];
	
	int topBottomShiftSetDepth[];
	int topBottomShiftMod4FromPrevRound[];
	
	
	
	// copy/paste of getTopBottomShiftMod4, except we return the index of the left_location...
	public int getTopBottomShiftLeftMostIndex(int index, int rot) {

		Coord2D curCoord = new Coord2D(index, rot);
		
		int curLocation = getIndexRotToTopBottomShiftLocation(index, rot);
		
		if(curLocation == UNINTERESTING) {
			return -1;
		}
		
		//Copy/paste code because it's clearer:
		if(curLocation == LEFT_TOP_LOCATION || curLocation == TOP_LOCATION || curLocation == RIGHT_TOP_LOCATION) {
			
			if(curLocation == LEFT_TOP_LOCATION) {
				return curCoord.i;
			}
			
			if(curLocation == RIGHT_TOP_LOCATION) {
				for(int i=0; i<4; i++) {
					curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, RIGHT);
				}
			}
				
			if(curCoord.j == 2) {
				//Go around function...
				curCoord = topLeftIndexRotAfter180Flip1x4layer(curCoord.i, curCoord.j);
			}
			
			while(getIndexRotToTopBottomShiftLocation(curCoord.i, curCoord.j) != LEFT_TOP_LOCATION) {
				curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, LEFT);
			}
			
			return curCoord.i;
			
		} else if(curLocation == LEFT_BOTTOM_LOCATION || curLocation == BOTTOM_LOCATION || curLocation == RIGHT_BOTTOM_LOCATION) {
			
			if(curLocation == LEFT_BOTTOM_LOCATION) {
				return curCoord.i;
			}
			
			if(curLocation == RIGHT_BOTTOM_LOCATION) {
				for(int i=0; i<4; i++) {
					curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, RIGHT);
				}
			}
				
			if(curCoord.j == 2) {
				//Go around function...
				curCoord = topLeftIndexRotAfter180Flip1x4layer(curCoord.i, curCoord.j);
			}
			
			while(getIndexRotToTopBottomShiftLocation(curCoord.i, curCoord.j) != LEFT_BOTTOM_LOCATION) {
				curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, LEFT);
			}

			return curCoord.i;
		}
		
		return -1;
	}
	
	public int getTopBottomShiftMod4(int index, int rot) {
		
		Coord2D curCoord = new Coord2D(index, rot);
		
		int curLocation = getIndexRotToTopBottomShiftLocation(index, rot);
		
		if(curLocation == UNINTERESTING) {
			return -1;
		}
		
		//Copy/paste code because it's clearer:
		if(curLocation == LEFT_TOP_LOCATION || curLocation == TOP_LOCATION || curLocation == RIGHT_TOP_LOCATION) {
			
			if(curLocation == LEFT_TOP_LOCATION) {
				return 0;
			}
			
			if(curLocation == RIGHT_TOP_LOCATION) {
				for(int i=0; i<4; i++) {
					curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, RIGHT);
				}
			}
				
			if(curCoord.j == 2) {
				//Go around function...
				curCoord = topLeftIndexRotAfter180Flip1x4layer(curCoord.i, curCoord.j);
			}
			
			int ret = 0;
			while(getIndexRotToTopBottomShiftLocation(curCoord.i, curCoord.j) != LEFT_TOP_LOCATION) {
				curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, LEFT);
				ret++;
			}
			
			return ret % 4;
			
		} else if(curLocation == LEFT_BOTTOM_LOCATION || curLocation == BOTTOM_LOCATION || curLocation == RIGHT_BOTTOM_LOCATION) {
			
			if(curLocation == LEFT_BOTTOM_LOCATION) {
				return 0;
			}
			
			if(curLocation == RIGHT_BOTTOM_LOCATION) {
				for(int i=0; i<4; i++) {
					curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, RIGHT);
				}
			}
				
			if(curCoord.j == 2) {
				//Go around function...
				curCoord = topLeftIndexRotAfter180Flip1x4layer(curCoord.i, curCoord.j);
			}
			
			int ret = 0;
			while(getIndexRotToTopBottomShiftLocation(curCoord.i, curCoord.j) != LEFT_BOTTOM_LOCATION) {
				curCoord = tryAttachCellInDir(curCoord.i, curCoord.j, LEFT);
				ret++;
			}
			
			return ret % 4;
		}
			
		
		
		return -1;
	}
	
	public static int UNINTERESTING = -1;
	
	public static int LEFT_TOP_LOCATION = 1;
	public static int TOP_LOCATION = 2;
	public static int RIGHT_TOP_LOCATION = 3;
	
	public static int LEFT_BOTTOM_LOCATION = 5;
	public static int BOTTOM_LOCATION = 6;
	public static int RIGHT_BOTTOM_LOCATION = 7;
	
	//pre: indexToRing is defined
	public int getIndexRotToTopBottomShiftLocation(int index, int rot) {
		
		if(indexToRing[index] > 0 && indexToRing[index] < dimensions[0] - 1) {
			return -1;
		}
		
		Coord2D tmp = new Coord2D(index, rot);
		
		if(indexToRing[index] == -1 && rot % 2 == 0) {
			if(index < dimensions[1] * dimensions[2]) {
				return TOP_LOCATION;

			} else {
				return BOTTOM_LOCATION;
			}
		}
		
		if(indexToRing[index] == 0) {
			
			Coord2D afterRight = tryAttachCellInDir(tmp.i, tmp.j, RIGHT);
			if(indexToRing[afterRight.i] == -1 && afterRight.j == 0) {
				return LEFT_TOP_LOCATION;
			}
			
			
			if(indexToRing[afterRight.i] == -1 && afterRight.j == 2) {
				return RIGHT_TOP_LOCATION;
			}
		}
		
		if(indexToRing[index] == dimensions[0] - 1) {
			
			Coord2D afterRight = tryAttachCellInDir(tmp.i, tmp.j, RIGHT);
			if(indexToRing[afterRight.i] == -1 && afterRight.j == 0) {
				return LEFT_BOTTOM_LOCATION;
			}
			
			
			if(indexToRing[afterRight.i] == -1 && afterRight.j == 2) {
				return RIGHT_BOTTOM_LOCATION;
			}
		}
		
		//System.out.println("indexToRing[" + index + "]: " + indexToRing[index]);
		
		return -1;
	}
	
	
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

	public int[] getListOfPotentialTops() {
		int ret[] = new int[2*(dimensions[1] + dimensions[2])];
		
		int curIndex = 0;
		for(int i=0; i<this.getNumCellsToFill(); i++) {
			if(indexToRing[i] == dimensions[0] - 1) {
				ret[curIndex] = i;
				curIndex++;
			}
		}
		
		return ret;
	}
	
	//TODO: Why did you hard-code this?
	public static int[] getOtherWidthsToConsider() {
		//TODO: make this malleable:
		//return new int[] {};
		
		//if((dimensions[1] + 3))
		return new int[] {3};
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
	
	public boolean isCellIndexoccupied(int i) {
		int indexArray = i / NUM_BYTES_IN_LONG;
		int bitShift = (NUM_BYTES_IN_LONG - 1) - i - indexArray * NUM_BYTES_IN_LONG;
		
		return ((1L << bitShift) & this.curState[indexArray]) != 0L;
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
		
		CuboidToFoldOnSemiGrained2 toPrint = new CuboidToFoldOnSemiGrained2(
				this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				false,
				false
				);
		
		toPrint.initializeNewBottomAndTopIndexAndRotation(
				this.prevGroundedIndexes[0],
				this.prevGroundedRotations[0],
				-1
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

	private void labelDebugTopBottomShiftLocation() {
		
		
		String labels[] = new String[getNumCellsToFill()];
		for(int i=0; i<labels.length; i++) {
			String labelSoFar = "00";
			for(int j=0; j<4; j++) {
				if(getIndexRotToTopBottomShiftLocation(i, j) != UNINTERESTING) {
					
					boolean repeat = false;
					if(labelSoFar.equals("00") == false) {
						repeat = true;
					}
					String tmp = "" + getIndexRotToTopBottomShiftLocation(i, j) + "" + getIndexRotToTopBottomShiftLocation(i, j);
					if(repeat && tmp.equals(labelSoFar) == false) {
						System.out.println("Ooops!");
						System.exit(1);
					}
					
					labelSoFar = tmp;
				}
			}
			
			labels[i] = labelSoFar;
			
			
		}
		
		System.out.println(DataModelViews.getFlatNumberingView(this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				labels));
		
	}
	
	private void labelDebugTopBottomShift(int rotation) {
		
		System.out.println("Debug rotation: " + rotation);
		
		String labels[] = new String[getNumCellsToFill()];
		for(int i=0; i<labels.length; i++) {
			String labelSoFar = "00";
			
			for(int rot=0; rot<4; rot++) {
				
				if(rot != rotation && (getIndexRotToTopBottomShiftLocation(i, rot) == TOP_LOCATION 
								|| getIndexRotToTopBottomShiftLocation(i, rot) == BOTTOM_LOCATION )) {
					continue;
				}
				if(topBottomShiftMod4[i][rot] >=0) {
					
					boolean repeat = false;
					if(labelSoFar.equals("00") == false) {
						repeat = true;
					}
					String tmp = "" + topBottomShiftMod4[i][rot] + "" + topBottomShiftMod4[i][rot];
					
					if(repeat && tmp.equals(labelSoFar) == false) {
						System.out.println("Ooops!");
						System.exit(1);
					}
					
					labelSoFar = tmp;
				}
				

			}
			labels[i] = labelSoFar;
			
			
		}
		
		System.out.println(DataModelViews.getFlatNumberingView(this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				labels));
		
	}
	
	private void labelDebugTopBottomShiftLeftMostIndex(int rotation) {
		
		System.out.println("Debug rotation: " + rotation);
		
		String labels[] = new String[getNumCellsToFill()];
		for(int i=0; i<labels.length; i++) {
			String labelSoFar = "---";
			
			for(int rot=0; rot<4; rot++) {
				
				if(rot != rotation && (getIndexRotToTopBottomShiftLocation(i, rot) == TOP_LOCATION 
								|| getIndexRotToTopBottomShiftLocation(i, rot) == BOTTOM_LOCATION )) {
					continue;
				}
				
				if(topBottomShiftIndexLeftMost[i][rot] >=0) {
					
					String tmp = "" + topBottomShiftIndexLeftMost[i][rot];
					while(tmp.length() < 3) {
						tmp = "0" + tmp;
					}
					
					
					labelSoFar = tmp;
				}
				

			}
			labels[i] = labelSoFar;
			
			
		}
		
		System.out.println(DataModelViews.getFlatNumberingView(this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				labels));
		
	}


	public int getBottomIndex() {
		return bottomIndex;
	}
	
	

	//END DEBUG PRINT STATE ON OTHER CUBOID:
}
