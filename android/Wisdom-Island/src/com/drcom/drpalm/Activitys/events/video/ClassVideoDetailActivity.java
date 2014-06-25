package com.drcom.drpalm.Activitys.events.video;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.reply.EventsReplyActivity;
import com.drcom.drpalm.Activitys.events.sent.EventsSentDetailActivity;
import com.drcom.drpalm.Activitys.events.sent.EventsSentReaderActivity;
import com.drcom.drpalm.Activitys.events.sent.ReplyersActivity;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Tool.CommonActions;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.nettool.NetStatusManager;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.MySlidingDrawer;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.controls.cache.ImageLoader.FileCallback;
import com.drcom.drpalm.View.events.reply.EventsReplyActivityManagement;
import com.drcom.drpalm.View.events.video.ClassVideoDetailActivityManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDetailsItem.Replyer;
import com.drcom.drpalm.objs.EventDraftItem;
import com.wisdom.island.R;

/**
 * 视频详细，抽屉显示视频信息
 * 
 * @author Administrator
 * 
 */

public class ClassVideoDetailActivity extends ModuleActivity {
//	private static final int DOWN = 0;// 下载成功的标签
//	private static final int NOT_DOWN = 1;// 下载失败的标签
	public static final int FINISHEDDOWN = 3;// 下载所有完成的标签
	public static String KEY_EVENT_ID = "KEY_EVENT_ID";
	public static String KEY_ISSENTEVENT = "KEY_ISSENTEVENT"; // 是否已发通告

//	public static final int RELOGIN_FAIL = 6;
//	public static final int RELOGIN_SUCCESS = 7;

	//
	private MySlidingDrawer albums_photo_lidingDrawer;// 抽屉组件
	private TextView photo_Info; // 抽屉中的相片说明
	private TextView photoName; // 抽屉中的相片名称
	private ImageView album_photo_arrow; // 抽屉右边箭头，加动画
	private ImageView collect, grade;// 分享，收藏，评论
	private Button mButtonPlay;
	private RelativeLayout mRelativeLayoutReadcount;
	private TextView mTxtViewReadcount;
//	private Button mButtonResend;
	private ImageView mImageViewPerview;
	private Animation mAnimationPlay;

	// 数据
	private boolean isSentEvent = false; // 是否已发
	private int eventid;
	private EventDetailsItem mEventDetailsItem = new EventDetailsItem();// 详细
	private ImageLoader mImageLoader;
//	private List<Imags> imageList = new ArrayList<EventDetailsItem.Imags>();// 存放图片对象
	private ClassVideoDetailActivityManager mClassVideoDetailActivityManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressBar();
			switch (msg.what) {
			case ClassVideoDetailActivityManager.UPDATEFAILED:
				
				break;
				
			case ClassVideoDetailActivityManager.UPDATEFINISH:
				refreshUI();
				break;
				
			case FINISHEDDOWN:
				Bitmap bitmap = mImageLoader.getBitmapFromCache(mEventDetailsItem.imgs.get(0).preview);
				if (null == bitmap) {
					mImageViewPerview.setBackgroundResource(R.drawable.downloadfaild_pic);
				} else {
					mImageViewPerview.setImageBitmap(bitmap);
				}
				setImageInfo();
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
		inflater.inflate(R.layout.classvideo_detail_view, mLayout_body);
		// 取参数
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(KEY_EVENT_ID)) {
			eventid = extras.getInt(KEY_EVENT_ID, -1);
		}
		if (extras.containsKey(KEY_ISSENTEVENT)) {
			isSentEvent = extras.getBoolean(KEY_ISSENTEVENT);
		}
		
		mClassVideoDetailActivityManager = new ClassVideoDetailActivityManager(this);
		initUI();
		
		if(eventid != -1){
			showProgressBar();
			mClassVideoDetailActivityManager.getEventDetail(isSentEvent, true, eventid, mHandler);
		}
		
