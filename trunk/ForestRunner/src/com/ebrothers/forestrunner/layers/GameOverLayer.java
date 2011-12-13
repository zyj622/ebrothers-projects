package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.ccColor3B;

import com.ebrothers.forestrunner.common.Game;

public class GameOverLayer extends MenuLayer {

	public GameOverLayer(long score) {
		super();

		CCSprite sprite02 = getNode("game_over.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);

		CCSprite spriteName = getNode("gameover_stage"
				+ (Game.current_level + 1) + ".png", winW * 9.3f / 10,
				winH * 7.4f / 8, 1, 1);
		addChild(spriteName, 2);

		CCBitmapFontAtlas scoreAtlas = CCBitmapFontAtlas.bitmapFontAtlas(
				"Score:" + score, "font1.fnt");
		scoreAtlas.setAnchorPoint(1, 1);
		scoreAtlas.setPosition(winW * 9.3f / 10, winH * 6.0f / 8);
		addChild(scoreAtlas, 3);
		scoreAtlas.setColor(ccColor3B.ccc3(156, 97, 0));

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
