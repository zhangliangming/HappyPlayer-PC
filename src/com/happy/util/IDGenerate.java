package com.happy.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Administrator id自动生成器
 */
public class IDGenerate {
	private static final AtomicInteger integer = new AtomicInteger(0);

	public static String getId() {
		long time = System.currentTimeMillis();
		StringBuilder str = new StringBuilder(20);
		str.append(time);
		int intValue = integer.getAndIncrement();
		if (integer.get() >= 10000) {
			integer.set(0);
		}
		if (intValue < 10) {
			str.append("000");
		} else if (intValue < 100) {
			str.append("00");
		} else if (intValue < 1000) {
			str.append("0");
		}
		str.append(intValue);
		return str.toString();
	}

	public static String getId(int length) {
		long time = System.currentTimeMillis();
		StringBuilder str = new StringBuilder(length);
		str.append(time);
		int intValue = integer.getAndIncrement();
		if (integer.get() >= 10000) {
			integer.set(0);
		}
		if (intValue < 10) {
			str.append("000");
		} else if (intValue < 100) {
			str.append("00");
		} else if (intValue < 1000) {
			str.append("0");
		}
		str.append(intValue);
		return str.toString();
	}

	public static String getId(String key) {
		long time = System.currentTimeMillis();
		StringBuilder str = new StringBuilder(18);
		str.append(time);
		int intValue = integer.getAndIncrement();
		if (integer.get() >= 10000) {
			integer.set(0);
		}
		if (intValue < 10) {
			str.append("000");
		} else if (intValue < 100) {
			str.append("00");
		} else if (intValue < 1000) {
			str.append("0");
		}
		str.append(intValue);
		return key + str.toString();
	}

	public static String getId(String key, int length) {
		long time = System.currentTimeMillis();
		StringBuilder str = new StringBuilder(length);
		str.append(time);
		int intValue = integer.getAndIncrement();
		if (integer.get() >= 10000) {
			integer.set(0);
		}
		if (intValue < 10) {
			str.append("000");
		} else if (intValue < 100) {
			str.append("00");
		} else if (intValue < 1000) {
			str.append("0");
		}
		str.append(intValue);
		return key + str.toString();
	}

	// public static void main(String[] args) {
	// /*
	// * for (int i = 0; i < 100003; i++) { new Test().start(); }
	// */
	// System.out.println(IDGenerate.getId());
	// }
	//
	// private static class Test extends Thread {
	// @Override
	// public void run() {
	// System.out.println(IDGenerate.getId());
	// }
	// }
}