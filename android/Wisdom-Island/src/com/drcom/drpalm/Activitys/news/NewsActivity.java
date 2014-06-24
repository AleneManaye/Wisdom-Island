package com.drcom.drpalm.Activitys.news;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.news.NewsActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm4tianzhujiao.R;

/**
 *
 * 显示新闻列表
 *
 * @author xpf
 * @param category
 *            新闻分类ID
 */
public class NewsActivity extends ModuleActivity {
	public static String KEY_CATEGORY = "KEY_CATEGORY";
	
	// 控件
	private RefreshListView mListView; // 新闻列表listView
	private View mFooterView;// mListView的尾部视图，查看更多
	private NewsListAdapter mAdapter;// mListView的CursorAdapter
	private LayoutInflater inflater;

	private List<NewsItem> data;
	private int newsCount = 10;// 请求的记录数目
	private int category;// 分类ID，通过getIntent获得
	private Date lastupdate, lastrefreshTime;

	private NewsActivityManagement mNewsActivityManagement;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NewsActivityManagement.DOWN:
				refreshUI();
				break;
			case NewsActivityManagement.NOT_DOWN:
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				new ErrorNotificatin(NewsActivity.this).showErrorNotification(strError);
				mListView.onRefreshComplete();

				break;
			case NewsActivityManagement.REFRESH:
				refreshUI();
				break;
			default:
				break;
			}
			hideProgressBar();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.news_main, mLayout_body);

		mNewsActivityManagement = new NewsActivityManagement(this);

		initNews();

		mListView.setVisibility(View.VISIBLE);
		mListView.setOnloadingRefreshVisible();
		mLayout_body.postDelayed(new Runnable() {

			@Override
			public void run() {
				refreshUI();
				// 大于15分钟自动刷新
				if ((new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 15) {
					requestNewsData("0", newsCount);
					Log.i("xpf", "自动刷新");
				}
			}
		}, 300);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getmSchoolImageLoader();
		refreshUI();
	}

	/**
	 * 初始化新闻组件
	 */
	private void initNews() {
		Intent intent = getIntent();// 获取分类ID
		category = intent.getIntExtra(KEY_CATEGORY, -1);

		// 初始化末尾，更多选项
		mFooterView = inflater.inflate(R.layout.foot_view, null);
		// 初始化数据源
		data = new ArrayList<NewsItem>();
		newsCount = 10;// 下载1条记录
		lastupdate = mNewsActivityManagement.getLastUpdate(); //new Date(0);
		lastrefreshTime = mNewsActivityManagement.getLastRefreshTime(category); //new Date(sp.getLong(LASTREFRESHDATE + category, 0));

		// 初始化listView,CursorAdapter
		mListView = (RefreshListView) findViewById(R.id.news_list);
		mListView.hideHeadView();

		mAdapter = new NewsListAdapter(this, data, category, getmSchoolImageLoader());
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(mScrollListener);

		// 隐藏工具栏
		hideToolbar();
		showProgressBar();

		// 更多選項，下载最新的记录
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newsCount += 10;
				requestNewsData(String.valueOf(lastupdate.getTime() / 1000), newsCount);
			}
		});
		mListView.setonRefreshListener(new OnRefreshListener() {// 点击文字 刷新
					@Override
					public void onRefresh() {
						newsCount = 10;
						requestNewsData("0", newsCount);
					}
				});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				NewsItem newsItem = (NewsItem) arg0.getItemAtPosition(arg2);
				if (newsItem != null) {
					mNewsActivityManagement.startDetailActivity(newsItem);
				}
			}
		});
		if (getIntent().getExtras().containsKey(SchoolView.TITLE)) {
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		} else {
			setTitleText(ModuleNameDefine.getNewsModuleNamebyId(category));
		}
	}

	/**
	 *
	 // 请求最新的数据
	 *
	 *
	 * @param lastupdate
	 *            最后更新时间,0为刷新
	 * @param count
	 *            请求的数量，
	 */

	private void requestNewsData(final String lastupdate, final int count) {
		// 网络不通时,返回
		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) {
			mListView.onRefreshComplete();
			return;
		}
		mListView.setOnloadingRefreshVisible();
		showProgressBar();
		// 則从网络上下载数据
		mNewsActivityManagement.sendGetNeededInfo(mHandler, category, lastupdate);
	}

	/**
	 * 更新UI
	 */
	private void refreshUI() {
		showProgressBar();
		data.clear();
		data.addAll(mNewsActivityManagement.getDataList(category, newsCount));

		if(mNewsActivityManagement.isExistAnyNews(category)) {
			mAdapter.notifyDataSetChanged();
			
			// 存在至少一条记录,显示界面
//			mListView.setVisibility(View.VISIBLE);
			// 列表头
			mListView.hideHeadView();
		}
		else {
//			mListView.setVisibility(View.VISIBLE);
			mListView.setHeadViewVisible();
			for (int i = 0; i < mListView.getFooterViewsCount(); i++) {
				// 没有记录，隐藏更多选项
				mListView.removeFooterView(mFooterView);
			}
		}

		

		if (newsCount > mNewsActivityManagement.getDataCount()) {// 没有取到更多数据，隐藏更多选项
			for (int i = 0; i < mListView.getFooterViewsCount(); i++) {
				mListView.removeFooterView(mFooterView);
			}
		} else {
			for (int i = 0; i < mListView.getFooterViewsCount(); i++) {// 取到了足够数据，显示更多选项
				mListView.removeFooterView(mFooterView);
			}
			mListView.addFooterView(mFooterView);
		}

		mListView.onRefreshComplete();

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

	protected void onDestroy() {
		data.clear();
		mNewsActivityManagement.sendReflashUnreadsumBroadcast();
		
		super.onDestroy();
	}

}
