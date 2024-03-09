package NewModelWithIntersection.fastRegionCheck;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
import Model.NeighbourGraphCreator;

public class FastRegionCheck {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CoordWithRotationAndIndex neighbours[][] = NeighbourGraphCreator.initNeighbourhood(145, 5, 1, true);
		
		FastRegionCheck test = new FastRegionCheck(neighbours, new long[(neighbours.length / 64) + 1]);
		
		System.out.println("END");
	}
	
	// Structure: [totalArea][NUM_ROTATION][NUM_CELLS_PER_LAYER];
	
	private CoordWithRotationAndIndex[][] neighbours;
	private int numLongsInState;
	
	private int preComputedCellsAroundCurLayer[][][];

	
	//                 [totalArea][NUM_ROTATION][numLongsInState];
	private long preComputedCellsAroundCurLayerLongState[][][];

	private long preComputedCellsAroundCurLayerLongStateHashMult[][][];
	

//  [totalArea][NUM_ROTATION][numLongsInState];
	private HashSet <Long> preComputedCellsAroundCurLayerSplit[][];
	private HashSet <Long> preComputedCellsAroundCurLayerDoNotSplit[][];
	
	//TODO: Make this work:
	public boolean regionSplit(long curState[], int topLeftIndex, int topLeftRotationRelativeFlatMap) {
		
		
		return preComputedCellsAroundCurLayerSplit[topLeftIndex][topLeftRotationRelativeFlatMap]
				.contains(getHash(curState, topLeftIndex, topLeftRotationRelativeFlatMap));
	}
	
	public long getHash(long curState[], int topLeftIndex, int topLeftRotationRelativeFlatMap) {
		long hash = 0L;
		
		for(int i=0; i<this.numLongsInState; i++) {
			hash += preComputedCellsAroundCurLayerLongStateHashMult[topLeftIndex][topLeftRotationRelativeFlatMap][i]
					* (preComputedCellsAroundCurLayerLongState[topLeftIndex][topLeftRotationRelativeFlatMap][i] & curState[i]);
		}
		
		return hash;
	}
	//END TODO
	
	public static final int NUM_SIDE_BUMP_OPTIONS = 13;
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_CELLS_PER_LAYER = 4;
	public static final int NUM_NEIGHBOURS_PER_CELL = 4;
	
	public static final int LAYER_WIDTH = 4;
	

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;

	
	public FastRegionCheck(CoordWithRotationAndIndex[][] neighbours, long curState[]) { 
		
		this.neighbours = neighbours;
		this.numLongsInState = curState.length;
		
		setupPreComputedCellsAroundCurLayer();
		setupPreComputedCellsInLongArray();
		setupPreComputedHashsForRegions();
	}
	
	//Step 1:
	private void setupPreComputedCellsAroundCurLayer() {
		
		preComputedCellsAroundCurLayer = new int[neighbours.length][NUM_ROTATIONS][];
		
		
		for(int index=0; index<neighbours.length; index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
				
				int indexToAdd = 0;

				//Because it's on a cuboid, there might be repeat indexes... but that's ok.
				// For the 1st implementation, I'll just deal with it.
				preComputedCellsAroundCurLayer[index][rotation] = new int[2 * (LAYER_WIDTH + 3)];
				
				//System.out.println("Number: " + numCellsAbovePerLayerMid[layerState]);
				Coord2D cur = new Coord2D(index, rotation);
				
				for(int indexCurLayer=0; indexCurLayer<LAYER_WIDTH; indexCurLayer++) {
					
					Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
					
					preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = above.i;
					indexToAdd++;

						
					cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				}
				
				cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
				
				Coord2D above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
				Coord2D aboveRight = tryAttachCellInDir(above.i, above.j, RIGHT);

				preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = aboveRight.i;
				indexToAdd++;
				
				Coord2D right = tryAttachCellInDir(cur.i, cur.j, RIGHT);

				preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = right.i;
				indexToAdd++;
				
				Coord2D belowOutsideLoop = tryAttachCellInDir(cur.i, cur.j, BELOW);
				
				Coord2D belowRight = tryAttachCellInDir(belowOutsideLoop.i, belowOutsideLoop.j, RIGHT);
				
				preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = belowRight.i;
				indexToAdd++;
				
				for(int indexCurLayer=0; indexCurLayer<LAYER_WIDTH; indexCurLayer++) {
					
					Coord2D below = tryAttachCellInDir(cur.i, cur.j, BELOW);
					
					preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = below.i;
					indexToAdd++;

					cur = tryAttachCellInDir(cur.i, cur.j, LEFT);
					
				}

				cur = tryAttachCellInDir(cur.i, cur.j, RIGHT);
				
				belowOutsideLoop = tryAttachCellInDir(cur.i, cur.j, BELOW);
				
				Coord2D belowLeft = tryAttachCellInDir(belowOutsideLoop.i, belowOutsideLoop.j, LEFT);
				
				preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = belowLeft.i;
				indexToAdd++;
				
				Coord2D left = tryAttachCellInDir(cur.i, cur.j, LEFT);
				
				preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = left.i;
				indexToAdd++;
				

				above = tryAttachCellInDir(cur.i, cur.j, ABOVE);
				Coord2D aboveLeft = tryAttachCellInDir(above.i, above.j, LEFT);
				

				preComputedCellsAroundCurLayer[index][rotation][indexToAdd] = aboveLeft.i;
				indexToAdd++;

			}
			
		}
	}

	//Step 2:
	private void setupPreComputedCellsInLongArray() {
		preComputedCellsAroundCurLayerLongState = new long[neighbours.length][NUM_ROTATIONS][numLongsInState];
		preComputedCellsAroundCurLayerLongStateHashMult = new long[neighbours.length][NUM_ROTATIONS][numLongsInState];
		
		
		for(int index=0; index<neighbours.length; index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
				boolean tmp[] = new boolean[neighbours.length];
				
				for(int i=0; i<tmp.length; i++) {
					tmp[i] = false;
				}
				
				for(int i=0; i<preComputedCellsAroundCurLayer[index][rotation].length; i++) {
					tmp[preComputedCellsAroundCurLayer[index][rotation][i]] = true;
				}
				
				preComputedCellsAroundCurLayerLongState[index][rotation] = convertBoolArrayToLongs(tmp);
				
			}
		}
		
	}

	//Step 3:
	private void setupPreComputedHashsForRegions() {
		
		//TODO:
		
		//TODO: formula for getting 14:
		

		preComputedCellsAroundCurLayerSplit = new HashSet[neighbours.length][NUM_ROTATIONS];
		preComputedCellsAroundCurLayerDoNotSplit  = new HashSet[neighbours.length][NUM_ROTATIONS];

		for(int index=0; index<neighbours.length; index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {

				
				int cellsAroundCurrentState[] = preComputedCellsAroundCurLayer[index][rotation];
				int numStates = (int) Math.pow(2, cellsAroundCurrentState.length);
				
				HashSet <Integer> stateSplits = new HashSet<Integer>();
				HashSet <Integer> stateConnected = new HashSet<Integer>();
				
				for(int stateIndex = 0; stateIndex < numStates; stateIndex++) {
					
					
					//Figure out if it splits
					//by doing a BFS:
					boolean fullyConnected = isFullyConnected(convertStateNumToBoolArray(stateIndex, cellsAroundCurrentState));
					
					if(fullyConnected) {
						stateConnected.add(stateIndex);
					} else {

						stateSplits.add(stateIndex);
					}
					
					
				}
				
				//TODO: at this point, we need to determine:
				// 1) preComputedCellsAroundCurLayerLongStateHashMult
				
				boolean isDone = false;
				
				for(int m=9; isDone == false; m++) {
					
					boolean combo[] = new boolean[m + numLongsInState - 1];
					for(int i=0; i<combo.length; i++) {
						combo[i] = false;
					}
					for(int i=0; i<numLongsInState - 1; i++) {
						combo[i] = true;
					}

					int debugComboIndex = 0;
					
					while(combo != null) {
						
						System.out.println(debugComboIndex);

						preComputedCellsAroundCurLayerSplit[index][rotation] = new HashSet<Long>();
						preComputedCellsAroundCurLayerDoNotSplit[index][rotation] = new HashSet<Long>();
						
						
						int curIndex = 0;
						for(int j=0; j<numLongsInState; j++) {
							
							preComputedCellsAroundCurLayerLongStateHashMult[index][rotation][j] = 1;
							
							while(curIndex < combo.length) {
								
								if(combo[curIndex] == false) {
									preComputedCellsAroundCurLayerLongStateHashMult[index][rotation][j]++;
								} else {
									curIndex++;
									break;
								}
								
								curIndex++;
							}
							
						}
						
						boolean hashCollision = false;
						
						for(int stateIndex = 0; stateIndex < numStates; stateIndex++) {
						//TODO: check if good.
							
							long curHash = getHash(convertBoolArrayToLongs(convertStateNumToBoolArray(stateIndex, cellsAroundCurrentState)),
									index,
									rotation
							);

							if(stateSplits.contains(stateIndex)) {
								preComputedCellsAroundCurLayerSplit[index][rotation].add(curHash);
							} else {
								preComputedCellsAroundCurLayerDoNotSplit[index][rotation].add(curHash);
							}
							
							if(preComputedCellsAroundCurLayerSplit[index][rotation].contains(curHash)
									&& preComputedCellsAroundCurLayerDoNotSplit[index][rotation].contains(curHash)) {
									
								System.out.println("HASH COLLISION (TRY THE NEXT ONE!) m =" + m);
								System.out.println("Combo index: " + debugComboIndex);
								for(int j=0; j<numLongsInState; j++) {
									System.out.println(preComputedCellsAroundCurLayerLongStateHashMult[index][rotation][j]);
								}
								System.out.println();
								hashCollision = true;
								break;
							}
						}
						
						if(hashCollision == false) {
							isDone = true;
							break;
						}
						
						combo = Combination.getNextCombination(combo);
						debugComboIndex++;
					}
					
				}
			}
		}
	}
	
	private boolean[] convertStateNumToBoolArray(int stateIndex, int cellsAroundCurrentState[]) {
		
		int cur = stateIndex;
		boolean ret[] = new boolean[neighbours.length];
		for(int i=0; i<ret.length; i++) {
			ret[i] = false;
		}
		
		for(int j=0; j<cellsAroundCurrentState.length; j++) {
			
			if(cur % 2 == 1) {
				ret[cellsAroundCurrentState[j]] = true;
			}
			cur /= 2;
		}
		
		
		return ret;
	}
	
	private boolean isFullyConnected(boolean state[]) {
		
		Queue <Integer> queue = new LinkedList<Integer>();
		
		int root = -1;
		for(int i=0; i<state.length; i++) {
			if(state[i]) {
				root = i;
				break;
			}
		}
		
		queue.add(root);
		
		boolean found[] = new boolean[state.length];
		for(int i=0; i<state.length; i++) {
			found[i] = false;
		}
		
		while( ! queue.isEmpty()) {
			
			int cur = queue.remove();
			if(cur == -1) {
				System.out.println("WARNING: the state has no entries");
				System.out.println("Might as well reture false.");
				return false;
			}
			
			for(int i=0; i<neighbours[cur].length; i++) {
				int neighbour = neighbours[cur][i].getIndex();
				
				if(found[neighbour] == false) {
					found[neighbour] = true;
					queue.add(neighbour);
					
				}
			}
			
		}
		
		//Check if fully connected:
		boolean ret = true;
		
		for(int i=0; i<state.length; i++) {
			if(state[i] && ! found[i]) {
				ret = false;
				break;
			}
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
	
	
	public final int NUM_BITS_IN_LONG = 64;

	private long[] convertBoolArrayToLongs(boolean tmpArray[]) {
		
		//1st entry:
		long ret[] = new long[numLongsInState];
		
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
