/*
 SettingManager.java
 Create by JiangBo   2012-04-10
设置模块逻辑实现，SharedPreference读，写
 */
package com.drcom.drpalm.View.setting;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.danceinfo.AttendanceActivity;
import com.drcom.drpalm.Activitys.setting.AccountManageActivity;
import com.drcom.drpalm.DB.LoginDB;
import com.drcom.drpalm.Definition.SettingStringDefine;
import com.drcom.drpalm.Tool.SimpleEncrypt;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.XmlParse.UserInfoHandler;
import com.drcom.drpalm.Tool.jsonparser.AttendanceinfoListParser;
import com.drcom.drpalm.Tool.jsonparser.UserInfoParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.objs.AttendanceinfoListResultItem;
import com.drcom.drpalm.objs.MessageObject;
public class SettingManager{
	
	private static final int PUSHSET_SUCCESS = 1;
	private static final int PUSHSET_FAILED = 2;
	
	Context m_context = null;
	public List<UserInfo> usrinfolist;
	// 密钥   
    public static String strKey = "Drcom.Drpalm";   
    
	private UserInfoHandler m_UsrInfoHandler;
	
	public boolean  bOnlyWiFi = false;
	public boolean  bAutoRefresh = false;
	public boolean  bAutoRefreshNews = false;
	public boolean  bAutoRefreshEvents = false;
	public boolean  bVoicePrompt = false;
	public boolean  bVibrationPrompt = false;
	public boolean  bPush = false;
	public int      nPushStartTime = 0;
	public int      nPushEndTime   = 23;
	public UserInfo mCurrentUserInfo = null;
	public UserInfo mUpdateUserInfo = null;
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	
	private String  strXMLUserInfo = "";
	private LoginDB mLoginDB;
	
	// add by Kingsley
	public String mMobileWebDomain = "";
	public String mSessionKey = "";
	public String mSchoolId = "";
	
	// add by Kingsley
	static private SettingManager mSettingManager = null;
	public static SettingManager getSettingManager(Context context)
	{
		if(null == mSettingManager){
			mSettingManager = new SettingManager(context);
			mSettingManager.loadSetting();
		}
    	return mSettingManager;
	}
	
	public static void Destroy(){
		mSettingManager = null;
	}
	
	public SettingManager(Context context) {
		// TODO Auto-generated constructor stub
		m_context = context;
		mLoginDB = LoginDB.getInstance(context, GlobalVariables.gSchoolKey);
		//init();
	}
	
//	public void  init()
//	{
//		usrinfolist = new LinkedList<UserInfo>();
//		for (int i = 0; i < 2; i++) {
//			UserInfo usrinfo = new UserInfo();
//			usrinfo.bAutoLogin = true;
//			usrinfo.bParentLock = true;
//			usrinfo.bRememberPwd = true;
//			usrinfo.nUsrType = i;
//			usrinfo.strUsrName = "usr"+i;
//			usrinfo.strPassword = "password"+i;
//			usrinfo.strParentLockPwd = "parentlockpwd"+i; 
//			usrinfolist.add(usrinfo);
//		}
//		strXMLUserInfo = UsrInfo2XML(usrinfolist);
//		return;
//		//writeToXml(m_context, strXMLUserInfo);
//	}
	
	public void setUserInfo(UserInfo userInfo)
	{
		mCurrentUserInfo = userInfo;
//		//遍历     
//		for(UserInfo user : usrinfolist)
//		{    
//			//删除旧的，加入新的
//			if(user.strUsrName.contentEquals(userInfo.strUsrName))
//			{
//				usrinfolist.remove(user);
//				break;
//				//usrinfolist.add(userInfo);
//				//return;
//			}         
//		}  
//		UserInfo info = new UserInfo();
//		info.bAutoLogin   = userInfo.bAutoLogin;
//		info.bRememberPwd = userInfo.bRememberPwd;
//		info.bParentLock  = userInfo.bParentLock;
//		info.nUsrType     = userInfo.nUsrType;
//		info.strUsrName   = userInfo.strUsrName;
//		info.strPassword  = userInfo.strPassword;
//		info.strParentLockPwd = userInfo.strParentLockPwd;
//		info.strUserNickName = userInfo.strUserNickName;
//		usrinfolist.add(info);
		
		mLoginDB.updataUserSetting(userInfo);
		
		return;
	}
	
