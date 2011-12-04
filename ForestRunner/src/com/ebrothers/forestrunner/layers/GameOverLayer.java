package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.ccColor3B;

import com.ebrothers.forestrunner.common.Globals;

public class GameOverLayer extends BasicLayer {

	public GameOverLayer() {
		super();

		CCSprite sprite02 = getNode("game_over.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);
		
		
		CCSprite spriteName = getNode("gameover_stage1.png", width * 9.3f / 10,
				height * 7.4f / 8, 1, 1);
		addChild(spriteName, 2);
		
		CCBitmapFontAtlas score = CCBitmapFontAtlas.bitmapFontAtlas("567", "font1.fnt");
		score.setAnchorPoint(0, 1);
		score.setPosition(680, 380);
		addChild(score,3);
		score.setColor(ccColor3B.ccRED);
		
		/****************************************************************************************/
		
		// 游戏菜单
		CCSprite spritePlay = getNode("button_play01.png", 0, 0);
		CCSprite spritePlaySelect = getNode("button_play02.png", 0, 0);
		CCMenuItemSprite cmsStart = CCMenuItemSprite.item(spritePlay,
				spritePlaySelect, this, "startGame");
		cmsStart.setScaleX(Globals.scale_ratio_x);
		cmsStart.setScaleY(Globals.scale_ratio_y);
		cmsStart.setAnchorPoint(1, 0);

		CCSprite spriteHigh = getNode("button_high01.png", 0, 0);
		CCSprite spriteHighSelect = getNode("button_high02.png", 0, 0);
		CCMenuItemSprite cmsHigh = CCMenuItemSprite.item(spriteHigh,
				spriteHighSelect, this, "startHigh");
		cmsHigh.setScaleX(Globals.scale_ratio_x);
		cmsHigh.setScaleY(Globals.scale_ratio_y);
		cmsHigh.setAnchorPoint(1, 0);

		CCSprite spriteMore = getNode("button_more01.png", 0, 0);
		CCSprite spriteMoreSelect = getNode("button_more02.png", 0, 0);
		CCMenuItemSprite cmsMore = CCMenuItemSprite.item(spriteMore,
				spriteMoreSelect, this, "more");

		cmsMore.setScaleX(Globals.scale_ratio_x);
		cmsMore.setScaleY(Globals.scale_ratio_y);
		cmsMore.setAnchorPoint(1, 0);
		
		
//		CCMenu cmMenu = CCMenu.menu(cmsStart, cmsHigh, cmsMore);
//		cmMenu.setAnchorPoint(1, 0);
//		cmMenu.alignItemsVertically();
//		cmMenu.setPosition(width - 130, 130);
//		cmMenu.alignItemsVertically(10f);
//		addChild(cmMenu, 5);
		
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
