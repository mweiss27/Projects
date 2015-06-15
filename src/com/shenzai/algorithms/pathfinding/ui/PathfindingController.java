package com.shenzai.algorithms.pathfinding.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.shenzai.algorithms.pathfinding.algorithms.dijkstra.DijkstraPath;
import com.shenzai.algorithms.pathfinding.algorithms.dijkstra.DijkstraProperties;
import com.shenzai.algorithms.pathfinding.algorithms.star.AStarPath;
import com.shenzai.algorithms.pathfinding.wrappers.node.Node;
import com.shenzai.algorithms.pathfinding.wrappers.node.NodeQuery;
import com.shenzai.algorithms.pathfinding.wrappers.node.NodeType;
import com.shenzai.util.Worker;

public class PathfindingController extends JPanel {

	private GridPanel gridPanel;
	private JRadioButton aStar = new JRadioButton("A Star") {{
		setSelected(true);
		setFocusPainted(false);
	}};
	private JRadioButton dijkstra = new JRadioButton("Dijkstra") {{
		setFocusPainted(false);
	}};
	
	private ButtonGroup group = new ButtonGroup() {{
		add(PathfindingController.this.aStar);
		add(PathfindingController.this.dijkstra);
	}};

	private JButton findPath = new JButton("Find Path") {{
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Worker.execute(new Runnable() {
					public void run() {
						PathfindingController.this.clear();
						if (PathfindingController.this.aStar.isSelected()) {
							AStarPath path = new AStarPath(PathfindingController.this.gridPanel.grid);
							for (Node node : PathfindingController.this.gridPanel.grid.getNodes()) {
								if (node.type == NodeType.CHECKPOINT) {
									path.addCheckpoint(node);
								}
							}
							NodeQuery nodes = path.toNodeQuery(PathfindingController.this.useDiagonals.isSelected());
							if (nodes != null && nodes.size() > 0) {
								System.out.println("Nodes in computed path: " + nodes.size());
								PathfindingController.this.gridPanel.drawPath = nodes;
								PathfindingController.this.gridPanel.repaint();
							}
						}
						else if (PathfindingController.this.dijkstra.isSelected()) {
							DijkstraPath path = new DijkstraPath(PathfindingController.this.gridPanel.grid);
							for (Node n : PathfindingController.this.gridPanel.grid.getNodes()) {
								DijkstraProperties.registerProperties(n, PathfindingController.this.gridPanel.grid, path);
								n.dijkstraProperties(path).reset();
								if (n.type == NodeType.CHECKPOINT) {
									path.addCheckpoint(n);
								}
							}
							NodeQuery nodes = path.toNodeQuery(PathfindingController.this.useDiagonals.isSelected());
							if (nodes != null && nodes.size() > 0) {
								System.out.println("Nodes in computed path: " + nodes.size());
								PathfindingController.this.gridPanel.drawPath = nodes;
								PathfindingController.this.gridPanel.repaint();
							}
						}
					}
				});
			}
		});
	}};

	private JButton resetGrid = new JButton("Reset grid") {{
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Worker.execute(new Runnable() {
					public void run() {
						PathfindingController.this.clear();
						Node[] nodes = PathfindingController.this.gridPanel.grid.getNodes();
						for (int i = 0; i < nodes.length; i++) {
							if (i == 0) {
								nodes[i].type = NodeType.START;
								PathfindingController.this.gridPanel.grid.start = nodes[i];
							}
							else if (i == nodes.length-1) {
								nodes[i].type = NodeType.FINISH;
								PathfindingController.this.gridPanel.grid.finish = nodes[i];
							}
							else {
								nodes[i].type = NodeType.UNBLOCKED;
							}
						}
						repaint();
					}
				});
			}
		});
	}};

	private JButton clear = new JButton("Clear Path") {{
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Worker.execute(new Runnable() {
					public void run() {
						PathfindingController.this.clear();
					}
				});
			}
		});
	}};

	private void clear() {
		if (this.gridPanel.drawPath != null) {
			for (Node n : this.gridPanel.drawPath) {
				n.type = NodeType.UNBLOCKED;
			}
		}
		for (Node n : this.gridPanel.grid.getNodes()) {
			if (n.type == NodeType.START) {
				this.gridPanel.grid.start = n;
			}
			else if (n.type == NodeType.FINISH) {
				this.gridPanel.grid.finish = n;
			}
		}
		this.gridPanel.drawPath = null;
		this.gridPanel.repaint();
	}

	private JCheckBox useDiagonals = new JCheckBox("Use Diagonals") {{
		setSelected(true);
	}};

	public PathfindingController(GridPanel gridPanel) {
		this.gridPanel = gridPanel;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(this.aStar);
		this.add(this.dijkstra);
		this.add(this.useDiagonals);
		this.add(this.findPath);
		this.add(this.resetGrid);
		this.add(this.clear);
	}



}
