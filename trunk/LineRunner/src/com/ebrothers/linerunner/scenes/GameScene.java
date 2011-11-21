package com.ebrothers.linerunner.scenes;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.linerunner.common.Globals;
import com.ebrothers.linerunner.layers.BackgroundLayer;
import com.ebrothers.linerunner.layers.BlocksLayer;
import com.ebrothers.linerunner.layers.RunnerLayer;
import com.ebrothers.linerunner.util.BlocksDataParser;
import com.ebrothers.linerunner.util.BlocksDataParser.LevelData;

public class GameScene extends CCScene {

	public static GameScene scene() {
		final BlocksDataParser p = new BlocksDataParser();
		p.loadLevelData(Globals.currentLevel);
		final LevelData d = p.getLevelData();
		GameScene scene = new GameScene();
		scene.addChild(new BackgroundLayer(d));
		scene.addChild(new BlocksLayer(d));
		scene.addChild(new RunnerLayer(d));
		return scene;
	}

	protected GameScene() {
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		cache.addSpriteFrames("gElem.plist");
	}
}
