package com.drcom.drpalm.objs;

import java.util.Date;

public class LatestNewsItem {
	public LatestNewsItem(){
		latest_news_lastupdate = new Date();
	}
	public String latest_news_type = "";
	public Integer latest_news_id = -1;//消息或活动ID -1为无效
	public Integer latest_news_channel = -1;//消息或活动分类ID -1为无效
	public String latest_news_title = "";
	public Date latest_news_lastupdate = null;
	public String user = "";
}
