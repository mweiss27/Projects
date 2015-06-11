package com.shenzai.chat.message;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int clientId;
	private String message;
	public boolean received;
	
	public Message(final int clientId, final String message) {
		this.clientId = clientId;
		this.message = message;
	}
	
	public int getClientId() {
		return this.clientId;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public String toString() {
		return (this.received ? "[R] " : "") + this.getMessage();
	}
	
}
