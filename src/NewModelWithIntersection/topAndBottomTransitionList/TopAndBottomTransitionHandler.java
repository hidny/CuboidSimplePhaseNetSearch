package NewModelWithIntersection.topAndBottomTransitionList;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.Utils;

public class TopAndBottomTransitionHandler {

	//This class will handle the surprisingly complicated logic of 
	// knowing whether a transition between the 1st/Last Ring and the top/bottom side is plausible.
	// The logic is simple with pen and paper, but hard to write in code. This took 2 weeks of trial and error.

	// The solution I went with might be a bit too clever:
	// Because I can't be bothered to figure out how to 
	// correctly resolve a possible off-by-one error with the transition list between the 1st/last Ring and the top/bottom sides,
	// I asked the algorithm to do it for me by offering 3 possible transition lists and asking the algo to go with the first transition list that works.
	// LOL! This is dirty even for me!
	// The first one is where the off-by-one error is -1,
	// the second one is where the off-by-one error is 0 (or there isn't any),
	// the third one is where the off-by-one error is +1.
	
	//All I have to do is make sure an off-by-two+ error won't happen, which seems reasonable in my head, but I think this will need some pen and paper proof.
	
	private int transitionsTop[][][];
	private int topTransitionListIndex[] = new int[2];
	private int topTransitionIndexSet[] = new int[2];
	
	private int transitionsBottom[][][];
	private int bottomTransitionListIndex[] = new int[2];
	private int bottomTransitionIndexSet[] = new int[2];

	
	
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
			return true;
		}
		
		//System.out.println("testing 2nd attempt");
		
		//System.out.println("currentLayerIndex: " + currentLayerIndex);
		//System.out.println("Target currentLayerIndex: " + (dimensions[0] - 1));
		
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
			topTransitionIndexSet[0] = -1;
			topTransitionIndexSet[1] = -1;
			
		} else if(currentLayerIndex == dimensions[0]) {
			
			transitionsBottom = initializeTransitionLists(
					dimensions,
					neighbours,
					currentIndexRotation,
					nextIndexRotation,
					indexToRing
			);
			bottomTransitionListIndex[0] = -1;
			bottomTransitionListIndex[1] = -1;
			
			bottomTransitionIndexSet[0] = -1;
			bottomTransitionIndexSet[1] = -1;
			

			//printTransitionLists();
			System.out.println("Debug");
			System.out.println("Details:");
			debugPrintTransitionListsDetails();
			
			System.out.println("Transition used at the bottom:");
			System.out.println(currentIndexRotation.i + ", " + currentIndexRotation.j);
			System.out.println(nextIndexRotation.i + ", " + nextIndexRotation.j);
			//System.exit(1);
		}
		
		//Find the valid transition

		int maxRingIndex = Math.max(indexToRing[currentIndexRotation.i],
                indexToRing[nextIndexRotation.i]);

		if(maxRingIndex == 0) {
			
			boolean ret = isTransitionValid(
					currentLayerIndex,
					currentIndexRotation,
					nextIndexRotation,
					transitionsTop,
					topTransitionListIndex,
					topTransitionIndexSet
				);

			//TODO: make sure the state is updated
			return ret;
			
		} else {
			
			System.out.println("maxRingIndex: " + maxRingIndex);

			boolean ret = isTransitionValid(
					currentLayerIndex,
					currentIndexRotation,
					nextIndexRotation,
					transitionsBottom,
					bottomTransitionListIndex,
					bottomTransitionIndexSet
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
				
				
				ret[i][j] = TopAndBottomTransitionList2.addTransitionsTopBottom(dimensions,
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
			int currentLayerIndex,
			Coord2D currentIndexRotation,
			Coord2D nextIndexRotation,
			int transitionsTopOrBottom[][][],
			int topTransitionListIndexTopOrBottom[],
			int transitionIndexSet[]
		) {
		
		for(int i=0; i<transitionsTopOrBottom.length; i++) {
			for(int j=0; j<transitionsTopOrBottom[i].length; j++) {
				
				if(topTransitionListIndexTopOrBottom[i] == j 
						|| topTransitionListIndexTopOrBottom[i] == -1
						|| transitionIndexSet[i] == -1
						|| transitionIndexSet[i] >= currentLayerIndex) {
					
					if(transitionsTopOrBottom[i][j][currentIndexRotation.i] == nextIndexRotation.i) {
						
						if(topTransitionListIndexTopOrBottom[i] == -1
								|| transitionIndexSet[i] >= currentLayerIndex) {
							
							topTransitionListIndexTopOrBottom[i] = j;
							transitionIndexSet[i] = currentLayerIndex;
						}
						return true;
						
					}
					
				}
				
			}
		}
		return false;
	}
	
	
	//Debug functions:
	
	public void debugPrintTransitionLists() {
		
		System.out.println("Top transitions:");
		debugPrintTopOrBottomTransitions(
				transitionsTop,
				topTransitionListIndex,
				topTransitionIndexSet
			);
		
		System.out.println();
		System.out.println("Bottom transitions:");
		debugPrintTopOrBottomTransitions(
				transitionsBottom,
				bottomTransitionListIndex,
				bottomTransitionIndexSet
			);
		
		
		
	}
	
	private void debugPrintTopOrBottomTransitions(
			int transitionsTopOrBottom[][][],
			int topTransitionListIndexTopOrBottom[],
			int transitionIndexSet[]
		) {
		
		for(int i=0; i<transitionsTopOrBottom.length; i++) {
			
			System.out.println("Transition list index: " + i);
			System.out.println("transitionIndexSet[" + i +"] = " + transitionIndexSet[i]);
			
			for(int j=0; j<transitionsTopOrBottom[i].length; j++) {
				
				if(topTransitionListIndexTopOrBottom[i] == j) {
					
					for(int k=0; k<transitionsTopOrBottom[i][j].length; k++) {
						
						if(transitionsTopOrBottom[i][j][k] != -1) {
							System.out.println("Transition " + k + ": " + transitionsTopOrBottom[i][j][k]);
						}
					}
					
				}
				
			}
			System.out.println();
		}
	}
	


	public void debugPrintTransitionListsDetails() {
		
		System.out.println("Top transitions details:");
		debugPrintTopOrBottomTransitionsDetails(
				transitionsTop,
				topTransitionListIndex,
				topTransitionIndexSet
			);
		
		System.out.println();
		System.out.println("Bottom transitions details:");
		debugPrintTopOrBottomTransitionsDetails(
				transitionsBottom,
				bottomTransitionListIndex,
				bottomTransitionIndexSet
			);
		
		
		
	}
	
	private void debugPrintTopOrBottomTransitionsDetails(
			int transitionsTopOrBottom[][][],
			int topTransitionListIndexTopOrBottom[],
			int transitionIndexSet[]
		) {
		
		for(int i=0; i<transitionsTopOrBottom.length; i++) {
			
			System.out.println("Transition list index: " + i);
			System.out.println("transitionIndexSet[" + i +"] = " + transitionIndexSet[i]);
			
			for(int j=0; j<transitionsTopOrBottom[i].length; j++) {
				
				System.out.println();
				System.out.println("j = " + j + ":");
					
				for(int k=0; k<transitionsTopOrBottom[i][j].length; k++) {
					
					if(transitionsTopOrBottom[i][j][k] != -1) {
						System.out.println("Transition for list " + j + ":" + k + ": " + transitionsTopOrBottom[i][j][k]);
					}
					}
					
				
			}
			System.out.println();
		}
	}
	
}
