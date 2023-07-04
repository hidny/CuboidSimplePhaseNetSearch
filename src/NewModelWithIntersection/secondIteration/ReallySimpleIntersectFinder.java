package NewModelWithIntersection.secondIteration;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class ReallySimpleIntersectFinder {

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
		//498 solutions: (113 unique solutions)
		//reallySimpleSearch(3, 3, 2);
		
		
		//N: 11
		//2364 solutions: (591 unique solutions)
		//reallySimpleSearch(5, 3, 1);
		
		//74 solutions (19 unique solutions)
		//reallySimpleSearch(7, 2, 1);
		
		
		//N: 13
		//680 solutions: (175 unique soltions)
		//reallySimpleSearch(3, 3, 3);
		
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
		
		//N: 17
		// 115268 solutions
		reallySimpleSearch(5, 5, 1);
		// 60 solutions (17 unique)
		//reallySimpleSearch(8, 3, 1);
		
		//reallySimpleSearch(11, 2, 1);
		
		//N: 19
		//
		//reallySimpleSearch(5, 3, 3);
		//
		//reallySimpleSearch(7, 4, 1);
		//
		//reallySimpleSearch(9, 3, 1);
		//
		
		//N: 20
		//
		//reallySimpleSearch(6, 5, 1);
		//
		//reallySimpleSearch(7, 3, 2);
		//
		//reallySimpleSearch(13, 2, 1);
		//
		
		// N = 22
		
		// N = 23 (4 other ones...)
		
		// N = 25
		
		// N = 26
		
		// N = 27
		
		// N = 29 (5 other ones...)
	}
	
	public static SolutionResolverInterface solutionResolver = new StandardResolverForSmallIntersectSolutions();

	public static void reallySimpleSearch(int a, int b, int c) {
		
		CuboidToFoldOnExtendedFaster cuboidToBuild = new CuboidToFoldOnExtendedFaster(a, b, c);
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);
		
		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		
		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescription.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		long ret = 0;
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
			
			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			cuboidToBuild = new CuboidToFoldOnExtendedFaster(a, b, c);
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
	
	public static int getNumLayers(CuboidToFoldOnExtendedFaster cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtendedFaster cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild));
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtendedFaster cuboidToBuild, int layerIndex, int numLayers) {

		long ret = 0;
		
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
