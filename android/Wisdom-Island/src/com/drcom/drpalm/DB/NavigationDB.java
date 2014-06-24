package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.util.Log;

import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.objs.BindaccountItem;
import com.drcom.drpalm.objs.NavigationItem;
import com.drcom.drpalm.objs.NavigationListItem;

/**
 * 导航页地区和学校数据库
 * @author menchx
 *
 */
public class NavigationDB {
	//table name 导航首页
	private static final String NAVIGATION_TYPE_TABLE = "navigation_type_table";
	
	//table name 导航学校列表
	private static final String SCHOOL_DISTRICT_TABLE = "school_district_table";
	
	//table bookmark
	private static final String SCHOOL_BOOKMARK_TABLE = "school_bookmark_table";
	
	//table bindaccount
	private static final String BINDACCOUNT_ITEM_TABLE = "bindaccount_item_table";
	
	//school table field
	private static final String ID = "_id";
//	private static final String NAME = "name";
	private static final String SCHOOL_KEY = "school_key";
	private static final String POINT_ID = "point_id";//primary key 
	private static final String STATUS = "status"; //用于与后台数据库同步，做删除操作
	private static final String PARENT_ID = "parent_id";
	private static final String TYPE = "type";
	private static final String TITLEURL = "titleurl";
	private static final String BOOKMARK = "bookmark";
	private static final String TEXT_S = "text_s";		//简单文字	// add by zjj 14-04-21
	private static final String TEXT_T = "text_t";		//繁体字
	private static final String TEXT_EN = "text_en";	//英文
	private static final String IMAGEURL = "imageurl";  //导航首页图片   add by zhr
	private static final String CONTENT = "content";

	// bindaccount_item_table字段
	private static final String USER_ID = "user_id";
	private static final String USER_NAME="user_name";
	private static final String USER_PASSWORD ="user_password";
	private static final String USER_SCHOOL_ID ="user_school_id";
	private static final String USER_IMAGEURL ="user_imageurl" ;			//账号头像url add by hzy 14-05-07
	private static final String USER_IMAGES_NUMBER ="user_image_number";	//账号头像数字      add by hzy 
	private static final String TOKENID = "okenid";	
	private static final String SCHOOLNAME_TEXT_S = "schoolname_text_s";		//简单文字	// add by zjj 14-04-21
	private static final String SCHOOLNAME_TEXT_T = "schoolname_text_t";		//繁体字
	private static final String SCHOOLNAME_TEXT_EN = "schoolname_text_en";	//英文//add by 04/06/09
	
	//databasehelper
	NavigationDatabaseHelper mNavigationDBHelper;
	private static NavigationDB mNavigationDB = null;
	
	
	//查询条件
	private static final String POINT_ID_WHERE = POINT_ID + "=?";
	
	public static NavigationDB getInstance(Context context){
		if(mNavigationDB == null){
			mNavigationDB = new NavigationDB(context);
		}
		return mNavigationDB;
	}
	
	private NavigationDB(Context context){
		mNavigationDBHelper = NavigationDatabaseHelper.getInstance(context);
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		createTable(db);
		db.close();
	}
	
	public void startTransaction(){
		mNavigationDBHelper.getWritableDatabase().beginTransaction();
	}
	
	public void endTransaction(){
		mNavigationDBHelper.getWritableDatabase().setTransactionSuccessful();
		mNavigationDBHelper.getWritableDatabase().endTransaction();
	}
	
