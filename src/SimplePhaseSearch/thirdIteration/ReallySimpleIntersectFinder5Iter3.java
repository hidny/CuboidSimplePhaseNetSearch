package SimplePhaseSearch.thirdIteration;

import java.util.ArrayList;

import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForSimplePhase;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;

public class ReallySimpleIntersectFinder5Iter3 {

	public static void main(String[] args) {

		//N:6 1 1: 223160
		//reallySimpleSearch(6, 1, 1);

		//N: 5 1 1: 23604
		//reallySimpleSearch(5, 1, 1);
		
		//Found 34 unique solutions
		//reallySimpleSearch(3, 2, 1);

		//N: 7
		//Found 6 unique solutions
		//reallySimpleSearch(3, 3, 1);
		

		//N: 8
		//Found 197 unique solutions
		//reallySimpleSearch(5, 2, 1);
		

		//N: 9
		//Found 33 unique solutions
		//reallySimpleSearch(4, 3, 1);
		

		//N: 10
		//Found 158 unique solutions
		//reallySimpleSearch(3, 3, 2);
		
		
		//N: 11
		//Found 1401 unique solutions
		/* From 11x1x1AND5x3x1Only1BottomNeighbourSolutions.txt:
		 * Current UTC timestamp in milliseconds: 1675458353391
Final number of unique solutions: 1401
Current UTC timestamp in milliseconds: 1675458353391

		 */
		//reallySimpleSearch(5, 3, 1);

		//Found 54 unique solutions
		//reallySimpleSearch(7, 2, 1);
		
		
		//N: 13
		//Found 203 unique solutions
		//reallySimpleSearch(3, 3, 3);

		//Found 77 unique solutions
		//reallySimpleSearch(6, 3, 1);
		
		//N: 14
		//Found 13264 unique solution.
		//reallySimpleSearch(5, 4, 1);
		
		
		//Found 355 unique solutions
		//reallySimpleSearch(9, 2, 1);

		//N:15
		//Found 507 unique solution.
		//reallySimpleSearch(5, 3, 2);

		//Found 31 unique solutions
		//reallySimpleSearch(7, 3, 1);

		//N: 17
		//Found  unique solutions
		//reallySimpleSearch(5, 5, 1);

		//Found 54 unique solutions ( just over 2 hours on oct 12th)
		//reallySimpleSearch(8, 3, 1);
		
		//Found 89 unique solution. (17 minutes on oct 12th)
		//reallySimpleSearch(11, 2, 1);

		//--------
		//N: 19
		//Found 11290 unique solution. (8 hours and 30 minutes on oct 12th)
		//reallySimpleSearch(5, 3, 3);
		
		//Found 52 unique solution. (12 hours and 30 minutes on oct 12th)
		//reallySimpleSearch(7, 4, 1);
		
		//Found  unique solutions
		//reallySimpleSearch(9, 3, 1);
		
		//N: 20
		//Found  unique solution
		//reallySimpleSearch(6, 5, 1);

		//Found 581 unique solution.
		//reallySimpleSearch(7, 3, 2);
		
		//Found 507 unique solutions (3 hours and 20 minutes on oct 12th)
		//reallySimpleSearch(13, 2, 1);
		//
		
		// N = 22
		//{5, 5, 2}, {6, 3, 3)

		//Found  unique solutions
		//reallySimpleSearch(5, 5, 2);

		//Found  unique solutions
		reallySimpleSearch(6, 3, 3);
		
		// N = 23 (4 other ones...)
		/*
		 * 5 x 4 x 3: 94
7 x 5 x 1: 94
11 x 3 x 1: 94
15 x 2 x 1: 94
		 */
		
		 //11,3,1:
		//5, 4, 3

		//Found  unique solutions
		//reallySimpleSearch(5, 4, 3);

		//Found  unique solutions
		//reallySimpleSearch(7, 5, 1);

		//Found  unique solutions
		//reallySimpleSearch(11, 3, 1);
		
		//Found  unique solutions
		//reallySimpleSearch(15, 2, 1);
		

		
		// N = 25
		/*
7 x 3 x 3: 102
9 x 3 x 2: 102
12 x 3 x 1: 102
		 */
		//Found  unique solutions
		//reallySimpleSearch(7, 3, 3);
		
		//Found  unique solutions
		//reallySimpleSearch(9, 3, 2);
		
		//Found  unique solutions
		//reallySimpleSearch(12, 3, 1);

		// N = 26

		//Found  unique solutions
		//reallySimpleSearch(17, 2, 1);

		//Found  unique solutions
		//reallySimpleSearch(8, 5, 1);
		
		System.out.println("END");
	}
	

	public static int debugBottomIndex = -1;
	public static int debugAboveBottomIndex = -1;

