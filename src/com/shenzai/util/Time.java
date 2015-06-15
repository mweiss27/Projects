package com.shenzai.util;

import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Time {

	private static Map<Thread, Entry<Long, Long>> ticTocMap = new HashMap<>();

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

	public static long tic() {
		final long currentMillis = System.currentTimeMillis();

		ticTocMap.put(Thread.currentThread(), new SimpleEntry<>(currentMillis, -1L));

		return currentMillis;
	}

	public static long toc() {
		final long currentMillis = System.currentTimeMillis();

		if (!ticTocMap.containsKey(Thread.currentThread())) {
			throw new IllegalStateException("tic was not called from this Thread prior to toc");
		}

		final long result = currentMillis - ticTocMap.get(Thread.currentThread()).getKey();
		ticTocMap.remove(Thread.currentThread());
		return result;
	}

}
