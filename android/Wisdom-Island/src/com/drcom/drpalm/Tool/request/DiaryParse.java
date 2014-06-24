package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.CommonTranslate.ItemDataTranslate;
import com.drcom.drpalm.objs.DiaryItem;
import com.drcom.drpalm.objs.EventDetailsItem;

public class DiaryParse extends BaseParse{
	
	private static final String DIARY_LIST = "diarylist";
	private static final String DIARY_ID = "diaryid";
	private static final String DIARY_TITLE = "title";
	private static final String DIARY_SUMMARY = "summary";
	private static final String DIARY_CONTENT = "contect";
	private static final String DIARY_STATUS = "status";
	private static final String DIARY_LASTUPDATE = "lastupdate";
	
	public DiaryParse(HashMap<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}
	
	public List<DiaryItem> parseDiaryList(){
		List<DiaryItem> list = new ArrayList<DiaryItem>();
		ArrayList<HashMap<String, Object>> objectList = null;
		if(mHashMap.containsKey(DIARY_LIST)){
			try {
				objectList = (ArrayList<HashMap<String, Object>>) mHashMap.get(DIARY_LIST);
				for (int i = 0; i < objectList.size(); i++) {
					HashMap<String, Object> objMap = objectList.get(i);
					DiaryItem diaryItem = new DiaryItem();
					if (objMap.containsKey(DIARY_ID)) {
						String value = (String) objMap.get(DIARY_ID);
						diaryItem.diaryId = ItemDataTranslate.String2Intger(value);
					}
					if (objMap.containsKey(DIARY_TITLE)) {
						diaryItem.diaryTitle = (String) objMap.get(DIARY_TITLE);
					}
					if (objMap.containsKey(DIARY_SUMMARY)) {
						diaryItem.diarySum = (String) objMap.get(DIARY_SUMMARY);
					}
					if (objMap.containsKey(DIARY_CONTENT)) {
						diaryItem.diaryContent = (String) objMap.get(DIARY_CONTENT);
					}
					if (objMap.containsKey(DIARY_STATUS)) {
						diaryItem.diaryStatus = (String) objMap.get(DIARY_STATUS);
					}
					if (objMap.containsKey(DIARY_LASTUPDATE)) {
						String value = (String) objMap.get(DIARY_LASTUPDATE);
						diaryItem.lastUpdate = DateFormatter.getDateFromSecondsString(value);
					}
					list.add(diaryItem);
				}
			} catch (Exception e) {

			}
		}
		return list;
	}
}
