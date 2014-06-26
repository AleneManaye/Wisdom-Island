package com.drcom.drpalm.Activitys.main;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.Navigation.NavigationMainActivity;
import com.drcom.drpalm.Activitys.Navigation.SchoolNavigation;
import com.drcom.drpalm.Activitys.login.LoginActivity;
import com.drcom.drpalm.DB.UpdateTimeDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.AppPassDataHelper;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.Tool.ResourceManagement;
import com.drcom.drpalm.Tool.XmlParse.SaxReadxml;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.MySrcollTabhost;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.login.LoginManager.LoginCallback;
import com.drcom.drpalm.View.login.LoginManager.OnlineStatus;
import com.drcom.drpalm.View.main.AppUpdateManagement;
import com.drcom.drpalm.View.main.MainActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.ToursItem;
import com.drcom.drpalm.objs.UpdateTimeItem;
import com.wisdom.island.R;

public class MainActivity extends ModuleActivity {
	private int GETNG_SUCCDDE = 1;
	private int GETNG_FAILED = 0;

	private int GETNG_NEWS_SUCCDDE = 1;
	private int GETNG_NEWS_EVENTS_SUCCDDE = 2;

	private int NEWS_INDEX = 0;		// 新闻所在页面
	private int EVENTS_INDEX = 1; 	// 通告所在页面
	private int MORE_INDEX = 2; 	// 更多所在页面

	private String KEY_REFLASHTIME = "mainflashtime";
	private String KEY_UNREADEVENTS = "KEY_UNREADEVENTS"; // 未读通告数
	public static String KEY_PUSHSTART = "KEY_PUSHSTART"; // 是否从PUSH启动
	public static String KEY_BINDCOUNTSTART = "KEY_BINDCOUNTSTART";	//是否从帐号关联进入
	//从外部应用调用时传递参数
//	public static String KEY_START2SCHOOL = "KEY_START2SCHOOL";	//启动时,中转到校园界面 	Boolean
//	public static String KEY_START2CLASS = "KEY_START2CLASS";	//启动时,中转到班级界面	Boolean
//	public static String KEY_START2MORE = "KEY_START2MORE";		//启动时,中转到更多界面	Boolean
//	public static String KEY_START_SCHOOLKEY = "KEY_START_SCHOOLKEY";	//启动时,传入指定SCHOOLKEY	String
//	public static String KEY_START_USERNAME = "KEY_START_USERNAME";		//启动时,传入用户名	String
//	public static String KEY_START_PASSWORD = "KEY_START_PASSWORD";		//启动时,传入密码	String
	

	// 控件
	private MySrcollTabhost srcolltabhost;
	private SchoolView schoolview;
	private ClassView classview;
	private MoreView moreview;
	private View v1;
	private View v2;
	private View v3;
	private ProgressDialog mDialogAutologin;
	private Button btnAdd; // 最新按钮

	// 变量
	private RequestOperation mRequestOperation = RequestOperation.getInstance();
//	private String[] moduleNames; // 模块名
	private LoginManager mLoginManager ;//= LoginManager.getInstance(GlobalVariables.gAppContext);
	private GroupReceiver mGroupReceiver; // Receiver
	private ResourceManagement mResourceManagement;
	private UpdateTimeDB mUpdateTimeDB;
//	private SharedPreferences sp; // 15分钟间隔自动刷新
//	private Editor editor;
//	private Date lastrefreshTime;
	private SettingManager setInstance;
	private boolean mIsPushStart = false; // 是否从PUSH通知栏启动
	private boolean mIsBindaccoutnStart = false; // 是否从帐号关联进入
	// private boolean mIsFirstTimeLogin = true; //是否首次启动
	private int mEventsNotReadSum = 0; // 通告未读总数
	private List<UpdateTimeItem> mEventsUnreadSumlist;
	// 下载标识
	private boolean isNotifyUpgrade = false;
	
	private MainActivityManagement mMainActivityManagement;
	private AppUpdateManagement mAppUpdateManagement;
	//初始化跳转逻辑
	private int mStartPage = 1;	//从外部应用调用时跳转到指定页面
//	private String mUsername = "";	//用户名
//	private String mPassword = "";	//密码

