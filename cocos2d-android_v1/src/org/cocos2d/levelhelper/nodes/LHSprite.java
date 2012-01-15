package org.cocos2d.levelhelper.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.config.ccMacros;
import org.cocos2d.levelhelper.LevelHelperLoader;
import org.cocos2d.levelhelper.nodes.LHPathNode.PathNodeNotifier;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import android.graphics.Bitmap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class LHSprite extends CCSprite {
	private Body body; // week ptr
	private String uniqueName;
	private HashMap<String, Object> customUserValues;
	@SuppressWarnings("unused")
	private int currentFrame;
	private LHAnimationNode animation;
	private LHPathNode pathNode;
	private LHParallaxNode parallaxNode;
	// used for the joints in case you create a level with SD graphics using
	// ipad template
	private CGSize realScale;
	private CCSpriteSheet spriteSheet;

	public LHParallaxNode parallaxFollowingThisSprite;

	public static LHSprite sprite() {
		return new LHSprite();
	}

	public LHSprite(CCTexture2D texture) {
		super(texture);
	}

	public LHSprite(CCTexture2D texture, CGRect rect) {
		super(texture, rect);
	}

	public LHSprite(CCSpriteFrame spriteFrame) {
		super(spriteFrame);
	}

	public LHSprite(String spriteFrameName, boolean isFrame) {
		super(spriteFrameName, isFrame);
	}

	public LHSprite(String filepath) {
		super(filepath);
	}

	public LHSprite() {
		super();
	}

	public LHSprite(String filepath, CGRect rect) {
		super(filepath, rect);
	}

	public LHSprite(Bitmap image, String key) {
		super(image, key);
	}

	public LHSprite(CCSpriteSheet spritesheet, CGRect rect) {
		super(spritesheet, rect);
	}

	public void setRealScale(CGSize _realScale) {
		realScale = CGSize.make(_realScale.width, _realScale.height);
	}

	public CGSize getRealScale() {
		return realScale;
	}

	// INFO
	public void setUniqueName(String name) {
		uniqueName = name;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setBody(Body _body) {
		assert (_body != null);
		body = _body;
	}

	public Body getBody() {
		return body;
	}

	public boolean removeBodyFromWorld() {
		if (body != null) {
			World _world = body.getWorld();
			if (_world != null) {
				_world.destroyBody(body);
				body = null;
				return true;
			}
		}
		return false;
	}

	// ANIMATION
	public void setAnimation(LHAnimationNode anim) {
		animation = anim;
		if (anim != null) {
			anim.setAnimationTexturePropertiesOnSprite(this);
			setFrame(0);
		}
	}

	public LHAnimationNode getAnimation() {
		return animation;
	}

	public String getAnimationName() {
		if (animation != null)
			return animation.getUniqueName();
		return "";
	}

	public int getNumberOfFrames() {
		if (animation != null)
			return animation.getNumberOfFrames();
		return -1;
	}

	public void setFrame(int frmNo) {
		if (animation == null)
			return;
		animation.setFrame(frmNo, this);
		currentFrame = frmNo;
	}

	public int getCurrentFrame() {
		if (animation != null) {
			ArrayList<CCSpriteFrame> frames = animation.getFrames();
			int count = frames.size();
			for (int i = 0; i < count; ++i) {
				CCSpriteFrame frame = frames.get(i);
				if (CGRect.equalToRect(frame.getRect(), getTextureRect())) {
					return i;
				}
			}
		}
		return 0;
	}

	public void stopAnimation() {
		stopAction(LevelHelperLoader.LH_ANIM_ACTION_TAG);
		setAnimation(null);
	}

	// PARALLAX
	public LHParallaxNode getParallaxNode() {
		return parallaxNode;
	}

	public void setParallaxNode(LHParallaxNode node) {
		parallaxNode = node;
	}

	// PATH
	public void setPathNode(LHPathNode node) {
		assert (node != null);
		pathNode = node;
	}

	public LHPathNode getPathNode() {
		return pathNode;
	}

	// will remove the path node if any - sprite will no longer move on a path
	public void cancelPathMovement() {
		if (pathNode != null) {
			pathNode.removeFromParentAndCleanup(true);
			pathNode = null;
		}
	}

	public void pausePathMovement(boolean pauseStatus) {
		if (pathNode != null) {
			pathNode.setPaused(pauseStatus);
		}
	}

	public void registerNotifierOnPathEndPoints(PathNodeNotifier notifier) {
		if (pathNode == null)
			return;
		pathNode.setNotifer(notifier);
	}

	// USER INFO
	public void setCustomValue(String key, Object value) {
		assert (key != null);
		assert (value != null);
		customUserValues.put(key, value);
	}

	public void getCustomValueWithKey(String key) {
		customUserValues.get(key);
	}

	// TRANSFORMATIONS
	// The following method will transform the physic body also - if any
	public void transformPosition(CGPoint pos) {
		setPosition(pos);
		if (body != null) {
			Vector2 boxPosition = LevelHelperLoader.pointsToMeters(pos);
			float angle = ccMacros.CC_DEGREES_TO_RADIANS(-1 * getRotation());
			body.setTransform(boxPosition, angle);
		}
	}

	public void transformRotation(float rot) {
		setRotation(rot);
		if (body != null) {
			Vector2 boxPosition = LevelHelperLoader
					.pointsToMeters(getPosition());
			float angle = ccMacros.CC_DEGREES_TO_RADIANS(-1 * rot);
			body.setTransform(boxPosition, angle);
		}
	}

	public static LHSprite spriteWithTexture(CCTexture2D pTexture) {
		return new LHSprite(pTexture);
	}

	public static LHSprite spriteWithTexture(CCTexture2D pTexture, CGRect rect) {
		return new LHSprite(pTexture, rect);
	}

	public static LHSprite spriteWithFile(String pszFileName) {
		return new LHSprite(pszFileName);
	}

	public static LHSprite spriteWithFile(String pszFileName, CGRect rect) {
		return new LHSprite(pszFileName, rect);
	}

	public static LHSprite spriteWithSpriteFrame(CCSpriteFrame pSpriteFrame) {
		return new LHSprite(pSpriteFrame);
	}

	public static LHSprite spriteWithSpriteFrameName(String pszSpriteFrameName) {
		return new LHSprite(pszSpriteFrameName);
	}

	public static LHSprite spriteWithSpriteSheet(CCSpriteSheet spriteSheet,
			CGRect rect) {
		return new LHSprite(spriteSheet, rect);
	}

	public CCSpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public void setSpriteSheet(CCSpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
}
