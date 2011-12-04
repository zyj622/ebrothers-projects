package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;

import android.content.Intent;
import android.util.Log;

import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.manager.SoundManager;

/**
 * 主界面 菜单
 * 
 * @author Administrator
 * 
 */

public class MainGameMenuLayer extends BasicLayer {
	CCMenu cmMenuOpen;
	CCMenu cmMenuClose;

	public MainGameMenuLayer() {
		super();

		// 游戏名字
		CCSprite spriteName = getNode("word.png", width - 80, height - 50, 1, 1);
		addChild(spriteName, 4);
		// 游戏菜单
		CCSprite spritePlay = getNode("button_play01.png", 0, 0);
		CCSprite spritePlaySelect = getNode("button_play02.png", 0, 0);
		CCMenuItemSprite cmsStart = CCMenuItemSprite.item(spritePlay,
				spritePlaySelect, this, "startGame");
		cmsStart.setScaleX(Globals.scale_ratio_x);
		cmsStart.setScaleY(Globals.scale_ratio_y);
		cmsStart.setAnchorPoint(1, 0);

		CCSprite spriteHigh = getNode("button_high01.png", 0, 0);
		CCSprite spriteHighSelect = getNode("button_high02.png", 0, 0);
		CCMenuItemSprite cmsHigh = CCMenuItemSprite.item(spriteHigh,
				spriteHighSelect, this, "startHigh");
		cmsHigh.setScaleX(Globals.scale_ratio_x);
		cmsHigh.setScaleY(Globals.scale_ratio_y);
		cmsHigh.setAnchorPoint(1, 0);

		CCSprite spriteMore = getNode("button_more01.png", 0, 0);
		CCSprite spriteMoreSelect = getNode("button_more02.png", 0, 0);
		CCMenuItemSprite cmsMore = CCMenuItemSprite.item(spriteMore,
				spriteMoreSelect, this, "more");

		cmsMore.setScaleX(Globals.scale_ratio_x);
		cmsMore.setScaleY(Globals.scale_ratio_y);
		cmsMore.setAnchorPoint(1, 0);

		CCMenu cmMenu = CCMenu.menu(cmsStart, cmsHigh, cmsMore);
		cmMenu.setAnchorPoint(1, 0);
		cmMenu.alignItemsVertically();
		cmMenu.setPosition(width - 130, 130);
		cmMenu.alignItemsVertically(10f);
		addChild(cmMenu, 5);
		/*****************************************************************************/

		CCSprite spriteSoundClose = getNode("sound01.png", 0, 0);
		CCMenuItemSprite cmsSoundClose = CCMenuItemSprite.item(
				spriteSoundClose, spriteSoundClose, this, "openSound");
		cmsSoundClose.setScale(Globals.scale_ratio_y);
		cmsSoundClose.setAnchorPoint(1, 0);
		cmMenuClose = CCMenu.menu(cmsSoundClose);
		cmMenuClose.alignItemsVertically();

		cmMenuClose.setPosition(width - 20, 20);
		addChild(cmMenuClose, 6);

		CCSprite spriteSoundOpen = getNode("sound02.png", 0, 0);
		CCMenuItemSprite cmsSoundOpen = CCMenuItemSprite.item(spriteSoundOpen,
				spriteSoundOpen, this, "closeSound");
		cmsSoundOpen.setScale(Globals.scale_ratio_y);
		cmsSoundOpen.setAnchorPoint(1, 0);
		cmMenuOpen = CCMenu.menu(cmsSoundOpen);
		cmMenuOpen.alignItemsVertically();

		cmMenuOpen.setPosition(width - 20, 20);
		
		addChild(cmMenuOpen, 6);
		
		//设置声音图标
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(LocalDataManager.SOUND, true);
		if(sound){
			cmMenuOpen.setVisible(true);
			cmMenuClose.setVisible(false);
		}else{
			cmMenuOpen.setVisible(false);
			cmMenuClose.setVisible(true);
		}
		

		CCSprite spriteShare = getNode("share_r.PNG", 0, 0);
		CCMenuItemSprite cmsShare = CCMenuItemSprite.item(spriteShare,
				spriteShare, this, "shareCallback");
		cmsShare.setScale(Globals.scale_ratio_y);
		cmsShare.setAnchorPoint(1, 1);
		CCMenu cmMenuShare = CCMenu.menu(cmsShare);
		cmMenuShare.setPosition(width, height);

		addChild(cmMenuShare, 6);
	}

	/**
	 * 分享
	 * @param o
	 */
	public void shareCallback(Object o) {
		SoundManager.getInstance().playEffect(CCDirector.sharedDirector().getActivity(), SoundManager.MUSIC_BUTTON);
		if (CCDirector.sharedDirector().getActivity() != null){
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Forest Runner");
			intent.putExtra(Intent.EXTRA_TEXT, "We play Forest Runner together");
			CCDirector.sharedDirector().getActivity().startActivity(Intent.createChooser(intent, CCDirector.sharedDirector().getActivity().getTitle()));
		}
	}

	/**
	 * 更多
	 * @param o
	 */
	public void more(Object o) {
		moreGame();
	}

	/**
	 * 关闭声音
	 * @param o
	 */
	public void closeSound(Object o) {
		cmMenuOpen.setVisible(false);
		cmMenuClose.setVisible(true);
		LocalDataManager.getInstance().writeSetting(LocalDataManager.SOUND, false);
		SoundManager.getInstance().playEffect(CCDirector.sharedDirector().getActivity(), SoundManager.MUSIC_BUTTON);
		LocalDataManager.getInstance().writeSetting(LocalDataManager.SOUND, false);
	}

	/**
	 * 打开声音
	 * @param o
	 */
	public void openSound(Object o) {
		cmMenuOpen.setVisible(true);
		cmMenuClose.setVisible(false);
		LocalDataManager.getInstance().writeSetting(LocalDataManager.SOUND, true);
	}
	
	/**
	 * 
	 * @param o
	 */
	public void startGame(Object o) {
		SoundManager.getInstance().playEffect(CCDirector.sharedDirector().getActivity(), SoundManager.MUSIC_BUTTON);
		SceneManager.getInstance().replaceTo(SceneManager.SCENE_STAGES);
	}

	/**
	 * 
	 * @param o
	 */
	public void startHigh(Object o) {
		SoundManager.getInstance().playEffect(CCDirector.sharedDirector().getActivity(), SoundManager.MUSIC_BUTTON);
		SceneManager.getInstance().replaceTo(SceneManager.SCENE_HIGHSCORE);
	}

	
	@Override
	public void onEnter() {
		super.onEnter();
		Log.i("game", "enter_main_game_layer");
	}
	public void onExit() {
		super.onExit();
		Log.i("game", "exit_main_game_layer");
	}
	
}
