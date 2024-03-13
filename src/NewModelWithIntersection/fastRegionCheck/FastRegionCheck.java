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
		
		CoordWithRotationAndIndex neighbours[][] = NeighbourGraphCreator.initNeighbourhood(27, 5, 1, true);
		
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
	private HashSet <Long> preComputedCellsAroundCurLayerSplitTmp[][];
	private HashSet <Long> preComputedCellsAroundCurLayerDoNotSplit[][];
	
	//TODO: Make this work:
	public boolean regionSplit(long curState[], int topLeftIndex, int topLeftRotationRelativeFlatMap) {
		
		
		return ! preComputedCellsAroundCurLayerDoNotSplit[topLeftIndex][topLeftRotationRelativeFlatMap]
				.contains(getHash(curState, topLeftIndex, topLeftRotationRelativeFlatMap));
	}
	
	public long getHash(long curState[], int topLeftIndex, int topLeftRotationRelativeFlatMap) {
		long hash = 0L;
		
		for(int i=0; i<this.numLongsInState; i++) {
			hash += preComputedCellsAroundCurLayerLongStateHashMult[topLeftIndex][topLeftRotationRelativeFlatMap][i]
					* (preComputedCellsAroundCurLayerLongState[topLeftIndex][topLeftRotationRelativeFlatMap][i] & curState[i]);
			
			
			//TODO: make this look smarter...
			hash += preComputedCellsAroundCurLayerLongStateHashMult[topLeftIndex][topLeftRotationRelativeFlatMap][i]
					* ((preComputedCellsAroundCurLayerLongState[topLeftIndex][topLeftRotationRelativeFlatMap][i] & curState[i]) >> 32);
			
		}
		
		return hash;
	}
	
	public long getHashVerboseDebug(long curState[], int topLeftIndex, int topLeftRotationRelativeFlatMap) {
		long hash = 0L;
		
		for(int i=0; i<this.numLongsInState; i++) {
			System.out.println("i = "+ i + "   " + (preComputedCellsAroundCurLayerLongState[topLeftIndex][topLeftRotationRelativeFlatMap][i] & curState[i]));
			
			long tmp =preComputedCellsAroundCurLayerLongStateHashMult[topLeftIndex][topLeftRotationRelativeFlatMap][i]
					* (preComputedCellsAroundCurLayerLongState[topLeftIndex][topLeftRotationRelativeFlatMap][i] & curState[i]);
			System.out.println("Add: " + tmp);
			hash += tmp;
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
		
		System.out.println("Setting up fast region check. Please wait.");
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

	public boolean[] resetBoolTable(boolean tmpBoolTable[], int preComputedCellsAroundCurLayerIndexRotation[], int stateNum) {
		
		int cur = stateNum;
		for(int i=0; i<preComputedCellsAroundCurLayerIndexRotation.length; i++) {
			
			if(cur % 2 == 1) {
				tmpBoolTable[preComputedCellsAroundCurLayerIndexRotation[i]] = true;
			} else {
				tmpBoolTable[preComputedCellsAroundCurLayerIndexRotation[i]] = false;
				
			}
			cur /= 2;
		}
		
		return tmpBoolTable;
	}
	
	private int[][] getFlagsToChange(int cellsAroundCurrentState[]) {
		
		int ret[][] = new int[cellsAroundCurrentState.length][2];
		
		for(int i=0; i<cellsAroundCurrentState.length; i++) {
			ret[i][0] = cellsAroundCurrentState[i] / NUM_BITS_IN_LONG;
			
			ret[i][1] = (NUM_BITS_IN_LONG - 1) - (cellsAroundCurrentState[i] % NUM_BITS_IN_LONG);
		}
		
		return ret;
	}
	
	private long[] updateCurStateWithStateIndex(long tmpCurState[], int flagsToChange[][], int stateNum) {

		int cur = stateNum;
		for(int i=0; i<flagsToChange.length; i++) {

			tmpCurState[flagsToChange[i][0]] |= 1L << flagsToChange[i][1];
			
			if(cur % 2 == 1) {
				//pass
			} else {
				tmpCurState[flagsToChange[i][0]] ^= 1L << flagsToChange[i][1];
				
			}
			cur /= 2;
		}
		
		return tmpCurState;
	}
	
	int DEBUG_INTERVAL = 100;
	//Step 3:
	private void setupPreComputedHashsForRegions() {
		

		preComputedCellsAroundCurLayerSplitTmp = new HashSet[neighbours.length][NUM_ROTATIONS];
		preComputedCellsAroundCurLayerDoNotSplit  = new HashSet[neighbours.length][NUM_ROTATIONS];
		
		//Made it mutable and slightly dangerous because I want it to go faster:
		long tmpCurState[] = new long[this.numLongsInState];
		boolean tmpBoolTable[] = new boolean[neighbours.length];
		
		

		for(int index=0; index<neighbours.length; index++) {
			
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {

				for(int i=0; i<this.numLongsInState; i++) {
					tmpCurState[i] = -1L;
				}
				for(int i=0; i<tmpBoolTable.length; i++) {
					tmpBoolTable[i] = false;
				}
				
				int cellsAroundCurrentState[] = preComputedCellsAroundCurLayer[index][rotation];
				int numStates = (int) Math.pow(2, cellsAroundCurrentState.length);
				
				HashSet <Integer> stateSplits = new HashSet<Integer>();
				HashSet <Integer> stateConnected = new HashSet<Integer>();
				
				for(int stateIndex = 0; stateIndex < numStates; stateIndex++) {
					
					
					//Figure out if it splits
					//by doing a BFS:
					boolean fullyConnected = isFullyConnected(
							resetBoolTable(tmpBoolTable, preComputedCellsAroundCurLayer[index][rotation], stateIndex),
							cellsAroundCurrentState
							);
					
					if(fullyConnected) {
						stateConnected.add(stateIndex);
					} else {

						stateSplits.add(stateIndex);
					}
					
				}
				
				int flagsToChange[][] = getFlagsToChange(preComputedCellsAroundCurLayer[index][rotation]);
				
				boolean foundCombinationOfHashMultsThatWork = false;
				
				for(int debugComboIndex = 0; foundCombinationOfHashMultsThatWork == false; debugComboIndex++) {
					
					if(debugComboIndex > 0) {
						System.out.println("Debug combo index: " + debugComboIndex + " for " + index + " and " + rotation + ".");
					}
					
					preComputedCellsAroundCurLayerSplitTmp[index][rotation] = new HashSet<Long>();
					preComputedCellsAroundCurLayerDoNotSplit[index][rotation] = new HashSet<Long>();
					
					
					for(int j=0; j<numLongsInState; j++) {
						//TODO: I had to play around with this. I don't know if this is 'random' enough to never find an infinite loop:
						//The good news is, if it fails, there will be an infinite loop, and we'll know there's a problem. 
						preComputedCellsAroundCurLayerLongStateHashMult[index][rotation][j] = 1 + 5*j*j + debugComboIndex;
					}
					
					boolean hashCollisionSoFar = false;
					
					for(int stateIndex = 0; stateIndex < numStates; stateIndex++) {
						
						
						long curHash = getHash(
								updateCurStateWithStateIndex(tmpCurState, flagsToChange, stateIndex),
								index,
								rotation
							);
						
						
						if(stateSplits.contains(stateIndex)) {
							preComputedCellsAroundCurLayerSplitTmp[index][rotation].add(curHash);
						} else {
							preComputedCellsAroundCurLayerDoNotSplit[index][rotation].add(curHash);
						}
						
						if(preComputedCellsAroundCurLayerSplitTmp[index][rotation].contains(curHash)
								&& preComputedCellsAroundCurLayerDoNotSplit[index][rotation].contains(curHash)) {
								
							System.out.println("HASH COLLISION for index " + index + " and rotation " + rotation + ". (TRY THE NEXT ONE!) debugComboIndex: " + debugComboIndex + " ( stateIndex: " + stateIndex + ", curHash: " + curHash + ")");
							/*for(int j=0; j<numLongsInState; j++) {
								System.out.println(preComputedCellsAroundCurLayerLongStateHashMult[index][rotation][j]);
							}*/
							for(int stateIndex2 = 0; stateIndex2 < numStates; stateIndex2++) {
								if(getHash(
										updateCurStateWithStateIndex(tmpCurState, flagsToChange, stateIndex2),
										index,
										rotation
									) == curHash) {
									System.out.println("Prev state: " + stateIndex2);
								}
							}
							System.out.println();
							System.out.println("---");
							//System.exit(1);
							
							hashCollisionSoFar = true;
							break;
						}
					}
					
					if(hashCollisionSoFar == false) {
						foundCombinationOfHashMultsThatWork = true;
						

						/*if(index < 10 || index % DEBUG_INTERVAL == 0) {
							System.out.println("Index: " + index);
							System.out.println("Num split: " + preComputedCellsAroundCurLayerSplitTmp[index][rotation].size());
							System.out.println("Num not split: " + preComputedCellsAroundCurLayerDoNotSplit[index][rotation].size());
							System.out.println();
						}*/
						
						preComputedCellsAroundCurLayerSplitTmp[index][rotation] = null;
					}
				}
					
			}
		}
	}
	
	//BFS search that also tries to set the input state to completely false.
	// If it succeeds, the search returns true.
	//I decided to mess with the input state table because I wanted
	// to not bother with creating a new found array.
	//LOL! I accidentally fixed a bug by making it more efficient!
	private boolean isFullyConnected(boolean state[], int cellsAroundCurrentState[]) {
		
		Queue <Integer> queue = new LinkedList<Integer>();
		
		int root = -1;
		
		for(int i=0; i<cellsAroundCurrentState.length; i++) {
			
			if( ! state[cellsAroundCurrentState[i]]) {
				root = cellsAroundCurrentState[i];
				break;
			}
		}
		
		if(root == -1) {
			//Maybe I should try to remove this edge case?
			//Nah...
			return false;
		}
		queue.add(root);
		state[root] = true;
		
		while( ! queue.isEmpty()) {
			
			int cur = queue.remove();
			
			for(int i=0; i<neighbours[cur].length; i++) {
				int neighbour = neighbours[cur][i].getIndex();
				
				if(! state[neighbour]) {
					state[neighbour] = true;
					queue.add(neighbour);
					
				}
			}
			
		}
		
		//Check if fully connected:
		boolean ret = true;
		
		for(int i=0; i<cellsAroundCurrentState.length; i++) {
			if(! state[cellsAroundCurrentState[i]]) {
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
