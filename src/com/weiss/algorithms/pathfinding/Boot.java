package com.weiss.algorithms.pathfinding;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.weiss.algorithms.pathfinding.ui.GridPanel;
import com.weiss.algorithms.pathfinding.ui.PathfindingController;
import com.weiss.algorithms.pathfinding.wrappers.grid.Grid;

public class Boot extends JFrame {

	public Boot() {
		final JPanel container = new JPanel();
		final Grid grid = new Grid(30, 30);

		final GridPanel gridPanel = new GridPanel(grid);
		container.add(gridPanel, BorderLayout.CENTER);
		container.add(new PathfindingController(gridPanel), BorderLayout.WEST);
		
		this.setTitle("Pathfinder");
		this.setContentPane(container);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					new Boot();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
