package com.weiss.remote_connect;

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
