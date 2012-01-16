package org.cocos2d.levelhelper;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.levelhelper.nodes.LHAnimationNode;
import org.cocos2d.levelhelper.nodes.LHBatch;
import org.cocos2d.levelhelper.nodes.LHBezierNode;
import org.cocos2d.levelhelper.nodes.LHContactNode;
import org.cocos2d.levelhelper.nodes.LHContactNode.ContactNodeNotifier;
import org.cocos2d.levelhelper.nodes.LHJoint;
import org.cocos2d.levelhelper.nodes.LHParallaxNode;
import org.cocos2d.levelhelper.nodes.LHPathNode;
import org.cocos2d.levelhelper.nodes.LHPathNode.PathNodeNotifier;
import org.cocos2d.levelhelper.nodes.LHSettings;
import org.cocos2d.levelhelper.nodes.LHSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.utils.GeometryUtil;

import android.util.FloatMath;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

public class LevelHelperLoader {
	private static final String TAG = "LevelHelperLoader";
	private ArrayList<LHObject> lhSprites;
	private ArrayList<LHObject> lhJoints;
	private ArrayList<LHObject> lhParallax;
	private ArrayList<LHObject> lhBeziers;
	private ArrayList<LHObject> lhAnims;
	// key - imageName - value LHDictionary
	private HashMap<String, LHObject> lhBatchInfo;

	private HashMap<String, LHAnimationNode> animationsInLevel;
	private HashMap<String, LHSprite> spritesInLevel;
	private HashMap<String, LHSprite> physicBoundariesInLevel;
	private HashMap<String, LHJoint> jointsInLevel;
	private HashMap<String, LHParallaxNode> parallaxesInLevel;
	private HashMap<String, LHBezierNode> beziersInLevel;
	private HashMap<String, LHBatch> batchNodesInLevel;

	private boolean notifOnLoopForeverAnim;
	private Object animNotifierTarget;
	private String animNotifierSelector;

	private PathNodeNotifier pathNotifier;

	private CGPoint safeFrame;
	private CGRect gameWorldRect;
	private CGPoint gravity;
	private HashMap<String, LHObject> wb;
	private CCLayer _cocosLayer;
	private World _box2dWorld;
	private LHContactNode contactNode;

	// private static final int LH_PATH_ACTION_TAG = 0;
	public static final int LH_ANIM_ACTION_TAG = 1;
	public static final boolean LH_SCENE_TESTER = false;

	public enum LevelHelper_TAG {
		DEFAULT_TAG, NUMBER_OF_TAGS
	}

