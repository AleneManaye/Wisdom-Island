package com.drcom.drpalm.Activitys.myphoto;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.EventsListActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.Activitys.news.NewsImageAdapter;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.View.controls.MyGallery;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.MySlidingDrawer;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.controls.cache.ImageLoader.BigImageCallback;
import com.drcom.drpalm.View.controls.cache.ImageLoader.FileCallback;
import com.drcom.drpalm.View.myphoto.MyPhotoDetailActivityManager;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.MyPhoto;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 相册详细，用ViewGroup 实现画廊效果，抽屉显示相片信息，子控件用WebView实现
 * 
 * @author Administrator
 * 
 */

public class MyPhotoDetailActivity extends ModuleActivity {
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签
	public static final int FINISHEDDOWN = 3;// 下载所有完成的标签
	public static final int FREE = 4;// 下载所有完成的标签
	public static final int REFRESH = 5;// 下载所有完成的标签
	private static final int DEL = 6;// 删除成功的标签
	private static final int NOT_DEL = 7;// 删除失败的标签
	private int MAXPHOTOSSIZE = 20;	//个人相册最大值
	
	// 控件
	private MyGallery mGallery;
	private NewsImageAdapter adapter;
	private MySlidingDrawer albums_photo_lidingDrawer;// 抽屉组件
	private TextView photo_Info; // 抽屉中的相片说明
	private TextView position; // 抽屉中的相片位置
//	private TextView photoName; // 抽屉中的相片名称
	private ImageView album_photo_arrow; // 抽屉右边箭头，加动画
	private ImageView downLoad, downloadAll;//, share, collect;
	private AlphaAnimation showAnimation, hideAnimation;// 显示，隐藏动画
	private LinearLayout titlebar, toolbar, toolbarLeft, toolbarRight;
	private ProgressBar mProgressBar;
	private Button back,add ,mBtnAdd,mBtnEdit,mBtnDel,mBtnCancel;	
	private SlidingDrawer moduleDrawer;	//功能菜单
	
	// 数据
	private ArrayList<MyPhoto> mMyPhotoList = new ArrayList<MyPhoto>();
	private List<String> imageUrls;// 存放图片url

	private int lastPosition = 0;
	private int downloadCount = 0;// 要下载的个数
	private boolean isShowTool = true; // 是否显示工具栏

	private GestureDetector gestureScanner;
	private boolean isButton = false;// 点到按钮就不自动隐藏
	private boolean isInitData = true; // 是否初始化数据(从最新消息进入,此值为FALSE)
	private boolean isNomal = true;// 是否竖屏
	private boolean ifRequest = true;
	private int num = 0;// Gallery当前图片下标；
	private ImageLoader mImageLoader;
	
	private MyPhotoDetailActivityManager mMyPhotoDetailActivityManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressBar();
			HideLoadingDialog();
			switch (msg.what) {
			case DOWN:
				// 界面
				mProgressBar.setVisibility(View.GONE);
				refreshUI();
				break;
			case FREE:
				mImageLoader.loadBigPic(mMyPhotoList.get(num).url, new BigImageCallback() {

					@Override
					public void imageLoaded(String bmp) {
						if (bmp.equals(mMyPhotoList.get(num).url)) {
							Message msg = new Message();
							msg.what = REFRESH;
							mHandler.sendMessage(msg);
						}

					}
				});
				break;
			case REFRESH:
				if (null != adapter) {
					adapter.notifyDataSetChanged();
				}
				break;
			case NOT_DOWN:
				mProgressBar.setVisibility(View.GONE);
//				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
//				new ErrorNotificatin(MyPhotoDetailActivity.this).showErrorNotification(strError);
				
				String err = (String) msg.obj;
				if (err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))) {
					GlobalVariables.showInvalidSessionKeyMessage(MyPhotoDetailActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
				
				break;
			case FINISHEDDOWN:
				Log.i("xpf", "pp下载ss完成");
				DownloadProgressUtils.hideProgressDialog();
				DownloadProgressUtils.finishedProgressDialog(MyPhotoDetailActivity.this);
				break;
			case DEL:
				refreshUI();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.my_photo_gallery, mLayout_body);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			isNomal = false;
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏
			isNomal = true;
		}
		
		mMyPhotoDetailActivityManager = new MyPhotoDetailActivityManager(this);
		
