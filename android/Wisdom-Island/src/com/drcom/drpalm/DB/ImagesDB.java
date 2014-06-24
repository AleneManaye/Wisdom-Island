package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.objs.ImagesDbItem;

/**
 * 图片存储数据库
 * @author Administrator
 *
 */
public class ImagesDB {


	public static int  CATEGORY_SLIDER = 1;   		 //主界面Slider上图片
	public static int  CATEGORY_MALL_ACTIVITY = 2;   //通告图片
	public static int  CATEGORY_DATA_MALL = 3;       //数据正佳图片
	public static int  CATEGORY_MERCHANT_GUIDE = 4;  //商户指南
	public static int  CATEGORY_GIFTS = 5;  		 //礼品
	public static int  NAVIGATION_SCHOOL_TITLE = 6;  //幼教存放学校titleUrl
	public static int  THUMB_RUL = 7;  //拇指图

	private static String IMAGES_TABLE = "imagestable";

	private static String _ID = "_id";
	private static String URL = "url";
	private static String DATA = "data";
	private static String CATEGORYID = "categoryid";

	private static ImagesDB imagesDBInstance = null;
	private DatabaseHelper mimagesDBHelper;
	
	//多学校处理
	private String schoolkey = "";

	public static ImagesDB getInstance(Context context, String key) {
		if(imagesDBInstance == null||!imagesDBInstance.schoolkey.equals(key)) {
			imagesDBInstance = new ImagesDB(context,key);
			imagesDBInstance.schoolkey = key;
			return imagesDBInstance;
		} else {
			return imagesDBInstance;
		}
	}
	public void startTransaction() {
		mimagesDBHelper.getWritableDatabase().beginTransaction();
	}
	public void endTransaction() {
		mimagesDBHelper.getWritableDatabase().setTransactionSuccessful();
		mimagesDBHelper.getWritableDatabase().endTransaction();
	}

	private ImagesDB(Context context, String key) {
		mimagesDBHelper = DatabaseHelper.getInstance(context,key) ;
		SQLiteDatabase db = mimagesDBHelper.getWritableDatabase() ;
		createTable(db) ;
		db.close();
	}

	private void createTable(SQLiteDatabase db)
	{
		//附件缓存
		db.execSQL("CREATE TABLE IF NOT EXISTS " + IMAGES_TABLE + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ URL + " TEXT,"
				+ DATA + " BLOB,"
				+ CATEGORYID + " INTEGER"
			+ ");");
		// 数据迁移
		mimagesDBHelper.updateTable(db, IMAGES_TABLE);
	}
	
	/**
	 * 清除图片cache相关
	 * @param 无
	 */
	public void clearAllImages()
	{
		SQLiteDatabase db = mimagesDBHelper.getWritableDatabase() ;
		db.delete(IMAGES_TABLE, CATEGORYID +"!=1", null);
	}

	static ImagesDbItem retrieveImagesItem(Cursor cursor) {
		int url_index = cursor.getColumnIndex(URL);
		int data_index = cursor.getColumnIndex(DATA);
		int category_index = cursor.getColumnIndex(CATEGORYID);

		ImagesDbItem item = new ImagesDbItem();
		item.url = cursor.getString(url_index);
		item.data = cursor.getBlob(data_index);
		item.categoryid = cursor.getInt(category_index);
		return item;
	}

	//获取Images列表(参数：分类ID，前limit条)
	public List<ImagesDbItem> getImagesList(int category, String limit) {
		SQLiteDatabase db = mimagesDBHelper.getReadableDatabase();
        String sql = SQLiteQueryBuilder.buildQueryString(false,
        		IMAGES_TABLE,
                null,
                CATEGORYID + "=" + Integer.toString(category),
                null,
                null,
                null,
                limit);

        Cursor cursor = db.rawQuery(sql, null);
		ArrayList<ImagesDbItem> Imageslist = new ArrayList<ImagesDbItem>();
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				Imageslist.add(retrieveImagesItem(cursor));
				cursor.moveToNext();
			}
		}
		cursor.close();
		return Imageslist;
	}
	//是否存在
	public boolean imagesExist(int category,String url)
	{
		SQLiteDatabase db = mimagesDBHelper.getReadableDatabase();
		Cursor cursor = db.query(
			IMAGES_TABLE,
			null,
			CATEGORYID + "=? and " + URL + " =?",
			new String[] {Integer.toString(category),url},
			null, null, null);
		int nCount = cursor.getCount();
		cursor.close();
		if(nCount > 0)
			return true;
		else
			return false;
	}

	private void saveImages(ImagesDbItem item)
	{
		SQLiteDatabase db = mimagesDBHelper.getWritableDatabase() ;

		ContentValues values = new ContentValues();
		values.put(URL, item.url);
		values.put(DATA, item.data);
		values.put(CATEGORYID, item.categoryid);

		if(imagesExist(item.categoryid,item.url)) //存在 update
			db.update(IMAGES_TABLE, values, CATEGORYID + "=? and " + URL + " =?", new String[] {Integer.toString(item.categoryid),item.url});
		else //insert
		{
			long i = db.insert(IMAGES_TABLE, URL, values) ;
			System.out.println("saveImages" + i );
		}
	}

	public void saveSmallImageData(String url, byte[] imageByte, int category_id){
		ImagesDbItem imagesDbItem = new ImagesDbItem();
		imagesDbItem.categoryid = category_id;
		imagesDbItem.url = url;
		imagesDbItem.data = imageByte;
		saveImages(imagesDbItem);
	}

	public void saveFullImageData(String url, byte[] imageByte, int category_id){
		ImagesDbItem imagesDbItem = new ImagesDbItem();
		imagesDbItem.categoryid = category_id;
		imagesDbItem.url = url;
		imagesDbItem.data = imageByte;
		saveImages(imagesDbItem);
	}
	
	public void saveThumbData(String url, byte[] imageByte, int category_id){
		ImagesDbItem imagesDbItem = new ImagesDbItem();
		imagesDbItem.categoryid = category_id;
		imagesDbItem.url = url;
		imagesDbItem.data = imageByte;
		saveImages(imagesDbItem);
	}
	
//	public void saveShopLogoData(ShopItem item, byte[] imageByte, int category_id){
//		ImagesDbItem imagesDbItem = new ImagesDbItem();
//		imagesDbItem.categoryid = category_id;
//		imagesDbItem.url = item.logoURL;
//		imagesDbItem.data = imageByte;
//		saveImages(imagesDbItem);
//	}
//	
//	public void saveShopDetailData(ShopItem item, byte[] imageByte, int category_id){
//		ImagesDbItem imagesDbItem = new ImagesDbItem();
//		imagesDbItem.categoryid = category_id;
//		imagesDbItem.url = item.img.smallURL;
//		imagesDbItem.data = imageByte;
//		saveImages(imagesDbItem);
//	}

	public byte[] getImageByCategory(String url, int category_id){
		SQLiteDatabase db = mimagesDBHelper.getReadableDatabase();
        String sql = SQLiteQueryBuilder.buildQueryString(false,
        		IMAGES_TABLE,
                null,
                CATEGORYID + "=" + Integer.toString(category_id)+" and "+URL+"="+"'"+url+"'" ,
                null,
                null,
                null,
                null);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        ImagesDbItem imageDbItem = new ImagesDbItem();
        if(cursor.getCount() != 0){
        	imageDbItem = retrieveImagesItem(cursor);
        }
        cursor.close();
        return imageDbItem.data;

	}
}
