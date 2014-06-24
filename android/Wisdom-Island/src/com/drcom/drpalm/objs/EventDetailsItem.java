package com.drcom.drpalm.objs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/*      
 * Date         : 2012-4-16
 * Author       : zeng han hua
 * Copyright    : City Hotspot Co., Ltd.
 */
public class EventDetailsItem implements Serializable {

	public EventDetailsItem(){
		imgs = new ArrayList<Imags>() ;
		listReplyer = new ArrayList<Replyer>();
//		attachmentList = new ArrayList<Attachment>() ;
	}
	
	public String user;
	//列表项
	public Integer eventid = 0;//活动ID
	public Integer type = 0;//活动类型
	public Integer orieventid ;//修改目标活动ID
	public String oristatus = "";//修改状态为：（ 取消：C，更改补充：A，不变：N）
	public String pubid = "";  //发布人ID
	public String pubname = "";//发布人名称
	public Date post;//活动发布时间
	public Date start ;//活动起始时间
	public Date end ;//活动截止时间
	public String status = "";//活动状态 通告状态（正常：N，取消：C [对应删除操作，客户端加中划线]，更改补充：A，草稿：D）
	public boolean ifeshow;   //是否加急
	public String title = "";//活动标题
	public String location = ""; //活动地点
	public String locationUrl = ""; //活动地点URL
	public Date cancelled ;//取消时间
	public String thumbUrl = "";//封面图标
	public Date lastawstime;//最后反馈时间(服务器数据)
	public Date lastawstimeread;//已阅读的最后反馈时间(本地缓存)
	public Date lastupdate;//最后更新时间
	public String body = "";//事件内容
	public String cleanbody = "";//无HTML标记的通告内容
	public boolean hasatt;//是否有附件
	public boolean isread = false ; //是否已阅
	public boolean bookmark ;//收藏标记
//	public boolean hasNewFeedback = false;	//是否有新回复
	public String summary = "";//摘要
	public String recvtotal = "0";//接收总人数
	public String readcount = "0";//已读人数
	public String lastawsuserid = "";//最后反馈人
	public int awscount = 0;		//所有反馈数量
	public int awscountclient = 0;	//所有反馈数量(本地)
	
	//已读和未读人名列表
	public String read_name_list = "";//已读人名列表
	public String unread_name_list = "";//未读人名列表
	
	//最后读取时间
	public Date lastreadtime;//最后
	
	public boolean isneedreview = false;	//是否需要回评 2013-12-19
	
	public List<ReviewTemp> mReviewTempList = new ArrayList<ReviewTemp>();	//回评模板
	
//	public Integer readcount ;//已读用户数
//	public Integer totalcount ;//总共用户数	
//	public List<Attachment> attachmentList ;//附件集
	
	//活动详细
	public String ownerid ;//接收人ID
	public String owner  = "";//接收人名称
	public List<Imags> imgs = new ArrayList<EventDetailsItem.Imags>();//图片URL地址集
	public List<Replyer> listReplyer = new ArrayList<EventDetailsItem.Replyer>();  //回复人List
	
	public static class Imags implements Serializable {
		public String URL ;
		public String imgDescription = "";
		public int attid = 0;
		public byte[] imgData = null;	//图片数据
		public String fileId = "";
		public String fileType = "";
		public String preview = "";		//附件预览图url（可无）
		public String size = "";		//附件大小
		public String attname = "";		//附件名字
		
		public byte[] getImgData() {
			return imgData;
		}
		public void setImgData(byte[] imgData) {
			this.imgData = imgData;
		}
		public Imags(String URL,String description,int id){
			this.URL = URL ;	
			this.imgDescription = description;
			this.attid = id;
		}
		public Imags(){};
	}
	
	public static class Replyer implements Serializable {
		public String  ReplyerId;             //回复人ID
		public String  ReplyerName;           //回复人名称
		public String  ReplyCount;            //回复数量
		public String  ReplyLastTime = "0";   //最后回复时间(服务器给的时间)
		public String  lastawstimeread = "0"; //已阅读的最后反馈时间(只存在本地,用作比较)
		public boolean isNewReply = false;	  //是否新回复
		public List<ReplyerMember> memberList = new ArrayList<EventDetailsItem.ReplyerMember>();
		
		public Replyer(String id,String name,String count,String lasttime,String lastawstimeread)
		{
			this.ReplyerId = id;
			this.ReplyerName = name;
			this.ReplyCount = count;
			this.ReplyLastTime = lasttime;
			this.lastawstimeread = lastawstimeread;
		}
	}

	public static class ReplyerMember implements Serializable {
		public String id;
		public String name;
		public String headimgurl;
		public String headimglastupdate = "0";
	}
	
	/**
	 * 回评模板
	 * @author zhaojunjie
	 *
	 */
	public static class ReviewTemp implements Serializable {
		public static String TYPE_COUNT = "count";
		public static String TYPE_TEXT = "text";
		
		public String id;
		public String title;
		public String type;
		public int max;
		public int defaultsum;
		public boolean required;	//表示必填
		
		public ReviewTemp(){
			
		}
	}
}
