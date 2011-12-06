package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.layers.GameOverLayer;
import com.ebrothers.forestrunner.layers.GameOverMenuLayer;
import com.ebrothers.forestrunner.layers.GameOverStarLayer;

public class GameOverScene extends CCScene {

	public static GameOverScene scene() {
		return new GameOverScene();
	}

	public GameOverScene() {
		super();
		long score = Game.score;
		addChild(new GameOverLayer(score));
		addChild(new GameOverStarLayer(score));
		addChild(new GameOverMenuLayer());
	}

}
