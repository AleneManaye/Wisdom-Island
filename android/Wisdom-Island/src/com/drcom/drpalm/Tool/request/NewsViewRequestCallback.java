package com.drcom.drpalm.Tool.request;


public interface NewsViewRequestCallback {
	public void onError(String err);
	public void onSuccess(NewsParse parse);
}
