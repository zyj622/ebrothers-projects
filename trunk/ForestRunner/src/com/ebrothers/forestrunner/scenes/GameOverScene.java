package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.layers.GameOverLayer;

public class GameOverScene extends CCScene {
	
	
	
	public static GameOverScene scene() {
		return new GameOverScene();
	}
	
	public GameOverScene() {
		super();
		addChild(new GameOverLayer());
	}

}
