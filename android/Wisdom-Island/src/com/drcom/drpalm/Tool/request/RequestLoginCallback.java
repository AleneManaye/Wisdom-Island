package com.drcom.drpalm.Tool.request;

import com.drcom.drpalm.Tool.XmlParse.UserInfo;


public interface RequestLoginCallback{
	public void onError(String str);
	public void onSuccess(UserInfo item);
}
