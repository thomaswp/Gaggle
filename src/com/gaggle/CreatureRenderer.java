package com.gaggle;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class CreatureRenderer {
	
	public static float STANDARD_SIZE = 50;
	
	private Vector2f size;
	
	public final ShapeType foot;
	public final ShapeType body;
	public final ShapeType head;
	
	public final Shape leftFootShape;
	public final Shape rightFootShape;
	public final Shape bodyShape;
	public final Shape headShape;
	
	private Color bodyColor;
	private Color footColor;
	private Color headColor;
	
	private int direction;
	private double speed;
	
	private int ticks;
	
	private Chromosome chromosome;
	
	private static FeatureMapping featureMapping = new FeatureMapping() {
		@Override
		protected void set(Chromosome chromosome) {
			size = new Vector2f(chromosome.scale * Goose.MAX_SCALE * 2.5f, chromosome.scale * Goose.MAX_SCALE * 1.5f);
			this.foot = ShapeType.Circle;
			this.body = ShapeType.Square;
			this.head = ShapeType.Triangle;
			this.headColor = Color.red;
			this.bodyColor = Color.blue;
			this.footColor = Color.green;
		}
	};
	
	public CreatureRenderer(Chromosome chromosome) {
		this.chromosome = chromosome;
		featureMapping.set(chromosome);
		this.size = featureMapping.size;
		this.foot = featureMapping.foot;
		this.body = featureMapping.body;
		this.head = featureMapping.head;
		this.headColor = featureMapping.headColor;
		this.footColor = featureMapping.footColor;
		this.bodyColor = featureMapping.bodyColor;
		
		//smallest dimension of the creature
		float minSize = Math.min(size.getX(), size.getY());
		
		//get all of the shapes with proper scaling
		bodyShape = getShape(body, size.getX(), size.getY());
		leftFootShape = getShape(foot, (minSize + STANDARD_SIZE*.333f)/4, (minSize + STANDARD_SIZE*.333f)/4);
		rightFootShape = getShape(foot, (minSize + STANDARD_SIZE*.333f)/4, (minSize + STANDARD_SIZE*.333f)/4);
		headShape = getShape(head, (minSize + STANDARD_SIZE)/4, (minSize + STANDARD_SIZE)/4);
		
		//offsets the head to be centered above the front end of the body
		headShape.setX(headShape.getX() + size.getX()/2);
		headShape.setY(headShape.getY() + size.getY()/2 - headShape.getHeight()/2);
		
		//offset the feet
		leftFootShape.setX(leftFootShape.getX() - size.getX()/4);
		leftFootShape.setY(leftFootShape.getY() + size.getY()/2 + leftFootShape.getHeight()/2);
		
		rightFootShape.setX(rightFootShape.getX() + size.getX()/4);
		rightFootShape.setY(rightFootShape.getY() + size.getY()/2 + leftFootShape.getHeight()/2);
		
		direction = 1;
		speed = .125;
		ticks = 0;
	}
	
	public void render(Graphics g) {
		double sze = size.getX()/40.0;
		double spd = speed*8;
		
		g.translate(0, -size.getY() * 0.2f);
		g.pushTransform();
			g.setLineWidth(2);
			g.scale(direction, 1);
			drawOutlinedShape(g, bodyShape, bodyColor);
			g.pushTransform();
				g.translate((float) (Math.cos(Math.toRadians(ticks/(4*sze)))*sze*spd), (float) (sze*spd*(-(Math.sin(Math.toRadians(-ticks/(4*sze)))*5+5)))); 
				drawOutlinedShape(g, headShape, headColor);
			g.popTransform();
			g.pushTransform();
				g.translate((float) (Math.sin(Math.toRadians(-ticks/(2*sze)))*10*sze*spd)*direction, (float) (Math.cos(Math.toRadians(-ticks/(2*sze)))*2*sze*spd)*direction); 
				drawOutlinedShape(g, leftFootShape, footColor);
			g.popTransform();
			g.pushTransform();
				g.translate((float) (Math.cos(Math.toRadians(ticks/(2*sze)))*10*sze*spd)*direction, (float) (Math.sin(Math.toRadians(ticks/(2*sze)))*2*sze*spd)*direction); 
				drawOutlinedShape(g, rightFootShape, footColor);
			g.popTransform();
		g.popTransform();
	}
	
	public void update(int delta, double spd) {
		ticks = (ticks + delta);
	}
	
	private static void drawOutlinedShape(Graphics g, Shape s, Color c) {
		g.setColor(c);
		g.fill(s);
		g.setColor(Color.black);
		g.draw(s);
	}
	
	private static Shape getShape(ShapeType s, float x, float y) {
		switch(s) {
		case Circle:
			return (Shape) new Ellipse(0 ,0 ,x/2, y/2);
		case Square:
			return(Shape) new Rectangle(-x/2,-y/2, x, y);
		case Triangle:
			float[] tri = {0, -y/2, x/2, y/2, -x/2, y/2};
			return (Shape) new Polygon(tri);
		}
		return null;
	}
	
	public void setSpeed(double spd) {
		speed = spd;
	}
}
