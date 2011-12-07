package com.ebrothers.forestrunner.util;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.nodes.CCNode;

public class CCFollowInH extends CCAction {
	/* node to follow */
	CCNode followedNode_;
	float nodeX2Screen_;

	public static CCFollowInH action(CCNode followedNode, float nodeX2Screen) {
		return new CCFollowInH(followedNode, nodeX2Screen);
	}

	protected CCFollowInH(CCNode followedNode, float nodeX2Screen) {
		super();
		followedNode_ = followedNode;
		nodeX2Screen_ = nodeX2Screen;
	}

	@Override
	public CCAction copy() {
		CCFollowInH f = new CCFollowInH(followedNode_, nodeX2Screen_);
		f.setTag(this.getTag());
		return f;
	}

	@Override
	public boolean isDone() {
		return (!followedNode_.isRunning());
	}

	@Override
	public void stop() {
		target = null;
		super.stop();
	}

	@Override
	public void step(float dt) {
		target.setPosition(-followedNode_.getPosition().x + nodeX2Screen_, 0);
	}

	@Override
	public void update(float time) {
	}

}
