package com.drcom.drpalm.View.news;



import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ImgsAttc.ImageAttcGalleryActivity;
import com.drcom.drpalm.Activitys.news.NewsDetailActivity;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.objs.NewsItem;

public class NewsDetailActivityManagement {
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签

	private Context mContext = null;
	private NewsItem mNewsItem = null;

	public NewsDetailActivityManagement(Context context) {
		mContext = context;
	}

	/*
	 * 打开画廊
	 */
	public void startImageGalleryActivity(NewsItem item) {
//		startNewsImageGalleryActivity(item.story_id);
		
		String[] urls = new String[item.otherImgs.size()];
		for(int i = 0;i < urls.length; i++){
			urls[i] = item.otherImgs.get(i).smallURL;
		}
		
		Intent i = new Intent(mContext, ImageAttcGalleryActivity.class);
		i.putExtra(ImageAttcGalleryActivity.KEY_IMGSURL, urls);
		mContext.startActivity(i);
	}

	public NewsItem getDataItem(int story_id) {
		NewsDB newsDB = NewsDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		this.mNewsItem = newsDB.retrieveNewsItem(story_id);
		this.mNewsItem.category_id = newsDB.getCategoryByStoryId(story_id);
		return mNewsItem;
	}

	/*
	 * 请求协议
	 */
	public void sendGetNeededInfo(final Handler handler, final int story_id, int getall) {
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {
			@Override
			public void onSuccess() {
				Message msg = new Message();
				msg.what = DOWN;
				handler.sendMessageDelayed(msg, 200);
			}

			@Override
			public void onError(String err) {
				Message msg = new Message();
				msg.obj = err;
				msg.what = NOT_DOWN;
				handler.sendMessageDelayed(msg, 100);
			}
		};
		mRequestOperation.sendGetNeededInfo("GetNewsDetail", new Object[] { story_id, getall, callback }, callback.getClass().getName());
	}

}
