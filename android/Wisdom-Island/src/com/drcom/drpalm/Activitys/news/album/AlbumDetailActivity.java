package com.drcom.drpalm.Activitys.news.album;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import android.widget.SlidingDrawer;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.news.NewsDetailActivity;
import com.drcom.drpalm.Activitys.news.NewsImageAdapter;
import com.drcom.drpalm.Activitys.shares.ShareToFriendViewHelper;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Tool.CommonActions;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.View.controls.MyGallery;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.MySlidingDrawer;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.controls.cache.ImageLoader.BigImageCallback;
import com.drcom.drpalm.View.controls.cache.ImageLoader.FileCallback;
import com.drcom.drpalm.View.news.album.AlbumDetailActivityManager;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm.objs.NewsItem.Image;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 相册详细，用ViewGroup 实现画廊效果，抽屉显示相片信息，子控件用WebView实现
 * 
 * @author Administrator
 * 
 */

public class AlbumDetailActivity extends ModuleActivity {
	public static String KEY_INIT = "KEY_INIT";
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签
	public static final int FINISHEDDOWN = 3;// 下载所有完成的标签
	public static final int FREE = 4;// 下载所有完成的标签
	public static final int REFRESH = 5;// 下载所有完成的标签

	// 控件
	private SlidingDrawer tellFriendDrawaer;// share抽屉控件
	private MyGallery mGallery;
	private NewsImageAdapter adapter;
	private MySlidingDrawer albums_photo_lidingDrawer;// 抽屉组件
	private TextView photo_Info; // 抽屉中的相片说明
	private TextView position; // 抽屉中的相片位置
	private TextView photoName; // 抽屉中的相片名称
	private ImageView album_photo_arrow; // 抽屉右边箭头，加动画
	private ImageView downLoad, downloadAll, share, collect;
	private AlphaAnimation showAnimation, hideAnimation;// 显示，隐藏动画
	private LinearLayout titlebar, toolbar, toolbarLeft, toolbarRight;
	private ProgressBar mProgressBar;
	private Button back;
	// 数据
	private NewsDB newsDB;
	private NewsItem newDetailItem;
	private int story_id;
	private List<Image> imageList;// 存放图片对象
	private List<String> imageUrls;// 存放图片url

	private int lastPosition = 0;
	private int downloadCount = 0;// 要下载的个数
	private boolean isShowTool = true; // 是否显示工具栏

	// private int toobuttonWidth;
	private GestureDetector gestureScanner;
	private boolean isButton = false;// 点到按钮就不自动隐藏
	private boolean isInitData = true; // 是否初始化数据(从最新消息进入,此值为FALSE)
	private boolean isNomal = true;// 是否竖屏
	private boolean ifRequest = true;
	private int num = 0;// Gallery当前图片下标；
	private ImageLoader mImageLoader;

