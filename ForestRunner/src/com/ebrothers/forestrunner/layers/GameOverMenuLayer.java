package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.ebrothers.forestrunner.common.Globals;

public class GameOverMenuLayer extends BasicLayer {
	
	
	
	public GameOverMenuLayer(){
		super();
		
		boolean overFlag = true;
		
		// 游戏菜单
		CCSprite spriteAgain = getNode("button_again01.png", 0, 0);
		CCSprite spriteAgainSelect = getNode("button_again02.png", 0, 0);
		CCMenuItemSprite cmsStart = CCMenuItemSprite.item(spriteAgain,
				spriteAgainSelect, this, "againGame");
		cmsStart.setScaleX(Globals.scale_ratio_x);
		cmsStart.setScaleY(Globals.scale_ratio_y);
		cmsStart.setAnchorPoint(1, 1);

		CCSprite spriteHigh = getNode("button_next01.png", 0, 0);
		CCSprite spriteHighSelect = getNode("button_next02.png", 0, 0);
		CCMenuItemSprite cmsHigh = CCMenuItemSprite.item(spriteHigh,
				spriteHighSelect, this, "nextStage");
		cmsHigh.setScaleX(Globals.scale_ratio_x);
		cmsHigh.setScaleY(Globals.scale_ratio_y);
		cmsHigh.setAnchorPoint(1, 1);

		CCSprite spriteMore = getNode("button_more01.png", 0, 0);
		CCSprite spriteMoreSelect = getNode("button_more02.png", 0, 0);
		CCMenuItemSprite cmsMore = CCMenuItemSprite.item(spriteMore,
				spriteMoreSelect, this, "more");

		cmsMore.setScaleX(Globals.scale_ratio_x);
		cmsMore.setScaleY(Globals.scale_ratio_y);
		cmsMore.setAnchorPoint(1, 1);
		
		CCMenu cmMenu = null;
		if(overFlag){
			cmMenu = CCMenu.menu(cmsStart, cmsHigh, cmsMore);
		}else{
			cmMenu = CCMenu.menu(cmsStart, cmsHigh, cmsMore);
		}
		
		cmMenu.setAnchorPoint(1, 1);
		cmMenu.alignItemsVertically();
		cmMenu.alignItemsVertically(10f);
		float offsetX = 0f;
		float offsetY = 0f;
		for (CCNode child : cmMenu.getChildren()) {
			final CGPoint point = child.getPositionRef();
			offsetX = point.x;
			offsetY = point.y;
			break;
		}
		cmMenu.setPosition((width * 9.3f / 10)-Math.abs(offsetX), (height * 4f / 8)-Math.abs(offsetY));
		
		addChild(cmMenu, 5);
		
		
		
	}
	
	
	public void againGame(Object o){
		
	}
	
	public void nextStage(Object o){
		
	}
	
	public void more(Object o){
		moreGame();
	}

}
