package NewModelWithIntersection.topAndBottomTransitionList;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.Utils;

public class TopAndBottomTransitionHandler {

	public TopAndBottomTransitionHandler() {
		
	}
	
	private int transitionsTop[][][];
	private int topTransitionListIndex[] = new int[2];
	
	private int transitionsBottom[][][];
	private int bottomTransitionListIndex[] = new int[2];

	
	
	public boolean isTopBottomTranstionsPossiblyFine(
			int currentLayerIndex,
			int dimensions[],
			CoordWithRotationAndIndex neighbours[][],
			Coord2D currentIndexRotation,
			Coord2D nextIndexRotation,
			int indexToRing[]
		) {
		
		int minRingIndex = Math.min(indexToRing[currentIndexRotation.i],
				                    indexToRing[nextIndexRotation.i]);
		
		if(minRingIndex >= 0) {
			System.out.println("ERROR: calling TopAndBottomTransitionHandler when the current and next index don't touch the top or bottom");
			System.exit(1);
		}
		
		System.out.println("testing 2nd attempt");
		
		//Check for initialization:
		if(currentLayerIndex == 0) {

			transitionsTop = initializeTransitionLists(
					dimensions,
					neighbours,
					currentIndexRotation,
					nextIndexRotation,
					indexToRing
			);

			topTransitionListIndex[0] = -1;
			topTransitionListIndex[1] = -1;
			
		} else if(currentLayerIndex == dimensions[0] - 1) {
			
			transitionsBottom = initializeTransitionLists(
					dimensions,
					neighbours,
					currentIndexRotation,
					nextIndexRotation,
					indexToRing
			);
			bottomTransitionListIndex[0] = -1;
			bottomTransitionListIndex[1] = -1;
			
		}
		
		//Find the valid transition:
		
		System.out.println("TODO");
		
		System.exit(1);


		int maxRingIndex = Math.min(indexToRing[currentIndexRotation.i],
                indexToRing[nextIndexRotation.i]);

		if(maxRingIndex == 0) {
			
			boolean ret =  isTransitionValid(
					currentIndexRotation,
					nextIndexRotation,
					transitionsTop,
					topTransitionListIndex
				);

			//TODO: make sure the state is updated
			return ret;
			
		} else {

			boolean ret = isTransitionValid(
					currentIndexRotation,
					nextIndexRotation,
					transitionsBottom,
					bottomTransitionListIndex
				);

			//TODO: make sure the state is updated
			return ret;
		}
		
	}
	

	private int[][][] initializeTransitionLists(
			int dimensions[],
			CoordWithRotationAndIndex neighbours[][],
			Coord2D currentIndexRotation,
			Coord2D nextIndexRotation,
			int indexToRing[]
		) {
		
		int ret[][][] = new int[2][3][Utils.getTotalArea(dimensions)];
		
		System.out.println("initializeTransitionLists");
		
		for(int i=0; i<ret.length; i++) {

			System.out.println();
			System.out.println("i = " + i + ":");
			System.out.println();
			for(int j=0; j<ret[0].length; j++) {

				System.out.println();
				System.out.println("j = " + j + ":");
			
				boolean useAtl = (i == 1);
				
				ret[i][j] = TopAndBottomTransitionList2.addBottomTransitionsTopBottom(dimensions,
						neighbours,
						currentIndexRotation,
						nextIndexRotation,
						indexToRing,
						useAtl,
						j-1
				);
			}
		}
		
		return ret;
	}
	

	private static boolean isTransitionValid(
			Coord2D currentIndexRotation,
			Coord2D nextIndexRotation,
			int transitionsTopOrBottom[][][],
			int topTransitionListIndexTopOrBottom[]
		) {
		
		for(int i=0; i<transitionsTopOrBottom.length; i++) {
			for(int j=0; j<transitionsTopOrBottom[i].length; j++) {
				
				if(topTransitionListIndexTopOrBottom[i] == j || topTransitionListIndexTopOrBottom[i] == -1 ) {
					
					if(transitionsTopOrBottom[i][j][currentIndexRotation.i] == nextIndexRotation.i) {
						
						topTransitionListIndexTopOrBottom[i] = j;
						
						return true;
						
					}
					
				}
				
			}
		}
		return false;
	}
	
}
