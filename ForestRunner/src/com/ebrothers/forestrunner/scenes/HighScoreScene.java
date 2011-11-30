package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.layers.HighScoreLayer;

public class HighScoreScene extends CCScene {

	public static HighScoreScene scene() {
		return new HighScoreScene();
	}

	public HighScoreScene() {
		super();
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		cache.removeAllSpriteFrames();
		cache.addSpriteFrames("stages.plist");

		addChild(new HighScoreLayer());
	}
}
