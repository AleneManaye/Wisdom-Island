package com.drcom.drpalm.Activitys.events.album;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestGetEventListCallback;
import com.drcom.drpalm.Tool.request.RequestGetEventListReloginCallback;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.events.album.ClassAlbumActivityManager;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.MessageObject;
import com.wisdom.island.R;

/**
 * 相册列表
 * 
 * @author Administrator
 * 
 */
public class ClassAlbumActivity extends ModuleActivity {

	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	public static final int RELOGINSUCCESS = 3; // 重登录
	
	public static final String CATEGORYID_KEY = "categoryidkey";
	//private static final String LASTREFRESHDATE = "classlastrefreshtime";
	private static final String SP_DATABASE_NAME = "classalbum";

	// 变量
	private int mCategoryid = 0; // 分类ID
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private List<EventDetailsItem> data = null;
	private ClassAlbumAdapter mAdapter;
	private int mCurNewCount = 0; // 当前纪录数
	private String mUsername = "";
	private SettingManager setInstance;
	// private LoginManager mLogininstance = LoginManager.getInstance(this);
	// private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	private Date lastrefreshTime;
//	private SharedPreferences sp;
//	private Editor editor;
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	private String mLastUpdatetime = "0";	//最顶一条消息的最后更新时间
	private String mLastReadtime = "0";		//最近一条被读时间
	
	private ClassAlbumActivityManager mClassAlbumActivityManager;
	// 控件
	private RefreshListView mListView;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == UPDATEFINISH) {
				refreshUI();

//				editor.putLong(LASTREFRESHDATE, new Date().getTime());// 保存最后次更新的时间
//				editor.commit();
				mClassAlbumActivityManager.saveLastUpdateTime(new Date().getTime());

			} else if (msg.arg1 == MOREFINISH) {
				refreshUI();
			} else if (msg.arg1 == UPDATEFAILED) {
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				if (strError.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))) {
					GlobalVariables.showInvalidSessionKeyMessage(ClassAlbumActivity.this);
				} else {
					new ErrorNotificatin(ClassAlbumActivity.this).showErrorNotification(strError);
				}

			}
			else if(msg.arg1 == RELOGINSUCCESS)
			{
				if (isRequestRelogin) {
					mClassAlbumActivityManager.getEventsList(mCategoryid,mLastUpdatetime, mLastReadtime,mHandler);
					isRequestRelogin = false;
				}
			}
			hideProgressBar();
			mListView.onRefreshComplete();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.album_grid_main, mLayout_body);

		hideToolbar();
		initalizeData();
		mListView.setOnloadingRefreshVisible();
		mLayout_body.postDelayed(new Runnable() {

			@Override
			public void run() {
//				refreshUI();
				Log.i("xpf", "xw time is =" + ((new Date().getTime() - lastrefreshTime.getTime()) / 1000 / 60) + " mUsername=" + mUsername);
				Log.i("zjj", "通告相册 刷新时间对比:" + new Date().getTime() + "'" + lastrefreshTime.getTime());
				if (!"".equalsIgnoreCase(mUsername) && (new Date().getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 15) {
					//sendGetEventsRequest(mLastUpdatetime);
					mListView.setOnloadingRefreshVisible();
					showProgressBar();
					mClassAlbumActivityManager.getEventsList(mCategoryid,mLastUpdatetime,mLastReadtime, mHandler);
				}
			}
		}, 300);
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getString(R.string.classalbum));
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getmClassImageLoader();
		refreshUI();
	}

	private void initalizeData() {
		// 得到分类ID
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(CATEGORYID_KEY)) {
				mCategoryid = extras.getInt(CATEGORYID_KEY);
			}
		}
		// 实例化类
		mClassAlbumActivityManager = new ClassAlbumActivityManager(ClassAlbumActivity.this);
		
		setInstance = SettingManager.getSettingManager(this);
		mEventsDB = EventsDB.getInstance(this, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		data = new ArrayList<EventDetailsItem>();
//		sp = getSharedPreferences(SP_DATABASE_NAME, MODE_WORLD_READABLE);
//		editor = sp.edit();
		//lastrefreshTime = new Date(sp.getLong(LASTREFRESHDATE, 0));
		lastrefreshTime = mClassAlbumActivityManager.getLastUpdateTime();

		mListView = (RefreshListView) findViewById(R.id.album_list);
		mListView.setDividerHeight(0);
		showProgressBar();
		mAdapter = new ClassAlbumAdapter(ClassAlbumActivity.this, data, getmClassImageLoader());
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(mScrollListener);
		// 点击下拉刷新列表
		mListView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				//sendGetEventsRequest(mLastUpdatetime);
				mListView.setOnloadingRefreshVisible();
				showProgressBar();
				mClassAlbumActivityManager.getEventsList(mCategoryid,mLastUpdatetime, mLastReadtime,mHandler);
			}
		});

	}

