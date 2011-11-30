package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.layers.MainGameBackgroundLayer;
import com.ebrothers.forestrunner.layers.MainGameMenuLayer;

public class MainScene extends CCScene {

	public static MainScene scene() {
		return new MainScene();
	}

	public MainScene() {
		super();
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		cache.removeAllSpriteFrames();
		cache.addSpriteFrames("mainmenu.plist");

		addChild(new MainGameBackgroundLayer());
		addChild(new MainGameMenuLayer());
	}

}
