package NewModelWithIntersection.grainIteration;

import java.util.ArrayList;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForSimplePhase;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import NewModelWithIntersection.fastRegionCheck.FastRegionCheck;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class IntersectFinderWithMx5x1 {

	public static void main(String[] args) {
		

		//reallySimpleSearchWithMby5by1(7, 53, 1);
		
		// N/A (1)
		// 15
		// 161
		// 1387
		// N/A (5)
		// 82794
		//reallySimpleSearchWithMby5by1(6, 17, 1);
		
		// N = 27
		
		// N = 29 (5 other ones...)
		
		
	}
	
	public static SolutionResolverInterface solutionResolver;

	public static void reallySimpleSearchWithMby5by1(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		solutionResolver = new StandardResolverForSmallIntersectSolutions();
		
		
		CuboidToFoldOnGrained cuboidToBuild = new CuboidToFoldOnGrained(a, b, c);
		
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		if(cuboidToBuild.getNumCellsToFill() % 12 != 10) {
			System.out.println(cuboidToBuild.getNumCellsToFill());
			System.out.println("ERROR: trying to find intersect between Mx5x1 solution and a cuboid solution that doesn't have a surface area that matches any Mx5x1 cuboid.");
			return;
		}
		
		int m = (cuboidToBuild.getNumCellsToFill() - 10) / 12;
		CuboidToFoldOnGrained Mby5by1cuboidToBuild = new CuboidToFoldOnGrained(m, 5, 1);
		int mby5by1StartRotations[] = new int[3];
		mby5by1StartRotations[0] = 0;
		//I'm not sure if 2 is needed, but it can't hurt:
		mby5by1StartRotations[1] = 2;
		mby5by1StartRotations[2] = 3;
		
		long ret = 0L;
		for(int i=0; i<mby5by1StartRotations.length; i++) {
			ret += reallySimpleSearchWithMby5by1StartingPoint(cuboidToBuild, Mby5by1cuboidToBuild, mby5by1StartRotations[i]);
		}

		System.out.println("Done");
		System.out.println("Found " + ret + " different solutions if we ignore symmetric solutions");
		System.out.println();
		System.out.println("Done using the 2nd iteration (using pre-computed long arrays)");
		System.out.println("Found " + BasicUniqueCheckImproved.uniqList.size() + " unique solution.");
		
		System.out.println("Done for " + a + "x" + b + "x" + c);
	}
	private static long reallySimpleSearchWithMby5by1StartingPoint(CuboidToFoldOnGrained cuboidToBuild, CuboidToFoldOnGrained cuboidToBuildMby5by1, int mby5by1StartRotation) {

		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);
		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		
		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForSimplePhase.getUniqueRotationListsWithCellInfo(cuboidToBuild);
		
		int a = cuboidToBuild.dimensions[0];
		int b = cuboidToBuild.dimensions[1];
		int c = cuboidToBuild.dimensions[2];
		
		int m = (cuboidToBuild.getNumCellsToFill() - 10) / 12;
		
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
			
			cuboidToBuildMby5by1 = new CuboidToFoldOnGrained(m, 5, 1);
			cuboidToBuildMby5by1.initializeNewBottomIndexAndRotation(0, mby5by1StartRotation);
			
			ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, cuboidToBuildMby5by1);
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + otherCuboidStartIndex + " and a rotation index of " + otherCuboidStartRotation +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		
		return ret;

	}
	
	public static int getNumLayers(CuboidToFoldOnGrained cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrained cuboidToBuild, CuboidToFoldOnGrained cuboidToBuildMby5by1) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, cuboidToBuildMby5by1, 0, getNumLayers(cuboidToBuild));
	}

	public static long debugIt = 0;
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnGrained cuboidToBuild, CuboidToFoldOnGrained cuboidToBuildMby5by1, int layerIndex, int numLayers) {

		debugIt++;
		
		if(debugIt % 10000000L == 0) {
			System.out.println("Debug print current state of search:");
			cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
		}
		long ret = 0;
		
		//cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();

		if(layerIndex == numLayers) {
			
			if(cuboidToBuild.isTopCellAbleToBeAddedFast() && cuboidToBuildMby5by1.isTopCellAbleToBeAddedFast()) {

				for(int sideBump=6; sideBump <10; sideBump++) {
					if(cuboidToBuild.isTopCellAbleToBeAddedForSideBumpFast(sideBump)
							&& cuboidToBuildMby5by1.isTopCellAbleToBeAddedForSideBumpFast(sideBump)) {
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
			
			if(cuboidToBuild.isNewLayerValidSimpleFast(sideBump) && cuboidToBuildMby5by1.isNewLayerValidSimpleFast(sideBump)) {
				cuboidToBuild.addNewLayerFast(sideBump);
				cuboidToBuildMby5by1.addNewLayerFast(sideBump);
				reference.addNextLevel(new Coord2D(0, sideBump), null);

				ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, cuboidToBuildMby5by1, layerIndex + 1, numLayers);
	
				cuboidToBuild.removePrevLayerFast();
				cuboidToBuildMby5by1.removePrevLayerFast();
				reference.removeCurrentTopLevel();
			}
		}
		
		return ret;
	}
}
