package com.drcom.drpalm.Tool.XmlParse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.drcom.drpalm.GlobalVariables;

public class ParseXmlFile {
	
//	final static String SettingFilePath_zh = "/Setting/Settings_zh.xml";
//	final static String SettingFilePath_en = "/Setting/Settings_en.xml";
//	final static String SettingFilePath_hk = "/Setting/Settings_hk.xml";
	final static String SettingFilePath = "/Setting/Settings.xml";
	
	final static String TranslateFilePath_zh = "/Setting/Translate/Translate_zh.xml";
	final static String TranslateFilePath_en = "/Setting/Translate/Translate_en.xml";
	final static String TranslateFilePath_hk = "/Setting/Translate/Translate_hk.xml";
	
	public ParseXmlFile()
	{
		
	}
//	//解析配置文件
//	public static  SettingItem parsesettingfile()
//	{
//		SettingItem settingItem = null;
//    	String strFileName = GlobalVariables.getResourceDirectory() + SettingFilePath;
//    	File file = new File( strFileName );
//    	if( !file.exists() )
//    	{
//    		System.out.println("DrPalmActivity.parsesettingfile()");
//    		return settingItem;
//    	}
//    	
//    	FileInputStream read;
//    	try {
//		    	read = new FileInputStream(file);
//		    	SAXParserFactory spf = SAXParserFactory.newInstance();
//		    	SAXParser sp = spf.newSAXParser();
//		
//		    	XMLReader xr = sp.getXMLReader();
//		    	SettingFileHandler handler = new SettingFileHandler();
//		    	xr.setContentHandler(handler);
//		    	xr.parse(new InputSource(read));
//		    	
//		    	settingItem = handler.getParsedData();
//	    	} 
//    	catch (FileNotFoundException e) {
//	    	// TODO Auto-generated catch block
//		    	e.printStackTrace();
//	    	}
//    	catch (SAXException e) {
//	    	// TODO Auto-generated catch block
//		    	e.printStackTrace();
//	    	} 
//    	catch (ParserConfigurationException e) {
//	    	// TODO Auto-generated catch block
//		    	e.printStackTrace();
//	    	} 
//    	catch (IOException e) {
//	    	// TODO Auto-generated catch block
//		    	e.printStackTrace();
//	    	}
//    	return settingItem;
//	}
	//解析配置文件
	public static  Map<String,String> parseTranslateFile()
	{
		Map<String, String> mapTranslate = null;
    	String language = Locale.getDefault().getLanguage();	
    	String country = Locale.getDefault().getCountry();
    	GlobalVariables.gLanguage = language;
		String strFilePath = "" ;
		if(language.contentEquals("zh")){
			if(country.contains("CN"))
				strFilePath = TranslateFilePath_zh;
			else
				strFilePath = TranslateFilePath_hk;
		}
		else if(language.contentEquals("en")){
			strFilePath = TranslateFilePath_en ;
		}
		//else
		//	strFilePath = TranslateFilePath_hk;
    	
    	String strFileName = GlobalVariables.getResourceDirectory() + strFilePath;
    	File file = new File( strFileName );
    	if( !file.exists() )
    	{
    		return mapTranslate;
    	}
    	
    	FileInputStream read;
    	try {
		    	read = new FileInputStream(file);
		    	SAXParserFactory spf = SAXParserFactory.newInstance();
		    	SAXParser sp = spf.newSAXParser();
		    	XMLReader xr = sp.getXMLReader();
		    	TranslateFileHandler handler = new TranslateFileHandler();
		    	xr.setContentHandler(handler);
		    	xr.parse(new InputSource(read));
		    	
		    	mapTranslate = handler.getTranslateMap();
	    	} 
    	catch (FileNotFoundException e) {
	    	// TODO Auto-generated catch block
		    	e.printStackTrace();
	    	}
    	catch (SAXException e) {
	    	// TODO Auto-generated catch block
		    	e.printStackTrace();
	    	} 
    	catch (ParserConfigurationException e) {
	    	// TODO Auto-generated catch block
		    	e.printStackTrace();
	    	} 
    	catch (IOException e) {
	    	// TODO Auto-generated catch block
		    	e.printStackTrace();
	    	}
    	return mapTranslate;
	}

}
