package com.movingtrumpet.helper;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlParser {

	// private static final String TAG = "XmlParser";

	public static List<String> getRssFeeds(String response)
			throws XmlPullParserException, IOException {

		// Log.e(TAG, "In Method: getRssFeed");

		String item = null;
		List<String> rssFeeds = null;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(new StringReader(response));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;

			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				rssFeeds = new ArrayList<String>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("item")) {
					item = "";
				} else if (item != null) {
					if (name.equalsIgnoreCase("title")) {
						item = item + parser.nextText();
					} else if (name.equalsIgnoreCase("description")) {
						item = item + ": " + parser.nextText();
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("item") && item != null) {
					rssFeeds.add(item);
					item = null;
				}
				break;
			}
			eventType = parser.next();
		}

		return rssFeeds;
	}

}
