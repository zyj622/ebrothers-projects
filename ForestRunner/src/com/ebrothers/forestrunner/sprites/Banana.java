package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.manager.SoundManager;

public class Banana extends GameSprite {
	public Banana() {
		super("star01.png");
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		frames.add(cache.getSpriteFrame("star01.png"));
		frames.add(cache.getSpriteFrame("star02.png"));
		addAnimation("shine", frames, 0.2f);
		frames.clear();
		for (int i = 0; i < 5; i++) {
			frames.add(cache.getSpriteFrame(String.format("flash0%d.png",
					(i + 1))));
		}
		addAnimation("flash", frames, 0.1f);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shine");
	}

	public static void addAsTopTriangle(CCNode parent, float cx, float cy) {
		Banana banana = new Banana();
		cy += banana.getBoundingHeight() / 2f;
		banana.setPosition(cx + 93 * Game.scale_ratio, cy + 5
				* Game.scale_ratio);
		parent.addChild(banana);
		banana = new Banana();
		banana.setPosition(cx + 14 * Game.scale_ratio, cy + 54
				* Game.scale_ratio);
		parent.addChild(banana);
		banana = new Banana();
		banana.setPosition(cx - 67 * Game.scale_ratio, cy + 6
				* Game.scale_ratio);
		parent.addChild(banana);
	}

	public static void addOn2Sides4(CCNode parent, float cx, float cy) {
		// left top
		Banana banana = new Banana();
		cy += banana.getBoundingHeight() / 2f;
		banana.setPosition(cx - 54 * Game.scale_ratio, cy + 34
				* Game.scale_ratio);
		parent.addChild(banana);
		// right top
		banana = new Banana();
		banana.setPosition(cx + 54 * Game.scale_ratio, cy + 34
				* Game.scale_ratio);
		parent.addChild(banana);
		// left bottomd
		banana = new Banana();
		banana.setPosition(cx - 67 * Game.scale_ratio, cy + 4
				* Game.scale_ratio);
		parent.addChild(banana);
		// right bottom
		banana = new Banana();
		banana.setPosition(cx + 67 * Game.scale_ratio, cy + 4
				* Game.scale_ratio);
		parent.addChild(banana);
	}

	public static void addOn2Sides2(CCNode parent, float cx, float cy) {
		// left
		Banana banana = new Banana();
		cy += banana.getBoundingHeight() / 2f;
		banana.setPosition(cx - 80 * Game.scale_ratio, cy);
		parent.addChild(banana);
		// right
		banana = new Banana();
		banana.setPosition(cx + 28 * Game.scale_ratio, cy);
		parent.addChild(banana);
	}

	@Override
	public boolean canCollision() {
		return true;
	}

	@Override
	public void onStartContact(GameSprite target) {
		SoundManager.sharedSoundManager()
				.playEffect(SoundManager.MUSIC_START_1);
		playeAnimation("flash", this, "flashDone");
		Game.score += 450;
		Game.delegate.updateScore();
	}

	public void flashDone() {
		// removeSelf();
		setVisible(false);
	}
}
