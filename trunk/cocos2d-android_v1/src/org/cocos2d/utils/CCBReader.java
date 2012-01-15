package org.cocos2d.utils;

import java.util.ArrayList;
import java.util.HashMap;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCLayerGradient;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.particlesystem.CCParticleSystem;
import org.cocos2d.particlesystem.CCQuadParticleSystem;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccColor4F;

public class CCBReader {

	public static int kCCBMemberVarAssignmentTypeNone = 0;
	public static int kCCBMemberVarAssignmentTypeDocumentRoot = 1;
	public static int kCCBMemberVarAssignmentTypeOwner = 2;

	public static CCScene sceneWithNodeGraphFromFile(String file) {
		return sceneWithNodeGraphFromFile(file, null);
	}

	public static CCScene sceneWithNodeGraphFromFile(String file, Object o) {
		CCNode node = nodeGraphFromFile(file);
		CCScene scene = CCScene.node();
		scene.addChild(node);
		return scene;
	}

	public static CCNode nodeGraphFromFile(String file) {
		return nodeGraphFromFile(file, null);
	}

	public static CCNode nodeGraphFromFile(String file, Object o) {
		HashMap<String, Object> dictionary = PlistParser.parse(file);
		return nodeGraphFromDictionary(dictionary, o);
	}

	public static CCNode nodeGraphFromDictionary(HashMap<String, Object> dict) {
		return nodeGraphFromDictionary(dict, null, "", null);
	}

	public static CCNode nodeGraphFromDictionary(HashMap<String, Object> dict,
			Object o) {
		return nodeGraphFromDictionary(dict, null, "", o);
	}

	@SuppressWarnings("unchecked")
	private static CCNode nodeGraphFromDictionary(HashMap<String, Object> dict,
			HashMap<String, Object> extraProps, String assetsDir, Object o) {
		assert (dict == null) : "WARNING! Trying to load invalid file type";
		String fileType = (String) dict.get("fileType");
		assert (fileType == null || fileType.equalsIgnoreCase("CocosBuilder")) : "WARNING! Trying to load invalid file type";
		int fileVersion = (Integer) dict.get("fileVersion");
		assert (fileVersion > 1) : "WARNING! Trying to load file made with a newer version of CocosBuilder, please update the CCBReader class";
		HashMap<String, Object> nodeGraph = (HashMap<String, Object>) dict
				.get("nodeGraph");
		return ccObjectFromDictionary(nodeGraph, extraProps, assetsDir, o);
	}

	public static CCNode ccObjectFromDictionary(
			HashMap<String, Object> nodeGraph,
			HashMap<String, Object> extraProps, String assetsDir, Object o) {
		return ccObjectFromDictionary(nodeGraph, extraProps, assetsDir, o, null);
	}

