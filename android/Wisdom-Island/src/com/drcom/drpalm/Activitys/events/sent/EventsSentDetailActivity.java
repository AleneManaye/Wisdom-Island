package com.drcom.drpalm.Activitys.events.sent;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.ImgsAttc.ImageAttcGalleryActivity;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Tool.CommonActions;
import com.drcom.drpalm.Tool.SendpermisManagement;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.drHttpClient.DrRequestImgsTask;
import com.drcom.drpalm.View.controls.MultiDirectionSlidingDrawer;
import com.drcom.drpalm.View.controls.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.drcom.drpalm.View.controls.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.drcom.drpalm.View.controls.MyMainItem;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.View.events.reply.EventsReplyActivityManagement;
import com.drcom.drpalm.View.events.sent.EventsSentDetailActivityManagement;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDetailsItem.Replyer;
import com.drcom.drpalm.objs.EventDraftItem;
import com.wisdom.island.R;

public class EventsSentDetailActivity extends ModuleActivity{
	public static String KEY_EVENT_ID = "KEY_EVENT_ID";
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	//变量
	private EventDetailsItem mEventDetailsItem = new EventDetailsItem();
	private int mEvent_id = -1;
	private String mUsername = "";	
	private SettingManager setInstance ;	
	private EventsDB mEventsDB;
//	private Cursor mEventCursor = null;
	private int mDownloadingAttcIndex = 0;
//	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private RelativeLayout.LayoutParams linearParams;
	private EventsSentDetailActivityManagement mEventsSentDetailActivityManagement;
	
	//控件
	private TextView mTxtViewReadcount;
	private TextView mTxtViewTitle;
	private TextView mTxtViewSender;
	private TextView mTxtViewReceiver;
	private TextView mTxtViewSendtime;
//	private TextView mTxtViewStarttime;
//	private TextView mTxtViewEndtime;
//	private TextView mTxtViewLocation;
//	private TextView mTxtViewBody;
	private WebView mWebViewBody;
	private Button mButtonResend;	//选项
    private MyMainItem mButtonAttachment;
	private Button mButtonReply;
//	private Button mButtonShare;
//	private Button mButtonMark;
//	private LinearLayout mTableLayout;
	private ImageView mImageViewDate;
	private MultiDirectionSlidingDrawer mDrawer;
	private RelativeLayout mRelativeLayoutReadcount;
	private SlidingDrawer moduleDrawer;	//功能菜单
	private Button mBtnRetransmission,mBtnReplace,mBtnDel,mBtnCancel;	//菜单功能按钮
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_detail_view2, mLayout_body);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(KEY_EVENT_ID)){
			mEvent_id = extras.getInt(KEY_EVENT_ID);
		}
		
		setInstance = SettingManager.getSettingManager(this);	
		mEventsDB = EventsDB.getInstance(this,GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		
		mEventsSentDetailActivityManagement = new EventsSentDetailActivityManagement(EventsSentDetailActivity.this,mUsername);
		
		//
		mTxtViewTitle = (TextView)findViewById(R.id.eventsdetali_title_txtview);
		mTxtViewSender = (TextView)findViewById(R.id.eventsdetali_sender_txtview);
		
		mTxtViewReceiver = (TextView)findViewById(R.id.eventsdetali_receiver_txtview);
		mTxtViewSendtime = (TextView)findViewById(R.id.eventsdetali_sendtime_txtview);
//		mTxtViewStarttime = (TextView)findViewById(R.id.eventsdetali_starttime_txtview);
//		mTxtViewEndtime = (TextView)findViewById(R.id.eventsdetali_endtime_txtview);
//		mTxtViewLocation = (TextView)findViewById(R.id.eventsdetali_location_txtview);
//		mTxtViewBody = (TextView)findViewById(R.id.eventsdetali_body_txtview);
//		mTableLayout = (LinearLayout)findViewById(R.id.eventsdetali_awsorglist_LinearLayout);
		moduleDrawer = (SlidingDrawer) findViewById(R.id.drawer_menu);// 菜单抽屉组件
		mBtnRetransmission = (Button)findViewById(R.id.DrawerButtonRetransmission);
		mBtnRetransmission.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDrawer();
				
				Intent i = new Intent(EventsSentDetailActivity.this, NewEventActivity.class);
				i.putExtra(NewEventActivity.KEY_DETAILITEM, mEventDetailsItem);
				i.putExtra(NewEventActivity.KEY_EDITTYPE, NewEventActivity.EDITTYPE_RETRANS);
				startActivity(i);
			}
		});
		
		mBtnReplace = (Button)findViewById(R.id.DrawerButtonReplace);
		mBtnReplace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDrawer();
				
				Intent i = new Intent(EventsSentDetailActivity.this, NewEventActivity.class);
				i.putExtra(NewEventActivity.KEY_DETAILITEM, mEventDetailsItem);
				i.putExtra(NewEventActivity.KEY_EDITTYPE, NewEventActivity.EDITTYPE_REPLACE);
				startActivity(i);
			}
		});
		
		mBtnDel = (Button)findViewById(R.id.DrawerButtonDel);
		mBtnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DelEvent();
				closeDrawer();
			}
		});
		
		mBtnCancel = (Button)findViewById(R.id.DrawerButtonCancel);
		mBtnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDrawer();
			}
		});

		mRelativeLayoutReadcount = (RelativeLayout)findViewById(R.id.eventsdetali_RLayout_readcount);
		mRelativeLayoutReadcount.setVisibility(View.VISIBLE);
		
		mTxtViewReadcount = (TextView)findViewById(R.id.eventsdetali_readcount_txtview);
		mTxtViewReadcount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventsSentDetailActivity.this,EventsSentReaderActivity.class);
				intent.putExtra(EventsSentReaderActivity.KEY_EVENT_ID, mEventDetailsItem.eventid);
				startActivity(intent);
			}
		});
		
		mImageViewDate = (ImageView)findViewById(R.id.eventsdetali_imgview);
		mImageViewDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEventsSentDetailActivityManagement.SaveCalendar(mEventDetailsItem);
			}
		});
		
		mWebViewBody = (WebView)findViewById(R.id.eventsdetali_body_webview);
		mWebViewBody.getSettings().setDefaultTextEncodingName("utf-8") ;
		mWebViewBody.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);	//自适应
		mWebViewBody.getSettings().setBuiltInZoomControls(false); 	//禁止缩放
		
		linearParams = (RelativeLayout.LayoutParams) mWebViewBody.getLayoutParams();
		mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
