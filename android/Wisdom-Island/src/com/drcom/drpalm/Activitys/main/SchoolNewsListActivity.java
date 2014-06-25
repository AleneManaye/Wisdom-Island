package com.drcom.drpalm.Activitys.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.drcom.drpalm.Activitys.news.NewsActivity;
import com.drcom.drpalm.Activitys.news.album.AlbumActivity;
import com.drcom.drpalm.DB.UpdateTimeDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.main.TheNewsMsgListActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.MessageObject;
import com.drcom.drpalm.objs.UpdateTimeItem;
import com.wisdom.island.R;

public class SchoolNewsListActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	private String KEY_REFLASHTIME = "schoolnewsflashtime";

	// 变量
	private UpdateTimeDB mUpdateTimeDB;
	private TheNewsAdapter mAdapter;
	private SettingManager setInstance;
	private SharedPreferences sp; // 15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	private List<UpdateTimeItem> mTheNewsItemList = new ArrayList<UpdateTimeItem>();
	private GroupReceiver mGroupReceiver; // Receiver
	private TheNewsMsgListActivityManagement mTheNewsMsgListActivityManagement;

	// 控件
	private RefreshListView mEventsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.thenews_list_view, mLayout_body);
		//
		setInstance = SettingManager.getSettingManager(this);
		mUpdateTimeDB = UpdateTimeDB.getInstance(this, GlobalVariables.gSchoolKey);
		sp = this.getSharedPreferences(KEY_REFLASHTIME, MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
		mTheNewsMsgListActivityManagement = new TheNewsMsgListActivityManagement(SchoolNewsListActivity.this);

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

		mAdapter = new TheNewsAdapter(SchoolNewsListActivity.this, mTheNewsItemList);
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

		setTitleText(getString(R.string.newestNews));
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

		mTheNewsMsgListActivityManagement.GetNewsData(mHandler);
		
//		// 拿最新5条新闻和通告
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationCallback callback = new RequestOperationCallback() {
//
//			@Override
//			public void onSuccess() { // 请求数据成功
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFINISH; // 刷新
//				message.obj = new MessageObject(true, false);
//				mHandler.sendMessage(message);
//
//				Log.i("zjj", "5条新--新闻列表:刷新成功");
//			}
//
//			@Override
//			public void onError(String str) {
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED;
//				message.obj = str;
//				mHandler.sendMessage(message);
//
//				Log.i("zjj", "5条新--新闻列表:刷新失败" + str);
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetNewsInfoList", new Object[] { callback }, callback.getClass().getName());
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
					GlobalVariables.showInvalidSessionKeyMessage(SchoolNewsListActivity.this);
				} else {
					new ErrorNotificatin(SchoolNewsListActivity.this).showErrorNotification(strError);
				}

				mEventsListView.hideHeadView();
			}
			hideProgressBar();
		}
	};

	private void initalizeCursorAdapter() {
		mTheNewsItemList.clear();
		mTheNewsItemList.addAll(mUpdateTimeDB.getNewsUpdateList());
		
		mTheNewsMsgListActivityManagement.FilterNews(mTheNewsItemList);
//		for (int j = 0; j < mTheNewsItemList.size(); j++) {
//			int id = mTheNewsItemList.get(j).update_time_channel.intValue();
//			for (int j2 = 0; j2 < GlobalVariables.blocks.size(); j2++) {
//				if (id == GlobalVariables.blocks.get(j2).getId() && "school".equals(GlobalVariables.blocks.get(j2).getType())) {
//					if (!GlobalVariables.blocks.get(j2).isVisible()) {
//						mTheNewsItemList.remove(j);
//						j = -1;
//					}
//				}
//			}
//		}
		mAdapter.notifyDataSetChanged();
		mEventsListView.hideHeadView();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mGroupReceiver);
		super.onDestroy();
	};

	// /**
	// * 保存刷新时间
	// */
	// private void SaveReflashtime(){
	// editor.putLong(KEY_REFLASHTIME , new
	// Date(System.currentTimeMillis()).getTime());// 保存最后一次刷新的时间
	// editor.commit();
	// }

	/**
	 * 点击事件
	 * 
	 * @param typeid
	 */
	private void ItemClicked(int typeid) {
		Intent intent = new Intent();
		if (typeid == RequestCategoryID.NEWS_NEWS_ID) {
			// 新闻列表3001
			intent.putExtra(NewsActivity.KEY_CATEGORY, RequestCategoryID.NEWS_NEWS_ID);
			intent.setClass(SchoolNewsListActivity.this, NewsActivity.class);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.NEWS_ACTIVITY_ID) {
			// 通告列表1001
			intent.putExtra(NewsActivity.KEY_CATEGORY, RequestCategoryID.NEWS_ACTIVITY_ID);
			intent.setClass(SchoolNewsListActivity.this, NewsActivity.class);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.NEWS_ALBUM_ID) {
			// 相册列表
			intent.setClass(SchoolNewsListActivity.this, AlbumActivity.class);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.NEWS_INFANTDIET_ID) {
			// 食谱列表2001
			intent.putExtra(NewsActivity.KEY_CATEGORY, RequestCategoryID.NEWS_INFANTDIET_ID);
			intent.setClass(SchoolNewsListActivity.this, NewsActivity.class);
			startActivity(intent);
		} else if (typeid == RequestCategoryID.NEWS_PARENTING_ID) {
			// 育儿列表4001
			intent.putExtra(NewsActivity.KEY_CATEGORY, RequestCategoryID.NEWS_PARENTING_ID);
			intent.setClass(SchoolNewsListActivity.this, NewsActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * initialize receiver
	 */
	private void initReceiver() {
		mGroupReceiver = new GroupReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActivityActionDefine.NEWS_UNREAD_SUM_DESC); // 通告未读数-1
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

				if (stringAction.equals(ActivityActionDefine.NEWS_UNREAD_SUM_DESC)) {
					if (extras != null) {
						if (extras.containsKey(ActivityActionDefine.NEWS_TYPE_ID)) {
							int type = extras.getInt(ActivityActionDefine.NEWS_TYPE_ID);

							for (int i = 0; i < mTheNewsItemList.size(); i++) {
								if (mTheNewsItemList.get(i).update_time_channel == type) {
									// int sum =
									// Integer.valueOf(mTheNewsItemList.get(i).update_unreadcount)
									// - 1;
									mTheNewsItemList.get(i).update_unreadcount = "0";
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
