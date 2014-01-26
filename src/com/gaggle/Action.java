package com.gaggle;

import java.util.Random;

public enum Action {
	Jump; //SpeedUp, SlowDown, Turn, Jump, StartStop;
	
	private static Action[] vals = values();
	private static Random rand = new Random();
	
	public static Action random() {
		return vals[rand.nextInt(vals.length)];
	}
}
