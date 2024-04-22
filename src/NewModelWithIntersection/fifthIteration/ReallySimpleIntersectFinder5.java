package NewModelWithIntersection.fifthIteration;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForSimplePhase;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import NewModelWithIntersection.fastRegionCheck.FastRegionCheck;
import NewModelWithIntersection.filterOutTwoTops.FilterOutTwoTops;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class ReallySimpleIntersectFinder5 {

	public static void main(String[] args) {
		
		//N: 5
		//38460 solutions (Makes sense because it's Nx1x1) (9702 unique solutions)
		//reallySimpleSearch(5, 1, 1);
		
		//26 solutions: (7 unique solutions)
		//reallySimpleSearch(3, 2, 1);

		//N: 7
		//6 solutions: (2 unique solutions)
		//reallySimpleSearch(3, 3, 1);
		

		//N: 8
		//404 solutions: (109 unique solutions)
		//reallySimpleSearch(5, 2, 1);
		

		//N: 9
		//42 solutions: (12 unique solutions)
		//reallySimpleSearch(4, 3, 1);
		

		//N: 10
		/*
		 * Looks like I had a typographical error for a long time. This number used to be 113, but after
		 * Landon correctly stated that there are 133 solutions, I
		 * reran this program without changes, and got 133 unique solutions...
		 * "Done
Found 498 different solutions if we ignore symmetric solutions

Done using the 2nd iteration (using pre-computed long arrays)
Found 133 unique solution."
*/
		//498 solutions: (133 unique solution)
		//reallySimpleSearch(3, 3, 2);
		
		
		//N: 11
		//2364 solutions: (591 unique solutions)
		//reallySimpleSearch(3, 5, 1);
		
		//74 solutions (19 unique solutions)
		//reallySimpleSearch(7, 2, 1);
		
		
		//N: 13
		//680 solutions: (175 unique soltions)
		reallySimpleSearch(3, 3, 3);
		
		//20 solutions: (6 unique solutions)
		//reallySimpleSearch(6, 3, 1);
		
		//N: 14
		//16504 solutions (That's promising!) (4182 unique solutions)
		//reallySimpleSearch(5, 4, 1);
		//564 solutions (152 unique solutions)
		//reallySimpleSearch(9, 2, 1);
		
		//N:15
		//722 solutions (184 unique solutions)
		//reallySimpleSearch(5, 3, 2);
		//36 solutions (9 unique solutions)
		//reallySimpleSearch(7, 3, 1);

		//N:16
		//3724 solutions (1285 unique solutions)
		//reallySimpleSearch(4, 3, 3);
		
		
		//N: 17
		// 115268 solutions (28817 uniq solutions) (This took 40 seconds)
		//reallySimpleSearch(5, 5, 1);
		
		//TODO: up to here...
		// 60 solutions (17 unique) (7 minutes)
		//reallySimpleSearch(8, 3, 1);
		//114 solutions (29 unique)
		//reallySimpleSearch(11, 2, 1);
		
		//N: 19 (No luck)
		// 8418 unique solution.
		//reallySimpleSearch(5, 3, 3);
		// 27 unique solutions (under 80 seconds)
		//reallySimpleSearch(7, 4, 1);
		
		//951 unique solution.
		//reallySimpleSearch(9, 3, 1);
		//
		
		//N: 20
		// Found 202106 unique solution.
		//reallySimpleSearch(6, 5, 1);
		// 296 unique solutions
		//reallySimpleSearch(7, 3, 2);
		//Found 798 non-unique solutions and 211 unique solution.
		//reallySimpleSearch(13, 2, 1);
		//
		
		//N = 21
		//Found 11 unique solution.
		//reallySimpleSearch(10, 3, 1);

		// N = 22
		//{5, 5, 2}, {6, 3, 3}
		// 24 uniq solutions
		//reallySimpleSearch(5, 5, 2);
		

		//Found 58891 unique solution.
		//reallySimpleSearch(6, 3, 3);
		
		// N = 23 (4 other ones...)
		/*
		 * 5 x 4 x 3: 94
7 x 5 x 1: 94
11 x 3 x 1: 94
15 x 2 x 1: 94
		 */
		
		
		//5, 4, 3 118 different solutions and 61 unique solution.
		//reallySimpleSearch(5, 4, 3);
		//System.exit(1);

		 //7, 5, 1
		//Found 1411798 unique solution.
		//reallySimpleSearch(7, 5, 1);
		
		 //11,3,1: (took about 20 hours)51 different solutions and  15 unique solution. (Latest change: less than 2 hours)
		//reallySimpleSearch(11, 3, 1);
		
		// Found 28 unique solution.
		//reallySimpleSearch(15, 2, 1);

		//N=24:
		//Found 6853 unique solution.
		//reallySimpleSearch(9, 4, 1);
		

		//UP TO HERE:
		
		// N = 25
		/*
		 * 7 x 3 x 3: 102
9 x 3 x 2: 102
12 x 3 x 1: 102
		 */

		//Found 410329 unique solution.
		//reallySimpleSearch(7, 3, 3);
		//System.exit(1);

		//Found 64 unique solution.
		//reallySimpleSearch(9, 3, 2);
		//System.exit(1);


		//UP TO HERE:
		//28 unique solution.
		//reallySimpleSearch(12, 3, 1);
		//System.exit(1);

		// N = 26
		//268 unique solution for 17x2x1 (just over 16 minutes)
		//reallySimpleSearch(17, 2, 1);
		

		//9,885,286 uniq solutions (and about 9,885,263 unique solutions after searching the cell left of 5x1 side)
		//reallySimpleSearch(8, 5, 1);
		
		// N = 27
		//Will need to optimize:
		//reallySimpleSearch(13, 3, 1);
		
		//Found 154 unique solution.
		//reallySimpleSearch(5, 5, 3);
		
		//Found 50 unique solution.
		//reallySimpleSearch(7, 6, 1);
		
		//N = 28

		//Found 2870327 unique solution.
		//reallySimpleSearch(8, 3, 3);
		
		// N = 29 (5 other ones...)
		//Found 165 unique solution.
		//reallySimpleSearch(7, 5, 2);
		
		//Found 36 unique solution.
		//reallySimpleSearch(11, 4, 1);
		
		
		//Not done yet:
		// N =30:
		//reallySimpleSearch(7, 4, 3);
		//reallySimpleSearch(11, 3, 2);
		
		
		//reallySimpleSearch(5, 5, 5);
	}
	
	public static SolutionResolverInterface solutionResolver;

	public static void reallySimpleSearch(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		solutionResolver = new StandardResolverForSmallIntersectSolutions();
		
		
		CuboidToFoldOnExtendedFaster5 cuboidToBuild = new CuboidToFoldOnExtendedFaster5(a, b, c);
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);

		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForSimplePhase.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		long ret = 0;
		
		FastRegionCheck fastRegionCheckSetup = cuboidToBuild.getFastRegionCheck();
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
			
			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			cuboidToBuild = new CuboidToFoldOnExtendedFaster5(a, b, c, fastRegionCheckSetup);
			cuboidToBuild.initializeNewBottomIndexAndRotation(otherCuboidStartIndex, otherCuboidStartRotation);
			
			if(fastRegionCheckSetup == null) {
				System.out.println("oops");
				System.exit(1);
			}
			
			ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild);
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + otherCuboidStartIndex + " and a rotation index of " + otherCuboidStartRotation +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		System.out.println("Done");
		System.out.println("Found " + ret + " different solutions if we ignore symmetric solutions");
		System.out.println();
		System.out.println("Done using the 2nd iteration (using pre-computed long arrays)");
		System.out.println("Found " + BasicUniqueCheckImproved.uniqList.size() + " unique solution.");

		System.out.println("Done for " + a + "x" + b + "x" + c);
	}
	
	public static int getNumLayers(CuboidToFoldOnExtendedFaster5 cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtendedFaster5 cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild), false);
	}
	
	public static final long DEBUG_MODULO =100000000L;
	public static long debug = 0;

	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtendedFaster5 cuboidToBuild, int layerIndex, int numLayers, boolean debugNope) {

		long ret = 0;
		if(debug % DEBUG_MODULO == 0) {
			cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
		}
		debug++;
		
		if(layerIndex == numLayers) {
			
			if(cuboidToBuild.isTopCellAbleToBeAddedFast()) {

				for(int sideBump=6; sideBump <10; sideBump++) {
					if(cuboidToBuild.isTopCellAbleToBeAddedForSideBumpFast(sideBump)) {
						ret++;
						
						reference.addNextLevel(new Coord2D(0, sideBump), null);
						if(BasicUniqueCheckImproved.isUnique(Utils.getOppositeCornersOfNet(reference.setupBoolArrayNet()), reference.setupBoolArrayNet()) ){
							System.out.println("Unique solution found");
							System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
							
							System.out.println(reference.toString());
							System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
							
							//TODO: this function is way too slow. What's going on?
							cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
							
							if(debugNope) {
								System.out.println("NOPE!");
							}
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
		
		for(int sideBump=3; sideBump <10; sideBump++) {
			if(layerIndex == 0 && sideBump > 6) {
				//TODO: make it faster by only starting recursion on the next layer...
				// I'm too lazy to do that for now.
				break;
			}
			
			if(cuboidToBuild.isNewLayerValidSimpleFast(sideBump)) {
				cuboidToBuild.addNewLayerFast(sideBump);
				reference.addNextLevel(new Coord2D(0, sideBump), null);

				
				if( cuboidToBuild.filterOutTwoTopsFaster4.isPossibleAfterBasicDeduction(cuboidToBuild.curState)) {
					ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, layerIndex + 1, numLayers, debugNope);
				} else {
					if(debugNope == false) {
						System.out.println("Nope at layerIndex: " + layerIndex + " debug: " + debug);
					}
					if(debug == 99313) {
						System.out.println("Debug state:");
						cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
						System.out.println("Debug here");
						cuboidToBuild.filterOutTwoTopsFaster4.isPossibleAfterBasicDeduction(cuboidToBuild.curState);
						System.out.println("END DEBUG");
					}
					
					ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, layerIndex + 1, numLayers, true);
						
				}
				
				
				cuboidToBuild.removePrevLayerFast();
				reference.removeCurrentTopLevel();
			}
		}
		
		return ret;
	}
}
