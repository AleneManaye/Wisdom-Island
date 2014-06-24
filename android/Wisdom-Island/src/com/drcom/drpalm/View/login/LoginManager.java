/*
 * File         : 
 * Date         : 2012-04-24
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  :  
 */
package com.drcom.drpalm.View.login;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.LoginDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.Encryption;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.jsonparser.LoginParser;
import com.drcom.drpalm.Tool.jsonparser.LogoutParser;
import com.drcom.drpalm.Tool.request.RequestLoginCallback;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.View.bindaccount.BindaccountListManagement;


public class LoginManager {
	public enum OnlineStatus{
		OFFLINE  ,
		ONLINE_LOGINED ,
		OFFLINE_LOGINED  
	};
//	private String mUsername;
//	private String mPassword;
//	private String mUserType;
//	private String mSessionKey;
//	private String mUnGetCount;
	private Context mContext;
	public  OnlineStatus mOnlineStatus = OnlineStatus.OFFLINE;	
	private LoginDB mLoginDB;
	private BindaccountListManagement mbindaccountManager;
	
	//private AlertDialog mAlertDialog;
	
	static LoginManager mLoginManager = null;
	static public LoginManager getInstance(Context context){
		if(null == mLoginManager){
			mLoginManager = new LoginManager(context);			
		}
		return mLoginManager;
	}
	
	public LoginManager(Context context){
		mContext = context;		
		mOnlineStatus = OnlineStatus.OFFLINE;
		mLoginDB = LoginDB.getInstance(context, GlobalVariables.gSchoolKey);
		mbindaccountManager = new BindaccountListManagement(context);
	}	
	
	public boolean loginOnline(final String name, final String pass, LoginCallback callback){
		//关闭一个Underground窗体,此窗体用于启动时过渡
		CloseUndergroud();
		
		if(OnlineStatus.ONLINE_LOGINED == mOnlineStatus){
			return true;
		}			
		return sendLoginRequest(name, pass, callback);
	}
	
	private boolean sendLoginRequest(String name, String pass, final LoginCallback callback){		
		String password = Encryption.toMd5(pass);
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestLoginCallback loginCallback = new RequestLoginCallback(){
			@Override
			public void onError(String str) {
				GlobalVariables.gSessionKey = "";
				callback.onLoginError(str);
			}
			@Override
			public void onSuccess(UserInfo item) {
				LoginSucceed(item,callback);
			}			
		};
		return RequestManager.Login(new LoginParser(), 
				name,
				password,
				GlobalVariables.getDeviceId(),
				GlobalVariables.mExternalPackageName.equals("")? mContext.getPackageName():GlobalVariables.mExternalPackageName, 	//由外部程序,则使用外部程序的包名,使PUSH消息发送到外部程序中
				loginCallback);
//		return mRequestOperation.sendGetNeededInfo("LoginGateway", new Object[]{name,password,GlobalVariables.getDeviceId(),mContext.getPackageName(),loginCallback},loginCallback.getClass().getName());
	}
	
	/**
	 * 登录成功事件
	 * @param item
	 * @param callback
	 */
	public void LoginSucceed(UserInfo item,final LoginCallback callback){
		mOnlineStatus = OnlineStatus.ONLINE_LOGINED;
//		mUsername = item.strUsrName; 
//		mUserType = item.strUsrType;
//		mSessionKey = item.sessionKey;
		GlobalVariables.gSessionKey = item.sessionKey;
		GlobalVariables.gPushSettingInfo = item.pushSetting;
		
		//比较头像是否要更新
		Log.i("zjj", "头像URL：" + item.headurl);
//		UserInfo tempuser = mLoginDB.getLoginInfoByName(item.strUsrName);
		
		mLoginDB.updataUserNewmsg(item);
		if(callback != null)
			callback.onLoginSuccess();
		
		Intent intent = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
		mContext.sendBroadcast(intent);
		
//		if(!tempuser.headurl.equals(item.headurl)){	//要更新
//			downloadPic(item,callback);
//		}else{	//不更新
//			
//			if(callback != null)
//				callback.onLoginSuccess();
//			
//			Intent intent = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
//			mContext.sendBroadcast(intent);
//		}
	}
	
//	private void downloadPic(final UserInfo item,final LoginCallback callback){
//		final ImageLoader il = new ImageLoader(GlobalVariables.ALBUM_SCHOOL_CACHE);
//		il.loadBigPic(item.headurl, new BigImageCallback() {
//					@Override
//					public void imageLoaded(String bmpurl) {
//						Log.i("zjj", "头像下载成功");
////						UserInfo itemtemp = new UserInfo();
////						itemtemp.strUsrName = item.strUsrName;
////						itemtemp.pic =  il.getBitmapFromCache(bmpurl);
//						
////						mLoginDB.saveLoginInfoPic(itemtemp);
//						
//						callback.onLoginSuccess();
//						
//						Intent intent = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
//						mContext.sendBroadcast(intent);
//					}
//				});
//	}
	
