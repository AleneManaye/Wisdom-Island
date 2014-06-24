package com.drcom.drpalm.DB;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.objs.AttendanceinfoItem;

/**
 * 考勤信息
 * @author zhaojunjie
 *
 */
public class AttendanceinfoDB {
	//table
	private static final String ATTENDANCE_TABLE = "attendancd_table";
	private static final String RECORDS_TABLE = "records_table";
	
	//tale field
	private static final String ATTENDANCE_ID = "_id";
	private static final String ATTENDANCE_TIME = "attendancetime";
	private static final String STATUS = "status";
	private static final String USER = "user";
	
	private static final String RECORD_ID = "recid";
	private static final String RECORD_ATTENDANCE_ID = "attendanceid";
	private static final String RECORD_TIME = "time";
	private static final String RECORD_STATUS = "status";
	private static final String RECORD_DES = "des";
	private static final String RECORD_USER = "user";
	
	DatabaseHelper attendanceinfoDBHelper;
	private static AttendanceinfoDB attendanceinfoDBInstance = null;
	
	private static final String ATTENDANCE_ID_AND_USER_WHERE = ATTENDANCE_ID + " =?" + " AND " + USER + " =?";
	private static final String RECORD_ID_AND_USER_WHERE = RECORD_ID + " =?" + " AND " + RECORD_USER + " =?";
	private static final String RECORD_ID_AND_RECORD_ATTENDANCE_ID_AND_USER_WHERE = RECORD_ID + " =?" + " AND " + RECORD_ATTENDANCE_ID + " =?" + " AND " + RECORD_USER + " =?";
	
	//多学校处理
	private String schoolkey = "";
	
	public static AttendanceinfoDB getInstance(Context context, String key) {
		if((attendanceinfoDBInstance == null)||!attendanceinfoDBInstance.schoolkey.equals(key)) {
			attendanceinfoDBInstance = new AttendanceinfoDB(context,key);
			attendanceinfoDBInstance.schoolkey = key;
		} 
		return attendanceinfoDBInstance;
	}
	
	private AttendanceinfoDB(Context context,String key) {
		attendanceinfoDBHelper = DatabaseHelper.getInstance(context,key) ;
		SQLiteDatabase db = attendanceinfoDBHelper.getWritableDatabase() ;
		createTable(db) ;
		db.close();
	}
	
	/**
	 * 开始事务
	 */
	public void startTransaction() {
		attendanceinfoDBHelper.getWritableDatabase().beginTransaction();
	}
	/**
	 * 结束事务
	 */
	public void endTransaction() {
		attendanceinfoDBHelper.getWritableDatabase().setTransactionSuccessful();
		attendanceinfoDBHelper.getWritableDatabase().endTransaction();
	}
	
	//创建表
	private void createTable(SQLiteDatabase db){
		String CTEATE_EVENT_TABLE  = "CREATE TABLE IF NOT EXISTS " + ATTENDANCE_TABLE + " ("
			+ ATTENDANCE_ID + " TEXT,"
			+ ATTENDANCE_TIME + " TEXT,"
			+ STATUS + " TEXT,"	
			+ USER + " TEXT"
			+ ");" ;
		db.execSQL(CTEATE_EVENT_TABLE);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + RECORDS_TABLE + " ("
				+ RECORD_ID + " TEXT,"
				+ RECORD_ATTENDANCE_ID + " TEXT,"
				+ RECORD_TIME + " TEXT,"
				+ RECORD_STATUS + " TEXT,"
				+ RECORD_DES + " TEXT,"
				+ RECORD_USER + " TEXT"
			+ ");");
		
