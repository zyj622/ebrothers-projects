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

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.LevelData;
import com.ebrothers.forestrunner.data.LevelDataParser;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.sprites.Background;
import com.ebrothers.forestrunner.sprites.Dinosaur;
import com.ebrothers.forestrunner.sprites.GameSprite;
import com.ebrothers.forestrunner.sprites.Runner;
import com.ebrothers.forestrunner.sprites.Trap;

public class GameLayer extends CCLayer implements UpdateCallback, GameDelegate {

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
	private int remainLives = Game.LIFE_AMOUNT;
	private CGPoint[] signs;
	private int sign_index = 0;
	/**
	 * When runner over the go sign, will store states. 0: lbp_index 1:
	 * rbp_index 2: co_index;
	 */
	private int[] states_bak;
	private final float runner2RScreen;

	public GameLayer(String level) {
		super();
		Logger.d(TAG, "GameLayer init...");
		setIsTouchEnabled(true);

		root = CCSpriteSheet.spriteSheet("sprites.png", 350);
		addChild(root);

		// add background
		background = Background.background();
		root.addChild(background);

		// build ground
		ground = new CCSprite();
		// "level/leveltest.txt"
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
			}
		});
		collisionObjects = collisions
				.toArray(new GameSprite[collisions.size()]);
		// get all signs: stop and start sign
		ArrayList<CGPoint> points = builder.getSignPoints();
		Collections.sort(points, new Comparator<CGPoint>() {
			public int compare(CGPoint object1, CGPoint object2) {
				return Float.compare(object1.x, object2.x);
			}
		});
		signs = points.toArray(new CGPoint[points.size()]);

		// init runner
		runner = new Runner();
		runnerRx2Screen = runner.getPosition().x + runner.getBoundingWidth()
				- 30;
		runnerLx2Screen = runner.getPosition().x;
		root.addChild(runner);

		// add stage title
		CGSize winSize = CCDirector.sharedDirector().winSize();
		GameSprite title = GameSprite.sprite("gameover_stage"
				+ (Game.current_level + 1) + ".png");
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
		menu.setScale(Game.scale_ratio);
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
		states_bak = new int[3];

		runner2RScreen = CCDirector.sharedDirector().winSize().width
				* Game.scale_ratio - runnerRx2Screen;
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float currX = getRunnerRx();
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

	@Override
	public void resumeGame() {
		pauseToggle.setSelectedIndex(0);
		runner.resumeSchedulerAndActions();
		restartGround();
		resumeSchedulerAndActions();
		setIsTouchEnabled(true);
	}

	@Override
	public void pauseGame() {
		pauseToggle.setSelectedIndex(1);
		runner.pauseSchedulerAndActions();
		stopGround();
		pauseSchedulerAndActions();
		setIsTouchEnabled(false);
	}

	@Override
	public void winGame() {
		pauseToggle.setIsEnabled(false);
		pauseGame();
		Game.isWin = true;
		// save passed level
		LocalDataManager ldm = LocalDataManager.getInstance();
		if (Game.current_level > (Integer) ldm.readSetting(
				LocalDataManager.PASSED, 0)) {
			ldm.writeSetting(LocalDataManager.PASSED, Game.current_level);
		}
		// save score of current level
		String level = String.valueOf(Game.current_level);
		if (Game.score > (Integer) ldm.readSetting(level, 0)) {
			ldm.writeSetting(level, Game.score);
		}
		SceneManager.getInstance().replaceTo(SceneManager.SCENE_GAMEOVER);
	}

	@Override
	public void loseGame() {
		Logger.d(TAG, "loseGame. ");
		remainLives--;
		if (remainLives == 0) {
			// game over
			Game.isWin = false;
			SceneManager.getInstance().replaceTo(SceneManager.SCENE_GAMEOVER);
		} else {
			life.setString("x" + (remainLives - 1));
			restartGame();
		}
	}

	@Override
	public void updateScore() {
		score.setString("+" + Game.score);
	}

	@Override
	public void update(float d) {
		float runnerRx = getRunnerRx();
		float runnerLx = getRunnerLx();
		float runnerRy = getRunnerRy(runnerRx);
		float runnerLy = getRunnerLy(runnerLx);
		float currY = runner.getPosition().y - Runner.y_offset;
		if (runnerRy != currY && !runner.isInAction()) {
			if (runnerLy == 0 || currY == 0) {
				// fall in gap
				runner.fallToGap(this, "loseGame");
				stopGround();
				return;
			} else if (runnerLy < currY) {
				runner.fallToGround(runnerRy);
			}
			if (runnerRy > currY) {
				Logger.d(TAG, "update. runnerRy=" + runnerRy + ", currY="
						+ currY);
				// knock down
				runner.knockDown();
				stopGround();
				ground.runAction(CCSequence.actions(
						CCMoveBy.action(0.6f, CGPoint.ccp(150, 0)),
						CCCallFunc.action(this, "loseGame")));
				return;
			}
		}

		GameSprite[] objects = collisionObjects;
		if (co_index >= objects.length) {
			return;
		}

		// detect collision
		while (!objects[co_index].getVisible()) {
			co_index++;
			if (co_index >= objects.length) {
				return;
			}
		}
		GameSprite object = objects[co_index];
		CGPoint position = object.getPosition();
		runnerRect.set(runnerLx, currY, runner.getBoundingWidth(),
				runner.getBoundingHeight());
		float objectW = object.getBoundingWidth();
		float objectLx = position.x - objectW / 2f;
		objectRect.set(objectLx, position.y, objectW,
				object.getBoundingHeight());

		if (object instanceof Trap) {
			Trap trap = (Trap) object;
			if (!trap.isTriggered() && (objectLx - runnerRx) < 200) {
				trap.trigger();
			}
		} else if (object instanceof Dinosaur) {
			Dinosaur dinosaur = (Dinosaur) object;
			if (!dinosaur.isRushed() && (objectLx - runnerRx) < runner2RScreen) {
				dinosaur.rush();
			}
		}

		if (CGRect.intersects(runnerRect, objectRect)) {
			if (object.isFatal()) {
				stopGround();
			}
			Logger.d(TAG, "collistion. runnerRect=" + runnerRect
					+ ", objectRect=" + objectRect + ", object=" + object);
			runner.onStartContact(object);
			object.onStartContact(runner);
			co_index++;
		} else if (runnerRx > position.x) {
			co_index++;
		}

		if (sign_index >= signs.length) {
			return;
		}

		if (runnerLx >= signs[sign_index].x) {
			// save states after over signs
			states_bak[0] = lbp_index;
			states_bak[1] = rbp_index;
			states_bak[2] = co_index;
			sign_index++;
		}
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

	public float getFutureY(float futureX) {
		for (int i = 0; i < _bp_x.length; i++) {
			if (futureX < _bp_x[i]) {
				return _bp_y[i];
			}
		}
		return Game.groundM_y;
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

	private float getRunnerLx() {
		return -ground.getPosition().x + runnerLx2Screen;
	}

	private float getRunnerRx() {
		return -ground.getPosition().x + runnerRx2Screen;
	}

	public void jumpToGapDone() {
		runner.fallToGap(this, "loseGame");
		stopGround();
	}

	private void stopGround() {
		pauseSchedulerAndActions();
		background.pauseSchedulerAndActions();
		ground.stopAllActions();
	}

	private void restartGround() {
		resumeSchedulerAndActions();
		background.resumeSchedulerAndActions();
		ground.runAction(moveAction);
	}

	public void moveDone() {

	}

	private void restartGame() {
		resetStates();
		// restore all collision objects
		for (int i = 0; i < collisionObjects.length; i++) {
			collisionObjects[i].restore();
		}
		CGPoint restartPoint = signs[sign_index];
		// restore ground
		ground.setPosition(-restartPoint.x + runnerRx2Screen, 0);
		// restore runner
		runner.restart(restartPoint);
		restartGround();
	}

	private void resetStates() {
		// reset sign_index
		if (sign_index > 0) {
			sign_index--;
		}
		// reset lbp_index
		lbp_index = states_bak[0];
		// reset rbp_index
		rbp_index = states_bak[1];
		// reset co_index
		co_index = states_bak[2];
		Logger.d(TAG, "resetStates. sign_index=" + sign_index + ", lbp_index="
				+ lbp_index + ", rbp_index=" + rbp_index + ", co_index="
				+ co_index);
	}

}