	public UserInfo getUserInfoByName(String username)
	{
		UserInfo userinfo = new UserInfo();
//		if(usrinfolist != null)
//		{
//			for(UserInfo info : usrinfolist)
//			{
//				if(info.strUsrName.contentEquals(username))
//				{
//					userinfo.bAutoLogin = info.bAutoLogin;
//					userinfo.bRememberPwd = info.bRememberPwd;
//					userinfo.bParentLock = info.bParentLock;
//					userinfo.nUsrType = info.nUsrType;
//					userinfo.strUsrName = info.strUsrName;
//					userinfo.strPassword = info.strPassword;
//					userinfo.strParentLockPwd = info.strParentLockPwd;
//					userinfo.strUserNickName = info.strUserNickName;
//					break;
//				}
//			}
//		}
		
		userinfo = mLoginDB.getLoginInfoByName(username);
		
		return userinfo;
	}
	
	public UserInfo getCurrentUserInfo()
	{
//		if(mCurrentUserInfo == null)
//		{
			mCurrentUserInfo = new UserInfo();
			
//			if(usrinfolist != null){
//				if(!usrinfolist.isEmpty()){
//					mCurrentUserInfo.bAutoLogin = usrinfolist.get(usrinfolist.size()-1).bAutoLogin;
//					mCurrentUserInfo.bRememberPwd = usrinfolist.get(usrinfolist.size()-1).bRememberPwd;
//					mCurrentUserInfo.bParentLock = usrinfolist.get(usrinfolist.size()-1).bParentLock;
//					mCurrentUserInfo.nUsrType = usrinfolist.get(usrinfolist.size()-1).nUsrType;
//					mCurrentUserInfo.strUsrName = usrinfolist.get(usrinfolist.size()-1).strUsrName;
//					mCurrentUserInfo.strPassword = usrinfolist.get(usrinfolist.size()-1).strPassword;
//					mCurrentUserInfo.strParentLockPwd = usrinfolist.get(usrinfolist.size()-1).strParentLockPwd;
//					mCurrentUserInfo.strUserNickName = usrinfolist.get(usrinfolist.size()-1).strUserNickName;
//					//mCurrentUserInfo = usrinfolist.get(usrinfolist.size()-1);
//				}
//			}
			
//			List<UserInfo> list = null;
			
			Cursor c = mLoginDB.getLoginInfoListCursor();
			if(c.getCount()>0){
				c.requery();
				c.moveToFirst();
				mCurrentUserInfo = mLoginDB.retrieveLoginInfoItem(c);
			}
			c.close();
			
//		}
		return mCurrentUserInfo;
	}
	
	public void GetUpdateUserInfo(final String lastupdate,final Handler handler,final UserInfo currentUserInfo){
		if(handler != null){
			Message msg = Message.obtain();
			msg.arg1 = AccountManageActivity.LOADING;
			handler.sendMessage(msg);
		}
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onCallbackError(String str) {
				if(handler != null){
					Message message = Message.obtain();
					message.arg1 = AccountManageActivity.UPDATEFAILED;
					message.obj = str;
					handler.sendMessage(message);
				}
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zhr", "取基本资料:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				
				if(handler != null){
					Log.i("zhr", "取基本资料:自动重登录成功");
					if(isRequestRelogin){
						GetUpdateUserInfo(lastupdate,handler,currentUserInfo);	//自动登录成功后，再次请求数据
						isRequestRelogin = false;
					}
				}
			}
			
			@Override
			public void onSuccess(Object obj) {	
				if(handler != null){
					mUpdateUserInfo = (UserInfo)obj;
					mUpdateUserInfo.strUsrName = currentUserInfo.strUsrName;
					mLoginDB.updataUserNewmsg(mUpdateUserInfo);
					Message message = Message.obtain();
					message.arg1 = AttendanceActivity.UPDATEFINISH;	//刷新
					message.obj = new MessageObject(true,false);
					handler.sendMessage(message);
				}
			}				
		};
		UserInfoParser parser = new UserInfoParser();
		RequestManager.GetUserInfo(lastupdate, parser, callback);
	}
	
	
