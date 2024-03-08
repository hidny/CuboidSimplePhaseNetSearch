package NewModelWithIntersection.fastRegionCheck;

import java.util.HashSet;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class FastRegionCheck {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	// Structure: [totalArea][NUM_ROTATION][NUM_CELLS_PER_LAYER];
	
	private CoordWithRotationAndIndex[][] neighbours;
	private int preComputedCellsAroundCurLayer[][][];
	private int numLongsInState;

	
	//                 [totalArea][NUM_ROTATION][numLongsInState];
	private long preComputedCellsAroundCurLayerLongState[][][];

	private long preComputedCellsAroundCurLayerLongStateHashMult[][][];
	

//  [totalArea][NUM_ROTATION][numLongsInState];
	private HashSet <Long> preComputedCellsAroundCurLayerSplit[][];
	private HashSet <Long> preComputedCellsAroundCurLayerDoNotSplit[][];
	
	//TODO: Make this work:
	public boolean regionSplit(int topLeftIndex, int topLeftRotationRelativeFlatMap) {
		
		long hash = 0L;
		
		for(int i=0; i<this.numLongsInState; i++) {
			hash += preComputedCellsAroundCurLayerLongStateHashMult[topLeftIndex][topLeftRotationRelativeFlatMap][i]
					* preComputedCellsAroundCurLayerLongState[topLeftIndex][topLeftRotationRelativeFlatMap][i];
		}
		
		return preComputedCellsAroundCurLayerSplit[topLeftIndex][topLeftRotationRelativeFlatMap].contains(hash);
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

	
	//Step 3:
	public void setupPreComputedHashsForRegions() {
		
		//TODO:
		
		//TODO: formula for getting 14:
		
		

		for(int index=0; index<neighbours.length; index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {


				
				int cellsAroundCurrentState[] = preComputedCellsAroundCurLayer[index][rotation];
				int numStates = (int) Math.pow(2, cellsAroundCurrentState.length);
				
				HashSet <Integer> stateSplits = new HashSet<Integer>();
				
				for(int state = 0; state < numStates; state++) {
					
					int cur = state;
					
					boolean tmp[] = new boolean[neighbours.length];
					for(int i=0; i<tmp.length; i++) {
						tmp[i] = false;
					}
					
					for(int j=0; j<cellsAroundCurrentState.length; j++) {
						
						if(cur % 2 == 1) {
							tmp[cellsAroundCurrentState[j]] = true;
						}
						cur /= 2;
					}
					
					
					
					//TODO: At this point, we have to figure out if it splits...
					
					//We do this by doing a BFS
					
					
					
				}
				
				//TODO: at this point, we need to determine:
				// 1) preComputedCellsAroundCurLayerLongStateHashMult
				
				//We have to actively check for collisions and dodge them if possible.
				
				// 2) preComputedCellsAroundCurLayerSplit
				// and optionally:
				// 3) preComputedCellsAroundCurLayerDoNotSplit
			}
		}
	}
	
	
	
	
	//Step 2:
	public void setupPreComputedCellsInLongArray() {
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
				
				for(int i=0; i<numLongsInState; i++) {
					preComputedCellsAroundCurLayerLongStateHashMult[index][rotation][i] = 1L;
				}
			}
		}
		
	}
	
	//Step 1:
	public void setupPreComputedCellsAroundCurLayer() {
		
		//preComputedCellsAroundCurLayer = new int[neighbours.length][NUM_ROTATIONS][];
		
		
		for(int index=0; index<neighbours.length; index++) {
			for(int rotation=0; rotation<NUM_ROTATIONS; rotation++) {
				
				int indexToAdd = 0;

				//TODO: because it's on a cuboid, there might be repeat indexes...
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
