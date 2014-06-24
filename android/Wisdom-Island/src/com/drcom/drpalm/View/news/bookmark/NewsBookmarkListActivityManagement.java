package com.drcom.drpalm.View.news.bookmark;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.news.NewsDetailActivity;
import com.drcom.drpalm.Activitys.news.album.AlbumDetailActivity;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.objs.NewsItem;

public class NewsBookmarkListActivityManagement {
	private Context mContext = null;

	public NewsBookmarkListActivityManagement(Context context) {
		mContext = context;
	}

	public void startDetailActivity(NewsItem item) {
		NewsDB newsDB = NewsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		int category = newsDB.getCategoryByStoryId(item.story_id);
		if (category == RequestCategoryID.NEWS_ALBUM_ID) {
			startAlbumDetailActivity(item.story_id);
		} else {
			startNewsDetailActivity(item.story_id);
		}
	}
	/*
	 * 打开详细
	 */
	public void startNewsDetailActivity(int itemId) {
		Intent intent = new Intent(mContext, NewsDetailActivity.class);
		intent.putExtra("story_id", itemId);
		mContext.startActivity(intent);
	}

	/*
	 * 打开相册
	 */
	public void startAlbumDetailActivity(int itemId) {
		Intent intent = new Intent(mContext, AlbumDetailActivity.class);
		intent.putExtra("story_id", itemId);
		mContext.startActivity(intent);
	}

	public List<NewsItem> getDataList() {
		List<NewsItem> list = new ArrayList<NewsItem>();
		NewsDB newsDB = NewsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		Cursor eventCursor = newsDB.getBookmarksCursor();
		eventCursor.requery();
		list.clear();
		if (eventCursor.getCount() > 0) {
			for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
				NewsItem item = newsDB.retrieveNewsItem(eventCursor);
				list.add(item);
			}
		}
		eventCursor.close();
		return list;
	}
}
