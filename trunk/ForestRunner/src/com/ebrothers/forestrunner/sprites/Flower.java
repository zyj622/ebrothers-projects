package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Flower extends GameSprite {

	public Flower() {
		super("flower01.png");
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 4; i++) {
			frames.add(cache.getSpriteFrame(String.format("flower0%d.png",
					i + 1)));
		}
		addAnimation("shake", frames);

		frames.clear();
		for (int i = 1; i < 6; i++) {
			frames.add(cache.getSpriteFrame(String.format("man3%d.png", i + 1)));
		}
		addAnimation("eat", frames, 0.2f);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shake");
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
		playeAnimation("eat", this, "eatDone");
	}

	public void eatDone() {
		playeLoopAnimation("shake");
	}
}
