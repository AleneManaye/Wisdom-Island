package com.drcom.drpalm.View.LeaveApplication;

import java.util.ArrayList;
import java.util.Date;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.LeaveApplication.ApplicationInfoActivity;
import com.drcom.drpalm.Activitys.bindaccount.NewbindaccountActivity;
import com.drcom.drpalm.Activitys.danceinfo.AttendanceActivity;
import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.Activitys.setting.AccountManageActivity;
import com.drcom.drpalm.DB.LeaveApplicationDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.jsonparser.LeaveApplicationListParser;
import com.drcom.drpalm.Tool.jsonparser.UserInfoParser;
import com.drcom.drpalm.Tool.request.RequestGetEventListCallback;
import com.drcom.drpalm.Tool.request.RequestGetEventListReloginCallback;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.drcom.drpalm.objs.MessageObject;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LeaveApplicationMainManager {
	private Context mContext;
	private SettingManager setInstance;
	private LeaveApplicationDB mLeaveDB;
	private ArrayList<LeaveApplicationMainItem> mItem = new ArrayList<LeaveApplicationMainItem>();
	private LeaveApplicationMainItem mItemDetail ;
	private String mUsername;
	private String KEY_REFLASHTIME = "leaveflashtime";
	private SharedPreferences sp; // 15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	private String mLastUpdatetime = "0";
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录
	
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	public LeaveApplicationMainManager(Context c){
		mContext = c;
		setInstance = SettingManager.getSettingManager(c);
		mLeaveDB = LeaveApplicationDB.getInstance(c, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		sp = c.getSharedPreferences(KEY_REFLASHTIME, c.MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
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
		editor.putLong(KEY_REFLASHTIME, new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
		editor.commit();
	}
	
	/**
	 * 发送告假列表请求
	 * @param lastupdate
	 * @param handler
	 */
	public void GetListData(final String lastupdate,final Handler handler){
		if(handler != null){
			Message msg = Message.obtain();
			msg.arg1 = AccountManageActivity.LOADING;
			handler.sendMessage(msg);
		}
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onCallbackError(String str) {
				if(handler != null){
					Message message = Message.obtain();
					message.arg1 = AccountManageActivity.UPDATEFAILED;
					message.obj = str;
					handler.sendMessage(message);
				}
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zhr", "告假列表:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				
				if(handler != null){
					Log.i("zhr", "告假列表:自动重登录成功");
					if(isRequestRelogin){
						GetListData(lastupdate,handler);	//自动登录成功后，再次请求数据
						isRequestRelogin = false;
					}
				}
			}
			
			@Override
			public void onSuccess(Object obj) {	
				if(handler != null){
					mItem = (ArrayList<LeaveApplicationMainItem>)obj;
					for(int i = 0;i<mItem.size();i++){
						mItem.get(i).user = mUsername;
					mLeaveDB.saveleavesItem(mItem.get(i));
					}
					
					Message message = Message.obtain();
					message.arg1 = AttendanceActivity.UPDATEFINISH;	//刷新
					message.obj = new MessageObject(true,false);
					handler.sendMessage(message);
				}
			}				
		};
		LeaveApplicationListParser parser = new LeaveApplicationListParser();
		RequestManager.getLeaveApplicationList(lastupdate, parser, callback);
	}
	
	/**
	 * 发送告假详细请求
	 * @param leaveid
	 * @param allfield
	 * @param handler
	 */
	public void GetDetailData(final String leaveid,final String allfield,final Handler handler){
		if(handler != null){
			Message msg = Message.obtain();
			msg.arg1 = AccountManageActivity.LOADING;
			handler.sendMessage(msg);
		}
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onCallbackError(String str) {
				if(handler != null){
					Message message = Message.obtain();
					message.arg1 = AccountManageActivity.UPDATEFAILED;
					message.obj = str;
					handler.sendMessage(message);
				}
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zhr", "告假详细:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				
				if(handler != null){
					Log.i("zhr", "告假详细:自动重登录成功");
					if(isRequestRelogin){
						GetListData(leaveid,handler);	//自动登录成功后，再次请求数据
						isRequestRelogin = false;
					}
				}
			}
			
			@Override
			public void onSuccess(Object obj) {	
				if(handler != null){
					mItemDetail = (LeaveApplicationMainItem)obj;
						mItemDetail.user = mUsername;
						Log.i("zhr","detail save item.isread:"+mItemDetail.isread);
					mLeaveDB.saveleavesItem(mItemDetail);
					
					Message message = Message.obtain();
					message.arg1 = AttendanceActivity.UPDATEFINISH;	//刷新
					message.obj = new MessageObject(true,false);
					handler.sendMessage(message);
				}
			}				
		};
		LeaveApplicationListParser parser = new LeaveApplicationListParser();
		RequestManager.getApplicationInfo(leaveid, allfield, parser, callback);
	}
	
	/**
	 * 取第一项的LastupdateTime
	 */
	public String GetTheFristLeaveLastupdateTime(Cursor cursor){
		if (cursor != null) {
			if(cursor.getCount() > 0){
				LeaveApplicationMainItem newsItem = mLeaveDB.retrieveLeaveApplicationMainItem(cursor);
				mLastUpdatetime = newsItem.lastupdate.getTime() + "";
			}
		}
		return mLastUpdatetime;
	}
	
	/**
	 * 选中某项
	 * @param c
	 */
	public void onListviewItemClick(Cursor c){
		LeaveApplicationMainItem newsItem = mLeaveDB.retrieveLeaveApplicationMainItem(c);
		Intent i = new Intent(mContext, ApplicationInfoActivity.class);
		i.putExtra(ApplicationInfoActivity.KEY_LEAVE_ID, newsItem.leaveid);
		mContext.startActivity(i);

		// 未读时,广播通知主界面减少未读数
//		if (!newsItem.isread) {
//			Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
//			intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID, 9992);
//			mContext.sendBroadcast(intent1);
//		}

	}
}