	private void createTable(SQLiteDatabase db){
		String CREATE_SCHOOL_DISTRICT_TABLE = "CREATE TABLE IF NOT EXISTS "+ SCHOOL_DISTRICT_TABLE + " ("
									+ ID + " INTEGER,"
//									+ NAME + " TEXT,"
									+ SCHOOL_KEY + " TEXT,"
									+ POINT_ID + " INTEGER,"
									+ PARENT_ID + " INTEGER,"
									+ TYPE + " TEXT,"
									+ TITLEURL + " TEXT,"
									+ BOOKMARK + " BOOLEAN DEFAULT 0 NOT NULL,"
									+ STATUS + " INTEGER,"
									+ TEXT_S +  " TEXT,"	// add by zjj 14-04-21
									+ TEXT_T + " TEXT,"
									+ TEXT_EN + " TEXT"
									+ ");";
		db.execSQL(CREATE_SCHOOL_DISTRICT_TABLE);
		
		String CREATE_SCHOOL_BOOKMARK_TABLE = "CREATE TABLE IF NOT EXISTS "+ SCHOOL_BOOKMARK_TABLE + " ("
									+ ID + " INTEGER,"
									+ POINT_ID + " INTEGER"
									+ ");";
		db.execSQL(CREATE_SCHOOL_BOOKMARK_TABLE);
		
		String CREATE_NAVIGATION_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS "+ NAVIGATION_TYPE_TABLE + " ("
									+ ID + " INTEGER,"
									+ TYPE + " TEXT,"
//									+ POINT_ID + " INTEGER,"
									+ TEXT_T + " TEXT,"  //add by zhr 14-04-22
									+ TEXT_S + " TEXT,"
									+ TEXT_EN + " TEXT,"
									+ CONTENT + " TEXT,"
									+ IMAGEURL + " TEXT"
									+ ");";
		db.execSQL(CREATE_NAVIGATION_TYPE_TABLE);
		
		// 创建bindaccount_item_table
		String CREATE_BINDACCOUNT_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS "+BINDACCOUNT_ITEM_TABLE +"("
				+ USER_ID +" TEXT,"
				+ USER_NAME +" TEXT,"
				+ USER_PASSWORD + " TEXT,"
				+ USER_SCHOOL_ID +" TEXT,"
				+ SCHOOL_KEY + " TEXT,"			
				+ TOKENID + " TEXT,"				//add by hzy 04/06/09
				+ USER_IMAGEURL + " TEXT,"		//账号头像url add by hzy 14-05-07
				+ USER_IMAGES_NUMBER + " TEXT,"  //账号头像数字      add by hzy
				+ SCHOOLNAME_TEXT_S +  " TEXT,"
				+ SCHOOLNAME_TEXT_T + " TEXT,"
				+ SCHOOLNAME_TEXT_EN + " TEXT"
				+");";
		db.execSQL(CREATE_BINDACCOUNT_ITEM_TABLE);
		
		mNavigationDBHelper.updateTable(db, SCHOOL_DISTRICT_TABLE);
		mNavigationDBHelper.updateTable(db, SCHOOL_BOOKMARK_TABLE);
		mNavigationDBHelper.updateTable(db, NAVIGATION_TYPE_TABLE);
		
		mNavigationDBHelper.updateTable(db, BINDACCOUNT_ITEM_TABLE);
	}
	
	public void clearChildID(String parentid){
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		db.delete(SCHOOL_DISTRICT_TABLE, PARENT_ID + "=?", new String[]{parentid});
	}
	
	public void clearChildBySearchKey(String searchkey){
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		
//		db.delete(SCHOOL_DISTRICT_TABLE, NAME + " LIKE '%" + searchkey + "%'", null);
		db.delete(SCHOOL_DISTRICT_TABLE, null, null);
	}
	
	
	public boolean saveNavigationItem(NavigationItem item){
		boolean bFlags = false;
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
//		values.put(NAME, item.name);
		values.put(TYPE, item.type);
		values.put(POINT_ID, item.point_id);
		values.put(PARENT_ID, item.parent_id);
		values.put(SCHOOL_KEY, item.key);
		values.put(TITLEURL, item.titleurl);
		values.put(STATUS,item.status);
		values.put(TEXT_S,item.text_s);
		values.put(TEXT_T,item.text_t);
		values.put(TEXT_EN,item.text_en);
		if(schoolDistrictExist(item.point_id)){
			db.update(SCHOOL_DISTRICT_TABLE, values, POINT_ID_WHERE, new String[]{String.valueOf(item.point_id)});
		}else{
			bFlags = true;
			db.insert(SCHOOL_DISTRICT_TABLE, POINT_ID, values);
		}
		return bFlags;
	}
	
	private boolean schoolDistrictExist(int point_id){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		
		
		Cursor cursor = db.query(SCHOOL_DISTRICT_TABLE, 
					null, 
					POINT_ID_WHERE, 
					new String[]{point_id+""}, 
					null, 
					null, 
					null);
		
		int count = cursor.getCount();
		cursor.close();
		if(count>0){
			return true;
		}else{
			return false;
		}
	}
	
	public Cursor getBookmarkCursor(String limit, CurrentLan lan){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		String[] fields = new String[] { SCHOOL_DISTRICT_TABLE + "." + POINT_ID, SCHOOL_KEY, PARENT_ID, TYPE, TITLEURL, BOOKMARK, STATUS, TEXT_EN, TEXT_S, TEXT_T };
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																SCHOOL_DISTRICT_TABLE + ", " + SCHOOL_BOOKMARK_TABLE, 
																fields, 
																SCHOOL_DISTRICT_TABLE + "."+ POINT_ID + "=" + SCHOOL_BOOKMARK_TABLE + "." + POINT_ID, 
																null, 
																null, 
																lan == CurrentLan.ENGLISH? TEXT_EN + " ASC":TEXT_S + " ASC", 
																limit);	
		return db.rawQuery(queryBuilder, null);
	}
	
	private Cursor getNavigationCursor(int parent_id, String limit, CurrentLan lan){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
																SCHOOL_DISTRICT_TABLE, 
																null, 
																PARENT_ID + "=" + parent_id, 
																null, 
																null, 
																lan == CurrentLan.ENGLISH? TEXT_EN + " ASC":TEXT_S + " ASC", 
																limit);	
		return db.rawQuery(queryBuilder, null);
	}
	
