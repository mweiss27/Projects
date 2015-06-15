package com.weiss.algorithms.convexhull.ui;

import java.awt.Dimension;
import java.util.Random;

import com.weiss.algorithms.convexhull.algorithms.CHAlgorithm;
import com.weiss.io.Log;
import com.weiss.wrappers.Point;

public class PointMap {

	private Point[] points;

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
		int x, y;
		for (int i = 0; i < this.points.length; i++) {
			x = 30 + rand.nextInt(dimension.width - 60); //add a margin on each side
			y = 30 + rand.nextInt(dimension.height - 60);

			this.points[i] = new Point(x, y);
		}
	}

	public Point[] generateSolution(final Class<? extends CHAlgorithm> type) {
		if (this.points.length == 3) {
		}
			CHAlgorithm alg;
			try {
				alg = (CHAlgorithm) type.getDeclaredConstructor(Point[].class).newInstance((Object) this.points);
				return alg.generateConvexHull();
			} catch (Exception e) {
				e.printStackTrace();
			}
		Log.err("Returning empty Point[]");
		return new Point[] { };
	}
	
	public Point[] getPoints() {
		return this.points;
	}

}
