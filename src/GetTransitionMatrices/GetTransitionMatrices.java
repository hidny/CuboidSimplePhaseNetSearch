package GetTransitionMatrices;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class GetTransitionMatrices {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getMatrixForBandSizeN(6);
	}
	
	public static void getMatrixForBandSizeN(int bandSize) {
		ArrayList<LayerStateWithGroundedNumber> curLayerStates = getOverCountOfPossibleLayerStates(bandSize);
		
		int matrixArray[][] = new int[curLayerStates.size()][curLayerStates.size()];
		
		
		for(int i=0; i<matrixArray.length; i++) {
			for(int j=0; j<matrixArray[0].length; j++) {
		
				matrixArray[i][j] = getNumOfWaysLayerStateCouldStack(
						curLayerStates.get(i),
						curLayerStates.get(j)
					);
				
			
				/*System.out.println(curLayerStates.get(i));
				System.out.println("over");
				System.out.println(curLayerStates.get(j));
				System.out.println(" = " + matrixArray[i][j]);

				System.out.println();
				System.out.println();
				*/
			}
		}
		
		
		Queue<Integer> queue = new LinkedList();
		queue.add(0);
		boolean explored1[] = new boolean[curLayerStates.size()];
		boolean explored2[] = new boolean[curLayerStates.size()];
		
		for(int i=0; i<explored1.length; i++) {
			explored1[i] = false;
			explored2[i] = false;
		}
		
		explored1[0] = true;
		explored2[0] = true;
		
		while(queue.isEmpty() == false) {
			
			int curJ = queue.poll();
			
			for(int i=0; i<curLayerStates.size(); i++) {
				
				if(matrixArray[i][curJ] > 0 && explored1[i] == false) {
					explored1[i] = true;
					queue.add(i);
				}
			}
		}
		
		queue = new LinkedList<Integer>();
		queue.add(0);
		
		while(queue.isEmpty() == false) {
			
			int curI = queue.poll();
			
			for(int j=0; j<curLayerStates.size(); j++) {
				
				if(matrixArray[curI][j] > 0 && explored2[j] == false) {
					explored2[j] = true;
					queue.add(j);
				}
			}
		}
		
		boolean reachable[] = new boolean[curLayerStates.size()];
		
		int size = 0;
		
		for(int i=0; i<reachable.length; i++) {
			reachable[i] = explored1[i] && explored2[i];
			System.out.println(explored1[i] + ", " +  explored2[i]);
			if(reachable[i]) {
				size++;
			}
		}
		
		int smallerMatrix[][] = new int[size][size];
		
		int curI = 0;
		int curJ = 0;
		
		for(int i=0, smallI=0; i<matrixArray.length; i++) {
			
			if(! reachable[i]) {
				continue;
			}
			
			for(int j=0, smallJ=0; j<matrixArray[0].length; j++) {

				if(! reachable[j]) {
					continue;
				}
				
				smallerMatrix[smallI][smallJ] = matrixArray[i][j];
				
				smallJ++;
			}
			
			smallI++;
		}
		

		System.out.println("Smaller:");
		String SPACE = "     ";
		for(int i=0; i<smallerMatrix.length; i++) {
			for(int j=0; j<smallerMatrix[0].length; j++) {
				System.out.print(smallerMatrix[i][j] + "" +  SPACE.substring((smallerMatrix[i][j] + "").length()));
			}
			System.out.println();
		}


		System.out.println("Bigger:");
		//String SPACE = "     ";
		for(int i=0; i<matrixArray.length; i++) {
			for(int j=0; j<matrixArray[0].length; j++) {
				System.out.print(matrixArray[i][j] + "" +  SPACE.substring((matrixArray[i][j] + "").length()));
			}
			System.out.println();
		}
		
		matrixArray = smallerMatrix;
		
		String wolfram = "";
		System.out.println("Wolfram alpha format:");
		
		wolfram += "{";
		for(int i=0; i<matrixArray.length; i++) {
			wolfram += "{";
			for(int j=0; j<matrixArray[0].length; j++) {
				if(j< matrixArray[0].length - 1) {
					wolfram += matrixArray[i][j] + ",";
				} else {
					wolfram += matrixArray[i][j];
				}
			}
			if(i< matrixArray.length - 1) {
				wolfram += "},";
			} else {
				wolfram += "}";
			}
		}
		wolfram += "}";
		System.out.println(wolfram);
		
		//{{7, 4, 5, 7, 2, 4, 5, 7, 2, 0, 2, 2, 5, 4, 7, 4, 4, 7, 1, 4, 4, 7}, {5, 0, 5, 5, 0, 0, 5, 5, 1, 1, 1, 1, 1, 2, 2, 0, 2, 2, 0, 0, 2, 2}, {2, 2, 0, 2, 0, 2, 0, 2, 1, 0, 1, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2}, {2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {5, 0, 0, 5, 0, 0, 0, 5, 0, 0, 2, 0, 2, 0, 8, 0, 0, 8, 0, 0, 0, 8}, {5, 0, 5, 5, 0, 0, 5, 5, 1, 1, 1, 1, 1, 2, 2, 0, 2, 2, 0, 0, 2, 2}, {2, 2, 0, 2, 0, 2, 0, 2, 1, 0, 1, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2}, {2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {1, 0, 2, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 1, 0, 2, 2, 0, 0, 2, 2}, {4, 2, 1, 2, 1, 2, 1, 2, 0, 0, 0, 0, 2, 0, 2, 1, 0, 1, 0, 1, 0, 1}, {3, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0, 2}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 2, 0, 2, 1, 0, 1, 0, 1, 0, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {2, 0, 2, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2, 0, 0, 2, 2}, {2, 2, 1, 2, 1, 2, 1, 2, 0, 0, 0, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2}, {2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {8, 0, 2, 8, 0, 0, 2, 8, 2, 0, 3, 0, 3, 0, 8, 0, 0, 8, 0, 0, 0, 8}, {2, 0, 2, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2, 0, 0, 2, 2}, {2, 2, 1, 2, 1, 2, 1, 2, 0, 0, 0, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2}, {2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}}

		String python = "";
		System.out.println("Python format:");
		
		python += "[";
		for(int i=0; i<matrixArray.length; i++) {
			python += "[";
			for(int j=0; j<matrixArray[0].length; j++) {
				if(j< matrixArray[0].length - 1) {
					python += matrixArray[i][j] + ",";
				} else {
					python += matrixArray[i][j];
				}
			}
			if(i< matrixArray.length - 1) {
				python += "],";
			} else {
				python += "]";
			}
		}
		python += "]";
		System.out.println(python);
	}
	
	public static int getNumOfWaysLayerStateCouldStack(LayerStateWithGroundedNumber top, LayerStateWithGroundedNumber bottom) {
		
		if(bottom.isIndexOccupied(0) && bottom.isIndexOccupied(1) && bottom.isIndexOccupied(2) ) {
			System.out.println("Debug");
		}
		int lengthArrays = top.getLengthArray();
		if(top.getLengthArray() != bottom.getLengthArray()) {
			System.out.println("ERROR: oops! top.getLengthArray() != bottom.getLengthArray()");
			System.exit(1);
		}
		
		int ret = 0;
		
		for(int sideBump= 0 - lengthArrays; sideBump < lengthArrays; sideBump++) {


			boolean connectionStillCurrentlyPossible = true;
			
			boolean currentIslandTouching = false;
			
			
			for(int i=0; i<bottom.getLengthArray(); i++) {
				
				if(i > 0 && ! bottom.isIndexOccupied(i)
						&& bottom.isIndexOccupied(i - 1)) {
					
					if( LayerStateWithGroundedNumber.isCellGroundedDirectly(bottom, i-1) == false
							&& ! currentIslandTouching) {
						connectionStillCurrentlyPossible = false;
						break;
					}
					
					currentIslandTouching = false;
					
					continue;
				} else if( ! bottom.isIndexOccupied(i)) {
					continue;
				}
				
				if(! bottom.isIndexOccupied(i)) {
					System.out.println("DOH!");
					System.exit(1);
				}
				
				int indexTop = i + sideBump;
				
				if(indexTop >=0 && indexTop < lengthArrays) {
					
					if(bottom.isIndexOccupied(i) 
							&& top.isIndexOccupied(indexTop)) {
						currentIslandTouching = true;
					}
				}
				
			}
			
			if(bottom.isIndexOccupied(bottom.getLengthArray() - 1)
				&& LayerStateWithGroundedNumber.isCellGroundedDirectly(bottom, bottom.getLengthArray()-1) == false
				&& ! currentIslandTouching) {
						connectionStillCurrentlyPossible = false;
					
			}
			
			
			if(! connectionStillCurrentlyPossible) {
				continue;
			}
			//TODO: check top afterwards:
			
			boolean currentIslandTouchingGroundedBelow = false;
			
			
			for(int i=0; i<top.getLengthArray(); i++) {
				
				if(i > 0 && ! top.isIndexOccupied(i)
						&& top.isIndexOccupied(i - 1)) {
					
					if( LayerStateWithGroundedNumber.isCellGroundedDirectly(top, i-1) == true
							&& ! currentIslandTouchingGroundedBelow) {
						connectionStillCurrentlyPossible = false;
						break;
					} else if( LayerStateWithGroundedNumber.isCellGroundedDirectly(top, i-1) == false
							&& currentIslandTouchingGroundedBelow) {
						connectionStillCurrentlyPossible = false;
						break;
					}
					
					currentIslandTouchingGroundedBelow = false;
					
					continue;
				} else if( ! top.isIndexOccupied(i)) {
					continue;
				}
				
				if(! top.isIndexOccupied(i)) {
					System.out.println("DOH 2!");
					System.exit(1);
				}
				
				int indexBottom = i - sideBump;
				
				if(indexBottom >=0 && indexBottom < lengthArrays) {
					
					if(bottom.isIndexOccupied(indexBottom)
							&& LayerStateWithGroundedNumber.isCellGroundedDirectly(bottom, indexBottom)
							&& top.isIndexOccupied(i)) {
						currentIslandTouchingGroundedBelow = true;
					}
				}
				
			}
			
			if(top.isIndexOccupied(bottom.getLengthArray() - 1)) {
				
				if( LayerStateWithGroundedNumber.isCellGroundedDirectly(top, bottom.getLengthArray() - 1) == true
						&& ! currentIslandTouchingGroundedBelow) {
					connectionStillCurrentlyPossible = false;

				} else if( LayerStateWithGroundedNumber.isCellGroundedDirectly(top, bottom.getLengthArray() - 1) == false
						&& currentIslandTouchingGroundedBelow) {
					connectionStillCurrentlyPossible = false;
				}
			}
			
			if(connectionStillCurrentlyPossible) {
				System.out.println("Side bump: " + sideBump);
				ret++;
			}
		}
		return ret;
	}
	
	public static ArrayList <LayerStateWithGroundedNumber> getOverCountOfPossibleLayerStates(int bandSize) {
		if(bandSize == 1) {
			System.out.println("Let's skip the case where n=1...");
			return null;
		}
		//TODO
		
		//TODO: refer to the correct theorem...
		int numLayerStateIgnoreGround = (int)Math.pow(2, bandSize - 2);
		
		ArrayList <LayerStateWithGroundedNumber> layerStatesWithGround = new ArrayList <LayerStateWithGroundedNumber>();
		
		for(int i=0; i<numLayerStateIgnoreGround; i++) {
			
			boolean array[] = generateLayerArray(i, bandSize);
			
			int numIslands = LayerStateWithGroundedNumber.getNumberOfIslands(array);
			
			printArray(array);
			
			System.out.println("Number of islands for above: " + numIslands);
			System.out.println();
			
			//TODO: mayhe there's more rules about what a useful layer state could be...
			int numLayerStatesToAdd = (int)Math.pow(2, numIslands);
			
			for(int j=0; j<numLayerStatesToAdd; j++) {
				if(j == 0) {
					//Skip groundState 0 because that can't be useful:
					continue;
				}
				
				
				layerStatesWithGround.add(new LayerStateWithGroundedNumber(array, j));
			}
			
			
		}
		
		System.out.println("Current Size of the Array: " + layerStatesWithGround.size());
		
		return layerStatesWithGround;
	}
	
	public static void printArray(boolean array[]) {

		for(int j=0; j<array.length; j++) {
			if(array[j]) {
				System.out.print("#");
			} else {
				System.out.print(".");
			}
		}
		System.out.println();
	}
	
	public static boolean[] generateLayerArray(int indexLayerType, int bandSize) {
		
		boolean ret[] = new boolean[2 * bandSize -1];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = false;
		}
		
		//TODO: refer to the right lemma:
		//index 0 and bandSize - 1 is always true:
		ret[0] = true;
		ret[bandSize - 1] = true;
		
		int cur = indexLayerType;
		
		int curIndex = ret.length - 1;
		
		for(int i=0; i< bandSize - 1; i++) {
			
			if(cur % 2 == 0) {
				ret[curIndex - bandSize] = true;
			} else {
				ret[curIndex] = true;
			}
			cur = cur / 2;
			curIndex--;
		}
		
		
		return ret;
	}

	/*Eigenvalues with python links after a quick google:
	 * https://pythonnumericalmethods.berkeley.edu/notebooks/chapter15.04-Eigenvalues-and-Eigenvectors-in-Python.html
	 * https://numpy.org/doc/stable/reference/generated/numpy.linalg.eig.html
	 * https://stackoverflow.com/questions/6684238/whats-the-fastest-way-to-find-eigenvalues-vectors-in-python
	 */
}
