package SolutionResolver;

import Coord.Coord2D;

public interface SolutionResolverInterface {

	
	public long resolveSolution(Model.CuboidToFoldOn cuboidDimensionsAndNeighbours, Coord2D paperToDevelop[], int indexCuboidonPaper[][][], boolean paperUsed[][]);
	
	
	public long getNumUniqueFound();
}
