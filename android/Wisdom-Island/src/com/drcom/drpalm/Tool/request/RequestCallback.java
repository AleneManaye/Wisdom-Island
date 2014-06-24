package com.drcom.drpalm.Tool.request;

public interface RequestCallback {
	public void onError();
	public void onResponse(String string);
}
