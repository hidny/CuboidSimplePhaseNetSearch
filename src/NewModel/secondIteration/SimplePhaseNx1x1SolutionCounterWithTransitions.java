package NewModel.secondIteration;

import java.math.BigInteger;
import java.util.Iterator;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;

public class SimplePhaseNx1x1SolutionCounterWithTransitions {

	public static int N = 5;
	
	
	public static void main(String args[]) {
		
		//Utils.printFromSolutionCode(new BigInteger("111693216346316918536"));
		//System.exit(1);
		
		System.out.println("Creating transition table:");
		Nx1x1StackTransitionTracker.initAllowedTransitions();
		
		Nx1x1CuboidToFold curSimpleNet = new Nx1x1CuboidToFold(N);
		
		
		System.out.println();
		System.out.println("Doing the actual search:");
		
		buildNetStartingFromBottom(curSimpleNet);
		

		System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
		
		Iterator<BigInteger> iter = BasicUniqueCheckImproved.debugUniqList.iterator();
		
		System.out.println("Missing solutions:");
		while(iter.hasNext()) {
			BigInteger next = iter.next();
			
			if(! BasicUniqueCheckImproved.uniqList.contains(next)) {
				System.out.println(next);
				Utils.printFromSolutionCode(next);
			}
		}
		
		
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
			
			Utils.printFromSolutionCode(array[i]);
		}
	}
	
	
	public static int numSolutions = 0;
	public static int iterator = 0;
	
	public static void buildNetStartingFromBottom(Nx1x1CuboidToFold curSimpleNet) {

		iterator++;
		
		
		for(int i=0; i<Nx1x1CuboidToFold.levelOptions.length; i++) {
			
			for(int j=0; j<Nx1x1CuboidToFold.NUM_SIDE_BUMP_OPTIONS; j++) {
				
				
				//System.out.println( i + ", " + j);
				Coord2D coord = new Coord2D(i, j);
				
				boolean legal = curSimpleNet.addNextLevel(coord, null);
				
				if(legal) {
					buildNetNextLevel(curSimpleNet, 1);
				}
				
				curSimpleNet.removeTopLevel();
				
			}
		}
	}
	
	public static void buildNetNextLevel(Nx1x1CuboidToFold curSimpleNet, int curLevelIndexToFill) {

		iterator++;
		
		if(curLevelIndexToFill > curSimpleNet.heightOfCuboid - 1) {
			
			
			if(curLevelIndexToFill > curSimpleNet.heightOfCuboid) {
			
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
				
			} else {
				
				//Adding the tippy-top level
				for(int i=0; i<Nx1x1CuboidToFold.levelOptions.length; i++) {
					
					for(int j=0; j<Nx1x1CuboidToFold.NUM_SIDE_BUMP_OPTIONS; j++) {
						
						
						//System.out.println( i + ", " + j);
						Coord2D coord = new Coord2D(i, j);
						
						boolean legal = curSimpleNet.addNextLevel(coord, null);
						
						if(legal) {
							buildNetNextLevel(curSimpleNet, curLevelIndexToFill + 1);
						}
						
						curSimpleNet.removeTopLevel();
						
					}
				}
				
			}
			
			
			return;
		}
		
		//Adding layer in-between:
		
		//TODO: this is redundant info:

		int transitionList[][] = Nx1x1StackTransitionTracker.getTransitionListToLookup(curSimpleNet.optionUsedPerLevel[curLevelIndexToFill - 1], curSimpleNet.cellsGrounded[curLevelIndexToFill - 1]);
		
		
		for(int i=0; i<transitionList[0].length; i++) {
			
			int coordI = transitionList[0][i];
			int coordJ = transitionList[1][i];

			//System.out.println( i + ", " + j);
			Coord2D coord = new Coord2D(coordI, coordJ);
			
			boolean legal = curSimpleNet.addNextLevel(coord, null);
			
			if(legal) {
				buildNetNextLevel(curSimpleNet, curLevelIndexToFill + 1);
			}
			
			curSimpleNet.removeTopLevel();
			
			
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
