package NewModelWithIntersection.firstIteration;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;

import DupRemover.BasicUniqueCheckImproved;
import Model.Utils;

public class GoForBroke {

	public static void main(String[] args) {
		
		//int check46[][] = new int[][]{{5, 3, 1}, {7, 2, 1}};
		//goForBroke(check46);
		

		//int check54[][] = new int[][]{{3, 3, 3}, {6, 3, 1}};
		//goForBroke(check54);
		
		//int check58[][] = new int[][]{{5, 4, 1}, {9, 2, 1}};
		//goForBroke(check58);
		

		//Nothing found
		//int check62[][] = new int[][]{{5, 3, 2}, {7, 3, 1}};
		//goForBroke(check62);

		
		int check70_1[][] = new int[][]{{5, 5, 1}, {8, 3, 1}};
		goForBroke(check70_1);
		
		//int check70_2[][] = new int[][]{{5, 5, 1}, {11, 2, 1}};
		//goForBroke(check70_2);
		
		//int check70_3[][] = new int[][]{{8, 3, 1}, {11, 2, 1}};
		//goForBroke(check70_3);
	}
	
	public static void goForBroke(int cuboids[][]) {
		if(Utils.getTotalArea(cuboids[0]) != Utils.getTotalArea(cuboids[1])) {
			System.out.println("Error: the area of the two cuboids don't match.");
			System.exit(1);
		}
		
		ReallySimpleIntersectFinder.reallySimpleSearch(cuboids[0][0], cuboids[0][1], cuboids[0][2]);
		//ReallySimpleIntersectFinder.reallySimpleSearch(2, 1, 1);
		HashSet<BigInteger> origList = BasicUniqueCheckImproved.uniqList;
		
		BasicUniqueCheckImproved.resetUniqList();
		
		ReallySimpleIntersectFinder.reallySimpleSearch(cuboids[1][0], cuboids[1][1], cuboids[1][2]);
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
		}
	}

}
