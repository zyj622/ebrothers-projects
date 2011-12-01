package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.layers.HighScoreLayer;

public class HighScoreScene extends CCScene {

	public static HighScoreScene scene() {
		return new HighScoreScene();
	}

	public HighScoreScene() {
		super();
		addChild(new HighScoreLayer());
	}
}
