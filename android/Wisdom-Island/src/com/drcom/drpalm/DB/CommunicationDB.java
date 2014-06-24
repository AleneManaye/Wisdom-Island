package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.drcom.drpalm.objs.CommunicateItem;
import com.drcom.drpalm.objs.ContactItem;

/***
 * 交流数据库
 * @author menchx
 *
 */
public class CommunicationDB {
	
	//table
	private static final String CONTACTS_TABLE = "contacts_table";
	private static final String COMMUNICATE_TABLE = "communicate_table";
	//contacts tale field
	private static final String CONTACT_ID = "_id";
	private static final String CONTACT_NAME = "contact_name";
	private static final String LASTUPDATE = "lastupdate";
	private static final String USER = "user";
	private static final String LASTAWSTIMEREAD = "lastawstimeread";// 阅读回复时,保存该回复的最后回复时间,跟LASTAWSTIME比较,是否有新回复
	private static final String UNREAD = "unread";
	private static final String HEADIMGURL = "headimgurl";	//头像url	2013-12-25
	private static final String HEADIMGLASTUPDATE = "headimglastupdate";	//头像最后更新时间
	private static final String ISVISIBLE = "isvisible";	//2014-02-13 是否可视
	//communicate table field
	private static final String TOPICID = "topic_id";//用于归类查找每个联系人
	private static final String MSGID = "msgid";
	private static final String SENDID = "sendid";
	private static final String SENDNAME = "sendname";
	private static final String RECVCID = "recvcid";
	private static final String RECVCNAME = "recvcname";
	private static final String BODY = "body";
	private static final String CONTACTLASTUPDATE = "contactlastupdate";
	
	private static final String CONTACT_ID_AND_USER_WHERE = CONTACT_ID + " =?" + " AND " + USER + " =?";
	private static final String TOPIC_ID_AND_USER_AND_MSGID_WHERE = TOPICID + " =?" + " AND " + USER + " =?" + " AND " + MSGID + " =?";
	
	DatabaseHelper communicateDBHelper;
	private static CommunicationDB communicationDBInstance = null;
	
	//多学校处理
	private String schoolkey = "";
	
	public static CommunicationDB getInstance(Context context, String key) {
		if((communicationDBInstance == null)||!communicationDBInstance.schoolkey.equals(key)) {
			communicationDBInstance = new CommunicationDB(context,key);
			communicationDBInstance.schoolkey = key;
		} 
		return communicationDBInstance;
	}
	
	private CommunicationDB(Context context,String key) {
		communicateDBHelper = DatabaseHelper.getInstance(context,key) ;
		SQLiteDatabase db = communicateDBHelper.getWritableDatabase() ;
		createTable(db) ;
		db.close();
	}
	
	/**
	 * 开始事务
	 */
	public void startTransaction() {
		communicateDBHelper.getWritableDatabase().beginTransaction();
	}
	/**
	 * 结束事务
	 */
	public void endTransaction() {
		communicateDBHelper.getWritableDatabase().setTransactionSuccessful();
		communicateDBHelper.getWritableDatabase().endTransaction();
	}
	
	/**
	 * 保存联系人
	 * @param item
	 * @return
	 */
	public boolean saveContacts(ContactItem item){
		boolean flags = false;
		SQLiteDatabase db = communicateDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CONTACT_ID, item.cnid);
		values.put(CONTACT_NAME, item.cnname);
		values.put(CONTACTLASTUPDATE, item.lastupdate.getTime());
		values.put(USER, item.user);
		values.put(UNREAD, item.unread);
		values.put(ISVISIBLE, true);
		values.put(HEADIMGURL,item.headimgurl);
		values.put(HEADIMGLASTUPDATE,item.headimglastupdate.getTime());
		
		if(contactExist(item)){
			db.update(CONTACTS_TABLE, values, CONTACT_ID_AND_USER_WHERE, new String[]{String.valueOf(item.cnid),item.user});
		}else{
			flags = true;
			db.insert(CONTACTS_TABLE, CONTACT_ID, values);
		}
		
