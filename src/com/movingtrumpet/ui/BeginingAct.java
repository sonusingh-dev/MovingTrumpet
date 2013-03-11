package com.movingtrumpet.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.VideoView;

import com.movingtrumpet.R;
import com.movingtrumpet.helper.DBAdapter;
import com.movingtrumpet.helper.FileHelper;
import com.movingtrumpet.helper.MyIntent;

public class BeginingAct extends BaseActivity {

	// private static final String TAG = "BeginingAct";

	private int videoIndex = 0;

	private long masterMaxId;
	private long masterMinId;

	private DBAdapter dbAdapter;

	private List<String> videoList;

	private VideoView videoView;

	private ProgressDialog pd;

	/**
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Log.e(TAG, "In Method: onCreate");
		setContentView(R.layout.main);

		dbAdapter = new DBAdapter(this);

		videoList = new ArrayList<String>();

		videoView = (VideoView) findViewById(R.id.videoView);

		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return false;
			}
		});

		videoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				videoIndex++;
				if (videoIndex < videoList.size()) {
					playVideo();
				} else {
					Intent intent = new Intent();
					intent.setClass(BeginingAct.this, CenterAct.class);
					startActivity(intent);
				}
			}
		});

		pd = new ProgressDialog(this);
		pd.setMessage("Downloading. Please wait...");
		pd.setCancelable(false);

		getMasterRowId();
		getVideos();
		playVideo();
	}

	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			getMasterRowId();
			getVideos();
			if (!videoView.isPlaying()) {
				playVideo();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(downloadReceiver, new IntentFilter(
				MyIntent.ACTION_VIEW));

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(downloadReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pd.isShowing()) {
			pd.dismiss();
		}
	}

	private void getMasterRowId() {

		dbAdapter.open();

		Cursor cursor = dbAdapter.getMax(DBAdapter.DBTABLE_MASTER,
				DBAdapter.KEY_PK_ROWID);
		if (cursor.moveToNext()) {
			masterMaxId = cursor.getLong(0);			
		}
		cursor.close();

		cursor = dbAdapter.getMin(DBAdapter.DBTABLE_MASTER,
				DBAdapter.KEY_PK_ROWID);
		if (cursor.moveToNext()) {
			masterMinId = cursor.getLong(0);			
		}
		cursor.close();
	}

	private void getVideos() {

		if (masterMaxId == 0) {
			return;
		}
		
		List<String> videos = new ArrayList<String>();
		Cursor cursor = dbAdapter.getAll(DBAdapter.DBTABLE_VIDEO_1,
				new String[] { DBAdapter.KEY_VIDEO_NAME },
				DBAdapter.KEY_FK_ROWID + "=? AND " + DBAdapter.KEY_STATUS
						+ "=?", new String[] { String.valueOf(masterMaxId),
						String.valueOf(DBAdapter.DATA_AVAILABLE) },
				DBAdapter.KEY_VIDEO_SEQUENCE);

		// check data if not available then move to previous one
		if (cursor != null && cursor.getCount() == 0) {
			masterMaxId = masterMaxId - 1;
			if (masterMaxId < masterMinId) {
				masterMaxId = 0;
				return;
			}
			cursor.close();
			getVideos();
		}

		while (cursor.moveToNext()) {
			String videoFile = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_VIDEO_NAME));
			String filePath = fileHelper.getFilePath(FileHelper.DIR_VIDEO_1,
					videoFile);
			videos.add(filePath);
		}

		if (!videos.isEmpty()) {
			videoList = videos;
		}

		cursor.close();
	}

	private void playVideo() {

		if (!pd.isShowing()) {
			pd.show();
		}

		if (videoList.isEmpty()) {
			return;
		}

		pd.dismiss();

		String videoFile = videoList.get(videoIndex);
		videoView.setVideoPath(videoFile);
		videoView.requestFocus();
		videoView.start();
	}

}