//	public String UsrInfo2XML(List<UserInfo> users){ 
//		//实现xml信息序列号的一个对象  
//		XmlSerializer serializer=Xml.newSerializer();       
//		StringWriter writer=new StringWriter();        
//		try {           
//			//xml数据经过序列化后保存到String中，然后将字串通过OutputStream保存为xml文件           
//			serializer.setOutput(writer);                        
//			//文档开始       
//			serializer.startDocument("utf-8", true);                       
//			//开始一个节点        
//			serializer.startTag("", "List");     
//			//开始一个子节点       
//			for(UserInfo user : users)
//			{               
//				serializer.startTag("", "item");                
//				//serializer.attribute("", "id", String.valueOf(1));  
//				
//				serializer.startTag("", SettingStringDefine.USERNAME);                
//				serializer.text(user.strUsrName);                
//				serializer.endTag("", SettingStringDefine.USERNAME);  
//				
//				serializer.startTag("", SettingStringDefine.USERNICKNAME);                
//				serializer.text(user.strUserNickName);                
//				serializer.endTag("", SettingStringDefine.USERNICKNAME);  
//				
//				serializer.startTag("", SettingStringDefine.PASSWORD);
//				//加密密码
//				String strEncryPwd = "";
//				try
//				{
//					strEncryPwd = SimpleEncrypt.encrypt(strKey, user.strPassword);
//				}
//				catch(Exception e){
//				}
//				serializer.text(strEncryPwd);   
//				serializer.endTag("", SettingStringDefine.PASSWORD); 
//				
//				serializer.startTag("", SettingStringDefine.USERTYPE);                
//				serializer.text(String.valueOf(user.nUsrType));                
//				serializer.endTag("", SettingStringDefine.USERTYPE);    
//				
//				serializer.startTag("", SettingStringDefine.REMEMBERPWD);                
//				serializer.text(String.valueOf(user.bRememberPwd));               
//				serializer.endTag("", SettingStringDefine.REMEMBERPWD); 
//				
//				serializer.startTag("", SettingStringDefine.AUTOLOGIN);                
//				serializer.text(String.valueOf(user.bAutoLogin));               
//				serializer.endTag("", SettingStringDefine.AUTOLOGIN); 
//				
//				serializer.startTag("", SettingStringDefine.PARENTLOCK);                
//				serializer.text(String.valueOf(user.bParentLock));               
//				serializer.endTag("", SettingStringDefine.PARENTLOCK); 
//				
//				//加密家长锁密码
//				serializer.startTag("", SettingStringDefine.PARENTLOCKPWD);    
//				String strEncryParentLockPwd = "";
//				try
//				{
//					strEncryParentLockPwd = SimpleEncrypt.encrypt(strKey, user.strParentLockPwd);
//				}
//				catch(Exception e){
//				}
//				serializer.text(strEncryParentLockPwd);                
//				serializer.endTag("", SettingStringDefine.PARENTLOCKPWD); 
//				
//				serializer.endTag("", "item");            
//			}                        
//			serializer.endTag("", "List");            
//				//关闭文档            
//			serializer.endDocument();        
//		} 
//		catch (IllegalArgumentException e) {           
//			// TODO Auto-generated catch block          
//			e.printStackTrace();       
//			} 
//		catch (IllegalStateException e) {            
//			// TODO Auto-generated catch block            
//			e.printStackTrace();        
//			} 
//		catch (IOException e) {            
//			// TODO Auto-generated catch block            
//			e.printStackTrace();        
//			}       
//		return writer.toString();   
//	}
	
