package semiGrained.iteration3;

import java.util.ArrayList;

// VM arguments set:
// -Xss5m
import Coord.Coord2D;
import DupRemover.BasicUniqueCheckImproved;
import GraphUtils.PivotCellDescription;
import GraphUtils.PivotCellDescriptionForSemiGrainedDepth3;
import Model.Utils;
import NewModel.firstIteration.Nx1x1CuboidToFold;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;

public class ReallySimpleIntersectFinderSemiGrained3 {

	public static boolean VERBOSE = true;
	
	public static void main(String[] args) {
		
		
		reallySimpleSearch(1, 3, 3);
		//reallySimpleSearch(4, 15, 3);
		
		//reallySimpleSearch(5, 15, 3);
		//reallySimpleSearch(3, 7, 3);
		//reallySimpleSearch(40, 51, 3);
		
		//reallySimpleSearch(80, 87, 3);
		
		//Found 0 unique solution.
		//Done for 1x3x3
		
		//Found 31 unique solution.
		//Done for 2x3x3
		
		//Found N/A unique solution.
		//Done for 3x3x3
		
		//Found 1231 unique solution.
		//Done for 4x3x3
		
		//Found 8399 unique solution.
		//Done for 5x3x3
		// (expected about 8,418)
		
		
		//Found 58799 unique solution.
		//Done for 6x3x3
		//Took 33 minutes
		//Now takes 3 minutes
		
		//Done using the 2nd iteration (using pre-computed long arrays)
		//Found 58799 unique solution.
		//Took 1 minute
		
		// Expected close to 58,891
		
		//Found 410031 unique solution.
		//Done for 7x3x3
		//Took 13 minutes
		//Now takes 6 minutes
		//(Expected about 410,329)
		
		//Found 2870223 unique solution.
		//Done for 8x3x3
		//Took 8 minutes with verbose off.
		//Took 30 minuts after optimizations... Maybe close chrome next time?
		//(Expected about 2,870,327)
		
		
		/*
		 * Found 2988 different solutions if we ignore symmetric solutions

Done using the 2nd iteration (using pre-computed long arrays)
Found 1113 unique solution.
Done for 4x7x3
25 minutes
Update: less than 30 seconds

Update: only 18 solutions... doh
and 3.5 minutes
		 */
		
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 231 unique solution.
Done for 4x15x3

Took: 23.5 hours!

231 means there might be 3-way solutions, but I haven't confirmed...

TODO: faster
		 */
		
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 231 unique solution.
Done for 4x15x3
Done in 3 hours and 16 minutes

All are valid 3-way solutions
		 */
		
		/*
		 * 
		reallySimpleSearch(5, 15, 3);
		After 3 days and 7 hours
		 * Num unique solutions found: 2092
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 2092 unique solution.
Done for 5x15x3

		 */
		
		/*
		 * After optimization:
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 231 unique solution.
Done for 4x15x3
Took 80 minutes
		 */
		
		/*
		 * Found 12 unique solution.
Done for 3x15x3
almost 8 minutes
		 */
		
		/* After optimizations:
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 231 unique solution.
Done for 4x15x3

Took 8 minutes...
		 */
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 2092 unique solution.
Done for 5x15x3
3 hours and 12 minutes
		 */
		
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 231 unique solution.
Done for 4x15x3

3 minutes and 5 seconds
		 */
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 2092 unique solution.
Done for 5x15x3
53 minutes.
		 */
		
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 2092 unique solution.
Done for 5x15x3

under 9 minutes...
		 */
		
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 231 unique solution.
Done for 4x51x3

After 8 hours and 15 minutes. (I think I could do better!)
		 */
		/*
		 * Done using the 2nd iteration (using pre-computed long arrays)
Found 3716 unique solution.
Done for 5x51x3
50 minutes
		 */
	}
	
	public static SolutionResolverInterface solutionResolver;

