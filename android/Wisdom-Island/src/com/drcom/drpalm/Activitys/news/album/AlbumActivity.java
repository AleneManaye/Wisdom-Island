package com.drcom.drpalm.Activitys.news.album;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.album.ClassAlbumActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.news.album.AlbumActivityManager;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 相册列表
 * 
 * @author Administrator
 * 
 */
public class AlbumActivity extends ModuleActivity {
	// 常量
	private static final int DOWN = 1;
	private static final int NOT_DOWN = 0;
	private static final int REFRESH = 2;
	private static final String LASTREFRESHDATE = "newslastrefreshtime";
	private static final String SP_DATABASE_NAME = "album";
	// 组件
	private RefreshListView mListView;
	private AlbumAdapter mAdapter;
	private View mFooterView;// mListView的尾部视图，查看更多
	// 数据
	private AlbumActivityManager mAlbumActivityManager;
	private NewsDB newsDB;
	private Date lastupdate, lastrefreshTime;
	private Cursor mAlbumCursor;
	private List<NewsItem> data;
	private int newsCount = 10;
	private LayoutInflater inflater;
//	private SharedPreferences sp;
//	private Editor editor;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DOWN:
				refreshUI();
				break;
			case NOT_DOWN:
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				new ErrorNotificatin(AlbumActivity.this).showErrorNotification(strError);
				break;
			case REFRESH:
				refreshUI();
				break;
			default:
				break;
			}
			mListView.onRefreshComplete();
			hideProgressBar();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.album_grid_main, mLayout_body);

		// 初始化组件
		initData();
		mListView.setOnloadingRefreshVisible();
		mLayout_body.postDelayed(new Runnable() {

			@Override
			public void run() {
//				refreshUI();
				// 大于15分钟自动刷新
				if ((new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 15) {
					//requestData("0");
					mListView.setOnloadingRefreshVisible();
					if(!mAlbumActivityManager.getNewsList(String.valueOf(lastupdate.getTime() / 1000), mHandler))
					{
						mListView.onRefreshComplete();
					}
				}
			}
		}, 300);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getmSchoolImageLoader();//重启时，清空图片缓存
		refreshUI();//刷新UI
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mAlbumActivityManager.sendReflashUnreadsumBroadcast();
//		mAlbumActivityManager = null;
		
		super.onDestroy();
	}
	
	/**
	 * 初始化相册组件
	 * 
	 */
	private void initData() {
		
		mAlbumActivityManager = new AlbumActivityManager(AlbumActivity.this);
		
		mListView = (RefreshListView) findViewById(R.id.album_list);
		mListView.setDividerHeight(0);
		// 初始化末尾，更多选项
		mFooterView = inflater.inflate(R.layout.foot_view, null);

		newsDB = NewsDB.getInstance(AlbumActivity.this, GlobalVariables.gSchoolKey);
		data = new ArrayList<NewsItem>();
//		sp = this.getSharedPreferences(SP_DATABASE_NAME, MODE_WORLD_READABLE);
//		editor = sp.edit();
		lastupdate = new Date(0);
		lastrefreshTime = mAlbumActivityManager.getLastRefreshTime();
		mAdapter = new AlbumAdapter(AlbumActivity.this, data, getmSchoolImageLoader());
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(mScrollListener);

		hideToolbar();
		showProgressBar();
//		setTitlebarBgColor(getResources().getColor(R.color.bglightred));

		mListView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				newsCount = 10;
				mListView.setOnloadingRefreshVisible();
				if(!mAlbumActivityManager.getNewsList("0", mHandler))
				{
					mListView.onRefreshComplete();
				}
				//requestData("0");
				Log.i("xpf", "刷新中");
			}
		});
		mFooterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newsCount += 10;
				//requestData(String.valueOf(lastupdate.getTime() / 1000));
				mListView.setOnloadingRefreshVisible();
				if(!mAlbumActivityManager.getNewsList(String.valueOf(lastupdate.getTime() / 1000), mHandler))
				{
					mListView.onRefreshComplete();
				}
			}
		});
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getString(R.string.album));
		}
	}

