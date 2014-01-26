package com.gaggle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreatureMap {
	public static ArrayList<ShapeType> footMap;
	public static ArrayList<ShapeType> bodyMap;
	public static ArrayList<ShapeType> headMap;
		
	public static void initRandomMap() {
		footMap = new ArrayList<ShapeType>(Arrays.asList(ShapeType.values()));
		footMap.remove(footMap.size()-1);
		Collections.shuffle(footMap);
		
		bodyMap = new ArrayList<ShapeType>(Arrays.asList(ShapeType.values()));
		bodyMap.remove(bodyMap.size()-1);
		Collections.shuffle(bodyMap);
		
		headMap = new ArrayList<ShapeType>(Arrays.asList(ShapeType.values()));
		headMap.remove(headMap.size()-1);
		Collections.shuffle(headMap);
	}
	
	public static ShapeType getMapping(List<ShapeType> map, float x, float y, float z) {
		if(x > y && x > z) {
			return map.get(0);
		}
		if(y > x && y > z) {
			return map.get(1);
		}
		return map.get(2);
	}
	
	public static ShapeType getMapping(List<ShapeType> map, int n) {
		return map.get(n % map.size());
	}
}
