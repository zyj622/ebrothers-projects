package com.ebrothers.linerunner.layers;

import java.util.ArrayList;

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import com.ebrothers.linerunner.util.BlocksDataParser.BuildData;
import com.ebrothers.linerunner.util.BlocksDataParser.LevelData;

public class BlocksLayer extends CCLayer {

	public BlocksLayer(LevelData levelData) {
		super();
		// create ground
		createSprite(levelData.ground);

		// create blocks
		ArrayList<BuildData> blocks = levelData.blocks;
		for (BuildData block : blocks) {
			createSprite(block);
		}
		
		// create endzone
		createSprite(levelData.endZone);
	}

	private void createSprite(BuildData data) {
		CGRect rect = CGRect.make(data.x, data.y, data.width, data.height);
		CCSprite sprite = CCSprite.sprite("block.png", rect);
		sprite.setPosition(data.x + data.width / 2f, data.y + data.height / 2f);
		addChild(sprite, 0);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		runAction(CCMoveTo.action(10, CGPoint.ccp(-2400, 0)));
	}
}