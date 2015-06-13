package com.shenzai.algorithms.convexhull;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Boot {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				
			}
		});
	}
	
}
