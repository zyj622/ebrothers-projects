package com.ebrothers.linerunner;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCTMXMapInfo;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

import com.ebrothers.linerunner.LineRunnerActivity.Layer0;

public class GroundLayerBuilder {
	private static final String TAG = "GroundLayerBuilder";
	private final CCLayer mLayer;

	public GroundLayerBuilder(CCLayer layer) {
		mLayer = layer;
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
				buildGround(data);
			} else if ("startzone".equals(name)) {
				buildStartZone(data);
			} else if ("endzone".equals(name)) {
				buildEndZone(data);
			} else {
				buildBlocks(data);
			}
		}
	}

	private void buildGround(BuildData data) {
		Logger.d(TAG, "buildGround. x=" + data.x + ", y=" + data.y + ", width="
				+ data.width + ", height=" + data.height);
		createSprite(data);
	}

	private void buildBlocks(BuildData data) {
		Logger.d(TAG, "buildBlocks. x=" + data.x + ", y=" + data.y + ", width="
				+ data.width + ", height=" + data.height);
		createSprite(data);
	}

	private void buildStartZone(BuildData data) {
		Logger.d(TAG, "buildStartZone. x=" + data.x + ", y=" + data.y
				+ ", width=" + data.width + ", height=" + data.height);
		// createSprite(data);
		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		CCSpriteFrame frame = cache.getSpriteFrame("run0.png");
		CCSprite sprite = CCSprite.sprite(frame);
		final float y = data.y + data.height / 2f + 10f;
		sprite.setPosition(data.x + data.width / 2f, y);
		sprite.setScale(1.5f);
		mLayer.addChild(sprite, 0, Layer0.kTagRunner);
	}

	private void buildEndZone(BuildData data) {
		Logger.d(TAG, "buildEndZone. x=" + data.x + ", y=" + data.y
				+ ", width=" + data.width + ", height=" + data.height);
		createSprite(data);
	}

	private void createSprite(BuildData data) {
		CCSpriteSheet sheet = (CCSpriteSheet) mLayer
				.getChildByTag(Layer0.kTagSpriteManager);
		CGRect rect = CGRect.make(data.x, data.y, data.width, data.height);
		CCSprite sprite = CCSprite.sprite("blackbox.png", rect);
		sprite.setPosition(data.x + data.width / 2f, data.y + data.height / 2f);
		sheet.addChild(sprite, 0);
	}

	private class BuildData {
		public float x;
		public float y;
		public float width;
		public float height;
		public ccColor4F color;
		public String image;
	}
}
