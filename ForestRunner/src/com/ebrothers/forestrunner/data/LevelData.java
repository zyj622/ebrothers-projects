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
		public float rx;
		public float width;
		public int type;
	}

}
