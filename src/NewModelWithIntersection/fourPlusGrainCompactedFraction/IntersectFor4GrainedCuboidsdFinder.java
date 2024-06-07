package NewModelWithIntersection.fourPlusGrainCompactedFraction;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForGrained;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;


public class IntersectFor4GrainedCuboidsdFinder {

	public static SolutionResolverInterface solutionResolver;

	public static void findIntersectFor4GrainedCuboidsFinder(int cuboid1[], int cuboid2[], int cuboid3[], int cuboid4[], int k) {
		
		int largestWidth = cuboid4[1];
		
		BasicUniqueCheckImproved.resetUniqList();
		solutionResolver = new StandardResolverForSmallIntersectSolutions();
		
		
		CuboidToFoldOnGrainedWithOtherGrainsFraction cuboidToBuild = new CuboidToFoldOnGrainedWithOtherGrainsFraction(cuboid1, cuboid2, cuboid3, cuboid4, k);
		
		
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
			if(otherCuboidStartIndex >= largestWidth) {
				continue;
			}
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			
			
			// It should be 2 * (largestWidth + 1)
			for(int j=0; j< 2 * (largestWidth + 1); j++) {
				if(j % 100 == 0) {
					System.out.println("Trying with twist amount: " + j);
				}
				cuboidToBuild.initializeNewBottomIndexAndRotationAndBetweenLayerTwist(otherCuboidStartIndex, otherCuboidStartRotation, j);
				ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild);
			}
			
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + otherCuboidStartIndex + " and a rotation index of " + otherCuboidStartRotation +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		System.out.println("Done");
		System.out.println("Found " + ret + " different solutions if we ignore symmetric solutions");
		System.out.println();
		System.out.println("Done using the 2nd iteration (using pre-computed long arrays)");
		System.out.println("Found " + BasicUniqueCheckImproved.uniqList.size() + " unique solution.");
	}
	
	public static int getNumLayers(CuboidToFoldOnGrainedWithOtherGrainsFraction cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrainedWithOtherGrainsFraction cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild));
	}

	public static long debugIt = 0;
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrainedWithOtherGrainsFraction cuboidToBuild, int layerIndex, int numLayers) {

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
							
							System.out.println("Unique solution found");
							System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
							
							System.out.println(reference.toString());
							System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
							
							cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
							
							System.out.println("Debugging transition handler for top and bottom:");
							cuboidToBuild.printTopAndBottomHandlerDebug();
							
							boolean stopIt = true;
							for(int i=0; i<cuboidToBuild.getOtherWidthsToConsider().length; i++) {
								for(int j=i+1; i<cuboidToBuild.getOtherWidthsToConsider().length; i++) {
									
									if(cuboidToBuild.getOtherWidthsToConsider()[i] == cuboidToBuild.getOtherWidthsToConsider()[j]) {
										stopIt = false;
									}
								}
							}
							if(stopIt) {
								System.exit(1);
							}

							//TODO delete
							System.exit(1);
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
