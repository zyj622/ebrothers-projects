package com.ebrothers.forestrunner.manager;

import java.util.HashMap;
import java.util.Map;

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

	private static Map<String, Integer> sourceSound;

	public static final String MUSIC_BACKGROUND = "music_background";
	public static final String MUSIC_BOX = "music_box";
	public static final String MUSIC_BUTTON = "music_button";
	public static final String MUSIC_DINOSAUR = "music_dinosaur";
	public static final String MUSIC_DOWN = "music_down";
	public static final String MUSIC_DOWNSLOPE = "music_downslope";
	public static final String MUSIC_FAIL = "music_fail";
	public static final String MUSIC_FIRE = "music_fire";
	public static final String MUSIC_FLOWWE = "music_flower";
	public static final String MUSIC_JUMP = "music_jump";
	public static final String MUSIC_JUMPDOWN = "music_jumpdown";
	public static final String MUSIC_LIFE = "music_life";
	public static final String MUSIC_MUSHROOM_1 = "music_mushroom1";
	public static final String MUSIC_MUSHROOM_2 = "music_mushroom2";
	public static final String MUSIC_RELIVE = "music_relive";
	public static final String MUSIC_START_1 = "music_star1";
	public static final String MUSIC_START_2 = "music_star2";
	public static final String MUSIC_START = "music_start";
	public static final String MUSIC_SUCCESS = "music_success";
	public static final String MUSIC_TRAP = "music_trap";
	public static final String MUSIC_UPSLOPE = "music_upslope";

	public static SoundManager getInstance() {
		if (_instance == null) {
			_instance = new SoundManager();
		}
		return _instance;
	}

	private SoundManager() {
		sourceSound = new HashMap<String, Integer>();
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

	public void playSound(Context context, String fileName, boolean loop) {
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(
				LocalDataManager.SOUND, true);
		if (!sound)
			return;
		if (sourceSound.containsKey(fileName)) {
			SoundEngine.sharedEngine().playSound(context,
					sourceSound.get(fileName), loop);
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

	public void playEffect(Context context, String fileName) {
		boolean sound = (Boolean) LocalDataManager.getInstance().readSetting(
				LocalDataManager.SOUND, true);
		Logger.d(TAG, "playEffect. sound=" + sound);
		if (!sound)
			return;
		try {
			if (sourceSound.containsKey(fileName)) {
				int id = sourceSound.get(fileName);
				SoundEngine.sharedEngine().playEffect(context, id);
			} else {
				Logger.e("game", "sound source null");
			}
		} catch (Exception e) {
			Logger.e(TAG, e);
		}
	}

}