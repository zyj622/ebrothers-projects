package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;

public class Cherry extends GameSprite {
	public Cherry() {
		super("star03.png");
		setAnchorPoint(0.5f, 0);
		setScale(1);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		frames.add(cache.getSpriteFrame("star03.png"));
		frames.add(cache.getSpriteFrame("star04.png"));
		addAnimation("shine", frames, 0.1f);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shine");
	}

	public static void addOnTop(CCNode parent) {
		Cherry cherry = new Cherry();
		CGPoint parentPos = parent.getPosition();
		float parentWidth = parent.getContentSize().width;
		cherry.setPosition(parentPos.x + parentWidth / 2f,
				parentPos.y + cherry.getContentSize().height * 2 + 20);
		parent.addChild(cherry);
	}
}
