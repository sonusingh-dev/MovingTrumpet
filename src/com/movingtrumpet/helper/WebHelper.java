package com.movingtrumpet.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class WebHelper {

	private static final String TAG = "WebHelper";

	public static final String JKEY_SUCCESS = "Success";
	public static final String JKEY_HASDATA = "HasData";
	public static final String JKEY_DATA = "Data";
	public static final String JKEY_MESSAGE = "Message";
	public static final String JKEY_LOGID = "LogId";
	public static final String JKEY_DEVICE_ID = "DeviceID";

	// Json Tag for startup Video
	public static final String JKEY_NAME = "Name";
	public static final String JKEY_URL = "Url";
	public static final String JKEY_SIZE = "Size";
	public static final String JKEY_SEQUENCE = "Sequence";

	// Json Tag for startup Video
	public static final String JKEY_VIDEO_NAME = "VideoName";
	public static final String JKEY_VIDEO_URL = "VideoUrl";
	public static final String JKEY_VIDEO_SIZE = "VideoSize";
	public static final String JKEY_VIDEO_SEQUENCE = "VideoSeqNo";
	public static final String JKEY_VIDEO_TYPE = "Type";

	// Json Tag for startup Video
	public static final String JKEY_IMAGES = "Images";
	public static final String JKEY_IMAGES_BOTTOM = "BottomImages";
	public static final String JKEY_IMAGE_NAME = "ImageName";
	public static final String JKEY_IMAGE_URL = "ImageUrl";
	public static final String JKEY_IMAGE_SIZE = "ImageSize";
	public static final String JKEY_IMAGE_SEQUENCE = "ImageSeqNo";

	// Json Tag for startup Video
	public static final String JKEY_BANNER_NAME = "Name";
	public static final String JKEY_BANNER_URL = "URL";
	public static final String JKEY_BANNER_SIZE = "Size";
	public static final String JKEY_BANNER_SEQUENCE = "";

	public static final String METHOD_REGISTER = "RegisterDevice";
	public static final String METHOD_VIDEO_1 = "GetBeginingVideos";
	public static final String METHOD_VIDEO_2 = "getCenterVideos";
	public static final String METHOD_BANNER_DEFAULT = "getBottomSideImgaes";
	public static final String METHOD_BANNER_RIGHT = "getRightSideImgaes";
	public static final String METHOD_RSS_FEEDS = "getRssFeeds";

	private static final String URL_SEP = "?callback=Json&Id=";

	// RSS FEEDS URL
	public static final String URL_RSS_FEED = "http://www.espncricinfo.com/rss/content/story/feeds/6.xml";

	// web service URLs
	private static final String URL = "http://movingtrumpate.aspnetdevelopment.in/api/";
	public static final String URL_REGISTER_DEVICE = "http://movingtrumpate.aspnetdevelopment.in/api/RegisterDevice?callback=Json&id=sys2";
	public static final String URL_VIDEO_1 = "http://movingtrumpate.aspnetdevelopment.in/api/GetBeginingVideos?callback=json&Id=75679310b57edea0";
	public static final String URL_VIDEO_2 = "http://movingtrumpate.aspnetdevelopment.in/api/getCenterVideos?callback=Json&Id=75679310b57edea0";
	public static final String URL_BANNER_DEFAULT = "http://movingtrumpate.aspnetdevelopment.in/api/getBottomSideImgaes?callback=json&Id=75679310b57edea0";
	public static final String URL_BANNER_RIGHT = "http://movingtrumpate.aspnetdevelopment.in/api/getRightSideImgaes?callback=Json&Id=75679310b57edea0";

	private String mDeviceId = null;

	private Context mContext;

	public WebHelper(Context context) {
		mContext = context;
		mDeviceId = Utility.getDeviceId(context);
	}

	private String getURL(String method) {
		String url = URL + method + URL_SEP + "75679310b57edea0";
		Log.i(TAG, "In Method: getURL: url: " + url);
		return url;
	}

	public String getResponse(String method) throws ClientProtocolException,
			IOException {

		if (!Utility.isNetworkAvailable(mContext)) {
			return null;
		}

		// Instantiate an HttpClient
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(getURL(method));

		// Set HTTP parameters
		HttpResponse response = httpClient.execute(request);

		// Get hold of the response entity (-> the data):
		HttpEntity responseEntity = response.getEntity();

		if (responseEntity != null) {
			// Read the content stream
			InputStream input = responseEntity.getContent();

			// convert content stream to a String
			String result = FileHelper.convertStreamToString(input);
			input.close();

			// Transform the String into a JSONObject
			return result;
		}

		return null;

	}

	public InputStream download(String URL) throws IOException {

		if (!Utility.isNetworkAvailable(mContext)) {
			return null;
		}

		// set the download URL, a url that points to a file on the internet
		// this is the file to be downloaded
		URL url = new URL(URL);

		// create the new connection
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();

		// set up some things on the connection
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);

		// and connect!
		urlConnection.connect();

		// this will be used in reading the data from the internet
		InputStream inputStream = urlConnection.getInputStream();
		return inputStream;
	}

	public boolean getStatus(String response) throws XmlPullParserException,
			IOException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();

		xpp.setInput(new StringReader(response));
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.TEXT) {
				boolean status = Boolean.parseBoolean(xpp.getText());
				return status;
			}
			eventType = xpp.next();
		}
		return false;
	}

}
