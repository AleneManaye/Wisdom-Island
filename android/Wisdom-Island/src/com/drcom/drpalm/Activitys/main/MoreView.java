package com.drcom.drpalm.Activitys.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.consultation.FeedBackActivity;
import com.drcom.drpalm.Activitys.events.sent.EventsSentDetailActivity;
import com.drcom.drpalm.Activitys.setting.AboutApplicationActivity;
import com.drcom.drpalm.Activitys.setting.AccountManageActivity;
import com.drcom.drpalm.Activitys.setting.SystemActivity;
import com.drcom.drpalm.Activitys.web.WebviewActivity;
import com.drcom.drpalm.View.controls.PageControlView;
import com.drcom.drpalm.View.events.sent.EventsSentDetailActivityManagement;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.login.LoginManager.LogoutCallback;
import com.drcom.drpalm.objs.ShareToFriendItem;
import com.wisdom.island.R;

public class MoreView extends LinearLayout {
	public static int REQUESTCODE_REFLASHWEB = 99;
	public String URLPARAM_SESSIONKEY = "&sessionkey=";
	public String URLPARAM_SCHOOLID = "&schoolid=";

	// 变量
	private Context mContext;
	private LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);

	// 控件
	private RelativeLayout mLayoutAccountmanage;
	private RelativeLayout mLayoutSystem;
	private RelativeLayout mLayoutSharemanage;
	private RelativeLayout mLayoutHotlinemanage;
	private RelativeLayout mLayoutAboutmanage;
	private RelativeLayout mLayoutAdvicefeedback;// 意见反馈
	private RelativeLayout mLayoutHelp;// 帮助
	private RelativeLayout mLayoutKindergartens;// 帮助
	private Button mButtonLogout;
	private TextView mTextViewAccountmanage;
	private TextView mTextViewSetting;
	private SlidingDrawer mDrawer;
	private WebView mWebView;
	private ImageView handle;
	private LinearLayout mContent;
	private String url = "";
	private Button cancel;
	private SlidingDrawer tellFriendDrawaer;
	private List<ShareToFriendItem> moreList=new ArrayList<ShareToFriendItem>();
	private PageControlView myPageControl;
	private FrameLayout viewPageContainer;

	public MoreView(Context context) {
		super(context);
		mContext = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_more, this);
		mDrawer = (SlidingDrawer) findViewById(R.id.drawer);
		handle = (ImageView) findViewById(R.id.handle);
//		handle.setBackgroundColor(mContext.getResources().getColor(R.color.bgblue));
		mContent = (LinearLayout) findViewById(R.id.content);
//		mContent.setBackgroundColor(mContext.getResources().getColor(R.color.bgblue));

		mWebView = (WebView) findViewById(R.id.myWebView);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(false);

		mWebView.scrollTo(0, 0);
		mDrawer.open();
		mWebView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 如果有打开新网页标识时,则新建一个网页窗口
				if (url.indexOf("newwindow=true") > -1) {
					Intent intent = new Intent(mContext, WebviewActivity.class);
					intent.putExtra(WebviewActivity.URL_KEY, url);
					((Activity) mContext).startActivityForResult(intent, REQUESTCODE_REFLASHWEB);
				} else {
					view.loadUrl(url); // 当开启新的页面的时候用webview来进行处理而不是用系统自带的浏览器处理
				}
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// TODO Auto-generated method stub
				// super.onReceivedError(view, errorCode, description,
				// failingUrl);
				view.loadUrl(GlobalVariables.URL_ERROR);
			}
		});
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				mWebView.scrollTo(0, 0);
				mDrawer.setClickable(true);
				handle.setImageDrawable(getResources().getDrawable(R.drawable.sliding_drawer_handle_bottom_down));
				// mWebView.scrollTo(0, 0);
			}
		});
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				handle.setImageDrawable(getResources().getDrawable(R.drawable.sliding_drawer_handle_bottom_up));
				mDrawer.setClickable(false);
			}
		});
		mDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener() {

			@Override
			public void onScrollStarted() {
				// mWebView.scrollTo(0, 0);

			}

			@Override
			public void onScrollEnded() {
				// if (mDrawer.isOpened()) {
				// mWebView.scrollTo(0, 0);
				// }

			}
		});

		mLayoutAccountmanage = (RelativeLayout) findViewById(R.id.setting_accountmanage_RLayout);
		mLayoutAccountmanage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, AccountManageActivity.class);
				mContext.startActivity(i);
			}
		});

		mLayoutSystem = (RelativeLayout) findViewById(R.id.setting_systemsetting_RLayout);
		mLayoutSystem.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, SystemActivity.class);
				mContext.startActivity(i);
			}
		});
		
		//告诉朋友
		moreList = GlobalVariables.shareList;
		mLayoutSharemanage = (RelativeLayout) findViewById(R.id.setting_share_RLayout);
		mLayoutSharemanage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String subject = mContext.getResources().getString(R.string.sharesubject);
				String url = mContext.getResources().getString(R.string.shareurl);
				shareContent(mContext, subject, url);
				
			}
		});
