package com.drcom.drpalm.Tool.jsonparser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.EventsTypeLanguage;
import com.drcom.drpalm.objs.EventsTypeLanguageResultItem;
/**
 * 
 * @author hzy
 *
 */
//{“opret”:{“opflag”:”1”,”code”:””},"titlelist":[{"eventtype":"1001","itemlist":[{"id":"1","zh-hans":"测试测验","zh-hant":"測試測驗","en":"test"},{"id":"2","zh-hans":"测试测验2","zh-hant":"測試測驗2","en":"test2"}]},{"eventtype":"2001","itemlist":[{"id":"2","zh-hans":"迟到/早退","zh-hant":"遲到/早退","en":"leave"}]}]}
public class ClassTitleParser implements IParser {

	@Override
	public Object parser(JSONObject json) {
		// TODO Auto-generated method stub
//		List<EventsTypeLanguage> list = new ArrayList<EventsTypeLanguage>();
//		EventsTypeLanguage type;
		
		if (json != null) {
			EventsTypeLanguageResultItem  resultItem = new EventsTypeLanguageResultItem();
			
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {

					JSONArray opretlist = json.getJSONArray("titlelist");

					for (int i = 0; i < opretlist.length(); i++) {

						JSONObject obj = opretlist.getJSONObject(i);
						resultItem.result = true;
						// 多语言数组对象
						JSONArray languagelist = obj.getJSONArray("itemlist");
						for (int k = 0; k < languagelist.length(); k++) {
							EventsTypeLanguage type = new EventsTypeLanguage();
							
							type.type_event = obj.getString("eventtype");
							
							JSONObject obj2 = languagelist.getJSONObject(k);
							type.type_index = obj2.getString("id");
							type.type_chinese = obj2.getString("zh-hans");
							type.type_chinese_character = obj2.getString("zh-hant");
							type.type_english = obj2.getString("en");
							resultItem.mEventsTypeLanguage.add(type);
						}
						
					}

				}else {
					resultItem.result = false;
					resultItem.errorcode = opret.getString("code");
					resultItem.errordes =  BaseParse
								.getErrorString(resultItem.errorcode);
						
				}
				return resultItem;
			} catch (Exception e) {

				Log.i("hzy", "ResultParser Exception:" + e.toString());
				resultItem.result = false;
				resultItem.errorcode = RequestDefine.JsonParserError;
				resultItem.errordes =  BaseParse
						.getErrorString(RequestDefine.JsonParserError);
				
				return resultItem; 
			}
			
		}
		return null;
	}

}
