package com.drcom.drpalm.objs;

import android.R.string;

public class Block {

	private int id=-1;
	private boolean visible;
	private String type ="";
	private String icon="",nameChs="",nameCht="",nameEn="";
	
	/**获取按钮图标在drawable中的字段名**/
	public String getIconString(){
		return icon;
	}
	/**获取按钮名字在String中的字段名**/
//	public String getNameString(){
//		return name;
//	}
	/**设置按钮图标在drawable中的字段名**/
	public void setIconString(String id){
		icon = id;
	}
	/**设置按钮名字在String中的字段名**/
//	public void setNameString(String id){
//		name = id;
//	}
	
	/**设置按钮名字(简体)**/
	public void setNameChsString(String nameChs){
		this.nameChs=nameChs;
	}
	
	/**设置按钮名字(繁体)**/
	public void setNameChtString(String nameCht){
		this.nameCht=nameCht;
	}
	
	/**设置按钮名字(英语)**/
	public void setNameEnString(String nameEn){
		this.nameEn=nameEn;
	}
	
	/**获取按钮名字(简体)**/
	public String getNameChsString(){
		return nameChs;
	}
	
	/**获取按钮名字(繁体)**/
	public String getNameChtString(){
		return nameCht;
	}
	
	/**获取按钮名字(英文)**/
	public String getNameEnString(){
		return nameEn;
	}

	public Block() {
		super();
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
