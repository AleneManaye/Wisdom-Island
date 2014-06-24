package com.drcom.drpalm.Tool.XmlParse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.content.Context;

import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.ShareToFriendItem;

public class ShareItemReadxml {
	/**
	 * 
	 * @param context
	 * @return 解析assert 目录下的模块配制文件"share_to_friend_items.xml",确定是否显示相关模块
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<ShareToFriendItem> saxXml(Context context) {
		try {
			InputStream inputStream = context.getAssets().open("share_to_friend_items.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance(); // 初始化sax解析器
			SAXParser sp = (SAXParser) spf.newSAXParser(); // 创建sax解析器
			// XMLReader xr = sp.getXMLReader();// 创建xml解析器
			ShareItemXMLContentHandler handler = new ShareItemXMLContentHandler();
			handler.setMContext(context);
			sp.parse(inputStream, handler);
			return handler.getShareList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<ShareToFriendItem>();
	}
}