    private boolean isLoginShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i("DEBUG_LIANG","ONCREATE");
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_view, mLayout_body);
		
		mMainActivityManagement = new MainActivityManagement(MainActivity.this);
		mAppUpdateManagement = new AppUpdateManagement(MainActivity.this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//外部程序传入SchoolKey
			if(extras.containsKey(AppPassDataHelper.KEY_START_SCHOOLKEY)){
				GlobalVariables.gSchoolKey = extras.getString(AppPassDataHelper.KEY_START_SCHOOLKEY);
				Log.i("jjj", "--------外部程序传入SchoolKey-----------:" + GlobalVariables.gSchoolKey);
				
				mRequestOperation.initDB();
			}
			
			if(extras.containsKey(AppPassDataHelper.KEY_SCHOOL_PACKAGENAME)){
				GlobalVariables.mExternalPackageName = extras.getString(AppPassDataHelper.KEY_SCHOOL_PACKAGENAME);
				Log.i("jjj", "--------外部程序传入PackageName-----------:" + GlobalVariables.mExternalPackageName);
			}
			
			//本应用的PUSH
			if (extras.containsKey(KEY_PUSHSTART)) {
				mIsPushStart = extras.getBoolean(KEY_PUSHSTART);

				mMainActivityManagement.StartFromPush();
//				// //取默认学校SchoolKey
//				SharedPreferences preferences = getSharedPreferences("default_school", Context.MODE_PRIVATE);
//				String schoolkey = preferences.getString("school_key", "");
//				GlobalVariables.gSchoolKey = schoolkey;
//
//				mRequestOperation.initDB();
//
//				Log.i("zjj", "--------gSchoolKey-----------:" + schoolkey);
//
//				// 清除收到的PUSH提示(只清除本应用的)
//				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//				notificationManager.cancelAll();

			}//外部程序传入 跳转方式--->校园
			else if(extras.containsKey(AppPassDataHelper.KEY_START2SCHOOL)) {
				mStartPage = NEWS_INDEX;
			}//外部程序传入 跳转方式--->班级
			else if(extras.containsKey(AppPassDataHelper.KEY_START2CLASS)) {
				mStartPage = EVENTS_INDEX;
//				if(extras.containsKey(KEY_START_USERNAME)) {
//					mUsername = extras.getString(KEY_START_USERNAME);
//				}
//				if(extras.containsKey(KEY_START_PASSWORD)) {
//					mPassword = extras.getString(KEY_START_PASSWORD);
//				}
				if(extras.containsKey(AppPassDataHelper.LOGIN_SUCCESS_BUNDLE_KEY)){
					Bundle testBundle = extras.getBundle(AppPassDataHelper.LOGIN_SUCCESS_BUNDLE_KEY);
					UserInfo myUserInfo = new UserInfo();
					myUserInfo = new AppPassDataHelper().unpackAppData(testBundle);
					
					//外部程序登录成功所生成的Tokenid(所有请求用到,也影响push)
					GlobalVariables.gSessionKey = myUserInfo.sessionKey;
					
					Log.i("jjj", "--------外部程序传入UserInfo:strUsrName-----------:" + myUserInfo.bAutoLogin);
					
					mMainActivityManagement.mIsExternalLogin = true;
					mMainActivityManagement.mUserInfo = myUserInfo;
					
					//外部程序是从PUSH启动
					if(extras.containsKey(AppPassDataHelper.KEY_START_FROM_PUSH)){
						mIsPushStart = extras.getBoolean(AppPassDataHelper.KEY_START_FROM_PUSH);
						if(mIsPushStart)
							mMainActivityManagement.StartFromPush();
					}
				}
			}//外部程序传入 跳转方式--->更多
			else if(extras.containsKey(AppPassDataHelper.KEY_START2MORE)){
				mStartPage = MORE_INDEX;
			}
			
			if(extras.containsKey(KEY_BINDCOUNTSTART)){
				mIsBindaccoutnStart = true;
			}
		}

		if(GlobalVariables.getAppDefaultSchoolKey()){
			/**
			 * 重新初始化DB接口，防止学校切换导致写入同一个库的问题（由于service绑定Application启动，导致先于获得schoolkey）
			 */
			mRequestOperation.initDB();
		}
		
//		//
//		SettingManager.Destroy();	//先清空静态变量,以免BUG后状态不会清除
//		LoginManager.destroy();		
//		//
//		setInstance = SettingManager.getSettingManager(MainActivity.this);
//		instance = LoginManager.getInstance(GlobalVariables.gAppContext);
//		mUpdateTimeDB = UpdateTimeDB.getInstance(this, GlobalVariables.gSchoolKey);
//		mResourceManagement = new ResourceManagement();//.getResourceManagement();
		
		mMainActivityManagement.init();
		setInstance = mMainActivityManagement.setInstance;
		mLoginManager = mMainActivityManagement.instance;
		mUpdateTimeDB = mMainActivityManagement.mUpdateTimeDB;
		mResourceManagement = mMainActivityManagement.mResourceManagement;
		
		
		hideToolbar();
		initTitlebar();
		setHasGestureToClose(false);
		
//		//
//		sp = this.getSharedPreferences(KEY_REFLASHTIME, MODE_WORLD_READABLE);
//		editor = sp.edit();
//		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));

		// 控件
		schoolview = new SchoolView(MainActivity.this);
		classview = new ClassView(MainActivity.this);
		moreview = new MoreView(MainActivity.this);
//		classview.setHandler(mHandlerClassview);

		v1 = View.inflate(this, R.layout.tabview, null);
		v2 = View.inflate(this, R.layout.tabview, null);
		v3 = View.inflate(this, R.layout.tabview, null);
		if(LanguageManagement.getSysLanguage(MainActivity.this) == CurrentLan.COMPLES_CHINESE){
			v1.setBackgroundResource(R.drawable.tab_icon_shool_hk_selector);
			v2.setBackgroundResource(R.drawable.tab_icon_class_lock_hk_selector);
			v3.setBackgroundResource(R.drawable.tab_icon_more_hk_selector);
		}else if(LanguageManagement.getSysLanguage(MainActivity.this) == CurrentLan.ENGLISH){
			v1.setBackgroundResource(R.drawable.tab_icon_shool_en_selector);
			v2.setBackgroundResource(R.drawable.tab_icon_class_lock_en_selector);
			v3.setBackgroundResource(R.drawable.tab_icon_more_en_selector);
		}else{
			v1.setBackgroundResource(R.drawable.tab_icon_shool_selector);
			v2.setBackgroundResource(R.drawable.tab_icon_class_lock_selector);
			v3.setBackgroundResource(R.drawable.tab_icon_more_selector);
		}
		
		srcolltabhost = (MySrcollTabhost) findViewById(R.id.mySrcollTabhost);
		srcolltabhost.addView(schoolview, v1);
		srcolltabhost.addView(classview, v2);
		srcolltabhost.addView(moreview, v3);
		srcolltabhost.AddOnSelectTabListener(EVENTS_INDEX, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Srcoll2Classview();
			}
		});
		srcolltabhost.AddOnSelectTabListener(NEWS_INDEX, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Srcoll2SchoolView();
			}
		});

		srcolltabhost.AddOnSelectTabListener(MORE_INDEX, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				srcolltabhost.SetCurrentTab(MORE_INDEX);

				if (btnAdd != null)
					btnAdd.setVisibility(View.GONE);
			}
		});

		srcolltabhost.SetCurrentTab(mStartPage);
		
		//
		// GlobalVariables.showLoading(MainActivity.this);
