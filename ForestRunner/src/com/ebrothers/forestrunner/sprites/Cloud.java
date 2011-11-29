package com.ebrothers.forestrunner.sprites;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCPlace;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;

import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.common.Logger;

public class Cloud extends CCSprite {

	public Cloud(String frameName) {
		super(CCSpriteFrameCache.sharedSpriteFrameCache().getSpriteFrame(
				frameName));
		setAnchorPoint(0.5f, 0.5f);
		setScaleX(Globals.scale_ratio_x);
		setScaleY(Globals.scale_ratio_y);
	}

	/**
	 * 
	 * @param start_x
	 * @param end_x
	 * @param height
	 * @param speed
	 */
	public void actionMoveHorizontal(float start_x, float end_x, float height,
			int speed) {
		CCMoveTo action = CCMoveTo.action(speed, CGPoint.ccp(end_x, height));
		CCRepeatForever actionForever = CCRepeatForever.action(CCSequence
				.actions(CCPlace.action(CGPoint.ccp(start_x, height)), action));
		runAction(actionForever);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		Logger.e("game", "cloud---------enter");
	}

}
