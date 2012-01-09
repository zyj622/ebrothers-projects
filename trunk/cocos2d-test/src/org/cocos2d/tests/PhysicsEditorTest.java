package org.cocos2d.tests;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.layers.CCPhysicsLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.utils.GB2ShapeCache;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PhysicsEditorTest extends Activity {
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
		static final int PTM_RATIO = 32;

		public MainLayer() {
			super(32);

			useDebugDraw();

			synchronized (world) {
				GB2ShapeCache cache = GB2ShapeCache.sharedShapeCache();
				cache.addShapesWithFile("shapedefs.plist");
				BodyDef def = new BodyDef();
				def.type = BodyType.StaticBody;
				def.position.set(100 / PTM_RATIO, 100 / PTM_RATIO);
				Body body = world.createBody(def);
				cache.addFixturesToBody(body, "orange");
				
				def = new BodyDef();
				def.type = BodyType.StaticBody;
				def.position.set(200 / PTM_RATIO, 200 / PTM_RATIO);
				body = world.createBody(def);
				cache.addFixturesToBody(body, "hamburger");
			}
		}
	}

}
