package com.movingtrumpet.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DataHelper {

	private static final String TAG = "DataHelper";

	public static final String FILE_NAME = "fileName";

	private boolean recordState = true;

	private long masterMaxId;

	private String video1Response;
	private String video2Response;
	private String bannerRightResponse;
	private String rssFeedsResponse;

	private DBAdapter dbAdapter;
	private FileHelper fileHelper;
	private WebHelper webHelper;

	public DataHelper(Context context) {
		dbAdapter = new DBAdapter(context);
		fileHelper = new FileHelper(context);
		webHelper = new WebHelper(context);
	}

	public void closeDb() {
		dbAdapter.close();
	}

	// function to check the new response and
	// downloads the files
	public boolean checkNewResponse() throws ClientProtocolException,
			IOException, JSONException {

		Log.e(TAG, "In Method: checkNewResponse");
		recordState = true;
		video1Response = getFeedResponse(WebHelper.METHOD_VIDEO_1);
		video2Response = getFeedResponse(WebHelper.METHOD_VIDEO_2);
		bannerRightResponse = getFeedResponse(WebHelper.METHOD_BANNER_RIGHT);

		if ((video1Response != null) && (video2Response != null)
				&& (bannerRightResponse != null)) {

			dbAdapter.open();
			ContentValues values = new ContentValues();
			values.putNull(DBAdapter.KEY_FEED_NAME);
			masterMaxId = dbAdapter.insert(DBAdapter.DBTABLE_MASTER, values);

			// fetches the web service response
			// and store in the database
			Log.e(TAG, "In Method: checkNewResponse: getResponse");
			getVideo1Response();
			getVideo2Response();
			getBannerRightResponse();

			// Downloads the files and save it on SDcard,
			// and store its path in Database
			Log.e(TAG, "In Method: checkNewResponse: saveResponse");
			saveVideo1Response();
			saveVideo2Response();
			saveBannerRightResponse();

			if (recordState) {
				values = new ContentValues();
				values.put(DBAdapter.KEY_STATUS, DBAdapter.DATA_AVAILABLE);
				dbAdapter.update(DBAdapter.DBTABLE_MASTER, values,
						DBAdapter.KEY_PK_ROWID + "=?",
						new String[] { String.valueOf(masterMaxId) });
			}
			Log.e(TAG, "In Method: checkNewResponse: Done");
			return true;
		}
		return false;
	}

	private String getFeedResponse(String method) {

		Log.e(TAG, "In Method: getFeedResponse");

		try {

			String response = webHelper.getResponse(method);
			if (response == null) {
				return null;
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
						Log.i(TAG, "In Method: getFeedResponse: data: " + data);
						return data;
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

		return null;
	}

	private void getVideo1Response() throws ClientProtocolException,
			IOException, JSONException {

		JSONArray jsonVideo1Response = new JSONArray(video1Response);
		for (int i = 0; i < jsonVideo1Response.length(); i++) {
			JSONObject jsonVideo = (JSONObject) jsonVideo1Response.get(i);
			String videoName = jsonVideo.getString(WebHelper.JKEY_VIDEO_NAME);
			String videoURL = jsonVideo.getString(WebHelper.JKEY_VIDEO_URL);
			String videoSize = jsonVideo.getString(WebHelper.JKEY_VIDEO_SIZE);
			String videoSequence = jsonVideo
					.getString(WebHelper.JKEY_VIDEO_SEQUENCE);

			ContentValues values = new ContentValues();
			values.put(DBAdapter.KEY_VIDEO_NAME, videoName);
			values.put(DBAdapter.KEY_VIDEO_SIZE, videoSize);
			values.put(DBAdapter.KEY_VIDEO_URL, videoURL);
			values.put(DBAdapter.KEY_VIDEO_SEQUENCE, videoSequence);
			values.put(DBAdapter.KEY_FK_ROWID, masterMaxId);
			dbAdapter.insert(DBAdapter.DBTABLE_VIDEO_1, values);
		}
	}

	private void getVideo2Response() throws ClientProtocolException,
			IOException, JSONException {

		JSONArray jsonVideo2Response = new JSONArray(video2Response);
		for (int i = 0; i < jsonVideo2Response.length(); i++) {
			JSONObject jsonVideo = (JSONObject) jsonVideo2Response.get(i);
			String videoName = jsonVideo.getString(WebHelper.JKEY_VIDEO_NAME);
			String videoURL = jsonVideo.getString(WebHelper.JKEY_VIDEO_URL);
			String videoSize = jsonVideo.getString(WebHelper.JKEY_VIDEO_SIZE);
			String videoSequence = jsonVideo
					.getString(WebHelper.JKEY_VIDEO_SEQUENCE);
			String videoType = jsonVideo.getString(WebHelper.JKEY_VIDEO_TYPE);

			ContentValues values = new ContentValues();
			values.put(DBAdapter.KEY_VIDEO_NAME, videoName);
			values.put(DBAdapter.KEY_VIDEO_SIZE, videoSize);
			values.put(DBAdapter.KEY_VIDEO_URL, videoURL);
			values.put(DBAdapter.KEY_VIDEO_SEQUENCE, videoSequence);
			values.put(DBAdapter.KEY_VIDEO_TYPE, videoType);
			values.put(DBAdapter.KEY_FK_ROWID, masterMaxId);
			dbAdapter.insert(DBAdapter.DBTABLE_VIDEO_2, values);
		}
	}

	private void getBannerRightResponse() throws ClientProtocolException,
			IOException, JSONException {

		JSONArray jsonBannerRightResponse = new JSONArray(bannerRightResponse);
		for (int i = 0; i < jsonBannerRightResponse.length(); i++) {
			JSONObject jsonImage = (JSONObject) jsonBannerRightResponse.get(i);
			String imageName = jsonImage.getString(WebHelper.JKEY_IMAGE_NAME);
			String imageURL = jsonImage.getString(WebHelper.JKEY_IMAGE_URL);
			String imageSize = jsonImage.getString(WebHelper.JKEY_IMAGE_SIZE);
			String imageSequence = jsonImage
					.getString(WebHelper.JKEY_IMAGE_SEQUENCE);

			String adsName = jsonImage.getString(WebHelper.JKEY_VIDEO_NAME);
			String adsURL = jsonImage.getString(WebHelper.JKEY_VIDEO_URL);
			String adsSize = jsonImage.getString(WebHelper.JKEY_VIDEO_SIZE);

			ContentValues values = new ContentValues();
			values.put(DBAdapter.KEY_BANNER_NAME, imageName);
			values.put(DBAdapter.KEY_BANNER_SIZE, imageSize);
			values.put(DBAdapter.KEY_BANNER_URL, imageURL);
			values.put(DBAdapter.KEY_BANNER_SEQUENCE, imageSequence);
			values.put(DBAdapter.KEY_ADS_NAME, adsName);
			values.put(DBAdapter.KEY_ADS_SIZE, adsSize);
			values.put(DBAdapter.KEY_ADS_URL, adsURL);
			values.put(DBAdapter.KEY_FK_ROWID, masterMaxId);
			dbAdapter.insert(DBAdapter.DBTABLE_BANNER_RIGHT, values);

		}
	}

	// function to check the old response and
	// downloads the files if not downloaded
	public boolean checkOldResponse() throws ClientProtocolException,
			IOException, JSONException {

		Log.e(TAG, "In Method: checkOldResponse");
		recordState = true;
		// Downloads the files and save it on SDcard,
		// and store its path in Database
		dbAdapter.open();
		long status = getMasterRowId();
		if (status == DBAdapter.DATA_AVAILABLE) {
			return true;
		}

		Log.e(TAG, "In Method: checkOldResponse: saveResponse");
		saveVideo1Response();
		saveVideo2Response();
		saveBannerRightResponse();

		if (recordState) {
			ContentValues values = new ContentValues();
			values.put(DBAdapter.KEY_STATUS, DBAdapter.DATA_AVAILABLE);
			dbAdapter.update(DBAdapter.DBTABLE_MASTER, values,
					DBAdapter.KEY_PK_ROWID + "=?",
					new String[] { String.valueOf(masterMaxId) });

			Log.e(TAG, "In Method: checkOldResponse: Done");
			return true;
		}

		return false;
	}

	private long getMasterRowId() {

		Cursor cursor = dbAdapter.getMax(DBAdapter.DBTABLE_MASTER,
				DBAdapter.KEY_PK_ROWID);
		if (cursor.moveToNext()) {
			masterMaxId = cursor.getLong(0);
			long status = cursor.getLong(1);
			cursor.close();
			return status;
		}
		return 0;
	}

	private void saveVideo1Response() {

		Cursor cursor = dbAdapter.getAll(
				DBAdapter.DBTABLE_VIDEO_1,
				new String[] { DBAdapter.KEY_PK_ROWID,
						DBAdapter.KEY_VIDEO_NAME, DBAdapter.KEY_VIDEO_SIZE,
						DBAdapter.KEY_VIDEO_URL },
				DBAdapter.KEY_FK_ROWID + "=? AND " + DBAdapter.KEY_STATUS
						+ "=?",
				new String[] { String.valueOf(masterMaxId),
						String.valueOf(DBAdapter.DATA_UNAVAILABLE) }, null);
		while (cursor.moveToNext()) {

			String rowId = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_PK_ROWID));
			String videoUrl = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_VIDEO_URL));
			String videoName = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_VIDEO_NAME));
			long videoSize = cursor.getLong(cursor
					.getColumnIndex(DBAdapter.KEY_VIDEO_SIZE));

			boolean videoDownloadState = downloadFile(FileHelper.DIR_VIDEO_1,
					videoUrl, videoName, videoSize);

			if (videoDownloadState) {
				ContentValues values = new ContentValues();
				values.put(DBAdapter.KEY_STATUS, DBAdapter.DATA_AVAILABLE);
				dbAdapter.update(DBAdapter.DBTABLE_VIDEO_1, values,
						DBAdapter.KEY_PK_ROWID + "=?", new String[] { rowId });
			}
		}
		cursor.close();
	}

	private void saveVideo2Response() {

		Cursor cursor = dbAdapter.getAll(DBAdapter.DBTABLE_VIDEO_2,
				new String[] { DBAdapter.KEY_PK_ROWID,
						DBAdapter.KEY_VIDEO_NAME, DBAdapter.KEY_VIDEO_SIZE,
						DBAdapter.KEY_VIDEO_URL, DBAdapter.KEY_BANNER_NAME,
						DBAdapter.KEY_BANNER_SIZE, DBAdapter.KEY_BANNER_URL },
				DBAdapter.KEY_FK_ROWID + "=? AND " + DBAdapter.KEY_STATUS
						+ "=?", new String[] { String.valueOf(masterMaxId),
						String.valueOf(DBAdapter.DATA_UNAVAILABLE) }, null);
		while (cursor.moveToNext()) {

			long rowId = cursor.getLong(cursor
					.getColumnIndex(DBAdapter.KEY_PK_ROWID));
			String videoUrl = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_VIDEO_URL));
			String videoName = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_VIDEO_NAME));
			long videoSize = cursor.getLong(cursor
					.getColumnIndex(DBAdapter.KEY_VIDEO_SIZE));

			boolean videoDownloadState = downloadFile(FileHelper.DIR_VIDEO_2,
					videoUrl, videoName, videoSize);

			if (videoDownloadState) {
				ContentValues values = new ContentValues();
				values.put(DBAdapter.KEY_STATUS, DBAdapter.DATA_AVAILABLE);
				dbAdapter.update(DBAdapter.DBTABLE_VIDEO_2, values,
						DBAdapter.KEY_PK_ROWID + "=?",
						new String[] { String.valueOf(rowId) });
			}

		}

		cursor.close();
	}

	private void saveBannerRightResponse() {

		Cursor cursor = dbAdapter.getAll(DBAdapter.DBTABLE_BANNER_RIGHT,
				new String[] { DBAdapter.KEY_PK_ROWID,
						DBAdapter.KEY_BANNER_NAME, DBAdapter.KEY_BANNER_SIZE,
						DBAdapter.KEY_BANNER_URL, DBAdapter.KEY_ADS_NAME,
						DBAdapter.KEY_ADS_SIZE, DBAdapter.KEY_ADS_URL },
				DBAdapter.KEY_FK_ROWID + "=? AND " + DBAdapter.KEY_STATUS
						+ "=?", new String[] { String.valueOf(masterMaxId),
						String.valueOf(DBAdapter.DATA_UNAVAILABLE) }, null);
		while (cursor.moveToNext()) {

			long rowId = cursor.getLong(cursor
					.getColumnIndex(DBAdapter.KEY_PK_ROWID));
			String imageUrl = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_BANNER_URL));
			String imageName = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_BANNER_NAME));
			long imageSize = cursor.getLong(cursor
					.getColumnIndex(DBAdapter.KEY_BANNER_SIZE));

			String adsUrl = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_ADS_URL));
			String adsName = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_ADS_NAME));
			long adsSize = cursor.getLong(cursor
					.getColumnIndex(DBAdapter.KEY_ADS_SIZE));

			boolean imageDownloadState = downloadFile(
					FileHelper.DIR_BANNER_RIGHT, imageUrl, imageName, imageSize);

			boolean adsDownloadState = downloadFile(
					FileHelper.DIR_BANNER_RIGHT, adsUrl, adsName, adsSize);

			if (imageDownloadState && adsDownloadState) {
				ContentValues values = new ContentValues();
				values.put(DBAdapter.KEY_STATUS, DBAdapter.DATA_AVAILABLE);
				dbAdapter.update(DBAdapter.DBTABLE_BANNER_RIGHT, values,
						DBAdapter.KEY_PK_ROWID + "=?",
						new String[] { String.valueOf(rowId) });
			}
		}

		cursor.close();
	}

	// function to check and store RSS Feeds
	public void checkRssFeed() {

		Log.e(TAG, "In Method: checkRssFeed");
		rssFeedsResponse = getFeedResponse(WebHelper.METHOD_RSS_FEEDS);

		try {

			if (rssFeedsResponse != null) {
				JSONObject jsonVideo = new JSONObject(rssFeedsResponse);
				String url = jsonVideo.optString(WebHelper.JKEY_URL, null);
				if (url != null) {
					InputStream inputStream = webHelper.download(url);
					if (inputStream != null) {
						fileHelper.saveFile(inputStream,
								FileHelper.DIR_RSS_FEED,
								FileHelper.RSS_FEED_FILE);
						Log.e(TAG, "In Method: checkRssFeed: Done");
					}
				}
			}

		} catch (JSONException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// function to check and delete old response
	public void deleteOldResponse() {

		Log.e(TAG, "In Method: deleteOldResponse");

		dbAdapter.open();
		deleteVideo1OldResponse();
		deleteVideo2OldResponse();
		deleteRightBannerOldResponse();
		deleteMasterOldResponse();

		Log.e(TAG, "In Method: deleteOldResponse: Done");
	}

	private void deleteVideo1OldResponse() {

		String sqlSelect = String.format(DBAdapter.SQL_SELECTION,
				DBAdapter.KEY_VIDEO_NAME, DBAdapter.DBTABLE_VIDEO_1,
				DBAdapter.KEY_VIDEO_NAME, DBAdapter.KEY_VIDEO_NAME,
				DBAdapter.DBTABLE_VIDEO_1);
		Log.i(TAG, "In Method: deleteVideo1OldResponse: sqlSelect: "
				+ sqlSelect);
		Cursor cursor = dbAdapter.rawQuery(sqlSelect, null);
		while (cursor.moveToNext()) {
			String videoFile = cursor.getString(1);
			boolean status = fileHelper.isFileExists(FileHelper.DIR_VIDEO_1,
					videoFile);
			if (status) {
				fileHelper.deleteFile(fileHelper.getFilePath(
						FileHelper.DIR_VIDEO_1, videoFile));
			}
		}
		cursor.close();

		String sqlDelete = String.format(DBAdapter.SQL_DELETION_ROW,
				DBAdapter.DBTABLE_VIDEO_1, DBAdapter.DBTABLE_VIDEO_1);
		Log.i(TAG, "In Method: deleteVideo1OldResponse: sqlDelete: "
				+ sqlDelete);
		dbAdapter.rawQuery(sqlDelete, null).close();
	}

	private void deleteVideo2OldResponse() {

		String sqlSelect1 = String.format(DBAdapter.SQL_SELECTION_VIDEO_ADS,
				DBAdapter.KEY_VIDEO_NAME, DBAdapter.DBTABLE_VIDEO_2,
				DBAdapter.KEY_VIDEO_NAME, DBAdapter.KEY_VIDEO_NAME,
				DBAdapter.DBTABLE_VIDEO_2);
		Log.i(TAG, "In Method: deleteVideo2OldResponse: sqlSelect: "
				+ sqlSelect1);
		Cursor cursor1 = dbAdapter.rawQuery(sqlSelect1, null);
		while (cursor1.moveToNext()) {
			String videoFile = cursor1.getString(1);
			boolean status = fileHelper.isFileExists(FileHelper.DIR_VIDEO_2,
					videoFile);
			if (status) {
				fileHelper.deleteFile(fileHelper.getFilePath(
						FileHelper.DIR_VIDEO_2, videoFile));
			}
		}
		cursor1.close();

		String sqlSelect2 = String.format(
				DBAdapter.SQL_SELECTION_VIDEO_CONTENT,
				DBAdapter.KEY_VIDEO_NAME, DBAdapter.DBTABLE_VIDEO_2,
				DBAdapter.KEY_VIDEO_NAME, DBAdapter.KEY_VIDEO_NAME,
				DBAdapter.DBTABLE_VIDEO_2);
		Log.i(TAG, "In Method: deleteVideo2OldResponse: sqlSelect: "
				+ sqlSelect2);
		Cursor cursor2 = dbAdapter.rawQuery(sqlSelect2, null);
		while (cursor2.moveToNext()) {
			String videoFile = cursor2.getString(1);
			boolean status = fileHelper.isFileExists(FileHelper.DIR_VIDEO_2,
					videoFile);
			if (status) {
				fileHelper.deleteFile(fileHelper.getFilePath(
						FileHelper.DIR_VIDEO_2, videoFile));
			}
		}
		cursor2.close();

		String sqlDelete = String.format(DBAdapter.SQL_DELETION_ROW,
				DBAdapter.DBTABLE_VIDEO_2, DBAdapter.DBTABLE_VIDEO_2);
		Log.i(TAG, "In Method: deleteVideo2OldResponse: sqlDelete: "
				+ sqlDelete);
		dbAdapter.rawQuery(sqlDelete, null).close();
	}

	private void deleteRightBannerOldResponse() {

		String columns = DBAdapter.KEY_BANNER_NAME + ", "
				+ DBAdapter.KEY_ADS_NAME;
		String sqlSelect = String.format(DBAdapter.SQL_SELECTION, columns,
				DBAdapter.DBTABLE_BANNER_RIGHT, DBAdapter.KEY_BANNER_NAME,
				DBAdapter.KEY_BANNER_NAME, DBAdapter.DBTABLE_BANNER_RIGHT);
		Log.i(TAG, "In Method: deleteRightBannerOldResponse: sqlSelect: "
				+ sqlSelect);
		Cursor cursor = dbAdapter.rawQuery(sqlSelect, null);
		while (cursor.moveToNext()) {
			String imageFile = cursor.getString(1);
			String adsFile = cursor.getString(2);
			boolean status = fileHelper.isFileExists(
					FileHelper.DIR_BANNER_RIGHT, imageFile);

			if (status) {
				status = fileHelper.deleteFile(fileHelper.getFilePath(
						FileHelper.DIR_BANNER_RIGHT, imageFile));
			}

			status = fileHelper.isFileExists(FileHelper.DIR_BANNER_RIGHT,
					adsFile);
			if (status) {
				fileHelper.deleteFile(fileHelper.getFilePath(
						FileHelper.DIR_BANNER_RIGHT, adsFile));
			}
		}
		cursor.close();

		String sqlDelete = String.format(DBAdapter.SQL_DELETION_ROW,
				DBAdapter.DBTABLE_BANNER_RIGHT, DBAdapter.DBTABLE_BANNER_RIGHT);
		Log.i(TAG, "In Method: deleteRightBannerOldResponse: sqlDelete: "
				+ sqlDelete);
		dbAdapter.rawQuery(sqlDelete, null).close();

	}

	private void deleteMasterOldResponse() {

		String sqlDelete = String.format(DBAdapter.SQL_DELETION_MASTER_ROW,
				DBAdapter.DBTABLE_MASTER, DBAdapter.DBTABLE_MASTER);
		Log.i(TAG, "In Method: deleteMasterOldResponse: sqlDelete: "
				+ sqlDelete);
		dbAdapter.rawQuery(sqlDelete, null).close();
	}

	// function to check file existence and to save the save
	private boolean downloadFile(String dir, String url, String fileName,
			long fileSize) {

		boolean fileExists = isFileExists(dir, fileName, fileSize);
		if (!fileExists) {
			File file = saveFile(url, dir, fileName, fileSize);
			if (file != null) {
				return true;
			} else {
				recordState = false;
				fileHelper.deleteFile(fileHelper.getFilePath(dir, fileName));
				return false;
			}
		} else {
			return true;
		}
	}

	// function to download and save the file
	private File saveFile(String url, String dir, String fileName, long fileSize) {

		try {
			InputStream inputStream = webHelper.download(url);
			if (inputStream == null) {
				return null;
			}

			File file = fileHelper.saveFile(inputStream, dir, fileName);

			long size1 = fileSize;
			long size2 = fileHelper.getFileSize(dir, fileName);
			if (size1 == size2) {
				return file;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// function to check file existence
	private boolean isFileExists(String dir, String fileName, long fileSize) {

		boolean status = fileHelper.isFileExists(dir, fileName);
		if (status) {
			long size1 = fileHelper.getFileSize(dir, fileName);
			long size2 = fileSize;
			if (size1 == size2) {
				return true;
			}
		}

		return false;
	}

}
