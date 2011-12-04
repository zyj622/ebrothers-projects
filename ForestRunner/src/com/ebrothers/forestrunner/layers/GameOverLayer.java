package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.ccColor3B;

import com.ebrothers.forestrunner.common.Globals;


public class GameOverLayer extends BasicLayer {

	public GameOverLayer() {
		super();
		
		String scoreCount = "50000";

		CCSprite sprite02 = getNode("game_over.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);
		
		
		CCSprite spriteName = getNode("gameover_stage1.png", width * 9.3f / 10,
				height * 7.4f / 8, 1, 1);
		addChild(spriteName, 2);
		
		CCBitmapFontAtlas score = CCBitmapFontAtlas.bitmapFontAtlas("Score:"+scoreCount, "font1.fnt");
		score.setAnchorPoint(1, 1);
		score.setPosition( width * 9.3f / 10, height * 6.0f / 8);
		addChild(score,3);
		score.setColor(ccColor3B.ccRED);
		
		/****************************************************************************************/
		
		/****************************************************************************************/
		
	}
	
	
	
	@Override
	public void onEnter() {
		super.onEnter();
	}
	
	
	@Override
	public void onExit() {
		super.onExit();
	}

}
