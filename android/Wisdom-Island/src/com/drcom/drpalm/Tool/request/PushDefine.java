package com.drcom.drpalm.Tool.request;

public class PushDefine {
	// Events Request
	// Get Event Request
	public static final String PUSH_PATH = "";
	public static final String PUSH_NAME = "param";
	public static final String PUSH_CMD = "cmd";
	public static final String PUSH_RET = "ret";
	public static final String PUSH_GETCHANLLENGE = "getclientchanllenge";
	public static final String PUSH_CHANLLENGE = "chanllenge";
	public static final String PUSH_REGISTER = "register";
	public static final String PUSH_TOKENID = "tokenid";
	public static final String PUSH_APPID = "appid";
	public static final String PUSH_CHECKCODE = "checkcode";
	public static final String PUSH_MODEL = "model";
	public static final String PUSH_SYSTEM = "system";
	public static final String PUSH_APPVER = "appver";
	public static final String PUSH_BODY = "body";
	public static final String PUSH_GETMESSAGE = "getpushmessage";

	public static final String Succeed =  "0";   //成功
	public static final String ServerRejectRequest = "1001";   //服务器拒绝访问，稍后再试
	public static final String DataFormatIncorrect = "1101";   //数据格式不正确
	public static final String CheckError = "2001";   //checkcode不正确，验证失败
	public static final String DataOverflow= "2101";   //数据超长（数据段超过1024）

	public static final String PUSH_UPGRADE_VER = "applastver";
	public static final String PUSH_UPGRADE_URL = "apppkgurl";
	public static final String PUSH_UPGRADE_DES = "appverdesc";

	public static final String PUSH_GET_APS =  "aps";
	public static final String PUSH_GET_ALERT = "alert";
	public static final String PUSH_GET_BADGE = "badge";
	public static final String PUSH_GET_ETYPE = "etype";
	public static final String PUSH_GET_SOUND = "sound";

	public static final String CONTEXTLENGTH = "Context-Length:";
	public static final String SPERATOR = "\r\n\r\n";
}
