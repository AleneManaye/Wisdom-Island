package com.drcom.drpalm.Activitys.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.drcom.drpalm.Activitys.events.EventsListActivity;
import com.drcom.drpalm.Activitys.events.album.ClassAlbumActivity;
import com.drcom.drpalm.Activitys.events.face2face.MemberListActivity;
import com.drcom.drpalm.Activitys.events.sent.EventsSentListActivity;
import com.drcom.drpalm.Activitys.events.video.ClassVideolistActivity;
import com.drcom.drpalm.Activitys.sysinfo.SysinfoListActivity;
import com.drcom.drpalm.DB.UpdateTimeDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.login.LoginManager.OnlineStatus;
import com.drcom.drpalm.View.main.TheNewsMsgListActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.MessageObject;
import com.drcom.drpalm.objs.UpdateTimeItem;
import com.drcom.drpalm4tianzhujiao.R;

public class ClassNewsListActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static String KEY_PUSHSTART = "KEY_PUSHSTART"; // 是否从PUSH启动(和MainActivity一致)
	private String KEY_REFLASHTIME = "classinfoflashtime";

	// 变量
	private UpdateTimeDB mUpdateTimeDB;
	private TheNewsAdapter mAdapter;
	private String mUsername = "";
	private SettingManager setInstance;
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	private SharedPreferences sp; // 15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	private List<UpdateTimeItem> mTheNewsItemList = new ArrayList<UpdateTimeItem>();
	private GroupReceiver mGroupReceiver; // Receiver
	private LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
	private TheNewsMsgListActivityManagement mClassNewsListActivityManagement;

	// 控件
	private RefreshListView mEventsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.thenews_list_view, mLayout_body);

		mClassNewsListActivityManagement = new TheNewsMsgListActivityManagement(ClassNewsListActivity.this);
		
		// 如果是从PUSH启动的话
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(KEY_PUSHSTART)) {
				// if(instance.getOnlineStatus() !=
				// OnlineStatus.ONLINE_LOGINED){
				Log.i("zjj", "ClassNewsListActivity启动:" + GlobalVariables.gAppRun);

				// 清除收到的PUSH提示(只清除本应用的)
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancelAll();

				// 新启动程序
				if (!GlobalVariables.gAppRun) {
					Log.i("zjj", "最新5通告启动 主界面");
					Intent i = new Intent(ClassNewsListActivity.this, MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra(MainActivity.KEY_PUSHSTART, true);
					startActivity(i);

					finishDraw();
					return;
				} else { // 程序正在运行
					if (instance.getOnlineStatus() != OnlineStatus.ONLINE_LOGINED) {
						Intent intent = new Intent(ActivityActionDefine.LOGIN_AND_SHOWNEWEVENTS_ACTION);
						sendBroadcast(intent);

						finishDraw();
						return;
					}
				}
			}
		}

		//
		setInstance = SettingManager.getSettingManager(this);
		mUpdateTimeDB = UpdateTimeDB.getInstance(this, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		sp = this.getSharedPreferences(KEY_REFLASHTIME, MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));

		//
		hideToolbar();
		mEventsListView = (RefreshListView) findViewById(R.id.thenewsListview);
		mEventsListView.setCacheColorHint(Color.WHITE);
		mEventsListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				sendGetEventsRequest();
			}
		});
		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UpdateTimeItem item = mTheNewsItemList.get(position - 1);
				ItemClicked(item.update_time_channel);
			}
		});
		mAdapter = new TheNewsAdapter(ClassNewsListActivity.this, mTheNewsItemList);
		mAdapter.mType = TheNewsAdapter.TYPE_CLASS;
		mEventsListView.setAdapter(mAdapter);

		// mEventsListView.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// initalizeCursorAdapter();
		// setTitlebarBgColor(getResources().getColor(R.color.bglightred));
		//
		// // 大于15分钟自动刷新
		// if ((new Date(System.currentTimeMillis()).getTime() -
		// lastrefreshTime.getTime()) / 1000 / 60 > 15) {
		// mEventsListView.hideHeadView();
		// sendGetEventsRequest();
		//
		// }
		// }
		// },300);

		setTitleText(getString(R.string.newestEvents));
		initReceiver();
		mEventsListView.hideHeadView();
		sendGetEventsRequest();
	}

	/**
	 * 请求网络(取列表)
	 * 
	 * @param lastActivityId
	 *            0:刷新 非0：更多
	 * @param uiHandler
	 */
	private void sendGetEventsRequest() {
		showProgressBar();

		mClassNewsListActivityManagement.GetData(mHandler);
		
//		// 拿最新5条新闻
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
//			@Override
//			public void onSuccess() { // 请求数据成功
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFINISH; // 刷新
//				message.obj = new MessageObject(true, false);
//				mHandler.sendMessage(message);
//
//				Log.i("zjj", "5条新列表:刷新成功");
//			}
//
//			@Override
//			public void onCallbackError(String str) {
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED;
//				message.obj = str;
//				mHandler.sendMessage(message);
//
//				Log.i("zjj", "5条新列表:刷新失败" + str);
//			}
//
//			@Override
//			public void onReloginError() {
//				// TODO Auto-generated method stub
//				Log.i("zjj", "5条新列表:自动重登录失败");
//			}
//
//			@Override
//			public void onReloginSuccess() {
//				// TODO Auto-generated method stub
//				Log.i("zjj", "5条新列表:自动重登录成功");
//				if (isRequestRelogin) {
//					sendGetEventsRequest(); // 自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetEventsInfoList", new Object[] { callback }, callback.getClass().getName());
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == UPDATEFINISH) {
				MessageObject obj = (MessageObject) msg.obj;
				if (obj.isSuccess) {
					initalizeCursorAdapter();
					// SaveReflashtime();
					hideProgressBar();
				}
			} else if (msg.arg1 == UPDATEFAILED) {
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				if (strError.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))) {
					GlobalVariables.showInvalidSessionKeyMessage(ClassNewsListActivity.this);
				} else {
					new ErrorNotificatin(ClassNewsListActivity.this).showErrorNotification(strError);
				}

				mEventsListView.hideHeadView();
			}
			hideProgressBar();
		}
	};

	private void initalizeCursorAdapter() {
		mTheNewsItemList.clear();
		mTheNewsItemList.addAll(mUpdateTimeDB.getEventsUpdateFlag(mUsername));
		mTheNewsItemList = mClassNewsListActivityManagement.Filter(mTheNewsItemList);
		mAdapter.notifyDataSetChanged();
		mEventsListView.hideHeadView();
	}

	@Override
	protected void onDestroy() {
		if (mGroupReceiver != null)
			unregisterReceiver(mGroupReceiver);
		super.onDestroy();
	}

	/**
	 * 点击事件
	 * 
	 * @param typeid
	 */
	private void ItemClicked(int typeid) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // 一定要这两个属性

		if (typeid == RequestCategoryID.EVENTS_NEWS_ID) {
			intent.setClass(ClassNewsListActivity.this, EventsListActivity.class);
			intent.putExtra(EventsListActivity.KEY_CATEGORYID, RequestCategoryID.EVENTS_NEWS_ID);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.EVENTS_ACTIVITY_ID) {
			intent.setClass(ClassNewsListActivity.this, EventsListActivity.class);
			intent.putExtra(EventsListActivity.KEY_CATEGORYID, RequestCategoryID.EVENTS_ACTIVITY_ID);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.EVENTS_COMMENT_ID) {
			intent.setClass(ClassNewsListActivity.this, EventsListActivity.class);
			intent.putExtra(EventsListActivity.KEY_CATEGORYID, RequestCategoryID.EVENTS_COMMENT_ID);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.EVENTS_ALBUM_ID) {
			intent.setClass(ClassNewsListActivity.this, ClassAlbumActivity.class);
			intent.putExtra(ClassAlbumActivity.CATEGORYID_KEY, RequestCategoryID.EVENTS_ALBUM_ID);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.EVENTS_COURSEWARE_ID) {
			intent.setClass(ClassNewsListActivity.this, EventsListActivity.class);
			intent.putExtra(EventsListActivity.KEY_CATEGORYID, RequestCategoryID.EVENTS_COURSEWARE_ID);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.EVENTS_SEND_ID) {
			intent.setClass(ClassNewsListActivity.this, EventsSentListActivity.class);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.SYSINFO_ID) {
			intent.setClass(ClassNewsListActivity.this, SysinfoListActivity.class);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.EVENTS_VIDEO_ID) {
			intent.setClass(ClassNewsListActivity.this, ClassVideolistActivity.class);
			intent.putExtra(ClassVideolistActivity.CATEGORYID_KEY, RequestCategoryID.EVENTS_VIDEO_ID);
			startActivity(intent);
		}else if (typeid == RequestCategoryID.EVENTS_COMMUNION_ID) {
			intent.setClass(ClassNewsListActivity.this, MemberListActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * initialize receiver
	 */
	private void initReceiver() {
		mGroupReceiver = new GroupReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC); // 通告未读数-1
		registerReceiver(mGroupReceiver, filter);
	}

	/**
	 * *************** 广播接收 ***************
	 */
	private class GroupReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String stringAction = intent.getAction();
				Bundle extras = intent.getExtras();

				if (stringAction.equals(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC)) {
					if (extras != null) {
						if (extras.containsKey(ActivityActionDefine.EVENTS_TYPE_ID)) {
							int type = extras.getInt(ActivityActionDefine.EVENTS_TYPE_ID);

							for (int i = 0; i < mTheNewsItemList.size(); i++) {
								if (mTheNewsItemList.get(i).update_time_channel == type) {
									int sum = Integer.valueOf(mTheNewsItemList.get(i).update_unreadcount) - 1;
									sum = sum < 1 ? 0 : sum;
									mTheNewsItemList.get(i).update_unreadcount = sum + "";
								}
							}

							mAdapter.notifyDataSetChanged();
						}
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
