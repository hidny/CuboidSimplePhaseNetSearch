package NewModel;

import java.util.ArrayList;

import Coord.Coord2D;
import Model.CuboidToFoldOn;

public class Nx1x1CuboidToFold {

	//Current goals:
	// 1) Only do legal stacks (if cells are isolated, then that's not ok)
	// 2) Make a search to find simple phase solutions to Nx1x1 cuboids
	// 3) Optimize the logic. (Make it memorize the output given a previous level, the number of isolated cells prev level, and whether or not it's the top level or the one before.)
	// 4) Add another cuboid along
	// 5) Find the # of simple phase solution to (5x1x1 and 3x2x1), and (7x1x1 and 3x3x1).
	// 6) Add filters based on the way the 2nd cuboid is setup.
	// 7) Find simple phase solutions for 15x1x1 + another cuboid
	// 8) Find simple phase solution for 17x1x1 + 2 other cuboids.
	// Done?
	// 9) Look for nets that fold 3 cuboids
	//
	// 10) take what you've learned and apply it to the original search.
	
	//Also: prove that the levelOptions should only include the 4 options listed in this class.
	
	public final static int levelOptions[][] = {
			{1, 1, 1, 1, 0, 0, 0},
			{1, 1, 0, 1, 0, 0, 1},
			{1, 0, 1, 1, 0, 1, 0},
			{1, 0, 0, 1, 0, 1, 1}
	};
	
	public static boolean levelOptionsBool[][] = null;
	
	public static void initStaticConstants() {
		fillLevelOptionsBool();
	}
	
	public static void fillLevelOptionsBool() {
		
		levelOptionsBool = new boolean[levelOptions.length][levelOptions[0].length];
		
		for(int i=0; i<levelOptions.length; i++) {
			for(int j=0; j<levelOptions[0].length; j++) {
				if(levelOptions[i][j] == 1) {
					levelOptionsBool[i][j] = true;
				} else {
					levelOptionsBool[i][j] = false;
				}
			}
		}
	}
	
	
	private int numLevelsUsed;
	private int sideBump[];
	private int optionUsedPerLevel[];
	
	//This is relevant because some cells on level n might not be connected to the bottom directly
	// and need the cells on level n+1 to eventually connect them to bottom.
	private boolean cellsJoinedToPrevOrNextLevel[][];
	private boolean cellsJoinedToOnlyNextLevel[][];
	
	private int numCellsJoinedPrevLevel[];
	
	private int height;
	
	public Nx1x1CuboidToFold(int n) {
		
		initStaticConstants();
		
		this.numLevelsUsed = 0;
		
		this.sideBump = new int[n + 1];
		this.optionUsedPerLevel = new int[n + 1];
		this.cellsJoinedToPrevOrNextLevel = new boolean[n + 1][levelOptions[0].length];
		this.cellsJoinedToOnlyNextLevel = new boolean[n + 1][levelOptions[0].length];
		
		numCellsJoinedPrevLevel = new int[n + 1];
		
		//TODO: just have a variable for n...
		
		
		this.height = n;
	}

	//TODO
	//Returns a list of ways you could add the next level to a Nx1x1 cuboid
	//The returned item is two numbers: 1) the levelOptionIndex and 2) the side bump compared to the prev level
	//I'll start with this function trying all 4 options multipled by all 7 side bumps possible, even though
	// some of those options are impossible. I'll refine it if I feel like it later.
	
	public final static int NUM_SIDE_BUMP_OPTIONS = 2 * levelOptions[0].length - 1;
	public final static int TOP_SHIFT_LEFT_1ST_IT = levelOptions[0].length - 1;

	
	//TODO: Maybe valid inputs to this function could be memeorized based on last 2 layers, or whatever...
	

