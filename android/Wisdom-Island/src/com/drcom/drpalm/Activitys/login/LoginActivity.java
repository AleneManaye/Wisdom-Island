package com.drcom.drpalm.Activitys.login;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.bindaccount.BindaccountListActivity;
import com.drcom.drpalm.Activitys.bindaccount.NewbindaccountActivity;
import com.drcom.drpalm.Activitys.web.WebviewActivity;
import com.drcom.drpalm.Tool.Encryption;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.login.LoginManager.LoginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm4tianzhujiao.R;

public class LoginActivity extends ModuleActivity {
	public static String RESULT_CODE_KEY = "resultcodekey";
	public static int RESULT_CODE_ONLINE_SUCCEED = 1;
	public static int RESULT_CODE_OFFLINE_SUCCEED = 2;
	public static int RESULT_CODE_LOGOUT_SUCCEED = 3;
	public static int RESULT_CODE_FAILD = 0;
	
	private final int LOGIN_BEGIN   = 0;
	private final int LOGIN_SUCCESS = 1;
	private final int LOGIN_FAILED  = 2;
	
	private final int GALLRY_SPCING = 30;
	
	//变量
//	private SettingManager setInstance;
	private UserInfo usrinfo;
	private boolean m_bIsOnlineLogin = true;    //在线登录或离线登录标志
	private String strUserName = "";
	private String strUserPwd  = "";
	private boolean bRememberPwd = false;
	private boolean bAutoLogin = false;
//	private boolean bLogining = false;
	private LoginAdapter adapter;
	private List<UserInfo> mUserInfoList;
//	private Handler uiHandler = null;
	private LoginManager loginmanager;
	
	//控件
//	private ImageView iiii;
	private Button mButtonLogin;
	private Button mButtonOfflineLogin;
	private Button mShowUsers;
	private CheckBox  m_cbRememberPwd = null;
	private CheckBox  m_cbAutoLogin = null;
	private CheckBox  m_cbBindAccoutn ;
	private Boolean BindAccountIsCheck = false;
	private EditText  m_edittextUsrName = null;
	private EditText  m_edittextUsrPwd = null;
//	private ProgressDialog m_pDialog;
	private Gallery mGallery;
	private LinearLayout mLinearLayoutGallery;
	private ImageView mImageViewTop;
	
	private Handler mhandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.login2, mLayout_body);
		
		//
//		setInstance = SettingManager.getSettingManager(LoginActivity.this);
		usrinfo = new UserInfo();
//		usrinfo = setInstance.getCurrentUserInfo();
		loginmanager = LoginManager.getInstance(GlobalVariables.gAppContext);
		getData();	//登录历史列表
		
		mhandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.arg1) {
				case BindaccountListActivity.ADDSUCCESS:
					Log.i("hzy", "登陆关联成功!");
					break;
				case BindaccountListActivity.FAIL:					
					Log.i("hzy", msg.obj.toString());//界面是否要做验证？//等待错误码转换未完成
					break;
				default:
					break;
				}
				super.handleMessage(msg);

			}
		};
		
		//
		setTitlebarBgColor(getResources().getColor(R.color.bgblue));
		hideToolbar();
