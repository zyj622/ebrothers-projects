package com.ebrothers.linerunner.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.layers.CCTMXMapInfo;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.types.ccColor4F;

public class BlocksDataParser {

	private LevelData levelData;

	public BlocksDataParser() {
		levelData = new LevelData();
	}

	public void loadLevelData(int level) {
		CCTMXMapInfo info = CCTMXMapInfo.formatWithTMXFile("levels/level"
				+ level + ".tmx");
		ArrayList<CCTMXObjectGroup> groups = info.objectGroups;
		if (groups.isEmpty()) {
			throw new RuntimeException("load scene data failed! level=" + level);
		}
		CCTMXObjectGroup group = groups.get(0);
		for (HashMap<String, String> object : group.objects) {
			final String name = object.get("name");
			final String x = object.get("x");
			final String y = object.get("y");
			final String width = object.get("width");
			final String height = object.get("height");
			final String color = object.get("color");
			final String image = object.get("image");
			final BuildData data = new BuildData();
			data.x = Float.parseFloat(x);
			data.y = Float.parseFloat(y);
			data.width = width == null ? 0 : Float.parseFloat(width);
			data.height = height == null ? 0 : Float.parseFloat(height);
			if (color != null) {
				String[] rgb = color.split(",");
				if (rgb.length == 3) {
					float r = Float.parseFloat(rgb[0]);
					float g = Float.parseFloat(rgb[1]);
					float b = Float.parseFloat(rgb[2]);
					data.color = new ccColor4F(r, g, b, 1);
				} else if (rgb.length == 4) {
					float r = Float.parseFloat(rgb[0]);
					float g = Float.parseFloat(rgb[1]);
					float b = Float.parseFloat(rgb[2]);
					float a = Float.parseFloat(rgb[3]);
					data.color = new ccColor4F(r, g, b, a);
				}
			}
			data.image = image;
			if ("ground".equals(name)) {
				levelData.ground = data;
			} else if ("startzone".equals(name)) {
				levelData.startZone = data;
			} else if ("endzone".equals(name)) {
				levelData.endZone = data;
			} else {
				levelData.blocks.add(data);
			}
		}
	}

	public LevelData getLevelData() {
		return levelData;
	}

	public class BuildData {
		public float x;
		public float y;
		public float width;
		public float height;
		public ccColor4F color;
		public String image;
	}

	public class LevelData {
		public LevelData() {
			blocks = new ArrayList<BuildData>();
		}

		public BuildData startZone;
		public BuildData endZone;
		public BuildData ground;
		public ArrayList<BuildData> blocks;
	}
}
