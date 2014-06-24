package com.drcom.drpalm.Tool.request;

public interface RequestGetEventListCallback {
	public void onError(String str);
	public void onSuccess();
	public void onLoading();
}
