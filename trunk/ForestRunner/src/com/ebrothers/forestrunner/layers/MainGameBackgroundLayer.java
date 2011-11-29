package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;

import com.ebrothers.forestrunner.sprites.Cloud;


/**
 * 主界面 背景
 * @author Administrator
 *
 */
public class MainGameBackgroundLayer extends BasicLayer {
	
	
	public MainGameBackgroundLayer(){
		super();
		
		CCSprite sprite02 = getNode("game_start02.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);
		
		Cloud cloud1 = new Cloud("cloud01.png");
		cloud1.actionMoveHorizontal(width, 0, height*9/11, 12);
		addChild(cloud1, 2);
		
		Cloud cloud2 = new Cloud("cloud01.png");
		cloud2.actionMoveHorizontal(width*3/2, 0, height*8/11, 25);
		addChild(cloud2, 2);
		
		Cloud cloud3 = new Cloud("cloud01.png");
		cloud3.actionMoveHorizontal(width-30, 0, height*7/11, 15);
		addChild(cloud3, 2);

		CCSprite sprite01 = getNode("game_start01.png", 0, 0, 0, 0);
		addChild(sprite01, 3);
		
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
