package com.drcom.drpalm.Tool.CommonTranslate;

public class ItemDataTranslate {
	public static Integer String2Intger(String string){
		Integer value = null;		
		try{
			value = Integer.valueOf(string);
		}catch(NumberFormatException e){
			
		}
		return value;
	}
	public static Long String2Long(String string){
		Long value = null;		
		try{
			value = Long.valueOf(string);
		}catch(NumberFormatException e){
			
		}
		return value;
	}
}
