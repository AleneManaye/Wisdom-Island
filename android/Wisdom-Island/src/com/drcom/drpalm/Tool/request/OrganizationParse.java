package com.drcom.drpalm.Tool.request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.drcom.drpalm.Tool.service.ItemDataTranslate;
import com.drcom.drpalm.objs.OrgLimitItem;
import com.drcom.drpalm.objs.OrganizationItem;
import com.drcom.drpalm.objs.StudentItem;

public class OrganizationParse extends BaseParse{
	public OrganizationParse(HashMap<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}
	public String parseCurrentType(){
		String str = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_RECTYPE)) {
			str = (String)mHashMap.get((OrganizationDefine.ORGINFO_RECTYPE));
		}
		return str;
	}	
	public Integer parseRetainCount(){
		String str = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_RECOUNT)) {
			str = (String)mHashMap.get((OrganizationDefine.ORGINFO_RECOUNT));
		}
		return ItemDataTranslate.String2Intger(str);
	}
	public boolean parseMore(){
		String str = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_MORE)) {
			str = (String)mHashMap.get((OrganizationDefine.ORGINFO_MORE));
		}
		return (1 == ItemDataTranslate.String2Intger(str))?true:false;
	}
	
	public Integer parseEndIndex(){
		String str = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_END)) {
			str = (String)mHashMap.get((OrganizationDefine.ORGINFO_END));
		}
		return ItemDataTranslate.String2Intger(str);
	}

	public ArrayList<OrganizationItem> parseOrganization(){
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<OrganizationItem> orgList = new ArrayList<OrganizationItem>();
		if(mHashMap.containsKey(OrganizationDefine.USEROWNORGSINFO_ORGLIST)) {	
			try{
				Object obj = mHashMap.get(OrganizationDefine.USEROWNORGSINFO_ORGLIST);
				if(null != obj){
					objectList = (ArrayList<HashMap<String,Object>>)obj;
					for(int i =0;i<objectList.size();i++){
						HashMap<String,Object> objMap = objectList.get(i);
						OrganizationItem orgItem = new OrganizationItem();
						if(objMap.containsKey(OrganizationDefine.ORGINFO_ID))
							orgItem.orgID = ItemDataTranslate.String2Intger((String)objMap.get(OrganizationDefine.ORGINFO_ID));
						if(objMap.containsKey(OrganizationDefine.ORGINFO_NAME))
							orgItem.orgName = (String)objMap.get(OrganizationDefine.ORGINFO_NAME);
						if(objMap.containsKey(OrganizationDefine.ORGINFO_TYPE))
							orgItem.orgType = (String)objMap.get(OrganizationDefine.ORGINFO_TYPE);
						orgList.add(orgItem);
					}
				}				
			}catch(Exception e){
				int a =0;
			}	
		}
		return orgList;
	}
	public ArrayList<OrganizationItem> parseParentOrganization(){
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<OrganizationItem> orgList = new ArrayList<OrganizationItem>();
		if(mHashMap.containsKey(OrganizationDefine.USEROWNORGSINFO_ORGLIST)) {	
			try{
				Object obj = mHashMap.get(OrganizationDefine.USEROWNORGSINFO_ORGLIST);
				if(null != obj){
					objectList = (ArrayList<HashMap<String,Object>>)obj;
					for(int i =0;i<objectList.size();i++){
						HashMap<String,Object> objMap = objectList.get(i);
						OrganizationItem orgItem = new OrganizationItem();
						if(objMap.containsKey(OrganizationDefine.ORGINFO_ID))
							orgItem.orgID = ItemDataTranslate.String2Intger((String)objMap.get(OrganizationDefine.ORGINFO_ID));
						if(objMap.containsKey(OrganizationDefine.ORGINFO_NAME))
							orgItem.orgName = (String)objMap.get(OrganizationDefine.ORGINFO_NAME);						
						orgList.add(orgItem);
					}
				}				
			}catch(Exception e){
				int a =0;
			}	
		}
		return orgList;
	}
	public ArrayList<StudentItem> parseSearchStudent(){
		ArrayList<HashMap<String,Object>> objectList = null;
		ArrayList<StudentItem> studentList = new ArrayList<StudentItem>();
		if(mHashMap.containsKey(OrganizationDefine.STUDENT_STULIST)) {	
			try{
				Object obj = mHashMap.get(OrganizationDefine.STUDENT_STULIST);
				objectList = (ArrayList<HashMap<String,Object>>)obj;
				if(null != obj){
					objectList = (ArrayList<HashMap<String,Object>>)obj;
					for(int i =0;i<objectList.size();i++){
						HashMap<String,Object> objMap = objectList.get(i);
						StudentItem item = new StudentItem();
						if(objMap.containsKey(OrganizationDefine.STUDENT_ID))
							item.id = ItemDataTranslate.String2Intger((String)objMap.get(OrganizationDefine.STUDENT_ID));
						if(objMap.containsKey(OrganizationDefine.ORGINFO_ID))
							item.name = (String)objMap.get(OrganizationDefine.STUDENT_STUNAME);
						if(objMap.containsKey(OrganizationDefine.ORGINFO_TYPE))
							item.orgname = (String)objMap.get(OrganizationDefine.STUDENT_ORGNAME);
						studentList.add(item);
					}
				}
			}catch(Exception e){
				int a =0;
			}
		}
		return studentList;
	}
	
	public String parseOrgUrl(){
		String str = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_URL)) {
			str = (String)mHashMap.get((OrganizationDefine.ORGINFO_URL));
		}
		return str;
	}
	
	public String parseOrgChecksum(){
		String str = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_CHECKSUM)) {
			str = (String)mHashMap.get((OrganizationDefine.ORGINFO_CHECKSUM));
		}
		return str;
	}
	
	public String parseSelfOrgid(){
		String str = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_SELFORGID)) {
			str = (String)mHashMap.get((OrganizationDefine.ORGINFO_SELFORGID));
		}
		return str;
	}
	
	public Long parseOrgLastupdate(){
		long lastupdate = -1;
		if(mHashMap.containsKey(OrganizationDefine.ORGINFO_LASTUPDATE)) {
			lastupdate = Long.valueOf((String)mHashMap.get((OrganizationDefine.ORGINFO_LASTUPDATE)));
		}
		return lastupdate;
	}
	
	public List<OrgLimitItem> parseOrgLimitList(){
		List<OrgLimitItem> list = new ArrayList<OrgLimitItem>();
		ArrayList<HashMap<String,Object>> objectList = null;
		if(mHashMap.containsKey(OrganizationDefine.ORGLIMIT_TOPORGS)) {	
			try{
				Object obj = mHashMap.get(OrganizationDefine.ORGLIMIT_TOPORGS);
				objectList = (ArrayList<HashMap<String,Object>>)obj;
				if(null != obj){
					objectList = (ArrayList<HashMap<String,Object>>)obj;
					for(int i =0;i<objectList.size();i++){
						HashMap<String,Object> objMap = objectList.get(i);
						OrgLimitItem item = new OrgLimitItem();
						if(objMap.containsKey(OrganizationDefine.ORGLIMIT_ORGID))
							item.orgID = ItemDataTranslate.String2Intger((String)objMap.get(OrganizationDefine.ORGLIMIT_ORGID));
						list.add(item);
					}
				}
			}catch(Exception e){
				int a =0;
			}
		}
		return list;
	}
	
}
