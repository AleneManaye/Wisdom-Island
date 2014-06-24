package com.drcom.drpalm.Tool.drHttpClient;

public interface DrHttpClientKeepAliveCallback extends DrHttpClientCallback{
	void onError(String err);
	void onRecvString(String line);
	//void onFinished();
}
