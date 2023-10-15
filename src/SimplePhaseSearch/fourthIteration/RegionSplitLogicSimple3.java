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
	}
	
	// Structure: [totalArea][NUM_ROTATION][NUM_LAYER_STATES][NUM_CELLS_PER_LAYER];

	private int preComputedCellsAboveCurLayerMid[][][][];
	private int preComputedCellsAboveCurLayerSide[][][][];
	
	private boolean tmpExplored[];
	private boolean tmpArray[];
	private CustomDangerousQueue queue;
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
	
	private int numCellsAbovePerLayerMid[];
	private int numCellsAbovePerLayerSide[];
	
	public void initNumCellsAbovePerLayer() {

		numCellsAbovePerLayerMid = new int[NUM_LAYER_STATES];
		numCellsAbovePerLayerSide = new int[NUM_LAYER_STATES];
				
		for(int i=0; i<numCellsAbovePerLayerMid.length; i++) {
			
			boolean foundFirst = false;
			int firstIndex = -1;
			int lastIndex = -1;
			
			for(int j=0; j<CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i].length; j++) {
				
				if(! foundFirst && CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i][j] == 1) {
					firstIndex = j;
					foundFirst = true;

				} else if(foundFirst && CuboidToFoldOnExtendedSimplePhase4.CELLS_TO_ADD_BY_STATE_GOING_UP_MIDDLE[i][j] == 0) {
					lastIndex = j;
				}
			}
			if(lastIndex == -1) {
				lastIndex = 7;
			}
			numCellsAbovePerLayerMid[i] = lastIndex - firstIndex;
		}
		
		//TODO: copy/paste code:
		for(int i=0; i<numCellsAbovePerLayerSide.length; i++) {
			
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
			numCellsAbovePerLayerSide[i] = lastIndex - firstIndex;
			
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
					
					preComputedCellsAboveCurLayerMid[layerState][index][rotation] = new int[numCellsAbovePerLayerMid[layerState]];
					preComputedCellsAboveCurLayerSide[layerState][index][rotation] = new int[numCellsAbovePerLayerSide[layerState]];
					

					Coord2D cur = new Coord2D(index, rotation);
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerMid[layerState]; indexCurLayer++) {
						
						Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
						
						//TODO: I didn't bother making sure that the indexes in the list are distinct.
						// I'll deal with this later.
						preComputedCellsAboveCurLayerMid[layerState][index][rotation][indexCurLayer] = above.i;
						
						cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);	
					}

					cur = new Coord2D(index, rotation);
					for(int indexCurLayer=0; indexCurLayer<numCellsAbovePerLayerSide[layerState]; indexCurLayer++) {
						
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
