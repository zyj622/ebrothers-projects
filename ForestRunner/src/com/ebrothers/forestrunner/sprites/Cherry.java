package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Cherry extends GameSprite {
	public Cherry() {
		super("star03.png");
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		frames.add(cache.getSpriteFrame("star03.png"));
		frames.add(cache.getSpriteFrame("star04.png"));
		addAnimation("shine", frames, 0.2f);
		frames.clear();
		for (int i = 0; i < 5; i++) {
			frames.add(cache.getSpriteFrame(String.format("flash0%d.png",
					(i + 1))));
		}
		addAnimation("flash", frames, 0.2f);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shine");
	}

	public static void addOnTop(CCNode parent, float cx, float cy,
			boolean offset) {
		Cherry cherry = new Cherry();
		cherry.setPosition(cx, offset ? cy + 120 : cy);
		parent.addChild(cherry);
	}

	@Override
	public boolean canCollision() {
		return true;
	}

	@Override
	public void onStartContact(GameSprite target) {
		playeAnimation("flash", this, "flashDone");
	}

	public void flashDone() {
		removeSelf();
	}
}
