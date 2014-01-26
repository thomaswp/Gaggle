package com.gaggle;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public interface GameObject {
	void update(GameContainer container, int delta);
	void render(GameContainer container, Graphics g);
	void dispose(World world);
}