	public static void reallySimpleSearch(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		
		
		CuboidToFoldOnExtendedSimplePhase3 cuboidToBuild = new CuboidToFoldOnExtendedSimplePhase3(a, b, c);
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForSimplePhase.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		long ret = 0;
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {

			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			cuboidToBuild = new CuboidToFoldOnExtendedSimplePhase3(a, b, c);
			cuboidToBuild.initializeNewBottomIndexAndRotation(otherCuboidStartIndex, otherCuboidStartRotation);
			
			debugBottomIndex = otherCuboidStartIndex;
			debugAboveBottomIndex = cuboidToBuild.getIndexAboveIndex(otherCuboidStartIndex, otherCuboidStartRotation);
			ret += findReallySimpleSolutionsRecursion(cuboidToBuild);
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + otherCuboidStartIndex + " and a rotation index of " + otherCuboidStartRotation +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		System.out.println("Done");
		System.out.println("Found " + ret + " different solutions if we ignore symmetric solutions");
		System.out.println();
		System.out.println("Done using the 2nd iteration (using pre-computed long arrays)");
		System.out.println("Found " + BasicUniqueCheckImproved.uniqList.size() + " unique solution.");

	}
	
	public static int getNumLayers(CuboidToFoldOnExtendedSimplePhase3 cuboidToBuild) {
		
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(CuboidToFoldOnExtendedSimplePhase3 cuboidToBuild) {
		return findReallySimpleSolutionsRecursionFirstLayer(cuboidToBuild, getNumLayers(cuboidToBuild));
	}

	public static final int FIRST_LEGAL_SIDE_BUMP_Nx1x1 = 3;
	public static final int WIDTH_Nx1x1 = 4;
	public static final int FIRST_CUR_LAYER_INDEX = 1;
	
	
	public static long findReallySimpleSolutionsRecursionFirstLayer(CuboidToFoldOnExtendedSimplePhase3 cuboidToBuild, int numLayers) {
		long ret = 0;
		
		if(numLayers == 0) {
			return 1L;
		}
		
		for(int sideBump=FIRST_LEGAL_SIDE_BUMP_Nx1x1; sideBump <FIRST_LEGAL_SIDE_BUMP_Nx1x1 + WIDTH_Nx1x1; sideBump++) {

			//TODO: hide (sideBump - 6) inside a function...
			if(CuboidToFoldOnExtendedSimplePhase3.LEVEL_OPTIONS[0][0 - (sideBump - 6)] != 1) {
				System.out.println("OOPS in findReallySimpleSolutionsRecursionFirstLayer!");
				System.exit(1);
			}
			
			cuboidToBuild.addFirstLayer(sideBump);

			ret += findReallySimpleSolutionsRecursion(cuboidToBuild, FIRST_CUR_LAYER_INDEX, numLayers, 0);

			cuboidToBuild.leaveOnlyTheBottomCell();
			
		}
		
		
		return ret;
	}
	
	public static int debugIterator = 0;
	public static long findReallySimpleSolutionsRecursion(CuboidToFoldOnExtendedSimplePhase3 cuboidToBuild, int curLayerIndex, int numLayers, int prevLayerStateIndex) {

		debugIterator++;
		//System.out.println("Iteration number: " + debugIterator);
		
		long ret = 0;
		
		if(curLayerIndex == numLayers) {
			
			if(cuboidToBuild.isTopCellAbleToBeAddedFast()) {

				for(int topSideBump=6; topSideBump<10; topSideBump++) {

					if(cuboidToBuild.isTopCellAbleToBeAddedForSideBumpFast(topSideBump)) {
						ret++;
						
						Nx1x1CuboidToFold reference = cuboidToBuild.createSolutionToPrint(topSideBump);
						
						if(
							BasicUniqueCheckImproved.isUnique(Utils.getOppositeCornersOfNet(reference.setupBoolArrayNet()), reference.setupBoolArrayNet()) ){
							System.out.println("Unique solution found");
							System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
							
							System.out.println(reference.toString());
							
							System.out.println("bottom index: " + debugBottomIndex);
							System.out.println("above bottom index: " + debugAboveBottomIndex);
							System.out.println();
							System.out.println("top index: " + cuboidToBuild.debugGetTopCell());
							System.out.println();
							
							System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
							
							
							//TODO: remove sanity check once confident:
							cuboidToBuild.debugMakeSureCuboidIsFilledExceptForTop();
						}
					}
				}
				
				if(ret > 0) {
					System.out.println("Found " + ret + " places for top from this net:");
					
				}
			}
			return ret;
		}
		
		//Go faster by iterating through the possible moves (i.e. (nextLayerState, sideBump) tuples)
		for(int i=0; i<cuboidToBuild.nextLayerPossibilities[prevLayerStateIndex].length; i++) {

			int nextLayerState = cuboidToBuild.nextLayerPossibilities[prevLayerStateIndex][i].i;
			int sideBump = cuboidToBuild.nextLayerPossibilities[prevLayerStateIndex][i].j;
			
			if(cuboidToBuild.isNewLayerValidSimpleFast(nextLayerState, sideBump)) {
				
				cuboidToBuild.addNewLayerFast(nextLayerState, sideBump);
				
				ret += findReallySimpleSolutionsRecursion(cuboidToBuild, curLayerIndex + 1, numLayers, nextLayerState);
	
				cuboidToBuild.removePrevLayerFast();
				

			}
			
		}
		
		return ret;
	}
}
