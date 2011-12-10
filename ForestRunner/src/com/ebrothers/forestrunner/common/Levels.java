package com.ebrothers.forestrunner.common;

import java.io.IOException;

import org.cocos2d.nodes.CCDirector;

public final class Levels {
	private static final boolean TEST_LEVEL = false;
	private static final String LEVEL_DIR = "level";
	private static final String TAG = "Levels";
	private static String[] levels;
	public static int count = 0;

	public static void load() {
		try {
			levels = CCDirector.sharedDirector().getActivity().getAssets()
					.list(LEVEL_DIR);
			count = levels.length;
		} catch (IOException e) {
			Logger.e(TAG, e);
		}
	}

	public static String getLevelDataPath(int level) {
		if (TEST_LEVEL) {
			return "leveltest.txt";
		} else {
			return LEVEL_DIR + "/" + levels[level];
		}
	}
}
