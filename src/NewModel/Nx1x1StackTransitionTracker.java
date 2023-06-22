package NewModel;

public class Nx1x1StackTransitionTracker {

	public static final int NUM_LEVEL_OPTIONS = 4;

	public static final int NUM_GROUNDED_COMBOS = (int)Math.pow(2, 4);
	public static final int NUM_SIDE_BUMP_OPTIONS = Nx1x1CuboidToFold.NUM_SIDE_BUMP_OPTIONS;
	
	public static final int NUM_TRANSITIONS = NUM_LEVEL_OPTIONS * NUM_LEVEL_OPTIONS * NUM_GROUNDED_COMBOS * NUM_SIDE_BUMP_OPTIONS;
	
	public static boolean allowedTransitions[] = new boolean[NUM_TRANSITIONS];
	
	//TODO: Translate the allowedTransitions array into lists, so the search for a new stackdoesn't need to loop as much.
	
	
	//Start with recording allowed transition for the middle layers (ignore top and bottom layer for now)
	//This transition table will hopefully make the search slightly faster by eliminating some bad stacks.
	
	public static void initAllowedTransitions() {
		return;
	}
	
	public static void setAllowedTransitions(Nx1x1CuboidToFold solution) {
		
		System.out.println("TODO");
		System.out.println(solution);
		
		for(int level=0; level < solution.heightOfCuboid - 1; level++) {
			System.out.println("BLAH");
			
			//TODO call setAllowedTransition(int levelOptionPrev, boolean prevGround[], int levelOptionCur, int sideBumpIndex)
			// We will need to figure out what the parameters are...
		}
	}
	
	public static void setAllowedTransition(int levelOptionPrev, boolean prevGround[], int levelOptionCur, int sideBumpIndex) {
		
	}

	public static boolean isAllowedTransition(int levelOptionPrev, boolean prevGround[], int levelOptionCur, int sideBumpIndex) {
		
		return false;
	}
	
	//TODO:
	public static int geTransitionIndexToLookup(int levelOptionPrev, boolean prevGround[], int levelOptionCur, int sideBumpIndex) {
		
		return -1;
	}
}
