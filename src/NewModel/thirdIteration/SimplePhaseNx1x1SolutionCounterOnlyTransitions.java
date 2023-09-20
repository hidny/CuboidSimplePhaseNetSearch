package NewModel.thirdIteration;


import Coord.Coord2D;
import NewModel.firstIteration.Nx1x1CuboidToFold;

public class SimplePhaseNx1x1SolutionCounterOnlyTransitions {

	public static int N = 10;
	
	//F(N) (but non-uniq)
	// 16
	// 112
	// 1040
	// 9872
	// 93744
	// 890160
	// 8452624
	// 80262928
	// 762146480
	// 7237055408
	// 68720347536
	// 652542491280
	
	//The sequence seems to be increasing exponentially by a factor of 9.4956227...
	
	// I solved this with matrices (i.e. turned this into an eigenvector problem and solved it)
	
	/*
	 * Entries for the matrics were found in the prints of Nx1x1StackTransitionTracker2:
"(0, 15, 0, 15): 7
(...)
(3, 12, 3, 12): 1"

Resultant Matrix M =
7  2  1  2  2  1  2
1  1  0  1  0  1  0
2  0  1  0  1  0  1
2  1  0  1  0  1  0
2  0  1  0  1  0  1
2  1  0  1  0  1  0
1  0  1  0  1  0  1


Formula derived by hand:
F(n) = 16 * (1  0  0  .. 0) M^(n) (1)
                                  (0)
                                  (0)
                                  (0)
                                  (...)
                                  
// Where n is the number of layers.
                              
I googled for the eigenvectors and eigenvalues...
// https://matrixcalc.org/ gives this:
//		9.4956226893
//So the sequence grows exponentially by a factor of about 9.4956226893
 * or: 9.49562268930808697738316414143286398511459028140825...
 * 
 * I also took pictures and got the exact form from wolframalpha.
	 */
	
	
	
	public static void main(String args[]) {
		
		//Utils.printFromSolutionCode(new BigInteger("111693216346316918536"));
		//System.exit(1);
		
		System.out.println("Latest code:");
		
		System.out.println("15 = " + LAYER_IS_GROUNDED_INDEX);
		
		System.out.println("Creating transition table:");
		Nx1x1StackTransitionTracker2.initAllowedTransitions();
		
		
		
		System.out.println();
		System.out.println("Doing the actual search:");
		
		buildNetStartingFromBottom(N);
		
		System.out.println("Num non-unique solutions found: " + numSolutions);
		
		
	}
	
	
	
	public static long numSolutions = 0L;
	public static int iterator = 0;
	
	public static void buildNetStartingFromBottom(int dimensionN) {
		
		iterator++;
		
		Nx1x1CuboidToFold curSimpleNet = new Nx1x1CuboidToFold(dimensionN);
		
		
		for(int i=0; i<Nx1x1CuboidToFold.levelOptions.length; i++) {
			
			for(int j=0; j<Nx1x1CuboidToFold.NUM_SIDE_BUMP_OPTIONS; j++) {
				
				
				//System.out.println( i + ", " + j);
				Coord2D coord = new Coord2D(i, j);
				
				boolean legal = curSimpleNet.addNextLevel(coord, null);
				
				if(legal) {
					int groundedIndex= Nx1x1StackTransitionTracker2.convertPrevGroundToIndex(
							i,
							curSimpleNet.cellsGrounded[0],
							curSimpleNet.cellsGroundedByLevelAbove[0]
					);
					
					System.out.println("grounded index: " + groundedIndex);
					
					buildNetNextLevel(1, i, groundedIndex, dimensionN);
				}
				
				curSimpleNet.removeCurrentTopLevel();
				
				
			}
		}
	}
	
	public static final int LAYER_IS_GROUNDED_INDEX = (int) (Math.pow(2, 4) - 1);
	
	public static void buildNetNextLevel(int curLevelIndexToFill, int optionUsedPrevLevel, int prevGroundedIndex, int dimensionN) {

		iterator++;
		
		if(curLevelIndexToFill > dimensionN - 1) {
			
			if(prevGroundedIndex == LAYER_IS_GROUNDED_INDEX) {
				numSolutions += 4;
			}
			return;
		}
		
		//Adding layer in-between:
		
		//TODO: this is redundant info:

		//TODO: get Next grounded in transition list:
		int transitionList[][] = Nx1x1StackTransitionTracker2.getTransitionListToLookup(optionUsedPrevLevel, prevGroundedIndex);
		
		
		for(int i=0; i<transitionList[0].length; i++) {
			
			//TODO: record enough to be able to reconstruct the net...
			buildNetNextLevel(curLevelIndexToFill + 1, transitionList[0][i], transitionList[2][i], dimensionN);
		
		}
		
	}
	
	
	public static Coord2D[] getOppositeCornersOfNet(boolean array[][]) {
		
		Coord2D corners[] = new Coord2D[2];
		
		int firsti = array.length;
		int lasti = 0;
		int firstj = array[0].length;
		int lastj = 0;
		
		
		for(int i = 0; i<array.length; i++) {
			for(int j=0; j<array[0].length; j++) {
				
				if(array[i][j]) {
					if(i > lasti) {
						lasti = i;
					}
					if(i < firsti) {
						firsti = i;
					}
					
					if(j > lastj) {
						lastj = j;
					}
					if(j < firstj) {
						firstj = j;
					}
				}
			}
		}
		
		corners[0] = new Coord2D(firsti, firstj);
		corners[1] = new Coord2D(lasti, lastj);
		
		
		
		return corners;
	}

}
