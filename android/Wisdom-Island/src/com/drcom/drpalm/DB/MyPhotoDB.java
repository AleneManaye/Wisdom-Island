package com.drcom.drpalm.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.objs.MyPhoto;

/**
 * 个人相册数据库
 * @author zjj
 *
 */
public class MyPhotoDB {
	//table
	private static final String TABLE = "myphotoTable";
	
	//systemInfo table field
	private static final String ID = "_id";
	private static final String USERNAME = "username";
	private static final String IMGID = "imgid";
	private static final String FILENAME = "filename";
	private static final String STATUS = "status";
	private static final String DES = "des";
	private static final String URL = "url";
	private static final String LASTUPDATETIME = "time";
	private static final String PIC = "pic";
	
	private static final String USERNAME_AND_IMGID_WHERE = USERNAME + " =? and " + IMGID + " =?";
	
	private static MyPhotoDB mDB;
	private DatabaseHelper mDBHelper;
	
	//多学校处理
	private String schoolkey = "";
	
	public static MyPhotoDB getInstance(Context context,String key){
		if(mDB == null||!mDB.schoolkey.equals(key)){
			mDB = new MyPhotoDB(context,key);
			mDB.schoolkey = key;
		}
		return mDB;
	}
	
	private void createTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " ("
				+ USERNAME + " TEXT,"
				+ IMGID + " TEXT,"
				+ FILENAME + " TEXT,"
				+ STATUS + " TEXT,"
				+ DES + " TEXT,"
				+ URL + " TEXT,"
				+ LASTUPDATETIME + " TEXT,"
				+ PIC + " BLOB"
			+ ");");
		mDBHelper.updateTable(db, TABLE);
	}
	
	public void startTransaction(){
		mDBHelper.getWritableDatabase().beginTransaction();
	}
	
	public void endTransaction(){
		mDBHelper.getWritableDatabase().setTransactionSuccessful();
		mDBHelper.getWritableDatabase().endTransaction();
	}
	
	public MyPhotoDB(Context context,String key){
		mDBHelper = DatabaseHelper.getInstance(context,key);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		createTable(db);
		db.close();
	}
	
	public boolean saveMyPhotoItem(MyPhoto item){
		boolean flags = false;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
//		values.put(ID, item.id);
		values.put(USERNAME, item.username);
		values.put(IMGID, item.imgid);
		values.put(STATUS, item.status);
		values.put(FILENAME, item.filename);
		values.put(DES, item.des);
		values.put(URL, item.url);
		values.put(LASTUPDATETIME, item.lastupdatetime);
		if(item.data != null)
			values.put(PIC, item.data);
		
		if(ItemExists(item)){
			db.update(TABLE, values, USERNAME_AND_IMGID_WHERE, new String[] {item.username,item.imgid});
		}else{
			flags = true;
			db.insert(TABLE, ID, values);
		}
		return flags;
	}
	
	// 删除个人相册
	public void deleteMyPhotoItem(String id, String username) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		db.delete(TABLE, IMGID + " = ? and " + USERNAME + " = ? ", new String[] { id, username });
	}
	
	/**
	 * 保存用户头像
	 * @param item
	 * @return
	 */
//	public void saveLoginInfoPic(MyPhoto item){
//		boolean flags = false;
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//		ContentValues values = new ContentValues();
//		values.put(PIC, item.data);
//		
//		if(ItemExists(item)){
//			db.update(TABLE, values, USERNAME_WHERE, new String[] {item.username});
//		}
//	}
	
	public MyPhoto retrieveMyPhotoItem(Cursor cursor){
		int username_index = cursor.getColumnIndex(USERNAME);
		int imgid_index = cursor.getColumnIndex(IMGID);
		int filename_index = cursor.getColumnIndex(FILENAME);
		int des_index = cursor.getColumnIndex(DES);
		int url_index = cursor.getColumnIndex(URL);
		int status_index = cursor.getColumnIndex(STATUS);
		int lastupdatetime_index = cursor.getColumnIndex(LASTUPDATETIME);
		int pic_index = cursor.getColumnIndex(PIC);
		
		MyPhoto item = new MyPhoto();
		item.username = cursor.getString(username_index);
		item.imgid = cursor.getString(imgid_index);
		item.filename = cursor.getString(filename_index);
		item.lastupdatetime = cursor.getString(lastupdatetime_index);
		item.url = cursor.getString(url_index);
		item.des = cursor.getString(des_index);
		item.status = cursor.getString(status_index);
		item.data = cursor.getBlob(pic_index) == null? new byte[1]:cursor.getBlob(pic_index);
		
		return item;
	}
	
//	public void delLoginInfoItem(UserInfo item){
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//		db.delete(TABLE, USERNAME_WHERE, new String[] {item.strUsrName});
//	}

//	public UserInfo getMyPhotoByName(String name){
//		SQLiteDatabase db = mDBHelper.getReadableDatabase();
//		Cursor c = db.query(TABLE, 
//				null, 
//				USERNAME_WHERE, 
//				new String[] {name}, 
//				null, 
//				null, 
//				null);
//		
//		UserInfo userinfo = new UserInfo();
//		if(c.getCount()>0){
//			c.requery();
//			c.moveToFirst();
//			userinfo = retrieveLoginInfoItem(c);
//		}
//		
//		c.close();
//		return userinfo;
//	}
	
	public Cursor getMyPhotoListCursor(String username){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
				TABLE, 
				null, 
				USERNAME + " = '" + username + "' and " + STATUS + " != 'C'", 
				null, 
				null, 
				LASTUPDATETIME + " DESC", 
				null);	//取前5条记录
		return db.rawQuery(queryBuilder, null);
	}
	
	private boolean ItemExists(MyPhoto item){
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		Cursor result = db.query(TABLE, 
								new String[]{USERNAME}, 
								USERNAME_AND_IMGID_WHERE, 
								new String[] {item.username,item.imgid}, 
								null, 
								null, 
								null);
		boolean infoItemExist = (result.getCount()>0);
		result.close();
		return infoItemExist;
	}
	
}
