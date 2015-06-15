package com.weiss.chat_client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.weiss.chat_client.ui.ChatClientController;
import com.weiss.chat_client.ui.ChatClientUI;


public class Boot {

	public static void main(String[] args) throws Exception {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				final ChatClientUI view = new ChatClientUI();
				new ChatClientController(view);
			}
		});
		
	}
	
}
