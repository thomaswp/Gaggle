package com.gaggle;

import java.util.Random;

public enum Condition {
	BoxInFront, PlatformInFront, Ledge, Touching; //, UpsideDown;
	
	private static Condition[] vals = values();
	private static Random rand = new Random();
	
	public static Condition random() {
		return vals[rand.nextInt(vals.length)];
	}
}