	public static void reallySimpleSearch(int a, int b, int c) {
		
		BasicUniqueCheckImproved.resetUniqList();
		solutionResolver = new StandardResolverForSmallIntersectSolutions();
		
		
		CuboidToFoldOnSemiGrained3 cuboidToBuild = new CuboidToFoldOnSemiGrained3(a, b, c);
		
		if(cuboidToBuild.getNumCellsToFill() % 4 != 2) {
			System.out.println("ERROR: trying to find intersect between Nx1x1 solution and a cuboid solution that doesn't have a surface area that matches any Nx1x1 cuboid.");
			return;
		}
		
		int listOfPotentialTops[] = cuboidToBuild.getListOfPotentialTops();
		System.out.println("potential tops:");
		for(int j=0; j<listOfPotentialTops.length; j++) {
			System.out.println(listOfPotentialTops[j]);
			
		}
		//System.exit(1);

		int NofNx1x1Cuboid = getNumLayers(cuboidToBuild);

		Nx1x1CuboidToFold reference = new Nx1x1CuboidToFold(NofNx1x1Cuboid);

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescriptionForSemiGrainedDepth3.getUniqueRotationListsWithCellInfo(cuboidToBuild, cuboidToBuild.getIndexToRing());
		
		long ret = 0;
		
		System.out.println("Size Test: " + startingPointsAndRotationsToCheck.size());

		//293
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
			
			
			int otherCuboidStartIndex =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int otherCuboidStartRotation = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			//Only start from ring index 0 and rotation 2 for Mx(3+4M)x3
			if(cuboidToBuild.getIndexToRingIndex(otherCuboidStartIndex) != 0
					|| otherCuboidStartRotation != 2) {
				continue;
			}
			
			System.out.println("Start recursion for other cuboid start index and rotation: (" + otherCuboidStartIndex + ", " + otherCuboidStartRotation + ")");
			
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
			int perimeter = 2 * (b + c);
			
			for(int j=0; j<listOfPotentialTops.length; j++) {
				System.out.println("listOfPotentialTops: " + listOfPotentialTops[j]);
				for(int globalShift=0; globalShift<perimeter; globalShift++) {
				
					//I'm starting to think I don't need to reinit every time:
						//cuboidToBuild = new CuboidToFoldOnSemiGrained2(a, b, c, false, true);
					//System.out.println("top index set todo: " + listOfPotentialTops[j]);
					
					cuboidToBuild.initializeNewBottomAndTopIndexAndRotation(otherCuboidStartIndex, otherCuboidStartRotation, listOfPotentialTops[j], globalShift);
	
					//System.out.println("(listOfPotentialTops, globalShift) set to: (" + listOfPotentialTops[j] + ", " + globalShift + ")");;
					ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild);
				}
			}
			
			
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + otherCuboidStartIndex + " and a rotation index of " + otherCuboidStartRotation +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		System.out.println("Done");
		System.out.println("Found " + ret + " different solutions if we ignore symmetric solutions");
		System.out.println();
		System.out.println("Done using the 2nd iteration (using pre-computed long arrays)");
		System.out.println("Found " + BasicUniqueCheckImproved.uniqList.size() + " unique solution.");

