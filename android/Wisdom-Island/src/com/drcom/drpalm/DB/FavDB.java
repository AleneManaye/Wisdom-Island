package com.drcom.drpalm.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.FavItem;

/**
 * 用户收藏通告数据库
 * @author menchx
 *
 */
public class FavDB {
	//table
	private static final String TABLE = "unuploadfavTable";	//没提交成功的收藏
	
	//systemInfo table field
	private static final String ID = "_id";
	private static final String EVENTID = "eventid";
	private static final String STATUS = "status";
	private static final String USERNAME = "username";
	
	private final String USERNAME_AND_EVENTID_WHERE = USERNAME + " =? AND " + EVENTID + " =?";
	private final String USERNAME_WHERE = USERNAME + " =?";
	
	private static FavDB mFavDB;
	private DatabaseHelper mFavDBHelper;
	
	//多学校处理
	private String schoolkey = "";
	
	public static FavDB getInstance(Context context,String key){
		if(mFavDB == null||!mFavDB.schoolkey.equals(key)){
			mFavDB = new FavDB(context,key);
			mFavDB.schoolkey = key;
		}
		return mFavDB;
	}
	
	private void createTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " ("
				+ EVENTID + " TEXT,"
				+ STATUS + " TEXT,"
				+ USERNAME + " TEXT"
			+ ");");
		mFavDBHelper.updateTable(db, TABLE);
	}
	
	public void startTransaction(){
		mFavDBHelper.getWritableDatabase().beginTransaction();
	}
	
	public void endTransaction(){
		mFavDBHelper.getWritableDatabase().setTransactionSuccessful();
		mFavDBHelper.getWritableDatabase().endTransaction();
	}
	
	public FavDB(Context context,String key){
		mFavDBHelper = DatabaseHelper.getInstance(context,key);
		SQLiteDatabase db = mFavDBHelper.getWritableDatabase();
		createTable(db);
		db.close();
	}
	
	public boolean saveFavItem(FavItem item){
		boolean flags = false;
		SQLiteDatabase db = mFavDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
//		values.put(ID, item.id);
		values.put(USERNAME, item.mUsername);
		values.put(EVENTID, item.mEventid);
		values.put(STATUS, item.mStatus);
		
		if(ItemExists(item)){
			db.update(TABLE, values, USERNAME_AND_EVENTID_WHERE, new String[] {item.mUsername,item.mEventid+""});
		}else{
			flags = true;
			db.insert(TABLE, ID, values);
		}
		return flags;
	}
	
	public FavItem retrieveLoginInfoItem(Cursor cursor){
//		int msg_id_index = cursor.getColumnIndex(SYSTEM_MSG_ID);
		int username_index = cursor.getColumnIndex(USERNAME);
		int eventid_index = cursor.getColumnIndex(EVENTID);
		int status_index = cursor.getColumnIndex(STATUS);
		
		FavItem item = new FavItem();
//		item.msg_id = cursor.getInt(msg_id_index);
		item.mUsername = cursor.getString(username_index);
		item.mEventid = cursor.getString(eventid_index);
		item.mStatus = cursor.getString(status_index);
		
		return item;
	}
	
//	public void delLoginInfoItem(UserInfo item){
//		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
//		db.delete(TABLE, USERNAME_WHERE, new String[] {item.strUsrName});
//	}

	/**
	 * 取收藏列表
	 */
	public Cursor getFavListCursor(String username){
		SQLiteDatabase db = mFavDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
				TABLE, 
				null, 
				"username = '" + username + "'", 
				null, 
				null, 
				null, 
				null);	
		return db.rawQuery(queryBuilder, null);
	}
	
	/**
	 * 以服务器收藏为准,删除本地离线保存的收藏记录
	 * @param item
	 */
	public void delOldFav(FavItem item){
		SQLiteDatabase db = mFavDBHelper.getReadableDatabase();
		db.delete(TABLE, USERNAME_AND_EVENTID_WHERE, new String[] {item.mUsername,item.mEventid+""});
	}
	
	/**
	 * 清空离线收藏表
	 * @param name
	 */
	public void delAllOldFav(String name) {
		SQLiteDatabase db = mFavDBHelper.getWritableDatabase();
		db.delete(TABLE, "user = ? ", new String[] { name });
	}
	
	private boolean ItemExists(FavItem item){
		SQLiteDatabase db = mFavDBHelper.getWritableDatabase();
		Cursor result = db.query(TABLE, 
								new String[]{EVENTID}, 
								USERNAME_AND_EVENTID_WHERE, 
								new String[] {item.mUsername , item.mEventid+""}, 
								null, 
								null, 
								null);
		boolean infoItemExist = (result.getCount()>0);
		result.close();
		return infoItemExist;
	}
	
}
