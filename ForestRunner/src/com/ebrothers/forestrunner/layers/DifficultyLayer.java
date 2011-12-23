package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.ccColor3B;

import com.ebrothers.forestrunner.common.Constants;
import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SoundManager;

public class DifficultyLayer extends MenuLayer {
	CCSprite spriteDifficultyNormal;
	CCSprite spriteDifficultySelector;

	CCSprite spriteNormalNofmal;
	CCSprite spriteNormalSelector;

	CCSprite spriteEasyNomal;
	CCSprite spriteEasySelector;

	public DifficultyLayer() {
		super();

		CCSprite sprite02 = getNode("game_choose.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);

		CCBitmapFontAtlas hardAtlasNormal = CCBitmapFontAtlas.bitmapFontAtlas(
				"Hard", "font1.fnt");
		hardAtlasNormal.setColor(ccColor3B.ccc3(156, 97, 0));
		CCBitmapFontAtlas hardAtlasPress = CCBitmapFontAtlas.bitmapFontAtlas(
				"Hard", "font1.fnt");
		hardAtlasPress.setColor(ccColor3B.ccRED);
		CCMenuItemSprite difficulty = CCMenuItemSprite.item(hardAtlasNormal,
				hardAtlasNormal, this, "setDifficulty");
		difficulty.setScale(Game.scale_ratio * 1.5f);
		difficulty.setTag(1);
		difficulty.setAnchorPoint(0, 0.5f);
		CCMenu difficultyMenu = CCMenu.menu(difficulty);
		difficultyMenu.setPosition(winW * 2 / 3f, winH / 3f);
		addChild(difficultyMenu, 2);

		CCBitmapFontAtlas normalAtlasNormal = CCBitmapFontAtlas
				.bitmapFontAtlas("Normal", "font1.fnt");
		normalAtlasNormal.setColor(ccColor3B.ccc3(156, 97, 0));
		CCBitmapFontAtlas normalAtlasPress = CCBitmapFontAtlas.bitmapFontAtlas(
				"Normal", "font1.fnt");
		normalAtlasPress.setColor(ccColor3B.ccRED);
		CCMenuItemSprite normal = CCMenuItemSprite.item(normalAtlasNormal,
				normalAtlasNormal, this, "setDifficulty");
		normal.setScale(Game.scale_ratio * 1.5f);
		normal.setTag(2);
		normal.setAnchorPoint(0, 0.5f);
		CCMenu normalMenu = CCMenu.menu(normal);
		normalMenu.setPosition(winW * 2 / 3f, winH / 2f);
		addChild(normalMenu, 2);

		CCBitmapFontAtlas easyAtlasNormal = CCBitmapFontAtlas.bitmapFontAtlas(
				"Easy", "font1.fnt");
		easyAtlasNormal.setColor(ccColor3B.ccc3(156, 97, 0));
		CCBitmapFontAtlas easyAtlasPress = CCBitmapFontAtlas.bitmapFontAtlas(
				"Easy", "font1.fnt");
		easyAtlasPress.setColor(ccColor3B.ccRED);
		CCMenuItemSprite easy = CCMenuItemSprite.item(easyAtlasNormal,
				easyAtlasNormal, this, "setDifficulty");
		easy.setScale(Game.scale_ratio * 1.5f);
		easy.setTag(3);
		easy.setAnchorPoint(0, 0.5f);
		CCMenu easyMenu = CCMenu.menu(easy);
		easyMenu.setPosition(winW * 2 / 3f, winH * 2 / 3f);
		addChild(easyMenu, 2);

		spriteDifficultyNormal = CCSprite.sprite(cache
				.getSpriteFrame("checkbox01.png"));
		CCMenuItemSprite difficultyMenuItemDifficulty = CCMenuItemSprite.item(
				spriteDifficultyNormal, spriteDifficultyNormal, this,
				"setDifficulty");
		difficultyMenuItemDifficulty.setAnchorPoint(1, 0.5f);
		difficultyMenuItemDifficulty.setTag(1);
		difficultyMenuItemDifficulty.setScale(Game.scale_ratio_x);
		CCMenu difficultyMenuDifficulty = CCMenu
				.menu(difficultyMenuItemDifficulty);
		difficultyMenuDifficulty.setPosition(winW * 2 / 3f - 20f, winH / 3f);
		addChild(difficultyMenuDifficulty, 2);

		spriteDifficultySelector = CCSprite.sprite(cache
				.getSpriteFrame("checkbox02.png"));
		spriteDifficultySelector.setAnchorPoint(1, 0.5f);
		spriteDifficultySelector.setScale(Game.scale_ratio_x);
		spriteDifficultySelector.setPosition(winW * 2 / 3f - 20f, winH / 3f);
		addChild(spriteDifficultySelector, 2);

		spriteNormalNofmal = CCSprite.sprite(cache
				.getSpriteFrame("checkbox01.png"));
		CCMenuItemSprite difficultyMenuItemNormal = CCMenuItemSprite.item(
				spriteNormalNofmal, spriteNormalNofmal, this, "setDifficulty");
		difficultyMenuItemNormal.setAnchorPoint(1, 0.5f);
		difficultyMenuItemNormal.setTag(2);
		difficultyMenuItemNormal.setScale(Game.scale_ratio_x);
		CCMenu difficultyMenuNormal = CCMenu.menu(difficultyMenuItemNormal);
		difficultyMenuNormal.setPosition(winW * 2 / 3f - 20f, winH / 2f);
		addChild(difficultyMenuNormal, 2);

		spriteNormalSelector = CCSprite.sprite(cache
				.getSpriteFrame("checkbox02.png"));
		spriteNormalSelector.setAnchorPoint(1, 0.5f);
		spriteNormalSelector.setScale(Game.scale_ratio_x);
		spriteNormalSelector.setPosition(winW * 2 / 3f - 20f, winH / 2f);
		addChild(spriteNormalSelector, 2);

		spriteEasyNomal = CCSprite.sprite(cache
				.getSpriteFrame("checkbox01.png"));
		CCMenuItemSprite difficultyMenuItemEasy = CCMenuItemSprite.item(
				spriteEasyNomal, spriteEasyNomal, this, "setDifficulty");
		difficultyMenuItemEasy.setAnchorPoint(1, 0.5f);
		difficultyMenuItemEasy.setTag(3);
		difficultyMenuItemEasy.setScale(Game.scale_ratio_x);
		CCMenu difficultyMenuEasy = CCMenu.menu(difficultyMenuItemEasy);
		difficultyMenuEasy.setPosition(winW * 2 / 3f - 20f, winH * 2 / 3f);
		addChild(difficultyMenuEasy, 2);
		spriteEasySelector = CCSprite.sprite(cache
				.getSpriteFrame("checkbox02.png"));
		spriteEasySelector.setAnchorPoint(1, 0.5f);
		spriteEasySelector.setScale(Game.scale_ratio_x);
		spriteEasySelector.setPosition(winW * 2 / 3f - 20f, winH * 2 / 3f);
		addChild(spriteEasySelector, 2);

		showCheckBox();
	}

	public void showCheckBox() {
		// 默认是Normal
		String difficultyKey = (String) LocalDataManager.getInstance()
				.readSetting(LocalDataManager.DIFFICULTY_KEY, Constants.NORMAL);
		if (difficultyKey.equals(Constants.NORMAL)) {
			spriteDifficultySelector.setVisible(false);
			spriteNormalSelector.setVisible(true);
			spriteEasySelector.setVisible(false);
		} else if (difficultyKey.equals(Constants.EASY)) {
			spriteDifficultySelector.setVisible(false);
			spriteNormalSelector.setVisible(false);
			spriteEasySelector.setVisible(true);
		} else if (difficultyKey.equals(Constants.HARD)) {
			spriteDifficultySelector.setVisible(true);
			spriteNormalSelector.setVisible(false);
			spriteEasySelector.setVisible(false);
		}
	}

	public void setDifficulty(Object o) {
		CCMenuItemSprite cmis = (CCMenuItemSprite) o;
		if (cmis.isEnabled()) {
			SoundManager.sharedSoundManager().playEffect(
					SoundManager.MUSIC_BUTTON);
			int tag = cmis.getTag();
			if (tag == 1) {
				LocalDataManager.getInstance().writeSetting(
						LocalDataManager.DIFFICULTY_KEY, Constants.HARD);
			} else if (tag == 3) {
				LocalDataManager.getInstance().writeSetting(
						LocalDataManager.DIFFICULTY_KEY, Constants.EASY);
			} else {
				LocalDataManager.getInstance().writeSetting(
						LocalDataManager.DIFFICULTY_KEY, Constants.NORMAL);
			}
			showCheckBox();
		}
	}

}
