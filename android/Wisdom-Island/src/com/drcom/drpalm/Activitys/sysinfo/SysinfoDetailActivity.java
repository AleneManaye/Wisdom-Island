package com.drcom.drpalm.Activitys.sysinfo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.DB.SystemInfoDB;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.View.sysinfo.SysinfoDetailActivityManagement;
import com.drcom.drpalm.objs.SystemInfoItem;
import com.wisdom.island.R;

/**
 * 系统消息
 * @author zhaojunjie
 *
 */
public class SysinfoDetailActivity extends ModuleActivity{
	public static String KEY_SYSINFO_ID = "KEY_SYSINFO_ID";
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	//变量
	private SystemInfoItem mSystemInfoItem;
//	private SystemInfoDB mSystemInfoDB ;
//	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private String id;
//	private Cursor mSysinfoCursor = null;
//	private String mUsername = "";	
//	private SettingManager setInstance ;	
	private SysinfoDetailActivityManagement mSysinfoDetailActivityManagement;
	//控件
	private TextView mTextViewBody;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.sysinfo_detail_view, mLayout_body);
		
//		mSystemInfoDB = SystemInfoDB.getInstance(this,GlobalVariables.gSchoolKey);
//		setInstance = SettingManager.getSettingManager(this);	
//		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		mSysinfoDetailActivityManagement = new SysinfoDetailActivityManagement(SysinfoDetailActivity.this);
		
		mTextViewBody = (TextView)findViewById(R.id.sysinfo_body_txtview);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(KEY_SYSINFO_ID)){
			id = extras.getString(KEY_SYSINFO_ID);
		}
		
		hideToolbar();
		
		mLayout_body.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				GetDataInDB();
				sendGetDetailRequest(id);
			}
		},300);
		setTitleText(getString(R.string.sysinfo));
	}
	
	/**
	 * 请求网络(取列表)
	 * @param lastActivityId 
	 * @param uiHandler
	 */
	private void sendGetDetailRequest (final String id){
		if(mSysinfoDetailActivityManagement.sendGetDetailRequest(mHandler, id)){
			showProgressBar();
		}
		
//		//非在线登录/网络不通时,返回
//		LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
//		if(HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT){	//LoginManager.OnlineStatus.ONLINE_LOGINED != instance.getOnlineStatus() ||
//			return;
//		}
//		
//		showProgressBar();
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
//			@Override
//			public void onSuccess() {
////				GetDataInDB(id);
//				
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFINISH;
//				mHandler.sendMessage(message) ;
//			}
//
//			@Override
//			public void onCallbackError(String err) {
//				// TODO Auto-generated method stub
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED;
//				message.obj = err;
//				mHandler.sendMessage(message);
//			}
//			
//			@Override
//			public void onReloginError() {
//				// TODO Auto-generated method stub
//				super.onReloginError();
//				Log.i("zjj", "通告列表:自动重登录失败");
//			}
//			
//			@Override
//			public void onReloginSuccess() {
//				// TODO Auto-generated method stub
//				super.onReloginSuccess();
//				Log.i("zjj", "通告列表:自动重登录成功");
//				if(isRequestRelogin){
//					sendGetDetailRequest(id);	//自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetSysMsgContent", new Object[]{id, callback},callback.getClass().getName());
		
	}
	
	/**
	 * UIHandler
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == UPDATEFINISH){
				GetDataInDB();
				ReflashUI();
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(SysinfoDetailActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
			}
			
			hideProgressBar();
		}
	};
	
	/**
	 * 从库读取
	 */
	private void GetDataInDB(){
//		mSysinfoCursor = mSystemInfoDB.getSystemInfoCursor(mUsername, id);
//		mSysinfoCursor.requery();
//		mSysinfoCursor.moveToFirst();
//		mSystemInfoItem = mSystemInfoDB.retrieveSystemInfoItem(mSysinfoCursor);
//		mSysinfoCursor.close();
		mSystemInfoItem = mSysinfoDetailActivityManagement.GetDataInDB(id);
	}
	
	/**
	 * 刷新界面
	 */
	private void ReflashUI(){
		mTextViewBody.setText( mSystemInfoItem.msg_body.replace(":",":\n----------------------\n").replace(";", ";\n\n\n"));
		hideProgressBar();
	}
}
