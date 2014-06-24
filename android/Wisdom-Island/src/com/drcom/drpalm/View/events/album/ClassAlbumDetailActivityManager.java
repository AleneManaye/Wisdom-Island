package com.drcom.drpalm.View.events.album;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.FavDB;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.FavItem;

public class ClassAlbumDetailActivityManager {
	
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签
	public static final int RELOGIN_FAIL = 6;
	public static final int RELOGIN_SUCCESS = 7;
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	private Context mContext;
	private LoginManager instance ;
	private String mUsername = "";	
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	
	public ClassAlbumDetailActivityManager(Context c,String username)
	{
		mContext = c;
		mUsername = username;
		instance = LoginManager.getInstance(GlobalVariables.gAppContext);
	}
	
	public Boolean getEventDetail(Boolean isSent,Boolean isgetAll,final int eventId,final Handler h){
		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) { // LoginManager.OnlineStatus.ONLINE_LOGINED
			return false;
		}

		int getall = 0;
		if (isgetAll)
			getall = 1;
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
		
			@Override
			public void onSuccess() {
				Message msg = new Message();
				msg.what = DOWN;
				h.sendMessageDelayed(msg, 200);
			}
			
			@Override
			public void onCallbackError(String err) {
				Message msg = new Message();
				msg.what = NOT_DOWN;
				msg.obj = err;
				h.sendMessageDelayed(msg, 100);
			}
			
			@Override
			public void onReloginError() {
			// TODO Auto-generated method stub
//				super.onReloginError();
//				Log.i("zjj", "通告详细:自动重登录失败");
				Message msg = new Message();
				msg.what = RELOGIN_FAIL;
				msg.obj = null;
				h.sendMessageDelayed(msg, 100);
			}
			
			@Override
			public void onReloginSuccess() {
			// TODO Auto-generated method stub
				//super.onReloginSuccess();
				//Log.i("zjj", "通告详细:自动重登录成功");
//				if (isRequestRelogin) {
//				requestEventDetail(eventid); // 自动登录成功后，再次请求数据
//				isRequestRelogin = false;
					Message msg = new Message();
					msg.what = RELOGIN_SUCCESS;
					msg.obj = null;
					h.sendMessageDelayed(msg, 100);
				}
			};
			if (!isSent) {
				mRequestOperation.sendGetNeededInfo("GetEventDetail", new Object[] { String.valueOf(eventId), getall, callback }, callback.getClass().getName());
			} else {
				mRequestOperation.sendGetNeededInfo("GetPublishEventDetail", new Object[] { String.valueOf(eventId), callback }, callback.getClass().getName());
			}
			return true;
	}
	
	/**
	 * 设置为收藏
	 */
	public void SetBookmark(boolean b,String eventid,String username){
		
		FavItem edi = new FavItem();
		edi.mEventid = eventid;
		edi.mUsername = username;
		if(b){
			//新增收藏
			edi.mStatus = "N";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
			
		}else{
			//取消收藏
			edi.mStatus = "C";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
		}
		
		ArrayList<FavItem> al =new ArrayList<FavItem>();
		al.add(edi);
		
		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT
				|| instance.getOnlineStatus() != LoginManager.OnlineStatus.ONLINE_LOGINED) {
			SaveToFavDB(edi);
		}else{
			RequestManager.SubmitClassfav(al, new SubmitResultParser(), addfavcallback);
		}
	}
	
	/**
	 * 收藏状态因不能提交到服务器，先保存在本地库中，下次登录后提交
	 * @param item
	 */
	private void SaveToFavDB(FavItem item){
		FavDB fdb = FavDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		fdb.saveFavItem(item);
		
		//更新本地收藏标识
//		if(mHandlerAddfav != null){
//			mHandlerAddfav.sendEmptyMessage(0);
//		}
	}
	
	/**
	 * 收藏请求结果
	 */
	private RequestOperationReloginCallback addfavcallback = new RequestOperationReloginCallback(){
		@Override
		public void onError(String str) {
			Log.i("zjj", "修改收藏状态  返回失败");
		}
		@Override
		public void onSuccess() {		
			Log.i("zjj", "修改收藏状态   返回成功");
//			if(mHandlerAddfav != null){
//				mHandlerAddfav.sendEmptyMessage(0);
//			}
		}								
	};
	
	/**
	 * 收藏事件返回句柄
	 */
//	private Handler mHandlerAddfav = new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//				mEventsDB.markAsBookmark(mEventDetailsItem,mEventDetailsItem.bookmark);
//			}
//	};
	
	
	
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
	 * 删除库中的通告
	 * @param db
	 * @param id
	 */
	public void DelEventInDB(EventsDB db ,int id){
		db.delPublishEvent(id,mUsername);
	}
}
