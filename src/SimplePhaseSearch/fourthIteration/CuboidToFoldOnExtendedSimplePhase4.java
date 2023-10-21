package SimplePhaseSearch.fourthIteration;

import java.util.ArrayList;

//TODO: Because this is for the 2nd iteration, I skipped basic optimizations like:
// region split check (This is a big one)
// isolated cell check (If an unused cell can't be touched by any cell, it's no good) (Probably related to region split check)
// I'll cover that later.

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.CuboidToFoldOnInterface;
import Model.NeighbourGraphCreator;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;

public class CuboidToFoldOnExtendedSimplePhase4  implements CuboidToFoldOnInterface {

	
	private CoordWithRotationAndIndex[][] neighbours;
	
	public static final int NUM_DIMENSIONS_CUBOID = 3;
	private int dimensions[] = new int[NUM_DIMENSIONS_CUBOID];

	public CuboidToFoldOnExtendedSimplePhase4(int a, int b, int c) {

		neighbours = NeighbourGraphCreator.initNeighbourhood(a, b, c);
		
		dimensions[0] = a;
		dimensions[1] = b;
		dimensions[2] = c;

		DIM_N_OF_Nx1x1 = (Utils.getTotalArea(this.dimensions)-2) / 4;
		
		sanityTestGetCellsToAddGoingUpAndDown();
		
		setupAnswerSheetInBetweenLayers();
		setupAnswerSheetForTopCell();
		
		setupNextLayerPossibilities();
		
	}
	
	public int getNumCellsToFill() {
		return Utils.getTotalArea(this.dimensions);
	}
	
	public CoordWithRotationAndIndex[] getNeighbours(int cellIndex) {
		return neighbours[cellIndex];
	}
	

	public int[] getDimensions() {
		return dimensions;
	}
	
	
	public void initializeNewBottomIndexAndRotation(int bottomIndex, int bottomRotationRelativeFlatMap) {
		
		this.groundedIndexMid = bottomIndex;
		this.groundRotationRelativeFlatMapMid = bottomRotationRelativeFlatMap;

		this.groundedIndexSide = bottomIndex;
		this.groundRotationRelativeFlatMapSide = bottomRotationRelativeFlatMap;
		

		prevSideBumps = new int[DIM_N_OF_Nx1x1 + 2];
		prevGroundedIndexesMid = new int[DIM_N_OF_Nx1x1 + 2];
		prevGroundedRotationsMid = new int[DIM_N_OF_Nx1x1 + 2];
		prevGroundedIndexesSide = new int[DIM_N_OF_Nx1x1 + 2];
		prevGroundedRotationsSide = new int[DIM_N_OF_Nx1x1 + 2];
		prevLayerStateIndex = new int[DIM_N_OF_Nx1x1 + 2];
		currentLayerIndex = 0;
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		tmpArray[bottomIndex] = true;
		this.curState = convertBoolArrayToLongs(tmpArray);
		
		//For now, assume 1x1xN and bottom layer index is 0...
		
		prevSideBumps[0] = 0;
		prevGroundedIndexesMid[0] = bottomIndex;
		prevGroundedRotationsMid[0] = bottomRotationRelativeFlatMap;
		prevGroundedIndexesSide[0] = bottomIndex;
		prevGroundedRotationsSide[0] = bottomRotationRelativeFlatMap;
		prevLayerStateIndex[0] = NOT_APPLICABLE;

		//Start it at layer 1:
		currentLayerIndex = 1;
		
		
	}


	//Constants:
	public final static int LEVEL_OPTIONS[][] = {
			{1, 1, 1, 1, 0, 0, 0},
			{1, 1, 0, 1, 0, 0, 1},
			{1, 0, 1, 1, 0, 1, 0},
			{1, 0, 0, 1, 0, 1, 1}
	};
	
	//public static final int NUM_POSSIBLE_SIDE_BUMPS = 13;
	public static final int NUM_POSSIBLE_SIDE_BUMPS = 2 * LEVEL_OPTIONS[0].length - 1;

	public final static int CELLS_TO_ADD_BY_STATE_GOING_DOWN[][] = {
			{0, 0, 0, 0, 0, 0, 0},
			
			//Ground right:
			{0, 0, 0, 0, 0, 0, 1},
			{0, 0, 0, 0, 0, 1, 0},
			{0, 0, 0, 0, 0, 1, 1},
			
			//Ground left:
			{1, 1, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0},
	};

	public final static int CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[][] = {
			{0, 0, 0, 0, 0, 0, 0},
			
			//Ground right:
			{1, 1, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0},
			
			//Ground left:
			{0, 0, 0, 0, 0, 0, 1},
			{0, 0, 0, 0, 0, 1, 0},
			{0, 0, 0, 0, 0, 1, 1},
	};
	
	public final static int CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[][] = {
			{1, 1, 1, 1, 0, 0, 0},

			//Hug middle:
			{0, 0, 0, 1, 0, 0, 0},
			{0, 0, 1, 1, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 0},

			//Hug middle:
			{0, 0, 0, 1, 0, 0, 0},
			{0, 0, 1, 1, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 0}
	};
	
	public static final int NUM_LAYER_STATES = CELLS_TO_ADD_BY_STATE_GOING_DOWN.length;
	public static final int LENGTH_LAYER_STATES = CELLS_TO_ADD_BY_STATE_GOING_DOWN[0].length;
	
	public static final int NUM_ROTATIONS = 4;
	

	public static final int NOT_APPLICABLE = -30;
	public static final int BAD_ROTATION = -10;
	public static final int BAD_INDEX = -20;
	
	// 2 longs means this is limited to a maximum of 128 squares.
	// I think that's fine for now. I might decide to increase it later.
	// Warning: if you increase this, you'll have to make 5-10 more changes on top of just increasing this number.
	private static final int NUM_BITS_IN_LONG = 64;
	private static final int NUM_LONGS_TO_USE = 2;
	
	
	//Variables to compute at construction time:
	
	private int DIM_N_OF_Nx1x1;
	
	private long answerSheetGoingUpMid[][][][][][];
	private int newGroundedIndexAboveMid[][][][][];
	private int newGroundedRotationAboveMid[][][][][];
	
	private long answerSheetGoingUpSide[][][][][][];
	private int newGroundedIndexAboveSide[][][][][];
	private int newGroundedRotationAboveSide[][][][][];

	private long answerSheetGoingDown[][][][][][];
	private int newGroundedIndexBelow[][][][][];
	private int newGroundedRotationBelow[][][][][];
	
	private long answerSheetForTopCell[][][][];
	private long answerSheetForTopCellAnySideBump[][][];
	

	private long answerSheetGoingUpFirstLayer[][][][];
	private int newGroundedIndexAboveFirst[][][];
	private int newGroundedRotationAboveFirst[][][];

	public Coord2D nextLayerPossibilities[][] = null;

	//State variables:
	private long curState[] = new long[NUM_LONGS_TO_USE];

	private int groundedIndexMid;
	private int groundRotationRelativeFlatMapMid;

	private int groundedIndexSide;
	private int groundRotationRelativeFlatMapSide;
	
	private int prevSideBumps[];
	private int prevGroundedIndexesMid[];
	private int prevGroundedRotationsMid[];

	private int prevGroundedIndexesSide[];
	private int prevGroundedRotationsSide[];

