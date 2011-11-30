package com.ebrothers.forestrunner.layers;

import java.util.ArrayList;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import android.view.MotionEvent;

import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.LevelData;
import com.ebrothers.forestrunner.data.LevelDataParser;
import com.ebrothers.forestrunner.sprites.Background;
import com.ebrothers.forestrunner.sprites.GameSprite;
import com.ebrothers.forestrunner.sprites.Runner;

public class GameLayer extends CCLayer implements UpdateCallback {
	private static final String TAG = "GameLayer";
	private float totalWidth = 0;
	private Runner runner;
	private CCSpriteSheet root;
	private CCSprite ground;
	private Background background;
	private CCSequence moveAction;
	// for break points
	private final float runnerRx;
	private float[] _bp_x;
	private float[] _bp_y;
	private int bp_index = 0;
	private CCMenuItemToggle pauseToggle;

	public GameLayer(String level) {
		super();
		Logger.d(TAG, "GameLayer init...");
		setIsTouchEnabled(true);

		root = CCSpriteSheet.spriteSheet("sprites.png", 250);
		addChild(root);

		// add background
		background = Background.background();
		root.addChild(background);

		// build ground
		ground = new CCSprite();
		LevelData data = LevelDataParser.parse(level);
		GameLevelBuilder builder = GameLevelBuilder.create();
		totalWidth = builder.build(ground, data);
		Logger.d(TAG, "GameLayer. totalWidth=" + totalWidth);
		root.addChild(ground);

		ArrayList<CGPoint> breakPoints = builder.getBreakPoints();
		int count = breakPoints.size();
		_bp_x = new float[count];
		_bp_y = new float[count];
		CGPoint point;
		for (int i = 0; i < count; i++) {
			point = breakPoints.get(i);
			_bp_x[i] = point.x;
			_bp_y[i] = point.y;
		}

		// init runner
		runner = new Runner();
		runnerRx = runner.getPosition().x + runner.getTextureRect().size.width;
		root.addChild(runner);

		// pause/resume
		GameSprite resumeSprite = GameSprite.sprite("pause02.png");
		GameSprite pauseSprite = GameSprite.sprite("pause01.png");
		CCMenuItemSprite resume = CCMenuItemSprite.item(resumeSprite,
				resumeSprite);
		CCMenuItemSprite pause = CCMenuItemSprite
				.item(pauseSprite, pauseSprite);
		pauseToggle = CCMenuItemToggle.item(this, "onPauseOrResume", resume,
				pause);
		pauseToggle.setAnchorPoint(0, 0);
		pauseToggle.setPosition(0, 0);
		CCMenu menu = CCMenu.menu(pauseToggle);
		menu.setScale(Globals.scale_ratio);
		menu.setAnchorPoint(0, 0);
		menu.setPosition(0, 0);
		addChild(menu);

		// create move action
		float winWidth = CCDirector.sharedDirector().winSize().width;
		moveAction = CCSequence.actions(
				CCMoveTo.action(45, CGPoint.ccp(-totalWidth + winWidth, 0)),
				CCCallFunc.action(this, "moveDone"));
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

	public void moveDone() {
		background.stopAllActions();
	}

	@Override
	public void onEnter() {
		super.onEnter();
		ground.runAction(moveAction);
		// ground.setPosition(-800, 0);
		schedule(this);
	}

	@Override
	public void onExit() {
		super.onExit();
		unschedule(this);
	}

	public void onPauseOrResume(Object object) {
		CCMenuItemToggle toggle = (CCMenuItemToggle) object;
		int index = toggle.selectedIndex();
		if (index == 0) {
			Logger.d(TAG, "onPauseOrResume. resume");
			resumeGame();
		} else {
			Logger.d(TAG, "onPauseOrResume. pause");
			pauseGame();
		}
	}

	public void resumeGame() {
		pauseToggle.setSelectedIndex(0);
		runner.resumeSchedulerAndActions();
		ground.resumeSchedulerAndActions();
		background.resumeSchedulerAndActions();
		resumeSchedulerAndActions();
		setIsTouchEnabled(true);
	}

	public void pauseGame() {
		pauseToggle.setSelectedIndex(1);
		runner.pauseSchedulerAndActions();
		ground.pauseSchedulerAndActions();
		background.pauseSchedulerAndActions();
		pauseSchedulerAndActions();
		setIsTouchEnabled(false);
	}

	private boolean isContacted(GameSprite spriteA, GameSprite spriteB) {
		return CGRect.intersects(spriteA.getBoundingBox(),
				spriteB.getBoundingBox());
	}

	public float getRunnerY(float runnerX) {
		assert (bp_index < _bp_x.length);
		if (runnerX < _bp_x[bp_index]) {
			return _bp_y[bp_index];
		} else {
			bp_index++;
			return _bp_y[bp_index];
		}
	}

	@Override
	public void update(float d) {
		float y = getRunnerY(-ground.getPosition().x + runnerRx);
		CGPoint pos = runner.getPosition();
		if (y != pos.y) {
			runner.setPosition(pos.x, y + Runner.y_offset);
		}
	}

}
