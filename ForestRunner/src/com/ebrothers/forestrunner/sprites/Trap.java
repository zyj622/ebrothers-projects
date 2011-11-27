package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Trap extends GameSprite {

	public Trap() {
		super("trap01.png");
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			frames.add(cache.getSpriteFrame(String.format("trap0%d.png",
					i + 1)));
		}
		addAnimation("trigger", frames);
	}
	
	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("trigger");
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
