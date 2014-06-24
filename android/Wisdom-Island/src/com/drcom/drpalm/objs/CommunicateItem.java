package com.drcom.drpalm.objs;

import java.util.Date;

public class CommunicateItem {
	/**
	 * 对方的话(显示在左边)
	 */
	public static final int MESSAGE_FROM = 0;	
	/**
	 * 自己的话(显示在右边)
	 */
	public static final int MESSAGE_TO = 1;	
	
	public CommunicateItem(){
		lastupdate = new Date();
	}
	public CommunicateItem(int direction, String content,Date time) {
		super();
		this.direction = direction;
		this.body = content;
		this.lastupdate = time;
	}
	public String topic_id = "";//分类ID 与联系人相关便于查找 -1未无效
	public Integer msg_id = -1; //消息ID -1未无效
	public String sendid = "";//发送人ID
	public String sendname = "";//发送人名字
	public String recvcid = "";//接收人ID
	public String recvcname = "";//接收人名字
	public Date lastupdate = null; //最后更新时间
	public String body = "";//内容
	public String user = "";
	public String headimgurl = "";	//头像URL
	
	public int direction = 0;      //发送人/接收人标识
}