	public boolean loginLocal(String name, String pass){
//		if(OnlineStatus.OFFLINE_LOGINED == mOnlineStatus){
//			return true;
//		}
//		SettingManager setInstance = SettingManager.getSettingManager(mContext);
//		List<UserInfo> list = setInstance.usrinfolist;
//		if(null == list)
//			return false;
//		for(UserInfo info : list)
//		{
////			info.bParentLock &&  strParentLockPwd
//			if(info.strUsrName.equals(name) 
//				&& info.strPassword.equals(pass))
//			{
//				UserInfo loginUsrinfo = new UserInfo();
//				loginUsrinfo.strUsrName = info.strUsrName;
//				loginUsrinfo.strPassword = info.strPassword;
//				loginUsrinfo.bAutoLogin = info.bAutoLogin;
//				loginUsrinfo.bRememberPwd = info.bRememberPwd;
//				loginUsrinfo.nUsrType = info.nUsrType;
//				loginUsrinfo.strUserNickName = info.strUserNickName;
//				loginUsrinfo.bParentLock = info.bParentLock;
//				loginUsrinfo.strParentLockPwd = info.strParentLockPwd;
//				//loginUsrinfo.bParentLock = 
//				setInstance.setUserInfo(loginUsrinfo);
//				setInstance.saveSetting();
//				mOnlineStatus = OnlineStatus.OFFLINE_LOGINED;
//				
//				Intent intent = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
//				mContext.sendBroadcast(intent);
//				
//				Log.i("zjj", "----在线登录成功-------");
//				Intent intent1 = new Intent(ActivityActionDefine.UNGETCOUNT_ACTION);
//				intent1.putExtra(ActivityActionDefine.UNGETCOUNT,0);
//				mContext.sendBroadcast(intent1);
//				
//				return true;
//			}
//		}
		
		List<UserInfo> list = GetLoginUserList();
		if(list != null){
			for (UserInfo userInfo : list) {
				if(userInfo.strUsrName.equals(name.toLowerCase()) && userInfo.strPassword.equals(pass)){
					//更新数据库 用户登录时间
					mLoginDB.updataUserNewmsg(userInfo);
					
					mOnlineStatus = OnlineStatus.OFFLINE_LOGINED;
					
					Intent intent = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
					mContext.sendBroadcast(intent);
					
					Log.i("zjj", "----离线登录成功-------");
					Intent intent1 = new Intent(ActivityActionDefine.UNGETCOUNT_ACTION);
					intent1.putExtra(ActivityActionDefine.UNGETCOUNT,0);
					mContext.sendBroadcast(intent1);
					return true;
				}
			}
		}
		return false;
	}
	public void logout(final LogoutCallback logoutcallback){
		switch(mOnlineStatus){
		case OFFLINE:
			break;
		case ONLINE_LOGINED:
			String sessionkey = GlobalVariables.gSessionKey;			
			String schoolid = GlobalVariables.gSchoolId;
			String domain = GlobalVariables.gGateawayDomain;

			RequestOperation mRequestOperation = RequestOperation.getInstance();
			RequestOperationCallback callback = new RequestOperationCallback(){
				@Override
				public void onError(String str) {
//					Log.i("zjj", "Logout onError" + str);
				}
				@Override
				public void onSuccess() {		
//					Log.i("zjj", "ActivityActionDefine.LOGIN_STATUS_CHANGED");
//					
//					Intent intent = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
//					mContext.sendBroadcast(intent);
				}								
			};
//			mRequestOperation.sendGetNeededInfo("Logout", new Object[]{callback},callback.getClass().getName());
			RequestManager.Logout(new LogoutParser(), callback);
			
			mOnlineStatus = OnlineStatus.OFFLINE;							
//			mSessionKey = "";
//			mUsername = "";
			
//			Log.i("zjj", "logoutcallback.onLogOut(true)");
			Intent intent1 = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
			mContext.sendBroadcast(intent1);
			
			logoutcallback.onLogOut(true);
			break;
		case OFFLINE_LOGINED:			
			mOnlineStatus = OnlineStatus.OFFLINE;
			
			Intent intent = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
			mContext.sendBroadcast(intent);
			
			logoutcallback.onLogOut(true);
			break;
		}		
	}
	
