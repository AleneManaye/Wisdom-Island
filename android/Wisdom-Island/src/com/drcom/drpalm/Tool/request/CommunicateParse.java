package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.service.ItemDataTranslate;
import com.drcom.drpalm.objs.CommunicateItem;
import com.drcom.drpalm.objs.ContactItem;

public class CommunicateParse extends BaseParse{
	
	public CommunicateParse(HashMap<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 解析联系人列表
	 * @return
	 */
	public ArrayList<ContactItem> parseContacts() {
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<ContactItem> contactsList = new ArrayList<ContactItem>();
		if(mHashMap.containsKey(CommunicateDefine.COMMUNICATE_CONTACTS)) {	
			try{
				objectList = (ArrayList<HashMap<String,Object>>)mHashMap.get(CommunicateDefine.COMMUNICATE_CONTACTS);
				for(int i =0;i<objectList.size();i++){
					HashMap<String,Object> objMap = objectList.get(i);
					ContactItem contactItem = new ContactItem();
					if(objMap.containsKey(CommunicateDefine.COMMINICATE_CONTACT_ID)){
						String value = (String)objMap.get(CommunicateDefine.COMMINICATE_CONTACT_ID);
						contactItem.cnid = value;
					}
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_CONTACT_NAME)){
						contactItem.cnname = (String)objMap.get(CommunicateDefine.COMMUNICATE_CONTACT_NAME);
					}						
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_CONTACT_LASTUPDATE)){
						String value = (String)objMap.get(CommunicateDefine.COMMUNICATE_CONTACT_LASTUPDATE);
						contactItem.lastupdate = DateFormatter.getDateFromSecondsString(value);
					}							
					contactsList.add(contactItem);
				}
			}catch(Exception e){

			}	
		}
		return contactsList;
	}
	
	
	public ArrayList<CommunicateItem> parseCommunicateContents() {
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<CommunicateItem> contentsList = new ArrayList<CommunicateItem>();
		if(mHashMap.containsKey(CommunicateDefine.COMMUNICATE_MSG_LIST)) {	
			try{
				objectList = (ArrayList<HashMap<String,Object>>)mHashMap.get(CommunicateDefine.COMMUNICATE_MSG_LIST);
				for(int i =0;i<objectList.size();i++){
					HashMap<String,Object> objMap = objectList.get(i);
					CommunicateItem contentItem = new CommunicateItem();
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_MSG_ID)){
						String value = (String)objMap.get(CommunicateDefine.COMMUNICATE_MSG_ID);
						contentItem.msg_id = ItemDataTranslate.String2Intger(value);
					}
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_SEND_ID)){
						contentItem.sendid = (String)objMap.get(CommunicateDefine.COMMUNICATE_SEND_ID);
					}
					if(objMap.containsKey(CommunicateDefine.COMMINICATE_SEND_NAME)){
						contentItem.sendname = (String)objMap.get(CommunicateDefine.COMMINICATE_SEND_NAME);
					}
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_REC_ID)){
						contentItem.recvcid = (String)objMap.get(CommunicateDefine.COMMUNICATE_REC_ID);
					}
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_REC_NAME)){
						contentItem.recvcname = (String)objMap.get(CommunicateDefine.COMMUNICATE_REC_NAME);
					}
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_ITEM_LASTUPDATE)){
						String value = (String)objMap.get(CommunicateDefine.COMMUNICATE_ITEM_LASTUPDATE);
						contentItem.lastupdate = DateFormatter.getDateFromSecondsString(value);
					}	
					if(objMap.containsKey(CommunicateDefine.COMMUNICATE_ITEM_BODY)){
						contentItem.body = (String)objMap.get(CommunicateDefine.COMMUNICATE_ITEM_BODY);
					}
					contentsList.add(contentItem);
				}
			}catch(Exception e){

			}	
		}
		return contentsList;
	}
}
