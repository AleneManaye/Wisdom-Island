package com.drcom.drpalm.Tool.service;

import java.util.ArrayList;
import java.util.List;

import com.drcom.drpalm.objs.*;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;

public class DrServiceJni {
	private static final String LIBRARY_NAME = "DrService";
	static{
		try{
			System.loadLibrary(LIBRARY_NAME);
		}catch( Exception e){
			e.printStackTrace();
		}

	}
	/*
	 *  杈��Jni�ュ�
	 */
	static final String JNI_LOG_SAVE_PATH = "/sdcard/DrpalmServiceLog/";
	static final String JNI_LOG_NAME = "JniLog";
	static final long TIME_FOR_CLEAN_JNI_LOG = 10*24*3600*1000;
	public void WriteJniLog(byte[] value) {
		DrServiceLog.clearInvalidLog(JNI_LOG_SAVE_PATH, TIME_FOR_CLEAN_JNI_LOG);
		DrServiceLog.writeLog(JNI_LOG_SAVE_PATH, JNI_LOG_NAME, new String(value));
	}
	public native boolean NativeInit();
	//取MAC地址
	public native String GetFirstMacAddress();
	//取设备ID
	public native String GetDeviceId();
	//获取网关
	public native boolean GetNetworkGate(String domain, String schoolKey, String numno, String dpi, String dspwidth, String dspheight,
			String os, String modelno, String appver, String tokenid, String appname,DrServiceJniCallback jniHttpCallback);
	//public native boolean GetSchoolKey(String domain, String seqid, DrServiceJniCallback jniHttpCallback);
	//获取资源包
	public native boolean GetTours(String domain, String schoolid, String lastupdate, String dspwidth,
			String dspheight, String dpi, String numno, String os, String modelno, DrServiceJniCallback jniHttpCallback);
	//登录
	public native boolean LoginGateway(String domain,String schoolid, String userId, String pass, String indetify, String packageName, DrServiceJniCallback jniHttpCallback);
	//注销
	public native boolean Logout(String domain, String schoolid, String sessionkey, DrServiceJniCallback jniHttpCallback);
	//推送机制上传
	public native boolean PushInfo(String domain, String schoolid, String sessionkey, boolean bPush, boolean bSound, boolean bShake, String time, DrServiceJniCallback jniHttpCallback);
	public native boolean KeepAlive(String domain, String schoolid, String sessionkey, DrServiceJniCallback jniHttpCallback);
	//终端应用异常或人工报Bug
	public native boolean SubmitProblem(String domain, String schoolid, String sessionkey, String problem, String suggestion,DrServiceJniCallback jniHttpCallback);
	//设置用户邮箱
	public native boolean SetUserEmail(String domain,String schoolid,String sessionkey,String email,DrServiceJniCallback jniHttpCallback);
	//获取最新我的校园信息
	public native boolean GetNewsModuleInfoList(String domain,String schoolid,DrServiceJniCallback jniHttpCallback);
	//获取最新我的班级信息
	public native boolean GetEventsModuleInfoList(String domain,String schoolid,String sessionkey,DrServiceJniCallback jniHttpCallback);
	//获取各模块最后更新时间
	public native boolean GetLastUpdate(String domain,String schoolid,String sessionkey,String deviceId,boolean hassessionkey,DrServiceJniCallback jniHttpCallback);

