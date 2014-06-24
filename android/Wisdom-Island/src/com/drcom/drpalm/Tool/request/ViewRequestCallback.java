package com.drcom.drpalm.Tool.request;


public interface ViewRequestCallback {
	public void onError(String err);
	public void onSuccess(BaseParse parse);
}
