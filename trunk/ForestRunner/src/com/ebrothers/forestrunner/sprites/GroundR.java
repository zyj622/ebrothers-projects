package com.ebrothers.forestrunner.sprites;

public class GroundR extends GameSprite {

	public GroundR() {
		super("ground34.png");
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
