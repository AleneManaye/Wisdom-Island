package com.drcom.drpalm.DB;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.objs.LatestNewsItem;

public class LatestNewsDB {
	public static String TYPE_NEWS = "news";
	public static String TYPE_EVENTS = "events";
	
	//table
	private static String LATEST_NEWS_TABLE = "latestnewstable";
	//table field
	private static String LATEST_NEWS_ID = "latest_news_id";
	private static String LATEST_NEWS_TYPE = "latest_news_type";
	private static String LATEST_NEWS_CHANNEL = "latest_news_channel";
	private static String LATEST_NEWS_TITLE = "latest_news_title";
	private static String LATEST_NEWS_LASTUPDATE = "latest_news_lastupdate";
	private static String USER = "user";
	
	private static String TYPE_AND_NEWS_ID_AND_USER_WHERE = LATEST_NEWS_TYPE + " =?" + " AND " + LATEST_NEWS_ID + " =?" + " AND " + USER + " =?";
	
	private static LatestNewsDB mLatestNewsDB;
	private DatabaseHelper mLatestNewsDBHelper;
	
	//多学校处理
	private String schoolkey = "";
	
	public static LatestNewsDB getInstance(Context context, String key){
		if(mLatestNewsDB == null||!mLatestNewsDB.schoolkey.equals(key)){
			mLatestNewsDB = new LatestNewsDB(context,key);
			mLatestNewsDB.schoolkey = key;
		}
		return mLatestNewsDB;
	}
	
	public void startTransaction(){
		mLatestNewsDBHelper.getWritableDatabase().beginTransaction();
	}
	
	public void endTransaction(){
		mLatestNewsDBHelper.getWritableDatabase().setTransactionSuccessful();
		mLatestNewsDBHelper.getWritableDatabase().endTransaction();
	}
	
	private LatestNewsDB(Context context, String key){
		mLatestNewsDBHelper = DatabaseHelper.getInstance(context,key);
		SQLiteDatabase db = mLatestNewsDBHelper.getWritableDatabase();
		createTable(db);
		db.close();
	}
	
	public void clearTable(String user){
		clearNews();
		clearEvents(user);
	}
	
	public void clearNews(){
		SQLiteDatabase db = mLatestNewsDBHelper.getWritableDatabase();
		db.delete(LATEST_NEWS_TABLE, LATEST_NEWS_TYPE + " =?", new String[]{TYPE_NEWS});
	}
	public void clearEvents(String user){
		SQLiteDatabase db = mLatestNewsDBHelper.getWritableDatabase();
		db.delete(LATEST_NEWS_TABLE, LATEST_NEWS_TYPE + " =? AND " + USER + "=?", new String[]{TYPE_EVENTS,user});
	}
	public void saveLatestNews(LatestNewsItem item){
		SQLiteDatabase db = mLatestNewsDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LATEST_NEWS_ID, item.latest_news_id);
		values.put(LATEST_NEWS_TYPE, item.latest_news_type);
		values.put(LATEST_NEWS_CHANNEL, item.latest_news_channel);
		values.put(LATEST_NEWS_TITLE, item.latest_news_title);
		values.put(USER, item.user);
		values.put(LATEST_NEWS_LASTUPDATE, item.latest_news_lastupdate.getTime());
		try{
			long i = db.insert(LATEST_NEWS_TABLE, null, values);
			long t = i;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public Cursor getLatestNewsCursor(String limit){
		SQLiteDatabase db = mLatestNewsDBHelper.getWritableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																LATEST_NEWS_TABLE, 
																null, 
																LATEST_NEWS_TYPE + " = '" + TYPE_NEWS + "'", 
																null, 
																null, 
																null, 
																limit);
		return db.rawQuery(queryBuilder, null);
	}
	
	public Cursor getLatestEventsCursor(String user,String limit){
		SQLiteDatabase db = mLatestNewsDBHelper.getWritableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																LATEST_NEWS_TABLE, 
																null, 
																USER + " = '" + user + "' AND " + LATEST_NEWS_TYPE + " = '" + TYPE_EVENTS + "'", 
																null, 
																null, 
																null, 
																limit);
		return db.rawQuery(queryBuilder, null);
	}
	
	public LatestNewsItem retrieveLatestNewsItem(Cursor cursor){
		int news_id_index = cursor.getColumnIndex(LATEST_NEWS_ID);
		int news_type_index = cursor.getColumnIndex(LATEST_NEWS_TYPE);
		int news_channel_index = cursor.getColumnIndex(LATEST_NEWS_CHANNEL);
		int news_title_index = cursor.getColumnIndex(LATEST_NEWS_TITLE);
		int news_lastupdate_index = cursor.getColumnIndex(LATEST_NEWS_LASTUPDATE);
		int news_user_index = cursor.getColumnIndex(USER);
		
		LatestNewsItem item = new LatestNewsItem();
		item.latest_news_id = cursor.getInt(news_id_index);
		item.latest_news_type = cursor.getString(news_type_index);
		item.latest_news_channel = cursor.getInt(news_channel_index);
		item.latest_news_title = cursor.getString(news_title_index);
		item.latest_news_lastupdate = new Date(cursor.getLong(news_lastupdate_index));
		item.user = cursor.getString(news_user_index);
		
		return item;		
	}
	
	private boolean latestNewsExist(LatestNewsItem item){
		SQLiteDatabase db = mLatestNewsDBHelper.getReadableDatabase();
		Cursor result = db.query(LATEST_NEWS_TABLE, 
							null, 
							USER + " =? AND" + LATEST_NEWS_ID + "=?" , 
							new String[]{item.user,String.valueOf(item.latest_news_id)}, 
							null, 
							null, 
							null);
		int i = result.getCount();
		boolean latestNewsExist = (result.getCount()>0);
		result.close();
		return latestNewsExist;
	}
	
	private String[] whereArgs(LatestNewsItem item){
		return new String[]{item.latest_news_type,String.valueOf(item.latest_news_id),item.user};
	}
	
	private void createTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS " + LATEST_NEWS_TABLE + " ("
				+ LATEST_NEWS_ID + " INTEGER,"
				+ LATEST_NEWS_TYPE + " TEXT,"
				+ LATEST_NEWS_CHANNEL + " INTEGER,"
				+ LATEST_NEWS_TITLE + " TEXT,"
				+ LATEST_NEWS_LASTUPDATE + " TEXT,"
				+ USER + " TEXT"
			+ ");");
		
		mLatestNewsDBHelper.updateTable(db, LATEST_NEWS_TABLE);
	}
}
