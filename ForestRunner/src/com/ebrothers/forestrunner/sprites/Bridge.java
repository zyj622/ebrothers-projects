package com.ebrothers.forestrunner.sprites;

public class Bridge extends GameSprite {

	public Bridge() {
		super("bridge01.png");
		setAnchorPoint(0, 0);
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
