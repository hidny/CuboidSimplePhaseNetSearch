package NewModel;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;

public class SimplePhaseNx1x1SolutionsCounter {

	public static int N = 4;
	
	
	public static void main(String args[]) {
		
		Nx1x1CuboidToFold curSimpleNet = new Nx1x1CuboidToFold(N);
		
		
		
		buildNet(curSimpleNet, 0);
		

		System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
	}
	
	
	public static int numSolutions = 0;
	public static int iterator = 0;
	
	public static void buildNet(Nx1x1CuboidToFold curSimpleNet, int numLevels) {
		
		if(numLevels > N) {
			System.out.println("Found solution:");
			System.out.println(curSimpleNet);
			
			numSolutions++;
			System.out.println("Num solutions so far: " + numSolutions);
			
			if(BasicUniqueCheckImproved.isUnique(getCornersOfNet(curSimpleNet.setupBoolArrayNet()), curSimpleNet.setupBoolArrayNet()) ){
				System.out.println("Unique solution found");
				System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
				System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
			}
			return;
		}
		
		iterator++;
		if(iterator == 3) {
			System.out.println("Debug");
		}
		//System.out.println(iterator);
		//System.out.println(curSimpleNet);
		//System.out.println("---");
		
		for(int i=0; i<Nx1x1CuboidToFold.levelOptions.length; i++) {
			

			
			for(int j=0; j<Nx1x1CuboidToFold.NUM_SIDE_BUMP_OPTIONS; j++) {
				
				
				//System.out.println( i + ", " + j);
				Coord2D coord = new Coord2D(i, j);
				
				boolean legal = curSimpleNet.addNextLevel(coord, null);
				
				if(legal) {
					buildNet(curSimpleNet, numLevels + 1);
				}
				
				curSimpleNet.removeTopLevel();
				
				
			}
			
			if(numLevels == N) {
				//Top level only has 1 way to place top cell...
				//Maybe I'll do copy/paste code to make this faster in future...
				break;
			}
		}
		
		
		
	}
	
	public static Coord2D[] getCornersOfNet(boolean array[][]) {
		
		Coord2D borders[] = new Coord2D[4];
		
		int firsti = array.length;
		int lasti = 0;
		int firstj = array[0].length;
		int lastj = 0;
		
		
		for(int i = 0; i<array.length; i++) {
			for(int j=0; j<array[0].length; j++) {
				
				if(array[i][j]) {
					if(i > lasti) {
						lasti = i;
					}
					if(i < firsti) {
						firsti = i;
					}
					
					if(j > lastj) {
						lastj = j;
					}
					if(j < firstj) {
						firstj = j;
					}
				}
			}
		}
		
		borders[0] = new Coord2D(firsti, firstj);
		borders[1] = new Coord2D(firsti, lastj);
		borders[2] = new Coord2D(lasti, lastj);
		borders[3] = new Coord2D(lasti, firstj);
		
		
		
		return borders;
	}
}
