package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Game;

public class GameSprite extends CCSprite {
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
		super.setScale(Game.scale_ratio);
		// CCTexture2D texture = getTexture();
		// if (texture != null) {
		// texture.setAliasTexParameters();
		// }
	}

	protected void addAnimation(String animationName,
			ArrayList<CCSpriteFrame> frames) {
		CCAnimation animation = CCAnimation.animation(animationName,
				0.5f / frames.size(), frames);
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

	protected void playeAnimation(String animationName, Object target,
			String selector) {
		runAction(CCSequence.actions(
				CCAnimate.action(animationByName(animationName), false),
				CCCallFunc.action(target, selector)));
	}

	protected void playeAnimation(String animationName) {
		runAction(CCAnimate.action(animationByName(animationName), false));
	}

	protected void playeDelayAnimation(String animationName1, float dt,
			String animationName2) {
		runAction(CCSequence.actions(
				CCAnimate.action(animationByName(animationName1), false),
				CCDelayTime.action(dt),
				CCAnimate.action(animationByName(animationName2), false)));
	}

	public void onStartContact(GameSprite target) {
		// do nothing
	}

	public boolean canCollision() {
		return false;
	}

	public boolean isFatal() {
		return false;
	}

	public float getBoundingWidth() {
		return getTextureRect().size.width * Game.scale_ratio;
	}

	public float getBoundingHeight() {
		return getTextureRect().size.height * Game.scale_ratio;
	}

	public void onRestore() {
		// do nothing
	}

}
