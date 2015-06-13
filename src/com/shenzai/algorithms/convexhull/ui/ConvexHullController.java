package com.shenzai.algorithms.convexhull.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ConvexHullController {

	
	public ConvexHullController(final PointMapUI view, final PointMap model) {
		
		view.generateNewPoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.generateRandomPointMap(view.pointView.getPreferredSize());
				view.setModel(model);
			}
		});
		
		view.generateSolution.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.generateSolution();
				view.setModel(model);
			}
		});
	}
	
}
