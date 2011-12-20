package com.ebrothers.forestrunner.common;

import android.util.Log;

public class Logger {

	public static final boolean LOGD = false;
	public static final boolean LOGE = false;

	public static void d(String tag, String msg) {
		if (LOGD) {
			Log.d(tag, "###### " + msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LOGE) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, Throwable tr) {
		if (LOGE) {
			Log.e(tag, "###### ERROR", tr);
		}
	}
}