	//获取BINDACCOUNT_ITEM_TABLE游标
	private Cursor getBindaccountItemCursor(){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false,
				BINDACCOUNT_ITEM_TABLE,
				null,
				null,
				null,
				null,
				null,
				null);
		return db.rawQuery(queryBuilder, null);
	}
	
	public void saveNavigationListItem(NavigationListItem item){
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TYPE, item.type);
//		values.put(POINT_ID, item.point_id);
		values.put(IMAGEURL, item.imageurl);
		values.put(TEXT_EN, item.name_en);
		values.put(TEXT_T, item.name_zht);
		values.put(TEXT_S, item.name_zhs);
		values.put(CONTENT, item.content);
//		if(!isNavigationListItemExists(item.point_id)){
			db.insert(NAVIGATION_TYPE_TABLE, CONTENT, values);
//		}
//		else{
//			db.update(NAVIGATION_TYPE_TABLE, values, POINT_ID_WHERE, new String[]{String.valueOf(item.point_id)});
//		}
	}
	
	public void clearAllNavigationList(){
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		db.delete(NAVIGATION_TYPE_TABLE, null, null);
	}
	
	private boolean isNavigationListItemExists(int point_id){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		Cursor cursor = db.query(NAVIGATION_TYPE_TABLE, 
								null, 
								POINT_ID_WHERE, 
								new String[]{point_id+""}, 
								null, 
								null, 
								null);
		int count = cursor.getCount();
		cursor.close();
		if(count>0){
			return true;
		}else{
			return false;
		}
	}
	
