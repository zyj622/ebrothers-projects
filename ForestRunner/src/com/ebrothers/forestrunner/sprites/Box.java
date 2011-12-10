package com.ebrothers.forestrunner.sprites;

import com.ebrothers.forestrunner.manager.SoundManager;

public class Box extends GameSprite {

	public Box() {
		super("box01.png");
		setAnchorPoint(0.5f, 0);
	}

	@Override
	public boolean canCollision() {
		return true;
	}

	@Override
	public boolean isFatal() {
		return true;
	}

	@Override
	public void onStartContact(GameSprite target) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BOX);
	}
}
