package com.drcom.drpalm.Tool.CommonTranslate;

public class StringTranslate {
	public static String HTMLChange(String source){  
	    String changeStr = "";  
	    changeStr = source.replaceAll("&","&amp;");    
	    //转换字符串中的“&”符号  
	    changeStr = changeStr.replaceAll(" ","&nbsp;");               
	    //转换字符串中的空格  
	    changeStr = changeStr.replaceAll("<","&lt;");
	    //转换字符串中的“<”符号  
	    changeStr = changeStr.replaceAll(">","&gt;");
	    //转换字符串中的“>”符号  
	    changeStr = changeStr.replaceAll("\n","<br>"); 
	    //转换字符串中的回车换行  
	    return changeStr;  
	}  
}
