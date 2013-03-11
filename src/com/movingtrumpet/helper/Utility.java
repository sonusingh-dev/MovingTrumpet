package com.movingtrumpet.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {

	// private static final String TAG = "Utility";

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable()) {
			return true;
		}
		return false;
	}

	public static boolean isSDCardExists() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		if (mExternalStorageAvailable && mExternalStorageWriteable)
			return true;
		else
			return false;

	}

	public static boolean isServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (MyIntent.ACTION_DOWNLOAD_SERVICE.equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isActivityRunning(Context context) {

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		for (int i = 0; i < services.size(); i++) {

			if (MyIntent.ACTION_DOWNLOAD_SERVICE
					.equals(services.get(i).topActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static String getIMEI(Context context) {

		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String androidDeviceIMEI = tManager.getDeviceId();
		return androidDeviceIMEI;
	}

	public static String getDeviceId(Context context) {

		String androidDeviceId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return androidDeviceId;
	}

	public static String getStartDate90() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();

		int day = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.set(Calendar.DAY_OF_YEAR, day - 90);

		day = calendar.get(Calendar.DAY_OF_YEAR);

		Date date = calendar.getTime();

		String startDate = sdf.format(date) + " 00:00:00";

		return startDate;
	}

	public static String getStartDate30() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();

		int day = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.set(Calendar.DAY_OF_YEAR, day - 30);

		day = calendar.get(Calendar.DAY_OF_YEAR);

		Date date = calendar.getTime();

		String startDate = sdf.format(date) + " 00:00:00";

		return startDate;
	}

	public static String getEndDate() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();

		int day = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.set(Calendar.DAY_OF_YEAR, day + 1);

		day = calendar.get(Calendar.DAY_OF_YEAR);

		Date date = calendar.getTime();

		String endDate = sdf.format(date) + " 00:00:00";

		return endDate;
	}

	/**
	 * method is used to parse html tag
	 * 
	 * @param HTML
	 * @return String
	 */
	public static String stipHtml(String html) {
		String temp = Html.fromHtml(html).toString();
		if (temp != null) {
			temp = temp.trim();
		}
		return temp;
	}

	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public static String anyTypeConversion(String data) {

		if (data.contains("anyType{}")) {
			data = null;
		}
		return data;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

}