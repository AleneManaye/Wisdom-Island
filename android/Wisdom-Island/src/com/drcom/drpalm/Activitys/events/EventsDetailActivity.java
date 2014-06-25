package com.drcom.drpalm.Activitys.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
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
import com.drcom.drpalm.Activitys.events.review.ReviewTestActivity;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.controls.MultiDirectionSlidingDrawer;
import com.drcom.drpalm.View.controls.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.drcom.drpalm.View.controls.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.drcom.drpalm.View.controls.MyMainItem;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.events.EventsDetailActivityManagement;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.wisdom.island.R;

public class EventsDetailActivity extends ModuleActivity{
	public static String KEY_EVENT_ID = "KEY_EVENT_ID";
	public static String KEY_INIT = "KEY_INIT";
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	//变量
	private boolean isInitData = true;	//是否初始化数据(从最新消息进入,此值为FALSE)
	private EventDetailsItem mEventDetailsItem = new EventDetailsItem();
	private int mEvent_id = -1;
//	private String mUsername = "";	
//	private SettingManager setInstance ;	
//	private EventsDB mEventsDB;
//	private Cursor mEventCursor = null;
//	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private EventsDetailActivityManagement mEventsDetailActivityManagement;
	
	//控件
	private SlidingDrawer tellFriendDrawaer;// share抽屉控件
	private TextView mTxtViewTitle;
	private TextView mTxtViewSender;
	private TextView mTxtViewReceiver;
	private TextView mTxtViewSendtime;
//	private TextView mTxtViewStarttime;
//	private TextView mTxtViewEndtime;
//	private TextView mTxtViewLocation;
//	private TextView mTxtViewBody;
	private WebView mWebViewBody;
	private MyMainItem mButtonAttachment;
	private Button mButtonReply;
	private Button mButtonShare;
	private Button mButtonMark;
	private Button mButtonReview;	//回评
//	private LinearLayout mTableLayout;
	private ImageView mImageViewDate;
	private MultiDirectionSlidingDrawer mDrawer;
	private RelativeLayout.LayoutParams linearParams;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.events_detail_view2, mLayout_body);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(KEY_EVENT_ID)){
			mEvent_id = extras.getInt(KEY_EVENT_ID);
		}
		if(extras.containsKey(KEY_INIT)){
			isInitData = extras.getBoolean(KEY_INIT);
		}
		
		mEventsDetailActivityManagement = new EventsDetailActivityManagement(EventsDetailActivity.this,mEvent_id);
		
//		setInstance = SettingManager.getSettingManager(this);	
//		mEventsDB = EventsDB.getInstance(this,GlobalVariables.gSchoolKey);
//		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		
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
		mImageViewDate = (ImageView)findViewById(R.id.eventsdetali_imgview);
		mImageViewDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEventsDetailActivityManagement.SaveCalendar(mEventDetailsItem);
			}
		});
		
		mWebViewBody = (WebView)findViewById(R.id.eventsdetali_body_webview);
//		mWebViewBody.setVerticalScrollbarOverlay(true);	//隐藏滚动条
		mWebViewBody.getSettings().setDefaultTextEncodingName("utf-8") ;
		mWebViewBody.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);	//自适应
		mWebViewBody.getSettings().setBuiltInZoomControls(true); 	//不显示放大缩小按钮
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			mWebViewBody.getSettings().setDisplayZoomControls(false);	//可缩放 2.3以下没此方法
		}
		
//	    //不显示滚动条
		mWebViewBody.setHorizontalScrollBarEnabled(false);
		mWebViewBody.setVerticalScrollBarEnabled(false);
//	    webview.setInitialScale(39); 
	    //支持JS
		mWebViewBody.getSettings().setJavaScriptEnabled(true);
		
		linearParams = (RelativeLayout.LayoutParams) mWebViewBody.getLayoutParams();
		mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
//				Log.i("xpf", "onOpen h= " + mDrawer.getDrawerHeight() + " hadlerHeigth= " + mDrawer.getDrawerHeight1() + " DrawerContentHeigth= " + mDrawer.getDrawerHeight2());
				linearParams.setMargins(MyMothod.Px2Dp(EventsDetailActivity.this, 20), mDrawer.getDrawerHeight(), MyMothod.Px2Dp(EventsDetailActivity.this, 20), 0);
				mWebViewBody.setLayoutParams(linearParams);
				mTxtViewTitle.setBackgroundResource(R.drawable.top_switcher_expanded);
			}
		});
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
//				Log.i("xpf", "onClose h= " + mDrawer.getDrawerHeight() + " hadlerHeigth= " + mDrawer.getDrawerHeight1() + " DrawerContentHeigth= " + mDrawer.getDrawerHeight2());
				linearParams.setMargins(MyMothod.Px2Dp(EventsDetailActivity.this, 20), mDrawer.getDrawerHeight1(), MyMothod.Px2Dp(EventsDetailActivity.this, 20), 0);
				mWebViewBody.setLayoutParams(linearParams);
				mTxtViewTitle.setBackgroundResource(R.drawable.top_switcher_collapsed);
			}
		});
		
		if(isInitData){
			GetDataInDB(mEvent_id);
			mEventsDetailActivityManagement.SetIsread();
		}
			
		InitToolbar();
		ReflashUI();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sendGetEventDetailRequest(mEvent_id);
	}
	
