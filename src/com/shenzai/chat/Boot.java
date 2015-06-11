package com.shenzai.chat;

import javax.swing.SwingUtilities;

import com.shenzai.chat.ui.ChatClientUI;


public class Boot {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ChatClientUI();
			}
		});
	}
	
}
