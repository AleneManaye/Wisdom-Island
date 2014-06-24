package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.objs.UpdateTimeItem;

/**
 * 最新更新数据库
 * @author menchx
 *
 */
public class UpdateTimeDB {
	
	//table
	private static final String UPDATE_TIME_TABLE = "updatetimetable";
	
	//update time table field
	private static final String UPDATE_TIME_TYPE = "updateTimeType";
	private static final String UPDATE_TIME_CHANNEL = "updateTimeChannel";
	private static final String UPDATE_TIME_LAST = "lastupdatetime";
	private static final String UPDATE_TIME_SAVE = "savedupdatetime";
	private static final String UPDATE_UNGETCOUNT = "ungetcount";
	private static final String UPDATE_TITLE = "title";
	private static final String USER = "user";
	
	private static String TYPE_AND_CHANNEL_ID_AND_USER_WHERE = UPDATE_TIME_TYPE + " =? " + " AND " + UPDATE_TIME_CHANNEL + " =?" + " AND " + USER + " =?";
	private static String TYPE_AND_CHANNEL_ID_WHERE = UPDATE_TIME_TYPE + " =? " + " AND " + UPDATE_TIME_CHANNEL + " =?";
	
	private static UpdateTimeDB mUpdateTimeDB;
	private DatabaseHelper mUpdateTimeDBHelper;
	
	private NewsDB mNewsDB;
	private EventsDB mEventsDB;
	
	//多学校处理
	private String schoolkey = "";
	
	public static UpdateTimeDB getInstance(Context context,String key){
		if(mUpdateTimeDB == null||!mUpdateTimeDB.schoolkey.equals(key)){	
			mUpdateTimeDB = new UpdateTimeDB(context,key);
			mUpdateTimeDB.schoolkey = key;
		}
		return mUpdateTimeDB;
	}
	
	public void startTransaction(){
		mUpdateTimeDBHelper.getWritableDatabase().beginTransaction();
	}
	
	public void endTransaction(){
		mUpdateTimeDBHelper.getWritableDatabase().setTransactionSuccessful();
		mUpdateTimeDBHelper.getWritableDatabase().endTransaction();
	}
	
	private UpdateTimeDB(Context context,String key){
		mUpdateTimeDBHelper = DatabaseHelper.getInstance(context,key);
		mNewsDB = NewsDB.getInstance(context,key);
		mEventsDB = EventsDB.getInstance(context,key);
		SQLiteDatabase db = mUpdateTimeDBHelper.getWritableDatabase();
		createTable(db);
		db.close();
	}
	