//	public boolean saveSchoolInfo()
//	{
//		if(m_context == null)
//			return false;
//		SharedPreferences sp = m_context.getSharedPreferences("SchoolInfo", Activity.MODE_PRIVATE);
//		Editor editor = sp.edit();//获取编辑器  
//		editor.putString("SchoolKey", "abcdefg");
//		editor.putString("CenterIp", "59.41.11.11");
//		boolean bReturn = editor.commit();//提交修改 
//		return bReturn;
//	}
//	
//	public boolean loadSchoolInfo()
//	{
//		if(m_context == null)
//			return false;
//		SharedPreferences sp = m_context.getSharedPreferences("SchoolInfo", Activity.MODE_PRIVATE);
//		String strSchoolKey = sp.getString("SchoolKey", "");
//		
//		return true;
//		
//	}
	
	public boolean saveSetting()
	{
		if(m_context == null)
			return false;
		
		Log.i("zjj", "~~~~~~~~~~~~~~~~save~~~~~~~~~~~~~~~~~~~~~~~~~" + GlobalVariables.gSchoolKey);
		SharedPreferences sp = m_context.getSharedPreferences(GlobalVariables.gSchoolKey + "Setting", Activity.MODE_PRIVATE);
		Editor editor = sp.edit();//获取编辑器  
		
		editor.putBoolean(SettingStringDefine.ONLYWIFI,bOnlyWiFi);
		editor.putBoolean(SettingStringDefine.AUTOREFRESH,bAutoRefresh);
		editor.putBoolean(SettingStringDefine.AUTOREFRESHNEWS, bAutoRefreshNews);
		editor.putBoolean(SettingStringDefine.AUTOREFRESHNEVENTS, bAutoRefreshEvents);
		editor.putBoolean(SettingStringDefine.VOICEPROMPT, bVoicePrompt);
		editor.putBoolean(SettingStringDefine.VIBRATIONPROMPT, bVibrationPrompt);
		editor.putBoolean(SettingStringDefine.PUSH, bPush);
		editor.putInt(SettingStringDefine.PUSHSTARTTIME, nPushStartTime);
		editor.putInt(SettingStringDefine.PUSHENDTIME, nPushEndTime);
//		strXMLUserInfo = UsrInfo2XML(usrinfolist);
//		editor.putString(SettingStringDefine.USERINFO, strXMLUserInfo);
		boolean bReturn = editor.commit();//提交修改 
		return bReturn;
	}
	
	public boolean loadSetting()
	{
		if(m_context == null)
			return false;
		
		Log.i("zjj", "~~~~~~~~~~~~~~~~load~~~~~~~~~~~~~~~~~~~~~~~~~" + GlobalVariables.gSchoolKey);
		
		SharedPreferences sp = m_context.getSharedPreferences(GlobalVariables.gSchoolKey + "Setting", Activity.MODE_PRIVATE);
		bOnlyWiFi = sp.getBoolean(SettingStringDefine.ONLYWIFI, false);
		bAutoRefresh = sp.getBoolean(SettingStringDefine.AUTOREFRESH, false);
		bAutoRefreshNews = sp.getBoolean(SettingStringDefine.AUTOREFRESHNEWS, false);
		bAutoRefreshEvents = sp.getBoolean(SettingStringDefine.AUTOREFRESHNEVENTS, false);
		bVoicePrompt = sp.getBoolean(SettingStringDefine.VOICEPROMPT, false);
		bVibrationPrompt = sp.getBoolean(SettingStringDefine.VIBRATIONPROMPT, false);
		bPush  = sp.getBoolean(SettingStringDefine.PUSH, false);
		nPushStartTime = sp.getInt(SettingStringDefine.PUSHSTARTTIME, 0);
		nPushEndTime = sp.getInt(SettingStringDefine.PUSHENDTIME, 23);
//		String str = sp.getString(SettingStringDefine.USERINFO, "");
//		
//		
//		// 创建一个新的字符串
//        StringReader read = new StringReader(str);
//        // 创建输入源 使用 InputSource 对象来确定读取XML
//        InputSource mInputSource = new InputSource(read);
//        try {
//         SAXParserFactory msSaxParserFactory = SAXParserFactory.newInstance();
//         SAXParser msSaxParser = msSaxParserFactory.newSAXParser();
//         XMLReader msXmlReader = msSaxParser.getXMLReader();
//         
//         m_UsrInfoHandler = new UserInfoHandler();
//         msXmlReader.setContentHandler(m_UsrInfoHandler);
//         msXmlReader.parse(mInputSource);
//        } catch (Exception e) {
//         
//        }
//        usrinfolist=m_UsrInfoHandler.getParsedData();
		return true;
	}
	
	public void setPushInfo2Server(boolean isPush,boolean isSound,boolean isShake,String pushTime,final Handler h)
	{
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {
			@Override
			public void onError(String str) {
				Message msg = new Message();
				msg.arg1 = PUSHSET_FAILED;
				msg.obj = str;
				h.sendMessage(msg);
			}

			@Override
			public void onSuccess() {
				Message msg = new Message();
				msg.arg1 = PUSHSET_SUCCESS;
				h.sendMessage(msg);
			}
		};
		mRequestOperation.sendGetNeededInfo("PushInfo", new Object[] { isPush, isSound, isShake, pushTime, callback }, callback.getClass().getName());
	}
}
