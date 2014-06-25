package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.objs.PushSettingInfo;
import com.drcom.drpalm.objs.PushSettingInfo.PushTime;
import com.drcom.drpalm.objs.ToursItem;
import com.wisdom.island.R;

public class GatewayParse extends BaseParse {
	
	/**
	 * Get gateway
	**/
	public static final String SCHOOLID = "schoolid";
	public static final String SCHOOLKEY = "schoolkey";
	public static final String GATEWAYLIST = "gwlist";
	public static final String GATEWAYNAME = "gw";
	public static final String GATEWAYADDR = "addr";
	public static final String GATEWAYPORT = "port";
	public static final String GATEWAYURL = "accurl";
	//push设置信息
	public static final String PUSHINFO = "pushinfo";
	public static final String IFPUSH = "ifpush";
	public static final String IFSOUND = "ifsound";
	public static final String IFSHAKE = "ifshake";
	public static final String PUSHTIME = "pushtime";
	public static final String PUSHSTART = "start";
	public static final String PUSHEND = "end";
	
	public GatewayParse(HashMap<String, Object> map) {
		super(map);
	}
	
	public String parseSchoolID(){
		String strSchoolId = null;
		if(mHashMap.containsKey(SCHOOLID)) {
			strSchoolId = (String)mHashMap.get(SCHOOLID);
		}	
		return strSchoolId;
	}
	public String parseSchoolKey(){
		String strSchoolKey = null;
		if(mHashMap.containsKey(SCHOOLKEY)) {
			strSchoolKey = (String)mHashMap.get(SCHOOLKEY);
		}	
		return strSchoolKey;
	}
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String,String>> parseGateway() {			
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<HashMap<String,String>> gatewayList = new ArrayList<HashMap<String,String>>();

		if(mHashMap.containsKey(GATEWAYLIST)) {		
			objectList = (ArrayList<HashMap<String,Object>>)mHashMap.get(GATEWAYLIST);
			for(int i =0;i<objectList.size();i++){
				HashMap<String,String> map = new HashMap<String,String>(); 
				HashMap<String,Object> objMap = objectList.get(i);
				String name = (String)objMap.get(GATEWAYNAME);
				String addr = (String)objMap.get(GATEWAYADDR);
				String port = (String)objMap.get(GATEWAYPORT);
				String accurl = (String)objMap.get(GATEWAYURL);
				map.put(GATEWAYNAME, name);
				map.put(GATEWAYADDR, addr);
				map.put(GATEWAYPORT, port);
				map.put(GATEWAYURL, accurl);
				gatewayList.add(map);
			}
		}	
		return gatewayList;
	}

	/**
	 * Login
	 */
	public static final String ACCTYPE = "acctype";
	public static final String SESSIONKEY = "sessionkey";	
	public static final String USERNAME = "username";
	public static final String UNGETCOUNT = "ungetcount";

