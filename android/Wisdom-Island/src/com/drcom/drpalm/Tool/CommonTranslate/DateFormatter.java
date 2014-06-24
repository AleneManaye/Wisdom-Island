package com.drcom.drpalm.Tool.CommonTranslate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import com.drcom.drpalm.GlobalVariables;
//import com.drcom.drpalm4tianzhujiao.R;

public class DateFormatter {
	static final SimpleDateFormat sDateFormatDMYHMS = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	static final SimpleDateFormat sDateFormatYYYYMMDDHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
	static final SimpleDateFormat sDateFormatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	static final SimpleDateFormat sDateFormatMMDD = new SimpleDateFormat("MM-dd", Locale.US);
	static final SimpleDateFormat sDateFormatHHmm = new SimpleDateFormat("HH:mm", Locale.US);
//	static final SimpleDateFormat sDateFormatMMDDChina = new SimpleDateFormat("M" 
//			+ GlobalVariables.gAppContext.getString(R.string.month) 
//			+ "d"
//			+ GlobalVariables.gAppContext.getString(R.string.day) 
//			, Locale.US);
	
	public static TimeZone getTimeZone(int timeZoneOffset){
		if (timeZoneOffset > 13 || timeZoneOffset < -12) {  
	        timeZoneOffset = 0;  
	    }  
		TimeZone timeZone;  
	    String[] ids = TimeZone.getAvailableIDs(timeZoneOffset * 60 * 60 * 1000); 
	    if (ids.length == 0) {  
	        // if no ids were returned, something is wrong. use default TimeZone  
	        timeZone = TimeZone.getDefault();  
	    } else {  
	        timeZone = new SimpleTimeZone(timeZoneOffset * 60 * 60 * 1000, ids[0]);  
	    }  	   
	    return timeZone;
	}
	
	
	//取得的时间秒转为毫秒再转时间格式	
	public static Date getDateFromSecondsString(String strTime){
		try{
			Long times = ItemDataTranslate.String2Long(strTime);
			times = times * 1000;
			return getDateFromMilliSeconds(times);
		}
		catch(Exception e){
			
		}
		return null;		
	}
	public static Date getDateFromMilliSecondsString(String strTime){
		Long times = ItemDataTranslate.String2Long(strTime);
		return getDateFromMilliSeconds(times);
	}
	public static Date getDateFromMilliSeconds(Long times){
		try{
			return new Date(times);
		}catch(Exception e){
			
		}
		return null;
	}
	
	public static Date getDateDMYHMS(Integer times){
		String strTime = Integer.toString(times);		
		return getDateDMYHMS(strTime); 
	}
	public static Date getDateDMYHMS(String times){
		
		Date date = null;
		try {
			sDateFormatDMYHMS.setTimeZone(getTimeZone(8));
			date = sDateFormatDMYHMS.parse(times);				
		} catch (ParseException e) {
			e.printStackTrace();			
		} 
		return date;
	}
	public static String getStringYYYYMMDDHHmm(Date date){		
		sDateFormatDMYHMS.setTimeZone(getTimeZone(8));
		return getStringFromDateFormatter(sDateFormatYYYYMMDDHHmm,date);
	}
	public static String getStringYYYYMMDD(Date date){
		
		sDateFormatYYYYMMDD.setTimeZone(getTimeZone(8));
		return getStringFromDateFormatter(sDateFormatYYYYMMDD,date);
	}
	public static String getStringMMDD(Date date){
		
		sDateFormatMMDD.setTimeZone(getTimeZone(8));
		return getStringFromDateFormatter(sDateFormatMMDD,date);
	}
//	public static String getStringMMDDChina(Date date){
//			
//		sDateFormatMMDDChina.setTimeZone(getTimeZone(8));
//		return getStringFromDateFormatter(sDateFormatMMDDChina,date);
//	}
	public static String getStringHHmm(Date date){
		
		sDateFormatMMDD.setTimeZone(getTimeZone(8));
		return getStringFromDateFormatter(sDateFormatHHmm,date);
	}
	public static String getStringFromDateFormatter(SimpleDateFormat formatter,Date date){
		String string = "";
		try {
			string = formatter.format(date);
		}
		catch(NullPointerException e){
			e.printStackTrace();	
		}
		return string;
	}
	
	/**
	 * 取周几
	 * @param weekDay
	 * @return
	 */
//	public static String getWeekStr(int weekDay){
//		switch (weekDay) {
//		case 0:
//			return GlobalVariables.gAppContext.getString(R.string.sunday);
//		case 1:
//			return GlobalVariables.gAppContext.getString(R.string.monday);
//		case 2:
//			return GlobalVariables.gAppContext.getString(R.string.tuesday);
//		case 3:
//			return GlobalVariables.gAppContext.getString(R.string.wednesday);
//		case 4:
//			return GlobalVariables.gAppContext.getString(R.string.thursday);
//		case 5:
//			return GlobalVariables.gAppContext.getString(R.string.firday);
//		case 6:
//			return GlobalVariables.gAppContext.getString(R.string.saturday);
//
//		default:
//			return "";
//		}
//	}
}
