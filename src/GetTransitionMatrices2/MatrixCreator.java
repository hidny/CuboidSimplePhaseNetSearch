package GetTransitionMatrices2;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

public class MatrixCreator {

	public static final int PERIMETER = 6;
	public static final int LEFT_EXTREME = 0 - PERIMETER * PERIMETER - PERIMETER;
	public static final int RIGHT_EXTREME = PERIMETER * PERIMETER + PERIMETER;
	
	public static void main(String args[]) {

		Hashtable <String, LayerState> validLayerStates = new Hashtable<String, LayerState>();
		LinkedList<LayerState> layerStateQueue = new LinkedList<LayerState>();
		
		//Start with the fully connected layer:
		LayerState currentBottomLayer = new LayerState(PERIMETER, 0);
		
		validLayerStates.put(currentBottomLayer.toString(), currentBottomLayer);
		layerStateQueue.add(currentBottomLayer);
		
		long numLayers = LayerState.getUpperBoundPossibleLayers(PERIMETER);
		
		System.out.println("Start:");
		
		while(layerStateQueue.isEmpty() == false) {
			
			System.out.println("++++++++++++++++++");
			currentBottomLayer = layerStateQueue.poll();
			System.out.println(currentBottomLayer);

			for(int i=0; i<numLayers; i++) {
				System.out.println(i);
				
				LayerState stateWithoutConnections = new LayerState(PERIMETER, i);
				
				if(stateWithoutConnections.isValid() == false) {
					continue;
				}

				/*if(currentBottomLayer.toString().contains("###-#---#")
						&& currentBottomLayer.toString().contains("1 <--> 2")
						&& stateWithoutConnections.toString().contains("#---#-#-#---#----")) {
					System.out.println("DEBUG");
				}*/

				for(int sideBump=LEFT_EXTREME; sideBump<=RIGHT_EXTREME; sideBump++) {
					
						
					LayerState layerAbove = LayerState.addLayerStateOnTopOfLayerState(currentBottomLayer, stateWithoutConnections, sideBump);
					
					if(layerAbove != null) {
						
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
						
						if( ! couldTouchTopRef.containsKey(layerAbove.toString())
								&& curLayerStateCouldReachLayer0(layerAbove)) {
							
							System.out.println("Could reach layer 0:");
							validLayerStates.put(layerAbove.toString(), layerAbove);
	
							System.out.println(sideBump + ":");
							System.out.println(layerAbove);
							System.out.println();
							
							layerStateQueue.add(layerAbove);
						}
						
					}
				}
			}
		}
	}
	
	public static Hashtable <String, Boolean> couldTouchTopRef = new Hashtable<String, Boolean>();
	
	public static boolean curLayerStateCouldReachLayer0(LayerState cur) {
		
		if(couldTouchTopRef.containsKey(cur.toString())) {
			return couldTouchTopRef.get(cur.toString());
		}
		
		
		//System.out.println("Checking:  " + cur);
		LayerState goal = new LayerState(PERIMETER, 0);
		
		LinkedList<LayerState> layerStateQueue = new LinkedList<LayerState>();
		layerStateQueue.add(cur);
		
		Hashtable <String, LayerState> touchedLayerStates = new Hashtable<String, LayerState>();
		touchedLayerStates.put(cur.toString(), cur);
		
		Hashtable <String, String> topToBottomRecords = new Hashtable<String, String>();
		
		
		long numLayers = LayerState.getUpperBoundPossibleLayers(PERIMETER);
		
		while( ! layerStateQueue.isEmpty()) {
			
			LayerState bottomLayer = layerStateQueue.poll();
			
			for(int i=0; i<numLayers; i++) {
				LayerState stateWithoutConnections = new LayerState(PERIMETER, i);
				
				if(stateWithoutConnections.isValid() == false) {
					continue;
				}
				
				for(int sideBump=LEFT_EXTREME; sideBump<=RIGHT_EXTREME; sideBump++) {
					
					LayerState layerAbove = LayerState.addLayerStateOnTopOfLayerState(bottomLayer, stateWithoutConnections, sideBump);
					
					if(layerAbove != null) {
						if(couldTouchTopRef.containsKey(layerAbove.toString())) {
							
							if(couldTouchTopRef.get(layerAbove.toString()) == true) {
								

								//TODO: copy/paste code 1
								LayerState curMemoize = bottomLayer;
								
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
								//END TODO: copy/paste code 1
							} else {
								//pass
							}
						
						} else if(layerAbove.equals(goal)) {
							
							//TODO: copy/paste code 1
							LayerState curMemoize = bottomLayer;
							
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

							System.out.println("Hello2");
							//END TODO: copy/paste code 1
							return true;
							
						} else if( ! touchedLayerStates.containsKey(layerAbove.toString())) {
							
							touchedLayerStates.put(layerAbove.toString(), layerAbove);
							layerStateQueue.add(layerAbove);

							topToBottomRecords.put(layerAbove.toString(), bottomLayer.toString());
							//System.out.println("Adding top to bottom record");
							//System.out.println(layerAbove.toString());
							//System.out.println(layerBelow.toString());
							
						}
					}
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
}
