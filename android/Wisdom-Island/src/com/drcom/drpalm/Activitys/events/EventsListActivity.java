package com.drcom.drpalm.Activitys.events;

import java.util.Date;

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
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.events.EventsListActivityManagement;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.MessageObject;
import com.wisdom.island.R;

public class EventsListActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	public static final String KEY_CATEGORYID = "categoryidkey";

	// 变量
	private int mCategoryid = 0; // 分类ID
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private EventsListCursorAdapter mAdapter;
	private int mCurNewCount = 0; // 当前纪录数
	private String mUsername = "";
	private SettingManager setInstance;
	private Date mDateStart = new Date(0); // 有效期开始
	private Date mDateEnd = new Date(0); // 有效期结束
	private int mOrderby = 2; // 排列方式
	private String mLastUpdatetime = "0";	//最顶一条消息的最后更新时间
	private String mLastReadtime = "0";		//最近一条被读时间
	private EventsListActivityManagement mEventsListActivityManagement;
	
	// 控件
	private RefreshListView mEventsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_list_view, mLayout_body);

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(KEY_CATEGORYID)) {
			mCategoryid = extras.getInt(KEY_CATEGORYID);
		}
		
		mEventsListActivityManagement = new EventsListActivityManagement(EventsListActivity.this, mCategoryid);
		setInstance = SettingManager.getSettingManager(this);
		mEventsDB = EventsDB.getInstance(this, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		
		//
		hideToolbar();

		mEventsListView = (RefreshListView) findViewById(R.id.eventhomeListview);
		mEventsListView.setCacheColorHint(Color.WHITE);
		mEventsListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(mEventsListView.getCount()>1){
					Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(1);
					mLastUpdatetime = mEventsListActivityManagement.GetTheFristEventLastupdateTime(newsCursor);
					mLastReadtime = mEventsListActivityManagement.GetTheLastReadtime();
					sendGetEventsRequest(mLastUpdatetime);
				}else{
					sendGetEventsRequest(mLastUpdatetime);
				}
			}
		});
		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(position);
				mEventsListActivityManagement.onListviewItemClick(newsCursor);
			}
		});
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(ModuleNameDefine.getEventsModuleNamebyId(mCategoryid));
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mAdapter == null){
			// 显示正在加载的title，CursorAdapter Cursor 可以为空
			mAdapter = new EventsListCursorAdapter(EventsListActivity.this, mEventCursor, getmClassImageLoader());
			mEventsListView.setAdapter(mAdapter);
			mEventsListView.setOnloadingRefreshVisible();
			// 异步加载数据,以免在初始化界面时加载,导致打开窗体时卡界面
			mEventsListView.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					initalizeCursorAdapter(mDateStart.getTime(), mDateEnd.getTime(), mOrderby);
					
					if(mCurNewCount>0)
						mLastUpdatetime = mEventsListActivityManagement.GetTheFristEventLastupdateTime((Cursor) mEventsListView.getItemAtPosition(1));
					
					// 大于15分钟自动刷新
					if (mEventsListActivityManagement.isTimeToReflash()) {
						sendGetEventsRequest(mLastUpdatetime);
						mDateStart = new Date(0);
						mDateEnd = new Date(0);
					}
				}
			}, 300);
		}else{
			initalizeCursorAdapter(mDateStart.getTime(), mDateEnd.getTime(), mOrderby);
		}
	}
	
	/**
	 * 
	 */
	private void initalizeCursorAdapter(long starttime, long endtime, int orderBy) {
		// initalizeFootView();

		mEventCursor = mEventsDB.getEventCursor(mCategoryid, starttime, endtime, mUsername, orderBy);
		mEventCursor.requery();
		mEventCursor.moveToFirst();

		mCurNewCount = mEventCursor.getCount();
		// 显示到列表中
		if (mAdapter == null) {
			mAdapter = new EventsListCursorAdapter(EventsListActivity.this, mEventCursor, getmClassImageLoader());
			mEventsListView.setAdapter(mAdapter);
		} else {
			mAdapter.changeCursor(mEventCursor);
		}

		// 列表头
		if (mEventCursor == null) {
			mEventsListView.hideHeadView();
		} else {
			if (mEventCursor.getCount() > 0) {
				mEventsListView.hideHeadView();
			} else {
				mEventsListView.setHeadViewVisible();
			}

		}

		hideProgressBar();
	}

	/*
	 * 初始化列表FootView更多
	 */
	// private void initalizeFootView(){
	// if(null == mFooterView){
	// Context context = EventsListActivity.this;
	// mFooterView = new FootView(context);
	//
	// }
	// mFooterView.setTitle(GlobalVariables.gAppResource.getString(R.string.loadmorenews));
	// if(0 == mEventsListView.getFooterViewsCount()){
	// mEventsListView.addFooterView(mFooterView);
	// }
	// }

	/**
	 * 请求网络(取列表)
	 * 
	 * @param lastActivityId
	 *            0:刷新 非0：更多
	 * @param uiHandler
	 */
	private void sendGetEventsRequest(final String lastupdate) {
		showProgressBar();
		mEventsListView.setOnloadingRefreshVisible();

		if(!mEventsListActivityManagement.GetData(lastupdate,mLastReadtime,mHandler)){
			initalizeCursorAdapter(mDateStart.getTime(), mDateEnd.getTime(), mOrderby);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == UPDATEFINISH) {
				MessageObject obj = (MessageObject) msg.obj;
				if (obj.isSuccess) {
					initalizeCursorAdapter(mDateStart.getTime(), mDateEnd.getTime(), mOrderby);
					mEventsListActivityManagement.SaveReflashtime();
				}
			} else if (msg.arg1 == MOREFINISH) {
				initalizeCursorAdapter(mDateStart.getTime(), mDateEnd.getTime(), mOrderby);
				mEventsListActivityManagement.SaveReflashtime();
			} else if (msg.arg1 == UPDATEFAILED) {
				String err = (String) msg.obj;
				if (err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))) {
					GlobalVariables.showInvalidSessionKeyMessage(EventsListActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
				mEventsListView.hideHeadView();
				hideProgressBar();
			}

		}
	};

	@Override
	protected void onDestroy() {
		if (mEventCursor != null)
			mEventCursor.close();
		mEventCursor = null;
		super.onDestroy();
	}
}
