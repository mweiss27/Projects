package com.shenzai.algorithms.convexhull.ui;

import java.awt.Dimension;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.shenzai.io.Log;
import com.shenzai.wrappers.Point;

public class PointMap {

	private Point[] points;
	private Point[] solution;
	//public Point[] endPoints;

	public PointMap(final int size, final Dimension dimension) {
		this.setNumPoints(size);
		this.generateRandomPointMap(dimension);
	}

	public void setNumPoints(final int numPoints) {
		this.points = new Point[numPoints];
	}

	public void generateRandomPointMap(final Dimension dimension) {
		Log.info("\n -- NEW HULL --\n");
		final Random rand = new Random();
		this.solution = null;
		//this.endPoints = null;
		int x, y;
		for (int i = 0; i < this.points.length; i++) {
			x = 30 + rand.nextInt(dimension.width - 60); //add a margin on each side
			y = 30 + rand.nextInt(dimension.height - 60);

			this.points[i] = new Point(x, y);
		}
	}

	public void generateSolution() {

		if (this.points.length == 3) {
			this.solution = this.points;
		}

		Point minX = null, maxX = null;

		for (final Point p : this.points) {
			if (minX == null || p.x < minX.x) {
				minX = p;
			}
			if (maxX == null || p.x > maxX.x) {
				maxX = p;
			}
		}
		

		List<Point> upper = new ArrayList<>(this.points.length - 2);
		List<Point> lower = new ArrayList<>(this.points.length - 2);

		Point[] endPoints = new Point[] { minX, maxX };
		partition(this.points, endPoints, lower);
		
		endPoints = new Point[] { maxX, minX };
		partition(this.points, endPoints, upper);
		
		
		final List<Point> hullPoints = new ArrayList<>(this.points.length);
		hullPoints.add(minX);
		hullPoints.add(maxX);
		
		Log.info("minX.next = maxX");
		minX.next = maxX;
		Log.info("maxX.next = minX");
		maxX.next = minX;
		
		
		Log.info("Initial hull on lower");
		hull(new Point[] { minX, maxX }, hullPoints, lower);
		Log.info("Initial hull on upper");
		hull(new Point[] { maxX, minX }, hullPoints, upper);
		
		this.solution = hullPoints.toArray(new Point[hullPoints.size()]);
	}
	
	/**
	 * Filters all points that are "below" (in the Swing co-ord sense), so physically higher on the screen
	 * We're checking for a negative cross product
	 * 
	 * x -> 	y
	 * 			|
	 * 			v
	 * 
	 */
	private static void partition(final Point[] allPoints, final Point[] endPoints, 
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

	private void hull(final Point[] endPoints, final List<Point> hullPoints, final List<Point> includedPoints) {
		
		if (includedPoints.size() == 0) {
			Log.err("includedPoints#size is 0");
			return;
		}
		
		Log.info("hull call");
		Log.info("\tEP: " + Arrays.asList(endPoints));
		Log.info("\tHP: " + hullPoints);
		Log.info("\tIP: " + includedPoints);
		
		Entry<Point, Double> farthest = null;
		for (final Point p : includedPoints) {
			final double distanceFromLine = computeDistanceFromLine(p, endPoints);
			if (farthest == null || distanceFromLine > farthest.getValue()) {
				farthest = new SimpleEntry<>(p, distanceFromLine);
			}
		}
		
		if (farthest == null) {
			throw new RuntimeException("farthest is null");
		}
		
		final Point farthestPoint = farthest.getKey();
		hullPoints.add(farthestPoint);
		includedPoints.remove(farthestPoint);

		endPoints[0].next = farthestPoint;
		farthestPoint.next = endPoints[1];
		
		Log.info("\tEP[0]: " + endPoints[0]);
		Log.info("\tFP: " + farthestPoint);
		Log.info("\tEP[1]: " + endPoints[1]);
		
		//Remove points within the new triangle
		for (int i = 0; i < includedPoints.size();) {
			if (isPointWithinTriangle(includedPoints.get(i), new Point[] { endPoints[0], farthestPoint, endPoints[1] })) {
				includedPoints.remove(i);
			}
			else {
				i++;
			}
		}
		
		final List<Point> newIncludedPoints = new ArrayList<>(includedPoints.size());
		
		Point[] newEndPoints = new Point[] { endPoints[0], farthestPoint };
		partition(includedPoints.toArray(new Point[includedPoints.size()]), newEndPoints, newIncludedPoints);
		hull(newEndPoints, hullPoints, newIncludedPoints);
		
		newIncludedPoints.clear();
		
		newEndPoints = new Point[] { farthestPoint, endPoints[1] };
		partition(includedPoints.toArray(new Point[includedPoints.size()]), newEndPoints, newIncludedPoints);
		hull(newEndPoints, hullPoints, newIncludedPoints);
		
	}

	private static double computeDistanceFromLine(final Point p, final Point[] endPoints) {
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
	private static int cross(final Point v1, final Point v2) {
		final int[] _v1 = new int[] { v1.x, v1.y, 0 };
		final int[] _v2 = new int[] { v2.x, v2.y, 0 };

		// the z component of the resulting cross vector
		int k = (_v1[0] * _v2[1]) - (_v1[1] * _v2[0]);

		return k;
	}

	private static int dot(final Point v1, final Point v2) {
		return (v1.x * v2.x) + (v1.y * v2.y);
	}
	
	private static double distanceBetween(final Point p1, final Point p2) {
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}
	
	/**
	 * Check the cross of each edge vector with the point. Positive? It's contained
	 */
	private static boolean isPointWithinTriangle(final Point p, final Point[] vertices) {
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

	public Point[] getPoints() {
		return this.points;
	}

	public Point[] getSolution() {
		return this.solution;
	}
}
