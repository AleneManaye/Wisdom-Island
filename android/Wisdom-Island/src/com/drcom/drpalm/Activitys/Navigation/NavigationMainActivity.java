package com.drcom.drpalm.Activitys.Navigation;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.bindaccount.BindaccountListActivity;
import com.drcom.drpalm.Activitys.web.WebviewActivity;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.View.Navigation.SchoolNavigationManagetment;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NavigationItem;
import com.drcom.drpalm.objs.NavigationListItem;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 导航首页
 * @author MCX
 *
 */
public class NavigationMainActivity extends ModuleActivity{
	
	public static final int GET_NAVIGATION_ITEM_START = 0;
	public static final int GET_NAVIGATION_ITEM_SUCCESSFUL = 1;
	public static final int GET_NAVIGATION_ITEM_FAILED = 2;
	
	private GridView mGridView;
	private NavigationMainAdapter mAdapter;
	private List<NavigationListItem> navigationList;
	private NavigationDB mNavigationDB;
	private boolean flag1= false, flag2 = true;
	private SchoolNavigationManagetment mSchoolNavigationManagetment;
	private Handler uiHandler = null;
	private boolean isAnimationDoing = false;
	private Context mContext;
	
	private GroupReceiver mGroupReceiver;	// Receiver
	
	private boolean isFormMain = false;	//是否从ManinActivity返回
	public static String NAVIGATION_INSTITUTION = "institution";//机构
	public static String NAVIGATION_PHONE = "phone";//热线电话
	public static String NAVIGATION_LINK = "link";//链接
	public static String NAVIGATION_MODULE = "module";//功能模块
	public static String NAVIGATION_ASS_ACOUNT = "ass_acount";//账号关联
	public static final String NAVIGATION_PARENT_ID = "parent_id";
	public static final String NAVIGATION_PARENT_TYPE = "parent_type";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.navigation_list, mLayout_body);
		mContext = this;
		
		Intent intent = getIntent();
		if(null != intent){
			Bundle bundle = intent.getExtras();
			if(null != bundle){
				if(bundle.containsKey(SchoolNavigation.KEY_FROMMAIN)){
					isFormMain = bundle.getBoolean(SchoolNavigation.KEY_FROMMAIN);
				}
			}
		}
		
		setHasGestureToClose(false);
		hideToolbar();
		hideBackButton();
		initHandler();
		initRefreshBtn();
		setTitleLogo(BitmapFactory.decodeResource(getResources(), R.drawable.defaulttitlelogo));	
		
		mLayout_body.setBackgroundColor(Color.WHITE);
		
		mSchoolNavigationManagetment = new SchoolNavigationManagetment(this);
		
		mGridView = (GridView)findViewById(R.id.test_grid);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(!isAnimationDoing){
					MyRotate anim=new MyRotate(0, 360, arg1.getWidth() / 2.0f, arg1.getHeight() / 2.0f, 0, false);
					anim.setDuration(400);
					arg1.startAnimation(anim);
					anim.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							isAnimationDoing = true;
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							isAnimationDoing = false;
							if(navigationList.get(arg2).type.equals(NAVIGATION_PHONE)){
								showCustomMessageDial(getResources().getString(R.string.hotlinetitle),navigationList.get(arg2).content);
							}else if(navigationList.get(arg2).type.equals(NAVIGATION_LINK)){
								Intent intent = new Intent(NavigationMainActivity.this,WebviewActivity.class);
								intent.putExtra(WebviewActivity.URL_KEY, navigationList.get(arg2).content+"lang=" + LanguageManagement.getSysLanguageUrlStr(NavigationMainActivity.this) + "&tokenid=" + GlobalVariables.Devicdid);
								intent.putExtra(WebviewActivity.URL_ERROR_KEY,GlobalVariables.URL_ERROR_1);
								startActivity(intent);
							}else if(navigationList.get(arg2).type.equals(NAVIGATION_MODULE)){
								if(navigationList.get(arg2).content.equals(NAVIGATION_ASS_ACOUNT)){
								//帐号关联
								Intent i = new Intent(NavigationMainActivity.this, BindaccountListActivity.class);
								startActivity(i);
								}
							}else if(navigationList.get(arg2).type.equals(NAVIGATION_INSTITUTION)){	
								Intent sendIntent = new Intent();
								sendIntent.setClass(NavigationMainActivity.this,SchoolNavigation.class);
								sendIntent.putExtra(NAVIGATION_PARENT_ID,navigationList.get(arg2).content);	//根目录ID(代理商ID)
								sendIntent.putExtra(NAVIGATION_PARENT_TYPE,navigationList.get(arg2).type);   //传入类型用于区分是从外部进入
								Log.i("zhr","content:"+navigationList.get(arg2).content+",type:"+navigationList.get(arg2).type);
								startActivity(sendIntent);
							}
						}
					});
				}
			}
		});
		mNavigationDB = NavigationDB.getInstance(this);
		initData();
		mSchoolNavigationManagetment.GetNavigationList(GlobalVariables.getAgentID(), uiHandler);
		initReceiver();
		
		//取上次登录的学校school_key
		SharedPreferences preference = getSharedPreferences("default_school", Context.MODE_PRIVATE);
		String school_key = preference.getString("school_key", "");
		
		if(isFormMain)
			school_key = "";
		
		if(!school_key.equals("")){
			NavigationItem item = mNavigationDB.getSchoolItem(school_key);
			if(item.key != null){
				mSchoolNavigationManagetment.openAppBySchoolkey(item);
			}
		}
	}
	
	private void initData(){
		if(navigationList == null){
			navigationList = new ArrayList<NavigationListItem>();
		}
		navigationList.clear();
		List<NavigationListItem> list = new ArrayList<NavigationListItem>();
		list = mNavigationDB.getNavigationLists();
//		NavigationListItem item = new NavigationListItem();
//		item.type = "hotline";
//		list.add(item);
//		NavigationListItem ebabyChannel = new NavigationListItem();
//		ebabyChannel.type = "ebabychannel";
//		list.add(ebabyChannel);
		int i;
		for(i=0;i<list.size();i++){
			if(i%2==0){
        		list.get(i).colorFlag=flag1;
        		flag1=!flag1;
        	}else{
        		list.get(i).colorFlag=flag2;
        		flag2=!flag2;
        	}
		}
		if(list.size()>0){
			navigationList.addAll(list);
		}
		if(mAdapter == null){
			mAdapter = new NavigationMainAdapter(this, navigationList);
			mGridView.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}
		//设置完成重新初始化，防止导致界面颜色块初始化显示错误
		flag1 = false;
		flag2 = true;
	}
	
	private void initHandler(){
		 uiHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.arg1 == GET_NAVIGATION_ITEM_START)
				{
					showProgressBar();
				}
				else if(msg.arg1 == GET_NAVIGATION_ITEM_SUCCESSFUL)
				{
					hideProgressBar();
					initData();
				}
				else if(msg.arg1 == GET_NAVIGATION_ITEM_FAILED)
				{
					hideProgressBar();	
					String strError = (msg.obj != null)?(String)msg.obj:"";
					new ErrorNotificatin(NavigationMainActivity.this).showErrorNotification(strError);
				}
			}
				
		};
	}
	
	/**
	 * 拨号提示框(自定义)
	 * 
	 * @param pTitle
	 * @param pMsg
	 */
	private void showCustomMessageDial(String pMsg,final String pNum) {
		final Dialog lDialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_hotlinedialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(pMsg);
		Button btn_ok = (Button) lDialog.findViewById(R.id.btn_dial);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// write your code to do things after users clicks OK
				Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + pNum));
				mContext.startActivity(intent);

				lDialog.dismiss();
			}
		});

		Button btn_cancel = (Button) lDialog.findViewById(R.id.btn_cancel);
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
     * 返回按钮事件
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
    	if(event.getAction()==KeyEvent.ACTION_DOWN){
	    	if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
	    		//提示框退出
				showExitMessage(getResources().getString(R.string.exit_sure2));
	    	}
    	}
    	return true;
    }
    
    /**
	 * 退出提示框(系统)
	 * @param pMsg
	 */
	private void showExitMessage(String pMsg) {
		AlertDialog.Builder builder = new Builder(NavigationMainActivity.this);
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
	private void Exit(){
		Intent exitIntent = new Intent();
		exitIntent.setAction(ActivityActionDefine.EXITAPP_ACTION);
		sendBroadcast(exitIntent);
		finishDraw();
	}
	
	/**
     * initialize receiver
     */
    private void initReceiver(){
    	mGroupReceiver = new GroupReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityActionDefine.EXITAPP_ACTION);
        registerReceiver(mGroupReceiver,filter);
    }
	
	/**
     * ***************
     * 广播接收
     * ***************
     */
    public class GroupReceiver extends BroadcastReceiver{
    	@Override
    	public void onReceive(Context context, Intent intent){
    		try{
    			String stringAction = intent.getAction();
    			Log.i("zjj", "stringAction:" + stringAction);
    			
    			if(stringAction.equals(ActivityActionDefine.EXITAPP_ACTION)){
    				finishDraw();
    			}
    		}
    		catch (Exception e) {
				// TODO: handle exception
    				e.printStackTrace();
			}
        }
	}
    
    private void initRefreshBtn(){
    	Button refreshBtn = new Button(this);
		LinearLayout.LayoutParams p_refresh = new LinearLayout.LayoutParams(MyMothod.Dp2Px(this, 32) , MyMothod.Dp2Px(this, 32));
		p_refresh.setMargins(MyMothod.Dp2Px(this, 5), 0, 0, 0);
		refreshBtn.setLayoutParams(p_refresh);
		refreshBtn.setBackgroundResource(R.drawable.refresh_btn_selector);
		refreshBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSchoolNavigationManagetment.GetNavigationList(GlobalVariables.getAgentID(), uiHandler);
			}
		});
		setTitleRightButton(refreshBtn);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	unregisterReceiver(mGroupReceiver);
    }
}
