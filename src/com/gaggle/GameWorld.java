package com.gaggle;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gaggle.Platform.PlatformType;


public class GameWorld implements GameObject, MouseListener, ContactListener {
	
	private World world;
	private List<GameObject> gameObjects = new ArrayList<>();
	private Vector2f origin = new Vector2f();
	private float scale = 1, targetScale = scale;
	private float minScale;
	private Vector2f resolution;
	private List<Goose> geese = new ArrayList<>();
	private List<Chromosome> chromosomes = new ArrayList<>();
	private Level level;
	
	private Background background;
	
	private int untilSpawn;
	private Vector2f worldDimensions;
	private Rectangle spawn, goal;
	private int pointsRemaining;
	private TrueTypeFont font;
	
	public GameWorld(GameContainer container, Level level) {
		world = new World(new Vec2(0, 10));
		world.setContactListener(this);
		resolution = new Vector2f(container.getWidth(), container.getHeight());
		container.getInput().addMouseListener(this);
		loadLevel(level);
	}

	private void loadLevel(Level level) {
		this.level = level;
		
		untilSpawn = level.getSpawnTime();
		worldDimensions = level.getDimensions();
		spawn = level.getSpawn();
		goal = level.getGoal();
		pointsRemaining = level.getMaxGeese() / 2;
		
		font = new TrueTypeFont(new Font("Arial", Font.BOLD, 50), true);
				
		minScale = Math.min(1, resolution.x / worldDimensions.x);
		minScale = Math.min(minScale,  resolution.y / worldDimensions.y);
		
		
		background = new Background();
		
		float w = worldDimensions.x, h = worldDimensions.y;
		float dw = w / 2;
		float border = 50;
		Color borderColor = new Color(0x4D5FB3);
		gameObjects.add(new Platform(world, new Rectangle(-dw, -h, w, border), PlatformType.Ceiling, borderColor));
		gameObjects.add(new Platform(world, new Rectangle(-dw, -h, border, h), PlatformType.Wall, borderColor));
		gameObjects.add(new Platform(world, new Rectangle(dw, -h, border, h + border), PlatformType.Wall, borderColor));
		gameObjects.add(new Platform(world, new Rectangle(-dw, 0, w + border, border), PlatformType.Floor, borderColor));
		
		for (GameObject obj : level.getObjects(world)) {
			gameObjects.add(obj);
		}
		
//		for (int i = 0; i < level.getMaxPool(); i++) {
//			chromosomes.add(new Chromosome(level.getActionCount()));
//		}
		
		Debug.log(chromosomes.size());
	}

	@Override
	public void update(GameContainer container, int delta) {
		world.step(delta / 1000f, 10, 10);
		for (GameObject obj : gameObjects) {
			obj.update(container, delta);
		}
		scale = Util.lerp(scale, targetScale, 0.1f);
		background.update(container, delta);
		
		for (int i = 0; i < geese.size(); i++) {
			Goose goose = geese.get(i);
			Vector2f pos = goose.getPosition();
			if (goal.contains(pos.x, pos.y)) {
				removeGoose(goose);
				i--;
				pointsRemaining--;
				if (checkWin()) {
					return;
				}
			}
		}
		
		if (level == null) return;
		untilSpawn -= delta * (geese.size() == level.getMaxGeese() ? 1 : 5);
		if (untilSpawn <= 0) {
			untilSpawn += level.getSpawnTime();
			
			if (geese.size() >= level.getMaxGeese()) {
				Goose goose = geese.get(0);
				removeGoose(goose);
			}
			
			Chromosome c1, c2;
			
			if(chromosomes.size() > 0) {
				double prbOld = chromosomes.size()/(double) level.getMaxPool();
				if(Math.random() < prbOld) {
					c1 = chromosomes.get((int)(chromosomes.size() * Math.random()));
					c2 = chromosomes.get((int)(chromosomes.size() * Math.random()));
				} else {
					c1 = new Chromosome(level.getActionCount());
					c2 = new Chromosome(level.getActionCount());
				}
			} else {
				c1 = new Chromosome(level.getActionCount());
				c2 = new Chromosome(level.getActionCount());
			}
			Chromosome c = c1.breed(c2);
			c.mutate();
			Goose newGoose = new Goose(world, level.getRandomSpawn(), c);
			gameObjects.add(newGoose);
			geese.add(newGoose);
		}
	}

	private boolean checkWin() {
		if (pointsRemaining == 0) {
			for (Goose goose : geese) {
				goose.dispose();
			}
			gameObjects.clear();
			geese.clear();
			chromosomes.clear();
			Level next = level.nextLevel();
			if (next != null) {
				loadLevel(next);
			} else {
				level = null;
			}
			return true;
		}
		return false;
	}

	private void removeGoose(Goose goose) {
		geese.remove(goose);
		if (goose.isSelected()) {
			chromosomes.add(goose.chromosome);
			if (chromosomes.size() > level.getMaxPool()) {
				chromosomes.remove(0);
			}
		}
		gameObjects.remove(goose);
		goose.dispose();
	}

	@Override
	public void render(GameContainer container, Graphics g) {
		
		background.render(container, g, scale, origin);
		
		g.pushTransform();
		g.translate(container.getWidth() / 2, container.getHeight() / 2);
		g.scale(scale, scale);
		g.translate(-origin.x, -origin.y);
		
		g.setColor(new Color(0xAA9DA6D1));
		g.fill(spawn);
		g.setColor(new Color(0xAA000000));
		g.draw(spawn);
		
		g.setColor(new Color(0xAA6EEBA8));
		g.fill(goal);
		g.setColor(new Color(0xAA000000));
		g.draw(goal);
		g.setFont(font);
		String text = "" + pointsRemaining;
		float w = font.getWidth(text);
		g.drawString(text, goal.getCenterX() - w / 2, goal.getCenterY() - font.getHeight() / 2);
		
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
