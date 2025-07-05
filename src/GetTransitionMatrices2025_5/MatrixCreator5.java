package GetTransitionMatrices2025_5;

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

//TODO: Fix bug! Make connections transitive!
//ODNE

//TODO: Stop loops before the end of the array.
//TODO: try to reduce left and right extreme further...

public class MatrixCreator5 {
	
	//TODO:
	
	//For finding the top eigenvalue, try using:
	//Try using: https://stackoverflow.com/questions/22507707/finding-largest-eigenvalue-in-sparse-matrix
	//" I use scipy.sparse.linalg.eigsh for symmetric sparse matrices passing which="LM":
	//eigvals, eigvecs = eigsh(A, k=10, which='LM', sigma=1.)""
	
	//https://docs.scipy.org/doc/scipy/reference/generated/scipy.sparse.linalg.eigsh.html
	
	//Perimeter 8 takes over an hour without a few optimizations...
	public static final int PERIMETER = 8;
	public static final int LEFT_EXTREME = 0 - PERIMETER + 1;
	//public static final int RIGHT_EXTREME = PERIMETER * PERIMETER + PERIMETER;
	
	public static void main(String args[]) {

		ArrayList <LayerState5> validLayerStates = getValidLayerStates();
		
		
		if(PERIMETER < 3) {
			int matrix[][] = createMatrix(validLayerStates);
			
			System.out.println("matrix width for perimter " + PERIMETER + ":" + matrix.length);
			printMatrix(matrix);
			
			System.out.println("Matrix to be used by python:");
			System.out.println(convertMatrixToPythonFormat(matrix));
		}
		printSparsePythonMatrix(validLayerStates);
	}
	
