package NewModelWithIntersection.grainIteration;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForGrained;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class ReallySimpleIntersectFinder6Grained {

	public static void main(String[] args) {
		
		//reallySimpleSearch(2, 161, 1);
		
		//reallySimpleSearch(2, 53, 1);
		
		//reallySimpleSearch(2, 29, 1);
		//reallySimpleSearch(2, 25, 1);
		//reallySimpleSearch(3, 17, 1);
		//reallySimpleSearch(8, 5, 1);
		
		//Sunday Feb 25:
		//reallySimpleSearch(4, 13, 1);
		//reallySimpleSearch(3, 29, 1);

		//reallySimpleSearch(6, 17, 1);
		

		//reallySimpleSearch(2, 53, 1);
		
		//reallySimpleSearch(2, 29, 1);
		//reallySimpleSearch(2, 33, 1);
		//reallySimpleSearch(2, 37, 1);
		//reallySimpleSearch(2, 41, 1);
		
		//reallySimpleSearch(2, 45, 1);
		
		//reallySimpleSearch(2, 49, 1);
		//reallySimpleSearch(2, 53, 1);
		
		//reallySimpleSearch(2, 57, 1);
		
		//reallySimpleSearch(2, 61, 1);
		//reallySimpleSearch(2, 65, 1);

		//reallySimpleSearch(2, 69, 1);
		
		//reallySimpleSearch(2, 73, 1);
		//System.exit(1);
		
		//reallySimpleSearch(2, 17, 1);
		
		//reallySimpleSearch(8, 53, 1);
		
		//reallySimpleSearch(6, 53, 1);
		
		//reallySimpleSearch(2, 209, 1);
		//reallySimpleSearch(11, 53, 1);
		
		//reallySimpleSearch(10, 89, 1);
		//reallySimpleSearch(4, 53, 1);

		//reallySimpleSearch(14, 13, 1);
		
		//reallySimpleSearch(6, 17, 1);
		// N = 27
		
		// N = 29 (5 other ones...)
		
		//reallySimpleSearch(4, 125, 1);
		
		//613862 for 7, 17, 1
		
		//15, 161, 1387, x, 
		//reallySimpleSearch(2, 149, 1);
		
		//reallySimpleSearch(4, 17, 1);
		
		reallySimpleSearch(4, 17, 1);
		//15 (2)
		//161 (3)
		//1387 (4)
		//10884 (5)
		//82794 (6)
		//613862 (7)
		
		
	}
	
	public static SolutionResolverInterface solutionResolver;

	public static void reallySimpleSearch(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		solutionResolver = new StandardResolverForSmallIntersectSolutions();
		
		
		CuboidToFoldOnGrained cuboidToBuild = new CuboidToFoldOnGrained(a, b, c);
		
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);

		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForGrained.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		long ret = 0;
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
			
			
			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			//Only start from top:
			if(otherCuboidStartIndex % 4 != 0) {
				continue;
			}
			if(otherCuboidStartIndex > 0 && otherCuboidStartRotation % 2 != 0) {
				continue;
			}
			//Only start from top:
			if(otherCuboidStartIndex >= b) {
				continue;
			}
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			cuboidToBuild = new CuboidToFoldOnGrained(a, b, c);
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
	
	public static int getNumLayers(CuboidToFoldOnGrained cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrained cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild));
	}

	public static void debugSideBumps(CuboidToFoldOnGrained cuboidToBuild, int layerIndex, int sideBumpTest[]) {
		
		if(layerIndex != sideBumpTest.length) {
			return;
		}
		
		boolean goodSoFar = true;
		for(int i=0; i<sideBumpTest.length; i++) {
			if(cuboidToBuild.prevSideBumps[i] != sideBumpTest[i]) {
				goodSoFar = false;
			}
			//System.out.println(cuboidToBuild.prevSideBumps[i]);
		}
		//System.out.println();
		
		if(goodSoFar) {
			cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
			System.out.println("Layer index: " + layerIndex);
			//System.exit(1);
		}
		
	}
	public static long debugIt = 0;
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrained cuboidToBuild, int layerIndex, int numLayers) {

		int hello[] = cuboidToBuild.prevGroundedIndexes;
		
		//debugSideBumps(cuboidToBuild, layerIndex, new int[] {6, 7, 9, 8, 7, 8, 9, 7});
		debugSideBumps(cuboidToBuild, layerIndex, new int[] {6, 7, 9, 8, 7, 8, 9, 7});
		
		debugSideBumps(cuboidToBuild, layerIndex, new int[] {6, 7, 9, 8, 7, 8, 9, 7, 6, 7, 7});
		
		debugIt++;
		
		if(debugIt % 10000000L == 0) {
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
							
							System.out.println("Prev ground indexes:");
							for(int j=0; j<layerIndex; j++) {
								System.out.println(cuboidToBuild.prevGroundedIndexes[j]);
							}
							System.out.println("------------");
							System.out.println("------------");
							System.out.println("------------");
							
							System.out.println("Unique solution found");
							System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
							
							System.out.println(reference.toString());
							System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
							
							cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
							
							System.out.println("Debugging transition handler for top and bottom:");
							cuboidToBuild.printTopAndBottomHandlerDebug();
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

				ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, layerIndex + 1, numLayers);
	
				cuboidToBuild.removePrevLayerFast();
				reference.removeCurrentTopLevel();
			}
		}
		
		return ret;
	}
}
