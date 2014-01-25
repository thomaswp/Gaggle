package com.gaggle;

import org.jbox2d.dynamics.Body;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

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
	
}
