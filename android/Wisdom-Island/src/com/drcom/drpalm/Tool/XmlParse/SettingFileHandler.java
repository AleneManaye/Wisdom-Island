package com.drcom.drpalm.Tool.XmlParse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*      
 * Date         : 2012-6-6
 * Author       : JiangBo
 * Copyright    : City Hotspot Co., Ltd.
 * 功能                         : 配置文件XML解析(其中包括需要加载的模块,News分类, Events分类等。。)
 */
public class SettingFileHandler extends DefaultHandler {
	
	final static String CenterAddress = "CenterAddress";
	final static String SchoolKey     = "SchoolKey";
	final static String ShowAboutInSettingModule = "ShowAboutInSettingModule";
	final static String SourceVersion = "ResourceVersion";
	//final static String ModuleList    = "ModuleList";
	
	//加载模块
	final static String ModuleName = "Name";
	final static String ModuleTips = "Tips";
	final static String ModulePicPath = "PicPath";
	final static String ModuleSelectedPicPath = "FocusPicPath";
	final static String ModuleOfflinePicPath = "OfflinePicPath";
	final static String ModuleOfflineSelectedPicPath = "OfflineFocusPicPath";
	final static String Module     = "Module" ;
	//News分类
	final static String NewsCatagory = "NewsCategory";
	final static String NewsCatagoryName = "NewsCategoryName";
	final static String NewsCatagoryId = "NewsCategoryId";
	final static String NewsCatagoryPicPath = "NewsCategoryPicPath";
	final static String NewsCatagoryPressedPicPath = "NewsCategorySelectedPicPath";
	//Events分类
	final static String EventsCategory = "EventsCategory";
	final static String EventsTitle = "EventsTitle";
	final static String EventsIds = "EventsIds";
	final static String EventsAccess = "EventsAccess";
	final static String EventsType = "EventsType";
	final static String EventsLocal = "EventsLocal";
	final static String EventsCatagoryPicPath = "EventsCategoryPicPath";
	final static String EventsCatagoryPressedPicPath = "CategorySPicPath";
	
	
	//模块信息
	public static class ModuleInfo {
		public String  strModuleName;
		public String  strModuleTipsKey;
		public String  strPicPath;
		public String  strSelectedPicPath;  //模块选中时图片
		public String  strOfflinePicPath;
		public String  strOfflineSelectedPicPath;
		public ModuleInfo(){
			strModuleName = "";
			strModuleTipsKey = "";
			strPicPath = "";
			strSelectedPicPath = "";
			strOfflinePicPath = "";
			strOfflineSelectedPicPath = "";
		}	
	}
	//News分类
	public static class NewsCategory{
		public String strNewsCategoryNameKey;
		public int    nNewsCategoryId ;
		public String strNewsCategoryPicPath;
		public String strNewsCategoryPressedPicPath;
		public NewsCategory(){
			strNewsCategoryNameKey = "";
			nNewsCategoryId = 0;
			strNewsCategoryPicPath = "";
			strNewsCategoryPressedPicPath = "";
		}
	}
	
	//Events分类
	public static class EventsCategory{
		public String strEventsCategoryNameKey ;
		public int    nEventsCategoryId;
		public int    nEventsCategoryAccess;
		public int    nEventsCategoryType;
		public int    nEventsCategoryLocal;
		public String strEventsCatagoryPicPath;
		public String strEventsCatagoryPressedPicPath;
		public EventsCategory(){
			strEventsCategoryNameKey = "";
			nEventsCategoryId = 0;
			nEventsCategoryAccess = 0;
			nEventsCategoryType = 0;
			nEventsCategoryLocal = 0;
			strEventsCatagoryPicPath = "";
			strEventsCatagoryPressedPicPath = "";
		}
	}

	public static class SettingItem {
		public boolean bShowAboutInSettingModule;
		public String  strCenterAddress;
		//public String  strSchoolKey;
		public String  strResourceVersion;
		public List<ModuleInfo> m_ListModuleInfo;
		public List<NewsCategory> m_ListNewsCategory;
		public List<EventsCategory> m_ListEventsCategories;
		public SettingItem(){
			bShowAboutInSettingModule = false;
			strCenterAddress = "";
			//strSchoolKey = "";
			strResourceVersion = "";
			m_ListModuleInfo = new ArrayList<SettingFileHandler.ModuleInfo>();
			m_ListNewsCategory = new ArrayList<SettingFileHandler.NewsCategory>();
			m_ListEventsCategories  = new ArrayList<SettingFileHandler.EventsCategory>();
		}	
	}
	private ModuleInfo  m_ModuleInfo = null;
	private NewsCategory m_NewsCategory = null;
	private EventsCategory m_EventsCategory = null;
	private SettingItem  m_SettingItem = null;
	
	private String temp="";
	private String localName="";
	
