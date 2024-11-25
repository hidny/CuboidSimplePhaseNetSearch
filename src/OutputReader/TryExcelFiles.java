package OutputReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import Model.Utils;

public class TryExcelFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String file1 = "C:\\Users\\Michael\\projectEuler2\\CuboidLidSearch\\magic_lid_16.csv";
			
			Scanner in = new Scanner(new File(file1));
			
			Coord2D paperToDevelop[];
			boolean array[][];
			
			ArrayList<String> potSolution = new ArrayList<String>();
			
			while(in.hasNext()) {
				potSolution.add(in.next());
			}
			
			int maxWidth = 0;
			for(int i=0; i<potSolution.size(); i++) {
				if(potSolution.get(i).split(",").length > maxWidth) {
					maxWidth = potSolution.get(i).split(",").length;
				}
			}
			
			array = new boolean[potSolution.size()][maxWidth];
			int numCoord = 0;
			
			for(int i=0; i<array.length; i++) {
				
				String line[] = potSolution.get(i).split(",");
				
				for(int j=0; j<line.length; j++) {
					if(line[j].equals("1")) {
						array[i][j] = true;
						numCoord++;
					} else {
						array[i][j] = false;
					}
				}
				
				for(int j=line.length; j<array[0].length; j++) {
					array[i][j] = false;
				}
			}
			
			paperToDevelop = new Coord2D[numCoord];
			
			int index = 0;
			for(int i=0; i<array.length; i++) {
				for(int j=0; j<array[0].length; j++) {
					if(array[i][j]) {
						paperToDevelop[index] = new Coord2D(i, j);
						index++;
					}
				}
			}
			
			BasicUniqueCheckImproved basicDupCheck = new BasicUniqueCheckImproved();
			
			basicDupCheck.isUnique(paperToDevelop, array);
			
			System.out.println("soution code: " + basicDupCheck.debugLastScore);
			
			Utils.printFold(array);
			
			copyPasteCode(basicDupCheck.debugLastScore + "");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void copyPasteCode(String solutionCodeString) {

		boolean VERBOSE = true;
		boolean table[][] = CheckSolutionsCodesAgainstOtherCuboid2.convertSolutionCodeToTable(solutionCodeString);
		

		int areaToCheck = CheckSolutionsCodesAgainstOtherCuboid2.getNumCellsUsed(table);
		int dimensionsToCheck[][] = CheckSolutionsCodesAgainstOtherCuboid2.getSetOfDimensionsForSurfaceArea(areaToCheck);
		
		
		System.out.println("Printing dimensions:");
		for(int i=0; i<dimensionsToCheck.length; i++) {
			System.out.println(dimensionsToCheck[i][0] + "x" + dimensionsToCheck[i][1] + "x" + dimensionsToCheck[i][2]);
			
		}
		System.out.println("Done printing dimensions.");
		
		boolean netToReplicate[][] = CheckSolutionsCodesAgainstOtherCuboid2.padBordersOfBoolTable(table);
		
		boolean solutionFound[] = new boolean[dimensionsToCheck.length];
		int numCuboidsCoveredByNet = 0;
		for(int i=0; i<dimensionsToCheck.length; i++) {
			
			if(ValidNetSolutionChecker.hasSolution(dimensionsToCheck[i], netToReplicate, false)) {
				System.out.println(dimensionsToCheck[i][0] + "x" + dimensionsToCheck[i][1] + "x" + dimensionsToCheck[i][2]);
				numCuboidsCoveredByNet++;
				
				System.out.println("Solution for: " + CheckSolutionsCodesAgainstOtherCuboid2.getDimensionsString(dimensionsToCheck[i]));
				
				ValidNetSolutionChecker.hasSolution(dimensionsToCheck[i], netToReplicate, VERBOSE);
				
				System.out.println();
				
				solutionFound[i] = true;
			} else {
				solutionFound[i] = false;
			}
		}
		
		System.out.println("Num solutions: " + numCuboidsCoveredByNet);
		
	}

}
