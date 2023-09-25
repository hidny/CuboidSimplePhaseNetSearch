package SimplePhaseSearch.firstIteration;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForNx1x1;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

//TODO: I made small changes to this. Please test new findReallySimpleSolutionsRecursionFirstLayer
// function.
public class ReallySimpleIntersectFinder5 {

	public static void main(String[] args) {
		
		//N: 5
		reallySimpleSearch(2, 1, 1);
		
		//reallySimpleSearch(3, 2, 1);

		//N: 7
		//reallySimpleSearch(3, 3, 1);
		

		//N: 8
		//reallySimpleSearch(5, 2, 1);
		

		//N: 9
		//X solutions: (Y unique solutions)
		//reallySimpleSearch(4, 3, 1);
		

		//N: 10
		//reallySimpleSearch(3, 3, 2);
		
		
		//N: 11
		//reallySimpleSearch(5, 3, 1);
		
		//reallySimpleSearch(7, 2, 1);
		
		
		//N: 13
		//reallySimpleSearch(3, 3, 3);
		
		//reallySimpleSearch(6, 3, 1);
		
		//N: 14
		//reallySimpleSearch(5, 4, 1);
		//reallySimpleSearch(9, 2, 1);
		
		//N:15
		//reallySimpleSearch(5, 3, 2);
		//reallySimpleSearch(7, 3, 1);
		
		//N: 17
		//reallySimpleSearch(5, 5, 1);
		//reallySimpleSearch(8, 3, 1);
		//reallySimpleSearch(11, 2, 1);
		
		//N: 19 (No luck)
		//reallySimpleSearch(5, 3, 3);
		//reallySimpleSearch(7, 4, 1);
		//reallySimpleSearch(9, 3, 1);
		
		//N: 20
		//reallySimpleSearch(6, 5, 1);
		//reallySimpleSearch(7, 3, 2);
		//reallySimpleSearch(13, 2, 1);
		//
		
		// N = 22
		//{5, 5, 2}, {6, 3, 3)
		
		//reallySimpleSearch(5, 5, 2);
		
		//reallySimpleSearch(6, 3, 3);
		
		// N = 23 (4 other ones...)
		/*
		 * 5 x 4 x 3: 94
7 x 5 x 1: 94
11 x 3 x 1: 94
15 x 2 x 1: 94
		 */
		
		 //11,3,1:
		//reallySimpleSearch(11, 3, 1);
		
		//5, 4, 3
		
		//reallySimpleSearch(5, 4, 3);
		
		//reallySimpleSearch(15, 2, 1);
		
		// N = 25
		/*
7 x 3 x 3: 102
9 x 3 x 2: 102
12 x 3 x 1: 102
		 */

		// N = 26
		//268 unique solution for 17x2x1
		//reallySimpleSearch(17, 2, 1);
		
		// N = 27
		
		// N = 29 (5 other ones...)
		
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

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForNx1x1.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		long ret = 0;
		
		//TODO: put it back after you're done testing!
		//for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
		for(int i=0; i<1; i++) {

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
	
	//TODO Nx1x1CuboidToFold reference is doing a lot of overhead work that could be done on solution time
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
		System.out.println("hello " + debugIterator);
		
		long ret = 0;
		
		if(curLayerIndex == numLayers) {
			
			
			if(cuboidToBuild.isTopCellAbleToBeAddedFast()) {

				for(int sideBump=6; sideBump <10; sideBump++) {

					if(cuboidToBuild.isTopCellAbleToBeAddedForSideBumpFast(sideBump)) {
						ret++;
						
						reference.addNextLevel(new Coord2D(0, sideBump), null);
						//TODO: remove 0 == 0 when done testing
						if(0 == 0 || BasicUniqueCheckImproved.isUnique(Utils.getOppositeCornersOfNet(reference.setupBoolArrayNet()), reference.setupBoolArrayNet()) ){
							System.out.println("Unique solution found");
							System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
							
							System.out.println(reference.toString());
							System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
						}
						reference.removeCurrentTopLevel();
					}

				}
				
				if(ret > 0) {
					System.out.println("Found " + ret + " places for top from this net:");
					
					//TODO: Make a debug function:
					//cuboidToBuild.debugPrintCuboidOnFlatPaperAndValidateIt(reference);
					System.out.println("----");
				}
			}
			return ret;
		}
		
		//TODO: go faster by iterating through the possible moves (i.e. (nextLayerState, sideBump) tuples)
		
		//TODO: Go back to iterating over layer states once done debug
		//for(int nextLayerState = 0; nextLayerState<CuboidToFoldOnExtendedSimplePhase1.NUM_LAYER_STATES; nextLayerState++) {
		for(int nextLayerState = 0; nextLayerState<1; nextLayerState++) {
		
			for(int sideBump=0; sideBump <CuboidToFoldOnExtendedSimplePhase1.NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
				
				
				if(cuboidToBuild.isNewLayerValidSimpleFast(nextLayerState, sideBump)) {
					cuboidToBuild.addNewLayerFast(nextLayerState, sideBump);
					reference.addNextLevel(new Coord2D(0, sideBump), null);
	
					ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, curLayerIndex + 1, numLayers);
		
					cuboidToBuild.removePrevLayerFast();
					reference.removeCurrentTopLevel();
				}
				
			}
		}
		
		return ret;
	}
}
