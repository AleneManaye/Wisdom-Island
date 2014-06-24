package com.drcom.drpalm.Definition;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 模块名管理类
 * @author zhaojunjie
 *
 */
public class ModuleNameDefine {
	/**
	 * 根据校园（新闻）模块ID取对应名字
	 * @param id
	 * @return
	 */
	public static String getNewsModuleNamebyId(int id){
		switch (id) {
		case RequestCategoryID.NEWS_NEWS_ID:
			return GlobalVariables.gAppContext.getString(R.string.news);
		case RequestCategoryID.NEWS_ACTIVITY_ID:
			return GlobalVariables.gAppContext.getString(R.string.activity);
		case RequestCategoryID.NEWS_ALBUM_ID:
			return GlobalVariables.gAppContext.getString(R.string.album);
		case RequestCategoryID.NEWS_INFANTDIET_ID:
			return GlobalVariables.gAppContext.getString(R.string.infant_diet);
		case RequestCategoryID.NEWS_PARENTING_ID:
			return GlobalVariables.gAppContext.getString(R.string.parenting);

		default:
			return "";
		}
	}
	
	/**
	 * 根据班级（通告）模块ID取对应名字
	 * @param id
	 * @return
	 */
	public static String getEventsModuleNamebyId(int id){
		switch (id) {
		case RequestCategoryID.EVENTS_NEWS_ID:
			return GlobalVariables.gAppContext.getString(R.string.classnews);
		case RequestCategoryID.EVENTS_ACTIVITY_ID:
			return GlobalVariables.gAppContext.getString(R.string.classactivity);
		case RequestCategoryID.EVENTS_COMMENT_ID:
			return GlobalVariables.gAppContext.getString(R.string.comment);
		case RequestCategoryID.EVENTS_ALBUM_ID:
			return GlobalVariables.gAppContext.getString(R.string.classalbum);
		case RequestCategoryID.EVENTS_COURSEWARE_ID:
			return GlobalVariables.gAppContext.getString(R.string.courseware);
		case RequestCategoryID.EVENTS_COMMUNION_ID:
			return GlobalVariables.gAppContext.getString(R.string.face2face);
		case RequestCategoryID.EVENTS_SEND_ID:
			return GlobalVariables.gAppContext.getString(R.string.sent);
		case RequestCategoryID.SYSINFO_ID:
			return GlobalVariables.gAppContext.getString(R.string.sysinfo);
		case RequestCategoryID.EVENTS_VIDEO_ID:
			return GlobalVariables.gAppContext.getString(R.string.video);
		case RequestCategoryID.EVENTS_INOUT_ID:
			return GlobalVariables.gAppContext.getString(R.string.hjt);
			
		default:
			return "";
		}
	}
}
