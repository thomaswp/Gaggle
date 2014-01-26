package com.gaggle;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Level2 extends Level {

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
		
		objects.add(new Box(world, new Rectangle(0, -75, 150, 150), new Color(0x91794C), 100, 200));
		objects.add(new Box(world, new Rectangle(50, -225, 150, 150), new Color(0x91794C), 100, 200));
		
		return objects;
	}

	@Override
	public Vector2f getDimensions() {
		return new Vector2f(2800, 1200);
	}

	@Override
	public int getMaxGeese() {
		return 20;
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
		return 2;
	}

	@Override
	public Level nextLevel() {
		return new Level1();
	}

}
