package GraphUtils;

import java.util.ArrayList;

import Model.CuboidToFoldOn;
import Model.CuboidToFoldOnInterface;

public class PivotCellDescription {

	//Plan is to use these pivot description to minimize the amount of rotations/reflection we have to do
	// in order to check if a cuboid matches a net.
	//TODO: once you know it's valid, figure out how to get list of start/rot to check for getting the uniq answer.
	
	
	//I'm hoping to also use this to reduce the baggage of the getCuboidIntersection algo.
	// That algo will hope shape 0, and a min amount of starting pos for shape B at every step of the way.
	
	
	/*
	private int cellsAbove;
	private int cellsRight;
	private int cellsBelow;
	private int cellsLeft;
	*/
	protected int lengthsAroundCell[] = new int[4];
	

	//Maybe not needed, but I'll keep it just in case:
	protected int cellIndex;
	protected int rotationRelativeToCuboidMap;
	
	public static final int NUM_ROTATIONS = 4;
	
	public PivotCellDescription(CuboidToFoldOnInterface exampleCuboid, int cellNumber, int rotation) {
		
		
		this.cellIndex = cellNumber;
		this.rotationRelativeToCuboidMap = rotation;
		
		for(int i=0; i<NUM_ROTATIONS; i++) {
			
			int curCellIndex = cellNumber;

			int ret=0;
			
			int rotToUse = (rotation + i) % NUM_ROTATIONS;

			while(Model.Utils.getSideCell(exampleCuboid, exampleCuboid.getNeighbours(curCellIndex)[rotToUse].getIndex())
					== Model.Utils.getSideCell(exampleCuboid, this.cellIndex)) {
				
				curCellIndex = exampleCuboid.getNeighbours(curCellIndex)[rotToUse].getIndex();
				
				ret++;
			}
			
			lengthsAroundCell[i] = ret;
		}
	}
	

	
	//TODO: get start positions and rotations relative pivot pos
	// int startPosRot[] = new Coord2D[8]
	
	// the cell number and rotation depend on the rotation and pos of the cells in the solution. (I think)
	//Guess way to fill it with 7x1x1: 
	// (cellNumber = 0, rotation = 0)
	// (cellNumber = 0, rotation = 1)
	// (cellNumber = 0, rotation = 2)
	// (cellNumber = 0, rotation = 3)
	
	// (cellNumber = 29, rotation = 0)
	// (cellNumber = 29, rotation = 1)
	// (cellNumber = 29, rotation = 2)
	// (cellNumber = 29, rotation = 3)
	
	/*
	 * Unique rotation lists for 7x1x1:
Cell and rotation: 0 and 0
0, 0, 0, 0, 
Cell and rotation: 1 and 0
0, 0, 6, 0, 
Cell and rotation: 1 and 1
0, 6, 0, 0, 
Cell and rotation: 1 and 2
6, 0, 0, 0, 
Cell and rotation: 1 and 3
0, 0, 0, 6, 
Cell and rotation: 2 and 0
1, 0, 5, 0, 
Cell and rotation: 2 and 1
0, 5, 0, 1, 
Cell and rotation: 2 and 2
5, 0, 1, 0, 
Cell and rotation: 2 and 3
0, 1, 0, 5, 
Cell and rotation: 3 and 0
2, 0, 4, 0, 
Cell and rotation: 3 and 1
0, 4, 0, 2, 
Cell and rotation: 3 and 2
4, 0, 2, 0, 
Cell and rotation: 3 and 3
0, 2, 0, 4, 
Cell and rotation: 4 and 0
3, 0, 3, 0, 
Cell and rotation: 4 and 1
0, 3, 0, 3, 
*/
	
	public boolean rotationArrayMatches(PivotCellDescription other) {
		
		for(int i=0; i<this.lengthsAroundCell.length; i++) {
			if(this.lengthsAroundCell[i] != other.lengthsAroundCell[i]) {
				return false;
			}
		}
		
		return true;
	}

	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOnInterface exampleCuboid) {
		return getUniqueRotationListsWithCellInfo(exampleCuboid, true);
	}

	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOnInterface exampleCuboid, boolean verbose) {
		
		ArrayList<PivotCellDescription> ret = new ArrayList<PivotCellDescription>();
		
		
		ArrayList<PivotCellDescription> listPivots = new ArrayList<PivotCellDescription>();
		
		System.out.println("Get arrays created:");
		for(int i=0; i<Model.Utils.getTotalArea(exampleCuboid.getDimensions()); i++) {
			
			System.out.println("Cell index " + i + ":");
			for(int j=0; j<NUM_ROTATIONS; j++) {
				//System.out.println("Rotation: " + j + ":");
				PivotCellDescription tmp = new PivotCellDescription(exampleCuboid, i, j);
			
				listPivots.add(tmp);
			
				for(int k=0; k<tmp.lengthsAroundCell.length; k++) {
					System.out.print(tmp.lengthsAroundCell[k] + ", ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
			
		}
		
		
		//It could be made faster, but meh.
		for(int i=0; i<listPivots.size(); i++) {
			
			boolean noMatchYet = true;
			
			for(int j=i-1; j>=0; j--) {
				if(listPivots.get(i).rotationArrayMatches(listPivots.get(j))) {
					noMatchYet = false;
					break;
				}
			}
			
			if(noMatchYet) {
				ret.add(listPivots.get(i));
			}
		}

		if(verbose) {
			System.out.println("Num unique pivot locations: " + ret.size());
			System.out.println("Total area multiplied by 4 rotations: " + (4 * Model.Utils.getTotalArea(exampleCuboid.getDimensions())));
			System.out.println();
			
	
			System.out.println("Unique rotation lists:");
			for(int i=0; i<ret.size(); i++) {
				
				PivotCellDescription tmp = ret.get(i);
				System.out.println("Cell and rotation: " + tmp.cellIndex + " and " + tmp.rotationRelativeToCuboidMap);
				for(int k=0; k<tmp.lengthsAroundCell.length; k++) {
					System.out.print(tmp.lengthsAroundCell[k] + ", ");
				}
				System.out.println();
			}
		}

		return ret;
	}
	
	public static void main(String args[]) {
		
		//TODO: function to get up to 8 symmetric cell locations.
		
		//CuboidToFoldOn exampleCuboid = new CuboidToFoldOn(1, 3, 3);
		
		//CuboidToFoldOn exampleCuboid = new CuboidToFoldOn(3, 2, 1);

		CuboidToFoldOn exampleCuboid = new CuboidToFoldOn(7, 1, 1);
		
		System.out.println(Model.DataModelViews.getFlatNumberingView(exampleCuboid.getDimensions()));
		
		System.out.println("Get side cells:");
		for(int i=0; i<Model.Utils.getTotalArea(exampleCuboid.getDimensions()); i++) {
			System.out.println(Model.Utils.getSideCell(exampleCuboid, i));
		}
		
		ArrayList<PivotCellDescription> uniqList = getUniqueRotationListsWithCellInfo(exampleCuboid);
		

		
	}



	public int[] getLengthsAroundCell() {
		return lengthsAroundCell;
	}



	public int getCellIndex() {
		return cellIndex;
	}



	public int getRotationRelativeToCuboidMap() {
		return rotationRelativeToCuboidMap;
	}
}
