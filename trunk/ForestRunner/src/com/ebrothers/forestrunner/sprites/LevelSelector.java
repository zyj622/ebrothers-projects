package com.ebrothers.forestrunner.sprites;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGSize;

import com.ebrothers.forestrunner.common.Constants;

public class LevelSelector extends CCSprite {

	public static LevelSelector levelSprite(int level, long score,
			boolean isNomal) {
		if (score >= 0 && score < Constants.LEVEL_ONE) {
			if (isNomal) {
				return new LevelSelector("stage_bg_normal.png", "stage_0.png",
						"stage_0.png", "stage_0.png", level);
			} else {
				return new LevelSelector("stage_bg_pressed.png", "stage_0.png",
						"stage_0.png", "stage_0.png", level);
			}
		} else if (score >= Constants.LEVEL_ONE && score < Constants.LEVEL_TWO) {
			if (isNomal) {
				return new LevelSelector("stage_bg_normal.png", "stage_1.png",
						"stage_0.png", "stage_0.png", level);
			} else {
				return new LevelSelector("stage_bg_pressed.png", "stage_1.png",
						"stage_0.png", "stage_0.png", level);
			}
		} else if (score >= Constants.LEVEL_TWO
				&& score < Constants.LEVEL_THREE) {
			if (isNomal) {
				return new LevelSelector("stage_bg_normal.png", "stage_2.png",
						"stage_0.png", "stage_0.png", level);
			} else {
				return new LevelSelector("stage_bg_pressed.png", "stage_2.png",
						"stage_0.png", "stage_0.png", level);
			}
		} else if (score >= Constants.LEVEL_THREE
				&& score < Constants.LEVEL_FOUR) {
			if (isNomal) {
				return new LevelSelector("stage_bg_normal.png", "stage_2.png",
						"stage_1.png", "stage_0.png", level);
			} else {
				return new LevelSelector("stage_bg_pressed.png", "stage_2.png",
						"stage_1.png", "stage_0.png", level);
			}
		} else if (score >= Constants.LEVEL_FOUR
				&& score < Constants.LEVEL_FIVE) {
			if (isNomal) {
				return new LevelSelector("stage_bg_normal.png", "stage_2.png",
						"stage_2.png", "stage_0.png", level);
			} else {
				return new LevelSelector("stage_bg_pressed.png", "stage_2.png",
						"stage_2.png", "stage_0.png", level);
			}
		} else if (score >= Constants.LEVEL_FIVE && score < Constants.LEVEL_SIX) {
			if (isNomal) {
				return new LevelSelector("stage_bg_normal.png", "stage_2.png",
						"stage_2.png", "stage_1.png", level);
			} else {
				return new LevelSelector("stage_bg_pressed.png", "stage_2.png",
						"stage_2.png", "stage_1.png", level);
			}
		} else if (score >= Constants.LEVEL_SIX) {
			if (isNomal) {
				return new LevelSelector("stage_bg_normal.png", "stage_2.png",
						"stage_2.png", "stage_2.png", level);
			} else {
				return new LevelSelector("stage_bg_pressed.png", "stage_2.png",
						"stage_2.png", "stage_2.png", level);
			}
		}
		return null;
	}

	public LevelSelector(String fileName) {
		super(CCSpriteFrameCache.sharedSpriteFrameCache().getSpriteFrame(
				fileName));
		setAnchorPoint(1, 1);
	}

	public LevelSelector(String fileName, String star01, String star02,
			String star03, int level) {
		super(CCSpriteFrameCache.sharedSpriteFrameCache().getSpriteFrame(
				fileName));
		// initSprite();
		setAnchorPoint(1, 1);
		CGSize cs = getContentSizeRef();
		// Logger.e("game",
		// "level===="+String.valueOf(cs.width)+"---"+String.valueOf(cs.height));
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();

		CCSpriteFrame spriteFrame1 = cache.getSpriteFrame(String.format(
				"stage_leve%d.png", level));
		CCSprite sprite1 = CCSprite.sprite(spriteFrame1);
		sprite1.setAnchorPoint(0.8f, 0.2f);
		sprite1.setPosition(cs.width / 2, cs.height / 2);
		addChild(sprite1);

		CCSpriteFrame spriteFrame2 = cache.getSpriteFrame(star01);
		CCSprite sprite2 = CCSprite.sprite(spriteFrame2);
		sprite2.setScale(1f);
		CGSize stageCs = sprite2.getContentSize();
		sprite2.setAnchorPoint(0, 0);
		// Logger.e("game",
		// "stage=="+String.valueOf(stageCs.width)+"---"+String.valueOf(stageCs.height));
		sprite2.setPosition((cs.width - stageCs.width * 3) / 2,
				stageCs.height * 0.7f / 2);
		addChild(sprite2);
		CCSpriteFrame spriteFrame3 = cache.getSpriteFrame(star02);
		CCSprite sprite3 = CCSprite.sprite(spriteFrame3);
		sprite3.setScale(1f);
		sprite3.setAnchorPoint(0, 0);
		sprite3.setPosition((cs.width - stageCs.width * 3) / 2 + stageCs.width,
				stageCs.height * 1.1f / 2);
		addChild(sprite3);
		CCSpriteFrame spriteFrame4 = cache.getSpriteFrame(star03);
		CCSprite sprite4 = CCSprite.sprite(spriteFrame4);
		sprite4.setScale(1f);
		sprite4.setAnchorPoint(0, 0);
		sprite4.setPosition((cs.width - stageCs.width * 3) / 2 + stageCs.width
				* 2, stageCs.height * 1.5f / 2);
		addChild(sprite4);
	}

}
