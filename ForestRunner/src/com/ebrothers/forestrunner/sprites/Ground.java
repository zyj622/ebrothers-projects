package com.ebrothers.forestrunner.sprites;

import org.cocos2d.nodes.CCNode;

import com.ebrothers.forestrunner.common.Globals;

public class Ground extends GameSprite {

	private Ground() {
		super();
		setAnchorPoint(0, 1);
	}

	public static CCNode createGround(int type, float left, float width) {
		float top = getTopByType(type);
		CCNode ground = new Ground();
		ground.setPosition(left, top);
		// add left ground
		GroundL leftGround = new GroundL();
		leftGround.setPosition(left, top);
		ground.addChild(leftGround);
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
			middle.setPosition(currentX, top);
			ground.addChild(middle);
			currentX += middle.getBoundingWidth() - 1;
		}
		// add right ground
		rightGround.setPosition(maxX, top);
		ground.addChild(rightGround);
		return ground;
	}

	private static float getTopByType(int type) {
		switch (type) {
		case SpriteType.GROUND_M:
			return Globals.groundM_y;
		case SpriteType.GROUND_L:
			return Globals.groundL_y;
		case SpriteType.GROUND_H:
			return Globals.groundH_y;
		}
		return Globals.groundM_y;
	}
}
