package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;

import android.R.bool;

import com.ebrothers.forestrunner.common.Game;

public class Runner extends GameSprite {
	public static final float JUMP_DURING_LONG = .7f;
	public static final float JUMP_DURING_SHORT = .6f;
	public static final float FALL_DURING = .2f;
	public static float y_offset;
	private boolean acting;

	public Runner() {
		super("man01.png");
		setAnchorPoint(0, 1);
		y_offset = getBoundingHeight() - 10;
		setPosition(100, Game.groundM_y + y_offset);
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
		addAnimation("jump", frames, 0.1f);
		frames.clear();
		frames.add(cache.getSpriteFrame("man11.png"));
		addAnimation("fallToGround", frames);
		frames.clear();
		for (int i = 0; i < 3; i++) {
			frames.add(cache.getSpriteFrame(String.format("man6%d.png", i + 1)));
		}
		addAnimation("fallToGap", frames);
		frames.clear();
		frames.add(cache.getSpriteFrame("man45.png"));
		frames.add(cache.getSpriteFrame("man46.png"));
		addAnimation("knockDown", frames, 0.1f);
		frames.clear();
		frames.add(cache.getSpriteFrame("man51.png"));
		frames.add(cache.getSpriteFrame("man52.png"));
		frames.add(cache.getSpriteFrame("man53.png"));
		addAnimation("knockDown1", frames, 0.1f);
		frames.clear();
		for (int i = 0; i < 4; i++) {
			frames.add(cache.getSpriteFrame(String.format("man4%d.png", i + 1)));
		}
		addAnimation("float", frames, 0.1f);
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

	public void jump(float y) {
		if (!acting) {
			stopAllActions();
			playeDelayAnimation("jump", 0.2f, "fallToGround");
			CGPoint to = CGPoint.ccp(getPosition().x, y + y_offset);
			float jHeight = 150;
			float during = JUMP_DURING_LONG;
			if (y > (getPosition().y - y_offset)) {
				jHeight = 90;
				during = JUMP_DURING_SHORT;
			}
			runAction(CCSequence.actions(
					CCJumpTo.action(during, to, jHeight, 1),
					CCCallFunc.action(this, "actionDone")));
			acting = true;
		}
	}

	public void jumpToGap(Object target, String selector) {
		if (!acting) {
			stopAllActions();
			playeDelayAnimation("jump", 0.2f, "fallToGround");
			runAction(CCSequence.actions(
					CCJumpTo.action(JUMP_DURING_LONG, getPosition(), 150, 1),
					CCCallFunc.action(this, "actionDone"),
					CCCallFunc.action(target, selector)));
			acting = true;
		}
	}

	public void fallToGap(Object target, String selector) {
		if (!acting) {
			stopAllActions();
			playeAnimation("fallToGap");
			CGPoint to = CGPoint.ccp(getPosition().x, 0);
			runAction(CCSequence.actions(CCMoveTo.action(0.3f, to),
					CCCallFunc.action(target, selector)));
			acting = true;
		}
	}

	public void fallToGround(float y) {
		if (!acting) {
			stopAllActions();
			playeAnimation("fallToGround");
			CGPoint to = CGPoint.ccp(getPosition().x, y + y_offset);
			runAction(CCSequence.actions(
					CCJumpTo.action(FALL_DURING, to, 5, 1),
					CCCallFunc.action(this, "actionDone")));
			acting = true;
		}
	}

	public void knockDown() {
		if (!acting) {
			stopAllActions();
			playeAnimation("knockDown", this, "knockDownDone");
			acting = true;
		}
	}

	public void knockDownDone() {
		stopAllActions();
		runAction(CCSequence.actions(
				CCMoveBy.action(0.2f, CGPoint.ccp(0, -60)),
				CCCallFunc.action(this, "loseGame")));
	}

	public void loseGame() {
		Game.delegate.loseGame();
	}

	@Override
	public void onStartContact(GameSprite target) {
		if (target instanceof Fire || target instanceof Flower) {
			stopAllActions();
			setVisible(false);
		} else if (target instanceof Dinosaur) {
			stopAllActions();
			playeAnimation("float", this, "loseGame");
		} else if (target instanceof Box || target instanceof Trap) {
			stopAllActions();
			playeAnimation("knockDown1", this, "loseGame");
		}
	}

	public void restart(CGPoint restartPoint) {
		if (!getVisible()) {
			setVisible(true);
		}
		setPosition(100, restartPoint.y + y_offset);
		acting = false;
		playeLoopAnimation("run");
	}
}
