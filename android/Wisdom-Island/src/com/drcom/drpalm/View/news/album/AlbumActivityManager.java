package com.drcom.drpalm.View.news.album;

import java.util.Date;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

public class AlbumActivityManager {
	private static final String LASTREFRESHDATE = "newslastrefreshtime";
	private static final String SP_DATABASE_NAME = "album";
	
	private static final int DOWN = 1;
	private static final int NOT_DOWN = 0;
	private static final int REFRESH = 2;
	
	private SharedPreferences sp;
	private Editor editor;
	private Context mContext = null;
	
	public AlbumActivityManager(Context c)
	{
		sp = c.getSharedPreferences(SP_DATABASE_NAME, Context.MODE_WORLD_READABLE);
		editor = sp.edit();
		mContext = c;
	}
	
	public Date getLastRefreshTime()
	{
		return new Date(sp.getLong(LASTREFRESHDATE + RequestCategoryID.NEWS_ALBUM_ID, 0));
	}
	
	public void saveLastRefreshTime()
	{
		editor.putLong(LASTREFRESHDATE + RequestCategoryID.NEWS_ALBUM_ID, new Date(System.currentTimeMillis()).getTime());// 保存最后次更新的时间
		editor.commit();
		return;
	}
	
	public Boolean getNewsList(final String lastupdate ,final Handler h)
	{
		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) {
			return false;
		}
		
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {
			@Override
			public void onSuccess() {
				saveLastRefreshTime();
				if ("0".equals(lastupdate)) {// 刷新
					Message msg = new Message();
					msg.what = REFRESH;
					h.sendMessageDelayed(msg, 200);
				} else {
					Message msg = new Message();
					msg.what = DOWN;
					h.sendMessageDelayed(msg, 300);
				}
			}

			@Override
			public void onError(String err) {
				Message msg = new Message();
				msg.what = NOT_DOWN;// 下载失败
				msg.obj = err;
				h.sendMessage(msg);
			}
		};
		mRequestOperation.sendGetNeededInfo("GetNews", new Object[] { RequestCategoryID.NEWS_ALBUM_ID, lastupdate, callback }, callback.getClass().getName());
		return true;
	}

	/*
	 * 广播通知主界面刷新NEW状态
	 */
	public void sendReflashUnreadsumBroadcast() {
		Intent intent1 = new Intent(ActivityActionDefine.NEWS_UNREAD_SUM_DESC);
//		intent1.putExtra(ActivityActionDefine.NEWS_TYPE_ID, category);
		mContext.sendBroadcast(intent1);
	}
	
}
