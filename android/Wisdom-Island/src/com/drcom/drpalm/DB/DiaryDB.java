package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.drcom.drpalm.objs.DiaryItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class DiaryDB {
	
	//table name 
	private static String DIARY_TABLE = "diarytable";

	//fiels
	private static String _ID = "_id";
	private static String DIARY_ID = "diary_id";
	private static String TITLE = "title";
	private static String SUMMARY = "summary";
	private static String CONTENT = "content";
	private static String STATUS = "status";
	private static String LASTUPDATE = "lastupdate";
	private static String USER = "user";
	
	private static String DIARY_ID_AND_USER_WHERE = DIARY_ID + " =? AND " + USER + " =?";
	
	private static DiaryDB diaryDBInstance = null;
	private DatabaseHelper mDiaryDBHelper;
	
	//多学校处理
	private String schoolkey = "";
	
	public static DiaryDB getInstance(Context context, String key) {
		if(diaryDBInstance == null||!diaryDBInstance.schoolkey.equals(key)) {
			diaryDBInstance = new DiaryDB(context,key);
			diaryDBInstance.schoolkey = key;
			return diaryDBInstance;
		} else {
			return diaryDBInstance;
		}
	}
	public void startTransaction() {
		mDiaryDBHelper.getWritableDatabase().beginTransaction();
	}
	public void endTransaction() {
		mDiaryDBHelper.getWritableDatabase().setTransactionSuccessful();
		mDiaryDBHelper.getWritableDatabase().endTransaction();
	}

	private DiaryDB(Context context, String key) {
		mDiaryDBHelper = DatabaseHelper.getInstance(context,key) ;
		SQLiteDatabase db = mDiaryDBHelper.getWritableDatabase() ;
		createTable(db) ;
		db.close();
	}
	
	public List<DiaryItem> getDiaryList(String user){
		List<DiaryItem> list = new ArrayList<DiaryItem>();
		SQLiteDatabase db = mDiaryDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																DIARY_TABLE, 
																null, 
																STATUS + " <> '" + "C" +"' AND " + USER + " = '" + user + "'", 
																null, 
																null, 
																LASTUPDATE + " DESC", 
																null);
		Cursor result = db.rawQuery(queryBuilder, null);
		if(result != null){
			result.moveToFirst();
			if(result.getCount()>0){
				while(!result.isAfterLast()){
					DiaryItem item = retrieveDiaryItem(result);
					list.add(item);
					result.moveToNext();
				}
			}
			result.close();
		}
		return list;
	}
	
	private DiaryItem retrieveDiaryItem(Cursor cursor){
		DiaryItem item = new DiaryItem();
		
		int diary_id_index = cursor.getColumnIndex(DIARY_ID);
		int diary_title_index = cursor.getColumnIndex(TITLE);
		int diary_summary_index = cursor.getColumnIndex(SUMMARY);
		int diary_content_index = cursor.getColumnIndex(CONTENT);
		int diary_status_index = cursor.getColumnIndex(STATUS);
		int diary_lastupdate_index = cursor.getColumnIndex(LASTUPDATE);
		
		item.diaryId = cursor.getInt(diary_id_index);
		item.diaryTitle = cursor.getString(diary_title_index);
		item.diarySum = cursor.getString(diary_summary_index);
		item.diaryContent = cursor.getString(diary_content_index);
		item.diaryStatus = cursor.getString(diary_status_index);
		item.lastUpdate = new Date(cursor.getLong(diary_lastupdate_index));
		
		return item;
	}
	
	public void saveDiaryItem(DiaryItem item){
		SQLiteDatabase db = mDiaryDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DIARY_ID, item.diaryId);
		values.put(TITLE, item.diaryTitle);
		values.put(SUMMARY, item.diarySum);
		values.put(CONTENT, item.diaryContent);
		values.put(STATUS, item.diaryStatus);
		values.put(USER, item.user);
		values.put(LASTUPDATE, item.lastUpdate.getTime());
		
		if(!diaryItemExist(item.diaryId,item.user)){
			db.insert(DIARY_TABLE, DIARY_ID, values);
		}else{
			db.update(DIARY_TABLE, values, DIARY_ID_AND_USER_WHERE, new String[]{item.diaryId+"",item.user});
		}
	}
	
	public DiaryItem getDiaryItemByDiaryID(int diary_id, String user){
		DiaryItem item = new DiaryItem();
		SQLiteDatabase db = mDiaryDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																DIARY_TABLE, 
																null, 
																DIARY_ID + " = " + diary_id + " AND " + USER + " = '" + user + "'", 
																null, 
																null, 
																LASTUPDATE + " DESC", 
																null);
		Cursor result = db.rawQuery(queryBuilder, null);
		if(result != null){
			result.moveToFirst();
			if(result.getCount() > 0){
				item = retrieveDiaryItem(result);
			}
			result.close();
		}
		return item;
	}
	
	private boolean diaryItemExist(int diaryId, String user){
		SQLiteDatabase db = mDiaryDBHelper.getReadableDatabase();
		Cursor cursor = db.query(DIARY_TABLE, 
								null, 
								DIARY_ID_AND_USER_WHERE, 
								new String[]{diaryId+"",user}, 
								null, 
								null, 
								null);
		int count = cursor.getCount();
		cursor.close();
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	private void createTable(SQLiteDatabase db)
	{
		//日志表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DIARY_TABLE + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DIARY_ID + " INTEGER,"
				+ TITLE + " TEXT,"
				+ SUMMARY + " TEXT,"
				+ CONTENT + " TEXT,"
				+ STATUS + " TEXT,"
				+ USER + " TEXT,"
				+ LASTUPDATE + " TEXT"
			+ ");");
		// 数据迁移
		mDiaryDBHelper.updateTable(db, DIARY_TABLE);
	}
}
