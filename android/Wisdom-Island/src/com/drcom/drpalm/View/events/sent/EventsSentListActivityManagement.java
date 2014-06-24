package com.drcom.drpalm.View.events.sent;

import java.util.Date;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.Activitys.events.album.ClassAlbumDetailActivity;
import com.drcom.drpalm.Activitys.events.sent.EventsSentDetailActivity;
import com.drcom.drpalm.Activitys.events.video.ClassVideoDetailActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.MessageObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class EventsSentListActivityManagement {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2;	//更多请求返回成功
	
	private String KEY_REFLASHTIME = "eventssentflashtime";
	private SharedPreferences sp;	//15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	private EventsDB mEventsDB;
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	
	private Context mContext;
	
	public EventsSentListActivityManagement(Context c){
		mContext = c;
		
		sp = c.getSharedPreferences(KEY_REFLASHTIME, c.MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
		
		mEventsDB = EventsDB.getInstance(c,GlobalVariables.gSchoolKey);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isOver15mins(){
		return (new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 15;
	}
	
	/**
	 * 保存刷新时间
	 */
	public void SaveReflahstime(){
		editor.putLong(KEY_REFLASHTIME , new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
		editor.commit();
	}
	
	/**
	 * 
	 */
	public void onClickEvensSentItem(Cursor newsCursor){
		EventDetailsItem newsItem = mEventsDB.retrievePublishEventDetailItem(newsCursor);
//		newsCursor.close();
		
		if(newsItem.type == RequestCategoryID.EVENTS_ALBUM_ID){
			Intent i = new Intent(mContext, ClassAlbumDetailActivity.class);
			i.putExtra(ClassAlbumDetailActivity.KEY_EVENT_ID, newsItem.eventid);
			i.putExtra(ClassAlbumDetailActivity.KEY_ISSENTEVENT, true);
			mContext.startActivity(i);
		}else if(newsItem.type == RequestCategoryID.EVENTS_VIDEO_ID){
			Intent intent = new Intent();
			if (newsItem.eventid != null)
				intent.putExtra(ClassVideoDetailActivity.KEY_EVENT_ID, newsItem.eventid);
			intent.putExtra(ClassVideoDetailActivity.KEY_ISSENTEVENT, true);
			intent.setClass(mContext, ClassVideoDetailActivity.class);
			mContext.startActivity(intent);
		}else{
			Intent i = new Intent(mContext, EventsSentDetailActivity.class);
			i.putExtra(EventsDetailActivity.KEY_EVENT_ID, newsItem.eventid);
			mContext.startActivity(i);
		}
		
//		//未读时,广播通知主界面减少未读数
//		if(!newsItem.isread){
//			Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
//			intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID,RequestCategoryID.EVENTS_SEND_ID);
//			mContext.sendBroadcast(intent1);
//		}
	}
	
	/**
	 * 请求网络(取列表)
	 * @param lastActivityId   0:刷新         非0：更多
	 * @param uiHandler
	 */
	public void sendGetEventsRequest(final String lastupdate,final Handler h){
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onSuccess() {
				Message message = Message.obtain();
				if(lastupdate.equals("0")){
					message.arg1 = UPDATEFINISH;	//刷新
				}else{
					message.arg1 = MOREFINISH;		//更多
				}
				message.obj = new MessageObject(true,false);
				h.sendMessage(message) ;
			}
			
			@Override
			public void onCallbackError(String str) {
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = str;
				h.sendMessage(message);
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "通告列表:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "通告列表:自动重登录成功");
				if(isRequestRelogin){
					sendGetEventsRequest(lastupdate,h);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetPublishEventList", new Object[]{0, lastupdate,callback},callback.getClass().getName());
	}
	
	/**
	 * 发送刷新主界面未读数广播,通知主界面刷新
	 */
	public void sendReflashUnreadsumBroadcast(){
		Intent intent1 = new Intent(ActivityActionDefine.GET_NEW_MSG_ACTION);
		mContext.sendBroadcast(intent1);
	}
}
