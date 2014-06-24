package com.drcom.drpalm.DB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.objs.AwsContentItem;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDetailsItem.Imags;
import com.drcom.drpalm.objs.EventDetailsItem.Replyer;
import com.drcom.drpalm.objs.EventDetailsItem.ReplyerMember;
import com.drcom.drpalm.objs.EventDetailsItem.ReviewTemp;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;
import com.drcom.drpalm.objs.EventsTypeLanguage;
import com.drcom.drpalm.objs.FavItem;
import com.drcom.drpalm.objs.NoticeTypeItem;

/*
 * Date         : 2012-4-16
 * Author       : zeng han hua
 * Copyright    : City Hotspot Co., Ltd.
 */
public class EventsDB {

	private static final String EVENT_TABLE = "myevents";
	private static final String EVENT_IMG_TABLE = "myevents_img";
	private static final String EVENT_ASWORG_TABLE = "myevents_asworg"; // 回复人列表
	// private static final String EVENT_OFFLINEREAD_TABLE = "myevents_offread";
	private static final String NOTICE_TYPE_TABLE = "notice_type";// 公告分类
	private static final String EVENT_REVIEWTEMP_TABLE = "myevents_reviewtemp";	//回评模板
	private static final String EVENT_MEMBER_TABLE = "myevent_member_table";	//成员
	
	// 等待自动回复表
	private static final String EVENT_AUTOREPLY_TABLE = "autoreply";

	// 已发送表
	private static final String EVENT_SEND_TABLE = "myevents_send";
	private static final String EVENT_SEND_IMG_TABLE = "myevents_send_img";
	private static final String EVENT_SEND_ASWORG_TABLE = "myevents_send_asworg";

	// 草稿表
	private static final String EVENT_DRAFT_TABLE = "myevents_draft";
	private static final String EVENT_DRAFT_IMG_TABLE = "myevents_draft_img";
	private static final String EVENTID_TO_RECIVEID_TABLE = "myeventid_to_reciveid";

	// 图片或附件缓存表
	private static final String ATTACHMENT_CACHE_TABLE = "attachment_cache";

	// 反馈内容表
	private static final String AWS_CONTENT_TABLE = "aws_content_table";

	private static final String USER = "user";

	private static final String OWNER_ID = "ownerid";
	private static final String OWNER = "owner";
	private static final String BODY_EVENT_ID = "_id";
	private static final String TYPE = "type"; // 类型
	private static final String ORIEVENT_ID = "orieventid";
	private static final String ORI_STATUS = "oristatus";
	private static final String PUB_ID = "pubid";
	private static final String PUB_NAME = "pubname";
	private static final String POST = "post";
	private static final String START = "start";
	private static final String END = "end";
	private static final String LOCATION = "location";
	private static final String LOCATION_URL = "locationurl";
	private static final String STATUS = "status";
	private static final String EVENT_TITLE = "title";
	private static final String EVENT_SUMMARY = "summary";
	private static final String EVENT_BODY = "body";
	private static final String EVENT_CLEAN_BODY = "cleanbody";
	private static final String IFESHOW = "ifeshow"; // 是否加急
	private static final String CANCELLED = "cancelled";
	private static final String BOOKMARKED = "bookmarked";
	private static final String ISREAD = "isread";
	private static final String THUMBURL = "thumburl";// 封面图
	private static final String LASTAWSTIME = "lastawstime";// 最后反馈时间
	private static final String LASTUPDATE = "lastupdate";// 最后更新时间
	private static final String LASTUPDATE_FAV = "lastupdatefav";// 收藏状态最后更新时间
	private static final String HASATT = "hasatt";// 是否有附件
	private static final String RECVTOTAL = "recvtotal";// 接收总人数
	private static final String READCOUNT = "readcount";// 已读人数
	private static final String LASTAWSUSERID = "lastawsuserid";	//最后反馈人
	private static final String AWSCOUNT = "awscount";	//反馈内容总数
	private static final String AWSCOUNTCLIENT = "awscountclient";	//反馈内容总数 (本地总数,以跟列表数据作比较是否有新评论)
	private static final String ISNEEDREVIEW = "isneedreview";	//是否需要回评
	
	//添加已读人名和未读人名字段
	private static final String READ_NAME_LIST = "readlist";//已读人物名列表
	private static final String UNREAD_NAME_LIST = "unreadlist";//未读人物名列表
	
	//最后阅读时间
	private static final String EVENT_LAST_READTIME = "lastreadtime";//已读人物名列表
	
	// private static final String HASFEEDBACK = "hasnewfeedback";//是否有新回复
	private static final String LASTAWSTIMEREAD = "lastawstimeread";// 阅读回复时,保存该回复的最后回复时间,跟LASTAWSTIME比较,是否有新回复

	private static final String EVENT_ID_WHERE_AND_USER_WHERE = BODY_EVENT_ID + " =? and " + USER + " =?";

	// 草稿
	private static final String SHORT_LOC = "short_loc";

	// myevents_img
	private static final String EVENT_ID = "event_id";
	private static final String URL = "url";
	private static final String DESCRIPTION = "description";
	private static final String ATTID = "attid";
	private static final String ATT_PREVIEW = "attpreview";
	private static final String ATT_SIZE = "attpsize";
	private static final String ATT_NAME = "attname";

	// myevents_asworg
	private static final String ASW_EVENT_ID = "event_id";
	private static final String ASW_REPLYERID = "replyerid";
	private static final String ASW_REPLYERNAME = "replyername";
	private static final String ASW_REPLYCOUNT = "replycount";
	private static final String ASW_REPLYLASTTIME = "replylasttime";

	// aws table field
	private static final String AWS_ID = "aws_id";
	private static final String AWS_TIME = "aws_time";
	private static final String AWS_GROUP_ID = "aws_group_id";//讨论组ID
	private static final String AWS_PUB_ID = "awspubid";
	private static final String AWS_PUB_NAME = "awspubname";
	private static final String AWS_REC_ID = "awsrecid";
	private static final String AWS_REC_NAME = "awsrecname";
	private static final String AWS_BODY = "awsbody";
	
	//回评模板
	private static final String REVIEWTEMP_ID = "reviewtempid";
	private static final String REVIEWTEMP_TITLE = "reviewtemptitle";
	private static final String REVIEWTEMP_TYPE = "reviewtemptype";
	private static final String REVIEWTEMP_MAX = "reviewtempmax";
	private static final String REVIEWTEMP_DEFAULT = "reviewtempdefault";
	private static final String REVIEWTEMP_REQUIRED = "reviewtemprequired";
	
	//成员列表
	private static final String MEMBER_ID = "memberid";
	private static final String MEMBER_NAME = "membername";
	private static final String MEMBER_IMGURL = "memberheadimgurl";
	private static final String MEMBER_LASTUPDATE = "memberheadimglastupdate";
	
	// notice type
	private static final String _ID = "_id";
	private static final String OBJ_TYPE = "objtype";
	private static final String OBJ_TYPE_DES = "objtypedes";
	// draft type
	private static final String PK_ID = "pk_id";
	private static final String CRC_ID = "crc_id";// 保存附件唯一标识
	private static final String SAVETIME = "save_time";// 保存时间
	private static final String IMAGE_TYPE = "image_type";// 图片类型
	private static final String ORG_ID = "org_id";// 机构id
	private static final String ORG_NAME = "org_name";// 机构名称
	private static final String ORG_TYPE = "org_type";// 机构类型
	private static final String IMAGE_BYTE_ARRAY = "image_byte";
	private static final String TYPE_TITLEID = "type_titleId";  //add by hzy

	// 新增字段,主要用于转发
	private static final String ATT_ITEM = "item";
	private static final String ATT_TYPE = "type";// file 时为普通发送 id则为转发通告id

	// 图片缓存字段
	private static final String ATTACHMENT_DATE = "attachment_date";
	private static final String MIMETYPE = "mimetype";
	private static final String HTTPSTATUS = "httpstatus";
	private static final String CONTENTLENGTH = "contentlength";
	private static final String LASTMODIFY = "lastmodify";
	// private static final String DESCRIPTION = "description";

	// SQLiteOpenHelper eventsDBHelper;
	DatabaseHelper eventsDBHelper;
	private String schoolkey = "";
	private static EventsDB eventsDBInstance = null;
	
	
	//table for EventsTypeLanguage.     --------------add by hzy 
	private static final String EVENTS_TYPE_LANGUAGE = "events_type_language";
	private static final String TYPE_ID = "_id";
	private static final String TYPE_INDEX = "type_index";
	private static final String TYPE_EVENT = "type_event";
	private static final String TYPE_CHINESE ="type_chinese";
	private static final String TYPE_CHINESE_CHARACTER = "type_chinese_character";
	private static final String TYPE_ENGLISH = "type_english";
	
	
	
	public static EventsDB getInstance(Context context, String key) {
		if (eventsDBInstance == null || !eventsDBInstance.schoolkey.equals(key)) {
			eventsDBInstance = new EventsDB(context, key);
			eventsDBInstance.schoolkey = key;
			return eventsDBInstance;
		} else {
			return eventsDBInstance;
		}
	}

	private EventsDB(Context context, String key) {
		eventsDBHelper = DatabaseHelper.getInstance(context, key);
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		createTable(db);
		// db.close();
	}

	// 创建表
	//通告列表
	private void createTable(SQLiteDatabase db) {
		String CTEATE_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS " + EVENT_TABLE + " (" 
			+ BODY_EVENT_ID + " INTEGER," 
			+ OWNER_ID + " INTEGER," 
			+ OWNER + " TEXT," 
			+ TYPE + " INTEGER," 
			+ ORIEVENT_ID + " INTEGER," 
			+ ORI_STATUS + " TEXT," 
			+ PUB_ID + " TEXT DEFAULT ''," 
			+ PUB_NAME + " TEXT," 
			+ POST + " TEXT,"
			+ START + " TEXT,"
			+ END + " TEXT," 
			+ STATUS + " TEXT," 
			+ EVENT_TITLE + " TEXT,"
			+ EVENT_SUMMARY + " TEXT," 
			+ LOCATION + " TEXT," 
			+ LOCATION_URL + " TEXT," 
			+ CANCELLED + " TEXT,"
			+ THUMBURL + " TEXT," 
			+ LASTAWSTIME + " TEXT," 
			+ LASTAWSTIMEREAD + " TEXT," 
			+ EVENT_LAST_READTIME + " TEXT,"
			+ LASTUPDATE + " TEXT DEFAULT ''," 
			+ LASTUPDATE_FAV + " TEXT," 
			+ EVENT_BODY + " TEXT," 
			+ EVENT_CLEAN_BODY + " TEXT," 
			+ USER + " TEXT," 
			+ LASTAWSUSERID + " TEXT," 
			+ HASATT + " BOOLEAN DEFAULT 0 NOT NULL,"
				// + HASFEEDBACK + " BOOLEAN DEFAULT 0 NOT NULL,"
			+ BOOKMARKED + " BOOLEAN DEFAULT 0 NOT NULL," 
			+ ISREAD + " BOOLEAN DEFAULT 0 NOT NULL," 
			+ ISNEEDREVIEW + " BOOLEAN DEFAULT 0 NOT NULL," 
			+ IFESHOW + " BOOLEAN DEFAULT 0 NOT NULL" + ");";
		db.execSQL(CTEATE_EVENT_TABLE);

		db.execSQL("CREATE INDEX IF NOT EXISTS EVENTS_INDEX ON " + EVENT_TABLE + "(" + BODY_EVENT_ID + " , " + USER + ")");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_IMG_TABLE 
				+ " (" + EVENT_ID + " INTEGER," 
				+ URL + " TEXT," 
				+ DESCRIPTION + " TEXT," 
				+ ATTID + " INTEGER," 
				+ ATT_PREVIEW + " TEXT," 
				+ ATT_SIZE + " TEXT," 
				+ ATT_NAME + " TEXT," 
				+ USER + " TEXT" 
				+ ");");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_ASWORG_TABLE 
				+ " (" + ASW_EVENT_ID + " INTEGER," 
				+ ASW_REPLYERID + " TEXT," 
				+ ASW_REPLYERNAME + " TEXT," 
				+ ASW_REPLYCOUNT + " TEXT,"
				+ ASW_REPLYLASTTIME + " TEXT," 
				+ LASTAWSTIMEREAD + " TEXT," 
				+ USER + " TEXT" + ");");

