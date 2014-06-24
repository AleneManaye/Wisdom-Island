package com.drcom.drpalm.DB;


import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.objs.SystemInfoItem;

/**
 * 系统信息数据库
 * @author menchx
 *
 */
public class SystemInfoDB {
	//table
	private static final String SYSTEM_INFO_TABLE = "systemInfoTable";
	
	//systemInfo table field
	private static final String SYSTEM_MSG_ID = "_id";
	private static final String SYSTEM_MSG_TITLE = "systemMsgTitle";
	private static final String SYSTEM_MSG_BODY = "systemMsgBody";
	private static final String SYSTEM_MSG_LASTUPDATE = "systemMsgLastupdate";
	private static final String SYSTEM_MSG_USER = "systemMsgUser";
	private static final String SYSTEM_MSG_INACTIVETIME = "systeminactivetime";
	
	private static final String USER_AND_MSG_ID_WHERE = SYSTEM_MSG_USER + " =?" + " AND " + SYSTEM_MSG_ID + " =?";
	
	private static SystemInfoDB mSystemInfoDB;
	private DatabaseHelper mSystemInfoDBHelper;
	
	//多学校处理
	private String schoolkey = "";
	
	public static SystemInfoDB getInstance(Context context,String key){
		if(mSystemInfoDB == null||!mSystemInfoDB.schoolkey.equals(key)){
			mSystemInfoDB = new SystemInfoDB(context,key);
			mSystemInfoDB.schoolkey = key;
		}
		return mSystemInfoDB;
	}
	
	public void startTransaction(){
		mSystemInfoDBHelper.getWritableDatabase().beginTransaction();
	}
	
	public void endTransaction(){
		mSystemInfoDBHelper.getWritableDatabase().setTransactionSuccessful();
		mSystemInfoDBHelper.getWritableDatabase().endTransaction();
	}
	
	public SystemInfoDB(Context context,String key){
		mSystemInfoDBHelper = DatabaseHelper.getInstance(context,key);
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		createTable(db);
//		db.close();
	}
	
	public boolean saveSystemInfoItem(SystemInfoItem item){
		boolean flags = false;
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SYSTEM_MSG_ID, item.msg_id);
		values.put(SYSTEM_MSG_TITLE, item.msg_title);
		values.put(SYSTEM_MSG_LASTUPDATE, item.msg_lastupdate.getTime());
		values.put(SYSTEM_MSG_INACTIVETIME, item.msg_inactivetime.getTime());
		values.put(SYSTEM_MSG_USER, item.msg_user);
		if(InfoItemExists(item)){
			db.update(SYSTEM_INFO_TABLE, values, USER_AND_MSG_ID_WHERE, whereArgs(item));
		}else{
			flags = true;
			db.insert(SYSTEM_INFO_TABLE, SYSTEM_MSG_ID, values);
		}
		return flags;
	}
	
	public void saveSystemInfoDetail(SystemInfoItem item){
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SYSTEM_MSG_ID, item.msg_id);
		values.put(SYSTEM_MSG_USER, item.msg_user);
		values.put(SYSTEM_MSG_BODY, item.msg_body);
		if(InfoItemExists(item)){
			db.update(SYSTEM_INFO_TABLE, values, USER_AND_MSG_ID_WHERE, whereArgs(item));
		}else{
			db.insert(SYSTEM_INFO_TABLE, SYSTEM_MSG_ID, values);
		}
		
	}
	
	public Cursor getSystemInfoListCursor(String user, long currentTime, String limit){
		SQLiteDatabase db = mSystemInfoDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																SYSTEM_INFO_TABLE, 
																null, 
																SYSTEM_MSG_USER + " = '" + user + "' AND " + SYSTEM_MSG_INACTIVETIME + " >= '" + String.valueOf(currentTime) + "'", 
																null, 
																null, 
																SYSTEM_MSG_ID + " DESC", 
																limit);
		return db.rawQuery(queryBuilder, null);
	}
	
	public Cursor getSystemInfoCursor(String user, String id){
		SQLiteDatabase db = mSystemInfoDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																SYSTEM_INFO_TABLE, 
																null, 
																SYSTEM_MSG_USER + " = '" + user + "' AND " + SYSTEM_MSG_ID + " = " + id, 
																null, 
																null, 
																null, 
																null);
		return db.rawQuery(queryBuilder, null);
	}
	
	public SystemInfoItem retrieveSystemInfoItem(Cursor cursor){
		int msg_id_index = cursor.getColumnIndex(SYSTEM_MSG_ID);
		int msg_title_index = cursor.getColumnIndex(SYSTEM_MSG_TITLE);
		int msg_body_index = cursor.getColumnIndex(SYSTEM_MSG_BODY);
		int msg_lastupdate_index = cursor.getColumnIndex(SYSTEM_MSG_LASTUPDATE);
		int msg_inactivetime_index = cursor.getColumnIndex(SYSTEM_MSG_INACTIVETIME);
		int msg_user_index = cursor.getColumnIndex(SYSTEM_MSG_USER);
		
		SystemInfoItem item = new SystemInfoItem();
		item.msg_id = cursor.getInt(msg_id_index);
		item.msg_title = cursor.getString(msg_title_index);
		item.msg_body = cursor.getString(msg_body_index);
		item.msg_lastupdate = new Date(cursor.getLong(msg_lastupdate_index));
		item.msg_inactivetime = new Date(cursor.getLong(msg_inactivetime_index));
		item.msg_user = cursor.getString(msg_user_index);
		
		return item;
	}
	
	private boolean InfoItemExists(SystemInfoItem item){
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		Cursor result = db.query(SYSTEM_INFO_TABLE, 
								new String[]{SYSTEM_MSG_ID}, 
								USER_AND_MSG_ID_WHERE, 
								whereArgs(item), 
								null, 
								null, 
								null);
		boolean infoItemExist = (result.getCount()>0);
		result.close();
		return infoItemExist;
	}
	
	private String[] whereArgs(SystemInfoItem item){
		return new String[] {item.msg_user,String.valueOf(item.msg_id)};
	}
	
	private void createTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS " + SYSTEM_INFO_TABLE + " ("
				+ SYSTEM_MSG_ID + " INTEGER,"
				+ SYSTEM_MSG_TITLE + " TEXT,"
				+ SYSTEM_MSG_BODY + " TEXT,"
				+ SYSTEM_MSG_LASTUPDATE + " TEXT,"
				+ SYSTEM_MSG_INACTIVETIME + " TEXT,"
				+ SYSTEM_MSG_USER + " TEXT"
			+ ");");
		mSystemInfoDBHelper.updateTable(db, SYSTEM_INFO_TABLE);
	}
	
	/**
	 * 清除系统消息
	 * @param user
	 */
	public void clean(String user) {
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		db.delete(SYSTEM_INFO_TABLE, SYSTEM_MSG_USER + " = ?", new String[] { user });
	}
}
