package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;

public class DifficultyLayer extends MenuLayer {
	
	
	
	public DifficultyLayer(){
		super();
		
		
		CCSprite sprite02 = getNode("game_choose.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);
		
		
		
		
	}
	
	

}
