package com.drcom.drpalm.objs;

import java.util.Date;

public class ContactItem {
	public ContactItem(){
		lastupdate = new Date();
	}
	public String cnid = "";//联系人ID -1为无效
	public String cnname = "";//联系人名字
	public Date lastupdate = new Date(0);//最后更新时间
	public Date lastawstimeread = new Date(0);//已阅读的最后反馈时间
	public String user ="";
	public String lastawstimeseconds = "";	//最后更新时间(秒数)上传时用
	public String unread = "0";		//未读
	//2013-12-25
	public String headimgurl = "";	//头像url
	public Date headimglastupdate = new Date(0);	//头像最后更新时间
}