		return flags;
	}
	
	/**
	 * 转化为联系人对像
	 * @param cursor
	 * @return
	 */
	public ContactItem retrieveContactItem(Cursor cursor){
		int contact_id_index = cursor.getColumnIndex(CONTACT_ID);
		int contact_name_index = cursor.getColumnIndex(CONTACT_NAME);
		int lastupdate_index = cursor.getColumnIndex(CONTACTLASTUPDATE);
		int lastawstimeread_index = cursor.getColumnIndex(LASTAWSTIMEREAD);
		int user_index = cursor.getColumnIndex(USER);
		int unread_index = cursor.getColumnIndex(UNREAD);
		int headimgurl_index = cursor.getColumnIndex(HEADIMGURL);
		int headimglastupdate_index = cursor.getColumnIndex(HEADIMGLASTUPDATE);
		
		ContactItem item = new ContactItem();
		item.cnid = cursor.getString(contact_id_index);
		item.cnname = cursor.getString(contact_name_index);
		item.lastupdate = new Date(cursor.getLong(lastupdate_index));
		item.lastawstimeread = new Date(cursor.getLong(lastawstimeread_index));
		item.lastawstimeseconds = cursor.getLong(lastawstimeread_index)/1000 + "";	//上传到服务器比较出新条数用
		item.user = cursor.getString(user_index);
		item.unread = cursor.getString(unread_index);
		item.headimgurl = cursor.getString(headimgurl_index);
		item.headimglastupdate = new Date(cursor.getLong(headimglastupdate_index));
		
		return item;
	}
	
	/**
	 * 取联系人列表
	 * @param username
	 * @return
	 */
	public Cursor getContacts(String username){
		SQLiteDatabase db = communicateDBHelper.getWritableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false,
				CONTACTS_TABLE,null, USER + " = '" + username + "' and " + ISVISIBLE + " = 1",
				null,
				null,
				CONTACTLASTUPDATE + " DESC",
				null);
		return db.rawQuery(joinQuery, null) ;
	}
	
	/**
	 * 更新联系人列表被读的最后回复的时间
	 * 
	 * @param lastawstimeread
	 *            服务器给的replylasttime(秒数)
	 * @param eventid
	 * @param username
	 * @param replyerid
	 */
	public void updataAsworgLastawstimeread(String lastawstimeread, String username, String replyerid) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIMEREAD, lastawstimeread);

		SQLiteDatabase db = communicateDBHelper.getWritableDatabase();
		int i = db.update(CONTACTS_TABLE, eventsValues, "user = ? and " + CONTACT_ID + " = ?", new String[] {  username, replyerid });

		Log.i("zjj", "更新联系人列表被读的最后回复的时间" + i);
	}
	
	/**
	 * 保存对话内容
	 * @param item
	 * @return
	 */
	public boolean saveCommunicateContent(CommunicateItem item){
		boolean flags = false;
		SQLiteDatabase db = communicateDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TOPICID, item.topic_id);
		values.put(MSGID, item.msg_id);
		values.put(SENDID, item.sendid);
		values.put(SENDNAME, item.sendname);
		values.put(RECVCID, item.recvcid);
		values.put(RECVCNAME, item.recvcname);
		values.put(LASTUPDATE, item.lastupdate.getTime());
		values.put(BODY, item.body);
		values.put(USER, item.user);
		if(!communicateExist(item)){
			flags = true;
			db.insert(COMMUNICATE_TABLE, MSGID, values);
		}
		return flags;
	}
	
	/**
	 * 内容列表
	 * @param user
	 * @param recid
	 * @return
	 */
	public List<CommunicateItem> getAwsContentList(String user,String recid){
		List<CommunicateItem> awsContentList = new ArrayList<CommunicateItem>();
		Cursor cursor = getCommunicateListCursor(user, recid);
//		int i = cursor.getCount();
		cursor.requery();
		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast()) {
				CommunicateItem item = retrieveCommunicateItem(cursor);
				awsContentList.add(item);
				cursor.moveToNext();
			}
		}
		cursor.close();
		
		return awsContentList;
	}
	
	/**
	 * 取聊天内容和头像
	 * @param user
	 * @param recid
	 * @return
	 */
	private Cursor getCommunicateListCursor(String user, String recid){
		SQLiteDatabase db = communicateDBHelper.getReadableDatabase();
		Cursor cursor = db.query(
				COMMUNICATE_TABLE + ", " + CONTACTS_TABLE, 
				null, 
				COMMUNICATE_TABLE + "." + USER + " = '" + user + 
				"' AND " + COMMUNICATE_TABLE + "." + TOPICID + " = '" + recid + 
				"' AND " + CONTACTS_TABLE + "." + USER + " = '" + user + 
				"' AND " + CONTACTS_TABLE + "." + CONTACT_ID + " = '" + recid + "'",
				null, 
				null, 
				null, 
				COMMUNICATE_TABLE + "." + LASTUPDATE);//AWS_TIME + " DESC"
		return cursor;
	}
	
	/**
	 * 转化为对话内容对像
	 * @param cursor
	 * @return
	 */
	public CommunicateItem retrieveCommunicateItem(Cursor cursor){
		int topic_id_index = cursor.getColumnIndex(TOPICID);
		int msgid_index = cursor.getColumnIndex(MSGID);
		int sendid_index = cursor.getColumnIndex(SENDID);
		int sendname_index = cursor.getColumnIndex(SENDNAME);
		int reciveid_index = cursor.getColumnIndex(RECVCID);
		int reciver_name_index = cursor.getColumnIndex(RECVCNAME);
		int body_index = cursor.getColumnIndex(BODY);
		int lastupdate_index = cursor.getColumnIndex(LASTUPDATE);
		int user_index = cursor.getColumnIndex(USER);
		int headimgurl_index = cursor.getColumnIndex(HEADIMGURL);
		
		CommunicateItem item = new CommunicateItem();
		item.topic_id = cursor.getString(topic_id_index);
		item.msg_id = cursor.getInt(msgid_index);
		item.sendid = cursor.getString(sendid_index);
		item.sendname = cursor.getString(sendname_index);
		item.recvcid = cursor.getString(reciveid_index);
		item.recvcname = cursor.getString(reciver_name_index);
		item.body = cursor.getString(body_index);
		item.lastupdate = new Date(cursor.getLong(lastupdate_index));
		item.user = cursor.getString(user_index);
		item.headimgurl = cursor.getString(headimgurl_index);
		
		return item;
	}
	
	private boolean communicateExist(CommunicateItem item){
		SQLiteDatabase db = communicateDBHelper.getReadableDatabase();
		Cursor result = db.query(COMMUNICATE_TABLE, 
								new String[]{MSGID}, 
								TOPIC_ID_AND_USER_AND_MSGID_WHERE, 
								new String[]{String.valueOf(item.topic_id),item.user,String.valueOf(item.msg_id)}, 
								null, 
								null, 
								null);
		boolean communicateExists = (result.getCount()>0);
		result.close();
		return communicateExists;
	}
	
	private boolean contactExist(ContactItem item){
		SQLiteDatabase db = communicateDBHelper.getReadableDatabase();
		Cursor result = db.query(CONTACTS_TABLE, 
								new String[]{CONTACT_ID}, 
								CONTACT_ID_AND_USER_WHERE, 
								new String[]{String.valueOf(item.cnid),item.user}, 
								null, 
								null, 
								null);
		boolean contactExists = (result.getCount()>0);
		result.close();
		return contactExists;
	}
	
	/**
	 * 设置联系人不可视
	 */
	public void updateContactsInvisible(String username){
		SQLiteDatabase db = communicateDBHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ISVISIBLE, false);
		db.update(CONTACTS_TABLE, values, USER + " =?", new String[]{username});
	}
	
	//创建表
	private void createTable(SQLiteDatabase db){
//		db.execSQL("CREATE TABLE IF NOT EXISTS " + CONTACTS_TABLE + " ("
//				+ CONTACT_ID + " TEXT,"
//				+ CONTACT_NAME + " TEXT,"
//				+ LASTUPDATE + " TEXT,"	
//				+ LASTAWSTIMEREAD + " TEXT," 
//				+ UNREAD + " TEXT," 
////				+ HEADIMGURL + " TEXT,"	//2013-12-25
////				+ HEADIMGLASTUPDATE + " TEXT,"
//				+ USER + " TEXT"
//			+ ");");
		String CTEATE_EVENT_TABLE  = "CREATE TABLE IF NOT EXISTS " + CONTACTS_TABLE + " ("
			+ CONTACT_ID + " TEXT,"
			+ CONTACT_NAME + " TEXT,"
			+ CONTACTLASTUPDATE + " TEXT,"	
			+ LASTAWSTIMEREAD + " TEXT," 
			+ UNREAD + " TEXT," 
			+ HEADIMGURL + " TEXT,"	//2013-12-25
			+ HEADIMGLASTUPDATE + " TEXT,"
			+ ISVISIBLE + " BOOLEAN DEFAULT 0 NOT NULL," 	//2014-02-13
			+ USER + " TEXT"
			+ ");" ;
		db.execSQL(CTEATE_EVENT_TABLE);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + COMMUNICATE_TABLE + " ("
				+ TOPICID + " TEXT,"
				+ MSGID + " INTEGER,"
				+ SENDID + " TEXT,"
				+ SENDNAME + " TEXT,"
				+ RECVCID + " TEXT,"
				+ RECVCNAME + " TEXT,"
				+ LASTUPDATE + " TEXT,"	
				+ BODY + " TEXT,"
				+ USER + " TEXT"
			+ ");");
		
		communicateDBHelper.updateTable(db, CONTACTS_TABLE);
		communicateDBHelper.updateTable(db, COMMUNICATE_TABLE);
	}
	
	
}
