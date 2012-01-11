package org.cocos2d.levelhelper.nodes;

import org.cocos2d.nodes.CCSpriteSheet;

public class LHBatch {
	private CCSpriteSheet spriteSheet;
	private String uniqueName;
	private int z;

	public void setSpriteSheet(CCSpriteSheet batch) {
		spriteSheet = batch;
	}

	public CCSpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public void setZ(int _z) {
		z = _z;
	}

	public int getZ() {
		return z;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String name) {
		uniqueName = name;
	}

	public void initWithUniqueName(String name) {
		uniqueName = name;
	}

	public static LHBatch batchWithUniqueName(String name) {
		LHBatch pobBatch = new LHBatch();
		pobBatch.initWithUniqueName(name);
		return pobBatch;
	}
}
