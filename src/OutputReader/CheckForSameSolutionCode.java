package OutputReader;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

public class CheckForSameSolutionCode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String file1 = "D:\\ouputGoForBrokeV5Area94First2CuboidsToUSE.txt";
			String file2 = "D:\\ouputGoForBrokeV5TestAfter.txt";
			
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
			
			int numMatches = 0;
			in = new Scanner(new File(file1));
			
			while(in.hasNextLine()) {
				String tmp = in.nextLine();
				
				if(tmp.toLowerCase().contains("solution code:")) {
					if(file1Solutions.contains(getSolutionCode(tmp))) {
						System.out.println("Found match!");
						numMatches++;
					}
				}
			}
			
			in.close();
			
			System.out.println("Num matches: " + numMatches);
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
