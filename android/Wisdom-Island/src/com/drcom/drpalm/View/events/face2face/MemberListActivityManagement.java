package com.drcom.drpalm.View.events.face2face;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.face2face.CommuniReplyActivity;
import com.drcom.drpalm.Activitys.events.face2face.MemberListActivity;
import com.drcom.drpalm.DB.CommunicationDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.jsonparser.ContectListParser;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.events.EventBaseInfoTools;
import com.drcom.drpalm.objs.AwsContentItem;
import com.drcom.drpalm.objs.ContactItem;
import com.drcom.drpalm.objs.ContectlistResultItem;
import com.drcom.drpalm.objs.MessageObject;
import com.drcom.drpalm.objs.MyphotolistResultItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MemberListActivityManagement {
	
	private Context mContext;
	private String mCurrentUserName;
	private CommunicationDB mCommunicationDB;
	
	private String KEY_REFLASHTIME = "refreshtime";
	private SharedPreferences refreshSharedPreferences;	//15分钟间隔自动刷新
	private Editor editor;
	
	public MemberListActivityManagement(Context context){
		this.mContext = context;
		mCommunicationDB = CommunicationDB.getInstance(mContext,GlobalVariables.gSchoolKey);
		EventBaseInfoTools baseInfo = new EventBaseInfoTools();
		mCurrentUserName = baseInfo.getCurrentUserName(mContext);
		
		refreshSharedPreferences = mContext.getSharedPreferences(KEY_REFLASHTIME, Context.MODE_WORLD_READABLE);
		editor = refreshSharedPreferences.edit();
	}
	
	public void jumpToCommuniReplyActivity(Cursor cursor){
		
		ContactItem newsItem = mCommunicationDB.retrieveContactItem(cursor);
		
		Intent i = new Intent(mContext, CommuniReplyActivity.class);
		i.putExtra(CommuniReplyActivity.REPLY_ASWPUBID, newsItem.cnid);
		i.putExtra(CommuniReplyActivity.REPLY_HEADSHOW, newsItem.cnname);
		mContext.startActivity(i);
		
//		//把回复时间写入本地,和下次刷新的时间对比,是否有新回复
//		if(newsItem.lastupdate.getTime()>newsItem.lastawstimeread.getTime()){
//			mCommunicationDB.updataAsworgLastawstimeread(newsItem.lastupdate.getTime()+"",
//					mCurrentUserName,
//					newsItem.cnid);
//		}
		
		// 未读时,广播通知主界面减少未读数
		if (!newsItem.unread.equals(0)) {
			Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
			intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID, RequestCategoryID.EVENTS_COMMUNION_ID);
			intent1.putExtra(ActivityActionDefine.EVENTS_DES_SUM, Integer.valueOf(newsItem.unread));
			mContext.sendBroadcast(intent1);
		}
		
	}
	
	/**
	 * 获取联系列表
	 * @param handler
	 */
	public void getContactList(ArrayList<ContactItem> contactItemList , final Handler handler){
		/*
		 * 使用RequestOperationReloginCallback类回调，当SESSIONKEY失效时，会自动登录，再请求数据接口
		 * 注意：代码要使用private boolean isRequestRelogin = true;	登录SECCION超时重登录标志记录，以免不断重登造成死循环
		 */
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onSuccess() {
				if(handler != null){
					Message message = Message.obtain();
					message.arg1 = MemberListActivity.UPDATEFINISH;	//刷新
					message.obj = new MessageObject(true,false);
					handler.sendMessage(message);
				}
			}
			
			@Override
			public void onCallbackError(String str) {
				if(handler != null){
					Message message = Message.obtain();
					message.arg1 = MemberListActivity.UPDATEFAILED;
					message.obj = str;
					handler.sendMessage(message);
				}
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "发送通告:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				
				if(handler != null){
					Log.i("zjj", "发送通告:自动重登录成功");
					Message message = Message.obtain();
					message.arg1 = MemberListActivity.UPRELOGINSUCCESS;
					handler.sendMessage(message);
				}
			}
			
			@Override
			public void onSuccess(Object obj) {	
				if(handler != null){
					//先设置为不可视
					mCommunicationDB.updateContactsInvisible(mCurrentUserName);
					
					Log.i("zjj", "取家园桥联系人列表请求结果   返回成功");
					ContectlistResultItem result = (ContectlistResultItem)obj;
					
					for(int i = 0 ; i<result.mFavlist.size(); i++){
						mCommunicationDB.saveContacts(result.mFavlist.get(i));
					}
					
					Message message = Message.obtain();
					message.arg1 = MemberListActivity.UPDATEFINISH;	//刷新
					message.obj = new MessageObject(true,false);
					handler.sendMessage(message);
				}
			}				
		};
//		mRequestOperation.sendGetNeededInfo("GetContactList", new Object[]{callback}, callback.getClass().getName());
		ContectListParser cp = new ContectListParser();
		cp.SetUsername(mCurrentUserName);
		RequestManager.GetContactList(contactItemList,cp , callback);
	}
	
	/**
	 * 获取联系人列表Cursor
	 * @return
	 */
	public Cursor getContactCursor(){
		return mCommunicationDB.getContacts(mCurrentUserName);
	}
	
	/**
	 * 获取联系人列表
	 * @return
	 */
	public ArrayList<ContactItem> getContactList(){
		ArrayList<ContactItem> clist = new ArrayList<ContactItem>();
		Cursor c = mCommunicationDB.getContacts(mCurrentUserName);
		c.requery();
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				ContactItem item = mCommunicationDB.retrieveContactItem(c);
				clist.add(item);
				c.moveToNext();
			}
		}
		c.close();
		
		return clist;
	}
	
	/**
	 * 获取最后刷新时间
	 * @return
	 */
	public Date getLastRefreshTime(){
		return new Date(refreshSharedPreferences.getLong(KEY_REFLASHTIME, 0));
	}
	
	/**
	 * 保存刷新时间
	 */
	public void SaveReflashtime(){
		editor.putLong(KEY_REFLASHTIME , new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
		editor.commit();
	}
	
	/**
	 * 取列表后,统计未读数,通知主界面更新数字图标
	 */
	public void BroadcastUnreadSum(){
		int sum = 0;
		ArrayList<ContactItem> list = getContactList();
		for(int i = 0 ; i < list.size(); i++){
			sum +=  Integer.valueOf(list.get(i).unread);
		}
		
		// 未读时,广播通知主界面增加未读数
		if(sum > 0){
			Log.i("zjj", "**********************************" + sum);
			Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
			intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID, RequestCategoryID.EVENTS_COMMUNION_ID);
			intent1.putExtra(ActivityActionDefine.EVENTS_DES_SUM, Integer.valueOf(sum));
			intent1.putExtra(ActivityActionDefine.EVENTS_DES_REPLACE, true);
			mContext.sendBroadcast(intent1);
		}
	}
}
