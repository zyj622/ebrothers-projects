package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;

import com.ebrothers.forestrunner.common.Globals;

public class Runner extends GameSprite {
	public static final float JUMP_DURING = .8f;
	public static final float FALL_DURING = .2f;
	public static float y_offset;
	private boolean acting;

	public Runner() {
		super("man01.png");
		setAnchorPoint(0, 1);
		y_offset = getBoundingHeight() - 10;
		setPosition(100, Globals.groundM_y + y_offset);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 8; i++) {
			frames.add(cache.getSpriteFrame(String.format("man0%d.png", i + 1)));
		}
		addAnimation("run", frames);
		frames.clear();
		for (int i = 0; i < 2; i++) {
			frames.add(cache.getSpriteFrame(String.format("man1%d.png", i + 1)));
		}
		addAnimation("jump", frames);
		frames.clear();
		frames.add(cache.getSpriteFrame("man11.png"));
		addAnimation("fall", frames);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("run");
	}

	@Override
	public boolean canCollision() {
		return true;
	}

	public void jump(float y) {
		if (!acting) {
			stopAllActions();
			playeDelayAnimation("jump", FALL_DURING, "fall");
			CGPoint to = CGPoint.ccp(getPosition().x, y + y_offset);
			float jHeight = 150;
			if (y > (getPosition().y - y_offset)) {
				jHeight = 100;
			}
			runAction(CCSequence.actions(
					CCJumpTo.action(JUMP_DURING, to, jHeight, 1),
					CCCallFunc.action(this, "actionDone")));
			acting = true;
		}
	}

	public boolean isInAction() {
		return acting;
	}

	public void actionDone() {
		run();
		acting = false;
	}

	public void run() {
		stopAllActions();
		playeLoopAnimation("run");
	}

	public void fallToGap() {

	}

	public void fallToGround(float y) {
		if (!acting) {
			stopAllActions();
			playeAnimation("fall");
			CGPoint to = CGPoint.ccp(getPosition().x, y + y_offset);
			runAction(CCSequence.actions(
					CCJumpTo.action(FALL_DURING, to, 10, 1),
					CCCallFunc.action(this, "actionDone")));
			acting = true;
		}
	}
}
