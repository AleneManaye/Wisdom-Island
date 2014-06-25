package com.drcom.drpalm.Activitys.tours;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.ResourceManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.wisdom.island.R;

/**
 * Tours
 * @author zhaojunjie
 *
 */
public class ToursAcitivity extends ModuleActivity {
	//变量
	private String URL = "";
	private boolean mIsErrorPage = false;
	//控件
	private WebView webview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.tours_view, mLayout_body);
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}
		
//		Log.i("zjj", "xxxxxxxxx" + webview.getUrl());
		
		//WEBVIEW
	    webview = (WebView) findViewById(R.id.webview);
	    webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);	//自适应
	    webview.getSettings().setBuiltInZoomControls(false); 	//禁止缩放
	    //让网页自适应手机屏幕
//	    webview.getSettings().setUseWideViewPort(true);
//	    webview.getSettings().setLoadWithOverviewMode(true);
//	    //不显示滚动条
	    webview.setHorizontalScrollBarEnabled(false);
	    webview.setVerticalScrollBarEnabled(false);
//	    webview.setInitialScale(39); 
	    //支持JS
	    webview.getSettings().setJavaScriptEnabled(true);
		// 滚动框
		webview.setWebViewClient(new WebViewClient() {
			//重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mIsErrorPage = false;
				view.loadUrl(url);
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
				view.clearView();
				mIsErrorPage = true;
			}
		});
		
		hideToolbar();
		initTours();
//		setTitleText(getString(R.string.tours));
	}
	
	private void initTours(){
		ResourceManagement r = new ResourceManagement();//.getResourceManagement();
		if(!r.getToursPath().equals("")){
//			if(LanguageManagement.getSysLanguage(ToursAcitivity.this) == CurrentLan.COMPLES_CHINESE){
//				URL = "file://" + r.getToursPath(CurrentLan.COMPLES_CHINESE) + getString(R.string.toursindex);
//			}else if(LanguageManagement.getSysLanguage(ToursAcitivity.this) == CurrentLan.ENGLISH){
//				URL = "file://" + r.getToursPath(CurrentLan.ENGLISH) + getString(R.string.toursindex);
//			}else{
//				URL = "file://" + r.getToursPath() + getString(R.string.toursindex);
//			}
			
			URL = "file://" + r.getToursPath(LanguageManagement.getSysLanguage(ToursAcitivity.this)) + getString(R.string.toursindex);
			
			webview.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					webview.loadUrl(URL);//("http://www.baidu.com");
				}
			}, 200);
		}else{
			//没下载资源包,则加载默认页面
			if(LanguageManagement.getSysLanguage(ToursAcitivity.this) == CurrentLan.COMPLES_CHINESE){
				webview.loadUrl("file:///android_asset/tours_t/index.htm");
			}else if(LanguageManagement.getSysLanguage(ToursAcitivity.this) == CurrentLan.ENGLISH){
				webview.loadUrl("file:///android_asset/tours_en/index.htm");  
			}else{
				webview.loadUrl("file:///android_asset/tours/index.htm");  
			}
			
		}
		r = null;
	}
	
	/**
	 * 截取返回按钮事件
	 */
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (mIsErrorPage) {
			return super.onKeyDown(keyCoder, event);
		}

		if (webview.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
			webview.goBack(); // goBack()表示返回webView的上一页面
			return true;
		}
		return super.onKeyDown(keyCoder, event);
	}
}
