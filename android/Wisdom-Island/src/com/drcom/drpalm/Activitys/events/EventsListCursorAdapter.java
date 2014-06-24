package com.drcom.drpalm.Activitys.events;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
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
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm4tianzhujiao.R;


/*      
 * Date         : 2012-4-24
 * Author       : zhaojunjie
 * Copyright    : City Hotspot Co., Ltd.
 * 当前活动
 */
public class EventsListCursorAdapter extends CursorAdapter {
	
	//控件
	private LinearLayout mLinearLayoutIV;
	private ImageView thumb_imgview;	//缩略图
	private ImageView event_rowbookmark ;
	private ImageView event_rowattachment ;
	private ImageView event_isPrize ;
	private ImageView event_isfeedback;
	private ImageView event_needreview;
	private TextView evnetsTitleTV = null;
	private TextView evnetsSummaryTV = null;
	private TextView evnetsDateTV = null;
	private ImageView mImgview_unreadmark;
	//变量
	private String accountName = "" ;
	private Context mContext ;
	private EventsDB mEventsDB ;
//	private ImagesDB mImagesDB ;
//	private DrRequestImgsTask task;
//	private List<ImageView> downloadlist = new ArrayList<ImageView>();	//下载队列
//	private boolean isCanDownload = true;	//能否下载 
//	private Handler mDownloadHandler;		//下载句柄
	private ImageLoader mImageLoader;
	private TextPaint tp;
	private Date today = new Date();

	public EventsListCursorAdapter(Context context,Cursor c,ImageLoader imageLoader) {
		super(context, c);
		// TODO Auto-generated constructor stub
		this.mContext = context ;
//		mImagesDB = ImagesDB.getInstance(this.mContext,GlobalVariables.gSchoolKey);
//		InitDownloadHandler();
//		InitDownloadThread();
		this.mImageLoader = imageLoader;
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
		EventDetailsItem item = mEventsDB.getInstance(context,GlobalVariables.gSchoolKey).retrieveEventDetailItem(cursor);

		//图标 LAYOUT
		mLinearLayoutIV = (LinearLayout)view.findViewById(R.id.IV_layout);
		
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
		
		//回复气泡
		event_isfeedback = (ImageView) view.findViewById(R.id.event_feedback);
		Log.i("zjj", "Adapter:" + item.title + "," + item.lastawstime.getTime() + "," + item.lastawstimeread.getTime() + "," + item.status);
		//大于 上次阅读过的回复的时间
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
		if(item.lastawstime.getTime() == 0){
			event_isfeedback.setVisibility(View.GONE);
		}else{
			//自己发的通告不显示
			if (item.pubid.equals(item.user)){	
				event_isfeedback.setVisibility(View.GONE);
			}
			//有新回复并不是自己发的
			else if (item.lastawstime.getTime() > item.lastawstimeread.getTime() && !item.lastawsuserid.equals(item.user)){	
				event_isfeedback.setImageResource(R.drawable.new_feedback_small);
				event_isfeedback.setVisibility(View.VISIBLE);
			}else{
				event_isfeedback.setImageResource(R.drawable.feedback_small);
				event_isfeedback.setVisibility(View.VISIBLE);
			}
		}
			
		
		
		//是否有中划线
		evnetsTitleTV = (TextView) view.findViewById(R.id.eventsTitle_Txtview);
		evnetsTitleTV.setText(item.title);
		tp = evnetsTitleTV.getPaint();
		if(item.status != null){
			if(item.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
				tp.setStrikeThruText(true);
			}else{
				tp.setStrikeThruText(false);
			}
		}
		
		//描述
		evnetsSummaryTV = (TextView) view.findViewById(R.id.eventsSummary_Txtview);
		evnetsSummaryTV.setText(item.summary);
		
		//发布时间&回复时间
		evnetsDateTV = (TextView) view.findViewById(R.id.eventsDate_Txtview);
//		if(item.lastawstime != null){
//			if(item.lastawstime.getTime() == 0){
//				evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.post).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.post):DateFormatter.getStringYYYYMMDD(item.post));
//			}else{
//				evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.lastawstime).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.lastawstime):DateFormatter.getStringYYYYMMDD(item.lastawstime));
//			}
//		}else{
//			evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.post).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.post):DateFormatter.getStringYYYYMMDD(item.post));
//		}
		String time = "";
		if(item.lastawstime != null){
			if(item.lastawstime.getTime() != 0){
				time = mContext.getString(R.string.feedback) + (DateFormatter.getStringYYYYMMDD(item.lastawstime).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.lastawstime):DateFormatter.getStringYYYYMMDD(item.lastawstime)) + "\n";
			}
		}
		time = time + mContext.getString(R.string.post) + (DateFormatter.getStringYYYYMMDD(item.post).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.post):DateFormatter.getStringYYYYMMDD(item.post));
		evnetsDateTV.setText(time);
		
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

		// 缩略图
