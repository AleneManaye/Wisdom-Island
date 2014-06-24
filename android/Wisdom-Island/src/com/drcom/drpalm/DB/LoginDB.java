package com.drcom.drpalm.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.View.controls.MyMothod;

/**
 * 登录用户数据库
 * @author menchx
 *
 */
public class LoginDB {
	//table
	private static final String TABLE = "loginInfoTable";
	
	//systemInfo table field
	private static final String ID = "_id";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String NICKNAME = "nickname";
	private static final String LEVEL = "level";
	private static final String LEVELUPSCORE = "levelupscore";
	private static final String CURSCORE = "curscore";
	private static final String REMEMBERPWD = "rememberpwd";
	private static final String AUTOLOGIN = "autologin";
	private static final String USERTYPE = "usertype";
	private static final String TIME = "time";
	private static final String HEADURL = "headurl";
	private static final String LASTUPDATE = "lastupdate"; 
	
//	private static final String PIC = "pic";
	
	private static final String USERNAME_WHERE = USERNAME + " =?";
	
	private static LoginDB mLoginDB;
	private DatabaseHelper mSystemInfoDBHelper;
	
	//多学校处理
	private String schoolkey = "";
	
	public static LoginDB getInstance(Context context,String key){
		if(mLoginDB == null||!mLoginDB.schoolkey.equals(key)){
			mLoginDB = new LoginDB(context,key);
			mLoginDB.schoolkey = key;
		}
		return mLoginDB;
	}
	
	private void createTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " ("
				+ USERNAME + " TEXT,"
				+ PASSWORD + " TEXT,"
				+ NICKNAME + " TEXT,"
				+ LEVEL + " TEXT,"
				+ LEVELUPSCORE + " TEXT,"
				+ CURSCORE + " TEXT,"
				+ REMEMBERPWD + " BOOLEAN DEFAULT 0 NOT NULL," 
				+ AUTOLOGIN + " BOOLEAN DEFAULT 0 NOT NULL," 
				+ USERTYPE + " TEXT,"
				+ TIME + " TEXT,"
				+ HEADURL + " TEXT,"
				+ LASTUPDATE + " TEXT"
//				+ PIC + " BLOB"
			+ ");");
		mSystemInfoDBHelper.updateTable(db, TABLE);
	}
	
	public void startTransaction(){
		mSystemInfoDBHelper.getWritableDatabase().beginTransaction();
	}
	
	public void endTransaction(){
		mSystemInfoDBHelper.getWritableDatabase().setTransactionSuccessful();
		mSystemInfoDBHelper.getWritableDatabase().endTransaction();
	}
	
	public LoginDB(Context context,String key){
		mSystemInfoDBHelper = DatabaseHelper.getInstance(context,key);
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		createTable(db);
		db.close();
	}
	
	/**
	 * 更新用户登录设置
	 * (如：密码，是否保存密码，是否自动登录)
	 * @param item
	 * @return
	 */
	public boolean updataUserSetting(UserInfo item){
		boolean flags = false;
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USERNAME, item.strUsrName.toLowerCase());	//转为小写保存
		values.put(PASSWORD, item.strPassword);
		values.put(REMEMBERPWD, item.bRememberPwd);
		values.put(AUTOLOGIN, item.bAutoLogin);

		if(InfoItemExists(item)){
			db.update(TABLE, values, USERNAME_WHERE, new String[] {item.strUsrName.toLowerCase()});
		}else{
			flags = true;
			db.insert(TABLE, ID, values);
		}
		return flags;
	}
	
	/**
	 * 更新用户部分资料
	 * (用作登录成功后,更新服务器上的资料)
	 * @param item
	 */
	public void updataUserNewmsg(UserInfo item){
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USERNAME, item.strUsrName.toLowerCase());	//转为小写保存
		values.put(NICKNAME, item.strUserNickName);
		values.put(LEVEL, item.level);
		values.put(LEVELUPSCORE, item.levelupscore);
		values.put(CURSCORE, item.curscore);
		values.put(USERTYPE, item.strUsrType);
		values.put(TIME, System.currentTimeMillis());
		values.put(HEADURL, item.headurl);
		values.put(LASTUPDATE, item.lastupdate);
