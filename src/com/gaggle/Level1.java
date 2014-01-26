package com.gaggle;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Level1 extends Level {

	@Override
	public Rectangle getSpawn() {
		return new Rectangle(-1300, -300, 200, 200);
	}

	@Override
	public Rectangle getGoal() {
		return new Rectangle(1000, -200, 200, 200);
	}

	@Override
	public List<PhysicsObject> getObjects(World world) {
		ArrayList<PhysicsObject> objects = new ArrayList<>();
		return objects;
	}

	@Override
	public Vector2f getDimensions() {
		return new Vector2f(2800, 1200);
	}

	@Override
	public int getMaxGeese() {
		return 10;
	}

	@Override
	public int getMaxPool() {
		return 15;
	}

	@Override
	public int getSpawnTime() {
		return 1500;
	}

	@Override
	public int getActionCount() {
		return 1;
	}

	@Override
	public Level nextLevel() {
		return new Level2();
	}

	@Override
	public int getRegenerationRate() {
		return -1;
	}

}
