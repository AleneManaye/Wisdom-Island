package com.drcom.drpalm.objs;

import java.util.Date;

public class SystemInfoItem {
	public SystemInfoItem(){
		msg_lastupdate = new Date();
		msg_inactivetime = new Date();
	}
	public Integer msg_id = -1;//消息ID -1 为无效
	public String msg_title = "";//消息标题
	public String msg_body = "";//消息内容
	public String msg_user = "";//当前用户
	public Date msg_lastupdate = null;//最后更新时间
	public Date msg_inactivetime = null;//失效时间
}
