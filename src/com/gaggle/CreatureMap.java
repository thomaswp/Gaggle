package com.gaggle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CreatureMap {
	public static List<ShapeType> footMap;
	public static List<ShapeType> bodyMap;
	public static List<ShapeType> headMap;
		
	public static void initRandomMap() {
		footMap = Arrays.asList(ShapeType.values());
		footMap.remove(ShapeType.Pentagon);
		Collections.shuffle(footMap);
		
		bodyMap = Arrays.asList(ShapeType.values());
		bodyMap.remove(ShapeType.Pentagon);
		Collections.shuffle(bodyMap);
		
		headMap = Arrays.asList(ShapeType.values());
		headMap.remove(ShapeType.Pentagon);
		Collections.shuffle(headMap);
	}
	
	public static ShapeType getMapping(List<ShapeType> map, float x, float y, float z) {
		return null;
	}
}