//				Log.i("xpf", "onOpen h= " + mDrawer.getDrawerHeight() + " hadlerHeigth= " + mDrawer.getDrawerHeight1() + " DrawerContentHeigth= " + mDrawer.getDrawerHeight2());
				linearParams.setMargins(MyMothod.Px2Dp(EventsSentDetailActivity.this, 20), mDrawer.getDrawerHeight(), MyMothod.Px2Dp(EventsSentDetailActivity.this, 20), 0);
				mWebViewBody.setLayoutParams(linearParams);
				mTxtViewTitle.setBackgroundResource(R.drawable.top_switcher_expanded);
			}
		});
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
//				Log.i("xpf", "onClose h= " + mDrawer.getDrawerHeight() + " hadlerHeigth= " + mDrawer.getDrawerHeight1() + " DrawerContentHeigth= " + mDrawer.getDrawerHeight2());
				linearParams.setMargins(MyMothod.Px2Dp(EventsSentDetailActivity.this, 20), mDrawer.getDrawerHeight1(), MyMothod.Px2Dp(EventsSentDetailActivity.this, 20), 0);
				mWebViewBody.setLayoutParams(linearParams);
				mTxtViewTitle.setBackgroundResource(R.drawable.top_switcher_collapsed);
			}
		});
		
		InitTitlebar();
		InitToolbar();
		sendGetEventDetailRequest(mEvent_id);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		GetDataInDB(mEvent_id);
		ReflashUI();
		SetIsread();
	}
	
	@Override
	protected void onDestroy() {
//		mEventCursor.close();
//		mEventCursor = null;
		super.onDestroy();
	};
	
	private void InitTitlebar(){
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar) , MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		//选项
		mButtonResend = new Button(this);
		mButtonResend.setBackgroundResource(R.drawable.btn_title_blue_selector);
		mButtonResend.setText(getString(R.string.btn_menu));
		mButtonResend.setTextAppearance(EventsSentDetailActivity.this, R.style.TitleBtnText);
		mButtonResend.setLayoutParams(p);
		mButtonResend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				EventDraftItem newsItem = new EventDraftItem();
				
