package com.ebrothers.forestrunner.common;

import com.ebrothers.forestrunner.layers.GameDelegate;

public final class Game {

	/**
	 * Current level index, based 0.
	 */
	public static int current_level = 0;

	public static float scale_ratio = 1f;
	public static float groundH_y;
	public static float groundM_y;
	public static float groundL_y;

	public static float scale_ratio_x = 1f;
	public static float scale_ratio_y = 1f;

	public static String more_url = "http://www.baidu.coms";

	public static GameDelegate delegate;

}