		if(NetStatusManager.getSettingManager(ClassVideoDetailActivity.this).GetNetType() != NetStatusManager.NetType.WIFI){
			Toast.makeText(ClassVideoDetailActivity.this, getString(R.string.msgvideousewifi), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(eventid != -1){
			GetDataInDB(eventid);
			refreshUI();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mClassVideoDetailActivityManager.destory();
		
		//释放占用内存较大对像
		mEventDetailsItem = null;
		
		mClassVideoDetailActivityManager = null;
		
		mImageViewPerview.destroyDrawingCache();
		
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		System.gc();
	}
	
	/**
	 * 实例化所有变量
	 */
	private void initUI() {
		albums_photo_lidingDrawer = (MySlidingDrawer) findViewById(R.id.album_photo_slidingDrawer);
		photoName = (TextView) findViewById(R.id.album_photo_text);
		album_photo_arrow = (ImageView) findViewById(R.id.album_photo_arrow);
		photo_Info = (TextView) findViewById(R.id.album_photo_content);
		mImageLoader = getmClassImageLoader();
		mRelativeLayoutReadcount = (RelativeLayout) findViewById(R.id.eventsdetali_RLayout_readcount); // 未读数
		if (isSentEvent) {
			mRelativeLayoutReadcount.setVisibility(View.VISIBLE);

//			mButtonResend = (Button) findViewById(R.id.title_button_edit);
//			mButtonResend.setVisibility(View.VISIBLE);
//			mButtonResend.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent i = new Intent(ClassVideoDetailActivity.this, NewEventActivity.class);
//					i.putExtra(NewEventActivity.KEY_DETAILITEM, mEventDetailsItem);
//					startActivity(i);
//				}
//			});
		}else{
			mRelativeLayoutReadcount.setVisibility(View.GONE);
		}
		mTxtViewReadcount = (TextView) findViewById(R.id.eventsdetali_readcount_txtview);
		mTxtViewReadcount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClassVideoDetailActivity.this, EventsSentReaderActivity.class);
				intent.putExtra(EventsSentReaderActivity.KEY_EVENT_ID, mEventDetailsItem.eventid);
				startActivity(intent);
			}
		});
		
		// 工具按钮：下载，批量下载，分享，收藏
		LayoutParams layoutParams = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Toolbar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Toolbar));
		layoutParams.setMargins(MyMothod.Dp2Px(this, 5), 0, MyMothod.Dp2Px(this, 5), 2);

		//分享
//		share = new ImageView(ClassVideoDetailActivity.this);
//		share.setLayoutParams(layoutParams);
//		share.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_share_btn));
//		share.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Share();// 分享
//			}
//		});
//		if (isSentEvent) {
//			share.setVisibility(View.GONE);
//		}

		//收藏
		collect = new ImageView(ClassVideoDetailActivity.this);
		collect.setLayoutParams(layoutParams);
		collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_unbookmark_btn));
		collect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mEventDetailsItem.bookmark){
					collect.setBackgroundResource(R.drawable.detail_unbookmark_btn);
					mClassVideoDetailActivityManager.SetBookmark(false);
				}
				else{
					collect.setBackgroundResource(R.drawable.detail_bookmark_btn);
					mClassVideoDetailActivityManager.SetBookmark(true);
				}
					
			}
		});
		if (isSentEvent) {
			collect.setVisibility(View.GONE);
		}

		grade = new ImageView(ClassVideoDetailActivity.this);
		grade.setLayoutParams(layoutParams);
		grade.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_feedback_btn_selector));

		// 回复
		grade.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isSentEvent) {
					Intent i = new Intent(ClassVideoDetailActivity.this, ReplyersActivity.class);
					i.putExtra(ReplyersActivity.KEY_EVENT, mEventDetailsItem);
					ClassVideoDetailActivity.this.startActivity(i);
				} else {
					// 保存读到的回复最后更新时间
					for (int i = 0; i < mEventDetailsItem.listReplyer.size(); i++) {
						Replyer rp = mEventDetailsItem.listReplyer.get(i);
//						mEventsDB.updataAsworgLastawstimeread(rp.ReplyLastTime, eventid, mUsername, rp.ReplyerId);
//						mEventsDB.updataEventLastawstimeread(DateFormatter.getDateFromSecondsString(rp.ReplyLastTime), mEventDetailsItem);
					}
					// 按钮复原
					grade.setBackgroundResource(R.drawable.detail_feedback_btn_selector);

					Intent i = new Intent(ClassVideoDetailActivity.this, EventsReplyActivity.class);
					i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, eventid);
					i.putExtra(EventsReplyActivity.REPLY_ASWPUBID, mEventDetailsItem.listReplyer.get(0).ReplyerId);//讨论组ID
					i.putExtra(EventsReplyActivity.REPLY_HEADSHOW, mEventDetailsItem.pubname);
					if (mEventDetailsItem.status.equals(EventDraftItem.ORISTATUS_TYPE_C)) {
						i.putExtra(EventsReplyActivity.REPLY_ABLE, false);
					}
					i.putExtra(EventsReplyActivity.REPLY_HEADSHOW, mEventDetailsItem.pubname);

					Log.i("zjj", "点击查看回复内容:" + eventid + "," + mEventDetailsItem.pubid);
					ClassVideoDetailActivity.this.startActivity(i);
				}

			}
		});
		
		// 打开时
		albums_photo_lidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				album_photo_arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.album_photo_arrow_open));
			}
		});
		// 关闭时
		albums_photo_lidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
				album_photo_arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.album_photo_arrow_close));
			}
		});
		
		//播放
		mAnimationPlay = AnimationUtils.loadAnimation(this, R.anim.playanim) ;
		mAnimationPlay.setAnimationListener(mAnimationPlayListener);
		mButtonPlay = (Button)findViewById(R.id.btn_play);
		mButtonPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mButtonPlay.startAnimation(mAnimationPlay);
			}
		});
		
		mImageViewPerview = (ImageView)findViewById(R.id.image_perview);
		
		ToolbarAddRightButton(grade);
