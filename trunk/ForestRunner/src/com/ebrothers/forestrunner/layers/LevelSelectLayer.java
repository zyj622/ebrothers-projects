package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;

import com.ebrothers.forestrunner.common.Constants;
import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Levels;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.manager.SoundManager;
import com.ebrothers.forestrunner.sprites.LevelSelector;

public class LevelSelectLayer extends MenuLayer {
	private static final String TAG = "LevelSelectLayer";

	public LevelSelectLayer() {
		super();
		CCSprite spriteBackground = getNode("game_choose.jpg", 0, 0, 0, 0);
		addChild(spriteBackground, 1);
		// 游戏名字
		CCSprite spriteName = getNode("image_select.png", winW * 9.3f / 10,
				winH * 7 / 8, 1, 1);
		addChild(spriteName, 2);

		/*********************************************************************************/
		int levelCount = Levels.count;
		CCSprite noramlSprite;
		CCSprite pressedSprite;
		CCSprite disableSprite;
		CCMenuItemSprite item;
		CCMenu menu;
		final LocalDataManager ldm = LocalDataManager.getInstance();
		String difficulty = (String) ldm.readSetting(
				LocalDataManager.DIFFICULTY_KEY, Constants.NORMAL);
		int hardPassed = (Integer) ldm.readSetting(
				LocalDataManager.HARD_PASSED, -1);
		int normalPassed = (Integer) ldm.readSetting(
				LocalDataManager.NORMAL_PASSED, -1);
		int easyPassed = (Integer) ldm.readSetting(
				LocalDataManager.EASY_PASSED, -1);
		int passedLevel = -1;
		if (Constants.HARD.equals(difficulty)) {
			passedLevel = hardPassed;
		} else if (Constants.NORMAL.equals(difficulty)) {
			passedLevel = Math.max(hardPassed, normalPassed);
		} else if (Constants.EASY.equals(difficulty)) {
			passedLevel = Math.max(easyPassed,
					Math.max(hardPassed, normalPassed));
		}

		for (int i = 0; i < 3; i++) {
			menu = CCMenu.menu();
			for (int j = 0; j < 3; j++) {
				int level = i * 3 + j;
				long score = 0;
				Logger.d(TAG, "LevelSelectLayer. level=" + level
						+ ", passedLevel=" + passedLevel);
				if (level <= passedLevel) {
					score = getLevelScore(level);
				}
				if (level < levelCount) {
					noramlSprite = LevelSelector.levelSprite(level + 1, score,
							true);
					pressedSprite = LevelSelector.levelSprite(level + 1, score,
							false);
					disableSprite = new LevelSelector("stage_locked.png");
					item = CCMenuItemSprite.item(noramlSprite, pressedSprite,
							disableSprite, this, "levelSelect");
					item.setTag(level);
					if (level <= passedLevel + 1) {
						item.setIsEnabled(true);
					} else {
						item.setIsEnabled(false);
					}
				} else {
					disableSprite = new LevelSelector("stage_locked.png");
					item = CCMenuItemSprite.item(disableSprite, disableSprite);
					item.setVisible(false);
				}
				item.setScale(Game.scale_ratio_y);
				item.setAnchorPoint(1, 0.5f);
				menu.addChild(item);
			}
			menu.alignItemsHorizontally();
			CCNode firstNode = menu.getChildren().get(0);
			float offsetX = firstNode.getPosition().x;
			float offsetY = firstNode.getPosition().y;
			menu.setPosition(((winW - Math.abs(offsetX)) * 9.5f) / 10f,
					((winH - Math.abs(offsetY)) * (3 - i)) / 5f);
			addChild(menu, 2);
		}
	}

	/**
	 * @param level
	 *            based 0
	 */
	private long getLevelScore(int level) {
		return (Long) LocalDataManager.getInstance().readSetting(
				String.valueOf(level), 0L);
	}

	public void levelSelect(Object o) {
		CCMenuItemSprite cmis = (CCMenuItemSprite) o;
		if (cmis.isEnabled()) {
			SoundManager.sharedSoundManager().playEffect(
					SoundManager.MUSIC_BUTTON);
			Game.current_level = cmis.getTag();
			SceneManager.sharedSceneManager()
					.replaceTo(SceneManager.SCENE_GAME);
		}
	}
}
