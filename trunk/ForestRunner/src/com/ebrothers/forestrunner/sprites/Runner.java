package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Globals;

public class Runner extends GameSprite {

	private boolean jumping;

	public Runner() {
		super("man01.png");
		setAnchorPoint(0, 1);
		setPosition(100, Globals.groundM_y + getBoundingHeight() - 10);
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

	public void jump() {
		if (!jumping) {
			stopAllActions();
			playeAnimation("jump");
			runAction(CCSequence.actions(
					CCJumpTo.action(.8f, getPosition(), 150, 1),
					CCCallFunc.action(this, "jumpDone")));
			jumping = true;
		}
	}

	public void jumpDone() {
		run();
		jumping = false;
	}

	public void run() {
		stopAllActions();
		playeLoopAnimation("run");
	}
}
