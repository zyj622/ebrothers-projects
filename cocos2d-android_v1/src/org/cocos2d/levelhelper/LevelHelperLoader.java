package org.cocos2d.levelhelper;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.GeometryUtil;

import android.util.FloatMath;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

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
	private HashMap<String, LHJoint> jointsInLevel;
	private HashMap<String, LHParallaxNode> parallaxesInLevel;
	private HashMap<String, LHBezierNode> beziersInLevel;
	private HashMap<String, LHBatch> batchNodesInLevel;

	private CGPoint safeFrame;
	private CGRect gameWorldRect;
	private CGPoint gravity;
	private LHObject wb;
	private CCLayer _cocosLayer;
	private World _box2dWorld;

	public LevelHelperLoader(String levelFile) {
		assert (levelFile != null && levelFile.length() != 0);
		lhBatchInfo = new HashMap<String, LHObject>();
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
				// WheelJointDef jointDef = new WheelJointDef();
				//
				// CGPoint axisPt =
				// stringToCGPoint(joint.get("Axis").stringValue());
				// Vector2 axis(axisPt.x, axisPt.y);
				// axis.Normalize();
				//
				// jointDef.motorSpeed = joint.get("MotorSpeed").floatValue();
				// //Usually in radians per second. ?????
				// jointDef.maxMotorTorque =
				// joint.get("MaxTorque").floatValue(); //Usually in N-m. ?????
				// jointDef.enableMotor = joint.get("EnableMotor").boolValue();
				// jointDef.frequencyHz = joint.get("Frequency").floatValue();
				// jointDef.dampingRatio = joint.get("Damping").floatValue();
				//
				// jointDef.initialize(bodyA, bodyB, posA, axis);
				// jointDef.collideConnected = collideConnected;
				//
				// if(world != null)
				// {
				// boxJoint = (b2WheelJoint)world.CreateJoint(&jointDef);
				// }
			}
				break;
			case LHJoint.LH_WELD_JOINT: {
				WeldJointDef jointDef = new WeldJointDef();
				// TODO jointDef.frequencyHz =
				// joint.get("Frequency").floatValue();
				// TODO jointDef.dampingRatio =
				// joint.get("Damping").floatValue();
				jointDef.initialize(bodyA, bodyB, posA);
				jointDef.collideConnected = collideConnected;
				if (world != null) {
					boxJoint = world.createJoint(jointDef);
				}
			}
				break;

			case LHJoint.LH_ROPE_JOINT: // NOT WORKING YET AS THE BOX2D JOINT
										// FOR THIS
				// TYPE IS A TEST JOINT
			{

				// b2RopeJointDef jointDef;
				//
				// jointDef.localAnchorA = bodyA.GetPosition();
				// jointDef.localAnchorB = bodyB.GetPosition();
				// jointDef.bodyA = bodyA;
				// jointDef.bodyB = bodyB;
				// jointDef.maxLength = joint.get("MaxLength").floatValue();
				// jointDef.collideConnected = collideConnected;
				//
				// if(world != null)
				// {
				// boxJoint = (b2RopeJoint)world.CreateJoint(&jointDef);
				// }
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
				.get("UniqueName").stringValue(), tag, type);
		// levelJoint.getTag() = tag;
		// levelJoint.type = (LH_JOINT_TYPE)type;
		// levelJoint.joint = boxJoint;
		// TODO boxJoint.setUserData(levelJoint);
		return levelJoint;
	}

	private LHJoint jointWithUniqueName(String stringValue) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createParallaxes() {
		// TODO Auto-generated method stub

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

	private LHBatch batchNodeForFile(String stringValue) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createAnimationFromDictionary(
			HashMap<String, LHObject> spriteProp, LHSprite ccsprite) {
		// TODO Auto-generated method stub

	}

	private void createPathOnSprite(LHSprite ccsprite,
			HashMap<String, LHObject> spriteProp) {
		// TODO Auto-generated method stub

	}

	private LHSprite spriteWithBatchFromDictionary(
			HashMap<String, LHObject> spriteProp, LHBatch bNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private Body b2BodyFromDictionary(HashMap<String, LHObject> physicProp,
			HashMap<String, LHObject> spriteProp, LHSprite ccsprite,
			World _box2dWorld2) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setCustomAttributesForNonPhysics(
			HashMap<String, LHObject> spriteProp, LHSprite ccsprite) {
		// TODO Auto-generated method stub

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
		// LHSettings::sharedInstance().imagePath(image.c_str()).c_str());
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
