package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.layers.MainGameBackgroundLayer;
import com.ebrothers.forestrunner.layers.MainGameMenuLayer;


public class MainScene extends CCScene{
	
	public static MainScene scene() {
		MainScene scene = new MainScene();
		scene.addChild(new MainGameBackgroundLayer());
		scene.addChild(new MainGameMenuLayer());
		return scene;
	}

}
