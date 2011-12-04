package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGSize;

import com.ebrothers.forestrunner.common.Constants;
import com.ebrothers.forestrunner.common.Globals;

public class GameOverStarLayer extends BasicLayer {

	public GameOverStarLayer(int score) {
		super();
		if(score>=0 && score < Constants.LEVEL_ONE){
			setStar("stage_0.png", "stage_0.png", "stage_0.png");
		}else if(score>=Constants.LEVEL_ONE && score < Constants.LEVEL_TWO){
			setStar("stage_1.png", "stage_0.png", "stage_0.png");
		}else if(score>=Constants.LEVEL_TWO && score < Constants.LEVEL_THREE){
			setStar("stage_2.png", "stage_0.png", "stage_0.png");
		}else if(score>=Constants.LEVEL_THREE && score < Constants.LEVEL_FOUR){
			setStar("stage_2.png", "stage_1.png", "stage_0.png");
		}else if(score>=Constants.LEVEL_FOUR && score < Constants.LEVEL_FIVE){
			setStar("stage_2.png", "stage_2.png", "stage_0.png");
		}else if(score>=Constants.LEVEL_FIVE && score < Constants.LEVEL_XIE){
			setStar("stage_2.png", "stage_2.png", "stage_1.png");
		}else if(score>=Constants.LEVEL_XIE){
			setStar("stage_2.png", "stage_2.png", "stage_2.png");
		}
	}
	
	public void setStar(String star03,String star02,String star01){
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSpriteFrame spriteFrame1 = cache.getSpriteFrame(star01);
		CCSprite sprite1 = CCSprite.sprite(spriteFrame1);
		sprite1.setScale(Globals.scale_ratio_x*2);
		CGSize starCs = sprite1.getContentSize();
		sprite1.setAnchorPoint(1, 1);
		sprite1.setPosition(width * 9.3f / 10, height * 5.2f / 8);
		addChild(sprite1);
		CCSpriteFrame spriteFrame2 = cache.getSpriteFrame(star02);
		CCSprite sprite2 = CCSprite.sprite(spriteFrame2);
		sprite2.setScale(Globals.scale_ratio_x*2);
		sprite2.setAnchorPoint(1, 1);
		sprite2.setPosition((width * 9.3f / 10)-(starCs.width*Globals.scale_ratio_x*2)*2.7f/2, height * 5.2f / 8);
		addChild(sprite2);
		CCSpriteFrame spriteFrame3 = cache.getSpriteFrame(star03);
		CCSprite sprite3 = CCSprite.sprite(spriteFrame3);
		sprite3.setScale(Globals.scale_ratio_x*2);
		sprite3.setAnchorPoint(1, 1);
		sprite3.setPosition((width * 9.3f / 10)-(starCs.width*Globals.scale_ratio_x*2)*2.7f, height * 5.2f / 8);
		addChild(sprite3);
	}
	

}
