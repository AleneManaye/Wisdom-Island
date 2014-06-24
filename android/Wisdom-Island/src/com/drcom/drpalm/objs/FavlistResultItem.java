package com.drcom.drpalm.objs;

import java.util.ArrayList;

/**
 * 返回结果对像
 * @author Administrator
 *
 */
public class FavlistResultItem {
	public boolean result = false;		//请求是否成功
	public String errorcode = "0";		//错误代码
	public String errordes = "";		//错误描述
	
	public ArrayList<FavItem> mFavlist = new ArrayList<FavItem>();
}
