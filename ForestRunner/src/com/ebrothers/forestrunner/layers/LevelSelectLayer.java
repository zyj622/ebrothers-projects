package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.sprites.LevelSelector;

public class LevelSelectLayer extends BasicLayer {

	public LevelSelectLayer() {
		super();
		CCSprite spriteBackground = getNode("game_choose.jpg", 0, 0, 0, 0);
		addChild(spriteBackground, 1);
		// 游戏名字
		CCSprite spriteName = getNode("image_select.png", width * 9.3f / 10,
				height * 7 / 8, 1, 1);
		addChild(spriteName, 2);

		/*********************************************************************************/
		CCSprite spriteNormal01 = LevelSelector.levelSprite(1, 1, true);
		CCSprite spriteNormal02 = LevelSelector.levelSprite(2, 50000, true);
		CCSprite spriteNormal03 = LevelSelector.levelSprite(3, 1, true);

		CCSprite spritePressed01 = LevelSelector.levelSprite(1, 1, false);
		CCSprite spritePressed02 = LevelSelector.levelSprite(2, 50000, false);
		CCSprite spritePressed03 = LevelSelector.levelSprite(3, 1, false);

		CCMenuItemSprite menuItem01 = CCMenuItemSprite.item(spriteNormal01,
				spritePressed01, this, "levelSelect");
		menuItem01.setTag(1);
		menuItem01.setScale(Game.scale_ratio_y);
		menuItem01.setAnchorPoint(1, 0.5f);

		CCMenuItemSprite menuItem02 = CCMenuItemSprite.item(spriteNormal02,
				spritePressed02, this, "levelSelect");
		menuItem02.setTag(2);
		menuItem02.setScale(Game.scale_ratio_y);
		menuItem02.setAnchorPoint(1, 0.5f);

		CCMenuItemSprite menuItem03 = CCMenuItemSprite.item(spriteNormal03,
				spritePressed03, this, "levelSelect");
		menuItem03.setTag(3);
		menuItem03.setScale(Game.scale_ratio_y);
		menuItem03.setAnchorPoint(1, 0.5f);

		CCMenu menu01 = CCMenu.menu(menuItem01, menuItem02, menuItem03);
		menu01.alignItemsHorizontally();
		float offsetX01 = 0f;
		float offsetY01 = 0f;
		for (CCNode child : menu01.getChildren()) {
			final CGPoint point = child.getPositionRef();
			offsetX01 = point.x;
			offsetY01 = point.y;
			break;
		}
		menu01.setPosition(((width - Math.abs(offsetX01)) * 9.5f) / 10f,
				((height - Math.abs(offsetY01)) * 3f) / 5f);
		addChild(menu01, 2);
		/*********************************************************************************/
		/*********************************************************************************/
		/*********************************************************************************/

		CCSprite spriteNormal04 = LevelSelector.levelSprite(4, 60000, true);
		CCSprite spriteNormal05 = LevelSelector.levelSprite(5, 0, true);
		CCSprite spriteNormal06 = LevelSelector.levelSprite(6, 0, true);

		CCSprite spritePressed04 = LevelSelector.levelSprite(4, 60000, false);
		CCSprite spritePressed05 = LevelSelector.levelSprite(5, 0, false);
		CCSprite spritePressed06 = LevelSelector.levelSprite(6, 0, false);

		CCMenuItemSprite menuItem04 = CCMenuItemSprite.item(spriteNormal04,
				spritePressed04, this, "levelSelect");
		menuItem04.setTag(4);
		menuItem04.setScale(Game.scale_ratio_y);
		menuItem04.setAnchorPoint(1, 0.5f);

		CCMenuItemSprite menuItem05 = CCMenuItemSprite.item(spriteNormal05,
				spritePressed05, this, "levelSelect");
		menuItem05.setTag(5);
		menuItem05.setScale(Game.scale_ratio_y);
		menuItem05.setAnchorPoint(1, 0.5f);

		CCMenuItemSprite menuItem06 = CCMenuItemSprite.item(spriteNormal06,
				spritePressed06, this, "levelSelect");
		menuItem06.setTag(6);
		menuItem06.setScale(Game.scale_ratio_y);
		menuItem06.setAnchorPoint(1, 0.5f);

		CCMenu menu02 = CCMenu.menu(menuItem04, menuItem05, menuItem06);
		menu02.alignItemsHorizontally();
		float offsetX02 = 0f;
		float offsetY02 = 0f;
		for (CCNode child : menu02.getChildren()) {
			final CGPoint point = child.getPositionRef();
			offsetX02 = point.x;
			offsetY02 = point.y;
			break;
		}
		menu02.setPosition(((width - Math.abs(offsetX02)) * 9.5f) / 10f,
				((height - Math.abs(offsetY02)) * 2f) / 5f);
		addChild(menu02, 2);

		/*********************************************************************************/
		/*********************************************************************************/
		/*********************************************************************************/

		CCSprite spriteNormal07 = LevelSelector.levelSprite(7, 0, true);
		CCSprite spriteNormal08 = LevelSelector.levelSprite(8, 0, true);
		CCSprite spriteNormal09 = LevelSelector.levelSprite(1, 0, true);

		CCSprite spritePressed07 = LevelSelector.levelSprite(7, 0, false);
		CCSprite spritePressed08 = LevelSelector.levelSprite(8, 0, false);
		CCSprite spritePressed09 = LevelSelector.levelSprite(1, 0, false);

		CCMenuItemSprite menuItem07 = CCMenuItemSprite.item(spriteNormal07,
				spritePressed07, this, "levelSelect");
		menuItem07.setTag(7);
		menuItem07.setScale(Game.scale_ratio_y);
		menuItem07.setAnchorPoint(1, 0.5f);

		CCMenuItemSprite menuItem08 = CCMenuItemSprite.item(spriteNormal08,
				spritePressed08, this, "levelSelect");
		menuItem08.setTag(8);
		menuItem08.setScale(Game.scale_ratio_y);
		menuItem08.setAnchorPoint(1, 0.5f);

		CCMenuItemSprite menuItem09 = CCMenuItemSprite.item(spriteNormal09,
				spritePressed09, this, "levelSelect");
		menuItem09.setScale(Game.scale_ratio_y);
		menuItem09.setAnchorPoint(1, 0.5f);
		menuItem09.setVisible(false);

		CCMenu menu03 = CCMenu.menu(menuItem07, menuItem08, menuItem09);
		menu03.alignItemsHorizontally();
		float offsetX03 = 0f;
		float offsetY03 = 0f;
		for (CCNode child : menu03.getChildren()) {
			final CGPoint point = child.getPositionRef();
			offsetX03 = point.x;
			offsetY03 = point.y;
			break;
		}
		menu03.setPosition(((width - Math.abs(offsetX03)) * 9.5f) / 10f,
				((height - Math.abs(offsetY03)) * 1f) / 5f);
		addChild(menu03, 2);

		/*********************************************************************************/

	}

	public void levelSelect(Object o) {
		CCMenuItemSprite cmis = (CCMenuItemSprite) o;
		Game.current_level = cmis.getTag() - 1;
		SceneManager.getInstance().replaceTo(SceneManager.SCENE_GAME);
		Logger.e("game", String.valueOf(cmis.getTag()));
	}

	@Override
	public void onEnter() {
		super.onEnter();
		Logger.e("game", "enter---------------LevelSelectLayer");
	}

	@Override
	public void onExit() {
		super.onExit();
		Logger.e("game", "exit---------------LevelSelectLayer");
	}

}
