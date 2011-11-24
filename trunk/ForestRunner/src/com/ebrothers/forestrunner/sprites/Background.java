package com.ebrothers.forestrunner.sprites;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CGPoint;

public class Background {
	private static final float SCALE_RATIO = 2f;
	private CCSpriteSheet spriteSheet;
	private CCSequence sequence;
	private float bgWidth;

	public static Background create(CCNode parent) {
		Background background = new Background();
		parent.addChild(background.spriteSheet);
		return background;
	}

	private Background() {
		spriteSheet = CCSpriteSheet.spriteSheet("static.png");
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSprite background1 = CCSprite.sprite(cache
				.getSpriteFrame("background01.jpg"));
		background1.setAnchorPoint(0, 0.5f);
		background1.setScale(SCALE_RATIO);
		background1.setPosition(0,
				CCDirector.sharedDirector().winSize().height / 2f);
		spriteSheet.addChild(background1);

		CCSprite background2 = CCSprite.sprite(cache
				.getSpriteFrame("background01.jpg"));
		background2.setAnchorPoint(0, 0.5f);
		background2.setScale(SCALE_RATIO);
		bgWidth = background1.getContentSize().width * SCALE_RATIO;
		background2.setPosition(bgWidth,
				CCDirector.sharedDirector().winSize().height / 2f);
		spriteSheet.addChild(background2);
		// action sequence for rolling
		CCMoveTo action = CCMoveTo.action(20, CGPoint.ccp(-bgWidth, 0));
		sequence = CCSequence.actions(action,
				CCCallFunc.action(this, "actionDone"));
	}

	public void roll() {
		spriteSheet.runAction(sequence);
	}

	public void actionDone() {
		spriteSheet.setPosition(0, 0);
		spriteSheet.stopAllActions();
		spriteSheet.runAction(sequence);
	}
}
