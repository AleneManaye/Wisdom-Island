package com.drcom.drpalm.Tool.request;

import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.login.LoginManager.LoginCallback;
import com.drcom.drpalm.View.login.LoginManager.OnlineStatus;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm4tianzhujiao.R;

public class RequestOperationReloginCallback implements RequestOperationCallback {

	/**
	 * RequestGetEventListCallback 接口返回失败
	 * 对错误进行处理
	 * 1.超时自动登录
	 * 
	 * 其它错误 回调到onCallbackError(),外层在onCallbackError()处理错误则可以
	 */
	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub
		//超时自动登录
 		if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.OvertimeSessionKey))){//OvertimeSessionKey)){
			Log.i("zjj", "----Session超时，自动重新登录-------");
			
			UserInfo loginUsrinfo = SettingManager.getSettingManager(GlobalVariables.gAppContext).getCurrentUserInfo();
			LoginManager loginmanager = LoginManager.getInstance(GlobalVariables.gAppContext);
			loginmanager.mOnlineStatus = OnlineStatus.OFFLINE;	//转为已下线状态
			loginmanager.loginOnline(loginUsrinfo.strUsrName, loginUsrinfo.strPassword,new LoginCallback() {
				@Override
				public void onLoginSuccess() {
					Log.i("zjj", "----在线登录成功-------");
					onReloginSuccess();
				}

				@Override
				public void onLoginError(String strError) {
					// TODO Auto-generated method stub
					Log.i("zjj", "----在线登录失败-------");
					onReloginError();
				}
			});
		}else{
			onCallbackError(err);
		}
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		
	}
	
	public void onSuccess(Object obj){
		
	}

	public void onCallbackError(String err){};
	
	public void onReloginError(){};
	
	public void onReloginSuccess(){};
	
	/**
	 * 被别人踢（sessionkey error）
	 */
	public void onInvalidSession(){};
}