	private AlbumDetailActivityManager mAlbumDetailActivityManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressBar();
			switch (msg.what) {
			case DOWN:
				// 界面
				// newDetailItem = newsDB.retrieveNewsItem(story_id);//
				// 下载成功从数据库中取数据
				// Log.i("xpf", newDetailItem.otherImgs.size() + "size1");
				// if (newDetailItem != null) {
				// if (newDetailItem.otherImgs.size() > 0) {
				// imageList = newDetailItem.otherImgs;
				// refreshUI();
				// }
				// }
				mProgressBar.setVisibility(View.GONE);
				refreshUI();
				break;
			case FREE:
				mImageLoader.loadBigPic(imageList.get(num).smallURL,
						new BigImageCallback() {

							@Override
							public void imageLoaded(String bmp) {
								if (bmp.equals(imageList.get(num).smallURL)) {
									Message msg = new Message();
									msg.what = REFRESH;
									mHandler.sendMessage(msg);
								}

							}
						});
				// 提前下载前1张和后1张
				// if (num - 1 >= 0) {
				// mImageLoader.loadBigPic(imageList.get(num - 1).smallURL, new
				// BigImageCallback() {
				// @Override
				// public void imageLoaded(String bmp) {
				// }
				// });
				// }
				// if (num + 1 < imageList.size()) {
				// mImageLoader.loadBigPic(imageList.get(num + 1).smallURL, new
				// BigImageCallback() {
				// @Override
				// public void imageLoaded(String bmp) {
				// }
				// });
				// }
				break;
			case REFRESH:
				if (null != adapter) {
					adapter.notifyDataSetChanged();
				}
				break;
			case NOT_DOWN:
				mProgressBar.setVisibility(View.GONE);
				String strError = (msg.obj != null) ? (String) msg.obj
						: getString(R.string.album_downfail);
				new ErrorNotificatin(AlbumDetailActivity.this)
						.showErrorNotification(strError);
				break;
			case FINISHEDDOWN:
				Log.i("xpf", "pp下载ss完成");
				DownloadProgressUtils.hideProgressDialog();
				DownloadProgressUtils
						.finishedProgressDialog(AlbumDetailActivity.this);
				break;
			default:
				break;
			}
			// mProgressBar.setVisibility(View.GONE);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.album_photo_gallery, mLayout_body);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			// toobuttonWidth = 30;
			isNomal = false;
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏
			// toobuttonWidth = 56;
			isNomal = true;
		}

		mAlbumDetailActivityManager = new AlbumDetailActivityManager();

		// setHasGestureToClose(false);
		// 数据
		initData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 释放占用内存较大对像
		mGallery = null;
		adapter = null;
		newsDB = null;
		newDetailItem = null;
		imageList = null;

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
		photoName = (TextView) findViewById(R.id.album_photo_text);
		position = (TextView) findViewById(R.id.album_photo_positon);
		album_photo_arrow = (ImageView) findViewById(R.id.album_photo_arrow);
		photo_Info = (TextView) findViewById(R.id.album_photo_content);
		titlebar = (LinearLayout) findViewById(R.id.linear_titlebar);
		toolbar = (LinearLayout) findViewById(R.id.linear_toolbar);
		toolbarLeft = (LinearLayout) findViewById(R.id.linear_toolbar_Left1);
		toolbarRight = (LinearLayout) findViewById(R.id.linear_toolbar_Right1);
		mProgressBar = (ProgressBar) findViewById(R.id.news_progressBar);
		tellFriendDrawaer = (SlidingDrawer) findViewById(R.id.tellFriend_drawer);// share抽屉控件
		back = (Button) findViewById(R.id.title_button_left1); // 返回按钮
		// ImageView image = (ImageView) findViewById(R.id.title_imageView1);
		// image.setImageBitmap(ModuleActivity.mBitmapTitleLogo);
		TextView titletxtv = (TextView) findViewById(R.id.title_txtv);
		titletxtv.setText(getString(R.string.album)
				+ getString(R.string.detail));
		mImageLoader = getmSchoolImageLoader();
		titlebar.setFocusableInTouchMode(true);
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
		imageList = new ArrayList<NewsItem.Image>();
		imageUrls = new ArrayList<String>();
		newsDB = NewsDB.getInstance(AlbumDetailActivity.this,
				GlobalVariables.gSchoolKey);

		// story_id = getIntent().getIntExtra("story_id", -1);
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("story_id")) {
			story_id = extras.getInt("story_id", -1);
		}
		Log.i("xpf", "story_id=" + story_id);
		if (extras.containsKey(KEY_INIT)) {
			isInitData = extras.getBoolean(KEY_INIT);
		}
		// 工具按钮：下载，批量下载，分享，收藏

		LayoutParams layoutParams = new LayoutParams(MyMothod.Dp2Px(this,
				GlobalVariables.btnWidth_Toolbar), MyMothod.Dp2Px(this,
				GlobalVariables.btnHeight_Toolbar));
		layoutParams.setMargins(MyMothod.Dp2Px(this, 5), 0,
				MyMothod.Dp2Px(this, 5), 2);

		downLoad = new ImageView(AlbumDetailActivity.this);
		downLoad.setLayoutParams(layoutParams);
		downLoad.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_download_btn_selector));

		downloadAll = new ImageView(AlbumDetailActivity.this);
		downloadAll.setLayoutParams(layoutParams);
		downloadAll.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_downloadall_btn_selector));

		share = new ImageView(AlbumDetailActivity.this);
		share.setLayoutParams(layoutParams);
		share.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_share_btn));

		collect = new ImageView(AlbumDetailActivity.this);
		collect.setLayoutParams(layoutParams);
		collect.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_unbookmark_btn));

		downLoad.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isButton = true;
				downloadCount = 1;
				int index = (int) mGallery.getSelectedItemId();
				if (imageList.size() > index && index >= 0) {
					DownloadProgressUtils
							.showProgressDialog(AlbumDetailActivity.this);
					downLoadImage(index);// 没有就下载
				}
			}
		});
		downloadAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isButton = true;
				downloadCount = imageList.size();
				if (imageList.size() > 0) {
					DownloadProgressUtils
							.showProgressDialog(AlbumDetailActivity.this);
					for (int i = 0; i < imageList.size(); i++) {
						downLoadImage(i);
					}
				}
			}
		});
		 share.setOnClickListener(new OnClickListener() {
		
		 @Override
		 public void onClick(View v) {
		 isButton = true;
		 Share();// 分享
		 }
		 });
		collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;
				if (newDetailItem != null) {
					boolean isMark = newDetailItem.bookmark ? false : true;
					newsDB.updateBookmarkStatus(newDetailItem, isMark);
					newDetailItem.bookmark = isMark;
					if (isMark)
						collect.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.detail_bookmark_btn));
					else
						collect.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.detail_unbookmark_btn));

				}
			}
		});
		// 打开时
		albums_photo_lidingDrawer
				.setOnDrawerOpenListener(new OnDrawerOpenListener() {
					@Override
					public void onDrawerOpened() {
						isButton = true;
						album_photo_arrow
								.setBackgroundDrawable(getResources()
										.getDrawable(
												R.drawable.album_photo_arrow_open));
					}
				});
		// 关闭时
		albums_photo_lidingDrawer
				.setOnDrawerCloseListener(new OnDrawerCloseListener() {
					@Override
					public void onDrawerClosed() {
						isButton = true;
						album_photo_arrow
								.setBackgroundDrawable(getResources()
										.getDrawable(
												R.drawable.album_photo_arrow_close));
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
		photo_Info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;
			}
		});
		if (story_id != -1) { // && isInitData
			// newDetailItem = newsDB.retrieveNewsItem(story_id);
			// if (newDetailItem != null) {
			// Log.i("xpf", newDetailItem.otherImgs.size() + "size");
			// if (newDetailItem.otherImgs.size() > 0) {// 如果数据库中有详细，则先从数据库中取
			// Message msg = new Message();
			// msg.what = DOWN;
			// mHandler.sendMessage(msg);
			// } else {
			// requestNewsDetails(story_id);
			// }
			// } else {
			// requestNewsDetails(story_id);
			// }

			// requestNewsDetails(story_id);

			if (storyIsRead(story_id)) {
				refreshUI();
			} else {
				mProgressBar.setVisibility(View.VISIBLE);
				mAlbumDetailActivityManager.getNewsDetail(!isInitData,
						story_id, mHandler);
			}
		}
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
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
		toolbarRight.addView(share);
		toolbarRight.addView(collect);
		if (!isNomal) {// 根据横竖屏来决定是否显示工具栏
			hideBar();
		} else {
			showBar();
		}
	}

	/**
	 * refreshUI
	 */
	private void refreshUI() {
		this.newDetailItem = newsDB.retrieveNewsItem(story_id);
		Log.i("xpf",
				"ppp REFRESH uistory_id .imgs="
						+ newDetailItem.otherImgs.size());
		imageList.clear();
		if (newDetailItem != null) {// 是否收藏
			if (newDetailItem.bookmark)
				collect.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.detail_bookmark_btn));
			else
				collect.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.detail_unbookmark_btn));
			newsDB.updateReadStatus(newDetailItem, true);// 标记已读

			if (newDetailItem.otherImgs.size() > 0
					&& newDetailItem.otherImgs != null) {
				imageList = newDetailItem.otherImgs;
			}
		}

		// 初始化ViewGroup组件
		if (imageList.size() > 0) {
			Log.i("xpf", "ppp 初始化adapter");
			imageUrls.clear();
			for (int i = 0; i < imageList.size(); i++) {
				imageUrls.add(imageList.get(i).smallURL);
			}
			adapter = new NewsImageAdapter(this, imageUrls, mImageLoader);
			mGallery.setAdapter(adapter);
			mGallery.setSelection(lastPosition);

			Log.i("xpf", "ppp LASTPOSITION=0 notifyDataChanged");
		}
		if (imageList.size() > 1) {
			downloadAll.setVisibility(View.VISIBLE);
		} else {
			downloadAll.setVisibility(View.GONE);
		}

		// 生成share界面
