package com.ebrothers.linerunner.layers;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CCTexParams;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import com.ebrothers.linerunner.util.BlocksDataParser.LevelData;

public class BackgroundLayer extends CCLayer {

	private CCSprite skyline1;
	private CCSprite skyline2;
	private CCSprite skyline3;

	public BackgroundLayer(LevelData d) {
		super();
		// add background
		CGSize size = CCDirector.sharedDirector().winSize();
		CGRect rect = CGRect.make(0, 0, size.width, size.height);
		CCSprite background = CCSprite.sprite("background.png", rect);
		background.setPosition(size.width / 2f, size.height / 2f);
		CCTexParams params = new CCTexParams(GL10.GL_LINEAR, GL10.GL_LINEAR,
				GL10.GL_REPEAT, GL10.GL_REPEAT);
		background.getTexture().setTexParameters(params);
		addChild(background);

		// TODO add rolling buildings
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSpriteFrame frame1 = cache.getSpriteFrame("skyline1.png");
		CCSpriteFrame frame2 = cache.getSpriteFrame("skyline2.png");
		CCSpriteFrame frame3 = cache.getSpriteFrame("skyline3.png");

		skyline1 = CCSprite.sprite(frame1);
		skyline2 = CCSprite.sprite(frame2);
		skyline3 = CCSprite.sprite(frame3);

		CCSpriteSheet sheet = CCSpriteSheet.spriteSheet("gElem.png");
		addChild(sheet);

		// sheet.addChild(skyline1);
		// sheet.addChild(skyline2);
		// sheet.addChild(skyline3);
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
