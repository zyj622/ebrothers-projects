package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.layers.LevelSelectLayer;

public class LevelSelectScene extends CCScene {
	
	public static LevelSelectScene scene() {
		LevelSelectScene scene = new LevelSelectScene();
		scene.addChild(new LevelSelectLayer());
		return scene;
	}

}
