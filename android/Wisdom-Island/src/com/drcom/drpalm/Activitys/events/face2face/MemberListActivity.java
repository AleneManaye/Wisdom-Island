package com.drcom.drpalm.Activitys.events.face2face;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.events.face2face.MemberListActivityManagement;
import com.drcom.drpalm.objs.MessageObject;
import com.wisdom.island.R;

public class MemberListActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int UPRELOGINSUCCESS = 2;//重新登录成功
	
	//变量
	private Cursor mCommunicationDBtCursor = null;
	private MemberListCursorAdapter mAdapter;	
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
//	private Date lastrefreshTime;
	
	//控件
	private RefreshListView mEventsListView;
	private MemberListActivityManagement mMemberListActivityManagement;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_list_notitle_pull_view, mLayout_body);
		
		mMemberListActivityManagement = new MemberListActivityManagement(this);
//		lastrefreshTime = mMemberListActivityManagement.getLastRefreshTime();
		
		//
		hideToolbar();
		mEventsListView = (RefreshListView)findViewById(R.id.eventhomeListview);
		mEventsListView.setCacheColorHint(Color.WHITE) ;
		mEventsListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	sendGetEventsRequest();
            }
        });
		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(position);
				mMemberListActivityManagement.jumpToCommuniReplyActivity(newsCursor);
			}
		});
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getString(R.string.face2face));
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(mAdapter == null){
			//显示正在加载的title，CursorAdapter Cursor 可以为空
			mAdapter = new MemberListCursorAdapter(MemberListActivity.this, mCommunicationDBtCursor, getmClassImageLoader());
			mEventsListView.setAdapter(mAdapter);
			mEventsListView.setOnloadingRefreshVisible();
			
			mEventsListView.postDelayed(new Runnable() {
				@Override
				public void run() {
//					initalizeCursorAdapter();
//					
//					// 大于15分钟自动刷新
//					if ((new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 15) {
//						mEventsListView.hideHeadView();
						sendGetEventsRequest();
//					}
				}
			},300);
		}else{
			initalizeCursorAdapter();
		}
	}
	
	private void initalizeCursorAdapter(){
//		initalizeFootView();
		
		mCommunicationDBtCursor = mMemberListActivityManagement.getContactCursor();
		if(mCommunicationDBtCursor != null){
			mCommunicationDBtCursor.requery();
			mCommunicationDBtCursor.moveToFirst();
			
			//显示到列表中
			if(mAdapter == null){
				mAdapter = new MemberListCursorAdapter(MemberListActivity.this, mCommunicationDBtCursor, getmClassImageLoader());
				mEventsListView.setAdapter(mAdapter);
			}else{
				mAdapter.changeCursor(mCommunicationDBtCursor);
			}
		}
		
		//列表头
		if (mCommunicationDBtCursor == null) {
			mEventsListView.hideHeadView();
		} else {
			if (mCommunicationDBtCursor.getCount() > 0) {
				mEventsListView.hideHeadView();
			} else {
				mEventsListView.setHeadViewVisible();
			}
		}
	}
	
	
	/**
	 * 请求网络(取列表)
	 * @param lastActivityId   0:刷新         非0：更多
	 * @param uiHandler
	 */
	private void sendGetEventsRequest(){
		showProgressBar();
		mEventsListView.setOnloadingRefreshVisible();
		mMemberListActivityManagement.getContactList(mMemberListActivityManagement.getContactList(),mHandler);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == UPDATEFINISH){
				MessageObject obj = (MessageObject)msg.obj;
				if(obj.isSuccess){
					//解决进入加载过程中退出，导致crash异常死机
					if(mMemberListActivityManagement != null){
						initalizeCursorAdapter();
						mMemberListActivityManagement.SaveReflashtime();
						mMemberListActivityManagement.BroadcastUnreadSum();
					}
				}
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(MemberListActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
//				mEventsListView.clearPullToRefresh(true);
				//解决进入加载过程中退出，导致crash异常死机
				if(mMemberListActivityManagement != null){
					initalizeCursorAdapter();
				}
			}else if(msg.arg1 == UPRELOGINSUCCESS){
				if(isRequestRelogin){
					sendGetEventsRequest();	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
			
			hideProgressBar();
		}
	};
	
	@Override
	protected void onDestroy() {
		mHandler = null;
		if(mCommunicationDBtCursor != null){
			mCommunicationDBtCursor.close();
			mCommunicationDBtCursor = null;
		}
		mAdapter = null;
		mMemberListActivityManagement = null;
		super.onDestroy();
	}
	
}
