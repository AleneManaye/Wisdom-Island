package com.drcom.drpalm.Activitys.events.sent;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.Imagetool.BitmapCache;
import com.drcom.drpalm.Tool.drHttpClient.DrRequestImgsTask;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm4tianzhujiao.R;


/*      
 * Date         : 2012-4-24
 * Author       : zhaojunjie
 * Copyright    : City Hotspot Co., Ltd.
 * 当前活动
 */
public class EventsSentListCursorAdapter extends CursorAdapter {
	
	//控件
	private LinearLayout mLinearLayoutIV;
	private ImageView imgview_other;	//代表别的老师所发
	private ImageView thumb_imgview;	//缩略图
	private ImageView event_rowbookmark ;
	private ImageView event_rowattachment ;
	private ImageView event_isPrize ;
	private ImageView event_isfeedback;
	private ImageView event_needreview;
	private TextView evnetsTitleTV = null;
	private TextView evnetsSummaryTV = null;
	private TextView evnetsDateTV = null;
	private TextView evnetsReadCount = null;
	private ImageView mImgview_unreadmark;
	
	private String mUsername = "" ;
	private Context mContext ;
	private EventsDB mEventsDB ;
	private TextPaint tp;
	private Date today = new Date();
	
	public EventsSentListCursorAdapter(Context context,Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		this.mContext = context ;
		
	}
	
	public void setUsername(String name){
		mUsername = name;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view =  vi.inflate(R.layout.events_listitem_view, null);
		bindView(view, context, cursor);
		return view ;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		EventDetailsItem item = mEventsDB.getInstance(context,GlobalVariables.gSchoolKey).retrievePublishEventDetailItem(cursor);

		//图标 LAYOUT
		mLinearLayoutIV = (LinearLayout)view.findViewById(R.id.IV_layout);
		
		//代表别的老师所发
		imgview_other = (ImageView) view.findViewById(R.id.Imgview_eventssend_other);
		if(item.pubid.equals(mUsername) ){
			imgview_other.setVisibility(View.GONE);
		}else{
			imgview_other.setVisibility(View.VISIBLE);
		}
		
		//收藏
		event_rowbookmark = (ImageView) view.findViewById(R.id.event_rowbookmark);
		if(item.bookmark){
			event_rowbookmark.setVisibility(View.VISIBLE);
		}else{
			event_rowbookmark.setVisibility(View.GONE);
		}

		//附件
		event_rowattachment = (ImageView) view.findViewById(R.id.event_rowattachment);
		if (item.hasatt) {
			event_rowattachment.setVisibility(View.VISIBLE);
		} else{
			event_rowattachment.setVisibility(View.GONE);
		}
		
		//加急
		event_isPrize = (ImageView) view.findViewById(R.id.event_isPrize);
		if (item.ifeshow) {
			event_isPrize.setVisibility(View.VISIBLE);
		} else {
			event_isPrize.setVisibility(View.GONE);
		}
		
		//回评
		event_needreview = (ImageView) view.findViewById(R.id.event_needreview);
		if (item.isneedreview){
			event_needreview.setVisibility(View.VISIBLE);
		}else{
			event_needreview.setVisibility(View.GONE);
		}
		
		//回复
		event_isfeedback = (ImageView) view.findViewById(R.id.event_feedback);
		//大于 上次阅读过的回复的时间
//		Log.i("zjj", "已发通告列表: " + item.title + "服务器最后回复时间:" + item.lastawstime.getTime() + ",已阅读过的回复时间:" + item.lastawstimeread.getTime());
//		if (item.lastawstime.getTime() > item.lastawstimeread.getTime()) {
//			event_isfeedback.setImageResource(R.drawable.new_feedback_small);
//			event_isfeedback.setVisibility(View.VISIBLE);
//		} else {
//			event_isfeedback.setImageResource(R.drawable.feedback_small);
//			event_isfeedback.setVisibility(View.VISIBLE);
//			
//			if(item.lastawstime.getTime() == 0)
//				event_isfeedback.setVisibility(View.GONE);
//		}
		
		Log.i("zjj", "已发通告列表: " + item.title + "回复总数:" + item.awscount);
		if (item.awscount > item.awscountclient) {
			event_isfeedback.setImageResource(R.drawable.new_feedback_small);
			event_isfeedback.setVisibility(View.VISIBLE);
		} else {
			event_isfeedback.setImageResource(R.drawable.feedback_small);
			event_isfeedback.setVisibility(View.VISIBLE);
			
			if(item.awscount == 0)
				event_isfeedback.setVisibility(View.GONE);
		}

		//是否有中划线
		evnetsTitleTV = (TextView) view.findViewById(R.id.eventsTitle_Txtview);
		evnetsTitleTV.setText(item.title);
		tp = evnetsTitleTV.getPaint();
		if(item.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
			tp.setStrikeThruText(true);
		}else{
			tp.setStrikeThruText(false);
		}
		
		//描述
		evnetsSummaryTV = (TextView) view.findViewById(R.id.eventsSummary_Txtview);
		evnetsSummaryTV.setText(mContext.getString(R.string.sender) + item.pubname);
		
		//发布时间&回复时间
		evnetsDateTV = (TextView) view.findViewById(R.id.eventsDate_Txtview);
//		if(item.lastawstime != null){
//			if(item.lastawstime.getTime() == 0){
//				evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.post).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.post):DateFormatter.getStringYYYYMMDD(item.post));
//			}else{
//				evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.lastawstime).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.lastawstime):DateFormatter.getStringYYYYMMDD(item.lastawstime));
////				evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.lastawstime));
//			}
//		}else{
//			evnetsDateTV.setText(mContext.getString(R.string.post) + (DateFormatter.getStringYYYYMMDD(item.post).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.post):DateFormatter.getStringYYYYMMDD(item.post)));
//			evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.post));
//		}
		String time = "";
		if(item.lastawstime != null){
			if(item.lastawstime.getTime() != 0){
				time = mContext.getString(R.string.feedback) + (DateFormatter.getStringYYYYMMDD(item.lastawstime).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.lastawstime):DateFormatter.getStringYYYYMMDD(item.lastawstime)) + "\n";
			}
		}
		time = time + mContext.getString(R.string.post) + (DateFormatter.getStringYYYYMMDD(item.post).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.post):DateFormatter.getStringYYYYMMDD(item.post));
		evnetsDateTV.setText(time);
		
		// 缩略图
		thumb_imgview = (ImageView) view.findViewById(R.id.newsRowIV);
		thumb_imgview.setVisibility(View.GONE);
		mLinearLayoutIV.setVisibility(View.GONE);
		//缓存有图片则从缓存读取
