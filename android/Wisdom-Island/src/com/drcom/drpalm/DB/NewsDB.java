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

import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm.objs.NewsItem.Image;

public class NewsDB {
	public static final int TOP_NEWS = 0;

	private static final String STORIES_TABLE = "stories";
	private static final String CATEGORIES_TABLE = "categories";
	private static final String IMAGE_URLS_TABLE = "image_urls";

	// story table field names
	private static final String STORY_ID = "_id"; // story_id
	private static final String TITLE = "title"; // title
	private static final String BODY = "body"; // content
	private static final String AUTHOR = "author"; // author
	private static final String SUMMARY = "summary"; // abstract
	private static final String POST_DATE = "post_date"; // postdate
	private static final String THUMB_URL = "thumb_url"; // thumburl
	private static final String STATUS = "status"; // status
	private static final String SMALL_URL = "small_url"; // smallurl 封面图
	private static final String FULL_URL = "full_url"; // fullurl 封面图
	private static final String LAST_UPDATE = "last_update"; // lastupdate
	private static final String BOOKMARKED = "bookmarked";
	private static final String ISREAD = "isread";
	
	private static final String SHARE_URL = "shareurl";  //分享URL

	// category table field names
	private static final String CATEGORY = "category";

	// image table field names
	private static final String IMAGE_STORY_ID = "story_id";
	private static final String IMAGE_CAPTION = "image_caption"; // caption

	// where cause
	private static final String STORY_ID_WHERE = STORY_ID + "=?";
	private static final String CATEGORY_WHERE = STORY_ID + "=?" + " AND " + CATEGORY + "=?";

	DatabaseHelper mNewsDBHelper;

	private static NewsDB newsDBInstance = null;

	// 多学校处理
	private String schoolkey = "";

	public static NewsDB getInstance(Context context, String key) {
		if (newsDBInstance == null || !newsDBInstance.schoolkey.equals(key)) {
			newsDBInstance = new NewsDB(context, key);
			newsDBInstance.schoolkey = key;
			return newsDBInstance;
		} else {
			return newsDBInstance;
		}
	}

	private NewsDB(Context context, String key) {
		mNewsDBHelper = DatabaseHelper.getInstance(context, key);
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		createTable(db);
		// db.close();
		// mNewsDBHelper = new NewsDatabaseHelper(context);
	}

	private String[] whereArgs(NewsItem newsItem) {
		return new String[] { Integer.toString(newsItem.story_id) };
	}

	public void clearAllStories() {
		String deleteWhere = STORY_ID + " IN (SELECT " + STORY_ID + " FROM " + STORIES_TABLE + " WHERE " + BOOKMARKED + "=0" + ")";
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();

		Log.i("xpf", "str=" + " delete from " + IMAGE_URLS_TABLE + " where " + IMAGE_STORY_ID + " not in " + " ( select " + STORY_ID + " from " + STORIES_TABLE + " where " + BOOKMARKED + " = 1);");
		db.execSQL(" delete from " + IMAGE_URLS_TABLE + " where " + IMAGE_STORY_ID + " not in " + " ( select " + STORY_ID + " from " + STORIES_TABLE + " where " + BOOKMARKED + " =?);",
				new String[] { "1" });
		db.delete(STORIES_TABLE, BOOKMARKED + " = 0", null);
		// db.delete(CATEGORIES_TABLE, null, null);
	}

