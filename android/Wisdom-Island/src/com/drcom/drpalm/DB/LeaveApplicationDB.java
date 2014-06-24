package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.drcom.drpalm.objs.EventDetailsItem.Imags;
import com.drcom.drpalm.objs.EventDetailsItem.Replyer;
import com.drcom.drpalm.objs.EventDetailsItem.ReviewTemp;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * 告假申请数据库
 * @author zhr
 * **/
public class LeaveApplicationDB {
	
	//告假申请列表
	private static final String LEAVE_APPLICATION_LIST_TABLE = "leave_application_list";
	private static final String LEAVE_ID = "_id";//告假id
	private static final String LEAVE_TYPE = "tpye";//请假类型
	private static final String LEAVE_CONTENT = "content";//告假内容
	private static final String PUB_ID = "pub_id";//发布人id
	private static final String PUB_NAME = "pub_name";//发布人名称
	private static final String OWNER_ID = "owner_id";//接收人id
	private static final String OWNER_NAME = "owner_name";//接收人名称
	private static final String LEAVE_POST = "post";//发布时间
	private static final String LEAVE_START = "start";//告假开始时间
	private static final String LEAVE_END = "end";//告假结束时间
	private static final String LEAVE_TITLE = "title";//告假标题
	private static final String LASTUPDATE = "lastupdate";//最后更新时间
	private static final String ISREAD = "isread";//是否已读
	private static final String HASATT = "hasatt";//是否有附件
	
	//图片附件
	private static final String LEAVE_IMG_TABLE = "leave_img";
	private static final String URL = "url";//图片url
	private static final String DESCRIPTION = "description";//图片描述
	private static final String ATTID = "attid";//附件id
	private static final String ATT_PREVIEW = "att_preview";//附件缩略图
	private static final String ATT_SIZE = "att_size";//附件大小
	private static final String ATT_NAME = "att_NAME";//附件名称
	
	private static final String USER = "user";
	private static final String LEAVE_ID_WHERE_AND_USER_WHERE = LEAVE_ID + " =? and " + USER + " =?";
	private static LeaveApplicationDB  leaveDBInstance = null;
	private DatabaseHelper leaveDBHelper;
	private String schoolkey = "";
	public static LeaveApplicationDB getInstance(Context context, String key) {
		if (leaveDBInstance == null || !leaveDBInstance.schoolkey.equals(key)) {
			leaveDBInstance = new LeaveApplicationDB(context, key);
			leaveDBInstance.schoolkey = key;
			return leaveDBInstance;
		} else {
			return leaveDBInstance;
		}
	}

	private LeaveApplicationDB(Context context, String key) {
		leaveDBHelper = DatabaseHelper.getInstance(context, key);
		SQLiteDatabase db = leaveDBHelper.getWritableDatabase();
		createTable(db);
		// db.close();
	}

