package com.drcom.drpalm.Tool;

import android.content.Context;

/**
 * 语言管理工具
 * @author zhaojunjie
 *
 */
public class LanguageManagement {
	/**
	 * 语言类型
	 * @author zhaojunjie
	 *
	 */
	public enum CurrentLan{
		SIMPLIFIED_CHINESE,
		COMPLES_CHINESE,
		ENGLISH
	}
	
	/**
	 * 系统当前语言
	 * @param c
	 * @return
	 */
	public static CurrentLan getSysLanguage(Context c){
		String lan = c.getResources().getConfiguration().locale.getCountry();
		if(lan.indexOf("US") > -1 || lan.indexOf("GB") > -1){
			return CurrentLan.ENGLISH;
		}else if(lan.indexOf("TW") > -1 || lan.indexOf("HK") > -1){
			return CurrentLan.COMPLES_CHINESE;
		}else {
			return CurrentLan.SIMPLIFIED_CHINESE;
		}
	}
	
	/**
	 * 系统当前语言(URL拼接专用)
	 * @param c
	 * @return
	 */
	public static String getSysLanguageUrlStr(Context c){
		String lan = c.getResources().getConfiguration().locale.getCountry();
		if(lan.indexOf("US") > -1 || lan.indexOf("GB") > -1){
			return "en";
		}else if(lan.indexOf("TW") > -1 || lan.indexOf("HK") > -1){
			return "zh-hant";
		}else {
			return "zh-hans";
		}
	}
}
