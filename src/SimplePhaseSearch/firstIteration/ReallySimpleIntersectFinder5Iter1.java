package SimplePhaseSearch.firstIteration;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForSimplePhase;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;

public class ReallySimpleIntersectFinder5Iter1 {

	public static void main(String[] args) {
		
		//N: 5
		//reallySimpleSearch(6, 1, 1);
		
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
		reallySimpleSearch(5, 3, 1);

		//Found 54 unique solutions
		//reallySimpleSearch(7, 2, 1);
		
		
		//N: 13
		//Found 203 unique solutions
		//reallySimpleSearch(3, 3, 3);

		//Found 77 unique solutions
		//reallySimpleSearch(6, 3, 1);
		
		//N: 14
		//Found 13264 unique solutions
		//reallySimpleSearch(5, 4, 1);
		
		//Found 355 unique solutions
		//reallySimpleSearch(9, 2, 1);
		
		//N:15
		//Found  unique solutions
		//reallySimpleSearch(5, 3, 2);
		
		//Found  unique solutions
		//reallySimpleSearch(7, 3, 1);
		
		//N: 17
		//Found  unique solutions
		//reallySimpleSearch(5, 5, 1);
		
		//Found  unique solutions
		//reallySimpleSearch(8, 3, 1);
		
		//Found  unique solutions
		//reallySimpleSearch(11, 2, 1);
		
		//N: 19 (No luck)
		//Found  unique solutions
		//reallySimpleSearch(5, 3, 3);
		
		//Found  unique solutions
		//reallySimpleSearch(7, 4, 1);
		
		//Found  unique solutions
		//reallySimpleSearch(9, 3, 1);
		
		//N: 20
		//Found  unique solutions
		//reallySimpleSearch(6, 5, 1);

		//Found  unique solutions
		//reallySimpleSearch(7, 3, 2);
		
		//Found  unique solutions
		//reallySimpleSearch(13, 2, 1);
		//
		
		// N = 22
		//{5, 5, 2}, {6, 3, 3)

		//Found  unique solutions
		//reallySimpleSearch(5, 5, 2);

		//Found  unique solutions
		//reallySimpleSearch(6, 3, 3);
		
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
	

	public static void reallySimpleSearch(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		
		
		CuboidToFoldOnExtendedSimplePhase1 cuboidToBuild = new CuboidToFoldOnExtendedSimplePhase1(a, b, c);
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);

		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForSimplePhase.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		long ret = 0;
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {

			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			cuboidToBuild = new CuboidToFoldOnExtendedSimplePhase1(a, b, c);
			cuboidToBuild.initializeNewBottomIndexAndRotation(otherCuboidStartIndex, otherCuboidStartRotation);
			
			ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild);
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + otherCuboidStartIndex + " and a rotation index of " + otherCuboidStartRotation +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		System.out.println("Done");
		System.out.println("Found " + ret + " different solutions if we ignore symmetric solutions");
		System.out.println();
		System.out.println("Done using the 2nd iteration (using pre-computed long arrays)");
		System.out.println("Found " + BasicUniqueCheckImproved.uniqList.size() + " unique solution.");

	}
	
	public static int getNumLayers(CuboidToFoldOnExtendedSimplePhase1 cuboidToBuild) {
		
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtendedSimplePhase1 cuboidToBuild) {
		return findReallySimpleSolutionsRecursionFirstLayer(reference, cuboidToBuild, getNumLayers(cuboidToBuild));
	}

	public static final int FIRST_LEGAL_SIDE_BUMP_Nx1x1 = 3;
	public static final int WIDTH_Nx1x1 = 4;
	public static final int FIRST_CUR_LAYER_INDEX = 1;
	
