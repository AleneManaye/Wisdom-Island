package com.drcom.drpalm.View.main;

import java.util.Date;
import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.UpdateTimeDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.AppPassDataHelper;
import com.drcom.drpalm.Tool.ResourceManagement;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.View.events.fav.FavManagement;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.login.LoginManager.LogoutCallback;
import com.drcom.drpalm.View.login.LoginManager.OnlineStatus;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.ToursItem;
import com.drcom.drpalm.objs.UpdateTimeItem;

public class MainActivityManagement {
	private int GETNG_SUCCDDE = 1;
	private int GETNG_FAILED = 0;
	
	private int GETNG_NEWS_SUCCDDE = 1;
	private int GETNG_NEWS_EVENTS_SUCCDDE = 2;
	
	private String KEY_REFLASHTIME = "mainflashtime";
	private String KEY_UNREADEVENTS = "KEY_UNREADEVENTS"; // 未读通告数
	
	private Context mContext;
	// 变量
	private RequestOperation mRequestOperation = RequestOperation.getInstance();
//	private String[] moduleNames; // 模块名
	private SharedPreferences sp; // 15分钟间隔自动刷新
	private Editor editor;
	private Date lastrefreshTime;
	public boolean mIsExternalLogin = false;	//是否外部程序启动并登录
	public UserInfo mUserInfo;					//外部程序启动,传入已登录成功的用户信息
	
	// private boolean mIsFirstTimeLogin = true; //是否首次启动
	private List<UpdateTimeItem> mEventsUnreadSumlist;
	
	public SettingManager setInstance;
	public LoginManager instance ;//= LoginManager.getInstance(GlobalVariables.gAppContext);
	public UpdateTimeDB mUpdateTimeDB;
	public ResourceManagement mResourceManagement;
	private FavManagement mFavManagement;
	
	//视频
//	private AudioServiceController mAudioController;
	
	public MainActivityManagement(Context c){
		mContext = c;
//		mAudioController = AudioServiceController.getInstance();
//		AudioServiceController.getInstance().bindAudioService(c);
	}
	
	/**
	 * 从PUSH启动
	 */
	public void StartFromPush(){
		if(GlobalVariables.gSchoolKey.equals("")){
			//如果不是从外部程序启动,则取默认学校SchoolKey
			SharedPreferences preferences = mContext.getSharedPreferences("default_school", Context.MODE_PRIVATE);
			String schoolkey = preferences.getString("school_key", "");
			GlobalVariables.gSchoolKey = schoolkey;
			
			Log.i("zjj", "--------gSchoolKey-----------:" + schoolkey);
		}
		

		mRequestOperation.initDB();

		// 清除收到的PUSH提示(只清除本应用的)
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}
	
