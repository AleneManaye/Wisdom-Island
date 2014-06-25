package com.drcom.drpalm.Activitys.sysinfo;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.ImagesDB;
import com.drcom.drpalm.DB.SystemInfoDB;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.Imagetool.BitmapCache;
import com.drcom.drpalm.Tool.drHttpClient.DrRequestImgsTask;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.objs.SystemInfoItem;
import com.wisdom.island.R;


/*      
 * Date         : 2012-4-24
 * Author       : zhaojunjie
 * Copyright    : City Hotspot Co., Ltd.
 * 当前活动
 */
public class SysinfoListCursorAdapter extends CursorAdapter {
	
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
	
	private String accountName = "" ;
	private Context mContext ;
	private SystemInfoDB mSystemInfoDB ;
	private ImagesDB mImagesDB ;
	private Date today = new Date();

	public SysinfoListCursorAdapter(Context context,Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		this.mContext = context ;
		mImagesDB = ImagesDB.getInstance(this.mContext,GlobalVariables.gSchoolKey);
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
		SystemInfoItem item = mSystemInfoDB.getInstance(context,GlobalVariables.gSchoolKey).retrieveSystemInfoItem(cursor);

		//图标 LAYOUT
		mLinearLayoutIV = (LinearLayout)view.findViewById(R.id.IV_layout);
		
		//收藏
		event_rowbookmark = (ImageView) view.findViewById(R.id.event_rowbookmark);
//		if(item.bookmark){
//			event_rowbookmark.setVisibility(View.VISIBLE);
//		}else{
			event_rowbookmark.setVisibility(View.GONE);
//		}

		//附件
		event_rowattachment = (ImageView) view.findViewById(R.id.event_rowattachment);
//		if (item.hasatt) {
//			event_rowattachment.setVisibility(View.VISIBLE);
//		} else{
			event_rowattachment.setVisibility(View.GONE);
//		}
		
		//加急
		event_isPrize = (ImageView) view.findViewById(R.id.event_isPrize);
//		if (item.ifeshow) {
//			event_isPrize.setVisibility(View.VISIBLE);
//		} else {
			event_isPrize.setVisibility(View.GONE);
//		}
		
		//回复
		event_isfeedback = (ImageView) view.findViewById(R.id.event_feedback);
//		if (item.lastawstime.getTime() != item.lastawstimeread.getTime()) {
//			event_isfeedback.setVisibility(View.VISIBLE);
//		} else {
			event_isfeedback.setVisibility(View.GONE);
//		}
			
		//回评
		event_needreview = (ImageView) view.findViewById(R.id.event_needreview);
		event_needreview.setVisibility(View.GONE);
		
		evnetsTitleTV = (TextView) view.findViewById(R.id.eventsTitle_Txtview);
		evnetsTitleTV.setText(item.msg_title);
		
		evnetsSummaryTV = (TextView) view.findViewById(R.id.eventsSummary_Txtview);
		evnetsSummaryTV.setText("");//(item.msg_user);
		
		//发布时间
		evnetsDateTV = (TextView) view.findViewById(R.id.eventsDate_Txtview);
//		evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.msg_lastupdate));
		evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.msg_lastupdate).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.msg_lastupdate):DateFormatter.getStringYYYYMMDD(item.msg_lastupdate));
		
//		if(item.isread){
//			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
//		}else{
//			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.black));
//		}

		// 缩略图
		thumb_imgview = (ImageView) view.findViewById(R.id.newsRowIV);
		thumb_imgview.setVisibility(View.GONE);
		mLinearLayoutIV.setVisibility(View.GONE);
//		if(item.thumbUrl.length() > 0){
//			thumb_imgview.setVisibility(View.VISIBLE);
//			GetData(item.thumbUrl,thumb_imgview);
//		}else{
//			thumb_imgview.setVisibility(View.GONE);
//		}
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
		Bitmap bmp = BitmapCache.getInstance().getBitmap(url,mImagesDB,ImagesDB.CATEGORY_MALL_ACTIVITY);
		if(bmp != null){
			thumbimgview.setImageBitmap(bmp);
			return;
		}
		
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
