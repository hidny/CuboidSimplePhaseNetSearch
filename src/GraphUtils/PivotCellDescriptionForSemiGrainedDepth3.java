package GraphUtils;

import java.util.ArrayList;

import Model.CuboidToFoldOn;
import Model.CuboidToFoldOnInterface;

public class PivotCellDescriptionForSemiGrainedDepth3 {

	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOnInterface exampleCuboid, int indexToRing[]) {
		return getUniqueRotationListsWithCellInfo(exampleCuboid, indexToRing, true);
	}
	
	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOnInterface exampleCuboid, int indexToRing[], boolean verbose) {

		ArrayList<PivotCellDescription> listPivots = new ArrayList<PivotCellDescription>();
		ArrayList<PivotCellDescription> ret = new ArrayList<PivotCellDescription>();
		
		for(int i=0; i<indexToRing.length; i++) {
			if(indexToRing[i] == 0) {
				listPivots.add(new PivotCellDescription(exampleCuboid, i, 2));
			}
		}
		
		//It could be made faster, but meh.
		for(int i=0; i<listPivots.size(); i++) {
			
			boolean noMatchYet = true;
			
			for(int j=i-1; j>=0; j--) {
				if(rotationArrayMatchesForSimplePhase(listPivots.get(i), listPivots.get(j))) {
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
		}

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

	private static boolean rotationArrayMatchesForSimplePhase(PivotCellDescription desc1, PivotCellDescription desc2) {
		
		boolean unreflectedMatchesSoFar = true;
		for(int i=0; i<desc1.lengthsAroundCell.length; i++) {
			if(desc1.lengthsAroundCell[i] != desc2.lengthsAroundCell[i]) {
				unreflectedMatchesSoFar = false;
				break;
			}
		}
		
		if(unreflectedMatchesSoFar) {
			return true;
		}
		
		//Accept side-by-side reflections as the same
		for(int i=0; i<desc1.lengthsAroundCell.length; i++) {
			if( i % 2 == 1 && desc1.lengthsAroundCell[i] != desc2.lengthsAroundCell[desc1.lengthsAroundCell.length - i]) {
				return false;
			} else if(i % 2 == 0 && desc1.lengthsAroundCell[i] != desc2.lengthsAroundCell[i]) {
				return false;
			}
		}

		
		return true;
		
	}

}
