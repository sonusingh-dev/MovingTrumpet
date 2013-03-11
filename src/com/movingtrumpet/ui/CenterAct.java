package com.movingtrumpet.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.movingtrumpet.helper.DBAdapter;
import com.movingtrumpet.helper.DataHelper;
import com.movingtrumpet.helper.FileHelper;
import com.movingtrumpet.helper.MyIntent;
import com.movingtrumpet.helper.XmlParser;

public class CenterAct extends BaseActivity {

	private final long INTERVAL = 10000;
	private final long DURATION = 100000;

	// private static final String TAG = "CenterAct";

	private int videoIndex;
	private int bannerRightIndex;

	private long masterMaxId;
	private long masterMinId;

	private String adsFile;

	private DBAdapter dbAdapter;

	private List<String> videoList;

	// Right Banner List and Ads
	private List<String> adsList;
	private List<String> bannerRightList;

	private RightBannerTimer rightBannerTimer;

	private ScrollTextView marqueeText;

	private ImageView imgRight;

	private VideoView videoView;

	boolean canParse = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.e(TAG, "In Method: onCreate");

		init();
		
		dbAdapter = new DBAdapter(this);
		videoList = new ArrayList<String>();
		bannerRightList = new ArrayList<String>();
		adsList = new ArrayList<String>();
		
		imgRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (adsFile != null) {
					Intent intent = new Intent();
					intent.setClass(CenterAct.this, AdsDetailAct.class);
					intent.putExtra(DataHelper.FILE_NAME, adsFile);
					startActivity(intent);
				}
			}
		});

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
				if (videoIndex >= videoList.size()) {
					videoIndex = 0;
				}
				playVideo();
			}
		});

		rightBannerTimer = new RightBannerTimer(DURATION, INTERVAL);

		// get data
		getMasterRowId();		
		getBannerRight();
		getVideos();
		if (canParse)
			getRssFeed();

		// play data
		playRightBanner();
		playVideo();
	}

	private void init() {

		LinearLayout lLinLayout = new LinearLayout(this);
		lLinLayout.setId(1);
		lLinLayout.setOrientation(LinearLayout.VERTICAL);
		lLinLayout.setGravity(Gravity.CENTER);
		lLinLayout.setBackgroundColor(Color.BLACK);

		LayoutParams lLinLayoutParms = new LayoutParams(800, 480);
		lLinLayout.setLayoutParams(lLinLayoutParms);

		this.setContentView(lLinLayout);

		RelativeLayout lRelLayout = new RelativeLayout(this);
		lRelLayout.setId(2);
		lRelLayout.setGravity(Gravity.CENTER);
		lRelLayout.setBackgroundColor(Color.BLACK);
		android.widget.RelativeLayout.LayoutParams lRelLayoutParms = new android.widget.RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		lRelLayout.setLayoutParams(lRelLayoutParms);
		lLinLayout.addView(lRelLayout);

		imgRight = new ImageView(this);
		imgRight.setId(4);
		android.widget.RelativeLayout.LayoutParams lImg2ViewLayoutParams = new android.widget.RelativeLayout.LayoutParams(
				240, ViewGroup.LayoutParams.FILL_PARENT);
		lImg2ViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lImg2ViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lImg2ViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		imgRight.setLayoutParams(lImg2ViewLayoutParams);
		lRelLayout.addView(imgRight);

		videoView = new VideoView(this);
		videoView.setId(3);
		android.widget.RelativeLayout.LayoutParams lVidViewLayoutParams = new android.widget.RelativeLayout.LayoutParams(
				560, 480);
		lVidViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lVidViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lVidViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		lVidViewLayoutParams.addRule(RelativeLayout.LEFT_OF, 4);
		videoView.setLayoutParams(lVidViewLayoutParams);
		lRelLayout.addView(videoView);

		marqueeText = new ScrollTextView(this);
		marqueeText.setId(5);
		android.widget.RelativeLayout.LayoutParams lTxtViewLayoutParams = new android.widget.RelativeLayout.LayoutParams(
				560, ViewGroup.LayoutParams.WRAP_CONTENT);
		lTxtViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		marqueeText.setBackgroundColor(Color.RED);
		marqueeText.setPadding(1, 1, 1, 1);
		marqueeText.setTextSize(14);
		marqueeText.setTypeface(null, Typeface.BOLD_ITALIC);
		marqueeText.setLayoutParams(lTxtViewLayoutParams);
		lRelLayout.addView(marqueeText);

	}

	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			getMasterRowId();			
			getBannerRight();
			getVideos();

			playRightBanner();
			if (!videoView.isPlaying()) {
				playVideo();
			}			
		}
	};

	private BroadcastReceiver rssFeedsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(MyIntent.ACTION_STOP_PARSING)) {
				canParse = false;
				marqueeText.setVisibility(View.INVISIBLE);
			} else if (intent.getAction()
					.equals(MyIntent.ACTION_RESUME_PARSING)) {
				canParse = true;
				marqueeText.setVisibility(View.VISIBLE);
				getRssFeed();
			}

		}
	};

	@Override
	public void onResume() {
		super.onResume();
		videoView.resume();
		registerReceiver(downloadReceiver, new IntentFilter(
				MyIntent.ACTION_VIEW));
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MyIntent.ACTION_STOP_PARSING);
		intentFilter.addAction(MyIntent.ACTION_RESUME_PARSING);
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
		registerReceiver(rssFeedsReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		videoView.suspend();
		unregisterReceiver(downloadReceiver);
		unregisterReceiver(rssFeedsReceiver);
	}

	private void getRssFeed() {

		String response = fileHelper.readFile(this, FileHelper.DIR_RSS_FEED,
				FileHelper.RSS_FEED_FILE);

		List<String> rssFeeds = null;
		if (response != null)
			try {
				rssFeeds = XmlParser.getRssFeeds(response);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (rssFeeds != null && !rssFeeds.isEmpty()) {
			marqueeText.setList(rssFeeds);
			marqueeText.startScroll();
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
		Cursor cursor = dbAdapter.getAll(
				DBAdapter.DBTABLE_VIDEO_2,
				new String[] { DBAdapter.KEY_VIDEO_NAME,
						DBAdapter.KEY_BANNER_NAME },
				DBAdapter.KEY_FK_ROWID + "=? AND " + DBAdapter.KEY_STATUS
						+ "=?",
				new String[] { String.valueOf(masterMaxId),
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

			String videoPath = fileHelper.getFilePath(FileHelper.DIR_VIDEO_2,
					videoFile);
			videos.add(videoPath);
		}

		if (!videos.isEmpty()) {
			videoList = videos;
		}

		cursor.close();

	}

	private void getBannerRight() {

		if (masterMaxId == 0) {
			return;
		}

		List<String> images = new ArrayList<String>();
		List<String> ads = new ArrayList<String>();

		Cursor cursor = dbAdapter.getAll(
				DBAdapter.DBTABLE_BANNER_RIGHT,
				new String[] { DBAdapter.KEY_BANNER_NAME,
						DBAdapter.KEY_ADS_NAME },
				DBAdapter.KEY_FK_ROWID + "=? AND " + DBAdapter.KEY_STATUS
						+ "=?",
				new String[] { String.valueOf(masterMaxId),
						String.valueOf(DBAdapter.DATA_AVAILABLE) },
				DBAdapter.KEY_BANNER_SEQUENCE);

		// check data if not available then move to previous one
		if (cursor != null && cursor.getCount() == 0) {
			masterMaxId = masterMaxId - 1;
			if (masterMaxId < masterMinId) {
				masterMaxId = 0;
				return;
			}
			cursor.close();
			getBannerRight();
		}

		while (cursor.moveToNext()) {

			String imageFile = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_BANNER_NAME));
			String adsFile = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_ADS_NAME));

			String imagePath = fileHelper.getFilePath(
					FileHelper.DIR_BANNER_RIGHT, imageFile);
			String adsPath = fileHelper.getFilePath(
					FileHelper.DIR_BANNER_RIGHT, adsFile);
			images.add(imagePath);
			ads.add(adsPath);
		}

		if (!images.isEmpty()) {
			bannerRightList = images;
			adsList = ads;
		}

		cursor.close();
	}

	private void playVideo() {

		if (videoList.isEmpty()) {
			return;
		}

		String videoFile = videoList.get(videoIndex);
		videoView.setVideoPath(videoFile);
		videoView.requestFocus();
		videoView.start();
	}

	private void playRightBanner() {

		if (bannerRightList.isEmpty()) {
			return;
		}

		rightBannerTimer.cancel();
		rightBannerTimer.start();
	}

	class RightBannerTimer extends CountDownTimer {

		public RightBannerTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			bannerRightIndex = 0;
			rightBannerTimer.start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			adsFile = null;
			if (bannerRightIndex >= bannerRightList.size()) {
				bannerRightIndex = 0;
			}

			adsFile = adsList.get(bannerRightIndex);
			String imageFile = bannerRightList.get(bannerRightIndex);
			Bitmap myBitmap = BitmapFactory.decodeFile(imageFile);
			imgRight.setImageBitmap(myBitmap);
			bannerRightIndex++;
		}
	}
}
