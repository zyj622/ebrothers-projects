package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.layers.HighScoreLayer;


public class HighScoreScene extends CCScene {

	public static HighScoreScene scene() {
		HighScoreScene scene = new HighScoreScene();
		scene.addChild(new HighScoreLayer());
		return scene;
	}
}
