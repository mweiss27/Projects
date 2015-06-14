package com.shenzai.wrappers;

/**
 * Lighter-weight Point class with a more useful toString
 */
public class Point {

	public int x, y;
	public Point next;
	
	public Point() {
		this(0, 0); //Not really needed, but looks nice?
	}
	
	public Point(final int x, final int y) {
		this.x = x;
		this.y = y;
	}
	
	public void translate(final int dx, final int dy) {
		this.x += dx;
		this.y += dy;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public String toStringOnlyThis() {
		return "(" + this.x + ", " + this.y + ")";
	}
	
	@Override
	public String toString() {
		return this.toStringOnlyThis() + " -> " + (this.next == null ? "null" : "(" + this.next.x + ", " + this.next.y + ")");
	}
	
}
