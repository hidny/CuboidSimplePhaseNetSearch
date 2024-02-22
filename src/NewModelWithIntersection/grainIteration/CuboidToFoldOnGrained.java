package NewModelWithIntersection.grainIteration;
import java.util.LinkedList;
import java.util.Queue;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.DataModelViews;
import Model.NeighbourGraphCreator;
import Model.Utils;
import SimplePhaseSearch.sixthIteration.CuboidToFoldOnExtendedSimplePhase6;

public class CuboidToFoldOnGrained  implements CuboidToFoldOnInterface {

	
	private CoordWithRotationAndIndex[][] neighbours;
	
	public int dimensions[] = new int[3];

	public CuboidToFoldOnGrained(int a, int b, int c) {
		this(a, b, c, true, true);
	}
	public CuboidToFoldOnGrained(int a, int b, int c, boolean verbose, boolean setup) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c, verbose);
		
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;

		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		
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
		
		
		this.topLeftGroundedIndex = bottomIndex;
		this.topLeftGroundRotationRelativeFlatMap = bottomRotationRelativeFlatMap;

		prevSideBumps = new int[DIM_N_OF_Nx1x1];
		prevGroundedIndexes = new int[DIM_N_OF_Nx1x1];
		prevGroundedRotations = new int[DIM_N_OF_Nx1x1];
		currentLayerIndex = 0;
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		tmpArray[bottomIndex] = true;
		this.curState = convertBoolArrayToLongs(tmpArray);
	}


	//Constants:

	//7 *2 -1
	public static final int NUM_POSSIBLE_SIDE_BUMPS = 13;
	
	public static final int NUM_NEIGHBOURS = 4;
	public static final int NUM_ROTATIONS = 4;
	

	public static final int BAD_ROTATION = -10;
	public static final int BAD_INDEX = -20;
	
	private static final int NUM_BYTES_IN_LONG = 64;
	private static final int NUM_LONGS_TO_USE = 3;
	
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
	private long curState[] = new long[NUM_LONGS_TO_USE];

	private int topLeftGroundedIndex = 0;
	private int topLeftGroundRotationRelativeFlatMap = 0;
	
	private int prevSideBumps[];
	private int prevGroundedIndexes[];
	private int prevGroundedRotations[];
	private int currentLayerIndex;
	
	private long debugThru = 0L;
	private long debugStop = 0L;
	private long debugBugFix = 0L;
	

	//TODO:
	//
	//input: index
	private int indexToRing[];
	
	//check if ring is decided: (depth)
	private int indexRingDecided[];
	
	//The ring mod 4 to use
	private int ringMod4AlreadySet[];
	
	//input: index and then rotation
	private int ringMod4Lookup[][];
	//END TODO
	
	//private long DEBUG_LAYER_INDEX = 14;
	
	//BFS to just get it done badly:
	//TODO: This could be so much faster
	// Filter the cells around the new layer and turn that into number (use the grounded index and rotation for help)
	// then use a lookup-table to decide if the region split (use the lookup table associate with the grounded index and rotation for help)
	public boolean unoccupiedRegionSplit(long newLayerDetails[], int sideBump) {
		
		

		curState[0] = curState[0] | newLayerDetails[0];
		curState[1] = curState[1] | newLayerDetails[1];
		curState[2] = curState[2] | newLayerDetails[2];
		
		//TODO: shortcut

		long checkAroundNewLayer[] = preComputedPossiblyEmptyCellsAroundNewLayer[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		if(((curState[0] & checkAroundNewLayer[0]) | (curState[1] & checkAroundNewLayer[1]) | (curState[2] & checkAroundNewLayer[2])) == 0L) {
			
			debugStop++;
			
			if(preComputedForceRegionSplitIfEmptyAroundNewLayer[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump]) {
				//System.out.println(topLeftGroundRotationRelativeFlatMap);
				//System.out.println(sideBump);
				debugBugFix++;
			} else {
			
				return false;
			}
		}
		
		debugThru++;
		
		if(debugThru % 100000000L == 0L) {
			System.out.println(debugThru + " goes thru while " + debugStop + " get stopped.");
			System.out.println((100.0 * debugThru) / (1.0 * (debugThru + debugStop)) + "% thru rate");
			System.out.println((100.0 * debugBugFix) / (1.0 * (debugThru + debugStop)) + "% debugBugFix rate");
			System.out.println("Side bumps used:");
			for(int i=0; i<currentLayerIndex; i++) {
				System.out.println(prevSideBumps[i]);
			}
			System.out.println("END side bumps used");
		}
		
		//TODO: 2nd shortcut:
		// 1st shortcut didn't make it go faster...
		//if(couldAlreadyDetermineSplit(cellsAroundNewLayer[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump][hashMap.get(hashkey)])) {
			
		//}
		
		//END TODO shortcut
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		
		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = isCellIoccupied(i);
		}
		
		
		
		curState[0] = curState[0] ^ newLayerDetails[0];
		curState[1] = curState[1] ^ newLayerDetails[1];
		curState[2] = curState[2] ^ newLayerDetails[2];
		
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
	}
	
	public boolean isCellIoccupied(int i) {
		int indexArray = i / NUM_BYTES_IN_LONG;
		int bitShift = (NUM_BYTES_IN_LONG - 1) - i - indexArray * NUM_BYTES_IN_LONG;
		
		return ((1L << bitShift) & this.curState[indexArray]) != 0L;
	}
	
	public boolean isNewLayerValidSimpleFast(int sideBump) {
	
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		if(newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump] < 0) {
			return false;
		}

		int nextIndex = newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		int nextRot = newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		int nextRingIndex = indexToRing[nextIndex];
		
		//TODO: should it be <= currentLayerIndex
		if(nextRingIndex >=0 && indexRingDecided[nextRingIndex] < currentLayerIndex - 10 && 
				ringMod4Lookup[nextIndex][nextRot] != ringMod4AlreadySet[nextRingIndex]) {
			
			/*System.out.println("currentLayerIndex:" + currentLayerIndex);
			System.out.println("next ring: " + nextRing);
			System.out.println("Mod 4 set: " + ringMod4AlreadySet[nextRing]);
			System.out.println("Mod 4 tried: " + ringMod4Lookup[newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump]]
					[newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump]]);
			System.out.println("index: " + newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump]);
			System.out.println("Rotation: " + newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump]);

			System.out.println("Prev index: "+ this.topLeftGroundedIndex);
			System.out.println("Prev rotation: "+ this.topLeftGroundRotationRelativeFlatMap);
			System.out.println();
			*/
			return false;
		} else {
			//System.out.println("Good");
			//System.out.println("index: " + newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump]);
			//System.out.println("Rotation: " + newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump]);

			//System.out.println("Prev index: "+ this.topLeftGroundedIndex);
			//System.out.println("Prev rotation: "+ this.topLeftGroundRotationRelativeFlatMap);
		}
		
		return ((curState[0] & tmp[0]) | (curState[1] & tmp[1]) | (curState[2] & tmp[2])) == 0L  && ! unoccupiedRegionSplit(tmp, sideBump);
		
	}
	
	public void addNewLayerFast(int sideBump) {
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		curState[0] = curState[0] | tmp[0];
		curState[1] = curState[1] | tmp[1];
		curState[2] = curState[2] | tmp[2];
		
		int tmp1 = newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		int tmp2 = newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		prevGroundedIndexes[currentLayerIndex] = this.topLeftGroundedIndex;
		prevGroundedRotations[currentLayerIndex] = this.topLeftGroundRotationRelativeFlatMap;
		prevSideBumps[currentLayerIndex] = sideBump;
		currentLayerIndex++;
		
		this.topLeftGroundedIndex = tmp1;
		this.topLeftGroundRotationRelativeFlatMap = tmp2;
		
		if(indexToRing[this.topLeftGroundedIndex] >= 0) {
			indexRingDecided[indexToRing[this.topLeftGroundedIndex]] = currentLayerIndex;
			ringMod4AlreadySet[indexToRing[this.topLeftGroundedIndex]] = ringMod4Lookup[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap];
		}
		
	}
	
	public void removePrevLayerFast() {

		
		currentLayerIndex--;
		this.topLeftGroundedIndex = prevGroundedIndexes[currentLayerIndex]; 
		this.topLeftGroundRotationRelativeFlatMap = prevGroundedRotations[currentLayerIndex];
		int sideBumpToCancel  = prevSideBumps[currentLayerIndex];
		
		
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBumpToCancel];
		curState[0] = curState[0] ^ tmp[0];
		curState[1] = curState[1] ^ tmp[1];
		curState[2] = curState[2] ^ tmp[2];
	}
	
	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedFast() {
		
		long tmp[] = answerSheetForTopCellAnySideBump[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap];
		
		boolean ret = ((~curState[0] & tmp[0]) | (~curState[1] & tmp[1]) | (~curState[2] & tmp[2])) != 0;

		
		return ret;
	}

	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedForSideBumpFast(int sideBump) {
		long tmp[] = answerSheetForTopCell[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		return ((~curState[0] & tmp[0]) | (~curState[1] & tmp[1]) | (~curState[2] & tmp[2])) != 0;
	}
	
	int ROTATION_AGAINST_GRAIN = 1;
	
	private void setupAnswerSheetInBetweenLayers() {
		
		answerSheet = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
		newGroundedRotationAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		newGroundedIndexAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		
		preComputedPossiblyEmptyCellsAroundNewLayer = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
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
		
		indexRingDecided = new int[dimensions[0]];
		ringMod4AlreadySet = new int[dimensions[0]];;
		
		for(int i=0; i<indexRingDecided.length; i++) {
			indexRingDecided[i] = -1;
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
	
	private int getRingMod4(int indexCell, int rotation) {
		
		if(indexCell < dimensions[1]) {
			return -1;
		}
		
		if(rotation % 2 == 1) {
			return -1;
		}
		//Rotation 0
		
		int ret = 0;
		while(tryAttachCellInDir(indexCell, 0, LEFT).i < indexCell) {
			indexCell = tryAttachCellInDir(indexCell, 0, LEFT).i;
			ret++;
		}

		//Rotation 2:
		if(rotation == 2) {
			ret = 3 - (ret%4);
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

	

	//DEBUG PRINT STATE ON OTHER CUBOID:
	public void printCurrentStateOnOtherCuboidsFlatMap() {
		
		CuboidToFoldOnGrained toPrint = new CuboidToFoldOnGrained(
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
			
			char label = (char)( (i) + 'A');
			

			String labelToUse = label + "" + label;
			
			
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
		
	}
	

	//END DEBUG PRINT STATE ON OTHER CUBOID:
}