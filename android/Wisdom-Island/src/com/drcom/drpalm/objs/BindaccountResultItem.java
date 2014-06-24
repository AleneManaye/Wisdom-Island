package com.drcom.drpalm.objs;

import java.util.ArrayList;
import java.util.List;

public class BindaccountResultItem {

	public boolean result = false;		//请求是否成功
	public String errorcode = "0";		//错误代码
	public String errordes = "";		//错误描述
	public List<BindaccountItem> mBindaccountItem = new ArrayList<BindaccountItem>(); //绑定账号
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
	public List<BindaccountItem> getmBindaccountItem() {
		return mBindaccountItem;
	}
	public void setmBindaccountItem(ArrayList<BindaccountItem> mBindaccountItem) {
		this.mBindaccountItem = mBindaccountItem;
	}
	
}
