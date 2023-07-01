package NewModelWithIntersection.firstIteration;
import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.NeighbourGraphCreator;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import NewModel.thirdIteration.Nx1x1StackTransitionTracker2;

public class CuboidToFoldOnExtended {

	public static final int SIDES_CUBOID = 6;

	public static final int NUM_NEIGHBOURS = 4;
	
	private boolean cellsUsed[];
	private int rotationPaperRelativeToCuboidFlatMap[];
	

	
	
	private CoordWithRotationAndIndex[][] neighbours;
	
	private int dimensions[] = new int[3];
	
	// ######################
	
	private int topLeftGroundedIndex = 0;
	private int topLeftGroundRotationRelativeFlatMap = 0;
	
	//TODO: put into constructor
	public void startBottomTODOConstructor(int bottomIndex, int bottomRotationRelativeFlatMap) {
		this.topLeftGroundedIndex = bottomIndex;
		this.topLeftGroundRotationRelativeFlatMap = bottomRotationRelativeFlatMap;
		
		cellsUsed[bottomIndex] = true;
		rotationPaperRelativeToCuboidFlatMap[bottomIndex] = bottomRotationRelativeFlatMap;
		
	}

	//TODO
	//Tmp array to avoid reinitiating it all the time:
	private boolean tmpArray[];

	public boolean isNewLayerValidSimple(int sideBump) {
		
		//TODO: memorize tmpArray and new grounded index + rot from here
		int leftMostRelativeTopLeftGrounded = sideBump - 6;
		
		if( leftMostRelativeTopLeftGrounded < -3 || leftMostRelativeTopLeftGrounded > 3) {
			return false;
		}
		

		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = false;
		}
		
		//Coord2D nextGounded = null;
		
		if(leftMostRelativeTopLeftGrounded<=0) {
			
			Coord2D aboveGroundedTopLeft = tryAttachCellInDir(topLeftGroundedIndex, topLeftGroundRotationRelativeFlatMap, ABOVE);

			tmpArray[aboveGroundedTopLeft.i] = true;
			
			Coord2D cur = aboveGroundedTopLeft;
			//Go to left:
			for(int i=0; i>leftMostRelativeTopLeftGrounded; i--) {
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				tmpArray[cur.i] = true;
			}
			
			//TODO: put on non-is valid version
			//nextGounded = cur;
			
			cur = aboveGroundedTopLeft;
			//Go to right:
			for(int i=0; i<leftMostRelativeTopLeftGrounded + 3; i++) {
				
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				tmpArray[cur.i] = true;
			}
			
		} else {
			
			Coord2D cur = new Coord2D(topLeftGroundedIndex, topLeftGroundRotationRelativeFlatMap);
			//Go to right until there's a cell above:
			
			for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {

				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			}
			
			
			Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);

			//TODO: put on non-is valid version
			//nextGounded = cellAbove;
			
			tmpArray[cellAbove.i] = true;
			
