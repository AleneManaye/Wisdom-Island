package com.drcom.drpalm.View.events.sent;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class EventsSentReaderActivityManagement {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	private Context mContext;
	
	private String mUsername = "";	
	private SettingManager setInstance ;	
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	
	public EventsSentReaderActivityManagement(Context c){
		mContext = c;
		
		setInstance = SettingManager.getSettingManager(c);	
		mEventsDB = EventsDB.getInstance(c,GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
	}
	
	/**
	 * 请求网络(取列表)
	 * @param lastActivityId 
	 * @param uiHandler
	 */
	public void sendGetEventReaderRequest (final int id,final Handler h){
		//是否取得此记录的所有信息
		
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onSuccess() {
				Message message = Message.obtain();
				message.arg1 = UPDATEFINISH;
				h.sendMessage(message) ;
			}

			@Override
			public void onCallbackError(String err) {
				// TODO Auto-generated method stub
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = err;
				h.sendMessage(message);
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "通告详细:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "通告详细:自动重登录成功");
				if(isRequestRelogin){
					sendGetEventReaderRequest(id,h);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetEventReadInfo", new Object[]{String.valueOf(id),callback},callback.getClass().getName());
	}
	
	/**
	 * 从库读取
	 */
	public EventDetailsItem GetDataInDB(int id){
		mEventCursor = mEventsDB.getOnePublishEventCursor(id,mUsername);
		mEventCursor.requery();
		mEventCursor.moveToFirst();
		EventDetailsItem eventDetailsItem = mEventsDB.retrievePublishEventDetailItem(mEventCursor);
		mEventCursor.close();
		
		return eventDetailsItem;
	}
}
