package com.drcom.drpalm.View.danceinfo;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.danceinfo.AttendanceActivity;
import com.drcom.drpalm.DB.AttendanceinfoDB;
import com.drcom.drpalm.Tool.Encryption;
import com.drcom.drpalm.Tool.jsonparser.AttendanceinfoListParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.AttendanceinfoListResultItem;
import com.drcom.drpalm.objs.MessageObject;

public class AttendanceinfoManager {
	
	private String KEY_REFLASHTIME = "attendanceinfoflashtime";
	private AttendanceinfoDB mAttendanceinfoDB;
	private Context mContext;
	private String mUsername;
	private String mPassword;
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private SharedPreferences sp; // 15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	
	public AttendanceinfoManager(Context context){
		this.mContext = context;
		mAttendanceinfoDB = AttendanceinfoDB.getInstance(mContext,GlobalVariables.gSchoolKey);
		SettingManager setInstance = SettingManager.getSettingManager(context);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		mPassword = setInstance.getCurrentUserInfo().strPassword;
		
		sp = context.getSharedPreferences(KEY_REFLASHTIME, context.MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
	}
	
	public void destory(){
		mAttendanceinfoDB = null;
		sp = null;
		editor = null;
		mContext = null;
	}
	
	/**
	 * 是否需要自动刷新
	 * @return
	 */
	public boolean isTimeToReflash(){
		return (new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 5;
	}
	
	/**
	 * 保存刷新时间
	 */
	public void SaveReflashtime() {
		editor.putLong(KEY_REFLASHTIME, new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
		editor.commit();
	}
	
	/**
	 * 
	 * @param lastupdate
	 * @param handler
	 */
	public void GetAttendanceinfo(final String lastupdate, final Handler handler){
		if(handler != null){
			Message msg = Message.obtain();
			msg.arg1 = AttendanceActivity.LOADING;
			handler.sendMessage(msg);
		}
		
		/*
		 * 使用RequestOperationReloginCallback类回调，当SESSIONKEY失效时，会自动登录，再请求数据接口
		 * 注意：代码要使用private boolean isRequestRelogin = true;	登录SECCION超时重登录标志记录，以免不断重登造成死循环
		 */
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
//			@Override
//			public void onSuccess() {
//				if(handler != null){
//					if(lastupdate.equals("0")){
//						Message message = Message.obtain();
//						message.arg1 = AttendanceActivity.UPDATEFINISH;	//刷新
//						message.obj = new MessageObject(true,false);
//						handler.sendMessage(message);
//					}else{
//						Message message = Message.obtain();
//						message.arg1 = AttendanceActivity.MOREFINISH;	//取更多
//						message.obj = new MessageObject(true,false);
//						handler.sendMessage(message);
//					}
//					
//				}
//			}
			
			@Override
			public void onCallbackError(String str) {
				if(handler != null){
					Message message = Message.obtain();
					message.arg1 = AttendanceActivity.UPDATEFAILED;
					message.obj = str;
					handler.sendMessage(message);
				}
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "取考勤:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				
				if(handler != null){
					Log.i("zjj", "取考勤:自动重登录成功");
					if(isRequestRelogin){
						GetAttendanceinfo(lastupdate,handler);	//自动登录成功后，再次请求数据
						isRequestRelogin = false;
					}
				}
			}
			
			@Override
			public void onSuccess(Object obj) {	
				if(handler != null){
					Log.i("zjj", "取考勤列表请求结果   返回成功");
					AttendanceinfoListResultItem result = (AttendanceinfoListResultItem)obj;
					
					for(int i = 0 ; i<result.mAttendancelist.size(); i++){
						mAttendanceinfoDB.saveAttendanceinfo(result.mAttendancelist.get(i));
					}
					
					SaveReflashtime();
					
					if(lastupdate.equals("0")){
						Message message = Message.obtain();
						message.arg1 = AttendanceActivity.UPDATEFINISH;	//刷新
						message.obj = new MessageObject(true,false);
						handler.sendMessage(message);
					}else{
						Message message = Message.obtain();
						message.arg1 = AttendanceActivity.MOREFINISH;	//取更多
						message.obj = new MessageObject(true,false);
						handler.sendMessage(message);
					}
				}
			}				
		};
		
		AttendanceinfoListParser parser = new AttendanceinfoListParser();
		parser.SetUsername(mUsername);
		String challenge = System.currentTimeMillis()/1000 + "";
		RequestManager.GetAttendanceinfo(mUsername, lastupdate,getVerificationkey(challenge),challenge,parser, callback);
	}
	
	private String getVerificationkey(String challenge){
		StringBuffer sb = new StringBuffer();
		sb.append(Encryption.toMd5(mUsername+"@DrCOM_"+mPassword));
		sb.append("@");
		sb.append(challenge);
		String key = Encryption.toMd5(sb.toString());
		sb = null;
		return key;
	}
}
