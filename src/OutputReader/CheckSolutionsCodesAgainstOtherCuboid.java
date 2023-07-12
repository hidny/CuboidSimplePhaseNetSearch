package OutputReader;

import java.io.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Scanner;

import DupRemover.BasicUniqueCheckImproved;
import Coord.Coord2D;

public class CheckSolutionsCodesAgainstOtherCuboid {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String file1 = "D:\\CheckA106with17x2x1.txt";
			
			Scanner in = new Scanner(new File(file1));
			
			HashSet<String> file1Solutions = new HashSet<String>();
			
			while(in.hasNextLine()) {
				String tmp = in.nextLine();
				
				//System.out.println(tmp);
				if(tmp.toLowerCase().contains("solution code:")) {
					
					String solutionCodeString = getSolutionCode(tmp);
					file1Solutions.add(solutionCodeString);
					//System.out.println(solutionCodeString);
					
					boolean table[][] = convertSolutionCodeToTable(solutionCodeString);
					
					BasicUniqueCheckImproved.isUnique(makeCoordList(table), table);
					
					if(BasicUniqueCheckImproved.debugLastScore.compareTo(new BigInteger(solutionCodeString)) != 0) {
						System.out.println("DOH! The solution code algo failed!");

						System.out.println(BasicUniqueCheckImproved.debugLastScore + "\nvs\n" + new BigInteger(solutionCodeString));
						System.out.println();
						System.out.println();
						System.exit(1);
					}
					

					//TODO: Check array against other dimensions! I'll do it later
				}
			}
			
			in.close();
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
}