	public void saveUpdateTimeItem(UpdateTimeItem item){
		SQLiteDatabase db = mUpdateTimeDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(UPDATE_TIME_TYPE, item.update_time_type);
		values.put(UPDATE_TIME_CHANNEL, item.update_time_channel);
		values.put(UPDATE_TIME_LAST, item.update_time_last.getTime());
		values.put(UPDATE_TIME_SAVE, item.update_time_save.getTime());
		values.put(UPDATE_UNGETCOUNT, item.update_unreadcount);
		values.put(UPDATE_TITLE, item.update_title);
		if(item.update_time_type.equals("event")){
			values.put(USER, item.user);
			if(updateTimeItemExist(item)){
				db.update(UPDATE_TIME_TABLE, values, TYPE_AND_CHANNEL_ID_AND_USER_WHERE, whereArgs(item));
			}else{
				try{
					db.insert(UPDATE_TIME_TABLE, UPDATE_TIME_TYPE, values);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}else if(item.update_time_type.equals("news")){
			if(newsUpdateTimeItemExist(item)){
				db.update(UPDATE_TIME_TABLE, values, TYPE_AND_CHANNEL_ID_WHERE, new String[]{item.update_time_type,String.valueOf(item.update_time_channel)});
			}else{
				try{
					db.insert(UPDATE_TIME_TABLE, UPDATE_TIME_TYPE, values);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}else if(item.update_time_type.equals("more")){
			if(newsUpdateTimeItemExist(item)){
				db.update(UPDATE_TIME_TABLE, values, TYPE_AND_CHANNEL_ID_WHERE, new String[]{item.update_time_type,String.valueOf(item.update_time_channel)});
			}else{
				try{
					db.insert(UPDATE_TIME_TABLE, UPDATE_TIME_TYPE, values);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}	
	}
	
	private Cursor getUpdateTimeCursor(String user){
		SQLiteDatabase db = mUpdateTimeDBHelper.getWritableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																UPDATE_TIME_TABLE, 
																null, 
																USER + " = '" + user + "'", 
																null, 
																null, 
																UPDATE_TIME_LAST + " DESC", 
																null);
		return db.rawQuery(queryBuilder, null);
	}
	
	private Cursor getNewsUpdateTimeCursor(){
		SQLiteDatabase db = mUpdateTimeDBHelper.getWritableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																UPDATE_TIME_TABLE, 
																null, 
																UPDATE_TIME_TYPE + " = '" + "news" + "'", 
																null, 
																null, 
																UPDATE_TIME_LAST + " DESC",
																null);
		return db.rawQuery(queryBuilder, null);
	}
	
	private Cursor getMoreUpdateTimeCursor(){
		SQLiteDatabase db = mUpdateTimeDBHelper.getWritableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																UPDATE_TIME_TABLE, 
																null, 
																UPDATE_TIME_TYPE + " = '" + "more" + "'", 
																null, 
																null, 
																UPDATE_TIME_LAST + " DESC",
																null);
		return db.rawQuery(queryBuilder, null);
	}
	
	private UpdateTimeItem retrieveUpdateTimeItem(Cursor cursor){
		int update_type_index = cursor.getColumnIndex(UPDATE_TIME_TYPE);
		int update_channel_index = cursor.getColumnIndex(UPDATE_TIME_CHANNEL);
		int update_last_index = cursor.getColumnIndex(UPDATE_TIME_LAST);
		int update_save_index = cursor.getColumnIndex(UPDATE_TIME_SAVE);
		int update_unreadcount = cursor.getColumnIndex(UPDATE_UNGETCOUNT);
		int update_title = cursor.getColumnIndex(UPDATE_TITLE);
		int update_user_index = cursor.getColumnIndex(USER);
		
		UpdateTimeItem item = new UpdateTimeItem();
		item.update_time_type = cursor.getString(update_type_index);
		item.update_time_channel = cursor.getInt(update_channel_index);
		item.update_time_last = new Date(cursor.getLong(update_last_index));
		item.update_time_save = new Date(cursor.getLong(update_save_index));
		item.update_unreadcount = cursor.getString(update_unreadcount);
		item.update_title = cursor.getString(update_title);
		item.user = cursor.getString(update_user_index);
		
		return item;		
	}
	
	public List<UpdateTimeItem> getNewsUpdateList(){
		ArrayList<UpdateTimeItem> updateFlagList = new ArrayList<UpdateTimeItem>();
		Cursor result = getNewsUpdateTimeCursor();
		int count = result.getCount();
		if(null != result){
			if(result.moveToFirst()){
				while(!result.isAfterLast()){
					UpdateTimeItem item = retrieveUpdateTimeItem(result);
					if(item.update_time_type.equals("news")){
						if(item.update_time_last.getTime() > mNewsDB.getLatestUpdateTime(item.update_time_channel).getTime()){
							item.update_unreadcount = "New";
						}
						updateFlagList.add(item);
					}
					result.moveToNext();
				}
			}
			result.close();
		}
		return updateFlagList;
	}
	
	public List<Integer> getNewsUpdateFlag(){
		ArrayList<Integer> updateFlagList = new ArrayList<Integer>();
		Cursor result = getNewsUpdateTimeCursor();
//		int count = result.getCount();
		if(null != result){
			if(result.moveToFirst()){
				while(!result.isAfterLast()){
					UpdateTimeItem item = retrieveUpdateTimeItem(result);
					if(item.update_time_type.equals("news")){
						if(item.update_time_last.getTime() > mNewsDB.getLatestUpdateTime(item.update_time_channel).getTime()){
							updateFlagList.add(item.update_time_channel);
						}
					}
					result.moveToNext();
				}
			}
			result.close();
		}
		return updateFlagList;
	}
	
	public List<UpdateTimeItem> getEventsUpdateFlag(String username){
		ArrayList<UpdateTimeItem> updateFlagList = new ArrayList<UpdateTimeItem>();
		Cursor result = getUpdateTimeCursor(username);
		if(null != result){
			if(result.moveToFirst()){
				while(!result.isAfterLast()){
					UpdateTimeItem item = retrieveUpdateTimeItem(result);
					if(item.update_time_type.equals("event")){
//						if(item.update_time_last.getTime() > mEventsDB.getLatestUpdateTime(username, item.update_time_channel).getTime()){
							updateFlagList.add(item);
//						}
					}
					result.moveToNext();
				}
			}
			result.close();
		}
		return updateFlagList;
	}
	
	public List<Integer> getMoreUpdateFlag(){
		ArrayList<Integer> updateFlagList = new ArrayList<Integer>();
		Cursor result = getMoreUpdateTimeCursor();
		int count = result.getCount();
		if(null != result){
			if(result.moveToFirst()){
				while(!result.isAfterLast()){
					UpdateTimeItem item = retrieveUpdateTimeItem(result);
//					if(item.update_time_type.equals("more")){
//						if(item.update_time_last.getTime() > mNewsDB.getLatestUpdateTime(item.update_time_channel).getTime()){
							int uncount = item.update_unreadcount.equals("")?0:Integer.valueOf(item.update_unreadcount);
							updateFlagList.add(uncount);
//						}
//					}
					result.moveToNext();
				}
			}
			result.close();
		}
		return updateFlagList;
	}
	
	
	private boolean newsUpdateTimeItemExist(UpdateTimeItem item){
		SQLiteDatabase db = mUpdateTimeDBHelper.getReadableDatabase();
		Cursor result = db.query(UPDATE_TIME_TABLE, 
							new String[]{UPDATE_TIME_TYPE}, 
							TYPE_AND_CHANNEL_ID_WHERE, 
							new String[]{item.update_time_type,String.valueOf(item.update_time_channel)}, 
							null, 
							null, 
							null);
		boolean updateTimeExist = (result.getCount()>0);
		result.close();
		return updateTimeExist;
	}
	
	private boolean updateTimeItemExist(UpdateTimeItem item){
		SQLiteDatabase db = mUpdateTimeDBHelper.getReadableDatabase();
		Cursor result = db.query(UPDATE_TIME_TABLE, 
							new String[]{UPDATE_TIME_TYPE}, 
							TYPE_AND_CHANNEL_ID_AND_USER_WHERE, 
							whereArgs(item), 
							null, 
							null, 
							null);
		boolean updateTimeExist = (result.getCount()>0);
		result.close();
		return updateTimeExist;
	}
	
	private String[] whereArgs(UpdateTimeItem item){
		return new String[]{item.update_time_type, String.valueOf(item.update_time_channel),item.user};
	}
	
	private void createTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS " + UPDATE_TIME_TABLE + " ("
				+ UPDATE_TIME_TYPE + " TEXT,"
				+ UPDATE_TIME_CHANNEL + " INTEGER,"
				+ UPDATE_TIME_LAST + " TEXT,"
				+ UPDATE_TIME_SAVE + " TEXT,"
				+ UPDATE_UNGETCOUNT + " TEXT,"
				+ UPDATE_TITLE + " TEXT,"
				+ USER + " TEXT"
			+ ");");
		
		mUpdateTimeDBHelper.updateTable(db, UPDATE_TIME_TABLE);
	}
}
