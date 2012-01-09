package org.cocos2d.levelhelper;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.GeometryUtil;

import android.util.FloatMath;

import com.badlogic.gdx.math.Vector2;

public class LevelHelperLoader {
	private ArrayList<LHObject> lhSprites;
	private ArrayList<LHObject> lhJoints;
	private ArrayList<LHObject> lhParallax;
	private ArrayList<LHObject> lhBeziers;
	private ArrayList<LHObject> lhAnims;
	// key - imageName - value LHDictionary
	private HashMap<String, LHObject> lhBatchInfo;

	private HashMap<String, LHAnimationNode> animationsInLevel;
	private HashMap<String, LHSprite> spritesInLevel;
	private HashMap<String, LHJoint> jointsInLevel;
	private HashMap<String, LHParallaxNode> parallaxesInLevel;
	private HashMap<String, LHBezierNode> beziersInLevel;
	private HashMap<String, LHBatch> batchNodesInLevel;

	private CGPoint safeFrame;
	private CGRect gameWorldRect;
	private CGPoint gravity;
	private LHObject wb;

	public LevelHelperLoader() {
		lhBatchInfo = new HashMap<String, LHObject>();
	}

	public void loadLevelHelperSceneFile(String levelFile) {
		LHObject lhObject = LHPlistParser.parse(levelFile);
		if (lhObject == null || LHObject.TYPE_LH_DICT != lhObject.type())
			return;
		HashMap<String, LHObject> dictionary = lhObject.dictValue();
		boolean fileInCorrectFormat = dictionary.get("Author").stringValue()
				.equals("Bogdan Vladu")
				&& dictionary.get("CreatedWith").stringValue()
						.equals("LevelHelper");
		assert (!fileInCorrectFormat) : "This file was not created with LevelHelper or file is damaged.";

		LHObject scenePref = dictionary.get("ScenePreference");
		safeFrame = stringToCGPoint(scenePref.dictValue().get("SafeFrame")
				.stringValue());
		gameWorldRect = stringToCGRect(scenePref.dictValue().get("GameWorld")
				.stringValue());

		CGRect color = stringToCGRect(scenePref.dictValue()
				.get("BackgroundColor").stringValue());
		CCDirector.gl.glClearColor(color.origin.x, color.origin.y,
				color.size.width, 1);

		CGSize winSize = CCDirector.sharedDirector().winSize();
		LHSettings.sharedInstance().setConvertRatio(
				CGPoint.make(winSize.width / safeFrame.x, winSize.height
						/ safeFrame.y));

		float safeFrameDiagonal = FloatMath.sqrt(safeFrame.x * safeFrame.x
				+ safeFrame.y * safeFrame.y);
		float winDiagonal = FloatMath.sqrt(winSize.width * winSize.width
				+ winSize.height * winSize.height);
		float PTM_conversion = winDiagonal / safeFrameDiagonal;

		LevelHelperLoader.setMeterRatio(LHSettings.sharedInstance()
				.lhPtmRatio() * PTM_conversion);

		// //////////////////////LOAD WORLD
		// BOUNDARIES///////////////////////////////
		if (null != dictionary.get("WBInfo")) {
			wb = dictionary.get("WBInfo");
		}

		// //////////////////////LOAD
		// SPRITES////////////////////////////////////////
		lhSprites = dictionary.get("SPRITES_INFO").arrayValue();

		// load batch nodes only if asked
		// /////////////////////////LOAD BATCH
		// IMAGES////////////////////////////////
		ArrayList<LHObject> batchInfo = dictionary.get("LoadedImages")
				.arrayValue();

		int size = batchInfo.size();
		for (int i = 0; i < size; i++) {
			HashMap<String, LHObject> imageInfo = batchInfo.get(i).dictValue();

			String image = imageInfo.get("Image").stringValue();
			lhBatchInfo.put(image, new LHObject(imageInfo));

			if (LHSettings.sharedInstance().preloadBatchNodes()) {
				loadBatchNodeWithImage(image);
			}
		}

		// /////////////////////LOAD
		// JOINTS//////////////////////////////////////////
		lhJoints = dictionary.get("JOINTS_INFO").arrayValue();

		// ////////////////////LOAD
		// PARALLAX/////////////////////////////////////////
		lhParallax = dictionary.get("PARALLAX_INFO").arrayValue();

		// //////////////////LOAD
		// BEZIER/////////////////////////////////////////////
		lhBeziers = dictionary.get("BEZIER_INFO").arrayValue();

		// //////////////////LOAD
		// ANIMS//////////////////////////////////////////////
		lhAnims = dictionary.get("ANIMS_INFO").arrayValue();

		gravity = stringToCGPoint(dictionary.get("Gravity").stringValue());

	}

	private LHBatch loadBatchNodeWithImage(String image) {
		if (image == null || image.length() == 0)
			return null;

		LHObject imageInfo = lhBatchInfo.get(image);

		if (imageInfo == null)
			return null;

		CCSpriteSheet sheet = CCSpriteSheet.spriteSheet(image);
		// LHSettings::sharedInstance()->imagePath(image.c_str()).c_str());
		LHBatch bNode = LHBatch.batchWithUniqueName(image);
		bNode.setSpriteSheet(sheet);

		// imageInfo.printAllKeys();

		bNode.setZ(imageInfo.dictValue().get("OrderZ").intValue());
		batchNodesInLevel.put(image, bNode);
		return bNode;
	}

	private CGPoint stringToCGPoint(String str) {
		return GeometryUtil.CGPointFromString(str);
	}

	private CGRect stringToCGRect(String str) {
		return GeometryUtil.CGRectFromString(str);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// PHYSICS
	// //////////////////////////////////////////////////////////////////////////////

	public static void setMeterRatio(float ratio) {
		LHSettings.sharedInstance().setLhPtmRatio(ratio);
	}

	// ------------------------------------------------------------------------------
	public static float meterRatio() {
		return LHSettings.sharedInstance().lhPtmRatio();
	}

	// ------------------------------------------------------------------------------
	public static float pixelsToMeterRatio() {
		return LHSettings.sharedInstance().lhPtmRatio()
				* LHSettings.sharedInstance().convertRatio().x;
	}

	// ------------------------------------------------------------------------------
	public static float pointsToMeterRatio() {
		return LHSettings.sharedInstance().lhPtmRatio();
	}

	// ------------------------------------------------------------------------------
	public static Vector2 pixelToMeters(CGPoint point) {
		return new Vector2(point.x / pixelsToMeterRatio(), point.y
				/ pixelsToMeterRatio());
	}

	// ------------------------------------------------------------------------------
	public static Vector2 pointsToMeters(CGPoint point) {
		return new Vector2(point.x / LHSettings.sharedInstance().lhPtmRatio(),
				point.y / LHSettings.sharedInstance().lhPtmRatio());
	}

	// ------------------------------------------------------------------------------
	public static CGPoint metersToPoints(Vector2 vec) {
		return CGPoint.make(vec.x * LHSettings.sharedInstance().lhPtmRatio(),
				vec.y * LHSettings.sharedInstance().lhPtmRatio());
	}

	// ------------------------------------------------------------------------------
	public static CGPoint metersToPixels(Vector2 vec) {
		return CGPoint
				.ccpMult(CGPoint.make(vec.x, vec.y), pixelsToMeterRatio());
	}
}