	public LevelHelperLoader(String levelFile) {
		assert (levelFile != null && levelFile.length() != 0);
		lhBatchInfo = new HashMap<String, LHObject>();
		batchNodesInLevel = new HashMap<String, LHBatch>();
		spritesInLevel = new HashMap<String, LHSprite>();
		beziersInLevel = new HashMap<String, LHBezierNode>();
		animationsInLevel = new HashMap<String, LHAnimationNode>();
		physicBoundariesInLevel = new HashMap<String, LHSprite>();
		jointsInLevel = new HashMap<String, LHJoint>();
		parallaxesInLevel = new HashMap<String, LHParallaxNode>();
		loadLevelHelperSceneFile(levelFile);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHOD
	// //////////////////////////////////////////////////////////////////////////////

	// LOADING
	public void addObjectsToWorld(World world, CCLayer cocosLayer) {
		_cocosLayer = cocosLayer;
		_box2dWorld = world;

		// order is important
		addBatchNodesToLayer(cocosLayer);
		createAllAnimationsInfo();
		createAllBeziers();
		createSpritesWithPhysics();
		createParallaxes();
		createJoints();
	}

	/**
	 * No physics
	 */
	public void addSpritesToLayer(CCLayer cocosLayer) {
		throw new RuntimeException(
				"Method addSpritesToLayer is not yet implemented. Please use addObjectsToWorld with all sprites set to NO PHYSICS");
	}

	public void dispose() {
		physicBoundariesInLevel.clear();
		physicBoundariesInLevel = null;
		removeAllBezierNodes();
		releaseAllParallaxes();
		removeAllJoints();
		removeAllSprites();
		batchNodesInLevel.clear();
		batchNodesInLevel = null;

		lhSprites.clear();
		lhSprites = null;
		lhJoints.clear();
		lhJoints = null;
		lhParallax.clear();
		lhParallax = null;
		lhBeziers.clear();
		lhBeziers = null;
		lhAnims.clear();
		lhAnims = null;
		// animationsInLevel.clear();
		lhBatchInfo.clear();
		lhBatchInfo = null;

		if (wb != null) {
			wb.clear();
			wb = null;
		}

		if (null != contactNode) {
			contactNode.removeFromParentAndCleanup(true);
			contactNode = null;
		}
	}

	private void releaseAllParallaxes() {
		for (String key : parallaxesInLevel.keySet()) {
			LHParallaxNode par = parallaxesInLevel.get(key);
			if (null != par) {
				par.removeFromParentAndCleanup(true);
			}
		}
		parallaxesInLevel.clear();
		parallaxesInLevel = null;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// COLLISION HANDLING
	// //////////////////////////////////////////////////////////////////////////////
	public static boolean isPaused() {
		return LHSettings.sharedInstance().levelPaused();
	}

	public static void setPaused(boolean value) {
		LHSettings.sharedInstance().setLevelPaused(value);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// UTILITIES
	// //////////////////////////////////////////////////////////////////////////////
	public static void dontStretchArtOnIpad() {
		LHSettings.sharedInstance().setStretchArt(false);
	}

	public static void preloadBatchNodes() {
		LHSettings.sharedInstance().setPreloadBatchNodes(true);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// COLLISION HANDLING
	// //////////////////////////////////////////////////////////////////////////////
	// see API Documentation on the website to see how to use this
	public void useLevelHelperCollisionHandling() {
		if (_box2dWorld == null) {
			Log.e(TAG,
					"LevelHelper WARNING: Please call useLevelHelperCollisionHandling after addObjectsToWorld");
			return;
		}
		contactNode = LHContactNode.contactNodeWithWorld(_box2dWorld);
		if (_cocosLayer != null) {
			_cocosLayer.addChild(contactNode);
		}
	}

	public void registerPreColisionCallbackBetweenTagA(LevelHelper_TAG tagA,
			LevelHelper_TAG tagB, ContactNodeNotifier _notifier) {
		if (contactNode == null) {
			Log.w(TAG,
					"LevelHelper WARNING: Please call registerPreColisionCallbackBetweenTagA after useLevelHelperCollisionHandling");
		}
		contactNode.registerPreColisionCallbackBetweenTagA(tagA.ordinal(),
				tagB.ordinal(), _notifier);
	}

	public void cancelPreCollisionCallbackBetweenTagA(LevelHelper_TAG tagA,
			LevelHelper_TAG tagB) {
		if (contactNode == null) {
			Log.w(TAG,
					"LevelHelper WARNING: Please call registerPreColisionCallbackBetweenTagA after useLevelHelperCollisionHandling");
		}
		contactNode.cancelPreColisionCallbackBetweenTagA(tagA.ordinal(),
				tagB.ordinal());
	}

	public void registerPostColisionCallbackBetweenTagA(LevelHelper_TAG tagA,
			LevelHelper_TAG tagB, ContactNodeNotifier _notifier) {
		if (contactNode == null) {
			Log.w(TAG,
					"LevelHelper WARNING: Please call registerPostColisionCallbackBetweenTagA after useLevelHelperCollisionHandling");
		}
		contactNode.registerPreColisionCallbackBetweenTagA(tagA.ordinal(),
				tagB.ordinal(), _notifier);
	}

	public void cancelPostCollisionCallbackBetweenTagA(LevelHelper_TAG tagA,
			LevelHelper_TAG tagB) {
		if (contactNode == null) {
			Log.w(TAG,
					"LevelHelper WARNING: Please call registerPreColisionCallbackBetweenTagA after useLevelHelperCollisionHandling");
		}
		contactNode.cancelPostColisionCallbackBetweenTagA(tagA.ordinal(),
				tagB.ordinal());
	}

	// //////////////////////////////////////////////////////////////////////////////
	// SPRITES
	// //////////////////////////////////////////////////////////////////////////////
	public LHSprite spriteWithUniqueName(String name) {
		return spritesInLevel.get(name);
	}

	public ArrayList<LHSprite> spritesWithTag(LevelHelper_TAG tag) {
		ArrayList<LHSprite> array = new ArrayList<LHSprite>();
		for (String key : spritesInLevel.keySet()) {
			LHSprite ccSprite = spritesInLevel.get(key);
			if (ccSprite != null && ccSprite.getTag() == tag.ordinal()) {
				array.add(ccSprite);
			}
		}
		return array;
	}

	public boolean removeSprite(LHSprite sprite) {
		if (sprite == null)
			return false;
		sprite.removeFromParentAndCleanup(true);
		spritesInLevel.remove(sprite.getUniqueName());
		return true;
	}

	public boolean removeSpritesWithTag(LevelHelper_TAG tag) {
		for (String key : spritesInLevel.keySet()) {
			LHSprite spr = spritesInLevel.get(key);
			if (spr != null) {
				if (tag.ordinal() == spr.getTag()) {
					removeSprite(spr);
					return true;
				}
			}
		}
		return false;
	}

	public void removeAllSprites() {
		Object[] keys = spritesInLevel.keySet().toArray();
		for (Object key : keys) {
			LHSprite spr = spritesInLevel.get(key);
			if (spr != null) {
				removeSprite(spr);
			}
		}
		spritesInLevel.clear();
		spritesInLevel = null;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// CREATION
	// //////////////////////////////////////////////////////////////////////////////
	// New sprite and associated body will be released automatically
	// or you can use removeFromParentAndCleanup(true), CCSprite method, to do
	// it at a specific time
	// you must set the desired position after creation

	// sprites returned needs to be added in the layer by you
	// new sprite unique name for the returned sprite will be
	// [OLDNAME]_LH_NEW__SPRITE_XX and [OLDNAME]_LH_NEW_BODY_XX
	// no physic body
	public LHSprite newSpriteWithUniqueName(String name) {
		int count = lhSprites.size();
		for (int i = 0; i < count; ++i) {
			HashMap<String, LHObject> dictionary = lhSprites.get(i).dictValue();
			HashMap<String, LHObject> spriteProp = dictionary.get(
					"GeneralProperties").dictValue();
			if (spriteProp.get("UniqueName").stringValue() == name) {
				LHSprite ccsprite = spriteFromDictionary(spriteProp);
				String uName = name + "_LH_NEW_SPRITE_"
						+ LHSettings.sharedInstance().newBodyId();
				ccsprite.setUniqueName(uName);
				return ccsprite;
			}
		}
		return null;
	}

	// with physic body
	public LHSprite newPhysicalSpriteWithUniqueName(String name) {
		int count = lhSprites.size();
		for (int i = 0; i < count; ++i) {
			HashMap<String, LHObject> dictionary = lhSprites.get(i).dictValue();
			HashMap<String, LHObject> spriteProp = dictionary.get(
					"GeneralProperties").dictValue();
			if (spriteProp.get("UniqueName").stringValue() == name) {
				HashMap<String, LHObject> physicProp = dictionary.get(
						"PhysicProperties").dictValue();
				LHSprite ccsprite = spriteFromDictionary(spriteProp);
				Body body = b2BodyFromDictionary(physicProp, spriteProp,
						ccsprite, _box2dWorld);
				if (body != null)
					ccsprite.setBody(body);
				String uName = name + "_LH_NEW_BODY_"
						+ LHSettings.sharedInstance().newBodyId();
				ccsprite.setUniqueName(uName);
				return ccsprite;
			}
		}
		return null;
	}

	// sprites are added in the coresponding batch node automatically
	// new sprite unique name for the returned sprite will be
	// [OLDNAME]_LH_NEW_BATCH_SPRITE_XX and [OLDNAME]_LH_NEW_BATCH_BODY_XX
	// no physic body
	public LHSprite newBatchSpriteWithUniqueName(String name) {
		int count = lhSprites.size();
		for (int i = 0; i < count; ++i) {
			HashMap<String, LHObject> dictionary = lhSprites.get(i).dictValue();
			HashMap<String, LHObject> spriteProp = dictionary.get(
					"GeneralProperties").dictValue();
			if (spriteProp.get("UniqueName").stringValue().equals(name)) {
				// find the coresponding batch node for this sprite
				LHBatch bNode = batchNodeForFile(spriteProp.get("Image")
						.stringValue());
				if (bNode != null) {
					CCSpriteSheet batch = bNode.getSpriteSheet();
					if (batch != null) {
						LHSprite ccsprite = spriteWithBatchFromDictionary(
								spriteProp, bNode);
						batch.addChild(ccsprite, spriteProp.get("ZOrder")
								.intValue());
						String uName = name + "_LH_NEW_BATCH_SPRITE_"
								+ LHSettings.sharedInstance().newBodyId();
						ccsprite.setUniqueName(uName);
						return ccsprite;
					}
				}
			}
		}
		return null;
	}

	// with physic body
	public LHSprite newPhysicalBatchSpriteWithUniqueName(String name) {
		int count = lhSprites.size();
		for (int i = 0; i < count; ++i) {
			HashMap<String, LHObject> dictionary = lhSprites.get(i).dictValue();
			HashMap<String, LHObject> spriteProp = dictionary.get(
					"GeneralProperties").dictValue();
			if (spriteProp.get("UniqueName").stringValue().equals(name)) {
				// find the coresponding batch node for this sprite
				LHBatch bNode = batchNodeForFile(spriteProp.get("Image")
						.stringValue());
				if (bNode != null) {
					CCSpriteSheet batch = bNode.getSpriteSheet();
					if (batch != null) {
						LHSprite ccsprite = spriteWithBatchFromDictionary(
								spriteProp, bNode);
						batch.addChild(ccsprite, spriteProp.get("ZOrder")
								.intValue());

						HashMap<String, LHObject> physicProp = dictionary.get(
								"PhysicProperties").dictValue();
						Body body = b2BodyFromDictionary(physicProp,
								spriteProp, ccsprite, _box2dWorld);
						if (body != null)
							ccsprite.setBody(body);
						String uName = name + "_LH_NEW_BATCH_BODY_"
								+ LHSettings.sharedInstance().newBodyId();
						ccsprite.setUniqueName(uName);
						return ccsprite;
					}
				}
			}
		}
		return null;
	}

	public LHJoint jointWithUniqueName(String key) {
		return jointsInLevel.get(key);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// JOIN
	// //////////////////////////////////////////////////////////////////////////////
	public ArrayList<LHJoint> jointsWithTag(LevelHelper_TAG tag) {
		ArrayList<LHJoint> jointsWithTag = new ArrayList<LHJoint>();
		for (String key : jointsInLevel.keySet()) {
			LHJoint levelJoint = jointsInLevel.get(key);
			if (levelJoint.getTag() == tag.ordinal()) {
				jointsWithTag.add(levelJoint);
			}
		}
		return jointsWithTag;
	}

	public void removeJointsWithTag(LevelHelper_TAG tag) {
		Object[] keys = jointsInLevel.keySet().toArray();
		for (Object key : keys) {
			LHJoint jt = jointsInLevel.get(key);
			if (jt != null) {
				if (jt.getTag() == tag.ordinal()) {
					jointsInLevel.remove(key);
				}
			}
		}
	}

	public void removeJoint(LHJoint joint) {
		if (joint == null)
			return;
		jointsInLevel.remove(joint.getUniqueName());
	}

	public void removeAllJoints() {
		jointsInLevel.clear();
		jointsInLevel = null;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// PARALLAX
	// //////////////////////////////////////////////////////////////////////////////
	public LHParallaxNode paralaxNodeWithUniqueName(String uniqueName) {
		return parallaxesInLevel.get(uniqueName);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// BEZIER
	// //////////////////////////////////////////////////////////////////////////////
	public LHBezierNode bezierNodeWithUniqueName(String name) {
		return beziersInLevel.get(name);
	}

	public void removeAllBezierNodes() {
		for (String key : beziersInLevel.keySet()) {
			LHBezierNode node = beziersInLevel.get(key);
			if (node != null) {
				node.removeFromParentAndCleanup(true);
			}
		}
		beziersInLevel.clear();
		beziersInLevel = null;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// GRAVITY
	// //////////////////////////////////////////////////////////////////////////////
	public boolean isGravityZero() {
		if (gravity.x == 0 && gravity.y == 0)
			return true;
		return false;
	}

	public void createGravity(World world) {
		if (isGravityZero())
			Log.w(TAG,
					"LevelHelper Warning: Gravity is not defined in the level. Are you sure you want to set a zero gravity?");
		world.setGravity(new Vector2(gravity.x, gravity.y));
	}

	// //////////////////////////////////////////////////////////////////////////////
	// PHYSIC BOUNDARIES
	// //////////////////////////////////////////////////////////////////////////////
	public void createPhysicBoundaries(World _world) {
		CGPoint wbConv = LHSettings.sharedInstance().realConvertRatio();
		createPhysicBoundariesHelper(_world, wbConv, CGPoint.make(0.0f, 0.0f));
	}

	// this method should be used when using dontStretchArtOnIpad
	// see api documentatin for more info
	public void createPhysicBoundariesNoStretching(World _world) {
		CGPoint pos_offset = LHSettings.sharedInstance().possitionOffset();
		CGPoint wbConv = LHSettings.sharedInstance().convertRatio();
		createPhysicBoundariesHelper(_world, wbConv,
				CGPoint.make(pos_offset.x / 2.0f, pos_offset.y / 2.0f));
	}

	public CGRect physicBoundariesRect() {
		CGPoint wbConv = LHSettings.sharedInstance().convertRatio();
		CGRect rect = stringToCGRect(wb.get("WBRect").stringValue());
		rect.origin.x = rect.origin.x * wbConv.x;
		rect.origin.y = rect.origin.y * wbConv.y;
		rect.size.width = rect.size.width * wbConv.x;
		rect.size.height = rect.size.height * wbConv.y;
		return rect;
	}

	public boolean hasPhysicBoundaries() {
		if (wb == null) {
			return false;
		}
		CGRect rect = stringToCGRect(wb.get("WBRect").stringValue());
		if (rect.size.width == 0 || rect.size.height == 0)
			return false;
		return true;
	}

	public Body leftPhysicBoundary() {
		return physicBoundarieForKey("LHPhysicBoundarieLeft");
	}

	public LHSprite leftPhysicBoundarySprite() {
		return physicBoundariesInLevel.get("LHPhysicBoundarieLeft");
	}

	public Body rightPhysicBoundary() {
		return physicBoundarieForKey("LHPhysicBoundarieRight");
	}

	public LHSprite rightPhysicBoundarySprite() {
		return physicBoundariesInLevel.get("LHPhysicBoundarieRight");
	}

	public Body topPhysicBoundary() {
		return physicBoundarieForKey("LHPhysicBoundarieTop");
	}

	public LHSprite topPhysicBoundarySprite() {
		return physicBoundariesInLevel.get("LHPhysicBoundarieTop");
	}

	public Body bottomPhysicBoundary() {
		return physicBoundarieForKey("LHPhysicBoundarieBottom");
	}

	public LHSprite bottomPhysicBoundarySprite() {
		return physicBoundariesInLevel.get("LHPhysicBoundarieBottom");
	}

	public void removePhysicBoundaries() {
		physicBoundariesInLevel.clear();
	}

	// //////////////////////////////////////////////////////////////////////////////
	// LEVEL INFO
	// //////////////////////////////////////////////////////////////////////////////
	// the device size set in loaded level
	public CGSize gameScreenSize() {
		return CGSize.make(safeFrame.x, safeFrame.y);
	}

	// the size of the game world
	public CGRect gameWorldSize() {
		CGPoint wbConv = LHSettings.sharedInstance().convertRatio();
		CGRect ws = gameWorldRect;
		ws.origin.x *= wbConv.x;
		ws.origin.y *= wbConv.y;
		ws.size.width *= wbConv.x;
		ws.size.height *= wbConv.y;
		return ws;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// BATCH
	// //////////////////////////////////////////////////////////////////////////////
	public int numberOfBatchNodesUsed() {
		return batchNodesInLevel.size() - 1;
	}

	public void removeUnusedBatchesFromMemory() {
		// TODO no getDescendants() method in CCSpriteSheet
		// for (String key : batchNodesInLevel.keySet()) {
		// LHBatch bNode = batchNodesInLevel.get(key);
		// if (bNode != null) {
		// CCSpriteSheet cNode = bNode.getSpriteSheet();
		// if (cNode.getDescendants().isEmpty()) {
		// // delete bNode;
		// batchNodesInLevel.}(key);
		// }
		// }
		// }
	}

	// //////////////////////////////////////////////////////////////////////////////
	// ANIMATION
	// //////////////////////////////////////////////////////////////////////////////
	public void startAnimationWithUniqueName(String animationName,
			LHSprite sprite, Object target, String selector) {
		LHAnimationNode animNode = animationsInLevel.get(animationName);
		if (animNode != null) {
			LHBatch batch = batchNodeForFile(animNode.getImageName());
			if (batch != null) {
				animNode.setSpriteSheet(batch.getSpriteSheet());
				animNode.computeFrames();
				if (target == null) {
					animNode.runAnimationOnSprite(sprite, animNotifierTarget,
							animNotifierSelector, notifOnLoopForeverAnim);
				} else {
					animNode.runAnimationOnSprite(sprite, target, selector,
							notifOnLoopForeverAnim);
				}
			}
		}
	}

	public void stopAnimationOnSprite(LHSprite sprite) {
		if (sprite != null) {
			sprite.stopAction(LH_ANIM_ACTION_TAG);
			sprite.setAnimation(null);
		}
	}

	// this will not start the animation - it will just prepare it
	private void prepareAnimationWithUniqueName(String animName, LHSprite sprite) {
		LHAnimationNode animNode = animationsInLevel.get(animName);
		if (animNode == null)
			return;
		LHBatch batch = batchNodeForFile(animNode.getImageName());
		if (batch != null) {
			animNode.setSpriteSheet(batch.getSpriteSheet());
			animNode.computeFrames();
			sprite.setAnimation(animNode);
		}
	}

	// needs to be called before addObjectsToWorld or addSpritesToLayer
	// signature for registered method should be like this: void
	// HelloWorld.spriteAnimHasEnded(CCSprite* spr, const std.string&
	// animName);
	// registration is done like this: lh.registerNotifierOnAnimationEnds(this,
	// callfuncND_selector(HelloWorld.spriteAnimHasEnded));
	// this will trigger for all type of animations even for the ones controlled
	// by you will next/prevFrameFor...
	public void registerNotifierOnAllAnimationEnds(Object target,
			String selector) {
		animNotifierTarget = target;
		animNotifierSelector = selector;
	}

	/**
	 * by default the notification on animation end works only on
	 * non-"loop forever" animations if you want to receive notifications on
	 * "loop forever" animations enable this behaviour before addObjectsToWorld
	 * by calling the following function
	 */
	public void enableNotifOnLoopForeverAnimations() {
		notifOnLoopForeverAnim = true;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// PATH
	// //////////////////////////////////////////////////////////////////////////////
	// describe path movement without setting the sprite position on the actual
	// points on the path
	public void moveSpriteOnPathWithUniqueName(LHSprite ccsprite,
			String pathUniqueName, float time, boolean startAtEndPoint,
			boolean isCyclic, boolean restartOtherEnd, int axis, boolean flipx,
			boolean flipy, boolean deltaMove) {
		if (ccsprite == null)
			return;
		LHBezierNode node = bezierNodeWithUniqueName(pathUniqueName);
		if (node != null) {
			LHPathNode pathNode = node.addSpriteOnPath(ccsprite, time,
					startAtEndPoint, isCyclic, restartOtherEnd, axis, flipx,
					flipy, deltaMove);
			if (pathNode != null) {
				pathNode.setNotifer(pathNotifier);
			}
		}
	}

	// DISCUSSION
	// signature for registered method should be like this: void
	// HelloWorld.spriteMoveOnPathEnded(LHSprite spr);
	// registration is done like this: lh.registerNotifierOnPathEnd(this,
	// callfuncN_selector(HelloWorld.spriteMoveOnPathEnded));
	public void registerNotifierOnAllPathEndPoints(PathNodeNotifier notifier) {
		pathNotifier = notifier;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHOD
	// //////////////////////////////////////////////////////////////////////////////

	private void createJoints() {
		int count = lhJoints.size();
		for (int i = 0; i < count; ++i) {
			HashMap<String, LHObject> jointDict = lhJoints.get(i).dictValue();
			LHJoint boxJoint = jointFromDictionary(jointDict, _box2dWorld);
			if (boxJoint != null) {
				jointsInLevel.put(jointDict.get("UniqueName").stringValue(),
						boxJoint);
			}
		}
	}

	private LHJoint jointFromDictionary(HashMap<String, LHObject> joint,
			World world) {
		if (joint == null || world == null)
			return null;

		Joint boxJoint = null;
		LHSprite sprA = (LHSprite) spritesInLevel.get(joint.get("ObjectA")
				.stringValue());
		Body bodyA = sprA.getBody();

		LHSprite sprB = (LHSprite) spritesInLevel.get(joint.get("ObjectB")
				.stringValue());
		Body bodyB = sprB.getBody();

		CGPoint sprPosA = sprA.getPosition();
		CGPoint sprPosB = sprB.getPosition();

		CGSize scaleA = sprA.getRealScale();
		CGSize scaleB = sprB.getRealScale();

		if (bodyA == null || bodyB == null)
			return null;

		CGPoint anchorA = stringToCGPoint(joint.get("AnchorA").stringValue());
		CGPoint anchorB = stringToCGPoint(joint.get("AnchorB").stringValue());

		boolean collideConnected = joint.get("CollideConnected").boolValue();

		int tag = joint.get("Tag").intValue();
		int type = joint.get("Type").intValue();

		Vector2 posA, posB;

		float convertX = LHSettings.sharedInstance().convertRatio().x;
		float convertY = LHSettings.sharedInstance().convertRatio().y;

		float ptm = LHSettings.sharedInstance().lhPtmRatio();

		if (!joint.get("CenterOfMass").boolValue()) {
			posA = new Vector2((sprPosA.x + anchorA.x * scaleA.width) / ptm,
					(sprPosA.y - anchorA.y * scaleA.height) / ptm);

			posB = new Vector2((sprPosB.x + anchorB.x * scaleB.width) / ptm,
					(sprPosB.y - anchorB.y * scaleB.height) / ptm);

		} else {
			posA = bodyA.getWorldCenter();
			posB = bodyB.getWorldCenter();
		}

		if (bodyA != null && bodyB != null) {
			switch (type) {
			case LHJoint.LH_DISTANCE_JOINT: {
				DistanceJointDef jointDef = new DistanceJointDef();

				jointDef.initialize(bodyA, bodyB, posA, posB);

				jointDef.collideConnected = collideConnected;

				jointDef.frequencyHz = joint.get("Frequency").floatValue();
				jointDef.dampingRatio = joint.get("Damping").floatValue();

				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;

			case LHJoint.LH_REVOLUTE_JOINT: {
				RevoluteJointDef jointDef = new RevoluteJointDef();
				jointDef.lowerAngle = ccMacros.CC_DEGREES_TO_RADIANS(joint.get(
						"LowerAngle").floatValue());
				jointDef.upperAngle = ccMacros.CC_DEGREES_TO_RADIANS(joint.get(
						"UpperAngle").floatValue());
				jointDef.motorSpeed = joint.get("MotorSpeed").floatValue(); // Usually
																			// in
																			// radians
																			// per
																			// second.
																			// ?????
				jointDef.maxMotorTorque = joint.get("MaxTorque").floatValue(); // Usually
																				// in
																				// N-m.
																				// ?????
				jointDef.enableLimit = joint.get("EnableLimit").boolValue();
				jointDef.enableMotor = joint.get("EnableMotor").boolValue();
				jointDef.collideConnected = collideConnected;
				jointDef.initialize(bodyA, bodyB, posA);

				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;

			case LHJoint.LH_PRISMATIC_JOINT: {
				PrismaticJointDef jointDef = new PrismaticJointDef();

				// Bouncy limit
				CGPoint axisPt = stringToCGPoint(joint.get("Axis")
						.stringValue());

				Vector2 axis = new Vector2(axisPt.x, axisPt.y);
				axis.nor();

				jointDef.initialize(bodyA, bodyB, posA, axis);

				jointDef.motorSpeed = joint.get("MotorSpeed").floatValue();
				jointDef.maxMotorForce = joint.get("MaxMotorForce")
						.floatValue();

				jointDef.lowerTranslation = ccMacros
						.CC_DEGREES_TO_RADIANS(joint.get("LowerTranslation")
								.floatValue());
				jointDef.upperTranslation = ccMacros
						.CC_DEGREES_TO_RADIANS(joint.get("UpperTranslation")
								.floatValue());

				jointDef.enableMotor = joint.get("EnableMotor").boolValue();
				jointDef.enableLimit = joint.get("EnableLimit").boolValue();
				jointDef.collideConnected = collideConnected;
				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;

			case LHJoint.LH_PULLEY_JOINT: {
				PulleyJointDef jointDef = new PulleyJointDef();

				CGPoint grAnchorA = stringToCGPoint(joint.get("GroundAnchorA")
						.stringValue());
				CGPoint grAnchorB = stringToCGPoint(joint.get("GroundAnchorB")
						.stringValue());

				CGSize winSize = CCDirector.sharedDirector().displaySize();

				grAnchorA.y = winSize.height - convertY * grAnchorA.y;
				grAnchorB.y = winSize.height - convertY * grAnchorB.y;

				Vector2 groundAnchorA = new Vector2(convertX * grAnchorA.x
						/ ptm, grAnchorA.y / ptm);

				Vector2 groundAnchorB = new Vector2(convertX * grAnchorB.x
						/ ptm, grAnchorB.y / ptm);

				float ratio = joint.get("Ratio").floatValue();
				jointDef.initialize(bodyA, bodyB, groundAnchorA, groundAnchorB,
						posA, posB, ratio);
				jointDef.collideConnected = collideConnected;

				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;

			case LHJoint.LH_GEAR_JOINT: {
				GearJointDef jointDef = new GearJointDef();

				jointDef.bodyA = bodyB;
				jointDef.bodyB = bodyA;

				if (bodyA == null || bodyB == null)
					return null;

				LHJoint jointAObj = jointWithUniqueName(joint.get("JointA")
						.stringValue());
				Joint jointA = jointAObj.getJoint();

				LHJoint jointBObj = jointWithUniqueName(joint.get("JointB")
						.stringValue());
				Joint jointB = jointBObj.getJoint();

				if (jointA == null && jointB == null)
					return null;

				jointDef.joint1 = jointA;
				jointDef.joint2 = jointB;

				jointDef.ratio = joint.get("Ratio").floatValue();
				jointDef.collideConnected = collideConnected;
				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;

			case LHJoint.LH_WHEEL_JOINT: // aka line joint
			{
				WheelJointDef jointDef = new WheelJointDef();

				CGPoint axisPt = stringToCGPoint(joint.get("Axis")
						.stringValue());
				Vector2 axis = new Vector2(axisPt.x, axisPt.y);
				axis.nor();

				jointDef.motorSpeed = joint.get("MotorSpeed").floatValue();
				// Usually in radians per second. ?????
				jointDef.maxMotorTorque = joint.get("MaxTorque").floatValue(); // Usually
																				// in
																				// N-m.
																				// ?????
				jointDef.enableMotor = joint.get("EnableMotor").boolValue();
				jointDef.frequencyHz = joint.get("Frequency").floatValue();
				jointDef.dampingRatio = joint.get("Damping").floatValue();

				jointDef.initialize(bodyA, bodyB, posA, axis);
				jointDef.collideConnected = collideConnected;

				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;
			case LHJoint.LH_WELD_JOINT: {
				WeldJointDef jointDef = new WeldJointDef();
				// jointDef.frequencyHz = joint.get("Frequency").floatValue();
				// jointDef.dampingRatio = joint.get("Damping").floatValue();
				jointDef.initialize(bodyA, bodyB, posA);
				jointDef.collideConnected = collideConnected;
				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;
			// NOT WORKING YET AS THE BOX2D JOINT FOR THIS TYPE IS A TEST JOINT
			case LHJoint.LH_ROPE_JOINT: {
				RopeJointDef jointDef = new RopeJointDef();
				jointDef.localAnchorA.set(bodyA.getPosition());
				jointDef.localAnchorB.set(bodyB.getPosition());
				jointDef.bodyA = bodyA;
				jointDef.bodyB = bodyB;
				jointDef.maxLength = joint.get("MaxLength").floatValue();
				jointDef.collideConnected = collideConnected;
				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;

			case LHJoint.LH_FRICTION_JOINT: {
				FrictionJointDef jointDef = new FrictionJointDef();
				jointDef.maxForce = joint.get("MaxForce").floatValue();
				jointDef.maxTorque = joint.get("MaxTorque").floatValue();
				jointDef.initialize(bodyA, bodyB, posA);
				jointDef.collideConnected = collideConnected;

				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}

			}
				break;

			default:
				Log.e(TAG, "Unknown joint type in LevelHelper file.");
				break;
			}
		}

		LHJoint levelJoint = LHJoint.jointWithUniqueName(joint
				.get("UniqueName").stringValue(), tag, type, boxJoint);
		// levelJoint.getTag() = tag;
		// levelJoint.type = (LH_JOINT_TYPE)type;
		// levelJoint.joint = boxJoint;
		boxJoint.setUserData(levelJoint);
		return levelJoint;
	}

	private void createParallaxes() {
		int count = lhParallax.size();
		for (int i = 0; i < count; i++) {
			HashMap<String, LHObject> parallaxDict = lhParallax.get(i)
					.dictValue();
			LHParallaxNode node = parallaxNodeFromDictionary(parallaxDict,
					_cocosLayer);
			if (node != null) {
				parallaxesInLevel.put(parallaxDict.get("UniqueName")
						.stringValue(), node);
			}
		}
	}

	private LHParallaxNode parallaxNodeFromDictionary(
			HashMap<String, LHObject> parallaxDict, CCLayer layer) {
		LHParallaxNode node = LHParallaxNode.nodeWithDictionary(parallaxDict);
		if (layer != null && node != null) {
			int z = parallaxDict.get("ZOrder").intValue();
			layer.addChild(node, z);
		}

		ArrayList<LHObject> spritesInfo = parallaxDict.get("Sprites")
				.arrayValue();
		int count = spritesInfo.size();
		for (int i = 0; i < count; ++i) {
			HashMap<String, LHObject> sprInf = spritesInfo.get(i).dictValue();
			float ratioX = sprInf.get("RatioX").floatValue();
			float ratioY = sprInf.get("RatioY").floatValue();
			String sprName = sprInf.get("SpriteName").stringValue();

			LHSprite spr = spriteWithUniqueName(sprName);
			if (node != null && spr != null) {
				node.addChild(spr, CGPoint.ccp(ratioX, ratioY));
			}
		}
		return node;
	}

	private void createSpritesWithPhysics() {
		int count = lhSprites.size();
		for (int i = 0; i < count; ++i) {
			HashMap<String, LHObject> dictionary = lhSprites.get(i).dictValue();
			HashMap<String, LHObject> spriteProp = dictionary.get(
					"GeneralProperties").dictValue();
			HashMap<String, LHObject> physicProp = dictionary.get(
					"PhysicProperties").dictValue();
			LHBatch bNode = batchNodeForFile(spriteProp.get("Image")
					.stringValue());
			if (bNode != null) {
				CCSpriteSheet batch = bNode.getSpriteSheet();
				if (batch != null) {
					LHSprite ccsprite = spriteWithBatchFromDictionary(
							spriteProp, bNode);

					if (!LHSettings.sharedInstance().isCoronaUser()) {
						batch.addChild(ccsprite, spriteProp.get("ZOrder")
								.intValue());
					} else
						_cocosLayer.addChild(ccsprite);

					String uniqueName = spriteProp.get("UniqueName")
							.stringValue();
					// 3 means no physic
					if (physicProp.get("Type").intValue() != 3) {
						Body body = b2BodyFromDictionary(physicProp,
								spriteProp, ccsprite, _box2dWorld);

						if (body != null)
							ccsprite.setBody(body);
						spritesInLevel.put(uniqueName, ccsprite);
					} else {
						spritesInLevel.put(uniqueName, ccsprite);
						setCustomAttributesForNonPhysics(spriteProp, ccsprite);
					}
					// ![[spriteProp get:@"IsInParallax"] boolValue] &&
					if (spriteProp.get("PathName").stringValue() != "None") {
						createPathOnSprite(ccsprite, spriteProp);
					}
					createAnimationFromDictionary(spriteProp, ccsprite);
				}
			}
		}

	}

	private LHBatch batchNodeForFile(String image) {
		LHBatch bNode = batchNodesInLevel.get(image);
		if (bNode != null) {
			return bNode;
		} else {
			bNode = loadBatchNodeWithImage(image);
			addBatchNodeToLayer(_cocosLayer, bNode);
			return bNode;
		}
	}

	private void addBatchNodeToLayer(CCLayer _cocosLayer2, LHBatch info) {
		if (info != null && _cocosLayer != null) {
			_cocosLayer.addChild(info.getSpriteSheet(), info.getZ());
		}

	}

	private void createAnimationFromDictionary(
			HashMap<String, LHObject> spriteProp, LHSprite ccsprite) {
		String animName = spriteProp.get("AnimName").stringValue();
		if (animName != "") {
			LHAnimationNode animNode = (LHAnimationNode) animationsInLevel
					.get(animName);
			if (animNode != null) {
				if (animNode.startAtLaunch) {
					LHBatch batch = batchNodeForFile(animNode.getImageName());
					if (batch != null) {
						animNode.setSpriteSheet(batch.getSpriteSheet());
						animNode.computeFrames();
						animNode.runAnimationOnSprite(ccsprite,
								animNotifierTarget, animNotifierSelector,
								notifOnLoopForeverAnim);
					}
				} else {
					prepareAnimationWithUniqueName(animName, ccsprite);
				}
			}
		}

	}

	private void createPathOnSprite(LHSprite ccsprite,
			HashMap<String, LHObject> spriteProp) {
		if (ccsprite == null || spriteProp == null)
			return;
		String uniqueName = spriteProp.get("PathName").stringValue();
		boolean isCyclic = spriteProp.get("PathIsCyclic").boolValue();
		float pathSpeed = spriteProp.get("PathSpeed").floatValue();
		// 0 is first 1 is end
		int startPoint = spriteProp.get("PathStartPoint").intValue();
		// false means will restart where it finishes
		boolean pathOtherEnd = spriteProp.get("PathOtherEnd").boolValue();
		// false means will restart where it finishes
		int axisOrientation = spriteProp.get("PathOrientation").intValue();

		boolean flipX = spriteProp.get("PathFlipX").boolValue();
		boolean flipY = spriteProp.get("PathFlipY").boolValue();

		moveSpriteOnPathWithUniqueName(ccsprite, uniqueName, pathSpeed,
				startPoint == 1, isCyclic, pathOtherEnd, axisOrientation,
				flipX, flipY, true);
	}

	private LHSprite spriteWithBatchFromDictionary(
			HashMap<String, LHObject> spriteProp, LHBatch lhBatch) {
		CGRect uv = stringToCGRect(spriteProp.get("UV").stringValue());
		if (lhBatch == null)
			return null;
		CCSpriteSheet batch = lhBatch.getSpriteSheet();
		if (batch == null)
			return null;
		String img = LHSettings.sharedInstance().imagePath(
				lhBatch.getUniqueName());

		if (LHSettings.sharedInstance().shouldScaleImageOnRetina(img)) {
			uv.origin.x *= 2.0f;
			uv.origin.y *= 2.0f;
			uv.size.width *= 2.0f;
			uv.size.height *= 2.0f;
		}
		LHSprite ccsprite = null;
		if (!LHSettings.sharedInstance().isCoronaUser())
			ccsprite = LHSprite.spriteWithSpriteSheet(batch, uv);
		else
			ccsprite = LHSprite.spriteWithFile(img, uv);
		setSpriteProperties(ccsprite, spriteProp);
		return ccsprite;
	}

	private void setSpriteProperties(LHSprite ccsprite,
			HashMap<String, LHObject> spriteProp) {
		// convert position from LH to Cocos2d coordinates
		CGSize winSize = CCDirector.sharedDirector().winSize();
		CGPoint position = stringToCGPoint(spriteProp.get("Position")
				.stringValue());
		position.x *= LHSettings.sharedInstance().convertRatio().x;
		position.y *= LHSettings.sharedInstance().convertRatio().y;
		position.y = winSize.height - position.y;
		CGPoint pos_offset = LHSettings.sharedInstance().possitionOffset();
		position.x += pos_offset.x;
		position.y -= pos_offset.y;
		ccsprite.setPosition(position);
		ccsprite.setRotation(spriteProp.get("Angle").intValue());
		ccsprite.setOpacity((int) (255 * spriteProp.get("Opacity").floatValue() * LHSettings
				.sharedInstance().customAlpha()));
		CGRect color = stringToCGRect(spriteProp.get("Color").stringValue());
		ccsprite.setColor(ccColor3B.ccc3((int) (255 * color.origin.x),
				(int) (255 * color.origin.y), (int) (255 * color.size.width)));
		CGPoint scale = stringToCGPoint(spriteProp.get("Scale").stringValue());
		ccsprite.setVisible(spriteProp.get("IsDrawable").boolValue());
		ccsprite.setTag(spriteProp.get("Tag").intValue());
		scale.x *= LHSettings.sharedInstance().convertRatio().x;
		scale.y *= LHSettings.sharedInstance().convertRatio().y;
		String img = LHSettings.sharedInstance().imagePath(
				spriteProp.get("Image").stringValue());
		ccsprite.setRealScale(CGSize.make(scale.x, scale.y));
		if (LHSettings.sharedInstance().shouldScaleImageOnRetina(img)) {
			scale.x /= 2.0f;
			scale.y /= 2.0f;
		}
		// this is to fix a noise issue on cocos2d.
		// scale.x += 0.0005f*scale.x;
		// scale.y += 0.0005f*scale.y;
		ccsprite.setScaleX(scale.x);
		ccsprite.setScaleY(scale.y);
		ccsprite.setUniqueName(spriteProp.get("UniqueName").stringValue());
	}

	private Body b2BodyFromDictionary(HashMap<String, LHObject> spritePhysic,
			HashMap<String, LHObject> spriteProp, LHSprite ccsprite,
			World _world) {
		BodyDef bodyDef = new BodyDef();
		int bodyType = spritePhysic.get("Type").intValue();
		// in case the user wants to create a body with a sprite that has type
		// as "NO_PHYSIC"
		if (bodyType == 3)
			bodyDef.type = BodyType.DynamicBody;
		bodyDef.type = BodyType.values()[bodyType];
		CGPoint pos = ccsprite.getPosition();
		bodyDef.position.set(pos.x / LHSettings.sharedInstance().lhPtmRatio(),
				pos.y / LHSettings.sharedInstance().lhPtmRatio());
		bodyDef.angle = ccMacros.CC_DEGREES_TO_RADIANS(-1
				* spriteProp.get("Angle").intValue());
		// bodyDef.userData = ccsprite;

		Body body = _world.createBody(bodyDef);
		body.setUserData(ccsprite);

		body.setFixedRotation(spritePhysic.get("FixedRot").boolValue());

		CGPoint linearVelocity = stringToCGPoint(spritePhysic.get(
				"LinearVelocity").stringValue());

		float linearDamping = spritePhysic.get("LinearDamping").floatValue();
		float angularVelocity = spritePhysic.get("AngularVelocity")
				.floatValue();
		float angularDamping = spritePhysic.get("AngularDamping").floatValue();

		boolean isBullet = spritePhysic.get("IsBullet").boolValue();
		boolean canSleep = spritePhysic.get("CanSleep").boolValue();

		ArrayList<LHObject> fixtures = spritePhysic.get("ShapeFixtures")
				.arrayValue();
		CGPoint scale = stringToCGPoint(spriteProp.get("Scale").stringValue());

		CGPoint size = stringToCGPoint(spriteProp.get("Size").stringValue());

		CGPoint border = stringToCGPoint(spritePhysic.get("ShapeBorder")
				.stringValue());

		CGPoint offset = stringToCGPoint(spritePhysic
				.get("ShapePositionOffset").stringValue());

		float gravityScale = spritePhysic.get("GravityScale").floatValue();

		scale.x *= LHSettings.sharedInstance().convertRatio().x;
		scale.y *= LHSettings.sharedInstance().convertRatio().y;

		// if(scale.x == 0)
		// scale.x = 0.01;
		// if(scale.y == 0)
		// scale.y = 0.01;

		float ptm = LHSettings.sharedInstance().lhPtmRatio();

		if (fixtures == null || fixtures.isEmpty()
				|| fixtures.get(0).arrayValue().isEmpty()) {
			// PolygonShape shape = new PolygonShape();
			FixtureDef fixture = new FixtureDef();
			CircleShape circle = new CircleShape();
			setFixtureDefPropertiesFromDictionary(spritePhysic, fixture);

			if (spritePhysic.get("IsCircle").boolValue()) {
				if (LHSettings.sharedInstance().convertLevel()) {
					// NSLog(@"convert circle");
					// this is for the ipad scale on circle look weird if we
					// dont do this
					float scaleSpr = ccsprite.getScaleX();
					ccsprite.setScaleY(scaleSpr);
				}

				float circleScale = scale.x; // if we dont do this we dont have
												// collision
				if (circleScale < 0)
					circleScale = -circleScale;

				float radius = (size.x * circleScale / 2.0f - border.x / 2.0f
						* circleScale)
						/ LHSettings.sharedInstance().lhPtmRatio();

				if (radius < 0)
					radius *= -1;
				circle.setRadius(radius);
				circle.setPosition(new Vector2(offset.x / 2.0f
						/ LHSettings.sharedInstance().lhPtmRatio(), -offset.y
						/ 2.0f / LHSettings.sharedInstance().lhPtmRatio()));
				fixture.shape = circle;
				body.createFixture(fixture);
			} else {
				// THIS WAS ADDED BECAUSE I DISCOVER A BUG IN BOX2d
				// that makes linearImpulse to not work the body is in contact
				// with
				// a box object
				int vsize = 4;
				Vector2[] verts = new Vector2[vsize];
				PolygonShape shape = new PolygonShape();
				for (int i = 0; i < verts.length; i++) {
					verts[i] = new Vector2();
				}

				if (scale.x * scale.y < 0.0f) {
					verts[3].x = ((-1 * size.x + border.x / 2.0f) * scale.x
							/ 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[3].y = ((-1 * size.y + border.y / 2.0f) * scale.y
							/ 2.0f - offset.y / 2.0f)
							/ ptm;
					verts[2].x = ((+size.x - border.x / 2.0f) * scale.x / 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[2].y = ((-1 * size.y + border.y / 2.0f) * scale.y
							/ 2.0f - offset.y / 2.0f)
							/ ptm;
					verts[1].x = ((+size.x - border.x / 2.0f) * scale.x / 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[1].y = ((+size.y - border.y / 2.0f) * scale.y / 2.0f - offset.y / 2.0f)
							/ ptm;
					verts[0].x = ((-1 * size.x + border.x / 2.0f) * scale.x
							/ 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[0].y = ((+size.y - border.y / 2.0f) * scale.y / 2.0f - offset.y / 2.0f)
							/ ptm;
				} else {
					verts[0].x = ((-1 * size.x + border.x / 2.0f) * scale.x
							/ 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[0].y = ((-1 * size.y + border.y / 2.0f) * scale.y
							/ 2.0f - offset.y / 2.0f)
							/ ptm;
					verts[1].x = ((+size.x - border.x / 2.0f) * scale.x / 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[1].y = ((-1 * size.y + border.y / 2.0f) * scale.y
							/ 2.0f - offset.y / 2.0f)
							/ ptm;
					verts[2].x = ((+size.x - border.x / 2.0f) * scale.x / 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[2].y = ((+size.y - border.y / 2.0f) * scale.y / 2.0f - offset.y / 2.0f)
							/ ptm;
					verts[3].x = ((-1 * size.x + border.x / 2.0f) * scale.x
							/ 2.0f + offset.x / 2.0f)
							/ ptm;
					verts[3].y = ((+size.y - border.y / 2.0f) * scale.y / 2.0f - offset.y / 2.0f)
							/ ptm;

				}
				shape.set(verts);
				fixture.shape = shape;
				body.createFixture(fixture);
			}
		} else {
			int count = fixtures.size();
			for (int k = 0; k < count; k++) {
				ArrayList<LHObject> curFixture = fixtures.get(k).arrayValue();

				int fixtureSize = curFixture.size();
				Vector2[] verts = new Vector2[fixtureSize];
				PolygonShape shape = new PolygonShape();
				int i = 0;

				for (int j = 0; j < fixtureSize; ++j) {
					String pointStr = curFixture.get(j).stringValue();

					CGPoint point = stringToCGPoint(pointStr);
					verts[i] = new Vector2(
							(point.x * (scale.x) + offset.x / 2.0f) / ptm,
							(point.y * (scale.y) - offset.y / 2.0f) / ptm);
					++i;
				}
				shape.set(verts);
				FixtureDef fixture = new FixtureDef();
				setFixtureDefPropertiesFromDictionary(spritePhysic, fixture);
				fixture.shape = shape;
				body.createFixture(fixture);
			}
		}

		setCustomAttributesForPhysics(spriteProp, body, ccsprite);
		body.setGravityScale(gravityScale);
		body.setSleepingAllowed(canSleep);
		body.setBullet(isBullet);
		body.setLinearVelocity(new Vector2(linearVelocity.x, linearVelocity.y));
		body.setAngularVelocity(angularVelocity);
		body.setLinearDamping(linearDamping);
		body.setAngularDamping(angularDamping);
		return body;
	}

	private void setCustomAttributesForPhysics(
			HashMap<String, LHObject> spriteProp, Body body, LHSprite ccsprite) {

	}

	private void setFixtureDefPropertiesFromDictionary(
			HashMap<String, LHObject> spritePhysic, FixtureDef shapeDef) {
		shapeDef.density = spritePhysic.get("Density").floatValue();
		shapeDef.friction = spritePhysic.get("Friction").floatValue();
		shapeDef.restitution = spritePhysic.get("Restitution").floatValue();
		shapeDef.filter.categoryBits = (short) spritePhysic.get("Category")
				.intValue();
		shapeDef.filter.maskBits = (short) spritePhysic.get("Mask").intValue();
		shapeDef.filter.groupIndex = (short) spritePhysic.get("Group")
				.intValue();
		if (spritePhysic.get("IsSensor") != null)
			shapeDef.isSensor = spritePhysic.get("IsSensor").boolValue();
		// in case we load a 1.3 level
		if (spritePhysic.get("IsSenzor") != null) {
			shapeDef.isSensor = spritePhysic.get("IsSenzor").boolValue();
		}
	}

	private void setCustomAttributesForNonPhysics(
			HashMap<String, LHObject> spriteProp, LHSprite ccsprite) {

	}

	private void createPhysicBoundariesHelper(World _world, CGPoint wbConv,
			CGPoint pos_offset) {
		if (!hasPhysicBoundaries()) {
			Log.w(TAG,
					"LevelHelper WARNING - Please create physic boundaries in LevelHelper in order to call method \"createPhysicBoundaries\"");
			return;
		}
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0.0f, 0.0f);
		Body wbBodyT = _world.createBody(bodyDef);
		Body wbBodyL = _world.createBody(bodyDef);
		Body wbBodyB = _world.createBody(bodyDef);
		Body wbBodyR = _world.createBody(bodyDef);
		{
			LHSprite spr = LHSprite.sprite();
			spr.setTag(wb.get("TagLeft").intValue());
			spr.setVisible(false);
			spr.setUniqueName("LHPhysicBoundarieLeft");
			spr.setBody(wbBodyL);
			wbBodyL.setUserData(spr);
			physicBoundariesInLevel.put("LHPhysicBoundarieLeft", spr);
		}
		{
			LHSprite spr = LHSprite.sprite();
			spr.setTag(wb.get("TagRight").intValue());
			spr.setVisible(false);
			spr.setUniqueName("LHPhysicBoundarieRight");
			spr.setBody(wbBodyR);
			wbBodyR.setUserData(spr);
			physicBoundariesInLevel.put("LHPhysicBoundarieRight", spr);
		}
		{
			LHSprite spr = LHSprite.sprite();
			spr.setTag(wb.get("TagTop").intValue());
			spr.setVisible(false);
			spr.setUniqueName("LHPhysicBoundarieTop");
			spr.setBody(wbBodyT);
			wbBodyT.setUserData(spr);
			physicBoundariesInLevel.put("LHPhysicBoundarieTop", spr);
		}
		{
			LHSprite spr = LHSprite.sprite();
			spr.setTag(wb.get("TagBottom").intValue());
			spr.setVisible(false);
			spr.setUniqueName("LHPhysicBoundarieBottom");
			spr.setBody(wbBodyB);
			wbBodyB.setUserData(spr);
			physicBoundariesInLevel.put("LHPhysicBoundarieBottom", spr);
		}

		wbBodyT.setSleepingAllowed(wb.get("CanSleep").boolValue());
		wbBodyL.setSleepingAllowed(wb.get("CanSleep").boolValue());
		wbBodyB.setSleepingAllowed(wb.get("CanSleep").boolValue());
		wbBodyR.setSleepingAllowed(wb.get("CanSleep").boolValue());

		CGRect rect = stringToCGRect(wb.get("WBRect").stringValue());
		CGSize winSize = CCDirector.sharedDirector().winSize();

		if (LH_SCENE_TESTER) {
			rect.origin.x += pos_offset.x;
			rect.origin.y += pos_offset.y;
		} else {
			rect.origin.x += pos_offset.x * 2.0f;
			rect.origin.y += pos_offset.y * 2.0f;
		}
		float ptm = LHSettings.sharedInstance().lhPtmRatio();
		{// TOP
			EdgeShape shape = new EdgeShape();
			Vector2 pos1 = new Vector2(rect.origin.x / ptm * wbConv.x,
					(winSize.height - rect.origin.y * wbConv.y) / ptm);
			Vector2 pos2 = new Vector2((rect.origin.x + rect.size.width)
					* wbConv.x / ptm, (winSize.height - rect.origin.y
					* wbConv.y)
					/ ptm);
			shape.set(pos1, pos2);
			FixtureDef fixture = new FixtureDef();
			setFixtureDefPropertiesFromDictionary(wb, fixture);
			fixture.shape = shape;
			wbBodyT.createFixture(fixture);
		}
		{// LEFT
			EdgeShape shape = new EdgeShape();
			Vector2 pos1 = new Vector2(rect.origin.x * wbConv.x / ptm,
					(winSize.height - rect.origin.y * wbConv.y) / ptm);
			Vector2 pos2 = new Vector2((rect.origin.x * wbConv.x) / ptm,
					(winSize.height - (rect.origin.y + rect.size.height)
							* wbConv.y)
							/ ptm);
			shape.set(pos1, pos2);
			FixtureDef fixture = new FixtureDef();
			setFixtureDefPropertiesFromDictionary(wb, fixture);
			fixture.shape = shape;
			wbBodyL.createFixture(fixture);
		}
		{// RIGHT
			EdgeShape shape = new EdgeShape();
			Vector2 pos1 = new Vector2((rect.origin.x + rect.size.width)
					* wbConv.x / ptm, (winSize.height - rect.origin.y
					* wbConv.y)
					/ ptm);

			Vector2 pos2 = new Vector2((rect.origin.x + rect.size.width)
					* wbConv.x / ptm,
					(winSize.height - (rect.origin.y + rect.size.height)
							* wbConv.y)
							/ ptm);
			shape.set(pos1, pos2);
			FixtureDef fixture = new FixtureDef();
			setFixtureDefPropertiesFromDictionary(wb, fixture);
			fixture.shape = shape;
			wbBodyR.createFixture(fixture);
		}
		{// BOTTOM
			EdgeShape shape = new EdgeShape();
			Vector2 pos1 = new Vector2(rect.origin.x * wbConv.x / ptm,
					(winSize.height - (rect.origin.y + rect.size.height)
							* wbConv.y)
							/ ptm);
			Vector2 pos2 = new Vector2((rect.origin.x + rect.size.width)
					* wbConv.x / ptm,
					(winSize.height - (rect.origin.y + rect.size.height)
							* wbConv.y)
							/ ptm);
			shape.set(pos1, pos2);
			FixtureDef fixture = new FixtureDef();
			setFixtureDefPropertiesFromDictionary(wb, fixture);
			fixture.shape = shape;
			wbBodyB.createFixture(fixture);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	// BEZIERS
	// //////////////////////////////////////////////////////////////////////////////
	private void createAllBeziers() {
		int count = lhBeziers.size();
		for (int i = 0; i < count; i++) {
			HashMap<String, LHObject> bezierDict = lhBeziers.get(i).dictValue();

			LHBezierNode node = LHBezierNode.nodeWithDictionary(bezierDict,
					_cocosLayer, _box2dWorld);
			String uniqueName = bezierDict.get("UniqueName").stringValue();
			if (node != null) {
				beziersInLevel.put(uniqueName, node);
				int z = bezierDict.get("ZOrder").intValue();
				_cocosLayer.addChild(node, z);
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	// ANIMATIONS
	// //////////////////////////////////////////////////////////////////////////////
	private void createAllAnimationsInfo() {
		int count = lhAnims.size();
		for (int i = 0; i < count; i++) {
			HashMap<String, LHObject> animInfo = lhAnims.get(i).dictValue();
			String uniqueAnimName = animInfo.get("UniqueName").stringValue();
			ArrayList<LHObject> framesInfo = animInfo.get("Frames")
					.arrayValue();
			boolean loop = animInfo.get("LoopForever").boolValue();
			float animSpeed = animInfo.get("Speed").floatValue();
			int repetitions = animInfo.get("Repetitions").intValue();
			boolean startAtLaunch = animInfo.get("StartAtLaunch").boolValue();
			String image = animInfo.get("Image").stringValue();
			LHAnimationNode animNode = LHAnimationNode
					.animationNodeWithUniqueName(uniqueAnimName);
			animNode.loop = loop;
			animNode.speed = animSpeed;
			animNode.repetitions = repetitions;
			animNode.startAtLaunch = startAtLaunch;
			animNode.setImageName(image);

			ArrayList<CGRect> frmsInfo = new ArrayList<CGRect>();
			int frameCount = framesInfo.size();
			for (int j = 0; j < frameCount; ++j) {
				HashMap<String, LHObject> rectDict = framesInfo.get(j)
						.dictValue();
				CGRect rect = stringToCGRect(rectDict.get("FrameRect")
						.stringValue());
				frmsInfo.add(rect);
			}
			animNode.setFramesInfo(frmsInfo);
			animationsInLevel.put(uniqueAnimName, animNode);
		}

	}

	private void addBatchNodesToLayer(CCLayer cocosLayer) {
		if (!LHSettings.sharedInstance().preloadBatchNodes())
			return;
		for (String key : batchNodesInLevel.keySet()) {
			LHBatch info = (LHBatch) batchNodesInLevel.get(key);
			cocosLayer.addChild(info.getSpriteSheet(), info.getZ(), 0);
		}
	}

	private void loadLevelHelperSceneFile(String levelFile) {
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

		if (CCDirector.gl != null) {
			CGRect color = stringToCGRect(scenePref.dictValue()
					.get("BackgroundColor").stringValue());
			CCDirector.gl.glClearColor(color.origin.x, color.origin.y,
					color.size.width, 1);
		}

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
			wb = dictionary.get("WBInfo").dictValue();
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
		// LHSettings.sharedInstance().imagePath(image));
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

	private Body physicBoundarieForKey(String key) {
		LHSprite spr = physicBoundariesInLevel.get(key);
		if (spr == null)
			return null;
		return spr.getBody();
	}

	private LHSprite spriteFromDictionary(HashMap<String, LHObject> spriteProp) {
		CGRect uv = stringToCGRect(spriteProp.get("UV").stringValue());
		String img = LHSettings.sharedInstance().imagePath(
				spriteProp.get("Image").stringValue());
		if (LHSettings.sharedInstance().shouldScaleImageOnRetina(img)) {
			uv.origin.x *= 2.0f;
			uv.origin.y *= 2.0f;
			uv.size.width *= 2.0f;
			uv.size.height *= 2.0f;
		}
		LHSprite ccsprite = LHSprite.spriteWithFile(img, uv);
		setSpriteProperties(ccsprite, spriteProp);
		return ccsprite;
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
