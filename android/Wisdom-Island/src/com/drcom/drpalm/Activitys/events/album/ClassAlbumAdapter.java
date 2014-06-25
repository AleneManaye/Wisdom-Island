package com.drcom.drpalm.Activitys.events.album;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.wisdom.island.R;

/**
 * 相册列表的adapter
 * 
 * @author Administrator
 * 
 */
public class ClassAlbumAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mLayoutInflater;
	private List<EventDetailsItem> data;
	private ImageLoader mImageLoader;
	private boolean mBusy = false;// 快速滑动列表时空忙碌，不加载图片，按着滑动和停止时为空闲，加载图片
	private Bitmap no_pic, no_pic_blue;
	private int firstVisableItem, visableCount;
	private TextPaint tp;

	public ClassAlbumAdapter(Context context, List<EventDetailsItem> data, ImageLoader mImageLoader) {
		super();
		this.context = context;
		this.mLayoutInflater = LayoutInflater.from(context);
		this.data = data;
		this.mImageLoader = mImageLoader;
		this.no_pic = GlobalVariables.no_pic;
		no_pic_blue = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_pic);
	}

	@Override
	public int getCount() {
		if (data.size() % 2 == 0)
			return data.size() / 2;
		else
			return data.size() / 2 + 1;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		ImageView img;
		ImageView isMark;
		ImageView ifeshow;
		ImageView reply;
		TextView tv;

		ImageView img1;
		ImageView isMark1;
		ImageView ifeshow1;
		ImageView reply1;
		TextView tv1;
		RelativeLayout album_sd1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.album_grid_item, null);
			vh.img = (ImageView) convertView.findViewById(R.id.img);
			vh.isMark = (ImageView) convertView.findViewById(R.id.album_mark);
			vh.ifeshow = (ImageView) convertView.findViewById(R.id.album_ifeshow);
			vh.reply = (ImageView) convertView.findViewById(R.id.album_reply);
			vh.tv = (TextView) convertView.findViewById(R.id.tv);

			vh.img1 = (ImageView) convertView.findViewById(R.id.img_right);
			vh.isMark1 = (ImageView) convertView.findViewById(R.id.album_mark_right);
			vh.ifeshow1 = (ImageView) convertView.findViewById(R.id.album_ifeshow_right);
			vh.reply1 = (ImageView) convertView.findViewById(R.id.album_reply_right);
			vh.tv1 = (TextView) convertView.findViewById(R.id.tv_right);
			vh.album_sd1 = (RelativeLayout) convertView.findViewById(R.id.album_asd_right);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.img.setImageBitmap(no_pic);
		vh.img1.setImageBitmap(no_pic);

		EventDetailsItem left = null;
		if (position * 2 < data.size()) {
			left = data.get(position * 2);
		}
		if (left != null) {
			vh.tv.setText(left.title);
			tp = vh.tv.getPaint();
			if(left.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
				tp.setStrikeThruText(true);
			}else{
				tp.setStrikeThruText(false);
			}
			
			if (left.isread) {
				vh.tv.setTextColor(context.getResources().getColor(R.color.light_gray));
			} else {
				vh.tv.setTextColor(context.getResources().getColor(R.color.gold));
			}
			
			if (left.bookmark)
				vh.isMark.setVisibility(View.VISIBLE);
			else {
				vh.isMark.setVisibility(View.GONE);
			}
			
			// vh.ifeshow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.important_small));
			if (left.ifeshow)
				vh.ifeshow.setVisibility(View.VISIBLE);
			else {
				vh.ifeshow.setVisibility(View.GONE);
			}
			
			//回复气泡
			// 大于 上次阅读过的回复的时间
//			if (left.awscount > left.awscountclient) {
//				vh.reply.setImageResource(R.drawable.new_feedback_small);
//				vh.reply.setVisibility(View.VISIBLE);
//			} else {
//				vh.reply.setImageResource(R.drawable.feedback_small);
//				vh.reply.setVisibility(View.VISIBLE);
//
//				if (left.awscount == 0)
//					vh.reply.setVisibility(View.GONE);
//			}
			if(left.lastawstime.getTime() == 0){
				vh.reply.setVisibility(View.GONE);
			}else{
				//自己发的通告不显示
				if (left.pubid.equals(left.user)){	
					vh.reply.setVisibility(View.GONE);
				}
				//有新回复并不是自己发的
				else if (left.lastawstime.getTime() > left.lastawstimeread.getTime() && !left.lastawsuserid.equals(left.user)){	
					vh.reply.setImageResource(R.drawable.new_feedback_small);
					vh.reply.setVisibility(View.VISIBLE);
				}else{
					vh.reply.setImageResource(R.drawable.feedback_small);
					vh.reply.setVisibility(View.VISIBLE);
				}
			}
			
			//图标
			if (isPic(left.thumbUrl)) {
				mImageLoader.DisplayImage(left.thumbUrl, vh.img, mBusy);
			} else {
				vh.img.setImageBitmap(no_pic_blue);
			}

			final EventDetailsItem leftItem = left;
			vh.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 跳转到相冊詳細界面
					if (leftItem != null) {
						Intent intent = new Intent();
						if (leftItem.eventid != null)
							intent.putExtra(ClassAlbumDetailActivity.KEY_EVENT_ID, leftItem.eventid);
						intent.setClass(context, ClassAlbumDetailActivity.class);
						context.startActivity(intent);

						// 未读时,广播通知主界面减少未读数
						if (!leftItem.isread && leftItem.status.equals(EventDraftItem.ORISTATUS_TYPE_N)) {
							Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
							intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID, RequestCategoryID.EVENTS_ALBUM_ID);
							context.sendBroadcast(intent1);
						}
					}
				}
			});
		}

		EventDetailsItem right = null;
		if (position * 2 + 1 < data.size()) {
			right = data.get(position * 2 + 1);
		}
		if (right != null) {
			vh.album_sd1.setVisibility(View.VISIBLE);
			vh.tv1.setText(right.title);
			tp = vh.tv1.getPaint();
			if(right.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
				tp.setStrikeThruText(true);
			}else{
				tp.setStrikeThruText(false);
			}
			if (right.isread) {
				vh.tv1.setTextColor(context.getResources().getColor(R.color.light_gray));
			} else {
				vh.tv1.setTextColor(context.getResources().getColor(R.color.gold));
			}
			if (right.bookmark)
				vh.isMark1.setVisibility(View.VISIBLE);
			else {
				vh.isMark1.setVisibility(View.GONE);
			}
			// vh.ifeshow1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.important_small));
			if (right.ifeshow)
				vh.ifeshow1.setVisibility(View.VISIBLE);
			else {
				vh.ifeshow1.setVisibility(View.GONE);
			}
			
			//回复气泡
			// vh.reply1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.feedback_small));
			// if (right.lastawstime.getTime() !=
			// right.lastawstimeread.getTime()) {
			// vh.reply1.setVisibility(View.VISIBLE);
			// } else {
			// vh.reply1.setVisibility(View.GONE);
			// }
//			// 大于 上次阅读过的回复的时间
//			if (right.awscount > right.awscountclient) {
//				vh.reply1.setImageResource(R.drawable.new_feedback_small);
//				vh.reply1.setVisibility(View.VISIBLE);
//			} else {
//				vh.reply1.setImageResource(R.drawable.feedback_small);
//				vh.reply1.setVisibility(View.VISIBLE);
//
//				if (right.awscount == 0)
//					vh.reply1.setVisibility(View.GONE);
//			}
			if(right.lastawstime.getTime() == 0){
				vh.reply1.setVisibility(View.GONE);
			}else{
				//自己发的通告不显示
				if (right.pubid.equals(right.user)){	
					vh.reply1.setVisibility(View.GONE);
				}
				//有新回复并不是自己发的
				else if (right.lastawstime.getTime() > right.lastawstimeread.getTime() && !right.lastawsuserid.equals(right.user)){	
					vh.reply1.setImageResource(R.drawable.new_feedback_small);
					vh.reply1.setVisibility(View.VISIBLE);
				}else{
					vh.reply1.setImageResource(R.drawable.feedback_small);
					vh.reply1.setVisibility(View.VISIBLE);
				}
			}
			
			if (isPic(right.thumbUrl)) {
				mImageLoader.DisplayImage(right.thumbUrl, vh.img1, mBusy);
			} else {
				vh.img1.setImageBitmap(no_pic_blue);
			}

			final EventDetailsItem rightItem = right;
			vh.img1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 跳转到相冊詳細界面
					Intent intent = new Intent();
					if (rightItem != null) {
						if (rightItem.eventid != null)
							intent.putExtra(ClassAlbumDetailActivity.KEY_EVENT_ID, rightItem.eventid);
						intent.setClass(context, ClassAlbumDetailActivity.class);
						context.startActivity(intent);

						// 未读时,广播通知主界面减少未读数
						if (!rightItem.isread && rightItem.status.equals(EventDraftItem.ORISTATUS_TYPE_N)) {
							Intent intent1 = new Intent(ActivityActionDefine.EVENTS_UNREAD_SUM_DESC);
							intent1.putExtra(ActivityActionDefine.EVENTS_TYPE_ID, RequestCategoryID.EVENTS_ALBUM_ID);
							context.sendBroadcast(intent1);
						}
					}
				}
			});
		} else {
			vh.album_sd1.setVisibility(View.INVISIBLE);
		}
		// 删除第一个可见项之前2项之前的所有项，节省内存开销
		if (firstVisableItem - 2 > 0 && (firstVisableItem - 2) * 2 + 1 < data.size() && !mBusy) {
			for (int i = 0; i < (firstVisableItem - 2) * 2; i++) {
				mImageLoader.remove(data.get(i).thumbUrl);
			}
		}
		// 删除最好一个可见项之后第2项之后的所有项，节省内存开销
		if (firstVisableItem + visableCount + 2 < this.getCount() && !mBusy) {
			for (int i = (firstVisableItem + visableCount + 2) * 2; i < data.size(); i++) {
				mImageLoader.remove(data.get(i).thumbUrl);
			}
		}

		return convertView;
	}

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public void setFirstVisableItem(int firstVisibleItem, int visibleItemCount) {
		firstVisableItem = firstVisibleItem;
		visableCount = visibleItemCount;
	}

	public boolean isPic(String fileName) {
		boolean flag = false;
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		if ("jpg".equalsIgnoreCase(fileExt) || "png".equalsIgnoreCase(fileExt) || "gif".equalsIgnoreCase(fileExt) || "jpeg".equalsIgnoreCase(fileExt)) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}
}
