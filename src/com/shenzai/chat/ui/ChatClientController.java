package com.shenzai.chat.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.shenzai.chat.client.ChatClient;
import com.shenzai.chat.server.ChatServer;
import com.shenzai.chat.util.ChatClientConfig;
import com.shenzai.chat.util.Time;
import com.shenzai.chat.util.Worker;

public class ChatClientController {

	private final ChatClientUI view;
	private ChatServer chatServer;

	public ChatClientController(final ChatClientUI view) {
		this.view = view;
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
				view.loginScreen.enterUsernamePanel.setVisible(true);
				Worker.execute(new Runnable() {
					@Override
					public void run() {
						try {

							synchronized(view.loginScreen.usernameButton) {
								try {
									view.loginScreen.usernameButton.wait();
								} catch (InterruptedException e) {
								}
							}
							final String username = view.loginScreen.usernameField.getText().trim();
							if (username.length() > 0) {
								final ChatClient chatClient = new ChatClient(ChatClientConfig.PORT, username);
								chatClient.start();
								view.loginScreen.usernameField.setText("");
							}
							view.loginScreen.loadingPanel.setVisible(false);
						} catch (final IOException e) {
							view.loginScreen.loadingPanel.setVisible(false);
							view.loginScreen.clientConnectFailedPanel.setVisible(true);
							Worker.execute(new Runnable() {
								@Override
								public void run() {
									Time.sleep(2000);
									view.loginScreen.clientConnectFailedPanel.setVisible(false);
								}
							});
						} finally {
							view.loginScreen.startClient.setEnabled(true);
							view.loginScreen.startServer.setEnabled(true);
							view.loginScreen.enterUsernamePanel.setVisible(false);
						}
					}
				});
			}
		});
		
		view.loginScreen.usernameButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (view.loginScreen.usernameButton.isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
					synchronized(view.loginScreen.usernameButton) {
						view.loginScreen.usernameButton.notifyAll();
					}
				}
			}
		});
		
		view.loginScreen.usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					synchronized(view.loginScreen.usernameButton) {
						view.loginScreen.usernameButton.notifyAll();
					}
				}
			}
		});
	}

}
