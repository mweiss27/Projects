package com.shenzai.algorithms.convexhull.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import com.shenzai.io.Log;
import com.shenzai.wrappers.swing.FlowPanel;

public class PointMapUI extends JPanel {
	
	public final JPanel pointView;
	public final JPanel controlButtons;
	public final JButton generateNewPoints;
	public final JButton generateSolution;
	
	private Point[] allPoints;
	private Point[] solution;
	
	public PointMapUI(final Dimension size) {
		super(new BorderLayout(0, 0));
		this.pointView = new JPanel() {
			@Override
			protected void paintComponent(Graphics g1) {
				final Graphics2D g = (Graphics2D) g1;
				super.paintComponent(g);
				
				if (allPoints != null) {
					drawPointMap(allPoints, g);
				}
				
				if (solution != null) {
					drawSolution(solution, g);
				}
			}
		};
		this.pointView.setPreferredSize(size);

		this.generateNewPoints = new JButton("New Field");
		this.generateSolution = new JButton("Solve");
		this.controlButtons = new FlowPanel(FlowLayout.CENTER, 3, this.generateNewPoints, this.generateSolution);
		this.controlButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
		
		this.add(this.pointView, BorderLayout.CENTER);
		this.add(this.controlButtons, BorderLayout.SOUTH);
	}
	
	public void setModel(final PointMap model) {
		this.allPoints = model.getPoints();
		this.solution = model.getSolution();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PointMapUI.this.repaint();
			}
		});
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
			gr.setStroke(new BasicStroke(1f));
			
			final Point first = new Point(), previous = new Point(), current = new Point();
			
			first.x = previous.x = points[0].x;
			first.y = previous.y = points[0].y;
			for (int i = 1; i < points.length; i++) {
				current.x = points[i].x;
				current.y = points[i].y;
				
				gr.drawLine(previous.x, previous.y, current.x, current.y);
				
				previous.x = current.x;
				previous.y = current.y;
			}
			gr.drawLine(current.x, current.y, first.x, first.y); // finish the convex
			gr.dispose();
		}
		else {
			Log.err("Invalid Point[] solution: " + Arrays.asList(points));
		}
	}
	
}
