package com.weiss.chat_client.message;

import java.io.Serializable;

public class ChatClientRequestMessage extends Message {

	private static final long serialVersionUID = 1L;

	public int requestId;
	private int clientId;
	private Object answer;
	private long timestamp;
	
	public ChatClientRequestMessage(final int clientId, final int request) {
		super(String.valueOf(request));
		this.clientId = clientId;
		this.timestamp = System.currentTimeMillis();
	}
	
	public <T extends Serializable> void setAnswer(final T answer) {
		this.answer = answer;
	}
	
	public int getClientId() {
		return this.clientId;
	}
	
	public Object getAnswer() {
		return this.answer;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof ChatClientRequestMessage) {
				ChatClientRequestMessage other = (ChatClientRequestMessage) obj;
				return other.timestamp == this.timestamp && other.getMessage().equals(this.getMessage());
			}
		}
		return false;
	}
	
}
