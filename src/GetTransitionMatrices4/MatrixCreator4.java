package GetTransitionMatrices4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

//TODO: Fix bug! Make connections transitive!

public class MatrixCreator4 {
	
	//TODO:
	
	//For finding the top eigenvalue, try using:
	//Try using: https://stackoverflow.com/questions/22507707/finding-largest-eigenvalue-in-sparse-matrix
	//" I use scipy.sparse.linalg.eigsh for symmetric sparse matrices passing which="LM":
	//eigvals, eigvecs = eigsh(A, k=10, which='LM', sigma=1.)""
	
	//https://docs.scipy.org/doc/scipy/reference/generated/scipy.sparse.linalg.eigsh.html
	
	//Perimeter 8 takes over an hour without a few optimizations...
	public static final int PERIMETER = 9;
	public static final int LEFT_EXTREME = 0 - PERIMETER * PERIMETER - PERIMETER;
	public static final int RIGHT_EXTREME = PERIMETER * PERIMETER + PERIMETER;
	
	public static void main(String args[]) {

		ArrayList <LayerState4> validLayerStates = getValidLayerStates();
		
		
		int matrix[][] = createMatrix(validLayerStates);
		
		System.out.println("matrix width for perimter " + PERIMETER + ":" + matrix.length);
		//printMatrix(matrix);
		
		//System.out.println("Matrix to be used by python:");
		//System.out.println(convertMatrixToPythonFormat(matrix));
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
	
	public static ArrayList <LayerState4> getValidLayerStates() {
		
		//Hashtable <String, LayerState> validLayerStates = new Hashtable<String, LayerState>();

		ArrayList<LayerState4> validLayerStates = new ArrayList<LayerState4>();
		HashSet<LayerState4> validLayerStatesHash = new HashSet<LayerState4>();
		
		LinkedList<LayerState4> layerStateQueue = new LinkedList<LayerState4>();
		
		//Start with the fully connected layer:
		LayerState4 currentBottomLayer = new LayerState4(PERIMETER, 0);
		
		validLayerStates.add(currentBottomLayer);
		layerStateQueue.add(currentBottomLayer);
		couldTouchTopRef.put(currentBottomLayer.toString(), true);
		
		long numLayers = LayerState4.getUpperBoundPossibleLayers(PERIMETER);
		
		System.out.println("Start:");
		
		while(layerStateQueue.isEmpty() == false) {
			
			System.out.println("++++++++++++++++++");
			currentBottomLayer = layerStateQueue.poll();
			System.out.println(currentBottomLayer);

			for(int i=0; i<numLayers; i++) {
				//System.out.println(i);
				
				LayerState4 stateWithoutConnections = new LayerState4(PERIMETER, i);
				
				if(stateWithoutConnections.isValid() == false) {
					continue;
				}

				/*if(currentBottomLayer.toString().contains("###-#---#")
						&& currentBottomLayer.toString().contains("1 <--> 2")
						&& stateWithoutConnections.toString().contains("#---#-#-#---#----")) {
					System.out.println("DEBUG");
				}*/

				for(int sideBump=LEFT_EXTREME; sideBump<=RIGHT_EXTREME; sideBump++) {
					
						
					LayerState4 layerAbove = LayerState4.addLayerStateOnTopOfLayerState(currentBottomLayer, stateWithoutConnections, sideBump);
					
					if(layerAbove != null) {
					
						int numNonRedundantConnectionsSoFar = LayerState4.getNumConnectedIslands(layerAbove.connections);
						
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
	
	public static boolean curLayerStateCouldReachLayer0(LayerState4 cur, HashSet<LayerState4> validLayerStatesHash) {
		
		if(couldTouchTopRef.containsKey(cur.toString())) {
			return couldTouchTopRef.get(cur.toString());
		}
		
		
		//System.out.println("Checking:  " + cur);
		LayerState4 goal = new LayerState4(PERIMETER, 0);
		
		LinkedList<LayerState4> layerStateQueue = new LinkedList<LayerState4>();
		layerStateQueue.add(cur);
		
		Hashtable <String, LayerState4> touchedLayerStates = new Hashtable<String, LayerState4>();
		touchedLayerStates.put(cur.toString(), cur);
		
		Hashtable <String, String> topToBottomRecords = new Hashtable<String, String>();
		
		
		long numLayers = LayerState4.getUpperBoundPossibleLayers(PERIMETER);
		
		while( ! layerStateQueue.isEmpty()) {
			
			LayerState4 bottomLayer = layerStateQueue.poll();
			//System.out.println("POLL");
			//System.out.println(bottomLayer);
			
			boolean foundSomething;
			boolean searchTheGoalList = true;
			for(int i=0; i<numLayers; i++) {

				LayerState4 stateWithoutConnections = new LayerState4(PERIMETER, i);
				
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
			
			searchTheGoalList = false;
			for(int i=0; i<numLayers; i++) {

				LayerState4 stateWithoutConnections = new LayerState4(PERIMETER, i);
				
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
			LayerState4 bottomLayer,
			HashSet<LayerState4> validLayerStatesHash,
			Hashtable <String, String> topToBottomRecords,
			Hashtable <String, LayerState4> touchedLayerStates,
			LinkedList<LayerState4> layerStateQueue,
			LayerState4 stateWithoutConnections,
			boolean searchTheGoalList
		) {
		
		if(stateWithoutConnections.isValid() == false) {
			return false;
		}
		
		for(int sideBump=LEFT_EXTREME; sideBump<=RIGHT_EXTREME; sideBump++) {
			
			LayerState4 layerAbove = LayerState4.addLayerStateOnTopOfLayerState(bottomLayer, stateWithoutConnections, sideBump);
			
			if(layerAbove != null) {
				
				int numNonRedundantConnectionsSoFar = LayerState4.getNumConnectedIslands(layerAbove.connections);
				
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
							
							LayerState4 curMemoize = bottomLayer;
							
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
	
	public static int[][] createMatrix(ArrayList <LayerState4> validLayerStates) {
		
		int ret[][] = new int[validLayerStates.size()][validLayerStates.size()];
		
		
		LayerState4 states[] = new LayerState4[validLayerStates.size()];
		
		System.out.println("Number of states: " + states.length);
		
		for(int i=0; i<states.length; i++) {
			for(int j=0; j<states.length; j++) {
				
				LayerState4 bottom = validLayerStates.get(j);
				LayerState4 top = validLayerStates.get(i);
				
				int curCellValue = 0;
				
				for(int sideBump=LEFT_EXTREME; sideBump<=RIGHT_EXTREME; sideBump++) {
					LayerState4 result = LayerState4.addLayerStateOnTopOfLayerState(bottom, top, sideBump);
					
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