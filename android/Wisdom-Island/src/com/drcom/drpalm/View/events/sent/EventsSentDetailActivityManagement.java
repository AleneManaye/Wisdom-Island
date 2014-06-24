package com.drcom.drpalm.View.events.sent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.drHttpClient.DrRequestImgsTask;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;

public class EventsSentDetailActivityManagement {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	private int mins = 5 * 60 * 1000;	//日历提醒延长时间
	
	private Context mContext;
	private String mUsername = "";	
//	private SettingManager setInstance ;	
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private Cursor mEventCursor = null;
	
	public EventsSentDetailActivityManagement(Context c,String username){
		mContext = c;
		mUsername = username;
//		setInstance = SettingManager.getSettingManager(c);	
//		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
	}
	
	/**
	 * 取已发详细
	 * @param id
	 * @param h
	 */
	public void sendGetEventDetailRequest (final int id,final Handler h){
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
					sendGetEventDetailRequest(id,h);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetPublishEventDetail", new Object[]{String.valueOf(id),callback},callback.getClass().getName());
	}
	
	/**
	 * 删除通告
	 * @param evenid
	 * @param h
	 */
	public void DelEvent(final String evenid,final Handler h){
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
				Log.i("zjj", "删除通告:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "删除通告:自动重登录成功");
				if(isRequestRelogin){
					DelEvent(evenid,h);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		RequestManager.SubmitDelEvent(evenid, new SubmitResultParser(), callback);
	}
	
	/**
	 * 从库读取
	 */
	public EventDetailsItem GetDataInDB(EventsDB db ,int id){
		mEventCursor = db.getOnePublishEventCursor(id,mUsername);
		mEventCursor.requery();
		mEventCursor.moveToFirst();
		EventDetailsItem  eventDetailsItem = db.retrievePublishEventDetailItem(mEventCursor);
		mEventCursor.close();
		
		return eventDetailsItem;
	}
	
	/**
	 * 删除库中的通告
	 * @param db
	 * @param id
	 */
	public void DelEventInDB(EventsDB db ,int id){
		db.delPublishEvent(id,mUsername);
	}
	
	public String readTextFromResource(int newsDetail) {
		InputStream raw = mContext.getResources().openRawResource(newsDetail);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		int i;
		try {
			i = raw.read();
			while (i != -1) {
				stream.write(i);
				i = raw.read();
			}
			raw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toString();
	}
	
	/**
	 * 
	 * @param item
	 */
	public void SaveCalendar(EventDetailsItem item){
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", item.start.getTime());
		intent.putExtra("endTime", item.start.getTime() + mins);
		intent.putExtra("title", item.title);
		intent.putExtra("description", item.body);
		intent.putExtra("eventLocation", item.location);
		mContext.startActivity(Intent.createChooser(intent, "Calendar"));
	}

}
