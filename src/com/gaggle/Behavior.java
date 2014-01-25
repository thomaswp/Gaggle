package com.gaggle;

public class Behavior implements Cloneable {
	public Condition condition;
	public Action action;
	
	public Behavior(Condition condition, Action action) {
		this.condition = condition;
		this.action = action;
	}
	
	//create a random behavior
	public Behavior() {
		this.condition = Condition.random();
		this.action = Action.random();
	}
	
	public Behavior clone() {
		return new Behavior(condition, action);
	}
}