//				Intent i = new Intent(EventsSentDetailActivity.this, NewEventActivity.class);
//				i.putExtra(NewEventActivity.KEY_DETAILITEM, mEventDetailsItem);
////				i.putExtra(NewEventActivity.KEY_DRAFTITEM_ID, newsItem.pk_id);
//				startActivity(i);
				
				
				
				if (!moduleDrawer.isOpened()) {
					openDrawer();
				}
				
			}
		});
		setTitleRightButton(mButtonResend);
	}
	
	private void InitToolbar(){
		
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Toolbar) , MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Toolbar));
		p.setMargins(MyMothod.Dp2Px(this, 5), 0, MyMothod.Dp2Px(this, 5), 2);
		
		//附件
		mButtonAttachment = new MyMainItem(this);
		mButtonAttachment.setLayoutParams(p);
        mButtonAttachment.setSize(GlobalVariables.btnWidth_Toolbar,GlobalVariables.btnHeight_Toolbar);
//		mButtonAttachment.setBackgroundResource(R.drawable.detail_attc_btn_selector);
        mButtonAttachment.setDelVisible(false);
        mButtonAttachment.setImgRsid(R.drawable.detail_attc_btn_selector);
        mButtonAttachment.setText("");
//		mButtonAttachment.setBackgroundResource(R.drawable.detail_attc_btn_selector);
		mButtonAttachment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] urls = new String[mEventDetailsItem.imgs.size()];
				String[] names = new String[mEventDetailsItem.imgs.size()];
				for(int i = 0;i < urls.length; i++){
					urls[i] = mEventDetailsItem.imgs.get(i).URL;
					names[i] = mEventDetailsItem.imgs.get(i).attname;
				}
				
				Intent i = new Intent(EventsSentDetailActivity.this, ImageAttcGalleryActivity.class);
				i.putExtra(ImageAttcGalleryActivity.KEY_IMGSURL, urls); 
				i.putExtra(ImageAttcGalleryActivity.KEY_IMGSNAME, names); 
				EventsSentDetailActivity.this.startActivity(i);
			}
		});
		
		//回复
		mButtonReply = new Button(this);
		mButtonReply.setLayoutParams(p);
		mButtonReply.setBackgroundResource(R.drawable.detail_feedback_btn_selector);
		mButtonReply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				mEventsDB.markAsReadFeedback(mEventDetailsItem);
				Intent i = new Intent(EventsSentDetailActivity.this, ReplyersActivity.class);
				i.putExtra(ReplyersActivity.KEY_EVENT, mEventDetailsItem); 
				EventsSentDetailActivity.this.startActivity(i);
			}
		});
		
//		//分享
//		mButtonShare = new Button(this);
//		mButtonShare.setLayoutParams(p);
//		mButtonShare.setBackgroundResource(R.drawable.detail_share_btn_selector);
//		mButtonShare.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Share();
//			}
//		});
//		
//		//收藏
//		mButtonMark = new Button(this);
//		mButtonMark.setLayoutParams(p);
//		mButtonMark.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(mEventDetailsItem.bookmark){
//					mButtonMark.setBackgroundResource(R.drawable.detail_unbookmark_btn);
//					SetBookmark(false);
//				}
//				else{
//					mButtonMark.setBackgroundResource(R.drawable.detail_bookmark_btn);
//					SetBookmark(true);
//				}
//			}
//		});

		ToolbarAddLeftButton(mButtonAttachment);
		ToolbarAddRightButton(mButtonReply);
//		ToolbarAddRightButton(mButtonShare);
//		ToolbarAddRightButton(mButtonMark);
	}
	
