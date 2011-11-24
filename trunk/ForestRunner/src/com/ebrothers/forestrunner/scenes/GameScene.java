package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.layers.BackgroundLayer;

public class GameScene extends CCScene {

	public static GameScene scene() {
		GameScene scene = new GameScene();
		scene.addChild(new BackgroundLayer());
		return scene;
	}

	public GameScene() {
		super();
		final CCSpriteFrameCache sharedSpriteFrameCache = CCSpriteFrameCache
				.sharedSpriteFrameCache();
		sharedSpriteFrameCache.addSpriteFrames("static.plist");
		sharedSpriteFrameCache.addSpriteFrames("sprites.plist");
	}

	@Override
	public void onEnter() {
		super.onEnter();
	}
}
