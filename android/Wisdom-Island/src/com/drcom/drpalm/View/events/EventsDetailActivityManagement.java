package com.drcom.drpalm.View.events;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.Activitys.events.reply.EventsReplyActivity;
import com.drcom.drpalm.Activitys.events.review.ReviewActivity;
import com.drcom.drpalm.Activitys.shares.ShareToFriendViewHelper;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.FavDB;
import com.drcom.drpalm.Tool.CommonActions;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.EventDetailsItem.Replyer;
import com.drcom.drpalm.objs.FavItem;
import com.drcom.drpalm4tianzhujiao.R;

public class EventsDetailActivityManagement {
	public static String KEY_EVENT_ID = "KEY_EVENT_ID";
	public static String KEY_INIT = "KEY_INIT";
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	private int mins = 5 * 60 * 1000;	//日历提醒延长时间
	
	private Context mContext;
	//变量
	private boolean isInitData = true;	//是否初始化数据(从最新消息进入,此值为FALSE)
	private EventDetailsItem mEventDetailsItem = new EventDetailsItem();
	private int mEvent_id = -1;
	private String mUsername = "";	
	private SettingManager setInstance ;	
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	private RelativeLayout.LayoutParams linearParams;
	private EventsDetailActivityManagement mEventsDetailActivityManagement;
	private LoginManager instance ;
	private String strbody;
	
	public EventsDetailActivityManagement(Context c,int id){
		mContext = c;
		mEvent_id = id;
		setInstance = SettingManager.getSettingManager(c);	
		mEventsDB = EventsDB.getInstance(c,GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		instance = LoginManager.getInstance(GlobalVariables.gAppContext);
	}
	
	/**
	 * 从库读取
	 */
	public EventDetailsItem GetDataInDB(int id){
		mEventCursor = mEventsDB.getOneEventCursor(id,mUsername);
		mEventCursor.requery();
		mEventCursor.moveToFirst();
		mEventDetailsItem = mEventsDB.retrieveEventDetailItem(mEventCursor);
		mEventCursor.close();
		mEventCursor = null;
		
		return mEventDetailsItem;
	}
	
	/**
	 * 
	 * @param item
	 */
	public void SaveCalendar(EventDetailsItem item){
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", item.start.getTime());
		intent.putExtra("endTime", item.start.getTime() + mins);
		intent.putExtra("title", item.title);
		intent.putExtra("description", item.body);
		intent.putExtra("eventLocation", item.location);
		mContext.startActivity(Intent.createChooser(intent, "Calendar"));
	}
	
	/**
	 * 点击回复
	 * @param item
	 */
	public void onReplyClicked(EventDetailsItem item){
		//保存读到的回复最后更新时间
		for(int i = 0; i < item.listReplyer.size(); i++){
			Replyer rp = item.listReplyer.get(i);
			mEventsDB.updataAsworgLastawstimeread(rp.ReplyLastTime,mEvent_id,mUsername,rp.ReplyerId);
			mEventsDB.updataEventLastawstimeread(DateFormatter.getDateFromSecondsString(rp.ReplyLastTime),mEventDetailsItem);
		}
//		//按钮复原
//		mButtonReply.setBackgroundResource(R.drawable.detail_feedback_btn_selector);
		
		Intent i = new Intent(mContext, EventsReplyActivity.class);
		i.putExtra(EventsReplyActivity.REPLY_EVENT_ID, mEvent_id);
		i.putExtra(EventsReplyActivity.REPLY_ASWPUBID,mEventDetailsItem.listReplyer.get(0).ReplyerId);//讨论组ID
		i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,mEventDetailsItem.pubname);
		if(mEventDetailsItem.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
			i.putExtra(EventsReplyActivity.REPLY_ABLE,false);
		}
		i.putExtra(EventsReplyActivity.REPLY_HEADSHOW,mEventDetailsItem.pubname);
		
		Log.i("zjj", "点击查看回复内容:" + mEvent_id + "," + mEventDetailsItem.pubid);
		mContext.startActivity(i);
	}
	
	/**
	 * 点击回评
	 * @param item
	 */
	public void onReviewClicked(EventDetailsItem item){
		Intent i = new Intent(mContext, ReviewActivity.class);
		i.putExtra(ReviewActivity.REPLY_EVENT_ID, mEvent_id);
		mContext.startActivity(i);
	}
	
