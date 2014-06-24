package com.drcom.drpalm.Tool.XmlParse;

import com.drcom.drpalm.objs.PushSettingInfo;

import android.R.integer;
import android.graphics.Bitmap;

/**
 * 用户信息
 * @author zhaojunjie
 *
 */
public class UserInfo {
	public static String USERTYPE_TEACHER = "0";
	public static String USERTYPE_PARENT = "1";
	public static String USERTYPE_STUDENT = "2";
	
	public boolean result = false;
	public String errorcode = "0";		//错误代码
	public String errordes = "";		//错误描述
	
	public String sessionKey = "";	
	public String headlastupdate = "";//头像最后更新时间
	public String lastupdate = "0";//最后更新时间
	
	public boolean bRememberPwd = true;
	public boolean bAutoLogin = false;
	public boolean bParentLock = false;
	public String  strUsrType = USERTYPE_STUDENT;        //用户类型:0表示教师,1表示家长,2表示学生
	public String  strUsrName = "";      //登陆名称
	public String  strPassword = "";
	public String  strParentLockPwd = "";
	public String  strUserNickName = "";     //用户称呼
	public String  level = "";  //用户等级
	public String  levelupscore = "";	//下一级所需积分
	public String  curscore = "";//当前积分
	public String  headurl = "";//头像url
	public Bitmap  pic;	//头像
	
	public String serviceenddate = "";//使用期限（自1970年1月1日起的秒数）
	
	public PushSettingInfo pushSetting = new PushSettingInfo();
	
//	public UserInfo(){
//		bRememberPwd = false;
//		bAutoLogin = false;
//		bParentLock = false;
//		nUsrType = 0;
//		strUsrName = "";
//		strPassword = "";
//		strParentLockPwd = "";
//		strUserNickName = "";
//		level = "";
//		levelupscore = "";
//		curscore = "";
//		pic = null;
//	}	
}
