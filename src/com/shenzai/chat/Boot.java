package com.shenzai.chat;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.shenzai.chat.ui.ChatClientController;
import com.shenzai.chat.ui.ChatClientUI;


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
