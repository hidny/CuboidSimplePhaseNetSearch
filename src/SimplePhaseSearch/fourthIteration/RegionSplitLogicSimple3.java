package SimplePhaseSearch.fourthIteration;


import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

//TODO: is this actually faster than RegionSplitLogicSimple?

public class RegionSplitLogicSimple3 {

	private CoordWithRotationAndIndex[][] neighbours;

	public RegionSplitLogicSimple3(CoordWithRotationAndIndex[][] neighbours) {
		this.neighbours = neighbours;
		this.tmpExplored = new boolean[this.neighbours.length];
		this.tmpArray = new boolean[this.neighbours.length];
		this.queue = new CustomDangerousQueue(this.neighbours.length);
		

		initNumCellsAbovePerLayer();
		setupAnswerCellsAboveLayer();
		
		setupPreComputedCellsAroundCurLayer();
		
		
		//testPrintIndexesAroundLayer();
		
	}
	
	private void testPrintIndexesAroundLayer() {
		
		System.out.println("Testing indexes around index...");
		
		//TODO: I didn't avoid duplicate indexes...
		//Will have to be careful about the corner indexes..
		for(int layerState=0; layerState<NUM_LAYER_STATES; layerState++) {
			
			if(layerState != 0) {
				continue;
			}
			for(int index=0; index<neighbours.length; index++) {
				for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
					
					System.out.println("State: " + layerState);
					System.out.println("Ground index: " + index);
					System.out.println("Ground rotation: " + rotation);
					System.out.println();
					for(int indexAround=0; indexAround<preComputedCellsAroundCurLayerMid[layerState][index][rotation].length; indexAround++) {

						System.out.println(preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexAround]);
						
					}
					System.out.println();
					
				}
			}
		}
		
		System.out.println("Done");
		//System.exit(1);
	}
	
	// Structure: [totalArea][NUM_ROTATION][NUM_LAYER_STATES][NUM_CELLS_PER_LAYER];

	
	private boolean tmpExplored[];
	private boolean tmpArray[];
	private CustomDangerousQueue queue;

	//TODO: have special code that dismisses adding layer state 0.
	//TODO: make it faster
	//pre: layerBeforeLastLayerAdded does not represent the bottom cell. (I guess it doesn't matter too much though.)
	public boolean untouchableRegionNotCreatedAfterLayerAddedQuick(long curState[],
			int layerBeforeLastLayerAdded,
			int lastLayerStateAdded,
			int indexGroundedBelowLayerMid,
			int rotationGroundedBelowLayerMid,
			int indexGroundedBelowLayerSide,
			int rotationGroundedBelowLayerSide,
			int prevSideBump,
			int curNumRegions
	) {
		
		if( layerBeforeLastLayerAdded != 0 && lastLayerStateAdded == 0) {
			return false;
		}
		//TODO:
		// make this faster later.
		

		for(int i=0; i<tmpArray.length; i++) {
			this.tmpArray[i] = isCellIoccupied(curState, i);
		}
		
		int numAround = 0;
		
		System.out.println();
		System.out.println();
		System.out.println("Length: " + preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length);
		for(int i=0; i<preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length; i++) {
			
			int tmpIndex = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i];
			
			if(tmpArray[tmpIndex]) {
				System.out.println("tmpIndex: " + tmpIndex);
				numAround++;
			} else {
				System.out.println("nope: " + tmpIndex);
			}
		}
		
		if(lastLayerStateAdded != 0) {
			for(int i=0; i<preComputedCellsAroundCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide].length; i++) {
				
				int tmpIndex = preComputedCellsAroundCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][i];
				
				if(tmpArray[tmpIndex]) {
					
					numAround++;
				}
			}
		}
		
		int test1 = preComputedNumCellsExpectedAroundCurLayerMid[layerBeforeLastLayerAdded][lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][prevSideBump];
		int test2 = preComputedNumCellsExpectedAroundCurLayerSide[layerBeforeLastLayerAdded][lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][prevSideBump];
		
		if(test1 < 0 || test2 < 0) {
			System.out.println("test1: " + test1);
			System.exit(1);
		}
		
		if(test1 == 0) {
			System.out.println("???");
			System.exit(1);
		}
		
		System.out.println("test1: " + test1);
		System.out.println("test2: " + test2);
		System.out.println("numAround: " + numAround);
		
		
		if(lastLayerStateAdded != 0) {

			if(numAround < test1 + test2) {
				System.out.println("indexGroundedBelowLayerMid: " + indexGroundedBelowLayerMid);
				System.out.println("rotationGroundedBelowLayerMid: " + rotationGroundedBelowLayerMid);
				System.out.println("layerBeforeLastLayerAdded: " + layerBeforeLastLayerAdded);
				System.out.println("lastLayerStateAdded: " + lastLayerStateAdded);
				System.out.println("Prev side bump: " + prevSideBump);
				
				System.out.println(numAround);
				System.out.println(test1);
				System.out.println(test2);
				System.out.println("oops! NumAround is too low (1)");
				System.exit(1);
			}
			return numAround == test1
			        + test2;
		} else {
			if(numAround < test1) {
				System.out.println("oops! NumAround is too low (2)");
				System.out.println("indexGroundedBelowLayerMid: " + indexGroundedBelowLayerMid);
				System.out.println("rotationGroundedBelowLayerMid: " + rotationGroundedBelowLayerMid);
				System.out.println("layerBeforeLastLayerAdded: " + layerBeforeLastLayerAdded);
				System.out.println("lastLayerStateAdded: " + lastLayerStateAdded);
				System.out.println("Prev side bump: " + prevSideBump);
				
				System.out.println(numAround + " vs " + test1);
				System.exit(1);
			}
			return numAround == test1;
			
		}
	}
	
	public boolean untouchableRegionCreatedAfterLayerAddedQuick(long curState[],
			int lastLayerStateAdded,
			int layerBeforeLastLayerAdded,
			int indexGroundedBelowLayerMid,
			int rotationGroundedBelowLayerMid,
			int indexGroundedBelowLayerSide,
			int rotationGroundedBelowLayerSide
	) {
		return false;
	}
	

	private int preComputedCellsAboveCurLayerMid[][][][];
	private int preComputedCellsAboveCurLayerSide[][][][];

	//TODO:
	// This check is meant to save time by checking if there's a split before iterating through
	// every sidebump:
	public boolean untouchableRegionCreatedAfterLayerAdded(long curState[],
			int lastLayerStateAdded,
			int indexGroundedBelowLayerMid,
			int rotationGroundedBelowLayerMid,
			int indexGroundedBelowLayerSide,
			int rotationGroundedBelowLayerSide
	) {
		
		
		for(int i=0; i<tmpArray.length; i++) {
			this.tmpArray[i] = isCellIoccupied(curState, i);
			this.tmpExplored[i] = false;
		}
		this.queue.resetQueue();
	
		for(int i=0; i<preComputedCellsAboveCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length; i++) {
			
			int tmpIndex = preComputedCellsAboveCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i];
			if(! tmpArray[tmpIndex]) {
				this.tmpExplored[tmpIndex] = true;
				this.queue.add(tmpIndex);
			}
		}
		
		for(int i=0; i<preComputedCellsAboveCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide].length; i++) {
			int tmpIndex = preComputedCellsAboveCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][i];
			if(! tmpArray[tmpIndex]) {
				this.tmpExplored[tmpIndex] = true;
				this.queue.add(tmpIndex);
			}
		}
		
		Integer v;
		
		while( ! this.queue.isEmpty()) {
			
			v = this.queue.poll();
			
			for(int i=0; i<NUM_NEIGHBOURS_PER_CELL; i++) {
				
				int neighbourIndex = this.neighbours[v.intValue()][i].getIndex();
				
				if( ! tmpArray[neighbourIndex] && ! tmpExplored[neighbourIndex]) {
					tmpExplored[neighbourIndex] = true;
					this.queue.add(neighbourIndex);
				}
				
			}
			
		}

		for(int i=0; i<tmpArray.length; i++) {
			if( ! tmpArray[i] && ! tmpExplored[i]) {

				return true;
			}
		}

		return false;
		
	}
	
	//Pre-compute:

	public static final int NUM_LAYER_STATES = 7;

	public static final int NUM_SIDE_BUMP_OPTIONS = 13;
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_CELLS_PER_LAYER = 4;
	

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	//TODO: this is only for going up through the middle...
	// Try to also cover the case of going up on the side and going from top to bottom...

	//TODO: handle the case of the 1st layer separately (i.e: bottom cell + 4 more cells)
	
	private int numCellsAbovePerLayerStateMid[];
	private int numCellsAbovePerLayerStateSide[];
	
	public void initNumCellsAbovePerLayer() {

		numCellsAbovePerLayerStateMid = new int[NUM_LAYER_STATES];
		numCellsAbovePerLayerStateSide = new int[NUM_LAYER_STATES];
				
		for(int i=0; i<numCellsAbovePerLayerStateMid.length; i++) {
			
			boolean foundFirst = false;
			int firstIndex = -1;
			int lastIndex = -1;
			
			for(int j=0; j<CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i].length; j++) {
				
				if(! foundFirst && CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i][j] == 1) {
					firstIndex = j;
					foundFirst = true;

				} else if(foundFirst && CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i][j] == 0) {
					lastIndex = j;
					break;
				}
			}
			if(lastIndex == -1) {
				lastIndex = CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i].length;
			}
			numCellsAbovePerLayerStateMid[i] = lastIndex - firstIndex;
			
		}
		
		//TODO: copy/paste code:
		for(int i=0; i<numCellsAbovePerLayerStateSide.length; i++) {
			
			boolean foundFirst = false;
			int firstIndex = -1;
			int lastIndex = -1;
			
			for(int j=0; j<CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i].length; j++) {
				
				if(! foundFirst && CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i][j] == 1) {
					firstIndex = j;
					foundFirst = true;

				} else if(foundFirst && CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i][j] == 0) {
					lastIndex = j;
				}
				
			}
			if(lastIndex == -1) {
				lastIndex = 7;
			}
			numCellsAbovePerLayerStateSide[i] = lastIndex - firstIndex;
			
		}
		//END TODO copy/paste code
		
		
	}
	
	public void setupAnswerCellsAboveLayer() {


		// Structure: [NUM_LAYER_STATES][totalArea][NUM_ROTATION][NUM_CELLS_PER_LAYER];

		preComputedCellsAboveCurLayerMid = new int[NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][];
		preComputedCellsAboveCurLayerSide = new int[NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][];
		
		for(int layerState=0; layerState<NUM_LAYER_STATES; layerState++) {
			
			for(int index=0; index<neighbours.length; index++) {
				for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
					
					preComputedCellsAboveCurLayerMid[layerState][index][rotation] = new int[numCellsAbovePerLayerStateMid[layerState]];
					preComputedCellsAboveCurLayerSide[layerState][index][rotation] = new int[numCellsAbovePerLayerStateSide[layerState]];
					

					Coord2D cur = new Coord2D(index, rotation);
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateMid[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// I'll deal with this later.
						preComputedCellsAboveCurLayerMid[layerState][index][rotation][indexCurLayer] = above.i;
						
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
					}

					cur = new Coord2D(index, rotation);
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateSide[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);

						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// I'll deal with this later.
						preComputedCellsAboveCurLayerSide[layerState][index][rotation][indexCurLayer] = above.i;
						
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
						
					}
				}
			}
		}
	}
	

	
	//TODO
	// Structure: [NUM_LAYER_STATES][totalArea][NUM_ROTATION][NUM_CELLS_PER_LAYER];
	private int preComputedCellsAroundCurLayerMid[][][][];
	private int preComputedCellsAroundCurLayerSide[][][][];


	public void setupPreComputedCellsAroundCurLayer() {
		
		preComputedCellsAroundCurLayerMid = new int[NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][];
		preComputedCellsAroundCurLayerSide = new int[NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][];
		
		for(int layerState=0; layerState<NUM_LAYER_STATES; layerState++) {
			
			//if(layerState != 0) {
				//TODO: remove this after testing.
			//	continue;
			//}
			
			for(int index=0; index<neighbours.length; index++) {
				for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
					
					int indexToAdd = 0;

					//TODO: because it's on a cuboid, there might be repeat indexes... that doesn't work...
					preComputedCellsAroundCurLayerMid[layerState][index][rotation] = new int[2 * (numCellsAbovePerLayerStateMid[layerState] + 3)];
					
					//System.out.println("Number: " + numCellsAbovePerLayerMid[layerState]);
					Coord2D cur = new Coord2D(index, rotation);
					
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateMid[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// I'll deal with this later.
						preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = above.i;
						indexToAdd++;

							
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
					}
					
					cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
					
					Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
					Coord2D aboveRight = tryAttachCellInDir(above.i, above.j, RIGHT);

					preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = aboveRight.i;
					indexToAdd++;
					
					Coord2D right = tryAttachCellInDir(cur.i, cur.j, RIGHT);

					preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = right.i;
					indexToAdd++;
					
					Coord2D belowOutsideLoop = tryAttachCellInDir(cur.i, cur.j, BELOW);
					
					Coord2D belowRight = tryAttachCellInDir(belowOutsideLoop.i, belowOutsideLoop.j, RIGHT);
					
					preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = belowRight.i;
					indexToAdd++;
					
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateMid[layerState]; indexCurLayer++) {
						
						Coord2D below = tryAttachCellInDir(cur.i, cur.j, BELOW);
						
						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// I'll deal with this later.
						preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = below.i;
						indexToAdd++;

						cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
						
					}

					cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
					
					belowOutsideLoop = tryAttachCellInDir(cur.i, cur.j, BELOW);
					
					Coord2D belowLeft = tryAttachCellInDir(belowOutsideLoop.i, belowOutsideLoop.j, LEFT);
					
					preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = belowLeft.i;
					indexToAdd++;
					
					Coord2D left = tryAttachCellInDir(cur.i, cur.j, LEFT);
					
					preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = left.i;
					indexToAdd++;
					

					above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
					Coord2D aboveLeft = tryAttachCellInDir(above.i, above.j, LEFT);
					

					preComputedCellsAroundCurLayerMid[layerState][index][rotation][indexToAdd] = aboveLeft.i;
					indexToAdd++;

				}
			}
			
			
			//TODO: Please reduce copy/paste code:
			for(int index=0; index<neighbours.length; index++) {
				for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
					
					int indexToAdd = 0;

					//TODO: because it's on a cuboid, there might be repeat indexes... that doesn't work...
					preComputedCellsAroundCurLayerSide[layerState][index][rotation] = new int[2 * (numCellsAbovePerLayerStateSide[layerState] + 3)];
					
					//System.out.println("Number: " + numCellsAbovePerLayerSide[layerState]);
					Coord2D cur = new Coord2D(index, rotation);
					
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateSide[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// I'll deal with this later.
						preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = above.i;
						indexToAdd++;

							
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
					}
					
					cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
					
					Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
					Coord2D aboveRight = tryAttachCellInDir(above.i, above.j, RIGHT);

					preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = aboveRight.i;
					indexToAdd++;
					
					Coord2D right = tryAttachCellInDir(cur.i, cur.j, RIGHT);

					preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = right.i;
					indexToAdd++;
					
					Coord2D belowOutsideLoop = tryAttachCellInDir(cur.i, cur.j, BELOW);
					
					Coord2D belowRight = tryAttachCellInDir(belowOutsideLoop.i, belowOutsideLoop.j, RIGHT);
					
					preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = belowRight.i;
					indexToAdd++;
					
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateSide[layerState]; indexCurLayer++) {
						
						Coord2D below = tryAttachCellInDir(cur.i, cur.j, BELOW);
						
						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// I'll deal with this later.
						preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = below.i;
						indexToAdd++;

						cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
						
					}

					cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
					
					belowOutsideLoop = tryAttachCellInDir(cur.i, cur.j, BELOW);
					
					Coord2D belowLeft = tryAttachCellInDir(belowOutsideLoop.i, belowOutsideLoop.j, LEFT);
					
					preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = belowLeft.i;
					indexToAdd++;
					
					Coord2D left = tryAttachCellInDir(cur.i, cur.j, LEFT);
					
					preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = left.i;
					indexToAdd++;
					

					above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
					Coord2D aboveLeft = tryAttachCellInDir(above.i, above.j, LEFT);
					

					preComputedCellsAroundCurLayerSide[layerState][index][rotation][indexToAdd] = aboveLeft.i;
					indexToAdd++;

				}
			}
		}
	}
	
	

	// Structure: [NUM_LAYER_STATES][NUM_LAYER_STATES][totalArea][NUM_ROTATION][sideBump]
	private int preComputedNumCellsExpectedAroundCurLayerMid[][][][][];
	private int preComputedNumCellsExpectedAroundCurLayerSide[][][][][];
	
	public static final int NUM_POSSIBLE_SIDE_BUMPS = 13;

	public void setupNumCellsExpectedAroundLayer(
			int newGroundedIndexAboveMid[][][][][],
			int newGroundedRotationAboveMid[][][][][],
			int newGroundedIndexAboveSide[][][][][],
			int newGroundedRotationAboveSide[][][][][]
		) {
		
		preComputedNumCellsExpectedAroundCurLayerMid = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		preComputedNumCellsExpectedAroundCurLayerSide = new int[NUM_LAYER_STATES][NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][NUM_POSSIBLE_SIDE_BUMPS];
		
		for(int layerStateBelow=0; layerStateBelow<NUM_LAYER_STATES; layerStateBelow++) {
			for(int layerStateAbove=0; layerStateAbove<NUM_LAYER_STATES; layerStateAbove++) {

				for(int index=0; index<neighbours.length; index++) {
					for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
						
						for(int sideBump=0; sideBump< NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
							
							//Initialize to negative numbers. This will hopefully help with debug.
							preComputedNumCellsExpectedAroundCurLayerMid[layerStateBelow][layerStateAbove][index][rotation][sideBump] = -30;
							
							//Default to 0 because in some cases, there are no side layers:
							preComputedNumCellsExpectedAroundCurLayerSide[layerStateBelow][layerStateAbove][index][rotation][sideBump] = 0;
						}
					}
				}
			}
		}
		
		for(int layerStateBelow=0; layerStateBelow<NUM_LAYER_STATES; layerStateBelow++) {
			for(int layerStateAbove=0; layerStateAbove<NUM_LAYER_STATES; layerStateAbove++) {

				for(int indexGroundBelow=0; indexGroundBelow<neighbours.length; indexGroundBelow++) {
					for(int rotationGroundBelow=0; rotationGroundBelow<NUM_ROTATIONS; rotationGroundBelow++) {
						
						for(int sideBump=0; sideBump< NUM_POSSIBLE_SIDE_BUMPS; sideBump++) {
					
							//[layerStateBelow][layerStateAbove][indexGroundedSideBelow][rotationGroundedSideBelow][sideBump]
							
							int midGroundIndexAbove = newGroundedIndexAboveMid[layerStateBelow][layerStateAbove][indexGroundBelow][rotationGroundBelow][sideBump];
							int midGroundRotationAbove = newGroundedRotationAboveMid[layerStateBelow][layerStateAbove][indexGroundBelow][rotationGroundBelow][sideBump];
							
							if(midGroundIndexAbove >= 0 && midGroundRotationAbove >=0) {
								
								//System.out.println(indexGroundBelow + ", " + rotationGroundBelow);
								Coord2D cur = new Coord2D(indexGroundBelow, rotationGroundBelow);
								int expectedIndexesBelow[] = new int[numCellsAbovePerLayerStateMid[layerStateBelow]];
								for(int i=0; i<expectedIndexesBelow.length; i++) {
									
									expectedIndexesBelow[i] = cur.i;
									cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
								}
								
								int numExpectedAroundNewLayer = 0;
								
								int tmpAround[] = 
										preComputedCellsAroundCurLayerMid[layerStateAbove][midGroundIndexAbove][midGroundRotationAbove];
								
								for(int i=0; i<tmpAround.length; i++) {
									
									NEXT_ELELMENT_AROUND:
									for(int j=0; j<expectedIndexesBelow.length; j++) {
										if(tmpAround[i] == expectedIndexesBelow[j]) {
											numExpectedAroundNewLayer++;
											break NEXT_ELELMENT_AROUND;
										}
									}
								}
								//[layerBeforeLastLayerAdded][lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid]
								preComputedNumCellsExpectedAroundCurLayerMid[layerStateBelow][layerStateAbove][midGroundIndexAbove][midGroundRotationAbove][sideBump]
										= numExpectedAroundNewLayer;
								
								if(numExpectedAroundNewLayer == 0) {
									System.out.println("doh");
									System.exit(1);
								}
								
							}
							
							//TODO: Copy/paste code for side:
							int sideGroundIndexAbove = newGroundedIndexAboveSide[layerStateBelow][layerStateAbove][indexGroundBelow][rotationGroundBelow][sideBump];
							int sideGroundRotationAbove = newGroundedRotationAboveSide[layerStateBelow][layerStateAbove][indexGroundBelow][rotationGroundBelow][sideBump];
							
							if(sideGroundIndexAbove >= 0 && sideGroundRotationAbove >=0) {
								
								Coord2D cur = new Coord2D(indexGroundBelow, rotationGroundBelow);
								int expectedIndexesBelow[] = new int[numCellsAbovePerLayerStateSide[layerStateBelow]];
								for(int i=0; i<expectedIndexesBelow.length; i++) {
									
									expectedIndexesBelow[i] = cur.i;
									cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
								}
								
								int numExpectedAroundNewLayer = 0;
								
								int tmpAround[] = 
										preComputedCellsAboveCurLayerSide[layerStateAbove][sideGroundIndexAbove][sideGroundRotationAbove];
								
								for(int i=0; i<tmpAround.length; i++) {
									
									NEXT_ELELMENT_AROUND:
									for(int j=0; j<expectedIndexesBelow.length; j++) {
										if(tmpAround[i] == expectedIndexesBelow[j]) {
											numExpectedAroundNewLayer++;
											break NEXT_ELELMENT_AROUND;
										}
									}
								}
								
								preComputedNumCellsExpectedAroundCurLayerSide[layerStateBelow][layerStateAbove][sideGroundIndexAbove][sideGroundRotationAbove][sideBump]
										= numExpectedAroundNewLayer;
								
							}
							
							//TODO: ahh!
							
						}
					}
				}
			}
				
		}
		
	}
	
	
	private static final int NUM_NEIGHBOURS_PER_CELL = 4;
	
	private static final int NUM_BITS_IN_LONG = 64;
	
	public boolean isCellIoccupied(long curState[], int i) {
		int indexArray = i / NUM_BITS_IN_LONG;
		int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
		
		return ((1L << bitShift) & curState[indexArray]) != 0L;
	}
	
	//TODO: copy/paste code:
	public static final int NUM_LONGS_TO_USE = 2;
	
	private static long[] setImpossibleForAnswerSheet() {
		
		long ret[] = new long[NUM_LONGS_TO_USE];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = -1L;
		}
		
		return ret;
	}
	

	private Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir) {
		CoordWithRotationAndIndex neighbours[] = this.neighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS_PER_CELL;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS_PER_CELL) % NUM_NEIGHBOURS_PER_CELL;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	
	
	//TODO copy/paste code:

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

}
