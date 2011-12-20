package com.ebrothers.forestrunner.sprites;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class Background extends CCSprite {

	public static Background background() {
		return new Background();
	}

	private CCSequence sequence;

	public Background() {
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSpriteFrame spriteFrame = cache.getSpriteFrame("background01.jpg");

		CCSprite background1 = CCSprite.sprite(spriteFrame);
		CCSprite background2 = CCSprite.sprite(spriteFrame);

		background1.setAnchorPoint(0, 1);
		background2.setAnchorPoint(0, 1);

		CGSize winSize = CCDirector.sharedDirector().winSize();

		CGSize contentSize = background1.getContentSize();
		float scaleRatio = winSize.height / contentSize.height;
		float width = contentSize.width * scaleRatio;
		float y = winSize.height;

		background1.setPosition(0, y);
		background2.setPosition(width, y);

		background1.setScale(scaleRatio);
		background2.setScale(scaleRatio);

		addChild(background1);
		addChild(background2);

		// action sequence for rolling
		CCMoveTo action = CCMoveTo.action(20, CGPoint.ccp(-width, 0));
		sequence = CCSequence.actions(action,
				CCCallFunc.action(this, "actionDone"));
	}

	public void actionDone() {
		stopAllActions();
		setPosition(0, 0);
		runAction(sequence);
	}

	public void roll() {
		runAction(sequence);
	}
}
