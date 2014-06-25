package com.drcom.drpalm.Activitys.events.sent;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.View.controls.FootView;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.events.sent.EventsSentListActivityManagement;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.MessageObject;
import com.wisdom.island.R;

public class EventsSentListActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2;	//更多请求返回成功
	private int PAGE_SIZE = 10;	//分页显示条数
	private String KEY_REFLASHTIME = "eventssentflashtime";
	
	//变量
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private EventsSentListCursorAdapter mAdapter;
	private int mCurNewCount = 0;	//当前纪录数
	private int mPageNum = 1;		//页数
	private int mNextPage = 2;		//下一页
//	private int mLastActivityId = 0;	//最后一个活动的ID
	private String mUsername = "";	
	private SettingManager setInstance ;	
//	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private Date mDate = new Date(0);			//有效期
	private int mOrderby = 0;	//排列方式
//	private SharedPreferences sp;	//15分钟间隔自动刷新
//	private Editor editor;
//	private Date lastrefreshTime;
	private EventsSentListActivityManagement mEventsSentListActivityManagement;
	
	//控件
	private RefreshListView mEventsListView;
	private FootView mFooterView = null;
//	private TextView mDateTextView;
//	private Button mBtnChooseDate;
//	private Button mBtnNewReply;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_list_notitle_pull_view, mLayout_body);
		
		//
		setInstance = SettingManager.getSettingManager(this);	
		mEventsDB = EventsDB.getInstance(this,GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
//		sp = this.getSharedPreferences(KEY_REFLASHTIME, MODE_WORLD_READABLE);
//		editor = sp.edit();
//		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
		mEventsSentListActivityManagement = new EventsSentListActivityManagement(this);
		
		//
		hideToolbar();
		//时间
//		mDateTextView = (TextView)findViewById(R.id.eventhomeDateTextView);
//		mDateTextView.setText(getResources().getString(R.string.all));//(DateFormatter.getStringYYYYMMDD(mDate));
//		//选日期
//		mBtnChooseDate = (Button)findViewById(R.id.buttonChooseDate);
//		mBtnChooseDate.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mOrderby = 0;
//				showDateMessage();
//			}
//		});
//		//按最新回复
//		mBtnNewReply = (Button)findViewById(R.id.buttonNewReply);
//		mBtnNewReply.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mOrderby = 2;
//				initalizeCursorAdapter(mDate.getTime(),mOrderby);
//			}
//		});
		
		mEventsListView = (RefreshListView)findViewById(R.id.eventhomeListview);
		mEventsListView.setCacheColorHint(Color.WHITE) ;
		mEventsListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//            	if(mCurNewCount == 0){
            		ReflashData();
//            	}else{
//            		Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(1);
//    				EventDetailsItem newsItem = mEventsDB.retrieveEventDetailItem(newsCursor);
//    				newsCursor.close();
    				
//    				sendGetEventsRequest(newsItem.lastupdate.getTime()/1000 + "");
//    				sendGetEventsRequest("0");
//            	}
            	
            }
        });
		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(view == mFooterView) {
					mNextPage = mPageNum + 1;
					Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(mEventsListView.getCount()-2);
					EventDetailsItem newsItem = mEventsDB.retrievePublishEventDetailItem(newsCursor);
					sendGetEventsRequest((newsItem.lastupdate.getTime()/1000) + "");
//					initalizeCursorAdapter();
					return;
				}
				
				Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(position);
				mEventsSentListActivityManagement.onClickEvensSentItem(newsCursor);
//				EventDetailsItem newsItem = mEventsDB.retrieveEventDetailItem(newsCursor);
////				newsCursor.close();
//				
//				if(newsItem.type == RequestCategoryID.EVENTS_ALBUM_ID){
//					Intent i = new Intent(EventsSentListActivity.this, ClassAlbumDetailActivity.class);
//					i.putExtra(ClassAlbumDetailActivity.KEY_EVENT_ID, newsItem.eventid);
//					i.putExtra(ClassAlbumDetailActivity.KEY_ISSENTEVENT, true);
//					startActivity(i);
//				}else{
//					Intent i = new Intent(EventsSentListActivity.this, EventsSentDetailActivity.class);
//					i.putExtra(EventsDetailActivity.KEY_EVENT_ID, newsItem.eventid);
//					startActivity(i);
//				}
//				
//				//未读时,广播通知主界面减少未读数
//				if(!newsItem.isread){
//					Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
//					intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID,RequestCategoryID.EVENTS_SEND_ID);
//					sendBroadcast(intent1);
//				}
			}
		});
		
