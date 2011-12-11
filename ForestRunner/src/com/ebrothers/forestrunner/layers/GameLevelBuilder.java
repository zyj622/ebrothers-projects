package com.ebrothers.forestrunner.layers;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.LevelData;
import com.ebrothers.forestrunner.data.LevelData.SpriteData;
import com.ebrothers.forestrunner.sprites.Banana;
import com.ebrothers.forestrunner.sprites.Box;
import com.ebrothers.forestrunner.sprites.Bridge;
import com.ebrothers.forestrunner.sprites.Cherry;
import com.ebrothers.forestrunner.sprites.Dinosaur;
import com.ebrothers.forestrunner.sprites.Fire;
import com.ebrothers.forestrunner.sprites.Flower;
import com.ebrothers.forestrunner.sprites.GameSprite;
import com.ebrothers.forestrunner.sprites.GoSign;
import com.ebrothers.forestrunner.sprites.Ground;
import com.ebrothers.forestrunner.sprites.Life;
import com.ebrothers.forestrunner.sprites.SpriteType;
import com.ebrothers.forestrunner.sprites.Stone;
import com.ebrothers.forestrunner.sprites.StopSign;
import com.ebrothers.forestrunner.sprites.Trap;

public class GameLevelBuilder {
	private static final String TAG = "GameLevelBuilder";
	private ArrayList<CGPoint> breakPoints;
	private ArrayList<CGPoint> signs;

	public static GameLevelBuilder create() {
		GameLevelBuilder map = new GameLevelBuilder();
		return map;
	}

	public GameLevelBuilder() {
		breakPoints = new ArrayList<CGPoint>();
		signs = new ArrayList<CGPoint>();
	}

	public float build(CCNode parent, LevelData levelData) {
		Logger.d(TAG, "build level map...");
		assert (levelData != null);
		float levelWidth = 0;
		// get sprite's data
		ArrayList<SpriteData> datas = levelData.getSpriteDatas();
		float nextX = 0;
		float lastGroundTop = Game.groundM_y;
		int count = datas.size();
		SpriteData spriteData;
		int preType = -1;
		int nextType = -1;
		float spriteWidth = 0;
		for (int i = 0; i < count; i++) {
			spriteData = datas.get(i);
			if ((i + 1) < count) {
				nextType = datas.get(i + 1).type;
			} else {
				nextType = -1;
			}
			// create game sprite by type
			switch (spriteData.type) {
			case SpriteType.GROUND_L:
				lastGroundTop = Game.groundL_y;
				spriteWidth = spriteData.width;
				createGround(parent, preType, nextType, spriteData.type, nextX,
						lastGroundTop, spriteWidth);
				break;
			case SpriteType.GROUND_M:
				lastGroundTop = Game.groundM_y;
				spriteWidth = spriteData.width;
				createGround(parent, preType, nextType, spriteData.type, nextX,
						lastGroundTop, spriteWidth);
				break;
			case SpriteType.GROUND_H:
				lastGroundTop = Game.groundH_y;
				spriteWidth = spriteData.width;
				createGround(parent, preType, nextType, spriteData.type, nextX,
						lastGroundTop, spriteWidth);
				break;
			case SpriteType.BRIDGE:
				Bridge bridge = new Bridge();
				bridge.setPosition(nextX - (14 * Game.scale_ratio),
						lastGroundTop - (20 * Game.scale_ratio));
				spriteWidth = bridge.getBoundingWidth()
						- (30 * Game.scale_ratio);
				Banana.addAsTopTriangle(parent, bridge.getPosition().x
						+ spriteWidth / 2f, lastGroundTop);
				parent.addChild(bridge, 1);
				break;
			case SpriteType.GAP:
				spriteWidth = spriteData.width;
				break;
			case SpriteType.STONE:
				Stone stone = new Stone();
				stone.setPosition(nextX, lastGroundTop);
				parent.addChild(stone);
				spriteWidth = stone.getBoundingWidth();
				Cherry.addOnTop(parent, nextX + spriteWidth / 2f,
						lastGroundTop, false);
				break;
			default:
				break;
			}
			createChildren(parent, spriteData.getChildren(), nextX,
					lastGroundTop);
			nextX += spriteWidth;
			levelWidth += spriteWidth;
			CGPoint point;
			if (SpriteType.GAP == spriteData.type) {
				point = CGPoint.ccp(nextX, 0);
			} else {
				point = CGPoint.ccp(nextX, lastGroundTop);
			}
			breakPoints.add(point);
			preType = spriteData.type;
		}
		return levelWidth;
	}

