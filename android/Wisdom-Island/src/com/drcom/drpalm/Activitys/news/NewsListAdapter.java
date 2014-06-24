package com.drcom.drpalm.Activitys.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm4tianzhujiao.R;

import java.util.Date;
import java.util.List;

public class NewsListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mLayoutInflater;
	private List<NewsItem> data;
	private ImageLoader mImageLoader;
	private boolean mBusy = false;// 快速滑动列表时忙碌，不加载图片，按着滑动和停止时为空闲，加载图片
	private Bitmap no_pic;
	private int firstVisableItem, visableCount;
	// private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private int category;
	private Date today = new Date();
	private TextPaint tp;

	public NewsListAdapter(Context context, List<NewsItem> data, int category, ImageLoader mImageLoader) {
		this.context = context;
		this.mLayoutInflater = LayoutInflater.from(context);
		this.data = data;
		this.category = category;
		this.mImageLoader = mImageLoader;
//        ResourceManagement resourceManagement = new ResourceManagement();
//        这里读取默认学校icon路径
//        this.no_pic = BitmapFactory.decodeFile(resourceManagement.getBlockURI("/global/title-image@2x.png"));
	}

	@Override
	public int getCount() {
		return data.size();
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
		ImageView mImgview_unreadmark;
		ImageView newsCover;
		ImageView isMark;
		TextView newsTitile;
		TextView newsSummary;
		TextView newsUpDate;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.news_main_item, null);
			vh.mImgview_unreadmark = (ImageView) convertView.findViewById(R.id.Imgview_unreadmark);
			vh.newsCover = (ImageView) convertView.findViewById(R.id.news_cover);
			vh.isMark = (ImageView) convertView.findViewById(R.id.news_isMark);
			vh.newsTitile = (TextView) convertView.findViewById(R.id.news_title);
			vh.newsSummary = (TextView) convertView.findViewById(R.id.news_summary);
			vh.newsUpDate = (TextView) convertView.findViewById(R.id.news_date);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		NewsItem item = data.get(position);
		if (item.isRead) { // 是否已读
			vh.newsTitile.setTextColor(context.getResources().getColor(R.color.light_gray));
			vh.newsSummary.setTextColor(context.getResources().getColor(R.color.light_gray));
			vh.newsUpDate.setTextColor(context.getResources().getColor(R.color.light_gray));
			vh.mImgview_unreadmark.setVisibility(View.GONE);
		} else {
			vh.newsTitile.setTextColor(context.getResources().getColor(R.color.listtitletxt));
			vh.newsSummary.setTextColor(context.getResources().getColor(R.color.dark_gray));
			vh.newsUpDate.setTextColor(context.getResources().getColor(R.color.orange));
			vh.mImgview_unreadmark.setVisibility(View.VISIBLE);
		}

		// 标题
		if (item.title != null)
			vh.newsTitile.setText(item.title);
		else
			vh.newsTitile.setText("");
		tp = vh.newsTitile.getPaint();
		if(item.status.equals(NewsItem.STATUS_TYPE_C)){
			tp.setStrikeThruText(true);
		}else{
			tp.setStrikeThruText(false);
		}
		

		if (item.summary != null)
			vh.newsSummary.setText(item.summary);// 描述
		else
			vh.newsSummary.setText("");

		if (item.lastupdate != null) {
			vh.newsUpDate.setText(DateFormatter.getStringYYYYMMDD(item.postDate).equals(DateFormatter.getStringYYYYMMDD(today)) ? DateFormatter.getStringHHmm(item.postDate) : DateFormatter
					.getStringYYYYMMDD(item.postDate));
			// vh.newsUpDate.setText(item.postDate == new Date(today.getYear(),
			// today.getMonth(),
			// today.getDay())?DateFormatter.getStringHHmm(item.postDate):DateFormatter.getStringYYYYMMDD(item.postDate));
			// vh.newsUpDate.setText(sdf.format(item.postDate));// 更新日期
		}

		// vh.isMark.setBackgroundDrawable(this.context.getResources().getDrawable(R.drawable.bookmark_small));
		if (item.bookmark)
			vh.isMark.setVisibility(View.VISIBLE);// 是否已读
		else
			vh.isMark.setVisibility(View.INVISIBLE);// 是否已读

        if(no_pic == null){
            no_pic = GlobalVariables.null_pic;
        }

		vh.newsCover.setImageBitmap(no_pic);
		category = NewsDB.getInstance(context, GlobalVariables.gSchoolKey).getCategoryByStoryId(item.story_id);

		// 新闻和育儿没有图片
		if (category == RequestCategoryID.NEWS_PARENTING_ID || category == RequestCategoryID.NEWS_NEWS_ID) {
			vh.newsCover.setVisibility(View.GONE);
		} else {
			vh.newsCover.setVisibility(View.VISIBLE);
			Log.i("xpf", "firstVisiableItem1 " + firstVisableItem + " visableCount= " + visableCount + " positon= " + position + " thumb_url= " + item.thumb_url);
			
//			if(item.thumb_url.equals("") || item.thumb_url.equals("null")){
//				vh.newsCover.setImageBitmap(no_pic);
//			}else{
            Log.i("IMAGE_URL","------");
            Log.i("IMAGE_URL",item.thumb_url);
				mImageLoader.DisplayImage(item.thumb_url, vh.newsCover, mBusy);

				// 删除第一个可见项之前2项之前的所有项，节省内存开销
				if (firstVisableItem - 2 > 0 && firstVisableItem - 2 < data.size() && !mBusy) {
					for (int i = 0; i < firstVisableItem - 2; i++) {
						mImageLoader.remove(data.get(i).thumb_url);
					}
				}
				// 删除最好一个可见项之后第2项之后的所有项，节省内存开销
				if (firstVisableItem + visableCount + 2 < data.size() && !mBusy) {
					for (int i = firstVisableItem + visableCount + 2; i < data.size(); i++) {
						mImageLoader.remove(data.get(i).thumb_url);
					}
				}
//			}
		}

		return convertView;
	}

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	// 和RefreshListView耦合
	public void setFirstVisableItem(int firstVisibleItem, int visibleItemCount) {
		firstVisableItem = firstVisibleItem;
		visableCount = visibleItemCount;
	}

}
