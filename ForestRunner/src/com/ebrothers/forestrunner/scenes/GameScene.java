package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.Levels;
import com.ebrothers.forestrunner.layers.GameLayer;

public class GameScene extends CCScene {
	private static final String TAG = "GameScene";
	private GameLayer gameLayer;

	public static GameScene scene(int level) {
		Logger.d(TAG, "create game scene...");
		return new GameScene(level);
	}

	private GameScene(int level) {
		super();
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		cache.removeAllSpriteFrames();
		cache.addSpriteFrames("sprites.plist");

		gameLayer = new GameLayer(Levels.getLevelDataPath(level));
		addChild(gameLayer);
	}

	public void pauseGame() {
		gameLayer.pauseGame();
	}

	public void resumeGame() {
		gameLayer.resumeGame();
	}

	@Override
	public void onEnter() {
		super.onEnter();
	}

}
