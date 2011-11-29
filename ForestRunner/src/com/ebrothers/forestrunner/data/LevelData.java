package com.ebrothers.forestrunner.data;

import java.util.ArrayList;

public class LevelData {
	private ArrayList<SpriteData> sprites;

	public LevelData() {
		sprites = new ArrayList<SpriteData>();
	}

	public void addSpriteData(SpriteData data) {
		sprites.add(data);
	}

	public ArrayList<SpriteData> getSpriteDatas() {
		return sprites;
	}

	public static class SpriteData {
		public SpriteData() {
			children = new ArrayList<SpriteData>();
		}

		public ArrayList<SpriteData> getChildren() {
			return children;
		}

		public void addChild(SpriteData child) {
			children.add(child);
		}

		public float rx;
		public float width;
		public int type;
		private ArrayList<SpriteData> children;
	}

}
