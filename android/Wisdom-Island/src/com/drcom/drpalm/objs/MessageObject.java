package com.drcom.drpalm.objs;

public class MessageObject{
	public boolean isSuccess = true;
	public boolean isSendUpdateCount = true;
	public MessageObject(){}
	public MessageObject(boolean isSuccess, boolean isSendUpdateCount){
		this.isSuccess = isSuccess;
		this.isSendUpdateCount = isSendUpdateCount;
	}
};
