package com.drcom.drpalm.objs;

import java.io.Serializable;

/**
 * 没提交成功的收藏
 * @author zhaojunjie
 *
 */
public class FavItem implements Serializable{
	public static String STATUS_MARK = "N";
	public static String STATUS_CANCEL = "C";
	
	//入库时用到
	public String mUsername = "";
	public String mEventid = "";
	public String mStatus = STATUS_MARK;
	
	//
	public String mCategroyid ;		//通告类型
	public String mLastupdatetime;	//最后更新时间
}