//		ToolbarAddRightButton(share);
		ToolbarAddRightButton(collect);

//		if (eventid != -1) {
//			if (isSentEvent) {
//				if (mEventsDB.publishEventExists(eventid, mUsername)) {
//					GetDataInDB(eventid);
//				}
//				mProgressBar.setVisibility(View.VISIBLE);
//				Boolean bReturn = mClassAlbumDetailActivityManager.getEventDetail(isSentEvent, !isInitData, eventid, mHandler);
//				if(!bReturn)
//					refreshUI();
				//requestEventDetail(eventid);// 从网络下载数据
//			} else {
//				if (mEventsDB.eventExists(eventid, mUsername)) {
//					GetDataInDB(eventid);
//				}
//				mProgressBar.setVisibility(View.VISIBLE);
//				Boolean bReturn = mClassAlbumDetailActivityManager.getEventDetail(isSentEvent, !isInitData, eventid, mHandler);
//				if(!bReturn)
//					refreshUI();
				//requestEventDetail(eventid);// 从网络下载数据
//			}

//		}
	}

	/**
	 * 设置当前视频的名称、位置、描述(第一个视频)
	 */
	private void setImageInfo() {
		// 设置相片信息,在抽屉组件中显示出来
		if(mEventDetailsItem.imgs.size()>0){
			if (mEventDetailsItem.imgs.get(0).imgDescription != null) {
				String description = mEventDetailsItem.imgs.get(0).imgDescription;
				TextView a = (TextView) findViewById(R.id.album_photo_content1);
				a.setText(description);
				a.setTextColor(getResources().getColor(R.color.blue));
			}
		}
	}

//	/**
//	 * 下载新闻详细信息
//	 * 
//	 * @param story_id
//	 *            新闻ID
//	 */
//	private void requestEventDetail(final int eventid) {
//		// 非在线登录/网络不通时,返回
//		// LoginManager instance =
//		// LoginManager.getInstance(GlobalVariables.gAppContext);
//		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) { // LoginManager.OnlineStatus.ONLINE_LOGINED
//																								// !=
//																								// instance.getOnlineStatus()
//																								// ||
//			refreshUI();
//			return;
//		}
//
//		int getall = 0;
//		if (!isInitData)
//			getall = 1;
//		mProgressBar.setVisibility(View.VISIBLE);
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
//
//			@Override
//			public void onSuccess() {
//				Message msg = new Message();
//				msg.what = DOWN;
//				mHandler.sendMessageDelayed(msg, 200);
//			}
//
//			@Override
//			public void onCallbackError(String err) {
//				Message msg = new Message();
//				msg.what = NOT_DOWN;
//				msg.obj = err;
//				mHandler.sendMessageDelayed(msg, 100);
//			}
//
//			@Override
//			public void onReloginError() {
//				// TODO Auto-generated method stub
//				super.onReloginError();
//				Log.i("zjj", "通告详细:自动重登录失败");
//			}
//
//			@Override
//			public void onReloginSuccess() {
//				// TODO Auto-generated method stub
//				super.onReloginSuccess();
//				Log.i("zjj", "通告详细:自动重登录成功");
//				if (isRequestRelogin) {
//					requestEventDetail(eventid); // 自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		if (!isSentEvent) {
//			mRequestOperation.sendGetNeededInfo("GetEventDetail", new Object[] { String.valueOf(eventid), getall, callback }, callback.getClass().getName());
//		} else {
//			mRequestOperation.sendGetNeededInfo("GetPublishEventDetail", new Object[] { String.valueOf(eventid), callback }, callback.getClass().getName());
//		}
//
//	}

	/**
	 * 从库读取
	 */
	private void GetDataInDB(int id) {
//		if (!isSentEvent) {
//			mEventCursor = mEventsDB.getOneEventCursor(id, mUsername);
//		} else {
//			mEventCursor = mEventsDB.getOnePublishEventCursor(id, mUsername);
//		}
//		if (mEventCursor != null) {
//			mEventCursor.requery();
//			mEventCursor.moveToFirst();
//			if (!isSentEvent) {
//				mEventDetailsItem = mEventsDB.retrieveEventDetailItem(mEventCursor);
//			} else {
//				mEventDetailsItem = mEventsDB.retrievePublishEventDetailItem(mEventCursor);
//			}
//			if (mEventDetailsItem != null) {
//				if (mEventDetailsItem.imgs.size() > 0)
//					imageList = mEventDetailsItem.imgs;
//			}
//		} else {
//			//requestEventDetail(eventid);// 从网络下载数据
//			mProgressBar.setVisibility(View.VISIBLE);
//			Boolean bReturn = mClassAlbumDetailActivityManager.getEventDetail(isSentEvent, !isInitData, eventid, mHandler);
//			if(!bReturn)
//				refreshUI();
//		}
//		mEventCursor.close();
//		showProgressBar();
		
		if(mClassVideoDetailActivityManager != null)
			mEventDetailsItem = mClassVideoDetailActivityManager.GetDataInDB(isSentEvent,id);
	}

	/**
	 * refreshUI
	 */
	private void refreshUI() {
		GetDataInDB(eventid);
		if (mEventDetailsItem != null) {// 是否收藏
			if (mEventDetailsItem.bookmark)
				collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_bookmark_btn));
			else
				collect.setBackgroundDrawable(getResources().getDrawable(R.drawable.detail_unbookmark_btn));
