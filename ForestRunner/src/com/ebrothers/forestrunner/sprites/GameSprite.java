package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Globals;

public abstract class GameSprite extends CCSprite {
	private float width;

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
		setScale(Globals.scale_ratio);
		setWidth(getTextureRect().size.width * Globals.scale_ratio);
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

	public abstract boolean canCollision();

	public abstract boolean isFatal();

	public boolean isCollideWith(GameSprite sprinte) {
		return false;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getWidth() {
		return width;
	}
}
