package com.gaggle;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.geom.Vector2f;

public class Constant {
	public final static int PIXELS_PER_METER = 30;
	public final static float DEGREES_PER_RADIAN = 180 / (float) Math.PI;
	
	public static float pixelsToMeters(double pixels) {
		return (float) pixels / PIXELS_PER_METER;
	}
	
	public static Vector2f metersToPixels(Vec2 meters) {
		return new Vector2f(metersToPixels(meters.x), metersToPixels(meters.y));
	}
	
	public static float metersToPixels(double meters) {
		return (float) meters * PIXELS_PER_METER;
	}
	
	public static Vec2 pixelsToMeters(Vector2f meters) {
		return new Vec2(pixelsToMeters(meters.x), pixelsToMeters(meters.y));
	}

	public static float radiansToDegrees(float radians) {
		return radians * DEGREES_PER_RADIAN;
	}
}
