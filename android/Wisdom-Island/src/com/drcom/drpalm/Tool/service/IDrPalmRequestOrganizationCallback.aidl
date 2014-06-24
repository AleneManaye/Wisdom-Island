package com.drcom.drpalm.Tool.service;
import com.drcom.drpalm.objs.OrganizationBackItem;
interface IDrPalmRequestOrganizationCallback
{    
	void onError(String err);
	void onSuccess(out OrganizationBackItem item);
}