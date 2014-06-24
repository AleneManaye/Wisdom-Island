package com.drcom.drpalm.View.events.draft;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.View.events.EventBaseInfoTools;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDraftItem;

public class EventsDraftListActivityManagement {
	
	private Context mContext;
	private EventsDB mEventsDB;
	private String mCurrentUserName;
	
	public EventsDraftListActivityManagement(Context context){
		this.mContext = context;
		this.mEventsDB = EventsDB.getInstance(mContext,GlobalVariables.gSchoolKey);
		EventBaseInfoTools baseInfo = new EventBaseInfoTools();
		mCurrentUserName = baseInfo.getCurrentUserName(mContext);
	}
	
	
	/**
	 * 跳转到通告详细界面
	 * @param cursor 当前通告数据库游标（获取event_id）
	 */
	public void jumpToEventdetailActivity(Cursor cursor){	
		EventDraftItem newsItem = mEventsDB.retrieveEventDraftItem(cursor,false);
		Intent i = new Intent(mContext, NewEventActivity.class);
		i.putExtra(NewEventActivity.KEY_DRAFTITEM_ID, newsItem.pk_id);
		mContext.startActivity(i);
	}
	
	public Cursor getAllEventDraftCuror(){
		return mEventsDB.getAllEventsDraftCursor(mCurrentUserName);
	}
		
}
