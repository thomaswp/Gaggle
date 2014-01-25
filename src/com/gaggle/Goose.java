package com.gaggle;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.gaggle.Platform.PlatformType;

public class Goose extends PhysicsObject {

	protected Circle circleA, circleB;
	protected Rectangle rect;
	protected Chromosome chromosome;
	protected List<GameObject> touchingPlatforms = new ArrayList<>();
	protected World world;
	protected Circle c = new Circle(0, 0, 5);
	
	protected int dir = 1;
	protected float speed = 6, targetSpeed = speed;
	
	protected boolean isGrounded, isPlatformInFront, isLedgeInFront, isTouchingGoose, isUpsideDown;
	
	public Goose(World world, Vector2f position, Chromosome chromosome) {
		this.world = world;
		this.chromosome = chromosome;
		float radius = 40 * chromosome.scale;
		circleA = new Circle(-radius / 2, 0, radius);
		circleB = new Circle(radius / 2, 0, radius);
		rect = new Rectangle(-radius * 0.75f, -radius * 0.95f, radius * 1.5f, radius * 1.9f);
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(Constant.pixelsToMeters(position));
		body = world.createBody(bodyDef);
		body.setType(BodyType.DYNAMIC);
		for (int i = 0; i < 2; i++) {
			CircleShape shape = new CircleShape();
			shape.setRadius(Constant.pixelsToMeters(radius));
			shape.m_p.x = Constant.pixelsToMeters(radius * (i - 0.5));
			Fixture f = body.createFixture(shape, chromosome.density);
			f.setRestitution(chromosome.restitution * 0.3f);
			f.setUserData(this);
		}
		c.setCenterX(radius);
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
		
		speed = Util.lerp(speed, targetSpeed, 0.9f);
		if (isGrounded) {
			body.applyForceToCenter(new Vec2(speed * body.m_mass * dir, 0));
		}
		
	}

	private void doActions() {
		if (isPlatformInFront) {
			System.out.println(isPlatformInFront);
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
		
		isUpsideDown = ((body.getAngle() + Math.PI * 2) % (Math.PI * 2)) < Math.PI;
	}
	
	private void turnAround() {
		dir *= -1;
	}
	
	private void jump() {
		if (isGrounded) {
			body.applyForceToCenter(new Vec2(0, 10 * chromosome.jump * body.m_mass));
		}
	}
	
	private void speedUp() {
		targetSpeed += chromosome.acceleration;
		targetSpeed = Math.min(targetSpeed, chromosome.maxSpeed * 6);
	}
	
	private void slowDown() {
		targetSpeed -= chromosome.acceleration;
		targetSpeed = Math.max(targetSpeed, 0);
	}
	
	
	private static class Flag {
		public boolean value;
	}

	@Override
	public void renderLocal(GameContainer container, Graphics g) {
		g.setColor(Color.red);
		g.fill(circleA);
		g.fill(circleB);
		g.fill(rect);
		
		g.setColor(Color.white);
		g.fill(c);
	}

}

