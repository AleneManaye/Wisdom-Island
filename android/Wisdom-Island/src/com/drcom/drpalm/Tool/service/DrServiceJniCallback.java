package com.drcom.drpalm.Tool.service;

public interface DrServiceJniCallback {
	public void onError(byte[] value);
	public void onSuccess(byte[] value);
	public void onReceiveData(byte[] value);
}