//		GetNetworkGate();
		mMainActivityManagement.GetNetworkGate(mHandler);

		initReceiver();
		
	}

	@Override
	protected void onDestroy() {
        Log.i("DEBUG_LIANG","ONDESTORY");
		unregisterReceiver(mGroupReceiver);
//		// 一定要销毁这两个对像,否则重进系统/BUG之后不会NEW
//		LoginManager.destroy();
//		SettingManager.Destroy();
//		
//		//释放内存(把占用内存大的变量致空,减少BUG的机率)
//		instance = null;
//		setInstance = null;
//		mResourceManagement = null;
//		
//		mRequestOperation = null;
//		
//		System.gc();
		
		mAppUpdateManagement.Destory();
		mMainActivityManagement.Destory();
		mMainActivityManagement = null;
		srcolltabhost = null;
		
		//句柄释放(要及时释放和使用时要判空，不然小米容易出问题)
		mHandlerNewmsg = null;
		mHandlerAutologin = null;
		
		System.gc();
		super.onDestroy();
	};

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("DEBUG_LIANG","ONSTOP");
    }

    @Override
	protected void onRestart() {
		// TODO Auto-generated method stub
        Log.i("DEBUG_LIANG","ONRESTART");
		super.onRestart();

		mMainActivityManagement.getDefaultSchoolid();

		//
		// //如果已经登录/程序已启动
		// if(mIsPushStart && instance.getOnlineStatus() ==
		// OnlineStatus.ONLINE_LOGINED){
		// OpenTheNewEvents();
		// }else if(mIsPushStart && instance.getOnlineStatus() !=
		// OnlineStatus.ONLINE_LOGINED){
		// Srcoll2Classview();
		// }
	}

	private void initTitlebar() {
		setTitleLogo(BitmapFactory.decodeResource(getResources(), R.drawable.defaulttitlelogo));

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));

		btnAdd = new Button(this);
		btnAdd.setLayoutParams(p);
		btnAdd.setBackgroundResource(R.drawable.btn_title_blue_selector);
		btnAdd.setText(getString(R.string.unreadmsg));
		btnAdd.setTextAppearance(MainActivity.this, R.style.TitleBtnText);
		btnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (srcolltabhost.getSelectIndex() == NEWS_INDEX) {
					Intent intent = new Intent(MainActivity.this, SchoolNewsListActivity.class);
					startActivityForResult(intent, 1);
				} else {
					Intent intent = new Intent(MainActivity.this, ClassNewsListActivity.class);
					startActivityForResult(intent, 1);
				}
			}
		});

		// ToolbarAddRightButton(btnAdd);
//		setTitleRightButton(btnAdd);

		// 返回导航
		if(GlobalVariables.getAppDefaultSchoolKey()){
            hideBackButton();
			SetBackBtnOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 提示框退出
					showCustomMessageExit(getResources().getString(R.string.exit_sure2));
				}
			});
		}else{
			SetBackBtnBackgroundText(getString(R.string.btn_navigation));
			SetBackBtnOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 提示框退出
					// showExitMessage(getResources().getString(R.string.exit_sure2));
					if ((mLoginManager.getOnlineStatus() == OnlineStatus.ONLINE_LOGINED) || (mLoginManager.getOnlineStatus() == OnlineStatus.OFFLINE_LOGINED)) {
						showCustomMessageBack(getResources().getString(R.string.logout_sure));
					} else {
						finish();
					}
				}
			});
		}
		
		setTitleLogo(mResourceManagement.getBitmapFromFile(getResources().getString(R.string.title_icon)));	
		setTitleText(getString(R.string.app_name));
	}

	/**
	 * 转到校园TAB(新闻)
	 */
	private void Srcoll2SchoolView(){
		srcolltabhost.SetCurrentTab(NEWS_INDEX);

		if (btnAdd != null){
			btnAdd.setText(getString(R.string.unreadmsg));
			btnAdd.setVisibility(View.VISIBLE);
		}

		// 大于15分钟自动刷新
		if (mMainActivityManagement.isOver15mins()) {
			mMainActivityManagement.GetNewMsg(mHandlerNewmsg);
		}
	}
	
	/**
	 * 转到通告TAB
	 */
	private void Srcoll2Classview() {
//		Log.i("zjj", "转到通告TAB:" + instance.getOnlineStatus());
		if (LoginManager.OnlineStatus.OFFLINE == mLoginManager.getOnlineStatus()) {
            if(!isLoginShow) {
                isLoginShow = true;
                OpenLoginActivity();
            }
		} else {
			mLayout_body.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					srcolltabhost.SetCurrentTab(EVENTS_INDEX);
					
					if (btnAdd != null){
						btnAdd.setText(getString(R.string.newest));
						btnAdd.setVisibility(View.VISIBLE);
					}
				}
			});

			// 大于15分钟自动刷新
			if (mMainActivityManagement.isOver15mins()) {
				mMainActivityManagement.GetNewMsg(mHandlerNewmsg);
			}
		}
	}

	/**
	 * 打开最新通告
	 */
	private void OpenTheNewEvents() {
		Intent intent = new Intent(MainActivity.this, ClassNewsListActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 1);
		Log.i("zjj", "主界面打开最新通告");
		//
		mIsPushStart = false; // 用作标识是否从PUSH启动,从而打开最新5条通告.
								// 已打开最新5条通告,把此标识设置为false
	}

	/**
	 * 打开登录界面
	 */
	private void OpenLoginActivity(){
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 0);
	}
	
	/**
	 * 取网关
	 */
