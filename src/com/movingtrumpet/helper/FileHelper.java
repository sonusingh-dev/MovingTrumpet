package com.movingtrumpet.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.os.Environment;

public class FileHelper {

	// private static final String TAG = "FileHelper";

	public static final String PREF_FILE_NAME = "moving_trumpet";

	public static final String KEY_DEVICE_ID = "deviceId";

	// startup video directory (beginning video)
	public static final String DIR_VIDEO_1 = "video_1";
	// top left video directory (center video)
	public static final String DIR_VIDEO_2 = "video_2";
	// default banner directory
	public static final String DIR_BANNER_DEFAULT = "banner_default";
	// right banner directory
	public static final String DIR_BANNER_RIGHT = "banner_right";
	// rss feeds directory
	public static final String DIR_RSS_FEED = "rss_feeds";

	public static final String DIR_DATABASE = "db";

	// rss feeds file name
	public static final String RSS_FEED_FILE = "rss_feed.xml";

	// root directory
	private static final String DIR_HOME = "/LocalDisk/MovingTrumpet/Files";

	private static String sRoot = null;

	// root directory
	// public static File root;

	public FileHelper(Context context) {

		if (sRoot == null) {

			if (Utility.isSDCardExists()) {
				sRoot = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + DIR_HOME;
			} else {
				sRoot = DIR_HOME;
			}

			File file = new File(sRoot);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// Toast.makeText(context, "Root Dir: " + sRoot,
		// Toast.LENGTH_LONG).show();

		// if (root == null) {
		// root = context.getExternalFilesDir(null);
		// }
	}

	// function to verify if directory exists
	public boolean isDirExists(String dirPath) {
		File dir = new File(dirPath);
		if (dir.exists()) {
			return true;
		}
		return false;
	}

	// function to create directory path
	private String getDirPath(String dir) {
		String dirPath = sRoot + "/" + dir;

		// String dirPath = root + "/" + dir;
		return dirPath;
	}

	// function to create directory if not exists
	private void checkDir(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			createNewDirectory(dirPath);
		}
	}

	// function to create new directory
	private void createNewDirectory(String dirPath) {
		File newDir = new File(dirPath);
		newDir.mkdirs();
	}

	// function to create directory path
	public String getFilePath(String dir, String fileName) {
		String path = getDirPath(dir) + "/" + fileName;
		return path;
	}

	// function to verify if file exists
	public boolean isFileExists(String dir, String fileName) {
		String dirPath = getDirPath(dir);
		File file = new File(dirPath, fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	// function to verify if file exists
	public long getFileSize(String dir, String fileName) {
		String dirPath = getDirPath(dir);
		File file = new File(dirPath, fileName);
		return file.length();
	}

	// function to create new file
	public void createNewFile(String dir, String fileName) {

		// it will create directory if not exists
		String dirPath = getDirPath(dir);
		checkDir(dirPath);

		File newFile = new File(dirPath, fileName);
		try {
			newFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public File saveFile(InputStream inputStream, String dir, String fileName)
			throws IOException {

		// it will create directory if not exists
		String dirPath = getDirPath(dir);
		checkDir(dirPath);

		// create a new file, specifying the path, and the filename
		// which we want to save the file as.
		File file = new File(dirPath, fileName);

		// this will be used to write the downloaded data into the file we
		// created
		FileOutputStream fileOutput = new FileOutputStream(file);

		// variable to store total downloaded bytes
		int downloadedSize = 0;

		// create a buffer...
		byte[] buffer = new byte[1024];
		int bufferLength = 0; // used to store a temporary size of the
								// buffer

		// now, read through the input buffer and write the contents to the
		// file
		while ((bufferLength = inputStream.read(buffer)) > 0) {
			// add the data in the buffer to the file in the file output
			// stream (the file on the sd card
			fileOutput.write(buffer, 0, bufferLength);
			// add up the size so we know how much is downloaded
			downloadedSize += bufferLength;
			// this is where you would do something to report the prgress,
			// like this maybe
			// updateProgress(downloadedSize, totalSize);

		}

		// close the output stream when done
		fileOutput.close();
		return file;
	}

	public static String convertStreamToString(InputStream inputStream) {

		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */

		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = null;
		String result = null;

		// convert response to string
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream));
			stringBuilder = new StringBuilder();
			stringBuilder.append(bufferedReader.readLine() + "\n");
			String line = "0";

			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}

			inputStream.close();
			result = stringBuilder.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	public String readFile(Context context, String dir, String fileName) {

		StringBuilder result = new StringBuilder();

		try {
			File file = new File(getFilePath(dir, fileName));

			if (!file.exists())
				return null;

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				result.append(line);
				result.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	public boolean deleteFile(String fileName) {
		File file = new File(fileName);
		boolean deleted = file.delete();
		return deleted;
	}

}
