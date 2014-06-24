package com.drcom.drpalm.objs;

import java.util.ArrayList;

/**
 * 考勤信息
 * @author zhaojunjie
 *
 */
public class AttendanceinfoItem {
	public static String KEY_IN = "I";
	public static String KEY_OUT = "O";
	public static String KEY_UNUSUAL = "E";
	
	public String id;
	public String attendancetime;	//考勤日期（自1970年1月1日起的秒数）
	public String status;			//状态标志（正常：N，异常：E）
	public ArrayList<Records> mRecordslist = new ArrayList<Records>();	//打卡记录数组
	//入库时用到
	public String mUsername = "";
	
	public static class Records{
		public String id;
		public String time;		//打卡时间（自1970年1月1日起的秒数）
		public String status;	//打卡状态标志（入园：I，离园：O，其它：E）
		public String des;		//打卡备注
		//入库时用到
		public String mUsername = "";
	}
}
