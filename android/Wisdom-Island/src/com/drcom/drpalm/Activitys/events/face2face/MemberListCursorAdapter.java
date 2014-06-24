package com.drcom.drpalm.Activitys.events.face2face;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.CommunicationDB;
import com.drcom.drpalm.DB.ImagesDB;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.Imagetool.BitmapCache;
import com.drcom.drpalm.Tool.drHttpClient.DrRequestImgsTask;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.objs.ContactItem;
import com.drcom.drpalm4tianzhujiao.R;


/*      
 * Date         : 2012-4-24
 * Author       : zhaojunjie
 * Copyright    : City Hotspot Co., Ltd.
 * 当前活动
 */
public class MemberListCursorAdapter extends CursorAdapter {
	
	//控件
	private ImageView thumb_imgview;	//缩略图
//	private ImageView event_rowbookmark ;
//	private ImageView event_rowattachment ;
//	private ImageView event_isPrize ;
//	private ImageView event_isfeedback;
	private TextView evnetsTitleTV = null;
	private TextView evnetsSummaryTV = null;
	private TextView evnetsDateTV = null;
	private TextView Txt_Unread = null;
	
	private String accountName = "" ;
	private Context mContext ;
	private CommunicationDB mCommunicationDB ;
	private ImagesDB mImagesDB ;
	private ImageLoader mImageLoader;

	public MemberListCursorAdapter(Context context,Cursor c,ImageLoader imageLoader) {
		super(context, c);
		// TODO Auto-generated constructor stub
		this.mContext = context ;
		mImagesDB = ImagesDB.getInstance(this.mContext,GlobalVariables.gSchoolKey);
		this.mImageLoader = imageLoader;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view =  vi.inflate(R.layout.member_listitem_view, null);
		bindView(view, context, cursor);
		return view ;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ContactItem item = mCommunicationDB.getInstance(context,GlobalVariables.gSchoolKey).retrieveContactItem(cursor);
		
		//回复
//		event_isfeedback = (ImageView) view.findViewById(R.id.event_feedback);
//		if (item.lastawstime.getTime() != item.lastawstimeread.getTime()) {
//			event_isfeedback.setVisibility(View.VISIBLE);
//		} else {
//			event_isfeedback.setVisibility(View.GONE);
//		}
		
		//未读数
		Txt_Unread = (TextView) view.findViewById(R.id.Number_txtv);
		if(item.lastawstimeread.getTime() < item.lastupdate.getTime()){
//			event_isfeedback.setVisibility(View.VISIBLE);
			
			if(item.unread.equals("0")){
				Txt_Unread.setVisibility(View.GONE);
			}else{
				Txt_Unread.setVisibility(View.VISIBLE);
				Txt_Unread.setText(item.unread);
			}
		}else{
//			event_isfeedback.setVisibility(View.GONE);
			Txt_Unread.setVisibility(View.GONE);
		}
		
		evnetsTitleTV = (TextView) view.findViewById(R.id.eventsTitle_Txtview);
		evnetsTitleTV.setText(item.cnname);
		evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.blue));
		
		evnetsSummaryTV = (TextView) view.findViewById(R.id.eventsSummary_Txtview);
		evnetsSummaryTV.setVisibility(View.GONE);
//		evnetsSummaryTV.setText(item.);
		
		evnetsDateTV = (TextView) view.findViewById(R.id.eventsDate_Txtview);
		if(item.lastupdate.getTime() == 0){
			evnetsDateTV.setVisibility(View.GONE);
		}else
		{
			evnetsDateTV.setVisibility(View.VISIBLE);
			evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.lastupdate));
		}
		
		
		
		
//		if(item.isread){
//			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
//		}else{
//			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.black));
//		}

		// 缩略图
		thumb_imgview = (ImageView) view.findViewById(R.id.newsRowIV);
//		thumb_imgview.setVisibility(View.GONE);
		//因为在XML中,把ImageView的Background设置为空白图标,所以要先把空白图标去掉
//		thumb_imgview.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
//		thumb_imgview.setImageResource(R.drawable.child);
		Log.i("zjj", "家园桥人头PIC:" + item.headimgurl);
		mImageLoader.DisplayImage(item.headimgurl, thumb_imgview, false);
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
