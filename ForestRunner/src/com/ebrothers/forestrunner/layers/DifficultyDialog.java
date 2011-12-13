package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGSize;

import com.ebrothers.forestrunner.common.Game;

public class DifficultyDialog extends AbstractDialog {
	public static DifficultyDialog dialog(CCNode parent) {
		return new DifficultyDialog(parent);
	}

	private DifficultyDialog(CCNode parent) {
		super(parent);

		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSprite background = CCSprite.sprite(cache
				.getSpriteFrame("alert_dialog_bg.png"));
		background.setScaleY(2.5f);
		CGSize winSize = CCDirector.sharedDirector().winSize();
		background.setPosition(winSize.width / 2f, winSize.height / 2f);
		addChild(background);

		CCSprite normalSprite = CCSprite.sprite(cache
				.getSpriteFrame("button_back01.png"));
		CCSprite selectedSprite = CCSprite.sprite(cache
				.getSpriteFrame("button_back02.png"));
		CCMenuItemSprite back = CCMenuItemSprite.item(normalSprite,
				selectedSprite, this, "onBack");
		CCMenu menu = CCMenu.menu(back);
		menu.setAnchorPoint(0.5f, 0);
		menu.setScale(Game.scale_ratio);
		menu.setPosition(winSize.width / 2f,
				winSize.height / 2f - background.getContentSize().height / 2f
						+ 20f);
		addChild(menu);
	}

	public void onBack(Object o) {
		dismiss();
	}

}
