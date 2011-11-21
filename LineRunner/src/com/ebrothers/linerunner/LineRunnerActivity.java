package com.ebrothers.linerunner;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCTexParams;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class LineRunnerActivity extends Activity {
	private static final String TAG = "LineRunnerActivity";
	private CCGLSurfaceView mGLSurfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the window status, no tile, full screen and don't sleep
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);

		setContentView(mGLSurfaceView);

		// attach the OpenGL view to a window
		CCDirector.sharedDirector().attachInView(mGLSurfaceView);

		// no effect here because device orientation is controlled by manifest
		CCDirector.sharedDirector().setDeviceOrientation(
				CCDirector.kCCDeviceOrientationPortrait);

		// show FPS
		// set false to disable FPS display, but don't delete fps_images.png!!
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);

		CCScene scene = CCScene.node();
		scene.addChild(new Layer0());

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(scene);
	}

	@Override
	public void onPause() {
		super.onPause();
		CCDirector.sharedDirector().pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		CCDirector.sharedDirector().resume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		CCDirector.sharedDirector().end();
	}

	static class Layer0 extends CCLayer {
		public static final int kTagSpriteManager = 1;
		public static final int kTagRunner = 2;
		private CCJumpTo jumpTo;
		private final float halfWidth;
		private CCRepeatForever running;
		private CCAnimate rolling;
		private boolean isRolling;

		public Layer0() {
			super();
			setIsTouchEnabled(true);
			CGSize size = CCDirector.sharedDirector().winSize();
			halfWidth = size.width / 2f;
			CGRect rect = CGRect.make(0, 0, size.width, size.height);
			CCSprite background = CCSprite.sprite("background.png", rect);
			background.setPosition(halfWidth, size.height / 2f);
			CCTexParams params = new CCTexParams(GL10.GL_LINEAR,
					GL10.GL_LINEAR, GL10.GL_REPEAT, GL10.GL_REPEAT);
			background.getTexture().setTexParameters(params);
			addChild(background);
			CCSpriteFrameCache cache = CCSpriteFrameCache
					.sharedSpriteFrameCache();
			cache.addSpriteFrames("gElem.plist");
			CCSpriteSheet sheet = CCSpriteSheet.spriteSheet("gElem.png");
			sheet.setTag(kTagSpriteManager);
			addChild(sheet, 0, kTagSpriteManager);
			new GroundLayerBuilder(this).loadLevelData(1);

			ArrayList<CCSpriteFrame> runFrames = new ArrayList<CCSpriteFrame>();
			for (int i = 0; i < 15; i++) {
				runFrames.add(cache.getSpriteFrame(String
						.format("run%d.png", i)));
			}
			CCAnimation run = CCAnimation.animation("run", 0.02f, runFrames);
			running = CCRepeatForever.action(CCAnimate.action(run));

			ArrayList<CCSpriteFrame> rollFrames = new ArrayList<CCSpriteFrame>();
			for (int i = 0; i < 27; i++) {
				rollFrames.add(cache.getSpriteFrame(String.format("roll%d.png",
						i)));
			}
			CCAnimation roll = CCAnimation.animation("roll", 0.02f, rollFrames);
			rolling = CCAnimate.action(roll);
		}

		@Override
		public void onEnter() {
			super.onEnter();
			CCSpriteSheet sheet = (CCSpriteSheet) getChildByTag(Layer0.kTagSpriteManager);
			final CCAction action = CCMoveTo.action(10, CGPoint.ccp(-2400, 0));
			sheet.runAction(action);
			CCSprite runner = (CCSprite) getChildByTag(Layer0.kTagRunner);
			runner.runAction(running);
			schedule("tick");
		}

		public void tick(float delta) {
			if (rolling.isDone() && isRolling) {
				CCSprite runner = (CCSprite) getChildByTag(Layer0.kTagRunner);
				runner.stopAllActions();
				runner.runAction(running);
				isRolling = false;
			}
		}

		@Override
		public void onExit() {
			super.onExit();
			unschedule("tick");
		}

		@Override
		public boolean ccTouchesBegan(MotionEvent event) {
			float x = event.getX();
			CCSprite runner = (CCSprite) getChildByTag(kTagRunner);
			if (x <= halfWidth) {
				runner.stopAllActions();
				if (jumpTo == null || jumpTo.isDone()) {
					jumpTo = CCJumpTo.action(.2f, runner.getPosition(), 80, 1);
					runner.runAction(jumpTo);
				}
			} else {
				runner.stopAllActions();
				runner.runAction(rolling);
				isRolling = true;
			}
			return CCTouchDispatcher.kEventHandled;
		}
	}
}