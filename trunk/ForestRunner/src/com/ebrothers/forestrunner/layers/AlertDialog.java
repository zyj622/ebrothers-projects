package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGSize;

import com.ebrothers.forestrunner.common.Game;

public class AlertDialog extends AbstractDialog {

	public AlertDialog(CCNode parent) {
		super(parent);
	}

	public static class Builder {
		private CCSprite background;
		private CCNode _parent;
		private CCBitmapFontAtlas title;
		private CCBitmapFontAtlas message;
		private CCMenuItemSprite negButton;
		private CCMenuItemSprite posButton;

		public Builder(CCNode parent) {
			_parent = parent;
		}

		public Builder setBackground(String frameName) {
			background = CCSprite.sprite(CCSpriteFrameCache
					.sharedSpriteFrameCache().getSpriteFrame(frameName));
			CGSize winSize = CCDirector.sharedDirector().winSize();
			background.setPosition(winSize.width / 2f, winSize.height / 2f);
			return this;
		}

		public Builder setTitle(String text, String fntFile) {
			title = CCBitmapFontAtlas.bitmapFontAtlas(text, fntFile);
			title.setAnchorPoint(0, 0);
			title.setPosition(20, -20);
			title.setScale(Game.scale_ratio);
			return this;
		}

		public Builder setMessage(String msg, String fntFile) {
			message = CCBitmapFontAtlas.bitmapFontAtlas(msg, fntFile);
			message.setAnchorPoint(0, 1);
			message.setScale(Game.scale_ratio);
			return this;
		}

		public Builder setNegativeButton(String normalFrame,
				String selectedFrame, CCNode target, String selector) {
			CCSpriteFrameCache cache = CCSpriteFrameCache
					.sharedSpriteFrameCache();
			negButton = CCMenuItemSprite.item(
					CCSprite.sprite(cache.getSpriteFrame(normalFrame)),
					CCSprite.sprite(cache.getSpriteFrame(selectedFrame)),
					target, selector);
			return this;
		}

		public Builder setPositiveButton(String normalFrame,
				String selectedFrame, CCNode target, String selector) {
			CCSpriteFrameCache cache = CCSpriteFrameCache
					.sharedSpriteFrameCache();
			posButton = CCMenuItemSprite.item(
					CCSprite.sprite(cache.getSpriteFrame(normalFrame)),
					CCSprite.sprite(cache.getSpriteFrame(selectedFrame)),
					target, selector);
			return this;
		}

		public CCBitmapFontAtlas getMessage() {
			return message;
		}

		public CCSprite getBackground() {
			return background;
		}

		public AlertDialog create() {
			assert (background != null);
			assert (message != null);
			AlertDialog dialog = new AlertDialog(_parent);
			dialog.addChild(background);
			float _y = -30;
			if (title != null) {
				background.addChild(title);
				_y -= title.getContentSize().height * title.getScaleY();
			}
			message.setPosition(
					background.getPosition().x
							- background.getContentSize().width
							* background.getScaleX() / 2f + 20,
					_y + background.getPosition().y
							+ background.getContentSize().height
							* background.getScaleY() / 2f);
			dialog.addChild(message);
			CCMenu menu = CCMenu.menu(posButton, negButton);
			menu.setAnchorPoint(0.5f, 0);
			menu.alignItemsHorizontally(10);
			menu.setPosition(background.getContentSize().width / 2f, 25);
			background.addChild(menu);
			return dialog;
		}
	}
}