//		item.thumbUrl = "http://fanyi.baidu.com/asset/fanyi-logo.png";
		thumb_imgview = (ImageView) view.findViewById(R.id.newsRowIV);
		if(isListiconVisiable(item.type)){
			thumb_imgview.setVisibility(View.VISIBLE);
			mLinearLayoutIV.setVisibility(View.VISIBLE);
//			GetData(item.thumbUrl,thumb_imgview);
//			if(item.thumbUrl.equals("") || item.thumbUrl.equals("null")){
//				thumb_imgview.setImageResource(R.drawable.no_pic);
//			}else{
				mImageLoader.DisplayImage(item.thumbUrl, thumb_imgview, false);
//			}
		}else{
			thumb_imgview.setVisibility(View.GONE);
			mLinearLayoutIV.setVisibility(View.GONE);
		}
		
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
	}
	
	/**
	 * 设置列表ICON是否可视
	 */
	private boolean isListiconVisiable(int type){
		switch (type) {
		case RequestCategoryID.EVENTS_NEWS_ID:
		case RequestCategoryID.EVENTS_COMMENT_ID:
		case RequestCategoryID.EVENTS_COURSEWARE_ID:
			return false;

		default:
			return true;
		}
	}
	
//	/**
//	 * 下载图片
//	 */
//	private void GetData(String url,ImageView thumbimgview){
//		if(url.equals("")){
//			System.out.println(":::::缓存URL,没URL");
//			return;
//		}
//		
//		Bitmap bmp = BitmapCache.getInstance().getBitmap(url,mImagesDB,ImagesDB.THUMB_RUL);
//		if(bmp != null){
//			//因为在XML中,把ImageView的Background设置为空白图标,所以要先把空白图标去掉
//			thumbimgview.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
//			thumbimgview.setImageBitmap(bmp);
//			System.out.println(":::::缓存URL,从缓存读取:" + url);
//			return;
//		}
//		
//		//没图片则访问网络获取
//		System.out.println(":::::缓存URL,从网络读取:" + url);
//		thumbimgview.setTag(url);
//		downloadlist.add(thumbimgview);
//		
////        DrRequestImgsTask task = new DrRequestImgsTask(
////        		this.mContext,
////        		url,
////        		false,
////        		callBack(this.mContext,url,thumbimgview));
////		task.execute();
////		System.out.println(":::::缩略图,从网络读取:" + item.title + "," + item.thumbURL);
//	}
//	
//	/**
//	 * 初始化下载线程
//	 * 采用队列单线程下载，防止内存不足
//	 */
//	private void InitDownloadThread(){
//		new Thread(){
//			@Override
//			public void run(){
//				while(true){
//					if(downloadlist.size()>0 && isCanDownload){
//						isCanDownload = false;
//						Message message = Message.obtain();
//						mDownloadHandler.sendMessage(message);
//					}
//				}
//			}
//		}.start();
//	}
//	
//	/**
//	 * 下载句柄
//	 */
//	private void InitDownloadHandler(){
//		mDownloadHandler = new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//				String url = (String)downloadlist.get(0).getTag();
//				
//		        task = new DrRequestImgsTask(
//        		mContext,
//        		url,
//        		false,
//        		callBack(mContext,url,downloadlist.get(0)));
//		        task.execute();
//		        
//		        downloadlist.remove(0);
//		        
//		        System.out.println("新建下载任务:" + url);
//			}
//		};
//	}
//	
//	/**
//	 * 下载图片回调
//	 * @param context
//	 * @param url
//	 * @return
//	 */
//	private UICallBack callBack(Context context,final String url,final ImageView thumbimgview) {
//		return new UICallBack() {
//			byte[] attachmentBytes;
//			@Override
//			public void callBack(Object b) {
//				if (b != null){
//					
//					attachmentBytes = (byte[])b;
//					b = null;
//					
//					Bitmap bmp = BitmapCache.getInstance().getBitmap(url,attachmentBytes,false);
//					thumbimgview.setBackgroundColor(Color.TRANSPARENT);
//					thumbimgview.setImageBitmap(bmp);
//					thumbimgview.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
//					
//					//保存到数据库
//					synchronized (mImagesDB) {
//						mImagesDB.startTransaction();
//						mImagesDB.saveThumbData(url, attachmentBytes, ImagesDB.THUMB_RUL);
//						mImagesDB.endTransaction();
//					}
//					attachmentBytes = null;
//					bmp = null;
//					isCanDownload = true;
//					return;
//				}
//			}
//		};
//	}

}