	// Add a new level to the Nx1x1 cuboid given the levelOptionIndex and sideBump
	// I also included the other cuboid to make sure that the other cuboid is ok with the addition.
	// I might need to also include: 
	//int indexCuboidOnPaper2ndCuboid[][]
	//We'll see.
	public boolean addNextLevel(Coord2D newLevelDesc, CuboidToFoldOn otherCuboid) {
		
		boolean isMoveLegal = true;
		
		optionUsedPerLevel[this.numLevelsUsed] = newLevelDesc.i;
		sideBump[this.numLevelsUsed] = newLevelDesc.j;
		this.numCellsJoinedPrevLevel[this.numLevelsUsed] = 0;
		
		if(this.numLevelsUsed == 0) {
			//Adding the 1st layer:

			int cellIndexOnLevelTouchingBottomCell = TOP_SHIFT_LEFT_1ST_IT - newLevelDesc.j;
			
			if(cellIndexOnLevelTouchingBottomCell >= 0
					&& levelOptionsBool[newLevelDesc.i][cellIndexOnLevelTouchingBottomCell]) {
				
				cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][cellIndexOnLevelTouchingBottomCell] = true;
				this.numCellsJoinedPrevLevel[this.numLevelsUsed]++;
				touchCellsBesideCell(cellIndexOnLevelTouchingBottomCell, newLevelDesc.i);
			}
			

			if(this.numCellsJoinedPrevLevel[this.numLevelsUsed] == 0) {
				//System.out.println("WARNING: 1st level/layer isn't touching bottom cell.");
				isMoveLegal = false;
				//return false;
			}
		
		//TODO: add code for adding the last layer or the top.
		} else if(this.numLevelsUsed == this.height) {
			
			if(this.numCellsJoinedPrevLevel[this.numLevelsUsed - 1] != 4) {
				//System.out.println("WARNING: previous level isn't touching all 4 cells at the top (" + this.numCellsJoinedPrevLevel[this.numLevelsUsed - 1] + ")");

				isMoveLegal = false;
				//return false;
			}
			
			int shiftTopToRight = sideBump[this.numLevelsUsed] - TOP_SHIFT_LEFT_1ST_IT;
			
			if(shiftTopToRight >= 0
					&& cellsJoinedToPrevOrNextLevel[this.numLevelsUsed - 1][shiftTopToRight]) {
				
				cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][shiftTopToRight] = true;
				
				this.numCellsJoinedPrevLevel[this.numLevelsUsed]++;
				
				optionUsedPerLevel[this.numLevelsUsed] = -2;
				
			} else {
				isMoveLegal = false;
			}
			
			
		} else {
			
			int shiftTopToLeft = TOP_SHIFT_LEFT_1ST_IT - sideBump[this.numLevelsUsed];
			int limitTop = levelOptionsBool[newLevelDesc.i].length - shiftTopToLeft;
			
			int jLimit = Math.min(levelOptionsBool[0].length, limitTop);
			
			boolean progressBeingMade = false;
			
			do {
				
				progressBeingMade = false;

				//Find out what cells in new layer is "grounded" to bottom cell
				for(int prevLevelIndex=Math.max(0, 0 - shiftTopToLeft); prevLevelIndex<jLimit; prevLevelIndex++) {
					
					if(this.cellsJoinedToPrevOrNextLevel[this.numLevelsUsed - 1][prevLevelIndex]
							&& levelOptionsBool[optionUsedPerLevel[this.numLevelsUsed]][prevLevelIndex + shiftTopToLeft]
							&& ! cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][prevLevelIndex + shiftTopToLeft]) {
			
						cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][prevLevelIndex + shiftTopToLeft] = true;
						
						this.numCellsJoinedPrevLevel[this.numLevelsUsed]++;
	
						touchCellsBesideCell(prevLevelIndex + shiftTopToLeft, newLevelDesc.i);
						
					}
					
				}
				
				//Find out if some cells in the previous layer could now be "grounded" to the bottom cell.
				for(int prevLevelIndex=Math.max(0, 0 - shiftTopToLeft); prevLevelIndex<jLimit; prevLevelIndex++) {
					
					if(cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][prevLevelIndex + shiftTopToLeft]
							&& levelOptionsBool[optionUsedPerLevel[this.numLevelsUsed - 1]][prevLevelIndex]
							&& ! this.cellsJoinedToPrevOrNextLevel[this.numLevelsUsed - 1][prevLevelIndex]) {
			
						this.cellsJoinedToPrevOrNextLevel[this.numLevelsUsed - 1][prevLevelIndex] = true;
						
						this.numCellsJoinedPrevLevel[this.numLevelsUsed - 1]++;
						
						this.cellsJoinedToOnlyNextLevel[this.numLevelsUsed - 1][prevLevelIndex] = true;
						
						//Inefficient loop, but whatever
						progressBeingMade = true;
						touchCellsBesideCell(prevLevelIndex, optionUsedPerLevel[this.numLevelsUsed - 1], true);
						
					}
					
				}
				
			} while(progressBeingMade);
			
			
			
			if(this.numCellsJoinedPrevLevel[this.numLevelsUsed - 1] != 4) {
				//System.out.println("WARNING: previous level isn't touching all 4 cells. (" + this.numCellsJoinedPrevLevel[this.numLevelsUsed - 1] + ")");

				isMoveLegal = false;
				//return false;
			}
			if(this.numCellsJoinedPrevLevel[this.numLevelsUsed] == 0) {
				//System.out.println("WARNING: current level isn't touching all previous level.");
				isMoveLegal = false;
				//return false;
				
			}
		}
		
		
		this.numLevelsUsed++;
		
		if(isMoveLegal == false) {
			//System.out.println("Move not legal");
		}
		return isMoveLegal;
	}
	
	public void removeTopLevel() {
		//In the name of efficiency, I'll be a bit dirty and not clean up everything:

		this.numLevelsUsed--;
		
		for(int i=0; i<cellsJoinedToPrevOrNextLevel[0].length; i++) {
			cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][i] = false;
			
		
		}
		
		if(this.numLevelsUsed > 0) {
			for(int i=0; i<cellsJoinedToPrevOrNextLevel[0].length; i++) {
				if(this.cellsJoinedToOnlyNextLevel[this.numLevelsUsed - 1][i]) {
					this.cellsJoinedToOnlyNextLevel[this.numLevelsUsed - 1][i] = false;
					this.cellsJoinedToPrevOrNextLevel[this.numLevelsUsed - 1][i] = false;
					
					this.numCellsJoinedPrevLevel[this.numLevelsUsed - 1]--;
				}
			}
		}
		
		this.numCellsJoinedPrevLevel[this.numLevelsUsed] = 0;
		
		//Probably not needed:
		this.sideBump[this.numLevelsUsed] = -10;
		
	}
	
	
	private void touchCellsBesideCell(int cellIndexOnLevel, int levelDescIndex) {
		touchCellsBesideCell(cellIndexOnLevel, levelDescIndex, false);
	}
	
	private void touchCellsBesideCell(int cellIndexOnLevel, int levelDescIndex, boolean fromTheTop) {
		for(int k=cellIndexOnLevel + 1; k<cellsJoinedToPrevOrNextLevel[this.numLevelsUsed].length; k++) {
			
			if(levelDescIndex < 0) {
				System.out.println("ERROR: level description index doesn't make sense");
				System.exit(1);
			}
			if(levelOptionsBool[levelDescIndex][k]) {
				
				if( ! cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][k]) {
					cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][k] = true;
					this.numCellsJoinedPrevLevel[this.numLevelsUsed]++;
					
					if(fromTheTop) {
						this.cellsJoinedToOnlyNextLevel[this.numLevelsUsed][k] = true;
					}
				}
			} else {
				break;
			}
		}
		
		for(int k=cellIndexOnLevel - 1; k>=0; k--) {
			if(levelOptionsBool[levelDescIndex][k]) {
				if( ! cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][k]) {
					cellsJoinedToPrevOrNextLevel[this.numLevelsUsed][k] = true;
					this.numCellsJoinedPrevLevel[this.numLevelsUsed]++;
					
					if(fromTheTop) {
						this.cellsJoinedToOnlyNextLevel[this.numLevelsUsed][k] = true;
					}
					
				}
			} else {
				break;
			}
		}
	}
	
	
	//TODO: experiment with multiple cuboids:

	public String toString() {
		
		String ret = "";

		ret += "Net:\n";
		
		boolean net[][] = setupBoolArrayNet();
		
		for(int i=0; i<net.length; i++) {
			for(int j=0; j<net[0].length; j++) {
				if(net[i][j]) {
					ret += "#";
				} else {
					ret += ".";
				}
			}
			int revIndex = net.length - 2 - i;
			if(revIndex >= 0 && revIndex < optionUsedPerLevel.length) {
				ret += "  (sideBump: " + sideBump[revIndex] + ") (numTouches " + this.numCellsJoinedPrevLevel[revIndex] + ")";
			}
			ret += "\n";
		}
		ret += "\n";
		ret += "\n";
		
		return ret;
	}
	
	public int getAmountSpaceLeftOfBottom() {
		int ret = 0;
		
		int cur = 0;
		
		for(int i=0; i<this.numLevelsUsed; i++) {
			cur += TOP_SHIFT_LEFT_1ST_IT - sideBump[i];
			
			ret = Math.max(ret, cur);
		}
		
		return ret;
	}
	
	public int getAmountSpaceRightOfBottom() {
		int ret = 0;
		
		int cur = TOP_SHIFT_LEFT_1ST_IT;
		
		for(int i=0; i<this.numLevelsUsed; i++) {
			cur += sideBump[i] - TOP_SHIFT_LEFT_1ST_IT;
			
			ret = Math.max(ret, cur);
		}
		
		return ret;
	}
	
	public boolean[][] setupBoolArrayNet() {
		int leftOfBottom = getAmountSpaceLeftOfBottom();
		int rightOfBottom = getAmountSpaceRightOfBottom();
		int width = leftOfBottom + 1+ rightOfBottom;
		
		boolean ret[][] = new boolean[numLevelsUsed + 1][width];
		
		//bottom:
		int bottomXCoord = leftOfBottom;
		ret[ret.length - 1][bottomXCoord] = true;
		
		
		int curXCoordStart = bottomXCoord;
		for(int i=0; i<Math.min(this.numLevelsUsed, this.height); i++) {
			curXCoordStart += sideBump[i] - TOP_SHIFT_LEFT_1ST_IT;
			
			for(int j=0; j<levelOptions[0].length; j++) {
				ret[ret.length - 2 - i][curXCoordStart + j] = levelOptionsBool[optionUsedPerLevel[i]][j];
			}
			
			//TODO: You will have to do something different for the top level
		}
		
		if(this.numLevelsUsed > this.height) {
			//Insert last layer:
			curXCoordStart += sideBump[this.numLevelsUsed - 1] - TOP_SHIFT_LEFT_1ST_IT;
			
			ret[0][curXCoordStart] = true;
		}
		
		return ret;
	}
	
	
	
	public static void origTest(CuboidToFoldOn testCuboid) {
		Nx1x1CuboidToFold tmp = new Nx1x1CuboidToFold(25);
		
		System.out.println(tmp);
		
		//Test 1: Simple
		tmp.addNextLevel(new Coord2D(0, 0), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(0, 0), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(0, 0), testCuboid);
		System.out.println(tmp);
		
		tmp.removeTopLevel();
		System.out.println(tmp);
		tmp.removeTopLevel();
		System.out.println(tmp);
		tmp.removeTopLevel();
		System.out.println(tmp);
		

		//Test 2: Change the amount of bump:
		tmp.addNextLevel(new Coord2D(0, 3), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(0, 4), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(0, 5), testCuboid);
		System.out.println(tmp);
		

		tmp.removeTopLevel();
		System.out.println(tmp);
		tmp.removeTopLevel();
		System.out.println(tmp);
		tmp.removeTopLevel();
		System.out.println(tmp);
		
		//Test 3: test multiple different level options.
		tmp.addNextLevel(new Coord2D(1, 3), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(2, 4), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(3, 5), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(0, 12), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(0, 6), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(1, 6), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(2, 6), testCuboid);
		System.out.println(tmp);
		tmp.addNextLevel(new Coord2D(3, 6), testCuboid);
		System.out.println(tmp);

		tmp.addNextLevel(new Coord2D(0, 9), testCuboid);
		System.out.println(tmp);
	}
	
	public static void secondTest() {
		
		CuboidToFoldOn testCuboid = new CuboidToFoldOn(3, 1, 1);
		

		Nx1x1CuboidToFold tmp = new Nx1x1CuboidToFold(3);
		
		//Test 2: Change the amount of bump:
		tmp.addNextLevel(new Coord2D(0, 3), testCuboid);
		System.out.println(tmp);
		
		tmp.addNextLevel(new Coord2D(0, 4), testCuboid);
		System.out.println(tmp);
		
		tmp.addNextLevel(new Coord2D(0, 5), testCuboid);
		System.out.println(tmp);
		
		tmp.addNextLevel(new Coord2D(0, 10), testCuboid);
		System.out.println(tmp);
	}
	
	/*
	 *


Net:
##.#..#  (sideBump: 3) (numTouches 1)
...#...



---
Net:
...####...  (sideBump: 9) (numTouches 4)
##.#..#...  (sideBump: 3) (numTouches 4)
...#......



---
Found solution:
Net:
...#......  (sideBump: 6) (numTouches 1)
...####...  (sideBump: 9) (numTouches 4)
##.#..#...  (sideBump: 3) (numTouches 4)
...#......

	 */
	public static void main(String args[]) {
		

		Nx1x1CuboidToFold tmp = new Nx1x1CuboidToFold(2);
		
		//Test 2: Change the amount of bump:
		tmp.addNextLevel(new Coord2D(1, 3), null);
		System.out.println(tmp);
		
		tmp.addNextLevel(new Coord2D(0, 9), null);
		System.out.println(tmp);
		
		
	}
	
	
}
