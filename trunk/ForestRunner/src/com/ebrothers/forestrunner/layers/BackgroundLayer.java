package com.ebrothers.forestrunner.layers;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCTMXLayer;
import org.cocos2d.layers.CCTMXTiledMap;

public class BackgroundLayer extends CCLayer {

	public BackgroundLayer() {
		super();
		CCTMXTiledMap map = CCTMXTiledMap.tiledMap("level/level1.tmx");
		CCTMXLayer layer = map.layerNamed("layer1");
//		CCMoveTo moveTo = CCMoveTo.action(10, CGPoint.ccp(-9000, 0));
//		layer.runAction(moveTo);
		setAnchorPoint(0, getAnchorPoint().y);
		setPosition(80, 0);
		addChild(map);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		schedule("tick", 1);
	}

	public void tick(float delta) {

	}

	@Override
	public void onExit() {
		super.onExit();
		unschedule("tick");
	}
}
