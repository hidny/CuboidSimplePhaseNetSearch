package NewSimpleNetAlgo;

import java.util.ArrayList;
import java.util.HashMap;

import Coord.Coord2D;
import GraphUtils.PivotCellDescription;
import Model.CuboidToFoldOn;
import Model.Utils;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class SimpleIntersectSearch {
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_NEIGHBOURS = NUM_ROTATIONS;
	
	public static void solveCuboidIntersections(CuboidToFoldOn cuboidToWrap, CuboidToFoldOn cuboidToBringAlong) {
		solveCuboidIntersections(cuboidToWrap, cuboidToBringAlong, true);
	}
	
	public static void solveCuboidIntersections(CuboidToFoldOn cuboidToBuild, CuboidToFoldOn cuboidToBringAlong, boolean skipSymmetries) {
		SolutionResolverInterface solutionResolver = null;
		
		
		if(Utils.getTotalArea(cuboidToBuild.getDimensions()) != Utils.getTotalArea(cuboidToBringAlong.getDimensions())) {
			System.out.println("ERROR: The two cuboid to intersect don't have the same area.");
			System.exit(1);
		}
		
		// Set the solution resolver to different things depending on the size of the cuboid:
		
		if(Utils.cuboidDimensionsMatch(cuboidToBuild, cuboidToBringAlong)) {
			//solutionResolver = new StandardResolverUsingMemory();
			solutionResolver = new StandardResolverForSmallIntersectSolutions();
		} else {
			solutionResolver = new StandardResolverForSmallIntersectSolutions();
		}
		
		
		solveCuboidIntersections(cuboidToBuild, cuboidToBringAlong, skipSymmetries, solutionResolver);
	}
	
	public static void solveCuboidIntersections(CuboidToFoldOn cuboidToBuild, CuboidToFoldOn cuboidToBringAlong, boolean skipSymmetries, SolutionResolverInterface solutionResolver) {
		
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
		//cube.set start location 0 and rotation 0
		

		//TODO: LATER use hashes to help.. (record potential expansions, and no-nos...)
		Coord2D paperToDevelop[] = new Coord2D[Utils.getTotalArea(cuboidToBuild.getDimensions())];
		for(int i=0; i<paperToDevelop.length; i++) {
			paperToDevelop[i] = null;
		}
		
		int GRID_SIZE = 2*Utils.getTotalArea(cuboidToBuild.getDimensions());
	
		boolean paperUsed[][] = new boolean[GRID_SIZE][GRID_SIZE];
		int indexCuboidOnPaper[][] = new int[GRID_SIZE][GRID_SIZE];

		int indexCuboidOnPaper2ndCuboid[][] = new int[GRID_SIZE][GRID_SIZE];
		
		for(int i=0; i<paperUsed.length; i++) {
			for(int j=0; j<paperUsed[0].length; j++) {
				paperUsed[i][j] = false;
				indexCuboidOnPaper[i][j] = -1;
				indexCuboidOnPaper2ndCuboid[i][j] = -1;
			}
		}

		//Default start location GRID_SIZE / 2, GRID_SIZE / 2
		int START_I = GRID_SIZE/2;
		int START_J = GRID_SIZE/2;
		
		CuboidToFoldOn cuboid = new CuboidToFoldOn(cuboidToBuild);
		//Insert start cell:
		
		//Once this reaches the total area, we're done!
		int numCellsUsedDepth = 0;

		int START_INDEX = 0;
		int START_ROTATION = 0;
		paperUsed[START_I][START_J] = true;
		paperToDevelop[numCellsUsedDepth] = new Coord2D(START_I, START_J);
		
		cuboid.setCell(START_INDEX, START_ROTATION);
		indexCuboidOnPaper[START_I][START_J] = START_INDEX;
		numCellsUsedDepth += 1;
		
		
		//TODO: Later try intersecting with all of them at once, so it's easier to get distinct solutions,
		// and maybe it's faster?

		//TODO: 2nd one
		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescription.getUniqueRotationListsWithCellInfo(cuboidToBringAlong);
		
		System.out.println("Num starting points and rotations to check: " + startingPointsAndRotationsToCheck.size());
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
		
			int startIndex2ndCuboid =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int startRotation2ndCuboid = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			CuboidToFoldOn cuboidToBringAlongStartRot = new CuboidToFoldOn(cuboidToBringAlong);

			cuboidToBringAlongStartRot.setCell(startIndex2ndCuboid, startRotation2ndCuboid);
			indexCuboidOnPaper2ndCuboid[START_I][START_J] = startIndex2ndCuboid;
			
			int topBottombridgeUsedNx1x1[] = new int[Utils.getTotalArea(cuboidToBuild.getDimensions())];
			
			long debugIterations[] = new long[Utils.getTotalArea(cuboidToBuild.getDimensions())];
			
			HashMap<Integer, Integer> CellIndexToOrderOfDev = new HashMap <Integer, Integer>();
			CellIndexToOrderOfDev.put(0, 0);
		
			doDepthFirstSearch(paperToDevelop, indexCuboidOnPaper, paperUsed, cuboid, numCellsUsedDepth, -1L, skipSymmetries, solutionResolver, cuboidToBringAlongStartRot, indexCuboidOnPaper2ndCuboid, topBottombridgeUsedNx1x1, false, debugIterations, CellIndexToOrderOfDev, 0, 0);
			
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + startIndex2ndCuboid + " and a rotation index of " + startRotation2ndCuboid +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		
		//TODO: end todo 2nd one
		
		
		System.out.println("Final number of unique solutions: " + solutionResolver.getNumUniqueFound());
	}

	
	
	public static final int nugdeBasedOnRotation[][] = {{-1, 0, 1, 0}, {0, 1, 0 , -1}};
	public static long numIterations = 0;
	
	public static long doDepthFirstSearch(Coord2D paperToDevelop[], int indexCuboidonPaper[][], boolean paperUsed[][], CuboidToFoldOn cuboid, int numCellsUsedDepth,
			long limitDupSolutions, boolean skipSymmetries, SolutionResolverInterface solutionResolver, CuboidToFoldOn cuboidToBringAlongStartRot, int indexCuboidOnPaper2ndCuboid[][],
			int topBottombridgeUsedNx1x1[],
			boolean debugNope, long debugIterations[],
			HashMap <Integer, Integer> CellIndexToOrderOfDev, int minIndexToUse, int minRotationToUse) {

		return -1L;
	}
	
	public static final int ONE_EIGHTY_ROTATION = 2;
	
	
	public static boolean cantAddCellBecauseOfOtherPaperNeighbours(Coord2D paperToDevelop[], int indexCuboidonPaper[][],
			boolean paperUsed[][], CuboidToFoldOn cuboid, int numCellsUsedDepth,
			HashMap <Integer, Integer> CellIndexToOrderOfDev, int minIndexToUse, int minRotationToUse,
			int indexNewCell, int new_i, int new_j, int curOrderedIndexToUse
		) {	

		//TODO: simplify it
		
		//TODO: make extensive use of Nx1x1CuboidToFold
		//Maybe this function will try to build the Nx1x1 cuboid recursively, level by level
		// For example, for the 13x1x1 cuboid, there are 13 levels.
		
		return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
