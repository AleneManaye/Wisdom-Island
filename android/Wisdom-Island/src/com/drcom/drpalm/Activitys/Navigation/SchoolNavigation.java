package com.drcom.drpalm.Activitys.Navigation;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.Navigation.NavigationListAdapter.OnBookmarkBtnClickListener;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.Activitys.web.WebviewActivity;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.Tool.nettool.NetStatusManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.View.Navigation.SchoolNavigationManagetment;
import com.drcom.drpalm.View.controls.CornerListView;
import com.drcom.drpalm.View.controls.CornerListView.OnCornerChanged;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NavigationItem;
import com.wisdom.island.R;

/**
 * 选地区
 * 
 * @author MCX
 * 
 */
public class SchoolNavigation extends ModuleActivity {

	public static final String NAVIGATION_PARENT_ID = "parent_id";
	public static final String NAVIGATION_PARENT_TYPE = "parent_type";
	public static final String KEY_FROMMAIN = "KEY_FROMMAIN";
	public static final int GET_SCHOOLLIST_START = 0;
	public static final int GET_SCHOOLLIST_SUCCESSFUL = 1;
	public static final int GET_SCHOOLLIST_FAILED = 2;
	public static final String FLAG = "flag";
	public static final String ACTION = "com.drcom.drpalm.Activitys.Navigation";
	public static final String SCHOOLKEY = "schoolkey";
	public static final String SCHOOLNAME = "schoolname";

	private CornerListView mBookmarkListView;
	private CornerListView mNavigationListView;
	// private NavigationListviewAdapter mBookmarkAdapter;
	// private NavigationListviewAdapter mNavigationAdapter;
	private NavigationListAdapter mBookmarkAdapter;
	private NavigationListAdapter mNavigationAdapter;
	private NavigationDB mNavigationDB;
	// private Cursor mBookmarkCursor = null;
	// private Cursor mNavigationCursor = null;
	private List<NavigationItem> mBookmarkList = new ArrayList<NavigationItem>();
	private List<NavigationItem> mNavigationList = new ArrayList<NavigationItem>();
	// private TextView mHeaderView = null;
	private LinearLayout mHeaderView = null;
	private LinearLayout mDivider; // 分割线

	private int mParentID = -1;

	private boolean isFormMain = false; // 是否从ManinActivity返回

	// private RequestOperation mRequestOperation =
	// RequestOperation.getInstance();

	private ProgressDialog m_pDialog;

	private GroupReceiver mGroupReceiver; // Receiver

	/* ebaby channel webview */
	private WebView mEbabyView;

	private SchoolNavigationManagetment mSchoolNavigationManagetment;
	private Handler uiHandler = null;

	private String parent_type = "";

