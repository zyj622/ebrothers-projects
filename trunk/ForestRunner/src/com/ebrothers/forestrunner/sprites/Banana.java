package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Banana extends GameSprite {
	public Banana() {
		super("star01.png");
		setAnchorPoint(0.5f, 0);
		setScale(1);
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

	public static void addAsTopTriangle(CCNode parent) {
		float x = parent.getPosition().x;
		float y = parent.getPosition().y;
		float w = parent.getContentSize().width;
		Banana banana = new Banana();
		banana.setPosition(x + 50, y + 20);
		parent.addChild(banana);
		banana = new Banana();
		banana.setPosition(x + w / 2f, y + 100);
		parent.addChild(banana);
		banana = new Banana();
		banana.setPosition(x + w - 50, y + 20);
		parent.addChild(banana);
	}

	public static void addOn2Sides(CCNode parent) {
		float x = parent.getPosition().x;
		float y = parent.getPosition().y;
		float w = parent.getContentSize().width;
		float h = parent.getContentSize().height;
		// left top
		Banana banana = new Banana();
		banana.setPosition(x - 10, y + h * 2 / 3f - 10);
		parent.addChild(banana);
		// right top
		banana = new Banana();
		banana.setPosition(x + w + 10, y + h * 2 / 3f - 10);
		parent.addChild(banana);
		// left bottomd
		banana = new Banana();
		banana.setPosition(x - 20, y + h / 3f - 20);
		parent.addChild(banana);
		// right bottom
		banana = new Banana();
		banana.setPosition(x + w + 20, y + h / 3f - 20);
		parent.addChild(banana);
	}
}
