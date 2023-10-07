package SimplePhaseSearch.thirdIteration;

import java.util.LinkedList;
import java.util.Queue;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

//For now, I'm just copying what's in CuboidToFoldOnExtendedFaster5
//I'll try to make sense of it and fix it up later.

public class RegionSplitLogic {

	private CoordWithRotationAndIndex[][] neighbours;

	public RegionSplitLogic(CoordWithRotationAndIndex[][] neighbours) {
		this.neighbours = neighbours;
	}
	
	//TODO: this variable is barely used
	// Use it as a tool to replace the slow breadth-first search in unoccupiedRegionSplit function. 
	private long preComputedPossiblyEmptyCellsAroundNewLayer[][][][][][];
	
	// This covers the case where the bottom layer and the top layer split the cuboid into 2 regions
	// by themselves. I'll have to handle it for the case where the bottom layer state is not 0 (i.e: 4-in-a-row)
	//This is highly applicable when the other cuboid is the 1x2xN cuboid
	private boolean preComputedForceRegionSplitIfEmptyAroundNewLayer[][][][][];
	
	
	
	//Looking at this, it seems like a badly made breadth first search algo that was supposed to be a proof of concept...
	//TODO: Maybe this could be much fast if we don't convert to bool array?
	//TODO: treat this as a proof-of-concept and make a faster version later.
	
	// What this function should do:
	// Does a bit mask around the cells of the new layer and
	// then use a lookup-table to decide if the region split (use the lookup table associate with the grounded index and rotation for help)

	//What this function actually does:
	// Does a bit mask around the cells of the new layer and
	// then uses a loop to decide if the region split
	
	private static int debugStop = 0;
	private static int debugBugFix = 0;
	private static int debugThru = 0;
	
