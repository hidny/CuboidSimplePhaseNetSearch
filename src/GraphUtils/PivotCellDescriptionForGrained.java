package GraphUtils;

import java.util.ArrayList;

import Model.CuboidToFoldOn;
import Model.CuboidToFoldOnInterface;

public class PivotCellDescriptionForGrained {

	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOnInterface exampleCuboid) {
		return getUniqueRotationListsWithCellInfo(exampleCuboid, true);
	}
	
	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOnInterface exampleCuboid, boolean verbose) {

		ArrayList<PivotCellDescription> listPivots = getUniqueRotationListsWithCellInfoInner(exampleCuboid, false);
		ArrayList<PivotCellDescription> ret = new ArrayList<PivotCellDescription>();
		
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

	public static final int NUM_ROTATIONS = 4;
	
	private static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfoInner(CuboidToFoldOnInterface exampleCuboid) {
		return getUniqueRotationListsWithCellInfoInner(exampleCuboid, true);
	}

	private static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfoInner(CuboidToFoldOnInterface exampleCuboid, boolean verbose) {
		
		ArrayList<PivotCellDescription> ret = new ArrayList<PivotCellDescription>();
		
		
		ArrayList<PivotCellDescription> listPivots = new ArrayList<PivotCellDescription>();
		
		if(verbose) {
			System.out.println("Get arrays created:");
		}
		//The difference is that this only goes up to: exampleCuboid.getDimensions()[1]
		for(int i=0; i<exampleCuboid.getDimensions()[1]; i++) {
			
			if(verbose) {
				System.out.println("Cell index " + i + ":");
			}
			
			for(int j=0; j<NUM_ROTATIONS; j++) {
				//System.out.println("Rotation: " + j + ":");
				PivotCellDescription tmp = new PivotCellDescription(exampleCuboid, i, j);
			
				listPivots.add(tmp);
			
				if(verbose) {
					for(int k=0; k<tmp.lengthsAroundCell.length; k++) {
						System.out.print(tmp.lengthsAroundCell[k] + ", ");
					}
					System.out.println();
				}
			}
			if(verbose) {
				System.out.println();
				System.out.println();
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

}
