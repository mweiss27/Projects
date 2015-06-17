package com.weiss.chat_client.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;

import com.weiss.chat_client.message.ChatClientRequestMessage;
import com.weiss.chat_client.message.ChatMessage;
import com.weiss.chat_client.message.Message;
import com.weiss.chat_client.message.Request;
import com.weiss.chat_client.message.SystemMessage;
import com.weiss.io.Log;

/**
 * A multi-threaded server to allow multiple [infinite] clients to connect
 * and chat.
 */
public class ChatServer extends Thread {

	public static final String SHUTDOWN_MESSAGE = ".shutdown";
	public static final String REFRESH_USERS_MESSAGE = ".refresh_users";

	private Map<Socket, ClientInfo> clients = new HashMap<>();
	private ExecutorService clientThreads = Executors.newFixedThreadPool(10);
	private ServerSocket server;
	
	public ChatServer(final int port) throws IOException {
		this.server = new ServerSocket(port);
		Log.info("[Server] Bound server to port: " + port);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.info("Server shutting down. Telling our clients to disconnect.");
					broadcast(new SystemMessage(SHUTDOWN_MESSAGE));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
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

					this.broadcast(new SystemMessage(clientName + " has connected."));
					this.broadcast(new SystemMessage(REFRESH_USERS_MESSAGE));

					final ClientInfo clientInfo = new ClientInfo();
					clientInfo.id = id;
					clientInfo.name = clientName;
					clientInfo.readIn = readIn;
					clientInfo.writeOut = writeOut;

					this.clients.put(connectedClient, clientInfo);

					//Start the listener thread for this client
					ChatServer.this.clientThreads.execute(new Runnable() {
						@Override
						public void run() {
							while (!connectedClient.isClosed()) {
								try {
									byte[] buffer = new byte[1024];
									int len = -1;
									do {
										if (!connectedClient.isClosed() && !connectedClient.isInputShutdown()) {
											len = clientInfo.readIn.read(buffer);
											ChatServer.this.handleInput(buffer);
										}

									} while (len >= 0);
								} catch (Exception any) {
									Log.err("[Server] Exception on clientThread for " + clientName + ": " + any.getMessage());
									any.printStackTrace();
									if (connectedClient.isClosed() || connectedClient.isInputShutdown() || any instanceof StreamCorruptedException) {
										break;
									}
								}
							}
							Log.err("[Server] " + clientName + " is no longer active.");
						}
					});

				} catch (final Exception any) {
					any.printStackTrace();
				}
			}
	}

	private void handleInput(final byte[] requestBytes) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream in = new ByteArrayInputStream(requestBytes);
		final ObjectInputStream objectIn = new ObjectInputStream(in);
		final Object readIn = objectIn.readObject();
		if (readIn instanceof ChatClientRequestMessage) {
			final ChatClientRequestMessage request = (ChatClientRequestMessage) readIn;
			if (request.getMessage() != null && request.getMessage().matches("[\\d]+")) {
				final int requestInteger = Integer.parseInt(request.getMessage());
				switch(requestInteger) {
				case Request.USER_LIST:

					List<String> users = new ArrayList<>(this.clients.size());

					ClientInfo clientInfo;
					for (final Socket client : this.clients.keySet()) {
						clientInfo = this.clients.get(client);
						users.add(clientInfo.name);
					}

					Log.info("[Server] Setting USER_LIST request answer to " + users);
					request.setAnswer(users.toArray(new String[users.size()]));
					this.sendAnswer(request);
					break;
				case Request.DISCONNECT:
					final int clientId = request.getClientId();
					Socket toDrop = null;
					for (final Socket client : this.clients.keySet()) {
						clientInfo = this.clients.get(client);
						if (clientInfo.id == clientId) {
							this.dropClient(client);
							toDrop = client;
							break;
						}
					}
					if (toDrop != null) {
						this.broadcast(new SystemMessage(this.clients.get(toDrop).name + " has disconnected."));
						this.clients.remove(toDrop);
					}
					this.broadcast(new SystemMessage(REFRESH_USERS_MESSAGE));
					break;
				default:
					Log.err("[Server] We don't recognize this request value: " + requestInteger);
				}
			}
		}
		else if (readIn instanceof ChatMessage) {
			final ChatMessage chatMessage = (ChatMessage) readIn;
			Log.info("[Server] Received a chat message from " + 
					chatMessage.getClientName() + ": " + chatMessage.getMessage());
			this.broadcast(chatMessage);
		}
		else {
			Log.err("[Server] We don't recognize this type of request: " + readIn.getClass());
		}

	}

	private void dropClient(final Socket client) {
		final ClientInfo clientInfo = this.clients.get(client);
		IOUtils.closeQuietly(clientInfo.readIn);
		IOUtils.closeQuietly(clientInfo.writeOut);
	}

	private void sendAnswer(final ChatClientRequestMessage request) {
		final int clientId = request.getClientId();
		ClientInfo clientInfo;
		for (final Socket client : this.clients.keySet()) {
			clientInfo = this.clients.get(client);
			if (clientInfo.id == clientId) {
				Log.info("[Server] Sending ChatClientRequesMessage back to " + clientInfo.name);
				try {
					final ByteArrayOutputStream out = new ByteArrayOutputStream();

					final ObjectOutput objectOut = new ObjectOutputStream(out);
					objectOut.writeObject(request);

					if (!client.isClosed() && !client.isOutputShutdown()) {
						clientInfo.writeOut.write(out.toByteArray());
					}
					else {
						Log.err("[Server] socket is closed or output is shutdown for " + this.clients.get(client).name);
					}

					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.err("[Server] We did not find a Socket for client " + clientId);
	}

	private void broadcast(final Message message) {
		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			final ObjectOutput objectOut = new ObjectOutputStream(out);
			objectOut.writeObject(message);

			for (final Socket client : this.clients.keySet()) {
				if (!client.isInputShutdown() && !client.isOutputShutdown()) {
					Log.info("[Server] Broadcasting " + message.getMessage() + " to client " + this.clients.get(client).name);
					if (!client.isClosed() && !client.isOutputShutdown()) {
						this.clients.get(client).writeOut.write(out.toByteArray());
					}
					else {
						Log.err("[Server] socket is closed or output is shutdown for " + this.clients.get(client).name);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
