package GraphUtils;

import java.util.ArrayList;

import Model.CuboidToFoldOn;
import Model.CuboidToFoldOnInterface;
import Model.Utils;

public class PivotCellDescriptionForNx1x1 {

	
	public static ArrayList<PivotCellDescription> getUniqueRotationListsWithCellInfo(CuboidToFoldOnInterface exampleCuboid) {

		ArrayList<PivotCellDescription> listPivots = PivotCellDescription.getUniqueRotationListsWithCellInfo(exampleCuboid, false);
		ArrayList<PivotCellDescription> ret = new ArrayList<PivotCellDescription>();
		
		//It could be made faster, but meh.
		for(int i=0; i<listPivots.size(); i++) {
			
			boolean noMatchYet = true;
			
			for(int j=i-1; j>=0; j--) {
				if(rotationArrayMatchesForNx1x1(listPivots.get(i), listPivots.get(j))) {
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

	private static boolean rotationArrayMatchesForNx1x1(PivotCellDescription desc1, PivotCellDescription desc2) {
		
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
		
		//Accept side-by-side relections as the same
		for(int i=0; i<desc1.lengthsAroundCell.length; i++) {
			if( i % 2 == 1 && desc1.lengthsAroundCell[i] != desc2.lengthsAroundCell[desc1.lengthsAroundCell.length - i]) {
				return false;
			} else if(i % 2 == 0 && desc1.lengthsAroundCell[i] != desc2.lengthsAroundCell[i]) {
				return false;
			}
		}

		
		return true;
		
	}

	
	public static ArrayList<PivotCellDescription> filterOutInconvinientRotationListsWithCellInfo(ArrayList<PivotCellDescription> current, CuboidToFoldOnInterface exampleCuboid) {
		
		ArrayList<PivotCellDescription> ret = new ArrayList<PivotCellDescription>();
		
		if( ! dimensionContains2and1or3and1(exampleCuboid.getDimensions())) {
			return current;
		}
		
		
		for(int i=0; i<current.size(); i++) {
			
			PivotCellDescription tmp = current.get(i);
			
			if( PivotCellGoesTopToBottom(tmp, exampleCuboid)
					&& curCellFarEnoughFromTopOrBottom(tmp, exampleCuboid)) {
				//Nope!
			} else {
				ret.add(current.get(i));
			}
			
		}
		
		System.out.println("Filtered rotation lists:");
		for(int i=0; i<ret.size(); i++) {
			
			PivotCellDescription tmp = ret.get(i);
			System.out.println("Cell and rotation: " + tmp.cellIndex + " and " + tmp.rotationRelativeToCuboidMap);
			for(int k=0; k<tmp.lengthsAroundCell.length; k++) {
				System.out.print(tmp.lengthsAroundCell[k] + ", ");
			}
			System.out.println();
		}
		
		System.out.println("Num starting points: " + ret.size());
		
		System.out.println("Num starting points: " + ret.size());
		
		return ret;
	}
	
	public static int NUM_ROTATIONS = 4;
	
	public static boolean PivotCellGoesTopToBottom(PivotCellDescription startLocation, CuboidToFoldOnInterface exampleCuboid) {
		
		int numToGoAround = 0;
		
		int indexCurrent = startLocation.cellIndex;
		int rotationCurrent = startLocation.rotationRelativeToCuboidMap;
		
		int indexPrev = -1;
		int rotationPrev = -1; 
		
		while(exampleCuboid.getNeighbours(indexCurrent)[rotationCurrent].getIndex() != startLocation.getCellIndex()) {
			numToGoAround++;
			
			indexPrev = indexCurrent;
			rotationPrev = rotationCurrent; 
			
			indexCurrent = exampleCuboid.getNeighbours(indexPrev)[rotationPrev].getIndex();
			rotationCurrent = (rotationPrev + exampleCuboid.getNeighbours(indexPrev)[rotationPrev].getRot()) % NUM_ROTATIONS;
			
			
			if(numToGoAround > 100) {
				System.exit(1);
			}
		}
		
		
		//TODO: avoid magic numbers:
		if(numToGoAround > 10) {
			return true;
		}
		
		return false;
	}
	
	private static boolean curCellFarEnoughFromTopOrBottom(PivotCellDescription startLocation, CuboidToFoldOnInterface exampleCuboid) {
		
		int otherCuboidLongDim = getLongDimension(exampleCuboid.getDimensions());
		int nofNx1x1 = (Utils.getTotalArea(exampleCuboid.getDimensions()) - 2) / 4;
		
		int numToGoTopOrBottom = 0;
		
		int indexCurrent = startLocation.cellIndex;
		int rotationCurrent = startLocation.rotationRelativeToCuboidMap;
		
		int indexPrev = -1;
		int rotationPrev = -1; 
		
		while(Utils.getSideCell(exampleCuboid, exampleCuboid.getNeighbours(indexCurrent)[rotationCurrent].getIndex()) != 0
				&& Utils.getSideCell(exampleCuboid, exampleCuboid.getNeighbours(indexCurrent)[rotationCurrent].getIndex()) != 5) {
			numToGoTopOrBottom++;
			
			indexPrev = indexCurrent;
			rotationPrev = rotationCurrent; 
			
			indexCurrent = exampleCuboid.getNeighbours(indexPrev)[rotationPrev].getIndex();
			rotationCurrent = (rotationPrev + exampleCuboid.getNeighbours(indexPrev)[rotationPrev].getRot()) % NUM_ROTATIONS;
			
			
			if(numToGoTopOrBottom > 100) {
				System.exit(1);
			}
		}
		
		if(numToGoTopOrBottom + otherCuboidLongDim >= nofNx1x1) {
			return true;
		}
		
		return false;
	}

	
	public static int getLongDimension(int dimensions[]) {
		int max = -1;
		
		for(int i=0; i<dimensions.length; i++) {
			max = Math.max(max, dimensions[i]);
		}
		
		return max;
	}
	
	public static boolean dimensionContains2and1or3and1(int dimensions[]) {
		
		boolean has1 = false;
		
		boolean has2or3 = false;
		
		for(int i=0; i<dimensions.length; i++) {
			if(dimensions[i] == 1) {
				has1 = true;
			} else if(dimensions[i] == 2 || dimensions[i] == 3) {
				has2or3 = true;
			}
		}
		
		
		return has1 && has2or3;
		
	}
}
