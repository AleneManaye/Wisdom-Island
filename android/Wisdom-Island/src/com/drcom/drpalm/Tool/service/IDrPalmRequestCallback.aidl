package com.drcom.drpalm.Tool.service;
interface IDrPalmRequestCallback
{    
	void onError(String err);
	void onSuccess();
}