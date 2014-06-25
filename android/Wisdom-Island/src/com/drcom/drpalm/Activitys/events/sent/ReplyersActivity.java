package com.drcom.drpalm.Activitys.events.sent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.reply.EventsReplyActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.events.sent.ReplyersActivityManagement;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.wisdom.island.R;

public class ReplyersActivity extends ModuleActivity {
	public static String KEY_EVENT = "KEY_EVENT";
	
	//变量
	private EventDetailsItem mEventDetailsItem;
	private ReplyersAdapter mAdapter;
//	private EventsDB mEventsDB;
	private ReplyersActivityManagement mReplyersActivityManagement;
	
	//控件
	private ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.replyers_list_view, mLayout_body);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(KEY_EVENT)){
			mEventDetailsItem = (EventDetailsItem) extras.getSerializable(KEY_EVENT);
		}
		
		mReplyersActivityManagement = new ReplyersActivityManagement(this);
		
//		mEventsDB = EventsDB.getInstance(this,GlobalVariables.gSchoolKey);
		
		mListView = (ListView)findViewById(R.id.replyersListview);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mReplyersActivityManagement.onItemClick(mEventDetailsItem, position);
				
//				Intent i = new Intent(ReplyersActivity.this, EventsReplyActivity.class);
//				i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, mEventDetailsItem.eventid);
//				i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,mEventDetailsItem.listReplyer.get(position).ReplyerId);
//				i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,mEventDetailsItem.listReplyer.get(position).ReplyerName);
//				if(mEventDetailsItem.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
//					i.putExtra(EventsReplyActivity.REPLY_ABLE,false);
//				}
//				Log.i("zjj", "点击查看回复内容:" + mEventDetailsItem.eventid + "," + mEventDetailsItem.listReplyer.get(position).ReplyerId);
//				ReplyersActivity.this.startActivity(i);
//				
//				mEventsDB.updataSendAsworgLastawstimeread(mEventDetailsItem.listReplyer.get(position).ReplyLastTime,
//						mEventDetailsItem.eventid,
//						mEventDetailsItem.user,
//						mEventDetailsItem.listReplyer.get(position).ReplyerId);
//				if(DateFormatter.getDateFromSecondsString(mEventDetailsItem.listReplyer.get(position).ReplyLastTime).getTime()>mEventDetailsItem.lastawstimeread.getTime()){
//					mEventsDB.updataSendEventLastawstimeread(DateFormatter.getDateFromSecondsString(mEventDetailsItem.listReplyer.get(position).ReplyLastTime),
//							mEventDetailsItem);
//					
//					mEventDetailsItem.lastawstimeread = DateFormatter.getDateFromSecondsString(mEventDetailsItem.listReplyer.get(position).ReplyLastTime);
//				}
//				
//				//更新查看时的最后回复时间
//				mEventDetailsItem.listReplyer.get(position).lastawstimeread = mEventDetailsItem.listReplyer.get(position).ReplyLastTime;
////				mEventDetailsItem.listReplyer.get(position).isNewReply = false;
			}
		});
		
		mAdapter = new ReplyersAdapter(this, mEventDetailsItem.listReplyer);
		mListView.setAdapter(mAdapter);
		
		hideToolbar();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}
}
