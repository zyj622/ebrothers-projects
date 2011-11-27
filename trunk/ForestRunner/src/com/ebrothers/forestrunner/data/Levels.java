package com.ebrothers.forestrunner.data;

import java.io.IOException;

import org.cocos2d.nodes.CCDirector;

import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.common.Logger;

public final class Levels {
	private static final String LEVEL_DIR = "level";
	private static final String TAG = "Levels";
	private static String[] levels;

	public static void load() {
		try {
			levels = CCDirector.sharedDirector().getActivity().getAssets()
					.list(LEVEL_DIR);
		} catch (IOException e) {
			Logger.e(TAG, e);
		}
	}

	public static String getCurrentLevelPath() {
		return LEVEL_DIR + "/" + levels[Globals.current_level];
	}
}
