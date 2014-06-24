package com.drcom.drpalm.Tool.request;



public interface PushCallback{
	public void onError(String err);
	public void onSuccess(PushParse parse);
}