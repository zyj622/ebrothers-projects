package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.manager.SoundManager;

public class Life extends GameSprite {
	public Life() {
		super("life01.png");

		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		frames.add(cache.getSpriteFrame("life01.png"));
		frames.add(cache.getSpriteFrame("life02.png"));
		addAnimation("shine", frames, 0.2f);
	}

	@Override
	public boolean canCollision() {
		return true;
	}
	
	@Override
	public boolean isStar() {
		return true;
	}

	@Override
	public void onStartContact(GameSprite target) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_LIFE);
		Game.delegate.addLife();
		setVisible(false);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shine");
	}
}
