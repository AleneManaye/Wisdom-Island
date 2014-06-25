package com.drcom.drpalm.Activitys.news.bookmark;

import java.util.List;

import android.content.Context;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.news.NewsListAdapter;
import com.drcom.drpalm.View.news.bookmark.NewsBookmarkListActivityManagement;
import com.drcom.drpalm.objs.NewsItem;
import com.wisdom.island.R;

public class NewsBookmarkListActivity extends ModuleActivity {
	// 变量
	// private int mCategoryid = 0; //分类ID
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	public static final String CATEGORYID_KEY = "categoryidkey";

	private NewsListAdapter mAdapter;
	private List<NewsItem> mData;

	private NewsBookmarkListActivityManagement mManagment;
	// 控件
	private ListView mEventsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_list_notitle_view, mLayout_body);

		mEventsListView = (ListView) findViewById(R.id.eventhomeListview);
		mEventsListView.setCacheColorHint(Color.WHITE);

		mManagment = new NewsBookmarkListActivityManagement(this);
		mData = mManagment.getDataList();
		mAdapter = new NewsListAdapter(NewsBookmarkListActivity.this, mData, -1, getmSchoolImageLoader());
		mEventsListView.setAdapter(mAdapter);
		hideToolbar();

		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NewsItem newsItem = (NewsItem) parent.getItemAtPosition(position);
				mManagment.startDetailActivity(newsItem);
			}
		});
		setTitleText(getString(R.string.marknews));
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initalizeCursorAdapter();
	}

	/**
	 *
	 * @param date
	 *            日期 Date.getTime()
	 */
	private void initalizeCursorAdapter() {
		// 显示到列表中
		mData.clear();
		mData.addAll(mManagment.getDataList());
		mAdapter.notifyDataSetChanged();
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
