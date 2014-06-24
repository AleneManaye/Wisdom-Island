package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.drcom.drpalm.objs.OrgLimitItem;
import com.drcom.drpalm.objs.OrganizationItem;

public class OrganizationDB {
	
	//table
	private static final String ORGANIZATION_TABLE = "Organization";
	private static final String ORGANIZATION_LIMIT_TABLE = "relation_limit";

	//节点信息表
	private static final String ID = "_id";
	private static final String ORGID = "orgId";
	private static final String ORGNAME = "orgName";
	private static final String ORGTYPE = "orgType";
	private static final String ORGSTATUS = "orgStatus";
	private static final String ORGRELATIONPATH = "orgRelationPath";
	private static final String ORGCHILDCOUNT = "orgchildcount";
	private static final String ORGPID = "orgPid";
	
	//权限关系表
	private static final String USER = "user";

	DatabaseHelper orgDBHelper;
	private static OrganizationDB orgDBInstance = null;

	private static final String ORG_ID_WHERE_AND_PID_WHERE = ORGID + " =? and " + ORGPID + " =? ";

	private static final String PARENT_ID_WHERE_AND_USER_WHERE = ORGPID + " =? and " + USER + " =?";
	
	//多学校处理
	private String schoolkey = "";

	public static OrganizationDB getInstance(Context context, String key) {
		if (orgDBInstance == null||!orgDBInstance.schoolkey.equals(key)) {
			orgDBInstance = new OrganizationDB(context,key);
			orgDBInstance.schoolkey = key;
		}
		return orgDBInstance;
	}

	private OrganizationDB(Context context,String key) {
		orgDBHelper = DatabaseHelper.getInstance(context,key);
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		createTable(db);
		db.close();
	}
	public void clearAllLimit(String username){
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		db.delete(ORGANIZATION_LIMIT_TABLE, USER + " =? ", new String[]{username});
	}
	
	public void saveOrganizationLimit(OrgLimitItem item){
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ORGID, item.orgID);
		values.put(USER, item.user);
		try{
			long count = db.insert(ORGANIZATION_LIMIT_TABLE, USER, values);
			long test = count;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private boolean isUserExist(OrgLimitItem item){
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		Cursor result = db.query(ORGANIZATION_LIMIT_TABLE, 
								new String[]{ORGID}, 
								USER + " =? ", 
								new String[]{item.user}, 
								null, 
								null, 
								null);
		boolean isExist = (result.getCount() > 0);
		result.close();
		return isExist;
	}

	// 创建表
	private void createTable(SQLiteDatabase db) {
		String CTEATE_ORGANIZATION_TABLE = "CREATE TABLE IF NOT EXISTS " + ORGANIZATION_TABLE + " (" + ID + " INTEGER," + ORGID + " INTEGER," + ORGPID + " INTEGER," + ORGNAME + " TEXT," + ORGTYPE
				+ " TEXT," + ORGSTATUS + " TEXT," + ORGRELATIONPATH + " TEXT," + ORGCHILDCOUNT + " INTEGER" + ");";

		db.execSQL(CTEATE_ORGANIZATION_TABLE);
		
		String CTEATE_LIMIT_TABLE  = "CREATE TABLE IF NOT EXISTS " + ORGANIZATION_LIMIT_TABLE + " ("
									+ ORGID + " INTEGER,"	
									+ USER + " TEXT"
									+ ");" ;
		db.execSQL(CTEATE_LIMIT_TABLE);
		
		// 数据迁移
		orgDBHelper.updateTable(db, ORGANIZATION_TABLE);
		orgDBHelper.updateTable(db, ORGANIZATION_LIMIT_TABLE);
	}

	public List<OrganizationItem> getChildPoint(int pid) {
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		List<OrganizationItem> list = new ArrayList<OrganizationItem>();

		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, ORGANIZATION_TABLE, null, ORGPID + " = " + Integer.toString(pid), null, null, null,
				null);
		Cursor result = db.rawQuery(queryBuilder, null);
		// Cursor result = db.query(ORGANIZATION_TABLE, null, ORGPID + "=? AND "
		// + USER + " =?", new String[] { Integer.toString(pid), username },
		// null, null, null);
		int count = result.getCount();
		if (result != null) {
			result.moveToFirst();
			while (!result.isAfterLast()) {
				OrganizationItem item = retreiveOrganizationItem(result);
				result.moveToNext();
				list.add(item);
			}
			result.close();
		}
		return list;
	}
	
	public OrganizationItem getORGItem(int id) {
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();

		String queryBuilder = SQLiteQueryBuilder.buildQueryString(false, ORGANIZATION_TABLE, null, ORGID + " = " + Integer.toString(id), null, null, null,
				null);
		Cursor result = db.rawQuery(queryBuilder, null);
		// Cursor result = db.query(ORGANIZATION_TABLE, null, ORGPID + "=? AND "
		// + USER + " =?", new String[] { Integer.toString(pid), username },
		// null, null, null);
		int count = result.getCount();
		OrganizationItem item = null;
		if (result != null) {
			result.moveToFirst();
			while (!result.isAfterLast()) {
				item = retreiveOrganizationItem(result);
				result.moveToNext();
			}
			result.close();
		}
		return item;
	}
	
