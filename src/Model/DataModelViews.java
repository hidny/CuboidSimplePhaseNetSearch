package Model;


public class DataModelViews {

	//Goal:
	// Functions to display relevant view to the console

	public static String getFlatNumberingView(int array[]) {
		return getFlatNumberingView(array[0], array[1], array[2]);
	}	
	
	public static String getFlatNumberingView(int a, int b, int c) {
		boolean emptyBool[] = new boolean[Utils.getTotalArea(a, b, c)];
		
		for(int i=0; i<emptyBool.length; i++) {
			emptyBool[i] = false;
		}
		
		return getFlatNumberingView(a, b, c, emptyBool);
		
	}
	
	//TODO:
	// Instead of a bool, maybe record which direction is going up on the folding paper
	// int [] instead of boolean (This shouldn't be too hard!)
	public static String getFlatNumberingView(int a, int b, int c, boolean addX[]) {
		
		int lengthSpace = (Utils.getTotalArea(a, b, c) + "").length();
		
		String SPACE = "";
		for(int i=0; i<lengthSpace; i++) {
			SPACE = SPACE + " ";
		}
		
		int numbering[][][] = Utils.getFlatNumberingOfCuboid(a, b, c);
		//1st row only has side 0:

		String ret ="";
		ret += "Map:\n";
		
		for(int i=0; i<c; i++) {
			
			for(int j=0; j < 2*b + 2 *c; j++) {

				if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
					ret += " " + SPACE + " ";
				}
				
				if(j >= c && j<c + b) {

					int num = numbering[0][i][j - c];
					ret += addCellNumberOrXs(num, addX[num], SPACE);
					
				} else {
					ret += " " + SPACE + " ";
				}
			}
			
			ret += "\n";
		}
		
		//2nd row has 4 sides:
		ret += "\n";

		for(int i=0; i<a; i++) {
			
			for(int j=0; j < 2*b + 2 *c; j++) {
				
				if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
					ret += " " + SPACE + " ";
				}
				if( j < c) {
					
					int num = numbering[1][i][j];

					ret += addCellNumberOrXs(num, addX[num], SPACE);
					
				} else if(j >= c && j<c + b) {
					
					int num = numbering[2][i][j - c];
					
					ret += addCellNumberOrXs(num, addX[num], SPACE);
					
				} else if(j >= c + b && j<2*c + b) {
					
					int num = numbering[3][i][j - c - b];
					
					ret += addCellNumberOrXs(num, addX[num], SPACE);
					
				} else if(j >= 2*c + b && j<2*c + 2*b) {
					
					int num = numbering[4][i][j - 2*c - b];
					
					ret += addCellNumberOrXs(num, addX[num], SPACE);
				} else {
					System.out.println("Doh!");
					System.exit(1);
				}
			}
			
