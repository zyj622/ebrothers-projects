package com.ebrothers.forestrunner.common;

import com.ebrothers.forestrunner.layers.GameDelegate;
import com.ebrothers.forestrunner.manager.LocalDataManager;

public final class Game {
	public static final int LIFE_AMOUNT = 5;
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

	public static String more_url = "http://www.baidu.com";

	public static GameDelegate delegate;

	public static long score;
	public static boolean isWin;

	private static final float SPEED_EASY = 280f;// pixel/s
	private static final float SPEED_NORMAL = 320f;// pixel/s
	private static final float SPEED_HARD = 360f;// pixel/s
	private static final float JUMP_DURATION_NORMAL = .65f;// s
	private static final float JUMP_DURATION_UP_NORMAL = .5f;// s
	private static final float RUN_INTERVAL_NORMAL = .06f;// s
	private static final int BANABA_SCORE_NORMAL = 450;
	private static final int CHERRY_SCORE_NORMAL = 200;
	private static final float MAX_JUMP_HEIGHT = 100;
	private static final float MIN_JUMP_HEIGHT = 50 * Game.scale_ratio;

	public static float speed = SPEED_NORMAL;
	public static float jump_duration = JUMP_DURATION_NORMAL;
	public static float jump_duration_up = JUMP_DURATION_UP_NORMAL;
	public static float jump_max_height;
	public static float jump_min_height;
	public static float run_interval = RUN_INTERVAL_NORMAL;
	public static int banana_score = BANABA_SCORE_NORMAL;
	public static int cherry_score = CHERRY_SCORE_NORMAL;

	public static void init() {
		score = 0;
		isWin = false;
		String difficulty = (String) LocalDataManager.getInstance()
				.readSetting(LocalDataManager.DIFFICULTY_KEY, Constants.NORMAL);
		jump_max_height = MAX_JUMP_HEIGHT * Game.scale_ratio;
		jump_min_height = MIN_JUMP_HEIGHT * Game.scale_ratio;
		if (Constants.EASY.equals(difficulty)) {
			speed = SPEED_EASY * Game.scale_ratio;
			jump_duration = SPEED_NORMAL * JUMP_DURATION_NORMAL / SPEED_EASY;
			jump_duration_up = SPEED_NORMAL * JUMP_DURATION_UP_NORMAL
					/ SPEED_EASY;
			run_interval = SPEED_NORMAL * RUN_INTERVAL_NORMAL / SPEED_EASY;
			banana_score = BANABA_SCORE_NORMAL - 50;
			cherry_score = CHERRY_SCORE_NORMAL - 50;
		} else if (Constants.NORMAL.equals(difficulty)) {
			speed = SPEED_NORMAL * Game.scale_ratio;
			jump_duration = JUMP_DURATION_NORMAL;
			jump_duration_up = JUMP_DURATION_UP_NORMAL;
			run_interval = RUN_INTERVAL_NORMAL;
			banana_score = BANABA_SCORE_NORMAL;
			cherry_score = CHERRY_SCORE_NORMAL;
		} else if (Constants.HARD.equals(difficulty)) {
			speed = SPEED_HARD * Game.scale_ratio;
			jump_duration = SPEED_NORMAL * JUMP_DURATION_NORMAL / SPEED_HARD;
			jump_duration_up = SPEED_NORMAL * JUMP_DURATION_UP_NORMAL
					/ SPEED_HARD;
			run_interval = SPEED_NORMAL * RUN_INTERVAL_NORMAL / SPEED_HARD;
			banana_score = BANABA_SCORE_NORMAL + 50;
			cherry_score = CHERRY_SCORE_NORMAL + 50;
		}
	}

}
