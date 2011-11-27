package com.ebrothers.forestrunner;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.scenes.MainScene;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class ForestRunnerActivity extends Activity {
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
		loadFrameCache();

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

		// Make the Scene active
		CCDirector.sharedDirector().runWithScene(MainScene.scene());
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 后退键场景切换
			CCScene cs = CCDirector.sharedDirector().getRunningScene();
			if (cs instanceof MainScene) {
				showDialog();
			} else {
				CCDirector.sharedDirector().popScene();
			}
		}
		return true;
	}

	private void loadFrameCache() {
		CCSpriteFrameCache sharedSpriteFrameCache = CCSpriteFrameCache
				.sharedSpriteFrameCache();
		sharedSpriteFrameCache.addSpriteFrames("static.plist");
		sharedSpriteFrameCache.addSpriteFrames("sprites.plist");
		sharedSpriteFrameCache.addSpriteFrames("backgrounds.plist");
		sharedSpriteFrameCache.addSpriteFrames("menu.plist");
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