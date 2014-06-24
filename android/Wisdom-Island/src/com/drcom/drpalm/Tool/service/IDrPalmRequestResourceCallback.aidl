package com.drcom.drpalm.Tool.service;
import com.drcom.drpalm.objs.ToursItem;
interface IDrPalmRequestResourceCallback
{    
	void onError(String err);
	void onSuccess(in List<ToursItem> list, long date);
}