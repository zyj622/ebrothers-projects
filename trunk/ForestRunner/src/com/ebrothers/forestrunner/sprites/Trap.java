package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.manager.SoundManager;

public class Trap extends GameSprite {

	private boolean triggered = false;

	public Trap() {
		super("trap01.png");
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			frames.add(cache.getSpriteFrame(String.format("trap0%d.png", i + 1)));
		}
		addAnimation("trigger", frames, 0.1f);
	}

	public void trigger() {
		triggered = true;
		playeAnimation("trigger");
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
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_TRAP);
	}

	@Override
	public void onRestore() {
		stopAllActions();
		setDisplayFrame(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("trap01.png"));
		triggered = false;
	}

	public boolean isTriggered() {
		return triggered;
	}

}
