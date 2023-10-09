package SimplePhaseSearch.thirdIteration;

import java.util.LinkedList;
import java.util.Queue;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

//TODO: is this actually faster than RegionSplitLogicSimple?

public class RegionSplitLogicSimple2 {

	private CoordWithRotationAndIndex[][] neighbours;

	public RegionSplitLogicSimple2(CoordWithRotationAndIndex[][] neighbours) {
		this.neighbours = neighbours;
	}
	
	private long preComputedCellsAroundNewLayerMid[][][][][][];
	
	public boolean unoccupiedRegionSplitSkipGoingUpMid(long curState[],
			int layerStateBelow,
			int layerStateAbove,
			int indexGroundedBelowLayer,
			int rotationGroundedBelowLayer,
			int sideBump) {
		
		
		if((curState[0] & preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][indexGroundedBelowLayer][rotationGroundedBelowLayer][sideBump][0]) == 0L
		 &&(curState[1] & preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][indexGroundedBelowLayer][rotationGroundedBelowLayer][sideBump][1]) == 0L
				) {
			
			// If there's no non-trivial cells around the new layer, it's (usually) safe to assume there's no region split.
			// TODO: handle exception where there is a region split if there's no non-trivial around the new layer.
			return false;
		}
		
		//TODO: please avoid doing a breadth-first search in the future.
		// This is the slow and reliable way that I should test new functions against:
		boolean tmpArray[] = new boolean[this.neighbours.length];
		
		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = isCellIoccupied(curState, i);
		}
		
		int firstUnoccupiedIndex = -1;
		for(int i=0; i<tmpArray.length; i++) {
			if(tmpArray[i] == false) {
				firstUnoccupiedIndex = i;
				break;
			}
		}

		Queue<Integer> visited = new LinkedList<Integer>();
		
		boolean explored[] = new boolean[this.neighbours.length];
		
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


	public static final int NUM_LAYER_STATES = 7;

	public static final int NUM_SIDE_BUMP_OPTIONS = 13;
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_CELLS_PER_LAYER = 4;
	

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	//TODO: this is only for going up through the middle...
	// Try to also cover the case of going up on the side and going from top to bottom...

	//TODO: handle the case of the 1st layer separately (i.e: bottom cell + 4 more cells)
	
	public void setupAnswerSheetInBetweenLayersMid(
			int totalArea,
			int newGroundedIndexAboveMid[][][][][],
			int newGroundedRotationAboveMid[][][][][]) {
			
			
		preComputedCellsAroundNewLayerMid = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][totalArea][NUM_NEIGHBOURS_PER_CELL][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
			
			for(int layerStateBelow=0; layerStateBelow<NUM_LAYER_STATES; layerStateBelow++) {
				for(int layerStateAbove=0; layerStateAbove<NUM_LAYER_STATES; layerStateAbove++) {
					
					for(int index=0; index<totalArea; index++) {
						for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
							
							for(int sideBump=0; sideBump<NUM_SIDE_BUMP_OPTIONS; sideBump++) {
								
								//Start with the case where layer state above and layer state below is state 0...
								if(layerStateBelow != 0 || layerStateAbove != 0) {
									preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump]  = setImpossibleForAnswerSheet();
									continue;
								}
								
								int curGroundIndexAbove = newGroundedIndexAboveMid[layerStateBelow][layerStateAbove][index][rotation][sideBump];
								int curRotationGroundIndexAbove = newGroundedRotationAboveMid[layerStateBelow][layerStateAbove][index][rotation][sideBump];
								
								if(curGroundIndexAbove < 0 || curRotationGroundIndexAbove < 0) {
									preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump]  = setImpossibleForAnswerSheet();
									continue;
								}

								//preComputedForceRegionSplitIfEmptyAroundNewLayer
								boolean arrayBelowLayer[] = new boolean[totalArea];
								
								//Get the bool array with the new layer indexes true:
								for(int i=0; i<arrayBelowLayer.length; i++) {
									arrayBelowLayer[i] = false;
								}
								
								Coord2D cur = new Coord2D(index, rotation);
								
								//TODO: This loop assumes it's layer state 0:
								//TODO: carefully stop it from assuming that in future.
								for(int i=0; i<NUM_CELLS_PER_LAYER; i++) {
									arrayBelowLayer[cur.i] = true;
									cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
								}
								
								cur = new Coord2D(curGroundIndexAbove, curRotationGroundIndexAbove);

								//preComputedForceRegionSplitIfEmptyAroundNewLayer
								boolean arrayAboveLayer[] = new boolean[totalArea];

								for(int i=0; i<arrayAboveLayer.length; i++) {
									arrayAboveLayer[i] = false;
								}
								
								//This loop assumes it's layer state 0:
								for(int i=0; i<NUM_CELLS_PER_LAYER; i++) {
									if(arrayAboveLayer[cur.i]) {
										System.out.println("ERROR: something went wrong in RegionSplitLogic -> setupAnswerSheetInBetweenLayers");
										System.exit(1);
									}
									arrayAboveLayer[cur.i] = true;
									cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
								}
								
								
								preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump]  = getPossiblyEmptyCellsAroundNewLayer(layerStateBelow, layerStateAbove, arrayBelowLayer, arrayAboveLayer);
								
								
								if((convertBoolArrayToLongs(arrayAboveLayer)[0]     & preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump][0]) != 0L
									|| (convertBoolArrayToLongs(arrayAboveLayer)[1] & preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump][1]) != 0L
									|| (convertBoolArrayToLongs(arrayBelowLayer)[0] & preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump][0]) != 0L
									|| (convertBoolArrayToLongs(arrayBelowLayer)[1] & preComputedCellsAroundNewLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump][1]) != 0L) {
									System.out.println("oops! Something went wrong in setupAnswerSheetInBetweenLayersMid.");
									System.exit(1);
								}
								
							}
						}
					}
				}
			}
	
	}


	private long[] getPossiblyEmptyCellsAroundNewLayer(int belowLayerStateIndex, int aboveLayerStateIndex, boolean arrayBelowLayer[], boolean arrayAboveLayer[]) {
		if(belowLayerStateIndex != 0 || aboveLayerStateIndex != 0) {
			System.out.println("ERROR: this function currently assumes that both the belowLayerIndex and aboveLayerIndex is 0 (getPossiblyEmptyCellsAroundNewLayer)");
			System.exit(1);
		}
		// Set output to the cells around the new layer that aren't the new layer and aren't the old layer:
		// This assumes both layers are state 0. (4 cells in a row)
		boolean output[] = new boolean[arrayAboveLayer.length];

		for(int i=0; i<arrayAboveLayer.length; i++) {
			output[i] = false;
		}
		
		for(int i=0; i<arrayAboveLayer.length; i++) {
			if(arrayAboveLayer[i]) {
				
				for(int dir=0; dir<NUM_ROTATIONS; dir++) {
					
					Coord2D cur = tryAttachCellInDir(i, 0, dir);
					
					if(arrayAboveLayer[cur.i] == false && arrayBelowLayer[cur.i] == false) {
						output[cur.i] = true;
					}
					
					//cells touching corner to corner are also around new layer:
					//This works in my head, but feels like a hack...
					for(int dir2=0; dir2<NUM_ROTATIONS; dir2++) {
						
						if(dir2 % 2 == dir % 2) {
							continue;
						}
						Coord2D cur2 = tryAttachCellInDir(cur.i, cur.j, dir2);
						
						if(arrayAboveLayer[cur2.i] == false && arrayBelowLayer[cur2.i] == false) {
							output[cur2.i] = true;
						}
						
						
					}
					
				}
			}
		}
		
		return convertBoolArrayToLongs(output);
	}

	
	
	private static final int NUM_NEIGHBOURS_PER_CELL = 4;
	
	private static final int NUM_BITS_IN_LONG = 64;
	
	public boolean isCellIoccupied(long curState[], int i) {
		int indexArray = i / NUM_BITS_IN_LONG;
		int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
		
		return ((1L << bitShift) & curState[indexArray]) != 0L;
	}
	
	//TODO: copy/paste code:
	public static final int NUM_LONGS_TO_USE = 2;
	
	private static long[] setImpossibleForAnswerSheet() {
		
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = -1L;
		}
		
		return ret;
	}
	

	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.neighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS_PER_CELL;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS_PER_CELL) % NUM_NEIGHBOURS_PER_CELL;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	
	
	//TODO copy/paste code:

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
