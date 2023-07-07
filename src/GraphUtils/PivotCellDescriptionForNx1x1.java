package GraphUtils;

import java.util.ArrayList;

import Model.CuboidToFoldOn;

public class PivotCellDescriptionForNx1x1 extends PivotCellDescription {

	public PivotCellDescriptionForNx1x1(CuboidToFoldOn exampleCuboid, int cellNumber, int rotation) {
		super(exampleCuboid, cellNumber, rotation);
		// TODO Auto-generated constructor stub
	}
	
	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOn exampleCuboid) {

		ArrayList<PivotCellDescription> ret = new ArrayList<PivotCellDescription>();
		
		
		ArrayList<PivotCellDescriptionForNx1x1> listPivots = new ArrayList<PivotCellDescriptionForNx1x1>();
		
		for(int i=0; i<Model.Utils.getTotalArea(exampleCuboid.getDimensions()); i++) {
			
			for(int j=0; j<NUM_ROTATIONS; j++) {
				PivotCellDescriptionForNx1x1 tmp = new PivotCellDescriptionForNx1x1(exampleCuboid, i, j);
			
				listPivots.add(tmp);
			
			}
			
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
		
		System.out.println("Num starting points: " + ret.size());

		return ret;
	}

	public boolean rotationArrayMatches(PivotCellDescription other) {
		
		boolean unreflectedMatchesSoFar = true;
		for(int i=0; i<this.lengthsAroundCell.length; i++) {
			if(this.lengthsAroundCell[i] != other.lengthsAroundCell[i]) {
				unreflectedMatchesSoFar = false;
				break;
			}
		}
		
		if(unreflectedMatchesSoFar) {
			return true;
		}
		
		//Accept side-by-side relections as the same
		for(int i=0; i<this.lengthsAroundCell.length; i++) {
			if( i % 2 == 1 && this.lengthsAroundCell[i] != other.lengthsAroundCell[this.lengthsAroundCell.length - i]) {
				return false;
			} else if(i % 2 == 0 && this.lengthsAroundCell[i] != other.lengthsAroundCell[i]) {
				return false;
			}
		}

		
		return true;
		
	}

}
