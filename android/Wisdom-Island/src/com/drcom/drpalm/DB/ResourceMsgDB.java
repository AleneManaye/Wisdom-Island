package com.drcom.drpalm.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.drcom.drpalm.objs.ToursItem;

/**
 * 资源包纪录数据库
 * @author Administrator
 *
 */
public class ResourceMsgDB {

	private static String RESMSG_TABLE = "resourcemsgtable";

	private String _ID = "_id";
	private String SCHOOLID = "schoolid";
	private String RESNAME = "resname";
	private String SIZE = "size";
	private String LASTMDATE = "lastmdate";
	private String FINDPW_URL = "findpwurl";	//找回密码URL
	private String ATTENDANCE_URL = "getattendanceurl";	//获取考勤信息URL

	private static ResourceMsgDB resmsgDBInstance = null;
	private DatabaseHelper mimagesDBHelper;
	
	//多学校处理
	private String schoolkey = "";

	public static ResourceMsgDB getInstance(Context context,String key) {
		if(resmsgDBInstance == null||!resmsgDBInstance.schoolkey.equals(key)) {
			resmsgDBInstance = new ResourceMsgDB(context,key);
			resmsgDBInstance.schoolkey = key;
			return resmsgDBInstance;
		} else {
			return resmsgDBInstance;
		}
	}
	public void startTransaction() {
		mimagesDBHelper.getWritableDatabase().beginTransaction();
	}
	public void endTransaction() {
		mimagesDBHelper.getWritableDatabase().setTransactionSuccessful();
		mimagesDBHelper.getWritableDatabase().endTransaction();
	}

	private ResourceMsgDB(Context context,String key) {
		mimagesDBHelper = DatabaseHelper.getInstance(context,key) ;
		SQLiteDatabase db = mimagesDBHelper.getWritableDatabase() ;
		createTable(db) ;
		db.close();
	}

	private void createTable(SQLiteDatabase db)
	{
		//附件缓存
		db.execSQL("CREATE TABLE IF NOT EXISTS " + RESMSG_TABLE + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SCHOOLID + " TEXT,"
				+ RESNAME + " TEXT,"
				+ SIZE + " TEXT,"
				+ LASTMDATE + " TEXT,"
				+ FINDPW_URL + " TEXT,"
				+ ATTENDANCE_URL + " TEXT"
			+ ");");
		// 数据迁移
		mimagesDBHelper.updateTable(db, RESMSG_TABLE);
	}
	
//	/**
//	 * 清除图片cache相关
//	 * @param 无
//	 */
//	public void clearAllImages()
//	{
//		SQLiteDatabase db = mimagesDBHelper.getWritableDatabase() ;
//		db.delete(IMAGES_TABLE, CATEGORYID +"!=1", null);
//	}

	private ToursItem retrieveImagesItem(Cursor cursor) {
		int schoolid_index = cursor.getColumnIndex(SCHOOLID);
		int resname_index = cursor.getColumnIndex(RESNAME);
		int size_index = cursor.getColumnIndex(SIZE);
		int lastmdate_index = cursor.getColumnIndex(LASTMDATE);
		int findpwurl_index = cursor.getColumnIndex(FINDPW_URL);
		

		ToursItem item = new ToursItem();
		item.schoolid = cursor.getString(schoolid_index);
		item.name = cursor.getString(resname_index);
		item.size = cursor.getString(size_index);
		item.lastmdate = cursor.getString(lastmdate_index);
		item.url = cursor.getString(findpwurl_index);
		return item;
	}

	public ToursItem getToursItem(String schoolid) {
		SQLiteDatabase db = mimagesDBHelper.getReadableDatabase();
        String sql = SQLiteQueryBuilder.buildQueryString(false,
        		RESMSG_TABLE,
                null,
                null,//SCHOOLID + " = '" + schoolid + "'",
                null,
                null,
                null,
                null);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.requery();
        
        ToursItem item = new ToursItem();
		if(cursor.moveToFirst()) {
			item = retrieveImagesItem(cursor);
		}
		cursor.close();
		return item;
	}
	
	//是否存在
	public boolean toursExist()//String schoolid)
	{
		SQLiteDatabase db = mimagesDBHelper.getReadableDatabase();
		Cursor cursor = db.query(
			RESMSG_TABLE,
			null,
			null,//SCHOOLID + " = '" + schoolid + "'",
			null,null, null, null);
		int nCount = cursor.getCount();
		cursor.close();
		if(nCount > 0)
			return true;
		else
			return false;
	}

	public void saveToursItem(ToursItem item)
	{
		SQLiteDatabase db = mimagesDBHelper.getWritableDatabase() ;

		ContentValues values = new ContentValues();
		values.put(SCHOOLID, item.schoolid);
		values.put(RESNAME, item.name);
		values.put(SIZE, item.size);
		values.put(LASTMDATE, item.lastmdate);
		values.put(FINDPW_URL, item.url);

		if(toursExist())//item.schoolid)) //存在 update
			db.delete(RESMSG_TABLE, null, null);
//			db.update(RESMSG_TABLE, values, SCHOOLID + " = ?", new String[] {item.schoolid});
//		else //insert
//		{
			long i = db.insert(RESMSG_TABLE, null, values);
			Log.i("zjj", "saveToursItem" + i);
//		}
	}

//	public byte[] getImageByCategory(String url, int category_id){
//		SQLiteDatabase db = mimagesDBHelper.getReadableDatabase();
//        String sql = SQLiteQueryBuilder.buildQueryString(false,
//        		IMAGES_TABLE,
//                null,
//                CATEGORYID + "=" + Integer.toString(category_id)+" and "+URL+"="+"'"+url+"'" ,
//                null,
//                null,
//                null,
//                null);
//
//        Cursor cursor = db.rawQuery(sql, null);
//        cursor.moveToFirst();
//        ImagesDbItem imageDbItem = new ImagesDbItem();
//        if(cursor.getCount() != 0){
//        	imageDbItem = retrieveImagesItem(cursor);
//        }
//        cursor.close();
//        return imageDbItem.data;
//
//	}
	
	/**
	 * 保存获取考勤信息
	 */
	public void SaveAttendanceUrl(String schoolid,String url){
		SQLiteDatabase db = mimagesDBHelper.getWritableDatabase() ;

		ContentValues values = new ContentValues();
		values.put(ATTENDANCE_URL, url);
		
		db.update(RESMSG_TABLE, values, SCHOOLID + " = ?", new String[] {schoolid});
		
	}
	
	/**
	 * 取获取考勤信息连接
	 * @return
	 */
	public String GetAttendanceUrl(){
		SQLiteDatabase db = mimagesDBHelper.getReadableDatabase();
        String sql = SQLiteQueryBuilder.buildQueryString(false,
        		RESMSG_TABLE,
                null,
                null,//SCHOOLID + " = '" + schoolid + "'",
                null,
                null,
                null,
                null);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.requery();
        
        String url = "";
		if(cursor.moveToFirst()) {
			url = cursor.getString(cursor.getColumnIndex(ATTENDANCE_URL));
		}
		cursor.close();
		return url;
	}
	
}