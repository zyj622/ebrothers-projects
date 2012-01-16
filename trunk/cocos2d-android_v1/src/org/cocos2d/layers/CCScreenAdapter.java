package org.cocos2d.layers;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;

public class CCScreenAdapter {
	// default portrait screen size
	private static final float DEFAULT_WIDTH = 480f;
	private static final float DEFAULT_HEIGHT = 320f;

	private static CCScreenAdapter sInstance;

	public static CCScreenAdapter sharedSceneAdapter() {
		if (sInstance == null) {
			sInstance = new CCScreenAdapter();
		}
		return sInstance;
	}

	public float getHorizontalScaleRatio() {
		final CGSize winSize = CCDirector.sharedDirector().winSize();
		if (CCDirector.sharedDirector().getLandscape()) {
			return winSize.width / DEFAULT_WIDTH;
		} else {
			return winSize.width / DEFAULT_HEIGHT;
		}
	}

	public float getVerticalScaleRatio() {
		final CGSize winSize = CCDirector.sharedDirector().winSize();
		if (CCDirector.sharedDirector().getLandscape()) {
			return winSize.height / DEFAULT_HEIGHT;
		} else {
			return winSize.height / DEFAULT_WIDTH;
		}
	}

	public void adapte(CCNode node) {
		node.setScaleX(getHorizontalScaleRatio());
		node.setScaleY(getVerticalScaleRatio());
	}

	public void adapteByHorizontal(CCNode node) {
		node.setScale(getHorizontalScaleRatio());
	}

	public void adapteByVertical(CCNode node) {
		node.setScale(getVerticalScaleRatio());
	}

	public float getAdaptedX(float x) {
		return x * getHorizontalScaleRatio();
	}

	public float getAdaptedY(float y) {
		return y * getVerticalScaleRatio();
	}

	public float getBasedWidth() {
		if (CCDirector.sharedDirector().getLandscape()) {
			return DEFAULT_WIDTH;
		} else {
			return DEFAULT_HEIGHT;
		}
	}

	public float getBasedHeight() {
		if (CCDirector.sharedDirector().getLandscape()) {
			return DEFAULT_HEIGHT;
		} else {
			return DEFAULT_WIDTH;
		}
	}
}