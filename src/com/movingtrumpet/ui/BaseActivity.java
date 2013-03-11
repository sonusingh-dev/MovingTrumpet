package com.movingtrumpet.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.movingtrumpet.helper.FileHelper;
import com.movingtrumpet.helper.MyIntent;
import com.movingtrumpet.helper.Utility;
import com.movingtrumpet.receiver.MyScreenReceiver;

public class BaseActivity extends Activity {

	// private static final String TAG = "BaseActivity";

	protected FileHelper fileHelper;
	private MyScreenReceiver screenReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Log.e(TAG, "In Method: onCreate");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		fileHelper = new FileHelper(this);

		IntentFilter screenfilter = new IntentFilter();
		screenfilter.addAction(Intent.ACTION_SCREEN_ON);
		screenfilter.addAction(Intent.ACTION_SCREEN_OFF);
		screenfilter.setPriority(500);

		screenReceiver = new MyScreenReceiver();
		registerReceiver(screenReceiver, screenfilter);

		if (!Utility.isServiceRunning(this)) {
			sendBroadcast(new Intent(MyIntent.ACTION_DOWNLOAD_SCHEDULE));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenReceiver);
	}
}
