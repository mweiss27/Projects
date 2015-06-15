package com.weiss.chat_client.message;

import java.io.Serializable;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public Message(final String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
