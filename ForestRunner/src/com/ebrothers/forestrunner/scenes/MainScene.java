package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.layers.MainGameLayer;


public class MainScene extends CCScene{
	
	public static MainScene scene() {
		MainScene scene = new MainScene();
		scene.addChild(new MainGameLayer());
		return scene;
	}

}
