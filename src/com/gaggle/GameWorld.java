package com.gaggle;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gaggle.Platform.PlatformType;


public class GameWorld implements GameObject, MouseListener, ContactListener {

	private final static int SPAWN_TIME = 5000;
	private static final int MAX_POOL = 10;
	
	private World world;
	private List<GameObject> gameObjects = new ArrayList<>();
	private Vector2f origin = new Vector2f();
	private float scale = 1, targetScale = scale;
	private Vector2f worldDimensions = new Vector2f(2800, 1200);
	private float minScale;
	private Vector2f resolution;
	private List<Goose> geese = new ArrayList<>();
	private List<Chromosome> chromosomes = new ArrayList<>();
	
	private Background background;
	
	private int untilSpawn = SPAWN_TIME;
	
	
	public GameWorld(GameContainer container) {
		world = new World(new Vec2(0, 10));
		
		world.setContactListener(this);
		
		minScale = Math.min(1, container.getWidth() / worldDimensions.x);
		minScale = Math.min(minScale, container.getHeight() / worldDimensions.y);
		
		resolution = new Vector2f(container.getWidth(), container.getHeight());
		
		background = new Background();
		
		float w = worldDimensions.x, h = worldDimensions.y;
		float dw = w / 2;
		float border = 50;
		gameObjects.add(new Platform(world, new Rectangle(-dw, -h, w, border), PlatformType.Ceiling));
		gameObjects.add(new Platform(world, new Rectangle(-dw, -h, border, h), PlatformType.Wall));
		gameObjects.add(new Platform(world, new Rectangle(dw, -h, border, h + border), PlatformType.Wall));
		gameObjects.add(new Platform(world, new Rectangle(-dw, 0, w + border, border), PlatformType.Floor));
		for (int i = 0; i < 5; i++) {
			Goose goose = new Goose(world, new Vector2f(-300 + i * 200, -100), new Chromosome());
			geese.add(goose);
			chromosomes.add(goose.chromosome);
			gameObjects.add(goose);
		}
		
		container.getInput().addMouseListener(this);
	}

	@Override
	public void update(GameContainer container, int delta) {
		world.step(delta / 1000f, 10, 10);
		for (GameObject obj : gameObjects) {
			obj.update(container, delta);
		}
		scale = Util.lerp(scale, targetScale, 0.1f);
		background.update(container, delta);
		
		untilSpawn -= delta;
		if (untilSpawn <= 0) {
			untilSpawn += SPAWN_TIME;
			
			Goose goose = geese.remove(0);
			if (goose.isSelected()) {
				chromosomes.add(goose.chromosome);
				if (chromosomes.size() > MAX_POOL) {
					chromosomes.remove(0);
				}
			}
			gameObjects.remove(goose);
			Chromosome c = chromosomes.get((int)(chromosomes.size() * Math.random())).clone();
//			c.mutate();
			Goose newGoose = new Goose(world, new Vector2f(-300 + (int)(Math.random() * 5) * 200, -100), c);
			gameObjects.add(newGoose);
			geese.add(newGoose);
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) {
		
		background.render(container, g, scale, origin);
		
		g.pushTransform();
		g.translate(container.getWidth() / 2, container.getHeight() / 2);
		g.scale(scale, scale);
		g.translate(-origin.x, -origin.y);
		for (GameObject obj : gameObjects) {
			obj.render(container, g);
		}
		g.popTransform();
	}

	private void updateBounds() {
		origin.x = Math.min(Math.max(origin.x, -worldDimensions.x / 2), worldDimensions.x / 2);
		origin.y = Math.min(Math.max(origin.y, -worldDimensions.y), -worldDimensions.y / 6 / scale);
	}
	
	private Vector2f mouseToWorldCoordinates(float x, float y) {
		x -= resolution.x / 2;
		y -= resolution.y / 2;
		x /= scale;
		y /= scale;
		x += origin.x;
		y += origin.y;
		
		return new Vector2f(x, y);
		
	}
	
	@Override
	public void inputEnded() { }

	@Override
	public void inputStarted() { }

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void setInput(Input input) { }

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		origin.x -= (newx - oldx) / scale;
		origin.y -= (newy - oldy) / scale;
		updateBounds();
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {

	}

	@Override
	public void mousePressed(int button, int x, int y) {
		Vector2f coords = mouseToWorldCoordinates(x, y);
		for (Goose goose : geese) {
			if (goose.isClicked(coords)) {
				goose.toggleSelected();
			}
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
	
	}

	@Override
	public void mouseWheelMoved(int change) {
		targetScale *= Math.pow(1.001, change);
		targetScale = Math.min(Math.max(minScale, targetScale), 10f);
		updateBounds();
	}

	private void handleBeginContact(GameObject o1, GameObject o2, boolean firstPass) {
		if (o1 instanceof Goose) {
			((Goose) o1).addTouchingObject( o2);
		}
		
		if (firstPass) {
			handleBeginContact(o2, o1, false);
		}
	}
	
	private void handleEndContact(GameObject o1, GameObject o2, boolean firstPass) {
		if (o1 instanceof Goose) {
			((Goose) o1).removeTouchingObject( o2);
		}
		
		if (firstPass) {
			handleEndContact(o2, o1, false);
		}
	}
	
	@Override
	public void beginContact(Contact contact) {
		if (contact.m_fixtureA.getUserData() instanceof GameObject &&
				contact.m_fixtureB.getUserData() instanceof GameObject) {
			handleBeginContact((GameObject) contact.m_fixtureA.getUserData(),
					(GameObject) contact.m_fixtureB.getUserData(), true);
		}
	}

	@Override
	public void endContact(Contact contact) {
		if (contact.m_fixtureA.getUserData() instanceof GameObject &&
				contact.m_fixtureB.getUserData() instanceof GameObject) {
			handleEndContact((GameObject) contact.m_fixtureA.getUserData(),
					(GameObject) contact.m_fixtureB.getUserData(), true);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) { }

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) { }
	
}
