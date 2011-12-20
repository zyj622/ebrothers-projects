package com.ebrothers.forestrunner.common;

public interface Constants {
	// Pixel to meters ratio. Box2D uses meters as the unit for measurement.
	// This ratio defines how many pixels correspond to 1 Box2D "meter"
	// Box2D is optimized for objects of 1x1 meter therefore it makes sense
	// to define the ratio so that your most common object type is 1x1
	// meter.
	public static final float PTM_RATIO = 32.0f;
	
	
	public static final int LEVEL_ONE = 30000;
	public static final int LEVEL_TWO = 45000;
	public static final int LEVEL_THREE = 60000;
	public static final int LEVEL_FOUR = 65000;
	public static final int LEVEL_FIVE = 70000;
	public static final int LEVEL_SIX = 75000;
	
	public static final String EASY = "easy";
	public static final String NORMAL = "normal";
	public static final String DIFFICULTY = "difficulty";
	
	public static final String ACTION_AD_CONTROL = "com.ebrothers.ads";
	
}
