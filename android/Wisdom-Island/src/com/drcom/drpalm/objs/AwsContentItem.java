package com.drcom.drpalm.objs;

import java.util.Date;

public class AwsContentItem {
	/**
	 * 对方的话(显示在左边)
	 */
	public static final int MESSAGE_FROM = 0;	
	/**
	 * 自己的话(显示在右边)
	 */
	public static final int MESSAGE_TO = 1;		
	
	public  int       direction = 0;      //发送人/接收人标识
	
	public AwsContentItem(){
		aws_time = new Date();
	}
	public AwsContentItem(int direction, String content,String pubname, Date time) {
		super();
		this.direction = direction;
		this.pub_name = pubname;
		this.aws_body = content;
		this.aws_time = time;
	}
	
	public Integer eventid = 0;//活动ID
	public String user = "";//当前用户
	public Integer aws_id = -1;//-1为无效，反馈ID
	public Date aws_time = null;//反馈时间
	public String pub_id = "";//反馈人ID
	public String pub_name = "";//反馈人名字
	public String rec_id = ""; //接收人ID
	public String rec_name = "";//接收人名字
	public String aws_body = "";//反馈内容
	public String aws_group_id = "";//讨论组ID
	public String headimgurl = "";	//头像URL
}
