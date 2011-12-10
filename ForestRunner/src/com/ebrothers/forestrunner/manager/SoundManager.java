package com.ebrothers.forestrunner.manager;

import java.util.HashMap;
import java.util.Map;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.sound.SoundEngine;

import android.content.Context;

import com.ebrothers.forestrunner.R;
import com.ebrothers.forestrunner.common.Logger;

/**
 * 
 * 声音管理类
 * 
 */
public class SoundManager {
	private static final String TAG = "SoundManager";
	private static SoundManager _instance;

	private static Map<Integer, Integer> sourceSound;

	public static final int MUSIC_BACKGROUND = 0;
	public static final int MUSIC_BOX = 1;
	public static final int MUSIC_BUTTON = 2;
	public static final int MUSIC_DINOSAUR = 3;
	public static final int MUSIC_DOWN = 4;
	public static final int MUSIC_DOWNSLOPE = 5;
	public static final int MUSIC_FAIL = 6;
	public static final int MUSIC_FIRE = 7;
	public static final int MUSIC_FLOWWE = 8;
	public static final int MUSIC_JUMP = 9;
	public static final int MUSIC_JUMPDOWN = 10;
	public static final int MUSIC_LIFE = 11;
	public static final int MUSIC_MUSHROOM_1 = 12;
	public static final int MUSIC_MUSHROOM_2 = 13;
	public static final int MUSIC_RELIVE = 14;
	public static final int MUSIC_START_1 = 15;
	public static final int MUSIC_START_2 = 16;
	public static final int MUSIC_START = 17;
	public static final int MUSIC_SUCCESS = 18;
	public static final int MUSIC_TRAP = 19;
	public static final int MUSIC_UPSLOPE = 20;

	public static SoundManager sharedSoundManager() {
		if (_instance == null) {
			_instance = new SoundManager();
		}
		return _instance;
	}

	private SoundManager() {
		sourceSound = new HashMap<Integer, Integer>();
		sourceSound.put(MUSIC_BACKGROUND, R.raw.music_background);
		sourceSound.put(MUSIC_BOX, R.raw.music_box);
		sourceSound.put(MUSIC_BUTTON, R.raw.music_button);
		sourceSound.put(MUSIC_DINOSAUR, R.raw.music_dinosaur);
		sourceSound.put(MUSIC_DOWN, R.raw.music_down);
		sourceSound.put(MUSIC_DOWNSLOPE, R.raw.music_downslope);
		sourceSound.put(MUSIC_FAIL, R.raw.music_fail);
		sourceSound.put(MUSIC_FIRE, R.raw.music_fire);
		sourceSound.put(MUSIC_FLOWWE, R.raw.music_flower);
		sourceSound.put(MUSIC_JUMP, R.raw.music_jump);
		sourceSound.put(MUSIC_JUMPDOWN, R.raw.music_jumpdown);
		sourceSound.put(MUSIC_LIFE, R.raw.music_life);
		sourceSound.put(MUSIC_MUSHROOM_1, R.raw.music_mushroom1);
		sourceSound.put(MUSIC_MUSHROOM_2, R.raw.music_mushroom2);
		sourceSound.put(MUSIC_RELIVE, R.raw.music_relive);
		sourceSound.put(MUSIC_START_1, R.raw.music_star1);
		sourceSound.put(MUSIC_START_2, R.raw.music_star2);
		sourceSound.put(MUSIC_START, R.raw.music_start);
		sourceSound.put(MUSIC_SUCCESS, R.raw.music_success);
		sourceSound.put(MUSIC_TRAP, R.raw.music_trap);
		sourceSound.put(MUSIC_UPSLOPE, R.raw.music_upslope);
	}

	public void preload(Context context) {
		SoundEngine soundEngine = SoundEngine.sharedEngine();
		for (Integer rawId : sourceSound.values()) {
			if (R.raw.music_background == rawId) {
				soundEngine.preloadSound(context, rawId);
			} else {
				soundEngine.preloadEffect(context, rawId);
			}
		}
	}

	public void playSound(int which, boolean loop) {
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(
				LocalDataManager.SOUND, true);
		if (!sound)
			return;
		if (sourceSound.containsKey(which)) {
			SoundEngine.sharedEngine().playSound(
					CCDirector.sharedDirector().getActivity(),
					sourceSound.get(which), loop);
		} else {
			Logger.e("game", "sound source null");
		}
	}

	public void pauseSound() {
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(
				LocalDataManager.SOUND, true);
		if (!sound)
			return;
		SoundEngine.sharedEngine().pauseSound();
	}

	public void resumeSound() {
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(
				LocalDataManager.SOUND, true);
		if (!sound)
			return;
		SoundEngine.sharedEngine().resumeSound();
	}

	public void playEffect(int which) {
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(
				LocalDataManager.SOUND, true);
		if (!sound)
			return;
		try {
			if (sourceSound.containsKey(which)) {
				int id = sourceSound.get(which);
				SoundEngine.sharedEngine().playEffect(
						CCDirector.sharedDirector().getActivity(), id);
			} else {
				Logger.e("game", "sound source null");
			}
		} catch (Exception e) {
			Logger.e(TAG, e);
		}
	}

}