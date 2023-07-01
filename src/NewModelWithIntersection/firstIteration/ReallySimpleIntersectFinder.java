package NewModelWithIntersection.firstIteration;

import Coord.Coord2D;
import NewModel.firstIteration.Nx1x1CuboidToFold;

public class ReallySimpleIntersectFinder {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		reallySimpleSearch(6, 1, 1);
	}

	public static void reallySimpleSearch(int a, int b, int c) {
		
		CuboidToFoldOnExtended cuboidToBuild = new CuboidToFoldOnExtended(a, b, c);
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);
		
		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		//TODO: be able to vary other cuboid start index and rotation
		//TODO: reuse PivotCell Description
		int otherCuboidStartIndex = 0;
		int otherCuboidStartRotation = 0;
		
		System.out.println("Start recursion:");
		cuboidToBuild.startBottomTODOConstructor(otherCuboidStartIndex, otherCuboidStartRotation);
		
		long ret = findReallySimpleSolutionsRecursion(reference, cuboidToBuild);
		
		System.out.println("Done");
		System.out.println("Found " + ret + " different solutions if we ignore symmetric solutions");
		
	}
	
	public static int getNumLayers(CuboidToFoldOnExtended cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtended cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild));
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnExtended cuboidToBuild, int layerIndex, int numLayers) {

		System.out.println("Layer Index: " + layerIndex);
		long ret = 0;
		
		if(layerIndex == numLayers) {
			
			if(cuboidToBuild.getNumPossibleTopCellPositions() > 0) {

				for(int sideBump=6; sideBump <10; sideBump++) {
					if(cuboidToBuild.tryToAddTopCell(sideBump)) {
						ret++;
					}
				}
				
				if(ret > 0) {
					System.out.println("Found " + ret + " places for top from this net:");
					//TODO: 2nd argument is the 2nd cubuoid start index. (take it from cuboidToBuild)
					//TODO: There should be a 3rd arg for 2nd cuboid rotation index.
					cuboidToBuild.debugPrintCuboidOnFlatPaperAndValidateIt(reference, 0);
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
			
			if(cuboidToBuild.isNewLayerValidSimple(sideBump)) {
				cuboidToBuild.addNewLayer(sideBump);
				reference.addNextLevel(new Coord2D(0, sideBump), null);
				
				//TODO: recursion
				ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, layerIndex + 1, numLayers);
				
				//TODO: remove new layer (TEST)
				cuboidToBuild.removePrevLayer(reference, layerIndex);
				reference.removeCurrentTopLevel();
			}
		}
		
		return ret;
	}
}
