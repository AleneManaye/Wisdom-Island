package com.drcom.drpalm.Tool.XmlParse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.drcom.drpalm.View.share.ShareIntentHelper;
import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.ShareToFriendItem;

//import android.annotation.SuppressLint;
import android.R;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class ShareItemXMLContentHandler extends DefaultHandler {

	private List<ShareToFriendItem> shareList;

	private ShareToFriendItem shareItem;
	private String tempStr;
	private Context mContext;

	private static final String SHARELISTSSTRING = "clients";
	private static final String SHARELISTSTRING = "client";
	private static final String IDSTRING = "id";
	private static final String TYPESTRING = "type";
	private static final String SHOWSTRING = "show";
	private static final String ICONSTRING = "icon";
	private static final String NAMESTRING = "name";
	private static final String TRUESTRING = "true";
	private static final String FALSESTRING = "false";

	public List<ShareToFriendItem> getShareList() {
		return shareList;
	}

	public void setshareList(List<ShareToFriendItem> shareList) {
		this.shareList = shareList;
	}
	
	public void setMContext(Context mContext){
		this.mContext = mContext;
	}

	/**
	 * 要开始读取xml文件的时候调用的方法 初始化shareList
	 */
	@Override
	public void startDocument() throws SAXException {
		// 这里做list的初始化工作
		shareList = new ArrayList<ShareToFriendItem>();
	}

	/**
	 *  读取到元素节点的时候用到这个方法；
	 */
	//@SuppressLint("UseValueOf")
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		// 先判断读到的元素是否是client
		if (SHARELISTSTRING.equals(localName)) {
			// 如果读到的是client这个元素 就要保存到我们的实体类当中
			shareItem = new ShareToFriendItem();
			// attributes是属性。
			shareItem.id=new Integer(attributes.getValue(IDSTRING));
			ApplicationInfo appInfo = mContext.getApplicationInfo();
			shareItem.btnIcon = mContext.getResources().getIdentifier(attributes.getValue(ICONSTRING), "drawable", appInfo.packageName);
			shareItem.btnName = mContext.getResources().getIdentifier(attributes.getValue(NAMESTRING), "string", appInfo.packageName);
			shareItem.type = attributes.getValue(TYPESTRING);
		}
		tempStr = localName;
	}

	/**
	 *  读取到文本节点的时候调用了这个方法
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (shareItem != null) {
			String valueString = new String(ch, start, length);
			if (SHOWSTRING.equals(tempStr)) {
				// 如果当前解析到的节点是visible 就要将visible中的文本节点元素的值得到
				if (TRUESTRING.equals(valueString.trim()))
					shareItem.visible = true;
				else if (FALSESTRING.equals(valueString.trim()))
					shareItem.visible = false;
			}
		}

	}

	/**
	 * 这个方法是每次遇到结束的标签都会执行的 并不是只遇到最后的结尾才调用 读取完毕遇到xml的结尾
	 * 就将封装好的一个shareItem保存到list中 并且清空shareItem对象
	 * 
	 */

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (SHARELISTSTRING.equals(localName) && shareItem != null) {
			shareList.add(shareItem);
			shareItem = null;
		}
		tempStr = null;

	}

}