	public String paresUsername(){
		String strUsername = null;
		if(mHashMap.containsKey(USERNAME)) {
			strUsername = (String)mHashMap.get((USERNAME));
		}
		return strUsername;
	}
	public String paresAccessType(){
		String strAccType = null;
		if(mHashMap.containsKey(ACCTYPE)) {
			strAccType = (String)mHashMap.get((ACCTYPE));
		}
		return strAccType;
	}
	public String paresSessionKey(){
		String strSessionKey = null;
		if(mHashMap.containsKey(SESSIONKEY)) {
			strSessionKey = (String)mHashMap.get((SESSIONKEY));
		}
		return strSessionKey;
	}
	public String parseLastUpdateDate(){
		String date = null;
		if(mHashMap.containsKey(RequestDefine.TOURS_PACKET_OPERATE)){
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_PACKET_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(RequestDefine.TOURS_LASTMDATE)) {
						date = (String)opMap.get(RequestDefine.TOURS_LASTMDATE);	
					}
				}	
			}catch (Exception e){
				
			}	
		}	
		return date;
	}
	
	public PushSettingInfo parsePushSetting(){
		PushSettingInfo pushSetting = new PushSettingInfo();
		if(mHashMap.containsKey(PUSHINFO)){
			try{
				Object pushinfo = (HashMap<String, Object>)mHashMap.get(PUSHINFO);
				if(null != pushinfo){
					HashMap<String, Object> pushinfoMap = (HashMap<String, Object>)pushinfo;
					if(pushinfoMap.containsKey(IFPUSH)){
						String value = (String)pushinfoMap.get(IFPUSH);
						if(value!=null){
							if(value.equals("1")){
								pushSetting.ifpush = true;	
							}else{
								pushSetting.ifpush = false;
							}
						}
					}
					if(pushinfoMap.containsKey(IFSOUND)){
						String value = (String)pushinfoMap.get(IFSOUND);
						if(value!=null){
							if(value.equals("1")){
								pushSetting.ifsound = true;	
							}else{
								pushSetting.ifsound = false;
							}
						}
					}
					if(pushinfoMap.containsKey(IFSHAKE)){
						String value = (String)pushinfoMap.get(IFSHAKE);
						if(value!=null){
							if(value.equals("1")){
								pushSetting.ifshake = true;	
							}else{
								pushSetting.ifshake = false;
							}
						}
					}
					if(pushinfoMap.containsKey(PUSHTIME)){
						try{
							Object pushtimeobj = (ArrayList<HashMap<String, Object>>)pushinfoMap.get(PUSHTIME);
							if(null != pushtimeobj){
								ArrayList<HashMap<String,Object>> pushtimeList = new ArrayList<HashMap<String,Object>>();
								pushtimeList = (ArrayList<HashMap<String, Object>>)pushtimeobj;
								for(int i=0;i<pushtimeList.size();i++){
									HashMap<String,Object> pushtimeMap = pushtimeList.get(i);
									PushTime item = new PushTime();
									if(pushtimeMap.containsKey(PUSHSTART)){
										String value = (String)pushtimeMap.get(PUSHSTART);
										item.start = value;
									}
									if(pushtimeMap.containsKey(PUSHEND)){
										String value = (String)pushtimeMap.get(PUSHEND);
										item.end = value;
									}
									pushSetting.pushTime.add(item);
								}
							}
						}catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return pushSetting;
	}
	
	public ArrayList<ToursItem> parseToursItems(){
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<ToursItem> toursItemList = new ArrayList<ToursItem>();
		if(mHashMap.containsKey(RequestDefine.TOURS_PACKET_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_PACKET_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(RequestDefine.TOURS_RESLIST)) {	
						try{
							objectList = (ArrayList<HashMap<String,Object>>)opMap.get(RequestDefine.TOURS_RESLIST);
							for(int i =0;i<objectList.size();i++){
								HashMap<String,Object> objMap = objectList.get(i);
								ToursItem item = new ToursItem();					
								if(objMap.containsKey(RequestDefine.TOURS_RESNAME))
									item.name = (String)objMap.get(RequestDefine.TOURS_RESNAME);		
								if(objMap.containsKey(RequestDefine.TOURS_URL))
									item.url = (String)objMap.get(RequestDefine.TOURS_URL);			
								if(objMap.containsKey(RequestDefine.TOURS_VERIFYCODE))
									item.verifycode = (String)objMap.get(RequestDefine.TOURS_VERIFYCODE);	
								toursItemList.add(item);
							}			
						}catch(Exception e){
						}
					}
				}	
			}catch (Exception e){
				
			}
		}
		return toursItemList;
	}
	
	public boolean parseAppResourceOperate(){
		boolean bFlag = false;
		if(mHashMap.containsKey(RequestDefine.TOURS_PACKET_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_PACKET_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(OPRET)) {
						Object obj2 = (HashMap<String, Object>)opMap.get(OPRET);
						if(null != obj2){
							HashMap<String, Object> opMap2 = (HashMap<String,Object>)obj2;
							if(opMap2.containsKey(OPFLAG)){
								String str = (String)opMap2.get(OPFLAG);
								if(str.contentEquals("1")){
									bFlag = true;
								}else{
									bFlag = false;
								}
							}
						}	
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return bFlag;
	}
	
	public boolean parseAppAttendanceOperate(){
		boolean bFlag = false;
		if(mHashMap.containsKey(RequestDefine.TOURS_ATTENDANCE_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_ATTENDANCE_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(OPRET)) {
						Object obj2 = (HashMap<String, Object>)opMap.get(OPRET);
						if(null != obj2){
							HashMap<String, Object> opMap2 = (HashMap<String,Object>)obj2;
							if(opMap2.containsKey(OPFLAG)){
								String str = (String)opMap2.get(OPFLAG);
								if(str.contentEquals("1")){
									bFlag = true;
								}else{
									bFlag = false;
								}
							}
						}	
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return bFlag;
	}
	
	public boolean parsePswUrlOperate(){
		boolean bFlag = false;
		if(mHashMap.containsKey(RequestDefine.TOURS_PSWURL_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_PSWURL_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(OPRET)) {
						Object obj2 = (HashMap<String, Object>)opMap.get(OPRET);
						if(null != obj2){
							HashMap<String, Object> opMap2 = (HashMap<String,Object>)obj2;
							if(opMap2.containsKey(OPFLAG)){
								String str = (String)opMap2.get(OPFLAG);
								if(str.contentEquals("1")){
									bFlag = true;
								}else{
									bFlag = false;
								}
							}
						}	
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return bFlag;
	}
	
	public String parsePswUrl(){
		String pswUrl = "";
		if(mHashMap.containsKey(RequestDefine.TOURS_PSWURL_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_PSWURL_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(RequestDefine.TOURS_URL)) {
						String value = (String)opMap.get(RequestDefine.TOURS_URL);
						pswUrl = value;
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return pswUrl;
	}
	
	public String parseAttendanceUrl(){
		String attendanceUrl = "";
		if(mHashMap.containsKey(RequestDefine.TOURS_ATTENDANCE_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_ATTENDANCE_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(RequestDefine.TOURS_URL)) {
						String value = (String)opMap.get(RequestDefine.TOURS_URL);
						attendanceUrl = value;
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return attendanceUrl;
	}
	
	public String parsePswUrlError(){
		String errString = "";
		if(mHashMap.containsKey(RequestDefine.TOURS_PSWURL_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_PSWURL_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(OPRET)) {
						Object obj2 = (HashMap<String, Object>)opMap.get(OPRET);
						if(null != obj2){
							HashMap<String, Object> opMap2 = (HashMap<String,Object>)obj2;
							if(opMap2.containsKey(CODE)){
								String str = (String)opMap2.get(CODE);
								if(str.equals(RequestDefine.NoLinkForPassword)){
									errString = GlobalVariables.gAppContext.getString(R.string.NoLinkForPassword);
								}
							}
						}	
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return errString;
	}
	
	public String parseToursError(){
		String errString = "";
		if(mHashMap.containsKey(RequestDefine.TOURS_PACKET_OPERATE)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(RequestDefine.TOURS_PACKET_OPERATE);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(OPRET)) {
						Object obj2 = (HashMap<String, Object>)opMap.get(OPRET);
						if(null != obj2){
							HashMap<String, Object> opMap2 = (HashMap<String,Object>)obj2;
							if(opMap2.containsKey(CODE)){
								String str = (String)opMap2.get(CODE);
								if(str.equals(RequestDefine.UnknownClientType)){
									errString = GlobalVariables.gAppContext.getString(R.string.UnknownClientType);
								}else if(str.equals(RequestDefine.NoNewResourceDownLoad)){
									errString = GlobalVariables.gAppContext.getString(R.string.NoNewResourceDownLoad);
								}else if(str.equals(RequestDefine.NoResourceDownLoad)){
									errString = GlobalVariables.gAppContext.getString(R.string.NoResourceDownLoad);
								}
							}
						}	
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return errString;
	}
}
