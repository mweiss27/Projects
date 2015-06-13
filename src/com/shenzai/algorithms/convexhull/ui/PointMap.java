package com.shenzai.algorithms.convexhull.ui;

import java.awt.Dimension;
import java.awt.Point;
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
		
		int x, y;
		for (int i = 0; i < this.points.length; i++) {
			x = rand.nextInt(dimension.width);
			y = rand.nextInt(dimension.height);
			
			this.points[i] = new Point(x, y);
		}
	}
	
	public void findSolution() {
		
	}
	
	public Point[] getPoints() {
		return this.points;
	}
	
	public Point[] getSolution() {
		return this.solution;
	}
}
