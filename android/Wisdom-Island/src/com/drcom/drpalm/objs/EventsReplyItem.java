package com.drcom.drpalm.objs;
/*      
 * Date         : 2012-8-8
 * Author       : JiangBo
 * Copyright    : City Hotspot Co., Ltd.
 */
public class EventsReplyItem {
	
	public static final int MESSAGE_FROM = 0;
	public static final int MESSAGE_TO = 1;
	
	public  int       direction = 0;      //
	
	public  int       replyId;    		//回复ID
	public  String    replyTime;  		//回复时间
	public  int       replytype;  		//反馈回复模板ID
	public  String    replyPubId; 		//回复人ID
	public  String    replyPubName;     //回复人名称
	public  String    replyRecId; 		//接收人ID
	public  String    replyRecName;     //接收人名称
	public  String    replytitle;       //标题
	public  String    replybody;       //接收内容
	
	public EventsReplyItem()
	{
		replyId = 0;
		replyTime = "";
		replytype = 0;
		replyPubId = "";
		replyPubName = "";
		replyRecId = "";
		replyRecName = "";
		replytitle = "";
		replybody = "";
	}
	
	public EventsReplyItem(int direction, String content,String time) {
		super();
		this.direction = direction;
		this.replybody = content;
		this.replyTime = time;
	}
	
	
}
