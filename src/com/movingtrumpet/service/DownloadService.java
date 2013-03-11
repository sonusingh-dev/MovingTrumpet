package com.movingtrumpet.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;

import com.movingtrumpet.helper.DataHelper;
import com.movingtrumpet.helper.MyIntent;
import com.movingtrumpet.helper.Utility;

public class DownloadService extends Service {

	// private static final String TAG = "DownloadService";

	private static final int NEW_DATA = 1;
	private static final int OLD_DATA = 2;

	// interface for clients that bind
	private IBinder mBinder;

	private Intent intent;

	private DownloadTask downloadTask;
	private static DataHelper dataHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		// The service is being created
		// Log.e(TAG, "In Method: onCreate");
		intent = new Intent(MyIntent.ACTION_VIEW);
		dataHelper = new DataHelper(this);
		registerReceiver(myNetworkReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private BroadcastReceiver myNetworkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Utility.isNetworkAvailable(context)) {
				startTask(OLD_DATA);
			} else {
				if (downloadTask != null) {
					downloadTask.cancel(true);
				}
			}
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Utility.isNetworkAvailable(this)) {
			startTask(NEW_DATA);
		}
	}

	private void startTask(int key) {
		
		if (downloadTask == null) {
			downloadTask = new DownloadTask();
			downloadTask.execute(new Integer(key));
		}		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// A client is binding to the service with bindService()
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// The service is no longer used and is being destroyed
		dataHelper.closeDb();
		unregisterReceiver(myNetworkReceiver);
	}
	
	private class DownloadTask extends AsyncTask<Integer, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			boolean state = false;
			int key = params[0];

			try {
				switch (key) {
				case NEW_DATA:
					state = dataHelper.checkNewResponse();
					if (state) {
						sendBroadcast(new Intent(MyIntent.ACTION_STOP_PARSING));
						dataHelper.checkRssFeed();
						sendBroadcast(new Intent(MyIntent.ACTION_RESUME_PARSING));
						dataHelper.deleteOldResponse();
						return new Boolean(state);
					}
					break;
				case OLD_DATA:
					state = dataHelper.checkOldResponse();
					if (state) {
						return new Boolean(state);
					}
					break;
				default:
					break;
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			downloadTask = null;
			if (result != null) {
				sendBroadcast(intent);
			}
		}

		@Override
		protected void onCancelled() {
			downloadTask = null;
		}
	}
}
