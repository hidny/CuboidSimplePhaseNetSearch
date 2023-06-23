package Model;


import java.math.BigInteger;

import Coord.Coord;
import Coord.Coord2D;

public class Utils {

	public static final int SIDES_CUBOID = 6;
	
	public static int[][][] getFlatNumberingOfCuboid(int array[]) {
		return getFlatNumberingOfCuboid(array[0], array[1], array[2]);
	}

	public static int[][][] getFlatNumberingOfCuboid(int a, int b, int c) {

		int temp_numbering[][][] = new int[SIDES_CUBOID][][];
		temp_numbering[0] = new int[c][b];
		temp_numbering[1] = new int[a][c];
		temp_numbering[2] = new int[a][b];
		temp_numbering[3] = new int[a][c];
		temp_numbering[4] = new int[a][b];
		temp_numbering[5] = new int[c][b];
		
		int currentNum = 0;
		
		
		for(int i=0; i<temp_numbering.length; i++) {
			for(int j=0; j<temp_numbering[i].length; j++) {
				for(int k=0; k<temp_numbering[i][j].length; k++) {
					temp_numbering[i][j][k] = currentNum;
					currentNum++;
				}
			}
		}
		if(currentNum != getTotalArea(a, b, c)) {
			System.out.println("Current num is not the total area. Something went wrong!");
			System.exit(1);
		}
		
		return temp_numbering;
	}

	public static Coord[] getFlatInverseNumberingOfCuboid(int temp_numbering[][][], int array[]) {
		return getFlatInverseNumberingOfCuboid(temp_numbering, array[0], array[1], array[2]);
	}

	public static Coord[] getFlatInverseNumberingOfCuboid(int temp_numbering[][][], int a, int b, int c) {
		
		Coord numberingInv[] = new Coord[getTotalArea(a, b, c)];
				
		for(int i=0; i<temp_numbering.length; i++) {
			for(int j=0; j<temp_numbering[i].length; j++) {
				for(int k=0; k<temp_numbering[i][j].length; k++) {

					int currentNum = temp_numbering[i][j][k];
					numberingInv[currentNum] = new Coord(i, j, k);
				}
			}
		}
		
		return numberingInv;
	}
	

	public static int getSideCell(CuboidToFoldOn inputCuboid, int cellIndex) {
		
		int answer = getFlatInverseNumberingOfCuboid(
				getFlatNumberingOfCuboid(inputCuboid.getDimensions()), 
				inputCuboid.getDimensions())[cellIndex].a;
		
		return answer;
	}
	
	public static int getTotalArea(int array[]) {
		if(array.length != 3) {
			System.out.println("ERROR in getTotalArea: there isn't 3 dimensions!");
			System.exit(1);
		}
		return getTotalArea(array[0], array[1], array[2]);
	}
	public static int getTotalArea(int a, int b, int c) {
		return 2*(a*b + a*c + b*c);
	}
	
