package com.drcom.drpalm.View.Navigation;

import java.util.List;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.Navigation.SchoolNavigation;
import com.drcom.drpalm.Activitys.Navigation.searchSchoolActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.DatabaseHelper;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.objs.NavigationItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

public class searchSchoolActivityManagement {
	
	private Context mContext;
	private RequestOperation mRequestOperation;
	private NavigationDB mNavigationDB;
	
	public searchSchoolActivityManagement(Context context){
		this.mContext = context;
		mRequestOperation = RequestOperation.getInstance();
		mNavigationDB = NavigationDB.getInstance(mContext);
	}
	
	public void openAppBySchoolkey(NavigationItem schoolItem){
		/*打开指定学校的APP 应用*/
		if(schoolItem != null){
			GlobalVariables.gSchoolKey = schoolItem.key;
			GlobalVariables.gTitleUrl = schoolItem.titleurl;
			
			SharedPreferences  preferences = mContext.getSharedPreferences("default_school", Context.MODE_PRIVATE);
			if(preferences.contains("school_key")){
				if(!preferences.getString("school_key", "").equals(schoolItem.key)){
					DatabaseHelper.setChanged(true);
				}
			}
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("school_key", schoolItem.key);
			editor.commit();
			
			/**
			 * 重新初始化DB接口，防止学校切换导致写入同一个库的问题（由于service绑定Application启动，导致先于获得schoolkey）
			 */
			mRequestOperation.initDB();
			
			Intent intent = new Intent(mContext,MainActivity.class);
			mContext.startActivity(intent);
		}
	}
	
	/**
	 * 搜索请求接口
	 * @param searchkey
	 * @param handler
	 */
	public void searchNavigation(final String appid, final String searchkey, final Handler handler){
		Message msg = Message.obtain();
		msg.arg1 = searchSchoolActivity.SEARCH_NAVIGATION_START;
		handler.sendMessage(msg);
		RequestOperation mRequestOperation = RequestOperation.getInstance();	
		mRequestOperation.SearchSchool(appid,searchkey, new RequestOperationCallback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = searchSchoolActivity.SEARCH_NAVIGATION_SUCCESSFUL;
				msg.obj = searchkey;
				handler.sendMessage(msg);
			}
			
			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = searchSchoolActivity.SEARCH_NAVIGATION_FAILED;
				msg.obj = err;
				handler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * 获取查询结果列表 
	 * @param searchKey  key
	 * @return
	 */
	public List<NavigationItem> getSearchResultList(String searchKey){
		return mNavigationDB.getSearchItems(searchKey, null,LanguageManagement.getSysLanguage(mContext));
	}
}