//	/**
//	 * 请求网络(取列表)
//	 * 
//	 * @param lastActivityId
//	 *            0:刷新 非0：更多
//	 * @param uiHandler
//	 */
//	private void sendGetEventsRequest(final String lastupdate) {
//
//		mListView.setOnloadingRefreshVisible();
//		// 非在线登录/网络不通时,返回
//		// LoginManager instance =
//		// LoginManager.getInstance(GlobalVariables.gAppContext);
//		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) { // LoginManager.OnlineStatus.ONLINE_LOGINED
//			mListView.onRefreshComplete();
//			// !=
//			// instance.getOnlineStatus()
//			// ||
//			return;
//		}
//		showProgressBar();
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestGetEventListCallback callback = new RequestGetEventListReloginCallback() {
//			@Override
//			public void onSuccess() {
//				Message message = Message.obtain();
//				if (lastupdate.equals("0")) {
//					message.arg1 = UPDATEFINISH; // 刷新
//				} else {
//					message.arg1 = MOREFINISH; // 更多
//				}
//				mHandler.sendMessage(message);
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
//			public void onLoading() {
//				Message message = Message.obtain();
//				message.arg1 = MOREFINISH; // 更多
//				message.obj = new MessageObject(true, false);
//				mHandler.sendMessage(message);
//			}
//
//			@Override
//			public void onReloginError() {
//				super.onReloginError();
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED; // 更多
//				message.obj = null;
//				mHandler.sendMessage(message);
//				Log.i("zjj", "通告列表:自动重登录失败");
//			}
//
//			@Override
//			public void onReloginSuccess() {
//				super.onReloginSuccess();
//				Log.i("zjj", "通告列表:自动重登录成功");
//				if (isRequestRelogin) {
//					sendGetEventsRequest(lastupdate); // 自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetEventsList", new Object[] { mCategoryid, lastupdate,mLastReadtime, callback }, callback.getClass().getName());
//	}

	private void refreshUI() {
		showProgressBar();
		mEventCursor = mEventsDB.getEventCursor(mCategoryid, 0, 0, mUsername, 0);
		mCurNewCount = mEventCursor.getCount();
		Log.i("xpf", "mCurNewCount=" + mCurNewCount);

		if (mCurNewCount > 0) {// 从数据库中取数据
			data.clear();
			for (mEventCursor.moveToFirst(); !mEventCursor.isAfterLast(); mEventCursor.moveToNext()) {
				EventDetailsItem item = mEventsDB.retrieveEventDetailItem(mEventCursor);
				data.add(item);
			}
			
			mLastReadtime = mEventsDB.getLastReadtime(mUsername,mCategoryid).getTime()/1000 + "";
			mLastUpdatetime = data.get(0).lastupdate.getTime()/1000 + "";
		}
		mEventCursor.close();
		// 列表头，如果有数据，则不显示，没有则显示点击刷新
		if (data.size() > 0) {
			mListView.hideHeadView();
		} else {
			mListView.setHeadViewVisible();
		}
		mAdapter.notifyDataSetChanged();
		hideProgressBar();
	}

	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				mAdapter.setFlagBusy(true);
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				mAdapter.setFlagBusy(false);
				mAdapter.notifyDataSetChanged();
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				mAdapter.setFlagBusy(false);
				break;
			default:
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mListView != null) {
				mListView.setFirstVisableItem(firstVisibleItem, visibleItemCount);
			}
			if (mAdapter != null) {
				mAdapter.setFirstVisableItem(firstVisibleItem, visibleItemCount);
			}
		}
	};
}
