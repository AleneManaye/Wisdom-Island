package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.service.ItemDataTranslate;

import com.drcom.drpalm.objs.SystemInfoItem;

public class SystemInfoParse extends BaseParse{
	public SystemInfoParse(HashMap<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}
	
	public Integer parseRetainCount(){
		String str = null;
		if(mHashMap.containsKey(SystemInfoDefine.SYSTEM_RETCOUNT)) {
			str = (String)mHashMap.get((SystemInfoDefine.SYSTEM_RETCOUNT));
		}
		return ItemDataTranslate.String2Intger(str);
	}
	
	public ArrayList<SystemInfoItem> parseSystemInfos() {
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<SystemInfoItem> infosList = new ArrayList<SystemInfoItem>();
		if(mHashMap.containsKey(SystemInfoDefine.SYSTEM_MSGS)) {	
			try{
				objectList = (ArrayList<HashMap<String,Object>>)mHashMap.get(SystemInfoDefine.SYSTEM_MSGS);
				for(int i =0;i<objectList.size();i++){
					HashMap<String,Object> objMap = objectList.get(i);
					SystemInfoItem systemInfoItem = new SystemInfoItem();
					if(objMap.containsKey(SystemInfoDefine.SYSTEM_MSG_ID)){
						String value = (String)objMap.get(SystemInfoDefine.SYSTEM_MSG_ID);
						systemInfoItem.msg_id = ItemDataTranslate.String2Intger(value);
					}
					if(objMap.containsKey(SystemInfoDefine.SYSTEM_MSG_TITLE)){
						systemInfoItem.msg_title = (String)objMap.get(SystemInfoDefine.SYSTEM_MSG_TITLE);
					}						
					if(objMap.containsKey(SystemInfoDefine.SYSTEM_MSG_LASTUPDATE)){
						String value = (String)objMap.get(SystemInfoDefine.SYSTEM_MSG_LASTUPDATE);
						systemInfoItem.msg_lastupdate = DateFormatter.getDateFromSecondsString(value);
					}
					if(objMap.containsKey(SystemInfoDefine.SYSTEM_MSG_INACTIVETIME)){
						String value = (String)objMap.get(SystemInfoDefine.SYSTEM_MSG_INACTIVETIME);
						systemInfoItem.msg_inactivetime = DateFormatter.getDateFromSecondsString(value);
					}
					infosList.add(systemInfoItem);
				}
			}catch(Exception e){

			}	
		}
		return infosList;
	}
	
	public SystemInfoItem parseSystemInfoDetail(){
		SystemInfoItem systemInfoItem = new SystemInfoItem();
		if(mHashMap.containsKey(SystemInfoDefine.SYSTEM_MSG_ID)){
			String value = (String)mHashMap.get(SystemInfoDefine.SYSTEM_MSG_ID);
			systemInfoItem.msg_id = ItemDataTranslate.String2Intger(value);
		}
		if(mHashMap.containsKey(SystemInfoDefine.SYSTEM_MSG_BODY)){
			systemInfoItem.msg_body = (String)mHashMap.get(SystemInfoDefine.SYSTEM_MSG_BODY);
		}
		return systemInfoItem;
	}
}
