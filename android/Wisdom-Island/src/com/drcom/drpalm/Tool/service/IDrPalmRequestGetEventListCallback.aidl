package com.drcom.drpalm.Tool.service;

interface IDrPalmRequestGetEventListCallback
{    
	void onError(String err);
	void onSuccess();
	void onLoading();
}