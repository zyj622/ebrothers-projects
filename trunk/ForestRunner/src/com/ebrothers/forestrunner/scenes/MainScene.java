package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;

import com.ebrothers.forestrunner.layers.MainGameBackgroundLayer;
import com.ebrothers.forestrunner.layers.MainGameMenuLayer;

public class MainScene extends CCScene {

	public static MainScene scene() {
		return new MainScene();
	}

	public MainScene() {
		super();
		addChild(new MainGameBackgroundLayer());
		addChild(new MainGameMenuLayer());
	}

}
