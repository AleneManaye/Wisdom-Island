package com.drcom.drpalm.Tool.service;
import com.drcom.drpalm.objs.LoginSucItem;
interface IDrPalmRequestLoginCallback
{    
	void onError(String err);
	//void onSuccess(out LoginSucItem item);
}