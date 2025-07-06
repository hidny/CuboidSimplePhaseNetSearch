package GetTransitionMatrices2025_7;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import GetTransitionMatrices2025_6.LayerState6;

//TODO: Fix bug! Make connections transitive!
//ODNE

//TODO: Stop loops before the end of the array.
//TODO: try to reduce left and right extreme further...

public class MatrixCreator7 {
	
	//TODO:
	
	//For finding the top eigenvalue, try using:
	//Try using: https://stackoverflow.com/questions/22507707/finding-largest-eigenvalue-in-sparse-matrix
	//" I use scipy.sparse.linalg.eigsh for symmetric sparse matrices passing which="LM":
	//eigvals, eigvecs = eigsh(A, k=10, which='LM', sigma=1.)""
	
	//https://docs.scipy.org/doc/scipy/reference/generated/scipy.sparse.linalg.eigsh.html
	
	//Perimeter 8 takes over an hour without a few optimizations...
	public static final int PERIMETER = 9;
	public static final int LEFT_EXTREME = 0 - PERIMETER + 1;
	//public static final int RIGHT_EXTREME = PERIMETER * PERIMETER + PERIMETER;
	
	public static void main(String args[]) {

		ArrayList <LayerState7> validLayerStates = getValidLayerStates();
		
		
		if(PERIMETER < 3) {
			int matrix[][] = createMatrix(validLayerStates);
			
			System.out.println("matrix width for perimter " + PERIMETER + ":" + matrix.length);
			printMatrix(matrix);
			
			System.out.println("Matrix to be used by python:");
			System.out.println(convertMatrixToPythonFormat(matrix));
		}
		//printSparsePythonMatrix(validLayerStates);
		
		//For now, I just want to debug:
		printNumbers(validLayerStates);
	}
	
	//TODO: this algo is slow!
	// I could eliminate a whole loop if I needed to!
	
	public static void printNumbers(ArrayList <LayerState7> validLayerStates) {
		int numNonZeroCells = 0;
		int sumOfAllEntries = 0;
		int maxWidth = 0;

		System.out.println("Number of states: " + validLayerStates.size());
		System.out.println("debugNumCouldReachFromLayer0: " + debugNumCouldReachFromLayer0);
		System.out.println("debugNumCouldReachLayer0_yes: " + debugNumCouldReachLayer0_yes);
		System.out.println("debugNumCouldReachLayer0_no: " + debugNumCouldReachLayer0_no);
		
		for(int i=0; i<validLayerStates.size(); i++) {
			
			if(validLayerStates.get(i).getWidthLayer() > maxWidth) {
				maxWidth = validLayerStates.get(i).getWidthLayer();
			}
			
			for(int j=0; j<validLayerStates.size(); j++) {
				
				LayerState7 bottom = validLayerStates.get(j);
				LayerState7 top = validLayerStates.get(i);
				

				int currentBottomLayerWidth = bottom.getWidthLayer();
				int currentTopLayerWidth = top.getWidthLayer();
				
				int curCellValue = 0;
				
				for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
					LayerState7 result = LayerState7.addLayerStateOnTopOfLayerState(bottom, top, sideBump);
					
					if(result != null && top.equals(result)) {
						curCellValue++;
					}
					
				}
				
				
				if(curCellValue > 0) {
					
					numNonZeroCells++;
					
					sumOfAllEntries+= curCellValue;
				}
			}
			
		}
		
