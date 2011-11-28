package com.ebrothers.forestrunner.sprites;

public class Box extends GameSprite {

	public Box() {
		super("box01.png");
		setAnchorPoint(0.5f, 0);
		Cherry.addOnTop(this);
		Banana.addOn2Sides(this);
	}

	@Override
	public boolean canCollision() {
		return true;
	}

	@Override
	public boolean isFatal() {
		return true;
	}

}
