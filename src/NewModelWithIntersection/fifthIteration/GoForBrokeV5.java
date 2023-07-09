package NewModelWithIntersection.fifthIteration;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;

import DupRemover.BasicUniqueCheckImproved;
import Model.Utils;

public class GoForBrokeV5 {

	public static void main(String[] args) {
		
		int check46[][] = new int[][]{{5, 3, 1}, {7, 2, 1}};
		goForBroke(check46);
		

		int check54[][] = new int[][]{{3, 3, 3}, {6, 3, 1}};
		goForBroke(check54);
		
		int check58[][] = new int[][]{{5, 4, 1}, {9, 2, 1}};
		goForBroke(check58);
		

		//Nothing found
		int check62[][] = new int[][]{{5, 3, 2}, {7, 3, 1}};
		goForBroke(check62);


		//Nothing found
		int check70[][] = new int[][]{{5, 5, 1}, {8, 3, 1}, {11, 2, 1}};
		goForBroke(check70);
		
		

		//Nothing found
		int check78[][] = new int[][]{{5, 3, 3}, {7, 4, 1}, {9, 3, 1}};
		goForBroke(check78);
		

		//Nothing found
		int check82[][] = new int[][]{{6, 5, 1}, {7, 3, 2}, {13, 2, 1}};
		goForBroke(check82);
		
		// 6,3,3: Found 58891 unique solution.
		//No triple :(
		//int check90[][] = new int[][]{{5, 5, 2}, {6, 3, 3}};
		//goForBroke(check90);
		
		
		//15,2,1 112 diff solutions and 28 unique solutions.
		// 11, 3, 1: 15 unique solutions
		//int check94[][] = new int[][]{{5, 4, 3}, {11, 3, 1}, {15, 2, 1}, {7, 5, 1}};
		//goForBroke(check94);
	}
	
	public static void goForBroke(int cuboids[][]) {
		for(int i=1; i<cuboids.length; i++) {
			if(Utils.getTotalArea(cuboids[0]) != Utils.getTotalArea(cuboids[i])) {
				System.out.println("Error: the area of the two cuboids don't match.");
				System.exit(1);
			}
		}

		BasicUniqueCheckImproved.resetUniqList();
		
		ReallySimpleIntersectFinder5.reallySimpleSearch(cuboids[0][0], cuboids[0][1], cuboids[0][2]);
		//ReallySimpleIntersectFinder.reallySimpleSearch(2, 1, 1);
		HashSet<BigInteger> origList = BasicUniqueCheckImproved.uniqList;
		
		for(int i=1; i<cuboids.length; i++) {
			BasicUniqueCheckImproved.resetUniqList();
			
			ReallySimpleIntersectFinder5.reallySimpleSearch(cuboids[i][0], cuboids[i][1], cuboids[i][2]);
			//ReallySimpleIntersectFinder.reallySimpleSearch(2, 1, 1);
			
			Iterator<BigInteger> it = BasicUniqueCheckImproved.uniqList.iterator();
			
			System.out.println("Area of cuboids checked: " + Utils.getTotalArea(cuboids[0]));
			
			System.out.println("Looking for Solution that folds into 3 cuboids:");
			boolean found = false;
			while(it.hasNext()) {
				BigInteger curSolution = it.next();
				if(origList.contains(curSolution)) {
					System.out.println("Found net that folds 3 cuboids!");
					System.out.println("Solution code: " + curSolution);
					found = true;
				}
			}
			if(found == false) {
				System.out.println("(Nothing found)");
			} else {
				System.exit(1);
			}
			
			if(i + 1 <cuboids.length) {
				HashSet<BigInteger> newList = BasicUniqueCheckImproved.uniqList;
				
				origList.addAll(newList);
			}
			
		}
	}

}
