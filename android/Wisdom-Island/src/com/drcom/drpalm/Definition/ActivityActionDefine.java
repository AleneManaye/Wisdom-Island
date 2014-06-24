package com.drcom.drpalm.Definition;

import com.drcom.drpalm.GlobalVariables;


public class ActivityActionDefine {
//	public static final String PUSH_ACTION = "drcom.action.Activity_PUSH_ACTION";
//	public static final String POPACTIVITY_ACTION = "drcom.action.Activity_POPACTIVITY_ACTION";
//	public static final String UPDATETIME_ACTION = "drcom.action.SlightBar_UPDATENEWSTIME_ACTION";
//	public static final String NOTIFICATION_ACTION = "drcom.action.SlightBar_NOTIFICATION_ACTION";
//	public static final String NO_DOWNLOADTOURS_ACTION = "drcom.action.Resource_NO_DOWNLOADTOURS_ACTION";
//	public static final String DOWNLOADTOURS_ACTION = "drcom.action.Resource_DOWNLOADTOURS_ACTION";
//	public static final String PUSH_GETMESSAGE_ACTION = "com.drcom.drpalm.PUSH_GETMESSAGE_ACTION";

	//public static final String PACKAGENAME = "com.drcom.drpalmcqgs";
	public static final String PACKAGENAME = GlobalVariables.gAppContext.getPackageName();

	public static final String PUSH_ACTION = PACKAGENAME+".action.Activity_PUSH_ACTION";
	public static final String POPACTIVITY_ACTION = PACKAGENAME+".action.Activity_POPACTIVITY_ACTION";
	public static final String UPDATETIME_ACTION = PACKAGENAME+".action.SlightBar_UPDATENEWSTIME_ACTION";
	public static final String NOTIFICATION_ACTION = PACKAGENAME+".action.SlightBar_NOTIFICATION_ACTION";
	public static final String NO_DOWNLOADTOURS_ACTION = PACKAGENAME+".action.Resource_NO_DOWNLOADTOURS_ACTION";
	public static final String DOWNLOADTOURS_ACTION = PACKAGENAME+".action.Resource_DOWNLOADTOURS_ACTION";
	public static final String LOGIN_STATUS_CHANGED   =  PACKAGENAME+ ".action.LOGIN_STATUS_CHANGED";

	public static final String PUSH_GETMESSAGE_ACTION = PACKAGENAME+".PUSH_GETMESSAGE_ACTION";
	public static final String PUSH_UPGRADEAPP_ACTION = PACKAGENAME+".PUSH_UPGRADEAPP_ACTION";	//PUSH中的更新广播
	public static final String UPGRADEAPP_ACTION = PACKAGENAME+".UPGRADEAPP_ACTION";	//更新广播(界面处理)
	public static final String UPGRADEAPP_CANCEL_ACTION = PACKAGENAME+".UPGRADEAPP_CANCEL_ACTION";	//取消更新广播(通知栏处理)
	public static final String EXITAPP_ACTION = PACKAGENAME+".EXITAPP_ACTION";	//退出应用
	public static final String LOGIN_AND_SHOWNEWEVENTS_ACTION = PACKAGENAME+".LOGIN_AND_SHOWNEWEVENTS_ACTION";	//登录并显示最新
	public static final String CHANGEUSERPIC_ACTION = PACKAGENAME+".CHANGEUSERPIC_ACTION";	//更换头像成功
	public static final String CLOSE_UNDERGROUND_ACTIVITY = PACKAGENAME+".CLOSE_UNDERGROUND_ACTIVITY";	//关闭undergroud界面
	public static final String GET_NEW_MSG_ACTION = PACKAGENAME+".GET_NEW_MSG_ACTION";	//刷新主界面 未读数广播
	public static final String SHOWLOGIN_ACTION = PACKAGENAME+".SHOWLOGIN_ACTION";	//打开登录界面 广播


	public static final String CLEAR_NOTIFICATION = "clear_notification";
	public static final String PUSH_MESSAGE_COUNT = "push_message_count";
	public static final String PUSH_MESSAGE = "push_message";
	public static final String UPDATE_TYPE = "update_type";
	public static final String UPDATE_TIME = "update_time";

	public static final String PUSH_UPGRADE = "push_upgrade";

	public static final String NOTIFICATION_TIP = "notification_tip";
	public static final String UNGETCOUNT_ACTION = "ungetcount_action";
	public static final String UNGETCOUNT = "ungetcount";
	public static final String UNGETCOUNT_STATUE = "ungetcount_statue";
	public static final String TOURS_ITEM_LIST = "tours_item_list";
	public static final String TOURS_LASTUPDATE_DATE = "tours_lastupdate_date";
	public static final String TORESREPLACE_ISCUSTOMIZE = "ToResourceReplace_IsCustomize";
	
	public static final String EVENTS_UNREAD_SUM_DESC = PACKAGENAME+".EVENTS_UNREAD_SUM_DESC";	//通告未读条数-1
	public static final String EVENTS_TYPE_ID = "EVENTS_TYPE_ID";
	public static final String EVENTS_DES_SUM = "EVENTS_DES_SUM";	//主界面图标,数字减少数目
	public static final String EVENTS_DES_REPLACE = "EVENTS_DES_REPLACE";	//主界面图标,数字是增减还是替换
	
	public static final String NEWS_UNREAD_SUM_DESC = PACKAGENAME+".NEWS_UNREAD_SUM_DESC";	//新闻未读条数-1
	public static final String NEWS_TYPE_ID = "NEWS_TYPE_ID";
}