	private int prevLayerStateIndex[];
	
	private int currentLayerIndex;
	
	private RegionSplitLogicSimple3 regionSplitLogicSimple3;
	
	public void printStateStuffDEBUG() {
		System.out.println("DEBUG:");
		System.out.println(groundedIndexMid);
		System.out.println(groundRotationRelativeFlatMapMid);

		System.out.println(groundedIndexSide);
		System.out.println(groundRotationRelativeFlatMapSide);
		
		for(int i=0; i<prevSideBumps.length; i++) {
			System.out.println("i = " + i);
			System.out.println(prevSideBumps[i]);
			System.out.println(prevGroundedIndexesMid[i]);
			System.out.println(prevGroundedRotationsMid[i]);

			System.out.println(prevGroundedIndexesSide[i]);
			System.out.println(prevGroundedRotationsSide[i]);

			System.out.println(prevLayerStateIndex[i]);
			
		}
		System.out.println(currentLayerIndex);
		System.out.println("END DEBUG");
	}
	
	private long tmpStateTestGoingDown[] = new long[NUM_LONGS_TO_USE];
	
	public boolean isNewLayerValidSimpleFast(int layerStateToAdd, int sideBump) {
	
		/*System.out.println("prevLayerIndex[currentLayerIndex - 1]] = " + prevLayerIndex[currentLayerIndex - 1]);
		System.out.println("layerStateToAdd: " + layerStateToAdd);
		System.out.println("groundedIndexMid: " + groundedIndexMid);
		System.out.println("groundRotationRelativeFlatMapMid: " + groundRotationRelativeFlatMapMid);
		System.out.println("sideBump: " + sideBump);
		System.out.println("");
		*/
		long tmp[] = answerSheetGoingUpMid[prevLayerStateIndex[currentLayerIndex - 1]][layerStateToAdd][groundedIndexMid][groundRotationRelativeFlatMapMid][sideBump];
		
		if( ((curState[0] & tmp[0]) | (curState[1] & tmp[1])) == 0L) {
			
			long tmp2[] = answerSheetGoingUpSide[prevLayerStateIndex[currentLayerIndex - 1]][layerStateToAdd][groundedIndexSide][groundRotationRelativeFlatMapSide][sideBump];
			
			boolean validForMidAndSidePart = ( ((curState[0] | tmp[0]) & tmp2[0] ) | ((curState[1] | tmp[1])) & tmp2[1] ) == 0L;
			
			//Check validity of grounding from higher layers to lower layers:
			if(validForMidAndSidePart 
					&& layerStateToAdd == 0 
					&& prevLayerStateIndex[currentLayerIndex - 1] != 0) {
				
				int curSideBump = sideBump;
				int curGroundIndexAbove = newGroundedIndexAboveMid[prevLayerStateIndex[currentLayerIndex - 1]][layerStateToAdd][this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][curSideBump];
				int curRotationGroundIndexAbove = newGroundedRotationAboveMid[prevLayerStateIndex[currentLayerIndex - 1]][layerStateToAdd][this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][curSideBump];
				
				
				tmpStateTestGoingDown[0] = (curState[0] | tmp[0] | tmp2[0]);
				tmpStateTestGoingDown[1] = (curState[1] | tmp[1] | tmp2[1]);
				
				//Dangerous, but ok:
				prevLayerStateIndex[currentLayerIndex] = 0;
				
				for(int curLayerBelow=currentLayerIndex - 1; prevLayerStateIndex[curLayerBelow] != 0; curLayerBelow--) {

					
					long tmp3[] = answerSheetGoingDown[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
					
					
					boolean debug2 = (( tmpStateTestGoingDown[0] & tmp3[0] )
							         | (tmpStateTestGoingDown[1] & tmp3[1] )) == 0L;
					
					if(! debug2) {

						return false;

					} else {
						tmpStateTestGoingDown[0] = (tmpStateTestGoingDown[0] | tmp3[0]);
						tmpStateTestGoingDown[1] = (tmpStateTestGoingDown[1] | tmp3[1]);
						
						int newCurGroundIndexAbove =  newGroundedIndexBelow[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
						curRotationGroundIndexAbove = newGroundedRotationBelow[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
						
						curGroundIndexAbove = newCurGroundIndexAbove;
						curSideBump = prevSideBumps[curLayerBelow];
						
						
					}
				}
			}
			// At this point, it's valid going from top to bottom:
			
			return validForMidAndSidePart;
			
		} else {
			return false;
		}
		
	}
	
	public void addNewLayerFast(int layerStateIndex, int sideBump) {
		
		/*System.out.println("groundedIndexMid: " + groundedIndexMid);
		System.out.println("groundRotationRelativeFlatMapMid: " + groundRotationRelativeFlatMapMid);
		System.out.println("sideBump: " + sideBump);
		*/

		long tmp[] = answerSheetGoingUpMid[prevLayerStateIndex[currentLayerIndex - 1]][layerStateIndex][groundedIndexMid][groundRotationRelativeFlatMapMid][sideBump];
		curState[0] = curState[0] | tmp[0];
		curState[1] = curState[1] | tmp[1];
		
		tmp = answerSheetGoingUpSide[prevLayerStateIndex[currentLayerIndex - 1]][layerStateIndex][groundedIndexSide][groundRotationRelativeFlatMapSide][sideBump];
		curState[0] = curState[0] | tmp[0];
		curState[1] = curState[1] | tmp[1];
		
		
		int tmp1 = newGroundedIndexAboveMid[prevLayerStateIndex[currentLayerIndex - 1]][layerStateIndex][this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][sideBump];
		int tmp2 = newGroundedRotationAboveMid[prevLayerStateIndex[currentLayerIndex - 1]][layerStateIndex][this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][sideBump];
		
		int tmp3 = newGroundedIndexAboveSide[prevLayerStateIndex[currentLayerIndex - 1]][layerStateIndex][this.groundedIndexSide][this.groundRotationRelativeFlatMapSide][sideBump];
		int tmp4 = newGroundedRotationAboveSide[prevLayerStateIndex[currentLayerIndex - 1]][layerStateIndex][this.groundedIndexSide][this.groundRotationRelativeFlatMapSide][sideBump];
		
		prevGroundedIndexesMid[currentLayerIndex] = this.groundedIndexMid;
		prevGroundedRotationsMid[currentLayerIndex] = this.groundRotationRelativeFlatMapMid;

		prevGroundedIndexesSide[currentLayerIndex] = this.groundedIndexSide;
		prevGroundedRotationsSide[currentLayerIndex] = this.groundRotationRelativeFlatMapSide;

		prevSideBumps[currentLayerIndex] = sideBump;
		prevLayerStateIndex[currentLayerIndex] = layerStateIndex;
		currentLayerIndex++;
		

		this.groundedIndexMid = tmp1;
		this.groundRotationRelativeFlatMapMid = tmp2;
		

		//Connect newly grounded lower layers:
		if(layerStateIndex == 0 
		&& prevLayerStateIndex[currentLayerIndex - 2] != 0) {

			int curSideBump = sideBump;
			int curGroundIndexAbove = this.groundedIndexMid;
			int curRotationGroundIndexAbove = this.groundRotationRelativeFlatMapMid;
		
			for(int curLayerBelow=currentLayerIndex - 2; prevLayerStateIndex[curLayerBelow] != 0; curLayerBelow--) {
				
				long tmp5[] = answerSheetGoingDown[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
				
				curState[0] = (curState[0] | tmp5[0]);
				curState[1] = (curState[1] | tmp5[1]);
				
			
				int newCurGroundIndexAbove =  newGroundedIndexBelow[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
				curRotationGroundIndexAbove = newGroundedRotationBelow[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
				
				curGroundIndexAbove = newCurGroundIndexAbove;
				curSideBump = prevSideBumps[curLayerBelow];
			
			}
		}
		//END Connect newly grounded lower layers
				
		//TODO: Maybe remove the need for this if condition in the future iterations:
		if(layerStateIndex == 0) {
			this.groundedIndexSide = tmp1;
			this.groundRotationRelativeFlatMapSide = tmp2;
		} else {
			this.groundedIndexSide = tmp3;
			this.groundRotationRelativeFlatMapSide = tmp4;
		}
		
	}
	
	public static final int FIRST_LAYER_INDEX = 0;
	public static final int BOTTOM_CELL_INDEX = 0;

	public void addFirstLayer(int sideBump) {
		long tmp[] = answerSheetGoingUpFirstLayer[groundedIndexMid][groundRotationRelativeFlatMapMid][sideBump];
		curState[0] = curState[0] | tmp[0];
		curState[1] = curState[1] | tmp[1];
		
		int tmp1 = newGroundedIndexAboveFirst[this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][sideBump];
		int tmp2 = newGroundedRotationAboveFirst[this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][sideBump];
		
		prevGroundedIndexesMid[currentLayerIndex] = this.groundedIndexMid;
		prevGroundedRotationsMid[currentLayerIndex] = this.groundRotationRelativeFlatMapMid;

		prevGroundedIndexesSide[currentLayerIndex] = this.groundedIndexSide;
		prevGroundedRotationsSide[currentLayerIndex] = this.groundRotationRelativeFlatMapSide;

		prevSideBumps[currentLayerIndex] = sideBump;
		prevLayerStateIndex[currentLayerIndex] = FIRST_LAYER_INDEX;
		currentLayerIndex++;
		
		
		this.groundedIndexMid = tmp1;
		this.groundRotationRelativeFlatMapMid = tmp2;

		this.groundedIndexSide = tmp1;
		this.groundRotationRelativeFlatMapSide = tmp2;
		/*
		System.out.println("In addFirstLayer ground index:" + this.groundedIndexMid);
		System.out.println("In addFirstLayer rotation index:" + this.groundRotationRelativeFlatMapMid);
		*/
	}
	
	public void leaveOnlyTheBottomCell() {
		
		currentLayerIndex--;
		this.groundedIndexMid = prevGroundedIndexesMid[currentLayerIndex]; 
		this.groundRotationRelativeFlatMapMid = prevGroundedRotationsMid[currentLayerIndex];

		this.groundedIndexSide = prevGroundedIndexesSide[currentLayerIndex]; 
		this.groundRotationRelativeFlatMapSide = prevGroundedRotationsSide[currentLayerIndex];
		
		boolean tmpArray[] = new boolean[getNumCellsToFill()];
		
		
		for(int i=0; i<tmpArray.length; i++) {
			tmpArray[i] = false;
		}
		
		tmpArray[groundedIndexMid] = true;
		
		
		curState = convertBoolArrayToLongs(tmpArray);
		
	}
	
	
	public void removePrevLayerFast() {
		
		currentLayerIndex--;
		this.groundedIndexMid = prevGroundedIndexesMid[currentLayerIndex]; 
		this.groundRotationRelativeFlatMapMid = prevGroundedRotationsMid[currentLayerIndex];

		this.groundedIndexSide = prevGroundedIndexesSide[currentLayerIndex]; 
		this.groundRotationRelativeFlatMapSide = prevGroundedRotationsSide[currentLayerIndex];
		

		int layerStateBelow = prevLayerStateIndex[currentLayerIndex - 1];
		int layerStateIndexUsed = prevLayerStateIndex[currentLayerIndex];
		int sideBumpToCancel  = prevSideBumps[currentLayerIndex];
		
		//Also cancel top to bottom cells!  (This is currently missing, and will cause it to not work)
		if(layerStateIndexUsed == 0 
				&& layerStateBelow != 0) {
			
			int curSideBump = sideBumpToCancel;
			int curGroundIndexAbove = newGroundedIndexAboveMid[layerStateBelow][layerStateIndexUsed][this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][curSideBump];
			int curRotationGroundIndexAbove = newGroundedRotationAboveMid[layerStateBelow][layerStateIndexUsed][this.groundedIndexMid][this.groundRotationRelativeFlatMapMid][curSideBump];

			for(int curLayerBelow=currentLayerIndex - 1; prevLayerStateIndex[curLayerBelow] != 0; curLayerBelow--) {
				
				long tmp3[] = answerSheetGoingDown[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
				
				curState[0] = (curState[0] & (~tmp3[0]));
				curState[1] = (curState[1] & (~tmp3[1]));
				
				int newCurGroundIndexAbove =  newGroundedIndexBelow[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
				curRotationGroundIndexAbove = newGroundedRotationBelow[prevLayerStateIndex[curLayerBelow]][prevLayerStateIndex[curLayerBelow + 1]][curGroundIndexAbove][curRotationGroundIndexAbove][curSideBump];
				
				curGroundIndexAbove = newCurGroundIndexAbove;
				curSideBump = prevSideBumps[curLayerBelow];
			
			}
			
			
		}
		

		long tmp[] = answerSheetGoingUpMid[layerStateBelow][layerStateIndexUsed][groundedIndexMid][groundRotationRelativeFlatMapMid][sideBumpToCancel];
		curState[0] = curState[0] ^ tmp[0];
		curState[1] = curState[1] ^ tmp[1];

		tmp = answerSheetGoingUpSide[layerStateBelow][layerStateIndexUsed][groundedIndexSide][groundRotationRelativeFlatMapSide][sideBumpToCancel];
		curState[0] = curState[0] ^ tmp[0];
		curState[1] = curState[1] ^ tmp[1];
		
		
	}
	
	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedFast() {
		
		long tmp[] = answerSheetForTopCellAnySideBump[groundedIndexMid][groundRotationRelativeFlatMapMid];
		
		boolean ret = this.prevLayerStateIndex[currentLayerIndex - 1] == 0 &&   ((~curState[0] & tmp[0]) | (~curState[1] & tmp[1]) ) != 0L;
		
		return ret;
	}

	//pre: The only cell left is top cell:
	public boolean isTopCellAbleToBeAddedForSideBumpFast(int sideBump) {
		long tmp[] = answerSheetForTopCell[groundedIndexMid][groundRotationRelativeFlatMapMid][sideBump];
		
		return this.prevLayerStateIndex[currentLayerIndex - 1] == 0 &&  ((~curState[0] & tmp[0]) | (~curState[1] & tmp[1]) ) != 0L;
	}
	
	public static long debugEmpty = 0L;
	public static long debugNotEmpty = 0L;
	
	public boolean untouchableRegionCreatedAfterLayerAdded() {
		
		//TODO: do some kind of short-cut, but only do the shortcut if top to bottom cells aren't a thing:
		//Make sure there's no top to bottom cells that make things too complicated:
		//(prevLayerStateIndex[currentLayerIndex - 1] != 0 || prevLayerStateIndex[currentLayerIndex - 2] == 0)
				
				//Do the actual check:
		
		/*if(regionSplitLogicSimple3.untouchableRegionNotCreatedAfterLayerAddedQuick(curState,
				int layerBeforeLastLayerAdded,
				int lastLayerStateAdded,
				int indexGroundedBelowLayerMid,
				int rotationGroundedBelowLayerMid,
				int indexGroundedBelowLayerSide,
				int rotationGroundedBelowLayerSide,
				int prevSideBump,
				int curNumRegions
		)*/
		if(regionSplitLogicSimple3.untouchableRegionNotCreatedAfterLayerAddedQuick(curState,
				this.prevLayerStateIndex[currentLayerIndex - 2],
				this.prevLayerStateIndex[currentLayerIndex - 1],
				this.groundedIndexMid,
				this.groundRotationRelativeFlatMapMid,
				this.groundedIndexSide,
				this.groundRotationRelativeFlatMapSide,
				prevSideBumps[currentLayerIndex - 1],
				//TODO: actually use curNumRegion in future:
				1
				)
		)
				{
			
			//TODO: for some combinations, it's obviously bad if empty
			
			System.out.println("Empty");
			debugEmpty++;
			//System.exit(1);
			//For now, let's say that it's ok.
			return false;
		} else {
			debugNotEmpty++;
			System.out.println("keep going");
		}
		
		System.out.println(debugEmpty + " vs " + debugNotEmpty);
				
		return regionSplitLogicSimple3.untouchableRegionCreatedAfterLayerAdded
				(curState,
					prevLayerStateIndex[currentLayerIndex - 1],
					groundedIndexMid,
					groundRotationRelativeFlatMapMid,
					groundedIndexSide,
					groundRotationRelativeFlatMapSide);
	}
	
	// ***********************************************************
	// ***********************************************************
	//Pre-compute functions that make the program faster:
	
	private void setupAnswerSheetInBetweenLayers() {
		

		answerSheetGoingDown = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		newGroundedIndexBelow = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		newGroundedRotationBelow = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		

		answerSheetGoingUpMid = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		newGroundedRotationAboveMid = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		newGroundedIndexAboveMid = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		
		answerSheetGoingUpSide = new long[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		newGroundedRotationAboveSide = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		newGroundedIndexAboveSide = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		

		answerSheetGoingUpFirstLayer = new long[Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		newGroundedIndexAboveFirst = new int[Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		newGroundedRotationAboveFirst = new int[Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		
		for(int layerStateBelow=0; layerStateBelow<NUM_LAYER_STATES; layerStateBelow++) {
			for(int layerStateAbove=0; layerStateAbove<NUM_LAYER_STATES; layerStateAbove++) {
				for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
					for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
						
						for(int sideBump=0; sideBump<NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
						
							handleLayerStateOverLayerStatePreComputeTopToBottom(layerStateBelow, layerStateAbove, index, rotation, sideBump);
							handleLayerStateOverLayerStatePreComputeBottomToTopMid(layerStateBelow, layerStateAbove, index, rotation, sideBump);
							handleLayerStateOverLayerStatePreComputeBottomToTopSide(layerStateBelow, layerStateAbove, index, rotation, sideBump);
							
							
						}
					}
				}
			}
		}

		regionSplitLogicSimple3 = new RegionSplitLogicSimple3(this.neighbours);
		
		regionSplitLogicSimple3.
			setupNumCellsExpectedAroundLayer(
				newGroundedIndexAboveMid,
				newGroundedRotationAboveMid,
				newGroundedIndexAboveSide,
				newGroundedRotationAboveSide
			);
		
		
	}
	
	private void setupNextLayerPossibilities() {

		nextLayerPossibilities = new Coord2D[NUM_LAYER_STATES][];
		
		for(int m=0; m<NUM_LAYER_STATES; m++) {

			nextLayerPossibilities[m] = this.getSideBumpAndStateIdArrayToIterateOverByPreviousLayer(m);
			
		}
	}
	
	private Coord2D[] getSideBumpAndStateIdArrayToIterateOverByPreviousLayer(int prevLayer) {
		
		ArrayList<Coord2D> options = new ArrayList<Coord2D>();
		for(int nextLayerState = 0; nextLayerState<CuboidToFoldOnExtendedSimplePhase4.NUM_LAYER_STATES; nextLayerState++) {
			
				for(int sideBump=0; sideBump < CuboidToFoldOnExtendedSimplePhase4.NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
					
					boolean foundAWay = false;
					
					SEARCH_INDEX_ROTATION_THAT_WORKS:
					for(int groundIndex = 0; groundIndex<this.getNumCellsToFill(); groundIndex++) {
						for(int rotationIndex = 0; rotationIndex<NUM_ROTATIONS; rotationIndex++) {

							
							if(isAnswerSheetImpossible(answerSheetGoingUpMid[prevLayer][nextLayerState][groundIndex][rotationIndex][sideBump])) {
								continue;
							} else if(isAnswerSheetImpossible(answerSheetGoingUpSide[prevLayer][nextLayerState][groundIndex][rotationIndex][sideBump])) {
								continue;
							} else if(prevLayer != 0 && nextLayerState == 0
									&& isAnswerSheetImpossible(answerSheetGoingDown[prevLayer][nextLayerState][groundIndex][rotationIndex][sideBump])) {
								continue;
							} else {
								foundAWay = true;
								break SEARCH_INDEX_ROTATION_THAT_WORKS;
							}
						}
					}
					
					if(foundAWay) {
						options.add(new Coord2D(nextLayerState, sideBump));
					}
				}
		}
		
		Coord2D ret[] = new Coord2D[options.size()];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = options.get(i);
		}
		return ret;
	}
	
	
	//If bottom layer is index 1,2, or 3 top layer can't be 4, 5, 6, (and vice-versa)
	private boolean topAndBottomStateDontMix(int layerStateBelow, int layerStateAbove) {
		return layerStateAbove != 0 
				&& layerStateBelow != 0
				&& connectToTopToBottomFromLeft(layerStateBelow) != connectToTopToBottomFromLeft(layerStateAbove);
	}
	private boolean connectToTopToBottomFromLeft(int layerState) {
		
		if(layerState <= 3) {
			return true;
		} else {
			return false;
		}
	}

	
	private void handleLayerStateOverLayerStatePreComputeTopToBottom(int layerStateBelow, int layerStateAbove, int indexGroundedAbove, int rotationGroundedAbove, int sideBump) {
		
		if(topAndBottomStateDontMix(layerStateBelow, layerStateAbove)) {
			// No need to connect from top to bottom because
			// below state connects the left part while above state connects the right part.

			answerSheetGoingDown[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_INDEX;
			newGroundedRotationBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_ROTATION;						
			return;

		} else if(layerStateBelow == 0) {	
			// No need to connect from top to bottom because
			// below layer is fully connected
			// I set it to impossible, but it's more like "Not applicable"...
			
			answerSheetGoingDown[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_INDEX;
			newGroundedRotationBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_ROTATION;						
			return;

		}
		
		int groundingFromAbove[] = CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateAbove];
		
		if(layerStateAbove == 0) {
			groundingFromAbove = LEVEL_OPTIONS[0];
		}
		
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		

		int leftMostRelativeBottomLayer = sideBump - 6;
		
		boolean connectedAndNoProblems = false;
		boolean wentThroughLoopAlready = false;
		
		int aboveLeftmostGroundedIndex = -1;
		
		for(int i=0; i<CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[0].length; i++) {
			if(groundingFromAbove[i] == 1) {
				aboveLeftmostGroundedIndex = i;
				break;
			}
		}

		for(int i=0; i<CELLS_TO_ADD_BY_STATE_GOING_DOWN[0].length; i++) {
			
			int topLayerIndex = i - leftMostRelativeBottomLayer;
			
			if( topLayerIndex < 0 || topLayerIndex >= CELLS_TO_ADD_BY_STATE_GOING_DOWN.length) {
				continue;
			}

			if(groundingFromAbove[topLayerIndex] == 1) {
				
				if(CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow][i] == 1) {

					connectedAndNoProblems = true;
					
					Coord2D curGroundAbove = new Coord2D(indexGroundedAbove, rotationGroundedAbove);

					
					if(topLayerIndex < aboveLeftmostGroundedIndex) {
						System.out.println("ERROR: something went wrong in handleLayerStateOverLayerStatePreComputeBottomToTopMid");
						System.exit(1);
					}

					for(int j=aboveLeftmostGroundedIndex; j < topLayerIndex; j++) {
						curGroundAbove = tryAttachCellInDir(curGroundAbove.i, curGroundAbove.j, RIGHT);
					}
					Coord2D cellBelowCurGround = tryAttachCellInDir(curGroundAbove.i, curGroundAbove.j, BELOW);


					Coord2D curCell = cellBelowCurGround;
					tmpArray[curCell.i] = true;

					//Go to left:
					for(int k=i; k - 1 >=0 && CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow][k-1] == 1; k--) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, LEFT);
						tmpArray[curCell.i] = true;
					}
					
					Coord2D nextGounded = curCell;
					
					curCell = cellBelowCurGround;
					
					//Go to right:
					for(int k=i; k + 1 < CELLS_TO_ADD_BY_STATE_GOING_DOWN.length 
							&& CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow][k+1] == 1; k++) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, RIGHT);
						tmpArray[curCell.i] = true;
					}

					if(wentThroughLoopAlready == false) {

						answerSheetGoingDown[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = convertBoolArrayToLongs(tmpArray);
						
						newGroundedIndexBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = nextGounded.i;
						newGroundedRotationBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = nextGounded.j;
					
						tmpArray = new boolean[Utils.getTotalArea(this.dimensions)];
						wentThroughLoopAlready = true;
						
					} else {
						
						//If the answer isn't consistent, then it doesn't work:
						
						if( ! longArrayMatches(convertBoolArrayToLongs(tmpArray),
								answerSheetGoingDown[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump]
								)
							|| nextGounded.i !=
								newGroundedIndexBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump]
							|| nextGounded.j !=
									newGroundedRotationBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump]
							) {

							connectedAndNoProblems = false;
							break;

						}
						
					}
				}
			}
		
		} // end loop through places to connect top and bottom layer
		
		if(connectedAndNoProblems == false) {
			answerSheetGoingDown[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_INDEX;
			newGroundedRotationBelow[layerStateBelow][layerStateAbove][indexGroundedAbove][rotationGroundedAbove][sideBump] = BAD_ROTATION;
		}
		
	}
	
	private void handleLayerStateOverLayerStatePreComputeBottomToTopSide(int layerStateBelow, int layerStateAbove, int indexGroundedSideBelow, int rotationGroundedSideBelow, int sideBump) {
		
		if(topAndBottomStateDontMix(layerStateBelow, layerStateAbove)) {

			// No need to connect from bottom to top because
			// below state connects the left part while above state connects the right part.

			answerSheetGoingUpSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = BAD_INDEX;
			newGroundedRotationAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = BAD_ROTATION;						
			return;

		} else if(layerStateAbove == 0) {
			answerSheetGoingUpSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = setAllPossibleForAnswerSheet();
			newGroundedIndexAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = BAD_INDEX;
			newGroundedRotationAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = BAD_ROTATION;						
			return;
		}
		
		int groundingFromBelow[] = CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[layerStateBelow];
		
		if(layerStateBelow == 0) {
			groundingFromBelow = LEVEL_OPTIONS[0];
		}
		
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		

		int leftMostRelativeBottomLayer = sideBump - 6;
		
		boolean connectedAndNoProblems = false;
		boolean wentThroughLoopAlready = false;

		int belowLeftmostGroundedIndex = -1;
		
		for(int i=0; i<CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[0].length; i++) {
			if(groundingFromBelow[i] == 1) {
				belowLeftmostGroundedIndex = i;
				break;
			}
		}

		for(int i=0; i<CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[0].length; i++) {
			
			int topLayerIndex = i - leftMostRelativeBottomLayer;
			
			if( topLayerIndex < 0 || topLayerIndex >= CELLS_TO_ADD_BY_STATE_GOING_DOWN.length) {
				continue;
			}

			if(groundingFromBelow[i] == 1) {
				
			
				if(CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[layerStateAbove][topLayerIndex] == 1) {

					connectedAndNoProblems = true;

					Coord2D curGroundBelow = new Coord2D(indexGroundedSideBelow, rotationGroundedSideBelow);
					
					if(i < belowLeftmostGroundedIndex) {
						System.out.println("ERROR: something went wrong in handleLayerStateOverLayerStatePreComputeBottomToTopMid");
						System.exit(1);
					}

					for(int j=belowLeftmostGroundedIndex; j < i; j++) {
						curGroundBelow = tryAttachCellInDir(curGroundBelow.i, curGroundBelow.j, RIGHT);
					}
					
					Coord2D cellAboveCurGround = tryAttachCellInDir(curGroundBelow.i, curGroundBelow.j, ABOVE);

					Coord2D curCell = cellAboveCurGround;
					
					tmpArray[curCell.i] = true;
					
					//Go to left:
					for(int k=topLayerIndex; k - 1 >= 0 && CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[layerStateAbove][k-1] == 1; k--) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, LEFT);
						tmpArray[curCell.i] = true;
					}
					
					Coord2D nextGounded = curCell;
					
					curCell = cellAboveCurGround;
					
					//Go to right:
					for(int k=topLayerIndex; k + 1 < CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE.length
							&& CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[layerStateAbove][k+1] == 1; k++) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, RIGHT);
						tmpArray[curCell.i] = true;
					}
					
					if(wentThroughLoopAlready == false) {

						answerSheetGoingUpSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = convertBoolArrayToLongs(tmpArray);
						
						newGroundedIndexAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = nextGounded.i;
						newGroundedRotationAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = nextGounded.j;
					
						tmpArray = new boolean[Utils.getTotalArea(this.dimensions)];
						wentThroughLoopAlready = true;
						
					} else {
						
						//If the answer isn't consistent, then it doesn't work:
						
						if(! longArrayMatches(convertBoolArrayToLongs(tmpArray),
								answerSheetGoingUpSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump]
							)
							|| nextGounded.i !=
								newGroundedIndexAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump]
							|| nextGounded.j !=
								newGroundedRotationAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump]
							) {

							connectedAndNoProblems = false;
							break;

						}
						
					}
				}
			}
		
		} // end loop through places to connect top and bottom layer
		
		if(connectedAndNoProblems == false) {
			answerSheetGoingUpSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = BAD_INDEX;
			newGroundedRotationAboveSide[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump] = BAD_ROTATION;
		}
	}
	
	private boolean ungroundedSideWorksWithSideBump(int layerStateBelow, int layerStateAbove, int sideBumpUsed) {
		
		if(topAndBottomStateDontMix(layerStateBelow, layerStateAbove)) {
			return false;
		}
		
		int arrayToUseBelow[] = null;
		if(layerStateBelow == 0) {

			//if layer State Below is 0, the ungrounded side doesn't need to connect to the bottom layer,
			// so we don't worry about it:
			return true;
			
		} else {
			arrayToUseBelow = CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow];
		}
		
		int arrayToUseAbove[] = null;
		if(layerStateAbove == 0) {
			arrayToUseAbove = LEVEL_OPTIONS[0];
		} else {
			arrayToUseAbove = CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateAbove];
		}
		
		int leftMostRelativeBottomLayer = sideBumpUsed - 6;
		
		for(int i=0; i<arrayToUseBelow.length; i++) {
			int topLayerIndex = i - leftMostRelativeBottomLayer;
			
			if(topLayerIndex < 0 || topLayerIndex >= arrayToUseAbove.length) {
				continue;
			}
			
			if(arrayToUseBelow[i] == 1
					&& arrayToUseAbove[topLayerIndex] == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	//TODO: this is copy/paste code of ungroundedSideWorksWithSideBump. Maybe I could refactor this one day?
	private boolean groundedSideWorksWithSideBump(int layerStateBelow, int layerStateAbove, int sideBumpUsed) {
		
		if(topAndBottomStateDontMix(layerStateBelow, layerStateAbove)) {
			return false;
		}
		
		int arrayToUseAbove[] = null;

		if(layerStateAbove == 0) {
			//if layer State Above is 0, the grounded side doesn't need to connect to the top layer,
			// so we don't worry about it:
			return true;
			
		} else {
			arrayToUseAbove = CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[layerStateAbove];
		}
		
		int arrayToUseBelow[] = null;
		if(layerStateBelow == 0) {
			arrayToUseBelow = LEVEL_OPTIONS[0];
		} else {
			arrayToUseBelow = CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[layerStateBelow];
		}
		
		int leftMostRelativeBottomLayer = sideBumpUsed - 6;
		
		for(int i=0; i<arrayToUseBelow.length; i++) {
			int topLayerIndex = i - leftMostRelativeBottomLayer;
			
			if(topLayerIndex < 0 || topLayerIndex >= arrayToUseAbove.length) {
				continue;
			}
			
			if(arrayToUseBelow[i] == 1
					&& arrayToUseAbove[topLayerIndex] == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private static int translateStateToLayerIndex(int stateIndex) {
		
		if(stateIndex >=4) {
			return stateIndex - 3;
		} else {
			return stateIndex;
		}
	}
	
	public static int debug = 0;
	private void handleLayerStateOverLayerStatePreComputeBottomToTopMid(int layerStateBelow, int layerStateAbove, int indexGroundedMidBelow, int rotationGroundedMidBelow, int sideBump) {
		
		if(topAndBottomStateDontMix(layerStateBelow, layerStateAbove)) {

			// No need to connect from bottom to top because
			// below state connects the left part while above state connects the right part.

			answerSheetGoingUpMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = BAD_INDEX;
			newGroundedRotationAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = BAD_ROTATION;						
			return;

		} else if( ! ungroundedSideWorksWithSideBump(layerStateBelow, layerStateAbove, sideBump)
				|| ! groundedSideWorksWithSideBump(layerStateBelow, layerStateAbove, sideBump)) {
			
			answerSheetGoingUpMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = BAD_INDEX;
			newGroundedRotationAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = BAD_ROTATION;
			
			//debugPrintLowerAndUpperLayers(layerStateBelow, layerStateAbove, sideBump, indexGroundedMidBelow, rotationGroundedMidBelow);
			
			return;
		}
		
		int groundingFromBelow[] = CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[layerStateBelow];
		
		if(layerStateBelow == 0) {
			groundingFromBelow = LEVEL_OPTIONS[0];
		}
		
		
		boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
		

		int leftMostRelativeBottomLayer = sideBump - 6;
		
		boolean connectedAndNoProblems = false;
		boolean wentThroughLoopAlready = false;
		
		int belowLeftmostGroundedIndex = -1;
		
		for(int i=0; i<CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[0].length; i++) {
			if(groundingFromBelow[i] == 1) {
				belowLeftmostGroundedIndex = i;
				break;
			}
		}

		for(int i=0; i<CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[0].length; i++) {
			
			int topLayerIndex = i - leftMostRelativeBottomLayer;
			
			if( topLayerIndex < 0 || topLayerIndex >= CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[0].length) {
				continue;
			}
			
			

			if(groundingFromBelow[i] == 1) {
				
				boolean layerGoodToAdd = false;
				if(CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[layerStateAbove][topLayerIndex] == 1) {

					//Check if layer state 0 can be added by also considering cells that will need to be grounded top to bottom:
					if(layerStateBelow != FIRST_LAYER_INDEX
							&& layerStateAbove == FIRST_LAYER_INDEX) {
						
						for(int k=0; k<CELLS_TO_ADD_BY_STATE_GOING_DOWN.length; k++) {
							
							int topLayerIndex2 = k - leftMostRelativeBottomLayer;
							
							if( topLayerIndex2 < 0 || topLayerIndex2 >= CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[0].length) {
								continue;
							}

							if(CELLS_TO_ADD_BY_STATE_GOING_DOWN[layerStateBelow][k] == 1 &&
									CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[layerStateAbove][topLayerIndex2] == 1
							) {
								layerGoodToAdd = true;
								break;
							}
						}
						
					} else {
						layerGoodToAdd = true;
					}
					
					if( ! layerGoodToAdd) {
						continue;
					}
					//END check if layer state 0 can be added by also considering cells that will need to be grounded top to bottom:
					
					
					connectedAndNoProblems = true;

					Coord2D curGroundBelow = new Coord2D(indexGroundedMidBelow, rotationGroundedMidBelow);
					
					if(i < belowLeftmostGroundedIndex) {
						System.out.println("ERROR: something went wrong in handleLayerStateOverLayerStatePreComputeBottomToTopMid");
						System.exit(1);
					}

					for(int j=belowLeftmostGroundedIndex; j < i; j++) {
						curGroundBelow = tryAttachCellInDir(curGroundBelow.i, curGroundBelow.j, RIGHT);
					}
					
					Coord2D cellAboveCurGround = tryAttachCellInDir(curGroundBelow.i, curGroundBelow.j, ABOVE);

					Coord2D curCell = cellAboveCurGround;

					tmpArray[curCell.i] = true;
					
					//Go to left:
					for(int k=topLayerIndex; k - 1 >=0 && CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[layerStateAbove][k-1] == 1; k--) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, LEFT);
						tmpArray[curCell.i] = true;
					}
					
					Coord2D nextGounded = curCell;
					
					curCell = cellAboveCurGround;
					
					//Go to right:
					for(int k=topLayerIndex; k + 1 < CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE.length
							&& CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[layerStateAbove][k+1] == 1; k++) {
						curCell = tryAttachCellInDir(curCell.i, curCell.j, RIGHT);
						tmpArray[curCell.i] = true;
					}

					if(i == BOTTOM_CELL_INDEX && layerStateBelow == FIRST_LAYER_INDEX && layerStateAbove == FIRST_LAYER_INDEX) {

						//Handle the slightly different logic for the 1st layer to be added:
						answerSheetGoingUpFirstLayer[indexGroundedMidBelow][rotationGroundedMidBelow][sideBump]  = convertBoolArrayToLongs(tmpArray);;
						newGroundedIndexAboveFirst[indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = nextGounded.i;
						newGroundedRotationAboveFirst[indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = nextGounded.j;
					}
					
					//printStateFromLongs(convertBoolArrayToLongs(tmpArray));
	
					if(wentThroughLoopAlready == false) {

						answerSheetGoingUpMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = convertBoolArrayToLongs(tmpArray);
						
						newGroundedIndexAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = nextGounded.i;
						newGroundedRotationAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = nextGounded.j;
					
						tmpArray = new boolean[Utils.getTotalArea(this.dimensions)];
						wentThroughLoopAlready = true;
						
					} else {
						
						//If the answer isn't consistent, then it doesn't work:
						
						if( ! longArrayMatches(convertBoolArrayToLongs(tmpArray),
								answerSheetGoingUpMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump])
							|| nextGounded.i !=
									newGroundedIndexAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump]
							|| nextGounded.j !=
									newGroundedRotationAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump]
							) {

							
							connectedAndNoProblems = false;
							break;

						}
						
					}
				}
			}
		
		} // end loop through places to connect top and bottom layer
		
		if(connectedAndNoProblems == false) {
			answerSheetGoingUpMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = setImpossibleForAnswerSheet();
			newGroundedIndexAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = BAD_INDEX;
			newGroundedRotationAboveMid[layerStateBelow][layerStateAbove][indexGroundedMidBelow][rotationGroundedMidBelow][sideBump] = BAD_ROTATION;
		}
	}
	
	public static boolean longArrayMatches(long array1[], long array2[]) {
		
		if(array1.length != array2.length) {
			return false;
		}
		
		for(int i=0; i<array1.length; i++) {
			if(array1[i] != array2[i]) {
				return false;
			}
		}
		return true;
	}
	
	public void setupAnswerSheetForTopCell() {
		
		answerSheetForTopCell = new long[Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS][NUM_LONGS_TO_USE];
		answerSheetForTopCellAnySideBump = new long[Utils.getTotalArea(this.dimensions)][NUM_ROTATIONS][NUM_LONGS_TO_USE];
		
		for(int index=0; index<Utils.getTotalArea(this.dimensions); index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
				
				boolean tmpArrayForAnySideBump[] = new boolean[Utils.getTotalArea(this.dimensions)];
				
				for(int sideBump=0; sideBump<NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
					
					
					Coord2D cur = new Coord2D(index, rotation);
					//Go to right until there's a cell above:
			
					int leftMostRelativeTopLeftGrounded = sideBump - 6;
					
					//TODO: this only works for layer index 0. It's ok for now, but will need to change once we change types
					// of solutions (i.e. Once we move away from just Simple phase solutions)
					if(leftMostRelativeTopLeftGrounded >= 0 && leftMostRelativeTopLeftGrounded < 4) {
					
						boolean tmpArray[] = new boolean[Utils.getTotalArea(this.dimensions)];
						
						for(int i=0; i<leftMostRelativeTopLeftGrounded; i++) {
				
							cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
						}
						
						Coord2D cellAbove = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
						
						tmpArray[cellAbove.i] = true;
						tmpArrayForAnySideBump[cellAbove.i] = true;
						
						answerSheetForTopCell[index][rotation][sideBump] = convertBoolArrayToLongs(tmpArray);
						//return ! this.cellsUsed[cellAbove.i];
			
					} else {
						answerSheetForTopCell[index][rotation][sideBump] = setImpossibleForTopAnswerSheet();
					}
				}
				
				answerSheetForTopCellAnySideBump[index][rotation] = convertBoolArrayToLongs(tmpArrayForAnySideBump);
			}
		}
		
	}

	private long[] convertBoolArrayToLongs(boolean tmpArray[]) {
		
		//1st entry:
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = 0;
		}
		
		for(int i=0; i<tmpArray.length; i++) {
			
			if(tmpArray[i]) {
				int indexArray = i / NUM_BITS_IN_LONG;
				int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
				
				ret[indexArray] += 1L << bitShift;
			}
		}
		
		
		return ret;
	}

	public void debugMakeSureCuboidIsFilledExceptForTop() {
		boolean array[] = debugGetBoolArrayFromLongs(curState, getNumCellsToFill());
		
		int numInactive = 0;
		for(int i=0; i<array.length; i++) {
			if(! array[i]) {
				
				numInactive++;

				if(numInactive > 1) {
					System.out.println("ERROR: cuboid is not filled!");
					printInactiveStateFromLongs(curState, getNumCellsToFill());
					System.exit(1);
				}
			}
		}
	}
	
	
	public int debugGetTopCell() {
		boolean array[] = debugGetBoolArrayFromLongs(curState, getNumCellsToFill());
		
		int curRet = -1;
		
		for(int i=0; i<array.length; i++) {
			if(! array[i]) {
				
				if(curRet != -1) {
					System.out.println("WARNING: more than 1 top cell?");
				}
				curRet = i;
			}
		}
		
		return curRet;
	}
	public void printStateFromLongs() {
		
		printStateFromLongs(curState, getNumCellsToFill());
	}
	
	public void printStateFromLongs(long answerSheet[]) {
		
		printStateFromLongs(answerSheet, getNumCellsToFill());
	}

	public static void printStateFromLongs(long inputLongs[], int numCells) {
	
		boolean array[] = debugGetBoolArrayFromLongs(inputLongs, numCells);
		
		System.out.println("Active cells:");
		for(int i=0; i<array.length; i++) {
			if(array[i]) {
				System.out.println(i);
			}
		}
		System.out.println();
	}
	
	private static void printInactiveStateFromLongs(long inputLongs[], int numCells) {
		
		boolean array[] = debugGetBoolArrayFromLongs(inputLongs, numCells);
		
		System.out.println("Inactive cells:");
		for(int i=0; i<array.length; i++) {
			if(!array[i]) {
				System.out.println(i);
			}
		}
		System.out.println();
	}
	
	public static boolean[] debugGetBoolArrayFromLongs(long inputLongs[], int numCells) {
		boolean ret[] = new boolean[numCells];
		
		for(int i=0; i<ret.length; i++) {
			int indexArray = i / NUM_BITS_IN_LONG;
			int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
			
			if( (inputLongs[indexArray]  & 1L << bitShift) != 0) {
				ret[i] = true;
			} else {
				ret[i] = false;
			}
		}
		return ret;
	}
	
	private static long[] setImpossibleForAnswerSheet() {
		
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = -1L;
		}
		
		return ret;
	}
	
	private static boolean isAnswerSheetImpossible(long sheet[]) {
		for(int i=0; i<sheet.length; i++) {
			if(sheet[i] != -1L) {
				return false;
			}
		}
		return true;
	}
	
	private static long[] setAllPossibleForAnswerSheet() {
		
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = 0L;
		}
		
		return ret;
	}
	
	private static long[] setImpossibleForTopAnswerSheet() {
		
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = 0L;
		}
		
		return ret;
	}

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.neighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_ROTATIONS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_ROTATIONS) % NUM_ROTATIONS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}

	public int getIndexAboveIndex(int curIndex, int rotationRelativeFlatMap) {
		return tryAttachCellInDir(curIndex, rotationRelativeFlatMap, CuboidToFoldOnExtendedSimplePhase4.ABOVE).i;
	}
	
	//Pre: The cuboid is built
	public Nx1x1CuboidToFold createSolutionToPrint(int topSideBump) {
		
		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(DIM_N_OF_Nx1x1);
		
		
		//1st layer:
		reference.addNextLevel(new Coord2D(0, prevSideBumps[1]), null);
		
		//In between layers:
		for(int i=2; i<prevSideBumps.length - 1; i++) {
			if(prevLayerStateIndex[i] >= 4) {
				//TODO: hide this hack...
				reference.addNextLevel(new Coord2D(prevLayerStateIndex[i] - 3, prevSideBumps[i]), null);
			} else {
				reference.addNextLevel(new Coord2D(prevLayerStateIndex[i], prevSideBumps[i]), null);
			}
		}
		
		//Put top cell:
		reference.addNextLevel(new Coord2D(0, topSideBump), null);
		
		return reference;
	}
	
	// Only for testing:
	public static void main(String args[]) {

		sanityTestGetCellsToAddGoingUpAndDown();
		
		CuboidToFoldOnExtendedSimplePhase4 test1 = new CuboidToFoldOnExtendedSimplePhase4(5, 1, 1);
		
		for(int m=0; m<7; m++) {
			System.out.println("m = " + m);
			Coord2D array[] = test1.getSideBumpAndStateIdArrayToIterateOverByPreviousLayer(m);
			
			for(int i=0; i<array.length; i++) {
				System.out.println(array[i].i + ", " + array[i].j);
			}
			
			System.out.println();
		}
		

		//CuboidToFoldOnExtendedSimplePhase1 test2 = new CuboidToFoldOnExtendedSimplePhase1(3, 2, 1);
		
	}

	public static void sanityTestGetCellsToAddGoingUpAndDown() {
		
		int goingUp[][] = new int[NUM_LAYER_STATES][LEVEL_OPTIONS.length];
		int goingDown[][] = new int[NUM_LAYER_STATES][LEVEL_OPTIONS.length];
		


		int goingUpMiddle[][] = new int[NUM_LAYER_STATES][LEVEL_OPTIONS.length];
		int goingUpSide[][] = new int[NUM_LAYER_STATES][LEVEL_OPTIONS.length];
		
		
		//1st
		for(int j=0; j<LEVEL_OPTIONS.length; j++) {
			goingUp[0][j] = LEVEL_OPTIONS[0][j];
			goingDown[0][j] = 0;
			
			goingUpMiddle[0][j] = LEVEL_OPTIONS[0][j];
			goingUpSide[0][j] = 0;
		}
		
		//left and middle ones going up and right going down:
		for(int i=1; i<4; i++) {
			
			int numZeroFoundAfter1 = 0;
			boolean currentlyReadOnes = false;

			for(int j=0; j<LEVEL_OPTIONS.length; j++) {
				
				if(LEVEL_OPTIONS[i][j] == 1) {
					currentlyReadOnes = true;
				} else if(LEVEL_OPTIONS[i][j] == 0) {

					if(currentlyReadOnes) {
						numZeroFoundAfter1++;
					}
					currentlyReadOnes = false;
				}
				
				goingUp[i][j] = 0;
				goingDown[i][j] = 0;
				
				if(numZeroFoundAfter1 == 0) {
					goingUpSide[i][j] = LEVEL_OPTIONS[i][j];
				}
				
				if(numZeroFoundAfter1 == 1) {
					goingUpMiddle[i][j] = LEVEL_OPTIONS[i][j];
				}
				
				if(numZeroFoundAfter1 < 2) {
					goingUp[i][j] = LEVEL_OPTIONS[i][j];
				}
				
				if(numZeroFoundAfter1 >= 2) {
					goingDown[i][j] = LEVEL_OPTIONS[i][j];
				}
			}
		}
		
		int INDEX_ADJUST = 3;
		
		//middle and right ones going up and right going down:
		for(int i=1; i<4; i++) {
			
			int numZeroFoundAfter1 = 0;
			boolean currentlyReadOnes = false;

			for(int j=0; j<LEVEL_OPTIONS.length; j++) {
				
				if(LEVEL_OPTIONS[i][j] == 1) {
					currentlyReadOnes = true;
				} else if(LEVEL_OPTIONS[i][j] == 0) {

					if(currentlyReadOnes) {
						numZeroFoundAfter1++;
					}
					currentlyReadOnes = false;
				}
				
				goingUp[i + INDEX_ADJUST][j] = 0;
				goingDown[i + INDEX_ADJUST][j] = 0;
				
				if(numZeroFoundAfter1 < 1) {
					goingDown[i + INDEX_ADJUST][j] = LEVEL_OPTIONS[i][j];
				}
				
				if(numZeroFoundAfter1 >= 1) {
					goingUp[i + INDEX_ADJUST][j] = LEVEL_OPTIONS[i][j];
				}
				
				if(numZeroFoundAfter1 == 1) {
					goingUpMiddle[i + INDEX_ADJUST][j] = LEVEL_OPTIONS[i][j];
				}
				
				if(numZeroFoundAfter1 == 2) {
					goingUpSide[i + INDEX_ADJUST][j] = LEVEL_OPTIONS[i][j];
				}
			}
		}
		
		
		
		//Sanity test:
		
		for(int i=0; i<NUM_LAYER_STATES; i++) {
			for(int j=0; j<LEVEL_OPTIONS.length; j++) {

				if(goingDown[i][j] != CELLS_TO_ADD_BY_STATE_GOING_DOWN[i][j]) {
					System.out.println("ERROR: unexpected value for CELLS_TO_ADD_BY_STATE_GOING_DOWN. (" + i + "," + j + ")");
					System.exit(1);
				}

				if(goingUpMiddle[i][j] != CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i][j]) {
					System.out.println("ERROR: unexpected value for CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE. (" + i + "," + j + ")");
					System.exit(1);
				}

				if(goingUpSide[i][j] != CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i][j]) {
					System.out.println("ERROR: unexpected value for CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE. (" + i + "," + j + ")");
					System.exit(1);
				}
			}
		}
		
	}
	

	private void debugPrintLowerAndUpperLayers(int layerStateBelow, int layerStateAbove, int sideBump, int indexGroundedMidBelow, int rotationGroundedMidBelow) {
		if(indexGroundedMidBelow == 0 && rotationGroundedMidBelow == 0) {
			System.out.println("Hello new condition...");
			System.out.println(layerStateBelow + ", " + layerStateAbove + ", " + sideBump);
			
			Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(3);
			reference.addNextLevel(new Coord2D(0, 6), null);
			reference.addNextLevel(new Coord2D(translateStateToLayerIndex(layerStateBelow), 6), null);
			reference.addNextLevel(new Coord2D(translateStateToLayerIndex(layerStateAbove), sideBump), null);
			System.out.println(reference);
		}
	}

}
