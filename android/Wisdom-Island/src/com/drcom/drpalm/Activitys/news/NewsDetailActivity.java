package com.drcom.drpalm.Activitys.news;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ZoomButtonsController;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.ImgsAttc.ImageAttcGalleryActivity;
import com.drcom.drpalm.Activitys.shares.ShareToFriendViewHelper;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Tool.CommonActions;
import com.drcom.drpalm.View.controls.MyMainItem;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.MyWebview;
import com.drcom.drpalm.View.news.NewsDetailActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NewsItem;
import com.wisdom.island.R;

/**
*
* 新闻详细
*
* @author xpf
* @param story_id
*            新闻详细ID
*/
public class NewsDetailActivity extends ModuleActivity {
	public static String KEY_INIT = "KEY_INIT";
	private int story_id;// 新闻详细ID
	private MyWebview mWebView;// 网页控件
	private NewsDB newsDB;
	private NewsItem mNewsItem = new NewsItem();
	private boolean isInitData = true; // 是否初始化数据(从最新消息进入,此值为FALSE)

	private MyMainItem attc;
	private ImageView share, collect;
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签
	private SlidingDrawer tellFriendDrawaer;// share抽屉控件
	private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");

	private NewsDetailActivityManagement mNewsDetailActivityManagement;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressBar();
			switch (msg.what) {
			case DOWN:
				refreshUI();
				break;
			case NOT_DOWN:
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				new ErrorNotificatin(NewsDetailActivity.this).showErrorNotification(strError);
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater mLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutInflater.inflate(R.layout.news_detail_html, mLayout_body);

		mNewsDetailActivityManagement = new NewsDetailActivityManagement(this);
		// 初始化数据库组件
		initNewsDetail();
	}

	/**
	 * 初始化相关变量
	 *
	 */
	private void initNewsDetail() {
		// 获取story_id、category
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("story_id")) {
			story_id = extras.getInt("story_id");
		}
		if (extras.containsKey(KEY_INIT)) {
			isInitData = extras.getBoolean(KEY_INIT);
		}
		// 初始化布局组件
		mWebView = (MyWebview) findViewById(R.id.news_detail_content);
		// newsDetailProgressBar.setVisibility(View.VISIBLE);// 显示正在下载
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);	//自适应
		mWebView.setVisibility(View.GONE);
		
		// 数据库对象
		newsDB = NewsDB.getInstance(NewsDetailActivity.this, GlobalVariables.gSchoolKey);
		// 工具按钮：下载，批量下载，分享，收藏

		LayoutParams layoutParams = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Toolbar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Toolbar));
		layoutParams.setMargins(MyMothod.Dp2Px(this, 5), 0, MyMothod.Dp2Px(this, 5), 2);

		attc = new MyMainItem(NewsDetailActivity.this);
		attc.setLayoutParams(layoutParams);
