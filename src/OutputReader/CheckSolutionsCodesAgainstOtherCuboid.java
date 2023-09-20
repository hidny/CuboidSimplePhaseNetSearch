package OutputReader;

import java.io.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Scanner;

import DupRemover.BasicUniqueCheckImproved;
import Model.Utils;
import Coord.Coord2D;

public class CheckSolutionsCodesAgainstOtherCuboid {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String file1 = "D:\\CheckA106with17x2x1.txt";
			
			int dimensionsCuboidToCheck[] = new int[]{8, 5, 1};
			
			//Alt: {17, 2, 1}
			int otherDimensions1[] = new int[] {17, 2, 1};
			int otherDimensions2[] = new int[] {26, 1, 1};
			
			int areaOfCuboidToCheck = Utils.getTotalArea(dimensionsCuboidToCheck);
			
			Scanner in = new Scanner(new File(file1));
			
			HashSet<String> file1Solutions = new HashSet<String>();
			
			int numSoutionsFound = 0;
			int numSolutionsInFile = 0;
			
			while(in.hasNextLine()) {
				String tmp = in.nextLine();
				
				//System.out.println(tmp);
				if(tmp.toLowerCase().contains("solution code:")) {
					
					numSolutionsInFile++;
					
					String solutionCodeString = getSolutionCode(tmp);
					file1Solutions.add(solutionCodeString);
					//System.out.println(solutionCodeString);
					
					boolean table[][] = convertSolutionCodeToTable(solutionCodeString);
					
					if(areaOfCuboidToCheck != getNumCellsUsed(table) ) {
						System.out.println("The area of the solution in the file doesn't match the area of the dimensions to check!");
						System.out.println("area in file: " + getNumCellsUsed(table));
						System.out.println("Area to check: " + areaOfCuboidToCheck);
						System.out.println("Dimension set to check: ("+ dimensionsCuboidToCheck[0] +"," + dimensionsCuboidToCheck[1] + "," + dimensionsCuboidToCheck[2] +")");
						System.exit(1);
					}
					
					BasicUniqueCheckImproved.isUnique(makeCoordList(table), table);
					
					if(BasicUniqueCheckImproved.debugLastScore.compareTo(new BigInteger(solutionCodeString)) != 0) {
						System.out.println("DOH! The solution code algo failed!");

						System.out.println(BasicUniqueCheckImproved.debugLastScore + "\nvs\n" + new BigInteger(solutionCodeString));
						System.out.println();
						System.out.println();
						System.exit(1);
					}
					
					boolean netToReplicate[][] = padBordersOfBoolTable(table);
					

					//TODO: Check array against other dimensions! I'll do it later
					
					if(ValidNetSolutionChecker.hasSolution(dimensionsCuboidToCheck, netToReplicate, false)) {
						
						numSoutionsFound++;
						
						System.out.println("Found 3-way solution!");

						if(isRotationallySymmetric(table)) {
							System.out.println("This net is rotationally symmetric!");
						}

						System.out.println("Printing just the fold without indexes:");
						Utils.printFold(table);
						
						boolean VERBOSE = true;
						System.out.println("Solution for: " + getDimensionsString(dimensionsCuboidToCheck));
						
						ValidNetSolutionChecker.hasSolution(dimensionsCuboidToCheck, netToReplicate, VERBOSE);
						
						System.out.println();
						
						if(Utils.getTotalArea(otherDimensions1) == getNumCellsUsed(table) ) {
							
							System.out.println("Solution for: " + getDimensionsString(otherDimensions1));
							
							if( ! ValidNetSolutionChecker.hasSolution(otherDimensions1, netToReplicate, VERBOSE)) {
								System.out.println("oops! otherDimensions1 doesn't work");
							}
							
						} else {
							System.out.println("WARNING: dimensions set for otherDimensions1 don't match the area of the dimensionsCuboidToCheck variable.");
						}
						System.out.println();
						
						if(Utils.getTotalArea(otherDimensions2) == getNumCellsUsed(table) ) {
							System.out.println("Solution for: " + getDimensionsString(otherDimensions2));
							
							if( ! ValidNetSolutionChecker.hasSolution(otherDimensions2, netToReplicate, VERBOSE)) {
								System.out.println("oops! otherDimensions1 doesn't work");
							}
							
						} else {
							System.out.println("WARNING: dimensions set for otherDimensions1 don't match the area of the dimensionsCuboidToCheck variable.");
						}
						System.out.println();
						
						
						
						System.out.println();
						
						
					}
					
				}
			}
			
			in.close();
			
			System.out.println("Number of solutions in file: " + numSolutionsInFile);
			System.out.println();
			System.out.println();
			System.out.println();
			
