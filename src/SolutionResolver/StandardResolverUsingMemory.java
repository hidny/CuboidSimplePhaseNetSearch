package SolutionResolver;

import Model.CuboidToFoldOn;
import Model.Utils;
import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;

public class StandardResolverUsingMemory implements SolutionResolverInterface {
	
	private int numFound = 0;
	
	public StandardResolverUsingMemory() {
		
	}
	
	@Override
	public long resolveSolution(CuboidToFoldOn cuboidDimensionsAndNeighbours, Coord2D paperToDevelop[], int[][][] indexCuboidonPaper, boolean[][] paperUsed) {
		
		//TODO: Maybe have global vars elsewhere? 
		numFound++;
		
		if(numFound % 1000000 == 0) {
			System.out.println(numFound +
				" (num unique: " + BasicUniqueCheckImproved.uniqList.size() + ")");
			Utils.printFoldWithIndex(indexCuboidonPaper[0]);
		}
		
		if(BasicUniqueCheckImproved.isUnique(paperToDevelop, paperUsed)) {
		//if(memorylessUniqueCheckSkipSymmetriesMemManage2.isUnique(paperToDevelop, paperUsed, indexCuboidonPaper)) {
			
			if(BasicUniqueCheckImproved.uniqList.size() % 100000 == 0) {
				Utils.printFold(paperUsed);
				for(int i=0; i<indexCuboidonPaper.length; i++) {
					Utils.printFoldWithIndex(indexCuboidonPaper[i]);
				}
				System.out.println("Num unique solutions found: " + 
						BasicUniqueCheckImproved.uniqList.size());
			}

			return 1L;
		} else {

			return 0L;
		}
	}

	@Override
	public long getNumUniqueFound() {
		// TODO Auto-generated method stub
		return BasicUniqueCheckImproved.uniqList.size();
	}
}
