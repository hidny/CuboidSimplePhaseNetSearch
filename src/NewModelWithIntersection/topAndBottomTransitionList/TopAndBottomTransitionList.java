package NewModelWithIntersection.topAndBottomTransitionList;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class TopAndBottomTransitionList {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static int[][] addBottomTransitionsBottom2Mod4(
			CoordWithRotationAndIndex neighbours[][],
			boolean tmpIndexRotLastRing[][],
			boolean tmpIndexRotBottom[][],
			int newGroundedIndexAbove[][][],
			int newGroundedRotationAbove[][][],
			int transitionTopOrBottomSide[][],
			int edgeSquareOnBottom,
			int lastRingIndexUsed,
			int lastRingRotationUsed
	) {
		return addBottomTransitions(
				neighbours,
				tmpIndexRotLastRing,
				tmpIndexRotBottom,
				newGroundedIndexAbove,
				newGroundedRotationAbove,
				transitionTopOrBottomSide,
				lastRingIndexUsed,
				lastRingRotationUsed,
				edgeSquareOnBottom,
				0,
				2);
	}
	
	public static int[][] addBottomTransitionsBottom1Mod4(
			CoordWithRotationAndIndex neighbours[][],
			boolean tmpIndexRotLastRing[][],
			boolean tmpIndexRotBottom[][],
			int newGroundedIndexAbove[][][],
			int newGroundedRotationAbove[][][],
			int transitionTopOrBottomSide[][],
			int edgeSquareOnBottom,
			int lastRingIndexUsed,
			int lastRingRotationUsed
	) {
		return addBottomTransitions(
				neighbours,
				tmpIndexRotLastRing,
				tmpIndexRotBottom,
				newGroundedIndexAbove,
				newGroundedRotationAbove,
				transitionTopOrBottomSide,
				lastRingIndexUsed,
				lastRingRotationUsed,
				edgeSquareOnBottom,
				1,
				1);
	}

	
	private static int[][] addBottomTransitions(
			CoordWithRotationAndIndex neighbours[][],
			boolean tmpIndexRotLastRing[][],
			boolean tmpIndexRotBottom[][],
			int newGroundedIndexAbove[][][],
			int newGroundedRotationAbove[][][],
			int transitionTopOrBottomSide[][],
			int lastRingIndexUsed,
			int lastRingRotationUsed,
			int edgeSquareOnBottom,
			int outputTransitionTopOrBottomSideIndex,
			int outputBottomIndexMod4onRot0
	) {
		
		for(int i=0; i<tmpIndexRotBottom.length; i++) {
			for(int j=0; j<tmpIndexRotBottom[0].length; j++) {
				if(tmpIndexRotBottom[i][j]) {
					System.out.println("hello tmpIndexRotBottom[" + i + "][" + j + "] = " + tmpIndexRotBottom[i][j]);
				}
			}
		}


		boolean tmpIndexRotLastRingUsed[][] = new boolean[neighbours.length][NUM_ROTATIONS];
		for(int i=0; i<tmpIndexRotLastRingUsed.length; i++) {
			for(int j=0; j<tmpIndexRotLastRingUsed[0].length; j++) {
				//tmpIndexRotTopUsed[i][j] = false;
				tmpIndexRotLastRingUsed[i][j] = false;
			}
		}
		System.out.println("---");
		System.out.println("DEBUG March 29 for " + outputBottomIndexMod4onRot0);
		tmpIndexRotLastRingUsed[lastRingIndexUsed][lastRingRotationUsed] = true;
		Coord2D flipped = topLeftIndexRotAfter180Flip1x4layer(neighbours, lastRingIndexUsed, lastRingRotationUsed);
		tmpIndexRotLastRingUsed[flipped.i][flipped.j] = true;
		
		System.out.println("Last Ring Index Used: " + lastRingIndexUsed);
		System.out.println("lastRingRotationUsed Used: " + lastRingRotationUsed);
		System.out.println("---");
		
		
		boolean curProgress = true;
		while(curProgress == true) {
			
			curProgress = false;
			
			//ADD transitionTopOrBottomSide
			for(int i=0; i<tmpIndexRotLastRingUsed.length; i++) {
				for(int j=0; j<tmpIndexRotLastRingUsed[0].length; j++) {
	
					if(tmpIndexRotBottom[i][j] == true ) {
						System.out.println("March 29-2 test " + i + ", " + j);
						System.out.println("outputBottomIndexMod4onRot0: " +outputBottomIndexMod4onRot0);
					}
					if(tmpIndexRotBottom[i][j] == true 
							&& (  		(i % 4 == outputBottomIndexMod4onRot0 && j==0)
									||  (i % 4 == (outputBottomIndexMod4onRot0 + 3)%4 && j==2)
									||   i == edgeSquareOnBottom
								)
						) {
						
						if(i < neighbours.length - 1
								&& i == edgeSquareOnBottom
								&& ((i % 4 == outputBottomIndexMod4onRot0 && j==0)
										||  (i % 4 == (outputBottomIndexMod4onRot0 + 3)%4 && j==2)
								)
							) {

							System.out.println("ERROR: unexpected edge square in addBottomTransitions");
							System.out.println(i);
							System.out.println(outputBottomIndexMod4onRot0);
							System.out.println(j);
							System.exit(1);
						}
						
						//System.out.println("STOP");
						//System.exit(1);
						System.out.println("TEST " + i + ", " + j + " (" + outputBottomIndexMod4onRot0 + " mod 4)");
						for(int tmpSideBump=3; tmpSideBump<=9; tmpSideBump++) {
							/*
							 * 
	int nextIndex = newGroundedIndexAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
	int nextRot = newGroundedRotationAbove[this.topLeftGroundedIndex][this.topLeftGroundRotationRelativeFlatMap][sideBump];
							 */
							
							//System.out.println("TEST2 " + newGroundedIndexAbove[i][j][tmpSideBump]);
							if(newGroundedIndexAbove[i][j][tmpSideBump] < 0) {
								continue;
							}
							
							
							

							//System.out.println("TEST3 " + newGroundedIndexAbove[i][j][tmpSideBump]);
							//System.out.println("TEST3 newGroundedRotationAbove" + newGroundedRotationAbove[i][j][tmpSideBump]);
							
							if(tmpIndexRotLastRing
									[newGroundedIndexAbove[i][j][tmpSideBump]]
									[newGroundedRotationAbove[i][j][tmpSideBump]] == true
									&&
									(	
										(
										tmpIndexRotLastRingUsed
										[newGroundedIndexAbove[i][j][tmpSideBump]]
										[newGroundedRotationAbove[i][j][tmpSideBump]] == false
										
										&& tmpSideBump == 6
										)
									||	
										(
										tmpIndexRotLastRingUsed
										[newGroundedIndexAbove[i][j][tmpSideBump]]
										[newGroundedRotationAbove[i][j][tmpSideBump]] == false
										&& i == edgeSquareOnBottom
										)
									||
									tmpIndexRotLastRingUsed
										[newGroundedIndexAbove[i][j][tmpSideBump]]
										[newGroundedRotationAbove[i][j][tmpSideBump]] == true
										
									)
									
								) {
								
								//System.out.println("GOING FOR IT");
								//System.exit(1);
								
								//Going for it!
								for(int tmpSideBump2=3; tmpSideBump2<=9; tmpSideBump2++) {
									
									if(newGroundedIndexAbove[i][j][tmpSideBump2] < 0) {
										continue;
									}
									
									if(
											tmpIndexRotLastRing
											[newGroundedIndexAbove[i][j][tmpSideBump2]]
											[newGroundedRotationAbove[i][j][tmpSideBump2]] == true
										&&
											tmpIndexRotLastRingUsed
											[newGroundedIndexAbove[i][j][tmpSideBump2]]
											[newGroundedRotationAbove[i][j][tmpSideBump2]] == false) {
										
										
										curProgress = true;
										
										tmpIndexRotLastRingUsed
										[newGroundedIndexAbove[i][j][tmpSideBump2]]
										[newGroundedRotationAbove[i][j][tmpSideBump2]] = true;
										
										//System.out.println("DEBUG MARCH 29");
										
										transitionTopOrBottomSide[outputTransitionTopOrBottomSideIndex][i] = newGroundedIndexAbove[i][j][tmpSideBump2];
										//transitionTopOrBottomSide[0][newGroundedIndexAbove[i][j][tmpSideBump2]] = i;
	
										//System.out.println("transitionTopOrBottomSide 3: " +newGroundedIndexAbove[i][j][tmpSideBump2]);
	
										Coord2D flippedIndexAndRotationBottom = topLeftIndexRotAfter180Flip1x4layer(neighbours, i, j);
										
										Coord2D flippedIndexAndRotationRing = topLeftIndexRotAfter180Flip1x4layer(
												neighbours,
												newGroundedIndexAbove[i][j][tmpSideBump2],
												newGroundedRotationAbove[i][j][tmpSideBump2]);
	
										//if(flippedIndexAndRotationRing.i == 60) {
										//	System.out.println("DEBUG");
										//}
										//System.out.println("transitionTopOrBottomSide 4: " + flippedIndexAndRotationBottom.i);
										
										transitionTopOrBottomSide[outputTransitionTopOrBottomSideIndex][flippedIndexAndRotationRing.i] = flippedIndexAndRotationBottom.i;

										tmpIndexRotLastRingUsed[flippedIndexAndRotationRing.i][flippedIndexAndRotationRing.j] = true;
	
									}
								}
							}
						}
					}
				}
			} //END ADD transitionTopOrBottomSide
						
		}//END progress loop
		
		

		return transitionTopOrBottomSide;
	}

	public static final int ABOVE = 0;
	public static final int RIGHT = 1;
	public static final int BELOW = 2;
	public static final int LEFT = 3;
	
	
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_NEIGHBOURS = 4;
	
	//Copy/paste code I should consider isolating:
	
	public static Coord2D topLeftIndexRotAfter180Flip1x4layer(CoordWithRotationAndIndex neighbours[][], int index, int rotation) {
		Coord2D flippedIndexAndRotation = new Coord2D(index, rotation);
		
		for(int j=0; j<4 - 1; j++) {
			flippedIndexAndRotation = tryAttachCellInDir(neighbours, flippedIndexAndRotation.i, flippedIndexAndRotation.j, RIGHT);
		}
		
		int flipRotation = (flippedIndexAndRotation.j + NUM_ROTATIONS/2) % NUM_ROTATIONS;
		
		return new Coord2D(flippedIndexAndRotation.i, flipRotation);
	}
	

	private static Coord2D tryAttachCellInDir(
			CoordWithRotationAndIndex neighbours[][],
			int curIndex,
			int rotationRelativeFlatMap,
			int dir
		) {

		CoordWithRotationAndIndex neighboursCell[] = neighbours[curIndex];
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighboursCell[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighboursCell[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
}
