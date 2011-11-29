package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;

import com.ebrothers.forestrunner.common.Logger;

public class LevelSelectLayer extends BasicLayer {
	
	public LevelSelectLayer(){
		super();
		CCSprite sprite02 = getNode("game_choose.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);
		
	}
	
	
	@Override
	public void onEnter() {
		super.onEnter();
		Logger.e("game", "enter---------------LevelSelectLayer");
	}
	
	
	@Override
	public void onExit() {
		super.onExit();
		Logger.e("game", "exit---------------LevelSelectLayer");
	}

}
