package org.cocos2d.utils;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_LINES;
import static javax.microedition.khronos.opengles.GL10.GL_LINE_LOOP;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_FAN;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCDrawingPrimitives;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4F;

import android.util.FloatMath;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.utils.MathUtils;

public class GLESDebugDraw {
	// draw shapes
	public static final int DRAW_SHAPE = 0x0001;
	// draw joint connections
	public static final int DRAW_JOINT = 0x0002;
	// draw axis aligned bounding boxes
	public static final int DRAW_AABB = 0x0004;
	// draw broad-phase pairs
	public static final int DRAW_PAIR = 0x0008;
	// draw center of mass frame
	public static final int DRAW_CENTEROFMASS = 0x0010;
	private static FastFloatBuffer tmpFloatBuf;

	private static FastFloatBuffer getVertices(int size) {
		if (tmpFloatBuf == null || tmpFloatBuf.capacity() < size) {
			ByteBuffer vbb = ByteBuffer.allocateDirect(4 * size);
			vbb.order(ByteOrder.nativeOrder());
			tmpFloatBuf = FastFloatBuffer.createBuffer(vbb);
		}
		tmpFloatBuf.rewind();
		return tmpFloatBuf;
	}

	private final float mRatio;
	private final World mWorld;
	private GL10 mGL;
	private int flag = DRAW_SHAPE | DRAW_JOINT | DRAW_CENTEROFMASS;

	/**
	 * @param ratio
	 */
	public GLESDebugDraw(World world, float ratio) {
		mWorld = world;
		mRatio = ratio;
	}

	void drawPolygon(CGPoint[] oldVertices, int vertexCount, ccColor4F color) {
		CGPoint[] poli = new CGPoint[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			poli[i] = CGPoint.ccpMult(oldVertices[i], mRatio);
		}
		mGL.glColor4f(color.r, color.g, color.b, 1);
		CCDrawingPrimitives.ccDrawPoly(mGL, poli, vertexCount, true);
	}

	void drawSolidPolygon(CGPoint[] oldVertices, int vertexCount,
			ccColor4F color) {
		CGPoint[] poli = new CGPoint[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			poli[i] = CGPoint.ccpMult(oldVertices[i], mRatio);
		}
		FastFloatBuffer vertices = getVertices(2 * vertexCount);

		for (int i = 0; i < vertexCount; i++) {
			vertices.put(poli[i].x);
			vertices.put(poli[i].y);
		}
		vertices.position(0);

		mGL.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);