//		System.out.println(":::::取缩略图:" + item.title + "," + item.thumbURL);
//		if(item.thumbBitmap != null){
//			thumb_imgview.setImageBitmap(item.thumbBitmap);
//			System.out.println(":::::缓存有图片则从缓存读取..缓存有图片则从缓存读取");
//		}else if(item.thumbURL == null){
//			thumb_imgview.setImageDrawable(mDrawableDefaultImg);
//			System.out.println(":::::缓存URL为空");
//		}else{
//			thumb_imgview.setImageDrawable(mDrawableDefaultImg);
//			GetData(item, thumb_imgview);
//		}
		
//		if(item.thumburl != ""){
//			GetData(item.thumburl, thumb_imgview);
//		}
		
		//已读数
		evnetsReadCount = (TextView) view.findViewById(R.id.eventsTitle_ReadCount);
		evnetsReadCount.setVisibility(View.VISIBLE);
		evnetsReadCount.setText(item.readcount + "/" + item.recvtotal);
		
		//未读标识
		mImgview_unreadmark = (ImageView)view.findViewById(R.id.Imgview_unreadmark);
		//已读
		if(item.isread){
			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
			evnetsSummaryTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
			evnetsDateTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
			mImgview_unreadmark.setVisibility(View.GONE);
		}else{
			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.listtitletxt));
			evnetsSummaryTV.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
			evnetsDateTV.setTextColor(mContext.getResources().getColor(R.color.orange));
			mImgview_unreadmark.setVisibility(View.VISIBLE);
		}
	}
	
	
	/**
	 * 下载图片
	 */
	private void GetData(String url,ImageView thumbimgview){
		//本地读数据库取是否有图片
//		mActivitysDB = ActivitysDB.getInstance(mContext);
//		EventDraftItem.Attachment attm = mActivitysDB.retrieveAttachmentBytes(item.thumbURL);
//		if(attm.data != null){
//			item.thumbBitmap = MyMothod.Byte2Bitmap(attm.data);
//			thumbimgview.setImageBitmap(item.thumbBitmap);
//			System.out.println(":::::缓存URL,从数据库读取:" + item.thumbURL);
//			return;
//		}
		
		//没图片则访问网络获取
        DrRequestImgsTask task = new DrRequestImgsTask(
        		this.mContext,
        		url,
        		false,
        		callBack(this.mContext,url,thumbimgview));
		task.execute();
//		System.out.println(":::::缩略图,从网络读取:" + item.title + "," + item.thumbURL);
	}
	
	/**
	 * 下载图片回调
	 * @param context
	 * @param url
	 * @return
	 */
	private UICallBack callBack(Context context,final String url,final ImageView thumbimgview) {
		return new UICallBack() {
			byte[] attachmentBytes;
			@Override
			public void callBack(Object b) {
				if (b != null)
					attachmentBytes = (byte[])b;
					b = null;
					
					Bitmap bmp = BitmapCache.getInstance().getBitmap(url,attachmentBytes,true);
					thumbimgview.setBackgroundColor(Color.TRANSPARENT);
					thumbimgview.setImageBitmap(bmp);
					bmp = null;
					
					//保存到数据库
//					synchronized(mActivitysDB){
//						mActivitysDB.startTransaction() ;
//						mActivitysDB.saveAttachment(item.thumbURL,attachmentBytes,"thumbpic") ;
//						mActivitysDB.endTransaction() ;
//					}
					attachmentBytes = null;
					bmp = null;
					return;
			}
		};
	}

}
