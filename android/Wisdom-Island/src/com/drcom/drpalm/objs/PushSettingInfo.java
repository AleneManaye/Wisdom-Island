package com.drcom.drpalm.objs;

import java.util.ArrayList;
import java.util.List;

public class PushSettingInfo {
	public boolean ifpush = false;
	public boolean ifsound = false;
	public boolean ifshake = false;
	public List<PushTime> pushTime = null;
	public PushSettingInfo(){
		pushTime = new ArrayList<PushSettingInfo.PushTime>();
	}
	public static class PushTime{
		public String start = "";
		public String end = "";
	}
}