		System.out.println("Done for " + a + "x" + b + "x" + c);
	}
	
	public static int getNumLayers(CuboidToFoldOnSemiGrained3 cuboidToBuild) {
		return (cuboidToBuild.getNumCellsToFill() - 2) / 4;
	}
	
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnSemiGrained3 cuboidToBuild) {
		return findReallySimpleSolutionsRecursion(reference, cuboidToBuild, 0, getNumLayers(cuboidToBuild));
	}

	public static long debugIt = 0;
	public static long findReallySimpleSolutionsRecursion(Nx1x1CuboidToFold reference, CuboidToFoldOnSemiGrained3 cuboidToBuild, int layerIndex, int numLayers) {

		debugIt++;

		if(debugIt % 1000000000L == 0) {
			System.out.println("Debug print current state of search:");
			cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();
		}

		long ret = 0;
		
		//cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();

		if(layerIndex == numLayers) {
			
			if(cuboidToBuild.isTopCellAbleToBeAddedFast()) {

				for(int sideBump=6; sideBump <10; sideBump++) {
					if(cuboidToBuild.isTopCellAbleToBeAddedForSideBumpFast(sideBump)) {
						ret++;
						
						reference.addNextLevel(new Coord2D(0, sideBump), null);
						if(BasicUniqueCheckImproved.isUnique(Utils.getOppositeCornersOfNet(reference.setupBoolArrayNet()), reference.setupBoolArrayNet()) ){
							
							
							if(VERBOSE) {
								
								System.out.println("Unique solution found");
								System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
								
								System.out.println("Global Shift: " + cuboidToBuild.globalShift);
								System.out.println("topIndex: " + cuboidToBuild.topIndex);
								
								cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();

								System.out.println(reference.toString());
								System.out.println("Solution code: " + BasicUniqueCheckImproved.debugLastScore);
								
								//TODO:
								//System.out.println("Debugging transition handler for top and bottom:");
								//cuboidToBuild.printTopAndBottomHandlerDebug();
								
							} else {
								if(BasicUniqueCheckImproved.uniqList.size() % 10000 == 0) {
									System.out.println("Num unique solutions found: " + BasicUniqueCheckImproved.uniqList.size());
									
								}
							}

							

							int topIndexCell = -1;
							for(int j=0; j<cuboidToBuild.getNumCellsToFill(); j++) {
								if ( ! cuboidToBuild.isCellIndexoccupied(j)) {
									System.out.println("Top index/cell unoccupied: " + j);
									topIndexCell = j;
								}
							}

							//SANITY TEST
							if(cuboidToBuild.debugFalseIndex >= 0 && topIndexCell == cuboidToBuild.getTopIndexAssumed()) {
								
								System.out.println("Net in question:");
								cuboidToBuild.printCurrentStateOnOtherCuboidsFlatMap();

								System.out.println(reference.toString());
								
								System.out.println("ERROR: DEBUG false index: " + cuboidToBuild.debugFalseIndex);
								System.out.println("ERROR: DEBUG cuboid index that broke: " + cuboidToBuild.debugFalseCuboidIndex);
								System.out.println("ERROR: DEBUG cuboid rotation that broke: " + cuboidToBuild.debugFalseCuboidRot);
								
								System.out.println("top index set: " + cuboidToBuild.getTopIndexAssumed());
								
								//for(int i=0; i<cuboidToBuild.debugTopShiftIndex.length; i++) {
								//	System.out.println("topShiftIndex at layer " + i + ": " + cuboidToBuild.debugTopShiftIndex[i]);
								//}
								for(int i=0; i<cuboidToBuild.debugBottomShiftIndex.length; i++) {
									System.out.println("BottomShiftIndex at layer " + i + ": " + cuboidToBuild.debugTopShiftIndex[i]);
								}
								
								/*System.out.println("Debug allowed transitions ring 0 to top:");
								for(int i=0; i<cuboidToBuild.getNumCellsToFill(); i++) {
									
									if(cuboidToBuild.setup1stAndLastRing.ring0ToTopTransitions[cuboidToBuild.setup1stAndLastRing.getTopShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i] != -1) {
										System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ring0ToTopTransitions[cuboidToBuild.setup1stAndLastRing.getTopShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i]);
										
									}
									
								}*/
								
								//TODO: maybe cache this result if possible... nah...
								//System.out.println("top/bottom type: " + cuboidToBuild.setup1stAndLastRing.getTopShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound));
								
								cuboidToBuild.setup1stAndLastRing.DEBUG = true;
								/*cuboidToBuild.setup1stAndLastRing.setupRing0AndTopTransitions(
										new Coord2D(cuboidToBuild.getBottomIndex(), 2),
										cuboidToBuild.debugRing0ToMinus1_1,
										cuboidToBuild.debugRing0ToMinus1_2,
										cuboidToBuild,
										cuboidToBuild.topBottomShiftIndexLeftMost);
								*/
								//TODO TEST
								
								/*System.out.println("Debug allowed transitions ring 0 to top again:");
								for(int i=0; i<cuboidToBuild.getNumCellsToFill(); i++) {
									
									if(cuboidToBuild.setup1stAndLastRing.ring0ToTopTransitions[cuboidToBuild.setup1stAndLastRing.getTopShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i] != -1) {
										System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ring0ToTopTransitions[cuboidToBuild.setup1stAndLastRing.getTopShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i]);
										
									}
									
								}*/
								
								/*
								System.out.println("Debug allowed transitions ring second last to last:");
								for(int i=0; i<cuboidToBuild.getNumCellsToFill(); i++) {
									
									if( cuboidToBuild.getIndexToRing()[i] ==   cuboidToBuild.getDimensions()[0] - 2
	                                                                  && cuboidToBuild.setup1stAndLastRing.ringSecondLastToLastRingTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i] != -1) {
										System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ringSecondLastToLastRingTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i]);
										
									}
									
								}
								
								System.out.println("");
								System.out.println("Transition list 2:");
								for(int i=0; i<cuboidToBuild.getNumCellsToFill(); i++) {
									
									if( cuboidToBuild.getIndexToRing()[i] ==   cuboidToBuild.getDimensions()[0] - 1
	                                                                  && cuboidToBuild.setup1stAndLastRing.ringSecondLastToLastRingTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i] != -1) {
										System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ringSecondLastToLastRingTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i]);
										
									}
									
								}
								*/
								
								
								System.out.println("Bottom transition used: " + cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound));
								
								System.out.println("Debug allowed transitions ring last to bottom:");
								for(int k=0; k<8; k++) {
									System.out.println("Index k: " + k);
									for(int i=0; i<cuboidToBuild.getNumCellsToFill(); i++) {
										
										/*if( cuboidToBuild.getIndexToRing()[i] ==   cuboidToBuild.getDimensions()[0] - 1
		                                                                  && cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i] != -1) {
											System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i]);
											
										}*/
										if( cuboidToBuild.getIndexToRing()[i] ==   cuboidToBuild.getDimensions()[0] - 1
	                                            && cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[k][i] != -1) {
											System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[k][i]);
											
										}
										
									}
								}
								
								System.out.println("");
								System.out.println("Transition list bottom and ring last:");

								for(int k=0; k<8; k++) {
									System.out.println("Index k: " + k);
									for(int i=0; i<cuboidToBuild.getNumCellsToFill(); i++) {
										
										/*if( cuboidToBuild.getIndexToRing()[i] ==  -1
		                                                                  && cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i] != -1) {
											System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[cuboidToBuild.setup1stAndLastRing.getBottomShiftType(cuboidToBuild.topBottomShiftMod4FromPrevRound)][i]);
											
										}*/
										if( cuboidToBuild.getIndexToRing()[i] ==  -1
	                                            && cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[k][i] != -1) {
											System.out.println(i + " --> " + cuboidToBuild.setup1stAndLastRing.ringLastToBottomTransitions[k][i]);
											
										}
																
									}
								}
								
								System.out.println("debug");
								//System.exit(1);
								
							}

							//END SANITY TEST
							
						}
						

						
						reference.removeCurrentTopLevel();
					}
				}
				
				if(ret > 0 && VERBOSE) {
					System.out.println("----");
					System.out.println("Found " + ret + " places for top from this net:");
					
					//TODO: Make a debug function:
					//cuboidToBuild.debugPrintCuboidOnFlatPaperAndValidateIt(reference);
					System.out.println("----");
				}
			}
			
			return ret;
		}
		int debugNumBranches = 0;
		for(int sideBump=3; sideBump <10; sideBump++) {
			if(layerIndex == 0 && sideBump > 6) {
				//TODO: make it faster by only starting recursion on the next layer...
				// I'm too lazy to do that for now.
				break;
			}
			
			if(cuboidToBuild.isNewLayerValidSimpleFast(sideBump)) {
				debugNumBranches++;
				cuboidToBuild.addNewLayerFast(sideBump);
				reference.addNextLevel(new Coord2D(0, sideBump), null);

				ret += findReallySimpleSolutionsRecursion(reference, cuboidToBuild, layerIndex + 1, numLayers);
	
				cuboidToBuild.removePrevLayerFast();
				reference.removeCurrentTopLevel();
			}
		}
		
		//TODO DEBUG
		if(debugNumBranches > 1 && layerIndex > 2 * (cuboidToBuild.dimensions[0] + cuboidToBuild.dimensions[2])) {
			System.out.println("Layer index: " + layerIndex + " has " + debugNumBranches + " branches.");
			System.out.println(cuboidToBuild.topLeftGroundedIndex + ", " + cuboidToBuild.topLeftGroundRotationRelativeFlatMap);
			System.out.println("Index to ring: " + cuboidToBuild.getIndexToRing()[cuboidToBuild.topLeftGroundedIndex]);
			System.out.println();
			//DOH!
			//System.out.println(cuboidToBuild.)
			System.exit(1);
		}
		//DEBUG
		return ret;
	}
}