			cur = cellAbove;
			//Go to right:
			for(int i=0; i<3; i++) {
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				tmpArray[cur.i] = true;
			}
			
		}
		//END TODO: memorize from here
		
		int numNew = 0;
		for(int i=0; i<tmpArray.length; i++) {
			if(tmpArray[i]) {
				numNew++;
				
				if(this.cellsUsed[i]) {
					return false;
				}
			}
		}
		
		if(numNew != 4) {
			return false;
		}
		
		
		//this.topLeftGroundedIndex = nextGounded.i;
		//this.topLeftGroundRotationRelativeFlatMap = nextGounded.j;
		
		return true;
	}
	
	
	public void addNewLayer(int sideBump) {
		
		//TODO: memorize tmpArray and new grounded index + rot from here
		int leftMostRelativeTopLeftGrounded = sideBump - 6;
		
		if( leftMostRelativeTopLeftGrounded < -3 || leftMostRelativeTopLeftGrounded > 3) {
			System.out.println("ERROR: invalid move (1)");
			System.exit(1);
		}
		

		Coord2D nextGounded = null;
		
		if(leftMostRelativeTopLeftGrounded<=0) {
			
			Coord2D aboveGroundedTopLeft = tryAttachCellInDir(topLeftGroundedIndex, topLeftGroundRotationRelativeFlatMap, ABOVE);

			cellsUsed[aboveGroundedTopLeft.i] = true;
			rotationPaperRelativeToCuboidFlatMap[aboveGroundedTopLeft.i] = aboveGroundedTopLeft.j;
			
			Coord2D cur = aboveGroundedTopLeft;
			//Go to left:
			for(int i=0; i>leftMostRelativeTopLeftGrounded; i--) {
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				cellsUsed[cur.i] = true;
				rotationPaperRelativeToCuboidFlatMap[cur.i] = cur.j;
			}
			
			//TODO: put on non-is valid version
			nextGounded = cur;
			
			cur = aboveGroundedTopLeft;
			//Go to right:
			for(int i=0; i<leftMostRelativeTopLeftGrounded + 3; i++) {
				
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				cellsUsed[cur.i] = true;
				rotationPaperRelativeToCuboidFlatMap[cur.i] = cur.j;
			}
			
		} else {
			
			Coord2D cur = new Coord2D(topLeftGroundedIndex, topLeftGroundRotationRelativeFlatMap);
			//Go to right until there's a cell above:
			
			for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {

				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			}
			
			
			Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);

			//TODO: put on non-is valid version
			nextGounded = cellAbove;
			
			cellsUsed[cellAbove.i] = true;
			rotationPaperRelativeToCuboidFlatMap[cellAbove.i] = cellAbove.j;
			
			cur = cellAbove;
			//Go to right:
			for(int i=0; i<3; i++) {
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				cellsUsed[cur.i] = true;
				rotationPaperRelativeToCuboidFlatMap[cur.i] = cur.j;
			}
			
		}
		//END TODO: memorize from here
		
		
		this.topLeftGroundedIndex = nextGounded.i;
		this.topLeftGroundRotationRelativeFlatMap = nextGounded.j;
		
	}
	

	//Pre: assuming it's the last layer and the final layer is the 4-in-a-row horizontal:
	public int getNumPossibleTopCellPositions() {
		
		int ret = 0;
		Coord2D cur = new Coord2D(topLeftGroundedIndex, topLeftGroundRotationRelativeFlatMap);
		//Go to right until there's a cell above:

		//TODO: add 4 in a row constant
		for(int i=0; i<4; i++) {
			
			Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);
			
			if( ! this.cellsUsed[cellAbove.i]) {
				ret++;
			}
			
			cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
		}
		
		
		return ret;
	}

	public boolean tryToAddTopCell(int sideBump) {
		
		Coord2D cur = new Coord2D(topLeftGroundedIndex, topLeftGroundRotationRelativeFlatMap);
		//Go to right until there's a cell above:

		int leftMostRelativeTopLeftGrounded = sideBump - 6;
		
		if(leftMostRelativeTopLeftGrounded >= 0 && leftMostRelativeTopLeftGrounded < 4) {
		
			for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {
	
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			}
			
			Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);
			
			
			return ! this.cellsUsed[cellAbove.i];

		} else {
			return false;
		}
		
	}
	

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.getNeighbours(curIndex);
		
		/*System.out.println("Second neighbours:");
		for(int i=0; i<neighbours.length; i++) {
			System.out.println(neighbours[i].getIndex() + ", " + neighbours[i].getRot());
		}
		*/
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		

		//TODO: don't allocate new mem for this! just have all possible coords in an array.
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	
	public boolean addCellsPlanningComments(Nx1x1CuboidToFold nx1x1Cuboid) {
		
		//internal: topLeftGrounded index
		//param: new layer's option and side bump
		
		//nx1x1Cuboid.cellsGrounded[][]
		//nx1x1Cuboid.sideBump[]
		//nx1x1Cuboid.optionUsedPerLevel[]
		//public static int transitionTable[][][][] = new int
		// [NUM_LEVEL_OPTIONS][NUM_GROUNDED_COMBOS][NUM_LEVEL_OPTIONS][NUM_SIDE_BUMP_OPTIONS]
		// = index GROUNDED_COMBO;
		
		
		//Check legal by updating tmpArray first and then make actual update.
		//...
		
		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = false;
		}
		
		
		for(int i=0; i<tmpArray.length; i++) {
			if(tmpArray[i] && cellsUsed[i]) {
				return false;
			}
		}
		
		for(int i=0; i<tmpArray.length; i++) {
			if(tmpArray[i]) {
				cellsUsed[i] = true;
			}
		}
		
		

		/*
		 * int transitionList[][] = Nx1x1StackTransitionTracker2.getTransitionListToLookup(optionUsedPrevLevel, prevGroundedIndex);
		//(index 0 is for next level option, index 1 is for sideBumpIndex, and index 2 for the next GroundedIndex number)
		
		for(int i=0; i<transitionList[0].length; i++) {
			
			//TODO: record enough to be able to reconstruct the net...
			buildNetNextLevel(curLevelIndexToFill + 1, transitionList[0][i], transitionList[2][i], dimensionN);
		
		}
		
		 */
		//curTopLeftGroundedCell = ??
		//curTopLeftGroundedRot = ??
		
		//TODO: only 4 in a rows 1st
		//TODO: draw it out first
		//TODO: draw out the simplest possible thing that works plz!
		
		return true;
	}
	
	//

	
	public CuboidToFoldOnExtended(int a, int b, int c) {

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
		
		//New:
		tmpArray = new boolean[Utils.getTotalArea(a, b, c)];
	}

	//For debug:
	public boolean[] getCellsUsed() {
		return cellsUsed;
	}

	//Create same cuboid, but remove state info:
	public CuboidToFoldOnExtended(CuboidToFoldOnExtended orig) {

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

	
	//TODO:
	//Doesn't work because we don't have info about where cells are...
	
	//TODO: What about the start rotation dude?
	public void debugPrintCuboidOnFlatPaperAndValidateIt(Nx1x1CuboidToFold reference, int startIndex) {
		
		int GRID_SIZE = 2*Utils.getTotalArea(this.getDimensions());
		
		boolean paperUsed[][] = new boolean[GRID_SIZE][GRID_SIZE];
		int indexCuboidOnPaper[][] = new int[GRID_SIZE][GRID_SIZE];
		
		for(int i=0; i<paperUsed.length; i++) {
			for(int j=0; j<paperUsed[0].length; j++) {
				paperUsed[i][j] = false;
				indexCuboidOnPaper[i][j] = -1;
			}
		}
		
		int START_I = GRID_SIZE/2;
		int START_J = GRID_SIZE/2;

		//Add first cell:
		int curIndex = startIndex;
		int curI = START_I;
		int curJ = START_J;

		paperUsed[curI][curJ] = true;
		indexCuboidOnPaper[curI][curJ] = curIndex;
		
		//TODO: rework this later:
		for(int i=0; i<reference.numLevelsUsed; i++) {
			
			System.out.println("Print layer: i = " + i);
			int firstindexPrevLayer = indexCuboidOnPaper[curI][curJ];
			
			curI--;
			
			for(int j=0; j<4; j++) {
				paperUsed[curI][curJ + j - 6 + reference.sideBump[i]] = true;
				indexCuboidOnPaper[curI][curJ + j - 6 + reference.sideBump[i]] = getIndexOnLayerForJthCell(j, firstindexPrevLayer, reference.sideBump[i]);
			}
			
			curJ += reference.sideBump[i] - 6;

			System.out.println("---");
			Utils.printFold(paperUsed);
			Utils.printFoldWithIndex(indexCuboidOnPaper);
			System.out.println("---");
		}
		
		//End insert cell
		
		
		System.out.println("Last print:");
		Utils.printFold(paperUsed);
		Utils.printFoldWithIndex(indexCuboidOnPaper);
	}
	
	//tODO: this will need to be reworked if we want to add different types of layers...
	public int getIndexOnLayerForJthCell(int j, int firstindexPrevLayer, int sideBump) {
		
		int output[] = new int[4];

		int leftMostRelativeTopLeftGrounded = sideBump - 6;
		
		if( leftMostRelativeTopLeftGrounded < -3 || leftMostRelativeTopLeftGrounded > 3) {
			System.out.println("ERROR 1: side bump is obviously impossible");
			System.exit(1);
			return -1;
		}
		
		//Coord2D nextGounded = null;
		
		if(leftMostRelativeTopLeftGrounded<=0) {
			
			Coord2D aboveGroundedTopLeft = tryAttachCellInDir(firstindexPrevLayer, this.getRotationPaperRelativeToMap(firstindexPrevLayer), ABOVE);

			if( ! this.isCellIndexUsed(aboveGroundedTopLeft.i) ) {
				System.out.println("ERROR 2: cell should be used");
				System.exit(1);
			}
			output[0 - leftMostRelativeTopLeftGrounded] = aboveGroundedTopLeft.i;
			
			Coord2D cur = aboveGroundedTopLeft;
			//Go to left:
			for(int i=0; i>leftMostRelativeTopLeftGrounded; i--) {
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				
				if( ! this.isCellIndexUsed(cur.i) ) {
					System.out.println("ERROR 3: cell should be used");
					System.exit(1);
				}
				output[(0 - leftMostRelativeTopLeftGrounded) - 1 - i] = cur.i;
			}
			
			//TODO: put on non-is valid version
			//nextGounded = cur;
			
			cur = aboveGroundedTopLeft;
			//Go to right:
			for(int i=0; i<leftMostRelativeTopLeftGrounded + 3; i++) {
				
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);

				if( ! this.isCellIndexUsed(cur.i) ) {
					System.out.println("ERROR 4: cell should be used");
					System.exit(1);
				}

				output[(0 - leftMostRelativeTopLeftGrounded) + 1 + i] = cur.i;
			}
			
		} else {
			
			Coord2D cur = new Coord2D(firstindexPrevLayer, this.getRotationPaperRelativeToMap(firstindexPrevLayer));
			//Go to right until there's a cell above:
			
			for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {

				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			}
			
			
			Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);

			//TODO: put on non-is valid version
			//nextGounded = cellAbove;
			

			if( ! this.isCellIndexUsed(cellAbove.i) ) {
				System.out.println("ERROR 5: cell should be used");
				System.exit(1);
			}
			output[0] = cellAbove.i;
			
			cur = cellAbove;
			//Go to right:
			for(int i=0; i<3; i++) {
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				
				if( ! this.isCellIndexUsed(cur.i) ) {
					System.out.println("ERROR 6: cell should be used");
					System.exit(1);
				}
				
				output[i + 1] = cur.i;
			}
			
		}
		
		return output[j];
	}
	


	//TODO: play around and test it!
	public static void main(String args[]) {
		CuboidToFoldOnExtended cuboidToBuild = new CuboidToFoldOnExtended(3, 2, 1);
		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(5);
		
		int otherCuboidStartIndex = 0;
		
		reference.addNextLevel(new Coord2D(0, 5), null);
		reference.addNextLevel(new Coord2D(0, 7), null);
		reference.addNextLevel(new Coord2D(0, 5), null);
		int bottomIndex = 0;
		int bottomRotationRelativeFlatMap = 0;
		
		cuboidToBuild.startBottomTODOConstructor(bottomIndex, bottomRotationRelativeFlatMap);
		if(cuboidToBuild.isNewLayerValidSimple(5)){
			System.out.println("Valid 1");
		}
		cuboidToBuild.addNewLayer(5);
		
		if(cuboidToBuild.isNewLayerValidSimple(7)){
			System.out.println("Valid 2");
		}
		cuboidToBuild.addNewLayer(7);
		
		if(cuboidToBuild.isNewLayerValidSimple(5)){
			System.out.println("Valid 3");
		}
		cuboidToBuild.addNewLayer(5);
		
		cuboidToBuild.debugPrintCuboidOnFlatPaperAndValidateIt(reference, otherCuboidStartIndex);
		
		System.out.println("Num possible solutions: " + cuboidToBuild.getNumPossibleTopCellPositions());
		

		for(int i=0; i<20; i++) {
			if(cuboidToBuild.tryToAddTopCell(i)) {
				System.out.println("Side bump " + i + " works for adding top cell");
			}
		}
	}
	
	public static void testStackSkew() {

		CuboidToFoldOnExtended cuboidToBuild = new CuboidToFoldOnExtended(3, 2, 1);
		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(5);
		
		int otherCuboidStartIndex = 0;
		
		reference.addNextLevel(new Coord2D(0, 5), null);
		reference.addNextLevel(new Coord2D(0, 7), null);
		reference.addNextLevel(new Coord2D(0, 5), null);
		int bottomIndex = 0;
		int bottomRotationRelativeFlatMap = 0;
		
		cuboidToBuild.startBottomTODOConstructor(bottomIndex, bottomRotationRelativeFlatMap);
		if(cuboidToBuild.isNewLayerValidSimple(5)){
			System.out.println("Valid 1");
		}
		cuboidToBuild.addNewLayer(5);
		
		if(cuboidToBuild.isNewLayerValidSimple(7)){
			System.out.println("Valid 2");
		}
		cuboidToBuild.addNewLayer(7);
		
		if(cuboidToBuild.isNewLayerValidSimple(5)){
			System.out.println("Valid 3");
		}
		cuboidToBuild.addNewLayer(5);
		
		if(cuboidToBuild.isNewLayerValidSimple(6)){
			System.out.println("Error: Valid when it should not be...");
		} else {
			System.out.println("4th time is correctly invalid...");
		}
		
		System.out.println("HELLO");
		cuboidToBuild.debugPrintCuboidOnFlatPaperAndValidateIt(reference, otherCuboidStartIndex);
		
		
	}
	
	public static void testStackOnTop() {

		CuboidToFoldOnExtended cuboidToBuild = new CuboidToFoldOnExtended(3, 2, 1);
		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(5);
		
		int otherCuboidStartIndex = 0;
		
		reference.addNextLevel(new Coord2D(0, 6), null);
		reference.addNextLevel(new Coord2D(0, 6), null);
		reference.addNextLevel(new Coord2D(0, 6), null);
		int bottomIndex = 0;
		int bottomRotationRelativeFlatMap = 0;
		
		cuboidToBuild.startBottomTODOConstructor(bottomIndex, bottomRotationRelativeFlatMap);
		if(cuboidToBuild.isNewLayerValidSimple(6)){
			System.out.println("Valid 1");
		}
		cuboidToBuild.addNewLayer(6);
		
		if(cuboidToBuild.isNewLayerValidSimple(6)){
			System.out.println("Valid 2");
		}
		cuboidToBuild.addNewLayer(6);
		
		if(cuboidToBuild.isNewLayerValidSimple(6)){
			System.out.println("Valid 3");
		}
		cuboidToBuild.addNewLayer(6);
		
		if(cuboidToBuild.isNewLayerValidSimple(6)){
			System.out.println("Error: Valid when it should not be...");
		} else {
			System.out.println("4th time is correctly invalid...");
		}
		
		System.out.println("HELLO");
		cuboidToBuild.debugPrintCuboidOnFlatPaperAndValidateIt(reference, otherCuboidStartIndex);
		
	}
	
	
	public static void oldTesting() {
		CuboidToFoldOnExtended cuboidToBuild = new CuboidToFoldOnExtended(3, 2, 1);
		//CuboidToFoldOn c = new CuboidToFoldOn(1, 1, 1);
		
		
		int GRID_SIZE = 2*Utils.getTotalArea(cuboidToBuild.getDimensions());
		
		boolean paperUsed[][] = new boolean[GRID_SIZE][GRID_SIZE];
		int indexCuboidOnPaper[][] = new int[GRID_SIZE][GRID_SIZE];

		
		for(int i=0; i<paperUsed.length; i++) {
			for(int j=0; j<paperUsed[0].length; j++) {
				paperUsed[i][j] = false;
				indexCuboidOnPaper[i][j] = -1;
			}
		}

		//Default start location GRID_SIZE / 2, GRID_SIZE / 2
		int START_I = GRID_SIZE/2;
		int START_J = GRID_SIZE/2;
		
		//Add first cell:
		int curIndex = 0;
		int curRotation = 0;
		cuboidToBuild.setCell(curIndex, curRotation);
		
		int curI=0;
		int curJ=0;

		indexCuboidOnPaper[START_I + curI][START_J + curJ] = 0;
		paperUsed[START_I + curI][START_J + curJ] = true;
		
		Utils.printFoldWithIndex(indexCuboidOnPaper);

		//Add Above cell/layer:
		CoordWithRotationAndIndex neighbours[] = cuboidToBuild.getNeighbours(curIndex);
		
		System.out.println("First neighbours:");
		for(int i=0; i<neighbours.length; i++) {
			System.out.println(neighbours[i].getIndex() + ", " + neighbours[i].getRot());
		}
		
		curIndex = neighbours[0].getIndex();
		curRotation = neighbours[0].getRot();
		
		cuboidToBuild.setCell(curIndex, curRotation);
		
		curI--;
		indexCuboidOnPaper[START_I + curI][START_J + curJ] = curIndex;
		paperUsed[START_I + curI][START_J + curJ] = true;
		Utils.printFoldWithIndex(indexCuboidOnPaper);
		

		//Add Above cell/layer:
		neighbours = cuboidToBuild.getNeighbours(curIndex);
		
		/*
		System.out.println("Second neighbours:");
		for(int i=0; i<neighbours.length; i++) {
			System.out.println(neighbours[i].getIndex() + ", " + neighbours[i].getRot());
		}
		*/
		
		curIndex = neighbours[curRotation].getIndex();
		curRotation = (curRotation + neighbours[curRotation].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		//TODO: is it + or -?
		
		cuboidToBuild.setCell(curIndex, curRotation);
		
		curI--;
		indexCuboidOnPaper[START_I + curI][START_J + curJ] = curIndex;
		paperUsed[START_I + curI][START_J + curJ] = true;
		Utils.printFoldWithIndex(indexCuboidOnPaper);
		
		//3rd one:
		Coord2D funct = cuboidToBuild.tryAttachCellInDir(curIndex, curRotation, ABOVE);
		
		curIndex = funct.i;
		curRotation = funct.j;

		cuboidToBuild.setCell(curIndex, curRotation);
		
		curI--;
		indexCuboidOnPaper[START_I + curI][START_J + curJ] = curIndex;
		paperUsed[START_I + curI][START_J + curJ] = true;
		Utils.printFoldWithIndex(indexCuboidOnPaper);
		

		//3rd one:
		funct = cuboidToBuild.tryAttachCellInDir(curIndex, curRotation, RIGHT);
		
		curIndex = funct.i;
		curRotation = funct.j;

		cuboidToBuild.setCell(curIndex, curRotation);
		
		curJ++;
		indexCuboidOnPaper[START_I + curI][START_J + curJ] = curIndex;
		paperUsed[START_I + curI][START_J + curJ] = true;
		Utils.printFoldWithIndex(indexCuboidOnPaper);
		

		//4rd one:
		funct = cuboidToBuild.tryAttachCellInDir(curIndex, curRotation, RIGHT);
		
		curIndex = funct.i;
		curRotation = funct.j;

		cuboidToBuild.setCell(curIndex, curRotation);
		
		curJ++;
		indexCuboidOnPaper[START_I + curI][START_J + curJ] = curIndex;
		paperUsed[START_I + curI][START_J + curJ] = true;
		Utils.printFoldWithIndex(indexCuboidOnPaper);
	}

}
