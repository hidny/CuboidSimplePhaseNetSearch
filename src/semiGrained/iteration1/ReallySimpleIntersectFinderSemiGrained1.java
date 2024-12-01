package semiGrained.iteration1;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForGrained;
import GraphUtils.PivotCellDescriptionForSimplePhase;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class ReallySimpleIntersectFinderSemiGrained1 {

	public static boolean VERBOSE = false;
	
	public static void main(String[] args) {
		
		
		reallySimpleSearch(4, 7, 3);
		
		//Found 0 unique solution.
		//Done for 1x3x3
		
		//Found 31 unique solution.
		//Done for 2x3x3
		
		//Found N/A unique solution.
		//Done for 3x3x3
		
		//Found 1231 unique solution.
		//Done for 4x3x3
		
		//Found 8399 unique solution.
		//Done for 5x3x3
		// (expected about 8,418)
		
		
		//Found 58799 unique solution.
		//Done for 6x3x3
		//Took 33 minutes
		
		//Done using the 2nd iteration (using pre-computed long arrays)
		//Found 58799 unique solution.
		//Took 1 minute
		
		// Expected close to 58,891
		
		//Found 410031 unique solution.
		//Done for 7x3x3
		//Took 13 minutes
		//(Expected about 410,329)
		
		//Found 2870223 unique solution.
		//Done for 8x3x3
		//Took 8 minutes with verbose off.
		//(Expected about 2,870,327)
		
		
		/*
		 * Found 2988 different solutions if we ignore symmetric solutions

Done using the 2nd iteration (using pre-computed long arrays)
Found 1113 unique solution.
Done for 4x7x3
25 minutes
		 */
	}
	
	public static SolutionResolverInterface solutionResolver;

	public static void reallySimpleSearch(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		solutionResolver = new StandardResolverForSmallIntersectSolutions();
		
		
		CuboidToFoldOnSemiGrained cuboidToBuild = new CuboidToFoldOnSemiGrained(a, b, c);
		
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);

		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		//TODO: For efficiency, maybe filter for the 1st ring index?
		//Also, this doesn't work for 3x3x3 because of the symmetries.
		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForSimplePhase.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		long ret = 0;
		
		System.out.println("Size Test: " + startingPointsAndRotationsToCheck.size());
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
			
			
			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			//Only start from ring index 0 and rotation 2 for Mx(3+4M)x3
			if(cuboidToBuild.getIndexToRingIndex(otherCuboidStartIndex) != 0
					|| otherCuboidStartRotation != 2) {
				continue;
			}
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			cuboidToBuild = new CuboidToFoldOnSemiGrained(a, b, c);
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

		System.out.println("Done for " + a + "x" + b + "x" + c);
	}
	
	public static int getNumLayers(CuboidToFoldOnSemiGrained cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnSemiGrained cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild));
	}

	public static long debugIt = 0;
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnSemiGrained cuboidToBuild, int layerIndex, int numLayers) {

		
		debugIt++;
		
		if(debugIt % 100000000L == 0) {
			System.out.println("Debug print current state of search:");
			cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
		}
		long ret = 0;
		
		//cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();

		if(layerIndex == numLayers) {
			
			if(cuboidToBuild.isTopCellAbleToBeAddedFast()) {

				for(int sideBump=6; sideBump <10; sideBump++) {
					if(cuboidToBuild.isTopCellAbleToBeAddedForSideBumpFast(sideBump)) {
						ret++;
						
						reference.addNextLevel(new Coord2D(0, sideBump), null);
						if(BasicUniqueCheckImproved.isUnique(Utils.getOppositeCornersOfNet(reference.setupBoolArrayNet()), reference.setupBoolArrayNet()) ){
							
							if(VERBOSE) {
								
								System.out.println("Unique solution found");
								System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
								
								cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();

								System.out.println(reference.toString());
								System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
								
								//TODO:
								//System.out.println("Debugging transition handler for top and bottom:");
								//cuboidToBuild.printTopAndBottomHandlerDebug();
								
							} else {
								if(BasicUniqueCheckImproved.uniqList.size() % 10000 == 0) {
									System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
									
								}
							}
							
						}
						reference.removeCurrentTopLevel();
					}
				}
				
				if(ret > 0 && VERBOSE) {
					System.out.println("----");
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

				ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, layerIndex + 1, numLayers);
	
				cuboidToBuild.removePrevLayerFast();
				reference.removeCurrentTopLevel();
			}
		}
		
		return ret;
	}
}