//	private void GetNetworkGate() {
//		mRequestOperation.GetNetworkGate(new RequestOperationCallback() {
//
//			@Override
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				Message message = Message.obtain();
//				message.arg1 = GETNG_SUCCDDE;
//				mHandler.sendMessage(message);
//			}
//
//			@Override
//			public void onError(String err) {
//				// TODO Auto-generated method stub
//				Message message = Message.obtain();
//				message.arg1 = GETNG_FAILED;
//				message.obj = err;
//				mHandler.sendMessage(message);
//			}
//		});
//	}

	/**
	 * 获取活动和新闻模块最新更新时间
	 */
//	private void GetNewMsg() {
//		if (instance.getOnlineStatus() == OnlineStatus.ONLINE_LOGINED) {
//			RequestOperation mRequestOperation = RequestOperation.getInstance();
//			RequestOperationCallback callback = new RequestOperationCallback() {
//
//				@Override
//				public void onSuccess() {
//					// TODO Auto-generated method stub
//					Message message = Message.obtain();
//					message.arg1 = GETNG_NEWS_EVENTS_SUCCDDE;
//					mHandlerNewmsg.sendMessage(message);
//				}
//
//				@Override
//				public void onError(String err) {
//					// TODO Auto-generated method stub
//					Message message = Message.obtain();
//					message.arg1 = GETNG_FAILED;
//					message.obj = err;
//					mHandlerNewmsg.sendMessage(message);
//				}
//			};
//			mRequestOperation.sendGetNeededInfo("GetEventsLastUpdate", new Object[] { callback }, callback.getClass().getName());
//		} else if (instance.getOnlineStatus() == OnlineStatus.OFFLINE) {
//			RequestOperation mRequestOperation = RequestOperation.getInstance();
//			RequestOperationCallback callback = new RequestOperationCallback() {
//
//				@Override
//				public void onSuccess() {
//					// TODO Auto-generated method stub
//					Message message = Message.obtain();
//					message.arg1 = GETNG_NEWS_SUCCDDE;
//					mHandlerNewmsg.sendMessage(message);
//				}
//
//				@Override
//				public void onError(String err) {
//					// TODO Auto-generated method stub
//					Message message = Message.obtain();
//					message.arg1 = GETNG_FAILED;
//					message.obj = err;
//					mHandlerNewmsg.sendMessage(message);
//				}
//			};
//			mRequestOperation.sendGetNeededInfo("GetNewsLastUpdate", new Object[] { callback }, callback.getClass().getName());
//		}
//	}

	/**
	 * 自动登录
	 */
	private void Autologin() {
		UserInfo usrinfo = setInstance.getCurrentUserInfo();
		if (usrinfo.bAutoLogin || mIsBindaccoutnStart) {
			ShowAutologinDialog();
			
			//从帐号关联进入
			if(mIsBindaccoutnStart){
				mLoginManager.loginOnline(GlobalVariables.gTempUsermane, GlobalVariables.gTempPW, new LoginCallback() {
					@Override
					public void onLoginSuccess() {
						Message message = Message.obtain();
						message.arg1 = GETNG_SUCCDDE;
						mHandlerAutologin.sendMessage(message);

					}

					@Override
					public void onLoginError(String strError) {
						Message message = Message.obtain();
						message.arg1 = GETNG_FAILED;
						message.obj = strError;
						mHandlerAutologin.sendMessage(message);
					}
				});
				
				GlobalVariables.gTempUsermane = "";
				GlobalVariables.gTempPW = "";
			}else{
				mLoginManager.loginOnline(usrinfo.strUsrName, usrinfo.strPassword, new LoginCallback() {
					@Override
					public void onLoginSuccess() {
						Message message = Message.obtain();
						message.arg1 = GETNG_SUCCDDE;
						mHandlerAutologin.sendMessage(message);

					}

					@Override
					public void onLoginError(String strError) {
						Message message = Message.obtain();
						message.arg1 = GETNG_FAILED;
						message.obj = strError;
						mHandlerAutologin.sendMessage(message);
					}
				});
			}
			
		} else {
			// 有新消息模块
			mMainActivityManagement.GetNewMsg(mHandlerNewmsg);

			if (mIsPushStart) {
				Srcoll2Classview();
			}
		}
	}

	/**
	 * 自动登录等待框
	 */
	private void ShowAutologinDialog() {
		mDialogAutologin = new ProgressDialog(this);
		mDialogAutologin.setMessage(getResources().getString(R.string.autologinloading));
		mDialogAutologin.setIndeterminate(true);
		mDialogAutologin.setCancelable(true);
		mDialogAutologin.show();
	}

	/**
	 * 网关结果
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == GETNG_SUCCDDE) {
				// ////////////////////////资源包///////////////////////////////////////
				// 检测资源包是否要更新
				if(mResourceManagement != null)
					mResourceManagement.CheckResourceUpdate();

				//外部程序已登录
				if(mMainActivityManagement != null)
					mMainActivityManagement.ExternalLogin();
				
				// 自动登录
				if (mLoginManager.getOnlineStatus() != OnlineStatus.ONLINE_LOGINED 
						|| mIsBindaccoutnStart) {
					Autologin();
				}
			} else if (msg.arg1 == GETNG_FAILED) {
				String strError = (msg.obj != null) ? (String) msg.obj : "";
				new ErrorNotificatin(MainActivity.this).showErrorNotification(strError);
			}
			
			if(mResourceManagement != null)
				mResourceManagement.getSystemMsg();
		}
	};

	/**
	 * 新信息标识结果
	 */
	private Handler mHandlerNewmsg = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mMainActivityManagement == null){
				return;
			}
			
			// GlobalVariables.hideLoading();
			if (msg.arg1 == GETNG_NEWS_SUCCDDE) {
				schoolview.ReflashItemstatus(mUpdateTimeDB.getNewsUpdateFlag());
				
				mMainActivityManagement.SaveReflashtime();

				// 设置分页图标的数字
				List<Integer> moresumlist = mUpdateTimeDB.getMoreUpdateFlag();
				if (moresumlist.size() > 0) {
					srcolltabhost.setTabviewNum(MORE_INDEX, moresumlist.get(0));
				}

				// 通告未读数（上次的记录）
				srcolltabhost.setTabviewNum(EVENTS_INDEX, mMainActivityManagement.getEventUnreadSum());

			} else if (msg.arg1 == GETNG_NEWS_EVENTS_SUCCDDE) {
				schoolview.ReflashItemstatus(mUpdateTimeDB.getNewsUpdateFlag());

				String username = setInstance.getCurrentUserInfo().strUsrName;
				mEventsUnreadSumlist = mUpdateTimeDB.getEventsUpdateFlag(username);
				classview.ReflashItemstatus(mEventsUnreadSumlist);

				mMainActivityManagement.SaveReflashtime();

				// 设置分页图标的数字
				// 班级
				mEventsNotReadSum = 0;
				String unread = "0";
				for (int i = 0; i < mEventsUnreadSumlist.size(); i++) {
					unread = mEventsUnreadSumlist.get(i).update_unreadcount;
					unread = unread.equals("") ? "0" : unread;
					mEventsNotReadSum += Integer.valueOf(unread);
				}
				srcolltabhost.setTabviewNum(EVENTS_INDEX, mEventsNotReadSum);
				// 保存通告未读数
//				editor.putInt(KEY_UNREADEVENTS, mEventsNotReadSum);
//				editor.commit();
				mMainActivityManagement.SaveEventUnreadSum(mEventsNotReadSum);

				// 更多
				List<Integer> moresumlist = mUpdateTimeDB.getMoreUpdateFlag();
				if (moresumlist.size() > 0) {
					srcolltabhost.setTabviewNum(MORE_INDEX, moresumlist.get(0));
				}

			} else if (msg.arg1 == GETNG_FAILED) {
				// String strError = (msg.obj != null)?(String)msg.obj:"";
				// new
				// ErrorNotificatin(MainActivity.this).showErrorNotification(strError);
			}
			
			//如果是从帐号关联进入
