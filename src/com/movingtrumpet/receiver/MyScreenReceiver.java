package com.movingtrumpet.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.movingtrumpet.helper.DBAdapter;
import com.movingtrumpet.ui.MainActivity;

public class MyScreenReceiver extends BroadcastReceiver {

	// private static final String TAG = "MyScreenReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		// Log.e(TAG, "In Method:  onReceive");

		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			new DBAdapter(context).close();
			Activity activity = (Activity) context;
			activity.finish();

		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

		} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)
				|| intent.getAction().equals(
						Intent.ACTION_MEDIA_SCANNER_FINISHED)) {

			intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);			

			// if (Utility.isSDCardExists()) {
			// intent = new Intent(context, MainActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(intent);
			// }
		}
	}
}
