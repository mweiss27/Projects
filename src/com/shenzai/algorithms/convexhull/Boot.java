package com.shenzai.algorithms.convexhull;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.shenzai.algorithms.convexhull.ui.ConvexHullController;
import com.shenzai.algorithms.convexhull.ui.PointMap;
import com.shenzai.algorithms.convexhull.ui.PointMapUI;

public class Boot {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				final Dimension size = new Dimension(600, 500);
				final PointMapUI view = new PointMapUI(size);
				final PointMap model = new PointMap(8, size);
				new ConvexHullController(view, model);
				
				view.setModel(model);
				
				final JFrame window = new JFrame("Convex Hull Algorithm");
				
				window.setContentPane(view);
				window.pack();
				window.setLocationRelativeTo(null);
				window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				window.setResizable(false);
				window.setVisible(true);
			}
		});
	}
	
}