	private void createChildren(CCNode parent, ArrayList<SpriteData> children,
			float parentLeft, float parentTop) {
		int count = children.size();
		SpriteData child;
		GameSprite sprite = null;
		for (int i = 0; i < count; i++) {
			child = children.get(i);
			switch (child.type) {
			case SpriteType.BOX:
				sprite = new Box();
				sprite.setPosition(parentLeft + child.rx, parentTop - 4
						* Game.scale_ratio);
				parent.addChild(sprite);
				Cherry.addOnTop(parent, parentLeft + child.rx, parentTop, true);
				Banana.addOn2Sides4(parent, parentLeft + child.rx, parentTop);
				break;
			case SpriteType.DINORSAUR_1:
				sprite = Dinosaur.dinosaur(Dinosaur.DINOSAUR_1);
				sprite.setPosition(parentLeft + child.rx, parentTop - 15);
				parent.addChild(sprite);
				break;
			case SpriteType.DINORSAUR_2:
				sprite = Dinosaur.dinosaur(Dinosaur.DINOSAUR_2);
				sprite.setPosition(parentLeft + child.rx, parentTop - 15);
				parent.addChild(sprite);
				break;
			case SpriteType.DINORSAUR_3:
				sprite = Dinosaur.dinosaur(Dinosaur.DINOSAUR_3);
				sprite.setPosition(parentLeft + child.rx, parentTop - 15);
				parent.addChild(sprite);
				break;
			case SpriteType.FIRE:
				sprite = new Fire();
				sprite.setPosition(parentLeft + child.rx, parentTop - 8);
				parent.addChild(sprite, 1);
				Cherry.addOnTop(parent, parentLeft + child.rx, parentTop, true);
				Banana.addOn2Sides4(parent, parentLeft + child.rx, parentTop);
				break;
			case SpriteType.FLOWER:
				sprite = new Flower();
				sprite.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(sprite, 1);
				Cherry.addOnTop(parent, parentLeft + child.rx, parentTop, true);
				Banana.addOn2Sides4(parent, parentLeft + child.rx, parentTop);
				break;
			case SpriteType.GO_SIGN:
				sprite = new GoSign();
				sprite.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(sprite);
				Banana.addOn2Sides2(parent, parentLeft + child.rx, parentTop);
				signs.add(CGPoint.ccp(parentLeft + child.rx, parentTop));
				break;
			case SpriteType.STOP_SIGN:
				sprite = new StopSign();
				sprite.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(sprite);
				Banana.addOn2Sides2(parent, parentLeft + child.rx, parentTop);
				signs.add(CGPoint.ccp(parentLeft + child.rx, parentTop));
				break;
			case SpriteType.LIFE:
				sprite = new Life();
				sprite.setPosition(parentLeft + child.rx, parentTop + child.ry);
				parent.addChild(sprite);
				break;
			case SpriteType.TRAP:
				sprite = new Trap();
				sprite.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(sprite);
				Cherry.addOnTop(parent, parentLeft + child.rx, parentTop, true);
				Banana.addOn2Sides4(parent, parentLeft + child.rx, parentTop);
				break;
			}
		}
	}

	private void createGround(CCNode parent, int preType, int nextType,
			int type, float left, float top, float width) {
		// if previous ground is tall than current, padding current ground.
		float paddingLeft = 0;
		float paddingRight = 0;
		if (isGroundType(preType) && preType > type) {
			paddingLeft = 180;
			left -= paddingLeft;
			width += paddingLeft;
		}
		if (isGroundType(nextType) && nextType > type) {
			paddingRight = 80;
			width += paddingRight;
		}
		createGround(parent, left, top, width);

		if (width > 550f) {
			GameSprite sprite = GameSprite.sprite("ground37.png");
			sprite.setAnchorPoint(0, 1);
			sprite.setPosition(350f + left, top - 55f * Game.scale_ratio);
			parent.addChild(sprite, 1);
		} else if (width > 400f) {
			GameSprite sprite = GameSprite.sprite("ground38.png");
			sprite.setAnchorPoint(0, 1);
			sprite.setPosition(130f + left, top - 55f * Game.scale_ratio);
			parent.addChild(sprite, 1);
		}
	}

	private boolean isGroundType(int type) {
		return (SpriteType.GROUND_H == type) || (SpriteType.GROUND_L == type)
				|| (SpriteType.GROUND_M == type);
	}

	private void createGround(CCNode parent, float left, float top, float width) {
		// add left ground
		Ground leftGround = Ground.ground(Ground.GROUND_L);
		leftGround.setPosition(left, top);
		parent.addChild(leftGround, 1);
		float leftWidth = leftGround.getBoundingWidth();
		float currentX = left + leftWidth;
		Ground rightGround = Ground.ground(Ground.GROUND_R);
		float rightWidth = rightGround.getBoundingWidth();
		float maxX = currentX + width - leftWidth - rightWidth;
		// add middle grounds
		boolean flag = true;
		while (currentX < maxX) {
			Ground middle = null;
			if (flag) {
				middle = Ground.ground(Ground.GROUND_M1);
				flag = false;
			} else {
				middle = Ground.ground(Ground.GROUND_M2);
				flag = true;
			}
			if (maxX - currentX < 50) {
				middle.setPosition(currentX - 50, top);
			} else {
				middle.setPosition(currentX, top);
			}
			parent.addChild(middle, 1);
			currentX += middle.getBoundingWidth() - 1;
		}
		// add right ground
		rightGround.setPosition(maxX, top);
		parent.addChild(rightGround, 1);
	}

	public ArrayList<CGPoint> getBreakPoints() {
		return breakPoints;
	}

	public ArrayList<CGPoint> getSignPoints() {
		return signs;
	}
}