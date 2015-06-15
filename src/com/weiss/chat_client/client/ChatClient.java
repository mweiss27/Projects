package com.weiss.chat_client.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;

import com.weiss.chat_client.message.ChatClientRequestMessage;
import com.weiss.chat_client.message.ChatMessage;
import com.weiss.chat_client.message.Message;
import com.weiss.chat_client.message.MessageReceivedEvent;
import com.weiss.chat_client.message.Request;
import com.weiss.chat_client.message.SystemMessage;
import com.weiss.chat_client.server.ChatServer;
import com.weiss.chat_client.ui.cards.ChatWindow;
import com.weiss.io.Log;
import com.weiss.util.Worker;

public class ChatClient {

	private int id; //assigned by the server
	private String name;
	private Socket socket;
	private DataOutputStream writeOut; //messages TO the server
	private DataInputStream readIn; //messages FROM the server

	private BlockingQueue<ChatMessage> messagesToSend = new ArrayBlockingQueue<>(10);
	private List<ChatClientRequestMessage> messagesWaitingOnAnswer = new ArrayList<>();
	private volatile boolean active = true;

	private Thread inputThread, outputThread;

	private MessageReceivedEvent onChatMessageReceived;
	private MessageReceivedEvent onSystemMessageReceived;

	private ChatWindow chatWindow;
	private ScheduledExecutorService userListPoll;
	private Lock userListPollLock = new ReentrantLock();

