package com.shenzai.chat.util;

import java.awt.Font;
import java.net.InetAddress;
import java.util.prefs.Preferences;

import com.shenzai.chat.client.ChatClient;

public class ChatClientConfig {

	public static final InetAddress HOST_ADDRESS;
	public static final int PORT = 50500;
	private static final Font baseFont = new Font("Arial Unicode", Font.PLAIN, 12);
	
	private static Preferences prefs = Preferences.userNodeForPackage(ChatClient.class);
	
	static {
		InetAddress toUse = null;
		try {
			toUse = InetAddress.getByName("192.168.0.4");
		} catch (Exception e) {
			try {
				toUse = InetAddress.getLocalHost();
			} catch (Exception e1) {
				System.err.println("Apparently getting a host address is impossible.");
				System.exit(0);
			}
		}
		HOST_ADDRESS = toUse;
	}
	
	public static void set(final String key, final String value) {
		prefs.put(key, value);
	}
	
	public static String get(final String key) {
		return get(key, "");
	}
	
	public static String get(final String key, final String def) {
		return prefs.get(key, def);
	}
	
	public static final Font getFont() {
		return baseFont;
	}
	
	public static final Font getFont(final float size) {
		return baseFont.deriveFont(size);
	}
	
	public static final Font getFont(final int style) {
		return baseFont.deriveFont(style);
	}
	
	public static final Font getFont(final int style, final float size) {
		return baseFont.deriveFont(style, size);
	}
}
