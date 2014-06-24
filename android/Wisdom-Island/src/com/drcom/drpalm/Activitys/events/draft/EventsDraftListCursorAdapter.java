package com.drcom.drpalm.Activitys.events.draft;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm4tianzhujiao.R;

/*      
 * Date         : 2012-4-24
 * Author       : zhaojunjie
 * Copyright    : City Hotspot Co., Ltd.
 * 当前活动
 */
public class EventsDraftListCursorAdapter extends CursorAdapter {

	// 控件
	private LinearLayout mLinearLayoutIV;
	private ImageView thumb_imgview; // 缩略图
	private ImageView event_rowbookmark;
	private ImageView event_rowattachment;
	private ImageView event_isPrize;
	private ImageView event_isfeedback;
	private ImageView event_delete;
	private TextView evnetsTitleTV = null;
	private TextView evnetsSummaryTV = null;
	private TextView evnetsDateTV = null;

	private String accountName = "";
	private Context mContext;
	private EventsDB mEventsDB;
	private Cursor mEventCursor;

	public EventsDraftListCursorAdapter(Context context, Cursor c, String mUserName) {
		super(context, c);
		this.mContext = context;
		accountName = mUserName;
		mEventsDB = EventsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = vi.inflate(R.layout.events_draftitem_view, null);
		bindView(view, context, cursor);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		EventDraftItem item = mEventsDB.retrieveEventDraftItem(cursor, false);

		// 图标 LAYOUT
		mLinearLayoutIV = (LinearLayout) view.findViewById(R.id.IV_layout);

		// 收藏
		event_rowbookmark = (ImageView) view.findViewById(R.id.event_rowbookmark);
		event_rowbookmark.setVisibility(View.GONE);

		// 附件
		event_rowattachment = (ImageView) view.findViewById(R.id.event_rowattachment);
		if (item.isAttc) {
			event_rowattachment.setVisibility(View.VISIBLE);
		} else {
			event_rowattachment.setVisibility(View.GONE);
		}

		// 加急
		event_isPrize = (ImageView) view.findViewById(R.id.event_isPrize);
		if (item.bifeshow) {
			event_isPrize.setVisibility(View.VISIBLE);
		} else {
			event_isPrize.setVisibility(View.GONE);
		}

		// 回复
		event_isfeedback = (ImageView) view.findViewById(R.id.event_feedback);
		event_isfeedback.setVisibility(View.GONE);

		evnetsTitleTV = (TextView) view.findViewById(R.id.eventsTitle_Txtview);
		evnetsTitleTV.setText(item.title);

		evnetsSummaryTV = (TextView) view.findViewById(R.id.eventsSummary_Txtview);
		evnetsSummaryTV.setText(item.owner);

		evnetsDateTV = (TextView) view.findViewById(R.id.eventsDate_Txtview);
		evnetsDateTV.setText(DateFormatter.getStringYYYYMMDD(item.save));

		// 缩略图
		thumb_imgview = (ImageView) view.findViewById(R.id.newsRowIV);
		thumb_imgview.setVisibility(View.GONE);
		mLinearLayoutIV.setVisibility(View.GONE);

		// 删除按钮
		event_delete = (ImageView) view.findViewById(R.id.events_draft_delete);
		final int pk_id = item.pk_id;
		Log.i("xpf", "pk_id" + pk_id);
		event_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
				ab.setTitle(mContext.getResources().getString(R.string.deleteitem));
				ab.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i("xpf", "pk1_id" + pk_id);
						mEventsDB.deleteEventsDraftItem(pk_id, accountName);
						mEventCursor = mEventsDB.getAllEventsDraftCursor(accountName);// .getEventCursor(RequestCategoryID.EVENTS_NEWS_ID,0,0,mUsername,0);
						mEventCursor.requery();
						mEventCursor.moveToFirst();
						EventsDraftListCursorAdapter.this.changeCursor(mEventCursor);
					}
				});
				ab.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				ab.show();
			}
		});

		// 缓存有图片则从缓存读取
		// System.out.println(":::::取缩略图:" + item.title + "," + item.thumbURL);
		// if(item.thumbBitmap != null){
		// thumb_imgview.setImageBitmap(item.thumbBitmap);
		// System.out.println(":::::缓存有图片则从缓存读取..缓存有图片则从缓存读取");
		// }else if(item.thumbURL == null){
		// thumb_imgview.setImageDrawable(mDrawableDefaultImg);
		// System.out.println(":::::缓存URL为空");
		// }else{
		// thumb_imgview.setImageDrawable(mDrawableDefaultImg);
		// GetData(item, thumb_imgview);
		// }

		// if(item.thumburl != ""){
		// GetData(item.thumburl, thumb_imgview);
		// }
	}

	/**
	 * 下载图片
	 */
	private void GetData(String url, ImageView thumbimgview) {
		// 本地读数据库取是否有图片
		// mActivitysDB = ActivitysDB.getInstance(mContext);
		// EventDraftItem.Attachment attm =
		// mActivitysDB.retrieveAttachmentBytes(item.thumbURL);
		// if(attm.data != null){
		// item.thumbBitmap = MyMothod.Byte2Bitmap(attm.data);
		// thumbimgview.setImageBitmap(item.thumbBitmap);
		// System.out.println(":::::缓存URL,从数据库读取:" + item.thumbURL);
		// return;
		// }

		// 没图片则访问网络获取
		DrRequestImgsTask task = new DrRequestImgsTask(this.mContext, url, false, callBack(this.mContext, url, thumbimgview));
		task.execute();
		// System.out.println(":::::缩略图,从网络读取:" + item.title + "," +
		// item.thumbURL);
	}

	/**
	 * 下载图片回调
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	private UICallBack callBack(Context context, final String url, final ImageView thumbimgview) {
		return new UICallBack() {
			byte[] attachmentBytes;

			@Override
			public void callBack(Object b) {
				if (b != null)
					attachmentBytes = (byte[]) b;
				b = null;

				Bitmap bmp = BitmapCache.getInstance().getBitmap(url, attachmentBytes, true);
				thumbimgview.setBackgroundColor(Color.TRANSPARENT);
				thumbimgview.setImageBitmap(bmp);
				bmp = null;

				// 保存到数据库
				// synchronized(mActivitysDB){
				// mActivitysDB.startTransaction() ;
				// mActivitysDB.saveAttachment(item.thumbURL,attachmentBytes,"thumbpic")
				// ;
				// mActivitysDB.endTransaction() ;
				// }
				attachmentBytes = null;
				bmp = null;
				return;
			}
		};
	}

}
