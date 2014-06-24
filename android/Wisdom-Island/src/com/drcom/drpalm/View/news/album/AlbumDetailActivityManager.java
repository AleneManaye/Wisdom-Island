package com.drcom.drpalm.View.news.album;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;

public class AlbumDetailActivityManager {
	
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签
	
	public AlbumDetailActivityManager()
	{
		
	}
	
	public void getNewsDetail(Boolean isAll, final int newsId, final Handler h)
	{
		int getall = 0;
		if (isAll) {
			getall = 1;
		}
		
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {
			@Override
			public void onSuccess() {
				Message msg = new Message();
				msg.what = DOWN;
				h.sendMessage(msg);
			}

			@Override
			public void onError(String err) {
				Message msg = new Message();
				msg.what = NOT_DOWN;
				msg.obj = err;
				h.sendMessage(msg);
			}
		};
		mRequestOperation.sendGetNeededInfo("GetNewsDetail", new Object[] { newsId, getall, callback }, callback.getClass().getName());
	}

}
