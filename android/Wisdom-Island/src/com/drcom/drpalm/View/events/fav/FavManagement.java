package com.drcom.drpalm.View.events.fav;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.EventsListActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.FavDB;
import com.drcom.drpalm.Tool.jsonparser.FavListParser;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.events.EventsListActivityManagement;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.FavItem;
import com.drcom.drpalm.objs.FavlistResultItem;

/**
 * 收藏通告管理类
 * @author zhaojunjie
 *
 */
public class FavManagement {
	private String KEY_NEEDGATALL = "KEY_NEEDGATALL";	//是否需要下载全部通告
	
	private EventsDB mEventsDB;
	private FavDB mFavDB;
	
	private SharedPreferences sp;
	private Editor editor;
	
	private Context mContext;
	private SettingManager setInstance ;
	private String mUsername = "";	
	
	public FavManagement(Context c){
		setInstance = SettingManager.getSettingManager(c);	
		mEventsDB = EventsDB.getInstance(c,GlobalVariables.gSchoolKey);
		mFavDB = FavDB.getInstance(c, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		mContext = c;
		sp = c.getSharedPreferences(KEY_NEEDGATALL, c.MODE_WORLD_READABLE);
		editor = sp.edit();
	}
	
	/**
	 * 从服务器中取得收藏列表
	 */
	public void GetFavlist(){
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		String lastupdatetime = mEventsDB.getLastupdatetimeFav(mUsername);
//		Log.i("zjj", "取" + mUsername + "通告收藏最后更新时间:" + lastupdatetime);
		
		FavListParser flp = new FavListParser();
		flp.SetUsername(mUsername);
		RequestManager.GetClassFavorite(lastupdatetime,flp , callbackgetfav);
	}
	
	private RequestOperationReloginCallback callbackgetfav = new RequestOperationReloginCallback(){
		@Override
		public void onSuccess(Object obj) {
			// TODO Auto-generated method stub
			super.onSuccess(obj);
			FavlistResultItem resultModule = (FavlistResultItem)obj;
			for(int i = 0 ; i < resultModule.mFavlist.size();i++){
//				Log.i("zjj", "通告收藏 id:" 
//						+ resultModule.mFavlist.get(i).mEventid 
//						+ "分类ID:" 
//						+ resultModule.mFavlist.get(i).mCategroyid
//						+ "状态:" 
//						+ resultModule.mFavlist.get(i).mStatus);
				
				mEventsDB.updateFavStatus(resultModule.mFavlist.get(i));
				mFavDB.delOldFav(resultModule.mFavlist.get(i));
			}
			
			SendOfflineFav();
		}
		
		@Override
		public void onError(String err) {
			// TODO Auto-generated method stub
			super.onError(err);
		}
	};
	
	/**
	 * 把离线收藏的通告发送到服务器中
	 * 回调后会自动从服务器中取得收藏列表
	 */
	public void SendOfflineFav(){
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		ArrayList<FavItem> favItems = new ArrayList<FavItem>();
		
		Cursor cursor = mFavDB.getFavListCursor(mUsername);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				FavItem item = mFavDB.retrieveLoginInfoItem(cursor);
				favItems.add(item);
				cursor.moveToNext();
			}
		}
		cursor.close();
		if(favItems.size() == 0)
			return;
		
		//转化为可发送类型
//		ArrayList<EventDetailsItem> al =new ArrayList<EventDetailsItem>();
//		for(int i = 0 ; i < favItems.size(); i++){
//			EventDetailsItem edi = new EventDetailsItem();
//			edi.eventid = favItems.get(i).mEventid;
//			edi.status = favItems.get(i).mStatus;
//			
//			al.add(edi);
//		}
		
		//发送
		RequestManager.SubmitClassfav(favItems, new SubmitResultParser(), addfavcallback);
	}
	
	/**
	 * 提交离线收藏状态请求结果
	 */
	private RequestOperationReloginCallback addfavcallback = new RequestOperationReloginCallback(){
		@Override
		public void onError(String str) {
			Log.i("zjj", "提交离线收藏状态  返回失败" + str);
//			GetFavlist();
		}
		@Override
		public void onSuccess() {		
			Log.i("zjj", "提交离线收藏状态   返回成功");
//			GetFavlist();
		}								
	};
	
	/**
	 * 标识是否需要在打开收藏时下载所有通告
	 */
	private void SaveIsGetallEvents(boolean b){
		editor.putBoolean(KEY_NEEDGATALL, b);
		editor.commit();
	}
	
}
