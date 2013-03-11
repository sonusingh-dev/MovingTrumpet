package com.movingtrumpet.receiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.movingtrumpet.helper.MyIntent;

public class MyScheduleReceiver extends BroadcastReceiver {

	// Restart service every 3601 (1 hour) seconds
	private static final long REPEAT_TIME = 1000 * 901;

	// private static final String TAG = "MyScheduleReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		// Log.e(TAG, "In Method: onReceive");
		if (intent.getAction().equals(MyIntent.ACTION_DOWNLOAD_SCHEDULE)) {

			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(context, MyStartServiceReceiver.class);
			PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
			Calendar cal = Calendar.getInstance();

			// Start 3 seconds after boot completed
			cal.add(Calendar.SECOND, 1);

			// Fetch every 901 seconds
			// InexactRepeating allows Android to optimize the energy
			// consumption
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					cal.getTimeInMillis(), REPEAT_TIME, pending);
			// if (Utility.isSDCardExists()) {
			// alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
			// cal.getTimeInMillis(), REPEAT_TIME, pending);
			// }
		}
	}
}