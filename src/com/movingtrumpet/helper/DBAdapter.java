package com.movingtrumpet.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

	// private static final String TAG = "DBAdapter";

	// status of data in status field
	public static final long DATA_UNAVAILABLE = 0;
	public static final long DATA_AVAILABLE = 1;

	// Database fields
	public static final String KEY_PK_ROWID = "_id_pk";
	public static final String KEY_FK_ROWID = "_id_fk";

	// Master fields
	public static final String KEY_FEED_NAME = "feed_name";
	public static final String KEY_FEED_TIMESTAMP = "feed_timestamp";
	public static final String KEY_FEED_STATUS = "feed_status";
	public static final String KEY_STATUS = "status";

	// Video fields
	public static final String KEY_VIDEO_NAME = "video_name";
	public static final String KEY_VIDEO_SIZE = "video_size";
	public static final String KEY_VIDEO_TYPE = "video_type";
	public static final String KEY_VIDEO_URL = "video_url";
	public static final String KEY_VIDEO_SEQUENCE = "video_sequence";
	public static final String KEY_VIDEO_COUNT = "video_count";

	// Banner fields
	public static final String KEY_BANNER_NAME = "banner_name";
	public static final String KEY_BANNER_SIZE = "banner_size";
	public static final String KEY_BANNER_URL = "banner_url";
	public static final String KEY_BANNER_SEQUENCE = "banner_sequence";
	public static final String KEY_BANNER_COUNT = "banner_count";
	public static final String KEY_ADS_NAME = "ads_name";
	public static final String KEY_ADS_SIZE = "ads_size";
	public static final String KEY_ADS_PATH = "ads_path";
	public static final String KEY_ADS_URL = "ads_url";

	// Database Tables
	public static final String DBTABLE_MASTER = "master";
	public static final String DBTABLE_VIDEO_1 = "video_1";
	public static final String DBTABLE_VIDEO_2 = "video_2";
	public static final String DBTABLE_BANNER_DEFAULT = "default_banner";
	public static final String DBTABLE_BANNER_BOTTOM = "bottom_banner";
	public static final String DBTABLE_BANNER_RIGHT = "right_banner";

	// query to select the files
	public static final String SQL_SELECTION = "SELECT _id_pk, %s FROM %s WHERE _id_fk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp NOT BETWEEN date('"
			+ Utility.getStartDate90()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "')) AND %s NOT IN "
			+ "(SELECT %s FROM %s WHERE _id_fk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp BETWEEN date('"
			+ Utility.getStartDate90()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "')));";

	// query to select the Content video files
	public static final String SQL_SELECTION_VIDEO_CONTENT = "SELECT _id_pk, %s FROM %s WHERE _id_fk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp NOT BETWEEN date('"
			+ Utility.getStartDate30()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "')) AND video_type = 'Content' AND %s NOT IN "
			+ "(SELECT %s FROM %s WHERE _id_fk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp BETWEEN date('"
			+ Utility.getStartDate30()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "')));";

	// query to select the Ads video files
	public static final String SQL_SELECTION_VIDEO_ADS = "SELECT _id_pk, %s FROM %s WHERE _id_fk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp NOT BETWEEN date('"
			+ Utility.getStartDate90()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "')) AND video_type = 'Ad' AND %s NOT IN "
			+ "(SELECT %s FROM %s WHERE _id_fk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp BETWEEN date('"
			+ Utility.getStartDate90()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "')));";

	public static final String SQL_DELETION_ROW = "DELETE FROM %s WHERE _id_pk IN "
			+ "(SELECT _id_pk FROM %s WHERE _id_fk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp NOT BETWEEN date('"
			+ Utility.getStartDate90()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "')));";

	public static final String SQL_DELETION_MASTER_ROW = "DELETE FROM %s WHERE _id_pk IN "
			+ "(SELECT _id_pk FROM master WHERE feed_timestamp NOT BETWEEN date('"
			+ Utility.getStartDate90()
			+ "') AND date('"
			+ Utility.getEndDate()
			+ "'));";

	// Create Table for Master
	private static final String CREATE_TABLE_MASTER = "CREATE TABLE IF NOT EXISTS "
			+ DBTABLE_MASTER
			+ " ("
			+ KEY_PK_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_FEED_NAME
			+ " TEXT,"
			+ KEY_FEED_TIMESTAMP
			+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
			+ KEY_STATUS + " TEXT NOT NULL DEFAULT 0);";

	// Create Table for Startup Video
	private static final String CREATE_TABLE_VIDEO_1 = "CREATE TABLE IF NOT EXISTS "
			+ DBTABLE_VIDEO_1
			+ " ("
			+ KEY_PK_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_VIDEO_NAME
			+ " TEXT NOT NULL,"
			+ KEY_VIDEO_SIZE
			+ " INTEGER NOT NULL,"
			+ KEY_VIDEO_URL
			+ " TEXT NOT NULL,"
			+ KEY_VIDEO_SEQUENCE
			+ " INTEGER NOT NULL,"
			+ KEY_STATUS
			+ " INTEGER NOT NULL DEFAULT 0,"
			+ KEY_FK_ROWID
			+ " INTEGER,"
			+ "FOREIGN KEY ("
			+ KEY_FK_ROWID
			+ ") REFERENCES "
			+ DBTABLE_MASTER
			+ " (" + KEY_PK_ROWID + "));";

	// Create Table for Left Top Video
	private static final String CREATE_TABLE_VIDEO_2 = "CREATE TABLE IF NOT EXISTS "
			+ DBTABLE_VIDEO_2
			+ " ("
			+ KEY_PK_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_VIDEO_NAME
			+ " TEXT NOT NULL,"
			+ KEY_VIDEO_SIZE
			+ " INTEGER NOT NULL,"
			+ KEY_VIDEO_URL
			+ " TEXT NOT NULL,"
			+ KEY_VIDEO_SEQUENCE
			+ " INTEGER NOT NULL,"
			+ KEY_VIDEO_TYPE
			+ " TEXT,"
			+ KEY_VIDEO_COUNT
			+ " INTEGER NOT NULL DEFAULT 0,"
			+ KEY_BANNER_NAME
			+ " TEXT,"
			+ KEY_BANNER_SIZE
			+ " INTEGER,"
			+ KEY_BANNER_URL
			+ " TEXT,"
			+ KEY_BANNER_SEQUENCE
			+ " INTEGER,"
			+ KEY_BANNER_COUNT
			+ " INTEGER NOT NULL DEFAULT 0,"
			+ KEY_STATUS
			+ " INTEGER NOT NULL DEFAULT 0,"
			+ KEY_FK_ROWID
			+ " INTEGER,"
			+ "FOREIGN KEY ("
			+ KEY_FK_ROWID
			+ ") REFERENCES "
			+ DBTABLE_MASTER
			+ " (" + KEY_PK_ROWID + "));";

	// Create Table for Right Banner
	private static final String CREATE_TABLE_BANNER_RIGHT = "CREATE TABLE IF NOT EXISTS "
			+ DBTABLE_BANNER_RIGHT
			+ " ("
			+ KEY_PK_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_BANNER_NAME
			+ " TEXT NOT NULL,"
			+ KEY_BANNER_SIZE
			+ " INTEGER NOT NULL,"
			+ KEY_BANNER_URL
			+ " TEXT NOT NULL,"
			+ KEY_BANNER_SEQUENCE
			+ " INTEGER NOT NULL,"
			+ KEY_BANNER_COUNT
			+ " INTEGER NOT NULL DEFAULT 0,"
			+ KEY_ADS_NAME
			+ " TEXT NOT NULL,"
			+ KEY_ADS_SIZE
			+ " INTEGER NOT NULL,"
			+ KEY_ADS_URL
			+ " TEXT NOT NULL,"
			+ KEY_STATUS
			+ " INTEGER NOT NULL DEFAULT 0,"
			+ KEY_FK_ROWID
			+ " INTEGER,"
			+ "FOREIGN KEY ("
			+ KEY_FK_ROWID
			+ ") REFERENCES " + DBTABLE_MASTER + " (" + KEY_PK_ROWID + "));";

	private static DBHelper dbHelper;
	private static SQLiteDatabase db;

	public DBAdapter(Context context) {
		if (dbHelper == null)
			dbHelper = new DBHelper(context);
	}

	// Opens the database
	public void open() throws SQLException {
		// Log.e(TAG, "In Method: open");
		if (db == null || !db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
	}

	// Closes the database
	public void close() {
		dbHelper.close();		
		dbHelper = null;
		db = null;
	}

	// Method is called during creation of the database
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_MASTER);
		db.execSQL(CREATE_TABLE_VIDEO_1);
		db.execSQL(CREATE_TABLE_VIDEO_2);
		db.execSQL(CREATE_TABLE_BANNER_RIGHT);
	}

	// Method is called during an update of the database, e.g. if you increase
	// the database version
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS titles");
	}

	// Insert a data into the database
	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}

	// Retrieves all the data
	public Cursor getAll(String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		return db.query(table, columns, selection, selectionArgs, null, null,
				orderBy);
	}

	// Retrieves the Max
	public Cursor getMax(String table, String column) {
		return db.rawQuery("SELECT MAX(" + column + "), "
				+ DBAdapter.KEY_STATUS + " FROM " + table, null);
	}

	// Retrieves the Min
	public Cursor getMin(String table, String column) {
		return db.rawQuery("SELECT MIN(" + column + "), "
				+ DBAdapter.KEY_STATUS + " FROM " + table, null);
	}

	// Retrieves a particular data
	public Cursor get(String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy) throws SQLException {
		Cursor cursor = db.query(true, table, columns, selection,
				selectionArgs, null, null, orderBy, null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}

	//
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return db.rawQuery(sql, selectionArgs);
	}

	// Updates a data
	public boolean update(String table, ContentValues values, String selection,
			String[] selectionArgs) {
		boolean updated = db.update(table, values, selection, selectionArgs) > 0;
		return updated;
	}

	// Deletes a particular data
	public boolean delete(String table, String selection, String[] selectionArgs) {
		boolean deleted = db.delete(table, selection, selectionArgs) > 0;
		return deleted;
	}

}