	public static Object createCustomClassWithName(String className) {
		if (className != null && !className.equals("")) {
			try {
				Class<?> c = Class.forName(className);
				return c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static CCNode ccObjectFromDictionary(
			HashMap<String, Object> nodeGraph,
			HashMap<String, Object> extraProps, String assetsDir, Object o,
			CCNode root) {
		String className = (String) nodeGraph.get("class");
		HashMap<String, Object> props = (HashMap<String, Object>) nodeGraph
				.get("properties");
		String customClass = (String) props.get("customClass");
		ArrayList<?> children = (ArrayList<?>) nodeGraph.get("children");
		if(extraProps != null) customClass = null;

		CCNode node = null;
		if (className.equalsIgnoreCase("CCParticleSystem")) {
			//暂时有问题
			String spriteFile = (String) props.get("spriteFile");
			CCParticleSystem sys = new CCQuadParticleSystem(256);
			sys.cleanup();
			sys.setTexture(CCTextureCache.sharedTextureCache().addImage(spriteFile));
			node = sys;
			setPropsForNode(node, props, extraProps);
			setPropsForParticleSystem((CCParticleSystem) node, props, extraProps);
		} else if (className.equalsIgnoreCase("CCMenuItemImage")) {
			String spriteFileNormal = assetsDir + props.get("spriteFileNormal");
			String spriteFileSelected = assetsDir
					+ props.get("spriteFileSelected");
			String spriteFileDisabled = assetsDir
					+ props.get("spriteFileDisabled");

			CCSprite spriteNormal = null;
			CCSprite spriteSelected = null;
			CCSprite spriteDisabled = null;
			
			String spriteSheetFile = (String) props.get("spriteFramesFile");
			if(spriteSheetFile != null && !spriteSheetFile.equals("")){
				spriteSheetFile = assetsDir + spriteSheetFile;
			}else{
				spriteNormal = CCSprite.sprite(spriteFileNormal);
				spriteSelected = CCSprite.sprite(spriteFileSelected);
				spriteDisabled = CCSprite.sprite(spriteFileDisabled);
			}
				
			CCNode target = null;
			String selector = null;
			if(extraProps == null){
				int targetType = intValFromDict(props, "target");
				if(targetType == kCCBMemberVarAssignmentTypeDocumentRoot)
					target = root;
				else if(targetType == kCCBMemberVarAssignmentTypeOwner)
					target = (CCNode) o;
				
				String selectorName = (String) props.get("selector");
				if(selectorName != null && !selectorName.equals("") && target != null){
					selector = selectorName;
				}
			}
			node = CCMenuItemImage.item(spriteNormal, spriteSelected, spriteDisabled, target, selector);
			
			setPropsForNode(node, props, extraProps);
			setPropsForMenuItem((CCMenuItem) node,props,extraProps);
			setPropsForMenuItemImage((CCMenuItemSprite)node, props, extraProps);
		} else if (className.equalsIgnoreCase("CCMenu")) {
			node = CCMenu.menu();
			setPropsForNode(node, props, extraProps);
			setPropsForLayer((CCLayer)node, props, extraProps);
		} else if (className.equalsIgnoreCase("CCLabelBMFont")) {
			String fontFile = assetsDir + props.get("fontFile");
			String str = (String) props.get("string");
			node = CCBitmapFontAtlas.bitmapFontAtlas(str, fontFile);
			if(node == null){
				node = CCBitmapFontAtlas.bitmapFontAtlas(str, "");
			}
			setPropsForNode(node, props, extraProps);
			setPropsForLabelBMFont((CCBitmapFontAtlas)node, props, extraProps);
		} else if (className.equalsIgnoreCase("CCSprite")) {
			String spriteFile = (String) props.get("spriteFile");
			String spriteSheetFile = (String) props.get("spriteFramesFile");
			if (spriteSheetFile != null && spriteSheetFile.equals("")) {
				CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(
						spriteSheetFile);
				node = CCSprite.sprite(CCSpriteFrameCache
						.sharedSpriteFrameCache().getSpriteFrame(spriteFile));
			} else {
				node = CCSprite.sprite(spriteFile);
			}
			if (node == null)
				return null;
			setPropsForNode(node, props, extraProps);
			setPropsForSprite((CCSprite) node, props, extraProps);
		} else if (className.equalsIgnoreCase("CCLayerGradient")) {
			//有点问题
			node = (CCLayerGradient) createCustomClassWithName(customClass);
			if(node == null){
				node = CCLayerGradient.node(new ccColor4B(255, 255, 0, 255));
			}
			setPropsForNode(node, props, extraProps);
			setPropsForLayer((CCLayer)node, props, extraProps);
			setPropsForLayerColor((CCColorLayer)node, props, extraProps);
			setPropsForLayerGradient((CCLayerGradient)node,props,extraProps);
		} else if (className.equalsIgnoreCase("CCLayerColor")) {
			node = (CCNode) createCustomClassWithName(customClass);
			if(node == null){
				node = CCColorLayer.node(new ccColor4B(255, 255, 0, 255));
			}
			setPropsForNode(node, props, extraProps);
			setPropsForLayer((CCLayer)node, props, extraProps);
			setPropsForLayerColor((CCColorLayer)node, props, extraProps);
		} else if (className.equalsIgnoreCase("CCLayer")) {
			node = (CCNode) createCustomClassWithName(customClass);
			if (node == null) {
				node = CCLayer.node();
			} else {
				if (!(node instanceof CCLayer)) {
					System.out
							.println("WARNING! %@ is not subclass of CCLayer");
					node = null;
				}
			}
			setPropsForNode(node, props, extraProps);
		} else if (className.equalsIgnoreCase("CCNode")) {
			node = (CCNode) createCustomClassWithName(customClass);
			if (node == null) {
				node = CCNode.node();
			}
			setPropsForNode(node, props, extraProps);
		} else {
			return null;
		}

		if (root == null)
			root = node;

		for (int i = 0; i < children.size(); i++) {
			HashMap<String, Object> childrenDict = (HashMap<String, Object>) children
					.get(i);
			CCNode child = ccObjectFromDictionary(childrenDict, extraProps,
					assetsDir, o, root);
			int zOrder = intValFromDict(
					(HashMap<String, Object>) childrenDict.get("properties"),
					"zOrder");
			if (child != null){
				node.addChild(child, zOrder);
			}
		}

		if (extraProps == null) {
			String assignmentName = (String) props
					.get("memberVarAssignmentName");
			int assignmentType = (Integer) props.get("memberVarAssignmentType");
			if (assignmentName != null && assignmentName.equals("")
					&& assignmentType != 0) {
				Object assignTo = null;
				if (assignmentType == kCCBMemberVarAssignmentTypeOwner)
					assignTo = o;
				else if (assignmentType == kCCBMemberVarAssignmentTypeDocumentRoot)
					assignTo = root;
				if (assignTo != null) {

				}

			}

		}
		return node;
	}

	/******* pragma mark Read properties from dictionary ************/

	public static int intValFromDict(HashMap<String, Object> dict, String key) {
		return (Integer) dict.get(key);
	}

	public static float floatValFromDict(HashMap<String, Object> dict,
			String key) {
		double val = (Double) dict.get(key);
		return (float) val;
	}

	public static boolean boolValFromDict(HashMap<String, Object> dict,
			String key) {
		return (Boolean) dict.get(key);
	}

	public static CGPoint pointValFromDict(HashMap<String, Object> dict,
			String key) {
		ArrayList<?> arr = (ArrayList<?>) dict.get(key);
		if (arr == null)
			return CGPoint.ccp(0, 0);

		double x = (Double) arr.get(0);
		double y = (Double) arr.get(1);
		return CGPoint.ccp((float) x, (float) y);
	}

	public static CGSize sizeValFromDict(HashMap<String, Object> dict,
			String key) {
		ArrayList<?> arr = (ArrayList<?>) dict.get(key);
		double w = (Double) arr.get(0);
		double h = (Double) arr.get(1);
		return CGSize.make((float) w, (float) h);
	}

	public static ccColor3B color3ValFromDict(HashMap<String, Object> dict,
			String key) {
		ArrayList<?> arr = (ArrayList<?>) dict.get(key);
		int r = (Integer) arr.get(0);
		int g = (Integer) arr.get(1);
		int b = (Integer) arr.get(2);
		return ccColor3B.ccc3(r, g, b);
	}

	public static ccColor4F color4fValFromDict(HashMap<String, Object> dict,
			String key) {
		ArrayList<?> arr = (ArrayList<?>) dict.get(key);
		double rr = (Double) arr.get(0);
		double gg = (Double) arr.get(1);
		double bb = (Double) arr.get(2);
		double aa = (Double) arr.get(3);
		return ccColor4F.ccc4FFromccc4B(new ccColor4B((int)rr, (int)gg, (int)bb, (int)aa));
	}

	public static ccBlendFunc blendFuncValFromDict(
			HashMap<String, Object> dict, String key) {
		ArrayList<?> arr = (ArrayList<?>) dict.get(key);
		int src = (Integer) arr.get(0);
		int dst = (Integer) arr.get(1);
		return new ccBlendFunc(src, dst);
	}

	/******* pragma mark Read properties from dictionary ************/
	/******* pragma mark Store extra properties (only used by editor) ********/

	public static void setPropsForNode(CCNode node,
			HashMap<String, Object> props, HashMap<String, Object> extraProps) {
		node.setPosition(pointValFromDict(props, "position"));
		if (!(node instanceof CCSprite) && !(node instanceof CCMenuItemImage)) {
			node.setContentSize(sizeValFromDict(props, "contentSize"));
		}
		node.setScaleX(floatValFromDict(props, "scaleX"));
		node.setScaleY(floatValFromDict(props, "scaleY"));
		node.setAnchorPoint(pointValFromDict(props, "anchorPoint"));
		node.setRotation(floatValFromDict(props, "rotation"));
		node.setRelativeAnchorPoint(boolValFromDict(props,
				"isRelativeAnchorPoint"));
		node.setVisible(boolValFromDict(props, "visible"));
		if (extraProps != null) {

		} else {
			node.setTag(intValFromDict(props, "tag"));
		}
	}

	public static void setPropsForLayer(CCLayer node,
			HashMap<String, Object> props, HashMap<String, Object> extraProps) {
		if (extraProps != null) {

		}
	}

	public static void setExtraProp(Object prop, String key,
			int tag, HashMap<String, Object> dict) {
		 @SuppressWarnings("unchecked")
		HashMap<String, Object> props = (HashMap<String, Object>) dict.get(tag);
		 if(props == null){
			 props = new HashMap<String, Object>();
			 dict.put(key, props);
		 }
		 props.put(key, prop);
	}

	public static void setPropsForSprite(CCSprite node,
			HashMap<String, Object> props, HashMap<String, Object> extraProps) {
		node.setOpacity(intValFromDict(props, "opacity"));
		node.setColor(color3ValFromDict(props, "color"));
		node.setFlipX(boolValFromDict(props, "flipX"));
		node.setFlipY(boolValFromDict(props, "flipY"));
		node.setBlendFunc(blendFuncValFromDict(props, "blendFunc"));
		if (extraProps != null) {

		}
	}

	public static void setPropsForParticleSystem(CCParticleSystem node,
			HashMap<String, Object> props, HashMap<String, Object> extraProps) {
		node.setEmitterMode(intValFromDict(props, "emitterMode"));
		node.setEmissionRate(floatValFromDict(props, "emissionRate"));
		node.setDuration(floatValFromDict(props, "duration"));
		node.setPosVar(pointValFromDict(props, "posVar"));
		//node.setTotalParticles(intValFromDict(props, "totalParticles"));
		node.setLife(floatValFromDict(props, "life"));
		node.setLifeVar(floatValFromDict(props, "lifeVar"));
		node.setStartSize(intValFromDict(props, "startSize"));
		node.setStartSizeVar(intValFromDict(props,"startSizeVar"));
		node.setEndSize(intValFromDict(props, "endSize"));
		node.setEndSizeVar(intValFromDict(props, "endSizeVar"));
		if(node instanceof CCQuadParticleSystem){
			node.setStartSpin(intValFromDict(props, "startSpin"));
			node.setStartSpinVar(intValFromDict(props, "startSpinVar"));
			node.setEndSpin(intValFromDict(props, "endSpin"));
			node.setEndSpinVar(intValFromDict(props, "endSpinVar"));
		}
		node.setStartColor(color4fValFromDict(props, "startColor"));
		node.setStartColorVar(color4fValFromDict(props, "startColorVar"));
		node.setEndColor(color4fValFromDict(props, "endColor"));
		node.setEndColorVar(color4fValFromDict(props, "endColorVar"));
		node.setBlendFunc(blendFuncValFromDict(props, "blendFunc"));
		if(intValFromDict(props, "emitterMode") == CCParticleSystem.kCCParticleModeGravity){
			node.setGravity(pointValFromDict(props, "gravity"));
			node.setAngle(intValFromDict(props, "angle"));
			node.setAngleVar(intValFromDict(props, "angleVar"));
			node.setSpeed(intValFromDict(props, "speed"));
			node.setSpeedVar(intValFromDict(props, "speedVar"));
			node.setTangentialAccel(intValFromDict(props, "tangentialAccel"));
			node.setTangentialAccelVar(intValFromDict(props, "tangentialAccelVar"));
			node.setRadialAccel(intValFromDict(props, "radialAccel"));
			node.setRadialAccelVar(intValFromDict(props, "radialAccelVar"));
		}else{
			node.setStartRadius(intValFromDict(props, "startRadius"));
			node.setStartRadiusVar(intValFromDict(props, "startRadiusVar"));
			node.setEndRadius(intValFromDict(props, "endRadius"));
			node.setEndRadiusVar(intValFromDict(props, "endRadiusVar"));
			node.setRotatePerSecond(intValFromDict(props, "rotatePerSecond"));
			node.setRotatePerSecondVar(intValFromDict(props, "rotatePerSecondVar"));
		}
		if(extraProps != null){
			setExtraProp(props.get("spriteFile"), "spriteFile", node.getTag(), extraProps);
		}
		node.setPositionType(CCParticleSystem.kCCPositionTypeGrouped);
		
	}
	
	
	public static void setPropsForMenuItem(CCMenuItem node,HashMap<String, Object> props, HashMap<String, Object> extraProps){
		node.setIsEnabled(boolValFromDict(props, "isEnabled"));
		if(extraProps != null){
			setExtraProp(props.get("selector"), "selector", node.getTag(), extraProps);
			setExtraProp(props.get("target"), "target", node.getTag(), extraProps);
			String spriteFramesFile = (String) props.get("spriteFramesFile");
			if(spriteFramesFile != null){
				setExtraProp(spriteFramesFile, "spriteFramesFile", node.getTag(), extraProps);
			}
		}
	}
	
	public static void setPropsForMenuItemImage(CCMenuItemSprite node,HashMap<String, Object> props, HashMap<String, Object> extraProps){
		if(extraProps != null){
			setExtraProp(props.get("spriteFileNormal"), "spriteFileNormal", node.getTag(), extraProps);
			setExtraProp(props.get("spriteFileSelected"), "spriteFileSelected", node.getTag(), extraProps);
			setExtraProp(props.get("spriteFileDisabled"), "spriteFileDisabled", node.getTag(), extraProps);
		}
	}
	
	public static void setPropsForMenu(CCMenu node, HashMap<String, Object> props, HashMap<String, Object> extraProps){
		if(extraProps != null){
		}
	}
	
	public static void setPropsForLabelBMFont(CCBitmapFontAtlas node, HashMap<String, Object> props, HashMap<String, Object> extraProps){
		node.setOpacity(intValFromDict(props, "opacity"));
		node.setColor(color3ValFromDict(props, "color"));
		if(extraProps != null){
			setExtraProp(props.get("fontFile"), "fontFile", node.getTag(), extraProps);
		}
	}
	
	public static void setPropsForLayerColor(CCColorLayer node,HashMap<String, Object> props, HashMap<String, Object> extraProps){
		node.setColor(color3ValFromDict(props, "color"));
		node.setOpacity(intValFromDict(props, "opacity"));
	}
	
	public static void setPropsForLayerGradient(CCLayerGradient node,HashMap<String, Object> props, HashMap<String, Object> extraProps){
		node.setStartColor(color3ValFromDict(props, "color"));
		node.setStartOpacity(intValFromDict(props, "opacity"));
		node.setEndColor(color3ValFromDict(props, "endColor"));
		node.setEndOpacity(intValFromDict(props, "endOpacity"));
		node.setVector(pointValFromDict(props, "vector"));
	}

	/******* pragma mark Store extra properties (only used by editor) ********/

}
