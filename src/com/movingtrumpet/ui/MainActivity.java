package com.movingtrumpet.ui;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.movingtrumpet.helper.FileHelper;
import com.movingtrumpet.helper.Utility;
import com.movingtrumpet.helper.WebHelper;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private String mDeviceId;

	private AlertDialog mAlertDialog;

	SharedPreferences mPreferences;
	SharedPreferences.Editor mEditor;

	/**
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPreferences = getSharedPreferences(FileHelper.PREF_FILE_NAME,
				MODE_PRIVATE);
		mEditor = mPreferences.edit();
		mDeviceId = mPreferences.getString(FileHelper.KEY_DEVICE_ID, null);
		Log.e(TAG, "In Method: onCreate: mDeviceId: " + mDeviceId);
		if (mDeviceId == null) {
			getDeviceResponse();
		} else {
			moveToNext();
		}
	}

	private void getDeviceResponse() {

		try {

			mDeviceId = Utility.getDeviceId(this);
			WebHelper webHelper = new WebHelper(this);
			String response = webHelper.getResponse(WebHelper.METHOD_REGISTER);
			if (response == null) {
				Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG)
						.show();
				finish();
				return;
			}

			String[] split = response.split("\\(")[1].split("\\)");
			response = split[0];

			JSONObject jsonObject = new JSONObject(response);
			String success = jsonObject.optString(WebHelper.JKEY_SUCCESS, null);
			if (success != null) {
				if (Boolean.parseBoolean(success)) {
					String data = jsonObject.optString(WebHelper.JKEY_DATA,
							null);
					if (data != null) {
						JSONObject jsonData = new JSONObject(data);
						if (jsonData != null) {
							String deviceId = jsonData.optString(
									WebHelper.JKEY_DEVICE_ID, null);
							if (!mDeviceId.equals(deviceId)) {
								Toast.makeText(this, "Registration Failed",
										Toast.LENGTH_LONG).show();
								finish();
								return;
							}
						}
					}
				}
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

		showDeviceIdDialog();
	}

	public void showDeviceIdDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Device Registered");
		builder.setMessage("Device Id: " + Utility.getDeviceId(this));

		final CheckBox chkShowDailog = new CheckBox(this);
		chkShowDailog.setText("Never show again");

		builder.setView(chkShowDailog);

		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (chkShowDailog.isChecked()) {
					mEditor.putString(FileHelper.KEY_DEVICE_ID, mDeviceId);
					mEditor.commit();
				}
				moveToNext();
			}
		});

		mAlertDialog = builder.create();
		mAlertDialog.show();
	}

	private void moveToNext() {

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, BeginingAct.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			mAlertDialog.dismiss();
		}
	}
}