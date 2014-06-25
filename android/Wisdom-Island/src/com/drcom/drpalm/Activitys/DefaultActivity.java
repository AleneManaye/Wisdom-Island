package com.drcom.drpalm.Activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.Navigation.NavigationMainActivity;
import com.drcom.drpalm.Activitys.main.FirstSettingActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.wisdom.island.R;

/**
 * 欢迎界面
 * 
 * @author zhaojunjie
 * 
 */
public class DefaultActivity extends Activity {
	private Handler mHandler = new Handler();
//	private RequestOperation mRequestOperation = RequestOperation.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//一定要放在第一行
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defaulf_view);
		
//		hideTitle();
//		hideToolbar();
		
		// 1秒后中转到主界面
		mHandler.postDelayed(mRunnable,1000);
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
//			try {
//				finishDraw();
				NextActivity();
				DefaultActivity.this.finish();
//				overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
		}
	};

	/**
	 * 打开下一个窗体
	 */
	private void NextActivity(){
		/*
		 * modify by kk for test
		 *  关闭欢迎界面
		 */
		/*
		if (isFirstEnter()) {
			Intent sendIntent = new Intent();
			sendIntent.setClass(DefaultActivity.this,FirstSettingActivity.class);
			startActivity(sendIntent);
		}else */if(GlobalVariables.getAppDefaultSchoolKey()){
			//保存为默认的SCHOOL
			SharedPreferences  preferences = getSharedPreferences("default_school", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("school_key", GlobalVariables.gSchoolKey);
			editor.commit();
			
			//
			Intent sendIntent = new Intent();
			sendIntent.setClass(DefaultActivity.this,MainActivity.class);
			startActivity(sendIntent);
		}else {
			Intent sendIntent = new Intent();
			sendIntent.setClass(DefaultActivity.this,NavigationMainActivity.class);	//导航首页
			
//			sendIntent.setClass(DefaultActivity.this,SchoolNavigation.class);
//			sendIntent.putExtra(SchoolNavigation.NAVIGATION_PARENT_ID,GlobalVariables.getAgentID());	//根目录ID(代理商ID)
			startActivity(sendIntent);
		}
	}
	
	/**
	 * 是否首次启动
	 * 
	 * @return
	 */
	private boolean isFirstEnter() {
		SharedPreferences settingSharedPreferences = getSharedPreferences("FirstEnterSetting", MODE_WORLD_READABLE);
		if (!settingSharedPreferences.getString("firstStart", "0").equals(getVersion())) {
			SharedPreferences.Editor editor = settingSharedPreferences.edit();
			editor.putString("firstStart", getVersion());
			editor.commit();
			return true;
		}
		return false;
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "4.0.0";
		}
	}

}