	public static void printSparsePythonMatrix(ArrayList <LayerState5> validLayerStates) {
		
		
		System.out.println("Printing sparse Python Matrix:");
		try {
			PrintWriter output = new PrintWriter("D:\\outputMatrixPerimeter" + PERIMETER + ".py");
		
			output.println("import numpy as np\r\n"
					+ "from numpy.linalg import eig");
			
			output.println();
			String varName = "tmpMatrix" + PERIMETER;
			
			output.println(varName + " = [[0 for x in range(" + validLayerStates.size() + ")] for y in range(" + validLayerStates.size() + ")]");
			
			System.out.println("Number of states: " + validLayerStates.size());
			int numNonZeroCells = 0;
			int sumOfAllEntries = 0;
			int maxWidth = 0;
			
			for(int i=0; i<validLayerStates.size(); i++) {
				
				if(validLayerStates.get(i).getWidthLayer() > maxWidth) {
					maxWidth = validLayerStates.get(i).getWidthLayer();
				}
				
				for(int j=0; j<validLayerStates.size(); j++) {
					
					LayerState5 bottom = validLayerStates.get(j);
					LayerState5 top = validLayerStates.get(i);
					

					//TODO: copy/paste code
					int currentBottomLayerWidth = bottom.getWidthLayer();
					int currentTopLayerWidth = top.getWidthLayer();
					
					int curCellValue = 0;
					
					for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
						LayerState5 result = LayerState5.addLayerStateOnTopOfLayerState(bottom, top, sideBump);
						
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
	
	public static ArrayList <LayerState5> getValidLayerStates() {
		
		//Hashtable <String, LayerState> validLayerStates = new Hashtable<String, LayerState>();

		ArrayList<LayerState5> validLayerStates = new ArrayList<LayerState5>();
		HashSet<LayerState5> validLayerStatesHash = new HashSet<LayerState5>();
		
		LinkedList<LayerState5> layerStateQueue = new LinkedList<LayerState5>();
		
		//Start with the fully connected layer:
		LayerState5 currentBottomLayer = new LayerState5(PERIMETER, 0);
		
		validLayerStates.add(currentBottomLayer);
		layerStateQueue.add(currentBottomLayer);
		couldTouchTopRef.put(currentBottomLayer.toString(), true);
		
		long numLayers = LayerState5.getUpperBoundPossibleLayers(PERIMETER);
		
		System.out.println("Start:");
		
		while(layerStateQueue.isEmpty() == false) {
			
			System.out.println("++++++++++++++++++");
			currentBottomLayer = layerStateQueue.poll();
			System.out.println(currentBottomLayer);

			int currentBottomLayerWidth = currentBottomLayer.getWidthLayer();
			
			for(int i=0; i<numLayers; i++) {
				//System.out.println(i);
				
				LayerState5 stateWithoutConnections = new LayerState5(PERIMETER, i);
				
				if(stateWithoutConnections.isValid() == false) {
					continue;
				}
				int currentTopLayerWidth = stateWithoutConnections.getWidthLayer();

				/*if(currentBottomLayer.toString().contains("###-#---#")
						&& currentBottomLayer.toString().contains("1 <--> 2")
						&& stateWithoutConnections.toString().contains("#---#-#-#---#----")) {
					System.out.println("DEBUG");
				}*/

				for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
					//System.out.println(currentBottomLayerWidth + ", " + currentTopLayerWidth + ", " + sideBump);
					//	
					//if(currentTopLayerWidth == 7 && sideBump == -3) {
					//	System.out.println("DEBUG");
					//}
					LayerState5 layerAbove = LayerState5.addLayerStateOnTopOfLayerState(currentBottomLayer, stateWithoutConnections, sideBump);
					
					if(layerAbove != null) {
					
						int numNonRedundantConnectionsSoFar = LayerState5.getNumConnectedIslands(layerAbove.connections);
						
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
						
						/*if(currentBottomLayer.toString().contains("###-#---#")
								&& currentBottomLayer.toString().contains("1 <--> 2")
								&& stateWithoutConnections.toString().contains("#---#-#-#---#----")
								) {
							System.out.println("DEBUG2 with sideBump " + sideBump);
							System.out.println(layerAbove);
							
							if(sideBump == 4 || sideBump == -4) {
								System.out.println("DEBUG 3");
								LayerState layerAbove2 = LayerState.addLayerStateOnTopOfLayerState(currentBottomLayer, stateWithoutConnections, sideBump);
								
							}
						}*/
						
						if( numNonRedundantConnectionsSoFar == halfNonRedundantConnectionsNeededNow
								&& ! couldTouchTopRef.containsKey(layerAbove.toString())
								&& curLayerStateCouldReachLayer0(layerAbove, validLayerStatesHash)) {
							
							System.out.println("Could reach layer 0: (Number of states that reacn layer 0: " + (validLayerStates.size() + 1) + ")");
							validLayerStates.add(layerAbove);
							validLayerStatesHash.add(layerAbove);
	
							System.out.println(sideBump + ":");
							System.out.println(layerAbove);
							System.out.println();
							
							
							layerStateQueue.add(layerAbove);
						}
						
					}
				}
			}
		}
		
		return validLayerStates;
		
		
	}
	
	//TODO: clean up the code!
	public static Hashtable <String, Boolean> couldTouchTopRef = new Hashtable<String, Boolean>();
	
	public static boolean curLayerStateCouldReachLayer0(LayerState5 cur, HashSet<LayerState5> validLayerStatesHash) {
		
		if(couldTouchTopRef.containsKey(cur.toString())) {
			return couldTouchTopRef.get(cur.toString());
		}
		
		
		//System.out.println("Checking:  " + cur);
		LayerState5 goal = new LayerState5(PERIMETER, 0);
		
		LinkedList<LayerState5> layerStateQueue = new LinkedList<LayerState5>();
		layerStateQueue.add(cur);
		
		Hashtable <String, LayerState5> touchedLayerStates = new Hashtable<String, LayerState5>();
		touchedLayerStates.put(cur.toString(), cur);
		
		Hashtable <String, String> topToBottomRecords = new Hashtable<String, String>();
		
		
		long numLayers = LayerState5.getUpperBoundPossibleLayers(PERIMETER);
		
		while( ! layerStateQueue.isEmpty()) {
			
			LayerState5 bottomLayer = layerStateQueue.poll();
			//System.out.println("POLL");
			//System.out.println(bottomLayer);
			
			boolean foundSomething;
			boolean searchTheGoalList = true;
			/*for(int i=0; i<numLayers; i++) {

				LayerState5 stateWithoutConnections = new LayerState5(PERIMETER, i);
				
				foundSomething = searchForLayer(
						bottomLayer,
						validLayerStatesHash,
						topToBottomRecords,
						touchedLayerStates,
						layerStateQueue,
						stateWithoutConnections,
						searchTheGoalList
					);
				
				if(foundSomething) {
					return true;
				}
			}*/
			
			searchTheGoalList = false;
			for(int i=0; i<numLayers; i++) {

				LayerState5 stateWithoutConnections = new LayerState5(PERIMETER, i);
				
				foundSomething = searchForLayer(
						bottomLayer,
						validLayerStatesHash,
						topToBottomRecords,
						touchedLayerStates,
						layerStateQueue,
						stateWithoutConnections,
						searchTheGoalList
					);

				if(foundSomething) {
					return true;
				}
			}
		}
		
		Enumeration<String> badStates = touchedLayerStates.keys();
		
		while(badStates.hasMoreElements()) {

			String curBadState = badStates.nextElement();
			couldTouchTopRef.put(curBadState, false);
		}
		
		return false;
	}
	
	
	public static boolean searchForLayer(
			LayerState5 bottomLayer,
			HashSet<LayerState5> validLayerStatesHash,
			Hashtable <String, String> topToBottomRecords,
			Hashtable <String, LayerState5> touchedLayerStates,
			LinkedList<LayerState5> layerStateQueue,
			LayerState5 stateWithoutConnections,
			boolean searchTheGoalList
		) {
		
		if(stateWithoutConnections.isValid() == false) {
			return false;
		}
		//TODO: copy/paste code
		int currentBottomLayerWidth = bottomLayer.getWidthLayer();
		int currentTopLayerWidth = stateWithoutConnections.getWidthLayer();
		
		for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
			
			LayerState5 layerAbove = LayerState5.addLayerStateOnTopOfLayerState(bottomLayer, stateWithoutConnections, sideBump);
			
			if(layerAbove != null) {
				
				int numNonRedundantConnectionsSoFar = LayerState5.getNumConnectedIslands(layerAbove.connections);
				
				if(searchTheGoalList && ! validLayerStatesHash.contains(layerAbove)) {
					return false;
				} else if( ! searchTheGoalList && validLayerStatesHash.contains(layerAbove)) {
					return false;
				}
				
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
				
					if(couldTouchTopRef.containsKey(layerAbove.toString())) {
						
						if(couldTouchTopRef.get(layerAbove.toString()) == true) {
							
							LayerState5 curMemoize = bottomLayer;
							
							if(! couldTouchTopRef.contains(curMemoize.toString())) {
								couldTouchTopRef.put(curMemoize.toString(), true);
								//System.out.println("Adding: " + curMemoize.toString());
							}
							
							while(topToBottomRecords.containsKey(curMemoize.toString())) {
								curMemoize = touchedLayerStates.get(topToBottomRecords.get(curMemoize.toString()));
								if(! couldTouchTopRef.contains(curMemoize.toString())) {
									couldTouchTopRef.put(curMemoize.toString(), true);
									//System.out.println("Adding: " + curMemoize.toString());
								}
							}

							System.out.println("Hello1");
							return true;

						} else {
							//pass
						}
					
					} else if( ! touchedLayerStates.containsKey(layerAbove.toString())) {
						
						touchedLayerStates.put(layerAbove.toString(), layerAbove);
						layerStateQueue.add(layerAbove);

						topToBottomRecords.put(layerAbove.toString(), bottomLayer.toString());
						//System.out.println("Adding top to bottom record");
						//System.out.println(layerAbove.toString());
						//System.out.println(layerBelow.toString());
						
					}
				}
			} else {
				//System.out.println("Nope");
			}
		}
		
		return false;
	
	}
	
	public static int[][] createMatrix(ArrayList <LayerState5> validLayerStates) {
		
		int ret[][] = new int[validLayerStates.size()][validLayerStates.size()];
		
		
		LayerState5 states[] = new LayerState5[validLayerStates.size()];
		
		System.out.println("Number of states: " + states.length);
		
		for(int i=0; i<states.length; i++) {
			for(int j=0; j<states.length; j++) {
				
				LayerState5 bottom = validLayerStates.get(j);
				LayerState5 top = validLayerStates.get(i);
				

				//TODO: copy/paste code
				int currentBottomLayerWidth = bottom.getWidthLayer();
				int currentTopLayerWidth = top.getWidthLayer();
				
				int curCellValue = 0;
				
				for(int sideBump=LEFT_EXTREME; currentTopLayerWidth + sideBump < currentBottomLayerWidth + PERIMETER; sideBump++) {
					LayerState5 result = LayerState5.addLayerStateOnTopOfLayerState(bottom, top, sideBump);
					
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