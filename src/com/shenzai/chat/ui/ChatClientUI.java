package com.shenzai.chat.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.shenzai.chat.config.ChatClientConfig;

public class ChatClientUI extends JFrame {

	private JPanel container;

	public JButton startServer;
	public JButton startClient;

	public JLabel connectingToServer;
	public JLabel loadingGif;
	private Image[] loadingSprites;

	public ChatClientUI() {
		super("Shenzai's Chat Client");
		try {
			this.setIconImage(ImageIO.read(ChatClientUI.class.getResourceAsStream("/com/shenzai/chat/resources/chat_bubble.png")));
		} catch (IOException e) {
			System.err.println("Error reading chat_bubble.png");
		}

		this.init();

		this.setContentPane(this.container);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}

	private void init() {
		this.container = new JPanel(new GridBagLayout());
		this.container.setPreferredSize(new Dimension(800, 550));

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

		gbc.anchor = GridBagConstraints.SOUTHEAST;
		this.container.add(this.startServer, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		this.container.add(this.startClient, gbc);

		try {
			loadLoadingSprites();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.loadingGif = new JLabel(new ImageIcon((this.loadingSprites = loadLoadingSprites())[0]));
		
		this.connectingToServer = new JLabel("Connecting to server") {

			private ScheduledExecutorService exec;
			private int index;

			@Override
			public void setVisible(boolean visible) {
				super.setVisible(visible);
				if (ChatClientUI.this.loadingGif != null) {
					ChatClientUI.this.loadingGif.setVisible(visible);
					
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
		
		final JPanel loadingPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = gbc2.gridy = 0;
		gbc.weightx = gbc2.weighty = 1D;
		gbc2.anchor = GridBagConstraints.NORTH;
		gbc2.gridwidth = GridBagConstraints.REMAINDER;
		gbc2.gridy++;
		gbc2.weighty = 0D;
		loadingPanel.add(Box.createVerticalStrut(15), gbc2);
		gbc2.gridy++;
		gbc2.weighty = 0D;
		loadingPanel.add(this.connectingToServer, gbc2);
		gbc2.gridy++;
		loadingPanel.add(Box.createVerticalStrut(5), gbc2);
		gbc2.gridy++;
		loadingPanel.add(this.loadingGif, gbc2);
		
		gbc.weighty = 0D;
		gbc.gridy++;
		this.container.add(loadingPanel, gbc);
		
		Executors.newSingleThreadExecutor().submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					connectingToServer.setVisible(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

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

	public static void main(String[] args) throws Exception {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		new ChatClientUI();
	}

}