//			if(mIsBindaccoutnStart)
//				Srcoll2Classview();
			
		}
	};

	/**
	 * 自动登录结果
	 */
	private Handler mHandlerAutologin = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// GlobalVariables.hideLoading();
			if (msg.arg1 == GETNG_SUCCDDE) {
				Log.i("zjj", "------自动登录成功---------:是否PUSH:" + mIsPushStart);
				if (mDialogAutologin != null)
					mDialogAutologin.dismiss();

				if (mIsPushStart) {
					OpenTheNewEvents();
					Srcoll2Classview();
				}

			} else if (msg.arg1 == GETNG_FAILED) {
				Log.i("zjj", "------自动登录失败---------");
				// 有新消息模块
				if(mHandlerNewmsg != null)
					mMainActivityManagement.GetNewMsg(mHandlerNewmsg);
				
				mDialogAutologin.dismiss();
			}
		}
	};

//	/**
//	 * 班级界面事件
//	 */
//	private Handler mHandlerClassview = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			// GlobalVariables.hideLoading();
//			if (msg.arg1 == ClassView.CHANGE_SUM) {
//				Integer dessum = (msg.obj != null) ? (Integer) msg.obj : 0;
//				ResetEventsNotReadSum(dessum);
//			}
//		}
//	};

	/**
	 * 更新通告页面未读数
	 * 
	 * @param dessum
	 */
	private void ResetEventsNotReadSum(int eventunreadsum) {
		mEventsNotReadSum = eventunreadsum;
		srcolltabhost.setTabviewNum(EVENTS_INDEX, eventunreadsum);

		// 保存通告未读数
//		editor.putInt(KEY_UNREADEVENTS, mEventsNotReadSum);
//		editor.commit();
		mMainActivityManagement.SaveEventUnreadSum(mEventsNotReadSum);
	}

	@Override
	// 当结果返回后判断并执行操作
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
        isLoginShow = false;
		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					if (extras.getInt(LoginActivity.RESULT_CODE_KEY) == LoginActivity.RESULT_CODE_FAILD) {
//						 srcolltabhost.SetCurrentTab(srcolltabhost.getForwardIndex());
						srcolltabhost.SetCurrentTab(0);
					} else if (extras.getInt(LoginActivity.RESULT_CODE_KEY) == LoginActivity.RESULT_CODE_LOGOUT_SUCCEED) {
						// tabHost.setCurrentTab(0);
					} else if (extras.getInt(LoginActivity.RESULT_CODE_KEY) == LoginActivity.RESULT_CODE_ONLINE_SUCCEED) {
						// tabHost.setCurrentTab(mTabTempIndex);
						if (mIsPushStart) {
							OpenTheNewEvents();
						}
						Srcoll2Classview();

					} else if (extras.getInt(LoginActivity.RESULT_CODE_KEY) == LoginActivity.RESULT_CODE_OFFLINE_SUCCEED) {
						// tabHost.setCurrentTab(mTabTempIndex);
						if (mIsPushStart) {
							OpenTheNewEvents();
						}
						Srcoll2Classview();

					}
				}
			} 
