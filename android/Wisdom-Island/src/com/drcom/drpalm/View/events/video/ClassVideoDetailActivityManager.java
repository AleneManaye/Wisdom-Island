package com.drcom.drpalm.View.events.video;

import java.util.ArrayList;
import java.util.Date;

import org.videolan.vlc.betav7neon.VlcManagement;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.FavItem;
import com.drcom.drpalm4tianzhujiao.R;

public class ClassVideoDetailActivityManager {
	
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
//	public static final int RELOGIN_FAIL = 6;
//	public static final int RELOGIN_SUCCESS = 7;
	
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	
	private Context mContext;
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private String mUsername = "";
	private SettingManager setInstance;
	private VlcManagement vm = new VlcManagement();
	private EventDetailsItem mEventDetailsItem ;//= new EventDetailsItem();
	private LoginManager instance ;
	
	public ClassVideoDetailActivityManager(Context c)
	{
		mContext = c;
		mEventsDB = EventsDB.getInstance(c, GlobalVariables.gSchoolKey);
		setInstance = SettingManager.getSettingManager(c);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		instance = LoginManager.getInstance(GlobalVariables.gAppContext);
		//视频播放
		vm.init(c);
	}
	
	public EventDetailsItem GetDataInDB(boolean isSent, int id){
		if (!isSent) {
			mEventCursor = mEventsDB.getOneEventCursor(id, mUsername);
		} else {
			mEventCursor = mEventsDB.getOnePublishEventCursor(id, mUsername);
		}
			
//			EventDetailsItem eventdetailsItem = null;
			if (mEventCursor != null) {
				mEventCursor.requery();
				mEventCursor.moveToFirst();
				if (!isSent) {
					mEventDetailsItem = mEventsDB.retrieveEventDetailItem(mEventCursor);
					SetIsread(isSent);
				} else {
					mEventDetailsItem = mEventsDB.retrievePublishEventDetailItem(mEventCursor);
					SetIsread(isSent);
				}
			}
			
			return mEventDetailsItem;
	}
	
	
	public void UpdataSendEventLastawstime(long newestlastupdatetime, EventDetailsItem item){
		mEventsDB.updataSendEventLastawstime(newestlastupdatetime, item);
	}
	
	public void updataSendEventAwscoutnclient(int sum, EventDetailsItem item){
		mEventsDB.updataSendEventAwscoutnclient(sum, item);
	}
	
	public void UpdataEventLastawstime(Date newestlastupdatetime, EventDetailsItem item){
		mEventsDB.updataEventLastawstime(newestlastupdatetime, item);
	}
	
	public Boolean getEventDetail(final Boolean isSent,final Boolean isgetAll,final int eventId,final Handler h){
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
				msg.what = UPDATEFINISH;
				h.sendMessageDelayed(msg, 200);
			}
			
			@Override
			public void onCallbackError(String err) {
				Message msg = new Message();
				msg.what = UPDATEFAILED;
				msg.obj = err;
				h.sendMessageDelayed(msg, 100);
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
				if (isRequestRelogin) {
					getEventDetail(isSent ,isgetAll,eventId,h); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
					}
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
	 * 播放
	 */
	public void Play(String url,String urlperview){
		System.gc();
		Log.i("zjj", "播放视频url:" + url);
		
		if(GlobalVariables.getSDKVersionNumber() > 18){
			Toast.makeText(mContext, R.string.error_no_neon, Toast.LENGTH_LONG).show();
			return;
		}
		
//		if(url.indexOf("http:")>-1){	//http 协议使用Video播放
//			Intent i = new Intent(mContext, ClassVideoPlayActivity.class);
//			i.putExtra(ClassVideoPlayActivity.KEY_VIDEOURL, url);
//			mContext.startActivity(i);
//		}else{							//其它使用VLC播放
			vm.OpenVideo(mContext, url,urlperview);
//		}
	}
	
	
	
	/**
	 * 清除视频所占资源
	 */
	public void destory(){
		vm.destory(mContext);
	}
	
	
	/**
	 * 设置为已读
	 * @param isSent 是否已发通告
	 */
	public void SetIsread(boolean isSent){
		if(isSent){
			mEventsDB.markAreadySendAsRead(mEventDetailsItem);
		}else{
			mEventsDB.markAsRead(mEventDetailsItem);
		}
	}
	
	/**
	 * 设置为收藏
	 */
	public void SetBookmark(boolean b){
		mEventDetailsItem.bookmark = b;
		
//		EventDetailsItem edi = new EventDetailsItem();
//		edi.eventid = mEventDetailsItem.eventid;
//		if(b){
//			//新增收藏
//			edi.status = "N";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
//			
//		}else{
//			//取消收藏
//			edi.status = "C";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
//		}
//		
//		ArrayList<EventDetailsItem> al =new ArrayList<EventDetailsItem>();
//		al.add(edi);
		
		FavItem edi = new FavItem();
		edi.mEventid = mEventDetailsItem.eventid + "";
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
		item.mUsername = mUsername;
		
		FavDB fdb = FavDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		fdb.saveFavItem(item);
		
		//更新本地收藏标识
		if(mHandlerAddfav != null){
			mHandlerAddfav.sendEmptyMessage(0);
		}
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
			if(mHandlerAddfav != null){
				mHandlerAddfav.sendEmptyMessage(0);
			}
		}								
	};
	
	/**
	 * 收藏事件返回句柄
	 */
	private Handler mHandlerAddfav = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				mEventsDB.markAsBookmark(mEventDetailsItem,mEventDetailsItem.bookmark);
			}
	};
}