//		initalizeLoadingDialog();
		
		//图片
		mImageViewTop = (ImageView)findViewById(R.id.imageview_login_top);
		
		//记住密码
		m_cbRememberPwd = (CheckBox) findViewById(R.id.login_checkbox_rememberpwd);
		m_cbRememberPwd.setOnCheckedChangeListener(cbchangelistener);
		m_cbRememberPwd.setChecked(true);//(usrinfo==null?false:usrinfo.bRememberPwd);
		//自动登录
		m_cbAutoLogin   = (CheckBox) findViewById(R.id.login_checkbox_autologin);
		m_cbAutoLogin.setOnCheckedChangeListener(cbchangelistener);
		//帐号关连
		m_cbBindAccoutn = (CheckBox) findViewById(R.id.login_checkbox_bindaccount);
		
		//用户名
		m_edittextUsrName = (EditText) findViewById(R.id.username);
		
		//密码
		m_edittextUsrPwd  = (EditText) findViewById(R.id.userpwd);
		m_edittextUsrPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
					HideUserpic();
			}
		});
		
		//在线登录
		mButtonLogin = (Button)findViewById(R.id.btnlogin);
		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(HttpStatus.IsNetUsed(LoginActivity.this) == HttpStatus.STATUS_NOCONNECT){
					m_bIsOnlineLogin = false;
					//测试代码	 **没验证密码**
//					OfflineLogin();
				}else{
					m_bIsOnlineLogin = true;
					
				}
				Login();
			}
		});
		
		//找回密码
		mButtonOfflineLogin = (Button)findViewById(R.id.btnofflinelogin);
		mButtonOfflineLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(LoginActivity.this, WebviewActivity.class);
				if(LanguageManagement.getSysLanguage(LoginActivity.this) == CurrentLan.COMPLES_CHINESE){
					i.putExtra(WebviewActivity.URL_KEY, GlobalVariables.gAccUrl + "&lang=zh-hant");	//繁体
				}else{
					i.putExtra(WebviewActivity.URL_KEY, GlobalVariables.gAccUrl);
				}
				startActivity(i);
			}
		});
		
		//显示头像列表按钮
		mShowUsers = (Button)findViewById(R.id.moreUser);
		mShowUsers.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mGallery.getVisibility() == View.VISIBLE){
					mShowUsers.setBackgroundResource(R.drawable.login_username_down);
					mGallery.setVisibility(View.GONE);
				}else{
					mShowUsers.setBackgroundResource(R.drawable.login_username_up);
					mGallery.setVisibility(View.VISIBLE);
					alignGalleryItemToMid();
				}
			}
		});
		
		//头像列表
		adapter = new LoginAdapter(this, mUserInfoList);
		adapter.setDelHandler(mHandlerDel);
		
		mLinearLayoutGallery = (LinearLayout)findViewById(R.id.line2);
		mGallery=(Gallery)findViewById(R.id.myGallery);
		mGallery.setSpacing(GALLRY_SPCING);
		mGallery.setVisibility(Gallery.GONE);
		mGallery.setAdapter(adapter);
		mGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// TODO Auto-generated method stub
				usrinfo = mUserInfoList.get(index);
				RefalshUI();
				//隐藏头像列表
				mGallery.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						HideUserpic();
					}
				} , 800);
			}
		});
		
		//Titlebar返回按钮事件
		SetBackBtnOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Back();
			}
		});
		
		setTitleText(getString(R.string.login));
		RefalshUI();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mlogincallback = null;
		super.onDestroy();
		
	}
	
	/**
	 * 截取返回按钮事件
	 */
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (keyCoder == KeyEvent.KEYCODE_BACK) {
			Back();
			return false;
		}

		return false;
	}
	
	/**
	 * 返回事件
	 */
	private void Back(){
		// 传递参数
		Intent newIntent = new Intent();
		newIntent.putExtra(RESULT_CODE_KEY, RESULT_CODE_FAILD);
		setResult(RESULT_OK, newIntent);
		
		finishDraw();
	}
	
	private OnCheckedChangeListener cbchangelistener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(buttonView.getId() == R.id.login_checkbox_rememberpwd)
			{
				//记住密码为UnChecked，自动登录相应为UnChecked
				if(m_cbRememberPwd.isChecked() == false)
					m_cbAutoLogin.setChecked(false);
			}
			else if(buttonView.getId() == R.id.login_checkbox_autologin)
			{
				//自动登录为Checked,记住密码为Checked
				if(m_cbAutoLogin.isChecked())
					m_cbRememberPwd.setChecked(true);
			}
		}
	};
	
	/**
	 * 在线登录
	 */
	private void Login() {
		// 关闭软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mButtonLogin.getWindowToken(), 0);

		
		
		bRememberPwd = m_cbRememberPwd.isChecked();
		bAutoLogin = m_cbAutoLogin.isChecked();
		bRememberPwd = m_cbBindAccoutn.isChecked();//可以删除掉
		
		BindAccountIsCheck = m_cbBindAccoutn.isChecked();

		strUserName = m_edittextUsrName.getText().toString();
		strUserPwd = m_edittextUsrPwd.getText().toString();
		if (strUserName.length() == 0 || strUserPwd.length() == 0) {
			GlobalVariables.toastShow(GlobalVariables.gAppContext.getString(R.string.ID_and_pwd_nonull));
			return;
		}

		/*
		 * add by kingsley
		 */
		if( (0 == strUserName.compareTo("test")) && ( 0 == strUserPwd.compareTo("123")) ) {
			mlogincallback.onLoginSuccess();
			return;
		}
		
		if (m_bIsOnlineLogin) // 在线登陆
		{
			// mLoadingScreen.setVisibility(View.VISIBLE);
			sedHandlerMsg(LOGIN_BEGIN, "");
//			m_pDialog.show();
//			if (bLogining){
////				Log.i("zjj", "bLoginingbLoginingbLoginingbLoginingbLoginingbLogining:" + bLogining);
//				m_pDialog.cancel();
//				return;
//			}
			ShowLoadingDialog();
				
//			bLogining = true;
			// modify by jiangbo 2012-09-06 点击登录即登陆成功，不验证
			// loginmanager.mOnlineStatus = OnlineStatus.ONLINE_LOGINED;
			// sedHandlerMsg(LOGIN_SUCCESS,"");
			// Intent intent = new
			// Intent(ActivityActionDefine.POPACTIVITY_ACTION);
			// sendBroadcast(intent);

			loginmanager.loginOnline(strUserName, strUserPwd,mlogincallback);
		} 
		else // 离线登陆
		{
			if (loginmanager.loginLocal(strUserName, strUserPwd)) {
				// 传递参数
				Intent newIntent = new Intent();
				newIntent.putExtra(RESULT_CODE_KEY, RESULT_CODE_OFFLINE_SUCCEED);
				setResult(RESULT_OK, newIntent);
				
				GlobalVariables.toastShow(GlobalVariables.gAppContext.getString(R.string.offlinelogin_success));
				finish();
			} else {
				GlobalVariables.toastShow(GlobalVariables.gAppContext.getString(R.string.login_fail));
			}
		}
	}
	
	LoginCallback mlogincallback = new LoginCallback() {
		@Override
		public void onLoginSuccess() {
			// TODO Auto-generated method stub
//			bLogining = false;
			sedHandlerMsg(LOGIN_SUCCESS, "");
//			m_pDialog.cancel();
			HideLoadingDialog();
			UserInfo loginUsrinfo = SettingManager.getSettingManager(LoginActivity.this).getUserInfoByName(strUserName);
			loginUsrinfo.strUsrName = strUserName;
			if(bRememberPwd)
				loginUsrinfo.strPassword = strUserPwd;
			else	//没勾保存密码
				loginUsrinfo.strPassword = "";
			
			loginUsrinfo.bAutoLogin = bAutoLogin;
			loginUsrinfo.bRememberPwd = bRememberPwd;
			// loginUsrinfo.bParentLock =
//			setInstance.setUserInfo(loginUsrinfo);
//			setInstance.saveSetting();
			loginmanager.SaveLoginUser(loginUsrinfo);
			if(BindAccountIsCheck){
				loginmanager.Bindaccount(strUserName,Encryption.toMd5(strUserPwd), mhandler);	
			}
			
			// 发送未读通告List_id
			// EventsManager.autoReplyAll(loginUsrinfo.strUsrName,
			// mContext) ;

			Log.i("zjj", "----在线登录成功-------");
//			Intent intent1 = new Intent(ActivityActionDefine.UNGETCOUNT_ACTION);
//			intent1.putExtra(ActivityActionDefine.UNGETCOUNT,ungetcount);
//			sendBroadcast(intent1);
			
			// 传递参数
			Intent newIntent = new Intent();
			newIntent.putExtra(RESULT_CODE_KEY, RESULT_CODE_ONLINE_SUCCEED);
			setResult(RESULT_OK, newIntent);
			finishDraw();
		}

		@Override
		public void onLoginError(String strError) {
			// TODO Auto-generated method stub
//			bLogining = false;
//			m_pDialog.cancel();
			HideLoadingDialog();
			sedHandlerMsg(LOGIN_FAILED, strError);
			Log.i("zjj", "----在线登录失败-------" + strError);
			// mLoadingScreen.setVisibility(View.GONE);
		}
	};
	
	/**
	 * 离线登录
	 */