//	@Override
//	protected void onRestart() {
//		// TODO Auto-generated method stub
//		super.onRestart();
//		
//		GetDataInDB(mEvent_id);
//		ReflashUI();
//	}
	
	@Override
	protected void onDestroy() {
//		if(mEventCursor != null){
//			mEventCursor.close();
//			mEventCursor = null;
//		}
		super.onDestroy();
	};
	
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
				
				Intent i = new Intent(EventsDetailActivity.this, ImageAttcGalleryActivity.class);
				i.putExtra(ImageAttcGalleryActivity.KEY_IMGSURL, urls); 
				i.putExtra(ImageAttcGalleryActivity.KEY_IMGSNAME, names); 
				EventsDetailActivity.this.startActivity(i);
			}
		});
		
		//回复
		mButtonReply = new Button(this);
		mButtonReply.setLayoutParams(p);
		mButtonReply.setBackgroundResource(R.drawable.detail_feedback_btn_selector);
		mButtonReply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				//保存读到的回复最后更新时间
//				for(int i = 0; i < mEventDetailsItem.listReplyer.size(); i++){
//					Replyer rp = mEventDetailsItem.listReplyer.get(i);
//					mEventsDB.updataAsworgLastawstimeread(rp.ReplyLastTime,mEvent_id,mUsername,rp.ReplyerId);
//					mEventsDB.updataEventLastawstimeread(DateFormatter.getDateFromSecondsString(rp.ReplyLastTime),mEventDetailsItem);
//				}
				//按钮复原
				mButtonReply.setBackgroundResource(R.drawable.detail_feedback_btn_selector);
				
				mEventsDetailActivityManagement.onReplyClicked(mEventDetailsItem);
//				
//				Intent i = new Intent(EventsDetailActivity.this, EventsReplyActivity.class);
//				i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, mEvent_id);
//				i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,mEventDetailsItem.pubid);
//				i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,mEventDetailsItem.pubname);
//				if(mEventDetailsItem.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
//					i.putExtra(EventsReplyActivity.REPLY_ABLE,false);
//				}
//				i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,mEventDetailsItem.pubname);
//				
//				Log.i("zjj", "点击查看回复内容:" + mEvent_id + "," + mEventDetailsItem.pubid);
//				EventsDetailActivity.this.startActivity(i);
			}
		});
		
		//分享
		mButtonShare = new Button(this);
		mButtonShare.setLayoutParams(p);
		mButtonShare.setBackgroundResource(R.drawable.detail_share_btn_selector);
//		mButtonShare.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mEventsDetailActivityManagement.Share();
//			}
//		});
		
		//收藏
		mButtonMark = new Button(this);
		mButtonMark.setLayoutParams(p);
		mButtonMark.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mEventDetailsItem.bookmark){
					mButtonMark.setBackgroundResource(R.drawable.detail_unbookmark_btn);
					mEventsDetailActivityManagement.SetBookmark(false);
				}
				else{
					mButtonMark.setBackgroundResource(R.drawable.detail_bookmark_btn);
					mEventsDetailActivityManagement.SetBookmark(true);
				}
					
			}
		});
		
		//回评
		mButtonReview = new Button(this);
		mButtonReview.setLayoutParams(p);
		mButtonReview.setBackgroundResource(R.drawable.detail_review_btn_selector);
