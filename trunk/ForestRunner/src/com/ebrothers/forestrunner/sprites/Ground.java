package com.ebrothers.forestrunner.sprites;

public class Ground extends GameSprite {
	public static final int GROUND_L = 1;
	public static final int GROUND_M1 = 2;
	public static final int GROUND_M2 = 3;
	public static final int GROUND_R = 4;

	public static Ground ground(int type) {
		return new Ground(type);
	}

	private int _type;

	private Ground(int type) {
		super("ground3" + type + ".png");
		_type = type;
		setAnchorPoint(0, 1);
	}

	public int getType() {
		return _type;
	}

}
