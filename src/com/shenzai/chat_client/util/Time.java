package com.shenzai.chat_client.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

	public static void sleep(final int millis) {
		try {
			Thread.sleep(millis); 
		} catch (Exception ignored) {
		}
	}
	
	public static String getDayTimestamp(final long millis) {
		final SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd");
		return format.format(new Date(millis));
	}
	
	public static String getTimeTimestamp(final long millis) {
		final SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
		return format.format(new Date(millis));
	}
	
}