//		mButtonReview.setVisibility(View.GONE);
		mButtonReview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mEventDetailsItem.type == RequestCategoryID.EVENTS_COURSEWARE_ID){
					//测试代码: 演示 计划回复
					Intent i = new Intent(EventsDetailActivity.this, ReviewTestActivity.class);
					startActivity(i);
				}else{
					mEventsDetailActivityManagement.onReviewClicked(mEventDetailsItem);
				}
			}
		});

		ToolbarAddLeftButton(mButtonAttachment);
		ToolbarAddRightButton(mButtonReview);
		ToolbarAddRightButton(mButtonReply);
		ToolbarAddRightButton(mButtonShare);
		ToolbarAddRightButton(mButtonMark);
        

	}
	
	/**
	 * 初始化回复人列表
	 */
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
//					Intent i = new Intent(EventsDetailActivity.this, EventsReplyActivity.class);
//					i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, mEvent_id);
//					i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,rp.ReplyerId);
//					Log.i("zjj", "点击查看回复内容:" + mEvent_id + "," + rp.ReplyerId);
//					EventsDetailActivity.this.startActivity(i);
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
	private void sendGetEventDetailRequest (int id){
		showProgressBar();
		mEventsDetailActivityManagement.getData(id, mHandler);
	}
	
	/**
	 * 从库读取
	 */
	private void GetDataInDB(int id){
//		mEventCursor = mEventsDB.getOneEventCursor(id,mUsername);
//		mEventCursor.requery();
//		mEventCursor.moveToFirst();
//		mEventDetailsItem = mEventsDB.retrieveEventDetailItem(mEventCursor);
//		mEventCursor.close();
		mEventDetailsItem = mEventsDetailActivityManagement.GetDataInDB(mEvent_id);
	}
	
	/**
	 * 刷新界面
	 */
	private void ReflashUI(){
		mTxtViewTitle.setText(getResources().getString(R.string.diary_title) + mEventDetailsItem.title);
		mTxtViewSender.setText(getResources().getString(R.string.sender) + mEventDetailsItem.pubname);
		mTxtViewReceiver.setText(getResources().getString(R.string.addressee) + mEventDetailsItem.owner);
		mTxtViewSendtime.setText(getResources().getString(R.string.senttime) + DateFormatter.getStringYYYYMMDDHHmm(mEventDetailsItem.post));
//		mTxtViewStarttime.setText(getResources().getString(R.string.start_time) + DateFormatter.getStringYYYYMMDDHHmm(mEventDetailsItem.start));
//		mTxtViewEndtime.setText(getResources().getString(R.string.end_time) + DateFormatter.getStringYYYYMMDDHHmm(mEventDetailsItem.end));
//		mTxtViewLocation.setText(getResources().getString(R.string.location) + mEventDetailsItem.location);
		//如果WebView重复两次loadData,第2次的内容不会显示
		//所以这样保证有内容时,才显示到WebView中
		if(mEventDetailsItem.body.trim().length() > 0){
			String templateHtml = mEventsDetailActivityManagement.readTextFromResource(R.raw.events_detail);
			// Set Body
			templateHtml = templateHtml.replace("__BODY__", mEventDetailsItem.body);
			mWebViewBody.loadDataWithBaseURL("file:///android_res/raw/", templateHtml, "text/html", "UTF-8", null);
		}
		
		if(mEventDetailsItem.imgs.size()>0){
			mButtonAttachment.setVisibility(View.VISIBLE);
			mButtonAttachment.setNum(mEventDetailsItem.imgs.size() + "");
		}
		else
			mButtonAttachment.setVisibility(View.GONE);
		
		//回评 ( "||" 为测试代码,班级计划显示回评按钮)
		if(mEventDetailsItem.isneedreview || mEventDetailsItem.type == RequestCategoryID.EVENTS_COURSEWARE_ID)
			mButtonReview.setVisibility(View.VISIBLE);
		else
			mButtonReview.setVisibility(View.GONE);
		
		//回复按钮
//		mEventsDetailActivityManagement.IsNewReply(mButtonReply);
		if(mEventsDetailActivityManagement.IsNewReply() == 1){
			mButtonReply.setBackgroundResource(R.drawable.detail_new_feedback_btn_selector);
			mButtonReply.setVisibility(View.VISIBLE);
		}
		else if(mEventsDetailActivityManagement.IsNewReply() == 0){
			mButtonReply.setBackgroundResource(R.drawable.detail_feedback_btn_selector);
			mButtonReply.setVisibility(View.VISIBLE);
		}
		else
			mButtonReply.setVisibility(View.GONE);
		
		if(mEventDetailsItem.bookmark)
			mButtonMark.setBackgroundResource(R.drawable.detail_bookmark_btn);
		else
			mButtonMark.setBackgroundResource(R.drawable.detail_unbookmark_btn);
		
		
		
//		initReplyerList();
//		hideProgressBar();
		setTitleText(ModuleNameDefine.getEventsModuleNamebyId(mEventDetailsItem.type)+getString(R.string.detail));
		
		//share
		tellFriendDrawaer = (SlidingDrawer)findViewById(R.id.tellFriend_drawer);
			mEventsDetailActivityManagement.Share(mButtonShare,tellFriendDrawaer,getToolbar());
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
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(EventsDetailActivity.this);
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

}
