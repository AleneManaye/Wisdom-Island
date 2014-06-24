package com.drcom.drpalm.View.controls.myinterface;
/**
 *  请求数据接口后的回调类
 */
public interface UICallBack {
	
	/**
	 * 请求数据接口后的回调方法
	 * @author  zjj
	 * @param responseJson 值为NULL时，请求无返回;否则 请求返回成功
	 */
	public void callBack(Object responseJson);

}

