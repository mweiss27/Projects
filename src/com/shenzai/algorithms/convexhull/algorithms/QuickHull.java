package com.shenzai.algorithms.convexhull.algorithms;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.shenzai.io.Log;
import com.shenzai.wrappers.Point;

public class QuickHull extends CHAlgorithm {

	public QuickHull(final Point[] points) {
		super(points);
	}
	
	@Override
	public Point[] generateConvexHull() {
		final List<Point> hullPoints = new ArrayList<>(this.points.size());
		
		Point minX = null, maxX = null;

		for (final Point p : this.points) {
			if (minX == null || p.x < minX.x) {
				minX = p;
			}
			if (maxX == null || p.x > maxX.x) {
				maxX = p;
			}
		}

		List<Point> upper = new ArrayList<>(this.points.size() - 2);
		List<Point> lower = new ArrayList<>(this.points.size() - 2);

		// Initial partition before we start recursing
		Point[] endPoints = new Point[] { minX, maxX };
		CHUtil.partition(this.points, endPoints, lower);
		
		endPoints = new Point[] { maxX, minX };
		CHUtil.partition(this.points, endPoints, upper);
		
		hullPoints.add(minX);
		hullPoints.add(maxX);
		
		minX.next = maxX;
		maxX.next = minX;
		
		Log.info("Initial hull on lower");
		this.quickhull(new Point[] { minX, maxX }, hullPoints, lower);
		Log.info("Initial hull on upper");
		this.quickhull(new Point[] { maxX, minX }, hullPoints, upper);
		
		return hullPoints.toArray(new Point[hullPoints.size()]);
	}

	private void quickhull(final Point[] endPoints, final List<Point> hullPoints, final List<Point> includedPoints) {
		
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
			final double distanceFromLine = CHUtil.computeDistanceFromLine(p, endPoints);
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
			if (CHUtil.isPointWithinTriangle(includedPoints.get(i), new Point[] { endPoints[0], farthestPoint, endPoints[1] })) {
				includedPoints.remove(i);
			}
			else {
				i++;
			}
		}
		
		final List<Point> newIncludedPoints = new ArrayList<>(includedPoints.size());
		
		Point[] newEndPoints = new Point[] { endPoints[0], farthestPoint };
		CHUtil.partition(includedPoints, newEndPoints, newIncludedPoints);
		quickhull(newEndPoints, hullPoints, newIncludedPoints);
		
		newIncludedPoints.clear();
		
		newEndPoints = new Point[] { farthestPoint, endPoints[1] };
		CHUtil.partition(includedPoints, newEndPoints, newIncludedPoints);
		quickhull(newEndPoints, hullPoints, newIncludedPoints);
		
	}

}
