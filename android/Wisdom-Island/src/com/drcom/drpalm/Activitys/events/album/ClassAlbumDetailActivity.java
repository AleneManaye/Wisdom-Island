package com.drcom.drpalm.Activitys.events.album;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.Activitys.events.reply.EventsReplyActivity;
import com.drcom.drpalm.Activitys.events.sent.EventsSentReaderActivity;
import com.drcom.drpalm.Activitys.events.sent.ReplyersActivity;
import com.drcom.drpalm.Activitys.news.NewsImageAdapter;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.CommonActions;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.controls.MyGallery;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.MySlidingDrawer;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.controls.cache.ImageLoader.BigImageCallback;
import com.drcom.drpalm.View.controls.cache.ImageLoader.FileCallback;
import com.drcom.drpalm.View.events.album.ClassAlbumDetailActivityManager;
import com.drcom.drpalm.View.events.reply.EventsReplyActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDetailsItem.Imags;
import com.drcom.drpalm.objs.EventDetailsItem.Replyer;
import com.drcom.drpalm.objs.EventDraftItem;
import com.wisdom.island.R;

/**
 * 相册详细，用ViewGroup 实现画廊效果，抽屉显示相片信息，子控件用WebView实现
 * 
 * @author Administrator
 * 
 */

public class ClassAlbumDetailActivity extends ModuleActivity {
	public static String KEY_EVENT_ID = "KEY_EVENT_ID";
	public static String KEY_INIT = "KEY_INIT";
	public static String KEY_ISSENTEVENT = "KEY_ISSENTEVENT"; // 是否已发通告
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签
	public static final int FINISHEDDOWN = 3;// 下载所有完成的标签
	public static final int FREE = 4;// 下载所有完成的标签
	public static final int REFRESH = 5;// 下载所有完成的标签
	public static final int RELOGIN_FAIL = 6;
	public static final int RELOGIN_SUCCESS = 7;

	// 控件
//	private SlidingDrawer tellFriendDrawaer;// share抽屉控件
	private MyGallery mGallery;
	private NewsImageAdapter adapter;
	private MySlidingDrawer albums_photo_lidingDrawer;// 抽屉组件
	private TextView photo_Info; // 抽屉中的相片说明
	private TextView position; // 抽屉中的相片位置
	private TextView photoName; // 抽屉中的相片名称
	private ImageView album_photo_arrow; // 抽屉右边箭头，加动画
	private ImageView downLoad, downloadAll, share, collect, grade;// 下载，批量下载，分享，收藏，评论
	private Button back;
	private boolean isInitData = true; // 是否初始化数据(从最新消息进入,此值为FALSE)
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	private RelativeLayout mRelativeLayoutReadcount;
	private TextView mTxtViewReadcount;
	private Button mButtonResend;
	private SlidingDrawer moduleDrawer; // 功能菜单
	private Button mBtnRetransmission, mBtnReplace, mBtnDel, mBtnCancel; // 菜单功能按钮

	// 数据
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private String mUsername = "";
	private SettingManager setInstance;
	private int eventid;
	private EventDetailsItem mEventDetailsItem = new EventDetailsItem();// 详细
	private List<Imags> imageList;// 存放图片对象
	private List<String> imageUrls;// 存放图片url

