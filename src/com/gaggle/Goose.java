package com.gaggle;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gaggle.Platform.PlatformType;

public class Goose extends PhysicsObject {

	public static float MAX_DENSITY = 60, MAX_SPEED = 15, MAX_ACCEL = 3, MAX_SCALE = 50, MAX_RESTITUTION = 0.3f, MAX_JUMP = 10;
	
	protected Circle circleA, circleB;
	protected Rectangle rect, rectBase;
	protected Polygon plowA, plowB;
	protected Chromosome chromosome;
	protected List<GameObject> touchingPlatforms = new ArrayList<>();
	protected World world;
	protected Circle c = new Circle(0, 0, 5);
	protected CreatureRenderer renderer;
	
	protected int dir = 1;
	protected float targetSpeed = 6;
	
	protected boolean isGrounded, isPlatformInFront, isLedgeInFront, isTouchingGoose, isUpsideDown;
	protected boolean isMoving = true;
	
	public Goose(World world, Vector2f position, Chromosome chromosome) {
		this.world = world;
		this.chromosome = chromosome;
		this.renderer = new CreatureRenderer(chromosome);
		float radius = MAX_SCALE * chromosome.scale;
		circleA = new Circle(-radius / 2, 0, radius);
		circleB = new Circle(radius / 2, 0, radius);
		rect = new Rectangle(-radius * 0.75f, -radius * 0.95f, radius * 1.5f, radius * 1.9f);
		
		rectBase = new Rectangle(-radius * 1.5f, 0, radius * 3, radius * 0.9f);
		
		plowA = new Polygon(new float[] {
				radius * 1.5f, 0, radius * 2, radius * 0.5f, radius * 1.5f, radius
		});
		plowB = new Polygon(new float[] {
				-radius * 1.5f, 0, -radius * 2, radius, -radius * 1.5f, radius
		});

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(Constant.pixelsToMeters(position));
		body = world.createBody(bodyDef);
		body.setType(BodyType.DYNAMIC);
		
		addFixture(createShape(circleA));
		addFixture(createShape(circleB));
		addFixture(createShape(rectBase));
		
		Fixture[] plowFixes = new Fixture[] {
			addFixture(createShape(plowA)),
			addFixture(createShape(plowB)),
		};
		for (Fixture plowFix : plowFixes) {
			plowFix.setRestitution(0);
			plowFix.setFriction(1);
			plowFix.getFilterData().categoryBits = Constant.PLOW_BIT;
			plowFix.getFilterData().maskBits = Constant.PLOW_BIT;
		}
		
		
		c.setCenterX(radius);
	}
	
	protected Fixture addFixture(Shape shape) {
		Fixture f = body.createFixture(shape, chromosome.density * MAX_DENSITY);
		f.setFriction(1);
		f.setRestitution(chromosome.restitution * MAX_RESTITUTION);
		f.setUserData(this);
		f.getFilterData().categoryBits = Constant.GOOSE_BIT;
		return f;
	}
	
	public void addTouchingObject(GameObject platform) {
		touchingPlatforms.add(platform);
	}
	
	public void removeTouchingObject(GameObject platform) {
		touchingPlatforms.remove(platform);
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		calculateConditions();
		doActions();
		
		if (isGrounded && !isUpsideDown && isMoving) {
			Vec2 v = body.getLinearVelocity().clone();
			v.x = Util.lerp(v.x, targetSpeed * dir, 0.95f);
			body.setLinearVelocity(v);
		}
		
		renderer.update(delta, targetSpeed);
	}

	private void doActions() {
		if (isPlatformInFront) {
			turnAround();
			speedUp();
		}
	}
	
	private void calculateConditions() {
		isGrounded = isTouchingGoose = false;
		for (GameObject obj : touchingPlatforms) {
			if (obj instanceof Platform && ((Platform) obj).type == PlatformType.Floor) {
				isGrounded = true;
				break;
			} else if (obj instanceof Goose) {
				isTouchingGoose = true;
			}
		}
		
		final Flag flag = new Flag();
		Vec2 position = body.getPosition().clone();
		position.x += Constant.pixelsToMeters(circleA.radius * 1.7f * dir);
		
		world.queryAABB(new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fixture) {
				if (fixture.getUserData() instanceof Platform) {
					flag.value = true;
					return false;
				}
				return true;
			}
		}, new AABB(position, position));
		isPlatformInFront = flag.value;
		
		flag.value = false;
		position.y += Constant.pixelsToMeters(circleA.radius * 0.7f);
		world.queryAABB(new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fixture) {
				if (fixture.getUserData() instanceof Platform) {
					flag.value = true;
					return false;
				}
				return true;
			}
		}, new AABB(position, position));
		isLedgeInFront = flag.value;
		
		isUpsideDown = Math.abs(((body.getAngle() + Math.PI * 2) % (Math.PI * 2)) - Math.PI) < Math.PI * 0.6f;
	}
	
	private void turnAround() {
		dir *= -1;
	}
	
	private void jump() {
		if (isGrounded) {
			body.applyForceToCenter(new Vec2(0, MAX_JUMP * chromosome.jump * body.m_mass));
		}
	}
	
	private void speedUp() {
		targetSpeed += chromosome.acceleration * MAX_ACCEL;
		targetSpeed = Math.min(targetSpeed, chromosome.maxSpeed * MAX_SPEED);
	}
	
	private void slowDown() {
		targetSpeed -= chromosome.acceleration;
		targetSpeed = Math.max(targetSpeed, 0);
	}
	
	private void toggleMove() {
		isMoving = !isMoving;
	}
	
	private static class Flag {
		public boolean value;
	}

	@Override
	public void renderLocal(GameContainer container, Graphics g) {
		g.scale(dir, 1);
		
		
		
		g.setColor(new Color(1, 0, 0, 0.1f));
		g.fill(circleA);
		g.fill(circleB);
		g.fill(rect);
		
		g.setColor(new Color(1, 1, 1, 0.1f));
		g.fill(c);
		g.fill(rectBase);
		g.setColor(new Color(1, 1, 1, 0.1f));
		g.fill(plowA);
		g.fill(plowB);
		
		renderer.render(g);
		
	}

}

