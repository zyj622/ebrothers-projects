package com.ebrothers.forestrunner.layers;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.sprites.Background;

public class BackgroundLayer extends CCLayer {

	public BackgroundLayer() {
		super();
		Background.create(this).roll();

		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSprite sprite = CCSprite.sprite(cache.getSpriteFrame("ground31.png"));
		sprite.setScale(1.5f);
		sprite.setPosition(100,
				-CCDirector.sharedDirector().winSize().height / 2f);
		addChild(sprite);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		schedule("tick", 1);
	}

	public void tick(float delta) {

	}

	@Override
	public void onExit() {
		super.onExit();
		unschedule("tick");
	}
}