	public static void destroy()
	{
		mLoginManager = null;
	}
	public OnlineStatus getOnlineStatus(){
		return mOnlineStatus;
	}
	public static abstract class LoginCallback{
		public abstract void onLoginSuccess();
		public abstract void onLoginError(String strerror);
	}
	
	public static interface LogoutCallback
	{
		public void onLogOut(boolean bSuccess); 
	}
	
	/**
	 * 保存登录信息
	 * @param loginUsrinfo
	 */
	public void SaveLoginUser(UserInfo loginUsrinfo){
		//
		Save4External(GlobalVariables.gSchoolKey,"AutoLogin",loginUsrinfo.bAutoLogin);
		
		mLoginDB.updataUserSetting(loginUsrinfo);
	}
	
	public void Bindaccount(String account,String passwordMd5,Handler handler){
		mbindaccountManager.addAccountRelation(account, passwordMd5, GlobalVariables.gSchoolKey, GlobalVariables.Devicdid, handler);
	}
	
	/**
	 * 删除记录
	 * @param loginUsrinfo
	 */
	public void DelLoginUser(UserInfo loginUsrinfo){
		mLoginDB.delLoginInfoItem(loginUsrinfo);
	}
	
	/**
	 * 登录记录
	 * @return
	 */
	public List<UserInfo> GetLoginUserList(){
		List<UserInfo> list = null;
		
		Cursor c = mLoginDB.getLoginInfoListCursor();
		c.requery();
		if (c.moveToFirst()) {
			list = new ArrayList<UserInfo>();
			while (!c.isAfterLast()) {
				list.add(mLoginDB.retrieveLoginInfoItem(c));
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}
	
	/**
	 * 关闭Undergroud窗体
	 */
	private void CloseUndergroud(){
		Intent exitIntent = new Intent();
		exitIntent.setAction(ActivityActionDefine.CLOSE_UNDERGROUND_ACTIVITY);
		mContext.sendBroadcast(exitIntent);
	}
	
	/**
	 * 用于解决定制客户端与E宝贝自动登录同步问题
	 * @param name sharepreference 文件名
	 * @param key  
	 * @return
	 */
	private Boolean Save4External(String name,String key, boolean autologin){
		Boolean isAutoLogin = false;
		try {
			ApplicationInfo appInfo = mContext.getApplicationInfo();
			Context ct = mContext.createPackageContext ( appInfo.packageName, Context.CONTEXT_IGNORE_SECURITY);
//			String packageName = ct.getApplicationInfo().packageName;
			SharedPreferences preferences = ct.getSharedPreferences(name, Context.MODE_PRIVATE);
			Editor e = preferences.edit();
			e.putBoolean(key, autologin);
			e.commit();
//		    isAutoLogin = preferences.getBoolean(key, false);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isAutoLogin;
	}
}
