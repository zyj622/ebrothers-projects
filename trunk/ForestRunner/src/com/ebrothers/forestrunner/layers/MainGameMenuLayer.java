package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.content.Intent;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.manager.SoundManager;

/**
 * 主界面 菜单
 * 
 * @author Administrator
 * 
 */

public class MainGameMenuLayer extends MenuLayer {
	CCMenu cmMenuOpen;
	CCMenu cmMenuClose;
	private DifficultyDialog difficultyDialog;

	public MainGameMenuLayer() {
		super();
		// 游戏名字
		CCSprite spriteName = getNode("word.png", winW * 9 / 10, winH * 9 / 10,
				1, 1);
		addChild(spriteName);

		/*****************************************************************************/
		// 游戏菜单
		CCSprite spritePlay = getNode("button_play01.png", 0, 0);
		CCSprite spritePlaySelect = getNode("button_play02.png", 0, 0);
		CCMenuItemSprite cmsStart = CCMenuItemSprite.item(spritePlay,
				spritePlaySelect, this, "startGame");
		cmsStart.setScaleX(Game.scale_ratio_x);
		cmsStart.setScaleY(Game.scale_ratio_y);
		cmsStart.setAnchorPoint(1, 1);

		CCSprite spriteDifficulty1 = getNode("button_difficuty01.png", 0, 0);
		CCSprite spriteDifficulty2 = getNode("button_difficuty02.png", 0, 0);
		CCMenuItemSprite cmsDifficulty = CCMenuItemSprite.item(
				spriteDifficulty1, spriteDifficulty2, this, "setDifficulty");
		cmsDifficulty.setScaleX(Game.scale_ratio_x);
		cmsDifficulty.setScaleY(Game.scale_ratio_y);
		cmsDifficulty.setAnchorPoint(1, 1);

		CCSprite spriteHigh = getNode("button_high01.png", 0, 0);
		CCSprite spriteHighSelect = getNode("button_high02.png", 0, 0);
		CCMenuItemSprite cmsHigh = CCMenuItemSprite.item(spriteHigh,
				spriteHighSelect, this, "startHigh");
		cmsHigh.setScaleX(Game.scale_ratio_x);
		cmsHigh.setScaleY(Game.scale_ratio_y);
		cmsHigh.setAnchorPoint(1, 1);

		CCSprite spriteMore = getNode("button_more01.png", 0, 0);
		CCSprite spriteMoreSelect = getNode("button_more02.png", 0, 0);
		CCMenuItemSprite cmsMore = CCMenuItemSprite.item(spriteMore,
				spriteMoreSelect, this, "more");

		cmsMore.setScaleX(Game.scale_ratio_x);
		cmsMore.setScaleY(Game.scale_ratio_y);
		cmsMore.setAnchorPoint(1, 1);

		CCMenu cmMenu = CCMenu.menu(cmsStart, cmsDifficulty, cmsHigh, cmsMore);
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
		cmMenu.setPosition((winW * 8.5f / 10) - Math.abs(offsetX), (winH / 2)
				+ 25f - Math.abs(offsetY));
		addChild(cmMenu, 5);
		/*****************************************************************************/

		CCSprite spriteSoundClose = getNode("sound01.png", 0, 0);
		CCMenuItemSprite cmsSoundClose = CCMenuItemSprite.item(
				spriteSoundClose, spriteSoundClose, this, "openSound");
		cmsSoundClose.setScale(Game.scale_ratio_y);
		cmsSoundClose.setAnchorPoint(1, 0);
		cmMenuClose = CCMenu.menu(cmsSoundClose);
		cmMenuClose.alignItemsVertically();

		cmMenuClose.setPosition(winW - 15, 15);
		addChild(cmMenuClose);

		CCSprite spriteSoundOpen = getNode("sound02.png", 0, 0);
		CCMenuItemSprite cmsSoundOpen = CCMenuItemSprite.item(spriteSoundOpen,
				spriteSoundOpen, this, "closeSound");
		cmsSoundOpen.setScale(Game.scale_ratio_y);
		cmsSoundOpen.setAnchorPoint(1, 0);
		cmMenuOpen = CCMenu.menu(cmsSoundOpen);
		cmMenuOpen.alignItemsVertically();

		cmMenuOpen.setPosition(winW - 15, 15);

		addChild(cmMenuOpen);

		// 设置声音图标
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(
				LocalDataManager.SOUND, true);
		if (sound) {
			cmMenuOpen.setVisible(true);
			cmMenuClose.setVisible(false);
		} else {
			cmMenuOpen.setVisible(false);
			cmMenuClose.setVisible(true);
		}

		/*****************************************************************************/
		CCSprite spriteShare = getNode("share_r.PNG", 0, 0);
		CCMenuItemSprite cmsShare = CCMenuItemSprite.item(spriteShare,
				spriteShare, this, "shareCallback");
		cmsShare.setScale(Game.scale_ratio_y);
		cmsShare.setAnchorPoint(1, 1);
		CCMenu cmMenuShare = CCMenu.menu(cmsShare);
		cmMenuShare.setPosition(winW, winH);

		addChild(cmMenuShare);

		difficultyDialog = DifficultyDialog.dialog(this);
	}

	/**
	 * 分享
	 * 
	 * @param o
	 */
	public void shareCallback(Object o) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		if (CCDirector.sharedDirector().getActivity() != null) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Forest Runner");
			intent.putExtra(Intent.EXTRA_TEXT, "We play Forest Runner together");
			CCDirector
					.sharedDirector()
					.getActivity()
					.startActivity(
							Intent.createChooser(intent, CCDirector
									.sharedDirector().getActivity().getTitle()));
		}
	}

	/**
	 * 更多
	 * 
	 * @param o
	 */
	public void more(Object o) {
		moreGame();
	}

	/**
	 * 关闭声音
	 * 
	 * @param o
	 */
	public void closeSound(Object o) {
		cmMenuOpen.setVisible(false);
		cmMenuClose.setVisible(true);
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		LocalDataManager.getInstance().writeSetting(LocalDataManager.SOUND,
				false);
	}

	/**
	 * 打开声音
	 * 
	 * @param o
	 */
	public void openSound(Object o) {
		cmMenuOpen.setVisible(true);
		cmMenuClose.setVisible(false);
		LocalDataManager.getInstance().writeSetting(LocalDataManager.SOUND,
				true);
	}

	/**
	 * 
	 * @param o
	 */
	public void startGame(Object o) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		SceneManager.sharedSceneManager().replaceTo(SceneManager.SCENE_STAGES);
	}

	public void setDifficulty(Object o) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		if (difficultyDialog != null && !difficultyDialog.isShown()) {
			difficultyDialog.show();
		}
	}

	/**
	 * 
	 * @param o
	 */
	public void startHigh(Object o) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		SceneManager.sharedSceneManager().replaceTo(
				SceneManager.SCENE_HIGHSCORE);
	}

}
