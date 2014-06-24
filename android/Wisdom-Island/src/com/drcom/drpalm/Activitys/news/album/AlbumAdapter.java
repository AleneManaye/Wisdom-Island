package com.drcom.drpalm.Activitys.news.album;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextPaint;
import android.util.Log;
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
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 相册列表的adapter
 * 这里用listView 实现2列类似GridView的功能，可以添加更多选项，因为GridView没有添加FootView功能
 * @author Administrator
 * 
 */
public class AlbumAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mLayoutInflater;
	private List<NewsItem> data;
	private ImageLoader mImageLoader;
	private boolean mBusy = false;// 快速滑动列表时空忙碌，不加载图片，按着滑动和停止时为空闲，加载图片
	private Bitmap no_pic, no_pic_blue;
	private int firstVisableItem, visableCount;
	private TextPaint tp;

	public AlbumAdapter(Context context, List<NewsItem> data, ImageLoader mImageLoader) {
		super();
		this.context = context;
		this.mLayoutInflater = LayoutInflater.from(context);
		this.data = data;
		this.mImageLoader = mImageLoader;
		//url不可用时显示的默认图片
		this.no_pic_blue = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_pic);
		//图片正在下载时显示的图片
		this.no_pic = GlobalVariables.no_pic;

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

		NewsItem left = null;
		if (position * 2 < data.size()) {
			left = data.get(position * 2);
		}
		if (left != null) {
			if (left.bookmark)
				vh.isMark.setVisibility(View.VISIBLE);
			else {
				vh.isMark.setVisibility(View.GONE);
			}
			
			//title
			vh.tv.setText(left.title);
			if (left.isRead) {
				vh.tv.setTextColor(context.getResources().getColor(R.color.light_gray));
			} else {
				vh.tv.setTextColor(context.getResources().getColor(R.color.gold));
			}
			tp = vh.tv.getPaint();
			if(left.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
				tp.setStrikeThruText(true);
			}else{
				tp.setStrikeThruText(false);
			}
			
			if (isPic(left.thumb_url)) {
				mImageLoader.DisplayImage(left.thumb_url, vh.img, mBusy);
			} else {
				vh.img.setImageBitmap(no_pic_blue);
			}

			final NewsItem leftItem = left;
			vh.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (leftItem != null) {
						Intent intent = new Intent();
						intent.putExtra("story_id", leftItem.story_id);
						intent.setClass(context, AlbumDetailActivity.class);
						context.startActivity(intent);

						// 未读时,广播通知主界面减少未读数
						if (!leftItem.isRead) {
							Intent intent1 = new Intent(ActivityActionDefine.NEWS_UNREAD_SUM_DESC);
							intent1.putExtra(ActivityActionDefine.NEWS_TYPE_ID, RequestCategoryID.NEWS_ALBUM_ID);
							context.sendBroadcast(intent1);
						}
					}
				}
			});
		}

		NewsItem right = null;
		if (position * 2 + 1 < data.size()) {
			right = data.get(position * 2 + 1);
		}
		if (right != null) {
			vh.album_sd1.setVisibility(View.VISIBLE);
			if (right.bookmark)
				vh.isMark1.setVisibility(View.VISIBLE);
			else {
				vh.isMark1.setVisibility(View.GONE);
			}
			//title
			vh.tv1.setText(right.title);
			if (right.isRead) {
				vh.tv1.setTextColor(context.getResources().getColor(R.color.light_gray));
			} else {
				vh.tv1.setTextColor(context.getResources().getColor(R.color.gold));
			}
			tp = vh.tv1.getPaint();
			if(right.status.equals(EventDraftItem.ORISTATUS_TYPE_C)){
				tp.setStrikeThruText(true);
			}else{
				tp.setStrikeThruText(false);
			}
			
			Log.i("xpf", "right.thumb_url=" + right.thumb_url + "right.status：" + right.status);
			
			if (isPic(right.thumb_url)) {
				mImageLoader.DisplayImage(right.thumb_url, vh.img1, mBusy);
			} else {
				vh.img1.setImageBitmap(no_pic_blue);
			}
			final NewsItem rightItem = right;
			vh.img1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (rightItem != null) {
						Intent intent = new Intent();
						intent.putExtra("story_id", rightItem.story_id);
						intent.setClass(context, AlbumDetailActivity.class);
						context.startActivity(intent);

						// 未读时,广播通知主界面减少未读数
						if (!rightItem.isRead) {
							Intent intent1 = new Intent(ActivityActionDefine.NEWS_UNREAD_SUM_DESC);
							intent1.putExtra(ActivityActionDefine.NEWS_TYPE_ID, RequestCategoryID.NEWS_ALBUM_ID);
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
				mImageLoader.remove(data.get(i).thumb_url);
			}
		}
		// 删除最好一个可见项之后第2项之后的所有项，节省内存开销
		if (firstVisableItem + visableCount + 2 < this.getCount() && !mBusy) {
			for (int i = (firstVisableItem + visableCount + 2) * 2; i < data.size(); i++) {
				mImageLoader.remove(data.get(i).thumb_url);
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
