package com.ebrothers.forestrunner.sprites;

public class StopSign extends GameSprite {
	public StopSign() {
		super("stopsign01.png");
		setAnchorPoint(0.5f, 0);
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
