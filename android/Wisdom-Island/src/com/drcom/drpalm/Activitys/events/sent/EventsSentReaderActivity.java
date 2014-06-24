package com.drcom.drpalm.Activitys.events.sent;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.View.events.sent.EventsSentReaderActivityManagement;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm4tianzhujiao.R;

public class EventsSentReaderActivity extends ModuleActivity{
	public static String KEY_EVENT_ID = "KEY_EVENT_ID";
	public static String KEY_INIT = "KEY_INIT";
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	//变量
	private EventDetailsItem mEventDetailsItem = new EventDetailsItem();
	private int mEvent_id = -1;
//	private String mUsername = "";	
//	private SettingManager setInstance ;	
//	private EventsDB mEventsDB;
//	private Cursor mEventCursor = null;
//	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private EventsSentReaderActivityManagement mEventsSentReaderActivityManagement;
	
	//控件
	private TextView mTxtViewUnread;
	private TextView mTxtViewRead;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_reader_view, mLayout_body);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(KEY_EVENT_ID)){
			mEvent_id = extras.getInt(KEY_EVENT_ID);
		}
		
//		setInstance = SettingManager.getSettingManager(this);	
//		mEventsDB = EventsDB.getInstance(this,GlobalVariables.gSchoolKey);
//		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		mEventsSentReaderActivityManagement = new EventsSentReaderActivityManagement(this);
		
		mTxtViewUnread = (TextView)findViewById(R.id.textview_unreadname);
		mTxtViewRead = (TextView)findViewById(R.id.textview_readname);
		
		hideToolbar();
		sendGetEventReaderRequest(mEvent_id);
	}

	/**
	 * 请求网络(取列表)
	 * @param lastActivityId 
	 * @param uiHandler
	 */
	private void sendGetEventReaderRequest (int id){
		showProgressBar();
		mEventsSentReaderActivityManagement.sendGetEventReaderRequest(id, mHandler);
		
//		//是否取得此记录的所有信息
//		
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
//			@Override
//			public void onSuccess() {
//				GetDataInDB(id);
//				
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFINISH;
//				mHandler.sendMessage(message) ;
//			}
//
//			@Override
//			public void onCallbackError(String err) {
//				// TODO Auto-generated method stub
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED;
//				message.obj = err;
//				mHandler.sendMessage(message);
//			}
//			
//			@Override
//			public void onReloginError() {
//				// TODO Auto-generated method stub
//				super.onReloginError();
//				Log.i("zjj", "通告详细:自动重登录失败");
//			}
//			
//			@Override
//			public void onReloginSuccess() {
//				// TODO Auto-generated method stub
//				super.onReloginSuccess();
//				Log.i("zjj", "通告详细:自动重登录成功");
//				if(isRequestRelogin){
//					sendGetEventReaderRequest(mEvent_id);	//自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetEventReadInfo", new Object[]{String.valueOf(id),callback},callback.getClass().getName());
	}
	
	/**
	 * 从库读取
	 */
	private void GetDataInDB(int id){
//		mEventCursor = mEventsDB.getOnePublishEventCursor(id,mUsername);
//		mEventCursor.requery();
//		mEventCursor.moveToFirst();
//		mEventDetailsItem = mEventsDB.retrievePublishEventDetailItem(mEventCursor);
//		mEventCursor.close();
		
		mEventDetailsItem = mEventsSentReaderActivityManagement.GetDataInDB(id);
	}
	
	/**
	 * UIHandler
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == UPDATEFINISH){
				GetDataInDB(mEvent_id);
				ReflashUI();
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(EventsSentReaderActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
			}
			
			hideProgressBar();
		}
	};
	
	/**
	 * 刷新界面
	 */
	private void ReflashUI(){
		mTxtViewUnread.setText(mEventDetailsItem.unread_name_list);
		mTxtViewRead.setText(mEventDetailsItem.read_name_list);
	}
	
}