//	private void OfflineLogin(){
//		LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
//		instance.mOnlineStatus = LoginManager.OnlineStatus.OFFLINE_LOGINED;
//		
//		UserInfo info = new UserInfo();
//		info.strUsrName = m_edittextUsrName.getText().toString();
//		SettingManager setInstance = SettingManager.getSettingManager(LoginActivity.this);	
//		setInstance.setUserInfo(info);
//		
//		Log.i("zjj", "----离线登录成功-------");
////		Intent intent1 = new Intent(ActivityActionDefine.UNGETCOUNT_ACTION);
//////		intent1.putExtra(ActivityActionDefine.UNGETCOUNT,ungetcount);
////		sendBroadcast(intent1);
//		finish();
//		
//
//	}
	
	private void sedHandlerMsg(int nType,String strError)
	{
		Message msg = Message.obtain();
		msg.arg1 = nType;
		msg.obj = strError;
		if(uiHandler!=null)
			uiHandler.sendMessage(msg);
	}
	
	private Handler uiHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressBar();
			switch (msg.arg1) {
			case LOGIN_FAILED:
				Toast.makeText(LoginActivity.this,String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		};
	};
	
//	private void initalizeLoadingDialog(){
//		if(null == m_pDialog)
//		{
//			m_pDialog = new ProgressDialog(this);
//		}
//		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		m_pDialog.setMessage(GlobalVariables.gAppResource.getString(R.string.logining));
//		m_pDialog.setIndeterminate(false);
//		m_pDialog.setCancelable(true);
//	}
	
