package org.cocos2d.tests;

import org.cocos2d.layers.CCPhysicsLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.utils.GB2ShapeCache;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;

public class PhysicsEditorTest extends Activity {
	// static {
	// System.loadLibrary("gdx");
	// }
	public static PhysicsEditorTest app;
	private CCGLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);
		setContentView(mGLSurfaceView);

		// attach the OpenGL view to a window
		CCDirector.sharedDirector().attachInView(mGLSurfaceView);

		// set landscape mode
		CCDirector.sharedDirector().setLandscape(false);

		// show FPS
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

		CCScene scene = CCScene.node();
		scene.addChild(new MainLayer(), 2);

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
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
	}

	static class MainLayer extends CCPhysicsLayer {
		static final float PTM_RATIO = 32;

		public MainLayer() {
			super();

			useDebugDraw();
			
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

			GB2ShapeCache cache = GB2ShapeCache.sharedShapeCache();
			cache.addShapesWithFile("rider_head_bear.plist");
			BodyDef def = new BodyDef();
			def.type = BodyType.DynamicBody;
			def.position.set(10, 10);
			Body body = world.createBody(def);
			cache.addFixturesToBody(body, "rider_head_bear");

			CCSprite sprite = CCSprite.sprite("rider_head_bear.png");
			sprite.setAnchorPoint(cache.anchorPointForShape("rider_head_bear"));
			body.setUserData(sprite);
			addChild(sprite);
		}
	}

}
