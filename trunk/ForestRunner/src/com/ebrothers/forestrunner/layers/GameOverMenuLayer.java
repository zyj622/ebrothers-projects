package com.ebrothers.forestrunner.layers;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Levels;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.manager.SoundManager;

public class GameOverMenuLayer extends BasicLayer {

	public GameOverMenuLayer() {
		super();

		// 游戏菜单
		CCSprite spriteAgain = getNode("button_again01.png", 0, 0);
		CCSprite spriteAgainSelect = getNode("button_again02.png", 0, 0);
		CCMenuItemSprite cmsAgain = CCMenuItemSprite.item(spriteAgain,
				spriteAgainSelect, this, "againGame");
		cmsAgain.setScaleX(Game.scale_ratio_x);
		cmsAgain.setScaleY(Game.scale_ratio_y);
		cmsAgain.setAnchorPoint(1, 1);

		CCSprite spriteMore = getNode("button_more01.png", 0, 0);
		CCSprite spriteMoreSelect = getNode("button_more02.png", 0, 0);
		CCMenuItemSprite cmsMore = CCMenuItemSprite.item(spriteMore,
				spriteMoreSelect, this, "more");
		cmsMore.setScaleX(Game.scale_ratio_x);
		cmsMore.setScaleY(Game.scale_ratio_y);
		cmsMore.setAnchorPoint(1, 1);

		CCMenu cmMenu = null;
		if (Game.isWin && Game.current_level < Levels.count - 1) {
			CCSprite spriteHigh = getNode("button_next01.png", 0, 0);
			CCSprite spriteHighSelect = getNode("button_next02.png", 0, 0);
			CCMenuItemSprite cmsNext = CCMenuItemSprite.item(spriteHigh,
					spriteHighSelect, this, "nextStage");
			cmsNext.setScaleX(Game.scale_ratio_x);
			cmsNext.setScaleY(Game.scale_ratio_y);
			cmsNext.setAnchorPoint(1, 1);
			cmMenu = CCMenu.menu(cmsAgain, cmsNext, cmsMore);
			// 添加胜利标语
			CCSprite victory = getNode("victory.png", winW * 1.8f / 3,
					winH * 6.5f / 8, 1, 1);
			addChild(victory, 5);
		} else {
			cmMenu = CCMenu.menu(cmsAgain, cmsMore);
		}

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
		cmMenu.setPosition((winW * 9.3f / 10) - Math.abs(offsetX),
				(winH * 4f / 8) - Math.abs(offsetY));

		addChild(cmMenu, 5);

	}

	public void againGame(Object o) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		SceneManager.sharedSceneManager().replaceTo(SceneManager.SCENE_GAME);
	}

	public void nextStage(Object o) {
		SoundManager.sharedSoundManager().playEffect(SoundManager.MUSIC_BUTTON);
		Game.current_level++;
		SceneManager.sharedSceneManager().replaceTo(SceneManager.SCENE_GAME);
	}

	public void more(Object o) {
		moreGame();
	}

}
