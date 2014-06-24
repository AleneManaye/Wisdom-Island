package com.drcom.drpalm.Activitys.events.draft;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.View.events.EventBaseInfoTools;
import com.drcom.drpalm.View.events.draft.EventsDraftListActivityManagement;
import com.drcom.drpalm4tianzhujiao.R;

public class EventsDraftListActivity extends ModuleActivity {
	//变量
	private Cursor mEventCursor = null;
	private EventsDraftListCursorAdapter mAdapter;		
	//控件
	private ListView mEventsListView;
	private EventsDraftListActivityManagement mEventsDraftListActivityManagement;
	private String mUsername = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_list_notitle_view, mLayout_body);
		
		mEventsDraftListActivityManagement = new EventsDraftListActivityManagement(this);
		EventBaseInfoTools baseInfo = new EventBaseInfoTools();
		mUsername = baseInfo.getCurrentUserName(this);
			
		//
		hideToolbar();
		mEventsListView = (ListView)findViewById(R.id.eventhomeListview);
		mEventsListView.setCacheColorHint(Color.WHITE) ;

		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(position);
				mEventsDraftListActivityManagement.jumpToEventdetailActivity(newsCursor);
			}
		});
		
//		mEventsListView.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				initalizeCursorAdapter();
//			}
//		},300);
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getString(R.string.draft));
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initalizeCursorAdapter();
	}
	
	private void initalizeCursorAdapter(){
		
		mEventCursor = mEventsDraftListActivityManagement.getAllEventDraftCuror();
		mEventCursor.requery();
		mEventCursor.moveToFirst();
		
		//显示到列表中
		if(mAdapter == null){
			mAdapter = new EventsDraftListCursorAdapter(EventsDraftListActivity.this, mEventCursor, mUsername);
			mEventsListView.setAdapter(mAdapter);
		}else{
			mAdapter.changeCursor(mEventCursor);
		}
		
		hideProgressBar();
	}
	
	/*
	 * 初始化列表FootView更多
	 */
//	private void initalizeFootView(){
//		if(null == mFooterView){
//			Context context = EventsListActivity.this;
//			mFooterView = new FootView(context);
//
//		}
//		mFooterView.setTitle(GlobalVariables.gAppResource.getString(R.string.loadmorenews));
//		if(0 == mEventsListView.getFooterViewsCount()){
//			mEventsListView.addFooterView(mFooterView);
//		}
//	}
	
//	/**
//	 * 请求网络(取列表)
//	 * @param lastActivityId   0:刷新         非0：更多
//	 * @param uiHandler
//	 */
//	private void sendGetEventsRequest(final String lastupdate){
//		mRequestOperation.GetPublishEventList(0, lastupdate, new RequestOperationReloginCallback(){
//			@Override
//			public void onSuccess() {
//				Message message = Message.obtain();
//				if(lastupdate.equals("0")){
//					message.arg1 = UPDATEFINISH;	//刷新
//				}else{
//					message.arg1 = MOREFINISH;		//更多
//				}
//				message.obj = new MessageObject(true,false);
//				mHandler.sendMessage(message) ;
//			}
//			
//			@Override
//			public void onError(String str) {
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED;
//				message.obj = str;
//				mHandler.sendMessage(message);
//			}
//			
//			@Override
//			public void onReloginError() {
//				// TODO Auto-generated method stub
//				super.onReloginError();
//				Log.i("zjj", "通告列表:自动重登录失败");
//			}
//			
//			@Override
//			public void onReloginSuccess() {
//				// TODO Auto-generated method stub
//				super.onReloginSuccess();
//				Log.i("zjj", "通告列表:自动重登录成功");
//				if(isRequestRelogin){
//					sendGetEventsRequest(lastupdate);	//自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		});
//	}
//	
//	private Handler mHandler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			if(msg.arg1 == UPDATEFINISH){
//				MessageObject obj = (MessageObject)msg.obj;
//				if(obj.isSuccess){
//					initalizeCursorAdapter();
//				}
//			}
//		}
//	};
	
//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		initalizeCursorAdapter();
//	}
	
	@Override
	protected void onDestroy() {
		mEventCursor.close();
		mEventCursor = null;
		super.onDestroy();
	}
	
}
