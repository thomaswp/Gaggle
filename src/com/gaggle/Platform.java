package com.gaggle;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Platform extends PhysicsObject {

	public enum PlatformType {
		Wall, Ceiling, Floor
		
	}
	
	protected Rectangle rect = new Rectangle(0, 0, 0, 0);
	public final PlatformType type;
	
	public Platform(World world, Rectangle rect, PlatformType type) {
		this.rect.setWidth(rect.getWidth());
		this.rect.setHeight(rect.getHeight());
		this.rect.setCenterX(0);
		this.rect.setCenterY(0);
		this.type = type;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(Constant.pixelsToMeters(rect.getCenterX()), 
				Constant.pixelsToMeters(rect.getCenterY()));
		body = world.createBody(bodyDef);
		body.setType(BodyType.STATIC);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(Constant.pixelsToMeters(rect.getWidth() / 2), 
				Constant.pixelsToMeters(rect.getHeight()) / 2);
		Fixture f = body.createFixture(shape, 1);
		f.setUserData(this);
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		super.update(container, delta);
	}

	@Override
	public void renderLocal(GameContainer container, Graphics g) {
		g.setColor(Color.blue);
		g.fill(rect);
	}
	
}
