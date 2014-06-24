package com.drcom.drpalm.Activitys.ImgsAttc;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.news.NewsImageAdapter;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.View.controls.MyGallery;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.PageIndicatorView;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.controls.cache.ImageLoader.BigImageCallback;
import com.drcom.drpalm.View.controls.cache.ImageLoader.FileCallback;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 
 * 查看附件中的图片，支持双手放大，滑动，下标显示
 * 
 * @author xpf
 * 
 */
public class ImageAttcGalleryActivity extends ModuleActivity {
	public static String KEY_IMGSURL = "KEY_IMGSURL";
	public static String KEY_IMGSNAME = "KEY_IMGSNAME";
	
	public static final int FINISHEDDOWN = 3;// 下载所有完成的标签
	public static final int FREE = 4;// 下载所有完成的标签
	public static final int REFRESH = 5;// 下载所有完成的标签
	// 控件
	private MyGallery mGallery;
	private NewsImageAdapter adapter;
	private PageIndicatorView mPageControl;
	private LinearLayout titlebar, toolbar, toolbarLeft,mLLayoutImgmsg;
	private Button back;
	private ImageView downLoad, downloadAll, share;// 工具按钮：下载，批量下载，分享，收藏
	private TextView mTextviewAttTitle,mTextviewAttPosition;	//图片信息
	
	// 数据
	private ArrayList<String> imageList;
	private ArrayList<String> imagenamesList;
	private String[] imageurls;
	private String[] imagenames;
	
	private int lastPosition = 0;
	private int downloadCount = 0;// 要下载的个数
	private boolean isShowTool = true; // 是否显示工具栏
	private boolean isButton = false;
	private boolean isClickToShowTool = false;// 是否，可以通过点击来显示、隐藏工具栏
	private AlphaAnimation showAnimation, hideAnimation;// 显示，隐藏动画
	private int pointSize;
	private GestureDetector gestureScanner;
	private int num = 0;// Gallery当前图片下标；
	private ImageLoader mImageLoader;
	private boolean isOpenFile = false;	//下载完是否要打开文件
	private String mFilename2Open = ""; //下载完要打开的文件

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == FINISHEDDOWN) {
				DownloadProgressUtils.hideProgressDialog();
				if(isOpenFile){
					DownloadProgressUtils.finishedProgressDialog(ImageAttcGalleryActivity.this,mFilename2Open);
				}else{
					DownloadProgressUtils.finishedProgressDialog(ImageAttcGalleryActivity.this);
				}
			} else if (msg.what == FREE) {
				mImageLoader.loadBigPic(imageList.get(num), new BigImageCallback() {
					@Override
					public void imageLoaded(String bmp) {
						if (bmp.equals(imageList.get(num))) {
							Message msg = new Message();
							msg.what = REFRESH;
							handler.sendMessage(msg);
						}
					}
				});
			} else if (msg.what == REFRESH) {
				adapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.news_images1, mLayout_body);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("xpf", "横屏"); // 横屏
//			toobuttonWidth = 30;
			pointSize = 10;
			isClickToShowTool = false;
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("xpf", "竖屏"); // 竖屏
//			toobuttonWidth = 56;
			pointSize = 20;
			inflater.inflate(R.layout.news_images1, mLayout_body);
			isClickToShowTool = true;
		}
