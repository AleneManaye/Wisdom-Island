package com.drcom.drpalm.objs;

import java.util.ArrayList;
/**
 * 
 * @author hzy
 *
 */
public class EventsTypeLanguageResultItem {

	public boolean result = false;		//请求是否成功
	public String errorcode = "0";		//错误代码
	public String errordes = "";		//错误描述
	public ArrayList<EventsTypeLanguage> mEventsTypeLanguage = new ArrayList<EventsTypeLanguage>();
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public String getErrordes() {
		return errordes;
	}
	public void setErrordes(String errordes) {
		this.errordes = errordes;
	}
	
}
