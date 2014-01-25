package com.gaggle;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;


public class Game extends BasicGame {

	private World world;
	
	public Game() {
		super("Gaggle");
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
	}

	public static void main(String[] args)
			throws SlickException {
		
		AppGameContainer app =
				new AppGameContainer(new Game());

//		app.setAlwaysRender(true);
		app.setDisplayMode(800, 600, false);
		app.setShowFPS(false);
		app.start();

	}
}
