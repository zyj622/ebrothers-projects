package org.cocos2d.levelhelper.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.config.ccMacros;
import org.cocos2d.levelhelper.LHObject;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.util.FloatMath;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class LHParallaxNode extends CCNode {
	private boolean isContinuous;
	private int direction;
	private float speed;
	private boolean paused;
	private CGPoint lastPosition;
	private String uniqueName;
	private MoveEndListener moveEndCallBack;
	private CGSize winSize;
	private int screenNumberOnTheRight;
	private int screenNumberOnTheLeft;
	private int screenNumberOnTheTop;
	private int screenNumberOnTheBottom;
	private ArrayList<LHParallaxPointObject> sprites;
	private LHSprite followedSprite;
	private CGPoint lastFollowedSpritePosition;
	private boolean followChangeX;
	private boolean followChangeY;

	public LHParallaxNode() {
		sprites = new ArrayList<LHParallaxPointObject>();
	}

	public boolean initWithDictionary(HashMap<String, LHObject> parallaxDict) {

		if (null == parallaxDict)
			return false;

		followedSprite = null;
		isContinuous = parallaxDict.get("ContinuousScrolling").boolValue();
		direction = parallaxDict.get("Direction").intValue();
		speed = parallaxDict.get("Speed").floatValue();
		lastPosition = CGPoint.make(-100, -100);
		paused = false;
		winSize = CCDirector.sharedDirector().winSize();
		screenNumberOnTheRight = 1;
		screenNumberOnTheLeft = 0;
		screenNumberOnTheTop = 0;
		moveEndCallBack = null;
		uniqueName = parallaxDict.get("UniqueName").stringValue();
		if (!isContinuous)
			speed = 1.0f;

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public static LHParallaxNode nodeWithDictionary(
			HashMap<String, LHObject> properties) {
		LHParallaxNode pobNode = new LHParallaxNode();
		if (pobNode != null && pobNode.initWithDictionary(properties)) {
			return pobNode;
		}
		return null;
	}

	public boolean getIsContinuous() {
		return isContinuous;
	}

	public int getDirection() {
		return direction;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float s) {
		speed = s;
	}

	public boolean getIsPaused() {
		return paused;
	}

	public void setPaused(boolean pause) {
		paused = pause;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void addChild(LHSprite sprite, CGPoint ratio) {
		assert (sprite != null) : "Argument must be non-nil";

		LHParallaxPointObject obj = LHParallaxPointObject
				.pointWithCCPoint(ratio);
		obj.ccsprite = sprite;
		sprite.setParallaxNode(this);
		obj.body = sprite.getBody();
		obj.position = sprite.getPosition();
		obj.offset = sprite.getPosition();
		obj.initialPosition = sprite.getPosition();
		sprites.add(obj);

		int scrRight = (int) (obj.initialPosition.x / winSize.width);

		if (screenNumberOnTheRight <= scrRight)
			screenNumberOnTheRight = scrRight + 1;

		int scrLeft = (int) (obj.initialPosition.x / winSize.width);

		if (screenNumberOnTheLeft >= scrLeft)
			screenNumberOnTheLeft = scrLeft - 1;

		int scrTop = (int) (obj.initialPosition.y / winSize.height);

		if (screenNumberOnTheTop <= scrTop)
			screenNumberOnTheTop = scrTop + 1;

		int scrBottom = (int) (obj.initialPosition.y / winSize.height);

		if (screenNumberOnTheBottom >= scrBottom)
			screenNumberOnTheBottom = scrBottom - 1;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void removeChild(LHSprite sprite) {
		if (null == sprite)
			return;
		int size = sprites.size();
		for (int i = 0; i < size; ++i) {
			LHParallaxPointObject pt = sprites.get(i);
			if (pt.ccsprite.equals(sprite)) {
				sprites.remove(pt);
				return;
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void registerSpriteHasMovedToEndListener(
			MoveEndListener _moveEndCallBack) {
		moveEndCallBack = _moveEndCallBack;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public ArrayList<LHSprite> spritesInNode() {
		ArrayList<LHSprite> sprs = new ArrayList<LHSprite>();
		for (LHParallaxPointObject pt : sprites) {
			if (null != pt.ccsprite)
				sprs.add((LHSprite) pt.ccsprite);
		}
		return sprs;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public ArrayList<Body> bodiesInNode() {
		ArrayList<Body> sprs = new ArrayList<Body>();
		for (LHParallaxPointObject object : sprites) {
			if (null != object.body)
				sprs.add(object.body);
		}
		return sprs;
	}

	// //////////////////////////////////////////////////////////////////////////////
	private void setPositionOnPointWithOffset(CGPoint pos,
			LHParallaxPointObject point, CGPoint offset) {
		if (!isContinuous) {
			if (point.ccsprite != null) {
				point.ccsprite.setPosition(pos);
				if (point.body != null) {
					float angle = point.ccsprite.getRotation();
					point.body.setAwake(true);
					point.body.setTransform(new Vector2(pos.x
							/ LHSettings.sharedInstance().lhPtmRatio(), pos.y
							/ LHSettings.sharedInstance().lhPtmRatio()),
							ccMacros.CC_DEGREES_TO_RADIANS(-angle));
				}
			}
		} else {

			if (point.ccsprite != null) {

				CGPoint newPos = CGPoint.make(point.ccsprite.getPosition().x
						- offset.x, point.ccsprite.getPosition().y - offset.y);
				point.ccsprite.setPosition(newPos);

				if (point.body != null) {

					float angle = point.ccsprite.getRotation();
					point.body.setTransform(
							new Vector2(newPos.x
									/ LHSettings.sharedInstance().lhPtmRatio(),
									newPos.y
											/ LHSettings.sharedInstance()
													.lhPtmRatio()), ccMacros
									.CC_DEGREES_TO_RADIANS(-angle));
				}

			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	private CGSize getBounds(float rw, float rh, float radians) {
		float x1 = -rw / 2;
		float x2 = rw / 2;
		float x3 = rw / 2;
		float x4 = -rw / 2;
		float y1 = rh / 2;
		float y2 = rh / 2;
		float y3 = -rh / 2;
		float y4 = -rh / 2;

		float x11 = x1 * FloatMath.cos(radians) + y1 * FloatMath.sin(radians);
		float y11 = -x1 * FloatMath.sin(radians) + y1 * FloatMath.cos(radians);
		float x21 = x2 * FloatMath.cos(radians) + y2 * FloatMath.sin(radians);
		float y21 = -x2 * FloatMath.sin(radians) + y2 * FloatMath.cos(radians);
		float x31 = x3 * FloatMath.cos(radians) + y3 * FloatMath.sin(radians);
		float y31 = -x3 * FloatMath.sin(radians) + y3 * FloatMath.cos(radians);
		float x41 = x4 * FloatMath.cos(radians) + y4 * FloatMath.sin(radians);
		float y41 = -x4 * FloatMath.sin(radians) + y4 * FloatMath.cos(radians);

		float x_min = Math.min(Math.min(x11, x21), Math.min(x31, x41));
		float x_max = Math.max(Math.max(x11, x21), Math.max(x31, x41));

		float y_min = Math.min(Math.min(y11, y21), Math.min(y31, y41));
		float y_max = Math.max(Math.max(y11, y21), Math.max(y31, y41));

		return CGSize.make(x_max - x_min, y_max - y_min);
	}

	// //////////////////////////////////////////////////////////////////////////////
	private void repositionPoint(LHParallaxPointObject point) {
		CGSize spriteContentSize = point.ccsprite.getContentSize();

		float angle = point.ccsprite.getRotation();
		float rotation = ccMacros.CC_DEGREES_TO_RADIANS(angle);
		float scaleX = point.ccsprite.getScaleX();
		float scaleY = point.ccsprite.getScaleY();

		CGSize contentSize = getBounds(spriteContentSize.width,
				spriteContentSize.height, rotation);
		switch (direction) {
		case 1: // right to left
		{
			if (point.ccsprite.getPosition().x + contentSize.width / 2.0f
					* scaleX <= 0) {
				float difX = point.ccsprite.getPosition().x + contentSize.width
						/ 2.0f * scaleX;

				point.setOffset(CGPoint.ccp(winSize.width
						* screenNumberOnTheRight - point.ratio.x * speed
						- contentSize.width / 2.0f * scaleX + difX,
						point.offset.y));

				if (null != point.ccsprite) {
					CGPoint newPos = CGPoint.make(point.offset.x,
							point.ccsprite.getPosition().y);
					point.ccsprite.setPosition(newPos);

					if (point.body != null) {
						angle = point.ccsprite.getRotation();
						point.body.setTransform(new Vector2(newPos.x
								/ LHSettings.sharedInstance().lhPtmRatio(),
								newPos.y
										/ LHSettings.sharedInstance()
												.lhPtmRatio()), ccMacros
								.CC_DEGREES_TO_RADIANS(-angle));
					}
				}
				if (null != moveEndCallBack) {
					moveEndCallBack.onMoveEnd(point.ccsprite);
				}
			}
		}
			break;
		case 0:// left to right
		{
			if (point.ccsprite.getPosition().x - contentSize.width / 2.0f
					* scaleX >= winSize.width) {
				float difX = point.ccsprite.getPosition().x - contentSize.width
						/ 2.0f * scaleX - winSize.width;

				point.setOffset(CGPoint.ccp(winSize.width
						* screenNumberOnTheLeft + point.ratio.x * speed
						+ contentSize.width / 2.0f * scaleX + difX,
						point.offset.y));
				if (null != point.ccsprite) {
					CGPoint newPos = CGPoint.make(point.offset.x,
							point.ccsprite.getPosition().y);
					point.ccsprite.setPosition(newPos);
					if (point.body != null) {
						angle = point.ccsprite.getRotation();
						point.body.setTransform(new Vector2(newPos.x
								/ LHSettings.sharedInstance().lhPtmRatio(),
								newPos.y
										/ LHSettings.sharedInstance()
												.lhPtmRatio()), ccMacros
								.CC_DEGREES_TO_RADIANS(-angle));
					}
				}
				if (null != moveEndCallBack) {
					moveEndCallBack.onMoveEnd(point.ccsprite);
				}
			}
		}
			break;

		case 2:// up to bottom
		{
			if (point.ccsprite.getPosition().y + contentSize.height / 2.0f
					* scaleY <= 0) {
				float difY = point.ccsprite.getPosition().y
						+ contentSize.height / 2.0f * scaleY;

				point.setOffset(CGPoint.ccp(point.offset.x, winSize.height
						* screenNumberOnTheTop - point.ratio.y * speed
						- contentSize.height / 2.0f * scaleY + difY));
				if (null != point.ccsprite) {
					CGPoint newPos = CGPoint.make(
							point.ccsprite.getPosition().x, point.offset.y);
					point.ccsprite.setPosition(newPos);

					if (point.body != null) {

						angle = point.ccsprite.getRotation();
						point.body.setTransform(new Vector2(newPos.x
								/ LHSettings.sharedInstance().lhPtmRatio(),
								newPos.y
										/ LHSettings.sharedInstance()
												.lhPtmRatio()), ccMacros
								.CC_DEGREES_TO_RADIANS(-angle));
					}
				}
				if (null != moveEndCallBack) {
					moveEndCallBack.onMoveEnd(point.ccsprite);
				}
			}
		}
			break;

		case 3:// bottom to top
		{
			if (point.ccsprite.getPosition().y - contentSize.height / 2.0f
					* scaleY >= winSize.height) {
				float difY = point.ccsprite.getPosition().y
						- contentSize.height / 2.0f * scaleY - winSize.height;
				point.setOffset(CGPoint.ccp(point.offset.x, winSize.height
						* screenNumberOnTheBottom + point.ratio.y * speed
						+ contentSize.height / 2.0f * scaleY + difY));
				if (null != point.ccsprite) {
					CGPoint newPos = CGPoint.make(
							point.ccsprite.getPosition().x, point.offset.y);
					point.ccsprite.setPosition(newPos);
					if (point.body != null) {
						angle = point.ccsprite.getRotation();
						point.body.setTransform(new Vector2(newPos.x
								/ LHSettings.sharedInstance().lhPtmRatio(),
								newPos.y
										/ LHSettings.sharedInstance()
												.lhPtmRatio()), ccMacros
								.CC_DEGREES_TO_RADIANS(-angle));
					}
				}
				if (null != moveEndCallBack) {
					moveEndCallBack.onMoveEnd(point.ccsprite);
				}
			}
		}
			break;
		default:
			break;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	@Override
	public void setPosition(CGPoint newPosition) {
		super.setPosition(newPosition);
		visit();
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void setFollowSprite(LHSprite sprite, boolean changeXPosition,
			boolean changeYPosition) {
		if (null == sprite) {
			if (null != followedSprite)
				followedSprite.parallaxFollowingThisSprite = null;
		}
		followedSprite = sprite;
		followChangeX = changeXPosition;
		followChangeY = changeYPosition;
		if (null != sprite) {
			lastFollowedSpritePosition = sprite.getPosition();
			sprite.parallaxFollowingThisSprite = this;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void visit() {
		if (LHSettings.sharedInstance().levelPaused()) // level is paused
			return;

		if (paused) // this parallax is paused
			return;

		if (null != followedSprite) {
			float deltaFX = lastFollowedSpritePosition.x
					- followedSprite.getPosition().x;
			float deltaFY = lastFollowedSpritePosition.y
					- followedSprite.getPosition().y;
			lastFollowedSpritePosition = followedSprite.getPosition();

			CGPoint lastPosition = this.getPosition();
			if (followChangeX && !followChangeY) {
				super.setPosition(CGPoint.ccp(lastPosition.x + deltaFX,
						lastPosition.y));
			} else if (!followChangeX && followChangeY) {
				super.setPosition(CGPoint.ccp(lastPosition.x, lastPosition.y
						+ deltaFY));
			} else if (followChangeX && followChangeY) {
				super.setPosition(CGPoint.ccp(lastPosition.x + deltaFX,
						lastPosition.y + deltaFY));
			}
		}

		CGPoint pos = getPosition();
		if (!CGPoint.equalToPoint(pos, lastPosition) || isContinuous) {
			final int count = sprites.size();
			for (int k = 0; k < count; ++k) {
				LHParallaxPointObject point = sprites.get(k);

				float x = pos.x * point.ratio.x + point.offset.x;
				float y = pos.y * point.ratio.y + point.offset.y;

				int i = -1; // direction left to right //bottom to up
				if (direction == 1 || direction == 2) // right to left //up to
														// bottom
					i = 1;

				setPositionOnPointWithOffset(
						CGPoint.make(x, y),
						point,
						CGPoint.make(i * point.ratio.x * speed, i
								* point.ratio.y * speed));

				if (isContinuous) {
					repositionPoint(point);

					point.setOffset(CGPoint.ccp(point.offset.x + i
							* point.ratio.x * speed, point.offset.y + i
							* point.ratio.y * speed));

				}
			}
			lastPosition = pos;
		}
	}

	static class LHParallaxPointObject {
		public CGPoint position;
		public CGPoint ratio;
		public CGPoint offset;
		public CGPoint initialPosition;
		public CCNode ccsprite; // weak ref
		public Body body; // weak ref

		boolean initWithCCPoint(CGPoint point) {
			ratio = point;
			return true;
		}

		void setOffset(CGPoint pt) {
			offset = pt;
		}

		static LHParallaxPointObject pointWithCCPoint(CGPoint point) {

			LHParallaxPointObject pobPoint = new LHParallaxPointObject();
			if (pobPoint != null && pobPoint.initWithCCPoint(point)) {
				return pobPoint;
			}
			return null;
		}
	}

	public interface MoveEndListener {
		public void onMoveEnd(CCNode sprite);
	}

}