	public List<OrganizationItem> getTopPoint(String username){
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		List<OrganizationItem> list = new ArrayList<OrganizationItem>();
		String queryBuilder = SQLiteQueryBuilder.buildQueryString(true, 
													ORGANIZATION_TABLE + "," + ORGANIZATION_LIMIT_TABLE, 
													new String[]{ORGANIZATION_TABLE + "." + ORGID,ORGNAME,ORGTYPE}, 
													USER + " = '" + username + "' AND " + ORGANIZATION_TABLE + "." + 
													ORGID + " = " + ORGANIZATION_LIMIT_TABLE + "." + ORGID, 
													null, 
													null, 
													null, 
													null);
		Cursor result = db.rawQuery(queryBuilder,null);
		if(result != null){
			result.moveToFirst();
			while(!result.isAfterLast()){
				OrganizationItem item = new OrganizationItem();
				int org_id_index = result.getColumnIndex(ORGID);
				int org_name_index = result.getColumnIndex(ORGNAME);
				int org_type_index = result.getColumnIndex(ORGTYPE);
				item.orgID = result.getInt(org_id_index);
				item.orgName = result.getString(org_name_index);
				item.orgType = result.getString(org_type_index);
				list.add(item);
				result.moveToNext();
			}
			result.close();
		}
		return list;
	}

	private OrganizationItem retreiveOrganizationItem(Cursor cursor) {
		OrganizationItem item = new OrganizationItem();
		int org_id_index = cursor.getColumnIndex(ORGID);
		int org_name_index = cursor.getColumnIndex(ORGNAME);
		int org_type_index = cursor.getColumnIndex(ORGTYPE);
		int org_path_index = cursor.getColumnIndex(ORGRELATIONPATH);
		int org_count_index = cursor.getColumnIndex(ORGCHILDCOUNT);
		int org_pid_index = cursor.getColumnIndex(ORGPID);

		item.orgID = cursor.getInt(org_id_index);
		item.orgName = cursor.getString(org_name_index);
		item.orgType = cursor.getString(org_type_index);
		item.orgPath = cursor.getString(org_path_index);
		item.orgCount = cursor.getInt(org_count_index);
		item.orgPid = cursor.getInt(org_pid_index);

		return item;
	}

	private String[] whereArgs(OrganizationItem orgItem) {

		return new String[] { Integer.toString(orgItem.orgID), Integer.toString(orgItem.orgPid)};
	}

//	public void clearAllStories(String user) {
//		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
//		db.delete(ORGANIZATION_TABLE, "user = ?", new String[] { user });
//	}

	public void startTransaction() {
		orgDBHelper.getWritableDatabase().beginTransaction();
	}

	/**
	 * 结束事务
	 */
	public void endTransaction() {
		orgDBHelper.getWritableDatabase().setTransactionSuccessful();
		orgDBHelper.getWritableDatabase().endTransaction();
	}

	/**
	 * 保存事件到数据库
	 * 
	 * @param story
	 * @param saveCategories
	 */
	public void saveOrgItem(OrganizationItem orgItem) {
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		ContentValues orgValues = new ContentValues();
		orgValues.put(ORGID, orgItem.orgID);
		orgValues.put(ORGNAME, orgItem.orgName);
		orgValues.put(ORGTYPE, orgItem.orgType);
		orgValues.put(ORGSTATUS, orgItem.orgStatus);
		orgValues.put(ORGPID, orgItem.orgPid);
		orgValues.put(ORGRELATIONPATH, orgItem.orgPath);
		orgValues.put(ORGCHILDCOUNT, orgItem.orgCount);

		// add current item
		if (isOrgExist(orgItem.orgID, orgItem.orgPid)) {
			int i = db.update(ORGANIZATION_TABLE, orgValues, ORG_ID_WHERE_AND_PID_WHERE, whereArgs(orgItem));
		} else {
			try {
				long count = db.insert(ORGANIZATION_TABLE, null, orgValues);
				System.out.println("插入行返回值>>>>" + count);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * 清除组织架构表
	 */
	public void clearOrgItem(){
		SQLiteDatabase db = orgDBHelper.getWritableDatabase();
		db.delete(ORGANIZATION_TABLE, null, null);
	}
	
	public boolean isOrgExist(int id, int pid) {
		SQLiteDatabase db = orgDBHelper.getReadableDatabase();

		Cursor result = db.query(ORGANIZATION_TABLE, new String[] { ORGID }, ORG_ID_WHERE_AND_PID_WHERE, new String[] { Integer.toString(id), Integer.toString(pid)}, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}
}
