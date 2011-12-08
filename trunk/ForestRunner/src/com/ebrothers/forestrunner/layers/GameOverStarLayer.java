package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGSize;

import com.ebrothers.forestrunner.common.Constants;
import com.ebrothers.forestrunner.common.Game;

public class GameOverStarLayer extends BasicLayer {

	public GameOverStarLayer(long score) {
		super();
		if (Game.isWin) {
			if (score >= 0 && score < Constants.LEVEL_ONE) {
				setStar("star_0.png", "star_0.png", "star_0.png");
			} else if (score >= Constants.LEVEL_ONE
					&& score < Constants.LEVEL_TWO) {
				setStar("star_1.png", "star_0.png", "star_0.png");
			} else if (score >= Constants.LEVEL_TWO
					&& score < Constants.LEVEL_THREE) {
				setStar("star_2.png", "star_0.png", "star_0.png");
			} else if (score >= Constants.LEVEL_THREE
					&& score < Constants.LEVEL_FOUR) {
				setStar("star_2.png", "star_1.png", "star_0.png");
			} else if (score >= Constants.LEVEL_FOUR
					&& score < Constants.LEVEL_FIVE) {
				setStar("star_2.png", "star_2.png", "star_0.png");
			} else if (score >= Constants.LEVEL_FIVE
					&& score < Constants.LEVEL_SIX) {
				setStar("star_2.png", "star_2.png", "star_1.png");
			} else if (score >= Constants.LEVEL_SIX) {
				setStar("star_2.png", "star_2.png", "star_2.png");
			}
		} else {
			setStar("star_0.png", "star_0.png", "star_0.png");
		}
	}

	public void setStar(String star03, String star02, String star01) {
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSpriteFrame spriteFrame1 = cache.getSpriteFrame(star01);
		CCSprite sprite1 = CCSprite.sprite(spriteFrame1);
		sprite1.setScale(Game.scale_ratio_x);
		CGSize starCs = sprite1.getContentSize();
		sprite1.setAnchorPoint(1, 1);
		sprite1.setPosition(winW * 9.3f / 10, winH * 5.2f / 8);
		addChild(sprite1);
		CCSpriteFrame spriteFrame2 = cache.getSpriteFrame(star02);
		CCSprite sprite2 = CCSprite.sprite(spriteFrame2);
		sprite2.setScale(Game.scale_ratio_x);
		sprite2.setAnchorPoint(1, 1);
		sprite2.setPosition((winW * 9.3f / 10)
				- (starCs.width * Game.scale_ratio_x), winH * 5.2f / 8);
		addChild(sprite2);
		CCSpriteFrame spriteFrame3 = cache.getSpriteFrame(star03);
		CCSprite sprite3 = CCSprite.sprite(spriteFrame3);
		sprite3.setScale(Game.scale_ratio_x);
		sprite3.setAnchorPoint(1, 1);
		sprite3.setPosition((winW * 9.3f / 10)
				- (starCs.width * Game.scale_ratio_x) * 2f, winH * 5.2f / 8);
		addChild(sprite3);
	}

}
