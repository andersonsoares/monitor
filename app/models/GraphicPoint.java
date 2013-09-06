package models;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class GraphicPoint {

	private long x;
	
	private long y;
	
	public GraphicPoint() {	
	}
	
	public GraphicPoint(long x, long y) {
		this.x = x; 
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}
	
	
}
