package com.ebrothers.forestrunner.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LocalDataManager {

	// 用户配置数据
	public static final String SOUND = "sound";
	public static final String PASSED = "passed";
	
	public static final String DIFFICULTY_KEY = "difficulty";
	
	private static LocalDataManager instance;

	private SharedPreferences settings = null;

	public static LocalDataManager getInstance() {
		if (instance == null) {
			instance = new LocalDataManager();
		}
		return instance;
	}

	/**
	 * 初始化设置
	 * 
	 * @param context
	 */
	public void initialize(Context context) {
		settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 写操作,写完并commit
	 * 
	 * @param name
	 * @param obj
	 */
	public void writeSetting(String name, Object obj) {
		SharedPreferences.Editor editor = settings.edit();
		if (obj instanceof Boolean) {
			editor.putBoolean(name, (Boolean) obj);
		} else if (obj instanceof Integer) {
			editor.putInt(name, (Integer) obj);
		} else if (obj instanceof Float) {
			editor.putFloat(name, (Float) obj);
		} else if (obj instanceof Long) {
			editor.putLong(name, (Long) obj);
		} else if (obj instanceof String) {
			editor.putString(name, (String) obj);
		}
		editor.commit();
	}

	/**
	 * 清除掉已经有得设置
	 * 
	 * @param name
	 */
	public void deleteSetting(String name) {
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(name);
		editor.commit();
	}

	/**
	 * 读配置操作
	 * 
	 * @param name
	 * @param res
	 * @return
	 */
	public Object readSetting(String name, Object res) {
		if (res instanceof Boolean) {
			res = settings.getBoolean(name, (Boolean) res);
		} else if (res instanceof Integer) {
			res = settings.getInt(name, (Integer) res);
		} else if (res instanceof Float) {
			res = settings.getFloat(name, (Float) res);
		} else if (res instanceof Long) {
			res = settings.getLong(name, (Long) res);
		} else if (res instanceof String) {
			res = settings.getString(name, (String) res);
		}
		return res;
	}
}