package com.weiss.chat_client.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Server-stored information regarding each of its Clients 
 */
public class ClientInfo {

	public int id;
	public String name;
	public DataInputStream readIn;
	public DataOutputStream writeOut;
	
}