//		tellFriendDrawaer = (SlidingDrawer)findViewById(R.id.tellFriend_drawer);
//		String subject = mContext.getResources().getString(R.string.sharesubject);
//		String title = mContext.getResources().getString(R.string.sharetitle);
//		String url = mContext.getResources().getString(R.string.shareurl);
//		new ShareToFriendViewHelper(mLayoutSharemanage, tellFriendDrawaer, moreList, mContext,subject,title+url,null);
		
		
		//客服
		mLayoutHotlinemanage = (RelativeLayout) findViewById(R.id.setting_hotline_RLayout);
		mLayoutHotlinemanage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showCustomMessageDial(getResources().getString(R.string.hotlinetitle));
			}
		});

		//关于
		mLayoutAboutmanage = (RelativeLayout) findViewById(R.id.setting_about_RLayout);
		mLayoutAboutmanage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, AboutApplicationActivity.class);
				mContext.startActivity(i);
			}
		});
		// 意见反馈
		mLayoutAdvicefeedback = (RelativeLayout) findViewById(R.id.setting_advicefeedback_RLayout);
		mLayoutAdvicefeedback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIt = new Intent(mContext, FeedBackActivity.class);
				mContext.startActivity(returnIt);
			}
		});
		// 帮助
		mLayoutHelp = (RelativeLayout) findViewById(R.id.setting_help_RLayout);
		mLayoutHelp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIt = new Intent(mContext, WebviewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(WebviewActivity.URL_KEY, GlobalVariables.URL_HELP);
				returnIt.putExtras(bundle);
				mContext.startActivity(returnIt);
			}
		});
		// 幼儿园内集合
		mLayoutKindergartens = (RelativeLayout) findViewById(R.id.setting_kindergartens_RLayout);
		mLayoutKindergartens.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIt = new Intent(mContext, WebviewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(WebviewActivity.URL_KEY, GlobalVariables.URL_EBABYSET + mContext.getString(R.string.kgcollectionid));
				returnIt.putExtras(bundle);
				mContext.startActivity(returnIt);
			}
		});
		
		//注销
		mButtonLogout = (Button) findViewById(R.id.button_logout);
		mButtonLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginManager loginmanager = LoginManager.getInstance(mContext);
				loginmanager.logout(new LogoutCallback() {

					@Override
					public void onLogOut(boolean bSuccess) {
						if (bSuccess) //
						{
							GlobalVariables.toastShow(mContext.getResources().getString(R.string.logoutsucceed));
						}

					}
				});
			}
		});

		mTextViewAccountmanage = (TextView) findViewById(R.id.setting_accountmanage_TextView);
		mTextViewSetting = (TextView) findViewById(R.id.setting_systemsetting_TextView);

		ReflashUI();
	}

	/**
	 * 分享
	 * 
	 * @param context
	 * @param subject
	 * @param url
	 */
	private void shareContent(Context context, String subject, String url) {

		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, url);
		context.startActivity(Intent.createChooser(intent, "Share"));

	}

	/**
	 * 
	 */
	public void ReflashUI() {
		Log.i("zjj", "instance.getOnlineStatus()" + instance.getOnlineStatus());

		if (LoginManager.OnlineStatus.OFFLINE == instance.getOnlineStatus()) {
			mLayoutAccountmanage.setClickable(false);

			mTextViewAccountmanage.setTextColor(getResources().getColor(R.color.dark_gray));
			mLayoutAccountmanage.setVisibility(View.GONE);
			mButtonLogout.setVisibility(View.GONE);
			
			url = GlobalVariables.URL_EBABYCHANNEL + GlobalVariables.Devicdid;
		} else {
			mLayoutAccountmanage.setClickable(true);

			mTextViewAccountmanage.setTextColor(getResources().getColor(R.color.black));
			mLayoutAccountmanage.setVisibility(View.VISIBLE);
			mButtonLogout.setVisibility(View.VISIBLE);
			
			url = GlobalVariables.URL_EBABYCHANNEL 
			+ GlobalVariables.Devicdid
			+ URLPARAM_SESSIONKEY
			+ GlobalVariables.gSessionKey
			+ URLPARAM_SCHOOLID
			+ GlobalVariables.gSchoolId;
		}
		
		/*
		 * modify by kk for test
		 */
		EventsSentDetailActivityManagement mEventsSentDetailActivityManagement = new EventsSentDetailActivityManagement(mContext , "");
		String templateHtml =  mEventsSentDetailActivityManagement.readTextFromResource(R.raw.chanel);
		mWebView.loadDataWithBaseURL("file:///android_res/raw/", templateHtml, "text/html", "UTF-8", null);
//		mWebView.loadUrl(url);
	}

	/**
	 * 拨号提示框(自定义)
	 * 
	 * @param pTitle
	 * @param pMsg
	 */
	private void showCustomMessageDial(String pMsg) {
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
				Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + mContext.getResources().getString(R.string.phonelink)));
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
	 * 拨号提示框(系统)
	 * 
	 * @param pMsg
	 */
	// private void showDialMessage(String pMsg) {
	// AlertDialog.Builder builder = new Builder(mContext);
	// builder.setMessage(pMsg);
	// builder.setPositiveButton(R.string.OK, new
	// DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	// Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:"
	// + mContext.getResources().getString(R.string.phonelink)));
	// mContext.startActivity(intent);
	//
	// dialog.dismiss();
	// }
	// });
	//
	// builder.setNegativeButton(R.string.Cancel, new
	// DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	//
	// builder.create().show();
	// }
}
