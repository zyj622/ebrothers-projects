package com.ebrothers.forestrunner.layers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

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
	private final float runnerRx2Screen;
	private final float runnerLx2Screen;
	private static final float X_SPEED = 450f;// pixel/s
	private float[] _bp_x;
	private float[] _bp_y;
	private int lbp_index = 0;
	private int rbp_index = 0;
	private CCMenuItemToggle pauseToggle;
	private CCBitmapFontAtlas score;
	private CCBitmapFontAtlas life;
	// collision object
	private GameSprite[] collisionObjects;
	private int co_index = 0;
	private CGRect runnerRect;
	private CGRect objectRect;

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

		// get all collision objects
		ArrayList<GameSprite> collisions = new ArrayList<GameSprite>();
		List<CCNode> childSprites = ground.getChildren();
		if (childSprites != null && !childSprites.isEmpty()) {
			int size = childSprites.size();
			for (int j = 0; j < size; j++) {
				CCNode node = childSprites.get(j);
				if (node instanceof GameSprite
						&& ((GameSprite) node).canCollision()) {
					collisions.add((GameSprite) node);
				}
			}
		}

		Collections.sort(collisions, new Comparator<GameSprite>() {
			public int compare(GameSprite object1, GameSprite object2) {
				return Float.compare(object1.getPosition().x,
						object2.getPosition().x);
			};
		});

		collisionObjects = collisions
				.toArray(new GameSprite[collisions.size()]);

		if (Logger.LOGD) {
			for (int i = 0; i < collisionObjects.length; i++) {
				Logger.d(TAG, "GameLayer. [" + i + "]=" + collisionObjects[i]
						+ ", x=" + collisionObjects[i].getPosition().x);
			}
		}

		// init runner
		runner = new Runner();
		runnerRx2Screen = runner.getPosition().x + runner.getBoundingWidth()
				- 30;
		runnerLx2Screen = runner.getPosition().x;
		root.addChild(runner);

		// add stage title
		CGSize winSize = CCDirector.sharedDirector().winSize();
		GameSprite title = GameSprite.sprite("gameover_stage"
				+ (Globals.current_level + 1) + ".png");
		title.setAnchorPoint(0.5f, 1);
		title.setPosition(winSize.width / 2f, winSize.height);
		root.addChild(title);

		// add score
		GameSprite scoreIcon = GameSprite.sprite("score01.png");
		scoreIcon.setAnchorPoint(0, 1);
		scoreIcon.setPosition(0, winSize.height);
		root.addChild(scoreIcon);

		score = CCBitmapFontAtlas.bitmapFontAtlas("+0", "font2.fnt");
		score.setAnchorPoint(0, 1);
		score.setPosition(
				scoreIcon.getPosition().x + scoreIcon.getBoundingWidth(),
				winSize.height);
		addChild(score);

		// add life counter
		life = CCBitmapFontAtlas.bitmapFontAtlas("x4", "font2.fnt");
		life.setAnchorPoint(1, 1);
		life.setPosition(winSize.width, winSize.height);
		addChild(life);

		GameSprite lifeIcon = GameSprite.sprite("life01.png");
		lifeIcon.setAnchorPoint(1, 1);
		lifeIcon.setPosition(life.getPosition().x
				- life.getBoundingBox().size.width, winSize.height);
		root.addChild(lifeIcon);

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
		float moveDistance = totalWidth - winWidth;
		moveAction = CCSequence.actions(
				CCMoveTo.action(moveDistance / X_SPEED,
						CGPoint.ccp(-moveDistance, 0)),
				CCCallFunc.action(this, "moveDone"));

		runnerRect = CGRect.zero();
		objectRect = CGRect.zero();
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float currX = -ground.getPosition().x + runnerRx2Screen;
			float futureX = currX + Runner.JUMP_DURING_LONG * X_SPEED;
			float futureY = getFutureY(futureX);
			if (futureY == 0) {
				runner.jumpToGap(this, "jumpToGapDone");
			} else {
				runner.jump(futureY);
			}
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
		// ground.setPosition(-2100, 0);
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

	public float getFutureY(float futureX) {
		for (int i = 0; i < _bp_x.length; i++) {
			if (futureX < _bp_x[i]) {
				return _bp_y[i];
			}
		}
		return Globals.groundM_y;
	}

	public float getRunnerRy(float runnerX) {
		assert (rbp_index < _bp_x.length);
		if (runnerX < _bp_x[rbp_index]) {
			return _bp_y[rbp_index];
		} else {
			rbp_index++;
			return _bp_y[rbp_index];
		}
	}

	public float getRunnerLy(float runnerX) {
		assert (lbp_index < _bp_x.length);
		if (runnerX < _bp_x[lbp_index]) {
			return _bp_y[lbp_index];
		} else {
			lbp_index++;
			return _bp_y[lbp_index];
		}
	}

	@Override
	public void update(float d) {
		float runnerRx = -ground.getPosition().x + runnerRx2Screen;
		float runnerLx = -ground.getPosition().x + runnerLx2Screen;
		float currY = runner.getPosition().y - Runner.y_offset;
		float runnerRy = getRunnerRy(runnerRx);
		float runnerLy = getRunnerLy(runnerLx);
		if (runnerRy != currY && !runner.isInAction()) {
			if (runnerLy == 0 || currY == 0) {
				// fall in gap
				runner.fallToGap(this, "loseGame");
				background.pauseSchedulerAndActions();
				ground.pauseSchedulerAndActions();
			} else if (runnerLy < currY) {
				runner.fallToGround(runnerRy);
			}
			if (runnerRy > currY) {
				// knock down
				runner.knockDown();
				ground.stopAllActions();
				ground.runAction(CCSequence.actions(
						CCMoveBy.action(0.6f, CGPoint.ccp(150, 0)),
						CCCallFunc.action(this, "loseGame")));
			}
		}

		GameSprite[] objects = collisionObjects;
		if (co_index >= objects.length) {
			return;
		}

		// detect collision
		while (co_index < objects.length
				&& objects[co_index].getPosition().x < runnerLx) {
			co_index++;
			if (co_index >= objects.length) {
				return;
			}
		}

		GameSprite object = objects[co_index];
		CGPoint position = object.getPosition();
		runnerRect.set(runnerLx, currY, runner.getBoundingWidth(),
				runner.getBoundingHeight());
		objectRect.set(position.x, position.y, object.getBoundingWidth(),
				object.getBoundingHeight());

		if (CGRect.intersects(runnerRect, objectRect)) {
			if (object.isFatal()) {
				background.pauseSchedulerAndActions();
				ground.pauseSchedulerAndActions();
			}
			runner.onStartContact(object);
			object.onStartContact(runner);
			co_index++;
		}
	}

	public void jumpToGapDone() {
		runner.fallToGap(this, "loseGame");
	}

	public void moveDone() {
	}

	public void winGame() {
		pauseToggle.setIsEnabled(false);
		pauseGame();
	}

	public void loseGame() {
		pauseToggle.setIsEnabled(false);
		pauseGame();
	}

}
