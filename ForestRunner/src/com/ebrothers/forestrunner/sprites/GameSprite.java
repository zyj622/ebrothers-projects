package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.common.Logger;

public abstract class GameSprite extends CCSprite {
	private static final String TAG = "GameSprite";

	public GameSprite() {
		super();
		initSprite();
	}

	public GameSprite(String frameName) {
		super(CCSpriteFrameCache.sharedSpriteFrameCache().getSpriteFrame(
				frameName));
		initSprite();
	}

	private void initSprite() {
		super.setScale(Globals.scale_ratio);
	}

	protected void addAnimation(String animationName,
			ArrayList<CCSpriteFrame> frames) {
		CCAnimation animation = CCAnimation.animation(animationName, 0.1f,
				frames);
		addAnimation(animation);
	}

	protected void playeLoopAnimation(String animationName) {
		runAction(CCRepeatForever.action(CCAnimate
				.action(animationByName(animationName))));
	}

	public void onStartContact(GameSprite target) {
		Logger.d(TAG, "start contact. A=" + this + ", B=" + target);
		// do nothing
	}

	public boolean canCollision() {
		return false;
	}

	public boolean isFatal() {
		return false;
	}

	/**
	 * FIX SCALE FACTOR.
	 */
	@Deprecated
	@Override
	public void setScale(float s) {
		super.setScale(Globals.scale_ratio);
	}

	public float getBoundingWidth() {
		return getBoundingBox().size.width;
	}
}
