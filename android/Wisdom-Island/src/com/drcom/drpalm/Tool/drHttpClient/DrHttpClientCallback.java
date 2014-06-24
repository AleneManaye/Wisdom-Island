package com.drcom.drpalm.Tool.drHttpClient;

public interface DrHttpClientCallback {
	void onError(String err);
	void onFinished(String wholeString);
}