//			else if (requestCode == 1) {
//				Bundle moduleExtra = intent.getExtras();
//				if (moduleExtra != null) {
//					if (moduleExtra.containsKey(New5MsgActivity.JUMP_MODULE_ID)) {
//						srcolltabhost.SetCurrentTab(moduleExtra.getInt(New5MsgActivity.JUMP_MODULE_ID));
//					}
//				}
//			} 
			else if (requestCode == MoreView.REQUESTCODE_REFLASHWEB) {
				Log.i("zjj", "网页窗口关闭返回消息");
				moreview.ReflashUI();
				mMainActivityManagement.GetNewMsg(mHandlerNewmsg);
			}
		}
	}

	/**
	 * initialize receiver
	 */
	public void initReceiver() {
		mGroupReceiver = new GroupReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActivityActionDefine.DOWNLOADTOURS_ACTION);
		filter.addAction(ActivityActionDefine.LOGIN_STATUS_CHANGED);
		filter.addAction(ActivityActionDefine.NO_DOWNLOADTOURS_ACTION);
//		filter.addAction(ActivityActionDefine.UPGRADEAPP_ACTION); // 应用更新广播
//		filter.addAction(ActivityActionDefine.UPGRADEAPP_CANCEL_ACTION); // 取消应用更新广播
		filter.addAction(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC); // 通告未读数-1
		filter.addAction(ActivityActionDefine.NEWS_UNREAD_SUM_DESC); // 新闻未读数-1
		filter.addAction(ActivityActionDefine.LOGIN_AND_SHOWNEWEVENTS_ACTION); // 新闻未读数-1
		filter.addAction(ActivityActionDefine.EXITAPP_ACTION);	//退出程序
		filter.addAction(ActivityActionDefine.CHANGEUSERPIC_ACTION);	//更换头像成功
		filter.addAction(ActivityActionDefine.GET_NEW_MSG_ACTION);	//刷新主界面 未读数广播
		filter.addAction(ActivityActionDefine.SHOWLOGIN_ACTION);	//打开登录界面 广播
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
				Bundle extras = intent.getExtras();
				if (stringAction.equals(ActivityActionDefine.DOWNLOADTOURS_ACTION)) {
					Log.i("zjj", "需要更新资源信息包接收广播");
					List<ToursItem> list = null;
					if (extras.containsKey(ActivityActionDefine.TOURS_ITEM_LIST)) {
						list = extras.getParcelableArrayList(ActivityActionDefine.TOURS_ITEM_LIST);
					}

					mMainActivityManagement.UpdateResource(list, callBack());
//					if (list.size() > 0) {
//						Log.i("zjj", "更新资源信息包成功接收广播:" + list.get(0).name);
//						mResourceManagement.initResourceName(list.get(0).name + ".zip");
//						mResourceManagement.download(MainActivity.this, list.get(0).url, callBack());
//					}

				} else if (stringAction.equals(ActivityActionDefine.NO_DOWNLOADTOURS_ACTION)) {
					Log.i("zjj", "不需要更新资源信息包接收广播");
					if(mResourceManagement != null){
						setTitleLogo(mResourceManagement.getBitmapFromFile(getResources().getString(R.string.title_icon)));
						ReflashUIformRespkg();
					}

				} else if (stringAction.equals(ActivityActionDefine.LOGIN_STATUS_CHANGED)) {
					moreview.ReflashUI();
					classview.ReflashUI();

					if (LoginManager.OnlineStatus.OFFLINE == mLoginManager.getOnlineStatus()) {
						if(LanguageManagement.getSysLanguage(MainActivity.this) == CurrentLan.COMPLES_CHINESE){
							v2.setBackgroundResource(R.drawable.tab_icon_class_lock_hk_selector);
						}else if(LanguageManagement.getSysLanguage(MainActivity.this) == CurrentLan.ENGLISH){
							v2.setBackgroundResource(R.drawable.tab_icon_class_lock_en_selector);
						}else{
							v2.setBackgroundResource(R.drawable.tab_icon_class_lock_selector);
						}
					} else {
						//延时少少播欢迎动画
						classview.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								ShowWelcome(setInstance.getCurrentUserInfo().strUserNickName);
							}
						}, 800);
						
						if(LanguageManagement.getSysLanguage(MainActivity.this) == CurrentLan.COMPLES_CHINESE){
							v2.setBackgroundResource(R.drawable.tab_icon_class_hk_selector);
						}else if(LanguageManagement.getSysLanguage(MainActivity.this) == CurrentLan.ENGLISH){
							v2.setBackgroundResource(R.drawable.tab_icon_class_en_selector);
						}else{
							v2.setBackgroundResource(R.drawable.tab_icon_class_selector);
						}
						
						mMainActivityManagement.GetFavlist();
					}

					mMainActivityManagement.GetNewMsg(mHandlerNewmsg);
				} 
