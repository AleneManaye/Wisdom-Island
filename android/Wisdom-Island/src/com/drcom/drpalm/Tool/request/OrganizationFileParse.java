package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;

import com.drcom.drpalm.Tool.service.ItemDataTranslate;
import com.drcom.drpalm.objs.OrganizationItem;

public class OrganizationFileParse extends BaseParse{
	public OrganizationFileParse(HashMap<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
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
						if(objMap.containsKey(OrganizationDefine.ORGINFO_PID)){
							orgItem.orgPid = ItemDataTranslate.String2Intger((String)objMap.get(OrganizationDefine.ORGINFO_PID));
						}
						if(objMap.containsKey(OrganizationDefine.ORGINFO_STATUS)){
							orgItem.orgStatus = (String)objMap.get(OrganizationDefine.ORGINFO_STATUS);;
						}
						orgList.add(orgItem);
					}
				}				
			}catch(Exception e){
				int a =0;
			}	
		}
		return orgList;
	}
}
