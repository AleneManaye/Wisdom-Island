package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;

import com.drcom.drpalm.DB.LatestNewsDB;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.service.ItemDataTranslate;
import com.drcom.drpalm.objs.LatestNewsItem;
import com.drcom.drpalm.objs.UpdateTimeItem;

public class LatestNewsParse extends BaseParse{
	
	public LatestNewsParse(HashMap<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<LatestNewsItem> parseLatestNewsItems(){
		ArrayList<LatestNewsItem> latestNewsItems = new ArrayList<LatestNewsItem>();
		ArrayList<HashMap<String,Object>> objectList = null;
		if(mHashMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORIES)){
			try{
				objectList = (ArrayList<HashMap<String,Object>>)mHashMap.get(LatestNewsDefine.LATEST_NEWS_STORIES);
				for(int i =0;i<objectList.size();i++){
					HashMap<String,Object> objMap = objectList.get(i);
					LatestNewsItem storyItem = new LatestNewsItem();
					storyItem.latest_news_type = LatestNewsDB.TYPE_NEWS;
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_ID)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_ID);
						storyItem.latest_news_id = ItemDataTranslate.String2Intger(value);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_CHANNEL)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_CHANNEL);
						storyItem.latest_news_channel = ItemDataTranslate.String2Intger(value);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_TITLE)){
						storyItem.latest_news_title = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_TITLE);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_LATEUPDATE)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_LATEUPDATE);
						storyItem.latest_news_lastupdate = DateFormatter.getDateFromSecondsString(value);
					}
					latestNewsItems.add(storyItem);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENTS)){
			try{
				objectList = (ArrayList<HashMap<String,Object>>)mHashMap.get(LatestNewsDefine.LATEST_NEWS_EVENTS);
				for(int i =0;i<objectList.size();i++){
					HashMap<String,Object> objMap = objectList.get(i);
					LatestNewsItem storyItem = new LatestNewsItem();
					storyItem.latest_news_type = LatestNewsDB.TYPE_EVENTS;
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_ID)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_ID);
						storyItem.latest_news_id = ItemDataTranslate.String2Intger(value);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_TYPE)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_TYPE);
						storyItem.latest_news_channel = ItemDataTranslate.String2Intger(value);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE)){
						storyItem.latest_news_title = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE);
						storyItem.latest_news_lastupdate = DateFormatter.getDateFromSecondsString(value);
					}
					latestNewsItems.add(storyItem);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return latestNewsItems;
	}
	
	
	public ArrayList<UpdateTimeItem> parseLatestNewsModuleItems(){
		ArrayList<UpdateTimeItem> latestNewsItems = new ArrayList<UpdateTimeItem>();
		ArrayList<HashMap<String,Object>> objectList = null;
		if(mHashMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_MODULE)){
			try{
				Object obj = mHashMap.get(LatestNewsDefine.LATEST_NEWS_STORY_MODULE);
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
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_TITLE)){
							storyItem.update_title = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_TITLE);
						}
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_STORY_LATEUPDATE)){
							String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_STORY_LATEUPDATE);
							storyItem.update_time_last = DateFormatter.getDateFromSecondsString(value);
						}
						latestNewsItems.add(storyItem);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return latestNewsItems;
	}
	
	public ArrayList<UpdateTimeItem> parseLatestEventsModuleItems(){
		ArrayList<UpdateTimeItem> latestNewsItems = new ArrayList<UpdateTimeItem>();
		ArrayList<HashMap<String,Object>> objectList = null;
		if(mHashMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_MODULE)){
			try{
				Object obj = mHashMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_MODULE);
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
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE)){
							storyItem.update_title = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE);
						}
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE)){
							String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE);
							storyItem.update_time_last = DateFormatter.getDateFromSecondsString(value);
						}
						if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT)){
							storyItem.update_unreadcount = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT);
						}
						latestNewsItems.add(storyItem);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey(LatestNewsDefine.LATEST_NEWS_SENDEVENT_MODULE)){
			try{
				Object obj = mHashMap.get(LatestNewsDefine.LATEST_NEWS_SENDEVENT_MODULE);
				if(obj != null){
					HashMap<String,Object> objMap = (HashMap<String,Object>)obj;
					UpdateTimeItem storyItem = new UpdateTimeItem();
					storyItem.update_time_type = "event";
					storyItem.update_time_channel = RequestCategoryID.EVENTS_SEND_ID;
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE)){
						storyItem.update_title = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE);
						storyItem.update_time_last = DateFormatter.getDateFromSecondsString(value);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT)){
						storyItem.update_unreadcount = ((String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT)).trim();
					}
					latestNewsItems.add(storyItem);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey(LatestNewsDefine.LATEST_NEWS_SYSMSG_MODULE)){
			try{
				Object obj = mHashMap.get(LatestNewsDefine.LATEST_NEWS_SYSMSG_MODULE);
				if(obj != null){
					HashMap<String,Object> objMap = (HashMap<String,Object>)obj;
					UpdateTimeItem storyItem = new UpdateTimeItem();
					storyItem.update_time_type = "event";
					storyItem.update_time_channel = RequestCategoryID.SYSINFO_ID;
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE)){
						storyItem.update_title = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE);
						storyItem.update_time_last = DateFormatter.getDateFromSecondsString(value);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT)){
						storyItem.update_unreadcount = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT);
					}
					latestNewsItems.add(storyItem);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(mHashMap.containsKey(LatestNewsDefine.LATEST_NEWS_CONTACT_MODULE)){
			try{
				Object obj = mHashMap.get(LatestNewsDefine.LATEST_NEWS_CONTACT_MODULE);
				if(obj != null){
					HashMap<String,Object> objMap = (HashMap<String,Object>)obj;
					UpdateTimeItem storyItem = new UpdateTimeItem();
					storyItem.update_time_type = "event";
					storyItem.update_time_channel = RequestCategoryID.EVENTS_COMMUNION_ID;
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE)){
						storyItem.update_title = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_TITLE);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE)){
						String value = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_LASTUPDATE);
						storyItem.update_time_last = DateFormatter.getDateFromSecondsString(value);
					}
					if(objMap.containsKey(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT)){
						storyItem.update_unreadcount = (String)objMap.get(LatestNewsDefine.LATEST_NEWS_EVENT_UNREADCOUNT);
					}
					latestNewsItems.add(storyItem);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return latestNewsItems;
	}
}
