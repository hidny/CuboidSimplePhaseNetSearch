package NewModel;


import java.math.BigInteger;
import java.util.Iterator;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;

public class SimplePhaseNx1x1SolutionsCounter {

	public static int N = 4;
	
	
	public static void main(String args[]) {
		
		
		
		
		Nx1x1CuboidToFold curSimpleNet = new Nx1x1CuboidToFold(N);
		
		
		
		buildNet(curSimpleNet, 0);
		

		System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
		
	}
	
	public static void debugPrintSolutions() {
		Iterator<BigInteger> it = BasicUniqueCheckImproved.uniqList.iterator();
		
		BigInteger array[] = new BigInteger[BasicUniqueCheckImproved.uniqList.size()];
		
		for(int i=0; i<array.length; i++) {
			array[i] = it.next();
		}
		
		for(int i=0; i<array.length; i++) {
			
			for(int j=i+1; j<array.length; j++) {
				
				if(array[i].compareTo(array[j]) > 0) {
					BigInteger tmp = array[i];
					array[i] = array[j];
					array[j] = tmp;
					
				}
				
			}
		}
		
		System.out.println("---");
		for(int i=0; i<array.length; i++) {
			System.out.println(array[i]);
		}
	}
	
	
	public static int numSolutions = 0;
	public static int iterator = 0;
	
	public static void buildNet(Nx1x1CuboidToFold curSimpleNet, int numLevels) {
		
		if(numLevels > N) {
			
			//TODO: put all of this in a solution resolver (see Cuboid repo for example)
			
			System.out.println("Found solution:");
			System.out.println(curSimpleNet);
			
			numSolutions++;
			System.out.println("Num solutions so far: " + numSolutions);
			
			if(BasicUniqueCheckImproved.isUnique(getOppositeCornersOfNet(curSimpleNet.setupBoolArrayNet()), curSimpleNet.setupBoolArrayNet()) ){
				System.out.println("Unique solution found");
				System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
				System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
			}
			
			//TODO: This should actually work and be put in a cuboid resolver
			Nx1x1StackTransitionTracker.setAllowedTransitions(curSimpleNet);

			//END TODO: put all of this in a solution resolver (see Cuboid repo for example)
			
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
	
	public static Coord2D[] getOppositeCornersOfNet(boolean array[][]) {
		
		Coord2D corners[] = new Coord2D[2];
		
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
		
		corners[0] = new Coord2D(firsti, firstj);
		corners[1] = new Coord2D(lasti, lastj);
		
		
		
		return corners;
	}
}
