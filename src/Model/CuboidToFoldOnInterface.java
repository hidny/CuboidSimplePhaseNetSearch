package Model;

import Coord.CoordWithRotationAndIndex;

public interface CuboidToFoldOnInterface {

	public int[] getDimensions();
	public CoordWithRotationAndIndex[] getNeighbours(int cellIndex);
}
