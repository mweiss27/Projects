package com.shenzai.chat_client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.shenzai.chat_client.ui.ChatClientController;
import com.shenzai.chat_client.ui.ChatClientUI;


public class Boot {

	public static void main(String[] args) throws Exception {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final ChatClientUI view = new ChatClientUI();
				new ChatClientController(view);
			}
		});
		
	}
	
}
