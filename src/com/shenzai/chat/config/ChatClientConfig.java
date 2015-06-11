package com.shenzai.chat.config;

import java.awt.Font;

public class ChatClientConfig {

	public static final int PORT = 50500;
	private static final Font baseFont = new Font("Arial Unicode", Font.PLAIN, 12);
	
	public static final Font getFont() {
		return baseFont;
	}
	
	public static final Font getFont(final float size) {
		return baseFont.deriveFont(size);
	}
	
	public static final Font getFont(final int style) {
		return baseFont.deriveFont(style);
	}
	
	public static final Font getFont(final int style, final int size) {
		return baseFont.deriveFont(style, size);
	}
}
