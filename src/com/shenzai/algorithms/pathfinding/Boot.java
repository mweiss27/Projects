package com.shenzai.algorithms.pathfinding;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.shenzai.algorithms.pathfinding.ui.PathfindingController;
import com.shenzai.algorithms.pathfinding.ui.GridPanel;
import com.shenzai.algorithms.pathfinding.wrappers.grid.Grid;

public class Boot extends JFrame {

	public Boot() {
		JPanel container = new JPanel();
		final Grid grid = new Grid(20, 20);

		GridPanel gridPanel = new GridPanel(grid);
		container.add(gridPanel, BorderLayout.CENTER);
		container.add(new PathfindingController(gridPanel), BorderLayout.WEST);
		def(container);
	}

	/** Default JFrame operations */
	private void def(JPanel container) {
		this.setTitle("Pathfinder");
		this.add(container);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
		}
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					new Boot();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
