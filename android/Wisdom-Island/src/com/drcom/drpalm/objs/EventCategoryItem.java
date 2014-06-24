package com.drcom.drpalm.objs;

import java.util.List;

public class EventCategoryItem {
	public String name;
	public Integer catid;
	public EventCategoryItem(){}
	public static List<EventCategoryItem> catList = null;
//	public static final List<EventCategoryItem> getEventCategory(Context context){
//		SharedPreferences preferences = context.getSharedPreferences("EventCategory", Context.MODE_PRIVATE);
//		if(null != preferences){
//			if(null == catList){
//				catList = new ArrayList<EventCategoryItem>();
//			}
//			catList.clear();
//			Map<String, ?> map = preferences.getAll();
//			Iterator it = map.entrySet().iterator();
//			while(it.hasNext()){
//				EventCategoryItem item = new EventCategoryItem();
//				Map.Entry<String, ?> obj = (Map.Entry<String, ?>)it.next();
//				item.catid = ItemDataTranslate.String2Intger(obj.getKey());
//				item.name = (String)obj.getValue();
//				catList.add(item);
//			}						
//		}		
//		return catList;
//	}
}