//			mEventsDB.markAsRead(mEventDetailsItem);
		}

		if (mEventDetailsItem.title != null)
			photoName.setText(mEventDetailsItem.title);
		
		photo_Info.setTextColor(getResources().getColor(R.color.dark_gray));
		String sender = getResources().getString(R.string.sender) + mEventDetailsItem.pubname;
		String receiver = getResources().getString(R.string.addressee) + mEventDetailsItem.owner;
		String time = getResources().getString(R.string.senttime) + DateFormatter.getStringYYYYMMDDHHmm(mEventDetailsItem.post);
//		String location = getResources().getString(R.string.location) + mEventDetailsItem.location;
		String size = "";
		if(mEventDetailsItem.imgs.size()>0){
			size = mEventDetailsItem.imgs.get(0).size;
			if(size.length()>0){
				size = MyMothod.getSize(Double.valueOf(mEventDetailsItem.imgs.get(0).size));
			}
			size = getResources().getString(R.string.size) + size;
		}
		photo_Info.setText(sender + "\n" + receiver + "\n" + time + "\n" + size);	//+ "\n" + location);
		
//		if (imageList.size() > 1) {
//			downloadAll.setVisibility(View.VISIBLE);
//		} else {
//			downloadAll.setVisibility(View.GONE);
//		}

		if (isSentEvent) {
//			mTxtViewReadcount.setText(getResources().getString(R.string.eventscheckedsum) + mEventDetailsItem.readcount + "/" + mEventDetailsItem.recvtotal);

			boolean hasnewreply = false;
			if (mEventDetailsItem.listReplyer.size() > 0) {
				// 每一个回复的最后回复时间 与 通告的已看的最后回复的时间对比,是否有新回复
//				long newestlastupdatetime = 0;
				EventsReplyActivityManagement eram = new EventsReplyActivityManagement(ClassVideoDetailActivity.this,mEventDetailsItem.user);
				int sum = 0;
				for (int i = 0; i < mEventDetailsItem.listReplyer.size(); i++) {
					Replyer rp = mEventDetailsItem.listReplyer.get(i);
					if (DateFormatter.getDateFromSecondsString(rp.lastawstimeread).getTime() < DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime()) {
						hasnewreply = true;
					}

					if (hasnewreply)
						grade.setBackgroundResource(R.drawable.detail_new_feedback_btn_selector);
					else
						grade.setBackgroundResource(R.drawable.detail_feedback_btn_selector);

//					if (DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime() > newestlastupdatetime) {
//						newestlastupdatetime = DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime();
//					}
					sum += eram.getAwscontentSum(eventid, rp.ReplyerId);
				}
//				Log.i("zjj", "从详细中更新通告 newestlastupdatetime:" + newestlastupdatetime);
//				mClassVideoDetailActivityManager.UpdataSendEventLastawstime(newestlastupdatetime, mEventDetailsItem);

				Log.i("zjj", "从详细中更新已发通告 本地已读聊天内容总数:" + sum);
				mClassVideoDetailActivityManager.updataSendEventAwscoutnclient(sum,mEventDetailsItem);
				
				grade.setVisibility(View.VISIBLE);
			} else
				grade.setVisibility(View.GONE);
		} else {
			//如果是自己发的通告,则不显示回复按钮
			if(mEventDetailsItem.pubid.toLowerCase().equals(mEventDetailsItem.user.toLowerCase()) ){
				grade.setVisibility(View.GONE);
			}else{
				// 每一个回复的最后回复时间 与 通告的已看的最后回复的时间对比,是否有新回复
				Date newestlastupdatetime = new Date(0);
				if (mEventDetailsItem.listReplyer.size() > 0) {
					for (int i = 0; i < mEventDetailsItem.listReplyer.size(); i++) {
						Replyer rp = mEventDetailsItem.listReplyer.get(i);
						if (DateFormatter.getDateFromSecondsString(rp.lastawstimeread).getTime() < DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime()) {
							grade.setBackgroundResource(R.drawable.detail_new_feedback_btn_selector);
						}
	
						if (DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime() > newestlastupdatetime.getTime()) {
							newestlastupdatetime = DateFormatter.getDateFromSecondsString(rp.ReplyLastTime);
						}
					}
					Log.i("zjj", "从详细中更新通告 newestlastupdatetime:" + newestlastupdatetime);
					mClassVideoDetailActivityManager.UpdataEventLastawstime(newestlastupdatetime, mEventDetailsItem);
					grade.setVisibility(View.VISIBLE);
				} else {
					grade.setVisibility(View.GONE);
				}
			}
		}
		
		if(mEventDetailsItem.imgs.size() > 0){
			Log.i("zjj", "影音详细封面URL:" + mEventDetailsItem.imgs.get(0).preview);
			downLoadImage(mEventDetailsItem.imgs.get(0).preview);
		}
		
		setTitleText(ModuleNameDefine.getEventsModuleNamebyId(mEventDetailsItem.type)+getString(R.string.detail));
	}

	private AnimationListener mAnimationPlayListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			play();
		}
	};
	
	/**
	 * 播放
	 */
	private void play(){
		if(mEventDetailsItem.imgs.size()>0)
			mClassVideoDetailActivityManager.Play(mEventDetailsItem.imgs.get(0).URL,mEventDetailsItem.imgs.get(0).preview);//"rtsp://192.168.12.171/a.ts");//
	}
	
	/**
	 * 下载图片 s
	 * 
	 * @param index
	 */
	private void downLoadImage(String url) {
		if(url.equals("")){
			return;
		}
		
		String fileName = "";
		if(url.lastIndexOf(".") > -1){
			fileName = "image" + String.valueOf(eventid) + "_" + url.substring(url.lastIndexOf("."), url.length());
		}else{
			fileName = "image" + String.valueOf(eventid) + "_jpg";
		}
		
		Log.i("zjj", "视频相册 url:"  + url + ",fileName:" + fileName);
		getmClassImageLoader().loadFile(url, false, fileName, new FileCallback() {

			@Override
			public void fileLoaded(boolean isDone) {
					Message msg = new Message();
					msg.what = FINISHEDDOWN;
					mHandler.sendMessage(msg);
				}
		});
	}

	/**
	 * 分享
	 */
	private void Share() {
		if (mEventDetailsItem.imgs.size() > 0) {
			// String strtest = "</br>hello </ddd> world </br> io&nbsp;kdf";
			// 过滤所有以<开头以>结尾的标签
			String regEx = "<([^>]*)>";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(mEventDetailsItem.imgs.get(0).URL);
			Log.i("zjj", "分享" + mEventDetailsItem.imgs.get(0).URL);
			String strbody = m.replaceAll("").trim();
			// 过滤&nbsp;等标签(&开头;结尾)
			regEx = "&([^>]*);";
			p = Pattern.compile(regEx);
			m = p.matcher(strbody);
			strbody = m.replaceAll("").trim();
			if (strbody.length() > 126)
				strbody = strbody.substring(0, 126) + "……";
			if (mEventDetailsItem != null)
				CommonActions.shareContent(ClassVideoDetailActivity.this, mEventDetailsItem.title, mEventDetailsItem.title + "\n" + strbody);

		}
	}
}