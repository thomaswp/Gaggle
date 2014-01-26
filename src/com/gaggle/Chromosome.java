package com.gaggle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Chromosome implements Cloneable {
	public float density = 0.5f, scale = 0.5f, maxSpeed = 0.5f, acceleration = 0.5f, restitution = 0.5f, jump = 0.5f, sight = 0.5f, collision = 0.5f;
	public ArrayList<Behavior> behaviorList;
	
	static Random rand = new Random();
	
	public Chromosome(int actionCount) {
		density = getPseudorandom();
		scale = getPseudorandom();
		maxSpeed = getPseudorandom();
		acceleration = getPseudorandom();
		restitution = getPseudorandom();
		jump = getPseudorandom();
		sight = getPseudorandom();
		collision = getPseudorandom();
		
		behaviorList = new ArrayList<Behavior>(actionCount);
		for(int i = 0; i < actionCount; i++) {
			behaviorList.add(new Behavior());
		}
	}
	
	public Chromosome() {
		this(1);
	}
	
	public float getPseudorandom() {
		float r = (float) rand.nextGaussian();
		r *= 0.5f;
		r = Math.max(Math.min(r, 1), -0.8f);
		r += 1;
		r *= 0.5f;
		return r;
	}
	
	public Chromosome clone() {
		Chromosome c = new Chromosome();
		c.density = density;
		c.scale = scale;
		c.maxSpeed = maxSpeed;
		c.acceleration = acceleration;
		c.restitution = restitution;
		c.jump = jump;
		c.sight = sight;
		c.collision = collision;
		
		for(Behavior b : behaviorList) {
			c.behaviorList.add(b.clone());
		}
		
		return c;
	}
	
	public static final int MIN_MUTATIONS = 1;
	public static final int MAX_MUTATIONS = 5;
	
	//probability that a mutation is a trait mutation (not a behavioral mutation)
	public static final double PRB_TRAIT_MUT = .5;
	
	//probability that a Condition is mutated (not an action)
	public static final double PRB_COND_MUT = .5;
	
	
	
	public void mutate() {
		int numMut = rand.nextInt(MAX_MUTATIONS-MIN_MUTATIONS)+MIN_MUTATIONS;
		for(int i = 0; i < numMut; i++) {
			double a = rand.nextDouble();
			if(a < PRB_TRAIT_MUT) {
				//mutate a trait
				int b = rand.nextInt(8);
				
				float w1 = .6f;
				float w2 = .4f;
				
				float c = getPseudorandom()*w2;
				switch(b) {
				case 0:
					density = density*w1 + c;
					break;
				case 1:
					scale = scale*w1 + c;
					break;
				case 2:
					maxSpeed = maxSpeed*w1 + c;
					break;
				case 3:
					acceleration = acceleration*w1 + c;
					break;
				case 4:
					restitution = restitution*w1 + c;
					break;
				case 5:
					jump = jump*w1 + c;
				case 6:
					sight = sight*w1 + c;
				case 7:
					collision = collision*w1 + c;
				} 
			} else {
				//mutate a behavior
				if(behaviorList.size() > 0) {
					int b = rand.nextInt(behaviorList.size());
					Behavior behavior = behaviorList.get(b);
					double c = rand.nextDouble();
					if(c < PRB_COND_MUT) {
						//mutate conditional
						behavior.condition = Condition.random();
					} else {
						//mutate action
						behavior.action = Action.random();
					}
				}
			}
		}
	}
	
	
	
	public Chromosome breed(Chromosome other) {
		Chromosome baby = new Chromosome();
		baby.density = (float) selectRandom(density, other.density);
		baby.acceleration = (float) selectRandom(acceleration, other.acceleration);
		baby.scale = (float) selectRandom(scale, other.scale);
		baby.maxSpeed = (float) selectRandom(maxSpeed, other.maxSpeed);
		baby.restitution = (float) selectRandom(restitution, other.restitution);
		baby.jump = (float) selectRandom(jump, other.jump);
		baby.sight = (float) selectRandom(sight, other.sight);
		baby.collision = (float) selectRandom(collision, other.collision);
		
		Iterator<Behavior> i1 = behaviorList.iterator();
		Iterator<Behavior> i2 = other.behaviorList.iterator();
		while(i1.hasNext() || i2.hasNext()) {
			if(i1.hasNext() && i2.hasNext()) {
				baby.behaviorList.add(((Behavior)selectRandom(i1.next(),i2.next())).clone());
			} else if(i1.hasNext()) {
				baby.behaviorList.add(i1.next().clone());
			} else {
				baby.behaviorList.add(i2.next().clone());
			}
		}
		
		return baby;
	}
	
	public static Object selectRandom(Object a, Object b) {
		double r = rand.nextDouble();
		if(r < .5) {
			return a;
		}
		return b;
	}
}
