package SolutionResolver;

import Model.CuboidToFoldOn;
import Model.Utils;
import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;

public class StandardResolverForSmallIntersectSolutions implements SolutionResolverInterface {

	private long numUniqueFound = 0;
	private long numFound = 0;
	
	public StandardResolverForSmallIntersectSolutions() {
		
		
	}
	
	@Override
	public long resolveSolution(CuboidToFoldOn cuboidDimensionsAndNeighbours, Coord2D[] paperToDevelop,
			int[][][] indexCuboidonPaper, boolean[][] paperUsed) {	

		System.out.println(numFound +
				" (num unique: " + numUniqueFound + ")");
		
		numFound++;
		

		if(BasicUniqueCheckImproved.isUnique(paperToDevelop, paperUsed)) {
			numUniqueFound++;

			System.out.println("----");
			System.out.println("Unique solution found:");
			Utils.printFold(paperUsed);
			System.out.println("Shape 1:");
			Utils.printFoldWithIndex(indexCuboidonPaper[0]);
			System.out.println("Shape 2:");
			Utils.printFoldWithIndex(indexCuboidonPaper[1]);
			
			System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
			System.out.println("Num unique solutions found: " + 
					numUniqueFound);

			

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
