package com.weiss.algorithms.convexhull.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.weiss.io.Log;
import com.weiss.util.Time;
import com.weiss.wrappers.Point;

public class GiftWrap extends CHAlgorithm {

	public GiftWrap(final Point[] points) {
		super(points);
	}

	public Point[] generateConvexHull() {
		Time.tic();
		Log.info("Generating a GiftWrap convex hull");
		final List<Point> hullPoints = new ArrayList<>();

		final Point farthestLeft = this.points.stream().sorted(new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				return o1.x - o2.x;
			}
		}).findFirst().get();

		this.giftWrap(farthestLeft, hullPoints);

		final Point[] result = hullPoints.toArray(new Point[hullPoints.size()]);
		System.out.println("GiftWrap computed " + result.length + " convex points in " + Time.toc() + "ms.");
		return result;
	}

	private void giftWrap(final Point farthestLeft, final List<Point> hullPoints) {
		Log.info("giftWrap starting at " + farthestLeft + "\n\tIP: " + this.points);

		Point current = farthestLeft;
		Point toAdd = null;

		do {
			Log.info("Current: " + current.toStringOnlyThis());
			for (final Point p : this.points) {
				Log.info("\t" + p.toStringOnlyThis());
				if (p != current) {
					if (toAdd == null) {
						Log.info("\t\tNew toAdd");
						toAdd = p;
					}
					else {
						final Point pVector = new Point(toAdd.x - current.x, toAdd.y - current.y);
						final Point qVector = new Point(p.x - current.x, p.y - current.y);
						if (CHUtil.cross(pVector, qVector) <= 0) {
							Log.info("\t\tNew toAdd");
							toAdd = p;
						}
					}
				}
			}

			hullPoints.add(toAdd);
			current.next = toAdd;
			current = toAdd;

		} while (current != farthestLeft);

	}

}
