package com.gaggle;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;


public class Game extends BasicGame {
	
	private GameWorld world;
	
	public Game() {
		super("Gaggle");
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		world.render(container, g);
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		world = new GameWorld(container, new Level1());
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		world.update(container, delta);
	}

	public static void main(String[] args)
			throws SlickException {
		
		CreatureMap.initRandomMap();
		
		
		AppGameContainer app =
				new AppGameContainer(new Game());

//		app.setAlwaysRender(true);
		app.setDisplayMode(1200, 800, false);
		app.setShowFPS(false);
		app.start();
		app.getGraphics().setAntiAlias(true);
	}
}
