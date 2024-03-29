package NewModelWithIntersection.grainSpiralIteration;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForNx1x1;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import NewModelWithIntersection.fastRegionCheck.FastRegionCheck;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class ReallySimpleIntersectFinderSpiral {

	public static void main(String[] args) {
		
		//System.exit(1);
		//reallySimpleSearch(2, 161, 1);
		
		//reallySimpleSearch(2, 53, 1);
		
		//reallySimpleSearch(2, 29, 1);
		//reallySimpleSearch(2, 25, 1);
		//reallySimpleSearch(3, 17, 1);
		//reallySimpleSearch(20, 5, 1);
		

		//reallySimpleSearch(6, 53, 1);
		
		//reallySimpleSearch(20, 53, 1);
		
		/*for(int i=6; i<125; i++) {
			reallySimpleSearch(i, 53, 1);
		}
		

		for(int i=1; i<6; i++) {
			reallySimpleSearch(i, 53, 1);
		}*/
		
		//Sunday Feb 25:
		//reallySimpleSearch(4, 13, 1);
		//reallySimpleSearch(3, 29, 1);

		//reallySimpleSearch(6, 17, 1);
		

		//reallySimpleSearch(4, 21, 1);
		
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
		
		// Ryuhei mentioned this one for some reason...
		reallySimpleSearch(14, 13, 1);
		
		// N = 27
		
		// N = 29 (5 other ones...)
		
		
	}
	
	public static SolutionResolverInterface solutionResolver;

	public static void reallySimpleSearch(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		solutionResolver = new StandardResolverForSmallIntersectSolutions();
		
		
		CuboidToFoldOnGrainedSpiral cuboidToBuild = new CuboidToFoldOnGrainedSpiral(a, b, c, null);
		
		FastRegionCheck fastRegionCheck = cuboidToBuild.getFastRegionCheck();
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);

		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForNx1x1.getUniqueRotationListsWithCellInfo(cuboidToBuild, false);
		
		long ret = 0;
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
			
			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			//Only start from top:
			if(otherCuboidStartIndex >= b) {
				continue;
			}
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			cuboidToBuild = new CuboidToFoldOnGrainedSpiral(a, b, c, fastRegionCheck);
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
	
	public static int getNumLayers(CuboidToFoldOnGrainedSpiral cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrainedSpiral cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild));
	}

	public static long debugIt = 0;
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrainedSpiral cuboidToBuild, int layerIndex, int numLayers) {

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
							System.out.println("Unique solution found");
							System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
							
							System.out.println(reference.toString());
							System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
							
							cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
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
