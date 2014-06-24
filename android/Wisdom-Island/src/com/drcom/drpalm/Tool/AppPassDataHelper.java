package com.drcom.drpalm.Tool;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.objs.PushSettingInfo;
import com.drcom.drpalm.objs.PushSettingInfo.PushTime;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * 
 * @author MCX
 *	与外部程序通信调用参数传递解释类
 *	
 */
public class AppPassDataHelper {
	
	public static String KEY_SCHOOL_PACKAGENAME = "KEY_SCHOOL_PACKAGENAME";    //启动时，传入定制端包名  
	public static String KEY_START_FROM_PUSH = "KEY_START_FROM_PUSH"; //push启动标志
	public static String KEY_START2SCHOOL = "KEY_START2SCHOOL";	//启动时,中转到校园界面 	Boolean
	public static String KEY_START2CLASS = "KEY_START2CLASS";	//启动时,中转到班级界面	Boolean
	public static String KEY_START2MORE = "KEY_START2MORE";		//启动时,中转到更多界面	Boolean
	public static String KEY_START_SCHOOLKEY = "KEY_START_SCHOOLKEY";	//启动时,传入指定SCHOOLKEY	String
	public static String LOGIN_SUCCESS_BUNDLE_KEY = "loginsuccessbundlekey";  //登录成功传过去 userinfo 封装
	
	public static String ACTION_EXIT_APP = "EBABY_ACTION_EXIT_APP";	//退出程序广播
	
	//登录成功传递参数
	public static final String LOIG_SUCCESS_SESSION = "sessionKey";
	public static final String LOIG_SUCCESS_HEAD_LASTUPDATE = "headLastupdate";
	public static final String LOIG_SUCCESS_LASTUPDATE = "lastupdate";
	public static final String LOIG_SUCCESS_REMEBER_PASSWORD = "rememberPassword";
	public static final String LOIG_SUCCESS_AUTOLOGIN = "autologin";
	public static final String LOIG_SUCCESS_PARENT_LOCK = "parentLock";
	public static final String LOIG_SUCCESS_USER_TYPE = "userType";
	public static final String LOIG_SUCCESS_USER_NAME = "userName";
	public static final String LOIG_SUCCESS_PASSWORD = "password";
	public static final String LOIG_SUCCESS_PARENT_LOCK_PASSWORD = "parentLockPassword";
	public static final String LOIG_SUCCESS_USER_NICK_NAME = "userNickName";
	public static final String LOIG_SUCCESS_LEVEL = "level";
	public static final String LOIG_SUCCESS_LEVELUP_SCORE = "levelupScore";
	public static final String LOIG_SUCCESS_CURSCORE = "curScore";
	public static final String LOIG_SUCCESS_HEAD_URL = "headUrl";
	public static final String LOGIN_SUCCESS_AVATAR = "avatar";//头像
	
	//pushsetting相关
	public static final String LOIG_SUCCESS_PUSH_IFPUSH = "ifpush";
	public static final String LOIG_SUCCESS_PUSH_IFSOUND = "ifsound";
	public static final String LOGIN_SUCCESS_PUSH_IFSHAKE = "ifshake";
	public static final String LOGIN_SUCCESS_PUSH_TIMELIST = "pushTimeList";
	
	public void packAppData(Bundle bundle, UserInfo userInfo){
		bundle.putString(LOIG_SUCCESS_SESSION, userInfo.sessionKey);
		bundle.putString(LOIG_SUCCESS_HEAD_LASTUPDATE, userInfo.headlastupdate);
		bundle.putString(LOIG_SUCCESS_LASTUPDATE, userInfo.lastupdate);
		bundle.putString(LOIG_SUCCESS_USER_TYPE, userInfo.strUsrType);
		bundle.putString(LOIG_SUCCESS_USER_NAME, userInfo.strUsrName);
		bundle.putString(LOIG_SUCCESS_PASSWORD, userInfo.strPassword);
		bundle.putString(LOIG_SUCCESS_PARENT_LOCK_PASSWORD, userInfo.strParentLockPwd);
		bundle.putString(LOIG_SUCCESS_USER_NICK_NAME, userInfo.strUserNickName);
		bundle.putString(LOIG_SUCCESS_LEVEL, userInfo.level);
		bundle.putString(LOIG_SUCCESS_LEVELUP_SCORE, userInfo.levelupscore);
		bundle.putString(LOIG_SUCCESS_CURSCORE, userInfo.curscore);
		bundle.putString(LOIG_SUCCESS_HEAD_URL, userInfo.headurl);
		
		bundle.putBoolean(LOIG_SUCCESS_REMEBER_PASSWORD, userInfo.bRememberPwd);
		bundle.putBoolean(LOIG_SUCCESS_AUTOLOGIN, userInfo.bAutoLogin);
		bundle.putBoolean(LOIG_SUCCESS_PARENT_LOCK, userInfo.bParentLock);
		bundle.putBoolean(LOIG_SUCCESS_PUSH_IFPUSH, userInfo.pushSetting.ifpush);
		bundle.putBoolean(LOIG_SUCCESS_PUSH_IFSOUND, userInfo.pushSetting.ifsound);
		bundle.putBoolean(LOGIN_SUCCESS_PUSH_IFSHAKE, userInfo.pushSetting.ifshake);
		if(userInfo.pic != null){
			bundle.putByteArray(LOGIN_SUCCESS_AVATAR, Bitmap2Bytes(userInfo.pic));
		}
		bundle.putStringArrayList(LOGIN_SUCCESS_PUSH_TIMELIST, packPushTimeList(userInfo.pushSetting.pushTime));
	}
	
