package org.cocos2d.levelhelper.nodes;

import java.util.ArrayList;
import java.util.Collections;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LHPathNode extends CCNode {
	private LHSprite ccsprite; // week ptr
	private Body body; // week ptr
	private String uniqueName;
	private ArrayList<CGPoint> pathPoints;
	private float speed;
	private float interval;
	private boolean startAtEndPoint;
	private boolean isCyclic;
	private boolean restartOtherEnd;
	private int axisOrientation; // 0 NO ORIENTATION 1 X 2 Y
	private boolean flipX;
	private boolean flipY;
	private int currentPoint;
	private float elapsed;
	private boolean paused;
	private float initialAngle;
	private CGPoint prevPathPosition;
	private float m_time;
	private boolean isLine;
	private PathNodeNotifier pathNotifier;

	public LHPathNode() {
		speed = 0.2f;
		interval = 0.01f;
		paused = false;
		startAtEndPoint = false;
		isCyclic = false;
		restartOtherEnd = false;
		axisOrientation = 0;
		flipX = false;
		flipY = false;
		ccsprite = null;
		body = null;
		uniqueName = "";
		currentPoint = 0;
		isLine = true;
		pathNotifier = null;
		m_time = System.currentTimeMillis() / 1000f;
		elapsed = 0.0f;
	}

	public boolean getIsCyclic() {
		return isCyclic;
	}

	public void setIsCyclic(boolean b) {
		isCyclic = b;
	}

	public boolean getRestartOtherEnd() {
		return restartOtherEnd;
	}

	public void setRestartOtherEnd(boolean r) {
		restartOtherEnd = r;
	}

	public int getAxisOrientation() {
		return axisOrientation;
	}

	public void setAxisOrientation(int a) {
		axisOrientation = a;
	}

	boolean getPaused() {
		return paused;
	}

	public void setPaused(boolean p) {
		paused = p;
	}

	public boolean getIsLine() {
		return isLine;
	}

	public void setIsLine(boolean l) {
		isLine = l;
	}

	public boolean getFlipX() {
		return flipX;
	}

	public void setFlipX(boolean x) {
		flipX = x;
	}

	public boolean getFlipY() {
		return flipY;
	}

	public void setFlipY(boolean y) {
		flipY = y;
	}

	public void setUniqueName(String name) {
		uniqueName = name;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setNotifer(PathNodeNotifier _pathNotifier) {
		pathNotifier = _pathNotifier;
	}

	public boolean initPathWithPoints(ArrayList<CGPoint> points) {
		pathPoints = points;
		return true;
	}

	public void setSprite(LHSprite sprite) {
		assert (sprite != null) : "Sprite must not be nil";
		ccsprite = sprite;
		initialAngle = ccsprite.getRotation();
		ccsprite.setPathNode(this);
		if ((int) pathPoints.size() > 0)
			prevPathPosition = pathPoints.get(0);
	}

	public void setSpeed(float value) {
		speed = value;
		interval = speed / (pathPoints.size() - 1);
	}

	public float getSpeed() {
		return speed;
	}

	public void setBody(Body _body) {
		body = _body;
	}

	public static LHPathNode nodePathWithPoints(ArrayList<CGPoint> points) {
		LHPathNode pobNode = new LHPathNode();
		pobNode.initPathWithPoints(points);
		return pobNode;
	}

	public void setStartAtEndPoint(boolean val) {
		startAtEndPoint = val;
		if (startAtEndPoint) {
			Collections.reverse(pathPoints);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	private float rotationDegreeFromPoint(CGPoint endPoint, CGPoint startPoint) {
		float rotateDegree = (float) (Math.atan2(
				Math.abs(endPoint.x - startPoint.x),
				Math.abs(endPoint.y - startPoint.y)) * 180.0f / Math.PI);
		if (endPoint.y >= startPoint.y) {
			if (endPoint.x >= startPoint.x) {
				rotateDegree = 180.0f + rotateDegree;
			} else {
				rotateDegree = 180.0f - rotateDegree;
			}
		} else {
			if (endPoint.x <= startPoint.x) {
			} else {
				rotateDegree = 360.0f - rotateDegree;
			}
		}
		return rotateDegree;
	}

	@Override
	public void visit(GL10 gl) {
		// level is paused
		if (LHSettings.sharedInstance().levelPaused() || paused) {
			float convertedTime = System.currentTimeMillis() / 1000f;
			elapsed += convertedTime - m_time;
			m_time = convertedTime;
			return;
		}

		if (null == ccsprite)
			return;

		if (0 == pathPoints.size())
			return;

		CGPoint startPosition = pathPoints.get(currentPoint);

		int previousPoint = currentPoint - 1;
		if (previousPoint < 0) {
			previousPoint = 0;
		}

		CGPoint prevPosition = pathPoints.get(previousPoint);
		CGPoint endPosition = startPosition;

		float startAngle = rotationDegreeFromPoint(startPosition, prevPosition);
		if (currentPoint == 0)
			startAngle = initialAngle + 270;

		float endAngle = startAngle;

		if ((currentPoint + 1) < (int) pathPoints.size()) {
			endPosition = pathPoints.get(currentPoint + 1);
			endAngle = rotationDegreeFromPoint(endPosition, startPosition);
		} else {
			if (isCyclic) {
				if (!restartOtherEnd)
					Collections.reverse(pathPoints);

				if (flipX) {
					ccsprite.setFlipX(!ccsprite.flipX_);
				}

				if (flipY) {
					ccsprite.setFlipY(!ccsprite.flipY_);
				}

				currentPoint = -1;
			}

			if (null != pathNotifier) {
				pathNotifier.onNotify(ccsprite);
				if (!isCyclic)
					paused = true;
			}
		}

		if (axisOrientation == 1)
			startAngle += 90.0f;
		if (axisOrientation == 1)
			endAngle += 90.0f;

		if (startAngle > 360)
			startAngle -= 360;
		if (endAngle > 360)
			endAngle -= 360;

		float t = Math.min(1, elapsed / interval);

		CGPoint deltaP = CGPoint.ccpSub(endPosition, startPosition);

		CGPoint newPos = CGPoint.ccp((startPosition.x + deltaP.x * t),
				(startPosition.y + deltaP.y * t));

		if (startAngle > 270 && startAngle < 360 && endAngle > 0
				&& endAngle < 90) {
			startAngle -= 360;
		}

		if (startAngle > 0 && startAngle < 90 && endAngle < 360
				&& endAngle > 270) {
			startAngle += 360;
		}

		float deltaA = endAngle - startAngle;
		float newAngle = startAngle + deltaA * t;

		if (newAngle > 360)
			newAngle -= 360;

		if (null != ccsprite) {
			CGPoint sprPos = ccsprite.getPosition();

			CGPoint sprDelta = CGPoint.make(newPos.x - prevPathPosition.x,
					newPos.y - prevPathPosition.y);
			ccsprite.setPosition(CGPoint.ccp((sprPos.x + sprDelta.x),
					(sprPos.y + sprDelta.y)));

			prevPathPosition = newPos;
		}

		if (axisOrientation != 0) {
			ccsprite.setRotation(newAngle);
		}
		if (isLine) {
			if (axisOrientation != 0) {
				ccsprite.setRotation(endAngle);
			}
		}

		float dist = CGPoint.ccpDistance(prevPathPosition, endPosition);

		if (0.001 > dist) {
			if (currentPoint + 1 < (int) pathPoints.size()) {
				elapsed = 0.0f;
				currentPoint += 1;
			}
		}

		// updating all the shapes if any
		if (null != body) {
			// we dont update dynamic bodies
			if (BodyType.DynamicBody != body.getType()) {
				if (null != ccsprite) {
					float angle = ccsprite.getRotation();
					CGPoint pos = ccsprite.getPosition();
					body.setTransform(new Vector2(pos.x
							/ LHSettings.sharedInstance().lhPtmRatio(), pos.y
							/ LHSettings.sharedInstance().lhPtmRatio()),
							ccMacros.CC_DEGREES_TO_RADIANS(-angle));
				}
			}
		}

		// ///////////////////////////////////////
		super.visit(gl);
		float convertedTime = System.currentTimeMillis() / 1000f;
		elapsed += convertedTime - m_time;
		m_time = convertedTime;
	}

	public interface PathNodeNotifier {
		public void onNotify(CCNode sprite);
	}

}
