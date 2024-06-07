package OutputReader;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

import Model.Utils;

public class CheckForDeviousNets {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String file1 = "D:\\output5x1x1_holes_only.txt";
			String file2 = "D:\\output5x1x1_and_3x2x1_holes_only.txt";
			String file3 = "D:\\output3x2x1_holes_only.txt";
			
			Scanner in = new Scanner(new File(file1));
			
			HashSet<String> file1Solutions = new HashSet<String>();
			
			while(in.hasNextLine()) {
				String tmp = in.nextLine();
				
				//System.out.println(tmp);
				if(tmp.toLowerCase().contains("solution code:")) {
					file1Solutions.add(getSolutionCode(tmp));
					System.out.println(getSolutionCode(tmp));
				}
			}
			
			in.close();
			

			System.out.println("File 2:");
			
			int numNormalMatches = 0;
			in = new Scanner(new File(file2));
			
			while(in.hasNextLine()) {
				String tmp = in.nextLine();
				
				if(tmp.toLowerCase().contains("solution code:")) {
					if(file1Solutions.contains(getSolutionCode(tmp))) {
						System.out.println("Found normal match!");
						file1Solutions.remove(getSolutionCode(tmp));
						numNormalMatches++;
					}
				}
			}
			
			in.close();
			
			System.out.println("Num numNormalMatches: " + numNormalMatches);
			System.out.println("Num in file1 hashset: " + file1Solutions.size());
			
			System.out.println("File 3:");
			
			int numMatches = 0;
			in = new Scanner(new File(file3));
			
			while(in.hasNextLine()) {
				String tmp = in.nextLine();
				
				if(tmp.toLowerCase().contains("solution code:")) {
					if(file1Solutions.contains(getSolutionCode(tmp))) {
						System.out.println("Found devious match!");
						System.out.println(getSolutionCode(tmp));
						numMatches++;
						
						Utils.printFold(CheckSolutionsCodesAgainstOtherCuboid.convertSolutionCodeToTable(getSolutionCode(tmp)));
					}
				}
			}
			
			in.close();
			
			System.out.println("Num devious matches: " + numMatches);
			System.out.println("Num in file1 hashset: " + file1Solutions.size());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getSolutionCode(String line) {
		return line.split(" ")[line.split(" ").length - 1];
	}
}

/*
 * File 3:
Found devious match!
55774838424205914224
....#...|
....#..#|
########|
#..##..#|
####....|
.###....|

Found devious match!
14296803684634090306568
......#.|
......##|
.###.##.|
.#.####.|
##...#..|
.#####..|
....#...|

Found devious match!
111693216326184736312
......#|
...####|
...####|
####...|
##.#...|
.#.##..|
.###...|

Found devious match!
6799622435481464
....###|
.#####.|
######.|
#..###.|
####...|

Found devious match!
111693216326183760952
......#|
...####|
...####|
####...|
.#.##..|
##.#...|
.###...|

Found devious match!
111693220724230272056
.....#.|
...####|
...####|
####...|
.#.##..|
##.#...|
.###...|

Found devious match!
111693225087917044792
.....##|
...###.|
...####|
####...|
.#.##..|
##.#...|
.###...|

Found devious match!
111693225053557306424
.....##|
...##.#|
...####|
####...|
.#.##..|
##.#...|
.###...|

Found devious match!
14296805119112659914864
.....###|
...###..|
...###..|
####....|
.#.##...|
##.#....|
.###....|

Found devious match!
14296805949159360483440
....#.#.|
....####|
....#..#|
..######|
...#.#..|
##.#....|
.###....|

Found devious match!
14296805120194991673456
.....###|
...###.#|
...##...|
####....|
.#.##...|
##.#....|
.###....|

Found devious match!
1830000203132692530962544
.....#.#.|
.....####|
.....#..#|
....#####|
....#.#..|
###.#....|
..###....|

Found devious match!
14296805120164926902384
.....###|
...###.#|
...#...#|
####....|
.#.##...|
##.#....|
.###....|

Found devious match!
111693229520323286136
....#..|
...####|
...####|
####...|
.#.##..|
.#.#...|
####...|

Found devious match!
228456025869590387917024
.....#....|
.....###..|
.#####.###|
.#..#...#.|
###.#.....|
..###.....|

Found devious match!
6799622169143160
....##.|
.######|
######.|
#..###.|
####...|

Found devious match!
6799622433384312
....###|
.####.#|
######.|
#..###.|
####...|

Found devious match!
217589015299792112
....###.|
.####.##|
#####...|
#..###..|
####....|

Found devious match!
6962882717419551856
.....####|
....##..#|
....#####|
###.####.|
..###....|

Found devious match!
6962882715339177072
.....####|
.....#..#|
#...#####|
###.####.|
..###....|

Found devious match!
1830000148737375186181176
......###|
......#.#|
......#.#|
......###|
...#...#.|
####.###.|
...###...|

Found devious match!
14296805102517288627768
.....###|
....##.#|
.....#.#|
.....###|
..#...#.|
###.###.|
..###...|

Found devious match!
111693220445450839928
.....#.|
....###|
......#|
.##..##|
####.#.|
#..###.|
####...|

Found devious match!
871477555134113656
....###|
......#|
.###.##|
####.#.|
#..###.|
####...|

Found devious match!
55774831845328371473
......#.|
....###.|
..###.##|
####...#|
..######|
...#...#|

Found devious match!
6962882717448911984
.....####|
....##..#|
.#...####|
###.####.|
..###....|

Found devious match!
217589015299333360
....###.|
.####.##|
####...#|
#..###..|
####....|

Found devious match!
6962882715368537200
.....####|
.....#..#|
##...####|
###.####.|
..###....|

Found devious match!
1830000148737375178841144
......###|
......#.#|
......#.#|
......###|
......##.|
####.###.|
...###...|

Found devious match!
14296805102517286792760
.....###|
....##.#|
.....#.#|
.....###|
.....##.|
###.###.|
..###...|

Found devious match!
111693220308011951992
.....#.|
.....##|
......#|
.##..##|
######.|
#..###.|
####...|

Found devious match!
871477417695225720
.....##|
......#|
.###.##|
######.|
#..###.|
####...|

Found devious match!
6962883614699682914
....###..|
...##.##.|
.#.#...##|
########.|
..##...#.|

Found devious match!
6962883614833638498
....###..|
...##.###|
.#.#...#.|
########.|
..##...#.|

Found devious match!
6799621900707704
....#.#|
.######|
######.|
#..###.|
####...|

Found devious match!
871477348984137592
......#|
......#|
.######|
######.|
#..###.|
####...|

Num devious matches: 36
Num in file1 hashset: 46792
*/
