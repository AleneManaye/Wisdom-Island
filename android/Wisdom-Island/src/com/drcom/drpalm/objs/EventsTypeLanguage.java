package com.drcom.drpalm.objs;
/**
 * 
 * @author hzy
 *
 */
public class EventsTypeLanguage {

	public String type_index = "";
	public String type_event = "";
	public String type_chinese= "";
	public String type_chinese_character = "";
	public String type_english= "";


	public EventsTypeLanguage(String type_index, String type_event,
			String type_chinese, String type_chinese_character,
			String type_english) {
		super();
		this.type_index = type_index;
		this.type_event = type_event;
		this.type_chinese = type_chinese;
		this.type_chinese_character = type_chinese_character;
		this.type_english = type_english;
	}

	
	public EventsTypeLanguage() {
		super();
	}
	

	public String getType_index() {
		return type_index;
	}

	public void setType_index(String type_index) {
		this.type_index = type_index;
	}

	public String getType_event() {
		return type_event;
	}

	public void setType_event(String type_event) {
		this.type_event = type_event;
	}

	public String getType_chinese() {
		return type_chinese;
	}

	public void setType_chinese(String type_chinese) {
		this.type_chinese = type_chinese;
	}

	public String getType_chinese_character() {
		return type_chinese_character;
	}

	public void setType_chinese_character(String type_chinese_character) {
		this.type_chinese_character = type_chinese_character;
	}

	public String getType_english() {
		return type_english;
	}

	public void setType_english(String type_english) {
		this.type_english = type_english;
	}
	
	
	
}
