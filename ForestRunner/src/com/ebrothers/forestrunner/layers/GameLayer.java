package com.ebrothers.forestrunner.layers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.interval.CCDelayTime;
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
import com.ebrothers.forestrunner.manager.SoundManager;
import com.ebrothers.forestrunner.sprites.Background;
import com.ebrothers.forestrunner.sprites.Box;
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
	private static final float X_SPEED = 320f * Game.scale_ratio;// pixel/s
	private float[] _bp_x;
	private float[] _bp_y;
	private int lbp_index = 0;
	private int rbp_index = 0;
	private CCMenuItemToggle pauseToggle;
	private CCBitmapFontAtlas score;
	private CCBitmapFontAtlas life;
	// collision object
	private GameSprite[] collisionObjects;
	private GameSprite[] triggerObjects;
	private int co_index = 0;
	private int to_index = 0;
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
	private boolean isPlatformMoveDone;

	public GameLayer(String level) {
		super();
		Logger.d(TAG, "GameLayer init...");

		root = CCSpriteSheet.spriteSheet("sprites.png", 500);
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
		ArrayList<GameSprite> triggers = new ArrayList<GameSprite>();
		List<CCNode> childSprites = ground.getChildren();
		if (childSprites != null && !childSprites.isEmpty()) {
			int size = childSprites.size();
			for (int j = 0; j < size; j++) {
				CCNode node = childSprites.get(j);
				if (node instanceof GameSprite) {
					GameSprite o = (GameSprite) node;
					if (o.canCollision()) {
						collisions.add(o);
					}
					if (o.canTrigger()) {
						triggers.add(o);
					}
				}
			}
		}
		Comparator<GameSprite> comparator = new Comparator<GameSprite>() {
			public int compare(GameSprite object1, GameSprite object2) {
				return Float.compare(object1.getPosition().x,
						object2.getPosition().x);
			}
		};
		Collections.sort(collisions, comparator);
		Collections.sort(triggers, comparator);
		collisionObjects = new GameSprite[collisions.size()];
		collisionObjects = collisions.toArray(collisionObjects);
		triggerObjects = new GameSprite[triggers.size()];
		triggerObjects = triggers.toArray(triggerObjects);
		// get all signs: stop and start sign
		ArrayList<CGPoint> points = builder.getSignPoints();
		Collections.sort(points, new Comparator<CGPoint>() {
			public int compare(CGPoint object1, CGPoint object2) {
				return Float.compare(object1.x, object2.x);
			}
		});
		signs = new CGPoint[points.size()];
		signs = points.toArray(signs);

		// init runner
		runner = new Runner();
		root.addChild(runner);

		// add stage title
		CGSize winSize = CCDirector.sharedDirector().winSize();
		GameSprite title = GameSprite.sprite("gameover_stage"
				+ (Game.current_level + 1) + ".png");
		title.setPosition(winSize.width / 2f, winSize.height - 28
				* Game.scale_ratio);
		root.addChild(title);

		// add score
		GameSprite scoreIcon = GameSprite.sprite("score01.png");
		scoreIcon.setPosition(28 * Game.scale_ratio, winSize.height - 28
				* Game.scale_ratio);
		root.addChild(scoreIcon);

		score = CCBitmapFontAtlas.bitmapFontAtlas("+0", "font2.fnt");
		score.setScale(Game.scale_ratio);
		score.setAnchorPoint(0, 0.5f);
		score.setPosition(
				scoreIcon.getPosition().x
						+ scoreIcon.getBoundingBox().size.width / 2f + 5,
				scoreIcon.getPosition().y);
		addChild(score);

		// add life counter
		life = CCBitmapFontAtlas.bitmapFontAtlas("×4", "font2.fnt");
		life.setPosition(winSize.width - 28 * Game.scale_ratio, winSize.height
				- 28 * Game.scale_ratio);
		life.setScale(Game.scale_ratio);
		addChild(life);

		GameSprite lifeIcon = GameSprite.sprite("life01.png");
		lifeIcon.setPosition(life.getPosition().x
				- life.getBoundingBox().size.width - 5, life.getPosition().y);
		root.addChild(lifeIcon);

		// pause/resume
		GameSprite pauseSprite = GameSprite.sprite("pause01.png");
		GameSprite resumeSprite = GameSprite.sprite("pause02.png");
		CCMenuItemSprite pause = CCMenuItemSprite
				.item(pauseSprite, pauseSprite);
		CCMenuItemSprite resume = CCMenuItemSprite.item(resumeSprite,
				resumeSprite);
		pauseToggle = CCMenuItemToggle.item(this, "onPauseOrResume", pause,
				resume);
		pauseToggle.setPosition(25, 25);
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
		states_bak = new int[4];

		runner2RScreen = CCDirector.sharedDirector().winSize().width
				* Game.scale_ratio - getRunnerRX2Screen();
	}

	private float getRunnerRX2Screen() {
		return runner.getPosition().x + runner.getBoundingWidth() - 30;
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
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_START);
		SoundManager.sharedSoundManager().playSound(
				SoundManager.MUSIC_BACKGROUND, true);
		runAction(CCSequence.actions(CCDelayTime.action(0.8f),
				CCCallFunc.action(this, "onStartGame")));
		schedule(this);
	}

	public void onStartGame() {
		setIsTouchEnabled(true);
		runner.run();
		ground.runAction(moveAction);
	}

	@Override
	public void onExit() {
		super.onExit();
		SoundManager.sharedSoundManager().pauseSound();
		unschedule(this);
	}

	@Override
	public void resumeGame() {
		setIsTouchEnabled(true);
		resumeSchedulerAndActions();
		runner.resumeSchedulerAndActions();
		ground.resumeSchedulerAndActions();
		background.resumeSchedulerAndActions();
		for (GameSprite object : collisionObjects) {
			object.resumeSchedulerAndActions();
		}
		SoundManager.sharedSoundManager().resumeSound();
		pauseToggle.setSelectedIndex(0);
	}

	@Override
	public void pauseGame() {
		setIsTouchEnabled(false);
		runner.pauseSchedulerAndActions();
		ground.pauseSchedulerAndActions();
		background.pauseSchedulerAndActions();
		pauseSchedulerAndActions();
		for (GameSprite object : collisionObjects) {
			object.pauseSchedulerAndActions();
		}
		SoundManager.sharedSoundManager().pauseSound();
		pauseToggle.setSelectedIndex(1);
	}

	@Override
	public void winGame() {
		SoundManager.sharedSoundManager()
				.playEffect(SoundManager.MUSIC_SUCCESS);
		pauseToggle.setIsEnabled(false);
		pauseGame();
		Game.isWin = true;
		Game.score += (remainLives * 10000);
		// save passed level
		LocalDataManager ldm = LocalDataManager.getInstance();
		if (Game.current_level > (Integer) ldm.readSetting(
				LocalDataManager.PASSED, -1)) {
			ldm.writeSetting(LocalDataManager.PASSED, Game.current_level);
		}
		// save score of current level
		String level = String.valueOf(Game.current_level);
		if (Game.score > (Long) ldm.readSetting(level, 0L)) {
			ldm.writeSetting(level, Game.score);
		}
		SceneManager.sharedSceneManager()
				.replaceTo(SceneManager.SCENE_GAMEOVER);
	}

	@Override
	public void loseGame() {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_FAIL);
		remainLives--;
		if (remainLives == 0) {
			// game over
			Game.isWin = false;
			SceneManager.sharedSceneManager().replaceTo(
					SceneManager.SCENE_GAMEOVER);
		} else {
			life.setString("×" + (remainLives - 1));
			resetGame();
		}
	}

	@Override
	public void updateScore() {
		score.setString("+" + Game.score);
	}

	@Override
	public void addLife() {
		remainLives++;
		life.setString("×" + (remainLives - 1));
	}

	@Override
	public void update(float d) {
		if (isPlatformMoveDone && !runner.isInAction()) {
			float distance = CCDirector.sharedDirector().winSize().width
					- Runner.RELATIVE_SCREEN_LEFT;
			float during = distance / X_SPEED;
			runner.runAction(CCSequence.actions(
					CCMoveBy.action(during, CGPoint.ccp(distance, 0)),
					CCCallFunc.action(this, "winGame")));
		}

		float runnerRx = getRunnerRx();
		float runnerLx = getRunnerLx();
		float runnerRy = getRunnerRy(runnerRx);
		float runnerLy = getRunnerLy(runnerLx);
		float currY = runner.getPosition().y - Runner.y_offset;
		if ((int) runnerRy != (int) currY && !runner.isInAction()) {
			if (runnerLy == 0 || currY == 0) {
				// fall in gap
				runner.fallToGap(this, "loseGame");
				stopPlatform();
				return;
			} else if ((int) runnerLy < (int) currY) {
				runner.fallToGround(runnerRy);
			}
			if ((int) runnerRy > (int) currY) {
				Logger.d(TAG, "update. runnerRy=" + runnerRy + ", currY="
						+ currY);
				// knock down
				runner.knockDown();
				stopPlatform();
				ground.runAction(CCSequence.actions(
						CCMoveBy.action(0.6f,
								CGPoint.ccp(100 * Game.scale_ratio, 0)),
						CCCallFunc.action(this, "loseGame")));
				return;
			}
		}

		if (runner.isJumping() && (int) currY < (int) runnerRy) {
			runner.knockDown();
			stopPlatform();
			ground.runAction(CCSequence.actions(
					CCMoveBy.action(0.6f,
							CGPoint.ccp(100 * Game.scale_ratio, 0)),
					CCCallFunc.action(this, "loseGame")));
			return;
		}

		GameSprite[] tos = triggerObjects;
		if (to_index < tos.length) {
			GameSprite o = triggerObjects[to_index];
			float objectLx = o.getPosition().x - o.getTextureRect().size.width
					/ 2f;
			if (o instanceof Trap) {
				Trap trap = (Trap) o;
				if (!trap.isTriggered()
						&& (objectLx - runnerRx) < 140 * Game.scale_ratio) {
					trap.trigger();
					to_index++;
				}
			} else if (o instanceof Dinosaur) {
				Dinosaur dinosaur = (Dinosaur) o;
				if (!dinosaur.isRushed()
						&& (objectLx - runnerRx) < runner2RScreen) {
					dinosaur.rush();
					to_index++;
				}
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

		detectCollision();

		if (sign_index >= signs.length) {
			return;
		}

		if (runnerRx >= signs[sign_index].x) {
			// save states after over signs
			states_bak[0] = lbp_index;
			states_bak[1] = rbp_index;
			states_bak[2] = co_index;
			states_bak[3] = to_index;
			sign_index++;
		}
	}

	private void detectCollision() {
		GameSprite[] objects = collisionObjects;
		if (co_index >= objects.length) {
			return;
		}
		GameSprite object = objects[co_index];
		CGPoint position = object.getPosition();
		float objWidth = object.getContentSize().width - 10 * Game.scale_ratio;
		float objHeight = object.getContentSize().height - 10
				* Game.scale_ratio;
		float objectLx = position.x - objWidth / 2f;
		float currY = runner.getPosition().y - Runner.y_offset;
		float runnerRx = getRunnerRx();
		float runnerLx = getRunnerLx();
		objectRect.set(objectLx, position.y, objWidth, objHeight);
		runnerRect.set(runnerLx, currY, runner.getBoundingWidth(),
				runner.getBoundingHeight());
		if (CGRect.intersects(runnerRect, objectRect)) {
			if (object.isFatal()) {
				stopPlatform();
			}
			Logger.d(TAG, "collision. runnerRect=" + runnerRect
					+ ", objectRect=" + objectRect + ", object=" + object);
			runner.onStartContact(object);
			object.onStartContact(runner);
			if (object instanceof Box || object instanceof Trap) {
				ground.runAction(CCSequence.actions(
						CCMoveBy.action(0.6f,
								CGPoint.ccp(100 * Game.scale_ratio, 0)),
						CCCallFunc.action(this, "loseGame")));
			}
			co_index++;
			// detectCollision();
		} else if (runnerRx > position.x) {
			co_index++;
			// detectCollision();
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
		float[] bp_x = _bp_x;
		for (int i = 0; i < bp_x.length; i++) {
			if (futureX < bp_x[i]) {
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
			if (rbp_index >= _bp_y.length) {
				rbp_index--;
			}
			return _bp_y[rbp_index];
		}
	}

	public float getRunnerLy(float runnerX) {
		assert (lbp_index < _bp_x.length);
		if (runnerX < _bp_x[lbp_index]) {
			return _bp_y[lbp_index];
		} else {
			lbp_index++;
			if (lbp_index >= _bp_y.length) {
				lbp_index--;
			}
			return _bp_y[lbp_index];
		}
	}

	private float getRunnerLx() {
		return -ground.getPosition().x + runner.getPosition().x;
	}

	private float getRunnerRx() {
		return -ground.getPosition().x + getRunnerRX2Screen();
	}

	public void jumpToGapDone() {
		runner.fallToGap(this, "loseGame");
		stopPlatform();
	}

	private void stopPlatform() {
		setIsTouchEnabled(false);
		pauseSchedulerAndActions();
		background.pauseSchedulerAndActions();
		ground.stopAllActions();
	}

	public void moveDone() {
		Logger.d(TAG, "moveDone.");
		isPlatformMoveDone = true;
		setIsTouchEnabled(false);
	}

	private void resetGame() {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_RELIVE);
		setIsTouchEnabled(false);
		resetStates();
		CGPoint restartPoint = signs[sign_index];
		// restore all collision objects
		for (int i = 0; i < collisionObjects.length; i++) {
			collisionObjects[i].onRestore();
		}
		// restore ground
		final float groundX = -restartPoint.x + getRunnerRX2Screen();
		ground.setPosition(groundX, 0);
		runner.resetPosition(restartPoint);

		runAction(CCSequence.actions(CCDelayTime.action(0.8f),
				CCCallFuncND.action(this, "restartGame", groundX)));
	}

	public void restartGame(Object o, Object d) {
		setIsTouchEnabled(true);
		resumeSchedulerAndActions();
		float winWidth = CCDirector.sharedDirector().winSize().width;
		float moveDistance = totalWidth - winWidth;
		moveAction = CCSequence.actions(
				CCMoveTo.action((moveDistance + (Float) d) / X_SPEED,
						CGPoint.ccp(-moveDistance, 0)),
				CCCallFunc.action(this, "moveDone"));
		runner.run();
		background.resumeSchedulerAndActions();
		ground.runAction(moveAction);
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
		to_index = states_bak[3];
		Logger.d(TAG, "resetStates. sign_index=" + sign_index + ", lbp_index="
				+ lbp_index + ", rbp_index=" + rbp_index + ", co_index="
				+ co_index);
	}
}
