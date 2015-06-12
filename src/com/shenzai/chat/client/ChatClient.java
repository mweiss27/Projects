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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.shenzai.chat.message.ChatClientRequestMessage;
import com.shenzai.chat.message.ChatMessage;
import com.shenzai.chat.message.Message;
import com.shenzai.chat.message.MessageReceivedEvent;
import com.shenzai.chat.message.Request;
import com.shenzai.chat.message.SystemMessage;
import com.shenzai.chat.server.ChatServer;
import com.shenzai.chat.ui.cards.ChatWindow;
import com.shenzai.io.Log;

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
						//any.printStackTrace();
					}
				}
				Log.info("[Client] " + ChatClient.this.getName() + " no longer active. Ending inputThread.");
			}
		});

		this.setOnSystemMessageReceived(new MessageReceivedEvent() {
			@Override
			public void messageReceived(Message message) {
				Log.info(ChatClient.this.getName() + " received System Message:" + message.getMessage());
				if (message.getMessage().equals(ChatServer.SHUTDOWN_MESSAGE)) {
					ChatClient.this.disconnect();
				}
			}
		});
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
				final String[] userList = ChatClient.this.pollUserList();
				if (ChatClient.this.chatWindow != null) {
					ChatClient.this.chatWindow.connectedUsers.setListData(userList);
				}
			}
		}, 0, 10, TimeUnit.SECONDS);
	}

	public String[] pollUserList() {
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
				return (String[]) request.getAnswer();
			}
			Log.err("[Client] Invalid return type for request answer. Expected String[], returned: " + request.getAnswer().getClass());
		}
		else {
			Log.err("[Client] Request answer was null.");
		}
		return new String[] { };
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
			if (readIn != null) {
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

		} catch (Exception e) {
			e.printStackTrace();
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
	}

}