			System.out.println("Final number of solutions found: " + numSoutionsFound);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getDimensionsString(int dimensions[]) {
		return "("+ dimensions[0] +"," + dimensions[1] + "," + dimensions[2] +")";
	}
	
	public static String getSolutionCode(String line) {
		return line.split(" ")[line.split(" ").length - 1];
	}
	
	public static boolean[][] convertSolutionCodeToTable(String solutionCode) {
		
		boolean array[] = convertSolutionCodeTo1DBoolArray(solutionCode);
		
		
		int START_DIM_INDEX = 2;
		int BYTE_SIZE = 8;
		int height = convertBoolArrayToInt(array, START_DIM_INDEX, START_DIM_INDEX + BYTE_SIZE);
		int width = convertBoolArrayToInt(array, START_DIM_INDEX + BYTE_SIZE, START_DIM_INDEX + 2 * BYTE_SIZE);
		
		int startDataIndex = START_DIM_INDEX + 2*BYTE_SIZE;
		
		boolean ret[][] = new boolean[height][width];
		
		for(int i=0; i + startDataIndex < array.length; i++) {
			
			int retI  = i / width;
			int retJ  = i % width;
			
			ret[retI][retJ] = array[i + startDataIndex];
		}
		
		
		return ret;
		
	}
	
	public static int convertBoolArrayToInt(boolean array[], int startIndex, int endIndex) {
		int ret = 0;
		
		for(int i=startIndex; i<endIndex; i++) {
			if(array[i]) {
				ret = 2*ret + 1;
			} else {
				ret *= 2;
			}
		}
		
		return ret;
	}
	
	public static boolean[] convertSolutionCodeTo1DBoolArray(String solutionCode) {
		
		BigInteger solutionCodeBool = new BigInteger(solutionCode);
		
		//System.out.println(solutionCodeBool);
		
		int lengthArray = 0;
		
		BigInteger cur = solutionCodeBool;
		BigInteger TWO = new BigInteger("2");

		lengthArray+=1;
		
		while(cur.divide(TWO).compareTo(BigInteger.ZERO) > 0) {
			lengthArray++;
			cur = cur.divide(TWO);
		}
		
		boolean ret[] = new boolean[lengthArray];
		cur = solutionCodeBool;
		
		for(int i=0; i<lengthArray; i++) {
			
			if(cur.divideAndRemainder(TWO)[1].compareTo(BigInteger.ONE) == 0) {
				ret[lengthArray - 1 - i] = true;
			} else {

				ret[lengthArray - 1 - i] = false;
			}
			
			cur = cur.divide(TWO);
		}
		return ret;
	}
	
	
	public static Coord.Coord2D[] makeCoordList(boolean array[][]) {
		
		int numElements = 0;
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[0].length; j++) {
				if(array[i][j]) {
					numElements++;
				}
			}
		}

		Coord.Coord2D ret[] = new Coord.Coord2D[numElements];
		int index = 0;
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[0].length; j++) {
				if(array[i][j]) {
					ret[index] = new Coord.Coord2D(i, j);
					index++;
				}
			}
		}
		
		return ret;
	}
	
	public static final int DEFAULT_BORDER_PADDING = 1;
	public static boolean[][] padBordersOfBoolTable(boolean table[][]) {
		
		boolean ret[][] = new boolean[table.length + 2 * DEFAULT_BORDER_PADDING][table[0].length + 2 * DEFAULT_BORDER_PADDING];
		
		for(int i=0; i<ret.length; i++) {
			for(int j=0; j<ret[0].length; j++) {
				ret[i][j] = false;
				
				if(i>=DEFAULT_BORDER_PADDING
						&& j>=DEFAULT_BORDER_PADDING
						&& i-DEFAULT_BORDER_PADDING < table.length
						&& j-DEFAULT_BORDER_PADDING<table[0].length
				) {
					ret[i][j] = table[i-1][j-1];
				}
			}
		}
		
		
		return ret;
	}
	
	public static int getNumCellsUsed(boolean table[][]) {
		int ret=0;
		for(int i=0; i<table.length; i++) {
			for(int j=0; j<table[0].length; j++) {
				if(table[i][j]) {
					ret++;
				}
			}
		}
		return ret;
	}
	
	//Pre: the borders of the input table are hitting the net, or are at least evenly padded:
	public static boolean isRotationallySymmetric(boolean table[][]) {
		
		boolean ret = true;
		
		for(int i=0; i<table.length; i++) {
			for(int j=0; j<table[0].length; j++) {
				if(table[i][j] != table[table.length - 1- i][table[0].length - 1 - j]) {
					return false;
				}
			}
		}
		
		return ret;
	}
	
}
