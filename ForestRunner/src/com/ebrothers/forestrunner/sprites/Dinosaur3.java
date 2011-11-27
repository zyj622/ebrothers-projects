package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Dinosaur3 extends GameSprite {

	public Dinosaur3() {
		super("dinosaur31.png");
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 6; i++) {
			frames.add(cache.getSpriteFrame(String.format("dinosaur3%d.png",
					i + 1)));
		}
		addAnimation("run", frames);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("run");
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
