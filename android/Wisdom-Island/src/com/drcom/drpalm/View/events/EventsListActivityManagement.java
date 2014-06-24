package com.drcom.drpalm.View.events;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.Activitys.events.EventsListActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestGetEventListCallback;
import com.drcom.drpalm.Tool.request.RequestGetEventListReloginCallback;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.MessageObject;

public class EventsListActivityManagement {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	public static final String CATEGORYID_KEY = "categoryidkey";
	private String KEY_REFLASHTIME = "eventsflashtime";

	// 变量
	private int mCategoryid = 0; // 分类ID
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
//	private EventsListCursorAdapter mAdapter;
//	private int mCurNewCount = 0; // 当前纪录数
//	private int mLastActivityId = 0; // 最后一个活动的ID
	private String mUsername = "";
	private SettingManager setInstance;
//	private LoginManager mLogininstance ;//= LoginManager.getInstance(this);
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
//	private Date mDateStart = new Date(0); // 有效期开始
//	private Date mDateEnd = new Date(0); // 有效期结束
//	private int mOrderby = 2; // 排列方式
	private SharedPreferences sp; // 15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	private String mLastUpdatetime = "0";	//最顶一条消息的最后更新时间
	private String mLastReadtime = "0";		//最近一条被读时间
	private Context mContext;
	
	public EventsListActivityManagement(Context c,int categoryid){
		mCategoryid = categoryid;
		mContext = c;
		//
//		mLogininstance = LoginManager.getInstance(c);
		setInstance = SettingManager.getSettingManager(c);
		mEventsDB = EventsDB.getInstance(c, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		sp = c.getSharedPreferences(KEY_REFLASHTIME, c.MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME + mCategoryid, 0));
	}
	
	/**
	 * 是否需要自动刷新
	 * @return
	 */
	public boolean isTimeToReflash(){
		return (new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 15;
	}
	
	/**
	 * 保存刷新时间
	 */
	public void SaveReflashtime() {
		editor.putLong(KEY_REFLASHTIME + mCategoryid, new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
		editor.commit();
	}
	
	/**
	 * 发送请求
	 * @param lastupdate
	 * @param h
	 * @return 是否发送成功
	 */
	public boolean GetData(final String lastupdate,final String lastReadtime,final Handler h){
		// 非在线登录/网络不通时,返回
//		LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) { // LoginManager.OnlineStatus.ONLINE_LOGINED
																								// !=
																								// instance.getOnlineStatus()
																								// ||

//			initalizeCursorAdapter(mDateStart.getTime(), mDateEnd.getTime(), mOrderby);
			return false;
		}
		/*
		 * 使用RequestOperationReloginCallback类回调，当SESSIONKEY失效时，会自动登录，再请求数据接口
		 * 注意：代码要使用private boolean isRequestRelogin = true;
		 * 登录SECCION超时重登录标志记录，以免不断重登造成死循环
		 */
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestGetEventListCallback callback = new RequestGetEventListReloginCallback() {
			@Override
			public void onSuccess() { // 请求数据成功
//				super.onSuccess();
				Message message = Message.obtain();
				if (lastupdate.equals("0")) {
					message.arg1 = UPDATEFINISH; // 刷新
				} else {
					message.arg1 = MOREFINISH; // 更多
				}
				message.obj = new MessageObject(true, false);
				h.sendMessage(message);

				Log.i("zjj", "通告列表:刷新成功");
			}

			@Override
			public void onCallbackError(String str) {
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = str;
				h.sendMessage(message);

				Log.i("zjj", "通告列表:刷新失败" + str);
			}

			@Override
			public void onLoading() {
				// TODO Auto-generated method stub
				Message message = Message.obtain();
				message.arg1 = MOREFINISH; // 更多
				message.obj = new MessageObject(true, false);
				h.sendMessage(message);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("zjj", "通告列表:自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("zjj", "通告列表:自动重登录成功");
				if (isRequestRelogin) {
					GetData(lastupdate,lastReadtime,h); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetEventsList", new Object[] { mCategoryid, lastupdate,lastReadtime, callback }, callback.getClass().getName());
		return true;
	}
	
	/**
	 * 取第一项的LastupdateTime
	 */
	public String GetTheFristEventLastupdateTime(Cursor cursor){
		if (cursor != null) {
			if(cursor.getCount() > 0){
//				Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(1);
				EventDetailsItem newsItem = mEventsDB.retrieveEventDetailItem(cursor);
				mLastUpdatetime = newsItem.lastupdate.getTime()/1000 + "";
			}
		}
		return mLastUpdatetime;
	}
	
	/**
	 * 最近一条被读时间
	 * @return
	 */
	public String GetTheLastReadtime(){
		return mEventsDB.getLastReadtime(mUsername,mCategoryid).getTime()/1000 + "";
	}
	
	/**
	 * 选中某项
	 * @param c
	 */
	public void onListviewItemClick(Cursor c){
		EventDetailsItem newsItem = mEventsDB.retrieveEventDetailItem(c);

		Intent i = new Intent(mContext, EventsDetailActivity.class);
		i.putExtra(EventsDetailActivity.KEY_EVENT_ID, newsItem.eventid);
		mContext.startActivity(i);

		// 未读时,广播通知主界面减少未读数
		if (!newsItem.isread && newsItem.status.equals(EventDraftItem.ORISTATUS_TYPE_N) ) {
			Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
			intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID, mCategoryid);
			mContext.sendBroadcast(intent1);
		}

	}
}
