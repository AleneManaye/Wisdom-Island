package com.drcom.drpalm.Tool.service;
interface IDrPalmRequestGetStatusCallback
{    
	void onError(String err);
	void onSuccess(int count);
}