package SimplePhaseSearch.thirdIteration;

import java.util.LinkedList;
import java.util.Queue;

import Coord.Coord2D;
import Model.Utils;

public class RegionSplitLogic {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	//For now, I'm just copying what's in CuboidToFoldOnExtendedFaster5
	//I'll try to make sense of it and fix it up later.
	

	private long preComputedPossiblyEmptyCellsAroundNewLayer[][][][];
	
	//TODO: what does this variable even mean?
	//AHA:
	// This covers the case where the bottom layer and the top layer split the cuboid into 2 regions
	// by themselves. I'll have to handle it for the case where the bottom layer state is not 0 (i.e: 4-in-a-row)
	private boolean preComputedForceRegionSplitIfEmptyAroundNewLayer[][][];
	
	
	
	//Looking at this, it seems like a badly made breadth first search algo that was supposed to be a proof of concept...
	//TODO: Maybe this could be much fast if we don't convert to bool array?
	
	// What this function does:
	// Does a bit mask around the cells of the new layer and
	// then use a lookup-table to decide if the region split (use the lookup table associate with the grounded index and rotation for help)

	private static int debugStop = 0;
	private static int debugBugFix = 0;
	private static int debugThru = 0;
	
	public boolean unoccupiedRegionSplit(long newLayerDetails[], int sideBump, long curState[], int totalArea) {
		
		curState[0] = curState[0] | newLayerDetails[0];
		curState[1] = curState[1] | newLayerDetails[1];
		
		//TODO: shortcut

		long checkAroundNewLayer[] = preComputedPossiblyEmptyCellsAroundNewLayer[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		if(((curState[0] & checkAroundNewLayer[0]) | (curState[1] & checkAroundNewLayer[1])) == 0L) {
			
			debugStop++;
			
			if(preComputedForceRegionSplitIfEmptyAroundNewLayer[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump]) {
				
				//This is the corner case where the bottom layer and the top layer split the unoccupied cells into two regions:
				//System.out.println(topLeftGroundRotationRelativeFlatMap);
				//System.out.println(sideBump);
				debugBugFix++;
			} else {
			
				//At this point, we know that the unoccupied cells didn't split into two regions:
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
		
		boolean tmpArray[] = new boolean[totalArea];
		
		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = isCellIoccupied(i);
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
	
	public static final int NUM_SIDE_BUMP_OPTIONS = 15;
	public static final int NUM_NEIGHBOURS_PER_CELL = 4;
	
	public static final int NUM_ROTATIONS = 4;
	
	private void setupAnswerSheetInBetweenLayers(int totalArea) {
			
			
			preComputedPossiblyEmptyCellsAroundNewLayer = new long[totalArea][NUM_NEIGHBOURS_PER_CELL][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
			preComputedForceRegionSplitIfEmptyAroundNewLayer = new boolean[totalArea][NUM_NEIGHBOURS_PER_CELL][NUM_SIDE_BUMP_OPTIONS];

			for(int index=0; index<totalArea; index++) {
				for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
					
					for(int sideBump=0; sideBump<NUM_SIDE_BUMP_OPTIONS; sideBump++) {
						
						//TODO: setup tmpArray with layer above (and layer below?)
						
						//Start with the case where layer above is state 0...
						
						//TODO: setup the tmpArray
						
						preComputedPossiblyEmptyCellsAroundNewLayer[index][rotation][sideBump]  = getPossiblyEmptyCellsAroundNewLayer(tmpArray, index, rotation);
						preComputedForceRegionSplitIfEmptyAroundNewLayer[index][rotation][sideBump]  = checkPreComputedForceRegionSplitIfEmptyAroundNewLayer(tmpArray, index, rotation);
						
						
					}
				}
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

}
