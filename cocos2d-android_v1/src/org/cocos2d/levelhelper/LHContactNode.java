package org.cocos2d.levelhelper;

import org.cocos2d.levelhelper.LevelHelperLoader.LevelHelper_TAG;
import org.cocos2d.nodes.CCNode;

import com.badlogic.gdx.physics.box2d.World;

public class LHContactNode extends CCNode {

	public static LHContactNode contactNodeWithWorld(World _box2dWorld) {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerPreColisionCallbackBetweenTagA(LevelHelper_TAG tagA,
			LevelHelper_TAG tagB, Object target, String selector) {
		// TODO Auto-generated method stub
		
	}

	public void cancelPreColisionCallbackBetweenTagA(int tagA, int tagB) {
		// TODO Auto-generated method stub
		
	}

	public void cancelPostColisionCallbackBetweenTagA(int ordinal, int ordinal2) {
		// TODO Auto-generated method stub
		
	}

}
