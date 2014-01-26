package com.gaggle;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gaggle.Platform.PlatformType;

public class Level5 extends Level {

	@Override
	public Rectangle getSpawn() {
		return new Rectangle(-1300, -1100, 200, 200);
	}

	@Override
	public Rectangle getGoal() {
		return new Rectangle(1000, -600, 200, 200);
	}

	@Override
	public List<PhysicsObject> getObjects(World world) {
		ArrayList<PhysicsObject> objects = new ArrayList<>();
		
		Color borderColor = new Color(0x4D5FB3);
		objects.add(new Platform(world, new Rectangle(-1350, -900, 750, 900), PlatformType.Floor, borderColor));	
		objects.add(new Platform(world, new Rectangle(-200, -900, 600, 50), PlatformType.Floor, borderColor));	
		objects.add(new Platform(world, new Rectangle(460, -900, 940, 50), PlatformType.Floor, borderColor));	
		objects.add(new Platform(world, new Rectangle(200, -400, 1200, 50), PlatformType.Floor, borderColor));	
		objects.add(new Platform(world, new Rectangle(200, -850, 50, 450), PlatformType.Floor, borderColor));	
//		objects.add(new Box(world, new Rectangle(0, -75, 150, 150), new Color(0x91794C), 100, 200));
//		objects.add(new Box(world, new Rectangle(50, -225, 150, 150), new Color(0x91794C), 100, 200));
		
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

	@Override
	public int getRegenerationRate() {
		return 20000;
	}

	@Override
	public String getHintText() {
		return "Good luck!";
	}

	
	@Override
	public Vector2f getHintTextLocation() {
		return new Vector2f(-250, -500);
	}
}
