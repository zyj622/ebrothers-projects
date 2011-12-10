package com.ebrothers.forestrunner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ebrothers.forestrunner.manager.SoundManager;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		new BackgroundWorker().execute();
	}

	class BackgroundWorker extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			SoundManager.sharedSoundManager().preload(getApplicationContext());
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), ForestRunnerActivity.class);
			startActivity(intent);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			finish();
		}
	}
}
