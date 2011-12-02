package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Banana extends GameSprite {
	public Banana() {
		super("star01.png");
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		frames.add(cache.getSpriteFrame("star01.png"));
		frames.add(cache.getSpriteFrame("star02.png"));
		addAnimation("shine", frames, 0.2f);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shine");
	}

	public static void addAsTopTriangle(CCNode parent, float cx, float cy) {
		Banana banana = new Banana();
		banana.setPosition(cx + 80, cy + 10);
		parent.addChild(banana);
		banana = new Banana();
		banana.setPosition(cx, cy + 80);
		parent.addChild(banana);
		banana = new Banana();
		banana.setPosition(cx - 80, cy + 10);
		parent.addChild(banana);
	}

	public static void addOn2Sides4(CCNode parent, float cx, float cy) {
		// left top
		Banana banana = new Banana();
		banana.setPosition(cx - 60, cy + 70);
		parent.addChild(banana);
		// right top
		banana = new Banana();
		banana.setPosition(cx + 60, cy + 70);
		parent.addChild(banana);
		// left bottomd
		banana = new Banana();
		banana.setPosition(cx - 80, cy + 8);
		parent.addChild(banana);
		// right bottom
		banana = new Banana();
		banana.setPosition(cx + 80, cy + 8);
		parent.addChild(banana);
	}

	public static void addOn2Sides2(CCNode parent, float cx, float cy) {
		// left
		Banana banana = new Banana();
		banana.setPosition(cx - 120, cy);
		parent.addChild(banana);
		// right
		banana = new Banana();
		banana.setPosition(cx + 40, cy);
		parent.addChild(banana);
	}

	@Override
	public boolean canCollision() {
		return true;
	}
}
