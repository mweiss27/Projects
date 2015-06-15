package com.weiss.chat_client.message;

public class ChatMessage extends Message {

	private static final long serialVersionUID = 1L;
	
	private int clientId;
	private String clientName;
	public long receivedAt = -1L;
	
	public ChatMessage(final int clientId, final String clientName, final String message) {
		super(message);
		this.clientId = clientId;
		this.clientName = clientName;
	}
	
	public int getClientId() {
		return this.clientId;
	}
	
	public String getClientName() {
		return this.clientName;
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
