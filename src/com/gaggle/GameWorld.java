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
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gaggle.Platform.PlatformType;


public class GameWorld implements GameObject, MouseListener, ContactListener, KeyListener {
	
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
	private Rectangle spawn, goal, goalRefill = new Rectangle(0, 0, 0, 0);
	private String[] hintLines;
	private int pointsRemaining;
	private TrueTypeFont font;
	private int regenTimer;
	
	public GameWorld(GameContainer container, Level level) {
		world = new World(new Vec2(0, 10));
		world.setContactListener(this);
		resolution = new Vector2f(container.getWidth(), container.getHeight());
		container.getInput().addMouseListener(this);
		container.getInput().addKeyListener(this);
		loadLevel(level);
		origin.set(0, -300);
		scale = targetScale = 0.7f;
	}

	private void loadLevel(Level level) {
		this.level = level;
		
		untilSpawn = level.getSpawnTime();
		worldDimensions = level.getDimensions();
		spawn = level.getSpawn();
		goal = level.getGoal();
		pointsRemaining = level.getPointsToWin();
		hintLines = level.getHintText().split("\n");
		
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
		
		regenTimer = 0;
		
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
				regenTimer = 0;
				if (checkWin()) {
					return;
				}
			}
		}
		
		regenTimer += delta;
		if (level.getRegenerationRate() > 0 && regenTimer > level.getRegenerationRate()) {
			regenTimer -= level.getRegenerationRate();
			pointsRemaining = Math.min(pointsRemaining + 1, level.getPointsToWin());
		}
		
		if (level == null) return;
		untilSpawn -= delta * (geese.size() == level.getMaxGeese() ? 1 : 5);
		if (untilSpawn <= 0) {
			untilSpawn += level.getSpawnTime();
			
			if (geese.size() >= level.getMaxGeese()) {
				Goose goose = geese.get(0);
				removeGoose(goose);
			}
			
			createNewGoose();
		}
	}

	private void createNewGoose() {
		Chromosome c;
		if (Math.random() < 0.5) {
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
			c = c1.breed(c2);
		} else {
			if(chromosomes.size() > 0) {
				double prbOld = chromosomes.size()/(double) level.getMaxPool();
				if(Math.random() < prbOld) {
					c = chromosomes.get((int)(chromosomes.size() * Math.random())).clone();
				} else {
					c = new Chromosome(level.getActionCount());
				}
			} else {
				c = new Chromosome(level.getActionCount());
			}
		}
		c.mutate();
		Goose newGoose = new Goose(world, level.getRandomSpawn(), c);
		gameObjects.add(newGoose);
		geese.add(newGoose);
	}

	private boolean checkWin() {
		if (pointsRemaining == 0) {
			for (GameObject object : gameObjects) {
				object.dispose(world);
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
	
	private void resetPopulation() {
		for (Goose goose : geese) {
			gameObjects.remove(goose);
			goose.dispose();
		}
		geese.clear();
		chromosomes.clear();
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
		
		g.setFont(font);
		g.setColor(Color.black);
		Vector2f loc = level.getHintTextLocation();
		float y = loc.y - hintLines.length * font.getHeight() * 0.75f;
		for (String line : hintLines) {
			float w = font.getWidth(line);
			g.drawString(line, loc.x - w / 2, y);
			y += font.getHeight() * 1.5f;
		}
		
		g.setColor(new Color(0xAA9DA6D1));
		g.fill(spawn);
		g.setColor(new Color(0xAA000000));
		g.draw(spawn);
		
		g.setColor(new Color(0xAA6EEBA8));
		g.fill(goal);
		float fillHeight = (level.getRegenerationRate() > 0 && pointsRemaining < level.getPointsToWin()) ? 
				(float)regenTimer / level.getRegenerationRate() : 0;
		fillHeight *= goal.getHeight();
		goalRefill.setBounds(goal.getMinX(), goal.getMaxY() - fillHeight, goal.getWidth(), fillHeight);
		g.setColor(new Color(0x55777777));
		g.fill(goalRefill);
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
		if (moving) {
			origin.x -= (newx - oldx) / scale;
			origin.y -= (newy - oldy) / scale;
			updateBounds();
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {

	}

	boolean moving = false;
	@Override
	public void mousePressed(int button, int x, int y) {
		if (button == 1) {
			moving = true;
		} else {
			Vector2f coords = mouseToWorldCoordinates(x, y);
			for (Goose goose : geese) {
				if (goose.isClicked(coords)) {
					goose.toggleSelected();
					return;
				}
			}
			for (Goose goose : geese) {
				if (goose.getPosition().distance(coords) < 35) {
					goose.toggleSelected();
					return;
				}
			}
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if (button == 1) moving = false;
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

	@Override
	public void dispose(World world) {
		
	}

	@Override
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_R) {
			resetPopulation();
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		
	}
	
}
