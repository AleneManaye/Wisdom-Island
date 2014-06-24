package com.drcom.drpalm.Tool.XmlParse;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*      
 * Date         : 2012-6-19
 * Author       : JiangBo
 * Copyright    : City Hotspot Co., Ltd.
 * 功能                         : 翻译文件XML解析
 */
public class TranslateFileHandler extends DefaultHandler {
	
	final static String TAGNAME = "string";
	
	final static String CenterAddress = "CenterAddress";
	final static String SchoolKey     = "SchoolKey";
	final static String ShowAboutInSettingModule = "ShowAboutInSettingModule";
	//final static String ModuleList    = "ModuleList";
	
	//加载模块
	final static String ModuleName = "Name";
	final static String ModuleTips = "Tips";
	final static String ModulePicPath = "PicPath";
	final static String ModuleSelectedPicPath = "SelectedPicPath";
	final static String Module     = "Module" ;
	//News分类
	final static String NewsCatagory = "NewsCategory";
	final static String NewsCatagoryName = "NewsCategoryName";
	final static String NewsCatagoryId = "NewsCategoryId";	
	//Events分类
	final static String EventsCategory = "EventsCategory";
	final static String EventsTitle = "EventsTitle";
	final static String EventsIds = "EventsIds";
	final static String EventsAccess = "EventsAccess";
	final static String EventsType = "EventsType";
	final static String EventsLocal = "EventsLocal";
	
	private Map<String, String>  mMapTranslate = new HashMap<String, String>();
	
	
	private String value="";
	private String key= "";
	private String localName="";
	
	public Map<String, String> getTranslateMap() { 
        return mMapTranslate; 
    }
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		//super.characters(ch, start, length);
		 //得到xml值
		  String data=new String(ch,start,length);
		  value = data;
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		// TODO Auto-generated method stub
		if(localName.contentEquals(TAGNAME))
		{
			mMapTranslate.put(key, value);
			value = "";
			key = "";
		}
		System.out.println("");
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		this.key = attributes.getValue("name");
		this.localName = localName;
	}

}
