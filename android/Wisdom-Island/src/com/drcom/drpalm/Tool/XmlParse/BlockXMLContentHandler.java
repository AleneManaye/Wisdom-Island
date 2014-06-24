package com.drcom.drpalm.Tool.XmlParse;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.drcom.drpalm.objs.Block;

//import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 模块XML配置文件解释工具
 * @author zhaojunjie
 *
 */
public class BlockXMLContentHandler extends DefaultHandler {

	private List<Block> blocks;

	private Block block;
	private String tempStr;

	private static final String BLOCKSsTRING = "blocks";
	private static final String BLOCKSTRING = "block";
	private static final String IDSTRING = "id";
	private static final String TYPESTRING = "type";
	private static final String SHOWSTRING = "show";
	private static final String SCHOOLSTRING = " o9school";
	private static final String CLASSSTRING = "class";
	private static final String TRUESTRING = "true";
	private static final String FALSESTRING = "false";
	private static final String ICONSTRING = "icon";
	private static final String NAMECHSSTRING = "namechs"; 
	private static final String NAMECHTSTRING = "namecht"; 
	private static final String NAMEENSTRING = "nameen"; 

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	/**
	 * 要开始读取xml文件的时候调用的方法 初始化persons
	 */
	@Override
	public void startDocument() throws SAXException {
		// 这里做list的初始化工作
		blocks = new ArrayList<Block>();
	}

	/**
	 * sax 读取到元素节点的时候用到这个方法；
	 */
	//@SuppressLint("UseValueOf")
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		// 先判断读到的元素是否是person
		if (BLOCKSTRING.equals(localName)) {
			// 如果读到的是person这个元素 就要保存到我们的实体类当中
			block = new Block();
			// attributes是属性。
			
			block.setId(new Integer(attributes.getValue(IDSTRING)));
			block.setIconString(attributes.getValue(ICONSTRING));
			block.setNameChsString(attributes.getValue(NAMECHSSTRING));
			block.setNameChtString(attributes.getValue(NAMECHTSTRING));
			block.setNameEnString(attributes.getValue(NAMEENSTRING));
			if (SCHOOLSTRING.equals(attributes.getValue(TYPESTRING))) {
				block.setType(SCHOOLSTRING);
			} else if (CLASSSTRING.equals(attributes.getValue(TYPESTRING))) {
				block.setType(CLASSSTRING);
			}else{
				block.setType(attributes.getValue(TYPESTRING));
			}
		}
		tempStr = localName;
	}

	/**
	 * sax 读取到文本节点的时候调用了这个方法
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (block != null) {
			String valueString = new String(ch, start, length);
			if (SHOWSTRING.equals(tempStr)) {
				// 如果当前解析到的节点是name 就要将name中的文本节点元素的值得到
				if (TRUESTRING.equals(valueString.trim()))
					block.setVisible(true);
				else if (FALSESTRING.equals(valueString.trim()))
					block.setVisible(false);
			}
		}

	}

	/**
	 * 这个方法是每次遇到结束的标签都会执行的 并不是只遇到最后的结尾才调用 读取完毕遇到person的结尾
	 * 就将封装好的一个personbean保存到list中 并且清空person对象
	 * 
	 */

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (BLOCKSTRING.equals(localName) && block != null) {
			blocks.add(block);
			block = null;
		}
		tempStr = null;

	}

}
