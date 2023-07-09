package Model;
import Coord.CoordWithRotationAndIndex;

public class CuboidToFoldOn implements CuboidToFoldOnInterface {

	public static final int SIDES_CUBOID = 6;

	public static final int NUM_NEIGHBOURS = 4;
	
	protected boolean cellsUsed[];
	protected int rotationPaperRelativeToCuboidFlatMap[];
	

	
	
	protected CoordWithRotationAndIndex[][] neighbours;
	
	protected int dimensions[] = new int[3];

	
	public CuboidToFoldOn(int a, int b, int c) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c);
		
		cellsUsed = new boolean[Utils.getTotalArea(a, b, c)];
		rotationPaperRelativeToCuboidFlatMap = new int[Utils.getTotalArea(a, b, c)];
		
		for(int i=0; i<cellsUsed.length; i++) {
			cellsUsed[i] = false;
			rotationPaperRelativeToCuboidFlatMap[i] = -1;
		}
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;
	}

	//For debug:
	public boolean[] getCellsUsed() {
		return cellsUsed;
	}

	//Create same cuboid, but remove state info:
	public CuboidToFoldOn(CuboidToFoldOn orig) {

		neighbours = orig.neighbours;
		
		cellsUsed = new boolean[orig.cellsUsed.length];
		rotationPaperRelativeToCuboidFlatMap = new int[orig.cellsUsed.length];
		
		for(int i=0; i<cellsUsed.length; i++) {
			cellsUsed[i] = false;
			rotationPaperRelativeToCuboidFlatMap[i] = -1;
		}
		
		dimensions = orig.dimensions;
	}
	
	//Get dimensions for symmetry-resolver functions:
	public int[] getDimensions() {
		return dimensions;
	}

	public void setCell(int index, int rotation) {
		if(cellsUsed[index]) {
			System.out.println("Error: Setting cell when a cell is already activated!");
			System.exit(1);
		}
		

		cellsUsed[index] = true;
		rotationPaperRelativeToCuboidFlatMap[index] = rotation;
	}
	
	public void removeCell(int index) {
		if(!cellsUsed[index]) {
			System.out.println("Error: removing cell when a cell is not activated!");
			System.exit(1);
		}
		
		cellsUsed[index] = false;
		rotationPaperRelativeToCuboidFlatMap[index] = -1;
	}
	
	public int getNumCellsToFill() {
		return cellsUsed.length;
	}
	
	public CoordWithRotationAndIndex[] getNeighbours(int cellIndex) {
		return neighbours[cellIndex];
	}
	
	public int getRotationPaperRelativeToMap(int cellIndex) {
		return rotationPaperRelativeToCuboidFlatMap[cellIndex];
	}
	
	public boolean isCellIndexUsed(int cellIndex) {
		return cellsUsed[cellIndex];
	}
	
	public void resetState() {
		for(int i=0; i<cellsUsed.length; i++) {
			cellsUsed[i] = false;
		}
	}

	public static void main(String args[]) {
		CuboidToFoldOn c = new CuboidToFoldOn(3, 4, 5);
		//CuboidToFoldOn c = new CuboidToFoldOn(1, 1, 1);
	}

}

//Old ideas i won't follow up on:

//TODO: allow function to insert paper at 1st cell with any rotation

//TODO: for next cell to insert, make function to add cell relative to neighbour and record that
// it's taken with the appropriate rotation

//TODO: allow a cell to be removed no questions asked.

//TODO: this should be able to handle a depth first search and 
//for efficiency purposes, I'll make it mutable (it changes state when actions are done on it)

//TODO: This should be simple!
// Just use neighbours to help navigate and cellsUsed + rotationPaperRelativeToCuboidFlatMap to keep track of state.

//TODO: if move invalid:
// Current idea: return error number and stop changing state util :allow state change" function is run:

//Maybe in another class:
//TODO: have a function that checks for forced moves somewhere
//TODO: also have a function that checks for less obvious impossibilities? (I haven't thought that hard yet)
