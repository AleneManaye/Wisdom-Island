package com.drcom.drpalm.View.events;

import android.content.Context;

import com.drcom.drpalm.View.setting.SettingManager;

public class EventBaseInfoTools {
	
	/**
	 * 获取当前登录用户的用户名
	 * @param context
	 * @return
	 */
	public String getCurrentUserName(Context context){
		SettingManager setInstance = SettingManager.getSettingManager(context);
		return setInstance.getCurrentUserInfo().strUsrName;
	}
	
}