		mGL.glColor4f(color.r, color.g, color.b, .5f);
		mGL.glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);

		mGL.glColor4f(color.r, color.g, color.b, 1);
		mGL.glDrawArrays(GL_LINE_LOOP, 0, vertexCount);
	}

	private void disableState() {
		mGL.glDisable(GL_TEXTURE_2D);
		mGL.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		mGL.glDisableClientState(GL_COLOR_ARRAY);
	}

	private void restoreState() {
		mGL.glEnableClientState(GL_COLOR_ARRAY);
		mGL.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		mGL.glEnable(GL_TEXTURE_2D);
	}

	void drawCircle(CGPoint center, float radius, ccColor4F color) {
		center.set(center.x * mRatio, center.y * mRatio);
		mGL.glColor4f(color.r, color.g, color.b, 1);
		CCDrawingPrimitives.ccDrawCircle(mGL, center, radius * mRatio, 0, 32,
				true);
	}

	void drawSolidCircle(CGPoint center, float radius, CGPoint axis,
			ccColor4F color) {
		float k_segments = 16.0f;
		int vertexCount = 16;
		float k_increment = 2.0f * MathUtils.PI / k_segments;
		float theta = 0.0f;
		FastFloatBuffer vertices = getVertices(2 * vertexCount);

		for (int i = 0; i < k_segments; i++) {
			CGPoint p = CGPoint.ccpAdd(
					center,
					CGPoint.ccp(FloatMath.cos(theta) * radius,
							FloatMath.sin(theta) * radius));
			vertices.put(p.x * mRatio);
			vertices.put(p.y * mRatio);
			theta += k_increment;
		}
		vertices.position(0);

		mGL.glColor4f(color.r, color.g, color.b, 0.5f);
		mGL.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
		mGL.glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);
		mGL.glColor4f(color.r, color.g, color.b, 1);
		mGL.glDrawArrays(GL_LINE_LOOP, 0, vertexCount);

		// Draw the axis line
		drawSegment(center,
				CGPoint.ccpAdd(center, CGPoint.ccpMult(axis, radius)), color);
	}

	private void drawSegment(CGPoint p1, CGPoint p2, ccColor4F color) {
		mGL.glColor4f(color.r, color.g, color.b, 1);
		FastFloatBuffer vertices = getVertices(4);
		vertices.put(p1.x * mRatio);
		vertices.put(p1.y * mRatio);
		vertices.put(p2.x * mRatio);
		vertices.put(p2.y * mRatio);
		vertices.position(0);

		mGL.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
		mGL.glDrawArrays(GL_LINES, 0, 2);
	}

	private void drawTransform(Transform xf) {
		final Vector2 position = xf.getPosition();
		CGPoint p1 = CGPoint.ccp(position.x, position.y);
		CGPoint p2;
		float k_axisScale = 0.4f;
		p2 = CGPoint.ccpAdd(
				p1,
				CGPoint.ccp(k_axisScale * xf.vals[Transform.COS], k_axisScale
						* xf.vals[Transform.SIN]));
		drawSegment(p1, p2, new ccColor4F(1, 0, 0, 1));

		p2 = CGPoint.ccpAdd(
				p1,
				CGPoint.ccp(k_axisScale * -xf.vals[Transform.SIN], k_axisScale
						* xf.vals[Transform.COS]));
		drawSegment(p1, p2, new ccColor4F(0, 1, 0, 1));
	}

	void drawPoint(CGPoint p, float size, ccColor4F color) {
		mGL.glColor4f(color.r, color.g, color.b, 1);
		mGL.glPointSize(size);
		CCDrawingPrimitives.ccDrawPoint(mGL, p);
		mGL.glPointSize(1.0f);
	}

	void drawAABB(ccColor4F c) {
		mGL.glColor4f(c.r, c.g, c.b, 1);
		CGSize size = CCDirector.sharedDirector().winSize();
		FastFloatBuffer vertices = getVertices(8);
		vertices.put(0);
		vertices.put(0);
		vertices.put(size.width / mRatio);
		vertices.put(0);
		vertices.put(size.width / mRatio);
		vertices.put(size.height / mRatio);
		vertices.put(0);
		vertices.put(size.height / mRatio);
		vertices.position(0);
		mGL.glVertexPointer(2, GL_FLOAT, 0, vertices.bytes);
		mGL.glDrawArrays(GL_LINE_LOOP, 0, 8);
	}

	public void drawDebugData(GL10 gl) {
		mGL = gl;
		disableState();

		// draw shape
		if ((flag & DRAW_SHAPE) != 0) {
			Iterator<Body> bodies = mWorld.getBodies();
			while (bodies.hasNext()) {
				Body b = bodies.next();
				Transform xf = b.getTransform();
				for (Fixture f : b.getFixtureList()) {
					if (!b.isActive()) {
						drawShape(f, xf, new ccColor4F(0.5f, 0.5f, 0.3f, 1));
					} else if (BodyType.StaticBody == b.getType()) {
						drawShape(f, xf, new ccColor4F(0.5f, 0.9f, 0.5f, 1));
					} else if (BodyType.KinematicBody == b.getType()) {
						drawShape(f, xf, new ccColor4F(0.5f, 0.5f, 0.9f, 1));
					} else if (!b.isAwake()) {
						drawShape(f, xf, new ccColor4F(0.6f, 0.6f, 0.6f, 1));
					} else {
						drawShape(f, xf, new ccColor4F(0.9f, 0.7f, 0.7f, 1));
					}
				}
			}
		}
		// draw joint
		if ((flag & DRAW_JOINT) != 0) {
			Iterator<Joint> joints = mWorld.getJoints();
			while (joints.hasNext()) {
				Joint j = joints.next();
				drawJoint(j);
			}
		}

		// draw pair bits
		if ((flag & DRAW_PAIR) != 0) {
			ccColor4F color = new ccColor4F(0.3f, 0.9f, 0.9f, 1);
			List<Contact> contacts = mWorld.getContactList();
			for (Contact c : contacts) {
				Fixture fixtureA = c.getFixtureA();
				Fixture fixtureB = c.getFixtureB();
				final Vector2 centerA = fixtureA.getBody().getWorldCenter();
				final Vector2 centerB = fixtureB.getBody().getWorldCenter();
				CGPoint cA = CGPoint.ccp(centerA.x, centerA.y);
				CGPoint cB = CGPoint.ccp(centerB.x, centerB.y);
				drawSegment(cA, cB, color);
			}
		}

		// draw AABB bit
		if ((flag & DRAW_AABB) != 0) {
			ccColor4F color = new ccColor4F(0.9f, 0.3f, 0.9f, 1);
			drawAABB(color);
		}
		// draw mass
		if ((flag & DRAW_CENTEROFMASS) != 0) {
			Iterator<Body> bodies = mWorld.getBodies();
			while (bodies.hasNext()) {
				Body b = bodies.next();
				Transform xf = b.getTransform();
				xf.setPosition(b.getWorldCenter());
				drawTransform(xf);
			}
		}
		restoreState();
	}

	private void drawShape(Fixture fixture, Transform xf, ccColor4F color) {
		if (Type.Circle == fixture.getType()) {
			final CircleShape circle = (CircleShape) fixture.getShape();
			Vector2 center = xf.mul(circle.getPosition());
			float radius = circle.getRadius();
			CGPoint axis = CGPoint.ccp(xf.vals[Transform.COS],
					xf.vals[Transform.SIN]);
			drawSolidCircle(CGPoint.ccp(center.x, center.y), radius, axis,
					color);
		} else if (Type.Polygon == fixture.getType()) {
			final PolygonShape poly = (PolygonShape) fixture.getShape();
			int vertexCount = poly.getVertexCount();
			assert (vertexCount <= 8);
			final CGPoint[] vertices = new CGPoint[8];
			final Vector2 temp = new Vector2();
			for (int i = 0; i < vertexCount; ++i) {
				poly.getVertex(i, temp);
				final Vector2 vec = xf.mul(temp);
				vertices[i] = new CGPoint();
				vertices[i].x = vec.x;
				vertices[i].y = vec.y;
			}
			drawSolidPolygon(vertices, vertexCount, color);
		} else if (Type.Edge == fixture.getType()) {
			final EdgeShape edge = (EdgeShape) fixture.getShape();
			Vector2 vec1 = new Vector2();
			Vector2 vec2 = new Vector2();
			edge.getVertex1(vec1);
			edge.getVertex2(vec2);
			vec1 = xf.mul(vec1);
			vec2 = xf.mul(vec2);
			drawSegment(CGPoint.make(vec1.x, vec1.y),
					CGPoint.make(vec2.x, vec2.y), color);
		}
	}

	private void drawJoint(Joint joint) {
		Body bodyA = joint.getBodyA();
		Body bodyB = joint.getBodyB();
		Transform xf1 = bodyA.getTransform();
		Transform xf2 = bodyB.getTransform();
		final Vector2 positionA = xf1.getPosition();
		CGPoint x1 = CGPoint.ccp(positionA.x, positionA.y);
		final Vector2 positionB = xf2.getPosition();
		CGPoint x2 = CGPoint.ccp(positionB.x, positionB.y);
		final Vector2 anchorA = joint.getAnchorA();
		CGPoint p1 = CGPoint.ccp(anchorA.x, anchorA.y);
		final Vector2 anchorB = joint.getAnchorB();
		CGPoint p2 = CGPoint.ccp(anchorB.x, anchorB.y);
		ccColor4F color = new ccColor4F(0.5f, 0.8f, 0.8f, 1);
		if (JointType.DistanceJoint == joint.getType()) {
			drawSegment(p1, p2, color);
		} else if (JointType.PulleyJoint == joint.getType()) {
			PulleyJoint pulley = (PulleyJoint) joint;
			Vector2 vA = pulley.getGroundAnchorA();
			Vector2 vB = pulley.getGroundAnchorB();
			CGPoint pA = CGPoint.ccp(vA.x, vA.y);
			CGPoint pB = CGPoint.ccp(vB.x, vB.y);
			drawSegment(pA, p1, color);
			drawSegment(pB, p2, color);
			drawSegment(pA, pB, color);
		} else {
			drawSegment(x1, p1, color);
			drawSegment(p1, p2, color);
			drawSegment(x2, p2, color);
		}
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}
}
