package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import com.ebrothers.forestrunner.common.Constants;
import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SoundManager;

public class DifficultyDialog extends AbstractDialog {
	CCSprite spriteDifficultyNormal;
	CCSprite spriteDifficultySelector;

	CCSprite spriteNormalNofmal;
	CCSprite spriteNormalSelector;

	CCSprite spriteEasyNomal;
	CCSprite spriteEasySelector;

	public static DifficultyDialog dialog(CCNode parent) {
		return new DifficultyDialog(parent);
	}

	private DifficultyDialog(CCNode parent) {
		super(parent);
		// 背景
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSprite background = CCSprite.sprite(cache
				.getSpriteFrame("alert_dialog_bg.png"));
		background.setScaleY(Game.scale_ratio_y * 1.5f);
		CGSize winSize = CCDirector.sharedDirector().winSize();
		background.setPosition(winSize.width / 2f, winSize.height / 2f);
		addChild(background, 1);
		// back
		CCSprite normalSprite = CCSprite.sprite(cache
				.getSpriteFrame("button_back01.png"));
		CCSprite selectedSprite = CCSprite.sprite(cache
				.getSpriteFrame("button_back02.png"));
		CCMenuItemSprite back = CCMenuItemSprite.item(normalSprite,
				selectedSprite, this, "onBack");
		back.setAnchorPoint(0.5f, 1);
		CCMenu menu = CCMenu.menu(back);
		menu.setAnchorPoint(0, 0);
		menu.setScale(Game.scale_ratio);
		menu.setPosition(winSize.width / 2f,
				winSize.height / 2f - normalSprite.getContentSize().height
						* Game.scale_ratio * 1.3f);
		addChild(menu, 2);

		CCBitmapFontAtlas difficultyAtlasNormal = CCBitmapFontAtlas
				.bitmapFontAtlas("Hard", "font1.fnt");
		difficultyAtlasNormal.setColor(ccColor3B.ccc3(156, 97, 0));
		CCBitmapFontAtlas difficultyAtlasPress = CCBitmapFontAtlas
				.bitmapFontAtlas("Hard", "font1.fnt");
		difficultyAtlasPress.setColor(ccColor3B.ccRED);
		CCMenuItemSprite difficulty = CCMenuItemSprite.item(
				difficultyAtlasNormal, difficultyAtlasPress, this,
				"difficultySelect");
		difficulty.setScale(Game.scale_ratio);
		difficulty.setTag(1);
		difficulty.setAnchorPoint(0, 1);
		CCMenu difficultyMenu = CCMenu.menu(difficulty);
		difficultyMenu.setPosition(winSize.width / 2f - 20f,
				winSize.height / 2f);
		addChild(difficultyMenu, 2);

		CCBitmapFontAtlas normalAtlasNormal = CCBitmapFontAtlas
				.bitmapFontAtlas("Normal", "font1.fnt");
		normalAtlasNormal.setColor(ccColor3B.ccc3(156, 97, 0));
		CCBitmapFontAtlas normalAtlasPress = CCBitmapFontAtlas.bitmapFontAtlas(
				"Normal", "font1.fnt");
		normalAtlasPress.setColor(ccColor3B.ccRED);
		CCMenuItemSprite normal = CCMenuItemSprite.item(normalAtlasNormal,
				normalAtlasPress, this, "difficultySelect");
		normal.setScale(Game.scale_ratio);
		normal.setTag(2);
		normal.setAnchorPoint(0, 0.5f);
		CCMenu normalMenu = CCMenu.menu(normal);
		normalMenu.setPosition(winSize.width / 2f - 20f, winSize.height / 2f
				+ difficulty.getContentSize().height * 1.2f);
		addChild(normalMenu, 2);

		CCBitmapFontAtlas easyAtlasNormal = CCBitmapFontAtlas.bitmapFontAtlas(
				"Easy", "font1.fnt");
		easyAtlasNormal.setColor(ccColor3B.ccc3(156, 97, 0));
		CCBitmapFontAtlas easyAtlasPress = CCBitmapFontAtlas.bitmapFontAtlas(
				"Easy", "font1.fnt");
		easyAtlasPress.setColor(ccColor3B.ccRED);
		CCMenuItemSprite easy = CCMenuItemSprite.item(easyAtlasNormal,
				easyAtlasPress, this, "difficultySelect");
		easy.setScale(Game.scale_ratio);
		easy.setTag(3);
		easy.setAnchorPoint(0, 0.1f);
		CCMenu easyMenu = CCMenu.menu(easy);
		easyMenu.setPosition(winSize.width / 2f - 20f, winSize.height / 2f
				+ difficulty.getContentSize().height * 2.4f);
		addChild(easyMenu, 2);

		spriteDifficultyNormal = CCSprite.sprite(cache
				.getSpriteFrame("checkbox01.png"));
		spriteDifficultyNormal.setAnchorPoint(1, 0.9f);
		spriteDifficultyNormal.setScale(Game.scale_ratio_x);
		spriteDifficultyNormal.setPosition(winSize.width / 2f - 50f,
				winSize.height / 2f);
		addChild(spriteDifficultyNormal, 2);
		spriteDifficultySelector = CCSprite.sprite(cache
				.getSpriteFrame("checkbox02.png"));
		spriteDifficultySelector.setAnchorPoint(1, 0.9f);
		spriteDifficultySelector.setScale(Game.scale_ratio_x);
		spriteDifficultySelector.setPosition(winSize.width / 2f - 50f,
				winSize.height / 2f);
		addChild(spriteDifficultySelector, 2);

		spriteNormalNofmal = CCSprite.sprite(cache
				.getSpriteFrame("checkbox01.png"));
		spriteNormalNofmal.setAnchorPoint(1, 0.4f);
		spriteNormalNofmal.setScale(Game.scale_ratio_x);
		spriteNormalNofmal.setPosition(winSize.width / 2f - 50f, winSize.height
				/ 2f + difficulty.getContentSize().height * 1.2f);
		addChild(spriteNormalNofmal, 2);
		spriteNormalSelector = CCSprite.sprite(cache
				.getSpriteFrame("checkbox02.png"));
		spriteNormalSelector.setAnchorPoint(1, 0.4f);
		spriteNormalSelector.setScale(Game.scale_ratio_x);
		spriteNormalSelector
				.setPosition(winSize.width / 2f - 50f, winSize.height / 2f
						+ difficulty.getContentSize().height * 1.2f);
		addChild(spriteNormalSelector, 2);

		spriteEasyNomal = CCSprite.sprite(cache
				.getSpriteFrame("checkbox01.png"));
		spriteEasyNomal.setAnchorPoint(1, 0);
		spriteEasyNomal.setScale(Game.scale_ratio_x);
		spriteEasyNomal.setPosition(winSize.width / 2f - 50f, winSize.height
				/ 2f + difficulty.getContentSize().height * 2.4f);
		addChild(spriteEasyNomal, 2);
		spriteEasySelector = CCSprite.sprite(cache
				.getSpriteFrame("checkbox02.png"));
		spriteEasySelector.setAnchorPoint(1, 0);
		spriteEasySelector.setScale(Game.scale_ratio_x);
		spriteEasySelector.setPosition(winSize.width / 2f - 50f, winSize.height
				/ 2f + difficulty.getContentSize().height * 2.4f);
		addChild(spriteEasySelector, 2);

		showCheckBox();
	}

	public void showCheckBox() {
		// 默认是Normal
		String difficultyKey = (String) LocalDataManager.getInstance()
				.readSetting(LocalDataManager.DIFFICULTY_KEY, "");
		if (difficultyKey.equals("") || difficultyKey.equals(Constants.NORMAL)) {
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

	public void onBack(Object o) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		dismiss();
	}

	public void difficultySelect(Object o) {
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
