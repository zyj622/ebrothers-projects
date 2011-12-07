package com.ebrothers.forestrunner.layers;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGSize;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.sprites.GameSprite;

public class GameMenuLayer extends CCLayer {
	private CCMenuItemToggle pauseToggle;
	private CCBitmapFontAtlas score;
	private CCBitmapFontAtlas life;

	public GameMenuLayer() {
		super();
		// add stage title
		CGSize winSize = CCDirector.sharedDirector().winSize();
		GameSprite title = GameSprite.sprite("gameover_stage"
				+ (Game.current_level + 1) + ".png");
		title.setAnchorPoint(0.5f, 1);
		title.setPosition(winSize.width / 2f, winSize.height);
		addChild(title);

		// add score
		GameSprite scoreIcon = GameSprite.sprite("score01.png");
		scoreIcon.setAnchorPoint(0, 1);
		scoreIcon.setPosition(0, winSize.height);
		addChild(scoreIcon);

		score = CCBitmapFontAtlas.bitmapFontAtlas("+0", "font2.fnt");
		score.setAnchorPoint(0, 1);
		score.setPosition(
				scoreIcon.getPosition().x + scoreIcon.getBoundingWidth(),
				winSize.height);
		addChild(score);

		// add life counter
		life = CCBitmapFontAtlas.bitmapFontAtlas("x4", "font2.fnt");
		life.setAnchorPoint(1, 1);
		life.setPosition(winSize.width, winSize.height);
		addChild(life);

		GameSprite lifeIcon = GameSprite.sprite("life01.png");
		lifeIcon.setAnchorPoint(1, 1);
		lifeIcon.setPosition(life.getPosition().x
				- life.getBoundingBox().size.width, winSize.height);
		addChild(lifeIcon);

		// pause/resume
		GameSprite resumeSprite = GameSprite.sprite("pause02.png");
		GameSprite pauseSprite = GameSprite.sprite("pause01.png");
		CCMenuItemSprite resume = CCMenuItemSprite.item(resumeSprite,
				resumeSprite);
		CCMenuItemSprite pause = CCMenuItemSprite
				.item(pauseSprite, pauseSprite);
		pauseToggle = CCMenuItemToggle.item(this, "onPauseOrResume", resume,
				pause);
		pauseToggle.setAnchorPoint(0, 0);
		pauseToggle.setPosition(0, 0);
		CCMenu menu = CCMenu.menu(pauseToggle);
		menu.setScale(Game.scale_ratio);
		menu.setAnchorPoint(0, 0);
		menu.setPosition(0, 0);
		addChild(menu);
	}

	public void pause() {
		pauseToggle.setSelectedIndex(0);
	}

	public void resume() {
		pauseToggle.setSelectedIndex(1);
	}

	public void setMenuEnable(boolean flag) {
		pauseToggle.setIsEnabled(flag);
	}

	public void setRemainLives(int remain) {
		life.setString("x" + remain);
	}

	public void updateScore() {
		score.setString("+" + Game.score);
	}
}
