/*
 UserInfoHandler.java
 Create by JiangBo   2012-04-10
 XML解析
 */
package com.drcom.drpalm.Tool.XmlParse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.drcom.drpalm.Definition.SettingStringDefine;
import com.drcom.drpalm.Tool.SimpleEncrypt;
import com.drcom.drpalm.View.setting.SettingManager;


//import android.R.integer;

public class UserInfoHandler extends DefaultHandler{

	private UserInfo  m_UserInfo;
	private List<UserInfo>   m_ListUserInfo = null;
	
	private String temp="";
	private String localName="";
	
	public List<UserInfo> getParsedData() { 
        return m_ListUserInfo; 
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
		//super.endElement(uri, localName, qName);
		// TODO Auto-generated method stub
		if(localName.endsWith(SettingStringDefine.USERNAME)){
			m_UserInfo.strUsrName = temp;
		    temp="";
		}
		else if(localName.endsWith(SettingStringDefine.USERNICKNAME))
		{
			m_UserInfo.strUserNickName = temp;
			temp = "";
		}
		else if(localName.endsWith(SettingStringDefine.PASSWORD)){
			String strDecryPwd = "";
			try
			{
				strDecryPwd = SimpleEncrypt.decrypt(SettingManager.strKey, temp);
			}
			catch(Exception e){
			}
			m_UserInfo.strPassword = strDecryPwd;
			temp="";
		}
		else if(localName.endsWith(SettingStringDefine.USERTYPE)){
			m_UserInfo.strUsrType = temp; 
			temp="";
		}
		else if(localName.endsWith(SettingStringDefine.REMEMBERPWD)){
		 	boolean b = temp.contentEquals("true");
		 	m_UserInfo.bRememberPwd = b;
		 	temp="";
		}
		else if(localName.endsWith(SettingStringDefine.AUTOLOGIN)){
			boolean b = temp.contentEquals("true");
		 	m_UserInfo.bAutoLogin = b;
		 	temp="";
		}
		else if(localName.endsWith(SettingStringDefine.PARENTLOCK)){
			boolean b = temp.contentEquals("true");
		 	m_UserInfo.bParentLock = b;
		 	temp="";
		}
		else if(localName.endsWith(SettingStringDefine.PARENTLOCKPWD)){
			String strDecryParentLockPwd = "";
			try
			{
				strDecryParentLockPwd = SimpleEncrypt.decrypt(SettingManager.strKey, temp);
			}
			catch(Exception e){
			}
			m_UserInfo.strParentLockPwd = strDecryParentLockPwd;
			temp="";
		}
		
		else if(localName.endsWith("item")){
			m_ListUserInfo.add(m_UserInfo);
			m_UserInfo=new UserInfo();
		}
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		m_UserInfo = new UserInfo();
		m_ListUserInfo = new ArrayList<UserInfo>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		this.localName = localName;
	}
	

}
