package org.cocos2d.levelhelper.nodes;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.levelhelper.LHObject;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.FastFloatBuffer;
import org.cocos2d.utils.GeometryUtil;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class LHBezierNode extends CCNode {
	private boolean isClosed;
	private boolean isTile;
	private boolean isVisible;
	private boolean isLine;
	private boolean isPath;
	private String uniqueName;
	private Body body; // can be 0
	private ArrayList<CGPoint> pathPoints;
	// CCMutableArray<LHPathNode*> pathNodes;

	// ///////for the tile feature
	private CCTexture2D texture;
	private CGRect color;
	private CGRect lineColor;
	private float lineWidth;
	private CGSize winSize;
	private ArrayList<ArrayList<CGPoint>> trianglesHolder;
	private ArrayList<CGPoint> linesHolder;
	private CGSize imageSize;

	public boolean getIsClosed() {
		return isClosed;
	}

	public boolean getIsTile() {
		return isTile;
	}

	public boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(boolean v) {
		isVisible = v;
	}

	public boolean getIsLine() {
		return isLine;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public Body getBody() {
		return body;
	}

	public boolean initWithDictionary(HashMap<String, LHObject> bezierDict,
			CCLayer ccLayer, World world) {

		isClosed = bezierDict.get("IsClosed").boolValue();
		isTile = bezierDict.get("IsTile").boolValue();
		isVisible = bezierDict.get("IsDrawable").boolValue();
		isLine = bezierDict.get("IsSimpleLine").boolValue();
		isPath = bezierDict.get("IsPath").boolValue();

		uniqueName = bezierDict.get("UniqueName").stringValue();

		setTag(bezierDict.get("Tag").intValue());
		setVertexZ(bezierDict.get("ZOrder").intValue());

		String img = bezierDict.get("Image").stringValue();
		imageSize = CGSize.zero();
		if (img != null && img.length() != 0) {
			String path = LHSettings.sharedInstance().imagePath(img);
			texture = CCTextureCache.sharedTextureCache().addImage(path);
			if (texture != null) {
				imageSize = texture.getContentSize();
			}
		}

		winSize = CCDirector.sharedDirector().winSize();

		color = stringToCGRect(bezierDict.get("Color").stringValue());
		lineColor = stringToCGRect(bezierDict.get("LineColor").stringValue());
		lineWidth = bezierDict.get("LineWidth").floatValue();

		initTileVerticesFromDictionary(bezierDict);
		initPathPointsFromDictionary(bezierDict);
		createBodyFromDictionary(bezierDict, world);

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public static LHBezierNode nodeWithDictionary(
			HashMap<String, LHObject> properties, CCLayer ccLayer, World world) {
		LHBezierNode pobBNode = new LHBezierNode();
		if (pobBNode != null
				&& pobBNode.initWithDictionary(properties, ccLayer, world)) {
			return pobBNode;
		}
		return null;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public LHPathNode addSpriteOnPath(LHSprite spr, float pathSpeed,
			boolean startAtEndPoint, boolean isCyclic, boolean restartOtherEnd,
			int axis, boolean flipx, boolean flipy, boolean deltaMove) {

		LHPathNode node = LHPathNode.nodePathWithPoints(pathPoints);
		node.setStartAtEndPoint(startAtEndPoint);
		node.setSprite(spr);
		node.setBody(spr.getBody());

		if (!deltaMove) {
			if (pathPoints.size() > 0) {
				CGPoint pathPos = pathPoints.get(0);
				spr.transformPosition(pathPos);
			}
		}

		node.setSpeed(pathSpeed);
		node.setRestartOtherEnd(restartOtherEnd);
		node.setIsCyclic(isCyclic);
		node.setAxisOrientation(axis);
		node.setIsLine(isLine);
		node.setFlipX(flipx);
		node.setFlipY(flipy);
		node.setUniqueName(uniqueName);
		// pathNodes.addObject(node);

		this.getParent().addChild(node);

		return node;

	}

	// //////////////////////////////////////////////////////////////////////////////
	private static CGPoint pointOnCurve(CGPoint p1, CGPoint p2, CGPoint p3,
			CGPoint p4, float t) {
		float var1, var2, var3;
		CGPoint vPoint = CGPoint.zero();

		var1 = 1 - t;
		var2 = var1 * var1 * var1;
		var3 = t * t * t;
		vPoint.x = var2 * p1.x + 3 * t * var1 * var1 * p2.x + 3 * t * t * var1
				* p3.x + var3 * p4.x;
		vPoint.y = var2 * p1.y + 3 * t * var1 * var1 * p2.y + 3 * t * t * var1
				* p3.y + var3 * p4.y;
		return (vPoint);
	}

	// //////////////////////////////////////////////////////////////////////////////
	private void initTileVerticesFromDictionary(
			HashMap<String, LHObject> bezierDict) {
		// trianglesHolder = [[NSMutableArray alloc] init];

		CGPoint convert = LHSettings.sharedInstance().convertRatio();
		ArrayList<LHObject> fixtures = bezierDict.get("TileVertices")
				.arrayValue();

		if (fixtures != null) {
			int count = fixtures.size();
			for (int i = 0; i < count; ++i) {
				ArrayList<LHObject> fix = fixtures.get(i).arrayValue();

				ArrayList<CGPoint> triagle = new ArrayList<CGPoint>();
				for (int j = 0; j < fix.size(); ++j) {
					CGPoint point = stringToCGPoint(fix.get(j).stringValue());

					CGPoint pos_offset = LHSettings.sharedInstance()
							.possitionOffset();
					point.x += pos_offset.x;
					point.y += pos_offset.y;

					point.x = point.x * convert.x;
					point.y = winSize.height - point.y * convert.y;

					triagle.add(point);
				}

				trianglesHolder.add(triagle);
			}
		}

		// linesHolder = [[NSMutableArray alloc] init];
		if (isVisible) {
			ArrayList<LHObject> curvesInShape = bezierDict.get("Curves")
					.arrayValue();

			int MAX_STEPS = 25;
			int count = curvesInShape.size();
			for (int i = 0; i < count; ++i) {
				HashMap<String, LHObject> curvDict = curvesInShape.get(i)
						.dictValue();

				CGPoint endCtrlPt = stringToCGPoint(curvDict.get(
						"EndControlPoint").stringValue());
				CGPoint startCtrlPt = stringToCGPoint(curvDict.get(
						"StartControlPoint").stringValue());
				CGPoint endPt = stringToCGPoint(curvDict.get("EndPoint")
						.stringValue());
				CGPoint startPt = stringToCGPoint(curvDict.get("StartPoint")
						.stringValue());

				CGPoint pos_offset = LHSettings.sharedInstance()
						.possitionOffset();

				endCtrlPt.x += pos_offset.x;
				endCtrlPt.y += pos_offset.y;

				startCtrlPt.x += pos_offset.x;
				startCtrlPt.y += pos_offset.y;

				endPt.x += pos_offset.x;
				endPt.y += pos_offset.y;

				startPt.x += pos_offset.x;
				startPt.y += pos_offset.y;

				if (!isLine) {
					CGPoint prevPoint = CGPoint.zero();
					boolean firstPt = true;

					for (float t = 0; t <= (1 + (1.0f / MAX_STEPS)); t += 1.0f / MAX_STEPS) {
						CGPoint vPoint = pointOnCurve(startPt, startCtrlPt,
								endCtrlPt, endPt, t);

						if (!firstPt) {
							CGPoint pt1 = CGPoint.make(prevPoint.x * convert.x,
									winSize.height - prevPoint.y * convert.y);
							CGPoint pt2 = CGPoint.make(vPoint.x * convert.x,
									winSize.height - vPoint.y * convert.y);

							linesHolder.add(pt1);
							linesHolder.add(pt2);
						}
						prevPoint = vPoint;
						firstPt = false;
					}
				} else {

					CGPoint pos1 = CGPoint.make(startPt.x * convert.x,
							winSize.height - startPt.y * convert.y);
					CGPoint pos2 = CGPoint.make(endPt.x * convert.x,
							winSize.height - endPt.y * convert.y);

					linesHolder.add(pos1);
					linesHolder.add(pos2);
				}
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	private void initPathPointsFromDictionary(
			HashMap<String, LHObject> bezierDict) {
		// pathPoints = [[NSMutableArray alloc] init];

		ArrayList<LHObject> curvesInShape = bezierDict.get("Curves")
				.arrayValue();
		int MAX_STEPS = 25;
		CGPoint conv = LHSettings.sharedInstance().convertRatio();

		// int i = 0;
		int count = curvesInShape.size();
		for (int j = 0; j < count; ++j) {
			HashMap<String, LHObject> curvDict = curvesInShape.get(j)
					.dictValue();

			CGPoint endCtrlPt = stringToCGPoint(curvDict.get("EndControlPoint")
					.stringValue());
			CGPoint startCtrlPt = stringToCGPoint(curvDict.get(
					"StartControlPoint").stringValue());
			CGPoint endPt = stringToCGPoint(curvDict.get("EndPoint")
					.stringValue());
			CGPoint startPt = stringToCGPoint(curvDict.get("StartPoint")
					.stringValue());

			CGPoint pos_offset = LHSettings.sharedInstance().possitionOffset();
			endCtrlPt.x += pos_offset.x;
			endCtrlPt.y += pos_offset.y;

			startCtrlPt.x += pos_offset.x;
			startCtrlPt.y += pos_offset.y;

			endPt.x += pos_offset.x;
			endPt.y += pos_offset.y;

			startPt.x += pos_offset.x;
			startPt.y += pos_offset.y;

			if (!isLine) {
				for (float t = 0; t <= (1 + (1.0f / MAX_STEPS)); t += 1.0f / MAX_STEPS) {
					CGPoint vPoint = pointOnCurve(startPt, startCtrlPt,
							endCtrlPt, endPt, t);
					pathPoints.add(CGPoint.ccp(vPoint.x * conv.x,
							winSize.height - vPoint.y * conv.y));
				}
				pathPoints.remove(pathPoints.size() - 1);
			} else {
				pathPoints.add(CGPoint.ccp(startPt.x * conv.x, winSize.height
						- startPt.y * conv.y));
				if (j == curvesInShape.size() - 1) {
					pathPoints.add(CGPoint.ccp(endPt.x * conv.x, winSize.height
							- endPt.y * conv.y));
				}
				// ++i;
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	private void createBodyFromDictionary(HashMap<String, LHObject> bezierDict,
			World world) {
		if (isPath)
			return;

		if ((int) pathPoints.size() < 2)
			return;

		BodyDef bodyDef = new BodyDef();

		int bodyType = bezierDict.get("PhysicType").intValue();
		if (bodyType > 2)
			return;

		bodyDef.type = BodyType.values()[bodyType];

		bodyDef.position.set(0.0f, 0.0f);
		bodyDef.angle = 0.0f;

		// bodyDef.userData = this;

		body = world.createBody(bodyDef);
		body.setUserData(this);

		float ptm = LHSettings.sharedInstance().lhPtmRatio();

		// if(b2_version.major <= 2)
		// if(b2_version.minor <=2)
		// if(b2_version.revision <2)
		// Log.d(TAG,
		// "Please update to Box2d 2.2.2 or above or else you may experience asserts");

		int count = trianglesHolder.size();
		for (int k = 0; k < count; ++k) {
			ArrayList<CGPoint> fix = trianglesHolder.get(k);

			int size = fix.size();
			Vector2[] verts = new Vector2[size];
			int i = 0;
			for (int j = 0; j < size; ++j) {
				CGPoint pt = fix.get(j);

				verts[i].x = pt.x / ptm;
				verts[i].y = pt.y / ptm;
				++i;
			}

			PolygonShape shape = new PolygonShape();
			shape.set(verts);

			FixtureDef fixture = new FixtureDef();

			fixture.density = bezierDict.get("Density").floatValue();
			fixture.friction = bezierDict.get("Friction").floatValue();
			fixture.restitution = bezierDict.get("Restitution").floatValue();

			fixture.filter.categoryBits = (short) bezierDict.get("Category")
					.intValue();
			fixture.filter.maskBits = (short) bezierDict.get("Mask").intValue();
			fixture.filter.groupIndex = (short) bezierDict.get("Group")
					.intValue();

			fixture.isSensor = bezierDict.get("IsSenzor").boolValue();

			fixture.shape = shape;
			body.createFixture(fixture);
		}

		boolean firstPoint = true;
		CGPoint prevPoint = CGPoint.ccp(0, 0);
		for (int i = 0; i < (int) pathPoints.size(); ++i) {
			CGPoint pt = pathPoints.get(i);

			if (!firstPoint) {
				int size = 2;
				Vector2[] verts = new Vector2[size];

				verts[0].x = prevPoint.x / ptm;
				verts[0].y = prevPoint.y / ptm;
				verts[1].x = pt.x / ptm;
				verts[1].y = pt.y / ptm;

				EdgeShape shape = new EdgeShape();
				shape.set(verts[0], verts[1]);

				FixtureDef fixture = new FixtureDef();

				fixture.density = bezierDict.get("Density").floatValue();
				fixture.friction = bezierDict.get("Friction").floatValue();
				fixture.restitution = bezierDict.get("Restitution")
						.floatValue();

				fixture.filter.categoryBits = (short) bezierDict
						.get("Category").intValue();
				fixture.filter.maskBits = (short) bezierDict.get("Mask")
						.intValue();
				fixture.filter.groupIndex = (short) bezierDict.get("Group")
						.intValue();

				fixture.isSensor = bezierDict.get("IsSenzor").boolValue();

				fixture.shape = shape;
				body.createFixture(fixture);
			}

			firstPoint = false;
			prevPoint = pt;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	@Override
	public void draw(GL10 gl) {
		if (0.0f != LHSettings.sharedInstance().customAlpha()) {
			gl.glColor4f(color.origin.x, color.origin.y, color.size.width,
					color.size.height
							* LHSettings.sharedInstance().customAlpha());
			gl.glPushMatrix();
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			if (texture != null) {
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.name());

				gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
						GL10.GL_REPEAT);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
						GL10.GL_REPEAT);

				for (int k = 0; k < trianglesHolder.size(); k++) {
					ArrayList<CGPoint> fix = trianglesHolder.get(k);
					int size = fix.size();
					FastFloatBuffer glVertices = new FastFloatBuffer(size * 2);
					FastFloatBuffer glUV = new FastFloatBuffer(size * 2);
					for (int j = 0; j < size; j++) {
						CGPoint pt = fix.get(j);
						glVertices.put(pt.x);
						glVertices.put(pt.y);
						glUV.put(pt.x / imageSize.width);
						glUV.put((winSize.height - pt.y) / imageSize.height);
					}
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, glUV.bytes);
					gl.glVertexPointer(2, GL10.GL_FLOAT, 0, glVertices.bytes);
					gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, size);
				}

			}

			float oldLineWidth = 1.0f;
			// gl.glGetFloatv(GL11.GL_LINE_WIDTH, oldLineWidth);
			IntBuffer intBuffer = IntBuffer.allocate(1);
			gl.glGetIntegerv(GL11.GL_LINE_WIDTH, intBuffer);
			oldLineWidth = intBuffer.get();
			gl.glLineWidth(lineWidth);

			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glColor4f(lineColor.origin.x, lineColor.origin.y,
					lineColor.size.width, lineColor.size.height
							* LHSettings.sharedInstance().customAlpha());

			for (int i = 0; i < linesHolder.size(); i += 2) {
				CGPoint pt1 = linesHolder.get(i);
				CGPoint pt2 = linesHolder.get(i + 1);

				// CGPoint[] line = new CGPoint[2];
				// line[0].x = pt1.x;
				// line[0].y = pt1.y;
				// line[1].x = pt2.x;
				// line[1].y = pt2.y;
				FastFloatBuffer buffer = new FastFloatBuffer(4);
				buffer.put(pt1.x);
				buffer.put(pt1.y);
				buffer.put(pt2.x);
				buffer.put(pt2.y);
				gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer.bytes);
				gl.glDrawArrays(GL10.GL_LINES, 0, 2);
			}
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glLineWidth(oldLineWidth);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glPopMatrix();
		}
	}

	private CGPoint stringToCGPoint(String str) {
		return GeometryUtil.CGPointFromString(str);
	}

	private CGRect stringToCGRect(String str) {
		return GeometryUtil.CGRectFromString(str);
	}

}
