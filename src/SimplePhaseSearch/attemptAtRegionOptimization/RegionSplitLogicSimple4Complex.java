package SimplePhaseSearch.attemptAtRegionOptimization;


import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import SimplePhaseSearch.sixthIteration.CuboidToFoldOnExtendedSimplePhase6;

//TODO: is this actually faster than RegionSplitLogicSimple?

public class RegionSplitLogicSimple4Complex {

	private CoordWithRotationAndIndex[][] neighbours;

	public RegionSplitLogicSimple4Complex(CoordWithRotationAndIndex[][] neighbours) {
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
			
			//if(layerState != 0) {
			//	continue;
			//}
			for(int index=0; index<neighbours.length; index++) {

				if(index > 1) {
					continue;
				}
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
		System.exit(1);
	}
	
	
	private boolean tmpExplored[];
	private boolean tmpArray[];
	private CustomDangerousQueue queue;


	private long debug_return_true = 0;
	private long debug_return_false = 0;
	private long num_quick2_stops = 0;
	
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
		
		/*System.out.println();
		System.out.println();
		System.out.println("Length: " + preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length);
		*/
		for(int i=0; i<preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length; i++) {
			
			int tmpIndex = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i];
			
			if(tmpArray[tmpIndex]) {
				//System.out.println("tmpIndex: " + tmpIndex);
				numAround++;
			} else {
				//System.out.println("nope: " + tmpIndex);
			}
		}
		
		//System.out.println("......");
		if(lastLayerStateAdded != 0) {
			for(int i=0; i<preComputedCellsAroundCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide].length; i++) {
				
				int tmpIndex = preComputedCellsAroundCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][i];
				
