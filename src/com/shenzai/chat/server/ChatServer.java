package com.shenzai.chat.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.shenzai.chat.config.ChatClientConfig;
import com.shenzai.io.Log;

public class ChatServer extends Thread {

	private List<Socket> clients = new ArrayList<>();
	private ServerSocket server;
	
	public ChatServer(final int port) {
		try {
			this.server = new ServerSocket(ChatClientConfig.PORT);
		} catch (IOException e) {
			Log.err("Failed to bind server to port: " + ChatClientConfig.PORT);
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				
				Log.info("Waiting for a client to connect...");
				final Socket connectedClient = this.server.accept();
				// New client connected, give it an Id.
				final int id = clients.size();
				final DataOutputStream writeOut = new DataOutputStream(connectedClient.getOutputStream());
				writeOut.writeInt(id);
				
			} catch (final Exception any) {
				any.printStackTrace();
			}
		}
	}
	
}
