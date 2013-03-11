package com.movingtrumpet.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	// private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "trumpet.db";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Log.e(TAG, "In Method: onCreate");
		DBAdapter.onCreate(db);
	}

	public SQLiteDatabase open(FileHelper fileHelper) {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
				fileHelper.getFilePath(FileHelper.DIR_DATABASE, DATABASE_NAME),
				null);
		return db;
	}

	// Method is called during an update of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DBAdapter.onUpgrade(db, oldVersion, newVersion);
		onCreate(db);
	}
}