	public ChatClient(final InetAddress address, final int port, final String name) throws IOException {
		Log.info("[Client] Attempting to connect to " + address.getHostAddress() + ":" + port);
		this.socket = new Socket();
		this.socket.connect(new InetSocketAddress(address, port), 2000);
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
						Log.err("[Client] Exception on outputThread for " + ChatClient.this.getName() + ": " + any.getMessage());
						if (ChatClient.this.socket.isClosed() || ChatClient.this.socket.isInputShutdown()) {
							break;
						}
					}
				}
				Log.info("[Client] " + ChatClient.this.getName() + " no longer active. Ending outputThread.");
				System.exit(0);
			}
		});

		this.inputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (ChatClient.this.active) {
					try {
						byte[] buffer = new byte[1024];
						int len = -1;
						do {
							Log.info("[Client] " + ChatClient.this.getName() + " waiting for an input command");
							if (!ChatClient.this.socket.isInputShutdown()) {
								len = ChatClient.this.readIn.read(buffer);
								Log.info("[Client] " + ChatClient.this.getName() + " input command received. Handling");
								ChatClient.this.receive(buffer);
							}
							else {
								break;
							}
						} while (len >= 0);
					} catch (final Exception any) {
						Log.err("[Client] Exception on inputThread for " + ChatClient.this.getName() + ": " + any.getMessage());
						if (ChatClient.this.socket.isClosed() || ChatClient.this.socket.isInputShutdown()) {
							break;
						}
					}
				}
				Log.info("[Client] " + ChatClient.this.getName() + " no longer active. Ending inputThread.");
				System.exit(0);
			}
		});

		this.setOnSystemMessageReceived(new MessageReceivedEvent() {
			@Override
			public void messageReceived(final Message message) {
				Worker.execute(new Runnable() {
					@Override
					public void run() {
						final SystemMessage systemMessage = (SystemMessage) message;
						Log.info(ChatClient.this.getName() + " received System Message:" + systemMessage.getMessage());
						if (systemMessage.getMessage().equals(ChatServer.SHUTDOWN_MESSAGE)) {
							ChatClient.this.disconnect();
						}
						else if (systemMessage.getMessage().equals(ChatServer.REFRESH_USERS_MESSAGE)) {
							ChatClient.this.updateUserList();
						}
						else if (systemMessage.getMessage().contains("has connected")
								|| systemMessage.getMessage().contains("has disconnected")) {
							if (ChatClient.this.chatWindow.chatWindow.getText().trim().length() > 0) {
								ChatClient.this.chatWindow.chatWindow.setText(
										ChatClient.this.chatWindow.chatWindow.getText() + "\n");
							}
							ChatClient.this.chatWindow.chatWindow.setText(chatWindow.chatWindow.getText() + 
									systemMessage.getMessage());
							ChatClient.this.chatWindow.chatWindow.setCaretPosition(
									ChatClient.this.chatWindow.chatWindow.getText().length());
						}
						else {
							Log.err("Unknown SystemMessage: " + systemMessage.getMessage());
						}
					}
				});
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				Log.info("Client disconnecting. Removing ourself from the Server list.");
				final ChatClientRequestMessage request = new ChatClientRequestMessage(ChatClient.this.getId(), Request.DISCONNECT);
				try {
					ChatClient.this.send(request);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	public void start() {
		this.inputThread.start();
		this.outputThread.start();
		if (userListPoll != null && !userListPoll.isShutdown()) {
			userListPoll.shutdown();
		}
		userListPoll = Executors.newSingleThreadScheduledExecutor();
		userListPoll.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				ChatClient.this.updateUserList();
			}
		}, 0, 45, TimeUnit.SECONDS);
	}

	public void updateUserList() {
		if (this.userListPollLock.tryLock()) {
			try {
				String[] result = null;
				Log.info("[Client] " + this.getName() + " sending a request for USER_LIST");
				ChatClientRequestMessage request = new ChatClientRequestMessage(this.getId(), Request.USER_LIST);
				this.messagesWaitingOnAnswer.add(request);
				final int index = this.messagesWaitingOnAnswer.size() - 1;
				request.requestId = index;
				this.send(request);
				Log.info("[Client] " + this.getName() + " waiting on answer for USER_LIST");
				this.waitForAnswer(request);
				request = this.messagesWaitingOnAnswer.get(request.requestId);
				Log.info("[Client] " + this.getName() + " received an answer for USER_LIST");
				this.messagesWaitingOnAnswer.remove(index);
				if (request.getAnswer() != null) {
					if (request.getAnswer() instanceof String[]) {
						result = (String[]) request.getAnswer();
					}
					else {
						Log.err("[Client] Invalid return type for request answer. Expected String[], returned: " + request.getAnswer().getClass());
					}
				}
				else {
					Log.err("[Client] Request answer was null.");
				}
				if (result == null) {
					result = new String[] { };
				}

				if (ChatClient.this.chatWindow != null) {
					ChatClient.this.chatWindow.connectedUsers.setListData(result);
				}
			} catch (Exception any) {
				any.printStackTrace();
			} finally {
				this.userListPollLock.unlock();
			}
		}
		else {
			Log.err("[Client] " + this.getName() + " failed to lock userListPollLock");
		}
	}

	public void disconnect() {
		Log.info("[Client] " + this.getName() + " disconnected.");
		this.messagesToSend.clear();
		this.active = false;
		IOUtils.closeQuietly(this.readIn);
		IOUtils.closeQuietly(this.writeOut);
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

	public void send(final Message message) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		final ObjectOutput objectOut = new ObjectOutputStream(out);
		objectOut.writeObject(message);

		if (!this.socket.isClosed() && !this.socket.isOutputShutdown()) {
			this.writeOut.write(out.toByteArray());
		}
		else {
			Log.err("[Client] socket is closed or output is shutdown for " + this.getName());
		}
	}

	private void receive(final byte[] messageBytes) throws IOException, ClassNotFoundException {
		Log.info("[Client] Receiving " + messageBytes.length + " bytes");
		final ByteArrayInputStream in = new ByteArrayInputStream(messageBytes);
		final ObjectInputStream objectIn = new ObjectInputStream(in);

		final Object readIn = objectIn.readObject();
		Log.info("[Client] received object: " + readIn);
		if (readIn != null) {
			if (readIn instanceof ChatMessage) {
				final ChatMessage message = (ChatMessage) readIn;
				if (this.onChatMessageReceived != null) {
					this.onChatMessageReceived.messageReceived(message);
				}
				else {
					Log.err("[Client] Received a ChatMessage, but onChatMessageReceived is null");
				}
			}
			else if (readIn instanceof SystemMessage) {
				final SystemMessage message = (SystemMessage) readIn;
				if (this.onSystemMessageReceived != null) {
					this.onSystemMessageReceived.messageReceived(message);
				}
				else {
					Log.err("[Client] Received a SystemMessage, but onSystemMessageReceived is null");
				}
			}
			else if (readIn instanceof ChatClientRequestMessage) {
				final ChatClientRequestMessage answer = (ChatClientRequestMessage) readIn;
				Log.info("[Client] " + this.getName() + " received an answer (" + Arrays.asList((String[]) answer.getAnswer()) + ") . Notifying its wait");
				synchronized(this.messagesWaitingOnAnswer.get(answer.requestId)) {
					this.messagesWaitingOnAnswer.get(answer.requestId).notifyAll();
					this.messagesWaitingOnAnswer.set(answer.requestId, answer);
				}
			}
			else {
				Log.err("[Client] We don't recognize this type of message: " + readIn.getClass());
			}
		}
		else {
			Log.err("[Client] readIn is null");
		}

	}

	private void waitForAnswer(final ChatClientRequestMessage request) {
		synchronized(this.messagesWaitingOnAnswer.get(request.requestId)) {
			try {
				this.messagesWaitingOnAnswer.get(request.requestId).wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setOnChatMessageReceived(final MessageReceivedEvent e) {
		this.onChatMessageReceived = e;
	}

	public void setOnSystemMessageReceived(final MessageReceivedEvent e) {
		this.onSystemMessageReceived = e;
	}

	public void bindChatWindow(final ChatWindow chatWindow) {
		this.chatWindow = chatWindow;

		chatWindow.chatbox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					if (chatWindow.chatbox.getText().trim().length() > 0) {
						final ChatMessage chatMessage = new ChatMessage(ChatClient.this.getId(), 
								ChatClient.this.getName(), chatWindow.chatbox.getText());
						ChatClient.this.messagesToSend.offer(chatMessage);
						chatWindow.chatbox.setText("");
					}
				}
			}
		});

		chatWindow.sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (chatWindow.sendButton.isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
					if (chatWindow.chatbox.getText().trim().length() > 0) {
						final ChatMessage chatMessage = new ChatMessage(ChatClient.this.getId(), 
								ChatClient.this.getName(), chatWindow.chatbox.getText());

						try {
							ChatClient.this.send(chatMessage);
							chatWindow.chatbox.setText("");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		this.setOnChatMessageReceived(new MessageReceivedEvent() {
			@Override
			public void messageReceived(Message message) {
				final ChatMessage chatMessage = (ChatMessage) message;
				if (chatWindow.chatWindow.getText().trim().length() > 0) {
					chatWindow.chatWindow.setText(chatWindow.chatWindow.getText() + "\n");
				}
				chatWindow.chatWindow.setText(chatWindow.chatWindow.getText() + 
						chatMessage.getClientName() + ": " + chatMessage.getMessage());
				chatWindow.chatWindow.setCaretPosition(chatWindow.chatWindow.getText().length());
			}
		});
	}

}
