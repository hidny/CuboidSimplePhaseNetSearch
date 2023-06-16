package Coord;

public class CoordWithRotationAndIndex {

	private int a;
	private int b;
	private int c;
	
	//0: none
	//1: clockwise 90
	//2: opposite dir
	//3: counter-clockwise 90 
	private int rot;
	
	private int index;
	
	public CoordWithRotationAndIndex(int a, int b, int c, int rot, int index) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.rot = rot;
		this.index = index;
	}
	
	public CoordWithRotationAndIndex(Coord c, int rot, int index) {
		this.a = c.a;
		this.b = c.b;
		this.c = c.c;
		this.rot = rot;
		this.index = index;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public int getC() {
		return c;
	}

	public int getRot() {
		return rot;
	}

	public int getIndex() {
		return index;
	}
	
}
