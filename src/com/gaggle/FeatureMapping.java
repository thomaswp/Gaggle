package com.gaggle;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public abstract class FeatureMapping {
	public Vector2f size; 
	public ShapeType foot; 
	public ShapeType body; 
	public ShapeType head; 
	public Color footColor;
	public Color bodyColor; 
	public Color headColor;
	
	protected abstract void set(Chromosome chromosome);
}
