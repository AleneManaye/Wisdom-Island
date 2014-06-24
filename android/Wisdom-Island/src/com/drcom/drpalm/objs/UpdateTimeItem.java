package com.drcom.drpalm.objs;

import java.util.Date;

public class UpdateTimeItem {
	
	public UpdateTimeItem(){
		update_time_last = new Date(0);
		update_time_save = new Date(0);
	}
	public String update_time_type = "";
	public Integer update_time_channel = -1;//消息或活动分类ID -1为无效
	public Date update_time_last = null; //最后更新时间，服务器返回
	public Date update_time_save = null; //本地个模块读取获得，用于和服务器对比
	public String update_unreadcount = "0";//未读取条数
	public String update_title = "";//模块最后一条更新的title
	public String user = "";
}
