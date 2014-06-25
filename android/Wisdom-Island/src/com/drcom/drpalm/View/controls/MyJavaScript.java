package com.drcom.drpalm.View.controls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.Toast;

import com.drcom.drpalm.Activitys.ImgsAttc.ImageAttcGalleryActivity;
import com.wisdom.island.R;

/**
 * 
 * 该类中定义了在WebView中点击相关连接时， 响应点击的javascript(定义在HTML中)或者一些其它操作(Activity中)
 * 
 * @author xpf
 * @param context
 *            上下文
 * @param Handler
 * @param String
 *            json 在.html想要显示的数据，
 */
public class MyJavaScript {
	private WebView mWebView;
	// 使用一个handler来处理加载事件
	private Handler mHandler;
	private Context context;
	private String json;

	public MyJavaScript(Context context, Handler handler, String json) {
		this.mHandler = handler;
		mWebView = (WebView) ((Activity) context)
				.findViewById(R.id.news_detail_content);
		this.context = context;
		this.json = json;
	}

	/**
	 * 由html页面中调用该方法,显示新闻详细的内容
	 */
	public void clickOnAndroid() {
		mHandler.post(new Runnable() {
			public void run() {
				String javascript = "javascript:init()";
				mWebView.loadUrl(javascript);
			}
		});
	}

	/**
	 * 由HTML页面调用,点击动态生成的button,
	 * 
	 * @param num
	 *            list序号
	 */
	public void share() {
		Toast.makeText(context, "分享", 1).show();
	};

	/**
	 * 由HTML页面调用,点击动态生成的button,
	 * 
	 * @param num
	 *            list序号
	 */
	public void bookmark() {
		Toast.makeText(context, "添加书签", 1).show();
	};

	/**
	 * 显示附件中的图片
	 * 
	 */
	public void zoom() {
		Toast.makeText(context, "查看附件", 1).show();
		Intent intent = new Intent(context, ImageAttcGalleryActivity.class);
		intent.putExtra("json", json);
		context.startActivity(intent);
	};

}
