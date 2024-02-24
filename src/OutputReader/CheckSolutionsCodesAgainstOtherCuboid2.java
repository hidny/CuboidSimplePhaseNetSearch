package OutputReader;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import DupRemover.BasicUniqueCheckImproved;
import Model.Utils;
import Coord.Coord2D;

public class CheckSolutionsCodesAgainstOtherCuboid2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
			//Getting answer for area of 106:
			
			//String file1 = "D:\\output17x2x1_N=26Part2.txt";
			//String file1 = "D:\\GrainedSearch_17x3x1.txt";
			String file1 = "D:\\GrainedSearch_29_2_1.txt";
			
			int dimensionsToCheck[][] = null;
			int areaToCheck = -1;
			
			HashSet <String> codesThatCover3PlusCuboids = new HashSet <String>();
			Scanner in = new Scanner(new File(file1));
			
			HashSet<String> file1Solutions = new HashSet<String>();
			
			int numSoutionsFound = 0;
			int numSolutionsInFile = 0;
			
			while(in.hasNextLine()) {
				
				String tmp = in.nextLine();
				
				//System.out.println(tmp);
				if(tmp.toLowerCase().contains("solution code:")) {
					
					numSolutionsInFile++;
					if(numSolutionsInFile % 1000 == 0) {
						System.out.println("Number of solutions in file processed: " + numSolutionsInFile);
					}
					
					String solutionCodeString = getSolutionCode(tmp);
					file1Solutions.add(solutionCodeString);
					//System.out.println(solutionCodeString);
					
					boolean table[][] = convertSolutionCodeToTable(solutionCodeString);
					
					if(areaToCheck == -1) {
						areaToCheck = getNumCellsUsed(table);
						dimensionsToCheck = getSetOfDimensionsForSurfaceArea(areaToCheck);
					}
	
					if(areaToCheck != getNumCellsUsed(table) ) {
						System.out.println("The area of the last solution read in the file doesn't match the area of the dimensions to check!");
						System.out.println("area in file: " + getNumCellsUsed(table));
						System.out.println("Area to check: " + areaToCheck);
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
					
					int numCuboidsCoveredByNet = 0;
					for(int i=0; i<dimensionsToCheck.length; i++) {
						
						if(ValidNetSolutionChecker.hasSolution(dimensionsToCheck[i], netToReplicate, false)) {
							numCuboidsCoveredByNet++;
						}
					}
					
					if(numCuboidsCoveredByNet >= 3) {
						
						codesThatCover3PlusCuboids.add(solutionCodeString);
						
						System.out.println("Found " + numCuboidsCoveredByNet + "-way solution!");

						numSoutionsFound++;
						
						if(isRotationallySymmetric(table)) {
							System.out.println("This net is rotationally symmetric!");
						}

						System.out.println("Printing just the fold without indexes:");
						Utils.printFold(table);
						
						boolean VERBOSE = true;
						
						for(int i=0; i<dimensionsToCheck.length; i++) {
							if(ValidNetSolutionChecker.hasSolution(dimensionsToCheck[i], netToReplicate, false)) {
								System.out.println("Solution for: " + getDimensionsString(dimensionsToCheck[i]));
								
								ValidNetSolutionChecker.hasSolution(dimensionsToCheck[i], netToReplicate, VERBOSE);
								
								System.out.println();
								numCuboidsCoveredByNet++;
							}
						}
						
						System.out.println();
						
					}
					
				}
			}
			
			in.close();
			
			System.out.println("Number of solutions in file: " + numSolutionsInFile);
			System.out.println();
			System.out.println();
			System.out.println();
			
			System.out.println("Final number of 3+-way solutions found: " + numSoutionsFound);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int[][] getSetOfDimensionsForSurfaceArea(int origSurfaceArea) {
		
		int ret[][] = null;
		
		ArrayList <String>list = new ArrayList <String>();
		
		for(int a=1; a<100; a++) {
			for(int b=a; b<100; b++) {
				for(int c=b; c<100; c++) {
					
					int surfaceArea = 2 * (a*b + a*c + b*c);
					
					if(origSurfaceArea == surfaceArea) {
						list.add(a + "," + b + "," + c);
					}
				}
			}
		}
		
		ret = new int[list.size()][3];
		
		for(int i=0; i<ret.length; i++) {
			
			String tokens[] = list.get(i).split(",");
			
			for(int j=0; j<tokens.length; j++) {
				ret[i][j] = pint(tokens[j]);
			}
		}
		
		return ret;
	}
	
	public static int pint(String s) {
		if (isNumber(s)) {
			return Integer.parseInt(s);
		} else {
			System.out.println("Error: (" + s + ") is not a number");
			return -1;
		}
	}
	public static boolean isNumber(String val) {
		try {
			int a = Integer.parseInt(val);
			return true;
		} catch(Exception e) {
			return false;
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