package com.drcom.drpalm.objs;

public class NavigationItem {
	public String name = "";
	public String key = "";
	public int status = 1;//0 表示无效  1表示有效
	public int point_id = -1; //-1 无效节点
	public int parent_id = -1;//同上
	public String type = "";
	public String titleurl = "";//标题图片
	public boolean bookmark = false;//0表示未收藏 1表示收藏
	public String text_s = "";	//简体中文
	public String text_t = "";	//繁体中文
	public String text_en = "";	//英文
}
