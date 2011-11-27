package com.ebrothers.forestrunner.sprites;

public class GroundM1 extends GameSprite {

	public GroundM1() {
		super("ground32.png");
		setAnchorPoint(0, 1);
	}

	@Override
	public boolean canCollision() {
		return false;
	}

	@Override
	public boolean isFatal() {
		return false;
	}

}
