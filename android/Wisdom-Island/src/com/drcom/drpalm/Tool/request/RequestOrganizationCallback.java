package com.drcom.drpalm.Tool.request;

import com.drcom.drpalm.objs.OrganizationBackItem;

public interface RequestOrganizationCallback {
	public void onError(String str);
	public void onSuccess(OrganizationBackItem item);
}
