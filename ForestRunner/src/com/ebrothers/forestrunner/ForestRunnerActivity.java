package com.ebrothers.forestrunner;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.ebrothers.forestrunner.common.Constants;
import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Levels;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.scenes.GameScene;
import com.ebrothers.forestrunner.scenes.MainScene;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class ForestRunnerActivity extends Activity {
	private static final String TAG = "ForestRunnerActivity";
	private CCGLSurfaceView mGLSurfaceView;
	private BroadcastReceiver mBroadcastReceiver;
	private LinearLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "ForestRunnerActivity#onCreate.");

		// set the window status, no tile, full screen and don't sleep
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout = new LinearLayout(this);
		AdView adView = new AdView(this, AdSize.BANNER, "a14eec12a2a283c");
		layout.addView(adView);
		AdRequest request = new AdRequest();
		request.setTesting(true);

		adView.loadAd(request);

		RelativeLayout rl = new RelativeLayout(this);
		rl.addView(mGLSurfaceView);
		rl.addView(layout, params);
		setContentView(rl);
		// setContentView(mGLSurfaceView);
		// 初始化 preference
		LocalDataManager.getInstance().initialize(this);

		CCSpriteFrameCache cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		cache.addSpriteFrames("sprites.plist");
		cache.addSpriteFrames("stages.plist");
		cache.addSpriteFrames("mainmenu.plist");
		cache.addSpriteFrames("gameover.plist");

		Levels.load();

		// attach the OpenGL view to a window
		CCDirector.sharedDirector().attachInView(mGLSurfaceView);

		CGSize winSize = CCDirector.sharedDirector().winSize();
		Game.scale_ratio = winSize.height / 320f;
		Logger.d(TAG, "onCreate. scale_ratio=" + Game.scale_ratio);
		Game.scale_ratio_y = winSize.height / 320f;
		Game.scale_ratio_x = winSize.width / 480f;

		float winHeight = CCDirector.sharedDirector().winSize().getHeight();
		Game.groundH_y = winHeight / 2f;
		Game.groundM_y = winHeight / 3f;
		Game.groundL_y = winHeight / 10f;

		// no effect here because device orientation is controlled by manifest
		CCDirector.sharedDirector().setDeviceOrientation(
				CCDirector.kCCDeviceOrientationLandscapeLeft);

		// show FPS
		// set false to disable FPS display, but don't delete fps_images.png!!
		CCDirector.sharedDirector().setDisplayFPS(true);

		// frames per second
		CCDirector.sharedDirector().setAnimationInterval(1f / 60);

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(MainScene.scene());
		// CCDirector.sharedDirector().runWithScene(GameScene.scene(1));

		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String isShow = intent.getExtras().getString("isShow");
				if ("1".equals(isShow)) {
					layout.setVisibility(View.VISIBLE);
				} else {
					layout.setVisibility(View.GONE);
				}
			}

		};
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Constants.ACTION_AD_CONTROL);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		CCScene runningScene = CCDirector.sharedDirector().getRunningScene();
		if (runningScene instanceof GameScene && Game.delegate != null) {
			Game.delegate.pauseGame();
		}
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
		unregisterReceiver(mBroadcastReceiver);
		CCDirector.sharedDirector().end();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 后退键场景切换
			if (!SceneManager.sharedSceneManager().backTo()) {
				CustomAlertDialog.showExitConfirmDialog(this,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ForestRunnerActivity.this.finish();
							}
						});
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}