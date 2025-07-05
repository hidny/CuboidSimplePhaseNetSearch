package GetTransitionMatrices2025_6;

public class TestFirstPass {
	
	public static int PERIMETER = 11;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		long numLayers = LayerState6.getUpperBoundPossibleLayers(PERIMETER);
		
		int numValidStatesNoConnectionInfo = 0;
		
		for(int i=0; i<numLayers; i++) {
			//System.out.println(i);
			
			LayerState6 stateWithoutConnections = new LayerState6(PERIMETER, i);
			
			if(stateWithoutConnections.isValid() == false) {
				continue;
			}
			numValidStatesNoConnectionInfo++;
			
			if(i %1000 == 0) {
				System.out.println("Print random state:");
				System.out.println(stateWithoutConnections);
			}
		}
		
		System.out.println("numValidStatesNoConnectionInfo: " + numValidStatesNoConnectionInfo);
	}

}
