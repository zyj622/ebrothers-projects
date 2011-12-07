package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Levels;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.layers.GameBackgroundLayer;
import com.ebrothers.forestrunner.layers.GameDelegate;
import com.ebrothers.forestrunner.layers.GameLayer;
import com.ebrothers.forestrunner.layers.GameMenuLayer;

public class GameScene extends CCScene implements GameDelegate {
	private static final String TAG = "GameScene";
	private GameLayer gameLayer;
	private GameBackgroundLayer backgroundLayer;
	private GameMenuLayer menuLayer;

	public static GameScene scene(int level) {
		Logger.d(TAG, "create game scene...");
		return new GameScene(level);
	}

	private GameScene(int level) {
		super();
		Game.delegate = this;
		Game.score = 0;

		backgroundLayer = new GameBackgroundLayer();
		addChild(backgroundLayer);

		gameLayer = new GameLayer(Levels.getLevelDataPath(level));
		addChild(gameLayer);

		menuLayer = new GameMenuLayer();
		addChild(menuLayer);
	}

	@Override
	public void onEnter() {
		super.onEnter();
	}

	@Override
	public void pauseGame() {
		backgroundLayer.pause();
		gameLayer.pauseGame();
	}

	@Override
	public void resumeGame() {
		backgroundLayer.resume();
		gameLayer.resumeGame();
	}

	@Override
	public void loseGame() {
		gameLayer.loseGame();
	}

	@Override
	public void winGame() {
		gameLayer.winGame();
	}

	@Override
	public void updateScore() {
		menuLayer.updateScore();
	}

}
