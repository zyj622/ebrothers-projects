package org.cocos2d.layers;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;

public class CCScreenAdapter {
	// default portrait screen size
	private CGSize baseSize_p = CGSize.make(320f, 480f);
	private CGSize baseSize_l = CGSize.make(480f, 320f);

	private static CCScreenAdapter sInstance;

	public static CCScreenAdapter sharedSceneAdapter() {
		if (sInstance == null) {
			sInstance = new CCScreenAdapter();
		}
		return sInstance;
	}

	private float _scale_ratio_h = 1f;
	private float _scale_ratio_v = 1f;

	public void init() {
		final CGSize winSize = CCDirector.sharedDirector().winSize();
		if (CCDirector.sharedDirector().getLandscape()) {
			_scale_ratio_h = winSize.width / baseSize_l.width;
			_scale_ratio_v = winSize.height / baseSize_l.height;
		} else {
			_scale_ratio_h = winSize.width / baseSize_p.width;
			_scale_ratio_v = winSize.height / baseSize_p.height;
		}
	}

	public void adapte(CCNode node) {
		node.setScaleX(_scale_ratio_h);
		node.setScaleY(_scale_ratio_v);
	}

	public void adapteByHorizontal(CCNode node) {
		node.setScale(_scale_ratio_h);
	}

	public void adapteByVertical(CCNode node) {
		node.setScale(_scale_ratio_v);
	}

	public float getAdaptedX(float x) {
		return x * _scale_ratio_h;
	}

	public float getAdaptedY(float y) {
		return y * _scale_ratio_v;
	}

	public CGSize getBasedSize() {
		if (CCDirector.sharedDirector().getLandscape()) {
			return baseSize_l;
		} else {
			return baseSize_p;
		}
	}
}