package com.shenzai.chat.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.shenzai.chat.message.SystemMessage;
import com.shenzai.io.Log;

public class ChatServer extends Thread {

	private Map<Socket, ClientInfo> clients = new HashMap<>();
	private ServerSocket server;

	public ChatServer(final int port) throws IOException {
		this.server = new ServerSocket(port);
		Log.info("[Server] Bound server to port: " + port);
	}

	@Override
	public void run() {
		outer:
			while (true) {
				try {
					Log.info("[Server] Waiting for a client to connect...");
					final Socket connectedClient = this.server.accept();
					final DataInputStream readIn = new DataInputStream(connectedClient.getInputStream());
					final DataOutputStream writeOut = new DataOutputStream(connectedClient.getOutputStream());

					final String clientName = readIn.readUTF();

					ClientInfo existingInfo;
					for (final Socket client : this.clients.keySet()) {
						existingInfo = this.clients.get(client);
						if (existingInfo.name.equalsIgnoreCase(clientName)) {
							Log.err("[Server] We already have a client with username " + clientName);
							IOUtils.closeQuietly(readIn);
							IOUtils.closeQuietly(writeOut);
							IOUtils.closeQuietly(connectedClient);
							continue outer;
						}
					}

					// New client connected, give it an Id.
					final int id = clients.size() + 1;
					Log.info("[Server] " + clientName + " connected. Assigning id " + id);
					writeOut.writeInt(id);

					final SystemMessage newConnection = new SystemMessage(clientName + " has connected.");
					this.broadcast(newConnection);

					final ClientInfo clientInfo = new ClientInfo(id);
					clientInfo.name = clientName;
					clientInfo.readIn = readIn;
					clientInfo.writeOut = writeOut;

					this.clients.put(connectedClient, clientInfo);
				} catch (final Exception any) {
					any.printStackTrace();
				}
			}
	}

	private void broadcast(final SystemMessage sysMessage) {
		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			final ObjectOutput objectOut = new ObjectOutputStream(out);
			objectOut.writeObject(sysMessage);

			for (final Socket client : this.clients.keySet()) {
				Log.info("[Server] Broadcasting " + sysMessage.getMessage() + " to client " + this.clients.get(client).name);
				this.clients.get(client).writeOut.write(out.toByteArray());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
