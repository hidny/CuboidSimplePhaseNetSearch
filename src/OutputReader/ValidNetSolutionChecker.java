package OutputReader;


import Model.CuboidToFoldOn;
import Model.Utils;

import java.util.Hashtable;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class ValidNetSolutionChecker {

	//TODO: test this!
	
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_NEIGHBOURS = NUM_ROTATIONS;
	public static final int nugdeBasedOnRotation[][] = {{-1, 0, 1, 0}, {0, 1, 0 , -1}};

	//TODO: somehow modify it to find solutions with invisible cuts.
	//That's easier said than done! I could start by outputting where the invisible cuts are supposed to be...
	//I might do that much much later.
	
	//Possible optimizations that I'm actively ignoring:
	// (i.e: Optimizations that are more trouble than they are worth if we're just looking at solutions from output files)
	// 1) I could technically make this faster by asking it to ignore similar cuboid start locations
	// 2) I could technically make this faster by not redeclaring the objects at every call
	// These optimizations will make the code slightly harder to read, and won't really help because the bottle-neck
	// is how fast the file with the solutions can be read.
	
	public static boolean hasSolution(int dimensionsCuboid[], boolean netToReplicate[][]) {
		return hasSolution(dimensionsCuboid, netToReplicate, false);
	}
	public static boolean hasSolution(int dimensionsCuboid[], boolean netToReplicate[][], boolean verbose) {

		int startI = -1;
		int startJ = -1;
		
		FIND_START_POINT:
		for(int i=0; i<netToReplicate.length; i++) {
			for(int j=0; j<netToReplicate[0].length; j++) {
				if(netToReplicate[i][j]) {
					startI = i;
					startJ = j;
					
					break FIND_START_POINT;
				}
			}
		}
		
		return hasSolution(dimensionsCuboid, startI, startJ, netToReplicate, verbose);
	}
	

	private static int currentArea = -1;
	private static boolean paperUsed[][] = null;
	private static int indexCuboidOnPaper[][] = null;
	private static Coord2D newPaperToDevelop[] = null;
	private static Coord2D coord2DTable[][] = null;
	
	
	public static void initModelForAreaIfApplicable(int area) {
		
		if(area != currentArea) {
			
			System.out.println("Reinit:");
			currentArea = area;
			
			paperUsed = new boolean[2 * area][2 * area];
			indexCuboidOnPaper= new int[2 * area][2 * area];
			newPaperToDevelop = new Coord2D[area];
			
			coord2DTable = new Coord2D[2 * area][2 * area];
			for(int i=0; i<coord2DTable.length; i++) {
				for(int j=0; j<coord2DTable[0].length; j++) {
					coord2DTable[i][j] = new Coord2D(i, j);
				}
			}
			

			for(int i=0; i<indexCuboidOnPaper.length; i++) {
				for(int j=0; j<indexCuboidOnPaper[0].length; j++) {
					paperUsed[i][j] = false;
					indexCuboidOnPaper[i][j] = -1;
				}
			}
			
			for(int i=0; i<newPaperToDevelop.length; i++) {
				newPaperToDevelop[i] = null;
			}
		}
		
	}
	
	private static boolean hasSolution(int cuboidDimensions[], int startI, int startJ, boolean netToReplicate[][], boolean verbose) {
		
		int areaToFill = Utils.getTotalArea(cuboidDimensions);
		
		initModelForAreaIfApplicable(areaToFill);
		
		CuboidToFoldOn cuboidToUse = null;
		
		String dimensionsInString = cuboidDimensions[0] +"," + cuboidDimensions[1] + "," + cuboidDimensions[2];
		
		if(cuboidHash.containsKey(dimensionsInString)) {
			cuboidToUse = cuboidHash.get(dimensionsInString);
		} else {
			cuboidToUse = new CuboidToFoldOn(cuboidDimensions[0], cuboidDimensions[1], cuboidDimensions[2]);
			cuboidHash.put(dimensionsInString, cuboidToUse);
		}
		cuboidToUse.resetState();
		
		Coord2D refreshCells[] = getListOfCellsToRefresh(cuboidToUse, netToReplicate);
		
		for(int startIndex=0; startIndex<areaToFill; startIndex++) {
			
			for(int startRotation=0; startRotation<4; startRotation++) {

				boolean foundIt = false;
				if(isValidSetupAtIndexedStartLocationWithRotation(
						cuboidDimensions,
						startI,
						startJ,
						netToReplicate,
						startIndex,
						startRotation,
						
						//refreshed variables
						paperUsed,
						newPaperToDevelop,
						cuboidToUse,
						coord2DTable,
						indexCuboidOnPaper,
						//END refresh
						verbose)
				) {
					foundIt = true;
				}
			
				//Refresh:

				cuboidToUse.resetState();
				for(int i=0; i<refreshCells.length; i++) {
					int curi = refreshCells[i].i;
					int curj = refreshCells[i].j;
					indexCuboidOnPaper[curi][curj] = -1;
					paperUsed[curi][curj] = false;
					newPaperToDevelop[i] = null;
					
				}
				
				if(foundIt) {
					return true;
				}
				
				//END refresh
				
			} //End loop for each rotation
			
		} //End loop for each start position
		

		return false;
	}
	
	
	
	public static Coord2D[] getListOfCellsToRefresh(CuboidToFoldOn cuboidToUse, boolean netToReplicate[][]) {
		Coord2D ret[] = new Coord2D[cuboidToUse.getNumCellsToFill()];
		
		int curIndex = 0;
		for(int i=0; i<netToReplicate.length; i++) {
			for(int j=0; j<netToReplicate[0].length; j++) {
				if(netToReplicate[i][j]) {
					ret[curIndex] = new Coord2D(i, j);
					curIndex++;
				}
			}
		}
		if(curIndex != ret.length) {
			System.out.println("ERROR in getListOfCellsToRefresh!");
			System.exit(1);
		}
		
		return ret;
	}

	private static Hashtable<String, CuboidToFoldOn> cuboidHash = new Hashtable<String, CuboidToFoldOn>();
	
	private static boolean isValidSetupAtIndexedStartLocationWithRotation(
			int cuboidDimensions[],
			int startI,
			int startJ,
			boolean netToReplicate[][],
			int cuboidStartIndex,
			int rotation,
			boolean paperUsed[][],
			Coord2D newPaperToDevelop[],
			CuboidToFoldOn cuboidToUse,
			Coord2D coord2DTable[][],
			int indexCuboidOnPaper[][],
			boolean verbose
		) {
	
		
		
		
		paperUsed[startI][startJ] = true;
		for(int k=0; k<newPaperToDevelop.length; k++) {
			newPaperToDevelop[k] = null;
		}

		int numCellsUsedDepth = 0;
		newPaperToDevelop[numCellsUsedDepth] = coord2DTable[startI][startJ];
		
		cuboidToUse.setCell(cuboidStartIndex, rotation);
		indexCuboidOnPaper[startI][startJ] = cuboidStartIndex;
		numCellsUsedDepth += 1;
		
		//regionsToHandleRevOrder[0].resetStateWithStartIndexOnly(cuboidStartIndex);
	//END Setup to run imitation algo.

		return isValid(netToReplicate,
				newPaperToDevelop,
				indexCuboidOnPaper,
				paperUsed,
				cuboidToUse, 
				numCellsUsedDepth,
				coord2DTable,
				verbose);
	}
	
	
	public static boolean isValid(boolean netToReplicate[][],
			Coord2D paperToDevelop[],
			int indexCuboidOnPaper[][],
			boolean paperUsed[][],
			CuboidToFoldOn cuboid, 
			int numCellsUsedDepth,
			Coord2D coord2DTable[][],
			boolean verbose) {

		
		int curOrderedIndexToUse = 0;
		
		ADD_NEXT_CELL:
		while(numCellsUsedDepth < cuboid.getNumCellsToFill()) {
			//System.out.println("DEBUG: " + numCellsUsedDepth);
			//DEPTH-FIRST START:
			
			for(; curOrderedIndexToUse<numCellsUsedDepth; curOrderedIndexToUse++) {
				//System.out.println("DEBUG curOrderedIndexToUse: " + curOrderedIndexToUse);
				//System.out.println("DEBUG curOrderedIndexToUse: " + paperToDevelop[curOrderedIndexToUse].i);
				//System.out.println("DEBUG curOrderedIndexToUse: " + paperToDevelop[curOrderedIndexToUse].j);
				
				int indexToUse = indexCuboidOnPaper[paperToDevelop[curOrderedIndexToUse].i][paperToDevelop[curOrderedIndexToUse].j];
				
				CoordWithRotationAndIndex neighbours[] = cuboid.getNeighbours(indexToUse);
				
				int curRotation = cuboid.getRotationPaperRelativeToMap(indexToUse);
				
				//Try to attach a cell onto indexToUse using all 4 rotations:
				for(int j=0; j<neighbours.length; j++) {
					
					int rotationToAddCellOn = (j + curRotation) % NUM_ROTATIONS;
					
					int new_i = paperToDevelop[curOrderedIndexToUse].i + nugdeBasedOnRotation[0][rotationToAddCellOn];
					int new_j = paperToDevelop[curOrderedIndexToUse].j + nugdeBasedOnRotation[1][rotationToAddCellOn];
	
					int indexNewCell = neighbours[j].getIndex();
			
					if(paperUsed[new_i][new_j]) {
						//Cell we are considering to add is already there...
						continue;
	
					} else if(! netToReplicate[new_i][new_j]) {
						//Make sure to follow the netToRelplicate
						continue;
					
					} else if(cuboid.isCellIndexUsed(neighbours[j].getIndex())) {
	
						//Don't reuse a used cell:
						return false;
						
					}
					
					int rotationNeighbourPaperRelativeToMap = (curRotation - neighbours[j].getRot() + NUM_ROTATIONS) % NUM_ROTATIONS;
					
					//Setup for adding new cell:
					cuboid.setCell(indexNewCell, rotationNeighbourPaperRelativeToMap);
					
					paperUsed[new_i][new_j] = true;
					indexCuboidOnPaper[new_i][new_j] = indexNewCell;
					paperToDevelop[numCellsUsedDepth] = coord2DTable[new_i][new_j];

					numCellsUsedDepth += 1;
					//End setup

					// iterated again, but this time with a higher depth:
					// No need for recursion because we're just following 1 path.
					continue ADD_NEXT_CELL;
					
	
				} // End loop rotation
			} //End loop index
			
			//At this point, we can't go any further because we used up all the indexes and all regions:
			return false;
		}
		
		if(verbose) {
			
			Utils.printFoldWithIndex(indexCuboidOnPaper);
		}

		return true;
	
	}

}
