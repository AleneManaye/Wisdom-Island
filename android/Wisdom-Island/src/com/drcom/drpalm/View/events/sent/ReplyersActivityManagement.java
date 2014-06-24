package com.drcom.drpalm.View.events.sent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.reply.EventsReplyActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;

public class ReplyersActivityManagement {
	private EventsDB mEventsDB;
	private Context mContext;
	
	public ReplyersActivityManagement(Context c){
		mContext = c;
		mEventsDB = EventsDB.getInstance(c,GlobalVariables.gSchoolKey);
	}
	
	public void onItemClick(EventDetailsItem item,int position){
		Intent i = new Intent(mContext, EventsReplyActivity.class);
		i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, item.eventid);
		i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,item.listReplyer.get(position).ReplyerId);
		i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,item.listReplyer.get(position).ReplyerName);
		if(item.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
			i.putExtra(EventsReplyActivity.REPLY_ABLE,false);
		}
//		Log.i("zjj", "点击查看回复内容:" + item.eventid + "," + item.listReplyer.get(position).ReplyerId);
		mContext.startActivity(i);
		
		mEventsDB.updataSendAsworgLastawstimeread(item.listReplyer.get(position).ReplyLastTime,
				item.eventid,
				item.user,
				item.listReplyer.get(position).ReplyerId);
		if(DateFormatter.getDateFromSecondsString(item.listReplyer.get(position).ReplyLastTime).getTime()>item.lastawstimeread.getTime()){
			mEventsDB.updataSendEventLastawstimeread(DateFormatter.getDateFromSecondsString(item.listReplyer.get(position).ReplyLastTime),
					item);
			
			Log.i("zjj", "点击查看回复内容 更新最后阅读的回复时间:" + item.listReplyer.get(position).ReplyerName + "," + item.listReplyer.get(position).ReplyLastTime);
			item.lastawstimeread = DateFormatter.getDateFromSecondsString(item.listReplyer.get(position).ReplyLastTime);
		}
		
		//更新查看时的最后回复时间
		item.listReplyer.get(position).lastawstimeread = item.listReplyer.get(position).ReplyLastTime;
//		mEventDetailsItem.listReplyer.get(position).isNewReply = false;
	}
}