//		new ShareToFriendViewHelper(share, tellFriendDrawaer,
//				GlobalVariables.shareList, AlbumDetailActivity.this,
//				newDetailItem.title, newDetailItem.share_url,null);
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
		if (imageList.get(index).imageCaption != null)
			photoName.setText(newDetailItem.title);
		if (imageList.get(index).imageCaption != null) {
			photo_Info.setTextColor(getResources().getColor(R.color.blue));
			photo_Info.setText(imageList.get(index).imageCaption);
		}
		position.setText(String.valueOf(index + 1) + "/"
				+ String.valueOf(imageList.size()));// 位置
	}

	private boolean storyIsRead(int stroyId) {
		if (newsDB.storyExists(stroyId)) {
			NewsItem item = newsDB.retrieveNewsItem(stroyId);
			if (item.isRead) {
				return true;
			}
		}
		return false;
	}

	// /**
	// * 下载新闻详细信息
	// *
	// * @param story_id
	// * 新闻ID
	// */
	// private void requestNewsDetails(final int story_id) {
	// Log.i("xpf", "下载数据");
	// // int getall = 0;
	// // if (!isInitData)
	// // getall = 1;
	//
	// int getall = 0;
	// if (!isInitData) {
	// getall = 1;
	// }
	// if (newsDB.storyExists(story_id)) {
	// this.newDetailItem = newsDB.retrieveNewsItem(story_id);
	// if (newDetailItem.isRead) {// 已经下载了就不用下载了,
	// ifRequest = false;
	// } else {
	// ifRequest = true;
	// }
	// } else {
	// ifRequest = true;
	// }
	//
	// if (ifRequest) {
	// mProgressBar.setVisibility(View.VISIBLE);
	// RequestOperation mRequestOperation = RequestOperation.getInstance();
	// RequestOperationCallback callback = new RequestOperationCallback() {
	// @Override
	// public void onSuccess() {
	// Message msg = new Message();
	// msg.what = DOWN;
	// mHandler.sendMessage(msg);
	// }
	//
	// @Override
	// public void onError(String err) {
	// Message msg = new Message();
	// msg.what = NOT_DOWN;
	// msg.obj = err;
	// mHandler.sendMessage(msg);
	// }
	// };
	// mRequestOperation.sendGetNeededInfo("GetNewsDetail", new Object[] {
	// story_id, getall, callback }, callback.getClass().getName());
	// } else {
	// refreshUI();
	// }
	// }

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
		if (imageList.size() > 1) {
			downloadAll.setVisibility(View.VISIBLE);
		} else {
			downloadAll.setVisibility(View.GONE);
		}
		share.setVisibility(View.VISIBLE);
		collect.setVisibility(View.VISIBLE);
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
			share.setVisibility(View.INVISIBLE);
			collect.setVisibility(View.INVISIBLE);
			back.setVisibility(View.INVISIBLE);
		}
	};

	/**
	 * 下载图片
	 * 
	 * @param index
	 */
	private void downLoadImage(int index) {
		if (imageList.size() > 0) {
			String url = imageList.get(index).smallURL;
			String fileName = "image" + String.valueOf(story_id) + "_"
					+ String.valueOf(index)
					+ url.substring(url.lastIndexOf("."), url.length());
			getmSchoolImageLoader().loadFile(url, false, fileName,
					new FileCallback() {

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
	private void Share() {
		if (imageList.size() > 0) {
			// int index = (int) mGallery.getSelectedItemId();
			// String strtest = "</br>hello </ddd> world </br> io&nbsp;kdf";
			// 过滤所有以<开头以>结尾的标签
			// String regEx = "<([^>]*)>";
			// Pattern p = Pattern.compile(regEx);
			// Matcher m = p.matcher(newDetailItem.share_url);
			// Log.i("xpf", "分享" + newDetailItem.share_url);
			// String strbody = m.replaceAll("").trim();
			// // 过滤&nbsp;等标签(&开头;结尾)
			// regEx = "&([^>]*);";
			// p = Pattern.compile(regEx);
			// m = p.matcher(strbody);
			// strbody = m.replaceAll("").trim();
			// if (strbody.length() > 126)
			// strbody = strbody.substring(0, 126) + "……";
			// if (newDetailItem != null)
			// CommonActions.shareContent(AlbumDetailActivity.this,
			// newDetailItem.title, strbody);
			if (newDetailItem != null)
				CommonActions.shareContent(AlbumDetailActivity.this,
						newDetailItem.title, newDetailItem.share_url);

		}
	}

	/**
	 * 保存屏幕切换之前的数据,横、竖屏
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

	// 判断gallery是否在当前选项停留了1秒
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

}