package NewModel.thirdIteration;

import DupRemover.BasicUniqueCheckImproved;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import NewModel.firstIteration.SimplePhaseNx1x1SolutionsCounter;

public class Nx1x1StackTransitionTracker2 {

	public static final int NUM_LEVEL_OPTIONS = 4;

	public static final int NUM_GROUNDED_COMBOS = (int)Math.pow(2, 4);
	public static final int NUM_SIDE_BUMP_OPTIONS = Nx1x1CuboidToFold.NUM_SIDE_BUMP_OPTIONS;
	
	public static final int NUM_TRANSITIONS = NUM_LEVEL_OPTIONS * NUM_LEVEL_OPTIONS * NUM_GROUNDED_COMBOS * NUM_SIDE_BUMP_OPTIONS;
	
	public static int transitionTable[][][][] = new int[NUM_LEVEL_OPTIONS][NUM_GROUNDED_COMBOS][NUM_LEVEL_OPTIONS][NUM_SIDE_BUMP_OPTIONS];
	
	//TODO: Translate the allowedTransitions array into lists, so the search for a new stackdoesn't need to loop as much.
	
	
	//Start with recording allowed transition for the middle layers (ignore top and bottom layer for now)
	//This transition table will hopefully make the search slightly faster by eliminating some bad stacks.
	
	public static final int MIN_NUMBER_THAT_WORKS = 4;
	//public static final int DEBUG_NUMBER_THAT_WORKS = 5;
	
	public static final int NO_SOLUTION_FOUND_YET = -1;
	
	//This is not a good programming practice, but whatever.
	public static boolean isInitialized = false;
	
	public static void initAllowedTransitions() {
		
		isInitialized = true;
		
		//Once N >= 4, the magic number is 45. It reminds me of Trump. That's depressing.
		
		for(int i=0; i<Nx1x1StackTransitionTracker2.transitionTable.length; i++) {
			for(int j=0; j<Nx1x1StackTransitionTracker2.transitionTable[i].length; j++) {
				for(int k=0; k<Nx1x1StackTransitionTracker2.transitionTable[i][j].length; k++) {
					for(int m=0; m<Nx1x1StackTransitionTracker2.transitionTable[i][j][k].length; m++) {
						
						Nx1x1StackTransitionTracker2.transitionTable[i][j][k][m] = NO_SOLUTION_FOUND_YET;
						
						
					}
				}
			}
		}

		Nx1x1CuboidToFold curSimpleNet = new Nx1x1CuboidToFold(MIN_NUMBER_THAT_WORKS);
		
		SimplePhaseNx1x1SolutionsCounter.buildNet(curSimpleNet, 0);
		

		System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
		
		int count = 0;
		for(int i=0; i<Nx1x1StackTransitionTracker2.transitionTable.length; i++) {
			for(int j=0; j<Nx1x1StackTransitionTracker2.transitionTable[i].length; j++) {
				for(int k=0; k<Nx1x1StackTransitionTracker2.transitionTable[i][j].length; k++) {
					for(int m=0; m<Nx1x1StackTransitionTracker2.transitionTable[i][j][k].length; m++) {
						
						if(Nx1x1StackTransitionTracker2.transitionTable[i][j][k][m] >= 0) {
							
							System.out.println("(" + i + ", " + j + ", " + k + ", " + m+ "): " + Nx1x1StackTransitionTracker2.transitionTable[i][j][k][m]);
							count++;
						}
						
					}
				}
			}
		}
		
		//Once N >= 4, the magic number is 45. It reminds me of Trump. That's depressing.
		System.out.println("Number of allowed transitions: " + count);
		
		//debugPrintSolutions();
		
		
		Nx1x1StackTransitionTracker2.setupTrasitionListToLookup();
		
		int count2 = 0;
		
		for(int i=0; i<Nx1x1StackTransitionTracker2.transitionTable.length; i++) {
			for(int j=0; j<Nx1x1StackTransitionTracker2.transitionTable[i].length; j++) {
				count2 += Nx1x1StackTransitionTracker2.getTransitionListToLookup(i, j)[0].length;
			}
		}
		
		System.out.println("Counting using the 2nd structure: " + count2);

		BasicUniqueCheckImproved.debugUniqList = BasicUniqueCheckImproved.uniqList;

		BasicUniqueCheckImproved.resetUniqList();
		
	}
	
