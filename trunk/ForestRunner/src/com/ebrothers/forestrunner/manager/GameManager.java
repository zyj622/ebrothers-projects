package com.ebrothers.forestrunner.manager;


public final class GameManager {
	public static float runner_x = 0;
	private float[] _x;
	private float[] _y;
	private int bp_index = 0;
	
	public float getRunnerY() {
		assert (bp_index < _x.length);
		if (runner_x < _x[bp_index]) {
			return _y[bp_index];
		} else {
			bp_index++;
			return _y[bp_index];
		}
	}

	public void setBreakPoints(float[] x, float[] y) {
		assert (x.length == y.length);
		_x = x;
		_y = y;
	}

}