	public static int[] getBorders(int array[][]) {
		boolean array2[][] = new boolean[array.length][array[0].length];
		
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[0].length; j++) {
				if(array[i][j] >=0) {
					array2[i][j] = true;
				} else {
					array2[i][j] = false;
				}
			}
		}
		
		return getBorders(array2);
		
	}
	
	//Returns: new int[] {firsti, lasti, firstj, lastj};
	public static int[] getBorders(Coord2D paperToDevelop[]) {
		
		int ret[] = new int[] {
			paperToDevelop[0].i,
			paperToDevelop[0].i,
			paperToDevelop[0].j, 
			paperToDevelop[0].j
		};
		
		for(int i=1; i<paperToDevelop.length; i++) {
			
			if(paperToDevelop[i].i < ret[0]) {
				ret[0] = paperToDevelop[i].i;
			} else if(paperToDevelop[i].i > ret[1]) {
				ret[1] = paperToDevelop[i].i;
			}

			if(paperToDevelop[i].j < ret[2]) {
				ret[2] = paperToDevelop[i].j;
			} else if(paperToDevelop[i].j > ret[3]) {
				ret[3] = paperToDevelop[i].j;
			}
		}

		return ret;
	}
	
	public static int[] getBorders(boolean array[][]) {
		//TODO: make function to get borders...
		int firsti = 0;
		
		int lasti = array.length - 1;
		
		
		int firstj = 0;
		int lastj = array[0].length - 1;
		
		TOP_BORDER:
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[0].length; j++) {
				
				if(array[i][j]) {
					firsti = i;
					break TOP_BORDER;
				}
			}
		}
		

		BOTTOM_BORDER:
		for(int i=array.length - 1; i>=0; i--) {
			for(int j=0; j<array[0].length; j++) {
				
				if(array[i][j]) {
					lasti = i;
					break BOTTOM_BORDER;
				}
			}
		}
		
		
		LEFT_BORDER:
		for(int j=0; j<array[0].length; j++) {
			for(int i=0; i<array.length; i++) {
				
				if(array[i][j]) {
					firstj = j;
					break LEFT_BORDER;
				}
			}
		}
		

		RIGHT_BORDER:
		for(int j=array[0].length - 1; j>=0; j--) {
			for(int i=0; i<array.length; i++) {
				
				if(array[i][j]) {
					lastj = j;
					break RIGHT_BORDER;
				}
			}
		}

		//END TODO: make function to get borders...
		
		return new int[] {firsti, lasti, firstj, lastj};		
	}
	
	
	//TODO: remove empty border (reuse code)
		public static void printFold(boolean array[][]) {
			
			StringBuilder sb = new StringBuilder();
			
			int borders[] = getBorders(array);
			
			for(int i=borders[0]; i<borders[1] + 1; i++) {
				for(int j=borders[2]; j<borders[3] + 1; j++) {
					if(array[i][j]) {
						sb.append('#');
					} else {
						sb.append('.');
					}
				}
				sb.append("|" + System.lineSeparator());
			}
			System.out.println(sb.toString());
		}
		

		//TODO: remove empty border (reuse code)
		//TODO: improve and make it return a string
		public static void printFoldWithIndex(int array[][]) {
			
			int borders[] = getBorders(array);
			
			int maxLength = 0;
			for(int i=borders[0]; i<borders[1] + 1; i++) {
				for(int j=borders[2]; j<borders[3] + 1; j++) {
					if(array[i][j] >= 0) {
						if((array[i][j] + "").length() > maxLength) {
							maxLength = (array[i][j] + "").length();
						}
					}
				}
			}
			
			String space ="";
			String points = "";
			
			StringBuilder sb = new StringBuilder();
			
			for(int i=0; i<maxLength; i++) {
				space += " ";
				points += ".";
				
			}
			
			for(int i=borders[0]; i<borders[1] + 1; i++) {
				for(int j=borders[2]; j<borders[3] + 1; j++) {
					if(array[i][j] >= 0) {
						int lengthNum = (array[i][j] + "").length();
						
						if(lengthNum < maxLength) {
							sb.append("|" + space.substring(lengthNum) + array[i][j]);
						} else {
							sb.append("|"+ array[i][j]);
						}
						
					} else {
						sb.append("|" + points);
					}
				}
				sb.append("|" + System.lineSeparator());
			}
			System.out.println(sb.toString());
		}
		
		public static boolean debugBadFold(int array[][], boolean used[]) {
			
			int borders[] = getBorders(array);
			
			for(int i=borders[0]; i<borders[1] + 1; i++) {
				for(int j=borders[2]; j<borders[3] + 1; j++) {
					if(array[i][j] >= 0) {
						
						if(used[array[i][j]] == false) {
							return true;
						}
						
					}
				}
			}
			return false;
		}
		
		public static boolean cuboidDimensionsMatch(CuboidToFoldOn cuboid1, CuboidToFoldOn cuboid2) {

			return cuboid1.getDimensions()[0] == cuboid2.getDimensions()[0]
					&& cuboid1.getDimensions()[1] == cuboid2.getDimensions()[1]
							&& cuboid1.getDimensions()[2] == cuboid2.getDimensions()[2];
		}
		
		

		public static void printFromSolutionCode(BigInteger solutionCode) {
			boolean array[] = convertToBoolArray(solutionCode);
			
			int height = convertBoolArraySegmentToNumber(array, 2, 10);
			int width = convertBoolArraySegmentToNumber(array, 10, 18);
			
			boolean table[][] = new boolean[height][width];
			
			int currentIndex = 18;
			
			for(int i=0; i<table.length; i++) {
				for(int j=0; j<table[0].length; j++) {
					
					table[i][j] = array[currentIndex];
					currentIndex++;
				}
			}
			
			Utils.printFold(table);
			
		}
		
		public static int convertBoolArraySegmentToNumber(boolean array[], int startIndex, int endIndex) {
			
			int ret = 0;
			
			for(int i=startIndex; i<endIndex; i++) {
				if(array[i]) {
					ret = 2*ret + 1;
				} else {
					ret = 2*ret;
				}
			}
			
			return ret;
		}
		
		public static boolean[] convertToBoolArray(BigInteger solutionCode) {
			int sizeArray = 0;
			
			BigInteger tmp = solutionCode;
			
			BigInteger TWO = new BigInteger("2");
			
			while(tmp.compareTo(BigInteger.ZERO) > 0) {
				
				tmp = tmp.divide(TWO);
				sizeArray++;
			}
			
			boolean ret[] = new boolean[sizeArray];
			int currentIndex = sizeArray - 1;
		
			tmp = solutionCode;
			
			for(int i=0; i<ret.length; i++, currentIndex--) {
				
				if(tmp.divideAndRemainder(TWO)[1].compareTo(BigInteger.ONE) == 0) {
					ret[currentIndex] = true;
				} else {
					ret[currentIndex] = false;
				}
				
				tmp = tmp.divide(TWO);
			}
			
			
			return ret;
		}
}
