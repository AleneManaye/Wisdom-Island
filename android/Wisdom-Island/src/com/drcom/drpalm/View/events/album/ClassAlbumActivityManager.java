package com.drcom.drpalm.View.events.album;

import java.util.Date;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestGetEventListCallback;
import com.drcom.drpalm.Tool.request.RequestGetEventListReloginCallback;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.objs.MessageObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ClassAlbumActivityManager {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	public static final int RELOGINSUCCESS = 3; // 重登录
	
	public static final String CATEGORYID_KEY = "categoryidkey";
	private static final String LASTREFRESHDATE = "classlastrefreshtime";
	private static final String SP_DATABASE_NAME = "classalbum";
	private SharedPreferences sp;
	private Editor editor;
	
	public ClassAlbumActivityManager(Context c){
		sp = c.getSharedPreferences(SP_DATABASE_NAME, Context.MODE_WORLD_READABLE);
		editor = sp.edit();
	}
	
	public Date getLastUpdateTime(){
		return new Date(sp.getLong(LASTREFRESHDATE, 0));
	}
	
	public void saveLastUpdateTime(long time)
	{
		editor.putLong(LASTREFRESHDATE, time);// 保存最后次更新的时间
		editor.commit();
	}
	
	public void getEventsList(final int categoryId ,final String lastupdate,final String lastreadtime,final Handler h)
	{
		//mListView.setOnloadingRefreshVisible();
		
		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) { // LoginManager.OnlineStatus.ONLINE_LOGINED
			Message message = Message.obtain();
			if (lastupdate.equals("0")) {
				message.arg1 = UPDATEFINISH; // 刷新
			} else {
				message.arg1 = MOREFINISH; // 更多
			}
			h.sendMessage(message);
			return;
		}
		
		
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestGetEventListCallback callback = new RequestGetEventListReloginCallback() {
			@Override
			public void onSuccess() {
				Message message = Message.obtain();
				if (lastupdate.equals("0")) {
					message.arg1 = UPDATEFINISH; // 刷新
				} else {
					message.arg1 = MOREFINISH; // 更多
				} 
				message.obj = null;
				h.sendMessage(message);
			}

			@Override
			public void onCallbackError(String str) {
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = str;
				h.sendMessage(message);
			}

			@Override
			public void onLoading() {
				Message message = Message.obtain();
				message.arg1 = MOREFINISH; // 更多
				message.obj = new MessageObject(true, false);
				h.sendMessage(message);
			}

			@Override
			public void onReloginError() {
				super.onReloginError();
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED; // 更多
				message.obj = null;
				h.sendMessage(message);
				Log.i("zjj", "通告列表:自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				super.onReloginSuccess();
				Log.i("zjj", "通告列表:自动重登录成功");
				Message message = Message.obtain();
				message.arg1 = RELOGINSUCCESS; //
				message.obj = null;
				h.sendMessage(message);
				
//				if (isRequestRelogin) {
//					sendGetEventsRequest(lastupdate); // 自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetEventsList", new Object[] { categoryId, lastupdate,lastreadtime, callback }, callback.getClass().getName());
	}

}
