package com.shenzai.io;


public class Log {


	public static void info(final String message) {
		System.out.println(String.format("[I] %s", message));
	}
	
	public static void err(final String message) {
		System.err.println(String.format("[E] %s", message));
	}
	
	public static void debug(final String message) {
		System.out.println(String.format("[D] %s", message));
	}
	
	public static void main(String[] args) {
		info("Test info");
		err("Test err");
		debug("Test debug");
	}
	
}
