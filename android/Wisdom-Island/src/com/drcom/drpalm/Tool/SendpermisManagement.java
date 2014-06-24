package com.drcom.drpalm.Tool;

import com.drcom.drpalm.GlobalVariables;

/**
 * 发送通告类型管理类
 * @author zhaojunjie
 *
 */
public class SendpermisManagement {
	
	/**
	 * 判断类型是否能发送
	 * @param id
	 * @return
	 */
	public boolean isCanSend(String id){
		if(GlobalVariables.UserSendpermisList == null || GlobalVariables.UserSendpermisList.size()==0)
			return true;
		
		for(int i = 0 ; i < GlobalVariables.UserSendpermisList.size(); i++){
			if(GlobalVariables.UserSendpermisList.get(i).equals(id))
				return true;
		}
		
		return false;
	}
}
