package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import com.ebrothers.forestrunner.common.Game;

public class Fire extends GameSprite {

	private GameSprite burnSprite;

	public Fire() {
		super("fire01.png");
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			frames.add(cache.getSpriteFrame(String.format("fire0%d.png", i + 1)));
		}
		addAnimation("shining", frames);

		burnSprite = GameSprite.sprite("man21.png");
		burnSprite.setScale(1);
		burnSprite.setAnchorPoint(0.5f, 1);
		burnSprite.setPosition(burnSprite.getBoundingWidth() / 2f - 10f,
				getBoundingHeight());
		frames.clear();
		for (int i = 0; i < 8; i++) {
			frames.add(cache.getSpriteFrame(String.format("man2%d.png", i + 1)));
		}
		burnSprite.addAnimation("burn", frames, 0.1f);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		playeLoopAnimation("shining");
	}

	@Override
	public boolean canCollision() {
		return true;
	}

	@Override
	public boolean isFatal() {
		return true;
	}

	@Override
	public void onStartContact(GameSprite target) {
		addChild(burnSprite, 1);
		burnSprite.playeAnimation("burn", this, "burnDone");
	}

	public void burnDone() {
		removeChild(burnSprite, true);
		Game.delegate.loseGame();
	}
}