//		attc.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_attc_btn_selector));
//		attc.setVisibility(View.GONE);
		attc.setSize(GlobalVariables.btnWidth_Toolbar,GlobalVariables.btnHeight_Toolbar);
		attc.setDelVisible(false);
		attc.setImgRsid(R.drawable.detail_attc_btn_selector);
		attc.setText("");

		share = new ImageView(NewsDetailActivity.this);
		share.setLayoutParams(layoutParams);
		share.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_share_btn));

		collect = new ImageView(NewsDetailActivity.this);
		collect.setLayoutParams(layoutParams);
		collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_unbookmark_btn));

		ToolbarAddLeftButton(attc);
		// ToolbarAddLeftButton(downloadAll);

		ToolbarAddRightButton(share);
		ToolbarAddRightButton(collect);
		attc.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String[] urls = new String[mNewsItem.otherImgs.size()];
				for(int i = 0;i < urls.length; i++){
					urls[i] = mNewsItem.otherImgs.get(i).smallURL;
				}
				
				Intent i = new Intent(NewsDetailActivity.this, ImageAttcGalleryActivity.class);
				i.putExtra(ImageAttcGalleryActivity.KEY_IMGSURL, urls); 
				NewsDetailActivity.this.startActivity(i);
			}
		});
		tellFriendDrawaer = (SlidingDrawer)findViewById(R.id.tellFriend_drawer);
		
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Share();// 分享
			}
		});
		collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isMark = mNewsItem.bookmark ? false : true;
				newsDB.updateBookmarkStatus(mNewsItem, isMark);
				mNewsItem.bookmark = isMark;
				if (isMark)
					collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_bookmark_btn));
				else
					collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_unbookmark_btn));
			}
		});
		// 从网络下载指定sotry_id的新闻详细信息，并存入数据库
		requestNewsDetails(story_id);
	}

	/**
	 * 下载新闻详细信息
	 *
	 * @param story_id
	 *            新闻ID
	 */
	private void requestNewsDetails(int story_id) {
		showProgressBar();
		// 是否取得此记录的所有信息
		boolean ifRequest = true;
		int getall = 0;
		if (!isInitData) {
			getall = 1;
		}
		if (newsDB.storyExists(story_id)) {
			this.mNewsItem = mNewsDetailActivityManagement.getDataItem(story_id);//newsDB.retrieveNewsItem(story_id);
			if (mNewsItem.isRead) {// 已经下载了就不用下载了
				ifRequest = false;
			} else {
				ifRequest = true;
			}
		} else {
			ifRequest = true;
		}
		if (ifRequest) {
			mNewsDetailActivityManagement.sendGetNeededInfo(mHandler, story_id, getall);
		} else {
			refreshUI();
		}
	}

	/**
	 * 刷新UI界面
	 *
	 * @param sotry_id
	 *            新闻详细ID
	 */
	private void refreshUI() {
		showProgressBar();
		// standard view
		this.mNewsItem = mNewsDetailActivityManagement.getDataItem(story_id); //newsDB.retrieveNewsItem(story_id);
		if (this.mNewsItem != null) {// 非空检查
			newsDB.updateReadStatus(mNewsItem, true);// 是否已读
			
			// Web template
//			mWebView.setFocusable(false);
			mWebView.setVisibility(View.VISIBLE);
			String templateHtml = readTextFromResource(R.raw.news_detail);

			// Set Title
			templateHtml = templateHtml.replace("__TITLE__", mNewsItem.title);

			// Set Author
			templateHtml = templateHtml.replace("__AUTHOR__", mNewsItem.author);

			// Set Date
			templateHtml = templateHtml.replace("__DATE__", sdf.format(mNewsItem.postDate));

			// Set Description
			templateHtml = templateHtml.replace("__DEK__", mNewsItem.summary);

			// Set Image Count
			int galleryCount = 0;
			if (mNewsItem.otherImgs.size() != 0) {
				templateHtml = templateHtml.replace("__THUMBNAIL_URL__", mNewsItem.thumb_url);
				galleryCount = mNewsItem.otherImgs.size();
			}
			templateHtml = templateHtml.replace("__GALLERY_COUNT__", galleryCount + "");
			

			// Set Body
			templateHtml = templateHtml.replace("__BODY__", mNewsItem.body);

			mWebView.getSettings().setJavaScriptEnabled(true);
//			mWebView.getSettings().setSupportZoom(false);
			mWebView.getSettings().setBuiltInZoomControls(true); 	//不显示放大缩小按钮
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
				mWebView.getSettings().setDisplayZoomControls(false);	//可缩放
			}
			mWebView.setWebChromeClient(new MyWebChromeClient());
			mWebView.setVerticalScrollbarOverlay(true);
			mWebView.setHorizontalScrollbarOverlay(true);
			mWebView.addJavascriptInterface(new JavaScriptInterface(), "newsDetail");
			mWebView.loadDataWithBaseURL("file:///android_res/raw/", templateHtml, "text/html", "UTF-8", null);
			
			setTitleText(ModuleNameDefine.getNewsModuleNamebyId(this.mNewsItem.category_id)+getString(R.string.detail));
			
			//生成share界面
//			new ShareToFriendViewHelper(share, tellFriendDrawaer, GlobalVariables.shareList, NewsDetailActivity.this,mNewsItem.title, mNewsItem.share_url,getToolbar());
		
			if (mNewsItem.bookmark)
				collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_bookmark_btn));
			else
				collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_unbookmark_btn));
			
			if (galleryCount > 0){// 是否隐藏查看附件按钮
				attc.setVisibility(View.VISIBLE);
				attc.setNum(galleryCount+"");
			}
			else
				attc.setVisibility(View.GONE);
		}
		hideProgressBar();
	}

	/**
	 * Provides a hook for calling "alert" from javascript. Useful for debugging
	 * your javascript.
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			result.confirm();
			return true;
		}
	}

	private String readTextFromResource(int newsDetail) {
		InputStream raw = getResources().openRawResource(newsDetail);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		int i;
		try {
			i = raw.read();
			while (i != -1) {
				stream.write(i);
				i = raw.read();
			}
			raw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toString();
	}

	final class JavaScriptInterface {

		JavaScriptInterface() {
		}

		/**
		 * This is not called on the UI thread. Post a runnable to invoke
		 * loadUrl on the UI thread.
		 */

		public void clickViewImage() {
			mHandler.post(new Runnable() {
				public void run() {
					mNewsDetailActivityManagement.startImageGalleryActivity(mNewsItem);
				}
			});
		}

	}

	/**
	 * 隐藏zoom控件
	 *
	 * @param view
	 */
	public void setZoomControlGone(View view) {
		@SuppressWarnings("rawtypes")
		Class classType;
		java.lang.reflect.Field field;
		try {
			classType = WebView.class;
			field = classType.getDeclaredField("mZoomButtonsController");
			field.setAccessible(true);
			ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
			mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
			try {
				field.set(view, mZoomButtonsController);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Activity Leaked是webview的 ZoomButton，也就是那两个放大和缩小的按钮，导致的。如果设置为让他们出现，
	 * 并且可以自动隐藏，那么，由于他们的自动隐藏是一个渐变的过程，所以在逐渐消失的过程中 如果调用了父容器的destroy方法，就会导致Leaked。
	 * 所以解决方案是，在destroy之前，先让他俩立马消失
	 */
	@Override
	protected void onDestroy() {
		// if (mWebView != null) {
		// mWebView.getSettings().setBuiltInZoomControls(true);
		// mWebView.setVisibility(View.GONE);
		// long timeout = ViewConfiguration.getZoomControlsTimeout();
		// new Timer().schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// mWebView.destroy();
		// }
		// }, timeout); // 注意，这个timeout就是那两个放大缩小的按钮的消失的时间，你可以给他加上一，二
		// }
		super.onDestroy();
	}

	/**
	 * 分享
	 */
	private void Share() {
		if (mNewsItem != null) {
			// String strtest = "</br>hello </ddd> world </br> io&nbsp;kdf";
			// 过滤所有以<开头以>结尾的标签
			// String regEx = "<([^>]*)>";
			// Pattern p = Pattern.compile(regEx);
			// Matcher m = p.matcher(mNewsItem.title + ":" +
			// mNewsItem.share_url);
			//
			// String strbody = m.replaceAll("").trim();
			// // 过滤&nbsp;等标签(&开头;结尾)
			// regEx = "&([^>]*);";
			// p = Pattern.compile(regEx);
			// m = p.matcher(strbody);
			// strbody = m.replaceAll("").trim();
			// if (strbody.length() > 126)
			// strbody = strbody.substring(0, 126) + "……";
			// CommonActions.shareContent(NewsDetailActivity.this,
			// mNewsItem.title, strbody);
			CommonActions.shareContent(NewsDetailActivity.this, mNewsItem.title, mNewsItem.share_url);
			
		}
	}
}
