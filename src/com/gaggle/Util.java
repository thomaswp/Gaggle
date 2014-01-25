package com.gaggle;


public class Util {
	
	public static float lerp(float x0, float x1, float t) {
		return x0 * (1 - t) + x1 * t; 
	}
}
