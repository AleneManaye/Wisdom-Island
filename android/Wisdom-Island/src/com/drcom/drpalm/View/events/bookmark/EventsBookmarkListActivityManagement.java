package com.drcom.drpalm.View.events.bookmark;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.Activitys.events.album.ClassAlbumDetailActivity;
import com.drcom.drpalm.Activitys.events.video.ClassVideoDetailActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.View.events.EventsListActivityManagement;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;

public class EventsBookmarkListActivityManagement {
	private Context mContext = null;
//	private String mUserName = "";
	private String mUsername = "";
	private SettingManager setInstance;
	private EventsDB mEventsDB;
	private EventsListActivityManagement mEventsListActivityManagement;
	
	public EventsBookmarkListActivityManagement(Context context) {
		mContext = context;
		mEventsDB = EventsDB.getInstance(context, GlobalVariables.gSchoolKey);
		setInstance = SettingManager.getSettingManager(context);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		mEventsListActivityManagement = new EventsListActivityManagement(context, 0);
//		mUserName = userName;
	}

	public void startDetailActivity(Cursor c) {
		EventDetailsItem item = mEventsDB.retrieveEventDetailItem(c);
		if (item.type == RequestCategoryID.EVENTS_ALBUM_ID) {
			startAlbumDetailActivity(item.eventid);
		} if (item.type == RequestCategoryID.EVENTS_VIDEO_ID) {
			startVideoDetailActivity(item.eventid);
		} else {
			startNewsDetailActivity(item.eventid);
		}
	}
	/*
	 * 打开详细
	 */
	public void startNewsDetailActivity(int itemId) {
		Intent intent = new Intent(mContext, EventsDetailActivity.class);
		intent.putExtra(EventsDetailActivity.KEY_EVENT_ID, itemId);
		mContext.startActivity(intent);
	}

	/*
	 * 打开相册
	 */
	public void startAlbumDetailActivity(int itemId) {
		Intent intent = new Intent();
		intent.putExtra(ClassAlbumDetailActivity.KEY_EVENT_ID, itemId);
		intent.setClass(mContext, ClassAlbumDetailActivity.class);
		mContext.startActivity(intent);
	}
	
	/*
	 * 打开影音
	 */
	public void startVideoDetailActivity(int itemId) {
		Intent intent = new Intent();
		intent.putExtra(ClassVideoDetailActivity.KEY_EVENT_ID, itemId);
		intent.setClass(mContext, ClassVideoDetailActivity.class);
		mContext.startActivity(intent);
	}

//	public List<EventDetailsItem> getDataList() {
//		List<EventDetailsItem> list = new ArrayList<EventDetailsItem>();
//		EventsDB dataDB = EventsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
//		Cursor eventCursor = EventsDB.getBookmarksCursor();
//		eventCursor.requery();
//		list.clear();
//		if (eventCursor.getCount() > 0) {
//			for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
//				EventDetailsItem item = dataDB.retrieveEventDetailItem(eventCursor);
//				list.add(item);
//			}
//		}
//		eventCursor.close();
//		return list;
//	}
	
	/**
	 * 检测本地数据是否完整
	 */
	public void CheckData(Handler h){
		if(!mEventsDB.isNotallEvents(mUsername)){
			Log.i("zjj", "需要下载所有通告 再看收藏");
			mEventsListActivityManagement.GetData("0","0",h);
			return;
		}else{
			Message msg = new Message();
			msg.arg1 = EventsListActivityManagement.UPDATEFINISH;
			h.sendMessage(msg);
		}
	}
	
	/**
	 * 取收藏列表
	 */
	public Cursor getBookmarkEventCursor(){
		return mEventsDB.getBookmarkEventCursor(mUsername);
	}
	

}