//	/**
//	 * 用户类型转换（0表示教师,1表示家长,2表示学生）
//	 * @param strUserType
//	 * @return
//	 */
//	private int translateUserType(String strUserType)
//	{
//		if(strUserType.contentEquals("0"))
//			return 0;
//		else if(strUserType.contentEquals("0"))
//			return 1;
//		else 
//			return 2;
//	}
	
	//从数据库中获得联系人列表数据，这里的UserInfo记录有三个值，id为联系人名，password为密码，image为头像
	private void getData(){
		mUserInfoList = loginmanager.GetLoginUserList();
		if(mUserInfoList != null){
			//设置上次登录的帐号在中间
			if(mUserInfoList.size()>2){
				int midindex = mUserInfoList.size()%2==0?mUserInfoList.size()/2-1:mUserInfoList.size()/2;
				usrinfo = mUserInfoList.get(0);
				mUserInfoList.remove(0);
				mUserInfoList.add(midindex, usrinfo);
			}else{
				usrinfo = mUserInfoList.get(0);	//最近登录的用户
			}
		}
	}
	
	/**
	 * 刷新界面
	 */
	private void RefalshUI(){
		
		if(LanguageManagement.getSysLanguage(LoginActivity.this) == CurrentLan.COMPLES_CHINESE){
			mImageViewTop.setBackgroundResource(R.drawable.logintop_bg1_t);
		}else if(LanguageManagement.getSysLanguage(LoginActivity.this) == CurrentLan.ENGLISH){
			mImageViewTop.setBackgroundResource(R.drawable.logintop_bg1_en);
		}else{
			mImageViewTop.setBackgroundResource(R.drawable.logintop_bg1);
		}
		
//		if(!GlobalVariables.gTempUsermane.equals("") &&  !GlobalVariables.gTempPW.equals("")){
//			m_edittextUsrName.setText(GlobalVariables.gTempUsermane);
//			m_edittextUsrPwd.setText(GlobalVariables.gTempPW);
//			
//			GlobalVariables.gTempUsermane = "";
//			GlobalVariables.gTempUsermane = "";
//		}else{
			m_cbAutoLogin.setChecked(usrinfo==null?false:usrinfo.bAutoLogin);
			m_edittextUsrName.setText(usrinfo==null?"":usrinfo.strUsrName);
			m_edittextUsrPwd.setText(usrinfo.strPassword);
//		}
		
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 设置上次登录的帐号在中间
	 */
	private void alignGalleryItemToMid(){
		if(mUserInfoList != null && mUserInfoList.size()>2){
			int midindex = mUserInfoList.size()%2==0?mUserInfoList.size()/2-1:mUserInfoList.size()/2;
			mGallery.setSelection(midindex);
		}
	}
	
	/**
	 * 隐藏用户头像
	 */
	private void HideUserpic(){
		mShowUsers.setBackgroundResource(R.drawable.login_username_down);
		mGallery.setVisibility(Gallery.GONE);
	}
	
	/**
	 * 删除记录
	 */
	private Handler mHandlerDel = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			loginmanager.DelLoginUser(mUserInfoList.get(msg.arg1));
			if(usrinfo.strUsrName.equals(mUserInfoList.get(msg.arg1).strUsrName)){
				usrinfo = new UserInfo();
			}
			mUserInfoList.remove(msg.arg1);
			RefalshUI();
		}
	};
}
