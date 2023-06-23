package NewModel.secondIteration;

import DupRemover.BasicUniqueCheckImproved;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import NewModel.firstIteration.SimplePhaseNx1x1SolutionsCounter;

public class Nx1x1StackTransitionTracker {

	public static final int NUM_LEVEL_OPTIONS = 4;

	public static final int NUM_GROUNDED_COMBOS = (int)Math.pow(2, 4);
	public static final int NUM_SIDE_BUMP_OPTIONS = Nx1x1CuboidToFold.NUM_SIDE_BUMP_OPTIONS;
	
	public static final int NUM_TRANSITIONS = NUM_LEVEL_OPTIONS * NUM_LEVEL_OPTIONS * NUM_GROUNDED_COMBOS * NUM_SIDE_BUMP_OPTIONS;
	
	public static boolean allowedTransitions[][][][] = new boolean[NUM_LEVEL_OPTIONS][NUM_GROUNDED_COMBOS][NUM_LEVEL_OPTIONS][NUM_SIDE_BUMP_OPTIONS];
	
	//TODO: Translate the allowedTransitions array into lists, so the search for a new stackdoesn't need to loop as much.
	
	
	//Start with recording allowed transition for the middle layers (ignore top and bottom layer for now)
	//This transition table will hopefully make the search slightly faster by eliminating some bad stacks.
	public static final int MIN_NUMBER_THAT_WORKS = 4;
	
	public static void initAllowedTransitions() {
		
		//Once N >= 4, the magic number is 45. It reminds me of Trump. That's depressing.

		Nx1x1CuboidToFold curSimpleNet = new Nx1x1CuboidToFold(MIN_NUMBER_THAT_WORKS);
		
		SimplePhaseNx1x1SolutionsCounter.buildNet(curSimpleNet, 0);
		

		System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
		
		int count = 0;
		for(int i=0; i<Nx1x1StackTransitionTracker.allowedTransitions.length; i++) {
			for(int j=0; j<Nx1x1StackTransitionTracker.allowedTransitions[i].length; j++) {
				for(int k=0; k<Nx1x1StackTransitionTracker.allowedTransitions[i][j].length; k++) {
					for(int m=0; m<Nx1x1StackTransitionTracker.allowedTransitions[i][j][k].length; m++) {
						
						if(Nx1x1StackTransitionTracker.allowedTransitions[i][j][k][m]) {
							count++;
						}
						
					}
				}
			}
		}
		
		//Once N >= 4, the magic number is 45. It reminds me of Trump. That's depressing.
		System.out.println("Number of allowed transitions: " + count);
		
		//debugPrintSolutions();
		
		Nx1x1StackTransitionTracker.setupTrasitionListToLookup();
		
		int count2 = 0;
		
		for(int i=0; i<Nx1x1StackTransitionTracker.allowedTransitions.length; i++) {
			for(int j=0; j<Nx1x1StackTransitionTracker.allowedTransitions[i].length; j++) {
				count2 += Nx1x1StackTransitionTracker.getTransitionListToLookup(i, j)[0].length;
			}
		}
		
		System.out.println("Counting using the 2nd structure: " + count2);
		

		BasicUniqueCheckImproved.resetUniqList();
		
	}
	
	public static void setAllowedTransitions(Nx1x1CuboidToFold solution) {
		
		
		for(int prevLevel=0; prevLevel < solution.heightOfCuboid - 1; prevLevel++) {
			setAllowedTransition(solution.optionUsedPerLevel[prevLevel], solution.cellsGrounded[prevLevel], solution.optionUsedPerLevel[prevLevel + 1], solution.sideBump[prevLevel + 1]);
		}
	}
	
	//convert boolean array to number in a naive way for speed.
	public static int convertPrevGroundToIndex(boolean prevGround[]) {
		int ret = 0;
		
		if(prevGround[0]) {
			ret += 8;
		}

		if(prevGround[1]) {
			ret += 4;
		}
		
		if(prevGround[2]) {
			ret += 2;
		}
		
		if(prevGround[3]) {
			ret += 1;
		}

		return ret;
	}
	
	private static void setAllowedTransition(int levelOptionPrev, boolean prevGround[], int levelOptionCur, int sideBumpIndex) {
		allowedTransitions[levelOptionPrev][convertPrevGroundToIndex(prevGround)][levelOptionCur][sideBumpIndex] = true;
	}

	public static boolean isAllowedTransition(int levelOptionPrev, boolean prevGround[], int levelOptionCur, int sideBumpIndex) {
		
		return allowedTransitions[levelOptionPrev][convertPrevGroundToIndex(prevGround)][levelOptionCur][sideBumpIndex];
	}
	
	
	
	//1st index is the prev level state
	// 2nd index is length 2 (0 is for next level option and 1 is for sideBumpIndex)
	// 3rd index is the number of options there are.
	private static int listTransitionToGoBy[][][];
	
	public static void setupTrasitionListToLookup() {
		int list[][][] = new int[NUM_LEVEL_OPTIONS * NUM_GROUNDED_COMBOS][][];

		for(int i=0; i<list.length; i++) {
			
			int i1 = i / NUM_GROUNDED_COMBOS;
			int i2 = i % NUM_GROUNDED_COMBOS;
			
			int count = 0;
			
			for(int j=0; j<allowedTransitions[0][0].length; j++) {
				for(int k=0; k<allowedTransitions[0][0][0].length; k++) {
					
					if(allowedTransitions[i1][i2][j][k]) {
						count++;
					}
				}
			}
			
			list[i] = new int[2][count];
			
			int curIndex = 0;
			
			for(int j=0; j<allowedTransitions[0][0].length; j++) {
				for(int k=0; k<allowedTransitions[0][0][0].length; k++) {
					
					if(allowedTransitions[i1][i2][j][k]) {
						list[i][0][curIndex] = j;
						list[i][1][curIndex] = k;
						
						curIndex++;
					}
				}
			}
		}
		
		listTransitionToGoBy = list;
		
		
	}
	
	public static int[][] getTransitionListToLookup(int levelOptionPrev, boolean prevGround[]) {
		
		return listTransitionToGoBy[levelOptionPrev * NUM_GROUNDED_COMBOS + convertPrevGroundToIndex(prevGround)];
		
		
	}
	
	public static int[][] getTransitionListToLookup(int levelOptionPrev,int prevGroundIndex) {
		
		return listTransitionToGoBy[levelOptionPrev * NUM_GROUNDED_COMBOS + prevGroundIndex];
		
		
	}
	
	public static void main(String args[]) {

		initAllowedTransitions();
		
		System.out.println("Done");
	}
}
