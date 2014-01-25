package com.gaggle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Background {

	GradientFill hillFill, skyFill, cloudFill;
	List<Circle> mountains = new ArrayList<>();
	List<Cloud> clouds = new ArrayList<>();
	Rectangle allThings;
	Random rand = new Random();
	float mountainWidth = 0;
	
	public Background() {
		hillFill = new GradientFill(-50, -50, new Color(0x3FE330), 50, 50, new Color(0xAAF25C), true);
		skyFill = new GradientFill(-50, -50, new Color(0x9AFBFC), 50, 50, new Color(0x4DAFFF));
		cloudFill = new GradientFill(-50, -50, new Color(0xFFFFE0), 50, 50, new Color(0xE0E0EF));
		allThings = new Rectangle(-2000, -2000, 4000, 4000);
		
		int n = 15;
		float x = 0, y = 0;
		for (int i = 0; i < n; i++) {
			float mountainScale = (float) rand.nextGaussian();
			mountainScale = (float) (Math.exp(mountainScale / 2) / Math.E * 4);
			mountainScale = Math.min(Math.max(mountainScale, 0.1f), 3);
			Circle circle = new Circle(x, mountainScale * 200, mountainScale * 300);
			mountains.add(circle);
			mountainWidth += circle.radius * (0.7f + 0.6 * Math.random());
			x += circle.radius;
		}
		
		for (int i = 0; i < 10; i++) {
			clouds.add(new Cloud());
		}
	}
	
	public void update(GameContainer container, int delta) {
		for (Cloud cloud : clouds) {
			cloud.update(container, delta);
		}
	}

	public void render(GameContainer container, Graphics g, float scale, Vector2f origin) {
		g.fill(allThings, skyFill);
		
		
		
		for (Cloud cloud : clouds) {g.pushTransform();
			g.translate(container.getWidth() / 2, container.getHeight() / 2);
			float s = 1 / (1 / scale + 7) * 7;
			float t = 0.1f / cloud.speed;
			g.scale(s, s);
			g.translate(-origin.x * t, -origin.y * t);
			cloud.render(container, g);
			g.popTransform();
		}
		
		
		g.pushTransform();
		g.translate(container.getWidth() / 2, container.getHeight() / 2);
		float s = 1 / (1 / scale + 5) * 5;
		float t = 0.1f;
		g.scale(s, s);
		g.translate(-origin.x * t, -origin.y * t);
		
		g.translate(-mountainWidth / 2, 300);
		for (Circle c : mountains) {
			g.fill(c, hillFill);
		}
		
		g.popTransform();
		
	}
	
	private class Cloud implements GameObject {

		List<Circle> circles = new ArrayList<>();
		float x, y;
		float speed = 1 + 3 * rand.nextFloat();
		
		public Cloud() {
			int n = 2 + rand.nextInt(4);
			float x = 0;
			for (int i = 0; i < n; i++) {
				float cloudScale = (float) rand.nextGaussian();
				cloudScale = (float) (Math.exp(cloudScale / 2) / Math.E * 4);
				cloudScale = Math.min(Math.max(cloudScale, 0.1f), 3);
				Circle circle = new Circle(x, cloudScale * 25, cloudScale * 40);
				circles.add(circle);
				x += circle.radius;
			}
			
			y = rand.nextFloat() * 700 - 700;
			this.x = rand.nextFloat() * 3000 - 1500;
			
		}
		
		@Override
		public void update(GameContainer container, int delta) {
			x += speed * delta / 100f;
			
			if (x > 2000) {
				x = -2000;
			}
		}

		@Override
		public void render(GameContainer container, Graphics g) {
			g.pushTransform();
			g.translate(x, y);
			
//			g.setLineWidth(3);
//			g.setColor(Color.black);
//			for (Circle c : circles) {
//				g.draw(c);
//			}
			
			g.setColor(Color.white);
			for (Circle c : circles) {
				g.fill(c, cloudFill);
			}
			g.popTransform();
		}
		
	}

}
