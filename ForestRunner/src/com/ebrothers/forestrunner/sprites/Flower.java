package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Flower extends GameSprite {

	public Flower() {
		super("flower01.png");
		setAnchorPoint(0.5f, 0);
		Cherry.addOnTop(this);
		Banana.addOn2Sides(this);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 4; i++) {
			frames.add(cache.getSpriteFrame(String.format("flower0%d.png",
					i + 1)));
		}
		addAnimation("shake", frames);
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
}