	//获取地区或学校列表
	public native boolean GetSchoolList(String domain,String parentid,String numno , String appver, DrServiceJniCallback jniHttpCallback);
	//搜索学校
	public native boolean SearchSchool(String domain,String appid,String searchkey, String lang, DrServiceJniCallback jniHttpCallback);
	//获取新闻列表
	public native boolean GetNews(String domain, String schoolid, String category, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//获取新闻详细
	public native boolean GetNewsDetail(String domain,String schoolid,String story_id,String allfield,DrServiceJniCallback jniHttpCallback);
	//新闻搜索
	public native boolean SearchNews(String domain, String schoolid, String searchkey,String start,DrServiceJniCallback jniHttpCallback);
	//获取相册列表
	//入托咨询接口
	public native boolean PutConSult(String domain,String schoolid,String username,String email,String phone,String title,String content,String type,DrServiceJniCallback jniHttpCallback);
	//获取通告列表
	public native boolean GetEventList(String domain, String schoolid, String sessionkey, String category,String lastupdate, String lastreadtime, DrServiceJniCallback jniHttpCallback);
	//获取通告详细
	public native boolean GetEventDetail(String domain,String schoolid,String sessionkey,String eventid,String allfield,DrServiceJniCallback jniHttpCallback);
	//获取已发通告列表
	public native boolean GetPublishEventList(String domain, String schoolid, String sessionkey, String category,String lastupdate, DrServiceJniCallback jniHttpCallback);
	//获取已发通告详细
	public native boolean GetPublishEventDetail(String domain, String schoolid,String sessionkey,String eventid,DrServiceJniCallback jniHttpCallback);
	//发布通告
	public native boolean SubmitEvent4Kids(String domain, String schoolid, String sessionkey, String id, String type, String objtype,
			String oristatus, String ownerid, String owner, String start, String end,
			String shortloc, String title, String body, String emergent, String locurl, String thumbname, ArrayList<Attachment> fileList, DrServiceJniCallback jniHttpCallback);
	//自动回复
	public native boolean AutoAnswer(String domain, String schoolid, String sessionkey, String eventid,DrServiceJniCallback jniHttpCallback );
	//批量自动回复
	public native boolean AutoAnswerList(String domain, String schoolid, String sessionkey, String  eventids, DrServiceJniCallback jniHttpCallback);
	//获取反馈人列表
	public native boolean GetAnswerList(String domain, String schoolid, String sessionkey, String eventid, String albumid, DrServiceJniCallback jniHttpCallback);
	//获取反馈人有关回复
	public native boolean GetReplyInfo(String domain, String schoolid, String sessionkey, String eventid, String aswpubid, String lastawstime,DrServiceJniCallback jniHttpCallback);
	//反馈发送
	public native boolean ReplyPost(String domain, String schoolid, String sessionkey, String eventid, String aswpubid, String body,DrServiceJniCallback jniHttpCallback );

	//组织结构获取
	public native boolean GetOrganization(String domain, String schoolid, String sessionkey, String lastupdate,DrServiceJniCallback jniHttpCallback);
	//获取联系人交流内容
	public native boolean GetContactMsgs(String domain, String schoolid, String sessionkey, String contactid, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//交流内容发送
	public native boolean SendContactMsg(String domain, String schoolid, String sessionkey,String contactid, String body, DrServiceJniCallback jniHttpCallback);
	//获取系统消息列表
	public native boolean GetSysMsgs(String domain, String schoolid, String sessionkey, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//获取系统消息
	public native boolean GetSysMsgContent(String domain,String schoolid,String sessionkey,String sysmsgid, DrServiceJniCallback jniHttpCallback);
	//获取已读明细信息
	public native boolean GetEventReadInfo(String domain,String schoolid,String sessionkey,String eventid,DrServiceJniCallback jniHttpCallback);
	
	//3期
	//获取班级收藏夹接口
	public native boolean GetClassFavorite(String domain, String schoolid, String sessionkey, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//提交班级收藏接口
	public native boolean SubmitClassfav(String domain, String schoolid, String sessionkey, ArrayList<FavItem> fileList, DrServiceJniCallback jniHttpCallback);
	//获取基本资料接口
	public native boolean GetAccountinfo(String domain, String schoolid, String sessionkey, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//提交基本资料接口
	public native boolean SubmitAccountinfo(String domain, String schoolid, String sessionkey, byte[] picbyte, DrServiceJniCallback jniHttpCallback);
	//获取导航页显示列表
	public native boolean GetNavigation(String domain, String appid, DrServiceJniCallback jniHttpCallback);
	//获取个人相册接口
	public native boolean GetUseralbum(String domain, String schoolid, String sessionkey, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//提交个人相册接口
	public native boolean SubmitUseralbum(String domain, String schoolid, String sessionkey, ArrayList<MyPhoto> fileList, DrServiceJniCallback jniHttpCallback);
	//获取成长点滴接口
	public native boolean GetGrowdiary(String domain, String schoolid, String sessionkey, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//提交成长点滴接口
	public native boolean SubmitGrowdiary(String domain, String schoolid, String sessionkey, ArrayList<GrowdiaryItem> fileList, DrServiceJniCallback jniHttpCallback);
	//获取导航分类列表
	public native boolean GetNavigationList(String domain, String appid, DrServiceJniCallback jniHttpCallback);
	//2.3.3.1.8 删除通告
	public native boolean SubmitDelEvent(String domain, String schoolid, String sessionkey,String eventid, DrServiceJniCallback jniHttpCallback);
	//获取联系人列表
	public native boolean GetContactList(String domain, String schoolid, String sessionkey, ArrayList<ContactItem> contactList, DrServiceJniCallback jniHttpCallback);
	//2.3.3.1.11 提交班级回评接口
	public native boolean SubmitClassreview(String domain, String schoolid, String sessionkey,String evenitid, ArrayList<ReviewResult> fileList, DrServiceJniCallback jniHttpCallback);
	//2.3.5.1 获取考勤信息接口
	public native boolean GetAttendanceinfo(String domain, String schoolid, String userid,String lastupdate,String verificationkey,String challenge,DrServiceJniCallback jniHttpCallback);

	//4期
	//2.3.3.6.1 告假获取列表接口
	public native boolean GetLeavelist(String domain, String schoolid, String sessionkey, String lastupdate, DrServiceJniCallback jniHttpCallback);
	//2.3.3.6.2 告假获取详细接口
	public native boolean GetLeaveinfo(String domain, String schoolid, String sessionkey, String leaveid, String allfield, DrServiceJniCallback jniHttpCallback);
	//2.3.3.6.3 告假发送接口
	public native boolean SubmitLeave(String domain, String schoolid, String sessionkey, 
			String type, String ownerid, String owner,
			String start, String end, String title, String content, 
			ArrayList<Attachment> fileList, DrServiceJniCallback jniHttpCallback);
	//2.3.3.6.4 告假获取接收人列表接口
	public native boolean GetLeaveownerlist(String domain, String schoolid, String sessionkey, DrServiceJniCallback jniHttpCallback);
	//2.3.3.1.12 获取班级标题类型
	public native boolean GetEventtitlelist(String domain, String schoolid, String sessionkey, DrServiceJniCallback jniHttpCallback);
	//2.3.6.1 获取关联帐号列表
	public native boolean GetRelationaccount(String domain, String appid,String tokenid, DrServiceJniCallback jniHttpCallback);
    //2.3.6.2 添加关联帐号（http get方式）
    public native boolean Addrelationaccount(String domain, String account, String passwordMd5, String schoolkey, String tokenid, DrServiceJniCallback jniHttpCallback);
    //2.3.6.3 删除关联帐号（http post方式）
    public native boolean Delrelationaccount(String domain, List<BindaccountItem> accountlist, DrServiceJniCallback jniHttpCallback);

    //2.3.1.12 获取帐号信息（http post方式）
    public native boolean Getaccountsinfo(String domain,String tokenid, List<BindaccountItem> accounts, DrServiceJniCallback jniHttpCallback);
    
    // AES加解密
    public native String AesEncrypt(String src, String key);
    public native String AesDecrypt(String src, String key);
    
}
