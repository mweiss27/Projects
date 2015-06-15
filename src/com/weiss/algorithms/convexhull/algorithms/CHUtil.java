package com.weiss.algorithms.convexhull.algorithms;

import java.util.List;

import com.weiss.wrappers.Point;

public class CHUtil {

	/**
	 * Filters all points that are "below" (in the Swing co-ord sense), so physically higher on the screen
	 * We're checking for a negative cross product
	 * 
	 * x -> 	y
	 * 			|
	 * 			v
	 * 
	 */
	public static void partition(final List<Point> allPoints, final Point[] endPoints, 
			final List<Point> result) {
		final Point p1Vector = new Point(endPoints[1].x - endPoints[0].x, endPoints[1].y - endPoints[0].y);
		final Point p2Vector = new Point();
		for (final Point p : allPoints) {
			if (!p.equals(endPoints[0]) && !p.equals(endPoints[1])) {
				p2Vector.x = p.x - endPoints[0].x;
				p2Vector.y = p.y - endPoints[0].y;
				final int cross = cross(p1Vector, p2Vector);
				if (cross <= 0) {
					result.add(p);
				}
			}
		}
	}

	public static double computeDistanceFromLine(final Point p, final Point[] endPoints) {
		if (endPoints.length != 2) {
			throw new IllegalArgumentException("endPoints.length must equal 2");
		}
	
		final Point v = new Point(endPoints[1].x - endPoints[0].x, endPoints[1].y - endPoints[0].y);
		final Point u = new Point(p.x - endPoints[0].x, p.y - endPoints[0].y);
		
		//project u onto v
		final double scalar = ((double) dot(v, u)) / dot(v, v);
		v.x *= scalar;
		v.y *= scalar;
		
		return distanceBetween(u, v);
	}

	/**
	 * @return The z component of the result that signifies the direction of the resulting vector.
	 */
	public static int cross(final Point v1, final Point v2) {
		final int[] _v1 = new int[] { v1.x, v1.y, 0 };
		final int[] _v2 = new int[] { v2.x, v2.y, 0 };
	
		// the z component of the resulting cross vector
		int k = (_v1[0] * _v2[1]) - (_v1[1] * _v2[0]);
	
		return k;
	}

	public static int dot(final Point v1, final Point v2) {
		return (v1.x * v2.x) + (v1.y * v2.y);
	}

	public static double distanceBetween(final Point p1, final Point p2) {
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}

	/**
	 * Check the cross of each edge vector with the point. Positive? It's contained
	 */
	public static boolean isPointWithinTriangle(final Point p, final Point[] vertices) {
		if (vertices.length != 3) {
			throw new IllegalArgumentException("verticies must be length 3");
		}
		
		boolean result = check(vertices[0], vertices[1], vertices[2], p);
		result = result && check(vertices[1], vertices[2], vertices[0], p);
		result = result && check(vertices[2], vertices[0], vertices[1], p);
		
		return result;
	}

	private static boolean check(final Point a, final Point b, final Point c, final Point p) {
		final Point vector1 = new Point(b.x - a.x, b.y - a.y);
		final Point vector2 = new Point(c.x - a.x, c.y - a.y);
		final Point _p = new Point(p.x - a.x, p.y - a.y);
		
		return Math.signum(cross(vector1, vector2)) == Math.signum(cross(vector1, _p));
	}
	
}
