package com.ebrothers.forestrunner.sprites;

import java.util.ArrayList;

import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;

public class Dinosaur extends GameSprite {

	private static final int MOVE_DISTANCE = 300;
	public static final int DINOSAUR_1 = 1;
	public static final int DINOSAUR_2 = 2;
	public static final int DINOSAUR_3 = 3;

	private boolean rushed;

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
		addAnimation("rush", frames);
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
	public void restore() {
		stopAllActions();
		setDisplayFrame("rush", 0);
		setPosition(getPosition().x + MOVE_DISTANCE, getPosition().y);
		rushed = false;
	}

	public int getType() {
		return _type;
	}

	public boolean isRushed() {
		return rushed;
	}

	public void rush() {
		rushed = true;
		playeLoopAnimation("rush");
		runAction(CCMoveBy.action(2, CGPoint.ccp(-MOVE_DISTANCE, 0)));
	}

}
