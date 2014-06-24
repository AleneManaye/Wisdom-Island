package com.drcom.drpalm.View.news;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.news.NewsDetailActivity;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.objs.NewsItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class NewsActivityManagement {
	public static final int NOT_DOWN = 0;// 数据加载失败的标记
	public static final int DOWN = 1;// 数据加载成功的标记
	public static final int REFRESH = 2;// 刷新數據成功的标记

	private static final String LASTREFRESHDATE = "newslastrefreshtime";
	private static final String SP_DATABASE_NAME = "album";

	private Context mContext = null;
	private SharedPreferences mSp = null;
	private Editor mEditor = null;


	private Date mLastupdate , mLastRefreshTime;
	private int mDataCount = 0;
	private boolean isExistAnyData = false;

	public NewsActivityManagement(Context context) {
		mLastupdate = new Date(0);
		mSp = context.getSharedPreferences(SP_DATABASE_NAME, Context.MODE_WORLD_READABLE);
		mEditor = mSp.edit();
		mContext = context;
	}

	/*
	 * 打开详细
	 */
	public void startDetailActivity(NewsItem item) {
		NewsDB newsDB = NewsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
//		int category = newsDB.getCategoryByStoryId(item.story_id);
		startNewsDetailActivity(item.story_id);

//		if (!item.isRead) {
//			// 未读时,广播通知主界面减少未读数
//			checkUnRead(category);
//		}
	}

	private void startNewsDetailActivity(int itemId) {
		Intent intent = new Intent(mContext, NewsDetailActivity.class);
		intent.putExtra("story_id", itemId);
		mContext.startActivity(intent);
	}

	/*
	 * 广播通知主界面刷新NEW状态
	 */
	public void sendReflashUnreadsumBroadcast() {
		Intent intent1 = new Intent(ActivityActionDefine.NEWS_UNREAD_SUM_DESC);
//		intent1.putExtra(ActivityActionDefine.NEWS_TYPE_ID, category);
		mContext.sendBroadcast(intent1);
	}
	
	/*
	 * 获取最后更新时间 lastUpdate
	 */
	public Date getLastUpdate() {
		return mLastupdate;
	}

	/*
	 * 获取最后刷新时间
	 */
	public Date getLastRefreshTime(int category) {
		mLastRefreshTime = new Date(mSp.getLong(LASTREFRESHDATE + category, 0));
		return mLastRefreshTime;
	}

	/*
	 * 数据源是否为空
	 */
	public boolean isExistAnyNews(int category) {
		return isExistAnyData;
	}

	/*
	 * 数据源记录数
	 */
	public int getDataCount() {
		return mDataCount;
	}

	/*
	 * 获取数据源
	 */
	public List<NewsItem> getDataList(int category, int newsCount) {
		List<NewsItem> list = new ArrayList<NewsItem>();
		NewsDB dataDB = NewsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		Cursor cursor = dataDB.getLimitCursor(category, newsCount);
		mDataCount = cursor.getCount();
		if (mDataCount > 0) {
			isExistAnyData = true;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				NewsItem item = dataDB.retrieveNewsItem(cursor);
				mLastupdate = item.lastupdate;
				list.add(item);
			}
		}
		else {
			isExistAnyData = false;
		}
		cursor.close();


		return list;
	}

	/*
	 * 请求协议
	 */
	public void sendGetNeededInfo(final Handler handler, final int category, final String lastUpdate) {
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {
			@Override
			public void onSuccess() {
				mEditor.putLong(LASTREFRESHDATE + category, new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
				mEditor.commit();
				// 调用数据库组件
				if ("0".equals(lastUpdate)) {// 刷新
					Message msg = new Message();
					msg.what = REFRESH;
					handler.sendMessageDelayed(msg, 200);
				} else {
					Message msg = new Message();
					msg.what = DOWN;
					handler.sendMessageDelayed(msg, 300);
				}
			}

			@Override
			public void onError(String err) {
				Message msg = new Message();
				msg.what = NOT_DOWN;// 下载失败
				msg.obj = err;
				handler.sendMessage(msg);
			}
		};
		mRequestOperation.sendGetNeededInfo("GetNews", new Object[] { category, lastUpdate, callback }, callback.getClass().getName());
	}
}
