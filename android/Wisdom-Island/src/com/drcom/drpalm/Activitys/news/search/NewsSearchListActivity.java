package com.drcom.drpalm.Activitys.news.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.news.NewsListAdapter;
import com.drcom.drpalm.View.controls.NewSearchBar;
import com.drcom.drpalm.View.controls.NewSearchBar.OnEnterKeyClickListener;
import com.drcom.drpalm.View.controls.NewSearchBar.OnSearchButtonClickListener;
import com.drcom.drpalm.View.news.search.NewsSearchListActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm4tianzhujiao.R;

public class NewsSearchListActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	public static final String CATEGORYID_KEY = "categoryidkey";

	// 变量
	// private int mCategoryid = 0; //分类ID

	// private int mCurNewCount = 0; //当前纪录数
	// private int mLastActivityId = 0; //最后一个活动的ID
	// private String mUsername = "";
	// private SettingManager setInstance;
	// private LoginManager mLogininstance = LoginManager.getInstance(this);
	// private RequestOperation mRequestOperation =
	// RequestOperation.getInstance();
	// private boolean isRequestRelogin = true; //登录SECCION超时要重登录?
	// private Date mDate = new Date(0); //有效期
	// private int mOrderby = 0; //排列方式
	// private String mSearchkey = "";
	private NewsSearchListActivityManagement mManagment;
	private List<NewsItem> mData = new ArrayList<NewsItem>();
	private NewsListAdapter mAdapter;

	// 控件
	private ListView mEventsListView;
	private NewSearchBar mSearchbar;
	private String searchKey;

	// private FootView mFooterView = null;
	// private TextView mDateTextView;
	// private Button mBtnChooseDate;
	// private Button mBtnNewReply;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_list_search_view, mLayout_body);

		// Bundle extras = getIntent().getExtras();
		// if(extras.containsKey(CATEGORYID_KEY)){
		// mCategoryid = extras.getInt(CATEGORYID_KEY);
		// }

		//
		// setInstance = SettingManager.getSettingManager(this);

		mEventsListView = (ListView) findViewById(R.id.eventhomeListview);
		mEventsListView.setCacheColorHint(Color.WHITE);

		mManagment = new NewsSearchListActivityManagement(this);