//	/**
//	 * 初始化回复人列表
//	 */
//	private void initReplyerList(){
////        ReplyerlistitemView v = new ReplyerlistitemView(this);
////        v.setName("reply 1");
////        v.setDate("2013-01-02");
////        mTableLayout.addView(v);
//		mTableLayout.removeAllViews();
//        
//        for(int i = 0; i < mEventDetailsItem.listReplyer.size(); i++){
//			final Replyer rp = mEventDetailsItem.listReplyer.get(i);
//
//			ReplyerlistitemView v = new ReplyerlistitemView(this);
//			v.setReplyer(rp);
//			v.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent i = new Intent(EventsSentDetailActivity.this, EventsReplyActivity.class);
//					i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, mEvent_id);
//					i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,rp.ReplyerId);
//					Log.i("zjj", "点击查看回复内容:" + mEvent_id + "," + rp.ReplyerId);
//					EventsSentDetailActivity.this.startActivity(i);
//				}
//			});
//			mTableLayout.addView(v);
//        }
//	}
	
	/**
	 * 请求网络(取列表)
	 * @param lastActivityId 
	 * @param uiHandler
	 */
	private void sendGetEventDetailRequest (final int id){
		showProgressBar();
		mEventsSentDetailActivityManagement.sendGetEventDetailRequest(id, mHandler);
		
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
//			@Override
//			public void onSuccess() {
//				GetDataInDB(id);
//				
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFINISH;
//				mHandler.sendMessage(message) ;
//			}
//
//			@Override
//			public void onCallbackError(String err) {
//				// TODO Auto-generated method stub
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED;
//				message.obj = err;
//				mHandler.sendMessage(message);
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
//				if(isRequestRelogin){
//					sendGetEventDetailRequest(mEvent_id);	//自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetPublishEventDetail", new Object[]{String.valueOf(id),callback},callback.getClass().getName());
	}
	
	/**
	 * 从库读取
	 */
	private void GetDataInDB(int id){
//		mEventCursor = mEventsDB.getOnePublishEventCursor(id,mUsername);
//		mEventCursor.requery();
//		mEventCursor.moveToFirst();
//		mEventDetailsItem = mEventsDB.retrievePublishEventDetailItem(mEventCursor);
//		mEventCursor.close();
		
		mEventDetailsItem = mEventsSentDetailActivityManagement.GetDataInDB(mEventsDB, id);
	}
	
	/**
	 * 刷新界面
	 */
	private void ReflashUI(){
		mTxtViewReadcount.setText(getResources().getString(R.string.eventscheckedsum) 
				+  mEventDetailsItem.readcount 
				+ "/" 
				+ mEventDetailsItem.recvtotal);
		mTxtViewTitle.setText( mEventDetailsItem.title);
		mTxtViewSender.setText(getResources().getString(R.string.sender) + mEventDetailsItem.pubname);
		mTxtViewReceiver.setText(getResources().getString(R.string.addressee) + mEventDetailsItem.owner);
		mTxtViewSendtime.setText(getResources().getString(R.string.senttime) + DateFormatter.getStringYYYYMMDD(mEventDetailsItem.post));
//		mTxtViewStarttime.setText(getResources().getString(R.string.start_time) + DateFormatter.getStringYYYYMMDD(mEventDetailsItem.start));
//		mTxtViewEndtime.setText(getResources().getString(R.string.end_time) + DateFormatter.getStringYYYYMMDD(mEventDetailsItem.end));
//		mTxtViewLocation.setText(getResources().getString(R.string.location) + mEventDetailsItem.location);
		//如果WebView重复两次loadData,第2次的内容不会显示
		//所以这样保证有内容时,才显示到WebView中
		if(mEventDetailsItem.body.trim().length() > 0){
//			mWebViewBody.loadData(HtmlstrManager.getEventsContentStr(mEventDetailsItem.body), "text/html", "utf-8");
			String templateHtml =  mEventsSentDetailActivityManagement.readTextFromResource(R.raw.events_detail);
			// Set Body
			templateHtml = templateHtml.replace("__BODY__", mEventDetailsItem.body);
			mWebViewBody.loadDataWithBaseURL("file:///android_res/raw/", templateHtml, "text/html", "UTF-8", null);
		}
		
		if(mEventDetailsItem.imgs.size()>0){
			mButtonAttachment.setVisibility(View.VISIBLE);
            Log.i("NUMBER_LIANG",mEventDetailsItem.imgs.size() + "");
            mButtonAttachment.setNum(mEventDetailsItem.imgs.size() + "");
        }
		else
			mButtonAttachment.setVisibility(View.GONE);
		
//		if(mEventDetailsItem.bookmark)
//			mButtonMark.setBackgroundResource(R.drawable.detail_bookmark_btn);
//		else
//			mButtonMark.setBackgroundResource(R.drawable.detail_unbookmark_btn);
		
		boolean hasnewreply = false;
		if(mEventDetailsItem.listReplyer.size() > 0){
//			//每一个回复的最后回复时间 与 通告的已看的最后回复的时间对比,是否有新回复
//			long newestlastupdatetime = 0;
			EventsReplyActivityManagement eram = new EventsReplyActivityManagement(EventsSentDetailActivity.this,mUsername);
			int sum = 0;
			for(int i = 0; i < mEventDetailsItem.listReplyer.size(); i++){
				Replyer rp = mEventDetailsItem.listReplyer.get(i);
				if(DateFormatter.getDateFromSecondsString(rp.lastawstimeread ).getTime() < DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime()){
					hasnewreply = true;
				}
				
				if(hasnewreply)
					mButtonReply.setBackgroundResource(R.drawable.detail_new_feedback_btn_selector);
				else
					mButtonReply.setBackgroundResource(R.drawable.detail_feedback_btn_selector);
				
//				if(DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime() > newestlastupdatetime){
//					newestlastupdatetime = DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime();
//				}
				sum += eram.getAwscontentSum(mEvent_id, rp.ReplyerId);
			}
//			Log.i("zjj", "从详细中更新已发通告 newestlastupdatetime:" + newestlastupdatetime);
//			mEventsDB.updataSendEventLastawstime(newestlastupdatetime,mEventDetailsItem);
			
			Log.i("zjj", "从详细中更新已发通告 本地已读聊天内容总数:" + sum);
			mEventsDB.updataSendEventAwscoutnclient(sum,mEventDetailsItem);
			
			mButtonReply.setVisibility(View.VISIBLE);
		}else
			mButtonReply.setVisibility(View.GONE);
		
//		//不能重发
//		Log.i("zjj", "重发 " + mEventDetailsItem.status);
//		if(mEventDetailsItem.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
//			mButtonResend.setVisibility(View.GONE);
//		}
		
		//标题
		setTitleText(ModuleNameDefine.getEventsModuleNamebyId(mEventDetailsItem.type)+getString(R.string.detail));
		
		//菜单功能
		if(mEventDetailsItem.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){	//被替换的只能转发
			mBtnReplace.setVisibility(View.GONE);
			mBtnDel.setVisibility(View.GONE);
		}else{
			if(!mEventDetailsItem.pubid.equals(mUsername) ){
				mBtnReplace.setVisibility(View.GONE);
				mBtnDel.setVisibility(View.GONE);
			}else{
				mBtnReplace.setVisibility(View.VISIBLE);
				mBtnDel.setVisibility(View.VISIBLE);
			}
		}
		
		//权限
		SendpermisManagement sm = new SendpermisManagement();
		if (!sm.isCanSend(String.valueOf(mEventDetailsItem.type))) {
			mButtonResend.setVisibility(View.GONE);
		}
		
//		initReplyerList();
		hideProgressBar();
	}
	
	/**
	 * 设置为已读
	 */
	private void SetIsread(){
		mEventsDB.markAreadySendAsRead(mEventDetailsItem);
	}
	
	/**
	 * 设置为收藏
	 */
	private void SetBookmark(boolean b){
		mEventsDB.markSentAsBookmark(mEventDetailsItem,b);
		mEventDetailsItem.bookmark = b;
	}
	
	/**
	 * UIHandler
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == UPDATEFINISH){
				GetDataInDB(mEvent_id);
				ReflashUI();
				if(mEventDetailsItem.imgs.size()>0){
					GetData(mEventDetailsItem.imgs.get(mDownloadingAttcIndex).URL);
				}
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(EventsSentDetailActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
			}
			
			hideProgressBar();
		}
	};
	
	/**
	 * 回复人列表Item
	 * @author zhaojunjie
	 *
	 */
