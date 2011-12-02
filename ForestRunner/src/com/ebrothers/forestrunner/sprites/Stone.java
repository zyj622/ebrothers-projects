package com.ebrothers.forestrunner.sprites;

public class Stone extends GameSprite {

	public Stone() {
		super("stone01.png");
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
