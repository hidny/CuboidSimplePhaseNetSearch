package DupRemover;

import java.math.BigInteger;
import java.util.HashSet;

import Model.Utils;
import Coord.Coord2D;

public class BasicUniqueCheckImproved {


	public static int NUM_REFLECTIONS = 2;
	public static int NUM_ROTATIONS = 4;
	public static int CHECK_SYMMETRIES_ONE_DIM_FACTOR = 2;

	public static int MAX_WIDTH_PLUS_ONE = 256;

	public static HashSet<BigInteger> debugUniqList = new HashSet<BigInteger>();
	
	public static HashSet<BigInteger> uniqList = new HashSet<BigInteger>();
	public static BigInteger debugLastScore = null;
	
	public static void resetUniqList() {
		uniqList = new HashSet<BigInteger>();
	}
	
	public static boolean isUnique(Coord2D paperToDevelop[], boolean array[][]) {

		int borders[] = Utils.getBorders(paperToDevelop);
		
		int firsti = borders[0];
		int lasti = borders[1];
		int firstj = borders[2];
		int lastj = borders[3];

		BigInteger TWO = new BigInteger("2");
		
		
		long heightShape = lasti - firsti + 1;
		long widthShape = lastj - firstj + 1;
		
		BigInteger scores[] = new BigInteger[NUM_REFLECTIONS * NUM_ROTATIONS];
		boolean tooHigh[] = new boolean[NUM_REFLECTIONS * NUM_ROTATIONS];
		
		if(heightShape < widthShape) {
			scores = new BigInteger[NUM_REFLECTIONS * NUM_ROTATIONS / CHECK_SYMMETRIES_ONE_DIM_FACTOR];
			

			for(int i=0; i<scores.length; i++) {
				//3 * 256^2 fixes a possible hash collision
				// I made it 3 instead of 1 because in future, I want placement of first and second binary 1 to mean something
				scores[i] = new BigInteger((3 * MAX_WIDTH_PLUS_ONE * MAX_WIDTH_PLUS_ONE + heightShape * MAX_WIDTH_PLUS_ONE + widthShape) + "");

			}
			
			boolean onlyOneContender = false;
			
				//Outer loop will be the shorter dimension because I said so:
				for(int i=firsti, irev = lasti; i<=lasti; i++, irev--) {
					for(int j=firstj, jrev = lastj; j<=lastj; j++, jrev--) {
						
						for(int k=0; k<scores.length; k++) {
							if(tooHigh[k]) {
								continue;
							}
							scores[k] = scores[k].multiply(TWO);
						}
						
						if(!tooHigh[0] && array[i][j]) {
							scores[0] = scores[0].add(BigInteger.ONE);
						}
						
						if(!tooHigh[1] && array[i][jrev]) {
							scores[1] = scores[1].add(BigInteger.ONE);
						}
						
						if(!tooHigh[2] && array[irev][j]) {
							scores[2] = scores[2].add(BigInteger.ONE);
						}
						
						if(!tooHigh[3] && array[irev][jrev]) {
							scores[3] = scores[3].add(BigInteger.ONE);
						}
		
						if(! onlyOneContender  ) {
		
							tooHigh = refreshNumContenders(scores, tooHigh);
							
							int numContender = 0;
							for(int k=0; k<scores.length; k++) {
								if(! tooHigh[k]) {
									numContender++;
								}
							}
							if(numContender == 1) {
								onlyOneContender = true;
							}
						}
					}
				}
						
		} else if(heightShape > widthShape) {
			
			scores = new BigInteger[NUM_REFLECTIONS * NUM_ROTATIONS / CHECK_SYMMETRIES_ONE_DIM_FACTOR];
			

			for(int i=0; i<scores.length; i++) {
				//3 * 256^2 fixes a possible hash collision
				// I made it 3 instead of 1 because in future, I want placement of first and second binary 1 to mean something
				scores[i] = new BigInteger((3 * MAX_WIDTH_PLUS_ONE * MAX_WIDTH_PLUS_ONE + widthShape * MAX_WIDTH_PLUS_ONE + heightShape) + "");


			}
			
			boolean onlyOneContender = false;
			
			//Outer loop will be the shorter dimension because I said so:
			for(int j2=firstj, j2rev=lastj; j2<=lastj; j2++, j2rev--) {
				for(int i2=firsti, i2rev=lasti; i2<=lasti; i2++, i2rev--) {
					
					for(int k=0; k<scores.length; k++) {
						if(tooHigh[k]) {
							continue;
						}
						scores[k] = scores[k].multiply(TWO);
					}
					
					if(!tooHigh[0] && array[i2][j2]) {
						scores[0] = scores[0].add(BigInteger.ONE);
					}
					
					if(!tooHigh[1] && array[i2][j2rev]) {
						scores[1] = scores[1].add(BigInteger.ONE);
					}
					
					if(!tooHigh[2] && array[i2rev][j2]) {
						scores[2] = scores[2].add(BigInteger.ONE);
					}
					
					if(!tooHigh[3] && array[i2rev][j2rev]) {
						scores[3] = scores[3].add(BigInteger.ONE);
					}
	
					if(! onlyOneContender  ) {
	
						tooHigh = refreshNumContenders(scores, tooHigh);
						
						int numContender = 0;
						for(int k=0; k<scores.length; k++) {
							if(! tooHigh[k]) {
								numContender++;
							}
						}
						if(numContender == 1) {
							onlyOneContender = true;
						}
					}
					
				}
			}
			
		} else {
			//Do all 8 symmetries because it's an NxN grid:
			scores = new BigInteger[NUM_REFLECTIONS * NUM_ROTATIONS];


			for(int i=0; i<scores.length; i++) {
				//3 * 256^2 fixes a possible hash collision
				// I made it 3 instead of 1 because in future, I want placement of first and second 1 to mean something
				scores[i] = new BigInteger((3 * MAX_WIDTH_PLUS_ONE * MAX_WIDTH_PLUS_ONE + heightShape * MAX_WIDTH_PLUS_ONE + widthShape) + "");

			}

			boolean onlyOneContender = false;
			
			for(int i=firsti, irev = lasti; i<=lasti; i++, irev--) {
				for(int j=firstj, jrev = lastj; j<=lastj; j++, jrev--) {
					
					for(int k=0; k<scores.length; k++) {
						if(tooHigh[k]) {
							continue;
						}
						scores[k] = scores[k].multiply(TWO);
					}
					
					int tmp = (i-firsti) * (lastj - firstj + 1) + (j - firstj);
					int i2 = firsti + (tmp % (lasti - firsti + 1));
					int j2 = firstj + (tmp / (lasti - firsti + 1));
	
					int i2rev = lasti - (tmp % (lasti - firsti + 1));
					int j2rev = lastj - (tmp / (lasti - firsti + 1));
	
					if(!tooHigh[0] && array[i][j]) {
						scores[0] = scores[0].add(BigInteger.ONE);
					}
					
					if(!tooHigh[1] && array[i][jrev]) {
						scores[1] = scores[1].add(BigInteger.ONE);
					}
					
					if(!tooHigh[2] && array[irev][j]) {
						scores[2] = scores[2].add(BigInteger.ONE);
					}
					
					if(!tooHigh[3] && array[irev][jrev]) {
						scores[3] = scores[3].add(BigInteger.ONE);
					}
	
					
					if(!tooHigh[4] && array[i2][j2]) {
						scores[4] = scores[4].add(BigInteger.ONE);
					}
					
					if(!tooHigh[5] && array[i2][j2rev]) {
						scores[5] = scores[5].add(BigInteger.ONE);
					}
					
					if(!tooHigh[6] && array[i2rev][j2]) {
						scores[6] = scores[6].add(BigInteger.ONE);
					}
					
					if(!tooHigh[7] && array[i2rev][j2rev]) {
						scores[7] = scores[7].add(BigInteger.ONE);
					}
	
					if(! onlyOneContender  ) {
	
						tooHigh = refreshNumContenders(scores, tooHigh);
						
						int numContender = 0;
						for(int k=0; k<scores.length; k++) {
							if(! tooHigh[k]) {
								numContender++;
							}
						}
						if(numContender == 1) {
							onlyOneContender = true;
						}
					}
					
				}
				
				
			}
		}
		
		//Deal with symmetries by getting max scores from the possible symmetries:
		BigInteger min = BigInteger.ZERO;
		
		for(int i=0; i<scores.length; i++) {
			if(! tooHigh[i]) {
				min = scores[i];
				break;
			}
		}
		
		//Sanity check:
		//sanityCheck(array, max);
		//End Sanity check
		
		if(! uniqList.contains(min)) {
			uniqList.add(min);
			
			debugLastScore = min;
			
			//System.out.println("Max number: " + max);
			
			return true;
		} else {
			
			debugLastScore = min;
			return false;
		}
	}
	
	public static boolean[] refreshNumContenders(BigInteger scores[], boolean tooHigh[]) {
		for(int k=0; k<scores.length; k++) {
			if(tooHigh[k]) {
				continue;
			}
			for(int m=k+1; m<scores.length; m++) {
				if(tooHigh[m]) {
					continue;
				}
				
				if(scores[k].compareTo(scores[m]) > 0) {
					
					tooHigh[k] = true;
					
					break;
				} else if(scores[k].compareTo(scores[m]) < 0) {
					tooHigh[m] = true;
					
				}
			}
		}
		
		return tooHigh;
	}
	
}
