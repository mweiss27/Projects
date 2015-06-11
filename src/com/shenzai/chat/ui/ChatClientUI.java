package com.shenzai.chat.ui;

import java.awt.CardLayout;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.shenzai.chat.ui.cards.LoginScreen;
import com.shenzai.chat.util.SwingUtil;

public class ChatClientUI extends JFrame {

	private JPanel container;
	public final LoginScreen loginScreen;
	
	public static final String LOGIN_CARD = "login";
	public static final String CHAT_ROOM = "chat";
	
	public ChatClientUI() {
		super("Shenzai's Chat Client");
		try {
			this.setIconImage(ImageIO.read(ChatClientUI.class.getResourceAsStream("/com/shenzai/chat/resources/chat_bubble.png")));
		} catch (IOException e) {
			System.err.println("Error reading chat_bubble.png");
		}

		this.container = new JPanel(new CardLayout());
		
		this.loginScreen = new LoginScreen();
		
		this.container.add(this.loginScreen, LOGIN_CARD);

		SwingUtil.getCardLayout(this.container).show(this.container, LOGIN_CARD);
		
		this.setContentPane(this.container);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);

		this.loginScreen.initGlassPane();
		this.setGlassPane(this.loginScreen.glassPane);
		this.loginScreen.glassPane.setVisible(true);
		
	}

	

}
