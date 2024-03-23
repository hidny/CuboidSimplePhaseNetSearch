package NewModelWithIntersection.grainIteration;
import java.util.LinkedList;
import java.util.Queue;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.DataModelViews;
import Model.NeighbourGraphCreator;
import Model.Utils;
import NewModelWithIntersection.fastRegionCheck.FastRegionCheck;

public class CuboidToFoldOnGrained  implements CuboidToFoldOnInterface {

	
	private CoordWithRotationAndIndex[][] neighbours;
	
	public int dimensions[] = new int[3];

	private FastRegionCheck fastRegionCheck;

	public CuboidToFoldOnGrained(int a, int b, int c, FastRegionCheck fastRegionCheck) {
		this(a, b, c, true, true, fastRegionCheck);
	}


	public CuboidToFoldOnGrained(int a, int b, int c, boolean verbose, boolean setup, FastRegionCheck fastRegionCheck) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c, verbose);
		
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;

		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		
		numLongsToUse = (int) Math.floor(Utils.getTotalArea(this.dimensions) / 64) + 1;
		System.out.println(Utils.getTotalArea(this.dimensions));
		System.out.println("Num longs to use: " + numLongsToUse);
		
		curState = new long[numLongsToUse];
		
		//Hacky mechanism for not recalculating FastRegionCheck every time we construct CuboidToFoldOnGrained:
		if(fastRegionCheck == null) {
			this.fastRegionCheck = new FastRegionCheck(neighbours, curState);
		} else {
			this.fastRegionCheck = fastRegionCheck;
		}
				
		if(setup) {
			setupAnswerSheetInBetweenLayers();
			setupAnswerSheetForTopCell();
		}
		
		if(dimensions[1] % 4 != 1 || dimensions[2] != 1) {
			System.out.println("ERROR: For now, the grained cuboids must be the form: mx(4m+1)x1");
			System.exit(1);
		}
	}
	
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
		
		forcedRepetition = new int[DIM_N_OF_Nx1x1 + 2];
		currentLayerIndex = 0;
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		tmpArray[bottomIndex] = true;
		this.curState = convertBoolArrayToLongs(tmpArray);
		

		this.minTopIndex = -1;
		this.maxTopIndex = Utils.getTotalArea(this.dimensions);

		this.oldTopMin = new int[Utils.getTotalArea(this.dimensions)];
		this.oldTopMax = new int[Utils.getTotalArea(this.dimensions)];
		
		//TODO: Specific to cuboids of the form Mx(4N+1)x1: (Make this not be used when dealing with Nx3x3)
		if(bottomIndex % 4 != 0 || bottomIndex >= this.dimensions[1]) {
			this.curState = setImpossibleForAnswerSheet();
		}
		
		initializeForcedRepetition();
	}
	
	
	private void initializeForcedRepetition() {
		
		for(int i=0; i<forcedRepetition.length; i++) {
			forcedRepetition[i] = i;
		}
		
		boolean progress = true;

		//TODO: make this a param?
		
		//Test 53
		//int otherWidthsToConsider[] = new int[] {5, 41, 209};
		//int otherWidthsToConsider[] = new int[] {41, 209};
		
		//Ryuhei's idea:
		//int otherWidthsToConsider[] = new int[] {5, 9, 13};
		
		//Test standard:
		int otherWidthsToConsider[] = new int[] {5, 17};
		
		System.out.println("Starting initializeForcedRepetition()");
		while(progress == true) {
			
			progress = false;
		
			for(int i=0; i<otherWidthsToConsider.length; i++) {
				
				if( ((dimensions[1] + 1) * (dimensions[0] + 1)) % (otherWidthsToConsider[i] + 1) != 0) {
					System.out.println("ERROR in initializeForcedRepetition: unexpected forced width of "+ otherWidthsToConsider[i]);
					System.exit(1);
				}
				int altHeight = ((dimensions[1] + 1) * (dimensions[0] + 1)) / (otherWidthsToConsider[i] + 1) - 1;
				
				for(int j=0; j<forcedRepetition.length; j++) {
					
					int nextRingIndexAlt = getAltNextRingIndexForHeight(j, altHeight);
					int prevRingIndexAlt = getAltCurRingIndexForHeight(j, altHeight);
					
					int transitionIndex = Math.min(nextRingIndexAlt, prevRingIndexAlt);
					
					if(transitionIndex != -1
							&& forcedRepetition[j] != forcedRepetition[transitionIndex + 1]
						) {
						
						if(Math.abs(prevRingIndexAlt - nextRingIndexAlt) != 1) {
							System.out.println("ERROR in initializeForcedRepetition!");
							System.exit(1);
						}
						
						int loweredIndex = Math.min(forcedRepetition[transitionIndex + 1], forcedRepetition[j]);
						forcedRepetition[j] = loweredIndex;
						forcedRepetition[transitionIndex + 1] = loweredIndex;
						
						progress = true;
					}
				}
				
			}
			
		//Get alt heights...
		//getAltCurRingIndexForHeight(int currentLayerIndex, int height)
		}
		
		for(int i=0; i<forcedRepetition.length; i++) {
			System.out.println("forcedRepetion[" + i + "] = " + forcedRepetition[i]);
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

	private long preComputedPossiblyEmptyCellsAroundNewLayer[][][][];
	private boolean preComputedForceRegionSplitIfEmptyAroundNewLayer[][][];


	//State variables:
	private static int numLongsToUse;
	private long curState[];

	private int topLeftGroundedIndex = 0;
	private int topLeftGroundRotationRelativeFlatMap = 0;
	
	private int prevSideBumps[];
	private int prevGroundedIndexes[];
	private int prevGroundedRotations[];
	private int currentLayerIndex;
	private int forcedRepetition[];
	
	private long debugThru = 0L;
	private long debugStop = 0L;
	private long debugBugFix = 0L;
	

	//
	//input: index
	private int indexToRing[];
	
	//check if ring is decided: (depth)
	private int LayerIndexForRingDecided[];
	private int transitionBetweenRings[];
	//The ring mod 4 to use
	private int ringMod4AlreadySet[];
	
	//input: index and then rotation
	private int ringMod4Lookup[][];
	
	
	//TODO:
	private int bottomIndex;
	
	private int minTopIndex;
	private int maxTopIndex;
	
	private int oldTopMin[];
	private int oldTopMax[];
	
	
	// Filter the cells around the new layer and turn that into number (use the grounded index and rotation for help)
	// then use a lookup-table to decide if the region split (use the lookup table associate with the grounded index and rotation for help)
	public boolean unoccupiedRegionSplit(long newLayerDetails[], int sideBump) {
		
		
		int tmp1 = newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		int tmp2 = newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		if(fastRegionCheck.regionSplit(curState, tmp1, tmp2)) {
			//System.out.println("test " + topLeftGroundedIndex + "," + topLeftGroundRotationRelativeFlatMap);
			//System.out.println("side bump: " + sideBump);
			//printCurrentStateOnOtherCuboidsFlatMap();
			//System.exit(1);
			return true;
		} else {
			return false;
		}
		
	}

	public boolean isCellIoccupied(int i) {
		int indexArray = i / NUM_BYTES_IN_LONG;
		int bitShift = (NUM_BYTES_IN_LONG - 1) - i - indexArray * NUM_BYTES_IN_LONG;
		
		return ((1L << bitShift) & this.curState[indexArray]) != 0L;
	}
	
	
	public static int getAltNextRingIndexForHeight(int currentLayerIndex, int height) {
		
		return getAltCurRingIndexForHeight(currentLayerIndex + 1, height);
	}

	public static int getAltCurRingIndexForHeight(int currentLayerIndex, int height) {
		
		int prevRingIndexAlt = (currentLayerIndex % (2*(height + 1))) - 1;
		
		if(prevRingIndexAlt > height) {
			prevRingIndexAlt = 2*height - prevRingIndexAlt;
		
		} else if(prevRingIndexAlt == height) {
			prevRingIndexAlt = -1;
		}
		return prevRingIndexAlt;
	}
	
	public boolean isNewLayerValidForOtherMinNxMx1(int m, int sideBump) {
		
		//TODO: also add the 17 transition:
		
		//int ratio = (dimensions[1] + 1) / (m + 1);
		//int altHeight = ratio * (dimensions[0] + 1) - 1; 
		
		//General formula based on last 2 lines:
		int altHeight = ((dimensions[1] + 1) * (dimensions[0] + 1)) / (m + 1) - 1;
		
		int nextRingIndexAlt = getAltNextRingIndexForHeight(this.currentLayerIndex, altHeight);
		int prevRingIndexAlt = getAltCurRingIndexForHeight(this.currentLayerIndex, altHeight);
		
		int transitionIndex = Math.min(nextRingIndexAlt, prevRingIndexAlt);
		
		
		
		if(this.currentLayerIndex > altHeight
				&& transitionIndex != -1) {
			//I'm confused...
			
			/*
			if((this.currentLayerIndex / this.dimensions[0]) % 2 == 1) {
				transitionIndex = transitionBetweenRings.length - 1 - (transitionIndex % this.dimensions[0]);
				System.out.println("Case 1");
			} else {
				System.out.println("Case 2: " + transitionIndex);
				transitionIndex = transitionIndex % this.dimensions[0];
			}
			
			if(transitionIndex == 1) {
				System.out.println("Debug");
			}
			*/
			
			//System.out.println(this.currentLayerIndex + " vs " + (1 + transitionIndex));
			if(this.prevSideBumps[ 1 + transitionIndex] != sideBump) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isNewLayerValidSimpleFast(int sideBump) {
	
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
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
		
		if(nextRingIndex >=0
				&& LayerIndexForRingDecided[nextRingIndex] >= 0 
				&& LayerIndexForRingDecided[nextRingIndex] < currentLayerIndex
				&& ringMod4AlreadySet[nextRingIndex] >=0
				&& ringMod4Lookup[nextIndex][nextRot] != ringMod4AlreadySet[nextRingIndex]) {
			
			return false;
		}
		
		if(getRingMod4(nextIndex, nextRot) == -1 
				&& ! isAcceptableTopOrBottomIndexForInbetweenLayer(nextIndex, nextRot)) {
			return false;
		}
		
		//TODO:
		//Improved spec:
		if(forcedRepetition[this.currentLayerIndex] < this.currentLayerIndex
				&& sideBump != prevSideBumps[forcedRepetition[this.currentLayerIndex]]) {
			return false;
		}
		//END TODO
		return ! unoccupiedRegionSplit(tmp, sideBump);
		
	}
	
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
				
				transitionBetweenRings[transitionIndex] = sideBump;
			}
		}
		
		this.updateMinMaxTopIndexIfApplicable(this.topLeftGroundedIndex, this.topLeftGroundRotationRelativeFlatMap, this.currentLayerIndex);
		
		if(this.minTopIndex > this.maxTopIndex) {
			System.out.println("ERROR: DOH! this.minTopIndex > this.maxTopIndex");
			System.exit(1);
		}

	}
	
	public void removePrevLayerFast() {

		if(indexToRing[this.topLeftGroundedIndex] >= 0
				&& LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] == this.currentLayerIndex) {
			LayerIndexForRingDecided[indexToRing[this.topLeftGroundedIndex]] = -1;
		}
		if(this.minTopIndex == this.topLeftGroundedIndex) {
			this.minTopIndex = this.oldTopMin[currentLayerIndex];
		} else if(this.maxTopIndex == this.topLeftGroundedIndex) {
			this.maxTopIndex = this.oldTopMax[currentLayerIndex];
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
		
		answerSheet = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][numLongsToUse];
		newGroundedRotationAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		newGroundedIndexAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		
		preComputedPossiblyEmptyCellsAroundNewLayer = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][numLongsToUse];
		preComputedForceRegionSplitIfEmptyAroundNewLayer = new boolean[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];

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
					
					if( nextGounded.j % 2 == ROTATION_AGAINST_GRAIN) {
						
						answerSheet[index][rotation][sideBump] = setImpossibleForAnswerSheet();
						newGroundedIndexAbove[index][rotation][sideBump] = BAD_INDEX;
						newGroundedRotationAbove[index][rotation][sideBump] = BAD_ROTATION;						
						continue;
					}
					
					
					answerSheet[index][rotation][sideBump] = convertBoolArrayToLongs(tmpArray);
					preComputedPossiblyEmptyCellsAroundNewLayer[index][rotation][sideBump]  = getPossiblyEmptyCellsAroundNewLayer(tmpArray, index, rotation);
					preComputedForceRegionSplitIfEmptyAroundNewLayer[index][rotation][sideBump]  = checkPreComputedForceRegionSplitIfEmptyAroundNewLayer(tmpArray, index, rotation);
					
					newGroundedIndexAbove[index][rotation][sideBump] = nextGounded.i;
					newGroundedRotationAbove[index][rotation][sideBump] = nextGounded.j;
				}
			}
		}
		
		
		//TODO: complete this
		//System.out.println("Grain dimension is just the second one: " + dimensions[grainDim]);
		
		//int numTubes
		
		indexToRing = new int[getNumCellsToFill()];
		
		for(int i=0; i<indexToRing.length; i++) {
			indexToRing[i] = getIndexToRingIndex(i);
			//System.out.println("Cell " + i + ": " + indexToRing[i]);
		}
		
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
	}
	
	public boolean isTopOrBottomOfCuboid(int indexCell) {
		return indexCell < dimensions[1] || indexCell >= this.getNumCellsToFill() - dimensions[1];
	}
	
	public void updateMinMaxTopIndexIfApplicable(int indexCell, int rotation, int layerIndex) {
		
		if(indexCell >= this.getNumCellsToFill() - dimensions[1]) {
			
			int addOneToPlacement = 0;
			if(rotation == 2) {
				addOneToPlacement =1;
			}
			int placementMod4 = (indexCell+ addOneToPlacement - (this.getNumCellsToFill() - dimensions[1])) % 4;
			
			if(placementMod4 == 0) {
				this.oldTopMin[layerIndex] = this.minTopIndex;
				this.minTopIndex = indexCell;
			} else if(placementMod4 == 1) {
				this.oldTopMax[layerIndex] = this.maxTopIndex;
				this.maxTopIndex = indexCell;
				
			}
			
		}
	}
	//pre: getRingMod4(indexCell, rotation) returns -1:
	public boolean isAcceptableTopOrBottomIndexForInbetweenLayer(int indexCell, int rotation) {
		
		if(rotation % 2 == 1) {
			return false;
		}
		
		
		int placementMod4 = -1;
		
		int addOneToPlacement = 0;
		if(rotation == 2) {
			addOneToPlacement =1;
		}
		
		if(indexCell < dimensions[1]) {
			placementMod4 = (indexCell + addOneToPlacement) % 4;
			
			if(placementMod4 ==0 && indexCell < this.bottomIndex) {
				return true;
			} else if(placementMod4 ==1 && indexCell > this.bottomIndex) {
				return true;
			}
			
		} else {
			placementMod4 = (indexCell+ addOneToPlacement - (this.getNumCellsToFill() - dimensions[1])) % 4;
			
			if(placementMod4 ==0 && indexCell < this.maxTopIndex) {
				return true;
			} else if(placementMod4 ==1 && indexCell > this.minTopIndex) {
				return true;
			}
		}
		
		return false;
	}
	
	
	private int getRingMod4(int indexCell, int rotation) {
		
		if(indexCell < dimensions[1] || indexCell >= this.getNumCellsToFill() - dimensions[1]) {
			return -1;
		}
		
		if(rotation % 2 == 1) {
			return -1;
		}
		//Rotation 0
		
		int ret = 0;
		while(tryAttachCellInDir(indexCell, 0, LEFT).i < indexCell) {
			if(tryAttachCellInDir(indexCell, 0, LEFT).j != 0) {
				System.out.println(indexCell);
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
		
		while(indexCell >= dimensions[1]) {
			ret++;
			
			indexCell = tryAttachCellInDir(indexCell, 0, ABOVE).i;
		}
		
		if(ret >= dimensions[0]) {
			return -1;
		} else {
			return ret;
		}
	}
	
	boolean checkPreComputedForceRegionSplitIfEmptyAroundNewLayer(boolean newLayerArray[], int prevGroundIndex, int prevGroundRotation) {
		
		//TODO: copy/paste code (1)
		//preComputedForceRegionSplitIfEmptyAroundNewLayer
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
		//END TODO: copy/paste code
		
		
		//TODO: copy/paste code (2)
		int firstUnoccupiedIndex = -1;
		for(int i=0; i<tmpArray.length; i++) {
			if(tmpArray[i] == false) {
				firstUnoccupiedIndex = i;
				break;
			}
		}

		Queue<Integer> visited = new LinkedList<Integer>();
		
		boolean explored[] = new boolean[Utils.getTotalArea(this.dimensions)];
		
		explored[firstUnoccupiedIndex] = true;
		visited.add(firstUnoccupiedIndex);
		
		Integer v;
		
		while( ! visited.isEmpty()) {
			
			v = visited.poll();
			
			for(int i=0; i<NUM_NEIGHBOURS; i++) {
				
				int neighbourIndex = this.neighbours[v.intValue()][i].getIndex();
				
				if( ! tmpArray[neighbourIndex] && ! explored[neighbourIndex]) {
					explored[neighbourIndex] = true;
					visited.add(neighbourIndex);
				}
				
			}
			
		}

		for(int i=0; i<tmpArray.length; i++) {
			if( ! tmpArray[i] && ! explored[i]) {
				
				return true;
			}
		}

		return false;
		//END TODO copy/paste code
		
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

	public FastRegionCheck getFastRegionCheck() {
		return fastRegionCheck;
	}

	//DEBUG PRINT STATE ON OTHER CUBOID:
	public void printCurrentStateOnOtherCuboidsFlatMap() {
		
		CuboidToFoldOnGrained toPrint = new CuboidToFoldOnGrained(
				this.dimensions[0],
				this.dimensions[1],
				this.dimensions[2],
				false,
				false,
				this.fastRegionCheck
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
		
		
		System.out.println("Location in ring mod 4:");
		for(int i=0; i<this.currentLayerIndex; i++) {

			
			String labelToUse = getLabel(i);
			
			if(i < this.currentLayerIndex - 1) {
				System.out.println(labelToUse + ": " + (this.ringMod4Lookup[this.prevGroundedIndexes[i + 1]][this.prevGroundedRotations[i + 1]]) + " (" + this.prevGroundedIndexes[i + 1] + ", " + this.prevGroundedRotations[i + 1] + ")");

			} else {
				System.out.println(labelToUse + ": " + (this.ringMod4Lookup[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap]) + " (" + this.topLeftGroundedIndex + ", " + this.topLeftGroundRotationRelativeFlatMap + ")");

			}
		}
	}

	private String getLabel(int layerIndex) {

		char label = (char)( (layerIndex % 26) + 'A');
		
		String labelToUse = label + "" + label;
		if(layerIndex > 26 ) {
			labelToUse = label + "" + (layerIndex/26);
		}
		
		if(layerIndex >= 26* 10) {
			labelToUse = label + "" + (char)( ((layerIndex-10) / 26) + 'a');
		}
		return labelToUse;
	}

	//END DEBUG PRINT STATE ON OTHER CUBOID:
}
