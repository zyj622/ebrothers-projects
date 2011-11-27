package com.ebrothers.forestrunner.layers;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCPlace;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import com.ebrothers.forestrunner.scenes.HighScoreScene;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * 主菜单 HighScores界面 More 分享 界面控制
 * 
 * @author Administrator
 * 
 */

public class MainGameLayer extends BasicLayer {
	CCMenu cmMenuOpen;
	CCMenu cmMenuClose;

	CCSprite spriteCloud01;
	CCSprite spriteCloud02;
	CCSprite spriteCloud03;

	public MainGameLayer() {
		super();

		CCSprite sprite02 = getNode("game_start02.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);

		CCSprite sprite01 = getNode("game_start01.png", 0, 0, 0, 0);
		addChild(sprite01, 3);

		/*****************************************************************************/
		// 云
		spriteCloud01 = getNode("cloud01.png", 0, 0, 0, 0);
		addChild(spriteCloud01, 2);
		spriteCloud02 = getNode("cloud02.png", 0, 0, 0, 0);
		addChild(spriteCloud02, 2);
		spriteCloud03 = getNode("cloud03.png", 0, 0, 0, 0);
		addChild(spriteCloud03, 2);
		// 移动云
		actionMove();
		// 游戏名字
		CCSprite spriteName = getNode("word.png", width - 80, height - 50, 1, 1);
		addChild(spriteName, 4);
		// 游戏菜单
		CCSprite spritePlay = getNode("button_play01.png", 0, 0);
		CCSprite spritePlaySelect = getNode("button_play02.png", 0, 0);
		CCMenuItemSprite cmsStart = CCMenuItemSprite.item(spritePlay,
				spritePlaySelect, this, "startGame");
		cmsStart.setScaleX(SCALE_X);
		cmsStart.setScaleY(SCALE_Y);
		cmsStart.setAnchorPoint(1, 0);

		CCSprite spriteHigh = getNode("button_high01.png", 0, 0);
		CCSprite spriteHighSelect = getNode("button_high02.png", 0, 0);
		CCMenuItemSprite cmsHigh = CCMenuItemSprite.item(spriteHigh,
				spriteHighSelect, this, "startHigh");
		cmsHigh.setScaleX(SCALE_X);
		cmsHigh.setScaleY(SCALE_Y);
		cmsHigh.setAnchorPoint(1, 0);

		CCSprite spriteMore = getNode("button_more01.png", 0, 0);
		CCSprite spriteMoreSelect = getNode("button_more02.png", 0, 0);
		CCMenuItemSprite cmsMore = CCMenuItemSprite.item(spriteMore,
				spriteMoreSelect, this, "more");

		cmsMore.setScaleX(SCALE_X);
		cmsMore.setScaleY(SCALE_Y);
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
		cmsSoundClose.setScaleX(1.5f);
		cmsSoundClose.setScaleY(1.5f);
		cmsSoundClose.setAnchorPoint(1, 0);
		cmMenuClose = CCMenu.menu(cmsSoundClose);
		cmMenuClose.alignItemsVertically();

		cmMenuClose.setPosition(width - 20, 20);
		addChild(cmMenuClose, 6);

		CCSprite spriteSoundOpen = getNode("sound02.png", 0, 0);
		CCMenuItemSprite cmsSoundOpen = CCMenuItemSprite.item(spriteSoundOpen,
				spriteSoundOpen, this, "closeSound");
		cmsSoundOpen.setScaleX(1.5f);
		cmsSoundOpen.setScaleY(1.5f);
		cmsSoundOpen.setAnchorPoint(1, 0);
		cmMenuOpen = CCMenu.menu(cmsSoundOpen);
		cmMenuOpen.alignItemsVertically();

		cmMenuOpen.setPosition(width - 20, 20);
		cmMenuOpen.setVisible(false);
		addChild(cmMenuOpen, 6);

		CCSprite spriteShare = getNode("share_r.PNG", 0, 0);
		CCMenuItemSprite cmsShare = CCMenuItemSprite.item(spriteShare,
				spriteShare, this, "shareCallback");
		cmsShare.setScaleX(1.5f);
		cmsShare.setScaleY(1.5f);
		cmsShare.setAnchorPoint(1, 1);
		CCMenu cmMenuShare = CCMenu.menu(cmsShare);
		cmMenuShare.setPosition(width, height);

		addChild(cmMenuShare, 6);
	}

	public void shareCallback(Object o) {
		if (CCDirector.sharedDirector().getActivity() != null){
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Forest Runner");
			intent.putExtra(Intent.EXTRA_TEXT, "We play Forest Runner together");
			CCDirector.sharedDirector().getActivity().startActivity(Intent.createChooser(intent, CCDirector.sharedDirector().getActivity().getTitle()));
		}
	}

	public void more(Object o) {
		Intent it = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://www.baidu.com"));
		if (CCDirector.sharedDirector().getActivity() != null)
			CCDirector.sharedDirector().getActivity().startActivity(it);
	}

	public void closeSound(Object o) {
		cmMenuOpen.setVisible(false);
		cmMenuClose.setVisible(true);
	}

	public void openSound(Object o) {
		cmMenuOpen.setVisible(true);
		cmMenuClose.setVisible(false);
	}

	public void startHigh(Object o) {
		CCDirector.sharedDirector().pushScene(HighScoreScene.scene());
	}

	/**
	 * 
	 */
	public void actionMove() {
		CCMoveTo actionTo01 = CCMoveTo.action(12,
				CGPoint.ccp(0, height / 5 * 4));
		CCRepeatForever action1 = CCRepeatForever
				.action(CCSequence.actions(
						CCPlace.action(CGPoint.ccp(width, height / 5 * 4)),
						actionTo01));
		spriteCloud01.runAction(action1);
		CCMoveTo actionTo02 = CCMoveTo.action(25,
				CGPoint.ccp(0, height / 2 + 80));
		CCRepeatForever action2 = CCRepeatForever
				.action(CCSequence.actions(CCPlace.action(CGPoint.ccp(width / 2
						+ width, height / 2 + 80)), actionTo02));
		spriteCloud02.runAction(action2);
		CCMoveTo actionTo03 = CCMoveTo.action(15,
				CGPoint.ccp(0, height / 2 + 30));
		CCRepeatForever action3 = CCRepeatForever.action(CCSequence.actions(
				CCPlace.action(CGPoint.ccp(width - 30, height / 2 + 30)),
				actionTo03));
		spriteCloud03.runAction(action3);
	}

	@Override
	public void onEnter() {
		super.onEnter();
	}
	public void onExit() {
		super.onExit();
		Log.i("game", "exit_main_game_layer");
	}
	
}