	//TODO: figure out what the params should be and try to use this
	public boolean unoccupiedRegionSplit(long newLayerDetails[], int sideBump, long curState[], int totalArea,
			int prevLayerIndex,
			int currentLayerIndex,
			int topLeftGroundedIndex,
			int topLeftGroundRotationRelativeFlatMap) {
		
		curState[0] = curState[0] | newLayerDetails[0];
		curState[1] = curState[1] | newLayerDetails[1];
		
		long checkAroundNewLayer[] = preComputedPossiblyEmptyCellsAroundNewLayer[prevLayerIndex][currentLayerIndex][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		if(((curState[0] & checkAroundNewLayer[0]) | (curState[1] & checkAroundNewLayer[1])) == 0L) {
			//At this point, the cells around the current layer and new layer are empty.
			debugStop++;
			
			if(preComputedForceRegionSplitIfEmptyAroundNewLayer[prevLayerIndex][currentLayerIndex][topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump]) {
				
				//This is the corner case where the bottom layer and the top layer split the unoccupied cells into two regions:
				//System.out.println(topLeftGroundRotationRelativeFlatMap);
				//System.out.println(sideBump);
				debugBugFix++;
				
				//TODO: why didn't I return true here before?
				// Was it so that I could see the debug logs?
				// Was it because I'm a coward?
				
			} else {
			
				//At this point, we know that the unoccupied cells didn't split into two regions:
				return false;
			}
		}
		
		debugThru++;
		
		if(debugThru % 100000000L == 0L) {
			System.out.println("Region split debug:");
			System.out.println(debugThru + " goes thru while " + debugStop + " get stopped.");
			System.out.println((100.0 * debugThru) / (1.0 * (debugThru + debugStop)) + "% thru rate");
			System.out.println((100.0 * debugBugFix) / (1.0 * (debugThru + debugStop)) + "% debugBugFix rate");
			
		}
		
		
		//TODO: please avoid doing a breadth-first search in the future.
		// This is the slow and reliable way that I should test new functions against:
		boolean tmpArray[] = new boolean[totalArea];
		
		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = isCellIoccupied(curState, i);
		}
		
		
		curState[0] = curState[0] ^ newLayerDetails[0];
		curState[1] = curState[1] ^ newLayerDetails[1];
		
		int firstUnoccupiedIndex = -1;
		for(int i=0; i<tmpArray.length; i++) {
			if(tmpArray[i] == false) {
				firstUnoccupiedIndex = i;
				break;
			}
		}

		Queue<Integer> visited = new LinkedList<Integer>();
		
		boolean explored[] = new boolean[totalArea];
		
		explored[firstUnoccupiedIndex] = true;
		visited.add(firstUnoccupiedIndex);
		
		Integer v;
		
		while( ! visited.isEmpty()) {
			
			v = visited.poll();
			
			for(int i=0; i<NUM_NEIGHBOURS_PER_CELL; i++) {
				
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
	
	//Pre-compute:
	
	//TODO: put some of these in a constants file:
	public static final int NUM_SIDE_BUMP_OPTIONS = 15;
	public static final int NUM_NEIGHBOURS_PER_CELL = 4;
	
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_NEIGHBOURS = 4;
	public static final int NUM_CELLS_PER_LAYER = 4;
	
	public static final int NUM_LONGS_TO_USE = 2;
	private static final int NUM_BITS_IN_LONG = 64;
	
	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	public static final int NUM_LAYER_STATES = 7;
	//END Constants
	
	
	//TODO: this is only for going up through the middle...
	// Try to also cover the case of going up on the side and going from top to bottom...

	private void setupAnswerSheetInBetweenLayers(
			int totalArea,
			int newGroundedIndex[][][][][],
			int newGroundedRotation[][][][][]) {
			
			
			preComputedPossiblyEmptyCellsAroundNewLayer = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][totalArea][NUM_NEIGHBOURS_PER_CELL][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
			preComputedForceRegionSplitIfEmptyAroundNewLayer = new boolean[NUM_LAYER_STATES][NUM_LAYER_STATES][totalArea][NUM_NEIGHBOURS_PER_CELL][NUM_SIDE_BUMP_OPTIONS];
			for(int layerStateBelow=0; layerStateBelow<NUM_LAYER_STATES; layerStateBelow++) {
				for(int layerStateAbove=0; layerStateAbove<NUM_LAYER_STATES; layerStateAbove++) {
					
					for(int index=0; index<totalArea; index++) {
						for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
							
							for(int sideBump=0; sideBump<NUM_SIDE_BUMP_OPTIONS; sideBump++) {
								
								//Start with the case where layer state above and layer state below is state 0...
								if(layerStateBelow != 0 || layerStateAbove != 0) {
									preComputedPossiblyEmptyCellsAroundNewLayer[index][rotation][sideBump]  = null;
									preComputedForceRegionSplitIfEmptyAroundNewLayer[index][rotation][sideBump]  = null;
									continue;
								}
								
								//preComputedForceRegionSplitIfEmptyAroundNewLayer
								boolean tmpArray[] = new boolean[totalArea];
								
								//Get the bool array with the new layer indexes true:
								for(int i=0; i<tmpArray.length; i++) {
									tmpArray[i] = false;
								}
								
								Coord2D cur = new Coord2D(index, rotation);
								//This loop assumes it's layer state 0:
								for(int i=0; i<NUM_CELLS_PER_LAYER; i++) {
									tmpArray[cur.i] = true;
									cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
								}
								
								int curGroundIndexAbove = newGroundedIndex[layerStateBelow][layerStateAbove][index][rotation][sideBump];
								int curRotationGroundIndexAbove = newGroundedRotation[layerStateBelow][layerStateAbove][index][rotation][sideBump];

								cur = new Coord2D(curGroundIndexAbove, curRotationGroundIndexAbove);

								//This loop assumes it's layer state 0:
								for(int i=0; i<NUM_CELLS_PER_LAYER; i++) {
									if(tmpArray[cur.i]) {
										System.out.println("ERROR: something went wrong in RegionSplitLogic -> setupAnswerSheetInBetweenLayers");
										System.exit(1);
									}
									tmpArray[cur.i] = true;
									cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
								}
								
								
								preComputedPossiblyEmptyCellsAroundNewLayer[layerStateBelow][layerStateAbove][index][rotation][sideBump]  = getPossiblyEmptyCellsAroundNewLayer(layerStateBelow, layerStateAbove, tmpArray);
								preComputedForceRegionSplitIfEmptyAroundNewLayer[layerStateBelow][layerStateAbove][index][rotation][sideBump]  = checkPreComputedForceRegionSplitIfEmptyAroundNewLayer(layerStateBelow, layerStateAbove, tmpArray, totalArea);
								
							}
						}
					}
				}
			}
	
	}

	
	
	//TODO: don't assume layer state index above and below is 0 in the future:
	//pre: This assumes that the layer state of the new layer is 0.
	//pre: This also assumes that the layer state of the previous layer is 0.
	boolean checkPreComputedForceRegionSplitIfEmptyAroundNewLayer(int belowLayerStateIndex, int aboveLayerStateIndex, boolean aboveAndBelowLayerState[], int totalArea) {
		
		if(belowLayerStateIndex != 0 || aboveLayerStateIndex != 0) {
			System.out.println("ERROR: this function currently assumes that both the belowLayerIndex and aboveLayerIndex is 0 (checkPreComputedForceRegionSplitIfEmptyAroundNewLayer)");
			System.exit(1);
		}
		
		
		Queue<Integer> visited = new LinkedList<Integer>();
		
		boolean explored[] = new boolean[totalArea];
		
		int firstUnoccupiedIndex = -1;
		for(int i=0; i<aboveAndBelowLayerState.length; i++) {
			if(aboveAndBelowLayerState[i] == false) {
				firstUnoccupiedIndex = i;
				break;
			}
		}
		explored[firstUnoccupiedIndex] = true;
		visited.add(firstUnoccupiedIndex);
		
		Integer v;
		
		while( ! visited.isEmpty()) {
			
			v = visited.poll();
			
			for(int i=0; i<NUM_NEIGHBOURS; i++) {
				
				int neighbourIndex = this.neighbours[v.intValue()][i].getIndex();
				
				if( ! aboveAndBelowLayerState[neighbourIndex] && ! explored[neighbourIndex]) {
					explored[neighbourIndex] = true;
					visited.add(neighbourIndex);
				}
				
			}
			
		}
	
		for(int i=0; i<aboveAndBelowLayerState.length; i++) {
			if( ! aboveAndBelowLayerState[i] && ! explored[i]) {
				
				return true;
			}
		}
	
		return false;
		
	}
	
	private long[] getPossiblyEmptyCellsAroundNewLayer(int belowLayerStateIndex, int aboveLayerStateIndex, boolean aboveAndBelowLayerState[]) {
		if(belowLayerStateIndex != 0 || aboveLayerStateIndex != 0) {
			System.out.println("ERROR: this function currently assumes that both the belowLayerIndex and aboveLayerIndex is 0 (getPossiblyEmptyCellsAroundNewLayer)");
			System.exit(1);
		}
		// Set output to the cells around the new layer that aren't the new layer and aren't the old layer:
		// This assumes both layers are state 0. (4 cells in a row)
		boolean output[] = new boolean[aboveAndBelowLayerState.length];

		for(int i=0; i<aboveAndBelowLayerState.length; i++) {
			output[i] = false;
		}
		
		for(int i=0; i<aboveAndBelowLayerState.length; i++) {
			if(aboveAndBelowLayerState[i]) {
				
				for(int dir=0; dir<NUM_ROTATIONS; dir++) {
					
					Coord2D cur = tryAttachCellInDir(i, 0, dir);
					
					if(aboveAndBelowLayerState[cur.i] == false) {
						output[cur.i] = true;
					}
					
					//cells touching corner to corner are also around new layer:
					//This works in my head, but feels like a hack...
					for(int dir2=0; dir2<NUM_ROTATIONS; dir2++) {
						
						if(dir2 % 2 == dir % 2) {
							continue;
						}
						Coord2D cur2 = tryAttachCellInDir(cur.i, cur.j, dir2);
						
						if(aboveAndBelowLayerState[cur2.i] == false) {
							output[cur2.i] = true;
						}
						
						
					}
					
				}
			}
		}
		
		return convertBoolArrayToLongs(output);
	}

	
	//TODO: Below is copy/paste code
	// Maybe put this in a utility function or something?
	
	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.neighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}


	public boolean isCellIoccupied(long curState[], int i) {
		int indexArray = i / NUM_BITS_IN_LONG;
		int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
		
		return ((1L << bitShift) & curState[indexArray]) != 0L;
	}
	
	private long[] convertBoolArrayToLongs(boolean tmpArray[]) {
		
		//1st entry:
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = 0;
		}
		
		for(int i=0; i<tmpArray.length; i++) {
			
			if(tmpArray[i]) {
				int indexArray = i / NUM_BITS_IN_LONG;
				int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
				
				ret[indexArray] += 1L << bitShift;
			}
		}
		
		
		return ret;
	}

}
