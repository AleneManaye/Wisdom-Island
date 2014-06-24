package com.drcom.drpalm.View.sysinfo;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.SystemInfoDB;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.SystemInfoItem;

public class SysinfoDetailActivityManagement {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private Cursor mSysinfoCursor = null;
	private SystemInfoDB mSystemInfoDB ;
	private SettingManager setInstance ;	
	private String mUsername = "";	
	
	public SysinfoDetailActivityManagement(Context c){
		mSystemInfoDB = SystemInfoDB.getInstance(c,GlobalVariables.gSchoolKey);
		setInstance = SettingManager.getSettingManager(c);	
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
	}
	
	public boolean sendGetDetailRequest(final Handler h,final String id){
		//非在线登录/网络不通时,返回
//		LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
		if(HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT){	//LoginManager.OnlineStatus.ONLINE_LOGINED != instance.getOnlineStatus() ||
			return false;
		}
		
		
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onSuccess() {
//				GetDataInDB(id);
				
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
				Log.i("zjj", "通告列表:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "通告列表:自动重登录成功");
				if(isRequestRelogin){
					sendGetDetailRequest(h,id);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetSysMsgContent", new Object[]{id, callback},callback.getClass().getName());
		return true;
	}
	
	/**
	 * 从库读取
	 */
	public SystemInfoItem GetDataInDB(String id){
		SystemInfoItem systemInfoItem;
		mSysinfoCursor = mSystemInfoDB.getSystemInfoCursor(mUsername, id);
		mSysinfoCursor.requery();
		mSysinfoCursor.moveToFirst();
		systemInfoItem = mSystemInfoDB.retrieveSystemInfoItem(mSysinfoCursor);
		mSysinfoCursor.close();
		
		return systemInfoItem;
	}
}