//		setHasGestureToClose(false);
		// 数据
		initData();

	}

	/**
	 * 从数据库读取，初始化UI组件
	 * 
	 * @throws org.json.JSONException
	 */
	private void initData() {
		if (getLastNonConfigurationInstance() != null) { // 取得屏幕切换前的数据
			lastPosition = (Integer) getLastNonConfigurationInstance();
			Log.i("xpf", lastPosition + "lastPosition");
		}
		// getLatestNews();
		mGallery = (MyGallery) findViewById(R.id.albumdetail_gallery);
		mPageControl = (PageIndicatorView) findViewById(R.id.datamall_pageControl);
		titlebar = (LinearLayout) findViewById(R.id.linear_titlebar);
		toolbar = (LinearLayout) findViewById(R.id.linear_toolbar);
		toolbarLeft = (LinearLayout) findViewById(R.id.linear_toolbar_Left1);
		back = (Button) findViewById(R.id.title_button_left1); // 返回按钮
		mLLayoutImgmsg = (LinearLayout) findViewById(R.id.linear_attmsg);		//图片信息布局
		mTextviewAttTitle = (TextView)findViewById(R.id.textview_attname);		//图片标题
		mTextviewAttPosition = (TextView)findViewById(R.id.textview_positon);	//图片位置
		ImageView image = (ImageView) findViewById(R.id.title_imageView1);
		image.setImageBitmap(ModuleActivity.mBitmapTitleLogo);
		mImageLoader = getmClassImageLoader();
		hideProgressBar();
		hideTitle();
		hideToolbar();
		showAnimation = new AlphaAnimation(0f, 1f);
		showAnimation.setDuration(300);
		showAnimation.setFillAfter(true);
		showAnimation.setInterpolator(new LinearInterpolator());
		hideAnimation = new AlphaAnimation(1f, 0f);
		hideAnimation.setDuration(300);
		hideAnimation.setFillAfter(true);
		hideAnimation.setInterpolator(new LinearInterpolator());
		imageList = new ArrayList<String>();
		imagenamesList  = new ArrayList<String>();

		// 工具按钮：下载，批量下载，分享，收藏

		LayoutParams layoutParams = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Toolbar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Toolbar));
		layoutParams.setMargins(MyMothod.Dp2Px(this, 5), 0, MyMothod.Dp2Px(this, 5), 2);

		downLoad = new ImageView(ImageAttcGalleryActivity.this);
		downLoad.setLayoutParams(layoutParams);
		downLoad.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_download_btn_selector));
		downLoad.setVisibility(View.GONE);

		downloadAll = new ImageView(ImageAttcGalleryActivity.this);
		downloadAll.setLayoutParams(layoutParams);
		downloadAll.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_downloadall_btn_selector));
		downloadAll.setVisibility(View.GONE);

		share = new ImageView(ImageAttcGalleryActivity.this);
		share.setLayoutParams(layoutParams);
		share.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_share_btn_selector));

		downLoad.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isButton = true;
				DownloadProgressUtils.showProgressDialog(ImageAttcGalleryActivity.this);
				isButton = true;
				downloadCount = 1;
				isOpenFile = true;
				int index = (int) mGallery.getSelectedItemId();
				if (imageList.size() > index && index >= 0) {
					downLoadImage(index);// 没有就下载
				}
			}
		});
		downloadAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DownloadProgressUtils.showProgressDialog(ImageAttcGalleryActivity.this);
				isButton = true;
				downloadCount = imageList.size();
				isOpenFile = false;
				if (imageList.size() > 0) {
					for (int i = 0; i < imageList.size(); i++) {
						downLoadImage(i);
					}
				}
			}
		});

		share.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isButton = true;
			}
		});
		// 返回按钮
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;
				if (!isShowTool) {
					showBar();
				}
				finishDraw();
			}
		});
		toolbarLeft.addView(downLoad);
		toolbarLeft.addView(downloadAll);
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mPageControl.setCurrentPage(arg2);
				setImgInfo(arg2);
				num = arg2;
				if (!adapter.isHasDown()) {
					GalleryWhetherStop();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		// 添加双击显示工具栏
		gestureScanner = new GestureDetector(new MySimpleGestureListener());
		gestureScanner.setOnDoubleTapListener(new OnDoubleTapListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				Log.i("xpf", "onSingleTapConfirmedY=" + e.getY());
				if (isClickToShowTool) {
					if (!isButton) {// 如果点到的不是button就隐藏，
						if (isShowTool) {
							hideBar();
						} else {
							showBar();
						}
					} else {
						isButton = false;
					}
				}
				return false;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Log.i("xpf", "onDoubleTapY=" + e.getY());
				if (isClickToShowTool) {
					if (!isButton) {// 如果点到的不是button就隐藏，
						if (isShowTool) {
							hideBar();
						} else {
							showBar();
						}
					} else {
						isButton = false;
					}
				}
				return false;
			}
		});

		// 取参数
		Intent intent = getIntent();
		
		if(intent.hasExtra(KEY_IMGSURL)){
			imageurls = intent.getStringArrayExtra(KEY_IMGSURL);
			for(int i = 0; i < imageurls.length; i++){
				imageList.add(imageurls[i]); 
			}
		}
			
		if(intent.hasExtra(KEY_IMGSNAME)){
			imagenames = intent.getStringArrayExtra(KEY_IMGSNAME);
			for(int i = 0; i < imagenames.length; i++){
				imagenamesList.add(imagenames[i]); 
			}
		}
		
		Log.i("xpf", "图片数目为：" + imageList.size());
		if (imageList.size() > 0)
			refreshUI();
		
		if (isClickToShowTool) {// 根据横竖屏来决定是否显示工具栏
			showBar();
		} else {
			hideBar();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGallery.onTouchEvent(ev);
		gestureScanner.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * refreshUI
	 */
	private void refreshUI() {
		// 初始化ViewGroup组件
		if (imageList.size() > 0) {
			adapter = new NewsImageAdapter(this, imageList, mImageLoader);
			mGallery.setAdapter(adapter);
			mGallery.setSelection(lastPosition);
			mPageControl.setCount(imageList.size());
			mPageControl.setCurrentPage(lastPosition);
			mPageControl.setPointSize(pointSize);
			downLoad.setVisibility(View.VISIBLE);
		} else {
			downLoad.setVisibility(View.GONE);
		}
		if (imageList.size() > 1) {
			downloadAll.setVisibility(View.VISIBLE);
		} else {
			downloadAll.setVisibility(View.GONE);
		}
		
		//图片信息是否可视
		if(imagenamesList.size()>0){
			mLLayoutImgmsg.setVisibility(View.VISIBLE);
			mPageControl.setVisibility(View.GONE);
		}else{
			mLLayoutImgmsg.setVisibility(View.GONE);
			mPageControl.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置图片信息
	 */
	private void setImgInfo(int index){
		if(imagenamesList.size()>0){
			mTextviewAttTitle.setText(imagenamesList.get(index));
			mTextviewAttPosition.setText((index + 1) + "/" + imagenamesList.size());
		}
	}
	
	/**
	 * 显示上下工具栏
	 */
	private void showBar() {
		isShowTool = true;
		titlebar.clearAnimation();
		titlebar.setAnimation(showAnimation);
		toolbar.clearAnimation();
		toolbar.setAnimation(showAnimation);
		
		handler.removeCallbacks(runShowScrollBar);
		toolbar.setVisibility(View.VISIBLE);
		titlebar.setVisibility(View.VISIBLE);
		downLoad.setVisibility(View.VISIBLE);
		if (imageList.size() > 1) {
			downloadAll.setVisibility(View.VISIBLE);
		} else {
			downloadAll.setVisibility(View.GONE);
		}
		
		if(imagenamesList.size() > 1){
			mLLayoutImgmsg.clearAnimation();
			mLLayoutImgmsg.setAnimation(showAnimation);
		}else{
			mPageControl.clearAnimation();
			mPageControl.setAnimation(showAnimation);
			mPageControl.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏上下工具栏
	 */
	private void hideBar() {
		isShowTool = false;
		titlebar.clearAnimation();
		titlebar.setAnimation(hideAnimation);
		toolbar.clearAnimation();
		toolbar.setAnimation(hideAnimation);
		
		handler.postDelayed(runShowScrollBar, 500);
		if(imagenamesList.size() > 1){
			mLLayoutImgmsg.clearAnimation();
			mLLayoutImgmsg.setAnimation(hideAnimation);
		}else{
			mPageControl.clearAnimation();
			mPageControl.setAnimation(hideAnimation);
		}
	}

	Runnable runShowScrollBar = new Runnable() {// 隐藏
		@Override
		public void run() {
			toolbar.setVisibility(View.INVISIBLE);
			titlebar.setVisibility(View.INVISIBLE);
			downLoad.setVisibility(View.INVISIBLE);
			downloadAll.setVisibility(View.INVISIBLE);
			
			if(imagenamesList.size()==0){
				mPageControl.setVisibility(View.INVISIBLE);
			}
		}
	};

	/**
	 * 下载图片
	 * 
	 * @param index
	 */
	private void downLoadImage(int index) {
		if (imageList.size() > 0) {
			String url = imageList.get(index);
			downLoadImage(downloadCount, url, handler);
		}
	}

	/**
	 * 保存屏幕切换之前的数据
	 * 
	 */
	@Override
	public Integer onRetainNonConfigurationInstance() {
		if (imageList.size() > 0) {
			return mGallery.getSelectedItemPosition();
		} else
			return null;
	}

	/**
	 * 手势监听
	 * 
	 * @author Administrator
	 * 
	 */
	private class MySimpleGestureListener extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}
	}

	private void GalleryWhetherStop() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					int index = 0;
					index = num;
					Thread.sleep(1000);
					if (index == num) {
						Message msg = new Message();
						msg.what = FREE;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}
	
	/**
	 * 下载图片
	 * @param url
	 * @param h
	 */
	private void downLoadImage(int imgsSum ,String url,final Handler h){
		downloadCount = imgsSum;
		String fileName = url.substring(url.lastIndexOf("/"), url.length());
		if(isOpenFile)
			mFilename2Open = fileName;
		getmClassImageLoader().loadFile(url, false, fileName, new FileCallback() {

			@Override
			public void fileLoaded(boolean isDone) {
				downloadCount--;
				Log.i("xpf", "ssDCount" + downloadCount);
				if (downloadCount == 0) {
					Message msg = new Message();
					msg.what = FINISHEDDOWN;
					h.sendMessage(msg);
				}
			}
		});
	}
}
