package org.cocos2d.tests;

import java.util.Random;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCPhysicsLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class DebugDrawTest extends Activity {
	private CCGLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);
		CCDirector director = CCDirector.sharedDirector();
		director.attachInView(mGLSurfaceView);
		director.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
		setContentView(mGLSurfaceView);

		// show FPS
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);

		CCScene scene = CCScene.node();
		scene.addChild(new Box2DTestLayer());

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onPause() {
		super.onPause();

		CCDirector.sharedDirector().onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		CCDirector.sharedDirector().onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		CCDirector.sharedDirector().end();
		// CCTextureCache.sharedTextureCache().removeAllTextures();
	}

	//
	// Demo of calling integrating Box2D physics engine with cocos2d sprites
	// a cocos2d example
	// http://code.google.com/p/cocos2d-iphone
	//
	// by Steve Oldmeadow
	//
	static class Box2DTestLayer extends CCPhysicsLayer {
		public static final int kTagTileMap = 1;
		public static final int kTagSpriteManager = 1;
		public static final int kTagAnimation1 = 1;

		public Box2DTestLayer() {
			super();
			useDebugDraw();
			this.setIsTouchEnabled(true);
			this.setIsAccelerometerEnabled(true);

			CGSize s = CCDirector.sharedDirector().winSize();

			// Vector2 lower = new Vector2(-BUFFER, -BUFFER);
			// Vector2 upper = new Vector2(scaledWidth+BUFFER,
			// scaledHeight+BUFFER);

			// Define the ground body.
			BodyDef bxGroundBodyDef = new BodyDef();
			bxGroundBodyDef.position.set(0.0f, 0.0f);

			// Call the body factory which allocates memory for the ground body
			// from a pool and creates the ground box shape (also from a pool).
			// The body is also added to the world.
			Body groundBody = world.createBody(bxGroundBodyDef);

			// Define the ground box shape.
			Vector2 bottomLeft = new Vector2(0f, 0f);
			Vector2 topLeft = new Vector2(0f, scaledHeight);
			Vector2 topRight = new Vector2(scaledWidth, scaledHeight);
			Vector2 bottomRight = new Vector2(scaledWidth, 0f);

			EdgeShape bottom = new EdgeShape();
			// bottom
			bottom.set(bottomLeft, bottomRight);
			groundBody.createFixture(bottom, 0);

			// // top
			EdgeShape top = new EdgeShape();
			top.set(topLeft, topRight);
			groundBody.createFixture(top, 0);
			// left
			EdgeShape left = new EdgeShape();
			left.set(topLeft, bottomLeft);
			groundBody.createFixture(left, 0);

			// right
			EdgeShape right = new EdgeShape();
			right.set(topRight, bottomRight);
			groundBody.createFixture(right, 0);

			BodyDef bxLineDef = new BodyDef();
			bxLineDef.type = BodyType.StaticBody;
			Body lineBody = world.createBody(bxLineDef);
			EdgeShape line = new EdgeShape();
			line.set(topRight, bottomLeft);
			lineBody.createFixture(line, 0);

			BodyDef bxDef1 = new BodyDef();
			bxDef1.type = BodyType.StaticBody;
			Body body = world.createBody(bxLineDef);
			
			PolygonShape p = new PolygonShape();
			Vector2[] vertices = new Vector2[2];
			vertices[0] = new Vector2(5, 5);
			vertices[1] = new Vector2(10, 5);
			p.set(vertices);
			body.createFixture(p, 0);
			
			p = new PolygonShape();
			vertices = new Vector2[2];
			vertices[0] = new Vector2(5, 10);
			vertices[1] = new Vector2(10, 10);
			p.set(vertices);
			body.createFixture(p, 0);

			// Set up sprite
			CCSpriteSheet mgr = CCSpriteSheet.spriteSheet("blocks.png", 150);
			addChild(mgr, 0, kTagSpriteManager);

			addNewSpriteWithCoords(CGPoint.ccp(s.width / 2.0f, s.height / 2.0f));

			CCLabel label = CCLabel.makeLabel("Tap screen", "DroidSans", 32);
			label.setPosition(CGPoint.make(s.width / 2f, s.height - 50f));
			label.setColor(new ccColor3B(0, 0, 255));
			addChild(label);
		}

		private void addNewSpriteWithCoords(CGPoint pos) {
			// Define the dynamic body.
			// Set up a 1m squared box in the physics world
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.position.set(pos.x / ptm_ratio, pos.y / ptm_ratio);

			Shape shape = null;
			final int nextInt = new Random().nextInt(3);
			switch (nextInt) {
			case 0:
				PolygonShape dynamicBox = new PolygonShape();
				dynamicBox.setAsBox(.5f, .5f);
				shape = dynamicBox;
				break;
			case 1:
				CircleShape circleShape = new CircleShape();
				circleShape.setRadius(.5f);
				shape = circleShape;
				break;
			case 2:
				PolygonShape triangle = new PolygonShape();
				triangle.set(new Vector2[] { new Vector2(0, 0),
						new Vector2(1, 0), new Vector2(.5f, 1) });
				shape = triangle;
				break;
			default:
				break;
			}

			// Define another box shape for our dynamic body.
			synchronized (world) {
				// Define the dynamic body fixture and set mass so it's dynamic.
				Body body = world.createBody(bodyDef);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = shape;
				fixtureDef.density = 1.0f;
				fixtureDef.friction = 0.3f;
				body.createFixture(fixtureDef);
			}
		}

		@Override
		public boolean ccTouchesBegan(MotionEvent event) {
			CGPoint location = CCDirector.sharedDirector().convertToGL(
					CGPoint.make(event.getX(), event.getY()));

			addNewSpriteWithCoords(location);

			return CCTouchDispatcher.kEventHandled;
		}

		static float prevX = 0, prevY = 0;

		Vector2 gravity = new Vector2();

		@Override
		public void ccAccelerometerChanged(float accelX, float accelY,
				float accelZ) {

			// #define kFilterFactor 0.05f
			float kFilterFactor = 1.0f; // don't use filter. the code is here
										// just as an example

			float accX = (float) accelX * kFilterFactor + (1 - kFilterFactor)
					* prevX;
			float accY = (float) accelY * kFilterFactor + (1 - kFilterFactor)
					* prevY;

			prevX = accX;
			prevY = accY;

			// no filtering being done in this demo (just magnify the gravity a
			// bit)
			gravity.set(accY * 9.8f, accX * -9.8f);
			world.setGravity(gravity);
		}

	}
}
