package com.gaggle;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public abstract class PhysicsObject implements GameObject {
	protected Body body;

	protected abstract void renderLocal(GameContainer container, Graphics g);
	
	@Override
	public void update(GameContainer container, int delta) {
		
		
	}

	@Override
	public void render(GameContainer container, Graphics g) {
		g.pushTransform();
		g.translate(Constant.metersToPixels(body.getPosition().x), 
				Constant.metersToPixels(body.getPosition().y));
		g.rotate(0, 0, Constant.radiansToDegrees(body.getAngle()));
		renderLocal(container, g);
		g.popTransform();
	}
	
	protected PolygonShape createShape(Polygon poly) {
		PolygonShape shape = new PolygonShape();
		Vec2[] points = new Vec2[poly.getPointCount()];
		for (int i = 0; i < points.length; i++) {
			float[] p = poly.getPoint(i);
			Vec2 point = Constant.pixelsToMeters(new Vector2f(p));
			points[i] = point;
		}
		shape.set(points, points.length);
		return shape;
	}
	
	protected CircleShape createShape(Circle circle) {
		CircleShape shape = new CircleShape();
		shape.m_p.set(Constant.pixelsToMeters(new Vector2f(circle.getCenter())));
		shape.m_radius = Constant.pixelsToMeters(circle.radius);
		return shape;
	}
	
	protected PolygonShape createShape(Rectangle rect) {
		PolygonShape shape = new PolygonShape();
		float width = Constant.pixelsToMeters(rect.getWidth());
		float height = Constant.pixelsToMeters(rect.getHeight());
		Vec2 pos = Constant.pixelsToMeters(new Vector2f(rect.getCenter()));
		shape.setAsBox(width / 2, height / 2, pos, 0);
		return shape;
	}
	
	protected org.jbox2d.collision.shapes.Shape createShape(Shape shape) {
		if (shape instanceof Rectangle) {
			return createShape((Rectangle) shape);
		} else if (shape instanceof Circle) {
			return createShape((Circle) shape);
		} else if (shape instanceof Polygon) {
			return createShape((Polygon) shape);
		} else if (shape instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) shape;
			Circle circle = new Circle(ellipse.getCenterX(), ellipse.getCenterY(), (ellipse.getWidth() + ellipse.getHeight()) / 4);
			return createShape(circle);
		} else {
			Debug.log(shape);
		}
		return null;
	}
}
