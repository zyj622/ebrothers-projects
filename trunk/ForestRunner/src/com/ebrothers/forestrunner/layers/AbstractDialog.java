package com.ebrothers.forestrunner.layers;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.ccColor4B;

public abstract class AbstractDialog extends CCColorLayer {
	private boolean isShown;
	private CCNode _parent;

	public AbstractDialog(CCNode parent) {
		super(ccColor4B.ccc4(0, 0, 0, 180), CCDirector.sharedDirector()
				.winSize().width, CCDirector.sharedDirector().winSize().height);
		_parent = parent;
	}

	/**
	 * Default z is 5;
	 */
	public void show() {
		show(5);
	}

	public void show(int z) {
		_parent.addChild(this, z);
		isShown = true;
	}

	public void dismiss() {
		removeSelf();
		isShown = false;
	}

	public boolean isShown() {
		return isShown;
	}
}