				if(tmpArray[tmpIndex]) {
					//System.out.println("tmpIndex 2: " + tmpIndex);
					numAround++;
				} else {
					//System.out.println("nope 2: " + tmpIndex);
				}
			}
		}
		
		int test1 = preComputedNumCellsExpectedAroundCurLayerMid[layerBeforeLastLayerAdded][lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][prevSideBump];
		int test2 = preComputedNumCellsExpectedAroundCurLayerSide[layerBeforeLastLayerAdded][lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][prevSideBump];
		
		if(lastLayerStateAdded == 0 && test2 > 0) {
			System.out.println("Oops! test2 > 0 when it should be 0");
			System.exit(1);
		}
		if(test1 < 0 || test2 < 0) {
			System.out.println("test1: " + test1);
			System.exit(1);
		}
		
		if(test1 == 0) {
			System.out.println("???");
			System.exit(1);
		}
		
		/*System.out.println("test1: " + test1);
		System.out.println("test2: " + test2);
		System.out.println("numAround: " + numAround);
		*/
		
		if(lastLayerStateAdded != 0) {

			if(numAround < test1 + test2) {
				System.out.println("indexGroundedBelowLayerMid: " + indexGroundedBelowLayerMid);
				System.out.println("rotationGroundedBelowLayerMid: " + rotationGroundedBelowLayerMid);
				System.out.println("layerBeforeLastLayerAdded: " + layerBeforeLastLayerAdded);
				System.out.println("lastLayerStateAdded: " + lastLayerStateAdded);
				System.out.println("Prev side bump: " + prevSideBump);

				System.out.println("indexGroundedBelowLayerSide: " + indexGroundedBelowLayerSide);
				System.out.println("rotationGroundedBelowLayerSide: " + rotationGroundedBelowLayerSide);
				
				int tmp1[] = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid];
				int tmp2[] = preComputedCellsAroundCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide];
				
				
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
	

	/*
	public int untouchableRegionNotCreatedAfterLayerAddedQuick2(long curState[],
			int layerBeforeLastLayerAdded,
			int lastLayerStateAdded,
			int indexGroundedBelowLayerMid,
			int rotationGroundedBelowLayerMid,
			int indexGroundedBelowLayerSide,
			int rotationGroundedBelowLayerSide,
			int prevSideBump,
			int curNumRegions
	) {
		
		if(lastLayerStateAdded == 0) {
			
			//Start with checking the right of state 0...
			//Then do the left, then do it mostly around
			//Then do it with bit shifting.
			int indexTopRight = preComputedCellsAboveCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length;
			int indexBottomRight = indexTopRight + 2;
			
			int curNumber = 0;
			
			for(int i=indexTopRight; i<=indexBottomRight; i++) {
				
				int tmpIndex = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i];
				
				if(isCellIoccupied(curState, tmpIndex)) {
					//System.out.println("tmpIndex: " + tmpIndex);
					curNumber = 2*curNumber + 1;
				} else {
					//System.out.println("nope: " + tmpIndex);
					curNumber = 2*curNumber;
				}
			}
			
			if(curNumber == 4 || curNumber == 5 || curNumber == 6 || curNumber == 2) {
				curNumRegions = curNumRegions + 1;
				//System.out.println(curNumber);
				num_quick2_stops++;
			}
			
		}
		
		//Copy/paste code:
		
		if(lastLayerStateAdded == 0) {
			
			//Start with checking the right of state 0...
			//Then do the left, then do it mostly around
			//Then do it with bit shifting.
			int indexTopLeft = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length - 1;
			int indexBottomLeft = indexTopLeft - 2;
			
			int curNumber = 0;
			
			for(int i=indexBottomLeft; i<=indexTopLeft; i++) {
				
				int tmpIndex = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i];
				
				if(isCellIoccupied(curState, tmpIndex)) {
					//System.out.println("tmpIndex: " + tmpIndex);
					curNumber = 2*curNumber + 1;
				} else {
					//System.out.println("nope: " + tmpIndex);
					curNumber = 2*curNumber;
				}
			}
			
			if(curNumber == 1 || curNumber == 2 || curNumber == 3 || curNumber == 5) {
				curNumRegions = curNumRegions + 1;
				//System.out.println(curNumber);
				
				num_quick2_stops++;
			}
			
		}
		//End copy/paste code

		//Try going around:
		if(lastLayerStateAdded == 0) {
			
			int indexAboveRightMostCellOnLayer = 3;
			int indexAboveLeftMostCellOnLayer = 0;
			
			int start = indexAboveRightMostCellOnLayer;
			
			int lengthArray = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length;
			int end = indexAboveLeftMostCellOnLayer + lengthArray;
			
			
			boolean prevCellOccupied = false;
			int numOccupiedStretches = 0;
			
			for(int i=start; i<= end; i++) {

				int tmpIndex = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i % lengthArray];
				
				if(isCellIoccupied(curState, tmpIndex)) {
					
					if( ! prevCellOccupied) {
						prevCellOccupied = true;
						numOccupiedStretches++;
					}
					//System.out.println("tmpIndex: " + tmpIndex);
				} else {
					prevCellOccupied = false;
					//System.out.println("nope: " + tmpIndex);
				}
			}
			
			if(numOccupiedStretches > 2
			|| (numOccupiedStretches > 1 && layerBeforeLastLayerAdded == 0)) {
				curNumRegions = curNumRegions + 1;
				num_quick2_stops++;
			}
		}
		
		return curNumRegions;
	}*/
	
	public int untouchableRegionNotCreatedAfterLayerAddedQuick3(long curState[],
			int layerBeforeLastLayerAdded,
			int lastLayerStateAdded,
			int indexGroundedBelowLayerMid,
			int rotationGroundedBelowLayerMid,
			int indexGroundedBelowLayerSide,
			int rotationGroundedBelowLayerSide,
			int prevSideBump,
			int curNumRegions
	) {

		//Try going around:
		int indexAboveRightMostCellOnLayerMid = numCellsAbovePerLayerStateMid.length - 1;
		int indexAboveLeftMostCellOnLayerMid = 0;
		
		int start = indexAboveRightMostCellOnLayerMid;
		
		int lengthArray = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length;
		int end = indexAboveLeftMostCellOnLayerMid + lengthArray;
		
		
		boolean prevCellOccupied = false;
		int numOccupiedStretches = 0;
		
		for(int i=start; i<= end; i++) {

			int tmpIndex = preComputedCellsAroundCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i % lengthArray];
			
			if(isCellIoccupied(curState, tmpIndex)) {
				
				if( ! prevCellOccupied) {
					prevCellOccupied = true;
					numOccupiedStretches++;
				}
				//System.out.println("tmpIndex: " + tmpIndex);
			} else {
				prevCellOccupied = false;
				//System.out.println("nope: " + tmpIndex);
			}
		}
		
		int numOccupiedStretches2 = 0;
		if(lastLayerStateAdded > 0) {
			int indexAboveRightMostCellOnLayerSide = numCellsAbovePerLayerStateSide.length - 1;
			int indexAboveLeftMostCellOnLayerSide = 0;
			
			start = indexAboveRightMostCellOnLayerSide;
			
			lengthArray = preComputedCellsAroundCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide].length;
			end = indexAboveLeftMostCellOnLayerSide + lengthArray;
			
			
			prevCellOccupied = false;
			
			for(int i=start; i<= end; i++) {

				int tmpIndex = preComputedCellsAroundCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][i % lengthArray];
				
				if(isCellIoccupied(curState, tmpIndex)) {
					
					if( ! prevCellOccupied) {
						prevCellOccupied = true;
						numOccupiedStretches2++;
					}
					//System.out.println("tmpIndex: " + tmpIndex);
				} else {
					prevCellOccupied = false;
					//System.out.println("nope: " + tmpIndex);
				}
			}
		}
		
		
		if(numOccupiedStretches > 2
		|| (numOccupiedStretches > 1 && layerBeforeLastLayerAdded == 0 && lastLayerStateAdded == 0)) {
			curNumRegions = curNumRegions + (numOccupiedStretches-1);
			num_quick2_stops++;
		}
		
		if(numOccupiedStretches2 + numOccupiedStretches > 3) {
			System.exit(1);
			curNumRegions += 1;
			num_quick2_stops++;
		}
		
		return curNumRegions;
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

	
	// This check is meant to save time by checking if there's a split before iterating through
	// every sidebump.
	// This does a breadth-first search on the remaining empty cells, so it's a little bit slow, and 
	// I'm hoping to limit the number of times this needs to get called.
	// I implemented a bare-bones queue that barely works for this usecase. It's not something I'm proud of.
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
		if(this.queue.isEmpty()) {
			//The mid section should continue, and if the queue is empty, it can't continue:
			return true;
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
		
		if((debug_return_true + debug_return_false + 1) % 1000000L == 0) {
			System.out.println("Basic region split function stats check: ");
			
			System.out.println("Debug_quick_2_stops: " + num_quick2_stops);
			System.out.println("debug_return_true: " + debug_return_true);
			System.out.println("debug_return_false: " + debug_return_false);
			
			double perc = (100.0 * debug_return_true) / (1.0 * ((debug_return_true + debug_return_false)));
			System.out.println("Percent true: " + perc);
			
			
			double percQuick2 = (100.0 * num_quick2_stops) / (1.0 * ((debug_return_true + debug_return_false)));
			System.out.println("Percent quick2 stops: " + percQuick2);
			
		}

		for(int i=0; i<tmpArray.length; i++) {
			if( ! tmpArray[i] && ! tmpExplored[i]) {
				debug_return_true++;
				return true;
			}
		}
		debug_return_false++;
		return false;
		
	}
	
	//Same as above, but we only care about whether or not the region split in 2.
	//Maybe there's a function that could do this and the above one at the same time, but
	// I'll think about that later.
	public boolean twoRegionsCreatedAfterLayerAdded(long curState[],
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
	
		boolean addedRoot = false;

		for(int i=0; i<preComputedCellsAboveCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide].length; i++) {
			int tmpIndex = preComputedCellsAboveCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][i];
			if(! tmpArray[tmpIndex]) {
				this.tmpExplored[tmpIndex] = true;
				this.queue.add(tmpIndex);
				addedRoot = true;
				break;
			}
		}
		
		if(addedRoot == false) {
			for(int i=0; i<preComputedCellsAboveCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length; i++) {
				
				int tmpIndex = preComputedCellsAboveCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i];
				if(! tmpArray[tmpIndex]) {
					this.tmpExplored[tmpIndex] = true;
					this.queue.add(tmpIndex);
					addedRoot = true;
					break;
				}
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

	//Same as above, but we only care about whether or not the region split in 2.
		//Maybe there's a function that could do this and the above one at the same time, but
		// I'll think about that later.
		public boolean LeftRegionOfTwoRegionsCreatedAfterLayerAddedBigger(long curState[],
				int lastLayerStateAdded,
				int indexGroundedBelowLayerMid,
				int rotationGroundedBelowLayerMid
		) {
			
			if(lastLayerStateAdded != 0) {
				System.out.println("OOPS! LeftRegionOfTwoRegionsCreatedAfterLayerAddedBigger is only applicable to the case where the last Layer State is 0");
				System.exit(1);
			}
			
			int numCellsUnoccupied = 0;
			int numCellsFoundUnoccupied = 0;
			
			for(int i=0; i<tmpArray.length; i++) {
				this.tmpArray[i] = isCellIoccupied(curState, i);
				if( ! this.tmpArray[i]) {
					numCellsUnoccupied++;
				}
				this.tmpExplored[i] = false;
			}
			
			if(numCellsUnoccupied % 4 != 1) {
				System.out.println("ERROR in LeftRegionOfTwoRegionsCreatedAfterLayerAddedBigger: the number of cells unoccupied should be a 1 mod 4.");
				System.exit(1);
			}
			
			this.queue.resetQueue();
		
			for(int i=0; i<preComputedCellsAboveCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid].length; i++) {
				
				int tmpIndex = preComputedCellsAboveCurLayerMid[lastLayerStateAdded][indexGroundedBelowLayerMid][rotationGroundedBelowLayerMid][i];
				if(! tmpArray[tmpIndex]) {
					this.tmpExplored[tmpIndex] = true;
					this.queue.add(tmpIndex);
					numCellsFoundUnoccupied++;
					break;
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
						numCellsFoundUnoccupied++;
					}
					
				}
				
			}


			return numCellsFoundUnoccupied * 2 > numCellsUnoccupied;
			
		}
	
	//TODO: This might return false when the correct answer is true.
	// Fortunately, that will just slow it down, but not break the algorithm.
	public boolean isolatedSideRegionNotFilledUp(long curState[],
			int lastLayerStateAdded,
			int indexGroundedBelowLayerSide,
			int rotationGroundedBelowLayerSide
	) {
		
		
		for(int i=0; i<tmpArray.length; i++) {
			this.tmpArray[i] = isCellIoccupied(curState, i);
		}
		
		for(int i=0; i<preComputedCellsAboveCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide].length; i++) {
			int tmpIndex = preComputedCellsAboveCurLayerSide[lastLayerStateAdded][indexGroundedBelowLayerSide][rotationGroundedBelowLayerSide][i];
			if(! isCellIoccupied(curState, tmpIndex)) {
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
	
	private int numCellsAbovePerLayerStateMid[];
	private int numCellsAbovePerLayerStateSide[];
	
	//TODO: maybe try handling the case where we go from top to bottom?
	// I'll do that later.
	
	public void initNumCellsAbovePerLayer() {

		numCellsAbovePerLayerStateMid = new int[NUM_LAYER_STATES];
		numCellsAbovePerLayerStateSide = new int[NUM_LAYER_STATES];
				
		for(int i=0; i<numCellsAbovePerLayerStateMid.length; i++) {
			
			boolean foundFirst = false;
			int firstIndex = -1;
			int lastIndex = -1;
			
			for(int j=0; j<CuboidToFoldOnExtendedSimplePhase7.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i].length; j++) {
				
				if(! foundFirst && CuboidToFoldOnExtendedSimplePhase7.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i][j] == 1) {
					firstIndex = j;
					foundFirst = true;

				} else if(foundFirst && CuboidToFoldOnExtendedSimplePhase7.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i][j] == 0) {
					lastIndex = j;
					break;
				}
			}
			if(lastIndex == -1) {
				lastIndex = CuboidToFoldOnExtendedSimplePhase7.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i].length;
			}
			numCellsAbovePerLayerStateMid[i] = lastIndex - firstIndex;
			
		}
		
		//TODO: copy/paste code:
		for(int i=0; i<numCellsAbovePerLayerStateSide.length; i++) {
			
			if(i == 0) {
				numCellsAbovePerLayerStateSide[i] = 0;
				continue;
			}
			
			boolean foundFirst = false;
			int firstIndex = -1;
			int lastIndex = -1;
			
			for(int j=0; j<CuboidToFoldOnExtendedSimplePhase7.CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i].length; j++) {
				
				if(! foundFirst && CuboidToFoldOnExtendedSimplePhase7.CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i][j] == 1) {
					firstIndex = j;
					foundFirst = true;

				} else if(foundFirst && CuboidToFoldOnExtendedSimplePhase7.CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i][j] == 0) {
					lastIndex = j;
					break;
				}
				
			}
			if(lastIndex == -1) {
				lastIndex = CuboidToFoldOnExtendedSimplePhase6.CELLS_TO_ADD_BY_STATE_GOING_UP_ON_SIDE[i].length;
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
						// For the 1st implementatin, I'll just deal with it...
						preComputedCellsAboveCurLayerMid[layerState][index][rotation][indexCurLayer] = above.i;
						
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
					}

					//TODO: copy/paste code:
					cur = new Coord2D(index, rotation);
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateSide[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);

						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// For the 1st implementation, I'll just deal with it...
						preComputedCellsAboveCurLayerSide[layerState][index][rotation][indexCurLayer] = above.i;
						
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
						
					}

					//END TODO: copy/paste code:
				}
			}
		}
	}
	

	// Structure: [NUM_LAYER_STATES][totalArea][NUM_ROTATION][NUM_CELLS_PER_LAYER];
	private int preComputedCellsAroundCurLayerMid[][][][];
	private int preComputedCellsAroundCurLayerSide[][][][];


	public void setupPreComputedCellsAroundCurLayer() {
		
		preComputedCellsAroundCurLayerMid = new int[NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][];
		preComputedCellsAroundCurLayerSide = new int[NUM_LAYER_STATES][neighbours.length][NUM_ROTATIONS][];
		
		for(int layerState=0; layerState<NUM_LAYER_STATES; layerState++) {
			
			for(int index=0; index<neighbours.length; index++) {
				for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
					
					int indexToAdd = 0;

					//TODO: because it's on a cuboid, there might be repeat indexes...
					// For the 1st implementation, I'll just deal with it.
					preComputedCellsAroundCurLayerMid[layerState][index][rotation] = new int[2 * (numCellsAbovePerLayerStateMid[layerState] + 3)];
					
					//System.out.println("Number: " + numCellsAbovePerLayerMid[layerState]);
					Coord2D cur = new Coord2D(index, rotation);
					
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateMid[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
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

					preComputedCellsAroundCurLayerSide[layerState][index][rotation] = new int[2 * (numCellsAbovePerLayerStateSide[layerState] + 3)];
					
					Coord2D cur = new Coord2D(index, rotation);
					
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerStateSide[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
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
								} else if(layerStateBelow == 0 && numExpectedAroundNewLayer == 1) {
									System.out.println("ERROR: Something went wrong in setupNumCellsExpectedAroundLayer. There's not enough cells around the layer.");
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
							
							//END TODO: Copy/paste code for side:
							
						}
					}
				}
			}
				
		}
		
	}
	
	//TODO: Below is copy/paste code that shouldn't be here: 
	
	private static final int NUM_NEIGHBOURS_PER_CELL = 4;
	
	private static final int NUM_BITS_IN_LONG = 64;
	
	public boolean isCellIoccupied(long curState[], int i) {
		int indexArray = i / NUM_BITS_IN_LONG;
		int bitShift = (NUM_BITS_IN_LONG - 1) - i - indexArray * NUM_BITS_IN_LONG;
		
		return ((1L << bitShift) & curState[indexArray]) != 0L;
	}
	
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