//		initalizeCursorAdapter(mDate.getTime(),mOrderby);
		
		mEventsListView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// 大于15分钟自动刷新
				if (mEventsSentListActivityManagement.isOver15mins()) {
					mEventsListView.hideHeadView();
					ReflashData();
				}
			}
		},300);
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getString(R.string.sent));
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initalizeCursorAdapter(mDate.getTime(),mOrderby);
	}
	
	private void initalizeCursorAdapter(long date,int orderBy){
//		initalizeFootView();
		mEventCursor = mEventsDB.getEventSendCursor(date,date,mUsername,orderBy,(mPageNum * PAGE_SIZE )+"");// .getEventCursor(RequestCategoryID.EVENTS_NEWS_ID,0,0,mUsername,0);
		mEventCursor.requery();
		mEventCursor.moveToFirst();
		
		mCurNewCount = mEventCursor.getCount();
		
		//显示到列表中
		if(mAdapter == null){
			mAdapter = new EventsSentListCursorAdapter(EventsSentListActivity.this, mEventCursor);
			mAdapter.setUsername(mUsername);
			mEventsListView.setAdapter(mAdapter);
		}else{
			mAdapter.changeCursor(mEventCursor);
		}

		
		//列表头
		if (mEventCursor == null) {
			mEventsListView.hideHeadView();
		} else {
			if (mEventCursor.getCount() > 0) {
				mEventsListView.hideHeadView();
			} else {
				mEventsListView.setHeadViewVisible();
			}
		}
		
//		//是否还有"更多"
//		if(mPageNum == 1 && mEventsDB.getEventSendSum(mUsername) == PAGE_SIZE){
//			//添加"更多"
//			initalizeFootView();
//		}else{
//			if(mCurNewCount == mEventsDB.getEventSendSum(mUsername)){
//				mEventsListView.removeFooterView(mFooterView);
//			}
//		}
		//是否还有"更多"
		initalizeFootView();
		if(mCurNewCount<(mPageNum * PAGE_SIZE)){
			mEventsListView.removeFooterView(mFooterView);
		}
	}
	
	/*
	 * 初始化列表FootView更多
	 */
	private void initalizeFootView(){
		if(null == mFooterView){
			Context context = EventsSentListActivity.this;
			mFooterView = new FootView(context);

		}
		mFooterView.setTitle(GlobalVariables.gAppResource.getString(R.string.loadmorenews));
		if(0 == mEventsListView.getFooterViewsCount()){
			if(mEventsListView.getCount()>1){
				mEventsListView.addFooterView(mFooterView);
			}
		}
	}
	
	/**
	 * 刷新数据
	 */
	private void ReflashData(){
		mCurNewCount = 0;
		mPageNum = 1;
		sendGetEventsRequest("0");
	}
	
	/**
	 * 请求网络(取列表)
	 * @param lastActivityId   0:刷新         非0：更多
	 * @param uiHandler
	 */
	private void sendGetEventsRequest(final String lastupdate){
		showProgressBar();
		mEventsSentListActivityManagement.sendGetEventsRequest(lastupdate, mHandler);
		
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
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
//			public void onCallbackError(String str) {
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
//		};
//		mRequestOperation.sendGetNeededInfo("GetPublishEventList", new Object[]{0, lastupdate,callback},callback.getClass().getName());
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == UPDATEFINISH || msg.arg1 == MOREFINISH){
				MessageObject obj = (MessageObject)msg.obj;
				if(obj.isSuccess){
					//更多后,页数+1
					if(msg.arg1 == MOREFINISH)
						mPageNum = mNextPage;
					
					initalizeCursorAdapter(mDate.getTime(),mOrderby);
					mEventsSentListActivityManagement.SaveReflahstime();
				}
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(EventsSentListActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
				mEventsListView.clearPullToRefresh(true);
			}
			
			hideProgressBar();
		}
	};
	
	@Override
	protected void onDestroy() {
		mEventsSentListActivityManagement.sendReflashUnreadsumBroadcast();
		
		if(mEventCursor != null){
			mEventCursor.close();
			mEventCursor = null;
		}
		super.onDestroy();
	}
	
//	/**
//	 * 保存刷新时间
//	 */
//	private void SaveReflahstime(){
//		editor.putLong(KEY_REFLASHTIME , new Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
//		editor.commit();
//	}
	
	/**
	 * 选择日期对话框
	 * @param pTitle
	 */
//	private void showDateMessage() {
//		final AlertDialog dlg = new AlertDialog.Builder(
//				EventsSentListActivity.this).create();
//		dlg.show();
//		Window window = dlg.getWindow();
//		window.setContentView(R.layout.prizechoosedatequery_view);
//		final MyDatePicker mDatePicker = (MyDatePicker)window.findViewById(R.id.datetime_picker);
//		Button ok = (Button) window.findViewById(R.id.datetime_ok);
//		ok.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Time t = mDatePicker.getTime();
//				
//				Message msg = new Message();
//				msg.obj = t;
//				mChooseTimeHandler.sendMessage(msg);
//				
//				dlg.cancel();
//			}
//		});
//
//		// 关闭alert对话框
//		Button cancel = (Button) window.findViewById(R.id.datetime_cancel);
//		cancel.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				dlg.cancel();
//			}
//		});
//	}
	
//	private Handler mChooseTimeHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			Time t = (Time) msg.obj;
//
//			mDate = new Date(t.year - 1900, (t.month),t.monthDay, t.hour, t.minute); // 年份从1900开始
//			mDateTextView.setText(getResources().getString(
//					R.string.valid_time)
//					+ t.year
//					+ "-"
//					+ (t.month + 1)
//					+ "-"
//					+ t.monthDay);
//			
//			initalizeCursorAdapter(mDate.getTime(),mOrderby);
//		}
//	};
	
}
