package com.weiss.chat_client.ui.cards;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.weiss.chat_client.client.ChatClient;
import com.weiss.chat_client.util.ChatClientConfig;
import com.weiss.io.Log;

public class ChatWindow extends JPanel {

	public JLabel chattingAs;
	public JList<String> connectedUsers;
	public JTextField chatbox;
	private JScrollPane chatWindowScroll;
	public JTextPane chatWindow;
	public JLabel sendButton;

	public ChatWindow() {
		super(new BorderLayout(0, 0));
		this.init();
	}

	private void init() {
		this.setPreferredSize(new Dimension(500, 400));
		
		this.chattingAs = new JLabel("Chatting as: null", SwingConstants.CENTER);
		this.chattingAs.setFont(ChatClientConfig.getFont());
		
		this.connectedUsers = new JList<>();
		this.connectedUsers.setFont(ChatClientConfig.getFont());
		this.connectedUsers.setPreferredSize(new Dimension(100, 1));
		this.connectedUsers.setFocusable(false);
		
		
		this.chatbox = new JTextField(30);
		this.chatbox.setFont(ChatClientConfig.getFont());
		
		this.chatWindow = new JTextPane();
		this.chatWindow.setEditable(false);
		this.chatWindow.setFont(ChatClientConfig.getFont());
		
		
		this.chatWindowScroll = new JScrollPane(this.chatWindow);
		this.chatWindowScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.chatWindowScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		try {
			this.sendButton = new JLabel(
					new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream(
							"/com/weiss/chat_client/resources/send_button.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		} catch (IOException e) {
			Log.err("Error reading send_button.png");
			this.sendButton = new JLabel(">");
		}
		
		this.sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.sendButton.setToolTipText("Send");
		
		this.add(this.chattingAs, BorderLayout.NORTH);
		this.add(this.connectedUsers, BorderLayout.WEST);
		this.add(this.chatWindowScroll, BorderLayout.CENTER);
		
		JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		south.add(this.chatbox);
		south.add(this.sendButton);
		this.add(south, BorderLayout.SOUTH);
		
	}

	public void build(final ChatClient chatClient) {
		this.chattingAs.setText("Chatting as " + chatClient.getName());
	}
	
}
