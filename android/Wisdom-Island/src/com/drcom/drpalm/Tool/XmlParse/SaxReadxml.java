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

public class SaxReadxml {
	/**
	 * 
	 * @param context
	 * @return 解析assert 目录下的模块配制文件"block.xml",确定是否显示相关模块
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<Block> saxXml(Context context) {
		try {
			InputStream inputStream = context.getAssets().open("blocks.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance(); // 初始化sax解析器
			SAXParser sp = (SAXParser) spf.newSAXParser(); // 创建sax解析器
			// XMLReader xr = sp.getXMLReader();// 创建xml解析器
			BlockXMLContentHandler handler = new BlockXMLContentHandler();
			sp.parse(inputStream, handler);
			return handler.getBlocks();
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
		return new ArrayList<Block>();
	}
	
	/**
	 * 
	 * @param context
	 * @return 解析SD卡中(资源文件)目录下的模块配制文件"block.xml",确定是否显示相关模块
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<Block> saxXml(Context context,String path) {
		try {
//			InputStream inputStream = context.getAssets().open("blocks.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance(); // 初始化sax解析器
			SAXParser sp = (SAXParser) spf.newSAXParser(); // 创建sax解析器
			// XMLReader xr = sp.getXMLReader();// 创建xml解析器
			BlockXMLContentHandler handler = new BlockXMLContentHandler();
			sp.parse(path, handler);
			return handler.getBlocks();
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
		return new ArrayList<Block>();
	}
}
