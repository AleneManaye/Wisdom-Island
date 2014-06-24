package com.drcom.drpalm.Activitys.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm4tianzhujiao.R;

public class WebviewActivity extends ModuleActivity {
	public static String URL_KEY = "URL_KEY";
	public static String URL_ERROR_KEY = "URL_ERROR_KEY";
	
	private String mErrorPagePath = "";	//外部指定的错误页面路径
	private String URL;
	private boolean mIsErrorPage = false;
	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.web_view, mLayout_body);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(URL_KEY)){
			URL = extras.getString(URL_KEY);
		}
		if(extras.containsKey(URL_ERROR_KEY)){
			mErrorPagePath = extras.getString(URL_ERROR_KEY);
		}
		
		Log.i("zjj", "打开网页:" + URL);
		
		//WEBVIEW
	    webview = (WebView) findViewById(R.id.webview);
	    //让网页自适应手机屏幕
//	    webview.getSettings().setUseWideViewPort(true);
//	    webview.getSettings().setLoadWithOverviewMode(true);
	    //全屏
//	    webview.setHorizontalScrollBarEnabled(false);
//	    webview.setVerticalScrollBarEnabled(false);
	    //支持JS
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.loadUrl(URL);//("http://www.baidu.com");
		// 滚动框
		webview.setWebViewClient(new WebViewClient() {
			//重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mIsErrorPage = false;
//				view.loadUrl(url);
				
				//2013-12-30
				//如果有打开新网页标识时,则新建一个网页窗口
				if (url.indexOf("newwindow=true") > -1) {
					Intent intent = new Intent(WebviewActivity.this, WebviewActivity.class);
					intent.putExtra(WebviewActivity.URL_KEY, url.replace("newwindow=true", ""));
					startActivity(intent);
				} else {
					view.loadUrl(url); // 当开启新的页面的时候用webview来进行处理而不是用系统自带的浏览器处理
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				showProgressBar();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				hideProgressBar();
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
//				super.onReceivedError(view, errorCode, description, failingUrl);
				if(mErrorPagePath.equals("")){
					view.loadUrl(GlobalVariables.URL_ERROR);  
				}else{
					view.loadUrl(mErrorPagePath);
				}
				mIsErrorPage = true;
			}
		});
		
		hideToolbar();
		
		//Titlebar返回按钮事件
		SetBackBtnOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Back();
			}
		});
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
		if(mIsErrorPage){
			setResult(RESULT_OK);//(MoreView.REQUESTCODE_REFLASHWEB);
			finishDraw();
			return;
		}
		
		if (webview.canGoBack()) {
			webview.goBack(); // goBack()表示返回webView的上一页面
			return;
		}
		
		setResult(RESULT_OK);//(MoreView.REQUESTCODE_REFLASHWEB);
		finishDraw();
	}
}