	/**
	 * 主界面初始化
	 */
	public void init(){
		//
		SettingManager.Destroy();	//先清空静态变量,以免BUG后状态不会清除
		LoginManager.destroy();		
		//
		setInstance = SettingManager.getSettingManager(mContext);
		instance = LoginManager.getInstance(GlobalVariables.gAppContext);
		mUpdateTimeDB = UpdateTimeDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		mResourceManagement = new ResourceManagement();//.getResourceManagement();
		mFavManagement = new FavManagement(mContext);
		
		//
		sp = mContext.getSharedPreferences(KEY_REFLASHTIME, mContext.MODE_WORLD_READABLE);
		editor = sp.edit();
		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
		
		//
		GlobalVariables.gAppRun = true;
		DisplayMetrics dm = new DisplayMetrics();
		((MainActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		GlobalVariables.SCREEN_WIDTH = dm.widthPixels;
		GlobalVariables.SCREEN_HEIGHT = dm.heightPixels;
	}
	
	/**
	 * 取默认学校SchoolKey
	 */
	public void getDefaultSchoolid(){
		if(GlobalVariables.gSchoolKey.equals("")){
			SharedPreferences preferences = mContext.getSharedPreferences("default_school", Context.MODE_PRIVATE);
			String schoolkey = preferences.getString("school_key", "");
			GlobalVariables.gSchoolKey = schoolkey;
		}
	}
	
	/**
	 * 保存刷新时间
	 */
	public void SaveReflashtime(){
		lastrefreshTime = new Date(System.currentTimeMillis());
		editor.putLong(KEY_REFLASHTIME, lastrefreshTime.getTime());// 保存最后一次刷新的时间
		editor.commit();
	}
	
	/**
	 * 保存通告未读数
	 * @return
	 */
	public void SaveEventUnreadSum(int sum){
		editor.putInt(KEY_UNREADEVENTS, sum);
		editor.commit();
	}
	
	/**
	 * 通告未读数（上次的记录）
	 * @return
	 */
	public int getEventUnreadSum(){
		return sp.getInt(KEY_UNREADEVENTS, 0);
	}
	
	/**
	 * 距上次刷新时间是否大于5分钟
	 * @return
	 */
	public boolean isOver15mins(){
		return (new Date(System.currentTimeMillis()).getTime() - lastrefreshTime.getTime()) / 1000 / 60 > 5;
	}
	
	/**
	 * 取网关
	 * @param h
	 */
	public void GetNetworkGate(final Handler h){
		mRequestOperation.GetNetworkGate(new RequestOperationCallback() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(h != null){
					Message message = Message.obtain();
					message.arg1 = GETNG_SUCCDDE;
					h.sendMessage(message);
				}
			}

			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				if(h != null){
					Message message = Message.obtain();
					message.arg1 = GETNG_FAILED;
					message.obj = err;
					h.sendMessage(message);
				}
			}
		});
	}
	
	/**
	 * 获取活动和新闻模块最新更新时间
	 */
	public void GetNewMsg(final Handler h) {
		if (instance.getOnlineStatus() == OnlineStatus.ONLINE_LOGINED) {
			RequestOperation mRequestOperation = RequestOperation.getInstance();
			RequestOperationCallback callback = new RequestOperationCallback() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					if(h != null){
						Message message = Message.obtain();
						message.arg1 = GETNG_NEWS_EVENTS_SUCCDDE;
						h.sendMessage(message);
					}
					
				}

				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					if(h != null){
						Message message = Message.obtain();
						message.arg1 = GETNG_FAILED;
						message.obj = err;
						h.sendMessage(message);
					}
				}
			};
			mRequestOperation.sendGetNeededInfo("GetEventsLastUpdate", new Object[] { callback }, callback.getClass().getName());
		} else if (instance.getOnlineStatus() == OnlineStatus.OFFLINE) {
			RequestOperation mRequestOperation = RequestOperation.getInstance();
			RequestOperationCallback callback = new RequestOperationCallback() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					if(h != null){
						Message message = Message.obtain();
						message.arg1 = GETNG_NEWS_SUCCDDE;
						h.sendMessage(message);
					}
				}

				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					if(h != null){
						Message message = Message.obtain();
						message.arg1 = GETNG_FAILED;
						message.obj = err;
						h.sendMessage(message);
					}
				}
			};
			mRequestOperation.sendGetNeededInfo("GetNewsLastUpdate", new Object[] { callback }, callback.getClass().getName());
		}
	}
	
	/**
	 * 更新资源包
	 * @param list
	 * @param cb
	 */
	public void UpdateResource(List<ToursItem> list,UICallBack cb){
		if (list.size() > 0) {
			Log.i("zjj", "更新资源信息包成功接收广播:" + list.get(0).name + "下载地址:" + list.get(0).url);
			mResourceManagement.initResourceName(list.get(0).name + ".zip");
			mResourceManagement.download(mContext, list.get(0).url, cb);
		}
	}
	
	/**
	 * 取收藏通告列表
	 */
	public void GetFavlist(){
		mFavManagement.GetFavlist();
//		mFavManagement.SendOfflineFav();
	}
	
	/**
	 * 退出程序
	 */
	public void Exit(){
		//广播通知外部调用的程序退出
		Intent exitExternalIntent = new Intent();
		exitExternalIntent.setAction(AppPassDataHelper.ACTION_EXIT_APP);
		mContext.sendBroadcast(exitExternalIntent);
		
		//通知内部所有界面退出
		Intent exitIntent = new Intent();
		exitIntent.setAction(ActivityActionDefine.EXITAPP_ACTION);
		mContext.sendBroadcast(exitIntent);

		GlobalVariables.gAppRun = false;
	}
	
	/**
	 * 注销
	 */
	public void Logout() {
		instance.logout(new LogoutCallback() {

			@Override
			public void onLogOut(boolean bSuccess) {
				// TODO Auto-generated method stub
				// if(bSuccess) //
				// {
				// GlobalVariables.toastShow(getResources().getString(R.string.logoutsucceed));
				// }

			}
		});
	}
	
	/**
	 * 从外部启动
	 */
	public void ExternalLogin(){
		if(mIsExternalLogin){
			instance.LoginSucceed(mUserInfo, null);
			instance.SaveLoginUser(mUserInfo);
		}
	}
	
	public void Destory(){
		//清空数据
		GlobalVariables.gSchoolKey = "";
		
		// 一定要销毁这两个对像,否则重进系统/BUG之后不会NEW
		LoginManager.destroy();
		SettingManager.Destroy();
		
		//释放内存(把占用内存大的变量致空,减少BUG的机率)
		instance = null;
		setInstance = null;
		mResourceManagement = null;
		mRequestOperation = null;
		mFavManagement = null;
	}
}
