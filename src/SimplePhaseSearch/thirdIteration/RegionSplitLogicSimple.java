package SimplePhaseSearch.thirdIteration;

import java.util.LinkedList;
import java.util.Queue;

import Coord.CoordWithRotationAndIndex;

//For now, I'm just copying what's in CuboidToFoldOnExtendedFaster5
//I'll try to make sense of it and fix it up later.

public class RegionSplitLogicSimple {

	private CoordWithRotationAndIndex[][] neighbours;

	public RegionSplitLogicSimple(CoordWithRotationAndIndex[][] neighbours) {
		this.neighbours = neighbours;
	}
	
	public boolean unoccupiedRegionSplitSkip(long curState[]) {
		
		
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
	
	private static final int NUM_NEIGHBOURS_PER_CELL = 4;
	
	private static final int NUM_BITS_IN_LONG = 64;
	
	public boolean isCellIoccupied(long curState[], int i) {
		int indexArray = i / NUM_BITS_IN_LONG;
		int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
		
		return ((1L << bitShift) & curState[indexArray]) != 0L;
	}
	

}
