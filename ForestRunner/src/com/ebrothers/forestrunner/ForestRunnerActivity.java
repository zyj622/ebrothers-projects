package com.ebrothers.forestrunner;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Levels;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.manager.LocalDataManager;
import com.ebrothers.forestrunner.manager.SceneManager;
import com.ebrothers.forestrunner.scenes.GameScene;
import com.ebrothers.forestrunner.scenes.MainScene;

public class ForestRunnerActivity extends Activity {
	private static final String TAG = "ForestRunnerActivity";
	private CCGLSurfaceView mGLSurfaceView;

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

		setContentView(mGLSurfaceView);
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
		CCDirector.sharedDirector().end();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 后退键场景切换
			if (!SceneManager.sharedSceneManager().backTo()) {
				showDialog();
			}
		}
		return true;
	}

	private void showDialog() {
		Builder builder = new Builder(this);
		// 设置对话框的标题
		builder.setTitle("Exit");
		// 设置对话框的提示文本
		builder.setMessage("Are you sure you want to quit the game?");
		// 监听左侧按钮
		builder.setPositiveButton("Exit", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ForestRunnerActivity.this.finish();
			}
		});
		// 监听右侧按钮
		builder.setNegativeButton("Keep Playing", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
}