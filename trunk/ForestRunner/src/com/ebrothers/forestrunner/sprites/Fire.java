package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Fire extends GameSprite {

	public Fire() {
		super("fire01.png");
		setAnchorPoint(0.5f, 0);
		Cherry.addOnTop(this);
		Banana.addOn2Sides(this);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			frames.add(cache.getSpriteFrame(String.format("fire0%d.png", i + 1)));
		}
		addAnimation("shining", frames);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shining");
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
