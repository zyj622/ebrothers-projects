package com.ebrothers.forestrunner.sprites;

public class GroundM2 extends GameSprite {

	public GroundM2() {
		super("ground33.png");
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