	synchronized void clearCategory(int categoryId) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		db.delete(CATEGORIES_TABLE, CATEGORY + "=?", new String[] { Integer.toString(categoryId) });
	}

	public void startTransaction() {
		// if(mNewsDBHelper.getWritableDatabase().isOpen() &&
		// !mNewsDBHelper.getWritableDatabase().isDbLockedByOtherThreads()){
		mNewsDBHelper.getWritableDatabase().beginTransaction();
		// }
	}

	public void endTransaction() {
		try {
			mNewsDBHelper.getWritableDatabase().setTransactionSuccessful();
		} finally {
			if (mNewsDBHelper.getWritableDatabase().inTransaction()) {
				mNewsDBHelper.getWritableDatabase().endTransaction();
			}
		}
	}

	public synchronized void saveNewsDetailsItem(NewsItem story, int allfield, boolean saveCategories) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues newsValues = new ContentValues();
		newsValues.put(BODY, story.body);
		newsValues.put(SHARE_URL, story.share_url);
		if (allfield == 1) {
			newsValues.put(STORY_ID, story.story_id);
			newsValues.put(STATUS, story.status);
			newsValues.put(TITLE, story.title);
			newsValues.put(SUMMARY, story.summary);
			newsValues.put(AUTHOR, story.author);
			newsValues.put(POST_DATE, story.postDate.getTime());
			newsValues.put(LAST_UPDATE, story.lastupdate.getTime());
			newsValues.put(THUMB_URL, story.thumb_url);
			newsValues.put(SMALL_URL, story.front_cover_small);
			newsValues.put(FULL_URL, story.front_cover_full);

			if (saveCategories) {
				if (!categoryExists(story.category_id, story.story_id)) {
					ContentValues categoryValues = new ContentValues();
					categoryValues.put(STORY_ID, story.story_id);
					categoryValues.put(CATEGORY, story.category_id);
					db.insert(CATEGORIES_TABLE, CATEGORY, categoryValues);
				}
			}
		}
		if (storyExists(story.story_id)) {
			db.update(STORIES_TABLE, newsValues, STORY_ID_WHERE, whereArgs(story));
			// delete old images will re-save them
			db.delete(IMAGE_URLS_TABLE, IMAGE_STORY_ID + "=?", whereArgs(story));
		} else {
			long i = db.insert(STORIES_TABLE, BODY, newsValues);
		}
		// insert images
		for (Image image : story.otherImgs) {
			insertImage(story.story_id, image);
		}
	}

	public int getCategoryByStoryId(int story_id) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		Cursor c = db.rawQuery("select *from " + CATEGORIES_TABLE + " where " + STORY_ID + "='" + String.valueOf(story_id) + "'", null);
		int ca = 0;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			ca = c.getInt(c.getColumnIndex(CATEGORY));
		}
		c.close();
		// db.close();
		return ca;

	}

	public synchronized boolean saveNewsItem(NewsItem story, boolean saveCategories) {
		boolean bFlag = false;
		SQLiteDatabase db;// = mNewsDBHelper.getWritableDatabase();
		ContentValues newsValues = new ContentValues();
		newsValues.put(STORY_ID, story.story_id);
		newsValues.put(STATUS, story.status);
		newsValues.put(TITLE, story.title);
		newsValues.put(SUMMARY, story.summary);
		// newsValues.put(BODY, story.body);
		newsValues.put(AUTHOR, story.author);
		if (story != null && story.postDate != null) {
			newsValues.put(POST_DATE, story.postDate.getTime());
		}
		if (story != null && story.lastupdate != null) {
			newsValues.put(LAST_UPDATE, story.lastupdate.getTime());
		}
		newsValues.put(THUMB_URL, story.thumb_url);
		// newsValues.put(SMALL_URL, story.front_cover_small);
		// newsValues.put(FULL_URL, story.front_cover_full);

		if (storyExists(story.story_id)) {
			db = mNewsDBHelper.getWritableDatabase();
			bFlag = false;
			db.update(STORIES_TABLE, newsValues, STORY_ID_WHERE, whereArgs(story));
		} else {
			db = mNewsDBHelper.getWritableDatabase();
			long i = db.insert(STORIES_TABLE, BODY, newsValues);
			bFlag = true;
		}

		if (saveCategories) {
			if (!categoryExists(story.category_id, story.story_id)) {
				ContentValues categoryValues = new ContentValues();
				categoryValues.put(STORY_ID, story.story_id);
				categoryValues.put(CATEGORY, story.category_id);
				long i = db.insert(CATEGORIES_TABLE, CATEGORY, categoryValues);
				Log.i("jjj", "----------" + i);
			}
		}
		return bFlag;
	}

	private long insertImage(int storyId, Image image) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues imageValues = new ContentValues();
		imageValues.put(IMAGE_STORY_ID, storyId);
		imageValues.put(SMALL_URL, image.smallURL);
		imageValues.put(FULL_URL, image.fullURL);
		imageValues.put(IMAGE_CAPTION, image.imageCaption);
		return db.insert(IMAGE_URLS_TABLE, FULL_URL, imageValues);
	}

	public void updateReadStatus(NewsItem newsItem, boolean readStatus) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues newsValues = new ContentValues();
		newsValues.put(ISREAD, readStatus);
		db.update(STORIES_TABLE, newsValues, STORY_ID_WHERE, whereArgs(newsItem));
	}

	public void updateBookmarkStatus(NewsItem story, boolean bookmarkedStatus) {
		SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
		ContentValues newsValues = new ContentValues();
		newsValues.put(BOOKMARKED, bookmarkedStatus);
		db.update(STORIES_TABLE, newsValues, STORY_ID_WHERE, whereArgs(story));
	}

	public List<NewsItem> getTopTen() {
		Cursor topTenCursor = getNewsCursor(TOP_NEWS, "10");
		ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();
		if (topTenCursor.moveToFirst()) {
			while (!topTenCursor.isAfterLast()) {
				newsItems.add(retrieveNewsItem(topTenCursor));
				topTenCursor.moveToNext();
			}
		}
		topTenCursor.close();
		return newsItems;
	}

	public Cursor getTopTenCursor() {
		return getNewsCursor(TOP_NEWS, "10");
	}

	public Cursor getLimitCursor(int category, int limit) {
		return getNewsCursor(category, String.valueOf(limit));
	}

	public Cursor getLimitCursor(int limit) {
		return getNewsCursor(TOP_NEWS, String.valueOf(limit));
	}

	public Cursor getSearchNewsCursor(int category, String strKey) {
		// SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		// String[] fields = new String[] {STORIES_TABLE + "." + STORY_ID,
		// TITLE, BODY, AUTHOR, FEATURED, DESCRIPTION, POST_DATE, LINK,
		// THUMB_URL,BOOKMARKED,ISREAD};
		//
		// String joinQuery = SQLiteQueryBuilder.buildQueryString(false,
		// STORIES_TABLE +", " + CATEGORIES_TABLE,
		// fields,
		// CATEGORY + "=" + Integer.toString(category) + " AND " + TITLE +
		// " like'%"+ strKey +"%'"+" AND " + STORIES_TABLE + "." + STORY_ID +
		// "=" + CATEGORIES_TABLE + "." + STORY_ID,
		// //CATEGORY + "=" + Integer.toString(category) + " AND " +
		// STORIES_TABLE + "." + STORY_ID + "=" + CATEGORIES_TABLE + "." +
		// STORY_ID,
		// null,
		// null,
		// POST_DATE + " DESC, " + STORIES_TABLE + "." + STORY_ID + " DESC ",
		// null);
		//
		// return db.rawQuery(joinQuery, null);

		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
//		String[] fields = new String[] { STORIES_TABLE + "." + STORY_ID, 
//				TITLE, BODY, SUMMARY, AUTHOR, POST_DATE, LAST_UPDATE, THUMB_URL, SMALL_URL, FULL_URL, BOOKMARKED, ISREAD };

		Cursor cursor = db.query(STORIES_TABLE, null, TITLE + " like'%" + strKey + "%'" + " AND " + STATUS + " !='" + "D" + "'", null, null, null, LAST_UPDATE + " DESC", null);
		// String joinQuery = SQLiteQueryBuilder.buildQueryString(false,
		// STORIES_TABLE +", " + CATEGORIES_TABLE,
		// fields,
		// CATEGORY + "=" + Integer.toString(category) + " AND " + TITLE +
		// " like'%"+ strKey +"%'"+" AND " + STORIES_TABLE + "." + STORY_ID +
		// "=" + CATEGORIES_TABLE + "." + STORY_ID,
		// //CATEGORY + "=" + Integer.toString(category) + " AND " +
		// STORIES_TABLE + "." + STORY_ID + "=" + CATEGORIES_TABLE + "." +
		// STORY_ID,
		// null,
		// null,
		// POST_DATE + " DESC, " + STORIES_TABLE + "." + STORY_ID + " DESC ",
		// null);

		// return db.rawQuery(joinQuery, null);

		// SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		// String[] fields = new String[] {STORY_ID, TITLE, BODY, AUTHOR,
		// FEATURED, DESCRIPTION, POST_DATE, LINK, THUMB_URL,BOOKMARKED,ISREAD};
		// Cursor cursor = db.query(STORIES_TABLE, fields, BOOKMARKED + "=1",
		// null, null, null, STORY_ID + " DESC", null);
		return cursor;
	}

	public Cursor getNewsCursor(int category) {
		return getNewsCursor(category, null);
	}

	public Cursor getNewsCursor(int category, String limit) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		// Cursor c = db.rawQuery(
		// "select *from " + STORIES_TABLE + " where " + STORY_ID +
		// " in ( select " + STORY_ID + " from " + CATEGORIES_TABLE + " where "
		// + CATEGORY + " = " + String.valueOf(category) + ");",
		// null);
		//
		// Log.i("xpf", "c" + c.getCount());
		//
		// while (c.moveToNext()) {
		// Log.i("xpf", "c= " + c.getString(c.getColumnIndex(BOOKMARKED)));
		// }
		// c.requery();
		// return c;
