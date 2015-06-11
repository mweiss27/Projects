package com.shenzai.chat.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.shenzai.chat.message.ChatMessage;
import com.shenzai.chat.message.Message;
import com.shenzai.chat.message.MessageReceivedEvent;
import com.shenzai.chat.message.SystemMessage;
import com.shenzai.io.Log;

public class ChatClient {

	private int id; //assigned by the server
	private String name;
	private Socket socket;
	private DataOutputStream writeOut; //messages TO the server
	private DataInputStream readIn; //messages FROM the server

	private BlockingQueue<ChatMessage> messagesToSend = new ArrayBlockingQueue<>(10);
	private volatile boolean active = true;

	private Thread inputThread, outputThread;

	private MessageReceivedEvent onChatMessageReceived;
	private MessageReceivedEvent onSystemMessageReceived;

	public ChatClient(final int port, final String name) throws IOException {
		this.socket = new Socket(InetAddress.getLocalHost(), port);
		this.name = name;
		Log.info("[Client] Connected to server. Requesting a Client Id...");
		this.writeOut = new DataOutputStream(this.socket.getOutputStream());
		this.readIn = new DataInputStream(this.socket.getInputStream());

		this.writeOut.writeUTF(name);

		this.id = this.readIn.readInt();
		Log.info("[Client] Client Id received: " + this.id);

		this.outputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (ChatClient.this.active) {
					try {

						final ChatMessage next = ChatClient.this.messagesToSend.take();
						if (next != null) {
							Log.info("[Client] Sending message: " + next);
							ChatClient.this.send(next);
						}

					} catch (final Exception any) {
						any.printStackTrace();
					}
				}
				Log.info("[Client] " + ChatClient.this.getName() + " no longer active. Ending outputThread.");
			}
		});

		this.inputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (ChatClient.this.active) {
					try {
						byte[] buffer = new byte[1024];
						int len;
						do {
							len = ChatClient.this.readIn.read(buffer);

							ChatClient.this.receive(buffer);
						} while (len >= 0);
					} catch (final Exception any) {
						any.printStackTrace();
					}
				}
				Log.info("[Client] " + ChatClient.this.getName() + " no longer active. Ending inputThread.");
			}
		});
		
		this.setOnSystemMessageReceived(new MessageReceivedEvent() {
			@Override
			public void messageReceived(Message message) {
				Log.info(ChatClient.this.getName() + " received System Message:" + message.getMessage());
			}
		});
	}

	public void start() {
		this.inputThread.start();
		this.outputThread.start();
	}

	public void disconnect() {
		this.messagesToSend.clear();
		this.active = false;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void send(final ChatMessage message) {
		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			final ObjectOutput objectOut = new ObjectOutputStream(out);
			objectOut.writeObject(message);

			this.writeOut.write(out.toByteArray());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive(final byte[] messageBytes) {
		try {
			final ByteArrayInputStream in = new ByteArrayInputStream(messageBytes);
			final ObjectInputStream objectIn = new ObjectInputStream(in);

			final Object readIn = objectIn.readObject();
			if (readIn instanceof ChatMessage) {
				final ChatMessage message = (ChatMessage) readIn;
				if (this.onChatMessageReceived != null) {
					this.onChatMessageReceived.messageReceived(message);
				}
			}
			else if (readIn instanceof SystemMessage) {
				final SystemMessage message = (SystemMessage) readIn;
				if (this.onSystemMessageReceived != null) {
					this.onSystemMessageReceived.messageReceived(message);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOnChatMessageReceived(final MessageReceivedEvent e) {
		this.onChatMessageReceived = e;
	}

	public void setOnSystemMessageReceived(final MessageReceivedEvent e) {
		this.onSystemMessageReceived = e;
	}

}
