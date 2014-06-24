package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;

import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.service.ItemDataTranslate;
import com.drcom.drpalm.objs.UpdateTimeItem;

public class NewsUpdateTimeParse extends BaseParse{
	public NewsUpdateTimeParse(HashMap<String, Object> map) {
		super(map);
	}
	
	public ArrayList<UpdateTimeItem> parseNewsUpdateItems(){
		ArrayList<UpdateTimeItem> newsUpdateItems = new ArrayList<UpdateTimeItem>();
		ArrayList<HashMap<String,Object>> objectList = null;
		if(mHashMap.containsKey("news")){
			try{
				Object obj = mHashMap.get("news");
				if(obj != null){
					objectList = (ArrayList<HashMap<String,Object>>)obj;
					for(int i =0;i<objectList.size();i++){
						HashMap<String,Object> objMap = objectList.get(i);
						UpdateTimeItem storyItem = new UpdateTimeItem();
						storyItem.update_time_type = "news";
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_CHANNEL)){
							String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_CHANNEL);
							storyItem.update_time_channel = ItemDataTranslate.String2Intger(value);
						}
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_LATEUPDATE)){
							String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_LATEUPDATE);
							storyItem.update_time_last = DateFormatter.getDateFromSecondsString(value);
						}
						
						newsUpdateItems.add(storyItem);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey("event")){
			try{
				Object obj = mHashMap.get("event");
				if(obj != null){
					objectList = (ArrayList<HashMap<String,Object>>)obj;
					for(int i =0;i<objectList.size();i++){
						HashMap<String,Object> objMap = objectList.get(i);
						UpdateTimeItem storyItem = new UpdateTimeItem();
						storyItem.update_time_type = "event";
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_TYPE)){
							String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_TYPE);
							storyItem.update_time_channel = ItemDataTranslate.String2Intger(value);
						}
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT)){
							storyItem.update_unreadcount = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT);
						}
						newsUpdateItems.add(storyItem);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey("sentevent")){
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get("sentevent");
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey("unreadcount")){
						UpdateTimeItem item = new UpdateTimeItem();
						item.update_time_channel = RequestCategoryID.EVENTS_SEND_ID;
						item.update_time_type = "event";
						String value = (String)opMap.get("unreadcount");
						item.update_unreadcount = value;
						newsUpdateItems.add(item);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey("sysmsg")){
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get("sysmsg");
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey("unreadcount")){
						UpdateTimeItem item = new UpdateTimeItem();
						item.update_time_channel = RequestCategoryID.SYSINFO_ID;
						item.update_time_type = "event";
						String value = (String)opMap.get("unreadcount");
						item.update_unreadcount = value;
						newsUpdateItems.add(item);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey("ebabychannel")){
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get("ebabychannel");
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey("unreadcount")){
						UpdateTimeItem item = new UpdateTimeItem();
						item.update_time_channel = RequestCategoryID.EBABYCHENNAL_ID;
						item.update_time_type = "more";
						String value = (String)opMap.get("unreadcount");
						item.update_unreadcount = value;
						newsUpdateItems.add(item);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey("contact")){	//交流（家园桥）
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get("contact");
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey("unreadcount")){
						UpdateTimeItem item = new UpdateTimeItem();
						item.update_time_channel = RequestCategoryID.EVENTS_COMMUNION_ID;
						item.update_time_type = "event";
						String value = (String)opMap.get("unreadcount");
						item.update_unreadcount = value;
						newsUpdateItems.add(item);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return newsUpdateItems;
	}
}
