package NewModelWithIntersection.fourPlusGrains;

import Model.Utils;

public class Search1Net4CuboidsMain {

	public static void main(String[] args) {

		search();

	}
	
	public static void search() {
		
		int it = 0;
		for(int sum = 0; sum < 10; sum++) {
			for(int a = 1; a< sum; a++) {
				for(int b=1; a + b < sum; b++) {
					
					searchTuple(a, b, sum - a - b);
					it++;	
				}
			}
		}
		System.out.println("Num iterations: "+ it);
		
	}
	
	public static void searchTuple(int a, int b, int c) {
		
		System.out.println(a + "," + b + "," + c);

		
		int oddA = 2*a + 1;
		int oddB = 2*b + 1;
		int oddC = 2*c + 1;
		
		int width0 = 1;
		int width1 = 2 * oddA - 1;
		int width2 = (width1 + 1) * oddB - 1;
		int width3 = (width2 + 1) * oddC - 1;
		
		System.out.println("Widths:");
		System.out.println(width0 + "," + width1 + "," + width2 + "," + width3);
		

		//TODO: I'll need to change the data model to accomodate this: 
		// int h3 = 2 * oddA * oddB * oddC;
		
		//For now, hope for luck
		//Testing:
		//int h3 = 20;
		
		//Actual:
		int h3 = 2 * width3;

		int h2 = (h3 + 1) * oddC - 1;
		int h1 = (h2 + 1) * oddB - 1;
		int h0 = (h1 + 1) * oddA - 1;
		
		System.out.println("Heights:");
		System.out.println(h0 + "," + h1 + "," + h2 + "," + h3);
		

		System.out.println("----------");
		
		//TODO: test area...
		
		int cuboid1[] = new int[] {h0 , width0, 1};
		int cuboid2[] = new int[] {h1 , width1, 1};
		int cuboid3[] = new int[] {h2 , width2, 1};
		int cuboid4[] = new int[] {h3 , width3, 1};
		
		sanityTestCuboids(cuboid1, cuboid2, cuboid3, cuboid4);
		
		//searchTupleInner(cuboid1, cuboid2, cuboid3, cuboid4);
		
		//TODO: just call IntersectFor4GrainedCuboidsdFinder
		//IntersectFor4GrainedCuboidsdFinder.findIntersectFor4GrainedCuboidsFinder(cuboid1, cuboid2, cuboid3, cuboid3);
		IntersectFor4GrainedCuboidsdFinder.findIntersectFor4GrainedCuboidsFinder(cuboid1, cuboid2, cuboid3, cuboid4);
		
		//System.exit(1);
	}
	
	
	public static void sanityTestCuboids(int cuboid1[], int cuboid2[], int cuboid3[], int cuboid4[]) {
		int area1 = Utils.getTotalArea(cuboid1);
		int area2 = Utils.getTotalArea(cuboid2);
		int area3 = Utils.getTotalArea(cuboid3);
		int area4 = Utils.getTotalArea(cuboid4);

		if(area1 != area2) {
			System.out.println("Error sanityTestCuboids failed condition 1");
			System.exit(1);
		}
		if(area1 != area3) {
			System.out.println("Error sanityTestCuboids failed condition 2");
			System.exit(1);
									
		}
		if(area1 != area4) {
			System.out.println("Error sanityTestCuboids failed condition 3");
			System.exit(1);
			
		}
	}

}