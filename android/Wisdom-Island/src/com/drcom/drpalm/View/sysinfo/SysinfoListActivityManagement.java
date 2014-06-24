package com.drcom.drpalm.View.sysinfo;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.Activitys.sysinfo.SysinfoDetailActivity;
import com.drcom.drpalm.Activitys.sysinfo.SysinfoListActivity;
import com.drcom.drpalm.DB.SystemInfoDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestGetEventListReloginCallback;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.MessageObject;
import com.drcom.drpalm.objs.SystemInfoItem;

public class SysinfoListActivityManagement {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2;	//更多请求返回成功
	private String KEY_REFLASHTIME = "sysinfoflashtime";
	
	private Context mContext;
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private SharedPreferences sp;	//15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	private SystemInfoDB mSystemInfoDB;
	
	public SysinfoListActivityManagement(Context c){
		mContext = c;
		
		sp = c.getSharedPreferences(KEY_REFLASHTIME, c.MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
		
		mSystemInfoDB = SystemInfoDB.getInstance(c,GlobalVariables.gSchoolKey);
	}
	
	public boolean GetData(final String lastupdate,final Handler h){
		//非在线登录/网络不通时,返回
//		LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
		if(HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT){	//LoginManager.OnlineStatus.ONLINE_LOGINED != instance.getOnlineStatus() ||
			return false;
		}
		
		/*
		 * 使用RequestOperationReloginCallback类回调，当SESSIONKEY失效时，会自动登录，再请求数据接口
		 * 注意：代码要使用private boolean isRequestRelogin = true;	登录SECCION超时重登录标志记录，以免不断重登造成死循环
		 */
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestGetEventListReloginCallback callback = new RequestGetEventListReloginCallback(){
			@Override
			public void onSuccess() {	//请求数据成功
//				super.onSuccess();
				Message message = Message.obtain();
				if(lastupdate.equals("0")){
					message.arg1 = UPDATEFINISH;	//刷新
				}else{
					message.arg1 = MOREFINISH;		//更多
				}
				message.obj = new MessageObject(true,false);
				h.sendMessage(message) ;
				
				Log.i("zjj", "通告列表:刷新成功");
			}
			
			@Override
			public void onCallbackError(String str) {
//				super.onError(str);
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = str;
				h.sendMessage(message);
				
				Log.i("zjj", "通告列表:刷新失败" + str);
			}
			
			@Override
			public void onLoading() {
				// TODO Auto-generated method stub
				Message message = Message.obtain();
				message.arg1 = MOREFINISH;		//更多
				message.obj = new MessageObject(true,false);
				h.sendMessage(message) ;
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
//				super.onReloginError();
				Log.i("zjj", "通告列表:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
//				super.onReloginSuccess();
				Log.i("zjj", "通告列表:自动重登录成功");
				if(isRequestRelogin){
					GetData(lastupdate,h);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetSysMsgs", new Object[]{lastupdate,callback},callback.getClass().getName());
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isOver15mins(){
		return (new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 15;
	}
	
	/**
	 * 保存刷新时间
	 */
	public void SaveReflashtime(){
    	editor.putLong(KEY_REFLASHTIME , new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
		editor.commit();
	}
	
	/**
	 * 选中某项
	 * @param c
	 */
	public void onListviewItemClick(Cursor c){
		SystemInfoItem sysinfoItem = mSystemInfoDB.retrieveSystemInfoItem(c);
		
		Intent i = new Intent(mContext, SysinfoDetailActivity.class);
		i.putExtra(SysinfoDetailActivity.KEY_SYSINFO_ID, sysinfoItem.msg_id + "");
		mContext.startActivity(i);

	}
	
	/**
	 * 取更多
	 * @param c
	 * @param h
	 */
	public void getMore(Cursor c,Handler h){
		SystemInfoItem sysinfoItem = mSystemInfoDB.retrieveSystemInfoItem(c);
		GetData((sysinfoItem.msg_lastupdate.getTime()/1000) + "",h);
	}
}
