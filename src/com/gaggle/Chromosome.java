package com.gaggle;

import java.util.Random;

public class Chromosome {
	public float density = 0.5f, scale = 0.5f, maxSpeed = 0.5f, acceleration = 0.5f, restitution = 0.5f, jump = 0.5f;
	
	static Random rand = new Random();
	
	public Chromosome() {
		density = getPseudorandom();
		scale = getPseudorandom();
		maxSpeed = getPseudorandom();
		acceleration = getPseudorandom();
		restitution = getPseudorandom();
		jump = getPseudorandom();
	}
	
	public float getPseudorandom() {
		float r = (float) rand.nextGaussian();
		r *= 0.5f;
		r = Math.max(Math.min(r, 1), -0.8f);
		r += 1;
		r *= 0.5f;
		return r;
	}
}
