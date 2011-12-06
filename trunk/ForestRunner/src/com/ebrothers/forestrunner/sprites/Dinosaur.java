package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

public class Dinosaur extends GameSprite {

	public static final int DINOSAUR_1 = 1;
	public static final int DINOSAUR_2 = 2;
	public static final int DINOSAUR_3 = 3;

	public static Dinosaur dinosaur(int type) {
		return new Dinosaur(type);
	}

	private int _type;

	private Dinosaur(int type) {
		super("dinosaur" + type + "1.png");
		_type = type;
		setAnchorPoint(0.5f, 0);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 6; i++) {
			frames.add(cache.getSpriteFrame(String.format("dinosaur%d%d.png",
					type, i + 1)));
		}
		addAnimation("run", frames);
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

	@Override
	public boolean isFatal() {
		return true;
	}

	public int getType() {
		return _type;
	}

}