//		if(item.pic != null)
//			values.put(PIC, MyMothod.Bitmap2Bytes(item.pic));
		
		if(InfoItemExists(item)){
			db.update(TABLE, values, USERNAME_WHERE, new String[] {item.strUsrName.toLowerCase()});
		}else{
			db.insert(TABLE, ID, values);
		}
	}
	
	/**
	 * 保存用户头像
	 * @param item
	 * @return
	 */
//	public void saveLoginInfoPic(UserInfo item){
//		boolean flags = false;
//		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
//		ContentValues values = new ContentValues();
//		values.put(PIC, MyMothod.Bitmap2Bytes(item.pic));
//		
//		if(InfoItemExists(item)){
//			db.update(TABLE, values, USERNAME_WHERE, new String[] {item.strUsrName});
//		}
//	}
	
	public UserInfo retrieveLoginInfoItem(Cursor cursor){
//		int msg_id_index = cursor.getColumnIndex(SYSTEM_MSG_ID);
		int username_index = cursor.getColumnIndex(USERNAME);
		int pwd_index = cursor.getColumnIndex(PASSWORD);
		int nickname_index = cursor.getColumnIndex(NICKNAME);
		int rememberpwd_index = cursor.getColumnIndex(REMEMBERPWD);
		int autologin_index = cursor.getColumnIndex(AUTOLOGIN);
		int usertype_index = cursor.getColumnIndex(USERTYPE);
		int level_index = cursor.getColumnIndex(LEVEL);
		int levelupscore_index = cursor.getColumnIndex(LEVELUPSCORE);
		int curscore_index = cursor.getColumnIndex(CURSCORE);
		int headurl_index = cursor.getColumnIndex(HEADURL);
//		int pice_index = cursor.getColumnIndex(PIC);
		
		UserInfo item = new UserInfo();
//		item.msg_id = cursor.getInt(msg_id_index);
		item.strUsrName = cursor.getString(username_index);
		item.strPassword = cursor.getString(pwd_index);
		item.strUserNickName = cursor.getString(nickname_index);
		item.level = cursor.getString(level_index);
		item.levelupscore = cursor.getString(levelupscore_index);
		item.curscore = cursor.getString(curscore_index);
		item.bRememberPwd = cursor.getInt(rememberpwd_index) == 1?true:false;
		item.bAutoLogin = cursor.getInt(autologin_index) == 1?true:false;
		item.strUsrType = cursor.getString(usertype_index);
		item.headurl = cursor.getString(headurl_index);
//		byte[] data = cursor.getBlob(pice_index) == null? new byte[1]:cursor.getBlob(pice_index);
//		item.pic = MyMothod.Byte2Bitmap(data);
		
		return item;
	}
	
	public void delLoginInfoItem(UserInfo item){
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		db.delete(TABLE, USERNAME_WHERE, new String[] {item.strUsrName});
	}

	public UserInfo getLoginInfoByName(String name){
		SQLiteDatabase db = mSystemInfoDBHelper.getReadableDatabase();
		Cursor c = db.query(TABLE, 
				null, 
				USERNAME_WHERE, 
				new String[] {name}, 
				null, 
				null, 
				null);
		
		UserInfo userinfo = new UserInfo();
		if(c.getCount()>0){
			c.requery();
			c.moveToFirst();
			userinfo = retrieveLoginInfoItem(c);
		}
		
		c.close();
		return userinfo;
	}
	
	public Cursor getLoginInfoListCursor(){
		SQLiteDatabase db = mSystemInfoDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
				TABLE, 
				null, 
				null, 
				null, 
				null, 
				TIME + " DESC", 
				"5");	//取前5条记录
		return db.rawQuery(queryBuilder, null);
	}
	
	private boolean InfoItemExists(UserInfo item){
		SQLiteDatabase db = mSystemInfoDBHelper.getWritableDatabase();
		Cursor result = db.query(TABLE, 
								new String[]{USERNAME}, 
								USERNAME_WHERE, 
								new String[] {item.strUsrName}, 
								null, 
								null, 
								null);
		boolean infoItemExist = (result.getCount()>0);
		result.close();
		return infoItemExist;
	}
	
}