//	private class ReplyerlistitemView extends LinearLayout{
//		private Replyer mReplyer;
//		
//		public ReplyerlistitemView(Context context) {
//			super(context);
//			LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			inflater.inflate( R.layout.replyerlist_item_view, this);
//	        setBackgroundResource(R.drawable.list_item_bg_selector);
//		}
//		
//		public void setReplyer(Replyer replyer){
//			this.mReplyer = replyer;
//			
//			TextView et1 = (TextView)findViewById(R.id.replyerlist_item_name_txtview);
//        	et1.setText(replyer.ReplyerName);
//        	
//        	TextView et2 = (TextView)findViewById(R.id.replyerlist_item_date_txtview);
//        	et2.setText(DateFormatter.getStringYYYYMMDD(DateFormatter.getDateFromMilliSecondsString(replyer.ReplyLastTime)));
//		}
//		
//		public Replyer getReplyer(){
//			return mReplyer;
//		}
//	}
	
	/**
	 * 下载图片
	 */
	private void GetData(String url){
		
		//没图片则访问网络获取
        DrRequestImgsTask task = new DrRequestImgsTask(
        		EventsSentDetailActivity.this,
        		url,
        		false,
        		callBack(EventsSentDetailActivity.this,url));
		task.execute();
//		System.out.println(":::::缩略图,从网络读取:" + item.title + "," + item.thumbURL);
	}
	
	/**
	 * 下载图片回调
	 * @param context
	 * @param url
	 * @return
	 */
	private UICallBack callBack(Context context,final String url) {
		return new UICallBack() {
//			byte[] attachmentBytes;
			@Override
			public void callBack(Object b) {
				if (b != null)
//					attachmentBytes = (byte[])b;
//					b = null;
					
//					Bitmap bmp = BitmapCache.getInstance().getBitmap(url,attachmentBytes,true);
//					thumbimgview.setBackgroundColor(Color.TRANSPARENT);
//					thumbimgview.setImageBitmap(bmp);
					
					//保存到数据库
//					synchronized(mActivitysDB){
//						mActivitysDB.startTransaction() ;
//						mActivitysDB.saveAttachment(item.thumbURL,attachmentBytes,"thumbpic") ;
//						mActivitysDB.endTransaction() ;
//					}
//					attachmentBytes = null;
//					bmp = null;
					
					mEventDetailsItem.imgs.get(mDownloadingAttcIndex).imgData = (byte[])b;
					mEventDetailsItem.imgs.get(mDownloadingAttcIndex).fileId = GlobalVariables.calcCrc((byte[])b);
					if(url.indexOf(".") > -1){
						mEventDetailsItem.imgs.get(mDownloadingAttcIndex).fileType = url.substring(url.lastIndexOf(".") + 1);
					}
					
					if(mDownloadingAttcIndex < mEventDetailsItem.imgs.size() - 1){
						mDownloadingAttcIndex ++;
						GetData(mEventDetailsItem.imgs.get(mDownloadingAttcIndex).URL);
					}
					
					b = null;
					return;
			}
		};
	}
	
	/**
	 * 分享
	 */
	private void Share(){
//		String strtest = "</br>hello </ddd> world </br> io&nbsp;kdf";
        // 过滤所有以<开头以>结尾的标签
        String regEx= "<([^>]*)>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(mEventDetailsItem.body);
        
        String strbody =  m.replaceAll("").trim();
        // 过滤&nbsp;等标签(&开头;结尾)
        regEx = "&([^>]*);";
        p = Pattern.compile(regEx);
        m = p.matcher(strbody);
        strbody =  m.replaceAll("").trim();
        if(mEventDetailsItem.imgs.size()>0){
        	CommonActions.shareContent(EventsSentDetailActivity.this, mEventDetailsItem.title, mEventDetailsItem.title + "\n" + strbody + "\n" + mEventDetailsItem.imgs.get(0).URL);
        }else{
        	CommonActions.shareContent(EventsSentDetailActivity.this, mEventDetailsItem.title, mEventDetailsItem.title + "\n" + strbody);
        }
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
	private void DelEvent(){
		mEventsSentDetailActivityManagement.DelEvent(mEvent_id + "", mHandlerDel);
	}
	
	private Handler mHandlerDel = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == UPDATEFINISH){
				mEventsSentDetailActivityManagement.DelEventInDB(mEventsDB, mEvent_id);
				finishDraw();
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(EventsSentDetailActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
			}
			
			hideProgressBar();
		}
	};
}