	public static void setAllowedTransitions(Nx1x1CuboidToFold solution) {
		
		if( ! isInitialized) {
			return;
		}
		
		for(int prevLevel=0; prevLevel < solution.heightOfCuboid - 1; prevLevel++) {
			setAllowedTransition(solution.optionUsedPerLevel[prevLevel], solution.cellsGrounded[prevLevel], solution.cellsGroundedByLevelAbove[prevLevel], solution.optionUsedPerLevel[prevLevel + 1], solution.sideBump[prevLevel + 1], solution.cellsGrounded[prevLevel + 1], solution.cellsGroundedByLevelAbove[prevLevel + 1]);
		}
	}
	
	//convert boolean array to number in a naive way for speed.
	public static int convertPrevGroundToIndex(int levelOptionPrev, boolean prevGround[], boolean cellsGroundedByLevelAbove[]) {
		int ret = 0;
		
		boolean levelOption[] = Nx1x1CuboidToFold.LEVEL_OPTIONS_BOOL[levelOptionPrev];
		
		for(int i=0; i<levelOption.length; i++) {
			if(levelOption[i]) {
				
				if(prevGround[i] && ! cellsGroundedByLevelAbove[i]) {
					ret = 2*ret + 1;
				} else {
					ret = 2*ret;
				}
				
			}
		}

		return ret;
	}
	
	private static void setAllowedTransition(int levelOptionPrev, boolean prevGround[], boolean prevCellsGroundedByLevelAbove[], int levelOptionCur, int sideBumpIndex, boolean curGound[], boolean curCellsGroundedByLevelAbove[]) {
		if(transitionTable[levelOptionPrev][convertPrevGroundToIndex(levelOptionPrev, prevGround, prevCellsGroundedByLevelAbove)][levelOptionCur][sideBumpIndex] >= 0
				&& transitionTable[levelOptionPrev][convertPrevGroundToIndex(levelOptionPrev, prevGround, prevCellsGroundedByLevelAbove)][levelOptionCur][sideBumpIndex] != convertPrevGroundToIndex(levelOptionCur, curGound, curCellsGroundedByLevelAbove)) {
			System.out.println("Doh! There's 2 ways the next level could be grounded. This should not happen!");
			System.exit(1);
			
		}
		transitionTable[levelOptionPrev][convertPrevGroundToIndex(levelOptionPrev, prevGround, prevCellsGroundedByLevelAbove)][levelOptionCur][sideBumpIndex] = convertPrevGroundToIndex(levelOptionCur, curGound, curCellsGroundedByLevelAbove);
	}

	public static int isAllowedTransitionAndGetNextGroundIndex(int levelOptionPrev, int prevGroundIndex, int levelOptionCur, int sideBumpIndex) {
		
		return transitionTable[levelOptionPrev][prevGroundIndex][levelOptionCur][sideBumpIndex];
	}
	
	
	
	//1st index is the prev level state
	// 2nd index is length 3 (index 0 is for next level option, index 1 is for sideBumpIndex, and index 2 for the next GroundedIndex number)
	// 3rd index is the number of options there are.
	private static int listTransitionToGoBy[][][];
	
	public static void setupTrasitionListToLookup() {
		int list[][][] = new int[NUM_LEVEL_OPTIONS * NUM_GROUNDED_COMBOS][][];

		for(int i=0; i<list.length; i++) {
			
			int i1 = i / NUM_GROUNDED_COMBOS;
			int i2 = i % NUM_GROUNDED_COMBOS;
			
			int count = 0;
			
			for(int j=0; j<transitionTable[0][0].length; j++) {
				for(int k=0; k<transitionTable[0][0][0].length; k++) {
					
					if(transitionTable[i1][i2][j][k] >= 0) {
						count++;
					}
				}
			}
			
			list[i] = new int[3][count];
			
			int curIndex = 0;
			
			for(int j=0; j<transitionTable[0][0].length; j++) {
				for(int k=0; k<transitionTable[0][0][0].length; k++) {
					
					if(transitionTable[i1][i2][j][k] >= 0) {
						list[i][0][curIndex] = j;
						list[i][1][curIndex] = k;
						list[i][2][curIndex] = transitionTable[i1][i2][j][k];
						
						curIndex++;
					}
				}
			}
		}
		
		listTransitionToGoBy = list;
		
		
	}
	
	public static int[][] getTransitionListToLookup(int levelOptionPrev, int prevGroundIndex) {
		
		return listTransitionToGoBy[levelOptionPrev * NUM_GROUNDED_COMBOS + prevGroundIndex];
		
	}
	
	
	public static void main(String args[]) {

		initAllowedTransitions();
		
		System.out.println("Done");
	}
}
