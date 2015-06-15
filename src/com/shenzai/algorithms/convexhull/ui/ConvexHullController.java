package com.shenzai.algorithms.convexhull.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import com.shenzai.algorithms.convexhull.algorithms.CHAlgorithm;
import com.shenzai.util.Worker;


public class ConvexHullController {

	public ConvexHullController(final PointMapUI view, final PointMap model) {
		
		view.generateNewPoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Worker.execute(new Runnable() {
					@Override
					public void run() {
						model.generateRandomPointMap(view.pointView.getPreferredSize());
						view.setPoints(model.getPoints());
					}
				});
			}
		});
		
		view.generateSolution.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Worker.execute(new Runnable() {
					@Override
					public void run() {
						try {
							for (final JRadioButton alg : view.algorithms) {
								if (alg.isSelected()) {
									final Class<? extends CHAlgorithm> algorithmToUse = (Class<? extends CHAlgorithm>) Class.forName(alg.getName());
									view.setSolution(model.generateSolution(algorithmToUse));
								}
							}
							
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} 
					}
				});
			}
		});
	}
	
}
