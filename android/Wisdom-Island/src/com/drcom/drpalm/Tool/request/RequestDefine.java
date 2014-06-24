package com.drcom.drpalm.Tool.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestDefine {
	// Error Code
	public static final String JsonParserError =  "999999";    //JSON解释错误
	public static final String InvalidSchoolKey =  "100001";   //无效School参数
	public static final String UnopenSchoolGatway = "100002";   //学校未开通网关
	public static final String UnknownClientType = "100060";    //未知客户端类型
	public static final String NoNewResourceDownLoad = "100061";   //网关没有新资源可下载
	public static final String NoResourceDownLoad = "100062";  //网关资源包为空
	public static final String NoLinkForPassword = "100063";    //未配置找回密码链接
	public static final String AccountIsRepeatBinding = "100005";//账号关联重复绑定(2014-06-16)

	public static final String AccountNotExist = "200001";   //帐号不存在
	public static final String LoginInfoNotMatch= "200002";   //帐号或密码错误
	public static final String LoginParamEmpty = "200003";   //登录帐号、密码、学校ID为空
	public static final String InvalidSessionKey = "200004";   //无效SessionKey
	public static final String InvalidSchoolId = "200005";   //无效的学校序列ID
	public static final String SchoolIdisEmpty = "200006";   //学校SCHOOLID不可为空
	public static final String MailFormatError = "200013";   //邮箱格式错误
	public static final String OvertimeSessionKey = "200014";   //SessionKey超时
	public static final String LoginInfoNoMoney = "200015";   //账号过期


	public static final String NotTeacher = "500001";   //非教师身份
	public static final String OrgNotExist = "500002";   //组织不存在
	public static final String OrgIdNotNull = "500005";   //组织机构ID不可为空
	public static final String OrgResourceNotNew = "500006";   //没有新组织机构资源下载

	public static final String EventStartDateEmpty = "600001";   //发布event起始时间为空
	public static final String EventEndDateEmpty = "600002";   //发布event结束时间为空
	public static final String ReceiverIDEmpty = "600003";   //发布event接收者ID列表为空
	public static final String ReceiverNameEmpty = "600004" ;  //发布event接收者名字列表为空
	public static final String EventLocationEmpty = "600005";   //发布event地点为空
	public static final String EventTypeEmpty = "600006";   //发布event类型为空
	public static final String EventTitleEmpty = "600007";   //发布event标题为空
	public static final String EventContentEmpty = "600008";   //发布event内容为空
	public static final String EventUnknowError = "600009";   //发布event未知异常
	public static final String EventInvalidEventID = "600010";   //无效event ID
	public static final String ReplyTitleEmpty = "600011";   //回复的标题为空
	public static final String ReplyContentEmpty = "600012";   //回复的内容为空
	
	public static final String CategoryIdNotNull = "700005";   //新闻分类ID不能为空
	public static final String ConsultTelOrMailNotNull = "700006";   //咨询回复电话或邮件地址不能为空
	public static final String ConsultContentNotNull = "700007";   //咨询内容不可为空
	public static final String ConsultNameNotNull = "700007";   //咨询姓名不可为空
	
	public static final String AttendanceErrorParam = "800001";		//参数不正确
	public static final String AttendanceOvertime = "800002";		//链接超时
	public static final String AttendanceErrorCheck = "800003";		//校验不正确
	
	// Gateway Request
	public static final String NETAPP_PATH = "netapp";
	public static final String WEBAPI_PATH = "webapi";
	public static final String GET_GATE_PATH = "org.drcom.drpalm.getAppGw.flow";
	public static final String GET_SCHOOLKEY_PATH = "org.drcom.drpalm.getSchoolKey.flow";
	public static final String GET_GATE_SCHOOLKEY = "schoolkey";
	public static final String GET_GATE_SEQID = "seqid";
	
	// CommonRequest
	// Login
	public static final String LOGIN_PATH = "login/";
	public static final String LOGIN_USERID = "userid";
	public static final String LOGIN_PWD = "pwd";
	public static final String LOGIN_TOKEN = "devicetoken";
	// Logout
	public static final String LOGOUT_PATH = "logout/";
	// Keep alive
	public static final String KEEP_SESSION = "sessionkey";
	public static final String KEEP_PATH = "keep/";
	// Push
	public static final String PUSH_PATH = "pushinfo/";
	public static final String PUSH_SWITCH = "ifpush";
	public static final String PUSH_SOUND = "ifsound";
	public static final String PUSH_SHAKE = "ifshake";
	public static final String PUSH_TIME = "pushtime";
	// News
	public static final String NEWS_OFFICE_PATH = "newsoffice/";
	public static final String NEWS_CHANNEL = "channel";
	
	// Tours
	public static final String TOURS_PATH = "webapi";
	public static final String TOURS_GETCLIPKG = "getclipkg/";
	public static final String TOURS_LASTMDATE = "lastmdate";
	public static final String TOURS_NUMNO = "numno";
	public static final String TOURS_DSPWIDTH = "dspwidth";
	public static final String TOURS_DSPHEIGHT = "dspheight";
	public static final String TOURS_OS = "os";
	public static final String TOURS_MODELNO = "modelno";
	public static final String TOURS_DPI = "dpi";
	public static final String TOURS_RESLIST = "reslist";
	public static final String TOURS_RESNAME = "resname";
	public static final String TOURS_URL = "url";
	public static final String TOURS_VERIFYCODE = "verifycode";
	
	//添加拿修改密码URL字段，并做相关接口修改
	public static final String TOURS_PACKET_OPERATE = "packet";
	public static final String TOURS_PSWURL_OPERATE = "getpwd";
	public static final String TOURS_ATTENDANCE_OPERATE = "getattendance";	//获取考勤信息
	
	// static method
	public static String convertStreamToString(InputStream stream) {        
//		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
//	    byte[] buffer = new byte[1];  
//	    int len = -1;  
//	    int c = 0;
//	    try {
//			while ((len = stream.read(buffer)) != -1) {  
//			    outSteam.write(buffer, 0, len); 
//			    Log.d("convertStreamToString", "index:" + c + "byte:" +Integer.toHexString(buffer[0]));
//        		c++;
//			}
//			outSteam.close();  
//		    stream.close(); 
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}  
//		return outSteam.toString();
	     
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder(); 
        String line = null;
        int read;        
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }        	
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