//		mData = mManagment.getDataList();
		mAdapter = new NewsListAdapter(NewsSearchListActivity.this, mData, -1, getmSchoolImageLoader());
		//
		hideToolbar();
		// //时间
		// mDateTextView = (TextView)findViewById(R.id.eventhomeDateTextView);
		// mDateTextView.setText(getResources().getString(R.string.all));//(DateFormatter.getStringYYYYMMDD(mDate));
		// //选日期
		// mBtnChooseDate = (Button)findViewById(R.id.buttonChooseDate);
		// mBtnChooseDate.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// // mOrderby = 0;
		// // showDateMessage();
		// }
		// });
		// //按最新回复
		// mBtnNewReply = (Button)findViewById(R.id.buttonNewReply);
		// mBtnNewReply.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// // mOrderby = 2;
		// initalizeCursorAdapter();
		// }
		// });

		mSearchbar = (NewSearchBar) findViewById(R.id.search_bar);
		mSearchbar.setOnEnterKeyClickListener(new OnEnterKeyClickListener() {

			@Override
			public void onClick(String searchKey) {
				// TODO Auto-generated method stub
				NewsSearchListActivity.this.searchKey = searchKey;
				initalizeCursorAdapter(searchKey);
				sendGetSearchNews(searchKey);
			}
		});
		mSearchbar.setOnSearchButtonClickListener(new OnSearchButtonClickListener() {

			@Override
			public void onClick(View v, String searchKey) {
				// TODO Auto-generated method stub
				NewsSearchListActivity.this.searchKey = searchKey;
				initalizeCursorAdapter(searchKey);
				sendGetSearchNews(searchKey);
			}
		});

		mEventsListView.setAdapter(mAdapter);
		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NewsItem newsItem = (NewsItem) parent.getItemAtPosition(position);
				mManagment.startDetailActivity(newsItem);
			}
		});

		// initalizeCursorAdapter();
		setTitleText(getString(R.string.searchnews));
	}

	private void sendGetSearchNews(final String searchKey) {
		showProgressBar();
		final Handler myHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				hideProgressBar();
				if (msg.arg1 == NewsSearchListActivityManagement.FETCH_SUCCESSFUL) {
					initalizeCursorAdapter(searchKey);
				} else if (msg.arg1 == NewsSearchListActivityManagement.FETCH_FAILED) {
					String strError = (msg.obj != null) ? (String) msg.obj : "";
					// sendUpdateFail(strError);
					new ErrorNotificatin(NewsSearchListActivity.this).showErrorNotification(strError);
				}
			};
		};
		mManagment.sendGetSearchNews(searchKey, myHandler);
	}

	/**
	 *
	 * @param date
	 *            日期 Date.getTime()
	 */
	private void initalizeCursorAdapter(String searchkey) {
		if (searchkey.equals("")) {
			return;
		}
		// initalizeFootView();
		// mCurNewCount = mEventCursor.getCount();
		// 显示到列表中
		mData.clear();
		mData.addAll(mManagment.getDataList(searchkey));
		mAdapter.notifyDataSetChanged();

		// 列表头
		// if (mEventCursor == null) {
		// mEventsListView.hideHeadView();
		// } else {
		// if (mEventCursor.getCount() > 0) {
		// mEventsListView.hideHeadView();
		// } else {
		// mEventsListView.setHeadViewVisible();
		// }
		// }

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

	// /**
	// * 请求网络(取列表)
	// * @param lastActivityId 0:刷新 非0：更多
	// * @param uiHandler
	// */
	// private void sendGetEventsRequest(final String lastupdate){
	// mRequestOperation.GetEventsList(RequestCategoryID.EVENTS_NEWS_ID,
	// lastupdate, new RequestGetEventListCallback(){
	// @Override
	// public void onSuccess() {
	// Message message = Message.obtain();
	// if(lastupdate.equals("0")){
	// message.arg1 = UPDATEFINISH; //刷新
	// }else{
	// message.arg1 = MOREFINISH; //更多
	// }
	// message.obj = new MessageObject(true,false);
	// mHandler.sendMessage(message) ;
	// }
	//
	// @Override
	// public void onError(String str) {
	// Message message = Message.obtain();
	// message.arg1 = UPDATEFAILED;
	// message.obj = str;
	// mHandler.sendMessage(message);
	// }
	//
	// @Override
	// public void onLoading() {
	// // TODO Auto-generated method stub
	// Message message = Message.obtain();
	// message.arg1 = MOREFINISH; //更多
	// message.obj = new MessageObject(true,false);
	// mHandler.sendMessage(message) ;
	// }
	// });
	//
	// // /*
	// // * 使用RequestOperationReloginCallback类回调，当SESSIONKEY失效时，会自动登录，再请求数据接口
	// // * 注意：代码要使用private boolean isRequestRelogin = true;
	// 登录SECCION超时重登录标志记录，以免不断重登造成死循环
	// // */
	// // mRequestOperation.GetEventsList(mCategoryid, lastupdate, new
	// RequestOperationReloginCallback(){
	// // @Override
	// // public void onSuccess() { //请求数据成功
	// // super.onSuccess();
	// // Message message = Message.obtain();
	// // if(lastupdate.equals("0")){
	// // message.arg1 = UPDATEFINISH; //刷新
	// // }else{
	// // message.arg1 = MOREFINISH; //更多
	// // }
	// // message.obj = new MessageObject(true,false);
	// // mHandler.sendMessage(message) ;
	// //
	// // Log.i("zjj", "通告列表:刷新成功");
	// // }
	// //
	// // @Override
	// // public void onError(String str) {
	// // super.onError(str);
	// // Message message = Message.obtain();
	// // message.arg1 = UPDATEFAILED;
	// // message.obj = str;
	// // mHandler.sendMessage(message);
	// //
	// // Log.i("zjj", "通告列表:刷新失败" + str);
	// // }
	// //
	// // @Override
	// // public void onReloginError() {
	// // // TODO Auto-generated method stub
	// // super.onReloginError();
	// // Log.i("zjj", "通告列表:自动重登录失败");
	// // }
	// //
	// // @Override
	// // public void onReloginSuccess() {
	// // // TODO Auto-generated method stub
	// // super.onReloginSuccess();
	// // Log.i("zjj", "通告列表:自动重登录成功");
	// // if(isRequestRelogin){
	// // sendGetEventsRequest(lastupdate); //自动登录成功后，再次请求数据
	// // isRequestRelogin = false;
	// // }
	// // }
	// // });
	// }

	// private Handler mHandler = new Handler(){
	// @Override
	// public void handleMessage(Message msg) {
	// if(msg.arg1 == UPDATEFINISH){
	// MessageObject obj = (MessageObject)msg.obj;
	// if(obj.isSuccess){
	// initalizeCursorAdapter(mDate.getTime(),mOrderby);
	// hideProgressBar();
	// }
	// }else if(msg.arg1 == MOREFINISH){
	// initalizeCursorAdapter(mDate.getTime(),mOrderby);
	// }else if(msg.arg1 == UPDATEFAILED){
	// hideProgressBar();
	// }
	// }
	// };

	// @Override
	// protected void onRestart() {
	// super.onRestart();
	// initalizeCursorAdapter();
	// }
	@Override
	protected void onRestart() {
		super.onRestart();
		if (searchKey != null && !"".equalsIgnoreCase(searchKey)){
			sendGetSearchNews(searchKey);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// /**
	// * 选择日期对话框
	// * @param pTitle
	// */
	// private void showDateMessage() {
	// final AlertDialog dlg = new AlertDialog.Builder(
	// EventsBookmarkListActivity.this).create();
	// dlg.show();
	// Window window = dlg.getWindow();
	// window.setContentView(R.layout.prizechoosedatequery_view);
	// final MyDatePicker mDatePicker =
	// (MyDatePicker)window.findViewById(R.id.datetime_picker);
	// Button ok = (Button) window.findViewById(R.id.datetime_ok);
	// ok.setOnClickListener(new View.OnClickListener() {
	// public void onClick(View v) {
	// Time t = mDatePicker.getTime();
	//
	// Message msg = new Message();
	// msg.obj = t;
	// mChooseTimeHandler.sendMessage(msg);
	//
	// dlg.cancel();
	// }
	// });
	//
	// // 关闭alert对话框
	// Button cancel = (Button) window.findViewById(R.id.datetime_cancel);
	// cancel.setOnClickListener(new View.OnClickListener() {
	// public void onClick(View v) {
	// dlg.cancel();
	// }
	// });
	// }
	//
	// private Handler mChooseTimeHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// Time t = (Time) msg.obj;
	//
	// mDate = new Date(t.year - 1900, (t.month),t.monthDay, t.hour, t.minute);
	// // 年份从1900开始
	// mDateTextView.setText(getResources().getString(
	// R.string.valid_time)
	// + t.year
	// + "-"
	// + (t.month + 1)
	// + "-"
	// + t.monthDay);
	//
	// initalizeCursorAdapter(mDate.getTime(),mOrderby);
	// }
	// };

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// mEventsListView.onTouchEvent(ev);
	// // mGestureDetector.onTouchEvent(ev);
	// return super.dispatchTouchEvent(ev);
	// }
}