	private int lastPosition = 0;
	private boolean isShowTool = true; // 是否显示工具栏
	private LinearLayout titlebar, toolbar, toolbarLeft, toolbarRight;
	private ProgressBar mProgressBar;
	private Handler handler = new Handler();
	private AlphaAnimation showAnimation, hideAnimation;// 显示，隐藏动画
	// private int toobuttonWidth;
	private int downloadCount = 0;// 要下载的个数
	private GestureDetector gestureScanner;
	private boolean isButton = false;// 点到按钮就不自动隐藏
	private boolean isNomal = true;// 是否竖屏
	private boolean isSentEvent = false; // 是否已发按钮
	private int num = 0;// Gallery当前图片下标；
	private ImageLoader mImageLoader;
	private ClassAlbumDetailActivityManager mClassAlbumDetailActivityManager;
	private boolean mIsGradeVisible = true;	//回复按钮是否可视

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressBar();
			switch (msg.what) {
			case DOWN:
				// 界面
				refreshUI();
				Log.i("xpf", "iamgeList.size=" + imageList.size());
				break;
			case FREE:
				mImageLoader.loadBigPic(imageList.get(num).URL,
						new BigImageCallback() {
							@Override
							public void imageLoaded(String bmp) {
								if (bmp.equals(imageList.get(num).URL)) {
									Message msg = new Message();
									msg.what = REFRESH;
									Log.i("xpf", "下载完成");
									mHandler.sendMessage(msg);
								}
							}
						});
				// 提前下载前后2张
				// if (num - 1 >= 0) {
				// mImageLoader.loadBigPic(imageList.get(num - 1).URL, new
				// BigImageCallback() {
				// @Override
				// public void imageLoaded(String bmp) {
				// }
				// });
				// }
				// if (num + 1 < imageList.size()) {
				// mImageLoader.loadBigPic(imageList.get(num + 1).URL, new
				// BigImageCallback() {
				// @Override
				// public void imageLoaded(String bmp) {
				// }
				// });
				// }
				break;
			case REFRESH:
				if (null != adapter) {
					Log.i("xpf", "下载完成2，notifydatachanged");
					adapter.notifyDataSetChanged();
				}
				break;
			case NOT_DOWN:
				if (imageList.size() > 0) {
					refreshUI();
				} else {
					String strError = (msg.obj != null) ? (String) msg.obj
							: getString(R.string.album_downfail);
					if (strError.equals(GlobalVariables.gAppContext
							.getResources().getString(
									R.string.InvalidSessionKey))) {
						GlobalVariables
								.showInvalidSessionKeyMessage(ClassAlbumDetailActivity.this);
					} else {
						new ErrorNotificatin(ClassAlbumDetailActivity.this)
								.showErrorNotification(strError);
					}
				}
				break;
			case FINISHEDDOWN:
				DownloadProgressUtils.hideProgressDialog();
				DownloadProgressUtils
						.finishedProgressDialog(ClassAlbumDetailActivity.this);
				break;
			case RELOGIN_FAIL:

				break;
			case RELOGIN_SUCCESS:
				if (isRequestRelogin) {
					mClassAlbumDetailActivityManager.getEventDetail(
							isSentEvent, !isInitData, eventid, mHandler);
					isRequestRelogin = false;
				}
				break;
			default:
				break;
			}
			mProgressBar.setVisibility(View.GONE);
		};
	};

	// 手势

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.album_photo_gallery, mLayout_body);
		Log.i("xpf", "SW0" + GlobalVariables.SCREEN_WIDTH + " SH"
				+ GlobalVariables.SCREEN_HEIGHT + " TH"
				+ GlobalVariables.STATEBARHEIGHT);
		// 取参数
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(KEY_EVENT_ID)) {
			eventid = extras.getInt(KEY_EVENT_ID, -1);
		}
		if (extras.containsKey(KEY_INIT)) {
			isInitData = extras.getBoolean(KEY_INIT);
		}
		if (extras.containsKey(KEY_ISSENTEVENT)) {
			isSentEvent = extras.getBoolean(KEY_ISSENTEVENT);
		}

		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			// toobuttonWidth = 30;
			isNomal = false;// 是否竖屏
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏
			// toobuttonWidth = 56;
			isNomal = true;// 是否竖屏
		}

		// setHasGestureToClose(false);
		hideProgressBar();
		hideTitle();
		hideToolbar();
		initData();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (eventid != -1)
			GetDataInDB(eventid);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 释放占用内存较大对像
		mGallery = null;
		adapter = null;
		mEventsDB = null;
		mEventDetailsItem = null;
		imageList = null;

		super.onDestroy();
	}

	/**
	 * 实例化所有变量
	 */
	private void initData() {
		mEventsDB = EventsDB.getInstance(this, GlobalVariables.gSchoolKey);
		if (getLastNonConfigurationInstance() != null)// 取得屏幕切换前的数据
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
		back = (Button) findViewById(R.id.title_button_left1); // 返回按钮
		// ImageView image = (ImageView) findViewById(R.id.title_imageView1);
		// image.setImageBitmap(ModuleActivity.mBitmapTitleLogo);
		TextView titletxtv = (TextView) findViewById(R.id.title_txtv);
		titletxtv.setText(getString(R.string.classalbum)
				+ getString(R.string.detail));
		mImageLoader = getmClassImageLoader();
		mRelativeLayoutReadcount = (RelativeLayout) findViewById(R.id.eventsdetali_RLayout_readcount); // 未读数
		if (isSentEvent) {
			mRelativeLayoutReadcount.setVisibility(View.VISIBLE);

			mButtonResend = (Button) findViewById(R.id.title_button_edit);
			mButtonResend.setVisibility(View.VISIBLE);
			mButtonResend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Intent i = new Intent(ClassAlbumDetailActivity.this,
					// NewEventActivity.class);
					// i.putExtra(NewEventActivity.KEY_DETAILITEM,
					// mEventDetailsItem);
					// startActivity(i);

					if (!moduleDrawer.isOpened()) {
						openDrawer();
					}
				}
			});
		}
		mTxtViewReadcount = (TextView) findViewById(R.id.eventsdetali_readcount_txtview);
		mTxtViewReadcount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClassAlbumDetailActivity.this,
						EventsSentReaderActivity.class);
				intent.putExtra(EventsSentReaderActivity.KEY_EVENT_ID,
						mEventDetailsItem.eventid);
				startActivity(intent);
			}
		});

		showAnimation = new AlphaAnimation(0f, 1f);
		showAnimation.setDuration(300);
		showAnimation.setFillAfter(true);
		showAnimation.setInterpolator(new LinearInterpolator());
		hideAnimation = new AlphaAnimation(1f, 0f);
		hideAnimation.setDuration(300);
		hideAnimation.setFillAfter(true);
		hideAnimation.setInterpolator(new LinearInterpolator());

		imageList = new ArrayList<Imags>();
		imageUrls = new ArrayList<String>();
		setInstance = SettingManager.getSettingManager(this);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		mClassAlbumDetailActivityManager = new ClassAlbumDetailActivityManager(
				ClassAlbumDetailActivity.this, mUsername);
		hideProgressBar();
		hideTitle();
		hideToolbar();
		// 工具按钮：下载，批量下载，分享，收藏
		LayoutParams layoutParams = new LayoutParams(MyMothod.Dp2Px(this,
				GlobalVariables.btnWidth_Toolbar), MyMothod.Dp2Px(this,
				GlobalVariables.btnHeight_Toolbar));
		layoutParams.setMargins(MyMothod.Dp2Px(this, 5), 0,
				MyMothod.Dp2Px(this, 5), 2);

		downLoad = new ImageView(ClassAlbumDetailActivity.this);
		downLoad.setLayoutParams(layoutParams);
		downLoad.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_download_btn_selector));

		downloadAll = new ImageView(ClassAlbumDetailActivity.this);
		downloadAll.setLayoutParams(layoutParams);
		downloadAll.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_downloadall_btn_selector));

		share = new ImageView(ClassAlbumDetailActivity.this);
		share.setLayoutParams(layoutParams);
		share.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_share_btn));

		collect = new ImageView(ClassAlbumDetailActivity.this);
		collect.setLayoutParams(layoutParams);
		collect.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_unbookmark_btn));

		grade = new ImageView(ClassAlbumDetailActivity.this);
		grade.setLayoutParams(layoutParams);
		grade.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.detail_feedback_btn_selector));

		// 回复
		grade.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;

				if (isSentEvent) {
					Intent i = new Intent(ClassAlbumDetailActivity.this,
							ReplyersActivity.class);
					i.putExtra(ReplyersActivity.KEY_EVENT, mEventDetailsItem);
					ClassAlbumDetailActivity.this.startActivity(i);
				} else {
					// Intent i = new Intent(ClassAlbumDetailActivity.this,
					// EventsReplyActivity.class);
					// i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, eventid);
					// i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,
					// mEventDetailsItem.pubid);
					// ClassAlbumDetailActivity.this.startActivity(i);

					// 保存读到的回复最后更新时间
					for (int i = 0; i < mEventDetailsItem.listReplyer.size(); i++) {
						Replyer rp = mEventDetailsItem.listReplyer.get(i);
						mEventsDB.updataAsworgLastawstimeread(rp.ReplyLastTime,
								eventid, mUsername, rp.ReplyerId);
						mEventsDB.updataEventLastawstimeread(DateFormatter
								.getDateFromSecondsString(rp.ReplyLastTime),
								mEventDetailsItem);
					}
					// 按钮复原
					grade.setBackgroundResource(R.drawable.detail_feedback_btn_selector);

					Intent i = new Intent(ClassAlbumDetailActivity.this,
							EventsReplyActivity.class);
					i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, eventid);
					i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,
							mEventDetailsItem.listReplyer.get(0).ReplyerId);// 讨论组ID
					i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,
							mEventDetailsItem.pubname);
					if (mEventDetailsItem.status
							.equals(EventDraftItem.ORISTATUS_TYPE_C)) {
						i.putExtra(EventsReplyActivity.REPLY_ABLE, false);
					}
					i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,
							mEventDetailsItem.pubname);

					Log.i("zjj", "点击查看回复内容:" + eventid + ","
							+ mEventDetailsItem.pubid);
					ClassAlbumDetailActivity.this.startActivity(i);
				}

			}
		});

		downLoad.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DownloadProgressUtils
						.showProgressDialog(ClassAlbumDetailActivity.this);
				isButton = true;
				downloadCount = 1;
				int index = (int) mGallery.getSelectedItemId();
				if (imageList.size() > index && index >= 0) {
					downLoadImage(index);// 没有就下载
				}
			}
		});
		downloadAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isButton = true;
				DownloadProgressUtils
						.showProgressDialog(ClassAlbumDetailActivity.this);
				downloadCount = imageList.size();
				if (imageList.size() > 0) {
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
		if (isSentEvent) {
			collect.setVisibility(View.GONE);
		}
		collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButton = true;

				if (mEventDetailsItem != null && mEventsDB != null) {
					boolean isMark = mEventDetailsItem.bookmark ? false : true;
					mClassAlbumDetailActivityManager.SetBookmark(isMark,
							mEventDetailsItem.eventid + "", mUsername);
					mEventsDB.markAsBookmark(mEventDetailsItem, isMark);
					mEventDetailsItem.bookmark = isMark;
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
				finishDraw();
			}
		});
		photo_Info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("xpf", "点到了");
				isButton = true;
			}
		});
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				setImageInfo(arg2);
				num = arg2;
				Log.i("xpf", "has ishasDown?= " + adapter.isHasDown());
				// 如果内存和文件缓存中都没有，就开起下载线程，从网络读取；
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
				if (isNomal) {
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
		toolbarRight.addView(grade);
		toolbarRight.addView(share);
		toolbarRight.addView(collect);

		// 功能菜单
		moduleDrawer = (SlidingDrawer) findViewById(R.id.drawer_menu);// 菜单抽屉组件
		mBtnRetransmission = (Button) findViewById(R.id.DrawerButtonRetransmission);
		mBtnRetransmission.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDrawer();

				Intent i = new Intent(ClassAlbumDetailActivity.this,
						NewEventActivity.class);
				i.putExtra(NewEventActivity.KEY_DETAILITEM, mEventDetailsItem);
				i.putExtra(NewEventActivity.KEY_EDITTYPE,
						NewEventActivity.EDITTYPE_RETRANS);
				startActivity(i);
			}
		});

		mBtnReplace = (Button) findViewById(R.id.DrawerButtonReplace);
		mBtnReplace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDrawer();

				Intent i = new Intent(ClassAlbumDetailActivity.this,
						NewEventActivity.class);
				i.putExtra(NewEventActivity.KEY_DETAILITEM, mEventDetailsItem);
				i.putExtra(NewEventActivity.KEY_EDITTYPE,
						NewEventActivity.EDITTYPE_REPLACE);
				startActivity(i);
			}
		});

		mBtnDel = (Button) findViewById(R.id.DrawerButtonDel);
		mBtnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DelEvent();
				closeDrawer();
			}
		});

		mBtnCancel = (Button) findViewById(R.id.DrawerButtonCancel);
		mBtnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDrawer();
			}
		});

		if (eventid != -1) {
			if (isSentEvent) {
				if (mEventsDB.publishEventExists(eventid, mUsername)) {
					GetDataInDB(eventid);
					mEventsDB.markAreadySendAsRead(mEventDetailsItem);
				}
				mProgressBar.setVisibility(View.VISIBLE);
				Boolean bReturn = mClassAlbumDetailActivityManager
						.getEventDetail(isSentEvent, !isInitData, eventid,
								mHandler);
				if (!bReturn)
					refreshUI();
				// requestEventDetail(eventid);// 从网络下载数据
			} else {
				if (mEventsDB.eventExists(eventid, mUsername)) {
					GetDataInDB(eventid);
				}
				mProgressBar.setVisibility(View.VISIBLE);
				Boolean bReturn = mClassAlbumDetailActivityManager
						.getEventDetail(isSentEvent, !isInitData, eventid,
								mHandler);
				if (!bReturn)
					refreshUI();
				// requestEventDetail(eventid);// 从网络下载数据
			}

			Log.i("xpf", "event_id" + eventid);
		}
		if (!isNomal) {// 根据横竖屏来决定是否显示工具栏
			hideBar();
		} else {
			showBar();
		}
	}

	/**
	 * 设置当前图处的名称、位置、描述
	 * 
	 * @param index
	 *            当前图片的位置
	 */
	private void setImageInfo(int index) {
		// 设置相片信息,在抽屉组件中显示出来
		if (mEventDetailsItem.title != null)
			photoName.setText(mEventDetailsItem.title);
		if (imageList.get(index).imgDescription != null) {
			photo_Info.setTextColor(getResources().getColor(R.color.dark_gray));
			String sender = getResources().getString(R.string.sender)
					+ mEventDetailsItem.pubname;
			String receiver = getResources().getString(R.string.addressee)
					+ mEventDetailsItem.owner;
			String time = getResources().getString(R.string.senttime)
					+ DateFormatter
							.getStringYYYYMMDDHHmm(mEventDetailsItem.post);
			// String location = getResources().getString(R.string.location) +
			// mEventDetailsItem.location;
			String description = imageList.get(index).imgDescription;
			photo_Info.setText(sender + "\n" + receiver + "\n" + time);// + "\n"
																		// +
																		// location);
			TextView a = (TextView) findViewById(R.id.album_photo_content1);
			a.setText(description);
			a.setTextColor(getResources().getColor(R.color.blue));
		}
		position.setText(String.valueOf(index + 1) + "/"
				+ String.valueOf(imageList.size()));
	}

	// /**
	// * 下载新闻详细信息
	// *
	// * @param story_id
	// * 新闻ID
	// */
	// private void requestEventDetail(final int eventid) {
	// // 非在线登录/网络不通时,返回
	// // LoginManager instance =
	// // LoginManager.getInstance(GlobalVariables.gAppContext);
	// if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) ==
	// HttpStatus.STATUS_NOCONNECT) { //
	// LoginManager.OnlineStatus.ONLINE_LOGINED
	// // !=
	// // instance.getOnlineStatus()
	// // ||
	// refreshUI();
	// return;
	// }
	//
	// int getall = 0;
	// if (!isInitData)
	// getall = 1;
	// mProgressBar.setVisibility(View.VISIBLE);
	// RequestOperation mRequestOperation = RequestOperation.getInstance();
	// RequestOperationReloginCallback callback = new
	// RequestOperationReloginCallback() {
	//
	// @Override
	// public void onSuccess() {
	// Message msg = new Message();
	// msg.what = DOWN;
	// mHandler.sendMessageDelayed(msg, 200);
	// }
	//
	// @Override
	// public void onCallbackError(String err) {
	// Message msg = new Message();
	// msg.what = NOT_DOWN;
	// msg.obj = err;
	// mHandler.sendMessageDelayed(msg, 100);
	// }
	//
	// @Override
	// public void onReloginError() {
	// // TODO Auto-generated method stub
	// super.onReloginError();
	// Log.i("zjj", "通告详细:自动重登录失败");
	// }
	//
	// @Override
	// public void onReloginSuccess() {
	// // TODO Auto-generated method stub
	// super.onReloginSuccess();
	// Log.i("zjj", "通告详细:自动重登录成功");
	// if (isRequestRelogin) {
	// requestEventDetail(eventid); // 自动登录成功后，再次请求数据
	// isRequestRelogin = false;
	// }
	// }
	// };
	// if (!isSentEvent) {
	// mRequestOperation.sendGetNeededInfo("GetEventDetail", new Object[] {
	// String.valueOf(eventid), getall, callback },
	// callback.getClass().getName());
	// } else {
	// mRequestOperation.sendGetNeededInfo("GetPublishEventDetail", new Object[]
	// { String.valueOf(eventid), callback }, callback.getClass().getName());
	// }
	//
	// }

	/**
	 * 从库读取
	 */
	private void GetDataInDB(int id) {
		imageList.clear();
		if (!isSentEvent) {
			mEventCursor = mEventsDB.getOneEventCursor(id, mUsername);
		} else {
			mEventCursor = mEventsDB.getOnePublishEventCursor(id, mUsername);
		}
		if (mEventCursor != null) {
			mEventCursor.requery();
			mEventCursor.moveToFirst();
			if (!isSentEvent) {
				mEventDetailsItem = mEventsDB
						.retrieveEventDetailItem(mEventCursor);
			} else {
				mEventDetailsItem = mEventsDB
						.retrievePublishEventDetailItem(mEventCursor);
			}
			if (mEventDetailsItem != null) {
				if (mEventDetailsItem.imgs.size() > 0)
					imageList = mEventDetailsItem.imgs;
			}
		} else {
			// requestEventDetail(eventid);// 从网络下载数据
			mProgressBar.setVisibility(View.VISIBLE);
			Boolean bReturn = mClassAlbumDetailActivityManager.getEventDetail(
					isSentEvent, !isInitData, eventid, mHandler);
			if (!bReturn)
				refreshUI();
		}
		mEventCursor.close();
	}

	/**
	 * refreshUI
	 */
	private void refreshUI() {
		// mEventCursor = mEventsDB.getOneEventCursor(eventid, mUsername);
		// if (mEventCursor != null) {
		// mEventCursor.requery();
		// mEventCursor.moveToFirst();
		// if (mEventCursor.getCount() > 0) {
		// mEventDetailsItem = mEventsDB.retrieveEventDetailItem(mEventCursor);
		// }
		// if (mEventDetailsItem != null && mEventDetailsItem.imgs != null) {
		// if (mEventDetailsItem.imgs.size() > 0)
		// imageList = mEventDetailsItem.imgs;
		// }
		// }
		// mEventCursor.close();
		GetDataInDB(eventid);
		if (mEventDetailsItem != null) {// 是否收藏
			if (mEventDetailsItem.bookmark)
				collect.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.detail_bookmark_btn));
			else
				collect.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.detail_unbookmark_btn));
			mEventsDB.markAsRead(mEventDetailsItem);
		}

		// 初始化ViewGroup组件
		if (imageList.size() > 0) {
			imageUrls.clear();
			for (int i = 0; i < imageList.size(); i++) {
				imageUrls.add(imageList.get(i).URL);
			}
			adapter = new NewsImageAdapter(this, imageUrls, mImageLoader);
			mGallery.setAdapter(adapter);
			mGallery.setSelection(Integer.parseInt(String.valueOf(lastPosition)));
		}
		if (imageList.size() > 1) {
			downloadAll.setVisibility(View.VISIBLE);
		} else {
			downloadAll.setVisibility(View.GONE);
		}

		if (isSentEvent) {
			mTxtViewReadcount.setText(getResources().getString(
					R.string.eventscheckedsum)
					+ mEventDetailsItem.readcount
					+ "/"
					+ mEventDetailsItem.recvtotal);

			boolean hasnewreply = false;
			if (mEventDetailsItem.listReplyer.size() > 0) {
				// 每一个回复的最后回复时间 与 通告的已看的最后回复的时间对比,是否有新回复
				// long newestlastupdatetime = 0;
				EventsReplyActivityManagement eram = new EventsReplyActivityManagement(
						ClassAlbumDetailActivity.this, mUsername);
				int sum = 0;
				for (int i = 0; i < mEventDetailsItem.listReplyer.size(); i++) {
					Replyer rp = mEventDetailsItem.listReplyer.get(i);
					if (DateFormatter.getDateFromSecondsString(
							rp.lastawstimeread).getTime() < DateFormatter
							.getDateFromSecondsString(rp.ReplyLastTime)
							.getTime()) {
						hasnewreply = true;
					}

					if (hasnewreply)
						grade.setBackgroundResource(R.drawable.detail_new_feedback_btn_selector);
					else
						grade.setBackgroundResource(R.drawable.detail_feedback_btn_selector);

					// if
					// (DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime()
					// > newestlastupdatetime) {
					// newestlastupdatetime =
					// DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime();
					// }
					sum += eram.getAwscontentSum(eventid, rp.ReplyerId);
				}
				// Log.i("zjj", "从详细中更新通告 newestlastupdatetime:" +
				// newestlastupdatetime);
				// mEventsDB.updataSendEventLastawstime(newestlastupdatetime,
				// mEventDetailsItem);

				Log.i("zjj", "从详细中更新已发通告 本地已读聊天内容总数:" + sum);
				mEventsDB.updataSendEventAwscoutnclient(sum, mEventDetailsItem);

//				grade.setVisibility(View.VISIBLE);
				mIsGradeVisible = true;
			} else
//				grade.setVisibility(View.GONE);
				mIsGradeVisible = false;

			collect.setVisibility(View.GONE);
			share.setVisibility(View.GONE);
			if(mIsGradeVisible)
				grade.setVisibility(View.VISIBLE);
			else
				grade.setVisibility(View.GONE);
			
		} else {

			// 如果是自己发的通告,则不显示回复按钮
			if (mEventDetailsItem.pubid.toLowerCase().equals(
					mUsername.toLowerCase())) {
//				grade.setVisibility(View.GONE);
				mIsGradeVisible = false;
			} else {
				// 每一个回复的最后回复时间 与 通告的已看的最后回复的时间对比,是否有新回复
				Date newestlastupdatetime = new Date(0);
				if (mEventDetailsItem.listReplyer.size() > 0) {
					for (int i = 0; i < mEventDetailsItem.listReplyer.size(); i++) {
						Replyer rp = mEventDetailsItem.listReplyer.get(i);
						if (DateFormatter.getDateFromSecondsString(
								rp.lastawstimeread).getTime() < DateFormatter
								.getDateFromSecondsString(rp.ReplyLastTime)
								.getTime()) {
							grade.setBackgroundResource(R.drawable.detail_new_feedback_btn_selector);
						}

						if (DateFormatter.getDateFromSecondsString(
								rp.ReplyLastTime).getTime() > newestlastupdatetime
								.getTime()) {
							newestlastupdatetime = DateFormatter
									.getDateFromSecondsString(rp.ReplyLastTime);
						}
					}
					Log.i("zjj", "从详细中更新通告 newestlastupdatetime:"
							+ newestlastupdatetime);
					mEventsDB.updataEventLastawstime(newestlastupdatetime,
							mEventDetailsItem);
//					grade.setVisibility(View.VISIBLE);
					mIsGradeVisible = true;
				} else {
//					grade.setVisibility(View.GONE);
					mIsGradeVisible = false;
				}
			}
			
			if(mIsGradeVisible)
				grade.setVisibility(View.VISIBLE);
			else
				grade.setVisibility(View.GONE);

			// 生成share界面
//			tellFriendDrawaer = (SlidingDrawer) findViewById(R.id.tellFriend_drawer);
//			if (imageList.size() > 0) {
//				int index = (int) mGallery.getSelectedItemId();
//				String regEx = "<([^>]*)>";
//				Pattern p = Pattern.compile(regEx);
//				Matcher m = p.matcher(imageList.get(index).URL);
//				String strbody = m.replaceAll("").trim();
//				// 过滤&nbsp;等标签(&开头;结尾)
//				regEx = "&([^>]*);";
//				p = Pattern.compile(regEx);
//				m = p.matcher(strbody);
//				strbody = m.replaceAll("").trim();
//				if (strbody.length() > 126)
//					strbody = strbody.substring(0, 126) + "……";
//				if (mEventDetailsItem != null)
//					new ShareToFriendViewHelper(share, tellFriendDrawaer,
//							GlobalVariables.shareList,
//							ClassAlbumDetailActivity.this,
//							mEventDetailsItem.title, mEventDetailsItem.title
//									+ "\n" + strbody,null);
//
//			}
		}

		// 菜单功能
		if (!mEventDetailsItem.pubid.equals(mUsername)) {
			mBtnReplace.setVisibility(View.GONE);
			mBtnDel.setVisibility(View.GONE);
		} else {
			mBtnReplace.setVisibility(View.VISIBLE);
			mBtnDel.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGallery.onTouchEvent(ev);
		gestureScanner.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
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
		albums_photo_lidingDrawer.clearAnimation();
		albums_photo_lidingDrawer.setAnimation(showAnimation);
		mRelativeLayoutReadcount.clearAnimation();
		mRelativeLayoutReadcount.setAnimation(showAnimation);
		showAnimation.startNow();
		handler.removeCallbacks(runShowScrollBar);
		
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
		if(mIsGradeVisible)
			grade.setVisibility(View.VISIBLE);
		else
			grade.setVisibility(View.GONE);
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
		mRelativeLayoutReadcount.clearAnimation();
		mRelativeLayoutReadcount.setAnimation(hideAnimation);
		hideAnimation.startNow();
		handler.postDelayed(runShowScrollBar, 300);
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
//			grade.setVisibility(View.INVISIBLE);
			
			if(!mIsGradeVisible)
				grade.setVisibility(View.GONE);
			
			back.setVisibility(View.INVISIBLE);
			if (isSentEvent) {
				mRelativeLayoutReadcount.setVisibility(View.INVISIBLE);
			}
		}
	};

	/**
	 * 下载图片 s
	 * 
	 * @param index
	 */
	private void downLoadImage(int index) {

		if (imageList.size() > 0) {
			String url = imageList.get(index).URL;
			String fileName = "image" + String.valueOf(eventid) + "_"
					+ String.valueOf(index)
					+ url.substring(url.lastIndexOf("."), url.length());
			getmClassImageLoader().loadFile(url, false, fileName,
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
	 * 分享
	 */
	private void Share() {
		if (imageList.size() > 0) {
			int index = (int) mGallery.getSelectedItemId();
			// String strtest = "</br>hello </ddd> world </br> io&nbsp;kdf";
			// 过滤所有以<开头以>结尾的标签
			String regEx = "<([^>]*)>";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(imageList.get(index).URL);
			Log.i("xpf", "分享" + imageList.get(index).URL);
			String strbody = m.replaceAll("").trim();
			// 过滤&nbsp;等标签(&开头;结尾)
			regEx = "&([^>]*);";
			p = Pattern.compile(regEx);
			m = p.matcher(strbody);
			strbody = m.replaceAll("").trim();
			if (strbody.length() > 126)
				strbody = strbody.substring(0, 126) + "……";

			if (mEventDetailsItem != null)
				CommonActions.shareContent(ClassAlbumDetailActivity.this,
						mEventDetailsItem.title, mEventDetailsItem.title + "\n"
								+ strbody);

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
						mHandler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}

	/**
	 * 打开抽屉
	 */
	private void openDrawer() {
		moduleDrawer.animateOpen();
	}

	/**
	 * 关闭底部抽屉
	 * 
	 */
	private void closeDrawer() {
		moduleDrawer.animateClose();
	}

	/**
	 * 删除通告
	 */
	private void DelEvent() {
		mClassAlbumDetailActivityManager.DelEvent(eventid + "", mHandlerDel);
	}

	private Handler mHandlerDel = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == ClassAlbumDetailActivityManager.UPDATEFINISH) {
				mClassAlbumDetailActivityManager.DelEventInDB(mEventsDB,
						eventid);
				finishDraw();
			} else if (msg.arg1 == ClassAlbumDetailActivityManager.UPDATEFAILED) {
				String err = (String) msg.obj;
				if (err.equals(GlobalVariables.gAppContext.getResources()
						.getString(R.string.InvalidSessionKey))) {
					GlobalVariables
							.showInvalidSessionKeyMessage(ClassAlbumDetailActivity.this);
				} else {
					GlobalVariables.toastShow(err);
				}
			}

			hideProgressBar();
		}
	};
}