		attendanceinfoDBHelper.updateTable(db, ATTENDANCE_TABLE);
		attendanceinfoDBHelper.updateTable(db, RECORDS_TABLE);
	}
	
	/**
	 * 删除考勤纪录
	 * @param username
	 */
	public void cleanAttendanceinfo(String username){
		SQLiteDatabase db = attendanceinfoDBHelper.getReadableDatabase();
		db.delete(ATTENDANCE_TABLE, USER + " =?", new String[] {username});
		db.delete(RECORDS_TABLE, RECORD_USER + " =?", new String[] {username});
	}
	
	/**
	 * 保存考勤信息
	 * @param item
	 * @return
	 */
	public boolean saveAttendanceinfo(AttendanceinfoItem item){
		boolean flags = false;
		SQLiteDatabase db = attendanceinfoDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ATTENDANCE_ID, item.id);
		values.put(ATTENDANCE_TIME, item.attendancetime);
		values.put(STATUS, item.status);
		values.put(USER, item.mUsername);
		
		if(attendanceinfoExist(item)){
			db.update(ATTENDANCE_TABLE, values, ATTENDANCE_ID_AND_USER_WHERE, new String[]{String.valueOf(item.id),item.mUsername});
		}else{
			flags = true;
			db.insert(ATTENDANCE_TABLE, ATTENDANCE_ID, values);
		}
		
		// 插入考勤纪录
		if (!item.mRecordslist.isEmpty()) {
			for (AttendanceinfoItem.Records record : item.mRecordslist) {
				ContentValues recordvalues = new ContentValues();
				recordvalues.put(RECORD_ID, record.id);
				recordvalues.put(RECORD_ATTENDANCE_ID,item.id);
				recordvalues.put(RECORD_TIME, record.time);
				recordvalues.put(RECORD_STATUS, record.status);
				recordvalues.put(RECORD_DES, record.des);
				recordvalues.put(RECORD_USER, record.mUsername);
				
				if(recordExist(record,item.id)){
					db.update(RECORDS_TABLE, recordvalues, 
							RECORD_ID_AND_RECORD_ATTENDANCE_ID_AND_USER_WHERE, 
							new String[]{String.valueOf(record.id),
							item.id,record.mUsername});
				}else{
					flags = true;
					db.insert(RECORDS_TABLE, RECORD_ID, recordvalues);
				}
				
				recordvalues = null;
			}
		}
		
		values = null;
		
		return flags;
	}
	
	public boolean attendanceinfoExist(AttendanceinfoItem item) {
		SQLiteDatabase db = attendanceinfoDBHelper.getReadableDatabase();

		Cursor result = db.query(ATTENDANCE_TABLE, 
				new String[] { ATTENDANCE_ID }, 
				ATTENDANCE_ID_AND_USER_WHERE, 
				new String[] { item.id, item.mUsername },
				null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}
	
	public boolean recordExist(AttendanceinfoItem.Records record,String attendaceid) {
		SQLiteDatabase db = attendanceinfoDBHelper.getReadableDatabase();

		Cursor result = db.query(RECORDS_TABLE, 
				new String[] { RECORD_ID }, 
				RECORD_ID_AND_RECORD_ATTENDANCE_ID_AND_USER_WHERE, 
				new String[] { record.id,attendaceid, record.mUsername },
				null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}
	
	/**
	 * 取考勤列表
	 * @return
	 */
	public ArrayList<AttendanceinfoItem> getAttendanceinfos(String username,String limit){
		ArrayList<AttendanceinfoItem> AttendanceinfoList = new ArrayList<AttendanceinfoItem>();
		Cursor cursor = getAttendanceinfosCursor(username,limit);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				AttendanceinfoList.add(retrieveAttendanceinfoItem(cursor));
				cursor.moveToNext();
			}
		}
		cursor.close();
		return  AttendanceinfoList;
	}
	
	/**
	 * 取考勤列表总数
	 * @return
	 */
	public int getAttendanceinfosSum(String username){
		SQLiteDatabase db = attendanceinfoDBHelper.getReadableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
				ATTENDANCE_TABLE, null, 
				USER + " = '" + username + "'", 
				null, null, 
				ATTENDANCE_TIME + " DESC ",
				null);
		Cursor cursor = db.rawQuery(joinQuery, null);
//		cursor.requery();
		int sum = cursor.getCount();
		return sum;
	}
	
	/**
	 * 取考勤信息
	 * 
	 * @param username
	 * @param limit	条数
	 * @return
	 */
	public Cursor getAttendanceinfosCursor(String username,String limit) {
		SQLiteDatabase db = attendanceinfoDBHelper.getReadableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
				ATTENDANCE_TABLE, null, 
				USER + " = '" + username + "'", 
				null, null, 
				ATTENDANCE_TIME + " DESC ", 
				limit);
		return db.rawQuery(joinQuery, null);
	}
	
	/**
	 * 单独解析为单个Item数据
	 * 
	 * @param cursor
	 * @return
	 */
	public AttendanceinfoItem retrieveAttendanceinfoItem(Cursor cursor) {

		AttendanceinfoItem item = new AttendanceinfoItem();

		if (!cursor.isClosed() && cursor.getColumnCount() > 0) {
			int id_index = cursor.getColumnIndex(ATTENDANCE_ID);
			int time_index = cursor.getColumnIndex(ATTENDANCE_TIME);
			int status_index = cursor.getColumnIndex(STATUS);
			int user_index = cursor.getColumnIndex(USER);
			
			item.id = cursor.getString(id_index);
			item.attendancetime = cursor.getString(time_index);
			item.status = cursor.getString(status_index);
			item.mUsername = cursor.getString(user_index);
			
			ArrayList<AttendanceinfoItem.Records> recordtempItems = new ArrayList<AttendanceinfoItem.Records>();
			recordtempItems = retrieveRecordsItem(item);
			if (!recordtempItems.isEmpty())
				item.mRecordslist = recordtempItems;
		}

		return item;
	}
	
	/**
	 * 从传入的item中得到得item属性才能拿到List<Reviewtemp>
	 * 
	 * @param item
	 * @return
	 */
	public ArrayList<AttendanceinfoItem.Records> retrieveRecordsItem(AttendanceinfoItem item) {
		ArrayList<AttendanceinfoItem.Records> recordtempItems = new ArrayList<AttendanceinfoItem.Records>();
		SQLiteDatabase db = attendanceinfoDBHelper.getReadableDatabase();
		Cursor cursor = db.query(RECORDS_TABLE, null, 
				RECORD_ATTENDANCE_ID + " =?" + " AND " + RECORD_USER + " =?", 
				new String[] { item.id, item.mUsername }, null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int id_index = cursor.getColumnIndex(RECORD_ID);
				int time_index = cursor.getColumnIndex(RECORD_TIME);
				int status_index = cursor.getColumnIndex(RECORD_STATUS);
				int des_index = cursor.getColumnIndex(RECORD_DES);
				int user_index = cursor.getColumnIndex(RECORD_USER);
				
				AttendanceinfoItem.Records recordtemp = new AttendanceinfoItem.Records();
				recordtemp.id = cursor.getString(id_index);
				recordtemp.time = cursor.getString(time_index);
				recordtemp.status = cursor.getString(status_index);
				recordtemp.des = cursor.getString(des_index);
				recordtemp.mUsername = cursor.getString(user_index);
				
				recordtempItems.add(recordtemp);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return recordtempItems;
	}
}
