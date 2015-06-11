package com.shenzai.chat.message;

public class ChatMessage extends Message {

	private static final long serialVersionUID = 1L;
	
	private int clientId;
	public long receivedAt = -1L;
	
	public ChatMessage(final int clientId, final String message) {
		super(message);
		this.clientId = clientId;
	}
	
	public int getClientId() {
		return this.clientId;
	}
	
	public boolean isReceived() {
		return this.receivedAt != -1L;
	}
	
	public long getReceivedTimestamp() {
		return this.receivedAt;
	}
	
	@Override
	public String toString() {
		return (this.receivedAt != -1L ? "[R] " : "") + this.getMessage();
	}
	
}
