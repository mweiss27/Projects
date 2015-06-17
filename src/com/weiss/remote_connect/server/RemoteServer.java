package com.weiss.remote_connect.server;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.weiss.io.Log;
import com.weiss.remote_connect.util.RemoteConnectConfig;
import com.weiss.util.Time;

/**
 * A single-threaded server to ensure that only one client can connect at a time.
 */
public class RemoteServer {

	private final ServerSocket server;
	private final Rectangle screen = new Rectangle(0, 0);
	private BufferedImage screenCaptures;

	public RemoteServer(final int port) throws IOException {
		this.server = new ServerSocket(RemoteConnectConfig.PORT);
		this.screen.setSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	public void start() {
		while (true) {
			try {
				Log.info("[Server] Waiting for a client to connect...");
				final Socket connectedClient = this.server.accept();
				final DataInputStream readIn = new DataInputStream(connectedClient.getInputStream());
				final DataOutputStream writeOut = new DataOutputStream(connectedClient.getOutputStream());

				while (isSocketAvailable(connectedClient)) {


				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isSocketAvailable(final Socket socket) {
		return !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
	}

}