	//TODO The "Nx1x1CuboidToFold reference" is doing a lot of overhead work that could be done on solution time
	//Fix this in a future iteration
	public static long findReallySimpleSolutionsRecursionFirstLayer(Nx1x1CuboidToFold reference, CuboidToFoldOnExtendedSimplePhase1 cuboidToBuild, int numLayers) {
		long ret = 0;
		
		if(numLayers == 0) {
			return 1L;
		}
		
		for(int sideBump=FIRST_LEGAL_SIDE_BUMP_Nx1x1; sideBump <FIRST_LEGAL_SIDE_BUMP_Nx1x1 + WIDTH_Nx1x1; sideBump++) {

			//TODO: hide (sideBump - 6) inside a function...
			if(CuboidToFoldOnExtendedSimplePhase1.LEVEL_OPTIONS[0][0 - (sideBump - 6)] != 1) {
				System.out.println("OOPS in findReallySimpleSolutionsRecursionFirstLayer!");
				System.exit(1);
			}
			
			
			cuboidToBuild.addFirstLayer(sideBump);

			
			reference.addNextLevel(new Coord2D(0, sideBump), null);

			ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, FIRST_CUR_LAYER_INDEX, numLayers);

			cuboidToBuild.leaveOnlyTheBottomCell();
			reference.removeCurrentTopLevel();
			
		}
		
		
		return ret;
	}
	
	public static int debugIterator = 0;
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtendedSimplePhase1 cuboidToBuild, int curLayerIndex, int numLayers) {

		debugIterator++;
		//System.out.println("Iteration number: " + debugIterator);
		
		long ret = 0;
		
		if(curLayerIndex == numLayers) {
			
			if(cuboidToBuild.isTopCellAbleToBeAddedFast()) {

				for(int sideBump=6; sideBump <10; sideBump++) {

					if(cuboidToBuild.isTopCellAbleToBeAddedForSideBumpFast(sideBump)) {
						ret++;
						
						reference.addNextLevel(new Coord2D(0, sideBump), null);
						if(
							BasicUniqueCheckImproved.isUnique(Utils.getOppositeCornersOfNet(reference.setupBoolArrayNet()), reference.setupBoolArrayNet()) ){
							System.out.println("Unique solution found");
							System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
							
							System.out.println(reference.toString());
							System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
							
							//TODO: remove sanity check once confident:
							cuboidToBuild.debugMakeSureCuboidIsFilledExceptForTop();
						}
						reference.removeCurrentTopLevel();
					}
				}
				
				if(ret > 0) {
					System.out.println("Found " + ret + " places for top from this net:");
					
					//cuboidToBuild.debugPrintCuboidOnFlatPaperAndValidateIt(reference);
					//System.out.println("----");
				}
			}
			return ret;
		}
		
		//TODO: go faster by iterating through the possible moves (i.e. (nextLayerState, sideBump) tuples)
		
		//Iterating over all 7 possible layer states:
		for(int nextLayerState = 0; nextLayerState<CuboidToFoldOnExtendedSimplePhase1.NUM_LAYER_STATES; nextLayerState++) {
		
		//TODO: iterating over 'simply-stacked' options only for debug:
		//for(int nextLayerState = 0; nextLayerState<1; nextLayerState++) {
		
			for(int sideBump=0; sideBump < CuboidToFoldOnExtendedSimplePhase1.NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
				
				
				if(cuboidToBuild.isNewLayerValidSimpleFast(nextLayerState, sideBump)) {
					
					/*if(nextLayerState != 0) {
						System.out.println("Test in");
						cuboidToBuild.printStateFromLongs();
						cuboidToBuild.printStateStuffDEBUG();
						
					}*/
					cuboidToBuild.addNewLayerFast(nextLayerState, sideBump);
					if(nextLayerState >= 4) {
						//TODO: hide this hack...
						reference.addNextLevel(new Coord2D(nextLayerState - 3, sideBump), null);
					} else {
						reference.addNextLevel(new Coord2D(nextLayerState, sideBump), null);
					}
					ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, curLayerIndex + 1, numLayers);
		
					cuboidToBuild.removePrevLayerFast();
					reference.removeCurrentTopLevel();
					

					/*
					if(nextLayerState != 0) {
						System.out.println("Test out");
						cuboidToBuild.printStateFromLongs();
						cuboidToBuild.printStateStuffDEBUG();
					}*/
				}
				
			}
		}
		
		return ret;
	}
}
