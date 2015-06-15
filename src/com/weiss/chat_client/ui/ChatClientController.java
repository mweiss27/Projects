package com.weiss.chat_client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import com.weiss.chat_client.client.ChatClient;
import com.weiss.chat_client.server.ChatServer;
import com.weiss.chat_client.util.ChatClientConfig;
import com.weiss.io.Log;
import com.weiss.util.SwingUtil;
import com.weiss.util.Time;
import com.weiss.util.Worker;

public class ChatClientController {

	private final ChatClientUI view;
	private ChatServer chatServer;

	private boolean back;
	
	private static final Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

	public ChatClientController(final ChatClientUI view) {
		this.view = view;
		try {
			view.loginScreen.localIp = InetAddress.getLocalHost().getHostAddress();
			view.loginScreen.externalIp = ChatClientController.this.getExternalAddress();
		} catch (Exception e) {
		}
		this.initStartupScreen();
	}

	private void initStartupScreen() {

		this.view.loginScreen.startServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.loginScreen.startClient.setEnabled(false);
				view.loginScreen.startServer.setEnabled(false);
				view.loginScreen.serverAlreadyStartedPanel.setVisible(false);
				view.loginScreen.serverStartedSuccessfullyPanel.setVisible(false);
				view.loginScreen.loadingPanel.setVisible(true);
				Worker.execute(new Runnable() {
					@Override
					public void run() {
						try {
							chatServer = new ChatServer(ChatClientConfig.PORT);
							chatServer.start();
							view.loginScreen.loadingPanel.setVisible(false);
							view.loginScreen.serverStartedSuccessfullyPanel.setVisible(true);
							Worker.execute(new Runnable() {
								@Override
								public void run() {
									Time.sleep(2000);
									view.loginScreen.serverStartedSuccessfullyPanel.setVisible(false);
								}
							});
						} catch (final IOException e) {
							e.printStackTrace();
							view.loginScreen.loadingPanel.setVisible(false);
							view.loginScreen.serverAlreadyStartedPanel.setVisible(true);
							Worker.execute(new Runnable() {
								@Override
								public void run() {
									Time.sleep(2000);
									view.loginScreen.serverAlreadyStartedPanel.setVisible(false);
								}
							});
						} finally {
							view.loginScreen.loadingPanel.setVisible(false);
							view.loginScreen.startClient.setEnabled(true);
							view.loginScreen.startServer.setEnabled(true);
						}
					}
				});
			}
		});

		this.view.loginScreen.startClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.loginScreen.startClient.setEnabled(false);
				view.loginScreen.startServer.setEnabled(false);
				view.loginScreen.serverAlreadyStartedPanel.setVisible(false);
				view.loginScreen.serverStartedSuccessfullyPanel.setVisible(false);
				view.loginScreen.enterClientInfoPanel.setVisible(true);
			}
		});
		
		this.view.loginScreen.loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (view.loginScreen.loginButton.isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
					
					final String ip = view.loginScreen.ipField.getText();
					if (!IP_PATTERN.matcher(ip).matches()) {
						Log.err("[Login] IP does not match pattern: " + ip);
						view.loginScreen.invalidIpPanel.setVisible(true);
						return;
					}
					
					ChatClientController.this.loginAction();
				}
			}
		});

		this.view.loginScreen.backButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (view.loginScreen.backButton.isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
					ChatClientController.this.back = true;

					ChatClientController.this.loginAction();
				}
			}
		});

		final KeyAdapter ka = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					final String ip = view.loginScreen.ipField.getText();
					if (!IP_PATTERN.matcher(ip).matches()) {
						Log.err("[Login] IP does not match pattern: " + ip);
						view.loginScreen.invalidIpPanel.setVisible(true);
						return;
					}
					
					ChatClientController.this.loginAction();
				}
			}
		};
		
		this.view.loginScreen.usernameField.addKeyListener(ka);
		this.view.loginScreen.ipField.addKeyListener(ka);
	}
	
	private String getExternalAddress() throws IOException {
		URL ipAws = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                ipAws.openStream()));

		String ip = in.readLine(); //you get the IP as a String
		return ip.trim();
	}
	
	private void loginAction() {
		Worker.execute(new Runnable() {
			@Override
			public void run() {
				try {
					view.loginScreen.invalidIpPanel.setVisible(false);
					if (back) {
						throw new IOException();
					}
					final String username = view.loginScreen.usernameField.getText().trim();
					if (username.length() > 0) {

								view.loginScreen.enterClientInfoPanel.setVisible(false);
								view.loginScreen.loadingPanel.setVisible(true);
						
						ChatClientConfig.set("user", username);
						ChatClientConfig.set("server_ip", view.loginScreen.ipField.getText().trim());
						final ChatClient chatClient = new ChatClient(InetAddress.getByName(view.loginScreen.ipField.getText().trim()), ChatClientConfig.PORT, username);
						chatClient.start();
						view.loginScreen.usernameField.setText("");
						view.chatWindow.build(chatClient);
						view.loginScreen.glassPane.setVisible(false);
						chatClient.bindChatWindow(view.chatWindow);
						SwingUtil.getCardLayout(view.container).show(view.container, ChatClientUI.CHAT_ROOM_CARD);
					}
					view.loginScreen.loadingPanel.setVisible(false);
				} catch (final IOException e) {
					view.loginScreen.enterClientInfoPanel.setVisible(false);
					e.printStackTrace();
					view.loginScreen.loadingPanel.setVisible(false);
					if (!back) {
						view.loginScreen.clientConnectFailedLabel.setText(view.loginScreen.clientConnectFailedLabel.
								getText().replace("%IP%", view.loginScreen.ipField.getText().trim()));
						view.loginScreen.clientConnectFailedPanel.setSize(view.loginScreen.clientConnectFailedPanel.getPreferredSize());
						view.loginScreen.clientConnectFailedPanel.setVisible(true);
						Worker.execute(new Runnable() {
							@Override
							public void run() {
								Time.sleep(2000);
								view.loginScreen.clientConnectFailedPanel.setVisible(false);
							}
						});
					}
					back = false;
				} finally {
					view.loginScreen.startClient.setEnabled(true);
					view.loginScreen.startServer.setEnabled(true);
					view.loginScreen.enterClientInfoPanel.setVisible(false);
				}
			}
		});
	}

}
