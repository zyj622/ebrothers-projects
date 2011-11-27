package com.ebrothers.forestrunner.layers;

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;

import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.LevelData;
import com.ebrothers.forestrunner.data.LevelDataParser;

public class GameLayer extends CCLayer {
	private static final String TAG = "GameLayer";
	private float totalWidth = 0;

	public GameLayer(String level) {
		super();
		Logger.d(TAG, "GameLayer init...");
		// build level data
		GameLevelBuilder builder = GameLevelBuilder.create();
		LevelData data = LevelDataParser.parse(level);
		builder.build(this, data);
		totalWidth = builder.getLevelWidth();
	}

	@Override
	public void onEnter() {
		super.onEnter();
		Logger.d(TAG, "onEnter. totalWidth=" + totalWidth);
		float winWidth = CCDirector.sharedDirector().winSize().width;
		CCMoveTo action = CCMoveTo.action(20,
				CGPoint.ccp(-totalWidth + winWidth, 0));
		runAction(action);
		//setPosition(0, 0);
	}

}
