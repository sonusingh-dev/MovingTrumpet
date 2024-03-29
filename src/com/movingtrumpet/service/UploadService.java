package com.movingtrumpet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UploadService extends Service {

	private static final String TAG = "UploadService";

	int mStartMode; // indicates how to behave if the service is killed
	IBinder mBinder; // interface for clients that bind
	boolean mAllowRebind; // indicates whether onRebind should be used

	public UploadService() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		// The service is being created
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.e(TAG, "In Method: onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// The service is starting, due to a call to startService()
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.e(TAG, "In Method: onStartCommand");
		return mStartMode;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// A client is binding to the service with bindService()
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// All clients have unbound with unbindService()
		return mAllowRebind;
	}

	@Override
	public void onRebind(Intent intent) {
		// A client is binding to the service with bindService(),
		// after onUnbind() has already been called
	}

	@Override
	public void onDestroy() {
		// The service is no longer used and is being destroyed
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.e(TAG, "In Method: onDestroy");
	}
}
