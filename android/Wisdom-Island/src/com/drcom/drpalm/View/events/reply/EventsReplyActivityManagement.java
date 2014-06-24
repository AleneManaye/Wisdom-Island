package com.drcom.drpalm.View.events.reply;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.reply.EventsReplyActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.objs.AwsContentItem;
import com.drcom.drpalm.objs.EventDetailsItem;

public class EventsReplyActivityManagement {
	private  int SENDREPLY_BEGIN   = 0;
	private  int SENDREPLY_SUCCESS = 1;
	private  int SENDREPLY_FAILED  = 2;
	private  int GET_REPLY_START   = 3;
	private  int GET_REPLY_SUCCESS = 4;
	private  int GET_REPLY_FAILED  = 5;
	
	private String mUsername = "";
	private EventsDB mEventsDB = null;
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	
	public EventsReplyActivityManagement(Context c,String username){
		mUsername = username;
		mEventsDB = EventsDB.getInstance(c, GlobalVariables.gSchoolKey);
	}
	
	/**
	 * 取数据
	 * @param h
	 * @param eventid
	 * @param aswpubid
	 */
	public void getData(final Handler h,final String eventid,final String aswpubid){
		//请求
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
			
			@Override
			public void onSuccess() {
				Message msg = Message.obtain();
				msg.arg1 = GET_REPLY_SUCCESS;
				if(h!=null)
					h.sendMessage(msg);
			}
			
			@Override
			public void onCallbackError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = GET_REPLY_FAILED;
				msg.obj  = err;
				if(h!=null)
					h.sendMessage(msg);
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "回复内容列表:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "回复内容列表:自动重登录成功");
				if(isRequestRelogin){
					getData(h,eventid,aswpubid);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetReplyInfo", new Object[]{eventid, aswpubid, "0",callback},callback.getClass().getName());
	}
	
	/**
	 * 
	 * @param h
	 * @param eventid
	 * @param aswpubid
	 * @param sendStr
	 */
	public void SendData(final Handler h,final int eventid,final String aswpubid,final String sendStr){
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onSuccess() {
//				getDataInDB();
//				sendHandlerMsg(SENDREPLY_SUCCESS, sendStr);
				
				Message msg = Message.obtain();
				msg.arg1 = SENDREPLY_SUCCESS;
				msg.obj  = sendStr;
				if(h!=null)
					h.sendMessage(msg);
			}
			
			@Override
			public void onCallbackError(String err) {
				// TODO Auto-generated method stub
//				sendHandlerMsg(SENDREPLY_FAILED,err);
				
				Message msg = Message.obtain();
				msg.arg1 = SENDREPLY_FAILED;
				msg.obj  = err;
				if(h!=null)
					h.sendMessage(msg);
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "反馈回复:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "反馈回复:自动重登录成功");
				if(isRequestRelogin){
					SendData(h,eventid,aswpubid,sendStr);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("ReplyPost", new Object[]{eventid, aswpubid, sendStr,callback},callback.getClass().getName());
	}
	
	/**
	 * 
	 * @param eventid
	 * @param aswpubid 讨论组ID
	 * @return
	 */
	public List<AwsContentItem> getAwsContentList(int eventid,String aswpubid){
		return mEventsDB.getAwsContentList(eventid,mUsername,aswpubid);
	}
	
	/**
	 * 取已发通告,跟某人的聊天内容总数
	 * @param eventid
	 * @param aswpubid
	 * @return
	 */
	public int getAwscontentSum (int eventid,String aswpubid){
		Cursor c = mEventsDB.getAwsContentByPubID(eventid,mUsername,aswpubid);
		int sum = c.getCount();
		c.close();
		c = null;
		return sum;
	}
	
	/**
	 * (通告数据)
	 * 以eventid和对方的ID(replyerid)为准,定位到一个对话,
	 * 更新该对话被查看时的最后回复时间(只保存到本地,用作比较是否有新回复)
	 * @param lastawstimeread
	 * @param eventid
	 * @param username
	 * @param replyerid
	 */
	public void UpdateReadtime(String lastawstimeread, int eventid, String username, String replyerid){
		
		//接收的通告
		Cursor eventCursor = mEventsDB.getOneEventCursor(eventid,username);
		eventCursor.requery();
		eventCursor.moveToFirst();
		EventDetailsItem eventDetailsItem = null;
		if(eventCursor.getCount() > 0){
			mEventsDB.updataAsworgLastawstimeread(lastawstimeread, eventid, username, replyerid);
			
			eventDetailsItem = mEventsDB.retrieveEventDetailItem(eventCursor);
			mEventsDB.updataEventLastawstimeread(DateFormatter.getDateFromSecondsString(lastawstimeread),eventDetailsItem);
			mEventsDB.updataEventLastawstime(DateFormatter.getDateFromSecondsString(lastawstimeread),eventDetailsItem);
		}
		
		eventCursor.close();
		
		
		
		
		
		//已发的通告
		Cursor sendeventCursor = mEventsDB.getOnePublishEventCursor(eventid,username);
		sendeventCursor.requery();
		sendeventCursor.moveToFirst();
		EventDetailsItem sendeventDetailsItem = null;
		if(sendeventCursor.getCount()>0){
			mEventsDB.updataSendAsworgLastawstimeread(lastawstimeread,
					eventid,
					username,
					replyerid);
			
			sendeventDetailsItem = mEventsDB.retrievePublishEventDetailItem(sendeventCursor);
			mEventsDB.updataSendEventLastawstimeread(DateFormatter.getDateFromSecondsString(lastawstimeread), sendeventDetailsItem);
		}
		sendeventCursor.close();
		
		
	}
	
}
