package old;

import Model.Utils;

public class testFormula {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		boolean done = false;
		
		for(int h=1; h<20; h++) {
			for(int k1=1; k1<20; k1++) {
				for(int k2=1; k2<20; k2++) {
					for(int k3=1; k3<20; k3++) {
					
						int cuboid1[] = new int[] {(2*k1+1) * (2*k2+1) * (2*k3+1) * (h + 1) - 1 ,                                 2  - 1 , 1};
						int cuboid2[] = new int[] {           (2*k2+1) * (2*k3+1) * (h + 1) - 1 ,                       2 * (2*k1+1) - 1 , 1};
						int cuboid3[] = new int[] {                      (2*k3+1) * (h + 1) - 1 ,          2   * (2*k1+1) * (2*k2+1) - 1 , 1};
						int cuboid4[] = new int[] {                                 (h + 1) - 1 , 2 * (2*k1+1) * (2*k2+1) * (2*k3+1) - 1 , 1};
						
						int area1 = Utils.getTotalArea(cuboid1);
						int area2 = Utils.getTotalArea(cuboid2);
						int area3 = Utils.getTotalArea(cuboid3);
						int area4 = Utils.getTotalArea(cuboid4);
						
						if(area1 != area2) {
							System.out.println("oops! if 1");
							done = true;
						}
						if(area1 != area3) {
							System.out.println("oops! if 2");
							done = true;
													
						}
						if(area1 != area4) {
							System.out.println("oops! if 3");
							done = true;
							
						}
						
						if(done) {
							System.out.println("Constants:");
							System.out.println(h);
							System.out.println(k1);
							System.out.println(k2);
							System.out.println(k3);
							
							System.out.println("Areas:");
							System.out.println(area1);
							System.out.println(area2);
							System.out.println(area3);
							System.out.println(area4);
							System.out.println("Done");
							System.exit(1);
						}
					}
				}
			}
		}
	}

}
