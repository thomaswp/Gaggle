package com.gaggle;

import java.util.List;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public abstract class Level {
	public abstract Rectangle getSpawn();
	public abstract Rectangle getGoal();
	public abstract List<PhysicsObject> getObjects(World world);
	public abstract Vector2f getDimensions();
	public abstract int getMaxGeese();
	public abstract int getMaxPool();
	public abstract int getSpawnTime();
	public abstract int getActionCount();
	public abstract Level nextLevel();
	public abstract int getRegenerationRate();
	public abstract String getHintText();
	
	public Vector2f getHintTextLocation() {
		return new Vector2f(0, -300);
	}
	
	protected Vector2f getRandomSpawn() {
		Rectangle spawn = getSpawn();
		float offX = (float) (Math.random() * spawn.getWidth());
		float offY = (float) (Math.random() * spawn.getHeight());
		return new Vector2f(spawn.getX() + offX, spawn.getY() + offY);
	}
	
	public int getPointsToWin() {
		return getMaxGeese() / 2;
	}
	
	public static Level getStartLevel() {
		return new Level4();
	}

}


