package com.drcom.drpalm.Tool.request;

import java.util.List;

import com.drcom.drpalm.objs.ToursItem;

public interface RequestToursCallback {
	public void onError(String error);
	public void onSuccess(List<ToursItem> list, long date);
}
