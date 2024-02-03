package GetTransitionMatrices2;

import java.util.Hashtable;
import java.util.LinkedList;

public class MatrixCreator {

	public static final int PERIMETER = 6;
	public static final int LEFT_EXTREME = 0 - PERIMETER * PERIMETER - PERIMETER;
	public static final int RIGHT_EXTREME = PERIMETER * PERIMETER + PERIMETER;
	
	public static void main(String args[]) {

		Hashtable <String, LayerState> checkedLayerStates = new Hashtable<String, LayerState>();
		Hashtable <String, LayerState> validLayerStates = new Hashtable<String, LayerState>();
		
		LinkedList<LayerState> layerStateQueue = new LinkedList<LayerState>();
		
		LayerState root = new LayerState(PERIMETER, 0);
		
		layerStateQueue.add(root);
		validLayerStates.put(root.toString(), root);
		
		long numLayers = LayerState.getUpperBoundPossibleLayers(PERIMETER);
		
		System.out.println("Start:");
		
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
						
						if( ! checkedLayerStates.containsKey(layerAbove.toString())
								&& curLayerStateCouldReachLayer0(layerAbove)) {
							
							System.out.println("Could reach layer 0:");
							validLayerStates.put(layerAbove.toString(), layerAbove);
							layerStateQueue.add(layerAbove);

							System.out.println(sideBump + ":");
							System.out.println(layerAbove);
							System.out.println();
						}
						
						checkedLayerStates.put(layerAbove.toString(), layerAbove);
					}
				}
			}
		}
	}
	
	
	public static boolean curLayerStateCouldReachLayer0(LayerState cur) {
		
		//System.out.println("Checking:  " + cur);
		LayerState goal = new LayerState(PERIMETER, 0);
		
		LinkedList<LayerState> layerStateQueue = new LinkedList<LayerState>();
		layerStateQueue.add(cur);
		
		Hashtable <String, LayerState> touchedLayerStates = new Hashtable<String, LayerState>();
		touchedLayerStates.put(cur.toString(), cur);
		
		
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

						if(layerAbove.equals(goal)) {
							return true;
							
						} else if( ! touchedLayerStates.containsKey(layerAbove.toString())) {
							
							touchedLayerStates.put(layerAbove.toString(), layerAbove);
							layerStateQueue.add(layerAbove);

						}
					}
				}
			}
		}
		
		return false;
	}
}