	private String mFlag = "";
	private Button mBtn_search;
	private CurrentLan mCurrentLan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.navigation_main);
		// mLayout_body.setBackgroundResource(R.drawable.login_bg);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.navigation_main, mLayout_body);
		hideToolbar();
		// hideBackButton();
		setHasGestureToClose(false);

		Log.i("test circle", "onCreate");
		Intent intent = getIntent();
		if (null != intent) {
			Bundle bundle = intent.getExtras();
			if (null != bundle) {
				if (bundle.containsKey(NAVIGATION_PARENT_ID)) {
					mParentID = Integer.valueOf(bundle
							.getString(NAVIGATION_PARENT_ID));
				}
				if (bundle.containsKey(KEY_FROMMAIN)) {
					isFormMain = bundle.getBoolean(KEY_FROMMAIN);
				}
				if (bundle.containsKey(NAVIGATION_PARENT_TYPE)) {
					parent_type = bundle.getString(NAVIGATION_PARENT_TYPE);
				}
				if (bundle.containsKey(FLAG)) {
					mFlag = bundle.getString(FLAG);
				}
			}
		}

		initDialogSearchButton();
		initProgressBar();
		initHandler();

		mSchoolNavigationManagetment = new SchoolNavigationManagetment(this);

		mNavigationDB = NavigationDB.getInstance(this);
		mBookmarkListView = (CornerListView) findViewById(R.id.bookmarklistview);
		mBookmarkListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {
						/* 打开指定学校的APP 应用 */
						// Cursor schoolCursor = (Cursor)
						// listView.getItemAtPosition(position);
						// NavigationItem schoolItem =
						// mNavigationDB.retrieveNavigationItem(schoolCursor);
						NavigationItem schoolItem = mBookmarkList.get(position);
						if (schoolItem != null) {
							ShowLoadingDialog();
							mSchoolNavigationManagetment
									.openAppBySchoolkey(schoolItem);
						}
					}
				});
		mNavigationListView = (CornerListView) findViewById(R.id.navigationlistview);
		mNavigationListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {

						/* 导航页多级分层处理 */
						// Cursor navigateCursor = (Cursor)
						// listView.getItemAtPosition(position);
						// NavigationItem navigateItem =
						// mNavigationDB.retrieveNavigationItem(navigateCursor);
						NavigationItem navigateItem = mNavigationList
								.get(position);
						//判读是从哪里进来的（true为账号关联进来的）
						if (mFlag.equals(FLAG)) {
							String schoolName = "";
							mCurrentLan = LanguageManagement.getSysLanguage(SchoolNavigation.this);
							if (mCurrentLan == CurrentLan.SIMPLIFIED_CHINESE) {
								schoolName = navigateItem.text_s;
							} else if (mCurrentLan == CurrentLan.COMPLES_CHINESE) {
								schoolName = navigateItem.text_t;
							} else if (mCurrentLan == CurrentLan.ENGLISH) {
								schoolName = navigateItem.text_en;
							}

							Intent intent = new Intent(ACTION);
							intent.putExtra(SCHOOLKEY, navigateItem.key);
							intent.putExtra(SCHOOLNAME, schoolName);
							sendBroadcast(intent);
							SchoolNavigation.this.finish();
							
						} else {
							if (navigateItem.type.equals("local")) {
								mSchoolNavigationManagetment
										.enterSubNavigation(navigateItem);
							} else if (navigateItem.type.equals("school")) {
								if (navigateItem != null) {
									ShowLoadingDialog();
									mSchoolNavigationManagetment
											.openAppBySchoolkey(navigateItem);
								}
							}
						}
					}
				});
		mNavigationListView.setOnCornerChanged(new OnCornerChanged() {

			@Override
			public void changeCorner(int number) {
				// TODO Auto-generated method stub
				if (number == 0) {
					if (number == (mNavigationListView.getAdapter().getCount() - 1)) {
						mNavigationListView
								.setSelector(R.drawable.app_list_corner_round_bottom);
					} else {
						mNavigationListView
								.setSelector(R.drawable.app_list_corner_shape);
					}
				} else if (number == (mNavigationListView.getAdapter()
						.getCount() - 1))
					mNavigationListView
							.setSelector(R.drawable.app_list_corner_round_bottom);
				else {
					mNavigationListView
							.setSelector(R.drawable.app_list_corner_shape);
				}
			}
		});
		mHeaderView = (LinearLayout) findViewById(R.id.navigation_title);
		// mHeaderView.setText(getParentName());
		mHeaderView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new
				// Intent(SchoolNavigation.this,WebviewActivity.class);
				// intent.putExtra(WebviewActivity.URL_KEY,
				// GlobalVariables.URL_EBABYCHANNEL + GlobalVariables.Devicdid);
				// startActivity(intent);
			}
		});
		mDivider = (LinearLayout) findViewById(R.id.divider);

		mEbabyView = (WebView) findViewById(R.id.ebaby_channel_webveiw);
		mEbabyView.setScrollbarFadingEnabled(false);
		mEbabyView.setHorizontalScrollBarEnabled(false);
		mEbabyView.setVerticalScrollBarEnabled(false);
		// mEbabyView.loadUrl(GlobalVariables.URL_ERROR);
		if (NetStatusManager.getSettingManager(this).GetNetType() != NetStatusManager.NetType.NOTCONNECT) {// IsNetUsed(this)){
			mEbabyView.loadUrl(GlobalVariables.URL_NAVIGATION_EBABYCHANNEL
					+ GlobalVariables.Devicdid);
		}
		// 启用JS
		WebSettings webSettings = mEbabyView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mEbabyView.setVisibility(View.GONE);
		// View includeView = findViewById(R.id.include);
		// add by hzy 14-6-11隐藏资讯频道
		if (mFlag.equals(FLAG)) {
			mHeaderView.setVisibility(View.GONE);
		}

		mEbabyView.setWebViewClient(new WebViewClient() {
			// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// view.loadUrl(url);
				Intent intent = new Intent(SchoolNavigation.this,
						WebviewActivity.class);
				intent.putExtra(WebviewActivity.URL_KEY, url);
				startActivity(intent);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mEbabyView.setVisibility(View.VISIBLE);
				mHeaderView.setBackgroundResource(R.drawable.shape_bg_corners);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				// super.onReceivedError(view, errorCode, description,
				// failingUrl);
				// view.loadUrl(GlobalVariables.URL_ERROR);
				mEbabyView.setVisibility(View.GONE);
				mHeaderView
						.setBackgroundResource(R.drawable.connerlist_header_selector);
			}
		});

		// if(mParentID == 0){
		// initData();
		// }
		initialCursorAdapter();
		setListViewHeightBasedOnChildren(mBookmarkListView);
		setListViewHeightBasedOnChildren(mNavigationListView);

		// 实现点选收藏时同步刷新界面
		if (mBookmarkAdapter != null) {
			mBookmarkAdapter
					.setOnBookmarkBtnClickListener(new OnBookmarkBtnClickListener() {

						@Override
						public void onClick() {
							// TODO Auto-generated method stub
							refreshListview();
						}
					});
		}
		if (mNavigationAdapter != null) {
			mNavigationAdapter
					.setOnBookmarkBtnClickListener(new OnBookmarkBtnClickListener() {

						@Override
						public void onClick() {
							// TODO Auto-generated method stub
							refreshListview();
						}
					});
		}

		mSchoolNavigationManagetment.getSchoolList(mParentID, uiHandler);
		// if((parent_type != null)&&(!parent_type.equals(""))){
		// SharedPreferences preference = getSharedPreferences("default_school",
		// Context.MODE_PRIVATE);
		// String school_key = preference.getString("school_key", "");
		// if(isFormMain)
		// school_key = "";
		// if(!school_key.equals("")){
		// NavigationItem item = mNavigationDB.getSchoolItem(school_key);
		// if(item.key != null){
		// mSchoolNavigationManagetment.openAppBySchoolkey(item);
		// }
		// }
		// }

		setTitleLogo(BitmapFactory.decodeResource(getResources(),
				R.drawable.defaulttitlelogo));

		initReceiver();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("test circle", "onRestart");
		refreshListview();
		HideLoadingDialog();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("test circle", "onResume");
		HideLoadingDialog();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		HideLoadingDialog();
		super.onStop();
		Log.i("test circle", "onStop");
	}

	private void initialCursorAdapter() {
		// if(mParentID == 0){
		mBookmarkList = mNavigationDB.getBookmarkItems(null,
				LanguageManagement.getSysLanguage(SchoolNavigation.this));
		if (mBookmarkList.size() > 0) {
			mBookmarkListView.setVisibility(View.VISIBLE);
		} else {
			mBookmarkListView.setVisibility(View.GONE);
		}
		if (mBookmarkAdapter == null) {
			// mBookmarkAdapter = new NavigationListviewAdapter(this,
			// mBookmarkCursor);
			mBookmarkAdapter = new NavigationListAdapter(this, mBookmarkList);
			mBookmarkListView.setAdapter(mBookmarkAdapter);
		} else {
			mBookmarkAdapter.changeSourceData(mBookmarkList);
			mBookmarkAdapter.notifyDataSetChanged();
		}
		// }else{
		// mBookmarkListView.setVisibility(View.GONE);
		// }

		mNavigationList = mNavigationDB.getNavigationItems(mParentID, null,
				LanguageManagement.getSysLanguage(SchoolNavigation.this));
		if (mNavigationList.size() > 0) {
			// 如果是正式版本,排除带(DR_COM)字样的学校
			if (!GlobalVariables.getAppIsTestversion()) {
				List<NavigationItem> tempschoollist = new ArrayList<NavigationItem>();
				for (int i = 0; i < mNavigationList.size(); i++) {
					if (mNavigationList.get(i).name.indexOf("DR_COM") > -1) {
						tempschoollist.add(mNavigationList.get(i));
					}
				}
				mNavigationList.removeAll(tempschoollist);
			}

			mNavigationListView.setVisibility(View.VISIBLE);
			mDivider.setVisibility(View.VISIBLE);
		} else {
			mNavigationListView.setVisibility(View.GONE);
			mDivider.setVisibility(View.GONE);
		}
		if (mNavigationAdapter == null) {
			mNavigationAdapter = new NavigationListAdapter(this,
					mNavigationList);
			mNavigationListView.setAdapter(mNavigationAdapter);
		} else {
			mNavigationAdapter.changeSourceData(mNavigationList);
			mNavigationAdapter.notifyDataSetChanged();
		}
	}

	// private String getParentName(){
	// String parentName = "";
	// if(mParentID == 0){
	// parentName = getResources().getString(R.string.navigation_default_title);
	// }else if(mParentID > 0){
	// parentName = mNavigationDB.getNameByID(mParentID);
	// }
	// return parentName;
	// }

	private void setListViewHeightBasedOnChildren(ListView listView) {// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		HideLoadingDialog();
		unregisterReceiver(mGroupReceiver);
		if (mBookmarkList != null) {
			mBookmarkList.clear();
			mBookmarkList = null;
		}
		if (mNavigationList != null) {
			mNavigationList.clear();
			mNavigationList = null;
		}

		// int version = android.os.Build.VERSION.SDK_INT;
		// ActivityManager activityMgr = (ActivityManager) SchoolNavigation.this
		// .getSystemService(Context.ACTIVITY_SERVICE);
		// if (version <= 7) {
		// activityMgr.restartPackage(getPackageName());
		// } else {
		// System.exit(0);
		// }

		super.onDestroy();
	}

	// 刷新界面
	private void refreshListview() {
		initialCursorAdapter();
		setListViewHeightBasedOnChildren(mBookmarkListView);
		setListViewHeightBasedOnChildren(mNavigationListView);
	}

	private void sendUpdateFail(String strError) {
		Intent sendIntent = new Intent(ActivityActionDefine.NOTIFICATION_ACTION);
		sendIntent.putExtra(ActivityActionDefine.NOTIFICATION_TIP, this
				.getResources().getString(R.string.update_fail));
		sendBroadcast(sendIntent);
	}

	private void initDialogSearchButton() {
		mBtn_search = new Button(this);
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, 32),
				MyMothod.Dp2Px(this, 32));
		mBtn_search.setLayoutParams(p);
		mBtn_search.setBackgroundResource(R.drawable.search_btn_selector);
		mBtn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SchoolNavigation.this,
						searchSchoolActivity.class);
				intent.putExtra(FLAG, FLAG);
				startActivity(intent);
			}
		});
		setTitleRightButton(mBtn_search);

		Button refreshBtn = new Button(this);
		LinearLayout.LayoutParams p_refresh = new LinearLayout.LayoutParams(
				MyMothod.Dp2Px(this, 32), MyMothod.Dp2Px(this, 32));
		p_refresh.setMargins(MyMothod.Dp2Px(this, 5), 0, 0, 0);
		refreshBtn.setLayoutParams(p_refresh);
		refreshBtn.setBackgroundResource(R.drawable.refresh_btn_selector);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSchoolNavigationManagetment
						.getSchoolList(mParentID, uiHandler);
			}
		});
		setTitleRightButton(refreshBtn);
		hideProgressBar();
	}

	/**
	 * 返回按钮事件
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// if(mParentID == 0){
			// if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			// //提示框退出
			// showExitMessage(getResources().getString(R.string.exit_sure2));
			// }
			// }
			finish();
		}
		return true;
	}

	/**
	 * 退出提示框(系统)
	 * 
	 * @param pMsg
	 */
	private void showExitMessage(String pMsg) {
		AlertDialog.Builder builder = new Builder(SchoolNavigation.this);
		builder.setMessage(pMsg);
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Exit();
					}
				});

		builder.setNegativeButton(R.string.Cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * 退出程序
	 */
	private void Exit() {
		// int version = android.os.Build.VERSION.SDK_INT;
		// ActivityManager activityMgr = (ActivityManager) SchoolNavigation.this
		// .getSystemService(Context.ACTIVITY_SERVICE);
		// if (version <= 7) {
		// activityMgr.restartPackage(getPackageName());
		// } else {
		// System.exit(0);
		// }
		// add by menchx 解决导航登录状态下返回异常问题
		Intent exitIntent = new Intent();
		exitIntent.setAction(ActivityActionDefine.EXITAPP_ACTION);
		sendBroadcast(exitIntent);
		finishDraw();
	}

	/**
	 * initialize receiver
	 */
	private void initReceiver() {
		mGroupReceiver = new GroupReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActivityActionDefine.EXITAPP_ACTION);
		filter.addAction(ACTION);
		registerReceiver(mGroupReceiver, filter);
	}

	/**
	 * *************** 广播接收 ***************
	 */
	public class GroupReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String stringAction = intent.getAction();
				Log.i("zjj", "stringAction:" + stringAction);

				if (stringAction.equals(ActivityActionDefine.EXITAPP_ACTION)) {
					finishDraw();
				}
				if(stringAction.equals(ACTION)){
					SchoolNavigation.this.finish();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	private void initProgressBar() {
		if (m_pDialog == null) {
			m_pDialog = new ProgressDialog(this);
		}
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pDialog.setMessage(getResources().getString(R.string.loading));
		m_pDialog.setIndeterminate(false);
		m_pDialog.setCancelable(true);
	}

	private void initHandler() {
		uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.arg1 == GET_SCHOOLLIST_START) {
					showProgressBar();
				} else if (msg.arg1 == GET_SCHOOLLIST_SUCCESSFUL) {
					hideProgressBar();
					refreshListview();
				} else if (msg.arg1 == GET_SCHOOLLIST_FAILED) {
					hideProgressBar();
					String strError = (msg.obj != null) ? (String) msg.obj : "";
					new ErrorNotificatin(SchoolNavigation.this)
							.showErrorNotification(strError);
				}
			}

		};
	}
}
