package com.ebrothers.forestrunner.layers;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import android.view.MotionEvent;

import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.LevelData;
import com.ebrothers.forestrunner.data.LevelDataParser;
import com.ebrothers.forestrunner.sprites.GameSprite;
import com.ebrothers.forestrunner.sprites.Runner;

public class GameLayer extends CCLayer implements UpdateCallback {
	private static final String TAG = "GameLayer";
	private float totalWidth = 0;
	private Runner runner;
	private CCNode ground;
	private CCMoveTo moveAction;

	public GameLayer(String level) {
		super();
		Logger.d(TAG, "GameLayer init...");
		setIsTouchEnabled(true);
		// build ground
		ground = CCNode.node();
		GameLevelBuilder builder = GameLevelBuilder.create();
		LevelData data = LevelDataParser.parse(level);
		builder.build(ground, data);
		totalWidth = builder.getLevelWidth();
		Logger.d(TAG, "GameLayer. totalWidth=" + totalWidth);
		addChild(ground);

		// init runner
		runner = new Runner();
		addChild(runner);

		// create move action
		float winWidth = CCDirector.sharedDirector().winSize().width;
		moveAction = CCMoveTo
				.action(20, CGPoint.ccp(-totalWidth + winWidth, 0));
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			runner.jump();
			break;
		default:
			break;
		}
		return CCTouchDispatcher.kEventHandled;
	}

	@Override
	public void onEnter() {
		super.onEnter();
		ground.runAction(moveAction);
		schedule(this);
	}

	@Override
	public void update(float d) {
	}

	private boolean isContacted(GameSprite spriteA, GameSprite spriteB) {
		return CGRect.intersects(spriteA.getBoundingBox(),
				spriteB.getBoundingBox());
	}

	@Override
	public void onExit() {
		super.onExit();
		unschedule(this);
	}

}