		System.out.println();
		System.out.println("Number of non-zero cells: " + numNonZeroCells);
		System.out.println("Sum of all cell in matrix: " + sumOfAllEntries);
		System.out.println("Max width layer for perimeter: " + maxWidth);
		
	}
	public static void printSparsePythonMatrix(ArrayList <LayerState7> validLayerStates) {
		
		
		System.out.println("Printing sparse Python Matrix:");
		try {
			PrintWriter output = new PrintWriter("D:\\outputMatrixPerimeter" + PERIMETER + ".py");
		
			output.println("import numpy as np\r\n"
					+ "from numpy.linalg import eig");
			
			output.println();
			String varName = "tmpMatrix" + PERIMETER;
			
			output.println(varName + " = [[0 for x in range(" + validLayerStates.size() + ")] for y in range(" + validLayerStates.size() + ")]");
			
			System.out.println("Number of states: " + validLayerStates.size());
			System.out.println("debugNumCouldReachLayer0_calls: " + debugNumCouldReachFromLayer0);
			System.out.println("debugNumCouldReachLayer0_yes: " + debugNumCouldReachLayer0_yes);
			System.out.println("debugNumCouldReachLayer0_no: " + debugNumCouldReachLayer0_no);
			
			int numNonZeroCells = 0;
			int sumOfAllEntries = 0;
			int maxWidth = 0;
			
			for(int i=0; i<validLayerStates.size(); i++) {
				
				if(validLayerStates.get(i).getWidthLayer() > maxWidth) {
					maxWidth = validLayerStates.get(i).getWidthLayer();
				}
				
				for(int j=0; j<validLayerStates.size(); j++) {
					
					LayerState7 bottom = validLayerStates.get(j);
					LayerState7 top = validLayerStates.get(i);
					

					//TODO: copy/paste code
					int currentBottomLayerWidth = bottom.getWidthLayer();
					int currentTopLayerWidth = top.getWidthLayer();
					
					int curCellValue = 0;
					
					for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
						LayerState7 result = LayerState7.addLayerStateOnTopOfLayerState(bottom, top, sideBump);
						
						if(result != null && top.equals(result)) {
							curCellValue++;
						}
						
					}
					
					
					if(curCellValue > 0) {
						output.println(varName + "[" + i + "][" + j + "] = " + curCellValue);

						if(numNonZeroCells % 1000 == 0) {
							output.flush();
						}
						numNonZeroCells++;
						
						sumOfAllEntries+= curCellValue;
					}
				}
				
			}
			
			System.out.println("Number of non-zero cells: " + numNonZeroCells);
			System.out.println("Sum of all cell in matrix: " + sumOfAllEntries);
			System.out.println("Max width layer for perimeter: " + maxWidth);
			
			output.println("# Number of non-zero cells: " + numNonZeroCells);
			output.println("# Sum of all cell in matrix: " + sumOfAllEntries);
			output.println("# Max width layer for perimeter: " + maxWidth);
			
			
			output.println();
			output.println("print(str(" + varName + "))");
	
			output.println("a = np.array(" + varName + ")");
			output.println("w,v=eig(a)");
			output.println("print('E-value:', w)");
			output.println("print('E-vector', v)");
			output.println();
			
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printMatrix(int matrix[][]) {
		String SPACE = "    ";
		System.out.println("Matrix:");
		for(int i=0; i<matrix.length; i++) {
			for(int j=0; j<matrix[0].length; j++) {
				System.out.print(SPACE.substring(0, SPACE.length() - (matrix[i][j] + "").length()) + matrix[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static ArrayList <LayerState7> getValidLayerStates() {
		

		ArrayList<LayerState7> listLayerStates = new ArrayList<LayerState7>();
		HashSet<String> listLayerStatesHash = new HashSet<String>();
		
		
		LinkedList<LayerState7> layerStateQueue = new LinkedList<LayerState7>();
		

		//Start with the fully connected layer:
		LayerState7 currentBottomLayer = new LayerState7(PERIMETER, 0);
		
		listLayerStates.add(currentBottomLayer);
		listLayerStatesHash.add(currentBottomLayer.toString());
		layerStateQueue.add(currentBottomLayer);
		
		long numLayers = LayerState7.getUpperBoundPossibleLayers(PERIMETER);
		
		System.out.println("Get list of possible states:");
		
		while(layerStateQueue.isEmpty() == false) {
			
			//System.out.println("++++++++++++++++++");
			currentBottomLayer = layerStateQueue.poll();
			//System.out.println(currentBottomLayer);

			int currentBottomLayerWidth = currentBottomLayer.getWidthLayer();
			
			for(int i=0; i<numLayers; i++) {
				//System.out.println(i);
				
				LayerState7 stateWithoutConnections = new LayerState7(PERIMETER, i);
				
				if(stateWithoutConnections.isValid() == false) {
					continue;
				}
				int currentTopLayerWidth = stateWithoutConnections.getWidthLayer();


				for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
					
					LayerState7 layerAbove = LayerState7.addLayerStateOnTopOfLayerState(currentBottomLayer, stateWithoutConnections, sideBump);
					
					if(layerAbove != null) {
					
						int numNonRedundantConnectionsSoFar = LayerState7.getNumConnectedIslands(layerAbove.connections);
						
						if(layerAbove.getNumberOfIslands() % 2 == 0) {
							System.out.println("EVEN!");
							System.exit(1);
						}
						int halfNonRedundantConnectionsNeededNow = (layerAbove.getNumberOfIslands() - 1) / 2;
						
						if(numNonRedundantConnectionsSoFar > halfNonRedundantConnectionsNeededNow) {
							System.out.println("ERROR: too many connections!");
							System.out.println(layerAbove);
							System.exit(1);
						}
						
						if( numNonRedundantConnectionsSoFar == halfNonRedundantConnectionsNeededNow
								&& ! listLayerStatesHash.contains(layerAbove.toString())
								){
							debugNumCouldReachFromLayer0++;
							System.out.println("Add to list layer states");
							System.out.println(layerAbove);

							layerStateQueue.add(layerAbove);
							listLayerStates.add(layerAbove);
							listLayerStatesHash.add(layerAbove.toString());
							
						}
							
						
					}
				}
			}
		}

		System.out.println("Number of states that might be legal: " + listLayerStates.size());
		System.out.println("Or: " + listLayerStatesHash.size());
		System.out.println();
		

		System.out.println("Get list of valid states:");
		
		ArrayList<LayerState7> validLayerStates = new ArrayList<LayerState7>();
		HashSet<String> validLayerStatesHash = new HashSet<String>();

		validLayerStates.add(currentBottomLayer);
		validLayerStatesHash.add(currentBottomLayer.toString());
		
		//TODO: Maybe make this a hash set, so dups can be avoided? 
		ArrayList<Long> curRelevantLayerStateHoriNumbers = new ArrayList<Long>();
		ArrayList<Long> nextRelevantLayerStateHoriNumbers = new ArrayList<Long>();
		
		nextRelevantLayerStateHoriNumbers.add(currentBottomLayer.horiNumber);
		
		int debugNumLoops = 0;
		
		boolean progress = true;
		while(progress) {
			
			curRelevantLayerStateHoriNumbers = nextRelevantLayerStateHoriNumbers;
			nextRelevantLayerStateHoriNumbers = new ArrayList<Long>();
			
			progress = false;
			
			System.out.println("Start loop:");
			
			for(int i=0; i<listLayerStates.size(); i++) {
				LayerState7 current = listLayerStates.get(i);
				
				//System.out.println(current);
				
				if(validLayerStatesHash.contains(current.toString())) {
					continue;
				}
				
				if(curLayerStateCouldReachLegalLayerInSingleStep(current, validLayerStatesHash, curRelevantLayerStateHoriNumbers)) {

					validLayerStates.add(current);
					validLayerStatesHash.add(current.toString());
					progress = true;
					System.out.println("Add to valid layer states");
					System.out.println(current);
					System.out.println("horiNumber: " + current.horiNumber);
					
					nextRelevantLayerStateHoriNumbers.add(current.horiNumber);
				}
				
			}
			
			debugNumLoops++;
		}
		
		System.out.println("Number of check for valid layers loops: " + debugNumLoops);
		debugNumCouldReachLayer0_no = listLayerStates.size() - validLayerStates.size();
		debugNumCouldReachLayer0_yes = validLayerStates.size();
		
		return validLayerStates;
		
		
	}
	
	public static boolean curLayerStateCouldReachLegalLayerInSingleStep(LayerState7 bottomLayer, HashSet<String> validLayerStatesHash, ArrayList<Long> curRelevantLayerStateHoriNumbers) {
		
		boolean foundSomething = false;
			
		for(int i=0; i<curRelevantLayerStateHoriNumbers.size(); i++) {

			LayerState7 stateWithoutConnections = new LayerState7(PERIMETER, curRelevantLayerStateHoriNumbers.get(i));
			
			foundSomething = tryToAddLayerOnTopOfBottomLayerState(
					bottomLayer,
					validLayerStatesHash,
					stateWithoutConnections
				);

			if(foundSomething) {
				break;
			}
		}
		
		
		return foundSomething;
		
	}
	
	public static boolean tryToAddLayerOnTopOfBottomLayerState(
			LayerState7 bottomLayer,
			HashSet<String> validLayerStatesHash,
			LayerState7 stateWithoutConnections
		) {
		
		
		if(stateWithoutConnections.isValid() == false) {
			return false;
		}
		int currentBottomLayerWidth = bottomLayer.getWidthLayer();
		int currentTopLayerWidth = stateWithoutConnections.getWidthLayer();
		
		for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
			
			LayerState7 layerAbove = LayerState7.addLayerStateOnTopOfLayerState(bottomLayer, stateWithoutConnections, sideBump);
			
			if(layerAbove != null && validLayerStatesHash.contains(layerAbove.toString())) {

				int numNonRedundantConnectionsSoFar = LayerState6.getNumConnectedIslands(layerAbove.connections);
				
				if(layerAbove.getNumberOfIslands() % 2 == 0) {
					System.out.println("ERROR: even number of islands!");
					System.exit(1);
				}
				int halfNonRedundantConnectionsNeededNow = (layerAbove.getNumberOfIslands() - 1) / 2;
				
				if(numNonRedundantConnectionsSoFar > halfNonRedundantConnectionsNeededNow) {
					System.out.println("ERROR: too many connections!");
					System.out.println(layerAbove);
					System.exit(1);
				}
				
				if(numNonRedundantConnectionsSoFar == halfNonRedundantConnectionsNeededNow) {
					
					return true;
				}
				
			}
		}
		return false;
	}
	
	
	// Start at 1 because layer 0 could reach layer 0...
	public static int debugNumCouldReachFromLayer0 = 1;
	
	public static int debugNumCouldReachLayer0_yes = 0;
	public static int debugNumCouldReachLayer0_no = 0;
	
	
	public static int[][] createMatrix(ArrayList <LayerState7> validLayerStates) {
		
		int ret[][] = new int[validLayerStates.size()][validLayerStates.size()];
		
		
		LayerState7 states[] = new LayerState7[validLayerStates.size()];
		
		System.out.println("Number of states: " + states.length);
		System.out.println("debugNumCouldReachLayer0_calls: " + debugNumCouldReachFromLayer0);
		System.out.println("debugNumCouldReachLayer0_yes: " + debugNumCouldReachLayer0_yes);
		System.out.println("debugNumCouldReachLayer0_no: " + debugNumCouldReachLayer0_no);
		
		for(int i=0; i<states.length; i++) {
			for(int j=0; j<states.length; j++) {
				
				LayerState7 bottom = validLayerStates.get(j);
				LayerState7 top = validLayerStates.get(i);
				

				//TODO: copy/paste code
				int currentBottomLayerWidth = bottom.getWidthLayer();
				int currentTopLayerWidth = top.getWidthLayer();
				
				int curCellValue = 0;
				
				for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
					LayerState7 result = LayerState7.addLayerStateOnTopOfLayerState(bottom, top, sideBump);
					
					if(result != null && top.equals(result)) {
						curCellValue++;
					}
					
				}
				
				ret[i][j] = curCellValue;
			}
			
		}
		
		
		
		return ret;
		
	}
	
	public static String convertMatrixToPythonFormat(int matrix[][]) {
		String ret = "[";
		
		for(int i=0; i<matrix.length; i++) {
			
			ret += "[";
			
			for(int j=0; j<matrix[0].length; j++) {
				
				if(j<matrix[0].length - 1) {
					ret += " " + matrix[i][j] + ",";
				} else {
					ret += " " + matrix[i][j];
				}
			}
			
			if(i< matrix.length - 1) {
				ret += "],";
			} else {
				ret += "]";
			}
		}
		
		ret += "]";
		
		return ret;
	}
}

/*

//TODO: This needs to update because there was a bugfix...
Trying to find a pattern:
  top eigenvalue   growth rate per cell:
# 3: 5.44948974    -> 1.75975394...
# 4: 9.49562269    -> 1.755419274...
# 5: 16.51451906   -> 1.7521576781...
# 6: 28.7559620    -> 1.750335563...
# 7: 50.0597517    -> 1.748977002...
# 8: 87.09364577   -> 1.7478264563...

Does it tend down toward sqrt(3)?
#sqrt(3) =       1.73205080756887...


//Number of states: 1317
*/

/* P=1:
 * Number of states: 1
Number of non-zero cells: 1
Sum of all cell in matrix: 1
Max width layer for perimeter: 1
 */

/* P = 2:
 * Number of states: 1
Number of non-zero cells: 1
Sum of all cell in matrix: 3
Max width layer for perimeter: 2
 */


/* P = 3
Number of states: 3
Number of non-zero cells: 7
Sum of all cell in matrix: 11
Max width layer for perimeter: 5
*/
/* P = 4:
 * Number of states: 7
Number of non-zero cells: 31
Sum of all cell in matrix: 45
Max width layer for perimeter: 7

 */
/* P=5
 * Number of states: 20
Number of non-zero cells: 139
Sum of all cell in matrix: 195
Max width layer for perimeter: 13
 */

/* P =6:
 * Number of states: 56
Number of non-zero cells: 669
Sum of all cell in matrix: 881
Max width layer for perimeter: 16
 */
/*
 * P=7:
 * Number of states: 168
Number of non-zero cells: 3289
Sum of all cell in matrix: 4101
Max width layer for perimeter: 25

 */
/*
 * P = 8:
 * Number of states: 512
Number of non-zero cells: 16511
Sum of all cell in matrix: 19563
Max width layer for perimeter: 29

 */
/*
 * P=9:
 * 
Number of states: 1607
Number of non-zero cells: 83415
Sum of all cell in matrix: 95234
Max width layer for perimeter: 41
 */

/*
P=10:
Number of states: 5119
Number of non-zero cells: 426540
Sum of all cell in matrix: 471550
Max width layer for perimeter: 46

*/

/*
P=11
Number of states: 16557
Number of non-zero cells: 2192772
Sum of all cell in matrix: 2368312
*/