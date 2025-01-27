package semiGrained.iteration3x3;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;

public class Utils {

	public static int NUM_NEIGHBOURS = 4;
	
	//TODO: should I do this?
	private static Coord2D tryAttachCellInDir(int curIndex, int rotationRelativeFlatMap, int dir, CoordWithRotationAndIndex allNeighbours[][]) {
		CoordWithRotationAndIndex neighbours[] = allNeighbours[curIndex];
		
		int neighbourIndex = (rotationRelativeFlatMap + dir) % NUM_NEIGHBOURS;
		curIndex = neighbours[neighbourIndex].getIndex();
		rotationRelativeFlatMap = (rotationRelativeFlatMap + neighbours[neighbourIndex].getRot() + NUM_NEIGHBOURS) % NUM_NEIGHBOURS;
		
		return new Coord2D(curIndex, rotationRelativeFlatMap);
	}
	
}