//	/**
//	 * 请求所有相册数据 变量
//	 * 
//	 * @param count
//	 *            请求记录的数目 刷新为10条
//	 * @param lastUpdate
//	 *            最后更新时间 ，0 为刷新
//	 */
//	private void requestData(final String lastUpdate) {
//
//		mListView.setOnloadingRefreshVisible();
//		// 网络不通时,返回
//		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) {
//			mListView.onRefreshComplete();
//			return;
//		}
//		showProgressBar();
//		// 从网络下载数据
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationCallback callback = new RequestOperationCallback() {
//			@Override
//			public void onSuccess() {
////				editor.putLong(LASTREFRESHDATE + RequestCategoryID.NEWS_ALBUM_ID, new Date(System.currentTimeMillis()).getTime());// 保存最后次更新的时间
////				editor.commit();
//				mAlbumActivityManager.saveLastRefreshTime();
//				if ("0".equals(lastUpdate)) {// 刷新
//					Message msg = new Message();
//					msg.what = REFRESH;
//					mHandler.sendMessageDelayed(msg, 200);
//				} else {
//					Message msg = new Message();
//					msg.what = DOWN;
//					mHandler.sendMessageDelayed(msg, 300);
//				}
//			}
//
//			@Override
//			public void onError(String err) {
//				Message msg = new Message();
//				msg.what = NOT_DOWN;// 下载失败
//				msg.obj = err;
//				mHandler.sendMessage(msg);
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetNews", new Object[] { RequestCategoryID.NEWS_ALBUM_ID, lastUpdate, callback }, callback.getClass().getName());
//	}

	private void refreshUI() {
		showProgressBar();
		// 从数据库中取出指定条数的数据
		mAlbumCursor = newsDB.getNewsCursor(RequestCategoryID.NEWS_ALBUM_ID, String.valueOf(newsCount));

		Log.i("xpf", "mAlbumCursor.count=" + mAlbumCursor.getCount());
		data.clear();
		if (mAlbumCursor.getCount() > 0) {
			// 添加下载到的记录到第一个位置
			for (mAlbumCursor.moveToFirst(); !mAlbumCursor.isAfterLast(); mAlbumCursor.moveToNext()) {
				NewsItem item = newsDB.retrieveNewsItem(mAlbumCursor);
				lastupdate = item.lastupdate;
				data.add(item);
			}
		}
		mAlbumCursor.close();

		if (data.size() >= newsCount) {//如果有未加载完的数据，则显示查看更多选项，没有则不显示
			if (mListView.getFooterViewsCount() == 0)
				mListView.addFooterView(mFooterView);
		} else
			mListView.removeFooterView(mFooterView);

		Log.i("xpf", "count=" + newsCount + " data.size=" + data.size());
		if (newsCount > data.size()) {// 如果请求数据 >下载到的数据
		} else if (newsCount == data.size()) {// 如果请求数据 ==下载到的数据
		} else if (newsCount < data.size()) {// 如果请求数据<下载到的数据,只显示一部分
			List<NewsItem> idata = new ArrayList<NewsItem>();
			for (int i = 0; i < newsCount; i++) {
				idata.add(data.get(i));
			}
			data.clear();
			data = idata;
		}
		Log.i("xpf", "count=" + newsCount + " data.size=" + data.size());
		mAdapter.notifyDataSetChanged();
		// mAdapter = new AlbumAdapter(this, data);
		// mListView.setAdapter(mAdapter);
		mListView.onRefreshComplete();

		// 列表头
		if (data.size() > 0) {
			mListView.hideHeadView();
		} else {
			mListView.setHeadViewVisible();
		}
		hideProgressBar();
	}

	//添加listView的滑动监听，在开始滑动的时候不下载数据，防止UI阻塞，在停止滑动和手指按着不动时，开始下载图片
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