//		String[] fields = new String[] { STORIES_TABLE + "." + STORY_ID, 
//				TITLE, BODY, SUMMARY, AUTHOR, POST_DATE, LAST_UPDATE, THUMB_URL, SMALL_URL, SHARE_URL, FULL_URL, BOOKMARKED, ISREAD };

		String joinQuery = SQLiteQueryBuilder.buildQueryString(false, STORIES_TABLE + ", " + CATEGORIES_TABLE, null, CATEGORY + "=" + Integer.toString(category) + " AND " + STORIES_TABLE + "."
				+ STORY_ID + "=" + CATEGORIES_TABLE + "." + STORY_ID + " AND " + STATUS + " !='" + "D" + "'", null, null, LAST_UPDATE + " DESC ", limit); // POST_DATE
		// +
		// " DESC, "
		// +
		// STORIES_TABLE
		// +
		// "."
		// +
		// STORY_IDr
		// +
		// " DESC "

		return db.rawQuery(joinQuery, null);
	}

	public Cursor getSearchNewsCursor(String searchkey) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		// String[] fields = new String[] {STORIES_TABLE + "." + STORY_ID,
		// TITLE, BODY, SUMMARY, AUTHOR, POST_DATE, LAST_UPDATE, THUMB_URL,
		// SMALL_URL, FULL_URL, BOOKMARKED,ISREAD};
		//
		// String joinQuery = SQLiteQueryBuilder.buildQueryString(false,
		// STORIES_TABLE +", " + CATEGORIES_TABLE,
		// fields,
		// CATEGORY + "=" + Integer.toString(category) + " AND " + STORIES_TABLE
		// + "." + STORY_ID + "=" + CATEGORIES_TABLE + "." + STORY_ID,
		// null,
		// null,
		// POST_DATE + " DESC, " + STORIES_TABLE + "." + STORY_ID + " DESC ",
		// null);
		//
		// return db.rawQuery(joinQuery, null);

		String joinQuery;
		joinQuery = SQLiteQueryBuilder
				.buildQueryString(false, 
						STORIES_TABLE, 
						null, 
						TITLE + " LIKE '%" + searchkey + "%'" + " AND " + STATUS + " !='" + "D" + "'", 
						null, null, 
						POST_DATE + " DESC", 
						null);

		System.out.println("joinQuery>>>" + joinQuery);
		return db.rawQuery(joinQuery, null);
	}

	public Cursor getBookmarksCursor() {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
//		String[] fields = new String[] { STORY_ID, TITLE, BODY, SUMMARY, AUTHOR, POST_DATE, LAST_UPDATE, THUMB_URL, SMALL_URL, SHARE_URL, FULL_URL, BOOKMARKED, ISREAD };
		return db.query(STORIES_TABLE, null, BOOKMARKED + "=1", null, null, null, LAST_UPDATE + " DESC", null);
	}

	public NewsItem retrieveNewsItem(Cursor cursor) {
		int story_id_index = cursor.getColumnIndex(STORY_ID);
		int title_index = cursor.getColumnIndex(TITLE);
		int body_index = cursor.getColumnIndex(BODY);
		int summary_index = cursor.getColumnIndex(SUMMARY);
		int author_index = cursor.getColumnIndex(AUTHOR);
		int post_date_index = cursor.getColumnIndex(POST_DATE);
		int last_update_index = cursor.getColumnIndex(LAST_UPDATE);
		int thumb_url_index = cursor.getColumnIndex(THUMB_URL);
		int small_url_index = cursor.getColumnIndex(SMALL_URL);
		int share_url_index = cursor.getColumnIndex(SHARE_URL);
		int full_url_index = cursor.getColumnIndex(FULL_URL);
		int bookmarked_index = cursor.getColumnIndex(BOOKMARKED);
		int isread_index = cursor.getColumnIndex(ISREAD);
		int status_index = cursor.getColumnIndex(STATUS);

		NewsItem item = new NewsItem();
		item.story_id = cursor.getInt(story_id_index);
		item.title = cursor.getString(title_index);
		item.body = cursor.getString(body_index);
		item.summary = cursor.getString(summary_index);
		item.author = cursor.getString(author_index);
		item.postDate = new Date(cursor.getLong(post_date_index));
		item.lastupdate = new Date(cursor.getLong(last_update_index));
		item.thumb_url = cursor.getString(thumb_url_index);
		item.front_cover_small = cursor.getString(small_url_index);
		item.share_url = cursor.getString(share_url_index);
		item.front_cover_full = cursor.getString(full_url_index);
		if (cursor.getInt(bookmarked_index) == 1) {
			item.bookmark = true;
		} else {
			item.bookmark = false;
		}
		if (cursor.getInt(isread_index) == 1) {
			item.isRead = true;
		} else {
			item.isRead = false;
		}
		item.status = cursor.getString(status_index);
		return item;
	}

	static Image retrieveImage(Cursor cursor) {
		int small_url_index = cursor.getColumnIndex(SMALL_URL);
		int full_url_index = cursor.getColumnIndex(FULL_URL);
		int image_caption_index = cursor.getColumnIndex(IMAGE_CAPTION);

		Image image = new Image();
		image.smallURL = cursor.getString(small_url_index);
		image.fullURL = cursor.getString(full_url_index);
		image.imageCaption = cursor.getString(image_caption_index);

		return image;

	}

	public NewsItem retrieveNewsItem(int storyId) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();

		Cursor cursor = db.query(STORIES_TABLE, null, STORY_ID_WHERE, new String[] { Integer.toString(storyId) }, null, null, null);

		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		} else {
			NewsItem newsItem = retrieveNewsItem(cursor);
			cursor.close();
			populateImages(newsItem);

			return newsItem;
		}

	}

	public synchronized boolean isBookmarked(int storyId) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();

		Cursor cursor = db.query(STORIES_TABLE, new String[] { BOOKMARKED }, STORY_ID_WHERE, new String[] { Integer.toString(storyId) }, null, null, null);

		boolean isBookmarked = false;
		int bookmarked_index = cursor.getColumnIndex(BOOKMARKED);
		if (cursor.moveToFirst()) {
			isBookmarked = (cursor.getInt(bookmarked_index) == 1);
		}
		cursor.close();

		return isBookmarked;
	}

	public Date getLatestUpdateTime(int category_id) {
		Date lastUpdate = new Date(0);
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();
		Cursor result = db.query(STORIES_TABLE + ", " + CATEGORIES_TABLE, new String[] { LAST_UPDATE }, CATEGORY + "=" + Integer.toString(category_id) + " AND " + STORIES_TABLE + "." + STORY_ID + "="
				+ CATEGORIES_TABLE + "." + STORY_ID, null, null, null, LAST_UPDATE + " DESC");
		if (result.moveToFirst()) {
			int last_update_index = result.getColumnIndex(LAST_UPDATE);
			lastUpdate = new Date(result.getLong(last_update_index));
		}
		result.close();
		return lastUpdate;
	}

	void populateImages(NewsItem newsItem) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();

		Cursor cursor = db.query(IMAGE_URLS_TABLE, null, IMAGE_STORY_ID + "=?", whereArgs(newsItem), null, null, null);

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Image image = retrieveImage(cursor);
				newsItem.otherImgs.add(image);
				cursor.moveToNext();
			}
		}
		cursor.close();
	}

	public boolean storyExists(int storyId) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();

		Cursor result = db.query(STORIES_TABLE, new String[] { STORY_ID }, STORY_ID_WHERE, new String[] { Integer.toString(storyId) }, null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	private boolean categoryExists(int category, int storyId) {
		SQLiteDatabase db = mNewsDBHelper.getReadableDatabase();

		Cursor result = db.query(CATEGORIES_TABLE, new String[] { STORY_ID }, CATEGORY_WHERE, new String[] { Integer.toString(storyId), Integer.toString(category) },
		// CATEGORY_WHERE,
		// new String[] {Integer.toString(category)},
				null, null, null);

		boolean storyExists = (result.getCount() > 0);
		result.close();
		return storyExists;
	}

	private void createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + STORIES_TABLE + " (" + 
				STORY_ID + " INTEGER," 
				+ TITLE + " TEXT," 
				+ BODY + " TEXT," 
				+ AUTHOR + " TEXT," 
				+ POST_DATE + " INTEGER," 
				+ THUMB_URL + " TEXT," 
				+ SUMMARY + " TEXT," 
				+ SMALL_URL + " TEXT," 
				+ SHARE_URL + " TEXT," 
				+ FULL_URL + " TEXT," 
				+ LAST_UPDATE + " INTEGER," 
				+ STATUS + " TEXT," 
				+ BOOKMARKED + " BOOLEAN DEFAULT 0 NOT NULL," 
				+ ISREAD + " BOOLEAN DEFAULT 0 NOT NULL" + ");");

		db.execSQL("CREATE INDEX IF NOT EXISTS STORY_INDEX ON " + STORIES_TABLE + "(" + STORY_ID + ")");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + CATEGORIES_TABLE + " (" + STORY_ID + " INTEGER," + CATEGORY + " INTEGER" + ");");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + IMAGE_URLS_TABLE + " (" + IMAGE_STORY_ID + " INTEGER," + SMALL_URL + " TEXT," + FULL_URL + " TEXT," + IMAGE_CAPTION + " TEXT	" + ");");

		// 数据迁移
		mNewsDBHelper.updateTable(db, STORIES_TABLE);
		mNewsDBHelper.updateTable(db, CATEGORIES_TABLE);
		mNewsDBHelper.updateTable(db, IMAGE_URLS_TABLE);

	}

}
