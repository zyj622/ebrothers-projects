package com.ebrothers.forestrunner.layers;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.LevelData;
import com.ebrothers.forestrunner.data.LevelDataParser;
import com.ebrothers.forestrunner.sprites.GameSprite;
import com.ebrothers.forestrunner.sprites.Runner;

public class GameLayer extends CCLayer implements UpdateCallback {
	private static final String TAG = "GameLayer";
	private float totalWidth = 0;
	private GameSprite runner;

	public GameLayer(String level) {
		super();
		Logger.d(TAG, "GameLayer init...");
		// init runner
		runner = new Runner();
		addChild(runner);
		// build level data
		GameLevelBuilder builder = GameLevelBuilder.create();
		LevelData data = LevelDataParser.parse(level);
		builder.build(this, data);
		totalWidth = builder.getLevelWidth();
		Logger.d(TAG, "GameLayer. totalWidth=" + totalWidth);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		float winWidth = CCDirector.sharedDirector().winSize().width;
		CCMoveTo action = CCMoveTo.action(20,
				CGPoint.ccp(-totalWidth + winWidth, 0));
		runAction(action);
		schedule(this);
	}

	@Override
	public void update(float d) {
		final GameSprite spriteA = runner;
		for (CCNode child : getChildren()) {
			if (child instanceof GameSprite) {
				final GameSprite spriteB = (GameSprite) child;
				if (!spriteB.equals(spriteA) && spriteB.canCollision()
						&& spriteB.canCollision()
						&& isContacted(spriteA, spriteB)) {
					spriteA.onStartContact(spriteB);
					spriteB.onStartContact(spriteA);
				}
			}
		}
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