	/**
	 * 解析 UserInfo 对像
	 * @param bundle
	 * @return
	 */
	public UserInfo unpackAppData(Bundle bundle){
		UserInfo userInfo = new UserInfo();
		userInfo.sessionKey = bundle.getString(LOIG_SUCCESS_SESSION);
		userInfo.headlastupdate = bundle.getString(LOIG_SUCCESS_HEAD_LASTUPDATE);
		userInfo.lastupdate = bundle.getString(LOIG_SUCCESS_LASTUPDATE);
		userInfo.strUsrType = bundle.getString(LOIG_SUCCESS_USER_TYPE);
		userInfo.strUsrName = bundle.getString(LOIG_SUCCESS_USER_NAME);
		userInfo.strPassword = bundle.getString(LOIG_SUCCESS_PASSWORD);
		userInfo.strParentLockPwd = bundle.getString(LOIG_SUCCESS_PARENT_LOCK_PASSWORD);
		userInfo.strUserNickName = bundle.getString(LOIG_SUCCESS_USER_NICK_NAME);
		userInfo.level = bundle.getString(LOIG_SUCCESS_LEVEL);
		userInfo.levelupscore = bundle.getString(LOIG_SUCCESS_LEVELUP_SCORE);
		userInfo.curscore = bundle.getString(LOIG_SUCCESS_CURSCORE);
		userInfo.headurl = bundle.getString(LOIG_SUCCESS_HEAD_URL);
		
		userInfo.bRememberPwd = bundle.getBoolean(LOIG_SUCCESS_REMEBER_PASSWORD);
		userInfo.bAutoLogin = bundle.getBoolean(LOIG_SUCCESS_AUTOLOGIN);
		userInfo.bParentLock = bundle.getBoolean(LOIG_SUCCESS_PARENT_LOCK);
		userInfo.pushSetting.ifpush = bundle.getBoolean(LOIG_SUCCESS_PUSH_IFPUSH);
		userInfo.pushSetting.ifsound = bundle.getBoolean(LOIG_SUCCESS_PUSH_IFSOUND);
		userInfo.pushSetting.ifshake = bundle.getBoolean(LOGIN_SUCCESS_PUSH_IFSHAKE);
		if(bundle.containsKey(LOGIN_SUCCESS_AVATAR)){
			userInfo.pic = Bytes2Bimap(bundle.getByteArray(LOGIN_SUCCESS_AVATAR));
		}
		userInfo.pushSetting.pushTime = unpackPushTimeList(bundle.getStringArrayList(LOGIN_SUCCESS_PUSH_TIMELIST));
		return userInfo;
	}
	
	//bitmap 转 byte[]
	private byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	//byte[] 转 Bitmap
	private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
	
	//package pushtime list
	private ArrayList<String> packPushTimeList(List<PushTime> list){
		ArrayList<String> pushTimeList = new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			String pushTimeString = "";
			PushTime item = new PushTime();
			item = list.get(i);
			pushTimeString += item.start;
			pushTimeString +=",";
			pushTimeString += item.end;
			pushTimeList.add(pushTimeString);
		}
		return pushTimeList;
	}
	
	//unpack pushtime list
	private List<PushTime> unpackPushTimeList(ArrayList<String> list){
		List<PushTime> pushTimeList = new ArrayList<PushSettingInfo.PushTime>();
		for(int i=0;i<list.size();i++){
			PushTime item = new PushTime();
			String [] temp = null;
			temp = list.get(i).split(",");
			if(temp.length>1){
				item.start = temp[0];
				item.end = temp[1];
				pushTimeList.add(item);
			}	
		}
		return pushTimeList;
	}
}
