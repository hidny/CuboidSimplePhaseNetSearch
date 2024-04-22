package NewModelWithIntersection.fifthIteration;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.DataModelViews;
import Model.NeighbourGraphCreator;
import Model.Utils;
import NewModelWithIntersection.fastRegionCheck.FastRegionCheck;
import NewModelWithIntersection.filterOutTwoTops.FilterOutTwoTopsFaster4;

public class CuboidToFoldOnExtendedFaster5  implements CuboidToFoldOnInterface {

	
	public CoordWithRotationAndIndex[][] neighbours;
	
	public int dimensions[] = new int[3];

	private FastRegionCheck fastRegionCheck;
	
	public FilterOutTwoTopsFaster4 filterOutTwoTopsFaster4;
	
	public CuboidToFoldOnExtendedFaster5(int a, int b, int c) {
		this(a, b, c, true, true, null);
	}

	public CuboidToFoldOnExtendedFaster5(int a, int b, int c, FastRegionCheck fastRegionCheck) {
		this(a, b, c, true, true, fastRegionCheck);
	}
	
	public CuboidToFoldOnExtendedFaster5(int a, int b, int c, boolean verbose, boolean setup, FastRegionCheck fastRegionCheck) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c, verbose);

		//TODO: put this outside the constructor if possible.
		if(fastRegionCheck == null) {
			this.fastRegionCheck = new FastRegionCheck(neighbours, curState);
		} else {
			this.fastRegionCheck = fastRegionCheck;
		}
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;

		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		
		if(setup) {
			setupAnswerSheetInBetweenLayers();
 			setupAnswerSheetForTopCell();
		}
		
		filterOutTwoTopsFaster4 = new FilterOutTwoTopsFaster4(neighbours);
	}
	
	public int getNumCellsToFill() {
		return Utils.getTotalArea(this.dimensions);
	}
	
	public CoordWithRotationAndIndex[] getNeighbours(int cellIndex) {
		return neighbours[cellIndex];
	}
	

	@Override
	public int[] getDimensions() {
		// TODO Auto-generated method stub
		return dimensions;
	}
	
	

	public void initializeNewBottomIndexAndRotation(int bottomIndex, int bottomRotationRelativeFlatMap) {
		
		
		this.topLeftGroundedIndex = bottomIndex;
		this.topLeftGroundRotationRelativeFlatMap = bottomRotationRelativeFlatMap;

		prevSideBumps = new int[DIM_N_OF_Nx1x1];
		prevGroundedIndexes = new int[DIM_N_OF_Nx1x1];
		prevGroundedRotations = new int[DIM_N_OF_Nx1x1];
		currentLayerIndex = 0;
		if(DIM_N_OF_Nx1x1 > 0) {
			//Initialize so the debug print doesn't fail:
			prevGroundedIndexes[0] = bottomIndex;
		}
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
	public long curState[] = new long[NUM_LONGS_TO_USE];

	private int topLeftGroundedIndex = 0;
	private int topLeftGroundRotationRelativeFlatMap = 0;
	
	private int prevSideBumps[];
	private int prevGroundedIndexes[];
	private int prevGroundedRotations[];
	private int currentLayerIndex;
	
	private long debugThru = 0L;
	private long debugStop = 0L;
	private long debugBugFix = 0L;
	//private long DEBUG_LAYER_INDEX = 14;
	
	
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
	
	public boolean isNewLayerValidSimpleFast(int sideBump) {
	
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
	
		return ((curState[0] & tmp[0]) | (curState[1] & tmp[1]) | (curState[2] & tmp[2])) == 0L && ! unoccupiedRegionSplit(tmp, sideBump);
		
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
					
					answerSheet[index][rotation][sideBump] = convertBoolArrayToLongs(tmpArray);
					preComputedPossiblyEmptyCellsAroundNewLayer[index][rotation][sideBump]  = getPossiblyEmptyCellsAroundNewLayer(tmpArray, index, rotation);
					
					newGroundedIndexAbove[index][rotation][sideBump] = nextGounded.i;
					newGroundedRotationAbove[index][rotation][sideBump] = nextGounded.j;
				}
			}
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

	public FastRegionCheck getFastRegionCheck() {
		return fastRegionCheck;
	}

		//DEBUG PRINT STATE ON OTHER CUBOID:
		public void printCurrentStateOnOtherCuboidsFlatMap() {
			
			CuboidToFoldOnExtendedFaster5 toPrint = new CuboidToFoldOnExtendedFaster5(
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