			ret += "\n";
		}
		

		//3rd row has 1 side:
		ret += "\n";
		
		for(int i=0; i<c; i++) {
			
			for(int j=0; j < 2*b + 2 *c; j++) {

				if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
					ret += " " + SPACE + " ";
				}
				
				if(j >= c && j<c + b) {

					int num = numbering[5][i][j - c];
					ret += addCellNumberOrXs(num, false, SPACE);
					
				} else {
					ret += " " + SPACE + " ";
				}
			}
			
			ret += "\n";
		}
		
		//Bonus 4th row:
		ret += "\n";
		
		for(int i=0; i<a; i++) {
			
			for(int j=0; j < 2*b + 2 *c; j++) {

				if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
					ret += " " + SPACE + " ";
				}
				
				if(j >= c && j<c + b) {

					//Mind bender: It gets flipped!
					// because it's a 3D cuboid or something!
					int num = numbering[4][(a-1) - (i)][(b-1) - (j - c)];
					ret += addCellNumberOrXs(num, false, SPACE);
					
				} else {
					ret += " " + SPACE + " ";
				}
			}
			
			ret += "\n";
		}
				
		
		return ret;
		
	}
	
	private static String addCellNumberOrXs(int num, boolean addX, String Space) {
		
		String X = "";
		for(int i=0; i<Space.length(); i++) {
			X = X + "X";
		}
		
		if(addX) {
			//Add X to mark that the spot has been filled... if you want.
			return " " + X + " ";
		} else {
			return " " + Space.substring(((num) + "").length()) + num + " ";
		}
	}
	
	
	
	//New view for the Simple Phase Net Search:
	//TODO: Actually label the indexes...
	
		public static String getFlatNumberingView(int a, int b, int c, String labels[]) {
			
			int lengthSpace = (Utils.getTotalArea(a, b, c) + "").length();
			
			String SPACE = "";
			for(int i=0; i<lengthSpace; i++) {
				SPACE = SPACE + " ";
			}
			
			int numbering[][][] = Utils.getFlatNumberingOfCuboid(a, b, c);
			//1st row only has side 0:

			String ret ="";
			ret += "Map:\n";
			
			for(int i=0; i<c; i++) {
				
				for(int j=0; j < 2*b + 2 *c; j++) {

					if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
						ret += " " + SPACE + " ";
					}
					
					if(j >= c && j<c + b) {

						int num = numbering[0][i][j - c];
						ret += addCellNumberOrLabel(num, labels[num], SPACE);
						
					} else {
						ret += " " + SPACE + " ";
					}
				}
				
				ret += "\n";
			}
			
			//2nd row has 4 sides:
			ret += "\n";

			for(int i=0; i<a; i++) {
				
				for(int j=0; j < 2*b + 2 *c; j++) {
					
					if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
						ret += " " + SPACE + " ";
					}
					if( j < c) {
						
						int num = numbering[1][i][j];

						ret += addCellNumberOrLabel(num, labels[num], SPACE);
						
					} else if(j >= c && j<c + b) {
						
						int num = numbering[2][i][j - c];
						
						ret += addCellNumberOrLabel(num, labels[num], SPACE);
						
					} else if(j >= c + b && j<2*c + b) {
						
						int num = numbering[3][i][j - c - b];
						
						ret += addCellNumberOrLabel(num, labels[num], SPACE);
						
					} else if(j >= 2*c + b && j<2*c + 2*b) {
						
						int num = numbering[4][i][j - 2*c - b];
						
						ret += addCellNumberOrLabel(num, labels[num], SPACE);
					} else {
						System.out.println("Doh!");
						System.exit(1);
					}
				}
				
				ret += "\n";
			}
			

			//3rd row has 1 side:
			ret += "\n";
			
			for(int i=0; i<c; i++) {
				
				for(int j=0; j < 2*b + 2 *c; j++) {

					if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
						ret += " " + SPACE + " ";
					}
					
					if(j >= c && j<c + b) {

						int num = numbering[5][i][j - c];
						ret += addCellNumberOrLabel(num, labels[num], SPACE);
						
					} else {
						ret += " " + SPACE + " ";
					}
				}
				
				ret += "\n";
			}
			
			//Bonus 4th row:
			ret += "\n";
			
			for(int i=0; i<a; i++) {
				
				for(int j=0; j < 2*b + 2 *c; j++) {

					if( j == c || j == c + b || j == 2*c + b || j== 2*c + 2*b) {
						ret += " " + SPACE + " ";
					}
					
					if(j >= c && j<c + b) {

						//Mind bender: It gets flipped!
						// because it's a 3D cuboid or something!
						int num = numbering[4][(a-1) - (i)][(b-1) - (j - c)];
						ret += addCellNumberOrLabel(num, labels[num], SPACE);
						
					} else {
						ret += " " + SPACE + " ";
					}
				}
				
				ret += "\n";
			}
					
			
			
			return ret;
			
		}
		
		private static String addCellNumberOrLabel(int num, String label, String Space) {
			
			String X = "";
			for(int i=0; i<Space.length(); i++) {
				X = X + "X";
			}
			
			if(label != null) {
				if(label.length() <= Space.length()) {
					return " " + Space.substring(((label) + "").length()) + label + " ";
				} else {
					return " " + label.substring(0, Space.length()) + " ";
				}
			} else {
				return " " + Space.substring(((num) + "").length()) + num + " ";
			}
		}
}
