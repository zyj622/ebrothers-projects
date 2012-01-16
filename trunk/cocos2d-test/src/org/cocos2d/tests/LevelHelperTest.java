package org.cocos2d.tests;

import org.cocos2d.layers.CCMultiplexLayer;
import org.cocos2d.layers.CCPhysicsLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.levelhelper.LevelHelperLoader;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.tests.FontTest.Layer2;
import org.cocos2d.tests.FontTest.Layer3;
import org.cocos2d.tests.FontTest.Layer4;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class LevelHelperTest extends Activity {
	private CCGLSurfaceView mGLSurfaceView;

	public static final int kTagSpriteManager = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mGLSurfaceView = new CCGLSurfaceView(this);
		setContentView(mGLSurfaceView);
	}

	@Override
	public void onStart() {
		super.onStart();

		// attach the OpenGL view to a window
		CCDirector.sharedDirector().attachInView(mGLSurfaceView);

		// set landscape mode
		CCDirector.sharedDirector().setLandscape(false);

		// show FPS
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

		CCScene scene = CCScene.node();

		CCMultiplexLayer layer = CCMultiplexLayer.node(new Layer1(),
				new Layer2(), new Layer3(), new Layer4());
		scene.addChild(layer, 0);
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

	static class Layer1 extends CCPhysicsLayer {
		private LevelHelperLoader loader;

		public Layer1() {
			super();
			useDebugDraw();
		}

		@Override
		public void onEnter() {
			loader = new LevelHelperLoader("1.plhs");
			loader.addObjectsToWorld(world, this);
			super.onEnter();
		}

		@Override
		public void onExit() {
			loader.dispose();
			super.onExit();
		}
	}
}
