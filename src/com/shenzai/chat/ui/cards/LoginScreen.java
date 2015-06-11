package com.shenzai.chat.ui.cards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.shenzai.chat.ui.ChatClientUI;
import com.shenzai.chat.util.ChatClientConfig;

public class LoginScreen extends JPanel {

	public JPanel glassPane;

	public JButton startServer;
	public JButton startClient;

	public JPanel loadingPanel;
	public JPanel serverAlreadyStartedPanel;
	public JPanel serverStartedSuccessfullyPanel;
	public JPanel clientConnectFailedPanel;
	public JPanel enterUsernamePanel;
	public JTextField usernameField;
	public JLabel usernameButton;

	public JLabel connectingToServer;
	public JLabel loadingGif;
	private Image[] loadingSprites;
	
	public LoginScreen() {
		super(new GridBagLayout());
		this.init();
	}
	
	public void initGlassPane() {
		final int centerX = this.getPreferredSize().width / 2;
		final Rectangle glassPaneBounds = new Rectangle();

		glassPaneBounds.setLocation(centerX - (loadingPanel.getPreferredSize().width / 2), 
				this.startServer.getY() + this.startServer.getPreferredSize().height + 5);
		glassPaneBounds.setSize(this.loadingPanel.getPreferredSize());
		this.loadingPanel.setBounds(glassPaneBounds);

		glassPaneBounds.setLocation(centerX - (this.serverAlreadyStartedPanel.getPreferredSize().width / 2), 
				this.startServer.getY() + this.startServer.getPreferredSize().height + 5);
		glassPaneBounds.setSize(this.serverAlreadyStartedPanel.getPreferredSize());
		this.serverAlreadyStartedPanel.setBounds(glassPaneBounds);

		glassPaneBounds.setLocation(centerX - (this.serverStartedSuccessfullyPanel.getPreferredSize().width / 2), 
				this.startServer.getY() + this.startServer.getPreferredSize().height + 5);
		glassPaneBounds.setSize(this.serverStartedSuccessfullyPanel.getPreferredSize());
		this.serverStartedSuccessfullyPanel.setBounds(glassPaneBounds);

		glassPaneBounds.setLocation(centerX - (this.clientConnectFailedPanel.getPreferredSize().width / 2), 
				this.startServer.getY() + this.startServer.getPreferredSize().height + 5);
		glassPaneBounds.setSize(this.clientConnectFailedPanel.getPreferredSize());
		this.clientConnectFailedPanel.setBounds(glassPaneBounds);

		glassPaneBounds.setLocation(centerX - (this.enterUsernamePanel.getPreferredSize().width / 2), 
				this.startServer.getY() + this.startServer.getPreferredSize().height + 5);
		glassPaneBounds.setSize(this.enterUsernamePanel.getPreferredSize());
		this.enterUsernamePanel.setBounds(glassPaneBounds);
	}
	
	
	private void init() {
		this.setPreferredSize(new Dimension(500, 400));

		this.glassPane = new JPanel();
		this.glassPane.setLayout(null);
		this.glassPane.setOpaque(false);

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1D;

		this.startServer = new JButton("Start Server");
		this.startServer.setFocusPainted(false);
		this.startServer.setFont(ChatClientConfig.getFont(15f));

		this.startClient = new JButton("Start Client");
		this.startClient.setFocusPainted(false);
		this.startClient.setFont(ChatClientConfig.getFont(15f));

		gbc.insets = new Insets(0, 3, 0, 3);

		gbc.anchor = GridBagConstraints.EAST;
		this.add(this.startServer, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		this.add(this.startClient, gbc);

		this.loadingGif = new JLabel(new ImageIcon((this.loadingSprites = loadLoadingSprites())[0]));

		this.connectingToServer = new JLabel("Connecting to server") {

			private ScheduledExecutorService exec;
			private int index;

			@Override
			public void setVisible(boolean visible) {
				super.setVisible(visible);
				if (LoginScreen.this.loadingGif != null) {
					LoginScreen.this.loadingGif.setVisible(visible);

					if (visible) {
						if (exec != null && !exec.isShutdown()) {
							exec.shutdown();
						}
						index = 0;
						exec = Executors.newScheduledThreadPool(1);
						exec.scheduleWithFixedDelay(new Runnable() {
							@Override
							public void run() {
								if (index >=loadingSprites.length) {
									index = 0;
								}
								((ImageIcon) loadingGif.getIcon()).setImage(loadingSprites[index]);
								loadingGif.repaint();
								index++;
							};
						}, 0, 65, TimeUnit.MILLISECONDS);
					}
					else {
						exec.shutdown();
					}

				}
			}

		};
		this.connectingToServer.setFont(ChatClientConfig.getFont(18f));
		this.connectingToServer.setMinimumSize(this.connectingToServer.getPreferredSize());
		this.connectingToServer.setVisible(true);

		this.loadingPanel = new JPanel(new GridBagLayout());
		this.loadingPanel.setOpaque(false);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = gbc2.gridy = 0;
		gbc2.weightx = 1D;
		gbc2.weighty = 0D;
		gbc2.anchor = GridBagConstraints.NORTH;

		this.loadingPanel.add(Box.createVerticalStrut(15), gbc2);
		gbc2.gridy++;
		this.loadingPanel.add(this.connectingToServer, gbc2);
		gbc2.gridy++;
		this.loadingPanel.add(Box.createVerticalStrut(5), gbc2);
		gbc2.gridy++;
		gbc2.weighty = 1D;
		this.loadingPanel.add(this.loadingGif, gbc2);

		final JLabel serverAlreadyStartedLabel = new JLabel("A server is already bound to port " + ChatClientConfig.PORT);
		serverAlreadyStartedLabel.setFont(ChatClientConfig.getFont(Font.BOLD, 18f));
		serverAlreadyStartedLabel.setForeground(Color.red);

		this.serverAlreadyStartedPanel = new JPanel(new GridBagLayout());
		this.serverAlreadyStartedPanel.setOpaque(false);
		this.serverAlreadyStartedPanel.add(serverAlreadyStartedLabel);

		final JLabel serverStartedSuccessfullyLabel = new JLabel("Server bound to port " + ChatClientConfig.PORT);
		serverStartedSuccessfullyLabel.setFont(ChatClientConfig.getFont(Font.BOLD, 18f));
		serverStartedSuccessfullyLabel.setForeground(Color.green.darker());

		final JLabel clientConnectFailedLabel = new JLabel("Failed to connect to server on port " + ChatClientConfig.PORT);
		clientConnectFailedLabel.setFont(ChatClientConfig.getFont(Font.BOLD, 18f));
		clientConnectFailedLabel.setForeground(Color.red);

		this.serverStartedSuccessfullyPanel = new JPanel(new GridBagLayout());
		this.serverStartedSuccessfullyPanel.setOpaque(false);
		this.serverStartedSuccessfullyPanel.add(serverStartedSuccessfullyLabel);

		this.clientConnectFailedPanel = new JPanel(new GridBagLayout());
		this.clientConnectFailedPanel.setOpaque(false);
		this.clientConnectFailedPanel.add(clientConnectFailedLabel);

		this.enterUsernamePanel = new JPanel(new BorderLayout(0, 0));
		this.usernameField = new JTextField(15);
		
		this.enterUsernamePanel.setOpaque(false);
		this.enterUsernamePanel.add(new JLabel("  Enter a Username"), BorderLayout.NORTH); //intentional spaces
		final JPanel usernameCenter = new JPanel(new FlowLayout(FlowLayout.LEFT)); //flowlayout
		
		try {
			this.usernameButton = new JLabel(
					new ImageIcon(
							ImageIO.read(
									this.getClass().getResourceAsStream("/com/shenzai/chat/resources/login_button.png")
									).getScaledInstance(20, 20, Image.SCALE_SMOOTH)
							)
					);
		} catch (IOException e) {
			e.printStackTrace();
			this.usernameButton = new JLabel(">");
		}
		
		this.usernameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.usernameButton.setToolTipText("Login");
		
		usernameCenter.add(this.usernameField);
		usernameCenter.add(this.usernameButton);
		this.enterUsernamePanel.add(usernameCenter, BorderLayout.CENTER);

		this.glassPane.add(this.loadingPanel);
		this.glassPane.add(this.serverAlreadyStartedPanel);
		this.glassPane.add(this.serverStartedSuccessfullyPanel);
		this.glassPane.add(this.clientConnectFailedPanel);
		this.glassPane.add(this.enterUsernamePanel);

		this.loadingPanel.setVisible(false);
		this.serverAlreadyStartedPanel.setVisible(false);
		this.serverStartedSuccessfullyPanel.setVisible(false);
		this.clientConnectFailedPanel.setVisible(false);
		this.enterUsernamePanel.setVisible(false);

	}
	
	private Image[] loadLoadingSprites() {
		try {
			final BufferedImage sprite = ImageIO.read(ChatClientUI.class.getResourceAsStream("/com/shenzai/chat/resources/loadingSprites.png"));
			final int width = sprite.getWidth();
			final int SPRITE_WIDTH = 128;
			if (width % SPRITE_WIDTH == 0) {
				final Image[] result = new Image[width / SPRITE_WIDTH];
				for (int i = 0; i < result.length; i++) {
					result[i] = sprite.getSubimage((i * SPRITE_WIDTH), 0, SPRITE_WIDTH, SPRITE_WIDTH).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
				}
				return result;
			}
			else {
				System.err.println("Width % " + SPRITE_WIDTH + " != 0: " + width);
			}
			System.out.println("Loaded sprite file width : " + width);
		} catch (Exception e) {
			System.err.println("Error reading loading_sprites.png");
		}
		return new BufferedImage[] { };
	}
	
}