	/**
	 * 请求网络
	 * @param id
	 * @param h
	 */
	public void getData(final int id,final Handler h){
		//是否取得此记录的所有信息
		int getall = 0;
		if(!isInitData)
			getall = 1;
		
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onSuccess() {
				Message message = Message.obtain();
				message.arg1 = UPDATEFINISH;
				h.sendMessage(message) ;
			}

			@Override
			public void onCallbackError(String err) {
				// TODO Auto-generated method stub
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = err;
				h.sendMessage(message);
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "通告详细:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "通告详细:自动重登录成功");
				if(isRequestRelogin){
					getData(mEvent_id,h);	//自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetEventDetail", new Object[]{String.valueOf(id),getall,callback},callback.getClass().getName());
	}
	
	public String readTextFromResource(int newsDetail) {
		InputStream raw = mContext.getResources().openRawResource(newsDetail);
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
	
	/**
	 * 设置为已读
	 */
	public void SetIsread(){
		mEventsDB.markAsRead(mEventDetailsItem);
	}
	
	/**
	 * 设置为收藏
	 */
	public void SetBookmark(boolean b){
		mEventDetailsItem.bookmark = b;
		
//		EventDetailsItem edi = new EventDetailsItem();
//		edi.eventid = mEventDetailsItem.eventid;
//		if(b){
//			//新增收藏
//			edi.status = "N";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
//			
//		}else{
//			//取消收藏
//			edi.status = "C";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
//		}
//		
//		ArrayList<EventDetailsItem> al =new ArrayList<EventDetailsItem>();
//		al.add(edi);
		
		FavItem edi = new FavItem();
		edi.mEventid = mEventDetailsItem.eventid + "";
		if(b){
			//新增收藏
			edi.mStatus = "N";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
			
		}else{
			//取消收藏
			edi.mStatus = "C";	//暂时使用这个字段存放是否收藏(不能入库影响原通告的活动状态)
		}
		
		ArrayList<FavItem> al =new ArrayList<FavItem>();
		al.add(edi);
		
		if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT
				|| instance.getOnlineStatus() != LoginManager.OnlineStatus.ONLINE_LOGINED) {
			SaveToFavDB(edi);
		}else{
			RequestManager.SubmitClassfav(al, new SubmitResultParser(), addfavcallback);
		}
	}
	
	/**
	 * 收藏状态因不能提交到服务器，先保存在本地库中，下次登录后提交
	 * @param item
	 */
	private void SaveToFavDB(FavItem item){
		item.mUsername = mUsername;
		
		FavDB fdb = FavDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		fdb.saveFavItem(item);
		
		//更新本地收藏标识
		if(mHandlerAddfav != null){
			mHandlerAddfav.sendEmptyMessage(0);
		}
	}
	
	/**
	 * 
	 * @param b
	 * @return -1:不显示,  0:没有新回复      1:有新回复
	 */
	public int IsNewReply(){
		//如果是自己发的通告,则不显示回复按钮
		if(mEventDetailsItem.pubid.toLowerCase().equals(mUsername.toLowerCase()) ){//&& newestlastupdatetime == 0){
//			b.setVisibility(View.GONE);
			return -1;
		}else{
			//每一个回复的最后回复时间 与 通告的已看的最后回复的时间对比,是否有新回复
			Date newestlastupdatetime = new Date(0);
			if(mEventDetailsItem.listReplyer.size() > 0){
				for(int i = 0; i < mEventDetailsItem.listReplyer.size(); i++){
					Replyer rp = mEventDetailsItem.listReplyer.get(i);
					if(DateFormatter.getDateFromSecondsString(rp.lastawstimeread ).getTime() < DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime()){
//						b.setBackgroundResource(R.drawable.detail_new_feedback_btn_selector);
						return 1;
					}
					
					if(DateFormatter.getDateFromSecondsString(rp.ReplyLastTime).getTime() > newestlastupdatetime.getTime()){
						newestlastupdatetime = DateFormatter.getDateFromSecondsString(rp.ReplyLastTime);
					}
				}
				Log.i("zjj", "从详细中更新通告 newestlastupdatetime:" + newestlastupdatetime);
				mEventsDB.updataEventLastawstime(newestlastupdatetime,mEventDetailsItem);
				
				return 0;
			}else{
//				b.setVisibility(View.VISIBLE);
				return 0;
			}
		}
			
	}
	
	/**
	 * 分享
	 */
	public void Share(View mButtonShare,SlidingDrawer tellFriendDrawaer,LinearLayout toolbar){
//		String strtest = "</br>hello </ddd> world </br> io&nbsp;kdf";
        // 过滤所有以<开头以>结尾的标签
//        String regEx= "<([^>]*)>";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(mEventDetailsItem.body);
//        
//        String strbody =  m.replaceAll("").trim();
//        // 过滤&nbsp;等标签(&开头;结尾)
//        regEx = "&([^>]*);";
//        p = Pattern.compile(regEx);
//        m = p.matcher(strbody);
//        strbody =  m.replaceAll("").trim();
		
		strbody =  mEventDetailsItem.cleanbody;
		
        if(mEventDetailsItem.imgs.size()>0){
        	strbody =  mEventDetailsItem.title + "\n" + strbody + "\n" + mEventDetailsItem.imgs.get(0).URL;
        }else{
        	strbody = mEventDetailsItem.title + "\n" + strbody;
        }
//        new ShareToFriendViewHelper(mButtonShare, tellFriendDrawaer,
//				GlobalVariables.shareList,
//				mContext,
//				mEventDetailsItem.title, strbody,toolbar);
mButtonShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, strbody);
				mContext.startActivity(Intent.createChooser(intent, "Share"));
			}
		});
	}
	
	/**
	 * 收藏请求结果
	 */
	private RequestOperationReloginCallback addfavcallback = new RequestOperationReloginCallback(){
		@Override
		public void onError(String str) {
			Log.i("zjj", "修改收藏状态  返回失败");
		}
		@Override
		public void onSuccess() {		
			Log.i("zjj", "修改收藏状态   返回成功");
			if(mHandlerAddfav != null){
				mHandlerAddfav.sendEmptyMessage(0);
			}
		}								
	};
	
	/**
	 * 收藏事件返回句柄
	 */
	private Handler mHandlerAddfav = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				mEventsDB.markAsBookmark(mEventDetailsItem,mEventDetailsItem.bookmark);
			}
	};
}
