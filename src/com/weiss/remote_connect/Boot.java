package com.weiss.remote_connect;

import java.io.IOException;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.weiss.remote_connect.client.RemoteClient;
import com.weiss.remote_connect.server.RemoteServer;
import com.weiss.remote_connect.util.RemoteConnectConfig;

public class Boot {

	public static void main(String[] args) throws Exception {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							new RemoteServer(RemoteConnectConfig.PORT).start();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				
				/*Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							new RemoteClient(RemoteConnectConfig.PORT).start();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});*/
			}
		});
	}

}
