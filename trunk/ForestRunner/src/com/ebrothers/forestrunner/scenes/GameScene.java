package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.Levels;
import com.ebrothers.forestrunner.layers.BackgroundLayer;
import com.ebrothers.forestrunner.layers.GameLayer;
import com.ebrothers.forestrunner.layers.GameMenuLayer;

public class GameScene extends CCScene {
	private static final String TAG = "GameScene";

	public static GameScene scene() {
		Logger.d(TAG, "create game scene...");
		GameScene scene = new GameScene();
		scene.addChild(new BackgroundLayer());
		// "level/leveltest.txt"
		scene.addChild(new GameLayer(Levels.getCurrentLevelPath()));
		scene.addChild(new GameMenuLayer());
		return scene;
	}

	public GameScene() {
		super();
	}

	@Override
	public void onEnter() {
		super.onEnter();
	}

}
