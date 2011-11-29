package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.layers.MainGameBackgroundLayer;
import com.ebrothers.forestrunner.layers.MainGameMenuLayer;

public class MainScene extends CCScene {

	public static MainScene scene() {
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		cache.removeAllSpriteFrames();
		cache.addSpriteFrames("mainmenu.plist");
		MainScene scene = new MainScene();
		scene.addChild(new MainGameBackgroundLayer());
		scene.addChild(new MainGameMenuLayer());
		return scene;
	}

}
