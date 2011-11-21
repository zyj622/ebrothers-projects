package com.ebrothers.linerunner.layers;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGSize;

import android.view.MotionEvent;

import com.ebrothers.linerunner.common.NodeTags;
import com.ebrothers.linerunner.util.BlocksDataParser.BuildData;
import com.ebrothers.linerunner.util.BlocksDataParser.LevelData;

public class RunnerLayer extends CCLayer {
	private final float halfWidth;
	private CCJumpTo jump;
	private CCRepeatForever running;
	private CCSequence rolling;
	private CCSprite runner;
	private boolean isRunning;

	public RunnerLayer(LevelData d) {
		super();
		setIsTouchEnabled(true);
		CGSize size = CCDirector.sharedDirector().winSize();
		halfWidth = size.width / 2f;

		// create runner sprite
		createRunner(d.startZone);

		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> runFrames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 15; i++) {
			runFrames.add(cache.getSpriteFrame(String.format("run%d.png", i)));
		}
		CCAnimation run = CCAnimation.animation("run", 0.02f, runFrames);
		running = CCRepeatForever.action(CCAnimate.action(run));

		ArrayList<CCSpriteFrame> rollFrames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 27; i++) {
			rollFrames
					.add(cache.getSpriteFrame(String.format("roll%d.png", i)));
		}
		CCAnimation roll = CCAnimation.animation("roll", 0.02f, rollFrames);
		rolling = CCSequence.actions(CCAnimate.action(roll),
				CCCallFunc.action(this, "actionFinish"));

	}

	@Override
	public void onEnter() {
		super.onEnter();
		runner.runAction(running);
		isRunning = true;
		schedule("tick");
	}

	public void tick(float delta) {
		// TODO collision detect
	}

	@Override
	public void onExit() {
		super.onExit();
		unschedule("tick");
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		if (isRunning) {
			float x = event.getX();
			runner.stopAllActions();
			if (x <= halfWidth) {
				jump = CCJumpTo.action(.2f, runner.getPosition(), 80, 1);
				CCCallFunc callFunc = CCCallFunc.action(this, "actionFinish");
				runner.runAction(CCSequence.actions(jump, callFunc));
			} else {
				runner.runAction(rolling);
			}
			isRunning = false;
		}
		return CCTouchDispatcher.kEventHandled;
	}

	public void actionFinish() {
		runner.runAction(running);
		isRunning = true;
	}

	private void createRunner(BuildData data) {
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSpriteFrame frame = cache.getSpriteFrame("run0.png");
		runner = CCSprite.sprite(frame);
		final float y = data.y + data.height / 2f + 10f;
		runner.setPosition(data.x + data.width / 2f, y);
		runner.setScale(1.5f);
		addChild(runner, 0, NodeTags.kTagRunner);
	}

}
