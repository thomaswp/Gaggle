package com.gaggle;

public class Debug {
	
	
	public static void log(String format, Object... args) {
		log(String.format(format, args));
	}
	
	public static void log(double x) {
		System.out.println(x);
	}
	
	public static void log(int x) {
		System.out.println(x);
	}
	
	public static void log(Object x) {
		System.out.println(x);
	}
	
	public static void log(String x) {
		System.out.println(x);
	}
}
