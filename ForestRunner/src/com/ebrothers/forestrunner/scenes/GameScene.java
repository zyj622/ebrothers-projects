package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.Levels;
import com.ebrothers.forestrunner.layers.GameLayer;

public class GameScene extends CCScene {
	private static final String TAG = "GameScene";

	public static GameScene scene(int level) {
		Logger.d(TAG, "create game scene...");
		return new GameScene(level);
	}

	private GameScene(int level) {
		super();
		// "level/leveltest.txt"
		GameLayer gameLayer = new GameLayer(Levels.getLevelDataPath(level));
		Game.delegate = gameLayer;
		addChild(gameLayer);
	}

	@Override
	public void onEnter() {
		super.onEnter();
	}

}
