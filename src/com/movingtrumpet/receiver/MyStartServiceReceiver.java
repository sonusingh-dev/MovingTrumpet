package com.movingtrumpet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.movingtrumpet.service.DownloadService;

public class MyStartServiceReceiver extends BroadcastReceiver {

	// private static final String TAG = "MyStartServiceReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log.e(TAG, "In Method: onReceive");
		Intent service = new Intent(context, DownloadService.class);
		context.startService(service);
	}
}