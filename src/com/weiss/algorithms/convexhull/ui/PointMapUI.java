package com.weiss.algorithms.convexhull.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.weiss.algorithms.convexhull.algorithms.GiftWrap;
import com.weiss.algorithms.convexhull.algorithms.QuickHull;
import com.weiss.algorithms.util.AlgorithmsConfig;
import com.weiss.wrappers.Point;
import com.weiss.wrappers.swing.FlowPanel;
import com.weiss.wrappers.swing.VerticalFlowPanel;

public class PointMapUI extends JPanel {
	
	public final JPanel pointView;
	public final JPanel controlButtons;
	public final JButton generateNewPoints;
	public final JButton generateSolution;
	public final ButtonGroup algorithmSelecter;
	public final JRadioButton[] algorithms;
	
	private Point[] allPoints;
	private Point[] solution;
	
	public PointMapUI(final Dimension size) {
		super(new BorderLayout(0, 0));
		this.pointView = new JPanel() {
			@Override
			protected void paintComponent(Graphics g1) {
				final Graphics2D g = (Graphics2D) g1;
				super.paintComponent(g);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
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
		this.generateNewPoints.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('n'), "new");
		this.generateNewPoints.getActionMap().put("new", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateNewPoints.doClick();
			}
		});
		
		this.generateSolution = new JButton("Solve");
		this.generateSolution.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), "solve");
		this.generateSolution.getActionMap().put("solve", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateSolution.doClick();
			}
		});
		
		this.controlButtons = new FlowPanel(FlowLayout.CENTER, 3, this.generateNewPoints, this.generateSolution);
		this.controlButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
		
		this.algorithms = new JRadioButton[2];
		
		this.algorithms[0] = new JRadioButton("Quick Hull");
		this.algorithms[0].setName(QuickHull.class.getName());
		this.algorithms[0].setSelected(true);
		
		this.algorithms[1] = new JRadioButton("Gift Wrapping");
		this.algorithms[1].setName(GiftWrap.class.getName());
		
		this.algorithmSelecter = new ButtonGroup();
		Arrays.asList(this.algorithms).forEach(new Consumer<JRadioButton>() {
			@Override
			public void accept(JRadioButton t) {
				PointMapUI.this.algorithmSelecter.add(t);
			}
		});
		
		this.add(this.pointView, BorderLayout.CENTER);
		
		final JPanel southContainer = new VerticalFlowPanel(FlowLayout.CENTER, 0);
		southContainer.add(this.controlButtons);
		southContainer.add(new FlowPanel(FlowLayout.CENTER, 0, this.algorithms));
		
		this.add(southContainer, BorderLayout.SOUTH);
	}
	
	public void setPoints(final Point[] points) {
		this.allPoints = points;
		this.solution = null;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PointMapUI.this.repaint();
			}
		});
	}
	
	public void setSolution(final Point[] solution) {
		this.solution = solution;
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
			gr.setFont(AlgorithmsConfig.getFont(7f));
			for (final Point p : allPoints) {
				gr.fillOval(p.x-2, p.y-2, 4, 4);
				// gr.drawString(p.toString(), p.x-15, p.y+15);
			}
			
			gr.dispose();
			
		}
		else {
			System.err.println("allPoints is null");
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
			
			Point current = points[0];
			while (current.next != points[0]) {
				gr.drawLine(current.x, current.y, current.next.x, current.next.y);
				current = current.next;
			}
			gr.drawLine(current.x, current.y, current.next.x, current.next.y);
			
			gr.dispose();
		}
		else {
			System.err.println("Invalid Point[] solution: " + Arrays.asList(points));
		}
	}
	
}