//		setHasGestureToClose(false);
		// 数据
		initData();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//释放占用内存较大对像
		mGallery = null;
		adapter = null;
		mMyPhotoList = null;
		
		super.onDestroy();
	}

	/**
	 * 从数据库读取
	 */
	private void initData() {
		if (getLastNonConfigurationInstance() != null)// 取得屏幕切换前的数据{
			lastPosition = (Integer) getLastNonConfigurationInstance();
		mGallery = (MyGallery) findViewById(R.id.albumdetail_gallery);
		albums_photo_lidingDrawer = (MySlidingDrawer) findViewById(R.id.album_photo_slidingDrawer);
//		photoName = (TextView) findViewById(R.id.album_photo_text);
		position = (TextView) findViewById(R.id.album_photo_positon);
		album_photo_arrow = (ImageView) findViewById(R.id.album_photo_arrow);
		photo_Info = (TextView) findViewById(R.id.album_photo_content);
		titlebar = (LinearLayout) findViewById(R.id.linear_titlebar);
		toolbar = (LinearLayout) findViewById(R.id.linear_toolbar);
		toolbarLeft = (LinearLayout) findViewById(R.id.linear_toolbar_Left1);
		toolbarRight = (LinearLayout) findViewById(R.id.linear_toolbar_Right1);
		mProgressBar = (ProgressBar) findViewById(R.id.news_progressBar);
		back = (Button) findViewById(R.id.title_button_left1); // 返回按钮
		add = (Button) findViewById(R.id.title_button_edit); // 新增按钮
		ImageView image = (ImageView) findViewById(R.id.title_imageView1);
		image.setVisibility(View.GONE);
//		image.setImageBitmap(ModuleActivity.mBitmapTitleLogo);
		
		TextView txtviewtitle = (TextView)findViewById(R.id.title_textView1);
		txtviewtitle.setText(getString(R.string.selfalbum));
		txtviewtitle.setVisibility(View.VISIBLE);
		mImageLoader = getmSchoolImageLoader();
		titlebar.setFocusableInTouchMode(true);
		moduleDrawer = (SlidingDrawer) findViewById(R.id.drawer_menu);// 抽屉组件
		mBtnAdd = (Button)findViewById(R.id.DrawerButtonAdd);
		mBtnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;
				
				if(mMyPhotoList.size() >= MAXPHOTOSSIZE){
					String msg1 = getString(R.string.myphotomaxmsg);
					msg1 = String.format(msg1, MAXPHOTOSSIZE + "");
					Toast.makeText(MyPhotoDetailActivity.this, msg1, Toast.LENGTH_SHORT).show();
				}else{
					Intent i = new Intent(MyPhotoDetailActivity.this, AddPhotoActivity.class);
					startActivityForResult(i, 1);
					closeDrawer();
				}
			}
		});
		
		mBtnEdit = (Button)findViewById(R.id.DrawerButtonEdit);
		mBtnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;
				
				if(mMyPhotoList.size() == 0){
					return;
				}
				
				Intent i = new Intent(MyPhotoDetailActivity.this, AddPhotoActivity.class);
				i.putExtra(AddPhotoActivity.KEY_ISEDIT, true);
				i.putExtra(AddPhotoActivity.KEY_PHOTO, mMyPhotoList.get(mGallery.getSelectedItemPosition()));
				startActivityForResult(i, 1);
				closeDrawer();
			}
		});
		
		mBtnDel = (Button)findViewById(R.id.DrawerButtonDel);
		mBtnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;
				if(mMyPhotoList.size()>0){
					ShowLoadingDialog();
					mMyPhotoDetailActivityManager.delPic(mMyPhotoList.get(mGallery.getSelectedItemPosition()), mHandler);
				}
				closeDrawer();
			}
		});
		mBtnCancel = (Button)findViewById(R.id.DrawerButtonCancel);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isButton = true;
				
				closeDrawer();
			}
		});
		
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
		imageUrls = new ArrayList<String>();
		

		// 工具按钮：下载，批量下载，分享，收藏

		LayoutParams layoutParams = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Toolbar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Toolbar));
		layoutParams.setMargins(MyMothod.Dp2Px(this, 5), 0, MyMothod.Dp2Px(this, 5), 2);

		downLoad = new ImageView(MyPhotoDetailActivity.this);
		downLoad.setLayoutParams(layoutParams);
		downLoad.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_download_btn_selector));

		downloadAll = new ImageView(MyPhotoDetailActivity.this);
		downloadAll.setLayoutParams(layoutParams);
		downloadAll.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_downloadall_btn_selector));

		downLoad.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isButton = true;
				downloadCount = 1;
				int index = (int) mGallery.getSelectedItemId();
				if (mMyPhotoList.size() > index && index >= 0) {
					DownloadProgressUtils.showProgressDialog(MyPhotoDetailActivity.this);
					downLoadImage(index);// 没有就下载
				}
			}
		});
		downloadAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isButton = true;
				downloadCount = mMyPhotoList.size();
				if (mMyPhotoList.size() > 0) {
					DownloadProgressUtils.showProgressDialog(MyPhotoDetailActivity.this);
					for (int i = 0; i < mMyPhotoList.size(); i++) {
						downLoadImage(i);
					}
				}
			}
		});
		
		// 打开时
		albums_photo_lidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				isButton = true;
				album_photo_arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.album_photo_arrow_open));
			}
		});
		// 关闭时
		albums_photo_lidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
				isButton = true;
				album_photo_arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.album_photo_arrow_close));
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
		//新增按钮
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		p.addRule(RelativeLayout.CENTER_VERTICAL);
		add.setVisibility(View.VISIBLE);
		add.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_title_blue_selector));
		add.setText(getString(R.string.btn_menu));
		add.setTextAppearance(MyPhotoDetailActivity.this, R.style.TitleBtnText);
		add.setLayoutParams(p);
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isButton = true;

				if (!moduleDrawer.isOpened()) {
					openDrawer();
				}
			}
		});
		
		photo_Info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;
			}
		});
			
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setImageInfo(arg2);
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
				if (isNomal) {// 如果是竖屏
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
				return false;
			}
		});
		toolbarLeft.addView(downLoad);
		toolbarLeft.addView(downloadAll);
		if (!isNomal) {// 根据横竖屏来决定是否显示工具栏
			hideBar();
		} else {
			showBar();
		}
		
		//取本地数据并显示
		refreshUI();
		//取服务器数据
		getData();
	}

	/**
	 * refreshUI
	 */
	private void refreshUI() {
		
		
		this.mMyPhotoList = mMyPhotoDetailActivityManager.getMyPhotosFormDB();
		Log.i("zjj", "ppp REFRESH uistory_id .imgs=" + mMyPhotoList.size());

		// 初始化ViewGroup组件
		if (mMyPhotoList.size() > 0) {
			Log.i("zjj", "ppp 初始化adapter");
			imageUrls.clear();
			for (int i = 0; i < mMyPhotoList.size(); i++) {
				imageUrls.add(mMyPhotoList.get(i).url);
			}
			adapter = new NewsImageAdapter(this, imageUrls, mImageLoader);
			mGallery.setAdapter(adapter);
			mGallery.setSelection(lastPosition);

			Log.i("xpf", "ppp LASTPOSITION=0 notifyDataChanged");
		}else{
			imageUrls.clear();
			adapter = new NewsImageAdapter(this, imageUrls, mImageLoader);
			mGallery.setAdapter(adapter);
			
			photo_Info.setText("");
			position.setText("0/0");
		}
		
		
		if (mMyPhotoList.size() > 1) {
			downloadAll.setVisibility(View.VISIBLE);
		} else {
			downloadAll.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 从服务器中取数据
	 */
	private void getData(){
		mProgressBar.setVisibility(View.VISIBLE);
		if(mMyPhotoList.size() > 0){
			mMyPhotoDetailActivityManager.getMyphotoDetail(mMyPhotoList.get(mMyPhotoList.size()-1).lastupdatetime,mHandler);
		}else{
			mMyPhotoDetailActivityManager.getMyphotoDetail("0",mHandler);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGallery.onTouchEvent(ev);
		gestureScanner.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 设置当前图处的名称、位置、描述
	 * 
	 * @param index
	 *            当前图片的位置
	 */
	private void setImageInfo(int index) {
		// 设置相片信息,在抽屉组件中显示出来
//		if (mMyPhotoList.get(index).imageCaption != null)
//			photoName.setText(mMyPhoto.title);
		if (mMyPhotoList.get(index).des != null) {
			photo_Info.setTextColor(getResources().getColor(R.color.blue));
			photo_Info.setText(mMyPhotoList.get(index).des);
		}
		position.setText(String.valueOf(index + 1) + "/" + String.valueOf(mMyPhotoList.size()));// 位置
	}
	
//	private boolean storyIsRead(int stroyId){
//		if(newsDB.storyExists(stroyId))
//		{
//			NewsItem item = newsDB.retrieveNewsItem(stroyId);
//			if(item.isRead)
//			{
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * 显示上下工具栏
	 */
	private void showBar() {
		Log.i("xpf", "showBar");
		isShowTool = true;
		titlebar.clearAnimation();
		titlebar.setAnimation(showAnimation);
		toolbar.clearAnimation();
		toolbar.setAnimation(showAnimation);
		albums_photo_lidingDrawer.clearAnimation();
		albums_photo_lidingDrawer.setAnimation(showAnimation);
		showAnimation.startNow();
		mHandler.removeCallbacks(runShowScrollBar);
		toolbar.setVisibility(View.VISIBLE);
		titlebar.setVisibility(View.VISIBLE);
		downLoad.setVisibility(View.VISIBLE);
		if (mMyPhotoList.size() > 1) {
			downloadAll.setVisibility(View.VISIBLE);
		} else {
			downloadAll.setVisibility(View.GONE);
		}
		back.setVisibility(View.VISIBLE);
		albums_photo_lidingDrawer.setVisibility(View.VISIBLE);
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
		albums_photo_lidingDrawer.clearAnimation();
		albums_photo_lidingDrawer.setAnimation(hideAnimation);
		hideAnimation.startNow();
		mHandler.postDelayed(runShowScrollBar, 300);
		titlebar.setFocusableInTouchMode(false);
	}

	Runnable runShowScrollBar = new Runnable() {// 隐藏
		@Override
		public void run() {
			toolbar.setVisibility(View.INVISIBLE);
			titlebar.setVisibility(View.INVISIBLE);
			albums_photo_lidingDrawer.setVisibility(View.INVISIBLE);
			downLoad.setVisibility(View.INVISIBLE);
			downloadAll.setVisibility(View.INVISIBLE);
			back.setVisibility(View.INVISIBLE);
		}
	};

	/**
	 * 下载图片
	 * 
	 * @param index
	 */
	private void downLoadImage(int index) {
		if (mMyPhotoList.size() > 0) {
			String url = mMyPhotoList.get(index).url;
			String fileName = "myphoto_" + String.valueOf(index) + url.substring(url.lastIndexOf("."), url.length());
			getmSchoolImageLoader().loadFile(url, false, fileName, new FileCallback() {

				@Override
				public void fileLoaded(boolean isDone) {
					downloadCount--;
					Log.i("xpf", "ssDCount" + downloadCount);
					if (downloadCount == 0) {
						Message msg = new Message();
						msg.what = FINISHEDDOWN;
						mHandler.sendMessage(msg);
					}
				}
			});
		}
	}

	/**
	 * 分享 分享图片
	 */
//	private void Share() {
//		if (imageList.size() > 0) {
//			// int index = (int) mGallery.getSelectedItemId();
//			// String strtest = "</br>hello </ddd> world </br> io&nbsp;kdf";
//			// 过滤所有以<开头以>结尾的标签
//			// String regEx = "<([^>]*)>";
//			// Pattern p = Pattern.compile(regEx);
//			// Matcher m = p.matcher(mMyPhoto.share_url);
//			// Log.i("xpf", "分享" + mMyPhoto.share_url);
//			// String strbody = m.replaceAll("").trim();
//			// // 过滤&nbsp;等标签(&开头;结尾)
//			// regEx = "&([^>]*);";
//			// p = Pattern.compile(regEx);
//			// m = p.matcher(strbody);
//			// strbody = m.replaceAll("").trim();
//			// if (strbody.length() > 126)
//			// strbody = strbody.substring(0, 126) + "……";
//			// if (mMyPhoto != null)
//			// CommonActions.shareContent(AlbumDetailActivity.this,
//			// mMyPhoto.title, strbody);
//			if (mMyPhoto != null)
//				CommonActions.shareContent(MyPhotoDetailActivity.this, mMyPhoto.title, mMyPhoto.share_url);
//
//		}
//	}

	/**
	 * 保存屏幕切换之前的数据,横、竖屏
	 * 
	 */
	@Override
	public Integer onRetainNonConfigurationInstance() {
		if (mMyPhotoList.size() > 0) {
			return mGallery.getSelectedItemPosition();
		} else
			return null;
	}

	/**
	 * 没下载到图片时显示进度条
	 */
	public void showProgress() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏进度条
	 */
	public void hideProgress() {
		mProgressBar.setVisibility(View.GONE);
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
	
	//判断gallery是否在当前选项停留了1秒
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
						mHandler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		getData();
	}
	
	/**
	 * 打开抽屉
	 */
	public void openDrawer() {
		moduleDrawer.animateOpen();
	}

	/**
	 * 关闭底部抽屉
	 * 
	 */
	public void closeDrawer() {
		moduleDrawer.animateClose();
	}

}