package com.drcom.drpalm.View.events.search;

import android.content.Context;
import android.content.Intent;

import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.Activitys.events.album.ClassAlbumDetailActivity;
import com.drcom.drpalm.Activitys.events.video.ClassVideoDetailActivity;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.objs.EventDetailsItem;

public class EventsSearchListActivityManagement {
	private Context mContext = null;
//	private String mUserName = "";
	public EventsSearchListActivityManagement(Context context) {
		mContext = context;
//		mUserName = userName;
	}

	public void startDetailActivity(EventDetailsItem item) {
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

}
