package com.ebrothers.forestrunner.sprites;

public class GroundL extends GameSprite {

	public GroundL() {
		super("ground31.png");
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
