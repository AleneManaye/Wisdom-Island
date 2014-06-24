package com.drcom.drpalm.View.Navigation;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.Navigation.NavigationMainActivity;
import com.drcom.drpalm.Activitys.Navigation.SchoolNavigation;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.DatabaseHelper;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.NavigationParse;
import com.drcom.drpalm.Tool.request.NavigationRequest;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.Tool.request.ViewRequestCallback;
import com.drcom.drpalm.objs.NavigationItem;
import com.drcom.drpalm.objs.NavigationListItem;

public class SchoolNavigationManagetment {
	
	private Context mContext;
	private NavigationDB mNavigationDB;
	private RequestOperation mRequestOperation;
	
	public SchoolNavigationManagetment(Context context){
		this.mContext = context;
		mNavigationDB = NavigationDB.getInstance(mContext);
		mRequestOperation = RequestOperation.getInstance();
	}
	
	/**
	 * 打开指定应用
	 * @param schoolItem
	 */
	public void openAppBySchoolkey(final NavigationItem schoolItem){
		/*打开指定学校的APP 应用*/
		if(schoolItem != null){
			new Thread() {					
				@Override
				public void run() {					
					synchronized(mNavigationDB) {
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
			}.start();
		}
	}
	
	/**
	 * 进入导航子页面
	 * @param schoolItem
	 */
	public void enterSubNavigation(NavigationItem schoolItem){
		Intent intent = new Intent(mContext,SchoolNavigation.class);
		intent.putExtra(SchoolNavigation.NAVIGATION_PARENT_ID, schoolItem.point_id);
		mContext.startActivity(intent);
	}
	
	public void getSchoolList(int parent_id, final Handler handler){
		Message msg = Message.obtain();
		msg.arg1 = SchoolNavigation.GET_SCHOOLLIST_START;
		handler.sendMessage(msg);
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		mRequestOperation.GetSchoolList(String.valueOf(parent_id), new RequestOperationCallback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = SchoolNavigation.GET_SCHOOLLIST_SUCCESSFUL;
				handler.sendMessage(msg);
			}
			
			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = SchoolNavigation.GET_SCHOOLLIST_FAILED;
				msg.obj = err;
				handler.sendMessage(msg);
			}
		});
	}
	
	public void GetNavigationList(int app_id, final Handler handler){
		Message msg = Message.obtain();
		msg.arg1 = NavigationMainActivity.GET_NAVIGATION_ITEM_START;
		handler.sendMessage(msg);
		NavigationRequest request = new NavigationRequest();
		request.GetNavigationList(String.valueOf(app_id), new ViewRequestCallback() {
			
			@Override
			public void onSuccess(final BaseParse parse) {
				// TODO Auto-generated method stub
				new Thread() {					
					@Override
					public void run() {					
						synchronized(mNavigationDB) {
							NavigationParse navigationParse = (NavigationParse)parse;
							List<NavigationListItem> navigationItems = navigationParse.parseNavigationList();				
							if(navigationItems == null) {
								onError("");
								return;
							}	
							mNavigationDB.startTransaction();
							mNavigationDB.clearAllNavigationList();
							for(NavigationListItem item : navigationItems) {							
								mNavigationDB.saveNavigationListItem(item);
							}							
							mNavigationDB.endTransaction();
							Message msg = Message.obtain();
							msg.arg1 = NavigationMainActivity.GET_NAVIGATION_ITEM_SUCCESSFUL;
							handler.sendMessage(msg);
						}
					}
				}.start();
			}
			
			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = NavigationMainActivity.GET_NAVIGATION_ITEM_FAILED;
				msg.obj = err;
				handler.sendMessage(msg);
			}
		});
	}
}
