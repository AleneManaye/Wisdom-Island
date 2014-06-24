package com.drcom.drpalm.objs;

import java.util.ArrayList;

public class MapCatItem {
	
	public MapCatItem() {
		subcategories = new ArrayList<MapCatItem>();
	}
	
	public ArrayList<MapCatItem> subcategories;
	public String categoryName;
	public String categoryId;
	
}
