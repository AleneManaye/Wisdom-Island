package com.drcom.drpalm.View.news.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.news.NewsDetailActivity;
import com.drcom.drpalm.Activitys.news.album.AlbumDetailActivity;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.objs.NewsItem;

public class NewsSearchListActivityManagement {
	public static final int FETCH_SUCCESSFUL = 1;
	public static final int FETCH_FAILED = 2;

	private Context mContext = null;

	public NewsSearchListActivityManagement(Context context) {
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

	public List<NewsItem> getDataList(String searchkey) {
		List<NewsItem> list = new ArrayList<NewsItem>();
		NewsDB newsDB = NewsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		Cursor eventCursor = newsDB.getSearchNewsCursor(searchkey);
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


	public void sendGetSearchNews(final String searchKey, final Handler handler) {
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = FETCH_SUCCESSFUL;
				handler.sendMessage(msg);
			}

			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = FETCH_FAILED;
				msg.obj = err;
				handler.sendMessage(msg);
			}
		};
		mRequestOperation.sendGetNeededInfo("SearchNews", new Object[] { "0", searchKey, callback }, callback.getClass().getName());
	}
}
