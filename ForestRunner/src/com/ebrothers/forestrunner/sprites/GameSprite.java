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

public class GameSprite extends CCSprite {
	private static final String TAG = "GameSprite";

	public static GameSprite sprite() {
		return new GameSprite();
	}

	public static GameSprite sprite(String frameName) {
		return new GameSprite(frameName);
	}

	protected GameSprite() {
		super();
		initSprite();
	}

	protected GameSprite(String frameName) {
		super(CCSpriteFrameCache.sharedSpriteFrameCache().getSpriteFrame(
				frameName));
		initSprite();
	}

	private void initSprite() {
		super.setScale(Globals.scale_ratio);
		// CCTexture2D texture = getTexture();
		// if (texture != null) {
		// texture.setAliasTexParameters();
		// }
	}

	protected void addAnimation(String animationName,
			ArrayList<CCSpriteFrame> frames) {
		CCAnimation animation = CCAnimation.animation(animationName,
				0.4f / frames.size(), frames);
		addAnimation(animation);
	}

	protected void addAnimation(String animationName,
			ArrayList<CCSpriteFrame> frames, float dt) {
		CCAnimation animation = CCAnimation
				.animation(animationName, dt, frames);
		addAnimation(animation);
	}

	protected void playeLoopAnimation(String animationName) {
		runAction(CCRepeatForever.action(CCAnimate
				.action(animationByName(animationName))));
	}

	protected void playeAnimation(String animationName) {
		runAction(CCAnimate.action(animationByName(animationName), false));
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

	public float getBoundingWidth() {
		return getBoundingBox().size.width;
	}

	public float getBoundingHeight() {
		return getBoundingBox().size.height;
	}
}
