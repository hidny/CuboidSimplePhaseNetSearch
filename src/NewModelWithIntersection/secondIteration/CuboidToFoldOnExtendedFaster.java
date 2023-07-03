package NewModelWithIntersection.secondIteration;
import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOn;
import Model.NeighbourGraphCreator;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import NewModel.thirdIteration.Nx1x1StackTransitionTracker2;

public class CuboidToFoldOnExtendedFaster extends CuboidToFoldOn {

	public static final int SIDES_CUBOID = 6;

	public static final int NUM_NEIGHBOURS = 4;
	
	// ######################
	
	private int topLeftGroundedIndex = 0;
	private int topLeftGroundRotationRelativeFlatMap = 0;
	
	
	public void initializeNewBottomIndexAndRotation(int bottomIndex, int bottomRotationRelativeFlatMap) {
		
		
		this.topLeftGroundedIndex = bottomIndex;
		this.topLeftGroundRotationRelativeFlatMap = bottomRotationRelativeFlatMap;
		
		answerSheet = new long[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS][NUM_LONGS_TO_USE];
		
		newGroundedRotationAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];
		
		newGroundedIndexAbove = new int[Utils.getTotalArea(this.dimensions)][NUM_NEIGHBOURS][NUM_SIDE_BUMP_OPTIONS];

		
		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		prevSideBumps = new int[DIM_N_OF_Nx1x1];
		prevGroundedIndexes = new int[DIM_N_OF_Nx1x1];
		prevGroundedRotations = new int[DIM_N_OF_Nx1x1];
		currentLayerIndex = 0;
		
	}


	private static final int NUM_BYTES_IN_LONG = 64;
	private static final int NUM_LONGS_TO_USE = 3;
	
	private long curState[] = new long[NUM_LONGS_TO_USE];
	
	public static int NUM_SIDE_BUMP_OPTIONS = 15;
	
	private long answerSheet[][][][];
	
	private int newGroundedRotationAbove[][][];
	
	private int newGroundedIndexAbove[][][];
	

	private long answerSheetForTopCell[][][][];

	
	private int DIM_N_OF_Nx1x1;
	private int prevSideBumps[];
	private int prevGroundedIndexes[];
	private int prevGroundedRotations[];
	private int currentLayerIndex;
	
	public boolean isNewLayerValidSimpleFast(int sideBump) {
	
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		
		return ((curState[0] & tmp[0]) | (curState[1] & tmp[1]) | (curState[2] & tmp[2])) == 0L;
		
	}
	
	public void addNewLayerFast(int sideBump) {
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBump];
		curState[0] = curState[0] | tmp[0];
		curState[1] = curState[1] | tmp[1];
		curState[2] = curState[2] | tmp[2];
		
		int tmp1 = newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		int tmp2 = newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
		
		prevGroundedIndexes[currentLayerIndex] = this.topLeftGroundedIndex;
		prevGroundedRotations[currentLayerIndex] = this.topLeftGroundRotationRelativeFlatMap;
		prevSideBumps[currentLayerIndex] = sideBump;
		currentLayerIndex++;
		
		this.topLeftGroundedIndex = tmp1;
		this.topLeftGroundRotationRelativeFlatMap = tmp2;
	}
	
	public void removePrevLayerFast() {
		
		currentLayerIndex--;
		this.topLeftGroundedIndex = prevGroundedIndexes[currentLayerIndex]; 
		this.topLeftGroundRotationRelativeFlatMap = prevGroundedRotations[currentLayerIndex];
		int sideBumpToCancel  = prevSideBumps[currentLayerIndex];
		
		
		long tmp[] = answerSheet[topLeftGroundedIndex][topLeftGroundRotationRelativeFlatMap][sideBumpToCancel];
		curState[0] = curState[0] ^ tmp[0];
		curState[1] = curState[1] ^ tmp[1];
		curState[2] = curState[2] ^ tmp[2];
	}
	
	
	//TODO:
	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedFast() {
		return false;
	}

	//TODO:
	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedForSideBumpFast(int sideBump) {
		return false;
	}
	
	
	private boolean isNewLayerValidSimpleSlow(int sideBump) {
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		
		//TODO: memorize tmpArray and new grounded index + rot from here
		int leftMostRelativeTopLeftGrounded = sideBump - 6;
		
		if( leftMostRelativeTopLeftGrounded < -3 || leftMostRelativeTopLeftGrounded > 3) {
			return false;
		}
		

		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = false;
		}
		
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
		
		
		//this.topLeftGroundedIndex = nextGounded.i;
		//this.topLeftGroundRotationRelativeFlatMap = nextGounded.j;
		
		return true;
	}
	
	

	//TODO: also memorize this...
	public void tryToAddTopCellSlow(int sideBump) {
		
		Coord2D cur = new Coord2D(topLeftGroundedIndex, topLeftGroundRotationRelativeFlatMap);
		//Go to right until there's a cell above:

		int leftMostRelativeTopLeftGrounded = sideBump - 6;
		
		if(leftMostRelativeTopLeftGrounded >= 0 && leftMostRelativeTopLeftGrounded < 4) {
		
			for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {
	
				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
			}
			
			Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);
			
			
			
			//return ! this.cellsUsed[cellAbove.i];

		} else {
			//return false;
		}
		
	}
	

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.getNeighbours(curIndex);
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	
	
	public CuboidToFoldOnExtendedFaster(int a, int b, int c) {
		super(a, b, c);
	}


	//Create same cuboid, but remove state info:
	public CuboidToFoldOnExtendedFaster(CuboidToFoldOnExtendedFaster orig) {
		super(orig);
		
	}
}
