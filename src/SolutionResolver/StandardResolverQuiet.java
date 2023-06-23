package SolutionResolver;

import Model.CuboidToFoldOn;
import Model.Utils;
import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;

public class StandardResolverQuiet implements SolutionResolverInterface {

	private long numUniqueFound = 0;
	private long numFound = 0;
	
	public StandardResolverQuiet() {
		
		
	}
	
	@Override
	public long resolveSolution(CuboidToFoldOn cuboidDimensionsAndNeighbours, Coord2D[] paperToDevelop,
			int[][][] indexCuboidonPaper, boolean[][] paperUsed) {	

		
		numFound++;
		

		if(BasicUniqueCheckImproved.isUnique(paperToDevelop, paperUsed)) {
			numUniqueFound++;

			return 1L;
		} else {

			//System.out.println("Solution not found");
			return 0L;
		}
	}

	public long getNumUniqueFound() {
		return numUniqueFound;
	}


	
}
