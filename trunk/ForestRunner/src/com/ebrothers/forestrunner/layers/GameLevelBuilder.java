package com.ebrothers.forestrunner.layers;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;

import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.data.LevelData;
import com.ebrothers.forestrunner.data.LevelData.SpriteData;
import com.ebrothers.forestrunner.sprites.Box;
import com.ebrothers.forestrunner.sprites.Bridge;
import com.ebrothers.forestrunner.sprites.Dinosaur1;
import com.ebrothers.forestrunner.sprites.Dinosaur2;
import com.ebrothers.forestrunner.sprites.Dinosaur3;
import com.ebrothers.forestrunner.sprites.Fire;
import com.ebrothers.forestrunner.sprites.Flower;
import com.ebrothers.forestrunner.sprites.GameSprite;
import com.ebrothers.forestrunner.sprites.GoSign;
import com.ebrothers.forestrunner.sprites.GroundL;
import com.ebrothers.forestrunner.sprites.GroundM1;
import com.ebrothers.forestrunner.sprites.GroundM2;
import com.ebrothers.forestrunner.sprites.GroundR;
import com.ebrothers.forestrunner.sprites.SpriteType;
import com.ebrothers.forestrunner.sprites.Stone;
import com.ebrothers.forestrunner.sprites.StopSign;
import com.ebrothers.forestrunner.sprites.Trap;

public class GameLevelBuilder {
	private static final String TAG = "GameLevelBuilder";
	private float levelWidth = 0;

	public static GameLevelBuilder create() {
		GameLevelBuilder map = new GameLevelBuilder();
		return map;
	}

	public void build(CCNode parent, LevelData levelData) {
		Logger.d(TAG, "build level map...");
		assert (levelData != null);
		// get sprite's data
		ArrayList<SpriteData> datas = levelData.getSpriteDatas();
		float nextX = 0;
		float lastGroundTop = Globals.groundM_y;
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
				lastGroundTop = Globals.groundL_y;
				spriteWidth = spriteData.width;
				createGround(parent, preType, nextType, spriteData.type, nextX,
						lastGroundTop, spriteWidth);
				break;
			case SpriteType.GROUND_M:
				lastGroundTop = Globals.groundM_y;
				spriteWidth = spriteData.width;
				createGround(parent, preType, nextType, spriteData.type, nextX,
						lastGroundTop, spriteWidth);
				break;
			case SpriteType.GROUND_H:
				lastGroundTop = Globals.groundH_y;
				spriteWidth = spriteData.width;
				createGround(parent, preType, nextType, spriteData.type, nextX,
						lastGroundTop, spriteWidth);
				break;
			case SpriteType.BRIDGE:
				Bridge bridge = new Bridge();
				bridge.setPosition(nextX - (14 * Globals.scale_ratio),
						lastGroundTop - (20 * Globals.scale_ratio));
				parent.addChild(bridge);
				spriteWidth = bridge.getBoundingWidth()
						- (30 * Globals.scale_ratio);
				break;
			case SpriteType.GAP:
				spriteWidth = spriteData.width;
				break;
			case SpriteType.STONE:
				Stone stone = new Stone();
				stone.setPosition(nextX, lastGroundTop);
				parent.addChild(stone);
				spriteWidth = stone.getBoundingWidth();
				break;
			default:
				break;
			}
			createChildren(parent, spriteData.getChildren(), nextX,
					lastGroundTop);
			nextX += spriteWidth;
			levelWidth += spriteWidth;
			preType = spriteData.type;
		}
	}

	private void createChildren(CCNode parent, ArrayList<SpriteData> children,
			float parentLeft, float parentTop) {
		int count = children.size();
		SpriteData child;
		for (int i = 0; i < count; i++) {
			child = children.get(i);
			switch (child.type) {
			case SpriteType.BOX:
				Box box = new Box();
				box.setPosition(parentLeft + child.rx, parentTop - 4
						* Globals.scale_ratio);
				parent.addChild(box);
				break;
			case SpriteType.DINORSAUR_1:
				Dinosaur1 dinosaur = new Dinosaur1();
				dinosaur.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(dinosaur);
				break;
			case SpriteType.DINORSAUR_2:
				Dinosaur2 dinosaur2 = new Dinosaur2();
				dinosaur2.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(dinosaur2);
				break;
			case SpriteType.DINORSAUR_3:
				Dinosaur3 dinosaur3 = new Dinosaur3();
				dinosaur3.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(dinosaur3);
				break;
			case SpriteType.FIRE:
				Fire fire = new Fire();
				fire.setPosition(parentLeft + child.rx, parentTop - 8);
				parent.addChild(fire);
				break;
			case SpriteType.FLOWER:
				Flower flower = new Flower();
				flower.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(flower);
				break;
			case SpriteType.GO_SIGN:
				GoSign gosign = new GoSign();
				gosign.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(gosign);
				break;
			case SpriteType.STOP_SIGN:
				StopSign stopsign = new StopSign();
				stopsign.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(stopsign);
				break;
			case SpriteType.TRAP:
				Trap trap = new Trap();
				trap.setPosition(parentLeft + child.rx, parentTop - 4);
				parent.addChild(trap);
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
			sprite.setPosition(350f + left, top - 55f * Globals.scale_ratio);
			parent.addChild(sprite);
		} else if (width > 400f) {
			GameSprite sprite = GameSprite.sprite("ground38.png");
			sprite.setAnchorPoint(0, 1);
			sprite.setPosition(130f + left, top - 55f * Globals.scale_ratio);
			parent.addChild(sprite);
		}
	}

	private boolean isGroundType(int type) {
		return (SpriteType.GROUND_H == type) || (SpriteType.GROUND_L == type)
				|| (SpriteType.GROUND_M == type);
	}

	private void createGround(CCNode parent, float left, float top, float width) {
		// add left ground
		GroundL leftGround = new GroundL();
		leftGround.setPosition(left, top);
		parent.addChild(leftGround);
		float leftWidth = leftGround.getBoundingWidth() - 1;
		float currentX = left + leftWidth;
		GroundR rightGround = new GroundR();
		float rightWidth = rightGround.getBoundingWidth() - 1;
		float maxX = currentX + width - leftWidth - rightWidth;
		// add middle grounds
		boolean flag = false;
		while (currentX < maxX) {
			GameSprite middle = null;
			if (flag) {
				middle = new GroundM1();
				flag = false;
			} else {
				middle = new GroundM2();
				flag = true;
			}
			if (maxX - currentX < 50) {
				middle.setPosition(currentX - 50, top);
			} else {
				middle.setPosition(currentX, top);
			}
			parent.addChild(middle);
			currentX += middle.getBoundingWidth() - 1;
		}
		// add right ground
		rightGround.setPosition(maxX, top);
		parent.addChild(rightGround);
	}

	public float getLevelWidth() {
		return levelWidth;
	}
}