//	public void markAsBookmark(int point_id, boolean flags){
//		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
//		ContentValues values = new ContentValues();
//		values.put(BOOKMARK, flags);
//		db.update(SCHOOL_DISTRICT_TABLE, values, POINT_ID + " =?", new String[]{String.valueOf(point_id)});
//	}
	
	public void markAsBookmark(int point_id){
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(POINT_ID, point_id);
		if(!pointExists(point_id)){
			db.insert(SCHOOL_BOOKMARK_TABLE, POINT_ID, values);
		}
	}
	
	public void deleteBookmarkFlag(int point_id){
		SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
		if(pointExists(point_id)){
			db.delete(SCHOOL_BOOKMARK_TABLE, POINT_ID_WHERE, new String[]{String.valueOf(point_id)});
		}
	}
	
	private boolean pointExists(int point_id){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		Cursor cursor = db.query(SCHOOL_BOOKMARK_TABLE, 
								null, 
								POINT_ID_WHERE, 
								new String[]{String.valueOf(point_id)}, 
								null, 
								null, 
								null);
		int count = cursor.getCount();
		cursor.close();
		if(count>0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean getBookmarkByID(int point_id){
//		boolean bookmarkFlags = false;
//		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
//		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
//				SCHOOL_BOOKMARK_TABLE, 
//				new String[]{BOOKMARK}, 
//				POINT_ID + "=" + point_id, 
//				null, 
//				null, 
//				null, 
//				null);
//		Cursor result = db.rawQuery(queryBuilder, null);
//		if(null != result){
//			result.moveToFirst();
//			int bookmark_index = result.getColumnIndex(BOOKMARK);
//			bookmarkFlags = (result.getInt(bookmark_index) == 1?true:false);
//			result.close();
//		}
//		return bookmarkFlags;
		if(pointExists(point_id)){
			return true;
		}else{
			return false;
		}
	}
	
//	public String getNameByID(int point_id){
//		String name = "";
//		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
//		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
//				SCHOOL_DISTRICT_TABLE, 
//				new String[]{NAME}, 
//				POINT_ID + "=" + point_id, 
//				null, 
//				null, 
//				null, 
//				null);
//		Cursor result = db.rawQuery(queryBuilder, null);
//		if(null != result){
//			result.moveToFirst();
//			int name_index = result.getColumnIndex(NAME);
//			name = result.getString(name_index);
//			result.close();
//		}
//		return name;
//	}
	
	public NavigationItem getSchoolItem(String schoolkey){
		NavigationItem item = new NavigationItem();
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
				SCHOOL_DISTRICT_TABLE, 
				null, 
				SCHOOL_KEY + " = '" + schoolkey + "'", 
				null, 
				null, 
				null, 
				null);
		Cursor result = db.rawQuery(queryBuilder, null);
		if(null != result){
			if(result.moveToFirst()){
				item = retrieveNavigationItem(result);
			}
			result.close();
		}
		return item;
	}
	
	private Cursor searchItemsByKey(String key, String limit, CurrentLan lan){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		String queryString = "";
		
		if(lan == CurrentLan.SIMPLIFIED_CHINESE){
			queryString = SQLiteQueryBuilder.buildQueryString(false, 
					SCHOOL_DISTRICT_TABLE, 
					null, 
					TEXT_S + " LIKE '%" + key + "%' AND " + STATUS + "=" + "1" + " AND " + TYPE + "= 'school'", 
					null, 
					null, 
					POINT_ID + " DESC", 
					limit);
		}else if(lan == CurrentLan.COMPLES_CHINESE){
			queryString = SQLiteQueryBuilder.buildQueryString(false, 
					SCHOOL_DISTRICT_TABLE, 
					null, 
					TEXT_T + " LIKE '%" + key + "%' AND " + STATUS + "=" + "1" + " AND " + TYPE + "= 'school'", 
					null, 
					null, 
					POINT_ID + " DESC", 
					limit);
		}else{
			queryString = SQLiteQueryBuilder.buildQueryString(false, 
					SCHOOL_DISTRICT_TABLE, 
					null, 
					TEXT_EN + " LIKE '%" + key + "%' AND " + STATUS + "=" + "1" + " AND " + TYPE + "= 'school'", 
					null, 
					null, 
					POINT_ID + " DESC", 
					limit);
		}
			
		return db.rawQuery(queryString, null);
	}
	
	public List<NavigationItem> getBookmarkItems(String limit, CurrentLan lan){
		List<NavigationItem> list = new ArrayList<NavigationItem>();
		Cursor cursor = getBookmarkCursor(limit,lan);
		if(cursor != null){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				NavigationItem item = retrieveNavigationItem(cursor);
				list.add(item);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return list;
	}
	
	public List<NavigationListItem> getNavigationLists(){
		SQLiteDatabase db = mNavigationDBHelper.getReadableDatabase();
		List<NavigationListItem> list = new ArrayList<NavigationListItem>();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, 
				NAVIGATION_TYPE_TABLE, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
		Cursor result = db.rawQuery(queryBuilder, null);
		if(result != null){
			result.moveToFirst();
			if(result.getCount()>0){
				while(!result.isAfterLast()){
					NavigationListItem item = new NavigationListItem();
					int type_index = result.getColumnIndex(TYPE);
//					int point_id_index = result.getColumnIndex(POINT_ID);
					int imageurl_index = result.getColumnIndex(IMAGEURL);
					int name_en_index = result.getColumnIndex(TEXT_EN);
					int name_zhs_index = result.getColumnIndex(TEXT_S);
					int name_zht_index = result.getColumnIndex(TEXT_T);
					int content_index = result.getColumnIndex(CONTENT);
					item.content = result.getString(content_index);
					item.type = result.getString(type_index);
//					item.point_id = result.getInt(point_id_index);
					item.imageurl = result.getString(imageurl_index);//add by zhr
					item.name_en = result.getString(name_en_index);
					item.name_zhs = result.getString(name_zhs_index);
					item.name_zht = result.getString(name_zht_index);
					list.add(item);
					result.moveToNext();
				}
			}
			result.close();
		}
		return list;
	}
	
	public List<NavigationItem> getNavigationItems(int parent_id, String limit, CurrentLan lan){
		List<NavigationItem> list = new ArrayList<NavigationItem>();
		Cursor cursor = getNavigationCursor(parent_id, limit,lan);
//		int count = cursor.getCount();
		if(cursor != null){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				NavigationItem item = retrieveNavigationItem(cursor);
				list.add(item);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return list;
	}
	
	public List<NavigationItem> getSearchItems(String search_key, String limit, CurrentLan lan){
		List<NavigationItem> list = new ArrayList<NavigationItem>();
		Cursor cursor = searchItemsByKey(search_key, limit,lan);
		if(cursor != null){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				NavigationItem item = retrieveNavigationItem(cursor);
				list.add(item);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return list;
	}
	
	public NavigationItem retrieveNavigationItem(Cursor cursor){
		int parent_id_index = cursor.getColumnIndex(PARENT_ID);
		int point_id_index = cursor.getColumnIndex(POINT_ID);
//		int name_index = cursor.getColumnIndex(NAME);
		int school_key_index = cursor.getColumnIndex(SCHOOL_KEY);
		int status_index = cursor.getColumnIndex(STATUS);
		int type_index = cursor.getColumnIndex(TYPE);
		int titlr_url_index = cursor.getColumnIndex(TITLEURL);
		int bookmark_index = cursor.getColumnIndex(BOOKMARK);
		int text_en_index = cursor.getColumnIndex(TEXT_EN);	//add by zjj 14-04-23
		int text_s_index = cursor.getColumnIndex(TEXT_S);
		int text_t_index = cursor.getColumnIndex(TEXT_T);
		
		NavigationItem item = new NavigationItem();
		item.status = cursor.getInt(status_index);
		item.parent_id = cursor.getInt(parent_id_index);
		item.point_id = cursor.getInt(point_id_index);
		item.key = cursor.getString(school_key_index);
//		item.name = cursor.getString(name_index);
		item.type = cursor.getString(type_index);
		item.titleurl = cursor.getString(titlr_url_index);
		item.text_en = cursor.getString(text_en_index);
		item.text_s = cursor.getString(text_s_index);
		item.text_t = cursor.getString(text_t_index);
		if(cursor.getInt(bookmark_index)==1){
			item.bookmark = true ;
		}else{
			item.bookmark = false ;
		}
		
		return item;
	}
	
		//添加账号关联
		public boolean addAccount(BindaccountItem account){
			SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(USER_ID, account.pub_account);
			values.put(USER_NAME, account.pub_name);
			values.put(USER_PASSWORD, account.pub_password);
			values.put(USER_SCHOOL_ID, account.pub_schoolId);
			values.put(SCHOOL_KEY, account.schoolkey);      			//add 04/06/09
			values.put(TOKENID, account.tokenid);							//add 04/06/09
			values.put(USER_IMAGEURL, account.pub_imgaeurl);			//add by hzy
			values.put(USER_IMAGES_NUMBER, account.pub_image_number);	//add by hzy
			values.put(SCHOOLNAME_TEXT_S, account.pub_schoolName_hans);
			values.put(SCHOOLNAME_TEXT_T, account.pub_schoolName_hant);
			values.put(SCHOOLNAME_TEXT_EN, account.pub_schoolName_en);
			int i = (int) db.insert(BINDACCOUNT_ITEM_TABLE, null, values);
			if(i != -1){
				Log.i("hzy", "入库成功");
				return true;
			}else{
				return false;
			}

		}
		
		//获取账号关联数据
		public List<BindaccountItem> getAccountItem(){
			List<BindaccountItem> list = new ArrayList<BindaccountItem>();
			Cursor cursor = getBindaccountItemCursor();
			
			if(cursor != null){
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					BindaccountItem account = new BindaccountItem();
					account.pub_account = cursor.getString(cursor.getColumnIndex(USER_ID));
					account.pub_name = cursor.getString(cursor.getColumnIndex(USER_NAME));
					account.pub_password = cursor.getString(cursor.getColumnIndex(USER_PASSWORD));
					account.pub_schoolId = cursor.getString(cursor.getColumnIndex(USER_SCHOOL_ID));
					account.schoolkey = cursor.getString(cursor.getColumnIndex(SCHOOL_KEY));      //add 04/06/09
					account.tokenid = cursor.getString(cursor.getColumnIndex(TOKENID));				//add 04/06/09
					account.pub_imgaeurl = cursor.getString(cursor.getColumnIndex(USER_IMAGEURL));		//add by hzy
					account.pub_image_number = cursor.getString(cursor.getColumnIndex(USER_IMAGES_NUMBER)); //add by hzy
					account.pub_schoolName_hans = cursor.getString(cursor.getColumnIndex(SCHOOLNAME_TEXT_S));
					account.pub_schoolName_hant = cursor.getString(cursor.getColumnIndex(SCHOOLNAME_TEXT_T));
					account.pub_schoolName_en = cursor.getString(cursor.getColumnIndex(SCHOOLNAME_TEXT_EN));
					list.add(account);
					cursor.moveToNext();
				}
			}
			cursor.close();
			
			return list;
		}
		
		//删除账号关联整个表数据
		public void deleteBindaccountTable(){
			SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
			int i  = db.delete(BINDACCOUNT_ITEM_TABLE,  null, null);
			if (i>0){
				Log.i("deleteBindaccountTable", "删除表数据成功"+i);
			}else {
				Log.i("deleteBindaccountTable", "删除表失败或者没记录可删");
			}
		}
		
		//删除账号关联某条数据
		public Boolean deleteBindaccount(String accountId){
			SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
			int i = db.delete(BINDACCOUNT_ITEM_TABLE, USER_ID+"=?", new  String[]{accountId});
			if(i>0){
				return true;
			}else{
				return false;
			}		
		}
		
		public Boolean updateAccountDetail(BindaccountItem account){
			SQLiteDatabase db = mNavigationDBHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(USER_IMAGEURL, account.pub_imgaeurl);			//add by hzy
			values.put(USER_IMAGES_NUMBER, account.pub_image_number);	//add by hzy
			int i = db.update(BINDACCOUNT_ITEM_TABLE, values, USER_ID+"=? and "+USER_SCHOOL_ID+"=?",new String[] {account.pub_account,account.pub_schoolId});
			
			if(i>0){
				return true;
			}else{
				return false;
			}
			
		}
}