//				else if (stringAction.equals(ActivityActionDefine.UPGRADEAPP_ACTION)) {
//					// 提示过一次后,这次运行不再提示
//					if (!isNotifyUpgrade) {
//						isNotifyUpgrade = true;
//						PushUpgradeAppItem item = null;
//						if (extras.containsKey(ActivityActionDefine.PUSH_UPGRADE)) {
//							item = (PushUpgradeAppItem) extras.getParcelable(ActivityActionDefine.PUSH_UPGRADE);
//						}
//
//						// 检测本地版本号
//						PackageManager packageManager = GlobalVariables.gAppContext.getPackageManager();
//						try {
//							PackageInfo packInfo = packageManager.getPackageInfo(GlobalVariables.gAppContext.getPackageName(), 0);
//							if (item.version.equals(packInfo.versionName)) {
//								Log.i("zjj", "服务器返回版本号与本地相同,不提示");
//								return;
//							}
//						} catch (NameNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//						// 用户选择"以后不提示"后
//						if(mAppUpdateManagement.updatenever(item.version)){
//							return;
//						}
////						SharedPreferences sharedata = getSharedPreferences(Sharedpreferences_UPGRADE, 0);
////						String type = sharedata.getString(Sharedpreferences_UPGRADE_NEVER_KEY, "");
////						if (type.equals(item.version)) {
////							Log.i("zjj", "用户已设置不再提示更新");
////							return;
////						}
//
//						if (null != item) {
//							mAppUpdateManagement.showCustomMessageUpgrade(item);
//						}
//					}
//				}
//				else if (stringAction.equals(ActivityActionDefine.UPGRADEAPP_CANCEL_ACTION)) {
//					mAppUpdateManagement.Stop();
////					if (loader != null) {
////						loader.StopDownload();
////						if (nm != null) {
////							// 取消通知栏
////							nm.cancel(NF_ID);
////							// 删除已下载文件
////							File delFile = new File(dir + "/" + apkname);
////							if (delFile.exists()) {
////								delFile.delete();
////							}
////
////						}
////					}
//				}
				else if (stringAction.equals(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC)) {
					if (extras != null) {
						if (extras.containsKey(ActivityActionDefine.EVENTS_TYPE_ID)) {
							int type = extras.getInt(ActivityActionDefine.EVENTS_TYPE_ID);
							
							int dessum = 1;
							if(extras.containsKey(ActivityActionDefine.EVENTS_DES_SUM)){
								dessum = extras.getInt(ActivityActionDefine.EVENTS_DES_SUM);
							}
							
							//数字是增减还是替换
							boolean isreplace = false;
							if(extras.containsKey(ActivityActionDefine.EVENTS_DES_REPLACE)){
								isreplace = extras.getBoolean(ActivityActionDefine.EVENTS_DES_REPLACE);
							}

							int eventunreadsum = 0;
							for (int i = 0; i < mEventsUnreadSumlist.size(); i++) {
								if (mEventsUnreadSumlist.get(i).update_time_channel == type) {
									
									int sum = 0;
									if(isreplace){
										sum = dessum;
									}else{
										sum = Integer.valueOf(mEventsUnreadSumlist.get(i).update_unreadcount) - dessum;
										sum = sum < 1 ? 0 : sum;
									}
									
									mEventsUnreadSumlist.get(i).update_unreadcount = sum + "";
									classview.ReflashItemstatus(mEventsUnreadSumlist);

									
								}
								eventunreadsum += Integer.valueOf(mEventsUnreadSumlist.get(i).update_unreadcount);
							}
							ResetEventsNotReadSum(eventunreadsum);
						}
					}

				} else if (stringAction.equals(ActivityActionDefine.NEWS_UNREAD_SUM_DESC)) {
					schoolview.ReflashItemstatus(mUpdateTimeDB.getNewsUpdateFlag());
//					if (extras != null) {
//						if (extras.containsKey(ActivityActionDefine.NEWS_TYPE_ID)) {
//							int type = extras.getInt(ActivityActionDefine.NEWS_TYPE_ID);
//
//							schoolview.ReflashItemstatus(type, false);
//						}
//					}

				} else if (stringAction.equals(ActivityActionDefine.LOGIN_AND_SHOWNEWEVENTS_ACTION)) {
					mIsPushStart = true;
					Srcoll2Classview();
				} else if(stringAction.equals(ActivityActionDefine.EXITAPP_ACTION)){
//    				finishDraw();
					finish();
    			} else if(stringAction.equals(ActivityActionDefine.CHANGEUSERPIC_ACTION)){
    				Autologin();
    			} else if (stringAction.equals(ActivityActionDefine.GET_NEW_MSG_ACTION)){
    				// 有新消息模块
    				Log.i("jjj", "-----------有新消息模块-------------");
    				mMainActivityManagement.GetNewMsg(mHandlerNewmsg);
    			}else if (stringAction.equals(ActivityActionDefine.SHOWLOGIN_ACTION)){
    				OpenLoginActivity();
    			}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * 下载资源包成功回调
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	private UICallBack callBack() {
		return new UICallBack() {
			@Override
			public void callBack(Object b) {
				// Log.i("zjj", "下载资源包成功");
				// mResourceManagement.UnzipResourceWithZip();
				if(mResourceManagement != null){
					setTitleLogo(mResourceManagement.getBitmapFromFile(getResources().getString(R.string.title_icon)));
					ReflashUIformRespkg();
				}
			}
		};
	}
	
	/**
	 * 根据资源包配置,刷新9宫格排列
	 */
	private void ReflashUIformRespkg(){
		if(mResourceManagement != null){
			if(SaxReadxml.saxXml(MainActivity.this, mResourceManagement.getBlockURI(getResources().getString(R.string.block_path))).size()!=0){
				GlobalVariables.blocks = SaxReadxml.saxXml(MainActivity.this, mResourceManagement.getBlockURI(getResources().getString(R.string.block_path)));
			}
			if(mResourceManagement.getBitmapFromFile(getResources().getString(R.string.null_pic))!=null){
				GlobalVariables.null_pic = mResourceManagement.getBitmapFromFile(getResources().getString(R.string.null_pic));
			}
		}
//		
//		GlobalVariables.blocks = SaxReadxml.saxXml(MainActivity.this);
		classview.initData();
		schoolview.initData();
		Log.i("zhr","zhr+reflashuiformrespkg");
		classview.ReflashUI();
		
		if(mMainActivityManagement != null)
			mMainActivityManagement.GetNewMsg(mHandlerNewmsg);
		
		schoolview.RefreshUI();
	}

	// ----------------------------结束程序------------------------------
	/**
	 * 截取返回按钮事件
	 */
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (keyCoder == KeyEvent.KEYCODE_BACK) {
			showCustomMessageExit(getResources().getString(R.string.exit_sure2));
			return false;
		}

		return false;
	}

	/**
	 * 退出系统对话框(自定义)
	 * 
	 * @param pTitle
	 * @param pMsg
	 */
	private void showCustomMessageExit(String pMsg) {
		final Dialog lDialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_normaldialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(pMsg);
		Button btn_ok = (Button) lDialog.findViewById(R.id.ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// write your code to do things after users clicks OK
				lDialog.dismiss();

				Exit();
			}
		});

		Button btn_cancel = (Button) lDialog.findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lDialog.dismiss();
			}
		});
		lDialog.show();

	}

	/**
	 * 退出提示框(系统)
	 * 
	 * @param pMsg
	 */
	// private void showExitMessage(String pMsg) {
	// AlertDialog.Builder builder = new Builder(MainActivity.this);
	// builder.setMessage(pMsg);
	// builder.setPositiveButton(R.string.OK,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// Exit();
	// }
	// });
	//
	// builder.setNegativeButton(R.string.Cancel,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	//
	// builder.create().show();
	// }

	/**
	 * 退出程序 能过发送广播结束第一个ACTIVITY来关闭应用程序
	 */
	private void Exit() {
//		Intent exitIntent = new Intent();
//		exitIntent.setAction(ActivityActionDefine.EXITAPP_ACTION);
//		sendBroadcast(exitIntent);
//
//		GlobalVariables.gAppRun = false;
		if(mMainActivityManagement != null)
			mMainActivityManagement.Exit();
		
//		finishDraw();
		finish();
	}

	// ---------------------注销-----------------
	/**
	 * 注销系统对话框(自定义)
	 * 
	 * @param pTitle
	 * @param pMsg
	 */
	private void showCustomMessageBack(String pMsg) {
		final Dialog lDialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_normaldialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(pMsg);
		Button btn_ok = (Button) lDialog.findViewById(R.id.ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// write your code to do things after users clicks OK
				lDialog.dismiss();

//				SettingManager.Destroy();
				if (mLoginManager.getOnlineStatus() == LoginManager.OnlineStatus.ONLINE_LOGINED) {
					mMainActivityManagement.Logout();
				}
				
				//modify by menchx 防止出现多个导航界面				
				
				//打开导航界面
				Intent sendIntent = new Intent();
				sendIntent.setClass(MainActivity.this,NavigationMainActivity.class);
//				sendIntent.putExtra(SchoolNavigation.NAVIGATION_PARENT_ID,GlobalVariables.getAgentID());	//根目录ID(代理商ID)
				sendIntent.putExtra(SchoolNavigation.KEY_FROMMAIN,true);
				startActivity(sendIntent);
				
//				finishDraw();
				finish();
			}
		});

		Button btn_cancel = (Button) lDialog.findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lDialog.dismiss();
			}
		});
		lDialog.show();

	}

	/**
	 * 退出提示框(系统)
	 * 
	 * @param pMsg
	 */
	// private void showLogoutMessage(String pMsg) {
	// AlertDialog.Builder builder = new Builder(MainActivity.this);
	// builder.setMessage(pMsg);
	// builder.setPositiveButton(R.string.OK,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// SettingManager.Destroy();
	// if(instance.getOnlineStatus() ==
	// LoginManager.OnlineStatus.ONLINE_LOGINED){
	// Logout();
	// }
	// finish();
	// }
	// });
	//
	// builder.setNegativeButton(R.string.Cancel,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	//
	// builder.create().show();
	// }

	/**
	 * 注销
	 */
//	private void Logout() {
//		instance.logout(new LogoutCallback() {
//
//			@Override
//			public void onLogOut(boolean bSuccess) {
//				// TODO Auto-generated method stub
//				// if(bSuccess) //
//				// {
//				// GlobalVariables.toastShow(getResources().getString(R.string.logoutsucceed));
//				// }
//
//			}
//		});
//	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		GlobalVariables.STATEBARHEIGHT = statusBarHeight;
	}
}
