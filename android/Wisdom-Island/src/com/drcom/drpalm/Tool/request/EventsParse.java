package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.CommonTranslate.ItemDataTranslate;
import com.drcom.drpalm.objs.AwsContentItem;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.NoticeTypeItem;

public class EventsParse extends BaseParse {
	public EventsParse(HashMap<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}

	// public Integer parseCurrentCount(){
	// String str = null;
	// if(mHashMap.containsKey(EventsDefine.EVENTS_CURCOUNT)) {
	// str = (String)mHashMap.get((EventsDefine.EVENTS_CURCOUNT));
	// }
	// return ItemDataTranslate.String2Intger(str);
	// }
	public Integer parseRetainCount() {
		String str = null;
		if (mHashMap.containsKey(EventsDefine.EVENTS_RETCOUNT)) {
			str = (String) mHashMap.get((EventsDefine.EVENTS_RETCOUNT));
		}
		return ItemDataTranslate.String2Intger(str);
	}

	// public String parseCurrentMaxPost(){
	// String str = null;
	// if(mHashMap.containsKey(EventsDefine.EVENTS_CURMAXPOST)) {
	// str = (String)mHashMap.get((EventsDefine.EVENTS_CURMAXPOST));
	// }
	// return str;
	// }
	public ArrayList<EventDetailsItem> parseEvents() {
		ArrayList<HashMap<String, Object>> objectList = null;
		ArrayList<EventDetailsItem> eventsList = new ArrayList<EventDetailsItem>();
		if (mHashMap.containsKey(EventsDefine.EVENTS_EVNETLIST)) {
			try {
				objectList = (ArrayList<HashMap<String, Object>>) mHashMap.get(EventsDefine.EVENTS_EVNETLIST);
				for (int i = 0; i < objectList.size(); i++) {
					HashMap<String, Object> objMap = objectList.get(i);
					EventDetailsItem eventItem = new EventDetailsItem();
					if (objMap.containsKey(EventsDefine.EVENTS_EVENTID)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_EVENTID);
						eventItem.eventid = ItemDataTranslate.String2Intger(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_ISREAD)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_ISREAD);
						if (value.endsWith("1"))
							eventItem.isread = true;
						else
							eventItem.isread = false;
					}
					if (objMap.containsKey(EventsDefine.EVENTS_TYPE)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_TYPE);
						eventItem.type = ItemDataTranslate.String2Intger(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_ORIEVENTID)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_ORIEVENTID);
						eventItem.orieventid = ItemDataTranslate.String2Intger(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_ORISTATUS)) {
						eventItem.oristatus = (String) objMap.get(EventsDefine.EVENTS_ORISTATUS);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_PUBID)) {
						eventItem.pubid = ((String) objMap.get(EventsDefine.EVENTS_PUBID)).toLowerCase();
					}
					if (objMap.containsKey(EventsDefine.EVENTS_PUBNAME)) {
						eventItem.pubname = (String) objMap.get(EventsDefine.EVENTS_PUBNAME);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_POST)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_POST);
						eventItem.post = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_START)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_START);
						eventItem.start = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_END)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_END);
						eventItem.end = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_STATUS)) {
						eventItem.status = (String) objMap.get(EventsDefine.EVENTS_STATUS);
					}
					// 加急
					if (objMap.containsKey(EventsDefine.EVENTS_IFESHOW)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_IFESHOW);
						eventItem.ifeshow = value.contentEquals("1");
					}
					if (objMap.containsKey(EventsDefine.EVENTS_TITLE)) {
						eventItem.title = (String) objMap.get(EventsDefine.EVENTS_TITLE);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_LOCATION)) {
						eventItem.location = (String) objMap.get(EventsDefine.EVENTS_LOCATION);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_SUMMARY)) {
						eventItem.summary = (String) objMap.get(EventsDefine.EVENTS_SUMMARY);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_LOCATIONURL)) {
						eventItem.locationUrl = (String) objMap.get(EventsDefine.EVENTS_LOCATIONURL);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_CANCELLED)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_CANCELLED);
						eventItem.cancelled = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_THUMBURL)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_THUMBURL) + "";
						if (value.trim().length() == 0 || value.equals("null")) {
							eventItem.thumbUrl = "";
						} else {
							eventItem.thumbUrl = value;
						}
					}
					if (objMap.containsKey(EventsDefine.EVENTS_LASTAWSTIME)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_LASTAWSTIME);
						eventItem.lastawstime = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_LASTREADTIME)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_LASTREADTIME);
						eventItem.lastreadtime = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_LASTUPDATE)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_LASTUPDATE);
						eventItem.lastupdate = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_HASATT)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_HASATT);
						eventItem.hasatt = value.contentEquals("1");
					}
					if (objMap.containsKey(EventsDefine.EVENTS_OWNERID)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_OWNERID);
						eventItem.ownerid = value;
					}
					if (objMap.containsKey(EventsDefine.EVENTS_OWNER)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_OWNER);
						eventItem.owner = value;
					}
					if (objMap.containsKey(EventsDefine.EVENTS_SENTTOTAL)) {
						eventItem.recvtotal = (String) objMap.get(EventsDefine.EVENTS_SENTTOTAL);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_LASTAWSUSERID)) {
						eventItem.lastawsuserid = (String) objMap.get(EventsDefine.EVENTS_LASTAWSUSERID);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_AWSCOUNT)) {
						eventItem.awscount =Integer.valueOf((String) objMap.get(EventsDefine.EVENTS_AWSCOUNT));
					}
					if (objMap.containsKey(EventsDefine.EVENTS_NEEDREVIEW)) {
						String value = (String) objMap.get(EventsDefine.EVENTS_NEEDREVIEW);
						eventItem.isneedreview = value.contentEquals("1");
					}
					eventsList.add(eventItem);
				}
			} catch (Exception e) {

			}
		}
		return eventsList;
	}

	public EventDetailsItem parseEventsDetail() {
		ArrayList<HashMap<String, Object>> objectList = null;
		EventDetailsItem item = new EventDetailsItem();
		if (mHashMap.containsKey(EventsDefine.EVENTS_EVENTID)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_EVENTID);
			item.eventid = ItemDataTranslate.String2Intger(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_TYPE)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_TYPE);
			item.type = ItemDataTranslate.String2Intger(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_ORIEVENTID)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_ORIEVENTID);
			item.orieventid = ItemDataTranslate.String2Intger(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_ORISTATUS)) {
			item.oristatus = (String) mHashMap.get(EventsDefine.EVENTS_ORISTATUS);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_PUBID)) {
			item.pubid = ((String) mHashMap.get(EventsDefine.EVENTS_PUBID)).toLowerCase();
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_PUBNAME)) {
			item.pubname = (String) mHashMap.get(EventsDefine.EVENTS_PUBNAME);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_POST)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_POST);
			item.post = DateFormatter.getDateFromSecondsString(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_START)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_START);
			item.start = DateFormatter.getDateFromSecondsString(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_END)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_END);
			item.end = DateFormatter.getDateFromSecondsString(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_STATUS)) {
			item.status = (String) mHashMap.get(EventsDefine.EVENTS_STATUS);
		}
		// 加急
		if (mHashMap.containsKey(EventsDefine.EVENTS_IFESHOW)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_IFESHOW);
			item.ifeshow = value.contentEquals("1");
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_TITLE)) {
			item.title = (String) mHashMap.get(EventsDefine.EVENTS_TITLE);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_LOCATION)) {
			item.location = (String) mHashMap.get(EventsDefine.EVENTS_LOCATION);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_SUMMARY)) {
			item.summary = (String) mHashMap.get(EventsDefine.EVENTS_SUMMARY);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_LOCATIONURL)) {
			item.locationUrl = (String) mHashMap.get(EventsDefine.EVENTS_LOCATIONURL);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_CANCELLED)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_CANCELLED);
			item.cancelled = DateFormatter.getDateFromSecondsString(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_THUMBURL)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_THUMBURL) + "";
			if (value.trim().length() == 0 || value.equals("null")) {
				item.thumbUrl = "";
			} else {
				item.thumbUrl = value;
			}
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_LASTAWSTIME)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_LASTAWSTIME);
			item.lastawstime = DateFormatter.getDateFromSecondsString(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_LASTUPDATE)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_LASTUPDATE);
			item.lastupdate = DateFormatter.getDateFromSecondsString(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_HASATT)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_HASATT);
			item.hasatt = value.contentEquals("1");
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_BODY)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_BODY);
			item.body = value == null?"":value;
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_CLEAN_BODY)) {
			item.cleanbody = (String) mHashMap.get(EventsDefine.EVENTS_CLEAN_BODY);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_OWNERID)) {
			item.ownerid = (String) mHashMap.get(EventsDefine.EVENTS_OWNERID);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_OWNER)) {
			item.owner = (String) mHashMap.get(EventsDefine.EVENTS_OWNER);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_RECVTOTAL)) {
			item.recvtotal = (String) mHashMap.get(EventsDefine.EVENTS_RECVTOTAL);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_READCOUNT)) {
			item.readcount = (String) mHashMap.get(EventsDefine.EVENTS_READCOUNT);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_LASTREADTIME)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_LASTREADTIME);
			item.lastreadtime = DateFormatter.getDateFromSecondsString(value);
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_NEEDREVIEW)) {
			String value = (String) mHashMap.get(EventsDefine.EVENTS_NEEDREVIEW);
			item.isneedreview = value.contentEquals("1");
		}
		
		if (mHashMap.containsKey(EventsDefine.EVENTS_ATTS)) {
			Object obj = mHashMap.get(EventsDefine.EVENTS_ATTS);
			if (null != obj) {
				// must sure the type of the object, else throw a exception
				String strClass = obj.getClass().getName();
				if (!String.class.getName().equals(strClass)) {
					ArrayList<HashMap<String, Object>> imgsList = (ArrayList<HashMap<String, Object>>) obj;
					for (int j = 0; j < imgsList.size(); j++) {
						HashMap<String, Object> imgMap = imgsList.get(j);
						EventDetailsItem.Imags img = new EventDetailsItem.Imags();
						// add img into image list of item here
						if (imgMap.containsKey(EventsDefine.EVENTS_ATTDES)) {
							img.imgDescription = (String) imgMap.get(EventsDefine.EVENTS_ATTDES);
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_ATTURL)) {
							img.URL = (String) imgMap.get(EventsDefine.EVENTS_ATTURL);
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_ATTID)) {
							String value = (String) imgMap.get(EventsDefine.EVENTS_ATTID);
							img.attid = ItemDataTranslate.String2Intger(value);
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_PREVIEW)) {
							img.preview = (String) imgMap.get(EventsDefine.EVENTS_PREVIEW); 
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_SIZE)) {
							img.size = (String) imgMap.get(EventsDefine.EVENTS_SIZE); 
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_ATTNAME)) {
							img.attname = (String) imgMap.get(EventsDefine.EVENTS_ATTNAME); 
						}
						item.imgs.add(img);
					}
				}
			}
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_AWSORGLIST)) {
			Object obj = mHashMap.get(EventsDefine.EVENTS_AWSORGLIST);
			if (null != obj) {
				String strClass = obj.getClass().getName();
				if (!String.class.getName().equals(strClass)) {
					ArrayList<HashMap<String, Object>> qwsorglist = (ArrayList<HashMap<String, Object>>) obj;
					for (int j = 0; j < qwsorglist.size(); j++) {
						HashMap<String, Object> imgMap = qwsorglist.get(j);
						// add img into image list of item here
						String ReplyerId = "";
						String ReplyerName = "";
						String ReplyCount = "";
						String ReplyLastTime = "";

						if (imgMap.containsKey(EventsDefine.EVENTS_ASWPUBID)) {
							ReplyerId = (String) imgMap.get(EventsDefine.EVENTS_ASWPUBID);
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_AWSPUBNAME)) {
							ReplyerName = (String) imgMap.get(EventsDefine.EVENTS_AWSPUBNAME);
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_AWSCOUNT)) {
							ReplyCount = (String) imgMap.get(EventsDefine.EVENTS_AWSCOUNT);
						}
						if (imgMap.containsKey(EventsDefine.EVENTS_LASTAWSTIME)) {
							ReplyLastTime = (String) imgMap.get(EventsDefine.EVENTS_LASTAWSTIME);
						}
						EventDetailsItem.Replyer replyer = new EventDetailsItem.Replyer(ReplyerId, ReplyerName, ReplyCount, ReplyLastTime, "0");
						
						//讨论组成员列表
						if(imgMap.containsKey(EventsDefine.EVENTS_MEMBERLIST)){
							Object objmember = imgMap.get(EventsDefine.EVENTS_MEMBERLIST);
							if (null != objmember) {
								String strClassmember = objmember.getClass().getName();
								if (!String.class.getName().equals(strClassmember)) {
									ArrayList<HashMap<String, Object>> memberlist = (ArrayList<HashMap<String, Object>>) objmember;
									for (int m = 0; m < memberlist.size(); m++) {
										EventDetailsItem.ReplyerMember member = new EventDetailsItem.ReplyerMember();
										
										HashMap<String, Object> memberMap = memberlist.get(m);
										if (memberMap.containsKey(EventsDefine.EVENTS_MEMBER_ID)) {
											member.id = (String) memberMap.get(EventsDefine.EVENTS_MEMBER_ID);
										}
										
										if (memberMap.containsKey(EventsDefine.EVENTS_MEMBER_NAME)) {
											member.name = (String) memberMap.get(EventsDefine.EVENTS_MEMBER_NAME);
										}
										
										if (memberMap.containsKey(EventsDefine.EVENTS_MEMBER_IMGURL)) {
											member.headimgurl = (String) memberMap.get(EventsDefine.EVENTS_MEMBER_IMGURL);
										}
										
										if (memberMap.containsKey(EventsDefine.EVENTS_MEMBER_LASTUPDATE)) {
											member.headimglastupdate = (String) memberMap.get(EventsDefine.EVENTS_MEMBER_LASTUPDATE);
										}
										
										replyer.memberList.add(member);
									}
								}
							}
						}
						
						item.listReplyer.add(replyer);
					}
				}
			}
		}
		if (mHashMap.containsKey(EventsDefine.EVENTS_REVIEWTEMP)) {
			Object obj = mHashMap.get(EventsDefine.EVENTS_REVIEWTEMP);
			if (null != obj) {
				// must sure the type of the object, else throw a exception
				String strClass = obj.getClass().getName();
				if (!String.class.getName().equals(strClass)) {
					ArrayList<HashMap<String, Object>> imgsList = (ArrayList<HashMap<String, Object>>) obj;
					for (int j = 0; j < imgsList.size(); j++) {
						HashMap<String, Object> imgMap = imgsList.get(j);
						
						EventDetailsItem.ReviewTemp reviewtemp = new EventDetailsItem.ReviewTemp();
						if (imgMap.containsKey(EventsDefine.EVENTS_REVIEWTEMP_ID)) {
							reviewtemp.id = (String) imgMap.get(EventsDefine.EVENTS_REVIEWTEMP_ID);
						}
						
						if (imgMap.containsKey(EventsDefine.EVENTS_REVIEWTEMP_TITLE)) {
							reviewtemp.title = (String) imgMap.get(EventsDefine.EVENTS_REVIEWTEMP_TITLE);
						}
						
						if (imgMap.containsKey(EventsDefine.EVENTS_REVIEWTEMP_TYPE)) {
							reviewtemp.type = (String) imgMap.get(EventsDefine.EVENTS_REVIEWTEMP_TYPE);
						}
						
						if (imgMap.containsKey(EventsDefine.EVENTS_REVIEWTEMP_MAX)) {
							reviewtemp.max = Integer.valueOf((String) imgMap.get(EventsDefine.EVENTS_REVIEWTEMP_MAX));
						}
						
						if (imgMap.containsKey(EventsDefine.EVENTS_REVIEWTEMP_DEFAULT)) {
							reviewtemp.defaultsum = Integer.valueOf((String) imgMap.get(EventsDefine.EVENTS_REVIEWTEMP_DEFAULT));
						}
						
						if (imgMap.containsKey(EventsDefine.EVENTS_REVIEWTEMP_REQUIRED)) {
							reviewtemp.required = ((String) imgMap.get(EventsDefine.EVENTS_REVIEWTEMP_REQUIRED)).contentEquals("1");
						}
						
						item.mReviewTempList.add(reviewtemp);
					}
				}
			}
		}
		
		return item;
	}

	public ArrayList<AwsContentItem> parseEventsReply() {
		ArrayList<HashMap<String, Object>> objectList = null;
		ArrayList<AwsContentItem> eventsreplyList = new ArrayList<AwsContentItem>();

		if (mHashMap.containsKey(EventsDefine.EVENTS_AWSLIST)) {
			try {
				objectList = (ArrayList<HashMap<String, Object>>) mHashMap.get(EventsDefine.EVENTS_AWSLIST);
				for (int i = 0; i < objectList.size(); i++) {
					HashMap<String, Object> objMap = objectList.get(i);
					AwsContentItem item = new AwsContentItem();
	
					if (objMap.containsKey(EventsDefine.EVENTS_ASWID)) // ID
					{
						String value = (String) objMap.get(EventsDefine.EVENTS_ASWID);
						item.aws_id = ItemDataTranslate.String2Intger(value);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_AWSTIME)) // 时间
					{
						String value = (String) objMap.get(EventsDefine.EVENTS_AWSTIME);
						item.aws_time = DateFormatter.getDateFromSecondsString(value);
					}
					if (objMap.containsKey(EventsDefine.REPLY_AWSPUBID)) // 回复人ID
					{
						item.pub_id = (String) objMap.get(EventsDefine.REPLY_AWSPUBID);
					}
					if (objMap.containsKey(EventsDefine.REPLY_NAME)) // 回复人名称
					{
						item.pub_name = (String) objMap.get(EventsDefine.REPLY_NAME);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_AWSRECID)) // 接收人ID
					{
						item.rec_id = (String) objMap.get(EventsDefine.EVENTS_AWSRECID);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_AWSRECNAME)) // 接收人名称
					{
						item.rec_name = (String) objMap.get(EventsDefine.EVENTS_AWSRECNAME);
					}
					if (objMap.containsKey(EventsDefine.EVENTS_AWSBODY)) // 内容
					{
						item.aws_body = (String) objMap.get(EventsDefine.EVENTS_AWSBODY);
					}
	
					eventsreplyList.add(item);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}

		}
		// return eventsreplyMap;
		return eventsreplyList;
	}

	public ArrayList<NoticeTypeItem> parseNoticeType() {
		ArrayList<HashMap<String, Object>> objectList = null;
		ArrayList<NoticeTypeItem> noticeTypeList = new ArrayList<NoticeTypeItem>();
		if (mHashMap.containsKey(EventsDefine.OBJTYPE_TYPES)) {
			try {
				objectList = (ArrayList<HashMap<String, Object>>) mHashMap.get(EventsDefine.OBJTYPE_TYPES);
				for (int i = 0; i < objectList.size(); i++) {
					HashMap<String, Object> objMap = objectList.get(i);
					NoticeTypeItem item = new NoticeTypeItem();

					if (objMap.containsKey(EventsDefine.OBJTYPE_OBJTYPE)) {
						String value = (String) objMap.get(EventsDefine.OBJTYPE_OBJTYPE);
						item.objtype = ItemDataTranslate.String2Intger(value);
					}
					if (objMap.containsKey(EventsDefine.OBJTYPE_OBJTYPEDES))
						item.objtypedes = (String) objMap.get(EventsDefine.OBJTYPE_OBJTYPEDES);

					noticeTypeList.add(item);
				}
			} catch (Exception e) {

			}
		}
		return noticeTypeList;
	}

	public Integer parseFBCount() {
		String str = null;
		if (mHashMap.containsKey(EventsDefine.EVENTS_FBCOUNT)) {
			str = (String) mHashMap.get((EventsDefine.EVENTS_FBCOUNT));
		}
		return ItemDataTranslate.String2Intger(str);
	}

	public Date pareseLastFBTime() {
		Date date = null;
		if (mHashMap.containsKey(EventsDefine.EVENTS_FBCOUNT)) {
			String value = (String) mHashMap.get((EventsDefine.EVENTS_FBCOUNT));
			date = DateFormatter.getDateFromSecondsString(value);
		}
		return date;
	}
	
	public EventDetailsItem parseEventsReadInfo() {
		ArrayList<HashMap<String, Object>> objectList = null;
		EventDetailsItem item = new EventDetailsItem();
		String dividerSign = ",";
		if (mHashMap.containsKey(EventsDefine.EVENT_READ_LIST)) {
			Object obj = mHashMap.get(EventsDefine.EVENT_READ_LIST);
			if (null != obj) {
				// must sure the type of the object, else throw a exception
				String strClass = obj.getClass().getName();
				String readname = "";
				if (!String.class.getName().equals(strClass)) {
					ArrayList<HashMap<String, Object>> imgsList = (ArrayList<HashMap<String, Object>>) obj;
					for (int j = 0; j < imgsList.size()-1; j++) {
						HashMap<String, Object> imgMap = imgsList.get(j);
						// add img into image list of item here
						if (imgMap.containsKey(EventsDefine.EVENT_NAME)) {
							String value = (String) imgMap.get(EventsDefine.EVENT_NAME);
							readname += value + dividerSign;
						}
					}
					if(imgsList.size()>0){
						HashMap<String, Object> imgMap = imgsList.get(imgsList.size()-1);
						if (imgMap.containsKey(EventsDefine.EVENT_NAME)) {
							String value = (String) imgMap.get(EventsDefine.EVENT_NAME);
							readname += value;
						}
					}
					item.read_name_list = readname;
				}
			}
		}
		if (mHashMap.containsKey(EventsDefine.EVENT_UNREAD_LIST)) {
			Object obj = mHashMap.get(EventsDefine.EVENT_UNREAD_LIST);
			if (null != obj) {
				String strClass = obj.getClass().getName();
				String unreadname = "";
				if (!String.class.getName().equals(strClass)) {
					ArrayList<HashMap<String, Object>> qwsorglist = (ArrayList<HashMap<String, Object>>) obj;
					for (int j = 0; j < qwsorglist.size()-1; j++) {
						HashMap<String, Object> imgMap = qwsorglist.get(j);	
						if (imgMap.containsKey(EventsDefine.EVENT_NAME)) {
							String value = (String) imgMap.get(EventsDefine.EVENT_NAME);
							unreadname += value + dividerSign;
						}
					}
					if(qwsorglist.size() > 0){
						HashMap<String, Object> imgMap = qwsorglist.get(qwsorglist.size()-1);	
						if (imgMap.containsKey(EventsDefine.EVENT_NAME)) {
							String value = (String) imgMap.get(EventsDefine.EVENT_NAME);
							unreadname += value;
						}
					}
					item.unread_name_list = unreadname;
				}
			}
		}
		return item;
	} 
}
