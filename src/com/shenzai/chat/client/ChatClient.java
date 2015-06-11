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

import com.shenzai.chat.config.ChatClientConfig;
import com.shenzai.chat.message.Message;
import com.shenzai.chat.message.MessageReceivedEvent;
import com.shenzai.io.Log;

public class ChatClient {

	private int id; //assigned by the server
	private Socket socket;
	private DataOutputStream writeOut; //messages TO the server
	private DataInputStream readIn; //messages FROM the server

	private BlockingQueue<Message> messagesToSend = new ArrayBlockingQueue<>(10);
	private volatile boolean active = true;

	private Thread inputThread, outputThread;
	
	private MessageReceivedEvent messageReceivedEvent;

	public ChatClient(final int port) {
		try {
			this.socket = new Socket(InetAddress.getLocalHost(), ChatClientConfig.PORT);
			Log.info("Connected to server. Requesting a Client Id...");
			this.writeOut = new DataOutputStream(this.socket.getOutputStream());
			this.readIn = new DataInputStream(this.socket.getInputStream());
			
			this.id = this.readIn.readInt();
			Log.info("Client Id received: " + this.id);
			
			this.outputThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (ChatClient.this.active) {
						try {
							
							final Message next = ChatClient.this.messagesToSend.take();
							if (next != null) {
								Log.info("Sending message: " + next);
								ChatClient.this.send(next);
							}
							
						} catch (final Exception any) {
							any.printStackTrace();
						}
					}
				}
			});

			this.inputThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (ChatClient.this.active) {
						try {
							final List<Byte> bytes = new ArrayList<>();

							byte[] buffer = new byte[1024];
							int len;
							do {
								len = ChatClient.this.readIn.read(buffer);
								for (final byte b : buffer) {
									bytes.add(b);
								}
							} while (len >= 0);

							byte[] fullMessage = new byte[bytes.size()];
							for (int i = 0; i < fullMessage.length; i++) {
								fullMessage[i] = bytes.get(i);
							}

							ChatClient.this.receive(fullMessage);

						} catch (final Exception any) {
							any.printStackTrace();
						}
					}
				}
			});

		} catch (final IOException e) {
			e.printStackTrace();
		}
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

	public void setId(final int id) {
		this.id = id;
	}

	public void send(final Message message) {
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
			if (readIn instanceof Message) {
				final Message message = (Message) readIn;
				if (this.messageReceivedEvent != null) {
					this.messageReceivedEvent.messageReceived(message);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setOnMessageReceived(final MessageReceivedEvent e) {
		this.messageReceivedEvent = e;
	}

}
