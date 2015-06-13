package com.shenzai.algorithms.convexhull.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PointMap {

	private Point[] points;
	private Point[] solution;
	
	public PointMap(final int size, final Dimension dimension) {
		this.setNumPoints(size);
		this.generateRandomPointMap(dimension);
	}
	
	public void setNumPoints(final int numPoints) {
		this.points = new Point[numPoints];
	}
	
	public void generateRandomPointMap(final Dimension dimension) {
		final Random rand = new Random();
		this.solution = null;
		int x, y;
		for (int i = 0; i < this.points.length; i++) {
			x = 5 + rand.nextInt(dimension.width - 10); //add a margin of 5 on each side
			y = 5 + rand.nextInt(dimension.height - 10);
			
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
		
		int yOnLine;
		final double slope = ((double) (maxX.y - minX.y)) / (maxX.x - minX.x);
		for (final Point p : this.points) {
			if (!p.equals(minX) && !p.equals(maxX)) {
				yOnLine = (int) (minX.y + ((p.x - minX.x) * slope));
				if (p.y >= yOnLine) {
					upper.add(p);
				}
				else {
					lower.add(p);
				}
			}
		}
		hull(new Point[] { minX, maxX }, upper);
		hull(new Point[] { minX, maxX }, lower);
	}
	
	private void hull(final Point[] endPoints, final List<Point> includedPoints) {
		for (final Point p : includedPoints) {
			if (!p.equals(endPoints[0]) && !p.equals(endPoints[1])) {
				System.out.println(String.format("(" + p.x + ", " + p.y + ") is %.2f away from the line", 
						computeDistanceFromLine(p, endPoints)));
			}
		}
	}
	
	private double computeDistanceFromLine(final Point p, final Point[] endPoints) {
		if (endPoints.length != 2) {
			throw new IllegalArgumentException("endPoints.length must equal 2");
		}
		
		Point lineVector = new Point(endPoints[1].x - endPoints[0].x, endPoints[1].y - endPoints[0].y);
		//project p onto lineVector
		
		final double scalar = ((double) dot(lineVector, p)) / dot(lineVector, lineVector);
		lineVector.x *= scalar;
		lineVector.y *= scalar;
		return distanceBetween(p, lineVector);
	}
	
	private int dot(final Point v1, final Point v2) {
		return (v1.x * v2.x) + (v1.y * v2.y);
	}
	
	private double distanceBetween(final Point p1, final Point p2) {
		System.out.println("distanceBetween " + p1 + " and " + p2);
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) - Math.pow(p2.y - p1.y, 2));
	}
	
	public Point[] getPoints() {
		return this.points;
	}
	
	public Point[] getSolution() {
		return this.solution;
	}
}
