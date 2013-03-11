package com.movingtrumpet.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.movingtrumpet.R;
import com.movingtrumpet.helper.DataHelper;

public class AdsDetailAct extends BaseActivity {

	// private static final String TAG = "AdsDetailAct";

	private final long DURATION = 30000;

	private String fileName;
	private DefaultTimer defaultTimer;

	private ImageButton btnBack;
	private WebView mWebView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ads_detail);
		// Log.e(TAG, "In Method: onCreate");

		fileName = getIntent().getStringExtra(DataHelper.FILE_NAME);

		btnBack = (ImageButton) findViewById(R.id.btnBack);
		mWebView = (WebView) findViewById(R.id.webView);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		mWebView.getSettings().setAllowFileAccess(true);

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				defaultTimer.cancel();
				finish();
			}
		});

		mWebView.loadUrl("file://" + fileName);
		defaultTimer = new DefaultTimer(DURATION, DURATION);
		defaultTimer.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	class DefaultTimer extends CountDownTimer {

		public DefaultTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			finish();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// do nothing
		}
	}
}
