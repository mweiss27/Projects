package com.shenzai.algorithms.convexhull.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;

import javax.swing.JPanel;

import com.shenzai.io.Log;

public class PointMapView extends JPanel {
	
	private Point[] allPoints;
	private Point[] solution;
	
	public PointMapView(final Dimension size) {
		
		this.setPreferredSize(size);
		
	}
	
	@Override
	protected void paintComponent(Graphics g1) {
		final Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		
		if (this.allPoints != null) {
			this.drawPointMap(this.allPoints, g);
		}
		
		if (this.solution != null) {
			this.drawSolution(this.solution, g);
		}
		
	}
	
	public void setView(final PointMap model) {
		this.allPoints = model.getPoints();
		this.repaint();
	}
	
	public void setSolution(final PointMap model) {
		this.solution = model.getSolution();
		this.repaint();
	}
	
	private void drawPointMap(final Point[] allPoints, final Graphics2D g) {
		if (allPoints != null) {
			final Graphics2D gr = (Graphics2D) g.create();

			for (final Point p : allPoints) {
				gr.fillOval(p.x-2, p.y-2, 4, 4);
			}
			
			gr.dispose();
			
		}
		else {
			Log.err("allPoints is null");
		}
	}
	
	/**
	 * @param points An ordered array of points of the solution.
	 * @param g The graphics handle for the PointMapView.
	 */
	private void drawSolution(final Point[] points, final Graphics2D g) {
		if (points != null && points.length >= 3) {
			final Graphics2D gr = (Graphics2D) g.create();
			
			gr.setColor(Color.red);
			gr.setStroke(new BasicStroke(3f));
			
			final Point first = new Point(), previous = new Point(), current = new Point();
			
			first.x = previous.x = points[0].x;
			first.y = previous.y = points[0].y;
			for (int i = 1; i < points.length; i++) {
				current.x = points[i].x;
				current.y = points[i].y;
				
				gr.drawLine(previous.x, previous.y, current.x, current.y);
			}
			gr.drawLine(current.x, current.y, first.x, first.y); // finish the convex
			gr.dispose();
		}
		else {
			Log.err("Invalid Point[] solution: " + Arrays.asList(points));
		}
	}
	
}