	private void createTable(SQLiteDatabase db) {
		String CREATE_LEAVE_APPLICATION_LIST_TABLE = "CREATE TABLE IF NOT EXISTS "+ LEAVE_APPLICATION_LIST_TABLE + " ("
		+ USER + " TEXT,"
		+ LEAVE_ID + " INTEGER,"
		+ LEAVE_TYPE + " TEXT,"
		+ LEAVE_CONTENT + " TEXT,"
		+ PUB_ID + " TEXT,"
		+ PUB_NAME + " TEXT,"  
		+ OWNER_ID + " INTEGER,"
		+ OWNER_NAME + " TEXT,"
		+ LEAVE_POST + " TEXT,"
		+ LEAVE_START + " TEXT,"
		+ LEAVE_END + " TEXT,"
		+ LEAVE_TITLE + " TEXT,"
		+ LASTUPDATE + " TEXT DEFAULT '',"
		+ ISREAD + " INTEGER,"
		+ HASATT + " INTEGER"
		+ ");";
		db.execSQL(CREATE_LEAVE_APPLICATION_LIST_TABLE);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + LEAVE_IMG_TABLE 
				+ " (" + LEAVE_ID + " INTEGER," 
				+ URL + " TEXT," 
				+ DESCRIPTION + " TEXT," 
				+ ATTID + " INTEGER," 
				+ ATT_PREVIEW + " TEXT," 
				+ ATT_SIZE + " TEXT," 
				+ ATT_NAME + " TEXT," 
				+ USER + " TEXT" 
				+ ");");
		
	}
	
	/**
	 * 清除user用户的leave事件
	 */
	public void clearAllStories(String user) {
		SQLiteDatabase db = leaveDBHelper.getWritableDatabase();
		db.delete(LEAVE_APPLICATION_LIST_TABLE, " user = ?", new String[] { user });
	}
	/**
	 * 开始事务
	 */
	public void startTransaction() {
		leaveDBHelper.getWritableDatabase().beginTransaction();

	}

	/**
	 * 结束事务
	 */
	public void endTransaction() {
		try {
			leaveDBHelper.getWritableDatabase().setTransactionSuccessful();
		} finally {
			if (leaveDBHelper.getWritableDatabase().inTransaction()) {
				leaveDBHelper.getWritableDatabase().endTransaction();
			}

		}
	}
	
	/**
	 * 保存告假信息
	 * **/
	public synchronized boolean saveleavesItem(LeaveApplicationMainItem leaveItem) {
		boolean bFlag = false;
		SQLiteDatabase db = leaveDBHelper.getWritableDatabase();
		//一般告假信息（不包括附件详细信息）
		ContentValues leaveValues = new ContentValues();
		leaveValues.put(LEAVE_ID, leaveItem.leaveid);
		leaveValues.put(LEAVE_TYPE, leaveItem.type);
		if (leaveItem.content!=null) {
			leaveValues.put(LEAVE_CONTENT, leaveItem.content);
		}else{
			leaveValues.put(LEAVE_CONTENT, " ");
		}
		leaveValues.put(USER, leaveItem.user);
		leaveValues.put(PUB_ID, leaveItem.pubid);
		leaveValues.put(PUB_NAME, leaveItem.pubname);
		leaveValues.put(OWNER_ID, leaveItem.ownerid);
		leaveValues.put(OWNER_NAME, leaveItem.owner);
		leaveValues.put(LEAVE_TITLE, leaveItem.title);
		if (leaveItem.post == null) {
			leaveValues.put(LEAVE_POST, 0);
		} else {
			leaveValues.put(LEAVE_POST, leaveItem.post.getTime());
		}
		if (leaveItem.start == null) {
			leaveValues.put(LEAVE_START, 0);
		} else {
			leaveValues.put(LEAVE_START, leaveItem.start.getTime());
		}
		if (leaveItem.end == null) {
			leaveValues.put(LEAVE_END, 0);
		} else {
			leaveValues.put(LEAVE_END, leaveItem.end.getTime());
		}
		if (leaveItem.lastupdate == null) {
			leaveValues.put(LASTUPDATE, 0+"");
		} else {
			leaveValues.put(LASTUPDATE, leaveItem.lastupdate.getTime()+"");
		}
		leaveValues.put(ISREAD, leaveItem.isread);
		
//		Log.i("XXXX", " save lastupdate:" + leaveItem.title + "," + leaveItem.lastupdate.getTime());
		
		
		leaveValues.put(HASATT, leaveItem.hasatt);
		
		//保存附件详细信息（如果有）
		if (leaveItem.attr.size() != 0) {
			db.delete(LEAVE_IMG_TABLE, "_id = ? and user = ?", whereArgs(leaveItem));
			for (int i = 0; i < leaveItem.attr.size(); i++) {
				ContentValues attrValues = new ContentValues();
				attrValues.put(LEAVE_ID, leaveItem.leaveid);
				attrValues.put(URL, leaveItem.attr.get(i).URL);
				attrValues.put(USER, leaveItem.user);
				attrValues.put(DESCRIPTION, leaveItem.attr.get(i).imgDescription);
				attrValues.put(ATTID, leaveItem.attr.get(i).attid);
				attrValues.put(ATT_PREVIEW, leaveItem.attr.get(i).preview);
				attrValues.put(ATT_NAME, leaveItem.attr.get(i).attname);
				attrValues.put(ATT_SIZE, leaveItem.attr.get(i).size);
				db.insert(LEAVE_IMG_TABLE, LEAVE_ID, attrValues);
			}
			Log.i("zhr","告假申请附件详细信息已保存，有附件："+leaveItem.attr.size());
		}
		if (leaveExists(leaveItem.leaveid, leaveItem.user)) {
			bFlag = false;
			int i = db.update(LEAVE_APPLICATION_LIST_TABLE, leaveValues,
					LEAVE_ID_WHERE_AND_USER_WHERE, whereArgs(leaveItem));
			Log.i("zhr", "update:" + leaveItem.title);
		} else {
			try {
				bFlag = true;
				long k = db.insert(LEAVE_APPLICATION_LIST_TABLE, null,
						leaveValues);
				Log.i("zhr", "insert:" + leaveItem.title);
			} catch (Exception e) {
				Log.i("zhr", "insert error");
			}
		}
		return bFlag;
	}
	
	/**
	 * @return 返回全部告假申请item的cursor
	 * 
	 * **/
	
	public Cursor getLeaveCursor(String name,String limit){
		SQLiteDatabase db = leaveDBHelper.getReadableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, LEAVE_APPLICATION_LIST_TABLE, 
				null, 
				"user = '" + name+ "'",
				null, null, 
				LASTUPDATE +" DESC", 
				limit);
		return db.rawQuery(joinQuery, null);
	}
	

	/**
	 * @return 返回id为leaveid的item的cursor
	 * 
	 * **/
	
	public Cursor getLeaveIdCursor(String leaveid){
		SQLiteDatabase db = leaveDBHelper.getReadableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, LEAVE_APPLICATION_LIST_TABLE, 
				null, 
				"_id = '" + leaveid+ "'",
				null, null, 
				null, 
				null);
		return db.rawQuery(joinQuery, null);
	}
	
	/**
	 * 单独解析为单个Item数据
	 * @param cursor
	 * @return LeaveApplicationMainItem
	 */
	public LeaveApplicationMainItem retrieveLeaveApplicationMainItem(Cursor cursor) {

		LeaveApplicationMainItem item = new LeaveApplicationMainItem();

		if (!cursor.isClosed() && cursor.getColumnCount() > 0) {
			item.leaveid = cursor.getInt(cursor.getColumnIndex(LEAVE_ID));
			item.type = cursor.getString(cursor.getColumnIndex(LEAVE_TYPE));
			item.pubid = cursor.getString(cursor.getColumnIndex(PUB_ID));
			item.pubname = cursor.getString(cursor.getColumnIndex(PUB_NAME));
			item.ownerid = cursor.getString(cursor.getColumnIndex(OWNER_ID));
			item.owner = cursor.getString(cursor.getColumnIndex(OWNER_NAME));
			item.post = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(LEAVE_POST)))*1000);
			item.start = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(LEAVE_START)))*1000);
			item.end = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(LEAVE_END)))*1000);
			item.lastupdate = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(LASTUPDATE))));
			item.title = cursor.getString(cursor.getColumnIndex(LEAVE_TITLE));
			Log.i("XXXX", " get lastupdate:" + item.title + ","  +  Long.parseLong(cursor.getString(cursor.getColumnIndex(LASTUPDATE))));
			
			item.hasatt = cursor.getInt(cursor.getColumnIndex(HASATT));
			item.isread = cursor.getInt(cursor.getColumnIndex(ISREAD));
			item.user = cursor.getString(cursor.getColumnIndex(USER ));
			item.content = cursor.getString(cursor.getColumnIndex(LEAVE_CONTENT ));
			if(item.hasatt == 1){
				item.attr = retrieveImagesItem(item);
			}
		}
		return item;
	}
	
	/**
	 * 解析单独一个告假申请的附件信息
	 * **/
	public List<EventDetailsItem.Imags> retrieveImagesItem(LeaveApplicationMainItem item){
		List<Imags> ImageItems = new ArrayList<Imags>();
		SQLiteDatabase db = leaveDBHelper.getReadableDatabase();
		Cursor cursor = db.query(LEAVE_IMG_TABLE, null, "_id = ? and user = ?", whereArgs(item), null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int url_index = cursor.getColumnIndex(URL);
				int description_index = cursor.getColumnIndex(DESCRIPTION);
				int att_id_index = cursor.getColumnIndex(ATTID);
				int att_preview_index = cursor.getColumnIndex(ATT_PREVIEW);
				int att_size_index = cursor.getColumnIndex(ATT_SIZE);
				int att_name_index = cursor.getColumnIndex(ATT_NAME);
				Imags image = new Imags(cursor.getString(url_index), 
						cursor.getString(description_index), 
						cursor.getInt(att_id_index));
				image.preview = cursor.getString(att_preview_index);
				image.size = cursor.getString(att_size_index);
				image.attname = cursor.getString(att_name_index);
				ImageItems.add(image);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return ImageItems;
	}
	
	public boolean leaveExists(int id, String user) {
		SQLiteDatabase db = leaveDBHelper.getReadableDatabase();

		Cursor result = db.query(LEAVE_APPLICATION_LIST_TABLE,
				new String[] { LEAVE_ID }, LEAVE_ID_WHERE_AND_USER_WHERE,
				new String[] { Integer.toString(id), user }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	private String[] whereArgs(LeaveApplicationMainItem leaveItem) {

		return new String[] { Integer.toString(leaveItem.leaveid),
				leaveItem.user };
	}
}
