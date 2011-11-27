package com.ebrothers.forestrunner.data;

import java.io.IOException;
import java.io.InputStream;

import org.cocos2d.nodes.CCDirector;

import com.ebrothers.forestrunner.common.Logger;

public class LevelDataParser {
	private static final String TAG = "LevelDataParser";

	/**
	 * Parse level data with default parsing strategy(TxtDataParseStrategy).
	 */
	public static LevelData parse(String fileName) {
		return parse(getDefaultStrategy(), fileName);
	}

	/**
	 * Parse level data with given parsing strategy.
	 */
	public static LevelData parse(ParseStrategy strategy, String fileName) {
		assert (strategy != null);
		InputStream is = null;
		try {
			is = CCDirector.sharedDirector().getActivity().getAssets()
					.open(fileName);
			return strategy.parse(is);
		} catch (IOException e) {
			Logger.e(TAG, e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Just give the default strategy to parse level data.
	 * 
	 * @return TxtDataParseStrategy
	 */
	private static ParseStrategy getDefaultStrategy() {
		return new TxtDataParseStrategy();
	}
}