	public SettingItem getParsedData() { 
        return m_SettingItem; 
    }
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		//super.characters(ch, start, length);
		 //得到xml值
		  String data=new String(ch,start,length);
		  temp = data;
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
		if(localName.contentEquals(CenterAddress)) //CenterAddress
		{
			m_SettingItem.strCenterAddress = temp;
			temp = "";
		}
//		else if(localName.contentEquals(SchoolKey)) //SchoolKey
//		{
//			m_SettingItem.strSchoolKey = temp;
//		    temp="";
//		}
		else if(localName.contentEquals(SourceVersion))
		{
			m_SettingItem.strResourceVersion = temp;
			temp = "";
		}
		else if(localName.contentEquals(ShowAboutInSettingModule))//
		{
			if(temp.contentEquals("true"))
				m_SettingItem.bShowAboutInSettingModule = true;
			else 
				m_SettingItem.bShowAboutInSettingModule = false;
			temp="";
		}		
		else if(localName.contentEquals(ModuleName)) //模块名称
		{
			m_ModuleInfo.strModuleName = temp;
			temp = "";
		}
		else if(localName.contentEquals(ModuleTips)) //模块Tips
		{ 
			m_ModuleInfo.strModuleTipsKey = temp;
		    temp="";
		}else if(localName.contentEquals(ModulePicPath)) //模块图片
		{
			m_ModuleInfo.strPicPath = temp;
			temp="";
		}
		else if(localName.contentEquals(ModuleSelectedPicPath))//模块选中图片
		{
			m_ModuleInfo.strSelectedPicPath =temp; 
			temp="";
		}
		else if(localName.contentEquals(ModuleOfflinePicPath)) // 未登录模块图片
		{
			m_ModuleInfo.strOfflinePicPath = temp;
			temp = "";
		}
		else if(localName.contentEquals(ModuleOfflineSelectedPicPath)) //为登陆模块选中图片
		{
			m_ModuleInfo.strOfflineSelectedPicPath = temp;
			temp = "";
		}
		else if(localName.contentEquals(Module)){
			m_SettingItem.m_ListModuleInfo.add(m_ModuleInfo);
			m_ModuleInfo=new ModuleInfo();
		}
		else if(localName.contentEquals(NewsCatagoryName))  //News分类名称
		{
			m_NewsCategory.strNewsCategoryNameKey = temp;
			temp = "";
		}
		else if(localName.contentEquals(NewsCatagoryId)) //News分类ID
		{
			m_NewsCategory.nNewsCategoryId = Integer.valueOf(temp).intValue();
			temp = "";
		}
		else if(localName.contentEquals(NewsCatagoryPicPath)) //New分类图片
		{
			m_NewsCategory.strNewsCategoryPicPath = temp;
			temp = "";
		}
		else if(localName.contentEquals(NewsCatagoryPressedPicPath))//New分类图片Pressed
		{
			m_NewsCategory.strNewsCategoryPressedPicPath = temp;
			temp = "";
		}
		else if(localName.contentEquals(NewsCatagory)) 
		{
			m_SettingItem.m_ListNewsCategory.add(m_NewsCategory);
			m_NewsCategory=new NewsCategory();
		}
		else if(localName.contentEquals(EventsTitle)) //Events分类名称
		{
			m_EventsCategory.strEventsCategoryNameKey = temp;
			temp = "";
		}
		else if(localName.contentEquals(EventsIds))   //Events分类ID
		{
			m_EventsCategory.nEventsCategoryId = Integer.valueOf(temp).intValue();
			temp = "";
		}
		else if(localName.contentEquals(EventsAccess))   //Events Access类型
		{
			m_EventsCategory.nEventsCategoryAccess = Integer.valueOf(temp).intValue();
			temp = "";
		}
		else if(localName.contentEquals(EventsLocal))   //Events Local类型
		{
			m_EventsCategory.nEventsCategoryLocal = Integer.valueOf(temp).intValue();
			temp = "";
		}
		else if(localName.contentEquals(EventsCatagoryPicPath))
		{
			m_EventsCategory.strEventsCatagoryPicPath = temp;
			temp = "";
		}
		else if(localName.equals(EventsCatagoryPressedPicPath))
		{
			m_EventsCategory.strEventsCatagoryPressedPicPath = temp;
			System.out.println("------------" + temp);
			temp = "";
		}
		else if(localName.contentEquals(EventsType))   //Events Type类型
		{
			m_EventsCategory.nEventsCategoryType = Integer.valueOf(temp).intValue();
			temp = "";
		}
		else if(localName.contentEquals(EventsCategory))
		{
			m_SettingItem.m_ListEventsCategories.add(m_EventsCategory);
			m_EventsCategory = new EventsCategory();
		}
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		m_SettingItem = new SettingItem();
		m_ModuleInfo = new ModuleInfo();
		m_NewsCategory = new NewsCategory();
		m_EventsCategory = new EventsCategory();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		this.localName = localName;
	}

}