		//回评模板
		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_REVIEWTEMP_TABLE 
				+ " (" + EVENT_ID + " INTEGER," 
				+ REVIEWTEMP_ID + " TEXT,"
				+ REVIEWTEMP_TITLE + " TEXT,"
				+ REVIEWTEMP_TYPE + " TEXT,"
				+ REVIEWTEMP_MAX + " TEXT,"
				+ REVIEWTEMP_DEFAULT + " TEXT,"
				+ REVIEWTEMP_REQUIRED + " BOOLEAN DEFAULT 0 NOT NULL,"
				+ USER + " TEXT" 
				+ ");");
		
		//讨论组成员
		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_MEMBER_TABLE
				+ " (" + ASW_REPLYERID + " TEXT,"
				+ MEMBER_ID + " TEXT,"
				+ MEMBER_NAME + " TEXT,"
				+ MEMBER_IMGURL + " TEXT DEFAULT '',"
				+ MEMBER_LASTUPDATE + " TEXT"
				+ ");");
		
		// 离线阅读记录
		// db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_OFFLINEREAD_TABLE +
		// " ("
		// + EVENT_ID + " INTEGER,"
		// + USER + " TEXT"
		// + ");");

		// //附件缓存
		// db.execSQL("CREATE TABLE IF NOT EXISTS " + ATTACHMENT_CACHE_TABLE +
		// " ("
		// + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		// + URL + " TEXT,"
		// + DESCRIPTION + " TEXT,"
		// + ATTACHMENT_DATE + " BLOB,"
		// + MIMETYPE + " TEXT,"
		// + HTTPSTATUS + " INTEGER,"
		// + CONTENTLENGTH + " INTEGER,"
		// + LASTMODIFY + " TEXT,  UNIQUE (url) ON CONFLICT REPLACE"
		// + ");");
		//
		// db.execSQL("CREATE INDEX IF NOT EXISTS CACHE_URL_INDEX ON " +
		// ATTACHMENT_CACHE_TABLE + "(" + URL + ")");
		//
		//
		// 
		//已发送通告表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_SEND_TABLE + " (" + BODY_EVENT_ID + " INTEGER," 
				+ OWNER_ID + " INTEGER," 
				+ OWNER + " TEXT," 
				+ TYPE + " INTEGER," 
				+ ORIEVENT_ID + " INTEGER,"
				+ ORI_STATUS + " TEXT," 
				+ PUB_ID + " TEXT," 
				+ PUB_NAME + " TEXT," 
				+ POST + " TEXT,"
				+ START + " TEXT," 
				+ END + " TEXT," 
				+ STATUS + " TEXT," 
				+ EVENT_TITLE + " TEXT," 
				+ EVENT_SUMMARY + " TEXT," 
				+ LOCATION + " TEXT," 
				+ LOCATION_URL + " TEXT," 
				+ CANCELLED + " TEXT," 
				+ THUMBURL + " TEXT," 
				+ LASTAWSTIME + " TEXT," 
				+ LASTAWSTIMEREAD + " TEXT," 
				+ LASTUPDATE + " TEXT,"
				+ READ_NAME_LIST + " TEXT," 
				+ UNREAD_NAME_LIST + " TEXT,"
				+ EVENT_BODY + " TEXT," 
				+ RECVTOTAL + " TEXT," 
				+ READCOUNT + " TEXT," 
				+ EVENT_CLEAN_BODY + " TEXT," 
				+ USER + " TEXT," 
				+ AWSCOUNT + " INTEGER," 
				+ AWSCOUNTCLIENT + " INTEGER," 
				+ HASATT + " BOOLEAN DEFAULT 0 NOT NULL," 
				+ BOOKMARKED + " BOOLEAN DEFAULT 0 NOT NULL," 
				+ ISREAD + " BOOLEAN DEFAULT 0 NOT NULL," 
				+ IFESHOW + " BOOLEAN DEFAULT 0 NOT NULL" 
				+ ");");

		db.execSQL("CREATE INDEX IF NOT EXISTS EVENTS_SEND_INDEX ON " + EVENT_SEND_TABLE + "(" + BODY_EVENT_ID + " , " + USER + ")");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_SEND_IMG_TABLE + " (" + EVENT_ID + " INTEGER," 
				+ URL + " TEXT," 
				+ DESCRIPTION + " TEXT," 
				+ ATTID + " INTEGER," 
				+ ATT_PREVIEW + " TEXT," 
				+ ATT_SIZE + " TEXT," 
				+ ATT_NAME + " TEXT," 
				+ IMAGE_TYPE + " TEXT," 
				+ USER + " TEXT" + ");");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_SEND_ASWORG_TABLE + " (" + ASW_EVENT_ID + " INTEGER," + ASW_REPLYERID + " TEXT," + ASW_REPLYERNAME + " TEXT," + ASW_REPLYCOUNT + " TEXT,"
				+ ASW_REPLYLASTTIME + " TEXT," + LASTAWSTIMEREAD + " TEXT," + USER + " TEXT" + ");");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + AWS_CONTENT_TABLE + " (" + BODY_EVENT_ID + " INTEGER," 
				+ AWS_ID + " INTEGER," 
				+ AWS_TIME + " TEXT," 
				+ AWS_GROUP_ID + " TEXT," 
				+ AWS_PUB_ID + " TEXT," 
				+ AWS_PUB_NAME + " TEXT," 
				+ AWS_REC_ID + " TEXT," 
				+ AWS_REC_NAME + " TEXT," 
				+ AWS_BODY + " TEXT," 
				+ USER + " TEXT" 
				+ ");");

		// 草稿表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_DRAFT_TABLE + " (" + PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + BODY_EVENT_ID + " INTEGER," + OWNER_ID + " INTEGER," + OWNER + " TEXT,"
				+ TYPE + " TEXT,"
				// + OBJTYPE + " TEXT,"
				+ ORIEVENT_ID + " TEXT," + ORI_STATUS + " TEXT," + SAVETIME + " TEXT," + START + " INTEGER," + END + " INTEGER," + SHORT_LOC + " TEXT," + LOCATION + " TEXT," + EVENT_TITLE + " TEXT,"
				+ EVENT_BODY + " TEXT," + IFESHOW + " INTEGER," + HASATT + " BOOLEAN DEFAULT 0 NOT NULL," + USER + " TEXT," + TYPE_TITLEID + " TEXT" +");");

		db.execSQL("CREATE INDEX IF NOT EXISTS EVENTS_DRAFT_INDEX ON " + EVENT_DRAFT_TABLE + "(" + PK_ID + " , " + USER + ")");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_DRAFT_IMG_TABLE + " (" + PK_ID + " INTEGER," + CRC_ID + " TEXT," + IMAGE_TYPE + " TEXT," + DESCRIPTION + " TEXT," + ATT_ITEM + " TEXT,"
				+ ATT_TYPE + " TEXT," + IMAGE_BYTE_ARRAY + " BLOB," + USER + " TEXT" + ");");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENTID_TO_RECIVEID_TABLE + " (" + PK_ID + " INTEGER," + ORG_ID + " TEXT," + ORG_NAME + " TEXT," + ORG_TYPE + " TEXT," + USER + " TEXT" + ");");
		// db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENT_AUTOREPLY_TABLE +
		// " ("
		// + BODY_EVENT_ID + " INTEGER,"
		// + USER + " TEXT"
		// + ");");
		
		
		//create table for EVENTS_TYPE_LANGUAGE by hzy
		db.execSQL("CREATE TABLE IF NOT EXISTS " + EVENTS_TYPE_LANGUAGE + " (" + TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+TYPE_INDEX + " TEXT,"
				+TYPE_EVENT + " TEXT,"
				+ TYPE_CHINESE + " TEXT," 
				+ TYPE_CHINESE_CHARACTER + " TEXT," 
				+ TYPE_ENGLISH + " TEXT" 
				+ ");");

		
		
		

		// 数据迁移
		eventsDBHelper.updateTable(db, EVENT_TABLE);
		eventsDBHelper.updateTable(db, EVENT_ASWORG_TABLE);
		eventsDBHelper.updateTable(db, EVENT_IMG_TABLE);

		eventsDBHelper.updateTable(db, EVENT_SEND_TABLE);
		eventsDBHelper.updateTable(db, EVENT_SEND_IMG_TABLE);
		eventsDBHelper.updateTable(db, EVENT_SEND_ASWORG_TABLE);

		eventsDBHelper.updateTable(db, AWS_CONTENT_TABLE);

		// eventsDBHelper.updateTable(db, ATTACHMENT_CACHE_TABLE);
		//
		// eventsDBHelper.updateTable(db, EVENT_SEND_TABLE);
		// eventsDBHelper.updateTable(db, EVENT_SEND_IMG_TABLE);
		// eventsDBHelper.updateTable(db, EVENT_SEND_ASWORG_TABLE);
		//
		eventsDBHelper.updateTable(db, EVENT_DRAFT_TABLE);
		eventsDBHelper.updateTable(db, EVENT_DRAFT_IMG_TABLE);
		//
		eventsDBHelper.updateTable(db, EVENTID_TO_RECIVEID_TABLE);
		//
		eventsDBHelper.updateTable(db, EVENT_REVIEWTEMP_TABLE);
		eventsDBHelper.updateTable(db, EVENT_MEMBER_TABLE);
		
		//add by hzy
		eventsDBHelper.updateTable(db, EVENTS_TYPE_LANGUAGE);
	}

	private String[] whereArgs(EventDetailsItem EventItem) {

		return new String[] { Integer.toString(EventItem.eventid), EventItem.user };
	}

	/**
	 * 清除user用户的Events事件
	 */
	public void clearAllStories(String user) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		db.delete(EVENT_TABLE, " user = ? and " + BOOKMARKED + " = 0 ", new String[] { user });
		// db.execSQL(" delete from " + EVENT_TABLE + " where " +
		// "user = ? and " + BOOKMARKED + " = ?", new String[] { user, " 0 " });
		// db.execSQL(" delete from " + EVENT_IMG_TABLE + " where " +
		// "user = ? and " + EVENT_ID + " not in " + " ( select " +
		// BODY_EVENT_ID + " from " + EVENT_TABLE + " where " + BOOKMARKED +
		// " =?);",
		// new String[] { user, "1" });
		db.delete(EVENT_IMG_TABLE, "user = ? AND " + EVENT_ID + " not in ( select " + BODY_EVENT_ID + " from " + EVENT_TABLE + " where " + BOOKMARKED + " = 1);", new String[] { user });
		db.delete(EVENT_ASWORG_TABLE, "user = ?", new String[] { user });
		db.delete(EVENT_SEND_TABLE, "user = ?", new String[] { user });
		db.delete(EVENT_SEND_IMG_TABLE, "user = ?", new String[] { user });
		db.delete(EVENT_SEND_ASWORG_TABLE, "user = ?", new String[] { user });
		db.delete(AWS_CONTENT_TABLE, "user = ?", new String[] { user });
		db.delete(EVENT_DRAFT_TABLE, "user = ?", new String[] { user });
		db.delete(EVENT_DRAFT_IMG_TABLE, "user = ?", new String[] { user });
		db.delete(EVENTID_TO_RECIVEID_TABLE, "user = ?", new String[] { user });
		db.delete(EVENT_REVIEWTEMP_TABLE, USER + " = ?", new String[] { user });
	}

	/**
	 * @param 用户id
	 *            清除所有书签标记
	 */
	void clearAllBookmarks(String user) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(BOOKMARKED, false);
		db.update(EVENT_TABLE, eventsValues, "user =?", new String[] { user });
	}

	/**
	 * 开始事务
	 */
	public void startTransaction() {
		// if(eventsDBHelper.getWritableDatabase().isOpen() &&
		// !eventsDBHelper.getWritableDatabase().isDbLockedByOtherThreads()){
		eventsDBHelper.getWritableDatabase().beginTransaction();
		// }

	}

	/**
	 * 结束事务
	 */
	public void endTransaction() {
		// Log.i("zjj", "````````````````endTransaction()``````````````````");
		try {
			eventsDBHelper.getWritableDatabase().setTransactionSuccessful();
		} finally {
			if (eventsDBHelper.getWritableDatabase().inTransaction()) {
				// Log.i("zjj",
				// "eventsDBHelper.getWritableDatabase().endTransaction()");
				eventsDBHelper.getWritableDatabase().endTransaction();
			}

		}
	}

	/**
	 * 更新书签
	 * 
	 * @param story
	 * @param bookmarkedStatus
	 */
	void updateBookmarkStatus(EventDetailsItem eventItem, boolean bookmarkedStatus) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(BOOKMARKED, bookmarkedStatus);
		db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
	}

	/**
	 * 把服务器中收藏通告状态同步到本地
	 * @param eventid
	 * @param categoryid
	 * @param status
	 * @param lastupdatetimefav
	 */
	public void updateFavStatus(FavItem item){
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		if(item.mStatus.equals(FavItem.STATUS_CANCEL))
			eventsValues.put(BOOKMARKED, false);
		else
			eventsValues.put(BOOKMARKED, true);
		
		eventsValues.put(LASTUPDATE_FAV, item.mLastupdatetime);
		
		//更新存在表中的通告收藏状态
		int sum = db.update(EVENT_TABLE, 
				eventsValues,  
				BODY_EVENT_ID + " =? and " + USER + " =? and " + TYPE + " =? ", 
				new String[] { item.mEventid, item.mUsername,item.mCategroyid }
		);
		
		//表中不存在则新增
		if(sum == 0){
			eventsValues.put(USER, item.mUsername);
			eventsValues.put(BODY_EVENT_ID, item.mEventid);
			db.insert(EVENT_TABLE, null, eventsValues);
//			Log.i("zjj", "表中不存在则新增:" + item.mEventid);
//			EventDetailsItem edi = new EventDetailsItem();
//			edi.user = item.mUsername;
//			edi.eventid = Integer.valueOf(item.mEventid);
//			edi.status = item.mStatus;
//			saveEventsItem(edi);
		}
		
//		Log.i("zjj", "把服务器中收藏通告状态同步到本地条数:" + sum);
	}
	
	/**
	 * 收藏的通告是否都存在表中
	 * @param name
	 * @return
	 */
	public boolean isNotallEvents(String name){
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		String joinQuery;
		joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
				new String[]{LASTUPDATE}, 
				"user = '" + name + "' and bookmarked = 1 and " + LASTUPDATE + " = ''", 
				null, null, 
				null, 
				null);
		Cursor c = db.rawQuery(joinQuery, null);
		c.requery();
		int sum = c.getCount();
		c.close();
		c = null;
		Log.i("zjj", "判断收藏的通告是否都存在表中条数:" + sum);
		return sum > 0?false:true;
	}
	
	/**
	 * 保存事件到数据库
	 * 
	 * @param story
	 * @param saveCategories
	 */
	public synchronized boolean saveEventsItem(EventDetailsItem eventItem) {
		boolean bFlag = false;
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();

		eventsValues.put(BODY_EVENT_ID, eventItem.eventid);
		eventsValues.put(TYPE, eventItem.type);
		if (eventItem.orieventid != null) {
			ContentValues updateStatusValues = new ContentValues();
			updateStatusValues.put(STATUS, eventItem.oristatus);
			int x = db.update(EVENT_TABLE, updateStatusValues, EVENT_ID_WHERE_AND_USER_WHERE, new String[] { Integer.toString(eventItem.orieventid), eventItem.user });
			// Log.i("zjj", "-----" + x + ",title:" + eventItem.title + ",id:" +
			// eventItem.eventid + ",orieventid:" + eventItem.orieventid + "," +
			// eventItem.oristatus);
		}
		eventsValues.put(ORIEVENT_ID, eventItem.orieventid);
		eventsValues.put(ORI_STATUS, eventItem.oristatus);
		eventsValues.put(PUB_ID, eventItem.pubid.toLowerCase());	//转为小写保存(因为登录时不区分大小写,为了对比是否自己发的信息,入库时将发件人转化为小写)
		eventsValues.put(PUB_NAME, eventItem.pubname);
		if (eventItem.post == null) {
			eventsValues.put(POST, 0);
		} else {
			eventsValues.put(POST, eventItem.post.getTime());
		}
		if (eventItem.start == null) {
			eventsValues.put(START, 0);
		} else {
			eventsValues.put(START, eventItem.start.getTime());
		}
		if (eventItem.end == null) {
			eventsValues.put(END, 0);
		} else {
			eventsValues.put(END, eventItem.end.getTime());
		}
		eventsValues.put(STATUS, eventItem.status);
		eventsValues.put(IFESHOW, eventItem.ifeshow);
		eventsValues.put(EVENT_TITLE, eventItem.title);
		eventsValues.put(EVENT_SUMMARY, eventItem.summary);
		eventsValues.put(LOCATION, eventItem.location);
		eventsValues.put(LOCATION_URL, eventItem.locationUrl);
		if (eventItem.cancelled == null) {
			eventsValues.put(CANCELLED, 0);
		} else {
			eventsValues.put(CANCELLED, eventItem.cancelled.getTime());
		}
		eventsValues.put(THUMBURL, eventItem.thumbUrl);
		eventsValues.put(USER, eventItem.user);
		if (eventItem.lastawstime == null) {
			eventsValues.put(LASTAWSTIME, 0);
		} else {
			eventsValues.put(LASTAWSTIME, eventItem.lastawstime.getTime());
		}
		if (eventItem.lastreadtime == null) {
			eventsValues.put(EVENT_LAST_READTIME, 0);
		} else {
			eventsValues.put(EVENT_LAST_READTIME, eventItem.lastreadtime.getTime());
		}
		if (eventItem.lastupdate == null) {
			eventsValues.put(LASTUPDATE, 0);
		} else {
			eventsValues.put(LASTUPDATE, eventItem.lastupdate.getTime());
		}
		eventsValues.put(HASATT, eventItem.hasatt);
		eventsValues.put(ISREAD, eventItem.isread);
		eventsValues.put(LASTAWSUSERID, eventItem.lastawsuserid);	//11-07
		eventsValues.put(ISNEEDREVIEW, eventItem.isneedreview);	//2014-01-17

		if (eventExists(eventItem.eventid, eventItem.user)) {
			// if(eventExists(eventItem)){
			bFlag = false;
			int i = db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
//			Log.i("zjj", "下载通告 已存在 更新:" + eventItem.eventid);
		} else {
			try {
				// if(eventItem.lastawstime.getTime() != 0)
				// eventItem.hasNewFeedback = true;
				//
				// eventsValues.put(HASFEEDBACK, eventItem.hasNewFeedback);

				bFlag = true;
				db.insert(EVENT_TABLE, null, eventsValues);
			} catch (Exception e) {

			}
		}
		return bFlag;
	}

	/**
	 * 保存事件详细到事件库
	 * 
	 * @param eventItem
	 * @return
	 */
	public void saveEventsDetail(EventDetailsItem eventItem, int allfield) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(OWNER_ID, eventItem.ownerid);
		eventsValues.put(OWNER, eventItem.owner);
		eventsValues.put(BODY_EVENT_ID, eventItem.eventid);
		eventsValues.put(EVENT_BODY, eventItem.body);
		eventsValues.put(EVENT_CLEAN_BODY, eventItem.cleanbody);
		eventsValues.put(USER, eventItem.user);
		eventsValues.put(ISNEEDREVIEW, eventItem.isneedreview);	//2014-01-20
		if (allfield == 1) {
			eventsValues.put(TYPE, eventItem.type);
			if (eventItem.orieventid != null) {
				ContentValues updateStatusValues = new ContentValues();
				updateStatusValues.put(STATUS, eventItem.oristatus);
				db.update(EVENT_TABLE, updateStatusValues, EVENT_ID_WHERE_AND_USER_WHERE, new String[] { Integer.toString(eventItem.orieventid), eventItem.user });
			}
			eventsValues.put(ORIEVENT_ID, eventItem.orieventid);
			eventsValues.put(ORI_STATUS, eventItem.oristatus);
			eventsValues.put(PUB_ID, eventItem.pubid.toLowerCase());
			eventsValues.put(PUB_NAME, eventItem.pubname);
			if (eventItem.post == null) {
				eventsValues.put(POST, 0);
			} else {
				eventsValues.put(POST, eventItem.post.getTime());
			}
			if (eventItem.start == null) {
				eventsValues.put(START, 0);
			} else {
				eventsValues.put(START, eventItem.start.getTime());
			}
			if (eventItem.end == null) {
				eventsValues.put(END, 0);
			} else {
				eventsValues.put(END, eventItem.end.getTime());
			}
			eventsValues.put(STATUS, eventItem.status);
			eventsValues.put(IFESHOW, eventItem.ifeshow);
			eventsValues.put(EVENT_TITLE, eventItem.title);
			eventsValues.put(EVENT_SUMMARY, eventItem.summary);
			eventsValues.put(LOCATION, eventItem.location);
			eventsValues.put(LOCATION_URL, eventItem.locationUrl);
			if (eventItem.cancelled == null) {
				eventsValues.put(CANCELLED, 0);
			} else {
				eventsValues.put(CANCELLED, eventItem.cancelled.getTime());
			}
			eventsValues.put(THUMBURL, eventItem.thumbUrl);
			if (eventItem.lastawstime == null) {
				eventsValues.put(LASTAWSTIME, 0);
			} else {
				eventsValues.put(LASTAWSTIME, eventItem.lastawstime.getTime());
			}
			if (eventItem.lastupdate == null) {
				eventsValues.put(LASTUPDATE, 0);
			} else {
				eventsValues.put(LASTUPDATE, eventItem.lastupdate.getTime());
			}
			if (eventItem.lastreadtime == null) {	//05-13
				eventsValues.put(EVENT_LAST_READTIME, 0);
			} else {
				eventsValues.put(EVENT_LAST_READTIME, eventItem.lastreadtime.getTime());
			}
			eventsValues.put(HASATT, eventItem.hasatt);
		}
		if (eventExists(eventItem.eventid, eventItem.user)) {
			db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
		} else {
			try {
				long count = db.insert(EVENT_TABLE, null, eventsValues);
				Log.i("zjj", "保存事件详细到事件库:" + count);
			} catch (Exception e) {

			}
		}

		// //插入回复人列表
		// if(!eventItem.listReplyer.isEmpty())
		// {
		// db.delete(EVENT_ASWORG_TABLE, "event_id = ? and user = ?",
		// whereArgs(eventItem));
		// ContentValues replyvalues = new ContentValues();
		// {
		// for(Replyer replyer : eventItem.listReplyer)
		// {
		// replyvalues.put(ASW_EVENT_ID,eventItem.eventid);
		// replyvalues.put(ASW_REPLYERID, replyer.ReplyerId);
		// replyvalues.put(ASW_REPLYERNAME, replyer.ReplyerName);
		// replyvalues.put(ASW_REPLYCOUNT, replyer.ReplyCount);
		// replyvalues.put(ASW_REPLYLASTTIME, replyer.ReplyLastTime);
		// replyvalues.put(USER, eventItem.user) ;
		// long lreturn = db.insert(EVENT_ASWORG_TABLE, ASW_EVENT_ID,
		// replyvalues);
		// }
		// }
		// }
		// 插入回复人列表
		if (!eventItem.listReplyer.isEmpty()) {
			// db.delete(EVENT_SEND_ASWORG_TABLE, "event_id = ? and user = ?",
			// whereArgs(eventItem));
			ContentValues replyvalues = new ContentValues();
			{
				for (Replyer replyer : eventItem.listReplyer) {
					replyvalues.put(ASW_EVENT_ID, eventItem.eventid);
					replyvalues.put(ASW_REPLYERID, replyer.ReplyerId);
					replyvalues.put(ASW_REPLYERNAME, replyer.ReplyerName);
					replyvalues.put(ASW_REPLYCOUNT, replyer.ReplyCount);
					replyvalues.put(ASW_REPLYLASTTIME, replyer.ReplyLastTime);
					replyvalues.put(USER, eventItem.user);
					if (replyerExists(eventItem.eventid, eventItem.user, replyer.ReplyerId)) {
						int lreturn = db.update(EVENT_ASWORG_TABLE, replyvalues, "event_id = ? and user = ? and " + ASW_REPLYERID + " = ?", new String[] { Integer.toString(eventItem.eventid),
								eventItem.user, replyer.ReplyerId });
						Log.i("zjj", lreturn + "");
					} else {
						replyvalues.put(LASTAWSTIMEREAD, replyer.lastawstimeread);
						long lreturn = db.insert(EVENT_ASWORG_TABLE, ASW_EVENT_ID, replyvalues);
						Log.i("zjj", lreturn + "");
					}
					
					//讨论组成员列表
					for(ReplyerMember member : replyer.memberList){
						ContentValues membervalues = new ContentValues();
						membervalues.put(ASW_REPLYERID, replyer.ReplyerId);
						membervalues.put(MEMBER_ID, member.id);
						membervalues.put(MEMBER_IMGURL, member.headimgurl);
						membervalues.put(MEMBER_LASTUPDATE, member.headimglastupdate);
						membervalues.put(MEMBER_NAME, member.name);
						
						if(memberExists(replyer.ReplyerId, member.id)){
							db.update(EVENT_MEMBER_TABLE,
									membervalues,
									ASW_REPLYERID + " = ? and " + MEMBER_ID + " = ?", 
									new String[] {replyer.ReplyerId,member.id});
						}else{
							db.insert(EVENT_MEMBER_TABLE,replyer.ReplyerId,membervalues);
						}
					}

				}
			}
		}

		// 插入图片URL
		if (!eventItem.imgs.isEmpty()) {
			db.delete(EVENT_IMG_TABLE, "event_id = ? and user = ?", whereArgs(eventItem));
			ContentValues imageValues = new ContentValues();
			for (Imags imag : eventItem.imgs) {
				imageValues.put(EVENT_ID, eventItem.eventid);
				imageValues.put(URL, imag.URL);
				imageValues.put(DESCRIPTION, imag.imgDescription);
				imageValues.put(ATTID, imag.attid);
				imageValues.put(USER, eventItem.user);
				imageValues.put(ATT_PREVIEW, imag.preview);
				imageValues.put(ATT_SIZE, imag.size);
				imageValues.put(ATT_NAME, imag.attname);
				db.insert(EVENT_IMG_TABLE, EVENT_ID, imageValues);
			}
		}
		
		//插入回评模版
		if (eventItem.mReviewTempList.size()>0){
			db.delete(EVENT_REVIEWTEMP_TABLE, "event_id = ? and user = ?", whereArgs(eventItem));
			ContentValues reviewtempValues = new ContentValues();
			for (EventDetailsItem.ReviewTemp reviewtemp : eventItem.mReviewTempList) {
				reviewtempValues.put(EVENT_ID , eventItem.eventid);
				reviewtempValues.put(REVIEWTEMP_ID ,reviewtemp.id);
				reviewtempValues.put(REVIEWTEMP_TITLE ,reviewtemp.title);
				reviewtempValues.put(REVIEWTEMP_TYPE ,reviewtemp.type);
				reviewtempValues.put(REVIEWTEMP_MAX ,reviewtemp.max);
				reviewtempValues.put(REVIEWTEMP_DEFAULT ,reviewtemp.defaultsum);
				reviewtempValues.put(REVIEWTEMP_REQUIRED ,reviewtemp.required);
				reviewtempValues.put(USER, eventItem.user);
				db.insert(EVENT_REVIEWTEMP_TABLE, EVENT_ID,reviewtempValues);
			}
		}
		
		
	}

	//*********************************************
	//*********************************************
	// --------------收到的通告---------------------
	//*********************************************
	//*********************************************
	/**
	 * 更新通告最后回复的时间
	 * 
	 * @param lastawstimeread
	 * @param eventItem
	 */
	public void updataEventLastawstime(Date lastawstime, EventDetailsItem eventItem) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIME, lastawstime.getTime());

		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i = db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

		Log.i("zjj", "更新通告最后回复的时间" + i);
	}

	/**
	 * 更新通告被读的最后回复的时间
	 * 用作比较通告列表中的回复图标是否有NEW
	 * 
	 * @param lastawstimeread
	 * @param eventItem
	 */
	public void updataEventLastawstimeread(Date lastawstimeread, EventDetailsItem eventItem) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIMEREAD, lastawstimeread.getTime());

		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i = db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

		Log.i("zjj", "更新通告被读的最后回复的时间" + i);
	}

	/**
	 * 更新通告回复人列表被读的最后回复的时间
	 * 用作比较是否有新回复(详细)
	 * 
	 * @param lastawstimeread
	 *            服务器给的replylasttime(秒数)
	 * @param eventid
	 * @param username
	 * @param replyerid
	 */
	public void updataAsworgLastawstimeread(String lastawstimeread, int eventid, String username, String replyerid) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIMEREAD, lastawstimeread);

		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i = db.update(EVENT_ASWORG_TABLE, eventsValues, "event_id = ? and user = ? and " + ASW_REPLYERID + " = ?", new String[] { Integer.toString(eventid), username, replyerid });

		Log.i("zjj", "更新通告回复人列表被读的最后回复的时间" + i);
	}

	//*********************************************
	//*********************************************
	// --------------已发的通告---------------------
	//*********************************************
	//*********************************************
	/**
	 * 更新已发通告最后回复的时间
	 * 
	 * @param lastawstimeread
	 * @param eventItem
	 */
	public void updataSendEventLastawstime(long lastawstime, EventDetailsItem eventItem) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIME, lastawstime);

		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i = db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

		Log.i("zjj", "更新已发通告最后回复的时间" + i);
	}
	
	/**
	 * 更新已发通告 本地聊天内容总数
	 * @param lastawstime
	 * @param eventItem
	 */
	public void updataSendEventAwscoutnclient(int sum, EventDetailsItem eventItem) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(AWSCOUNTCLIENT, sum);

		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i = db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

		Log.i("zjj", "更新已发通告最后回复的时间" + i);
	}

	/**
	 * 更新已发通告被读的最后回复的时间
	 * 用作比较已发通告列表中的回复图标是否有NEW
	 * 
	 * @param lastawstimeread
	 * @param eventItem
	 */
	public void updataSendEventLastawstimeread(Date lastawstimeread, EventDetailsItem eventItem) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIMEREAD, lastawstimeread.getTime());

		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i = db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

		Log.i("zjj", "更新已发通告被读的最后回复的时间" + i);
	}

	/**
	 * 更新已发通告回复人列表被读的最后回复的时间
	 * 用作比较是否有新回复(详细&&回复人列表)
	 * 
	 * @param lastawstimeread
	 * @param eventid
	 * @param username
	 * @param replyerid
	 */
	public void updataSendAsworgLastawstimeread(String lastawstimeread, int eventid, String username, String replyerid) {
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIMEREAD, lastawstimeread);

		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i = db.update(EVENT_SEND_ASWORG_TABLE, eventsValues, "event_id = ? and user = ? and " + ASW_REPLYERID + " = ?", new String[] { Integer.toString(eventid), username, replyerid });

		Log.i("zjj", "更新已发通告回复人列表被读的最后回复的时间" + i);
	}

	/**
	 * 保存已发活动到数据库
	 * 
	 * @param story
	 * @param saveCategories
	 */
	public synchronized boolean savePublishEventsItem(EventDetailsItem eventItem) {
		boolean bFlag = false;
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();

		eventsValues.put(OWNER_ID, eventItem.ownerid);
		eventsValues.put(OWNER, eventItem.owner);
		eventsValues.put(BODY_EVENT_ID, eventItem.eventid);
		eventsValues.put(TYPE, eventItem.type);
		if (eventItem.orieventid != null) {
			if (publishEventExists(eventItem.orieventid, eventItem.user)) {
				ContentValues updateStatusValues = new ContentValues();
				updateStatusValues.put(STATUS, eventItem.oristatus);
				db.update(EVENT_SEND_TABLE, updateStatusValues, EVENT_ID_WHERE_AND_USER_WHERE, new String[] { Integer.toString(eventItem.orieventid), eventItem.user });
			}
		}
		eventsValues.put(ORIEVENT_ID, eventItem.orieventid);
		eventsValues.put(ORI_STATUS, eventItem.oristatus);
		eventsValues.put(PUB_ID, eventItem.pubid.toLowerCase());
		eventsValues.put(PUB_NAME, eventItem.pubname);
		if (eventItem.post == null) {
			eventsValues.put(POST, 0);
		} else {
			eventsValues.put(POST, eventItem.post.getTime());
		}
		if (eventItem.start == null) {
			eventsValues.put(START, 0);
		} else {
			eventsValues.put(START, eventItem.start.getTime());
		}
		if (eventItem.end == null) {
			eventsValues.put(END, 0);
		} else {
			eventsValues.put(END, eventItem.end.getTime());
		}
		eventsValues.put(STATUS, eventItem.status);
		eventsValues.put(IFESHOW, eventItem.ifeshow);
		eventsValues.put(EVENT_TITLE, eventItem.title);
		eventsValues.put(EVENT_SUMMARY, eventItem.summary);
		eventsValues.put(LOCATION, eventItem.location);
		eventsValues.put(LOCATION_URL, eventItem.locationUrl);
		if (eventItem.cancelled == null) {
			eventsValues.put(CANCELLED, 0);
		} else {
			eventsValues.put(CANCELLED, eventItem.cancelled.getTime());
		}
		eventsValues.put(THUMBURL, eventItem.thumbUrl);
		eventsValues.put(USER, eventItem.user);
		if (eventItem.lastawstime == null) {
			eventsValues.put(LASTAWSTIME, 0);
		} else {
			eventsValues.put(LASTAWSTIME, eventItem.lastawstime.getTime());
		}
		// if (eventItem.lastawstimeread == null) {
		// eventsValues.put(LASTAWSTIMEREAD, 0);
		// } else {
		// eventsValues.put(LASTAWSTIMEREAD,
		// eventItem.lastawstimeread.getTime());
		// }
		if (eventItem.lastupdate == null) {
			eventsValues.put(LASTUPDATE, 0);
		} else {
			eventsValues.put(LASTUPDATE, eventItem.lastupdate.getTime());
		}
		eventsValues.put(HASATT, eventItem.hasatt);
		eventsValues.put(RECVTOTAL, eventItem.recvtotal);	//2013-05-09
		eventsValues.put(READCOUNT, eventItem.readcount);	//2013-05-09
		eventsValues.put(AWSCOUNT, eventItem.awscount);		//2013-11-13

		if (publishEventExists(eventItem.eventid, eventItem.user)) {
			bFlag = false;
			int i = db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
		} else {
			try {
				bFlag = true;
				long count = db.insert(EVENT_SEND_TABLE, null, eventsValues);
			} catch (Exception e) {

			}
		}
		return bFlag;
	}

	/**
	 * 保存时间详细到事件库
	 * 
	 * @param eventItem
	 * @return
	 */
	public void savePublishEventsDetail(EventDetailsItem eventItem) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();

		eventsValues.put(BODY_EVENT_ID, eventItem.eventid);
		eventsValues.put(EVENT_BODY, eventItem.body);
		eventsValues.put(EVENT_CLEAN_BODY, eventItem.cleanbody);
		eventsValues.put(RECVTOTAL, eventItem.recvtotal);
		eventsValues.put(READCOUNT, eventItem.readcount);
		if (publishEventExists(eventItem.eventid, eventItem.user)) {
			db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
		} else {
			try {
				long count = db.insert(EVENT_SEND_TABLE, null, eventsValues);
			} catch (Exception e) {

			}
		}

		// 插入回复人列表
		if (!eventItem.listReplyer.isEmpty()) {
			// db.delete(EVENT_SEND_ASWORG_TABLE, "event_id = ? and user = ?",
			// whereArgs(eventItem));
			ContentValues replyvalues = new ContentValues();
			{
				for (Replyer replyer : eventItem.listReplyer) {
					replyvalues.put(ASW_EVENT_ID, eventItem.eventid);
					replyvalues.put(ASW_REPLYERID, replyer.ReplyerId);
					replyvalues.put(ASW_REPLYERNAME, replyer.ReplyerName);
					replyvalues.put(ASW_REPLYCOUNT, replyer.ReplyCount);
					replyvalues.put(ASW_REPLYLASTTIME, replyer.ReplyLastTime);
					replyvalues.put(USER, eventItem.user);
					if (sendreplyerExists(eventItem.eventid, eventItem.user, replyer.ReplyerId)) {
						int lreturn = db.update(EVENT_SEND_ASWORG_TABLE, 
								replyvalues, 
								"event_id = ? and user = ? and " + ASW_REPLYERID + " = ?", 
								new String[] { Integer.toString(eventItem.eventid),
								eventItem.user, replyer.ReplyerId });
						Log.i("zjj", lreturn + "");
					} else {
						replyvalues.put(LASTAWSTIMEREAD, replyer.lastawstimeread);
						long lreturn = db.insert(EVENT_SEND_ASWORG_TABLE, ASW_EVENT_ID, replyvalues);
						Log.i("zjj", lreturn + "");
					}
					
					//讨论组成员列表
					for(ReplyerMember member : replyer.memberList){
						ContentValues membervalues = new ContentValues();
						membervalues.put(ASW_REPLYERID, replyer.ReplyerId);
						membervalues.put(MEMBER_ID, member.id);
						membervalues.put(MEMBER_IMGURL, member.headimgurl);
						membervalues.put(MEMBER_LASTUPDATE, member.headimglastupdate);
						membervalues.put(MEMBER_NAME, member.name);
						
						if(memberExists(replyer.ReplyerId, member.id)){
							db.update(EVENT_MEMBER_TABLE,
									membervalues,
									ASW_REPLYERID + " = ? and " + MEMBER_ID + " = ?", 
									new String[] {replyer.ReplyerId,member.id});
						}else{
							db.insert(EVENT_MEMBER_TABLE,replyer.ReplyerId,membervalues);
						}
					}
					
				}
			}
		}

		// 插入图片URL
		if (!eventItem.imgs.isEmpty()) {
			db.delete(EVENT_SEND_IMG_TABLE, "event_id = ? and user = ?", whereArgs(eventItem));
			ContentValues imageValues = new ContentValues();
			for (Imags imag : eventItem.imgs) {
				imageValues.put(EVENT_ID, eventItem.eventid);
				imageValues.put(URL, imag.URL);
				imageValues.put(DESCRIPTION, imag.imgDescription);
				imageValues.put(ATTID, imag.attid);
				imageValues.put(ATT_PREVIEW, imag.preview);
				imageValues.put(ATT_SIZE, imag.size);
				imageValues.put(ATT_NAME, imag.attname);
				imageValues.put(IMAGE_TYPE, imag.URL.lastIndexOf(".")>0?imag.URL.substring(imag.URL.lastIndexOf(".") + 1, imag.URL.length()):"");
				imageValues.put(USER, eventItem.user);
				db.insert(EVENT_SEND_IMG_TABLE, EVENT_ID, imageValues);
			}
		}
	}
	
	public void saveEventReadInfo(EventDetailsItem eventItem){
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();

		eventsValues.put(BODY_EVENT_ID, eventItem.eventid);
		eventsValues.put(READ_NAME_LIST, eventItem.read_name_list);
		eventsValues.put(UNREAD_NAME_LIST, eventItem.unread_name_list);
		
		if (publishEventExists(eventItem.eventid, eventItem.user)) {
			db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
		} else {
			
		}
	}

	/**
	 * 保存反馈内容
	 * 
	 * @param item
	 */
	public void saveAwsContent(AwsContentItem item) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BODY_EVENT_ID, item.eventid);
		values.put(AWS_ID, item.aws_id);
		values.put(AWS_GROUP_ID, item.aws_group_id);
		values.put(AWS_TIME, item.aws_time.getTime());
		values.put(AWS_PUB_ID, item.pub_id);
		values.put(AWS_PUB_NAME, item.pub_name);
		values.put(AWS_REC_ID, item.rec_id);
		values.put(AWS_REC_NAME, item.rec_name);
		values.put(AWS_BODY, item.aws_body);
		values.put(USER, item.user);

		if (!awsContentExists(item)) {
			try {
				long count = db.insert(AWS_CONTENT_TABLE, AWS_BODY, values);
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 判断回复内容是否已存在
	 * 
	 * @return
	 */
	private boolean awsContentExists(AwsContentItem item) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor result = db.query(AWS_CONTENT_TABLE, new String[] { AWS_ID }, BODY_EVENT_ID + " = " + item.eventid + " AND " + USER + " = '" + item.user + "' AND " + AWS_PUB_ID + " IN ('"
				+ item.pub_id + "', '" + item.rec_id + "' )" + " AND " + AWS_REC_ID + " IN ('" + item.pub_id + "', '" + item.rec_id + "' )" + " AND " + AWS_ID + " = " + item.aws_id, null, null, null,
				null);
		boolean awsContentExists = (result.getCount() > 0);
		result.close();
		return awsContentExists;
	}

	/**
	 * 取回复对话内容列表
	 * 
	 * @param eventid
	 * @param user
	 * @param recid 讨论组ID
	 * @return
	 */
	public List<AwsContentItem> getAwsContentList(int eventid, String user, String recid) {
		List<AwsContentItem> awsContentList = new ArrayList<AwsContentItem>();
		Cursor cursor = getAwsContentByPubID(eventid, user, recid);
		int i = cursor.getCount();
		cursor.requery();
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				AwsContentItem item = retrieveAwsContentItem(cursor);
				awsContentList.add(item);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return awsContentList;
	}

	/**
	 * 取返回对话内容(同时取联系人头像)
	 * @param eventid
	 * @param user
	 * @param recid 讨论组ID
	 * @return
	 */
//	public Cursor getAwsContentByPubID(int eventid, String user, String recid) {
//		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
//		Cursor cursor = db.query(AWS_CONTENT_TABLE + ", " + EVENT_MEMBER_TABLE, 
//				null, 
//				AWS_CONTENT_TABLE + "." + BODY_EVENT_ID + " = " + eventid + " AND " 
//				+ AWS_CONTENT_TABLE + "." + USER + " = '" + user  + "' AND " 
//				+ AWS_CONTENT_TABLE + "." + AWS_GROUP_ID + " = '" + recid + "' AND "
//				+ EVENT_MEMBER_TABLE + "." + ASW_REPLYERID + " = '" + recid + "' AND "
//				+ AWS_CONTENT_TABLE + "." + AWS_PUB_ID + " = " + EVENT_MEMBER_TABLE + "." + MEMBER_ID , 
//				null, null, null, 
//				AWS_TIME);
//		return cursor;
//	}

	/**
	 * 取返回对话内容
	 * @param eventid
	 * @param user
	 * @param recid 讨论组ID
	 * @return
	 */
	public Cursor getAwsContentByPubID(int eventid, String user, String recid) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(AWS_CONTENT_TABLE, 
				null, 
				AWS_CONTENT_TABLE + "." + BODY_EVENT_ID + " = " + eventid + " AND " 
				+ AWS_CONTENT_TABLE + "." + USER + " = '" + user  + "' AND " 
				+ AWS_CONTENT_TABLE + "." + AWS_GROUP_ID + " = '" + recid
				+ "'" , 
				null, null, null, 
				AWS_TIME);
		return cursor;
	}
	
	
	private AwsContentItem retrieveAwsContentItem(Cursor cursor) {
		int event_id_index = cursor.getColumnIndex(BODY_EVENT_ID);
		int aws_id_index = cursor.getColumnIndex(AWS_ID);
		int aws_time_index = cursor.getColumnIndex(AWS_TIME);
		int aws_pubid_index = cursor.getColumnIndex(AWS_PUB_ID);
		int aws_pubname_index = cursor.getColumnIndex(AWS_PUB_NAME);
		int aws_recid_index = cursor.getColumnIndex(AWS_REC_ID);
		int aws_recname_index = cursor.getColumnIndex(AWS_REC_NAME);
		int aws_body_index = cursor.getColumnIndex(AWS_BODY);
		int user_index = cursor.getColumnIndex(USER);
//		int headimgurl_index = cursor.getColumnIndex(MEMBER_IMGURL);
		
		AwsContentItem item = new AwsContentItem();
		item.eventid = cursor.getInt(event_id_index);
		item.aws_id = cursor.getInt(aws_id_index);
		item.aws_time = new Date(cursor.getLong(aws_time_index));
		item.pub_id = cursor.getString(aws_pubid_index);
		item.pub_name = cursor.getString(aws_pubname_index);
		item.rec_id = cursor.getString(aws_recid_index);
		item.rec_name = cursor.getString(aws_recname_index);
		item.aws_body = cursor.getString(aws_body_index);
		item.user = cursor.getString(user_index);
		item.headimgurl = getMemberPicUrlByPubID(item.pub_id);

		return item;
	}

	/**
	 * 取联系人信息头像
	 * @param user
	 * @param recid
	 * @return
	 */
	public String getMemberPicUrlByPubID(String recid) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_MEMBER_TABLE, 
				null,
				MEMBER_ID + " = '" + recid  + "'" , 
				null, null, null, 
				null);
		
		cursor.requery();
		
		String url = "";
		if (cursor.moveToFirst()) {
			int headimgurl_index = cursor.getColumnIndex(MEMBER_IMGURL);
			
			if(headimgurl_index != -1)
				url = cursor.getString(headimgurl_index);
		}
		cursor.close();
		cursor = null;
		return url;
	} 
	
	
	
	public boolean publishEventExists(int id, String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		Cursor result = db.query(EVENT_SEND_TABLE, new String[] { BODY_EVENT_ID }, EVENT_ID_WHERE_AND_USER_WHERE, new String[] { Integer.toString(id), user }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	public boolean eventExists(int id, String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		Cursor result = db.query(EVENT_TABLE, new String[] { BODY_EVENT_ID }, EVENT_ID_WHERE_AND_USER_WHERE, new String[] { Integer.toString(id), user }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	/**
	 * 
	 * @param eventid
	 * @param username
	 * @param replyerId
	 * @return
	 */
	private boolean replyerExists(int eventid, String username, String replyerId) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		Cursor result = db.query(EVENT_ASWORG_TABLE, 
				new String[] { ASW_REPLYERNAME }, 
				"event_id = ? and user = ? and " + ASW_REPLYERID + " = ?", 
				new String[] { Integer.toString(eventid), 
				username,
				replyerId }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}
	
	/**
	 * 
	 * @param memberid
	 * @return
	 */
	private boolean memberExists(String aswpubid, String memberid){
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		
		Cursor result = db.query(EVENT_MEMBER_TABLE, 
				new String[] { MEMBER_ID }, 
				ASW_REPLYERID + " = ? and " + MEMBER_ID + " = ?", 
				new String[] {aswpubid, memberid},
				null, null, null);

		boolean memberExists = (result.getCount() > 0);
		result.close();
		return memberExists;
	}

	/**
	 * 
	 * @param eventid
	 * @param username
	 * @param replyerId
	 * @return
	 */
	private boolean sendreplyerExists(int eventid, String username, String replyerId) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		Cursor result = db.query(EVENT_SEND_ASWORG_TABLE, new String[] { ASW_REPLYERNAME }, "event_id = ? and user = ? and " + ASW_REPLYERID + " = ?", new String[] { Integer.toString(eventid),
				username, replyerId }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	// /**
	// *
	// * @param item 新的数据
	// * @return
	// */
	// private boolean eventExists(EventDetailsItem item) {
	// SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
	//
	// Cursor result = db.query(
	// EVENT_TABLE,
	// null,//new String[] {BODY_EVENT_ID},
	// EVENT_ID_WHERE_AND_USER_WHERE,
	// new String[] {Integer.toString(item.eventid),item.user},
	// null, null, null);
	//
	// result.requery();
	// result.moveToFirst();
	// boolean storyExists = (result.getCount() > 0);
	// if(storyExists)
	// hasNewFeedback(result,item);
	// result.close();
	// return storyExists;
	// }
	//
	// /**
	// * 是否有新回复
	// * @param c
	// * @param item
	// */
	// private void hasNewFeedback(Cursor c,EventDetailsItem item){
	// EventDetailsItem i = retrieveEventDetailItem(c); //库里的记录
	// if(item.lastawstime != i.lastawstime){
	// item.hasNewFeedback = true;
	// }
	// }

	/**
	 * 标记已阅读过的回复的时间
	 */
	public void markAsReadFeedback(EventDetailsItem eventItem) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(LASTAWSTIMEREAD, eventItem.lastawstime.getTime());
		db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
	}

	/**
	 * 
	 * 标记已经阅读
	 * 
	 * @param newsItem
	 */
	public void markAsRead(EventDetailsItem eventItem) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(ISREAD, 1);
		db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

	}

	/**
	 * 标记书签
	 * 
	 * @param b
	 *            true:收藏 false:取消收藏
	 */
	public void markAsBookmark(EventDetailsItem eventItem, boolean b) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		if (b)
			eventsValues.put(BOOKMARKED, 1);
		else
			eventsValues.put(BOOKMARKED, 0);
		db.update(EVENT_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

	}

	/**
	 * 标记已发通告书签
	 * 
	 * @param b
	 *            true:收藏 false:取消收藏
	 */
	public void markSentAsBookmark(EventDetailsItem eventItem, boolean b) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		if (b)
			eventsValues.put(BOOKMARKED, 1);
		else
			eventsValues.put(BOOKMARKED, 0);
		db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

	}

	/**
	 * 
	 * @param storyId
	 * @return 是否已标记书签
	 */
	public boolean isBookmarked(EventDetailsItem eventItem) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();

		boolean flag = false;
		Cursor cursor = db.query(EVENT_TABLE, new String[] { BOOKMARKED }, EVENT_ID_WHERE_AND_USER_WHERE, new String[] { Integer.toString(eventItem.eventid), eventItem.user }, null, null, null);
		int bookmarked_index = cursor.getColumnIndex(BOOKMARKED);
		if (cursor.moveToFirst()) {
			flag = (cursor.getInt(bookmarked_index) == 1);
		}
		cursor.close();
		return flag;

	}

	/**
	 * 
	 * 标记已发送通告已读
	 * 
	 * @param newsItem
	 */
	public void markAreadySendAsRead(EventDetailsItem eventItem) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(ISREAD, 1);
		db.update(EVENT_SEND_TABLE, eventsValues, EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));

	}

	/**
	 * 从数据库里面得到Item列，表但不包含图片列表
	 * 
	 * @param 用户id
	 * @return Cursor
	 */
	public List<EventDetailsItem> getAllEventDetail(String user) {
		Cursor cursor = getEventCursor(0, 0, 0, user, 0);
		return retrieveEventItem(cursor);
	}

	/**
	 * 
	 * @param category
	 *            事件种类,
	 * @param begin
	 *            起始时间
	 * @param end
	 *            结束时间
	 * @param user
	 *            用户名字
	 * @param orderBy
	 *            0 为发送排序，1为起始排序 2为反馈时间排序
	 * @return
	 */
	public Cursor getEventCursor(int category, long begin, long over, String name, int orderBy) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		// Cursor cursor ;
		if (category < 0) {
			if (begin == 0 && over == 0) {
				String joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
						null, 
						"user = '" + name + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						null);
				return db.rawQuery(joinQuery, null);
			} else {
				String joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
						null, 
						"user = '" + name + "' and (" + begin + " between start and end or " + over + " between start and end )" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						null);
				return db.rawQuery(joinQuery, null);
				// cursor = db.rawQuery(joinQuery, null) ;
				// //<-------注:用这种方式会报cursor没关闭错误
			}
		} else {
			if (orderBy == 0) {
				String joinQuery;
				if (begin == 0 && over == 0) {
					joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
							null, "user = '" + name + "' and type = " + category + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
							null, null, 
							LASTUPDATE + " DESC ," + POST + " DESC", 
							null);
				} else {
					joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
							null, 
							"user = '" + name + "' and type = " + category + " and (" + begin + " between start and end or  " + over
							+ " between start and end )" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
							null, null, 
							LASTUPDATE + " DESC ," + POST + " DESC", 
							null);
				}
				System.out.println("joinQuery>>>" + joinQuery);
				return db.rawQuery(joinQuery, null);
			} else if (orderBy == 2) {
				String joinQuery;
				// 按回复时间排序
				if (begin == 0 && over == 0) {
					// 按回复时间排序
					joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
							null, 
							"user = '" + name + "' and type = " + category + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
							null, null, 
							LASTUPDATE + " DESC ," + POST + " DESC", 
							null);
				} else {
					joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
							null, 
							"user = '" + name + "' and type = " + category + " and (" + begin + " between start and end or  " + over
							+ " between start and end )" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
							null, null, 
							LASTUPDATE + " DESC ," + POST + " DESC", 
							null);
				}
				System.out.println("joinQuery>>>" + joinQuery);
				return db.rawQuery(joinQuery, null);
			} else {
				String joinQuery;
				if (begin == 0 && over == 0) {
					joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
							null, 
							"user = '" + name + "' and type = " + category + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
							null, null, 
							LASTUPDATE + " DESC ," + POST + " DESC", 
							null);
				} else {
					joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, 
							null, 
							"user = '" + name + "' and type = " + category + " and (" + begin + " between start and end or  " + over
							+ " between start and end )" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
							null, null, 
							LASTUPDATE + " DESC ," + POST + " DESC", 
							null);
				}
				System.out.println("joinQuery>>>" + joinQuery);
				return db.rawQuery(joinQuery, null);
			}
		}
	}

	/**
	 * 取一条通告详细
	 * 
	 * @param id
	 * @param username
	 * @return
	 */
	public Cursor getOneEventCursor(int id, String username) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, null, "user = '" + username + "' and " + BODY_EVENT_ID + " = " + id, null, null, null, null);
		return db.rawQuery(joinQuery, null);
	}

	/**
	 * 取一条已发通告详细
	 * 
	 * @param id
	 * @param username
	 * @return
	 */
	public Cursor getOnePublishEventCursor(int id, String username) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_SEND_TABLE, null, "user = '" + username + "' and " + BODY_EVENT_ID + " = " + id, null, null, null, null);
		return db.rawQuery(joinQuery, null);
	}
	
	/**
	 * 删除已发通告
	 * @param id
	 * @param username
	 */
	public void delPublishEvent(int id,String username){
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		db.delete(EVENT_SEND_TABLE, USER + " =? AND " + BODY_EVENT_ID + " =?", new String[] {username,id+""});
	}

	public List<EventDetailsItem> getAllEventDetail(Cursor cursor) {
		return retrieveEventItem(cursor);
	}

	// 取得学校通告的分类数据
	public Cursor getSchoolNoticeCursor(int category, int objtype, String name) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_TABLE, null, "type = " + category + " and objtype = " + objtype + " and user = ? ", new String[] { name }, null, null, LASTUPDATE + " DESC", null);
		return cursor;
	}

	/**
	 * 
	 * @param user
	 * @return 判断当前用户使用有使用收藏夹
	 */
	public boolean bookmarkExist(String user) {

		boolean flag = false;
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_TABLE, null, "user = ? and bookmarked = 1 ", new String[] { user }, null, null, null);
		if (cursor.getCount() > 0) {
			flag = true;
		}
		cursor.close();
		return flag;
	}

	/**
	 * 取得收藏
	 * 
	 * @return List<EventItem>
	 */
	public Cursor getBookmarksCursor(String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_TABLE, null, BOOKMARKED + "=1 and user =? ", new String[] { user }, null, null, LASTUPDATE + " DESC", null);
		return cursor;
		// return retrieveEventItem(cursor) ;
	}

	// 拿搜索结果
	public Cursor getSearchEventsCursor(String user, String strKey) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, null, "user = '" + user + "' and " + EVENT_TITLE + " like'%" + strKey + "%'", null, null, POST + " DESC", null);
		Cursor cursor = db.rawQuery(joinQuery, null);
		return cursor;
	}

	// add by Jiangbo 2012-05-25
	// 解析SchoolNoticeType详细
	public String retrieveSchoolNoticeType(Cursor cursor) {
		String SchoolNoticeType = "";
		if (cursor == null)
			return SchoolNoticeType;

		int obj_type_des = cursor.getColumnIndex(OBJ_TYPE_DES);
		SchoolNoticeType = cursor.getString(obj_type_des);
		return SchoolNoticeType;
	}

	/**
	 * 将 Cursor 解析成 EventItems
	 * 
	 * @param cursor
	 * @return
	 */
	public List<EventDetailsItem> retrieveEventItem(Cursor cursor) {
		List<EventDetailsItem> eventItems = new ArrayList<EventDetailsItem>();
		EventDetailsItem item = new EventDetailsItem();
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				item = retrieveEventDetailItem(cursor);
				eventItems.add(item);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return eventItems;
	}

	/**
	 * 单独解析为单个Item数据
	 * 
	 * @param cursor
	 * @return
	 */
	public EventDetailsItem retrieveEventDetailItem(Cursor cursor) {

		EventDetailsItem item = new EventDetailsItem();

		if (!cursor.isClosed() && cursor.getColumnCount() > 0) {
			int body_event_id_index = cursor.getColumnIndex(BODY_EVENT_ID);
			int owner_id_index = cursor.getColumnIndex(OWNER_ID);
			int owner_index = cursor.getColumnIndex(OWNER);
			int type_index = cursor.getColumnIndex(TYPE);
			int orievent_id_index = cursor.getColumnIndex(ORIEVENT_ID);
			int ori_status_index = cursor.getColumnIndex(ORI_STATUS);
			int pub_id_index = cursor.getColumnIndex(PUB_ID);
			int pub_name_index = cursor.getColumnIndex(PUB_NAME);
			int post_index = cursor.getColumnIndex(POST);
			int start_index = cursor.getColumnIndex(START);
			int end_index = cursor.getColumnIndex(END);
			int status_index = cursor.getColumnIndex(STATUS);
			int event_title_index = cursor.getColumnIndex(EVENT_TITLE);
			int event_summary_index = cursor.getColumnIndex(EVENT_SUMMARY);
			int location_index = cursor.getColumnIndex(LOCATION);
			int location_url_index = cursor.getColumnIndex(LOCATION_URL);
			int cancelled_index = cursor.getColumnIndex(CANCELLED);
			int thumburl_index = cursor.getColumnIndex(THUMBURL);
			int lastaws_time_index = cursor.getColumnIndex(LASTAWSTIME);
			int lastupdate_index = cursor.getColumnIndex(LASTUPDATE);
			int hasatt_index = cursor.getColumnIndex(HASATT);
			int event_body_index = cursor.getColumnIndex(EVENT_BODY);
			int event_cleanbody_index = cursor.getColumnIndex(EVENT_CLEAN_BODY);
			int bookmark_index = cursor.getColumnIndex(BOOKMARKED);
			int isread_index = cursor.getColumnIndex(ISREAD);
			int ifeshow_index = cursor.getColumnIndex(IFESHOW);
			int user_index = cursor.getColumnIndex(USER);
			int lastawsread_time_index = cursor.getColumnIndex(LASTAWSTIMEREAD);
			int lastsread_time_index = cursor.getColumnIndex(EVENT_LAST_READTIME);
			int inneedreview_index = cursor.getColumnIndex(ISNEEDREVIEW);

			item.eventid = cursor.getInt(body_event_id_index);
			item.ownerid = cursor.getString(owner_id_index);
			item.owner = cursor.getString(owner_index);
			item.type = cursor.getInt(type_index);
			item.orieventid = cursor.getInt(orievent_id_index);
			item.oristatus = cursor.getString(ori_status_index);
			item.pubid = cursor.getString(pub_id_index);
			item.pubname = cursor.getString(pub_name_index);
			item.post = new Date(cursor.getLong(post_index));
			item.start = new Date(cursor.getLong(start_index));
			item.end = new Date(cursor.getLong(end_index));
			item.status = cursor.getString(status_index);
			item.title = cursor.getString(event_title_index);
			item.summary = cursor.getString(event_summary_index);
			item.location = cursor.getString(location_index);
			item.locationUrl = cursor.getString(location_url_index);
			item.cancelled = new Date(cursor.getLong(cancelled_index));
			item.thumbUrl = cursor.getString(thumburl_index);
			item.lastawstime = new Date(cursor.getLong(lastaws_time_index));
			item.lastupdate = new Date(cursor.getLong(lastupdate_index));
			item.lastawstimeread = new Date(cursor.getLong(lastawsread_time_index));
			
			if(lastsread_time_index != -1){
				item.lastreadtime = new Date(cursor.getLong(lastsread_time_index));
			}else{
				item.lastreadtime = new Date(0);
			}
			

			if (cursor.getInt(hasatt_index) == 1) {
				item.hasatt = true;
			} else {
				item.hasatt = false;
			}

			// if(cursor.getInt(hasfeedback_index) == 1){
			// item.hasNewFeedback = true;
			// }else{
			// item.hasNewFeedback = false;
			// }

			item.body = cursor.getString(event_body_index) == null ? "" : cursor.getString(event_body_index);
			if (event_cleanbody_index > 0) {
				item.cleanbody = cursor.getString(event_cleanbody_index);
			}
			item.isread = isReadOrBookmark(cursor.getInt(isread_index));
			item.bookmark = isReadOrBookmark(cursor.getInt(bookmark_index));
			item.ifeshow = isReadOrBookmark(cursor.getInt(ifeshow_index));
			item.isneedreview = isReadOrBookmark(cursor.getInt(inneedreview_index));
			item.user = cursor.getString(user_index);

			List<Imags> ImageItems = new ArrayList<Imags>();
			ImageItems = retrieveImagesItem(item);
			if (!ImageItems.isEmpty()) {
				item.imgs = ImageItems;
			}

			List<Replyer> replyItems = new ArrayList<Replyer>();
			replyItems = retrieveReplyerItem(item);
			if (!replyItems.isEmpty())
				item.listReplyer = replyItems;
			
			List<ReviewTemp> reviewtempItems = new ArrayList<EventDetailsItem.ReviewTemp>();
			reviewtempItems = retrieveReviewtempItem (item);
			if (!reviewtempItems.isEmpty())
				item.mReviewTempList = reviewtempItems;
		}

		return item;
	}

	/**
	 * 从传入的item中得到得item属性才能拿到List<Imags>
	 * 
	 * @param item
	 * @return
	 */
	public List<Imags> retrieveImagesItem(EventDetailsItem item) {
		List<Imags> ImageItems = new ArrayList<Imags>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_IMG_TABLE, null, "event_id = ? and user = ?", whereArgs(item), null, null, null);
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

	/**
	 * 从传入的item中得到得item属性才能拿到List<Reviewtemp>
	 * 
	 * @param item
	 * @return
	 */
	public List<ReviewTemp> retrieveReviewtempItem(EventDetailsItem item) {
		List<ReviewTemp> ReviewtempItems = new ArrayList<ReviewTemp>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_REVIEWTEMP_TABLE, null, "event_id = ? and user = ?", whereArgs(item), null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int id_index = cursor.getColumnIndex(REVIEWTEMP_ID);
				int title_index = cursor.getColumnIndex(REVIEWTEMP_TITLE);
				int type_index = cursor.getColumnIndex(REVIEWTEMP_TYPE);
				int max_index = cursor.getColumnIndex(REVIEWTEMP_MAX);
				int default_index = cursor.getColumnIndex(REVIEWTEMP_DEFAULT);
				int required_index = cursor.getColumnIndex(REVIEWTEMP_REQUIRED);
				
				EventDetailsItem.ReviewTemp reviewtemp = new ReviewTemp();
				reviewtemp.id = cursor.getString(id_index);
				reviewtemp.title = cursor.getString(title_index);
				reviewtemp.type = cursor.getString(type_index);
				reviewtemp.max = cursor.getInt(max_index);
				reviewtemp.defaultsum = cursor.getInt(default_index);
				if (cursor.getInt(required_index) == 1) {
					reviewtemp.required = true;
				} else {
					reviewtemp.required = false;
				}
				
				ReviewtempItems.add(reviewtemp);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return ReviewtempItems;
	}
	
	/**
	 * 取联系人信息
	 * 
	 * @param item
	 * @return
	 */
	public List<ReplyerMember> retrieveMemberlistItem(Replyer item) {
		List<ReplyerMember> memberlistItems = new ArrayList<ReplyerMember>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(EVENT_MEMBER_TABLE, 
				null,
				ASW_REPLYERID + " = ? ", 
				new String[] {item.ReplyerId},
				null, null, null);
		
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int id_index = cursor.getColumnIndex(MEMBER_ID);
				int name_index = cursor.getColumnIndex(MEMBER_NAME);
				int url_index = cursor.getColumnIndex(MEMBER_IMGURL);
				int lastupdate_index = cursor.getColumnIndex(MEMBER_LASTUPDATE);
				
				EventDetailsItem.ReplyerMember member = new ReplyerMember();
				member.id = cursor.getString(id_index);
				member.name = cursor.getString(name_index);
				member.headimgurl = cursor.getString(url_index);
				member.headimglastupdate = cursor.getString(lastupdate_index);
				
				memberlistItems.add(member);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return memberlistItems;
	}
	
	public List<Imags> getImagesList(int eventID, String name) {
		List<Imags> ImageItems = new ArrayList<Imags>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_IMG_TABLE, 
				null,
				"event_id = ? and user = ?",
				new String[] { Integer.toString(eventID), name }, null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int url_index = cursor.getColumnIndex(URL);
				int description_index = cursor.getColumnIndex(DESCRIPTION);
				int att_id_index = cursor.getColumnIndex(ATTID);
				Imags image = new Imags(cursor.getString(url_index), cursor.getString(description_index), cursor.getInt(att_id_index));
				image.setImgData(retrieveAttachmentBytes(cursor.getString(url_index)));
				ImageItems.add(image);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return ImageItems;
	}

	private static boolean isReadOrBookmark(int i) {
		boolean flag = false;
		if (i == 1) {
			flag = true;
		}
		return flag;
	}

	// 保存公告类型
	public synchronized void saveNoticeType(NoticeTypeItem noticeItem) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues noticeTypeValues = new ContentValues();
		noticeTypeValues.put(OBJ_TYPE, noticeItem.objtype);
		noticeTypeValues.put(OBJ_TYPE_DES, noticeItem.objtypedes);
		noticeTypeValues.put(USER, noticeItem.user);
		db.insert(NOTICE_TYPE_TABLE, OBJ_TYPE, noticeTypeValues);
	}

	public void deleteNoticeType(String user) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		db.delete(NOTICE_TYPE_TABLE, "user = ? ", new String[] { user });
	}

	public boolean eventTypeExists(String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		Cursor result = db.query(NOTICE_TYPE_TABLE, new String[] { OBJ_TYPE }, " user = ? ", new String[] { user }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	public List<NoticeTypeItem> getAllNoticeType(String user) {
		List<NoticeTypeItem> noticeList = new ArrayList<NoticeTypeItem>();
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		Cursor cursor = db.query(NOTICE_TYPE_TABLE, null, " user = ? ", new String[] { user }, null, null, OBJ_TYPE);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				System.out.println("while 执行次数");
				int type_index = cursor.getColumnIndex(OBJ_TYPE);
				int typedes_index = cursor.getColumnIndex(OBJ_TYPE_DES);
				int user_index = cursor.getColumnIndex(USER);

				NoticeTypeItem item = new NoticeTypeItem();
				item.objtype = cursor.getInt(type_index);
				item.objtypedes = cursor.getString(typedes_index);
				item.user = cursor.getString(user_index);
				noticeList.add(item);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return noticeList;

	}

	// 校内通告

	public Cursor getNoticeType(String user, int objtype) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		Cursor cursor = db.query(NOTICE_TYPE_TABLE, null, " user = ? " + " and objtype =" + objtype, new String[] { user }, null, null, OBJ_TYPE);
		return cursor;
	}

	public NoticeTypeItem retrieveNoticeTypeItem(Cursor cursor) {
		int type_index = cursor.getColumnIndex(OBJ_TYPE);
		int typedes_index = cursor.getColumnIndex(OBJ_TYPE_DES);
		int user_index = cursor.getColumnIndex(USER);

		NoticeTypeItem item = new NoticeTypeItem();
		item.objtype = cursor.getInt(type_index);
		item.objtypedes = cursor.getString(typedes_index);
		item.user = cursor.getString(user_index);
		return item;
	}

	public Cursor getNoticeType(String user) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		Cursor cursor = db.query(NOTICE_TYPE_TABLE, null, " user = ? ", new String[] { user }, null, null, OBJ_TYPE);
		return cursor;
	}

	// //保存已发通告
	// public synchronized boolean saveEventsSendItem(EventDetailsItem
	// eventItem) {
	// boolean bFlag = false;
	// System.out.println(eventsDBHelper) ;
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// ContentValues eventsValues = new ContentValues();
	// eventsValues.put(BODY_EVENT_ID, eventItem.id);
	// eventsValues.put(OWNER_ID, eventItem.ownerid);
	// eventsValues.put(OWNER, eventItem.owner);
	// eventsValues.put(EVENT, eventItem.event);
	// eventsValues.put(TYPE, eventItem.type);
	// eventsValues.put(OBJTYPE, eventItem.objtype);
	// if(eventItem.orieventid != null){
	// ContentValues updateStatusValues = new ContentValues();
	// updateStatusValues.put(BODY_EVENT_ID, eventItem.orieventid) ;
	// updateStatusValues.put(STATUS,eventItem.oristatus);
	// try{
	// db.update(EVENT_SEND_TABLE, updateStatusValues,
	// EVENT_ID_WHERE_AND_USER_WHERE, new
	// String[]{Integer.toString(eventItem.orieventid),eventItem.user}) ;
	// }catch (Exception e){
	// e.printStackTrace() ;
	// }
	// }
	// eventsValues.put(PUB_ID, eventItem.pubid);
	// eventsValues.put(PUB_NAME,eventItem.pubname);
	// eventsValues.put(IFESHOW, eventItem.ifeshow);
	//
	// if (eventItem.post == null) {
	// eventsValues.put(POST, 0);
	// } else {
	// eventsValues.put(POST, eventItem.post.getTime());
	// }
	// if (eventItem.start == null) {
	// eventsValues.put(START, 0);
	// } else {
	// eventsValues.put(START, eventItem.start.getTime());
	// }
	// if (eventItem.end == null) {
	// eventsValues.put(END, 0);
	// } else {
	// eventsValues.put(END, eventItem.end.getTime());
	// }
	// eventsValues.put(SHORT_LOC, eventItem.shortloc);
	// eventsValues.put(LOCATION,eventItem.location);
	// eventsValues.put(STATUS,eventItem.status);
	// eventsValues.put(EVENT_TITLE, eventItem.title);
	// eventsValues.put(EVENT_BODY, eventItem.body);
	// if (eventItem.cancelled == null) {
	// eventsValues.put(CANCELLED, 0);
	// } else {
	// eventsValues.put(CANCELLED, eventItem.cancelled.getTime());
	// }
	// eventsValues.put(READCOUNT, eventItem.readcount);
	// eventsValues.put(TOTALCOUNT, eventItem.totalcount);
	// System.out.println("保存已发通告已读用户数>>>>>" +eventItem.readcount);
	// System.out.println("保存已发通告收件人总数>>>>>" +eventItem.totalcount);
	// eventsValues.put(USER, eventItem.user);
	//
	// if(eventSendExists(eventItem.id,eventItem.user)){
	// bFlag = false;
	// System.out.println("更新>>>>" ) ;
	// int i = db.update(EVENT_SEND_TABLE, eventsValues,
	// EVENT_ID_WHERE_AND_USER_WHERE, whereArgs(eventItem));
	// db.delete(EVENT_SEND_IMG_TABLE, "event_id = ? and user = ?",
	// whereArgs(eventItem)) ;
	// System.out.println(i ) ;
	// }else{
	// bFlag = true;
	// try{
	// long count = db.insert(EVENT_SEND_TABLE, null, eventsValues) ;
	// System.out.println("插入行返回值>>>>" + count) ;
	// }catch(Exception e){
	// System.out.println(e.getMessage());
	// }
	// }
	//
	// //插入回复人列表
	// if(!eventItem.listReplyer.isEmpty())
	// {
	// db.delete(EVENT_SEND_ASWORG_TABLE, "event_id = ? and user = ?",
	// whereArgs(eventItem)) ;
	// ContentValues replyvalues = new ContentValues();
	// {
	// for(Replyer replyer : eventItem.listReplyer)
	// {
	// replyvalues.put(ASW_EVENT_ID,eventItem.id);
	// replyvalues.put(ASW_REPLYERID, replyer.ReplyerId);
	// replyvalues.put(ASW_REPLYERNAME, replyer.ReplyerName);
	// replyvalues.put(ASW_REPLYCOUNT, replyer.ReplyCount);
	// replyvalues.put(ASW_REPLYLASTTIME, replyer.ReplyLastTime);
	// replyvalues.put(USER, eventItem.user) ;
	// long lreturn = db.insert(EVENT_SEND_ASWORG_TABLE, ASW_EVENT_ID,
	// replyvalues);
	// System.out.println("EventsDB.saveEventsSendItem()");
	// }
	// }
	// }
	//
	// //插入图片URL
	// if(!eventItem.imgs.isEmpty()){
	// ContentValues imageValues = new ContentValues();
	// for(Imags imag:eventItem.imgs ){
	// imageValues.put(EVENT_ID, eventItem.id) ;
	// imageValues.put(URL, imag.URL) ;
	// imageValues.put(DESCRIPTION,imag.imgDescription);
	// imageValues.put(USER, eventItem.user) ;
	// db.insert(EVENT_SEND_IMG_TABLE, EVENT_ID, imageValues) ;
	// }
	//
	// }
	// return bFlag;
	// }

	// 判断已发公告是否存在
	private boolean eventSendExists(int id, String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		Cursor result = db.query(EVENT_SEND_TABLE, new String[] { BODY_EVENT_ID }, EVENT_ID_WHERE_AND_USER_WHERE, new String[] { Integer.toString(id), user }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	// 取得已发通告数据
	public Cursor getEventSendCursor(long begin, long over, String name, int orderBy, String limit) {
		// SQLiteDatabase db = eventsDBHelper.getReadableDatabase() ;
		// Cursor cursor = db.rawQuery("select * from " +EVENT_SEND_TABLE +
		// " where user = ? ORDER BY post DESC" ,new String[]{name}) ;
		// return cursor ;

		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		if (orderBy == 0) {
			String joinQuery;
			if (begin == 0 && over == 0) {
				joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
						EVENT_SEND_TABLE, 
						null, "user = '" + name + "'" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						limit);
			} else {
				joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
						EVENT_SEND_TABLE, 
						null, 
						"user = '" + name + "' and (" + begin + " between start and end or  " + over + " between start and end )" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'", 
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						limit);
			}
			System.out.println("joinQuery>>>" + joinQuery);
			return db.rawQuery(joinQuery, null);
			// cursor = db.rawQuery(joinQuery,null);
		} else if (orderBy == 2) {
			String joinQuery;
			// 按回复时间排序
			if (begin == 0 && over == 0) {
				// 按回复时间排序
				joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
						EVENT_SEND_TABLE, 
						null, 
						"user = '" + name + "'" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						limit);
			} else {
				joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
						EVENT_SEND_TABLE, 
						null, 
						"user = '" + name + "' and (" + begin + " between start and end or  " + over + " between start and end )" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						limit);
			}
			// joinQuery =
			// SQLiteQueryBuilder.buildQueryString(false,EVENT_TABLE,null,"user = '"
			// + name +"' and type = " + category ,null,null,LASTAWSTIME +
			// " DESC" ,null) ;
			System.out.println("joinQuery>>>" + joinQuery);
			return db.rawQuery(joinQuery, null);
		} else {
			String joinQuery;
			if (begin == 0 && over == 0) {
				joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
						EVENT_SEND_TABLE, 
						null, 
						"user = '" + name + "'" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						limit);
			} else {
				joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
						EVENT_SEND_TABLE, 
						null, 
						"user = '" + name + "' and (" + begin + " between start and end or  " + over + " between start and end )" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
						null, null, 
						LASTUPDATE + " DESC ," + POST + " DESC", 
						limit);
			}
			System.out.println("joinQuery>>>" + joinQuery);
			return db.rawQuery(joinQuery, null);
			// cursor = db.rawQuery(joinQuery,null);
		}
	}
	
	/**
	 * 取已发通告总数
	 * @param name
	 * @return
	 */
	public int getEventSendSum(String name){
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		
		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
				EVENT_SEND_TABLE, 
				null, "user = '" + name + "'" + " and " + STATUS + " != '" + EventDraftItem.ORISTATUS_TYPE_D + "'",
				null, null, null, null);
		Cursor cursor = db.rawQuery(joinQuery, null);
		
		return cursor.getCount();
	}

	// //单独解析为单个已发通告Item数据
	// public EventDetailsItem retrieveEventSendDetailItem(Cursor cursor){
	// int body_event_id_index = cursor.getColumnIndex(BODY_EVENT_ID);
	// int owner_id_index = cursor.getColumnIndex(OWNER_ID);
	// int owner_index = cursor.getColumnIndex(OWNER);
	// int event_index = cursor.getColumnIndex(EVENT);
	// int type_index = cursor.getColumnIndex(TYPE);
	// int objtype_index = cursor.getColumnIndex(OBJTYPE);
	// int orievent_id_index = cursor.getColumnIndex(ORIEVENT_ID);
	// int ori_status_index = cursor.getColumnIndex(ORI_STATUS);
	// int pub_name_index = cursor.getColumnIndex(PUB_NAME);
	// int post_index = cursor.getColumnIndex(POST);
	// int start_index = cursor.getColumnIndex(START);
	// int end_index = cursor.getColumnIndex(END);
	// int short_loc_index = cursor.getColumnIndex(SHORT_LOC);
	// int location_index = cursor.getColumnIndex(LOCATION);
	// int status_index = cursor.getColumnIndex(STATUS);
	// int event_title_index = cursor.getColumnIndex(EVENT_TITLE);
	// int event_body_index = cursor.getColumnIndex(EVENT_BODY);
	// int cancelled_index = cursor.getColumnIndex(CANCELLED);
	// int isread_index = cursor.getColumnIndex(ISREAD) ;
	// int bookmark_index = cursor.getColumnIndex(BOOKMARKED) ;
	// int readcount_index = cursor.getColumnIndex(READCOUNT) ;
	// int totalcount_index = cursor.getColumnIndex(TOTALCOUNT) ;
	// int user_index = cursor.getColumnIndex(USER);
	// int ifeshow_index = cursor.getColumnIndex(IFESHOW);
	//
	// EventDetailsItem item = new EventDetailsItem();
	// item.id = cursor.getInt(body_event_id_index) ;
	// item.ownerid= cursor.getString(owner_id_index) ;
	// item.owner= cursor.getString(owner_index) ;
	// item.event = cursor.getInt(event_index) ;
	// item.type = cursor.getInt(type_index) ;
	// item.objtype = cursor.getInt(objtype_index) ;
	// item.orieventid = cursor.getInt(orievent_id_index) ;
	// item.oristatus = cursor.getString(ori_status_index) ;
	// item.pubname = cursor.getString(pub_name_index) ;
	// item.post = new Date(cursor.getLong(post_index));
	// item.start = new Date(cursor.getLong(start_index)) ;
	// item.end = new Date(cursor.getLong(end_index)) ;
	// item.shortloc = cursor.getString(short_loc_index) ;
	// item.location = cursor.getString(location_index) ;
	// item.status = cursor.getString(status_index) ;
	// item.title = cursor.getString(event_title_index) ;
	// item.body = cursor.getString(event_body_index) ;
	// item.cancelled = new Date(cursor.getLong(cancelled_index)) ;
	// item.isread = isReadOrBookmark(cursor.getInt(isread_index)) ;
	// item.bookmark = isReadOrBookmark(cursor.getInt(bookmark_index)) ;
	// item.readcount = cursor.getInt(readcount_index) ;
	// item.totalcount = cursor.getInt(totalcount_index) ;
	// item.user = cursor.getString(user_index) ;
	// item.ifeshow = isReadOrBookmark(cursor.getInt(ifeshow_index));
	//
	// List<Imags> ImageItems = new ArrayList<Imags>() ;
	// ImageItems = retrieveSendImagesItem(item) ;
	// if(!ImageItems.isEmpty()){
	// item.imgs = ImageItems ;
	// }
	//
	// List<Replyer> replyItems = new ArrayList<Replyer>();
	// replyItems = retrieveSendReplyerItem(item);
	// if(!replyItems.isEmpty())
	// item.listReplyer = replyItems;
	//
	//
	// return item ;
	//
	// }

	// /**
	// * 返回含附件的通告Item
	// * @param cursor
	// * @return
	// */
	// public EventDetailsItem send2DraftDetailItem(Cursor cursor){
	// int body_event_id_index = cursor.getColumnIndex(BODY_EVENT_ID);
	// int owner_id_index = cursor.getColumnIndex(OWNER_ID);
	// int owner_index = cursor.getColumnIndex(OWNER);
	// int event_index = cursor.getColumnIndex(EVENT);
	// int type_index = cursor.getColumnIndex(TYPE);
	// int objtype_index = cursor.getColumnIndex(OBJTYPE);
	// int orievent_id_index = cursor.getColumnIndex(ORIEVENT_ID);
	// int ori_status_index = cursor.getColumnIndex(ORI_STATUS);
	// int pub_name_index = cursor.getColumnIndex(PUB_NAME);
	// int post_index = cursor.getColumnIndex(POST);
	// int start_index = cursor.getColumnIndex(START);
	// int end_index = cursor.getColumnIndex(END);
	// int short_loc_index = cursor.getColumnIndex(SHORT_LOC);
	// int location_index = cursor.getColumnIndex(LOCATION);
	// int status_index = cursor.getColumnIndex(STATUS);
	// int event_title_index = cursor.getColumnIndex(EVENT_TITLE);
	// int event_body_index = cursor.getColumnIndex(EVENT_BODY);
	// int cancelled_index = cursor.getColumnIndex(CANCELLED);
	// int isread_index = cursor.getColumnIndex(ISREAD) ;
	// int bookmark_index = cursor.getColumnIndex(BOOKMARKED) ;
	// int readcount_index = cursor.getColumnIndex(READCOUNT) ;
	// int totalcount_index = cursor.getColumnIndex(TOTALCOUNT) ;
	// int user_index = cursor.getColumnIndex(USER);
	//
	// EventDetailsItem item = new EventDetailsItem();
	// item.id = cursor.getInt(body_event_id_index) ;
	// item.ownerid= cursor.getString(owner_id_index) ;
	// item.owner= cursor.getString(owner_index) ;
	// item.event = cursor.getInt(event_index) ;
	// item.type = cursor.getInt(type_index) ;
	// item.objtype = cursor.getInt(objtype_index) ;
	// item.orieventid = cursor.getInt(orievent_id_index) ;
	// item.oristatus = cursor.getString(ori_status_index) ;
	// item.pubname = cursor.getString(pub_name_index) ;
	// item.post = new Date(cursor.getLong(post_index));
	// item.start = new Date(cursor.getLong(start_index)) ;
	// item.end = new Date(cursor.getLong(end_index)) ;
	// item.shortloc = cursor.getString(short_loc_index) ;
	// item.location = cursor.getString(location_index) ;
	// item.status = cursor.getString(status_index) ;
	// item.title = cursor.getString(event_title_index) ;
	// item.body = cursor.getString(event_body_index) ;
	// item.cancelled = new Date(cursor.getLong(cancelled_index)) ;
	// item.isread = isReadOrBookmark(cursor.getInt(isread_index)) ;
	// item.bookmark = isReadOrBookmark(cursor.getInt(bookmark_index)) ;
	// item.readcount = cursor.getInt(readcount_index) ;
	// item.totalcount = cursor.getInt(totalcount_index) ;
	// item.user = cursor.getString(user_index) ;
	// List<Imags> ImageItems = new ArrayList<Imags>() ;
	// ImageItems = retrieveSendImagesItem(item) ;
	// List<Attachment> attachmentList = new ArrayList<Attachment>() ;
	// if(!ImageItems.isEmpty()){
	// item.imgs = ImageItems ;
	// attachmentList = getSendAttachment(item.imgs) ;
	// item.attachmentList = attachmentList ;
	// }
	//
	// cursor.close() ;
	// return item ;
	//
	// }

	// --------------------------------已发------------------------------------
	/**
	 * 单独解析为单个已发送Item数据
	 * 
	 * @param cursor
	 * @return
	 */
	public EventDetailsItem retrievePublishEventDetailItem(Cursor cursor) {

		int body_event_id_index = cursor.getColumnIndex(BODY_EVENT_ID);
		int owner_id_index = cursor.getColumnIndex(OWNER_ID);
		int owner_index = cursor.getColumnIndex(OWNER);
		int type_index = cursor.getColumnIndex(TYPE);
		int orievent_id_index = cursor.getColumnIndex(ORIEVENT_ID);
		int ori_status_index = cursor.getColumnIndex(ORI_STATUS);
		int pub_id_index = cursor.getColumnIndex(PUB_ID);
		int pub_name_index = cursor.getColumnIndex(PUB_NAME);
		int post_index = cursor.getColumnIndex(POST);
		int start_index = cursor.getColumnIndex(START);
		int end_index = cursor.getColumnIndex(END);
		int status_index = cursor.getColumnIndex(STATUS);
		int event_title_index = cursor.getColumnIndex(EVENT_TITLE);
		int event_summary_index = cursor.getColumnIndex(EVENT_SUMMARY);
		int location_index = cursor.getColumnIndex(LOCATION);
		int location_url_index = cursor.getColumnIndex(LOCATION_URL);
		int cancelled_index = cursor.getColumnIndex(CANCELLED);
		int thumburl_index = cursor.getColumnIndex(THUMBURL);
		int lastaws_time_index = cursor.getColumnIndex(LASTAWSTIME);
		int lastupdate_index = cursor.getColumnIndex(LASTUPDATE);
		int hasatt_index = cursor.getColumnIndex(HASATT);
		int event_body_index = cursor.getColumnIndex(EVENT_BODY);
		int event_cleanbody_index = cursor.getColumnIndex(EVENT_CLEAN_BODY);
		int bookmark_index = cursor.getColumnIndex(BOOKMARKED);
		int isread_index = cursor.getColumnIndex(ISREAD);
		int ifeshow_index = cursor.getColumnIndex(IFESHOW);
		int user_index = cursor.getColumnIndex(USER);
		int recvtotal_index = cursor.getColumnIndex(RECVTOTAL);
		int readcount_index = cursor.getColumnIndex(READCOUNT);
		int lastawsread_time_index = cursor.getColumnIndex(LASTAWSTIMEREAD);
		int readname_index = cursor.getColumnIndex(READ_NAME_LIST);
		int unreadname_index = cursor.getColumnIndex(UNREAD_NAME_LIST);
		int awscout_index = cursor.getColumnIndex(AWSCOUNT);
		int awscoutclient_index = cursor.getColumnIndex(AWSCOUNTCLIENT);

		EventDetailsItem item = new EventDetailsItem();
		try{
			item.eventid = cursor.getInt(body_event_id_index);
			item.ownerid = cursor.getString(owner_id_index);
			item.owner = cursor.getString(owner_index);
			item.type = cursor.getInt(type_index);
			item.orieventid = cursor.getInt(orievent_id_index);
			item.oristatus = cursor.getString(ori_status_index);
			item.pubid = cursor.getString(pub_id_index);
			item.pubname = cursor.getString(pub_name_index);
			item.post = new Date(cursor.getLong(post_index));
			item.start = new Date(cursor.getLong(start_index));
			item.end = new Date(cursor.getLong(end_index));
			item.status = cursor.getString(status_index);
			item.title = cursor.getString(event_title_index);
			item.summary = cursor.getString(event_summary_index);
			item.location = cursor.getString(location_index);
			item.locationUrl = cursor.getString(location_url_index);
			item.cancelled = new Date(cursor.getLong(cancelled_index));
			item.thumbUrl = cursor.getString(thumburl_index);
			item.lastawstime = new Date(cursor.getLong(lastaws_time_index));
			item.lastupdate = new Date(cursor.getLong(lastupdate_index));
			item.lastawstimeread = new Date(cursor.getLong(lastawsread_time_index));
			item.recvtotal = cursor.getString(recvtotal_index);
			item.readcount = cursor.getString(readcount_index);
			item.read_name_list = cursor.getString(readname_index);
			item.unread_name_list = cursor.getString(unreadname_index);
			if(awscout_index != -1)
				item.awscount = cursor.getInt(awscout_index);
			if(awscoutclient_index != -1)
				item.awscountclient = cursor.getInt(awscoutclient_index);
	
			if (cursor.getInt(hasatt_index) == 1) {
				item.hasatt = true;
			} else {
				item.hasatt = false;
			}
	
			// if(cursor.getInt(hasfeedback_index) == 1){
			// item.hasNewFeedback = true;
			// }else{
			// item.hasNewFeedback = false;
			// }
	
			item.body = cursor.getString(event_body_index) == null ? "" : cursor.getString(event_body_index);
			if (event_cleanbody_index > 0) {
				item.cleanbody = cursor.getString(event_cleanbody_index);
			}
			item.isread = isReadOrBookmark(cursor.getInt(isread_index));
			item.bookmark = isReadOrBookmark(cursor.getInt(bookmark_index));
			item.ifeshow = isReadOrBookmark(cursor.getInt(ifeshow_index));
			item.user = cursor.getString(user_index);
	
			List<Imags> ImageItems = new ArrayList<Imags>();
			ImageItems = retrieveSendImagesItem(item);
			if (!ImageItems.isEmpty()) {
				item.imgs = ImageItems;
			}
	
			List<Replyer> replyItems = new ArrayList<Replyer>();
			replyItems = retrieveSendReplyerItem(item);
			if (!replyItems.isEmpty())
				item.listReplyer = replyItems;
		}catch (CursorIndexOutOfBoundsException e) {}
		
		return item;
	}

	// add by JiangBo 2012-08-08 取回复人
	public synchronized List<Replyer> retrieveReplyerItem(EventDetailsItem item) {
		List<Replyer> replyItems = new ArrayList<Replyer>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_ASWORG_TABLE, null, "event_id = ? and user = ?", whereArgs(item), null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int replyid_index = cursor.getColumnIndex(ASW_REPLYERID);
				int replyname_index = cursor.getColumnIndex(ASW_REPLYERNAME);
				int replycount_index = cursor.getColumnIndex(ASW_REPLYCOUNT);
				int replylasttime_index = cursor.getColumnIndex(ASW_REPLYLASTTIME);
				int lastawstimeread_index = cursor.getColumnIndex(LASTAWSTIMEREAD);

				Replyer reply = new Replyer(cursor.getString(replyid_index), cursor.getString(replyname_index), cursor.getString(replycount_index), cursor.getString(replylasttime_index),
						cursor.getString(lastawstimeread_index) == null ? "0" : cursor.getString(lastawstimeread_index));
				
				reply.memberList = retrieveMemberlistItem(reply);
				
				replyItems.add(reply);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return replyItems;
	}

	// add by JiangBo 发送表 取回复人
	public List<Replyer> retrieveSendReplyerItem(EventDetailsItem item) {
		List<Replyer> replyItems = new ArrayList<Replyer>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_SEND_ASWORG_TABLE, null, "event_id = ? and user = ?", whereArgs(item), null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int replyid_index = cursor.getColumnIndex(ASW_REPLYERID);
				int replyname_index = cursor.getColumnIndex(ASW_REPLYERNAME);
				int replycount_index = cursor.getColumnIndex(ASW_REPLYCOUNT);
				int replylasttime_index = cursor.getColumnIndex(ASW_REPLYLASTTIME);
				int lastawstimeread_index = cursor.getColumnIndex(LASTAWSTIMEREAD);

				Replyer reply = new Replyer(cursor.getString(replyid_index), cursor.getString(replyname_index), cursor.getString(replycount_index), cursor.getString(replylasttime_index),
						cursor.getString(lastawstimeread_index));
				
				reply.memberList = retrieveMemberlistItem(reply);
				
				replyItems.add(reply);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return replyItems;
	}

	/**
	 * 从传入的item中得到得item属性才能拿到List<Imags>
	 * 
	 * @param item
	 * @return
	 */
	public List<Imags> retrieveSendImagesItem(EventDetailsItem item) {
		List<Imags> ImageItems = new ArrayList<Imags>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_SEND_IMG_TABLE, null, "event_id = ? and user = ?", whereArgs(item), null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int url_index = cursor.getColumnIndex(URL);
				int description_index = cursor.getColumnIndex(DESCRIPTION);
				int att_id_index = cursor.getColumnIndex(ATTID);
				int att_preview_index = cursor.getColumnIndex(ATT_PREVIEW);
				int att_size_index = cursor.getColumnIndex(ATT_SIZE);
				int att_name_index = cursor.getColumnIndex(ATT_NAME);
				int att_filetype_index = cursor.getColumnIndex(IMAGE_TYPE);
				Imags image = new Imags(cursor.getString(url_index), cursor.getString(description_index), cursor.getInt(att_id_index));
				image.preview = cursor.getString(att_preview_index);
				image.size = cursor.getString(att_size_index);
				image.attname = cursor.getString(att_name_index);
				image.fileType = cursor.getString(att_filetype_index);
				ImageItems.add(image);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return ImageItems;
	}

	public List<Imags> getSendImagesList(int eventID, String name) {
		List<Imags> ImageItems = new ArrayList<Imags>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_SEND_IMG_TABLE, null, "event_id = ? and user = ?", new String[] { Integer.toString(eventID), name }, null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int url_index = cursor.getColumnIndex(URL);
				int description_index = cursor.getColumnIndex(DESCRIPTION);
				int att_id_index = cursor.getColumnIndex(ATTID);
				Imags image = new Imags(cursor.getString(url_index), cursor.getString(description_index), cursor.getInt(att_id_index));
				image.setImgData(retrieveAttachmentBytes(cursor.getString(url_index)));
				ImageItems.add(image);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return ImageItems;
	}

	// ------------------------------------草稿-------------------------------------
	/**
	 * 保存草稿
	 * 
	 * @param eventDraftItem
	 * @return
	 */
	public synchronized int saveEventsDraftItem(EventDraftItem eventDraftItem) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(PK_ID, eventDraftItem.pk_id);
		eventsValues.put(BODY_EVENT_ID, eventDraftItem.old_eventid);
		eventsValues.put(OWNER_ID, eventDraftItem.ownerid);
		eventsValues.put(OWNER, eventDraftItem.owner);
		eventsValues.put(TYPE, eventDraftItem.type);
		// eventsValues.put(OBJTYPE, eventDraftItem.objtype);
		eventsValues.put(ORI_STATUS, eventDraftItem.oristatus);
		if (eventDraftItem.save == null) {
			eventsValues.put(SAVETIME, new Date().getTime());
		} else {
			eventsValues.put(SAVETIME, eventDraftItem.save.getTime());
		}
		if (eventDraftItem.start == null) {
			eventsValues.put(START, 0);
		} else {
			eventsValues.put(START, eventDraftItem.start.getTime());
		}
		if (eventDraftItem.end == null) {
			eventsValues.put(END, 0);
		} else {
			eventsValues.put(END, eventDraftItem.end.getTime());
		}
		eventsValues.put(SHORT_LOC, eventDraftItem.shortloc);
		eventsValues.put(EVENT_TITLE, eventDraftItem.title);
		eventsValues.put(EVENT_BODY, eventDraftItem.body);
		eventsValues.put(USER, eventDraftItem.user);
		eventsValues.put(IFESHOW, eventDraftItem.bifeshow);
		eventsValues.put(HASATT, eventDraftItem.isAttc);
		
		eventsValues.put(TYPE_TITLEID, eventDraftItem.type_titleId);//添加了通告标题字段 by hzy
		
		int pk_id = 0;
		if (eventDraftItem.pk_id == null) {
			try {
				long count = db.insert(EVENT_DRAFT_TABLE, null, eventsValues);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			String sql = " select max(pk_id) from myevents_draft where user = ? ";
			Cursor cursor = db.rawQuery(sql, new String[] { eventDraftItem.user });
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				pk_id = cursor.getInt(0);
				cursor.moveToNext();
			}
			cursor.close();
		} else {
			pk_id = eventDraftItem.pk_id;
			int i = db.update(EVENT_DRAFT_TABLE, eventsValues, " pk_id = ? and user = ? ", whereEventDraftArgs(eventDraftItem));
			db.delete(EVENT_DRAFT_IMG_TABLE, "pk_id = ? and user = ? ", whereEventDraftArgs(eventDraftItem));
			db.delete(EVENTID_TO_RECIVEID_TABLE, "pk_id = ? and user = ? ", whereEventDraftArgs(eventDraftItem));
			System.out.println("更新草稿>>>>" + i);
		}

		// if(eventDraftExists(eventDraftItem.pk_id,eventDraftItem.user)){
		// System.out.println("更新>>>>" ) ;
		// int i = db.update(EVENT_DRAFT_TABLE, eventsValues,
		// " pk_id = ? and user = ? ", whereEventDraftArgs(eventDraftItem));
		// db.delete(EVENT_DRAFT_IMG_TABLE, "pk_id = ? and user = ?",
		// whereEventDraftArgs(eventDraftItem)) ;
		// db.delete(EVENTID_TO_RECIVEID_TABLE, "pk_id = ? and user = ?",
		// whereEventDraftArgs(eventDraftItem)) ;
		// System.out.println(i ) ;
		// }else{
		// try{
		// long count = db.insert(EVENT_DRAFT_TABLE, null, eventsValues) ;
		// System.out.println("插入行返回值>>>>" + count) ;
		// }catch(Exception e){
		// System.out.println(e.getMessage());
		// }
		// }

		// 插入附件
		System.out.println("还是保存附件数>>" + eventDraftItem.eventDraftAttachment.size());
		System.out.println("PK_ID：" + pk_id);
		if (!eventDraftItem.eventDraftAttachment.isEmpty()) {
			ContentValues imageValues = new ContentValues();
			for (int i = 0; i < eventDraftItem.eventDraftAttachment.size(); i++) {
				imageValues.put(PK_ID, pk_id);
				// imageValues.put(CRC_ID, eventDraftImage.fileId);
				// imageValues.put(IMAGE_TYPE, eventDraftImage.fileType);
				// imageValues.put(IMAGE_BYTE_ARRAY,eventDraftImage.data);
				imageValues.put(DESCRIPTION, eventDraftItem.eventDraftAttachment.get(i).description);
				imageValues.put(CRC_ID, eventDraftItem.eventDraftAttachment.get(i).fileId);
				imageValues.put(IMAGE_TYPE, eventDraftItem.eventDraftAttachment.get(i).fileType);
				imageValues.put(IMAGE_BYTE_ARRAY, eventDraftItem.eventDraftAttachment.get(i).data);
				imageValues.put(ATT_TYPE, eventDraftItem.eventDraftAttachment.get(i).type);
				imageValues.put(ATT_ITEM, eventDraftItem.eventDraftAttachment.get(i).item);
				imageValues.put(USER, eventDraftItem.user);
				
				db.insert(EVENT_DRAFT_IMG_TABLE, EVENT_ID, imageValues);
			}

			// for (Map.Entry<String, Attachment> mAttachmentMap :
			// eventDraftItem.mAttachmentMap.entrySet()) {
			// // 获取到pk_id
			// imageValues.put(PK_ID, pk_id);
			// // imageValues.put(CRC_ID, eventDraftImage.fileId);
			// // imageValues.put(IMAGE_TYPE, eventDraftImage.fileType);
			// // imageValues.put(IMAGE_BYTE_ARRAY,eventDraftImage.data);
			// imageValues.put(DESCRIPTION,
			// mAttachmentMap.getValue().description);
			// imageValues.put(CRC_ID, mAttachmentMap.getValue().fileId);
			// imageValues.put(IMAGE_TYPE, mAttachmentMap.getValue().fileType);
			// imageValues.put(IMAGE_BYTE_ARRAY,mAttachmentMap.getValue().data);
			// imageValues.put(USER, eventDraftItem.user);
			// db.insert(EVENT_DRAFT_IMG_TABLE, EVENT_ID, imageValues);
			// }

		}

		// //插入收件人Item
		// if(!eventDraftItem.receiverList.isEmpty()){
		// ContentValues orgTypeValues = new ContentValues();
		// for(OrganizationItem organizationItem:eventDraftItem.receiverList ){
		// //获取到pk_id
		// orgTypeValues.put(PK_ID, pk_id) ;
		// orgTypeValues.put(ORG_ID, organizationItem.orgID) ;
		// orgTypeValues.put(ORG_NAME, organizationItem.orgName) ;
		// orgTypeValues.put(ORG_TYPE, organizationItem.orgType) ;
		// orgTypeValues.put(USER, eventDraftItem.user) ;
		// db.insert(EVENTID_TO_RECIVEID_TABLE, EVENT_ID, orgTypeValues) ;
		// }
		//
		// }
		return pk_id;

	}

	private String[] whereEventDraftArgs(EventDraftItem eventDraftItem) {
		return new String[] { Integer.toString(eventDraftItem.pk_id), eventDraftItem.user };
	}

	// private boolean eventDraftExists(Integer pk_id, String user) {
	// // TODO Auto-generated method stub
	//
	// SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
	//
	// Cursor result = db.query(EVENT_DRAFT_TABLE, new String[] {PK_ID},
	// " pk_id = ? and user = ? ",
	// new String[] {Integer.toString(pk_id),user},null, null, null);
	//
	// boolean storyExists = (result.getCount() > 0);
	// result.close();
	// return storyExists;
	// }
	// 删除草稿
	public void deleteEventsDraftItem(int pk_id, String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		db.delete(EVENT_DRAFT_TABLE, " pk_id = ? and user = ? ", new String[] { Integer.toString(pk_id), user });
	}

	// //更新草稿
	// public void updateEventsDraftItem(EventDraftItem eventDraftItem){
	// saveEventsDraftItem(eventDraftItem) ;
	// }

	// 查询所有草稿 //event主界面要用到
	public Cursor getAllEventsDraftCursor(String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(EVENT_DRAFT_TABLE, null, " user = ? ", new String[] { user }, null, null, SAVETIME + " DESC", null);
		System.out.println("草稿查询结果数>>>>" + cursor.getCount());
		return cursor;
	}

	/**
	 * 取一条草稿详细
	 * 
	 * @param pk_id
	 * @param user
	 * @return
	 */
	public EventDraftItem getEventDraftItemById(int pk_id, String user) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(EVENT_DRAFT_TABLE, null, " pk_id = ? and user = ? ", new String[] { Integer.toString(pk_id), user }, null, null, null, null);
		cursor.moveToFirst();

		EventDraftItem i = retrieveEventDraftItem(cursor, true);
		cursor.close();
		return i;
	}

	/**
	 * 单独解析为单个EventDraftItem数据
	 * 
	 * @param getAttc
	 *            是否取附件
	 */
	public EventDraftItem retrieveEventDraftItem(Cursor cursor, boolean getAttc) {
		int pk_id_index = cursor.getColumnIndex(PK_ID);
		int body_event_id_index = cursor.getColumnIndex(BODY_EVENT_ID);
		int owner_id_index = cursor.getColumnIndex(OWNER_ID);
		int owner_index = cursor.getColumnIndex(OWNER);
		int type_index = cursor.getColumnIndex(TYPE);
		// int objtype_index = cursor.getColumnIndex(OBJTYPE);
		int orievent_id_index = cursor.getColumnIndex(ORIEVENT_ID);
		int ori_status_index = cursor.getColumnIndex(ORI_STATUS);
		int savetime_index = cursor.getColumnIndex(SAVETIME);
		int start_index = cursor.getColumnIndex(START);
		int end_index = cursor.getColumnIndex(END);
		int short_loc_index = cursor.getColumnIndex(SHORT_LOC);
		int location_index = cursor.getColumnIndex(LOCATION);
		int event_title_index = cursor.getColumnIndex(EVENT_TITLE);
		int event_body_index = cursor.getColumnIndex(EVENT_BODY);
		int event_ifeshow_index = cursor.getColumnIndex(IFESHOW);
		int user_index = cursor.getColumnIndex(USER);
		int isattc_index = cursor.getColumnIndex(HASATT);
		int type_titleid_index = cursor.getColumnIndex(TYPE_TITLEID);

		EventDraftItem item = new EventDraftItem();

		item.pk_id = cursor.getInt(pk_id_index);
		item.old_eventid = cursor.getInt(body_event_id_index) == -1?null:cursor.getInt(body_event_id_index);
		item.ownerid = cursor.getString(owner_id_index);
		item.owner = cursor.getString(owner_index);
		item.type = cursor.getInt(type_index);
		// item.objtype = cursor.getInt(objtype_index) ;
		item.oristatus = cursor.getString(ori_status_index);
		item.save = new Date(cursor.getLong(savetime_index));
		item.start = new Date(cursor.getLong(start_index));
		item.end = new Date(cursor.getLong(end_index));
		item.shortloc = cursor.getString(short_loc_index);
		// item. = cursor.getInt(location_index) ;
		item.title = cursor.getString(event_title_index);
		item.body = cursor.getString(event_body_index) == null ? "" : cursor.getString(event_body_index);
		item.user = cursor.getString(user_index);
		item.bifeshow = cursor.getInt(event_ifeshow_index) == 0 ? false : true;
		item.isAttc = cursor.getInt(isattc_index) == 0 ? false : true;
		item.type_titleId = cursor.getString(type_titleid_index);
		// Map<String,Attachment> eventDraftimages = new
		// HashMap<String,Attachment>() ;
		// List<OrganizationItem> receiverList = new
		// ArrayList<OrganizationItem>() ;
		//
		// eventDraftimages = retrieveEventImages(item);
		// if(!eventDraftimages.isEmpty()){
		// item.mAttachmentMap = eventDraftimages ;
		// }
		// receiverList = retrieveEventReceiverList(item) ;
		// if(!receiverList.isEmpty()){
		// item.receiverList = receiverList ;
		// }
		if (getAttc) {
			item.eventDraftAttachment = retrieveEventImages(item);
		}
		return item;
	}

	// private List<OrganizationItem> retrieveEventReceiverList(
	// EventDraftItem item) {
	// List<OrganizationItem> recevierList =new ArrayList<OrganizationItem>() ;
	// SQLiteDatabase db = eventsDBHelper.getReadableDatabase() ;
	// Cursor cursor = db.query(EVENTID_TO_RECIVEID_TABLE, null,
	// " pk_id = ? and user = ?", whereEventDraftArgs(item), null, null, null) ;
	// if(cursor.moveToFirst()) {
	// while(!cursor.isAfterLast()) {
	// int org_id_index = cursor.getColumnIndex(ORG_ID);
	// int org_name_index = cursor.getColumnIndex(ORG_NAME);
	// int org_type_index = cursor.getColumnIndex(ORG_TYPE);
	// OrganizationItem orgItem = new
	// OrganizationItem(cursor.getInt(org_id_index),cursor.getString(org_name_index),cursor.getString(org_type_index));
	// recevierList.add(orgItem) ;
	// cursor.moveToNext();
	// }
	// cursor.close();
	// }
	//
	// return recevierList;
	// }

	/**
	 * 取草稿附件
	 */
	private ArrayList<Attachment> retrieveEventImages(EventDraftItem item) {

		// Map<String,Attachment> EventDraftImage = new
		// HashMap<String,Attachment>() ;
		ArrayList<Attachment> eventDraftAttachmentList = new ArrayList<EventDraftItem.Attachment>();
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor cursor = db.query(EVENT_DRAFT_IMG_TABLE, null, " pk_id = ? and user = ?", whereEventDraftArgs(item), null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int crcid_index = cursor.getColumnIndex(CRC_ID);
				int imagetype_index = cursor.getColumnIndex(IMAGE_TYPE);
				int image_byte_array_index = cursor.getColumnIndex(IMAGE_BYTE_ARRAY);
				int image_description_index = cursor.getColumnIndex(DESCRIPTION);
				int att_item_index = cursor.getColumnIndex(ATT_ITEM);
				int att_type_index = cursor.getColumnIndex(ATT_TYPE);
				
				byte[] data = cursor.getBlob(image_byte_array_index) == null? new byte[1]:cursor.getBlob(image_byte_array_index);
				String crcid = cursor.getString(crcid_index);
				String imagetype = cursor.getString(imagetype_index);
				String imagedescription = cursor.getString(image_description_index);
				String attitem = cursor.getString(att_item_index);
				String atttype = cursor.getString(att_type_index);
				Attachment image = new Attachment(data,crcid ,imagetype ,imagedescription ,attitem ,atttype );
				// EventDraftImage.put(cursor.getString(crcid_index), image) ;
				eventDraftAttachmentList.add(image);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return eventDraftAttachmentList;
	}

	public void addAutoReplyItem(Integer id, String name) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues eventsValues = new ContentValues();
		eventsValues.put(BODY_EVENT_ID, id);
		eventsValues.put(USER, name);
		try {
			long count = db.insert(EVENT_AUTOREPLY_TABLE, null, eventsValues);
			System.out.println("插已读id返回值>>>>" + count);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void delAutoReplyItem(Integer id, String name) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		try {
			long count = db.delete(EVENT_AUTOREPLY_TABLE, BODY_EVENT_ID + " = ? and " + USER + " = ? ", new String[] { String.valueOf(id), name });
			System.out.println("删除返回值>>>>" + count);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 批量删除自动回复id
	 * 
	 * @param name
	 */
	synchronized public void delAutoReplyItem(String name) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		try {
			long count = db.delete(EVENT_AUTOREPLY_TABLE, USER + " = ? ", new String[] { name });
			System.out.println("批量删除返回值>>>>" + count);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// public List<Integer> getAutoReply(String name){
	// List<Integer> list = new ArrayList<Integer>();
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// Cursor cursor ;
	// cursor = db.rawQuery("select * from " +EVENT_AUTOREPLY_TABLE +
	// " WHERE user = ?" , new String[]{name}) ;
	// if(cursor.moveToFirst()) {
	// while(!cursor.isAfterLast()) {
	// int id =cursor.getInt(cursor.getColumnIndex(BODY_EVENT_ID));
	// list.add(id);
	// cursor.moveToNext();
	// }
	// cursor.close();
	// }
	// return list;
	// }

	/**
	 * 清除所有通告相关
	 * 
	 * @param 无
	 */
	public void clearAllEvents() {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		db.delete(EVENT_TABLE, null, null);
		db.delete(EVENT_IMG_TABLE, null, null);
		db.delete(NOTICE_TYPE_TABLE, null, null);
		db.delete(EVENT_SEND_TABLE, null, null);
		db.delete(EVENT_SEND_IMG_TABLE, null, null);
		db.delete(EVENT_DRAFT_TABLE, null, null);
		db.delete(EVENT_DRAFT_IMG_TABLE, null, null);
		db.delete(EVENTID_TO_RECIVEID_TABLE, null, null);

	}

	/**
	 * 清除离线通告
	 * 
	 * @param name
	 *            用户ID
	 */
	public void clearAllLinkoutEvent(String name) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		db.delete(EVENT_TABLE, "user = ? and " + BOOKMARKED + "=0", new String[] { name });
		db.delete(EVENT_IMG_TABLE, "user = ? ", new String[] { name });
		// db.delete(NOTICE_TYPE_TABLE, "user = ? ", new String[]{name}) ;
		db.delete(EVENT_SEND_TABLE, "user = ? ", new String[] { name });
		db.delete(EVENT_SEND_IMG_TABLE, "user = ? ", new String[] { name });
	}

	/**
	 * 从已发跳转到编辑界面所要查找到得已发Item ；
	 * 
	 * @param id
	 *            已发通告id
	 * @param name
	 *            用户名字
	 * @return
	 */
	// public EventDetailsItem findAlreadySendEventById(int id ,String name){
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// Cursor cursor = db.query(EVENT_SEND_TABLE, null, "_id = ? and user = ? ",
	// new String[]{Integer.toString(id),name}, null, null, null) ;
	// cursor.moveToFirst() ;
	// return send2DraftDetailItem(cursor) ;
	// }
	/**
	 * 校园通告最大校园事件发布时间
	 * 
	 * @param name
	 *            用户id
	 * @return 客户端当前最大校园事件发布时间
	 */
	public String getLastPostDate(String name) {
		String curpost = "0";
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		String sql = " select max(post) from myevents where user = ? ";
		Cursor cursor = db.rawQuery(sql, new String[] { name });
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			curpost = cursor.getString(0);
			cursor.moveToNext();
		}
		return curpost;
	}

	/**
	 * 校园已发通告最大校园事件发布时间
	 * 
	 * @param name
	 *            用户id
	 * @return 客户端当前最大校园事件发布时间
	 */
	public String getAlreadySendLastPostDate(String name) {
		String curpost = "0";
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		String sql = " select max(post) from myevents_send where user = ? ";
		Cursor cursor = db.rawQuery(sql, new String[] { name });
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			curpost = cursor.getString(0);
			cursor.moveToNext();
		}
		return curpost;
	}

	/**
	 * 
	 * @param url
	 *            附件的url
	 * @return 附件byte[]
	 */
	public byte[] retrieveAttachmentBytes(String url) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		// Cursor cursor = db.query(ATTACHMENT_CACHE_TABLE,new
		// String[]{ATTACHMENT_DATE} ," unique_id = ? ", new
		// String[]{unique_id}, null, null, null, null);
		Cursor cursor = db.rawQuery("select * from " + ATTACHMENT_CACHE_TABLE + " where url =  " + "'" + url + "'", null);

		byte[] attachmentBytes = null;
		int attachment_date_index = cursor.getColumnIndex(ATTACHMENT_DATE);
		if (cursor.moveToFirst()) {
			attachmentBytes = cursor.getBlob(attachment_date_index);
		}
		cursor.close();

		return attachmentBytes;
	}

	public synchronized void saveAttachment(String url, String description, byte[] attachmentBytes, String fileType, int statusCode, long contentLength, long lastmodifyTime) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();

		ContentValues attachmentValue = new ContentValues();
		attachmentValue.put(URL, url);
		attachmentValue.put(ATTACHMENT_DATE, attachmentBytes);
		attachmentValue.put(MIMETYPE, fileType);
		attachmentValue.put(HTTPSTATUS, statusCode);
		attachmentValue.put(CONTENTLENGTH, contentLength);
		attachmentValue.put(LASTMODIFY, lastmodifyTime);
		attachmentValue.put(DESCRIPTION, description);

		long i = db.insert(ATTACHMENT_CACHE_TABLE, URL, attachmentValue);
		System.out.println("保存的附件缓存数>>>>" + i);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// synchronized public void updateEventSendReadStatus(int id ,int
	// readCount,String name) {
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// ContentValues eventSendValue = new ContentValues();
	// eventSendValue.put(READCOUNT, readCount) ;
	// int i = db.update(EVENT_SEND_TABLE, eventSendValue,
	// "_id = ? and user = ?", new String[]{Integer.toString(id),name}) ;
	// System.out.println("更新已读人数条数>>>>" + i + "/更新已读人数" + readCount) ;
	// }
	// synchronized public int getEventSendReadStatus(int id, String name) {
	// int count = 0;
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// Cursor cursor = db.query(EVENT_SEND_TABLE, null, "_id = ? and user = ? ",
	// new String[]{Integer.toString(id),name}, null, null, null) ;
	// if(null != cursor){
	// cursor.moveToFirst();
	// count = cursor.getInt(cursor.getColumnIndex(READCOUNT));
	// }
	// return count;
	// }
	/**
	 * 取得所有已阅读但未成功反馈到服务器的通告id
	 * 
	 * @param user
	 * @return
	 */
	public List<Integer> getNoSuccessReplyIdList(String user) {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ArrayList<Integer> list = new ArrayList<Integer>();
		Cursor cursor = db.query(EVENT_AUTOREPLY_TABLE, null, USER + " = ? ", new String[] { user }, null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int id = cursor.getInt(cursor.getColumnIndex(BODY_EVENT_ID));
				list.add(id);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return list;
	}

	// private List<Attachment> getSendAttachment(List<Imags> imageList){
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// List<Attachment> attachmentList = new ArrayList<Attachment>() ;
	// if(!imageList.isEmpty()){
	// for(Imags image :imageList){
	// String url = image.URL ;
	// Cursor cursor = db.rawQuery("select * from "+ ATTACHMENT_CACHE_TABLE
	// +" where url =  " + "'" + url + "'", null);
	// byte[] attachmentBytes = null;
	// String attachmentType ;
	// int attachment_date_index = cursor.getColumnIndex(ATTACHMENT_DATE);
	// int attachment_type_index = cursor.getColumnIndex(MIMETYPE);
	// int attachment_desc_index = cursor.getColumnIndex(DESCRIPTION);
	// if(cursor.moveToFirst()) {
	// attachmentBytes = cursor.getBlob(attachment_date_index);
	// attachmentType = cursor.getString(attachment_type_index) ;
	// String fileId = GlobalVariables.calcCrc(attachmentBytes);
	// String description = cursor.getString(attachment_desc_index) ;
	// Attachment attachment = new
	// Attachment(attachmentBytes,fileId,attachmentType,description) ;
	// attachmentList.add(attachment) ;
	// }
	// cursor.close();
	//
	// }
	// }
	//
	// return attachmentList ;
	// }

	public boolean isAllattachmentDownload(EventDetailsItem item) {
		boolean flag = false;
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		String s = "";
		for (Imags url : item.imgs) {
			String u = url.URL;
			s += "'" + u + "',";
		}
		s = s.substring(0, s.length() - 1);
		Cursor cursor = db.rawQuery("select * from " + ATTACHMENT_CACHE_TABLE + " where " + URL + " in (" + s + ")", null);
		System.out.println("查询是否全部附件已经下载SQL>>>" + "select * from " + ATTACHMENT_CACHE_TABLE + " where " + URL + " in[" + s + "]");
		if (cursor.getCount() == item.imgs.size()) {
			flag = true;
		}
		cursor.close();
		return flag;
	}

	public boolean isAttachmentExists(String url) {
		boolean flag = false;
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT url FROM attachment_cache WHERE url =  " + "'" + url + "'", null);
		if (cursor.getCount() == 1) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除不在cursor范围内的附件数据
	 * 
	 * @param cursor
	 */
	public void deleteAccachments() {
		Cursor cursor = getExistsUrl();
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		String urlList = "";
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				urlList += "'" + cursor.getString(cursor.getColumnIndex("url")) + "',";
				cursor.moveToNext();
			}
		} else {
			db.delete(ATTACHMENT_CACHE_TABLE, null, null);
		}
		if (urlList.length() > 0) {
			urlList = urlList.substring(0, urlList.length() - 1);
			db.delete(ATTACHMENT_CACHE_TABLE, " url NOT IN (" + urlList + ")", null);
		}

	}

	public Date getLatestUpdateTime(String user_name, int category_id) {
		Date lastUpdate = new Date(0);
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor result = db.query(EVENT_TABLE, new String[] { LASTUPDATE }, "user = '" + user_name + "' and type = " + category_id, null, null, null, LASTUPDATE + " DESC");
		if (result.moveToFirst()) {
			int last_update_index = result.getColumnIndex(LASTUPDATE);
			lastUpdate = new Date(result.getLong(last_update_index));
		}
		result.close();
		return lastUpdate;
	}

	/**
	 * 取收藏最后更新时间
	 * @param username
	 * @return
	 */
	public String getLastupdatetimeFav(String username){
		String lastfavtime = "0";
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor result = db.query(EVENT_TABLE, new String[] { LASTUPDATE_FAV }, "user = '" + username + "'" , null, null, null, LASTUPDATE_FAV + " DESC");
		if (result.moveToFirst()) {
			int last_update_index = result.getColumnIndex(LASTUPDATE_FAV);
			lastfavtime = result.getString(last_update_index) == null?"0":result.getString(last_update_index);
		}
		result.close();
		return lastfavtime;
	}
	
	private Cursor getExistsUrl() {
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT url FROM " + EVENT_SEND_IMG_TABLE + " UNION  SELECT url FROM " + EVENT_IMG_TABLE, null);
		System.out.println("总共有url数>>" + cursor.getCount());
		return cursor;
	}

	// --------------------收藏的通告(接收)------------
	/**
	 * 
	 * @param user
	 *            用户名字
	 * @return
	 */
	public Cursor getBookmarkEventCursor(String name) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		String joinQuery;
		joinQuery = SQLiteQueryBuilder.buildQueryString(false, EVENT_TABLE, null, "user = '" + name + "' and bookmarked = 1", null, null, LASTUPDATE + " DESC", null);

		System.out.println("joinQuery>>>" + joinQuery);
		return db.rawQuery(joinQuery, null);
	}

	// --------------------查找通告--------------------
	/**
		 * 
		 */
	public Cursor getSearchEventCursor(String name, String searchkey) {
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();

		String joinQuery;
		joinQuery = SQLiteQueryBuilder.buildQueryString(false, 
				EVENT_TABLE, 
				null, 
				"user = '" + name + "' and title LIKE '%" + searchkey + "%'", 
				null, null, 
				POST + " DESC", 
				null);

		System.out.println("joinQuery>>>" + joinQuery);
		return db.rawQuery(joinQuery, null);
	}

	/**
	 * 最近一条被读时间
	 * @param storyId
	 * @return 
	 */
	public Date getLastReadtime(String user_name, int category_id) {
		Date lastReaddate = new Date(0);
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		Cursor result = db.query(EVENT_TABLE, new String[] { EVENT_LAST_READTIME }, "user = '" + user_name + "' and type = " + category_id, null, null, null, EVENT_LAST_READTIME + " DESC");
		if (result.moveToFirst()) {
			int last_update_index = result.getColumnIndex(EVENT_LAST_READTIME);
			lastReaddate = new Date(result.getLong(last_update_index));
		}
		result.close();
		return lastReaddate;
	}
	
	
	// --------------------离线阅读记录----------------
	// /**
	// * 保存离线阅读记录
	// * (提供外部调用)
	// */
	// public void addOfflineReadRecord(EventDetailsItem item){
	// ContentValues eventsValues = new ContentValues();
	// eventsValues.put(EVENT_ID, item.eventid);
	// eventsValues.put(USER, item.user);
	//
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// db.insert(EVENT_OFFLINEREAD_TABLE, null, eventsValues) ;
	// }
	//
	// /**
	// * 取离线阅读记录id列表
	// * (提供外部调用)
	// * @param username
	// * @return
	// */
	// public List<String> getOfflineReadIds(String username){
	// List<String> ids = new ArrayList<String>();
	//
	// Cursor c = getOfflineReadCursor(username);
	// c.requery();
	// if(c.moveToFirst()){
	// while(c.isAfterLast()){
	// ids.add(retrieveOfflineReadId(c));
	// c.moveToNext();
	// }
	// }
	// c.close();
	//
	// return ids;
	// }
	//
	// /**
	// * 清空离线阅读记录
	// * (提供外部调用)
	// * @param username
	// */
	// public void delOfflineReadRecord(String username){
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// db.delete(EVENT_TABLE, USER + " = '" + username + "'", null);
	// }
	//
	// /**
	// * 取离线阅读记录Cursor
	// */
	// private Cursor getOfflineReadCursor(String username){
	// SQLiteDatabase db = eventsDBHelper.getWritableDatabase() ;
	// String joinQuery =
	// SQLiteQueryBuilder.buildQueryString(false,EVENT_OFFLINEREAD_TABLE,null,"user = '"
	// + username + "'",null,null,null,null);
	// return db.rawQuery(joinQuery, null) ;
	// }
	//
	// /**
	// * 解析线阅读记录Cursor
	// * @param cursor
	// * @return
	// */
	// private String retrieveOfflineReadId(Cursor cursor){
	// int event_id_index = cursor.getColumnIndex(EVENT_ID);
	// return cursor.getString(event_id_index);
	// }
	
	
	//以下三个函数是对EventsTypeLanguage表的操作。 ①保存通告标题
	public void saveEventsTypeLanguage(EventsTypeLanguage types){
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TYPE_INDEX, types.type_index);
		contentValues.put(TYPE_EVENT, types.type_event);
		contentValues.put(TYPE_CHINESE, types.type_chinese);
		contentValues.put(TYPE_CHINESE_CHARACTER, types.type_chinese_character);
		contentValues.put(TYPE_ENGLISH, types.type_english);
		int i = (int) db.insert(EVENTS_TYPE_LANGUAGE, null, contentValues);
		if (i>0){
			Log.i("EventsTypeLanguage", "保存成功");
		}else {
			Log.i("EventsTypeLanguage", "保存失败");
		}
	}
	
	//②删除通告标题表内容
	public void deleteEventsTypeLanguageTable(){
		SQLiteDatabase db = eventsDBHelper.getWritableDatabase();
		int i  = db.delete(EVENTS_TYPE_LANGUAGE,  null, null);
		if (i>0){
			Log.i("EventsTypeLanguage", "删除表数据成功");
		}else {
			Log.i("EventsTypeLanguage", "删除表失败或者没记录可删");
		}
	}
	//③获取通告标题列表
	public List<EventsTypeLanguage> findEventsTypeLanguage(CurrentLan l,int eventtype){
		SQLiteDatabase db = eventsDBHelper.getReadableDatabase();
		List<EventsTypeLanguage> list = new ArrayList<EventsTypeLanguage>();
		Cursor cursor = db.query(EVENTS_TYPE_LANGUAGE, 
				null,
				TYPE_EVENT+" = '" + eventtype + "'"  , 
				null, null, null,
				null);
		
		if (cursor.moveToFirst()) {
			
			while (!cursor.isAfterLast()) {
				EventsTypeLanguage type = new EventsTypeLanguage();
				type.type_index = cursor.getString(cursor.getColumnIndex(TYPE_INDEX));
				if(l == CurrentLan.SIMPLIFIED_CHINESE){
					type.type_chinese = cursor.getString(cursor.getColumnIndex(TYPE_CHINESE));
				}else if (l==CurrentLan.COMPLES_CHINESE) {
					type.type_chinese_character=cursor.getString(cursor.getColumnIndex(TYPE_CHINESE_CHARACTER));
				}else if (l==CurrentLan.ENGLISH) {
					type.type_english = cursor.getString(cursor.getColumnIndex(TYPE_ENGLISH));
				}
				list.add(type);
				cursor.moveToNext();
			}
			cursor.close();// 游标关闭
		}
		
		return list;
		
	}
}
